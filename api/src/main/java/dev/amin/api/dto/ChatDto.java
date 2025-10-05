package dev.amin.api.dto;

import dev.amin.api.model.Chat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {

    private UUID id;
    private String title;
    private boolean active;
    private List<MessageDto> messages;

    public static ChatDto from(Chat chat) {
        ChatDto res = new ChatDto();
        res.setId(chat.getId());
        res.setTitle(chat.getTitle());
        res.setActive(chat.isActive());
        res.setMessages(chat.getMessages().stream()
                .map(MessageDto::from)
                .toList());

        return res;
    }
}
