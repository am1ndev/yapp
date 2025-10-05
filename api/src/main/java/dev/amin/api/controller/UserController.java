package dev.amin.api.controller;

import dev.amin.api.annotation.Actor;
import dev.amin.api.dto.UserDto;
import dev.amin.api.model.User;
import dev.amin.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> getUser(@Actor UUID actor) {
        User user = userService.find(actor);

        return ResponseEntity.ok(UserDto.from(user));
    }
}
