package backend.paymentbackend.merchant.repository;

import backend.paymentbackend.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

//merchant 
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {

    Optional<Merchant> findByEmail(String email);

    Optional<Merchant> findByApiKey(String apiKey);

    boolean existsByEmail(String email);

    boolean existsByApiKey(String apiKey);
}
