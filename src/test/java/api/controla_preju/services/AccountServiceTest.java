package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateAccountForm;
import api.controla_preju.dtos.forms.UpdateBalanceForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.AccountType;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.AccountRepository;
import api.controla_preju.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private CreateAccountForm form;
    private Account account;
    private User user;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        user = new User("test@email.com", "test name", "password");
        ReflectionTestUtils.setField(user, "id", userId);

        form = new CreateAccountForm("Acc", "Desc", AccountType.INVESTMENTS,
                1000L, true);

        account = new Account(
                "Acc",
                "Desc",
                AccountType.INVESTMENTS,
                1000L,
                true,
                user
        );
        ReflectionTestUtils.setField(account, "id", UUID.randomUUID());

        ReflectionTestUtils.setField(accountService, "maxAccountsPerUser", 7);
    }

    @Test
    @DisplayName("Should create account successfully")
    void shouldCreateAccount() {
        when(accountRepository.existsByNameAndUser(form.name(), user)).thenReturn(false);
        when(accountRepository.countByUser(user)).thenReturn(0);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account created = accountService.create(form, user);

        assertNotNull(created);
        assertEquals(form.name(), created.getName());
        assertEquals(user, created.getUser());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when creating account with duplicate name for the same user")
    void shouldThrowExceptionWhenAccountNameIsDuplicate() {
        when(accountRepository.existsByNameAndUser(form.name(), user)).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                accountService.create(form, user)
        );

        assertEquals("O usuário já possui uma conta com este nome.", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user has reached the maximum number of accounts")
    void shouldThrowExceptionWhenMaxAccountsReached() {
        when(accountRepository.existsByNameAndUser(form.name(), user)).thenReturn(false);
        when(accountRepository.countByUser(user)).thenReturn(7);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                accountService.create(form, user)
        );

        assertEquals("O usuário atingiu o limite de quantidade de contas.", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AuthorizationException when account does not belong to user")
    void shouldThrowExceptionWhenAccountNotBelongsToUser() {
        UUID otherUserId = UUID.randomUUID();
        UUID accountId = account.getId();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        assertThrows(AuthorizationException.class, () ->
                accountService.findById(accountId, otherUserId)
        );
    }

    @Test
    @DisplayName("Should delete account successfully")
    void shouldDeleteAccount() {
        accountService.delete(account);
        verify(accountRepository).delete(account);
    }

    @Test
    @DisplayName("Should update balance successfully when account allows it")
    void shouldUpdateBalance() {
        UpdateBalanceForm balanceForm = new UpdateBalanceForm(5000L);
        when(accountRepository.save(account)).thenReturn(account);

        Account updatedAccount = accountService.updateBalance(account, balanceForm);

        assertEquals(5000L, updatedAccount.getBalanceInCents());
        verify(accountRepository).save(account);
    }

}
