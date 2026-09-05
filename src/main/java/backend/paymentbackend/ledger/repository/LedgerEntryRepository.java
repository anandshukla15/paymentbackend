package backend.paymentbackend.ledger.repository;

import backend.paymentbackend.ledger.entity.LedgerEntry;
import backend.paymentbackend.ledger.entity.LedgerEntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {

    List<LedgerEntry> findByPaymentId(UUID paymentId);

    List<LedgerEntry> findByTransactionId(UUID transactionId);

    List<LedgerEntry> findByAccountId(UUID accountId);

    @Query("SELECT COALESCE(SUM(l.amount), 0) FROM LedgerEntry l WHERE l.paymentId = :paymentId AND l.entryType = :entryType")
    BigDecimal sumAmountByPaymentIdAndEntryType(
            @Param("paymentId") UUID paymentId,
            @Param("entryType") LedgerEntryType entryType
    );
}
