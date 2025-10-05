package dev.amin.api.service;

import dev.amin.api.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String ROLES_CLAIM = "roles";

    private final JwtEncoder encoder;

    @Value("${app.security.jwt.expiration-minutes}")
    private long expiration;

    @Value("${app.security.jwt.issuer}")
    private String issuer;

    public String generate(User user) {
        Instant now = Instant.now();
        Instant exp = now.plus(expiration, ChronoUnit.MINUTES);

        String sub = user.getId().toString();
        List<String> roles = List.of(user.getRole().toString());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(sub)
                .issuedAt(now)
                .expiresAt(exp)
                .issuer(issuer)
                .claim(ROLES_CLAIM, roles)
                .build();

        String generated = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        log.info("generated jwt token: {}", generated);
        return generated;
    }
}
