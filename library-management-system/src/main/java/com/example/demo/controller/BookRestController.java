package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Book;
import com.example.demo.model.Category;
import com.example.demo.model.Issue;
import com.example.demo.model.User;
import com.example.demo.service.BookService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:4200")
public class BookRestController {

    @Autowired
    private BookService bookService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private com.example.demo.service.ActivityLogService activityLogService;

    @Autowired
    private com.example.demo.service.UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Book>>> getAllBooks() {
        List<Book> books = bookService.findAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Books retrieved successfully", books));
    }

    @PostMapping("/seed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> seedBooks() {
        // Note: Allow seeding even if books exist (for testing purposes)
        // If you want to prevent re-seeding, uncomment the check below:
        // if (!bookService.findAll().isEmpty()) {
        //     return ResponseEntity.ok(new ApiResponse<>(false, "Books already exist. Delete existing books first.", null));
        // }

        // Create 10 sample books
        String[][] bookData = {
            {"The Great Gatsby", "F. Scott Fitzgerald", "Fiction", "5"},
            {"To Kill a Mockingbird", "Harper Lee", "Fiction", "7"},
            {"1984", "George Orwell", "Science Fiction", "6"},
            {"Pride and Prejudice", "Jane Austen", "Romance", "8"},
            {"The Catcher in the Rye", "J.D. Salinger", "Fiction", "4"},
            {"Lord of the Flies", "William Golding", "Fiction", "5"},
            {"Animal Farm", "George Orwell", "Political Satire", "6"},
            {"Brave New World", "Aldous Huxley", "Science Fiction", "5"},
            {"The Hobbit", "J.R.R. Tolkien", "Fantasy", "9"},
            {"Jane Eyre", "Charlotte Bronte", "Romance", "7"}
        };

        for (String[] data : bookData) {
            Book book = new Book();
            book.setTitle(data[0]);
            book.setAuthor(data[1]);
            book.setGenre(data[2]);
            int quantity = Integer.parseInt(data[3]);
            book.setQuantity(quantity);
            book.setAvailable(quantity);
            bookService.save(book);
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "10 books seeded successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable Long id) {
        Book book = bookService.findById(id);
        if (book != null) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Book retrieved successfully", book));
        }
        return ResponseEntity.ok(new ApiResponse<>(false, "Book not found", null));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Book>> createBook(@RequestBody Book book) {
        if (book.getCategory() != null && book.getCategory().getId() != null) {
            Category category = categoryService.findById(book.getCategory().getId());
            book.setCategory(category);
        }
        book.setAvailable(book.getQuantity());
        Book savedBook = bookService.save(book);
        
        // Log activity
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findByUsername(auth.getName());
        if (admin != null) {
            activityLogService.logBookAdded(savedBook, admin);
        }
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Book created successfully", savedBook));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Book>> updateBook(@PathVariable Long id, @RequestBody Book book) {
        Book existingBook = bookService.findById(id);
        if (existingBook == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Book not found", null));
        }

        int delta = book.getQuantity() - existingBook.getQuantity();
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setGenre(book.getGenre());
        existingBook.setQuantity(book.getQuantity());
        existingBook.setAvailable(Math.max(0, existingBook.getAvailable() + delta));

        if (book.getCategory() != null && book.getCategory().getId() != null) {
            Category category = categoryService.findById(book.getCategory().getId());
            existingBook.setCategory(category);
        }

        Book updatedBook = bookService.save(existingBook);
        return ResponseEntity.ok(new ApiResponse<>(true, "Book updated successfully", updatedBook));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        Book book = bookService.findById(id);
        if (book == null) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Book not found", null));
        }

        List<Issue> activeIssues = issueService.findActiveIssuesByBook(book);
        if (!activeIssues.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>(false, 
                "Cannot delete book: Book is currently issued to " + activeIssues.size() + " student(s)", null));
        }

        // Log activity before deletion
        org.springframework.security.core.Authentication auth = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        User admin = userService.findByUsername(auth.getName());
        if (admin != null) {
            activityLogService.logBookDeleted(book, admin);
        }
        
        bookService.deleteById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Book deleted successfully", null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Book>>> searchBooks(@RequestParam(required = false) String title,
                                                               @RequestParam(required = false) String author,
                                                               @RequestParam(required = false) String genre) {
        List<Book> allBooks = bookService.findAll();
        
        if (title != null && !title.isEmpty()) {
            allBooks = allBooks.stream()
                    .filter(b -> b.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .toList();
        }
        if (author != null && !author.isEmpty()) {
            allBooks = allBooks.stream()
                    .filter(b -> b.getAuthor().toLowerCase().contains(author.toLowerCase()))
                    .toList();
        }
        if (genre != null && !genre.isEmpty()) {
            allBooks = allBooks.stream()
                    .filter(b -> b.getGenre() != null && b.getGenre().toLowerCase().contains(genre.toLowerCase()))
                    .toList();
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Search results retrieved", allBooks));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<Book>>> getAvailableBooks() {
        List<Book> books = bookService.findAll().stream()
                .filter(b -> b.getAvailable() > 0)
                .toList();
        return ResponseEntity.ok(new ApiResponse<>(true, "Available books retrieved", books));
    }
}


