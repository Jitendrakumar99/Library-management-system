package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body) {
        if (mailSender == null || fromEmail == null || fromEmail.trim().isEmpty()) {
            System.out.println("Email service not configured. Would send email to: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + body);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (fromEmail != null && !fromEmail.trim().isEmpty()) {
                message.setFrom(fromEmail);
            }
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendBookIssuedConfirmation(String to, String bookTitle, String issueDate, String returnDate) {
        String subject = "Book Issued - Library Management System";
        String body = String.format(
            "Dear Student,\n\n" +
            "Your book request has been processed.\n\n" +
            "Book: %s\n" +
            "Issue Date: %s\n" +
            "Due Date: %s\n\n" +
            "Please return the book on or before the due date to avoid fines.\n\n" +
            "Thank you,\n" +
            "Library Management System",
            bookTitle, issueDate, returnDate
        );
        sendEmail(to, subject, body);
    }

    public void sendBookReminder(String to, String bookTitle, String returnDate, long daysRemaining) {
        String subject = "Book Return Reminder - Library Management System";
        String body = String.format(
            "Dear Student,\n\n" +
            "This is a reminder that you have a book due soon.\n\n" +
            "Book: %s\n" +
            "Due Date: %s\n" +
            "Days Remaining: %d\n\n" +
            "Please return the book on time to avoid fines.\n\n" +
            "Thank you,\n" +
            "Library Management System",
            bookTitle, returnDate, daysRemaining
        );
        sendEmail(to, subject, body);
    }

    public void sendOverdueAlert(String to, String bookTitle, String returnDate, int fineAmount) {
        String subject = "Overdue Book Alert - Library Management System";
        String body = String.format(
            "Dear Student,\n\n" +
            "You have an overdue book that needs to be returned immediately.\n\n" +
            "Book: %s\n" +
            "Due Date: %s\n" +
            "Current Fine: ₹%d\n\n" +
            "Please return the book as soon as possible to avoid additional fines.\n\n" +
            "Thank you,\n" +
            "Library Management System",
            bookTitle, returnDate, fineAmount
        );
        sendEmail(to, subject, body);
    }

    public void sendAccountApprovalNotification(String to, String username) {
        String subject = "Account Approved - Library Management System";
        String body = String.format(
            "Dear %s,\n\n" +
            "Your account has been approved by the administrator.\n\n" +
            "You can now login and access the library system.\n\n" +
            "Thank you,\n" +
            "Library Management System",
            username
        );
        sendEmail(to, subject, body);
    }

    public void sendContactEmail(String to, String from, String name, String subject, String message) {
        String emailSubject = "Contact Form: " + subject;
        String body = String.format(
            "You have received a new contact form submission:\n\n" +
            "Name: %s\n" +
            "Email: %s\n" +
            "Subject: %s\n\n" +
            "Message:\n%s\n\n" +
            "---\n" +
            "This email was sent from the Library Management System contact form.",
            name, from, subject, message
        );
        sendEmail(to, emailSubject, body);
    }
}
