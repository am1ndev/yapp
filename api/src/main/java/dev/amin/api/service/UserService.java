package dev.amin.api.service;

import dev.amin.api.dto.SignupRequest;
import dev.amin.api.exception.NotFoundException;
import dev.amin.api.model.User;
import dev.amin.api.model.User.Role;
import dev.amin.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static dev.amin.api.constant.ExceptionConstant.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    // TODO: cleanup and validate values (e.g. email) before saving

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public User save(SignupRequest request) {
        String password = encoder.encode(request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(password)
                .role(Role.USER)
                .build();

        User saved = repository.save(user);

        log.info("saved user: {}", saved);
        return saved;
    }

    public User find(UUID id) {
        User found = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        log.info("found user: id={}, {}", id, found);
        return found;
    }

    public User find(String email) {
        User found = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));

        log.info("found user: email={}, {}", email, found);
        return found;
    }

    public boolean exists(String email) {
        email = email.trim().toLowerCase();

//        return repository.existsByEmail(email);
        return repository.findByEmail(email).isPresent();
    }
}
