package dev.amin.api.repository;

import dev.amin.api.model.Token;
import dev.amin.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    Optional<Token> findByHash(String hash);

    @Query("select t from Token t where t.user = ?1 and t.revoked = false")
    List<Token> findAllByUser(User user);
}