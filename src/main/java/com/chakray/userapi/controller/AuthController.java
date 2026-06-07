package com.chakray.userapi.controller;

import com.chakray.userapi.model.LoginRequest;
import com.chakray.userapi.model.User;
import com.chakray.userapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.authenticate(
                request.getTaxId(),
                request.getPassword()
        );

        return ResponseEntity.ok(user);
    }
}