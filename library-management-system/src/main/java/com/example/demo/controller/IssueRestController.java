package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.model.User;
import com.example.demo.service.BookService;
import com.example.demo.service.IssueService;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
@CrossOrigin(origins = "http://localhost:4200")
public class IssueRestController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.example.demo.service.ActivityLogService activityLogService;

    @Autowired
    private com.example.demo.service.EmailService emailService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Issue>>> getAllIssues() {
        List<Issue> issues = issueService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Issues retrieved successfully", issues));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Issue>> getIssueById(@PathVariable Long id) {
        Issue issue = issueService.findById(id);
        if (issue != null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Issue retrieved successfully", issue));
        }
        return ResponseEntity.ok(new ApiResponse<>(false, "Issue not found", null));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<Issue>>> getIssuesByStudent(@PathVariable Long studentId) {
        User student = userService.findById(studentId);
        if (student == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Student not found", null));
        }
        List<Issue> issues = issueService.findByStudent(student);
        return ResponseEntity.ok(new ApiResponse<>(true, "Issues retrieved successfully", issues));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Issue>>> getActiveIssues() {
        List<Issue> issues = issueService.findAll().stream()
                .filter(i -> !i.isReturned() && (i.getStatus() == null || "APPROVED".equals(i.getStatus())))
                .toList();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active issues retrieved", issues));
    }

    @GetMapping("/returned")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Issue>>> getReturnedIssues() {
        List<Issue> issues = issueService.findAllReturned();
        return ResponseEntity.ok(new ApiResponse<>(true, "Returned issues retrieved", issues));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Issue>> createIssue(@RequestBody Issue issue) {
        User student = userService.findById(issue.getStudent().getId());
        Book book = bookService.findById(issue.getBook().getId());

        if (student == null || book == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Student or book not found", null));
        }

        if (book.getAvailable() <= 0) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Book is not available", null));
        }

        issue.setBook(book);
        issue.setStudent(student);
        issue.setIssueDate(LocalDate.now());
        issue.setReturnDate(LocalDate.now().plusDays(7));
        issue.setReturned(false);
        issue.setFine(0);

        Issue savedIssue = issueService.save(issue);

        book.setAvailable(book.getAvailable() - 1);
        bookService.save(book);

        // Log activity
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findByUsername(auth.getName());
        activityLogService.logBookIssued(book, student);

        // Send email notification if email exists
        if (student.getEmail() != null && !student.getEmail().isEmpty()) {
            emailService.sendBookIssuedConfirmation(
                student.getEmail(),
                book.getTitle(),
                savedIssue.getIssueDate().toString(),
                savedIssue.getReturnDate().toString()
            );
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Book issued successfully", savedIssue));
    }

    @PostMapping("/request")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<Issue>> requestBook(@RequestBody Book bookRequest) {
        try {
            // Get current authenticated user from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.ok(new ApiResponse<>(false, "User not authenticated", null));
            }

            String username = authentication.getName();
            User student = userService.findByUsername(username);
            Book book = bookService.findById(bookRequest.getId());

            if (student == null || book == null) {
                return ResponseEntity.ok(new ApiResponse<>(false, "Student or book not found", null));
            }

            if (book.getAvailable() <= 0) {
                return ResponseEntity.ok(new ApiResponse<>(false, "Book is not available", null));
            }

            // Check if student already has this book issued and not returned
            List<Issue> existingIssues = issueService.findByStudent(student);
            boolean alreadyIssued = existingIssues.stream()
                .anyMatch(i -> i.getBook().getId().equals(book.getId()) && !i.isReturned() && !"REJECTED".equals(i.getStatus()));
            
            if (alreadyIssued) {
                return ResponseEntity.ok(new ApiResponse<>(false, "You already have a request for this book or it's already issued", null));
            }

            // Check if there's already a pending request for this book
            boolean pendingRequest = existingIssues.stream()
                .anyMatch(i -> i.getBook().getId().equals(book.getId()) && "PENDING".equals(i.getStatus()));
            
            if (pendingRequest) {
                return ResponseEntity.ok(new ApiResponse<>(false, "You already have a pending request for this book", null));
            }

            // Create a PENDING request (not issued yet)
            Issue issue = new Issue();
            issue.setBook(book);
            issue.setStudent(student);
            issue.setStatus("PENDING");
            issue.setRequestedDate(LocalDate.now());
            // Don't set issueDate, returnDate yet - only set when approved
            issue.setReturned(false);
            issue.setFine(0);

            Issue savedIssue = issueService.save(issue);

            // Log activity - book requested (not issued yet)
            activityLogService.save(new com.example.demo.model.ActivityLog(
                "BOOK_REQUESTED",
                String.format("Student %s requested book: %s", student.getUsername(), book.getTitle()),
                student,
                book
            ));

            return ResponseEntity.ok(new ApiResponse<>(true, "Book request submitted. Waiting for admin approval.", savedIssue));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Error requesting book: " + e.getMessage(), null));
        }
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Issue>>> getPendingRequests() {
        List<Issue> pendingRequests = issueService.findPendingRequests();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pending requests retrieved", pendingRequests));
    }
    
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Issue>> approveRequest(@PathVariable Long id) {
        Issue issue = issueService.findById(id);
        if (issue == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Request not found", null));
        }
        
        if (!"PENDING".equals(issue.getStatus())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Request is not pending", null));
        }
        
        Book book = issue.getBook();
        if (book.getAvailable() <= 0) {
            // Reject automatically if book is no longer available
            issue.setStatus("REJECTED");
            issueService.save(issue);
            return ResponseEntity.ok(new ApiResponse<>(false, "Book is no longer available. Request rejected.", null));
        }
        
        // Approve the request - issue the book
        issue.setStatus("APPROVED");
        issue.setIssueDate(LocalDate.now());
        issue.setReturnDate(LocalDate.now().plusDays(7));
        
        Issue savedIssue = issueService.save(issue);
        
        // Decrease book availability
        book.setAvailable(book.getAvailable() - 1);
        bookService.save(book);
        
        // Log activity
        activityLogService.logBookIssued(book, issue.getStudent());
        
        // Send email notification if email exists
        if (issue.getStudent().getEmail() != null && !issue.getStudent().getEmail().isEmpty()) {
            emailService.sendBookIssuedConfirmation(
                issue.getStudent().getEmail(),
                book.getTitle(),
                savedIssue.getIssueDate().toString(),
                savedIssue.getReturnDate().toString()
            );
        }
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Book request approved and issued successfully", savedIssue));
    }
    
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Issue>> rejectRequest(@PathVariable Long id) {
        Issue issue = issueService.findById(id);
        if (issue == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Request not found", null));
        }
        
        if (!"PENDING".equals(issue.getStatus())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Request is not pending", null));
        }
        
        // Reject the request
        issue.setStatus("REJECTED");
        Issue savedIssue = issueService.save(issue);
        
        // Log activity
        activityLogService.save(new com.example.demo.model.ActivityLog(
            "BOOK_REQUEST_REJECTED",
            String.format("Admin rejected book request: %s by student %s", issue.getBook().getTitle(), issue.getStudent().getUsername()),
            issue.getStudent(),
            issue.getBook()
        ));
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Book request rejected", savedIssue));
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Issue>> returnBook(@PathVariable Long id) {
        Issue issue = issueService.findById(id);
        if (issue == null || issue.isReturned()) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Issue not found or already returned", null));
        }

        issue.setReturned(true);

        // Calculate fine
        LocalDate today = LocalDate.now();
        if (today.isAfter(issue.getReturnDate())) {
            long lateDays = ChronoUnit.DAYS.between(issue.getReturnDate(), today);
            int fine = (int) (lateDays * 10);
            issue.setFine(fine);
        } else {
            issue.setFine(0);
        }

        Issue updatedIssue = issueService.save(issue);

        Book book = issue.getBook();
        if (book != null) {
            book.setAvailable(book.getAvailable() + 1);
            bookService.save(book);
        }

        // Log activity
        User student = updatedIssue.getStudent();
        activityLogService.logBookReturned(book, student);

        return ResponseEntity.ok(new ApiResponse<>(true, "Book returned successfully", updatedIssue));
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<ApiResponse<IssueSummary>> getUserIssueSummary(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "User not found", null));
        }

        List<Issue> issues = issueService.findByStudent(user);
        long activeCount = issues.stream().filter(i -> !i.isReturned()).count();
        long returnedCount = issues.stream().filter(Issue::isReturned).count();
        int totalFine = issues.stream().mapToInt(Issue::getFine).sum();

        IssueSummary summary = new IssueSummary(activeCount, returnedCount, totalFine);
        return ResponseEntity.ok(new ApiResponse<>(true, "Summary retrieved successfully", summary));
    }

    // Inner class for summary
    public static class IssueSummary {
        private long activeCount;
        private long returnedCount;
        private int totalFine;

        public IssueSummary(long activeCount, long returnedCount, int totalFine) {
            this.activeCount = activeCount;
            this.returnedCount = returnedCount;
            this.totalFine = totalFine;
        }

        public long getActiveCount() { return activeCount; }
        public void setActiveCount(long activeCount) { this.activeCount = activeCount; }
        
        public long getReturnedCount() { return returnedCount; }
        public void setReturnedCount(long returnedCount) { this.returnedCount = returnedCount; }
        
        public int getTotalFine() { return totalFine; }
        public void setTotalFine(int totalFine) { this.totalFine = totalFine; }
    }
}


