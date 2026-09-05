package backend.paymentbackend.webhook.repository;

import backend.paymentbackend.webhook.entity.WebhookEvent;
import backend.paymentbackend.webhook.entity.WebhookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {

    Optional<WebhookEvent> findByEventId(String eventId);

    boolean existsByEventId(String eventId);

    List<WebhookEvent> findByPaymentId(UUID paymentId);

    List<WebhookEvent> findByStatus(WebhookStatus status);
}
