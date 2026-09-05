package backend.paymentbackend.outbox.repository;

import backend.paymentbackend.outbox.entity.OutboxEvent;
import backend.paymentbackend.outbox.entity.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    Optional<OutboxEvent> findByEventId(String eventId);

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);

    List<OutboxEvent> findByAggregateIdOrderByCreatedAtAsc(String aggregateId);
}
