package com.chakray.userapi.service;

import com.chakray.userapi.exception.ApiException;
import com.chakray.userapi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTests {

    private static final String KEY =
            "MnZ6akEAnQBflN8TOpilBjSDGVnsV2qgRlhmCGpgZV0=";

    private UserService userService;

    @BeforeEach
    void setUp() {
        CryptoService cryptoService = new CryptoService(KEY);
        userService = new UserService(cryptoService);
    }

    @Test
    void shouldFilterUsersByName() {
        List<User> users = userService.filterUsers("name co user");

        assertEquals(2, users.size());
        assertEquals("user2", users.get(0).getName());
    }

    @Test
    void shouldAuthenticateUsingTaxId() {
        User user = userService.authenticate(
                "BERR980202XXX",
                "password2"
        );

        assertEquals("user2", user.getName());
    }

    @Test
    void shouldRejectDuplicateTaxId() {
        User duplicate = new User();
        duplicate.setTaxId("AARR990101XXX");

        ApiException exception = assertThrows(
                ApiException.class,
                () -> userService.addUser(duplicate)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }
}