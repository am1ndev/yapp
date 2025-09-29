package dev.amin.api.repository;

import dev.amin.api.model.Chat;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Registered
public interface ChatRepository extends JpaRepository<Chat, UUID> {


}
