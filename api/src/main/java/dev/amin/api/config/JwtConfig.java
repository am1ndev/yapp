package dev.amin.api.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import dev.amin.api.util.PemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class JwtConfig {

    @Value("${app.security.jwt.private}")
    private RSAPrivateKey priv;

    @Value("${app.security.jwt.public}")
    private RSAPublicKey pub;

    @Value("${app.security.jwt.issuer}")
    private String issuer;

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(pub).build();

        // Validate issuer and expiry
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withTimestamp = new JwtTimestampValidator(); // default, but explicit
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withIssuer, withTimestamp);
        decoder.setJwtValidator(validator);

        return decoder;
    }

    @Bean
    public JwtEncoder jwtEncoder() throws Exception {
        RSAKey rsa = new RSAKey.Builder(pub)
                .privateKey(priv)
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsa));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }
}
