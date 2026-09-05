package backend.paymentbackend.common.exception;

import backend.paymentbackend.payment.entity.PaymentStatus;

public class IllegalStateTransitionException extends RuntimeException {

    private final PaymentStatus fromStatus;
    private final PaymentStatus toStatus;

    public IllegalStateTransitionException(PaymentStatus fromStatus, PaymentStatus toStatus) {
        super(String.format("Invalid payment state transition from %s to %s", fromStatus, toStatus));
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public PaymentStatus getFromStatus() {
        return fromStatus;
    }

    public PaymentStatus getToStatus() {
        return toStatus;
    }
}
