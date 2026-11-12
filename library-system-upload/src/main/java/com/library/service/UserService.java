package com.library.service;

import com.library.model.User;
import com.library.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public User updateProfile(String email, String firstName, String lastName, String profileImageUrl) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            user.setProfileImageUrl(profileImageUrl);
        }
        return userRepository.save(user);
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User promoteToAdmin(String userId) {
        User user = getUserById(userId);
        user.getRoles().add("ROLE_ADMIN");
        return userRepository.save(user);
    }

    public User demoteFromAdmin(String userId) {
        User user = getUserById(userId);
        user.getRoles().remove("ROLE_ADMIN");
        user.setRoles(Set.of("ROLE_USER"));
        return userRepository.save(user);
    }

    public void toggleUserStatus(String userId) {
        User user = getUserById(userId);
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public long getTotalUsers() { return userRepository.count(); }
}
