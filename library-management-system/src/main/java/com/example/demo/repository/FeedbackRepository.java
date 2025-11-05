package com.example.demo.repository;

import com.example.demo.model.Book;
import com.example.demo.model.Feedback;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByBook(Book book);
    List<Feedback> findByUser(User user);
    List<Feedback> findByBookOrderByCreatedAtDesc(Book book);
}





