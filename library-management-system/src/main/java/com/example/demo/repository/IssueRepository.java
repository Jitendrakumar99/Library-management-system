package com.example.demo.repository;

import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByStudent(User student);

    // ✅ New: get only returned issues
    List<Issue> findByReturnedTrue();
    
    // Find active (non-returned) issues by book
    List<Issue> findByBookAndReturnedFalse(Book book);
    
    // Find pending requests
    List<Issue> findByStatus(String status);
    
    // Find pending requests by student
    List<Issue> findByStudentAndStatus(User student, String status);
}
