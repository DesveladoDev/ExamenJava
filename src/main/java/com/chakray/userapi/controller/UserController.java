package com.chakray.userapi.controller;

import com.chakray.userapi.model.User;
import com.chakray.userapi.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers(
        @RequestParam(required = false) String sortedBy,
        @RequestParam(required = false) String filter
    ) {
        if (filter != null && !filter.isBlank()) {
            return userService.filterUsers(filter);
        }

        return userService.getUsers(sortedBy);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.addUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @PatchMapping("/{id}")
    public User updateUser(
            @PathVariable UUID id,
            @RequestBody User changes
    ) {
        return userService.updateUser(id, changes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
      userService.deleteUser(id);
      return ResponseEntity.noContent().build();
   }
}