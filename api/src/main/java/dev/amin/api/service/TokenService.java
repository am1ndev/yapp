package dev.amin.api.service;

import dev.amin.api.model.Token;
import dev.amin.api.model.User;
import dev.amin.api.repository.TokenRepository;
import dev.amin.api.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository repository;
    private final TokenUtils utils;

    @Value("${app.security.refresh.expiration-days}")
    private long expiration;

    public String generate(User user) {
        String raw = utils.random();
        String hashed = utils.hash(raw);

        Instant now = Instant.now();
        Instant expiry = now.plus(expiration, ChronoUnit.DAYS);

        Token refresh = Token.builder()
                .user(user)
                .hash(hashed)
                .expiresAt(expiry)
                .build();

        repository.save(refresh);
        return raw;
    }

    public Optional<Token> validate(String raw) {
        String hashed = utils.hash(raw);

        Optional<Token> stored = repository.findByHash(hashed);
        if (stored.isEmpty()) {
            return Optional.empty();
        }

        Token token = stored.get();
        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            return Optional.empty();
        }

        return Optional.of(token);
    }

    public Optional<Token> detect(String raw) {
        String hashed = utils.hash(raw);
        return repository.findByHash(hashed);
    }

    public void revoke(Token token) {
        token.setRevoked(true);
        repository.save(token);
    }

    public void revoke(User user) {
        List<Token> tokens = repository.findByUserAndRevokedFalse(user);
        tokens.forEach(t -> t.setRevoked(true));

        repository.saveAll(tokens);
    }
}
