package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Category;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryRestController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Categories retrieved successfully", categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        if (category != null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Category retrieved successfully", category));
        }
        return ResponseEntity.ok(new ApiResponse<>(false, "Category not found", null));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        Category existingCategory = categoryService.findByName(category.getName());
        if (existingCategory != null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Category name already exists", null));
        }
        Category savedCategory = categoryService.save(category);
        return ResponseEntity.ok(new ApiResponse<>(true, "Category created successfully", savedCategory));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category existingCategory = categoryService.findById(id);
        if (existingCategory == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Category not found", null));
        }

        Category categoryWithSameName = categoryService.findByName(category.getName());
        if (categoryWithSameName != null && !categoryWithSameName.getId().equals(id)) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Category name already exists", null));
        }

        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        Category updatedCategory = categoryService.save(existingCategory);
        return ResponseEntity.ok(new ApiResponse<>(true, "Category updated successfully", updatedCategory));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Category not found", null));
        }

        if (category.getBooks() != null && !category.getBooks().isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(false, 
                "Cannot delete category: It has associated books", null));
        }

        categoryService.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Category deleted successfully", null));
    }
}





