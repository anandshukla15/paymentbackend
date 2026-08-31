package backend.paymentbackend.payment.entity;

public enum PaymentStatus {

    CREATED,

    PENDING,

    PROCESSING,

    SUCCESS,

    FAILED,

    CANCELLED,

    REFUND_REQUESTED,

    REFUND_PROCESSING,

    PARTIALLY_REFUNDED,

    REFUNDED
}
