package com.example.demo.service;

import com.example.demo.model.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;

    public Category save(Category category) {
        return categoryRepo.save(category);
    }

    public Category findById(Long id) {
        return categoryRepo.findById(id).orElse(null);
    }

    public Category findByName(String name) {
        return categoryRepo.findByName(name);
    }

    public List<Category> findAll() {
        return categoryRepo.findAll();
    }

    public void deleteById(Long id) {
        categoryRepo.deleteById(id);
    }
}





