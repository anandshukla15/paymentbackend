package backend.paymentbackend.payment.statemachine;

import backend.paymentbackend.common.exception.IllegalStateTransitionException;
import backend.paymentbackend.payment.entity.Payment;
import backend.paymentbackend.payment.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentStateMachineTest {

    private PaymentStateMachine stateMachine;

    @BeforeEach
    void setUp() {
        stateMachine = new PaymentStateMachine();
    }

    @ParameterizedTest(name = "Valid transition from {0} to {1}")
    @CsvSource({
            "CREATED, PENDING",
            "CREATED, CANCELLED",
            "PENDING, PROCESSING",
            "PENDING, CANCELLED",
            "PENDING, FAILED",
            "PROCESSING, SUCCESS",
            "PROCESSING, FAILED",
            "SUCCESS, REFUND_REQUESTED",
            "REFUND_REQUESTED, REFUND_PROCESSING",
            "REFUND_REQUESTED, SUCCESS",
            "REFUND_PROCESSING, PARTIALLY_REFUNDED",
            "REFUND_PROCESSING, REFUNDED",
            "REFUND_PROCESSING, SUCCESS",
            "PARTIALLY_REFUNDED, REFUND_REQUESTED"
    })
    @DisplayName("Should accept valid state transitions")
    void testValidTransitions(PaymentStatus from, PaymentStatus to) {
        assertThat(stateMachine.isValidTransition(from, to)).isTrue();
    }

    @ParameterizedTest(name = "Invalid transition from {0} to {1}")
    @CsvSource({
            "SUCCESS, FAILED",
            "REFUNDED, SUCCESS",
            "FAILED, SUCCESS",
            "FAILED, PROCESSING",
            "CANCELLED, PENDING",
            "CREATED, SUCCESS",
            "CREATED, PROCESSING",
            "PENDING, SUCCESS",
            "SUCCESS, CREATED",
            "REFUNDED, REFUND_REQUESTED"
    })
    @DisplayName("Should reject invalid state transitions")
    void testInvalidTransitions(PaymentStatus from, PaymentStatus to) {
        assertThat(stateMachine.isValidTransition(from, to)).isFalse();

        assertThatThrownBy(() -> stateMachine.validateTransition(from, to))
                .isInstanceOf(IllegalStateTransitionException.class)
                .hasMessageContaining(String.format("Invalid payment state transition from %s to %s", from, to));
    }

    @Test
    @DisplayName("Idempotent transition to same status should be valid")
    void testSameStatusTransition() {
        assertThat(stateMachine.isValidTransition(PaymentStatus.SUCCESS, PaymentStatus.SUCCESS)).isTrue();
        assertThat(stateMachine.isValidTransition(PaymentStatus.CREATED, PaymentStatus.CREATED)).isTrue();
    }

    @Test
    @DisplayName("Null status should be invalid")
    void testNullStatusHandling() {
        assertThat(stateMachine.isValidTransition(null, PaymentStatus.CREATED)).isFalse();
        assertThat(stateMachine.isValidTransition(PaymentStatus.CREATED, null)).isFalse();
        assertThat(stateMachine.isValidTransition(null, null)).isFalse();
    }

    @Test
    @DisplayName("transition() should update payment entity status when valid")
    void testTransitionUpdatesPayment() {
        Payment payment = Payment.builder()
                .status(PaymentStatus.CREATED)
                .build();

        Payment updated = stateMachine.transition(payment, PaymentStatus.PENDING);
        assertThat(updated.getStatus()).isEqualTo(PaymentStatus.PENDING);

        stateMachine.transition(payment, PaymentStatus.PROCESSING);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PROCESSING);

        stateMachine.transition(payment, PaymentStatus.SUCCESS);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("transition() should throw exception when transition is forbidden")
    void testTransitionThrowsOnForbiddenState() {
        Payment payment = Payment.builder()
                .status(PaymentStatus.SUCCESS)
                .build();

        assertThatThrownBy(() -> stateMachine.transition(payment, PaymentStatus.FAILED))
                .isInstanceOf(IllegalStateTransitionException.class)
                .hasMessageContaining("Invalid payment state transition from SUCCESS to FAILED");

        // Payment status must remain unchanged
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    @DisplayName("getNextValidStates returns correct next states")
    void testGetNextValidStates() {
        assertThat(stateMachine.getNextValidStates(PaymentStatus.PROCESSING))
                .containsExactlyInAnyOrder(PaymentStatus.SUCCESS, PaymentStatus.FAILED);

        assertThat(stateMachine.getNextValidStates(PaymentStatus.FAILED)).isEmpty();
        assertThat(stateMachine.getNextValidStates(PaymentStatus.REFUNDED)).isEmpty();
    }
}
