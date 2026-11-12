package com.library.security.oauth;

import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.security.jwt.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        String email = extractEmail(oauthUser, registrationId);
        String name = extractName(oauthUser, registrationId);
        String providerId = extractProviderId(oauthUser, registrationId);
        String imageUrl = extractImageUrl(oauthUser, registrationId);
        User.AuthProvider provider = resolveProvider(registrationId);

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            String[] parts = (name != null ? name : "OAuth User").split(" ", 2);
            return userRepository.save(User.builder()
                    .email(email)
                    .firstName(parts[0])
                    .lastName(parts.length > 1 ? parts[1] : "")
                    .profileImageUrl(imageUrl)
                    .authProvider(provider)
                    .providerId(providerId)
                    .roles(Set.of("ROLE_USER"))
                    .enabled(true)
                    .emailVerified(true)
                    .build());
        });

        // Update provider info if changed
        if (!provider.equals(user.getAuthProvider())) {
            user.setAuthProvider(provider);
            user.setProviderId(providerId);
            userRepository.save(user);
        }

        String jwt = jwtUtils.generateTokenFromEmail(user.getEmail());

        Cookie jwtCookie = new Cookie("jwt", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60);
        response.addCookie(jwtCookie);

        log.info("OAuth2 login successful for: {}", email);
        response.sendRedirect("/books");
    }

    private String extractEmail(OAuth2User user, String registrationId) {
        Map<String, Object> attrs = user.getAttributes();
        if ("github".equals(registrationId)) {
            Object login = attrs.get("login");
            Object email = attrs.get("email");
            return email != null ? email.toString() : login + "@github.com";
        }
        return attrs.getOrDefault("email", "").toString();
    }

    private String extractName(OAuth2User user, String registrationId) {
        Map<String, Object> attrs = user.getAttributes();
        if ("github".equals(registrationId)) {
            Object name = attrs.get("name");
            return name != null ? name.toString() : attrs.getOrDefault("login", "").toString();
        }
        return attrs.getOrDefault("name", "").toString();
    }

    private String extractProviderId(OAuth2User user, String registrationId) {
        Map<String, Object> attrs = user.getAttributes();
        Object sub = attrs.get("sub");
        if (sub != null) return sub.toString();
        Object id = attrs.get("id");
        return id != null ? id.toString() : "";
    }

    private String extractImageUrl(OAuth2User user, String registrationId) {
        Map<String, Object> attrs = user.getAttributes();
        if ("github".equals(registrationId)) {
            return attrs.getOrDefault("avatar_url", "").toString();
        }
        return attrs.getOrDefault("picture", "").toString();
    }

    private User.AuthProvider resolveProvider(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> User.AuthProvider.GOOGLE;
            case "github" -> User.AuthProvider.GITHUB;
            default -> User.AuthProvider.LOCAL;
        };
    }
}
