package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.Issue;
import com.example.demo.model.User;
import com.example.demo.repository.IssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    public Issue findById(Long id) {
        return issueRepository.findById(id).orElse(null);
    }

    public Issue save(Issue issue) {
        return issueRepository.save(issue);
    }

    public List<Issue> findAll() {
        return issueRepository.findAll();
    }

    public List<Issue> findByStudent(User student) {
        return issueRepository.findByStudent(student);
    }
    
    public List<Issue> findAllReturned() {
        return issueRepository.findByReturnedTrue();
    }

    public List<Issue> findActiveIssuesByBook(Book book) {
        return issueRepository.findByBookAndReturnedFalse(book);
    }
    
    public List<Issue> findPendingRequests() {
        return issueRepository.findByStatus("PENDING");
    }
    
    public List<Issue> findPendingRequestsByStudent(User student) {
        return issueRepository.findByStudentAndStatus(student, "PENDING");
    }

}
