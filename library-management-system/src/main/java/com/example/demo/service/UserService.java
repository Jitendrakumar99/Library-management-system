package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    public User save(User user) { return userRepo.save(user); }
    public User findById(Long id) { return userRepo.findById(id).orElse(null); }
    public User findByUsername(String username) { return userRepo.findByUsername(username); }
    public User findByRollNumber(String rollNumber) { return userRepo.findByRollNumber(rollNumber); }
    public User findByEmail(String email) { return userRepo.findByEmail(email); }
    public User findByUsernameOrRollNumberOrEmail(String identifier) {
        // Try to find user by username first
        User user = userRepo.findByUsername(identifier);
        if (user != null) return user;
        
        // Try to find by roll number
        user = userRepo.findByRollNumber(identifier);
        if (user != null) return user;
        
        // Try to find by email
        user = userRepo.findByEmail(identifier);
        return user;
    }
    public List<User> findAllStudents() { return userRepo.findByRole("STUDENT"); }
    public List<User> findAll() { return userRepo.findAll(); }
    public List<User> findPendingUsers() { return userRepo.findByStatus("PENDING"); }
    public void deleteById(Long id) { userRepo.deleteById(id); }
}
