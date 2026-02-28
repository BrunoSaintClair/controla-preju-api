package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateRevenueForm;
import api.controla_preju.dtos.forms.UpdateRevenueForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Revenue;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.AccountType;
import api.controla_preju.entities.enums.RevenueCategory;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.RevenueRepository;
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
class RevenueServiceTest {

    @Mock
    private RevenueRepository revenueRepository;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private RevenueService revenueService;

    private User user;
    private Account account;
    private CreateRevenueForm createForm;
    private Revenue revenue;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User("test@email.com", "User Test", "password");
        ReflectionTestUtils.setField(user, "id", userId);

        account = new Account("Corrente", "Conta principal", AccountType.CONTA_CORRENTE, 1000L, true, user);
        ReflectionTestUtils.setField(account, "id", UUID.randomUUID());

        createForm = new CreateRevenueForm(
                "Salário",
                "Recebimento mensal",
                5000L,
                RevenueCategory.SALARIO,
                LocalDateTime.now(),
                account.getId()
        );

        revenue = new Revenue(
                createForm.title(),
                createForm.description(),
                createForm.amountInCents(),
                createForm.category(),
                createForm.createdAt(),
                account
        );
        ReflectionTestUtils.setField(revenue, "id", UUID.randomUUID());
    }

    @Test
    @DisplayName("Should create revenue and increase account balance successfully")
    void shouldCreateRevenueSuccessfully() {
        when(revenueRepository.existsDuplicate(anyString(), any(), anyLong(), any())).thenReturn(false);
        when(accountService.findById(account.getId(), userId)).thenReturn(account);
        when(revenueRepository.save(any(Revenue.class))).thenReturn(revenue);

        Revenue result = revenueService.create(createForm, user);

        assertNotNull(result);
        assertEquals(6000L, account.getBalanceInCents());
        verify(revenueRepository).save(any(Revenue.class));
    }

    @Test
    @DisplayName("Should throw exception when revenue is duplicate")
    void shouldThrowExceptionWhenRevenueIsDuplicate() {
        when(revenueRepository.existsDuplicate(anyString(), any(), anyLong(), any())).thenReturn(true);

        assertThrows(BusinessException.class, () -> revenueService.create(createForm, user));
        verify(revenueRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find revenue by id successfully")
    void shouldFindByIdSuccessfully() {
        when(revenueRepository.findById(revenue.getId())).thenReturn(Optional.of(revenue));

        Revenue result = revenueService.findById(revenue.getId(), userId);

        assertNotNull(result);
        assertEquals(revenue.getId(), result.getId());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when revenue does not exist")
    void shouldThrowExceptionWhenRevenueNotFound() {
        UUID id = UUID.randomUUID();
        when(revenueRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> revenueService.findById(id, userId));
    }

    @Test
    @DisplayName("Should throw AuthorizationException when user does not own the account linked to revenue")
    void shouldThrowExceptionWhenUserDoesNotOwnRevenue() {
        UUID otherUserId = UUID.randomUUID();
        when(revenueRepository.findById(revenue.getId())).thenReturn(Optional.of(revenue));

        assertThrows(AuthorizationException.class, () -> revenueService.findById(revenue.getId(), otherUserId));
    }

    @Test
    @DisplayName("Should decrease account balance when revenue is deleted")
    void shouldRestoreBalanceOnDelete() {
        account.setBalanceInCents(6000L);

        revenueService.delete(revenue);

        assertEquals(1000L, account.getBalanceInCents());
        verify(revenueRepository).delete(revenue);
    }

    @Test
    @DisplayName("Should update amount and adjust account balance correctly")
    void shouldUpdateRevenueAmount() {
        UpdateRevenueForm updateForm = new UpdateRevenueForm(null, null, 7000L, null, null);

        when(revenueRepository.save(any(Revenue.class))).thenReturn(revenue);

        revenueService.update(revenue, updateForm);

        assertEquals(3000L, account.getBalanceInCents());
        verify(revenueRepository).save(revenue);
    }

}
