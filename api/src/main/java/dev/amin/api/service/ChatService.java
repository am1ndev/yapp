package dev.amin.api.service;

import dev.amin.api.dto.ChatDto;
import dev.amin.api.exception.NotFoundException;
import dev.amin.api.model.Chat;
import dev.amin.api.model.Message;
import dev.amin.api.model.User;
import dev.amin.api.repository.ChatRepository;
import dev.amin.api.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static dev.amin.api.constant.ExceptionConstant.CHAT_NOT_FOUND;
import static dev.amin.api.model.Message.Type;
import static dev.amin.api.model.Message.builder;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ChatClient chatClient;

    @Transactional
    public Message chat(UUID actor, String input) {
        Chat chat = find(actor);

        Message message = builder()
                .chat(chat)
                .type(Type.USER)
                .content(input)
                .sentAt(Instant.now())
                .build();

        messageRepository.save(message);

        ChatResponse response = chatClient.prompt()
                .user(message.getContent())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, actor)) // we should use chatId here
                .call()
                .chatResponse();

        String output = response.getResult().getOutput().getText();

        Message reply = builder()
                .chat(chat)
                .type(Type.BOT)
                .content(output)
                .sentAt(Instant.now())
                .build();

        messageRepository.save(reply);

        return reply;
    }

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
// TODO: update controller endpoints e.g. /users/me
// TODO: check user and chat access when replying to a message