package api.controla_preju.services;

import api.controla_preju.dtos.forms.PayInvoiceForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.CreditCard;
import api.controla_preju.entities.Invoice;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.InvoiceStatus;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.InvoiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AccountService accountService;

    public InvoiceService(InvoiceRepository invoiceRepository, AccountService accountService) {
        this.invoiceRepository = invoiceRepository;
        this.accountService = accountService;
    }

    public Invoice findById(UUID invoiceId, UUID userId) {
        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow(() -> new EntityNotFoundException("Fatura não encontrada."));
        if (!invoice.getCreditCard().getUser().getId().equals(userId)) {
            throw new AuthorizationException("Esta fatura não pertence a você.");
        }
        return invoice;
    }

    public List<Invoice> findAllByCreditCardId(UUID creditCardId, UUID userId) {
        return invoiceRepository.findAllByCreditCardId(creditCardId);
    }

    @Transactional
    public Invoice getOrCreateInvoiceForFutureMonth(CreditCard card, LocalDateTime purchaseDate, int monthOffset) {
        LocalDate baseDate = purchaseDate.toLocalDate();
        if (baseDate.getDayOfMonth() >= card.getClosingDay()) {
            baseDate = baseDate.plusMonths(1);
        }
        baseDate = baseDate.plusMonths(monthOffset);

        int targetMonth = baseDate.getMonthValue();
        int targetYear = baseDate.getYear();

        return invoiceRepository.findByCreditCardIdAndMonthAndYear(card.getId(), targetMonth, targetYear)
                .orElseGet(() -> invoiceRepository.save(new Invoice(targetMonth, targetYear, 0L, InvoiceStatus.OPEN, card)));
    }

    @Transactional
    public void payInvoice(UUID invoiceId, PayInvoiceForm form, User user) {
        Invoice invoice = findById(invoiceId, user.getId());
        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("Esta fatura já está paga.");
        }

        Account account = accountService.findById(form.accountId(), user.getId());
        if (account.getBalanceInCents() < invoice.getTotalAmountInCents()) {
            throw new BusinessException("Saldo insuficiente na conta para pagar a fatura.");
        }

        account.setBalanceInCents(account.getBalanceInCents() - invoice.getTotalAmountInCents());
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.getCreditCard().restoreLimit(invoice.getTotalAmountInCents());

        invoiceRepository.save(invoice);
    }

    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

}
