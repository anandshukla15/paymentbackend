package backend.paymentbackend;

import backend.paymentbackend.ledger.entity.AccountType;
import backend.paymentbackend.ledger.entity.LedgerEntry;
import backend.paymentbackend.ledger.entity.LedgerEntryType;
import backend.paymentbackend.outbox.entity.OutboxEvent;
import backend.paymentbackend.outbox.entity.OutboxStatus;
import backend.paymentbackend.refund.entity.Refund;
import backend.paymentbackend.refund.entity.RefundStatus;
import backend.paymentbackend.webhook.entity.WebhookEvent;
import backend.paymentbackend.webhook.entity.WebhookStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEntitiesTest {

    @Test
    @DisplayName("Refund entity creation and fields")
    void testRefundEntity() {
        UUID refundId = UUID.randomUUID();
        Refund refund = Refund.builder()
                .id(refundId)
                .refundReference("REF-123456")
                .amount(new BigDecimal("250.00"))
                .currency("INR")
                .status(RefundStatus.PENDING)
                .reason("Customer requested cancellation")
                .build();

        assertThat(refund.getId()).isEqualTo(refundId);
        assertThat(refund.getRefundReference()).isEqualTo("REF-123456");
        assertThat(refund.getAmount()).isEqualTo(new BigDecimal("250.00"));
        assertThat(refund.getCurrency()).isEqualTo("INR");
        assertThat(refund.getStatus()).isEqualTo(RefundStatus.PENDING);
        assertThat(refund.getReason()).isEqualTo("Customer requested cancellation");
    }

    @Test
    @DisplayName("OutboxEvent entity creation and fields")
    void testOutboxEventEntity() {
        UUID eventUuid = UUID.randomUUID();
        OutboxEvent event = OutboxEvent.builder()
                .id(eventUuid)
                .eventId("EVT-998877")
                .aggregateId("PAY-112233")
                .eventType("PAYMENT_CREATED")
                .payload("{\"amount\": 1000}")
                .status(OutboxStatus.PENDING)
                .build();

        assertThat(event.getId()).isEqualTo(eventUuid);
        assertThat(event.getEventId()).isEqualTo("EVT-998877");
        assertThat(event.getAggregateId()).isEqualTo("PAY-112233");
        assertThat(event.getEventType()).isEqualTo("PAYMENT_CREATED");
        assertThat(event.getPayload()).isEqualTo("{\"amount\": 1000}");
        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PENDING);
    }

    @Test
    @DisplayName("WebhookEvent entity creation and fields")
    void testWebhookEventEntity() {
        UUID webhookId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        WebhookEvent webhook = WebhookEvent.builder()
                .id(webhookId)
                .eventId("WH-EVENT-001")
                .paymentId(paymentId)
                .eventType("PAYMENT_GATEWAY_SUCCESS")
                .payload("{\"status\": \"SUCCESS\"}")
                .status(WebhookStatus.RECEIVED)
                .build();

        assertThat(webhook.getId()).isEqualTo(webhookId);
        assertThat(webhook.getEventId()).isEqualTo("WH-EVENT-001");
        assertThat(webhook.getPaymentId()).isEqualTo(paymentId);
        assertThat(webhook.getStatus()).isEqualTo(WebhookStatus.RECEIVED);
    }

    @Test
    @DisplayName("LedgerEntry entity creation and fields")
    void testLedgerEntryEntity() {
        UUID ledgerId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        LedgerEntry entry = LedgerEntry.builder()
                .id(ledgerId)
                .paymentId(paymentId)
                .entryType(LedgerEntryType.DEBIT)
                .accountType(AccountType.CUSTOMER)
                .accountId(customerId)
                .amount(new BigDecimal("1000.00"))
                .currency("INR")
                .description("Payment debit for order #123")
                .build();

        assertThat(entry.getId()).isEqualTo(ledgerId);
        assertThat(entry.getPaymentId()).isEqualTo(paymentId);
        assertThat(entry.getEntryType()).isEqualTo(LedgerEntryType.DEBIT);
        assertThat(entry.getAccountType()).isEqualTo(AccountType.CUSTOMER);
        assertThat(entry.getAccountId()).isEqualTo(customerId);
        assertThat(entry.getAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(entry.getCurrency()).isEqualTo("INR");
    }
}
