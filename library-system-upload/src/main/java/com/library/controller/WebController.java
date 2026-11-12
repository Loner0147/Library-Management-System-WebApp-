package com.library.controller;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.BookService;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final BookService bookService;
    private final UserService userService;

    // ---------- Auth Pages ----------

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    // ---------- Root ----------

    @GetMapping("/")
    public String home() {
        return "redirect:/books";
    }

    // ---------- Books ----------

    @GetMapping("/books")
    public String booksPage(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "12") int size,
                             @RequestParam(required = false) String search,
                             @RequestParam(required = false) String genre,
                             Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Page<Book> booksPage;
        if (search != null && !search.isBlank()) {
            booksPage = bookService.searchBooks(search, page, size);
            model.addAttribute("search", search);
        } else if (genre != null && !genre.isBlank()) {
            booksPage = bookService.getBooksByGenre(genre, page, size);
            model.addAttribute("selectedGenre", genre);
        } else {
            booksPage = bookService.getAllBooks(page, size);
        }

        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());
        model.addAttribute("totalItems", booksPage.getTotalElements());
        model.addAttribute("genres", bookService.getAllGenres());

        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAdmin", currentUser.getRoles().contains("ROLE_ADMIN"));

        return "books/list";
    }

    @GetMapping("/books/{id}")
    public String bookDetailPage(@PathVariable String id, Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Book book = bookService.getBookById(id);
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        boolean isBorrowed = book.getBorrowHistory().stream()
                .anyMatch(r -> r.getUserId().equals(currentUser.getId()) && !r.isReturned());
        model.addAttribute("book", book);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAdmin", currentUser.getRoles().contains("ROLE_ADMIN"));
        model.addAttribute("isBorrowed", isBorrowed);
        return "books/detail";
    }

    // ---------- Admin Pages ----------

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalBooks", bookService.getTotalBooks());
        model.addAttribute("availableBooks", bookService.getAvailableBooks());
        model.addAttribute("borrowedBooks", bookService.getBorrowedBooks());
        model.addAttribute("totalUsers", userService.getTotalUsers());
        model.addAttribute("recentBooks", bookService.getRecentBooks());
        return "admin/dashboard";
    }

    @GetMapping("/admin/books")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminBooksPage(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "12") int size,
                                  @RequestParam(required = false) String search,
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Page<Book> booksPage = (search != null && !search.isBlank())
                ? bookService.searchBooks(search, page, size)
                : bookService.getAllBooks(page, size);
        model.addAttribute("books", booksPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", booksPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("currentUser", userService.getCurrentUser(userDetails.getUsername()));
        return "admin/books";
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminUsersPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("currentUser", userService.getCurrentUser(userDetails.getUsername()));
        return "admin/users";
    }

    // ---------- Profile ----------

    @GetMapping("/profile")
    public String profilePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        List<Book> borrowedBooks = bookService.getUserBorrowedBooks(userDetails.getUsername());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAdmin", currentUser.getRoles().contains("ROLE_ADMIN"));
        model.addAttribute("borrowedBooks", borrowedBooks);
        return "user/profile";
    }
}
