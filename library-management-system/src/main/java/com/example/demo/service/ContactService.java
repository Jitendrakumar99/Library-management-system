package com.example.demo.service;

import com.example.demo.model.Contact;
import com.example.demo.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }

    public Contact findById(Long id) {
        return contactRepository.findById(id).orElse(null);
    }

    public List<Contact> findAll() {
        return contactRepository.findAllOrderedByCreatedAtDesc();
    }

    public void deleteById(Long id) {
        contactRepository.deleteById(id);
    }
}

