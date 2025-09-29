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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.springframework.security.core.userdetails.User.builder;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final String REFRESH_COOKIE_NAME = "refresh_token";
    private final String REFRESH_COOKIE_PATH = "/api/v1/auth";
    private final String COOKIE_SAMESITE = "Lax"; // consider Strict in prod
    private final boolean COOKIE_SECURE = false; // set true in prod (HTTPS)

    private final AuthenticationManager authManager;
    private final UserService userService;
    private final ChatService chatService;
    private final JwtService jwtService;
    private final TokenService tokenService;

    @Value("${app.security.refresh.expiration-days}")
    private long expiration;

    public ResponseEntity<?> login(LoginRequest request, HttpServletResponse response) {
        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials");
        }

        User user = userService.find(request.getEmail());

        String access = jwtService.generate(user);
        String refresh = tokenService.generate(user); // create opaque refresh token and persist its hash

        ResponseCookie cookie = cookie(refresh);
        response.addHeader("Set-Cookie", cookie.toString());

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

        // rotate
        tokenService.revoke(stored);

        String newRaw = tokenService.generate(user); // refresh token
        String newAccess = jwtService.generate(user);

        ResponseCookie cookie = cookie(newRaw);
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(new TokenResponse(newAccess));
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

        return ResponseEntity.ok().build();
    }

    private ResponseCookie cookie(String token) {
        boolean reset = token == null || token.isEmpty();
        long age = Duration.of(expiration, ChronoUnit.DAYS).getSeconds();

        return ResponseCookie.from(REFRESH_COOKIE_NAME, reset ? "" : token)
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .path(REFRESH_COOKIE_PATH)
                .maxAge(reset ? 0 : age) // 30 days
                .sameSite(COOKIE_SAMESITE)
                .build();
    }
}
