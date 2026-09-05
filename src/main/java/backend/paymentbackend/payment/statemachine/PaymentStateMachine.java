package backend.paymentbackend.payment.statemachine;

import backend.paymentbackend.common.exception.IllegalStateTransitionException;
import backend.paymentbackend.payment.entity.Payment;
import backend.paymentbackend.payment.entity.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PaymentStateMachine {

    private static final Map<PaymentStatus, Set<PaymentStatus>> VALID_TRANSITIONS = new EnumMap<>(PaymentStatus.class);

    static {
        // From CREATED
        VALID_TRANSITIONS.put(PaymentStatus.CREATED, EnumSet.of(
                PaymentStatus.PENDING,
                PaymentStatus.CANCELLED
        ));

        // From PENDING
        VALID_TRANSITIONS.put(PaymentStatus.PENDING, EnumSet.of(
                PaymentStatus.PROCESSING,
                PaymentStatus.CANCELLED,
                PaymentStatus.FAILED
        ));

        // From PROCESSING
        VALID_TRANSITIONS.put(PaymentStatus.PROCESSING, EnumSet.of(
                PaymentStatus.SUCCESS,
                PaymentStatus.FAILED
        ));

        // From SUCCESS
        VALID_TRANSITIONS.put(PaymentStatus.SUCCESS, EnumSet.of(
                PaymentStatus.REFUND_REQUESTED
        ));

        // From REFUND_REQUESTED
        VALID_TRANSITIONS.put(PaymentStatus.REFUND_REQUESTED, EnumSet.of(
                PaymentStatus.REFUND_PROCESSING,
                PaymentStatus.SUCCESS // if refund request is cancelled or rejected before processing
        ));

        // From REFUND_PROCESSING
        VALID_TRANSITIONS.put(PaymentStatus.REFUND_PROCESSING, EnumSet.of(
                PaymentStatus.PARTIALLY_REFUNDED,
                PaymentStatus.REFUNDED,
                PaymentStatus.SUCCESS // if refund fails at provider, restores to SUCCESS
        ));

        // From PARTIALLY_REFUNDED
        VALID_TRANSITIONS.put(PaymentStatus.PARTIALLY_REFUNDED, EnumSet.of(
                PaymentStatus.REFUND_REQUESTED
        ));

        // Terminal states: FAILED, CANCELLED, REFUNDED have no outgoing transitions
        VALID_TRANSITIONS.put(PaymentStatus.FAILED, Collections.emptySet());
        VALID_TRANSITIONS.put(PaymentStatus.CANCELLED, Collections.emptySet());
        VALID_TRANSITIONS.put(PaymentStatus.REFUNDED, Collections.emptySet());
    }

    /**
     * Checks if the transition from currentStatus to targetStatus is valid.
     */
    public boolean isValidTransition(PaymentStatus currentStatus, PaymentStatus targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }
        if (currentStatus == targetStatus) {
            return true; // idempotent self-transition
        }
        Set<PaymentStatus> allowedTargets = VALID_TRANSITIONS.get(currentStatus);
        return allowedTargets != null && allowedTargets.contains(targetStatus);
    }

    /**
     * Validates transition and throws IllegalStateTransitionException if invalid.
     */
    public void validateTransition(PaymentStatus currentStatus, PaymentStatus targetStatus) {
        if (!isValidTransition(currentStatus, targetStatus)) {
            throw new IllegalStateTransitionException(currentStatus, targetStatus);
        }
    }

    /**
     * Applies transition to the Payment entity after validation.
     */
    public Payment transition(Payment payment, PaymentStatus targetStatus) {
        Objects.requireNonNull(payment, "Payment entity cannot be null");
        validateTransition(payment.getStatus(), targetStatus);
        payment.setStatus(targetStatus);
        return payment;
    }

    /**
     * Returns valid target states from given current state.
     */
    public Set<PaymentStatus> getNextValidStates(PaymentStatus currentStatus) {
        if (currentStatus == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(
                VALID_TRANSITIONS.getOrDefault(currentStatus, Collections.emptySet())
        );
    }
}
