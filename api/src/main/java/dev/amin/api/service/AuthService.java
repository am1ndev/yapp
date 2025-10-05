package dev.amin.api.service;

import dev.amin.api.dto.LoginRequest;
import dev.amin.api.dto.SignupRequest;
import dev.amin.api.dto.TokenResponse;
import dev.amin.api.dto.UserResponse;
import dev.amin.api.model.Chat;
import dev.amin.api.model.Token;
import dev.amin.api.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final String SET_COOKIE_HEADER = "Set-Cookie";
    private final String REFRESH_COOKIE_NAME = "REFRESH_TOKEN";
    private final String REFRESH_COOKIE_PATH = "/api/v1/auth";
    private final String COOKIE_SAMESITE = "Lax"; // 'Strict'
    private final boolean COOKIE_SECURE = false; // true (HTTPS)

    private final CsrfTokenRepository csrfTokenRepository;
    private final AuthenticationManager authManager;
    private final UserService userService;
    private final ChatService chatService;
    private final JwtService jwtService;
    private final TokenService tokenService;

    @Value("${app.security.refresh.expiration-days}")
    private long expiration;

    public ResponseEntity<?> login(LoginRequest dto, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect email or password");
        }

        User user = userService.find(dto.getEmail());

        String access = token(request, response, user);
        return ResponseEntity.ok(new TokenResponse(access));
    }

    public ResponseEntity<?> signup(SignupRequest request) {
        if (userService.exists(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        User user = userService.save(request);
        Chat chat = chatService.create(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.fromUser(user));
    }

    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String raw = Arrays.stream(cookies)
                .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (raw == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Token> any = tokenService.detect(raw);
        if (any.isPresent() && any.get().isRevoked()) {
            tokenService.revoke(any.get().getUser());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Token> validated = tokenService.validate(raw);
        // TODO: handle auth error in exception handling implementation
        if (validated.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Token token = validated.get();
        User user = token.getUser();

        tokenService.revoke(token);

        String access = token(request, response, user);
        return ResponseEntity.ok(new TokenResponse(access));
    }

    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Arrays.stream(cookies)
                .filter(c -> REFRESH_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .flatMap(tokenService::validate)
                .ifPresent(tokenService::revoke);

        ResponseCookie cookie = cookie(null);
        response.addHeader(SET_COOKIE_HEADER, cookie.toString());

        // set new csrf token if web needs public routes access
        csrfTokenRepository.saveToken(null, request, response);

        return ResponseEntity.ok().build();
    }

    private String token(HttpServletRequest request, HttpServletResponse response, User user) {
        String refresh = tokenService.generate(user);
        String access = jwtService.generate(user);

        ResponseCookie cookie = cookie(refresh);
        response.addHeader(SET_COOKIE_HEADER, cookie.toString());

        CsrfToken csrf = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(csrf, request, response);

        return access;
    }

    private ResponseCookie cookie(String token) {
        boolean clear = token == null || token.isEmpty();

        String value = clear ? "" : token;
        Duration age = clear ? Duration.ofDays(0) : Duration.ofDays(expiration);

        return ResponseCookie.from(REFRESH_COOKIE_NAME, value)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .path(REFRESH_COOKIE_PATH)
                .maxAge(age)
                .sameSite(COOKIE_SAMESITE)
                .build();
    }
}
