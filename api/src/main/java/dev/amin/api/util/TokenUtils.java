package dev.amin.api.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public final class TokenUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String hash(String token) {
        return DigestUtils.sha256Hex(token);
    }

    public String random() {
        byte[] bytes = new byte[64];
        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
