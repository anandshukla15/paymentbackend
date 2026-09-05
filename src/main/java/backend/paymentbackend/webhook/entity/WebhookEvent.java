package backend.paymentbackend.webhook.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "webhook_events",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_webhook_event_id", columnNames = "eventId")
        },
        indexes = {
                @Index(name = "idx_webhook_event_id", columnList = "eventId"),
                @Index(name = "idx_webhook_payment_id", columnList = "paymentId"),
                @Index(name = "idx_webhook_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String eventId;

    private UUID paymentId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookStatus status;

    @Column(nullable = false, updatable = false)
    private Instant receivedAt;

    private Instant processedAt;

    private String failureReason;

    @PrePersist
    protected void onCreate() {
        if (receivedAt == null) {
            receivedAt = Instant.now();
        }
        if (status == null) {
            status = WebhookStatus.RECEIVED;
        }
    }
}
