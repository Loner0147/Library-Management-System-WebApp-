package com.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class Book {

    @Id
    private String id;

    private String title;

    private String author;

    @Indexed(unique = true)
    private String isbn;
    private String description;
    private String genre;
    private String publisher;
    private Integer publicationYear;
    private String language;
    private Integer pageCount;
    private String coverImageUrl;

    /** URL to the digital document (PDF, ePub, etc.) */
    private String documentUrl;

    @Builder.Default
    private BookStatus status = BookStatus.AVAILABLE;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private int totalCopies = 1;

    @Builder.Default
    private int availableCopies = 1;

    /** ID of user who added the book */
    private String addedById;

    @Builder.Default
    private List<BorrowRecord> borrowHistory = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum BookStatus {
        AVAILABLE, BORROWED, RESERVED, ARCHIVED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BorrowRecord {
        private String userId;
        private String userName;
        private LocalDateTime borrowedAt;
        private LocalDateTime returnedAt;
        private boolean returned;
    }
}
