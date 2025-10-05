package dev.amin.api.controller;

import dev.amin.api.annotation.Actor;
import dev.amin.api.model.Chat;
import dev.amin.api.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<Chat> getChat(@Actor UUID actor) {
        log.info("Getting chat for actor {}", actor);
        Chat chat = chatService.find(actor);

        return ResponseEntity.ok(chat);
    }
}
