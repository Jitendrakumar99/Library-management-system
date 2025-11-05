package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.Feedback;
import com.example.demo.model.User;
import com.example.demo.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepo;

    public Feedback save(Feedback feedback) {
        return feedbackRepo.save(feedback);
    }

    public Feedback findById(Long id) {
        return feedbackRepo.findById(id).orElse(null);
    }

    public List<Feedback> findByBook(Book book) {
        return feedbackRepo.findByBook(book);
    }

    public List<Feedback> findByUser(User user) {
        return feedbackRepo.findByUser(user);
    }

    public List<Feedback> findByBookOrderByCreatedAtDesc(Book book) {
        return feedbackRepo.findByBookOrderByCreatedAtDesc(book);
    }

    public List<Feedback> findAll() {
        return feedbackRepo.findAll();
    }

    public void deleteById(Long id) {
        feedbackRepo.deleteById(id);
    }
}





