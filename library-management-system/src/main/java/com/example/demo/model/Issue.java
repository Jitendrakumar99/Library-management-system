package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties({"category"})
    private Book book;

    @ManyToOne
    private User student;

    private LocalDate issueDate;
    private LocalDate returnDate;
    private boolean returned;

    private int fine; // ✅ New field for fine

    private boolean returnArchived = false; // default false
    
    @Column(length = 20)
    private String status = "APPROVED"; // PENDING, APPROVED, REJECTED
    
    private LocalDate requestedDate; // Date when student requested the book

    public boolean isReturnArchived() { return returnArchived; }
    public void setReturnArchived(boolean returnArchived) { this.returnArchived = returnArchived; }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isReturned() { return returned; }
    public void setReturned(boolean returned) { this.returned = returned; }

    public int getFine() { return fine; }
    public void setFine(int fine) { this.fine = fine; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDate requestedDate) { this.requestedDate = requestedDate; }
}
