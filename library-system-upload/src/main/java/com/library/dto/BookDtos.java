package com.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class BookDtos {

    @Data
    public static class BookRequest {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Author is required")
        private String author;

        private String isbn;
        private String description;
        private String genre;
        private String publisher;
        private Integer publicationYear;
        private String language;
        private Integer pageCount;
        private String coverImageUrl;
        private String documentUrl;
        private List<String> tags = new ArrayList<>();
        private int totalCopies = 1;
    }

    @Data
    public static class BookResponse {
        private String id;
        private String title;
        private String author;
        private String isbn;
        private String description;
        private String genre;
        private String publisher;
        private Integer publicationYear;
        private String language;
        private Integer pageCount;
        private String coverImageUrl;
        private String documentUrl;
        private String status;
        private List<String> tags;
        private int totalCopies;
        private int availableCopies;
        private String createdAt;
    }

    @Data
    public static class BorrowRequest {
        @NotBlank
        private String bookId;
    }
}
