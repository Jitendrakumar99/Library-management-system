package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ContactRequest;
import com.example.demo.model.Contact;
import com.example.demo.service.ContactService;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "http://localhost:4200")
public class ContactRestController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ContactService contactService;

    @Value("${spring.mail.username:}")
    private String adminEmail;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendContactEmail(@RequestBody ContactRequest contactRequest) {
        try {
            // Validate request
            if (contactRequest.getName() == null || contactRequest.getName().trim().isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(false, "Name is required", null));
            }
            if (contactRequest.getEmail() == null || contactRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(false, "Email is required", null));
            }
            if (contactRequest.getSubject() == null || contactRequest.getSubject().trim().isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(false, "Subject is required", null));
            }
            if (contactRequest.getMessage() == null || contactRequest.getMessage().trim().isEmpty()) {
                return ResponseEntity.ok(new ApiResponse<>(false, "Message is required", null));
            }

            // Save contact submission to database
            Contact contact = new Contact(
                contactRequest.getName(),
                contactRequest.getEmail(),
                contactRequest.getSubject(),
                contactRequest.getMessage()
            );
            contactService.save(contact);

            // Send email to admin/library
            String adminEmailToUse = adminEmail != null && !adminEmail.trim().isEmpty() 
                ? adminEmail 
                : "library@example.com"; // Default admin email
            
            String emailSubject = "Contact Form: " + contactRequest.getSubject();
            String emailBody = String.format(
                "You have received a new contact form submission:\n\n" +
                "Name: %s\n" +
                "Email: %s\n" +
                "Subject: %s\n\n" +
                "Message:\n%s\n\n" +
                "---\n" +
                "This email was sent from the Library Management System contact form.",
                contactRequest.getName(),
                contactRequest.getEmail(),
                contactRequest.getSubject(),
                contactRequest.getMessage()
            );

            emailService.sendEmail(adminEmailToUse, emailSubject, emailBody);

            // Send confirmation email to user
            String confirmationSubject = "Thank You for Contacting Us - Library Management System";
            String confirmationBody = String.format(
                "Dear %s,\n\n" +
                "Thank you for contacting us. We have received your message:\n\n" +
                "Subject: %s\n\n" +
                "We will get back to you as soon as possible.\n\n" +
                "Best regards,\n" +
                "Library Management System Team",
                contactRequest.getName(),
                contactRequest.getSubject()
            );

            emailService.sendEmail(contactRequest.getEmail(), confirmationSubject, confirmationBody);

            return ResponseEntity.ok(new ApiResponse<>(true, "Your message has been sent successfully. We will get back to you soon.", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Failed to send message. Please try again later.", null));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Contact>>> getAllContacts() {
        List<Contact> contacts = contactService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Contact messages retrieved successfully", contacts));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteContact(@PathVariable Long id) {
        try {
            contactService.deleteById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Contact message deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Failed to delete contact message", null));
        }
    }
}

