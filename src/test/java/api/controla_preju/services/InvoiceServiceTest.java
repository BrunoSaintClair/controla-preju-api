package api.controla_preju.services;

import api.controla_preju.dtos.forms.PayInvoiceForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.CreditCard;
import api.controla_preju.entities.Invoice;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.AccountType;
import api.controla_preju.entities.enums.InvoiceStatus;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private InvoiceService invoiceService;

    private User user;
    private CreditCard creditCard;
    private Invoice invoice;
    private Account account;
    private UUID userId;
    private UUID invoiceId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User("test@email.com", "User Test", "password");
        ReflectionTestUtils.setField(user, "id", userId);

        creditCard = new CreditCard("Nubank", 500000L, 5, 12, user);
        ReflectionTestUtils.setField(creditCard, "id", UUID.randomUUID());

        invoiceId = UUID.randomUUID();
        invoice = new Invoice(5, 2026, 150000L, InvoiceStatus.CLOSED, creditCard);
        ReflectionTestUtils.setField(invoice, "id", invoiceId);

        account = new Account("Corrente", "Desc", AccountType.CHECKING_ACCOUNT, 300000L, user);
        ReflectionTestUtils.setField(account, "id", UUID.randomUUID());
    }

    @Test
    @DisplayName("Should fetch existing invoice for a specific month")
    void shouldFetchExistingInvoice() {
        LocalDateTime purchaseDate = LocalDateTime.of(2026, 4, 10, 10, 0);
        when(invoiceRepository.findByCreditCardIdAndMonthAndYear(creditCard.getId(), 5, 2026))
                .thenReturn(Optional.of(invoice));

        Invoice result = invoiceService.getOrCreateInvoiceForFutureMonth(creditCard, purchaseDate, 0);

        assertEquals(invoiceId, result.getId());
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create new invoice when it does not exist for the target month")
    void shouldCreateNewInvoice() {
        LocalDateTime purchaseDate = LocalDateTime.of(2026, 4, 10, 10, 0);
        when(invoiceRepository.findByCreditCardIdAndMonthAndYear(creditCard.getId(), 5, 2026))
                .thenReturn(Optional.empty());
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        Invoice result = invoiceService.getOrCreateInvoiceForFutureMonth(creditCard, purchaseDate, 0);

        assertEquals(5, result.getMonth());
        assertEquals(2026, result.getYear());
        assertEquals(InvoiceStatus.OPEN, result.getStatus());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Should pay invoice successfully adjusting account balance and card limit")
    void shouldPayInvoiceSuccessfully() {
        creditCard.setAvailableLimitInCents(350000L);
        PayInvoiceForm form = new PayInvoiceForm(account.getId());

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(accountService.findById(account.getId(), userId)).thenReturn(account);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(invoice);

        invoiceService.payInvoice(invoiceId, form, user);

        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertEquals(150000L, account.getBalanceInCents());
        assertEquals(500000L, creditCard.getAvailableLimitInCents());
        verify(invoiceRepository).save(invoice);
    }

    @Test
    @DisplayName("Should throw exception when paying already paid invoice")
    void shouldThrowExceptionWhenInvoiceAlreadyPaid() {
        invoice.setStatus(InvoiceStatus.PAID);
        PayInvoiceForm form = new PayInvoiceForm(account.getId());

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        assertThrows(BusinessException.class, () -> invoiceService.payInvoice(invoiceId, form, user));
        verify(accountService, never()).findById(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when account balance is lower than invoice total")
    void shouldThrowExceptionWhenAccountBalanceInsufficient() {
        account.setBalanceInCents(10000L);
        PayInvoiceForm form = new PayInvoiceForm(account.getId());

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(accountService.findById(account.getId(), userId)).thenReturn(account);

        assertThrows(BusinessException.class, () -> invoiceService.payInvoice(invoiceId, form, user));
        verify(invoiceRepository, never()).save(any());
    }

}