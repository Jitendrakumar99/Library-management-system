package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;
        
        // Try to find user by username first
        user = userRepository.findByUsername(username);
        
        // If user not found by username, try roll number
        if (user == null) {
            user = userRepository.findByRollNumber(username);
        }
        
        // If still not found, try email
        if (user == null) {
            user = userRepository.findByEmail(username);
        }

        // If user not found, check if it's admin from properties
        if (user == null && username.equals(adminUsername)) {
            // Create admin user if not exists
            user = new User();
            user.setUsername(adminUsername);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRole("ADMIN");
            user.setStatus("APPROVED");
            userRepository.save(user);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Update admin password if needed (only if logging in with admin username)
        if (username.equals(adminUsername) && user.getUsername().equals(adminUsername) && 
            !passwordEncoder.matches(adminPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRole("ADMIN");
            user.setStatus("APPROVED");
            userRepository.save(user);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername()) // Always use username for Spring Security
                .password(user.getPassword())
                .authorities(getAuthorities(user.getRole()))
                .build();
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return authorities;
    }
}

