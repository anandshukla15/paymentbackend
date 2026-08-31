package backend.paymentbackend.transaction.repository;

import backend.paymentbackend.transaction.entity.PaymentTransaction;
import backend.paymentbackend.transaction.entity.TransactionStatus;
import backend.paymentbackend.transaction.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, UUID> {

    List<PaymentTransaction> findByPaymentId(UUID paymentId);

    List<PaymentTransaction> findByPaymentIdAndType(
            UUID paymentId,
            TransactionType type
    );

    List<PaymentTransaction> findByStatus(TransactionStatus status);
}
