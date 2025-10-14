package dev.amin.api.service;

import dev.amin.api.dto.ChatDto;
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

    private final ChatRepository chatRepository;

    public Chat create(User user) {
        Chat chat = new Chat();
        chat.setUser(user);
        chat.setTitle("Auto Generated Chat 😛");

        Chat created = chatRepository.save(chat);

        log.info("created chat: {}", created);
        return created;
    }

    public Chat update(UUID actor, ChatDto request) {
        Chat chat = find(actor);

        if (request.getTitle() != null && !request.getTitle().equals(chat.getTitle())) {
            chat.setTitle(request.getTitle());
        }

        Chat updated = chatRepository.save(chat);

        log.info("updated chat: {}", updated);
        return updated;
    }

    public Chat find(UUID actor) {
        Chat chat = chatRepository.findByUser(actor)
                .orElseThrow(() -> new NotFoundException(CHAT_NOT_FOUND));

        log.info("found chat: userId={}, {}", actor, chat);
        return chat;
    }
}

// TODO: check logs, add toSting() models, dtos