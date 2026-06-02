package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Invoice;
import api.controla_preju.entities.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceJpaRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByCreditCardIdAndMonthAndYear(UUID creditCardId, int month, int year);
    List<Invoice> findAllByCreditCardId(UUID creditCardId);
    boolean existsByCreditCardId(UUID creditCardId);
    List<Invoice> findAllByStatus(InvoiceStatus status);
}
