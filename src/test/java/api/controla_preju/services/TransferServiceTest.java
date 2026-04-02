package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateTransferForm;
import api.controla_preju.dtos.forms.UpdateTransferForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Transfer;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.AccountType;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.TransferRepository;
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
class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;
    @Mock
    private UserService userService;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransferService transferService;

    private User user;
    private Account sourceAccount;
    private Account destinationAccount;
    private CreateTransferForm createForm;
    private Transfer transfer;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User("test@email.com", "User Test", "password");
        ReflectionTestUtils.setField(user, "id", userId);

        sourceAccount = new Account("Origem", "Conta de saída", AccountType.CHECKING_ACCOUNT, 2000L, true, user);
        ReflectionTestUtils.setField(sourceAccount, "id", UUID.randomUUID());

        destinationAccount = new Account("Destino", "Conta de entrada", AccountType.SAVINGS_ACCOUNT, 500L, true, user);
        ReflectionTestUtils.setField(destinationAccount, "id", UUID.randomUUID());

        createForm = new CreateTransferForm(
                "Pix para Poupança",
                "Transferência mensal",
                1000L,
                LocalDateTime.now(),
                sourceAccount.getId(),
                destinationAccount.getId()
        );

        transfer = new Transfer(
                createForm.title(),
                createForm.description(),
                createForm.amountInCents(),
                createForm.createdAt(),
                sourceAccount,
                destinationAccount
        );
        ReflectionTestUtils.setField(transfer, "id", UUID.randomUUID());
    }

    @Test
    @DisplayName("Should create transfer and update balances successfully")
    void shouldCreateTransferSuccessfully() {
        when(transferRepository.existsDuplicate(anyString(), any(), anyLong())).thenReturn(false);
        when(accountService.findById(sourceAccount.getId(), userId)).thenReturn(sourceAccount);
        when(accountService.findById(destinationAccount.getId(), userId)).thenReturn(destinationAccount);
        when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);

        Transfer result = transferService.create(createForm, userId);

        assertNotNull(result);
        assertEquals(1000L, sourceAccount.getBalanceInCents());
        assertEquals(1500L, destinationAccount.getBalanceInCents());
        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    @DisplayName("Should throw exception when transfer is duplicate")
    void shouldThrowExceptionWhenTransferIsDuplicate() {
        when(transferRepository.existsDuplicate(anyString(), any(), anyLong())).thenReturn(true);

        assertThrows(BusinessException.class, () -> transferService.create(createForm, userId));
        verify(transferRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when accounts are the same")
    void shouldThrowExceptionWhenAccountsAreSame() {
        CreateTransferForm sameAccountForm = new CreateTransferForm(
                "Erro", "Mesma conta", 100L, LocalDateTime.now(),
                sourceAccount.getId(), sourceAccount.getId()
        );

        when(accountService.findById(any(), any())).thenReturn(sourceAccount);

        assertThrows(BusinessException.class, () -> transferService.create(sameAccountForm, userId));
    }

    @Test
    @DisplayName("Should throw exception when source balance is insufficient")
    void shouldThrowExceptionWhenInsufficientBalance() {
        sourceAccount.setBalanceInCents(500L);
        when(accountService.findById(sourceAccount.getId(), userId)).thenReturn(sourceAccount);
        when(accountService.findById(destinationAccount.getId(), userId)).thenReturn(destinationAccount);

        assertThrows(BusinessException.class, () -> transferService.create(createForm, userId));
    }

    @Test
    @DisplayName("Should throw AuthorizationException when user does not own both accounts")
    void shouldThrowExceptionWhenUserDoesNotOwnAccounts() {
        UUID otherUserId = UUID.randomUUID();
        when(transferRepository.findById(transfer.getId())).thenReturn(Optional.of(transfer));

        assertThrows(AuthorizationException.class, () -> transferService.findById(transfer.getId(), otherUserId));
    }

    @Test
    @DisplayName("Should revert balances correctly when transfer is deleted")
    void shouldRevertBalancesOnDelete() {
        sourceAccount.setBalanceInCents(1000L);
        destinationAccount.setBalanceInCents(1500L);

        transferService.delete(transfer);

        assertEquals(2000L, sourceAccount.getBalanceInCents());
        assertEquals(500L, destinationAccount.getBalanceInCents());
        verify(transferRepository).delete(transfer);
    }


    @Test
    @DisplayName("Should update amount and adjust balances correctly")
    void shouldUpdateTransferAmount() {
        UpdateTransferForm updateForm = new UpdateTransferForm(null, null, 1500L, null);

        when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);

        transferService.update(transfer, updateForm);

        assertEquals(1500L, sourceAccount.getBalanceInCents());
        assertEquals(1000L, destinationAccount.getBalanceInCents());
        verify(transferRepository).save(transfer);
    }

}