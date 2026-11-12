package com.library.controller;

import com.library.dto.AuthDtos.MessageResponse;
import com.library.model.User;
import com.library.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        User user = userService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                           Authentication authentication) {
        userService.updateProfile(authentication.getName(), request.getFirstName(),
                request.getLastName(), request.getProfileImageUrl());
        return ResponseEntity.ok(new MessageResponse("Profile updated successfully"));
    }

    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                            Authentication authentication) {
        userService.changePassword(authentication.getName(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
    }

    @Data
    public static class UpdateProfileRequest {
        @NotBlank(message = "First name is required")
        @Size(max = 50)
        private String firstName;

        @NotBlank(message = "Last name is required")
        @Size(max = 50)
        private String lastName;

        private String profileImageUrl;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank(message = "Current password is required")
        private String currentPassword;

        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String newPassword;
    }
}
