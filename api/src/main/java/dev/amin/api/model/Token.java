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
@Table(name = "tokens", indexes = {
        @Index(name = "idx_token_hash", columnList = "hash"),
        @Index(name = "idx_token_user", columnList = "user_id")
})
public class Token extends Model {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(unique = true, nullable = false, length = 512)
    private String hash;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(nullable = false)
    private Instant expiresAt;
}
