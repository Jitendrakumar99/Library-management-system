package com.example.demo.repository;

import com.example.demo.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByActionType(String actionType);
    List<ActivityLog> findByUser_Id(Long userId);
    List<ActivityLog> findByBook_Id(Long bookId);
    List<ActivityLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT al FROM ActivityLog al ORDER BY al.createdAt DESC")
    List<ActivityLog> findAllOrderByCreatedAtDesc();
}

