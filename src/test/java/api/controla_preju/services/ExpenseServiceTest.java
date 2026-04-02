package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateExpenseForm;
import api.controla_preju.dtos.forms.UpdateExpenseForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Expense;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.AccountType;
import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
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
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private ExpenseService expenseService;

    private User user;
    private Account account;
    private CreateExpenseForm createForm;
    private Expense expense;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User("test@email.com", "User Test", "password");
        ReflectionTestUtils.setField(user, "id", userId);

        account = new Account("Corrente", "Conta principal", AccountType.CHECKING_ACCOUNT, 2000L, true, user);
        ReflectionTestUtils.setField(account, "id", UUID.randomUUID());

        createForm = new CreateExpenseForm(
                "Supermercado",
                "Compras do mês",
                500L,
                ExpenseCategory.GROCERIES,
                PaymentMethod.DEBIT_CARD,
                LocalDateTime.now(),
                account.getId()
        );

        expense = new Expense(
                createForm.title(),
                createForm.description(),
                createForm.amountInCents(),
                createForm.paymentMethod(),
                createForm.category(),
                createForm.createdAt(),
                account
        );
        ReflectionTestUtils.setField(expense, "id", UUID.randomUUID());
    }

    @Test
    @DisplayName("Should create expense and decrease account balance successfully")
    void shouldCreateExpenseSuccessfully() {
        when(expenseRepository.existsDuplicate(anyString(), any(), anyLong(), any())).thenReturn(false);
        when(accountService.findById(account.getId(), userId)).thenReturn(account);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        Expense result = expenseService.create(createForm, user);

        assertNotNull(result);
        assertEquals(1500L, account.getBalanceInCents());
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    @DisplayName("Should throw exception when creating expense with insufficient balance")
    void shouldThrowExceptionWhenBalanceIsInsufficient() {
        account.setBalanceInCents(100L);
        when(expenseRepository.existsDuplicate(anyString(), any(), anyLong(), any())).thenReturn(false);
        when(accountService.findById(account.getId(), userId)).thenReturn(account);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                expenseService.create(createForm, user)
        );

        assertEquals("Saldo insuficiente para registrar a despesa.", exception.getMessage());
        verify(expenseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when expense is duplicate")
    void shouldThrowExceptionWhenExpenseIsDuplicate() {
        when(expenseRepository.existsDuplicate(anyString(), any(), anyLong(), any())).thenReturn(true);

        assertThrows(BusinessException.class, () -> expenseService.create(createForm, user));
        verify(expenseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find expense by id successfully")
    void shouldFindByIdSuccessfully() {
        when(expenseRepository.findById(expense.getId())).thenReturn(Optional.of(expense));

        Expense result = expenseService.findById(expense.getId(), userId);

        assertNotNull(result);
        assertEquals(expense.getId(), result.getId());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when expense does not exist")
    void shouldThrowExceptionWhenExpenseNotFound() {
        UUID id = UUID.randomUUID();
        when(expenseRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> expenseService.findById(id, userId));
    }

    @Test
    @DisplayName("Should throw AuthorizationException when user does not own the account linked to expense")
    void shouldThrowExceptionWhenUserDoesNotOwnExpense() {
        UUID otherUserId = UUID.randomUUID();
        when(expenseRepository.findById(expense.getId())).thenReturn(Optional.of(expense));

        assertThrows(AuthorizationException.class, () -> expenseService.findById(expense.getId(), otherUserId));
    }

    @Test
    @DisplayName("Should increase account balance when expense is deleted")
    void shouldRestoreBalanceOnDelete() {
        account.setBalanceInCents(1500L);

        expenseService.delete(expense);

        assertEquals(2000L, account.getBalanceInCents());
        verify(expenseRepository).delete(expense);
    }

    @Test
    @DisplayName("Should update amount and adjust account balance correctly")
    void shouldUpdateExpenseAmountSuccessfully() {
        account.setBalanceInCents(1500L);

        UpdateExpenseForm updateForm = new UpdateExpenseForm(null, null, 800L, null, null, null);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        expenseService.update(expense, updateForm);

        assertEquals(1200L, account.getBalanceInCents());
        verify(expenseRepository).save(expense);
    }


    @Test
    @DisplayName("Should throw exception when update results in negative balance")
    void shouldThrowExceptionWhenUpdateExceedsBalance() {
        UpdateExpenseForm updateForm = new UpdateExpenseForm(null, null, 3000L, null, null, null);

        assertThrows(BusinessException.class, () -> expenseService.update(expense, updateForm));
        verify(expenseRepository, never()).save(any());
    }

}