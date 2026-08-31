package backend.paymentbackend.payment.repository;

import backend.paymentbackend.payment.entity.Payment;
import backend.paymentbackend.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByPaymentReference(String paymentReference);

    Optional<Payment> findByMerchantIdAndIdempotencyKey(
            UUID merchantId,
            String idempotencyKey
    );

    List<Payment> findByMerchantId(UUID merchantId);

    List<Payment> findByStatus(PaymentStatus status);

    boolean existsByPaymentReference(String paymentReference);
}
