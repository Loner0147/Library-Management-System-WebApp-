package com.library.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDtos {

    @Data
    public static class LoginRequest {
        @NotBlank @Email
        private String email;
        @NotBlank
        private String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String firstName;
        @NotBlank
        private String lastName;
        @NotBlank @Email
        private String email;
        @NotBlank @Size(min = 8)
        private String password;
    }

    @Data
    public static class JwtResponse {
        private String token;
        private String type = "Bearer";
        private String refreshToken;
        private String id;
        private String email;
        private String firstName;
        private String lastName;
        private java.util.List<String> roles;

        public JwtResponse(String token, String refreshToken, String id,
                           String email, String firstName, String lastName,
                           java.util.List<String> roles) {
            this.token = token;
            this.refreshToken = refreshToken;
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.roles = roles;
        }
    }

    @Data
    public static class TokenRefreshRequest {
        @NotBlank
        private String refreshToken;
    }

    @Data
    public static class MessageResponse {
        private String message;
        public MessageResponse(String message) { this.message = message; }
    }
}
