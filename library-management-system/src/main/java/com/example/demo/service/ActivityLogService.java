package com.example.demo.service;

import com.example.demo.model.ActivityLog;
import com.example.demo.model.Book;
import com.example.demo.model.User;
import com.example.demo.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public ActivityLog save(ActivityLog activityLog) {
        return activityLogRepository.save(activityLog);
    }

    public List<ActivityLog> findAll() {
        return activityLogRepository.findAllOrderByCreatedAtDesc();
    }

    public List<ActivityLog> findByActionType(String actionType) {
        return activityLogRepository.findByActionType(actionType);
    }

    public List<ActivityLog> findByUserId(Long userId) {
        return activityLogRepository.findByUser_Id(userId);
    }

    public List<ActivityLog> findByBookId(Long bookId) {
        return activityLogRepository.findByBook_Id(bookId);
    }

    public List<ActivityLog> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return activityLogRepository.findByCreatedAtBetween(start, end);
    }

    // Helper methods to log common activities
    public void logBookIssued(Book book, User student) {
        ActivityLog log = new ActivityLog(
            "BOOK_ISSUED",
            String.format("Book '%s' issued to student '%s'", book.getTitle(), student.getUsername()),
            student,
            book
        );
        save(log);
    }

    public void logBookReturned(Book book, User student) {
        ActivityLog log = new ActivityLog(
            "BOOK_RETURNED",
            String.format("Book '%s' returned by student '%s'", book.getTitle(), student.getUsername()),
            student,
            book
        );
        save(log);
    }

    public void logBookAdded(Book book, User admin) {
        ActivityLog log = new ActivityLog(
            "BOOK_ADDED",
            String.format("Book '%s' added by admin '%s'", book.getTitle(), admin.getUsername()),
            admin,
            book
        );
        save(log);
    }

    public void logBookDeleted(Book book, User admin) {
        ActivityLog log = new ActivityLog(
            "BOOK_DELETED",
            String.format("Book '%s' deleted by admin '%s'", book.getTitle(), admin.getUsername()),
            admin,
            book
        );
        save(log);
    }

    public void logUserApproved(User user, User admin) {
        ActivityLog log = new ActivityLog(
            "USER_APPROVED",
            String.format("User '%s' approved by admin '%s'", user.getUsername(), admin.getUsername()),
            admin
        );
        save(log);
    }

    public void logUserRejected(User user, User admin) {
        ActivityLog log = new ActivityLog(
            "USER_REJECTED",
            String.format("User '%s' rejected by admin '%s'", user.getUsername(), admin.getUsername()),
            admin
        );
        save(log);
    }

    public void logUserDeleted(User user, User admin) {
        ActivityLog log = new ActivityLog(
            "USER_DELETED",
            String.format("User '%s' deleted by admin '%s'", user.getUsername(), admin.getUsername()),
            admin
        );
        save(log);
    }
}

