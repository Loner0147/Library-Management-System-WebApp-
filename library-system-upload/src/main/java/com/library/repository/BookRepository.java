package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String title, String author, Pageable pageable);
    Page<Book> findByGenreIgnoreCase(String genre, Pageable pageable);
    Page<Book> findByStatus(Book.BookStatus status, Pageable pageable);
    List<Book> findTop8ByOrderByCreatedAtDesc();

    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'author': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } }, { 'tags': { $regex: ?0, $options: 'i' } } ] }")
    Page<Book> searchBooks(String query, Pageable pageable);

    List<Book> findByGenreIgnoreCase(String genre);
    long countByStatus(Book.BookStatus status);
}
