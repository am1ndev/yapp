package dev.amin.api.repository;

import dev.amin.api.model.Token;
import dev.amin.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByHash(String hash);
    List<Token> findByUserAndRevokedFalse(User user);
    List<Token> findByUser(User user);
}