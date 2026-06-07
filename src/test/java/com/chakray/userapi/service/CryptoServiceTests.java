package com.chakray.userapi.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CryptoServiceTests {

    private static final String KEY =
            "MnZ6akEAnQBflN8TOpilBjSDGVnsV2qgRlhmCGpgZV0=";

    @Test
    void shouldEncryptAndDecryptPassword() {
        CryptoService cryptoService = new CryptoService(KEY);
        String password = "password2";

        String encrypted = cryptoService.encrypt(password);
        String decrypted = cryptoService.decrypt(encrypted);

        assertNotEquals(password, encrypted);
        assertEquals(password, decrypted);
    }
}