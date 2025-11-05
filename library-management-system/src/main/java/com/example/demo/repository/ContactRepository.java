package com.example.demo.repository;

import com.example.demo.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT c FROM Contact c ORDER BY c.createdAt DESC")
    List<Contact> findAllOrderedByCreatedAtDesc();
}

