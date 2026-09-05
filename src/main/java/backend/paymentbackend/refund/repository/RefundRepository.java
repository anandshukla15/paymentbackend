package backend.paymentbackend.refund.repository;

import backend.paymentbackend.refund.entity.Refund;
import backend.paymentbackend.refund.entity.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefundRepository extends JpaRepository<Refund, UUID> {

    Optional<Refund> findByRefundReference(String refundReference);

    List<Refund> findByPaymentId(UUID paymentId);

    List<Refund> findByStatus(RefundStatus status);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Refund r WHERE r.payment.id = :paymentId AND r.status IN :statuses")
    BigDecimal sumAmountByPaymentIdAndStatusIn(
            @Param("paymentId") UUID paymentId,
            @Param("statuses") Collection<RefundStatus> statuses
    );
}
