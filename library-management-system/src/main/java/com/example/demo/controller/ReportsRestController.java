package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.model.User;
import com.example.demo.service.BookService;
import com.example.demo.service.IssueService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:4200")
public class ReportsRestController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("/most-issued-books")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMostIssuedBooks(
            @RequestParam(defaultValue = "10") int limit) {
        List<Issue> allIssues = issueService.findAll();
        
        Map<Book, Long> bookIssueCount = allIssues.stream()
            .collect(Collectors.groupingBy(
                Issue::getBook,
                Collectors.counting()
            ));

        List<Map<String, Object>> result = bookIssueCount.entrySet().stream()
            .sorted(Map.Entry.<Book, Long>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Map<String, Object> bookData = new HashMap<>();
                bookData.put("bookId", entry.getKey().getId());
                bookData.put("title", entry.getKey().getTitle());
                bookData.put("author", entry.getKey().getAuthor());
                bookData.put("issueCount", entry.getValue());
                return bookData;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Most issued books retrieved", result));
    }

    @GetMapping("/top-readers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopReaders(
            @RequestParam(defaultValue = "10") int limit) {
        List<Issue> allIssues = issueService.findAll();
        
        Map<User, Long> userIssueCount = allIssues.stream()
            .collect(Collectors.groupingBy(
                Issue::getStudent,
                Collectors.counting()
            ));

        List<Map<String, Object>> result = userIssueCount.entrySet().stream()
            .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
            .limit(limit)
            .map(entry -> {
                Map<String, Object> userData = new HashMap<>();
                userData.put("userId", entry.getKey().getId());
                userData.put("username", entry.getKey().getUsername());
                userData.put("totalIssues", entry.getValue());
                
                // Calculate returned count
                long returnedCount = allIssues.stream()
                    .filter(i -> i.getStudent().getId().equals(entry.getKey().getId()))
                    .filter(Issue::isReturned)
                    .count();
                userData.put("returnedCount", returnedCount);
                
                // Calculate active count
                long activeCount = allIssues.stream()
                    .filter(i -> i.getStudent().getId().equals(entry.getKey().getId()))
                    .filter(i -> !i.isReturned())
                    .count();
                userData.put("activeCount", activeCount);
                
                return userData;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Top readers retrieved", result));
    }

    @GetMapping("/monthly-activity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMonthlyActivity(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDate start = startDate != null ? startDate : LocalDate.now().minusMonths(6);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        
        List<Issue> issues = issueService.findAll().stream()
            .filter(i -> {
                LocalDate issueDate = i.getIssueDate();
                return issueDate != null && 
                       !issueDate.isBefore(start) && 
                       !issueDate.isAfter(end);
            })
            .collect(Collectors.toList());

        Map<String, Map<String, Long>> monthlyData = new LinkedHashMap<>();
        
        LocalDate current = start;
        while (!current.isAfter(end)) {
            String monthKey = current.getYear() + "-" + String.format("%02d", current.getMonthValue());
            monthlyData.put(monthKey, new HashMap<>());
            monthlyData.get(monthKey).put("issued", 0L);
            monthlyData.get(monthKey).put("returned", 0L);
            current = current.plusMonths(1);
        }

        for (Issue issue : issues) {
            String monthKey = issue.getIssueDate().getYear() + "-" + 
                            String.format("%02d", issue.getIssueDate().getMonthValue());
            if (monthlyData.containsKey(monthKey)) {
                monthlyData.get(monthKey).put("issued", 
                    monthlyData.get(monthKey).get("issued") + 1);
            }
            
            if (issue.isReturned() && issue.getReturnDate() != null) {
                String returnMonthKey = issue.getReturnDate().getYear() + "-" + 
                                      String.format("%02d", issue.getReturnDate().getMonthValue());
                if (monthlyData.containsKey(returnMonthKey)) {
                    monthlyData.get(returnMonthKey).put("returned", 
                        monthlyData.get(returnMonthKey).get("returned") + 1);
                }
            }
        }

        List<Map<String, Object>> result = monthlyData.entrySet().stream()
            .map(entry -> {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", entry.getKey());
                monthData.put("issued", entry.getValue().get("issued"));
                monthData.put("returned", entry.getValue().get("returned"));
                return monthData;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Monthly activity retrieved", result));
    }

    @GetMapping("/overdue-books")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOverdueBooks() {
        List<Issue> activeIssues = issueService.findAll().stream()
            .filter(i -> !i.isReturned())
            .filter(i -> {
                LocalDate returnDate = i.getReturnDate();
                return returnDate != null && LocalDate.now().isAfter(returnDate);
            })
            .collect(Collectors.toList());

        List<Map<String, Object>> result = activeIssues.stream()
            .map(issue -> {
                Map<String, Object> issueData = new HashMap<>();
                issueData.put("issueId", issue.getId());
                issueData.put("bookTitle", issue.getBook().getTitle());
                issueData.put("studentUsername", issue.getStudent().getUsername());
                issueData.put("issueDate", issue.getIssueDate());
                issueData.put("returnDate", issue.getReturnDate());
                issueData.put("daysOverdue", 
                    java.time.temporal.ChronoUnit.DAYS.between(issue.getReturnDate(), LocalDate.now()));
                issueData.put("fine", issue.getFine());
                return issueData;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(new ApiResponse<>(true, "Overdue books retrieved", result));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        List<Book> allBooks = bookService.findAll();
        List<Issue> allIssues = issueService.findAll();
        List<User> allUsers = userService.findAllStudents();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooks", allBooks.size());
        stats.put("totalUsers", allUsers.size());
        stats.put("totalIssues", allIssues.size());
        stats.put("activeIssues", allIssues.stream().filter(i -> !i.isReturned()).count());
        stats.put("returnedIssues", allIssues.stream().filter(Issue::isReturned).count());
        stats.put("overdueIssues", allIssues.stream()
            .filter(i -> !i.isReturned())
            .filter(i -> i.getReturnDate() != null && LocalDate.now().isAfter(i.getReturnDate()))
            .count());
        stats.put("totalFine", allIssues.stream().mapToInt(Issue::getFine).sum());
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Statistics retrieved", stats));
    }
}

