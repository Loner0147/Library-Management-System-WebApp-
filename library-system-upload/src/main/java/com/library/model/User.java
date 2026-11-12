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
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String profileImageUrl;

    @Builder.Default
    private Set<String> roles = new HashSet<>();

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean emailVerified = false;

    private AuthProvider authProvider;

    private String providerId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum AuthProvider {
        LOCAL, GOOGLE, GITHUB
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
