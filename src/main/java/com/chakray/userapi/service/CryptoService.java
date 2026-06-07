package com.chakray.userapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {

    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKeySpec secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public CryptoService(@Value("${app.aes-key}") String encodedKey) {
        byte[] key = Base64.getDecoder().decode(encodedKey);

        if (key.length != 32) {
            throw new IllegalArgumentException(
                    "AES key must contain exactly 32 bytes"
            );
        }

        this.secretKey = new SecretKeySpec(key, "AES");
    }

    public String encrypt(String plainText) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] encrypted = cipher.doFinal(
                    plainText.getBytes(StandardCharsets.UTF_8)
            );

            byte[] result = ByteBuffer
                    .allocate(iv.length + encrypted.length)
                    .put(iv)
                    .put(encrypted)
                    .array();

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Could not encrypt password",
                    exception
            );
        }
    }

    public String decrypt(String encryptedText) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);

            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);

            byte[] iv = new byte[IV_LENGTH];
            byteBuffer.get(iv);

            byte[] encrypted = new byte[byteBuffer.remaining()];
            byteBuffer.get(encrypted);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    new GCMParameterSpec(TAG_LENGTH, iv)
            );

            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Could not decrypt password",
                    exception
            );
        }
    }
}