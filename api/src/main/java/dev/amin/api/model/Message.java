package dev.amin.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages", indexes = {
        @Index(name = "idx_message_chat", columnList = "chat_id")
})
public class Message extends Model {

    public enum Type {
        USER, BOT
    }

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ToString.Exclude
    @JoinColumn(name = "chat_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Chat chat;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Instant sentAt;
}
