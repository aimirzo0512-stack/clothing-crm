package com.clothingstore.crm.service;

import com.clothingstore.crm.dto.auth.AuthResponse;
import com.clothingstore.crm.dto.auth.LoginRequest;
import com.clothingstore.crm.dto.auth.RegisterRequest;
import com.clothingstore.crm.entity.Role;
import com.clothingstore.crm.entity.User;
import com.clothingstore.crm.exception.BadRequestException;
import com.clothingstore.crm.repository.UserRepository;
import com.clothingstore.crm.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuditService auditService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new BadRequestException("Username already taken");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new BadRequestException("Email already registered");
        }
        User user = User.builder()
                .username(req.username())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .fullName(req.fullName())
                .role(req.role() != null ? req.role() : Role.EMPLOYEE)
                .enabled(true)
                .build();
        userRepository.save(user);
        auditService.log("REGISTER", "User", user.getId(), "New user registered: " + user.getUsername());
        return buildResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new BadRequestException("User not found"));
        auditService.log("LOGIN", "User", user.getId(), user.getUsername() + " logged in");
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, "Bearer", user.getId(), user.getUsername(),
                user.getFullName(), user.getRole(), tokenProvider.getExpirationMs());
    }
}
