package dev.amin.api.controller;

import dev.amin.api.dto.LoginRequest;
import dev.amin.api.dto.SignupRequest;
import dev.amin.api.dto.TokenResponse;
import dev.amin.api.model.Token;
import dev.amin.api.model.User;
import dev.amin.api.model.User.Role;
import dev.amin.api.repository.UserRepository;
import dev.amin.api.service.AuthService;
import dev.amin.api.service.JwtService;
import dev.amin.api.service.RateLimiterService;
import dev.amin.api.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        return authService.signup(req);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request, HttpServletResponse response) {
//        String ip = request.getRemoteAddr();
//        if (!rateLimiter.tryConsumeLogin(ip)) {
//            return ResponseEntity.status(429).body("Too many login attempts, try later");
//        }

        return authService.login(req, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
//        String ip = request.getRemoteAddr();
//        if (!rateLimiter.tryConsumeRefresh(ip)) {
//            return ResponseEntity.status(429).body("Too many refresh attempts");
//        }

        return authService.refresh(request, response);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }
}
