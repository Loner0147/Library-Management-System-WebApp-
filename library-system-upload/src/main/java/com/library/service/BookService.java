package com.library.service;

import com.library.dto.BookDtos.*;
import com.library.model.Book;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public Book addBook(BookRequest request, String addedById) {
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .isbn(request.getIsbn())
                .description(request.getDescription())
                .genre(request.getGenre())
                .publisher(request.getPublisher())
                .publicationYear(request.getPublicationYear())
                .language(request.getLanguage())
                .pageCount(request.getPageCount())
                .coverImageUrl(request.getCoverImageUrl())
                .documentUrl(request.getDocumentUrl())
                .tags(request.getTags())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .status(Book.BookStatus.AVAILABLE)
                .addedById(addedById)
                .build();
        return bookRepository.save(book);
    }

    public Book updateBook(String id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setDescription(request.getDescription());
        book.setGenre(request.getGenre());
        book.setPublisher(request.getPublisher());
        book.setPublicationYear(request.getPublicationYear());
        book.setLanguage(request.getLanguage());
        book.setPageCount(request.getPageCount());
        book.setCoverImageUrl(request.getCoverImageUrl());
        book.setDocumentUrl(request.getDocumentUrl());
        book.setTags(request.getTags());
        book.setTotalCopies(request.getTotalCopies());
        return bookRepository.save(book);
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }

    public Book getBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found: " + id));
    }

    public Page<Book> getAllBooks(int page, int size) {
        return bookRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    public Page<Book> searchBooks(String query, int page, int size) {
        return bookRepository.searchBooks(query, PageRequest.of(page, size));
    }

    public Page<Book> getBooksByGenre(String genre, int page, int size) {
        return bookRepository.findByGenreIgnoreCase(genre, PageRequest.of(page, size));
    }

    public List<Book> getRecentBooks() {
        return bookRepository.findTop8ByOrderByCreatedAtDesc();
    }

    public String borrowBook(String bookId, String userEmail) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No copies available");
        }

        // Check if user already borrowed
        boolean alreadyBorrowed = book.getBorrowHistory().stream()
                .anyMatch(r -> r.getUserId().equals(user.getId()) && !r.isReturned());
        if (alreadyBorrowed) {
            throw new RuntimeException("You already borrowed this book");
        }

        Book.BorrowRecord record = Book.BorrowRecord.builder()
                .userId(user.getId())
                .userName(user.getFullName())
                .borrowedAt(LocalDateTime.now())
                .returned(false)
                .build();

        book.getBorrowHistory().add(record);
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        if (book.getAvailableCopies() == 0) {
            book.setStatus(Book.BookStatus.BORROWED);
        }
        bookRepository.save(book);
        return "Book borrowed successfully";
    }

    public String returnBook(String bookId, String userEmail) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        book.getBorrowHistory().stream()
                .filter(r -> r.getUserId().equals(user.getId()) && !r.isReturned())
                .findFirst()
                .ifPresentOrElse(record -> {
                    record.setReturned(true);
                    record.setReturnedAt(LocalDateTime.now());
                    book.setAvailableCopies(book.getAvailableCopies() + 1);
                    if (book.getAvailableCopies() > 0) {
                        book.setStatus(Book.BookStatus.AVAILABLE);
                    }
                    bookRepository.save(book);
                }, () -> { throw new RuntimeException("No active borrow record found"); });

        return "Book returned successfully";
    }

    public List<Book> getUserBorrowedBooks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookRepository.findAll().stream()
                .filter(book -> book.getBorrowHistory().stream()
                        .anyMatch(r -> r.getUserId().equals(user.getId()) && !r.isReturned()))
                .collect(Collectors.toList());
    }

    public long getTotalBooks() { return bookRepository.count(); }
    public long getAvailableBooks() { return bookRepository.countByStatus(Book.BookStatus.AVAILABLE); }
    public long getBorrowedBooks() { return bookRepository.countByStatus(Book.BookStatus.BORROWED); }

    public List<String> getAllGenres() {
        return bookRepository.findAll().stream()
                .map(Book::getGenre)
                .filter(g -> g != null && !g.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
