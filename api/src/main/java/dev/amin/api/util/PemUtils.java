package dev.amin.api.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Base64;

public final class PemUtils {

    public static RSAPrivateKey readPrivateKey(String pemPath) throws Exception {
        String pem = Files.readString(Path.of(pemPath));
        pem = pem.replace("-----BEGIN PRIVATE KEY-----", "")
                 .replace("-----END PRIVATE KEY-----", "")
                 .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) kf.generatePrivate(keySpec);
    }

    public static RSAPublicKey readPublicKey(String pemPath) throws Exception {
        String pem = Files.readString(Path.of(pemPath));
        pem = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                 .replace("-----END PUBLIC KEY-----", "")
                 .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        return (RSAPublicKey) kf.generatePublic(spec);
    }
}
