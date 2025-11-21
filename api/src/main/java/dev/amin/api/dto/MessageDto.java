package dev.amin.api.dto;

import dev.amin.api.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private UUID id;
    private String content;
    private Instant sentAt;
    private Message.Type type;
    private Instant createdAt;
    private Instant updatedAt;

    public static MessageDto from(Message message) {
        MessageDto res = new MessageDto();
        res.setId(message.getId());
        res.setContent(message.getContent());
        res.setSentAt(message.getSentAt());
        res.setType(message.getType());
        res.setCreatedAt(message.getCreatedAt());
        res.setUpdatedAt(message.getUpdatedAt());

        return res;
    }
}
