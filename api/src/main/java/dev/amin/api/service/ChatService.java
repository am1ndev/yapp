package dev.amin.api.service;

import dev.amin.api.model.Chat;
import dev.amin.api.model.User;
import dev.amin.api.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository repository;

    public Chat create(User user) {
        Chat chat = new Chat();
        chat.setUser(user);
        chat.setTitle("Auto generated chat");

        return repository.save(chat);
    }

    public Chat find(UUID actor) {
        return repository.findByUser(actor)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
    }
}
