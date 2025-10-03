package dev.amin.api.controller;

import dev.amin.api.dto.LoginRequest;
import dev.amin.api.dto.SignupRequest;
import dev.amin.api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        return authService.login(req, request, response);
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
