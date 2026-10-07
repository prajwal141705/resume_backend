package com.example.resumebuilder.service;

import com.example.resumebuilder.config.JwtTokenProvider;
import com.example.resumebuilder.config.UserPrincipal;
import com.example.resumebuilder.exception.BadRequestException;
import com.example.resumebuilder.exception.ForbiddenException;
import com.example.resumebuilder.model.User;
import com.example.resumebuilder.model.dto.AuthResponse;
import com.example.resumebuilder.model.dto.LoginRequest;
import com.example.resumebuilder.model.dto.RegisterRequest;
import com.example.resumebuilder.model.dto.UserDto;
import com.example.resumebuilder.repository.ResumeRepository;
import com.example.resumebuilder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("Registration attempt failed: email '{}' already in use", normalizedEmail);
            throw new BadRequestException("Email is already registered");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of("ROLE_USER"))
                .status("ACTIVE")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        String token = tokenProvider.generateTokenFromUserIdAndEmail(
                savedUser.getId(),
                savedUser.getEmail(),
                "ROLE_USER"
        );

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToDto(savedUser))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if ("INACTIVE".equalsIgnoreCase(user.getStatus()) || "SUSPENDED".equalsIgnoreCase(user.getStatus())) {
            log.warn("Login blocked for deactivated account: {}", normalizedEmail);
            throw new ForbiddenException("Your account has been deactivated. Please contact an administrator.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        log.info("User logged in successfully: {}", user.getId());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(mapToDto(user))
                .build();
    }

    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ForbiddenException("No authenticated user found in security context");
        }

        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }

        throw new ForbiddenException("Invalid authentication principal");
    }

    public UserDto getCurrentUserDto() {
        String currentUserId = getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new com.example.resumebuilder.exception.ResourceNotFoundException("User not found with id: " + currentUserId));
        return mapToDto(user);
    }

    public UserDto mapToDto(User user) {
        long count = resumeRepository.countByUserId(user.getId());
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus() != null ? user.getStatus() : "ACTIVE")
                .roles(user.getRoles())
                .resumeCount(count)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
