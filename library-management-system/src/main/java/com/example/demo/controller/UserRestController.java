package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UserStatistics;
import com.example.demo.model.Issue;
import com.example.demo.model.User;
import com.example.demo.service.IssueService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private com.example.demo.service.ActivityLogService activityLogService;

    @Autowired
    private com.example.demo.service.EmailService emailService;

    @Autowired
    private IssueService issueService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved successfully", users));
    }

    @GetMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllStudents() {
        List<User> students = userService.findAllStudents();
        return ResponseEntity.ok(new ApiResponse<>(true, "Students retrieved successfully", students));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getPendingUsers() {
        List<User> pendingUsers = userService.findPendingUsers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending users retrieved successfully", pendingUsers));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> approveUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "User not found", null));
        }
        
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findByUsername(auth.getName());
        
        user.setStatus("APPROVED");
        User updatedUser = userService.save(user);
        
        // Log activity
        activityLogService.logUserApproved(user, admin);
        
        // Send email notification if email exists
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            emailService.sendAccountApprovalNotification(user.getEmail(), user.getUsername());
        }
        
        return ResponseEntity.ok(new ApiResponse<>(true, "User approved successfully", updatedUser));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> rejectUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "User not found", null));
        }
        
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findByUsername(auth.getName());
        
        user.setStatus("REJECTED");
        User updatedUser = userService.save(user);
        
        // Log activity
        activityLogService.logUserRejected(user, admin);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "User rejected successfully", updatedUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "User retrieved successfully", user));
        }
        return ResponseEntity.ok(new ApiResponse<>(false, "User not found", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userService.findById(id);
        if (existingUser == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "User not found", null));
        }

        existingUser.setUsername(user.getUsername());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        User updatedUser = userService.save(existingUser);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "User not found", null));
        }

        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Cannot delete admin user", null));
        }

        // Check if user has active issues
        List<Issue> activeIssues = issueService.findByStudent(user).stream()
            .filter(issue -> !issue.isReturned())
            .collect(Collectors.toList());

        if (!activeIssues.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(false, 
                "Cannot delete user: User has " + activeIssues.size() + " active book issue(s). Please return all books first.", null));
        }

        // Delete user if no active issues
        userService.deleteById(id);
        
        // Log activity
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findByUsername(auth.getName());
        if (admin != null) {
            activityLogService.logUserDeleted(user, admin);
        }
        
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }

    @GetMapping("/with-statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserStatistics>>> getAllUsersWithStatistics() {
        List<User> users = userService.findAll();
        List<UserStatistics> userStatisticsList = new ArrayList<>();

        for (User user : users) {
            List<Issue> issues = issueService.findByStudent(user);
            
            long totalIssuedBooks = issues.size();
            long activeIssuedBooks = issues.stream().filter(issue -> !issue.isReturned()).count();
            long returnedBooks = issues.stream().filter(Issue::isReturned).count();
            
            int totalFine = issues.stream().mapToInt(Issue::getFine).sum();
            
            // Calculate pending fine for active overdue books
            int pendingFine = issues.stream()
                .filter(issue -> !issue.isReturned())
                .filter(issue -> issue.getReturnDate() != null && LocalDate.now().isAfter(issue.getReturnDate()))
                .mapToInt(issue -> {
                    long lateDays = ChronoUnit.DAYS.between(issue.getReturnDate(), LocalDate.now());
                    return (int) (lateDays * 10); // ₹10 per day
                })
                .sum();

            UserStatistics statistics = new UserStatistics(user, totalIssuedBooks, activeIssuedBooks, returnedBooks, totalFine, pendingFine);
            userStatisticsList.add(statistics);
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Users with statistics retrieved successfully", userStatisticsList));
    }

    @GetMapping("/{id}/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserStatistics>> getUserStatistics(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "User not found", null));
        }

        List<Issue> issues = issueService.findByStudent(user);
        
        long totalIssuedBooks = issues.size();
        long activeIssuedBooks = issues.stream().filter(issue -> !issue.isReturned()).count();
        long returnedBooks = issues.stream().filter(Issue::isReturned).count();
        
        int totalFine = issues.stream().mapToInt(Issue::getFine).sum();
        
        // Calculate pending fine for active overdue books
        int pendingFine = issues.stream()
            .filter(issue -> !issue.isReturned())
            .filter(issue -> issue.getReturnDate() != null && LocalDate.now().isAfter(issue.getReturnDate()))
            .mapToInt(issue -> {
                long lateDays = ChronoUnit.DAYS.between(issue.getReturnDate(), LocalDate.now());
                return (int) (lateDays * 10); // ₹10 per day
            })
            .sum();

        UserStatistics statistics = new UserStatistics(user, totalIssuedBooks, activeIssuedBooks, returnedBooks, totalFine, pendingFine);
        return ResponseEntity.ok(new ApiResponse<>(true, "User statistics retrieved successfully", statistics));
    }
}


