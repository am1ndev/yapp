package dev.amin.api.service;

import dev.amin.api.exception.NotFoundException;
import dev.amin.api.model.Chat;
import dev.amin.api.model.User;
import dev.amin.api.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static dev.amin.api.constant.ExceptionConstant.CHAT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository repository;

    public Chat create(User user) {
        Chat chat = new Chat();
        chat.setUser(user);
        chat.setTitle("Auto Generated Chat 😛");

        Chat created = repository.save(chat);

        log.info("created chat: {}", created);
        return created;
    }

    public Chat find(UUID actor) {
        Chat chat = repository.findByUser(actor)
                .orElseThrow(() -> new NotFoundException(CHAT_NOT_FOUND));

        log.info("found chat: userId={}, {}", actor, chat);
        return chat;
    }
}
