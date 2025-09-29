package dev.amin.api.service;

import dev.amin.api.dto.SignupRequest;
import dev.amin.api.model.User;
import dev.amin.api.model.User.Role;
import dev.amin.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

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
        return saved;
    }

    public boolean exists(String email) {
        email = email.trim().toLowerCase();

//        return repository.existsByEmail(email);
        return repository.findByEmail(email).isPresent();
    }

    public User find(String email) {
        User found = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));

        return found;
    }

    public User find(UUID id) {
        User found = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User with id " + id + " not found"));

        return found;
    }
}
