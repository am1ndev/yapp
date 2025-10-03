package dev.amin.api.service;

import dev.amin.api.dto.LoginRequest;
import dev.amin.api.dto.SignupRequest;
import dev.amin.api.dto.TokenResponse;
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
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final String REFRESH_COOKIE_NAME = "REFRESH_TOKEN";
    private final String REFRESH_COOKIE_PATH = "/api/v1/auth";
    private final String COOKIE_SAMESITE = "Lax"; // consider 'Strict' in prod
    private final boolean COOKIE_SECURE = false; // set true in prod (HTTPS)

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

        String access = jwtService.generate(user);
        String refresh = tokenService.generate(user); // create opaque refresh token and persist its hash

        ResponseCookie cookie = cookie(refresh);
        response.addHeader("Set-Cookie", cookie.toString());

        CsrfToken csrf = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(csrf, request, response);

        return ResponseEntity.ok(new TokenResponse(access));
    }

    public ResponseEntity<?> signup(SignupRequest request) {
        if (userService.exists(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }

        User user = userService.save(request);
        Chat chat = chatService.create(user);

        return ResponseEntity.status(
                HttpStatus.CREATED).body(Map.of("id", user.getId(), "email", user.getEmail())
        );
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

        // check if token exists even in revoked state -> detect reuse
        Optional<Token> any = tokenService.findByRawAnyState(raw);
        if (any.isPresent() && any.get().isRevoked()) {
            // reuse detected: revoke ALL tokens for that user (defense)
            tokenService.revokeAll(any.get().getUser());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Token> valid = tokenService.validate(raw);
        if (valid.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Token stored = valid.get();
        User user = stored.getUser();

        tokenService.revoke(stored);

        String refresh = tokenService.generate(user);
        String access = jwtService.generate(user);

        ResponseCookie cookie = cookie(refresh);
        response.addHeader("Set-Cookie", cookie.toString());

        CsrfToken csrf = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(csrf, request, response);

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
        response.addHeader("Set-Cookie", cookie.toString());

        csrfTokenRepository.saveToken(null, request, response);

        return ResponseEntity.ok().build();
    }

    private ResponseCookie cookie(String token) {
        boolean reset = token == null || token.isEmpty();
        Duration age = reset ? Duration.ofDays(0) : Duration.ofDays(expiration);

        return ResponseCookie.from(REFRESH_COOKIE_NAME, reset ? "" : token)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .path(REFRESH_COOKIE_PATH)
                .maxAge(age) // 30 days
                .sameSite(COOKIE_SAMESITE)
                .build();
    }
}
