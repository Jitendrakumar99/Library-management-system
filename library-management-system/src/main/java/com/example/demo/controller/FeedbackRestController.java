package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Book;
import com.example.demo.model.Feedback;
import com.example.demo.model.User;
import com.example.demo.service.BookService;
import com.example.demo.service.FeedbackService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:4200")
public class FeedbackRestController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Feedback>>> getAllFeedback() {
        List<Feedback> feedbacks = feedbackService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback retrieved successfully", feedbacks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Feedback>> getFeedbackById(@PathVariable Long id) {
        Feedback feedback = feedbackService.findById(id);
        if (feedback != null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Feedback retrieved successfully", feedback));
        }
        return ResponseEntity.ok(new ApiResponse<>(false, "Feedback not found", null));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<List<Feedback>>> getFeedbackByBook(@PathVariable Long bookId) {
        Book book = bookService.findById(bookId);
        if (book == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Book not found", null));
        }
        List<Feedback> feedbacks = feedbackService.findByBookOrderByCreatedAtDesc(book);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback retrieved successfully", feedbacks));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Feedback>>> getFeedbackByUser(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "User not found", null));
        }
        List<Feedback> feedbacks = feedbackService.findByUser(user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback retrieved successfully", feedbacks));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<Feedback>> createFeedback(@RequestBody Feedback feedback) {
        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Rating must be between 1 and 5", null));
        }

        Book book = bookService.findById(feedback.getBook().getId());
        if (book == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Book not found", null));
        }

        feedback.setBook(book);
        Feedback savedFeedback = feedbackService.save(feedback);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback submitted successfully", savedFeedback));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<ApiResponse<Feedback>> updateFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        Feedback existingFeedback = feedbackService.findById(id);
        if (existingFeedback == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Feedback not found", null));
        }

        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Rating must be between 1 and 5", null));
        }

        existingFeedback.setComment(feedback.getComment());
        existingFeedback.setRating(feedback.getRating());
        Feedback updatedFeedback = feedbackService.save(existingFeedback);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback updated successfully", updatedFeedback));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(@PathVariable Long id) {
        Feedback feedback = feedbackService.findById(id);
        if (feedback == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Feedback not found", null));
        }

        feedbackService.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Feedback deleted successfully", null));
    }
}





