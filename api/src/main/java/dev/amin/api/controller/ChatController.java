package dev.amin.api.controller;

import dev.amin.api.annotation.Actor;
import dev.amin.api.dto.ChatDto;
import dev.amin.api.dto.MessageDto;
import dev.amin.api.model.Chat;
import dev.amin.api.model.Message;
import dev.amin.api.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chats")
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<ChatDto> getChat(@Actor UUID actor) {
        Chat chat = chatService.find(actor);

        return ResponseEntity.ok(ChatDto.from(chat));
    }

    @PostMapping
    public ResponseEntity<MessageDto> doChat(@Actor UUID actor, @RequestBody MessageDto request) {
        Message reply = chatService.chat(actor, request.getContent());

        return ResponseEntity.ok(MessageDto.from(reply));
    }

    @PutMapping
    public ResponseEntity<ChatDto> updateChat(@Actor UUID actor, @RequestBody ChatDto request) {
        Chat chat = chatService.update(actor, request);

        return ResponseEntity.ok(ChatDto.from(chat));
    }
}
