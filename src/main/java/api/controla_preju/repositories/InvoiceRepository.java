package api.controla_preju.repositories;

import api.controla_preju.entities.Invoice;
import api.controla_preju.entities.enums.InvoiceStatus;
import api.controla_preju.repositories.jpa.InvoiceJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InvoiceRepository {
    private final InvoiceJpaRepository jpaRepository;

    public InvoiceRepository(InvoiceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    public Invoice save(Invoice invoice) { return jpaRepository.save(invoice); }
    public Optional<Invoice> findById(UUID id) { return jpaRepository.findById(id); }
    public Optional<Invoice> findByCreditCardIdAndMonthAndYear(UUID creditCardId, int month, int year) { return jpaRepository.findByCreditCardIdAndMonthAndYear(creditCardId, month, year); }
    public List<Invoice> findAllByCreditCardId(UUID creditCardId) { return jpaRepository.findAllByCreditCardId(creditCardId); }
    public boolean existsByCreditCardId(UUID creditCardId) { return jpaRepository.existsByCreditCardId(creditCardId); }
    public List<Invoice> findAllByStatus(InvoiceStatus status) { return jpaRepository.findAllByStatus(status); }
}
