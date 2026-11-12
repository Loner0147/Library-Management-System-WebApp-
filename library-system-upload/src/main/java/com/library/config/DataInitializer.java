package com.library.config;

import com.library.model.Book;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedSampleBooks();
    }

    private void seedAdmin() {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .firstName("Library")
                    .lastName("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles(Set.of("ROLE_USER", "ROLE_ADMIN"))
                    .authProvider(User.AuthProvider.LOCAL)
                    .enabled(true)
                    .emailVerified(true)
                    .build();
            userRepository.save(admin);
            log.info("Admin user created: {}", adminEmail);
        }
    }

    private void seedSampleBooks() {
        if (bookRepository.count() == 0) {
            List<Book> books = List.of(
                Book.builder()
                    .title("Clean Code")
                    .author("Robert C. Martin")
                    .isbn("978-0132350884")
                    .description("A Handbook of Agile Software Craftsmanship — practical guide to writing clean, maintainable code.")
                    .genre("Technology")
                    .publisher("Prentice Hall")
                    .publicationYear(2008)
                    .language("English")
                    .pageCount(431)
                    .coverImageUrl("https://m.media-amazon.com/images/I/41xShlnTZTL._SX376_BO1,204,203,200_.jpg")
                    .documentUrl("https://example.com/docs/clean-code.pdf")
                    .tags(List.of("programming", "software", "best-practices"))
                    .totalCopies(3).availableCopies(3)
                    .status(Book.BookStatus.AVAILABLE)
                    .build(),
                Book.builder()
                    .title("The Great Gatsby")
                    .author("F. Scott Fitzgerald")
                    .isbn("978-0743273565")
                    .description("A novel of the Jazz Age that explores themes of wealth, class, love, and the American Dream.")
                    .genre("Fiction")
                    .publisher("Scribner")
                    .publicationYear(1925)
                    .language("English")
                    .pageCount(180)
                    .coverImageUrl("https://m.media-amazon.com/images/I/71FTb9X6wsL._AC_UF1000,1000_QL80_.jpg")
                    .documentUrl("https://example.com/docs/great-gatsby.pdf")
                    .tags(List.of("classic", "fiction", "american-literature"))
                    .totalCopies(2).availableCopies(2)
                    .status(Book.BookStatus.AVAILABLE)
                    .build(),
                Book.builder()
                    .title("Sapiens: A Brief History of Humankind")
                    .author("Yuval Noah Harari")
                    .isbn("978-0062316097")
                    .description("A sweeping narrative of human history from Stone Age to the present.")
                    .genre("History")
                    .publisher("Harper")
                    .publicationYear(2015)
                    .language("English")
                    .pageCount(464)
                    .coverImageUrl("https://m.media-amazon.com/images/I/713jIoMO3UL._AC_UF1000,1000_QL80_.jpg")
                    .documentUrl("https://example.com/docs/sapiens.pdf")
                    .tags(List.of("history", "science", "anthropology"))
                    .totalCopies(4).availableCopies(4)
                    .status(Book.BookStatus.AVAILABLE)
                    .build(),
                Book.builder()
                    .title("Design Patterns")
                    .author("Gang of Four")
                    .isbn("978-0201633610")
                    .description("Elements of Reusable Object-Oriented Software — the classic patterns reference.")
                    .genre("Technology")
                    .publisher("Addison-Wesley")
                    .publicationYear(1994)
                    .language("English")
                    .pageCount(395)
                    .coverImageUrl("https://m.media-amazon.com/images/I/51szD9HC9pL._SX395_BO1,204,203,200_.jpg")
                    .documentUrl("https://example.com/docs/design-patterns.pdf")
                    .tags(List.of("programming", "patterns", "oop"))
                    .totalCopies(2).availableCopies(2)
                    .status(Book.BookStatus.AVAILABLE)
                    .build(),
                Book.builder()
                    .title("To Kill a Mockingbird")
                    .author("Harper Lee")
                    .isbn("978-0061935466")
                    .description("A Pulitzer Prize-winning novel about racial injustice and moral growth in the American South.")
                    .genre("Fiction")
                    .publisher("HarperCollins")
                    .publicationYear(1960)
                    .language("English")
                    .pageCount(336)
                    .coverImageUrl("https://m.media-amazon.com/images/I/71FxgtFKcQL._AC_UF1000,1000_QL80_.jpg")
                    .documentUrl("https://example.com/docs/mockingbird.pdf")
                    .tags(List.of("classic", "fiction", "social-justice"))
                    .totalCopies(3).availableCopies(3)
                    .status(Book.BookStatus.AVAILABLE)
                    .build(),
                Book.builder()
                    .title("Atomic Habits")
                    .author("James Clear")
                    .isbn("978-0735211292")
                    .description("An easy and proven way to build good habits and break bad ones.")
                    .genre("Self-Help")
                    .publisher("Avery")
                    .publicationYear(2018)
                    .language("English")
                    .pageCount(320)
                    .coverImageUrl("https://m.media-amazon.com/images/I/81wgcld4wxL._AC_UF1000,1000_QL80_.jpg")
                    .documentUrl("https://example.com/docs/atomic-habits.pdf")
                    .tags(List.of("self-help", "productivity", "psychology"))
                    .totalCopies(5).availableCopies(5)
                    .status(Book.BookStatus.AVAILABLE)
                    .build()
            );
            bookRepository.saveAll(books);
            log.info("Sample books seeded: {} books", books.size());
        }
    }
}
