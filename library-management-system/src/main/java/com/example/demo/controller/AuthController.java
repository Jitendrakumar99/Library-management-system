package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        // Find user by username, roll number, or email
        User user = userService.findByUsernameOrRollNumberOrEmail(authRequest.getUsername());
        
        if (user == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Invalid username, roll number, email, or password"));
        }
        
        try {
            // Authenticate using the actual username (Spring Security uses username)
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Invalid username, roll number, email, or password"));
        }
        
        // Re-fetch user to ensure we have the latest data
        user = userService.findByUsername(user.getUsername());
        
        // Check if user is approved
        if (user.getStatus() == null || "PENDING".equals(user.getStatus())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Your account is pending approval. Please wait for admin approval."));
        }
        
        if ("REJECTED".equals(user.getStatus())) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Your account has been rejected. Please contact admin."));
        }
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());

        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole(), user.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        // Validate required fields
        if (registerRequest.getUsername() == null || registerRequest.getUsername().isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Username is required"));
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Password is required"));
        }
        if (registerRequest.getRollNumber() == null || registerRequest.getRollNumber().isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Roll number is required"));
        }
        
        User existingUser = userService.findByUsername(registerRequest.getUsername());
        if (existingUser != null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Username already exists"));
        }

        // Check if roll number already exists
        User existingRollNumber = userService.findByRollNumber(registerRequest.getRollNumber());
        if (existingRollNumber != null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Roll number already exists"));
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole("STUDENT");
        user.setStatus("PENDING"); // New registrations need admin approval
        user.setRollNumber(registerRequest.getRollNumber()); // Roll number is required
        if (registerRequest.getEmail() != null && !registerRequest.getEmail().isEmpty()) {
            user.setEmail(registerRequest.getEmail());
        }
        
        User savedUser = userService.save(user);
        // Don't generate token for pending users - they need approval
        return ResponseEntity.ok(new ApiResponse<>(true, "Registration submitted. Please wait for admin approval.", null));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                if (jwtUtil.validateToken(token, username)) {
                    return ResponseEntity.ok(new ApiResponse<>(true, "Token is valid", 
                        new AuthResponse(token, username, role, null)));
                }
            } catch (Exception e) {
                return ResponseEntity.ok(new ApiResponse<>(false, "Invalid token"));
            }
        }
        return ResponseEntity.ok(new ApiResponse<>(false, "Token is invalid"));
    }
}

