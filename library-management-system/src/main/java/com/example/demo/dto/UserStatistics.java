package com.example.demo.dto;

import com.example.demo.model.User;

public class UserStatistics {
    private User user;
    private long totalIssuedBooks;
    private long activeIssuedBooks;
    private long returnedBooks;
    private int totalFine;
    private int pendingFine; // Fine for active issues

    public UserStatistics() {}

    public UserStatistics(User user, long totalIssuedBooks, long activeIssuedBooks, long returnedBooks, int totalFine, int pendingFine) {
        this.user = user;
        this.totalIssuedBooks = totalIssuedBooks;
        this.activeIssuedBooks = activeIssuedBooks;
        this.returnedBooks = returnedBooks;
        this.totalFine = totalFine;
        this.pendingFine = pendingFine;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getTotalIssuedBooks() {
        return totalIssuedBooks;
    }

    public void setTotalIssuedBooks(long totalIssuedBooks) {
        this.totalIssuedBooks = totalIssuedBooks;
    }

    public long getActiveIssuedBooks() {
        return activeIssuedBooks;
    }

    public void setActiveIssuedBooks(long activeIssuedBooks) {
        this.activeIssuedBooks = activeIssuedBooks;
    }

    public long getReturnedBooks() {
        return returnedBooks;
    }

    public void setReturnedBooks(long returnedBooks) {
        this.returnedBooks = returnedBooks;
    }

    public int getTotalFine() {
        return totalFine;
    }

    public void setTotalFine(int totalFine) {
        this.totalFine = totalFine;
    }

    public int getPendingFine() {
        return pendingFine;
    }

    public void setPendingFine(int pendingFine) {
        this.pendingFine = pendingFine;
    }
}

