package dev.amin.api.service;

import dev.amin.api.model.Token;
import dev.amin.api.model.User;
import dev.amin.api.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final SecureRandom secureRandom = new SecureRandom();
    private final TokenRepository repository;

    @Value("${app.security.refresh.expiration-days}")
    private long expiration;

    public String generate(User user) {
        String raw = randomToken(); // implement a cryptographically secure random string generator using SecureRandom.

        String hash = hash(raw);
        Instant now = Instant.now();

        Token refresh = new Token();
        refresh.setUser(user);
        refresh.setHash(hash);
        refresh.setExpiresAt(now.plus(expiration, ChronoUnit.DAYS));

        repository.save(refresh);
        return raw;
    }

    // Validate raw token -> returns stored entity if valid
    public Optional<Token> validate(String raw) {
        String hash = hash(raw);
        Optional<Token> stored = repository.findByHash(hash);
        if (stored.isEmpty()) return Optional.empty();
        Token rt = stored.get();
        if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }
        return Optional.of(rt);
    }

    // If token is found but already revoked -> detect reuse, caller can decide to revoke all for user
    public Optional<Token> findByRawAnyState(String raw) {
        String h = hash(raw);
        return repository.findByHash(h);
    }

    public void revoke(Token token) {
        token.setRevoked(true);
        repository.save(token);
    }

    public void revokeAll(User user) {
        List<Token> tokens = repository.findByUserAndRevokedFalse(user);
        tokens.forEach(t -> t.setRevoked(true));

        repository.saveAll(tokens);
    }

    private String hash(String token) {
        return DigestUtils.sha256Hex(token);
    }

    private String randomToken() {
        byte[] b = new byte[64];
        secureRandom.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}
