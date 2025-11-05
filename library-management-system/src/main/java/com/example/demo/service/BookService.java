package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepo;

    public Book save(Book book) { return bookRepo.save(book); }
    public Book findById(Long id) { return bookRepo.findById(id).orElse(null); }
    public void deleteById(Long id) { bookRepo.deleteById(id); }
    public List<Book> findAll() { return bookRepo.findAll(); }
}
