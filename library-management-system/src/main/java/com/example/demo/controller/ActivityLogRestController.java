package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.ActivityLog;
import com.example.demo.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
@CrossOrigin(origins = "http://localhost:4200")
public class ActivityLogRestController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ActivityLog>>> getAllActivityLogs() {
        List<ActivityLog> logs = activityLogService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Activity logs retrieved successfully", logs));
    }

    @GetMapping("/action/{actionType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ActivityLog>>> getLogsByActionType(@PathVariable String actionType) {
        List<ActivityLog> logs = activityLogService.findByActionType(actionType);
        return ResponseEntity.ok(new ApiResponse<>(true, "Activity logs retrieved successfully", logs));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ActivityLog>>> getLogsByUserId(@PathVariable Long userId) {
        List<ActivityLog> logs = activityLogService.findByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Activity logs retrieved successfully", logs));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ActivityLog>>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ActivityLog> logs = activityLogService.findByDateRange(start, end);
        return ResponseEntity.ok(new ApiResponse<>(true, "Activity logs retrieved successfully", logs));
    }
}

