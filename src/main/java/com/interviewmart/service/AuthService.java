package com.interviewmart.service;

import com.interviewmart.dto.auth.AuthResponse;
import com.interviewmart.dto.auth.LoginRequest;
import com.interviewmart.dto.auth.LogoutRequest;
import com.interviewmart.dto.auth.RefreshRequest;
import com.interviewmart.dto.auth.RegisterRequest;
import com.interviewmart.entity.RefreshToken;
import com.interviewmart.entity.Role;
import com.interviewmart.entity.User;
import com.interviewmart.exception.ConflictException;
import com.interviewmart.exception.UnauthorizedException;
import com.interviewmart.repository.RefreshTokenRepository;
import com.interviewmart.repository.UserRepository;
import com.interviewmart.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(user.getPassword(), request.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        refreshTokenRepository.deleteByUser(user);
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtUtil.isTokenValid(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        String email = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        return toResponse(user, accessToken, refreshToken);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenRepository.deleteByToken(request.getRefreshToken());
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtUtil.getRefreshTokenExpiryMs()))
                .build());
        return toResponse(user, accessToken, refreshToken);
    }

    private AuthResponse toResponse(User user, String accessToken, String refreshToken) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", user.getEmail(), user.getRole().name());
    }
}
