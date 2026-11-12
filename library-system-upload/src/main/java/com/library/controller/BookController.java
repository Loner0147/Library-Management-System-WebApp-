package com.library.controller;

import com.library.dto.BookDtos.*;
import com.library.model.Book;
import com.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Page<Book>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(bookService.searchBooks(search, page, size));
        }
        if (genre != null && !genre.isBlank()) {
            return ResponseEntity.ok(bookService.getBooksByGenre(genre, page, size));
        }
        return ResponseEntity.ok(bookService.getAllBooks(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable String id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> addBook(@Valid @RequestBody BookRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookService.addBook(request, userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> updateBook(@PathVariable String id,
                                           @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(Map.of("message", "Book deleted successfully"));
    }

    @PostMapping("/{id}/borrow")
    public ResponseEntity<Map<String, String>> borrowBook(@PathVariable String id,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        String msg = bookService.borrowBook(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", msg));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<Map<String, String>> returnBook(@PathVariable String id,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        String msg = bookService.returnBook(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", msg));
    }

    @GetMapping("/my-borrowed")
    public ResponseEntity<List<Book>> getMyBorrowedBooks(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(bookService.getUserBorrowedBooks(userDetails.getUsername()));
    }

    @GetMapping("/genres")
    public ResponseEntity<List<String>> getGenres() {
        return ResponseEntity.ok(bookService.getAllGenres());
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Book>> getRecentBooks() {
        return ResponseEntity.ok(bookService.getRecentBooks());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(Map.of(
                "total", bookService.getTotalBooks(),
                "available", bookService.getAvailableBooks(),
                "borrowed", bookService.getBorrowedBooks()
        ));
    }
}
