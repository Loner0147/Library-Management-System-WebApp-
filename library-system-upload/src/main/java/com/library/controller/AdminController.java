package com.library.controller;

import com.library.model.User;
import com.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}/promote")
    public ResponseEntity<User> promoteToAdmin(@PathVariable String id) {
        return ResponseEntity.ok(userService.promoteToAdmin(id));
    }

    @PutMapping("/users/{id}/demote")
    public ResponseEntity<User> demoteFromAdmin(@PathVariable String id) {
        return ResponseEntity.ok(userService.demoteFromAdmin(id));
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<Map<String, String>> toggleUserStatus(@PathVariable String id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok(Map.of("message", "User status updated"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(Map.of("totalUsers", userService.getTotalUsers()));
    }
}
