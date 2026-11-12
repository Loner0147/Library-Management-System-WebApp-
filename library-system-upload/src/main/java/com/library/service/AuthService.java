package com.library.service;

import com.library.dto.AuthDtos.*;
import com.library.model.RefreshToken;
import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new JwtResponse(jwt, refreshToken.getToken(), user.getId(),
                user.getEmail(), user.getFirstName(), user.getLastName(), roles);
    }

    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of("ROLE_USER"))
                .authProvider(User.AuthProvider.LOCAL)
                .enabled(true)
                .emailVerified(false)
                .build();

        userRepository.save(user);
        return new MessageResponse("User registered successfully!");
    }

    public JwtResponse refreshToken(String requestRefreshToken) {
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(token -> {
                    User user = userRepository.findById(token.getUserId()).orElseThrow();
                    String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());
                    return new JwtResponse(jwt, requestRefreshToken, user.getId(),
                            user.getEmail(), user.getFirstName(), user.getLastName(),
                            user.getRoles().stream().toList());
                })
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }
}
