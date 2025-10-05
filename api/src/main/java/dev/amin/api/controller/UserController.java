package dev.amin.api.controller;

import dev.amin.api.annotation.Actor;
import dev.amin.api.dto.UserResponse;
import dev.amin.api.model.User;
import dev.amin.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getUser(@Actor UUID actor) {
        User user = userService.find(actor);

        log.warn("user id: {}", actor.toString());

        return ResponseEntity.ok(UserResponse.fromUser(user));
    }
}
