package api.controla_preju.services;

import api.controla_preju.repositories.AccountRepository;
import api.controla_preju.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;


    @Test
    @DisplayName("Should create account successfully")
    void shouldCreateAccount() {
    }

    @Test
    @DisplayName("Should throw exception when creating account with duplicate name for the same user")
    void shouldThrowExceptionWhenAccountNameIsDuplicate() {
    }

    @Test
    @DisplayName("Should throw exception when user has reached the maximum number of accounts")
    void shouldThrowExceptionWhenMaxAccountsReached() {
    }

    @Test
    @DisplayName("Should find every account from user")
    void shouldFindEveryAccountFromUser() {
    }

    @Test
    @DisplayName("Should find account by ID successfully when account belongs to user")
    void shouldFindById() {
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when account is not found")
    void shouldThrowExceptionWhenAccountNotFound() {
    }

    @Test
    @DisplayName("Should throw AuthorizationException when account does not belong to user")
    void shouldThrowExceptionWhenAccountNotBelongsToUser() {
    }

    @Test
    @DisplayName("Should delete account successfully")
    void shouldDeleteAccount() {
    }

    @Test
    @DisplayName("Should update balance successfully when account allows it")
    void shouldUpdateBalance() {
    }

    @Test
    @DisplayName("Should throw exception when updating balance if account does not allow it")
    void shouldThrowExceptionWhenUpdateBalanceNotAllowed() {
    }

}
