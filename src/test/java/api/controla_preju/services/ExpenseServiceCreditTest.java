package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateExpenseForm;
import api.controla_preju.entities.*;
import api.controla_preju.entities.enums.*;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceCreditTest {

    @Mock private ExpenseRepository expenseRepository;
    @Mock private CreditCardService creditCardService;
    @Mock private InvoiceService invoiceService;

    @InjectMocks private ExpenseService expenseService;

    private User user;
    private CreditCard creditCard;
    private Invoice invoice;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User("test@email.com", "User", "password");
        ReflectionTestUtils.setField(user, "id", userId);

        creditCard = new CreditCard("Nubank", 500000L, 5, 12, user);
        ReflectionTestUtils.setField(creditCard, "id", UUID.randomUUID());

        invoice = new Invoice(5, 2026, 0L, InvoiceStatus.OPEN, creditCard);
        ReflectionTestUtils.setField(invoice, "id", UUID.randomUUID());
    }

    @Test
    @DisplayName("Should create credit card expense with 3 installments correctly adjusting math remainder")
    void shouldCreateCreditCardExpenseWithInstallments() {
        CreateExpenseForm form = new CreateExpenseForm(
                "Compra Parcelada", "Desc", 10000L, ExpenseCategory.TECHNOLOGY,
                PaymentMethod.CREDIT_CARD, LocalDateTime.now(), TransactionStatus.COMPLETED, false,
                null, creditCard.getId(), 3
        );

        when(creditCardService.findById(creditCard.getId(), userId)).thenReturn(creditCard);
        when(invoiceService.getOrCreateInvoiceForFutureMonth(any(), any(), anyInt())).thenReturn(invoice);
        when(expenseRepository.save(any(Expense.class))).thenAnswer(i -> i.getArgument(0));

        List<Expense> result = expenseService.create(form, user);

        assertEquals(3, result.size());

        assertEquals(3334L, result.get(0).getAmountInCents());
        assertEquals("Compra Parcelada (1/3)", result.get(0).getTitle());

        assertEquals(3333L, result.get(1).getAmountInCents());
        assertEquals("Compra Parcelada (2/3)", result.get(1).getTitle());

        assertEquals(3333L, result.get(2).getAmountInCents());
        assertEquals("Compra Parcelada (3/3)", result.get(2).getTitle());

        assertEquals(490000L, creditCard.getAvailableLimitInCents());

        verify(creditCardService, times(1)).save(creditCard);
        verify(expenseRepository, times(3)).save(any(Expense.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when available limit is insufficient")
    void shouldThrowExceptionWhenInsufficientLimit() {
        CreateExpenseForm form = new CreateExpenseForm(
                "Macbook Pro", "Laptop", 1000000L, ExpenseCategory.TECHNOLOGY,
                PaymentMethod.CREDIT_CARD, LocalDateTime.now(), TransactionStatus.COMPLETED, false,
                null, creditCard.getId(), 10
        );

        when(creditCardService.findById(creditCard.getId(), userId)).thenReturn(creditCard);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            expenseService.create(form, user);
        });

        assertEquals("Limite disponível insuficiente.", exception.getMessage());
        verify(expenseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete credit card expense correctly restoring limit and reducing invoice total")
    void shouldDeleteCreditCardExpense() {
        invoice.setTotalAmountInCents(5000L);
        creditCard.setAvailableLimitInCents(495000L);

        Expense expense = new Expense("Teste", "Desc", 5000L, PaymentMethod.CREDIT_CARD,
                ExpenseCategory.OTHERS, LocalDateTime.now(), TransactionStatus.COMPLETED,
                false, invoice, null);

        expenseService.delete(expense);

        assertEquals(0L, invoice.getTotalAmountInCents());
        assertEquals(500000L, creditCard.getAvailableLimitInCents());
        verify(expenseRepository).delete(expense);
    }

}