package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateCreditCardForm;
import api.controla_preju.dtos.forms.UpdateCreditCardForm;
import api.controla_preju.entities.CreditCard;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.CreditCardRepository;
import api.controla_preju.repositories.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardServiceTest {

    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private CreditCardService creditCardService;

    private User user;
    private CreditCard creditCard;
    private CreateCreditCardForm createForm;
    private UUID userId;
    private UUID cardId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User("test@email.com", "User Test", "password");
        ReflectionTestUtils.setField(user, "id", userId);

        createForm = new CreateCreditCardForm("Nubank", 500000L, 5, 12);

        cardId = UUID.randomUUID();
        creditCard = new CreditCard("Nubank", 500000L, 5, 12, user);
        ReflectionTestUtils.setField(creditCard, "id", cardId);
    }

    @Test
    @DisplayName("Should create credit card successfully")
    void shouldCreateCreditCard() {
        when(creditCardRepository.existsByNameAndUser(createForm.name(), user)).thenReturn(false);
        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(creditCard);

        CreditCard result = creditCardService.create(createForm, user);

        assertNotNull(result);
        assertEquals(500000L, result.getLimitInCents());
        assertEquals(500000L, result.getAvailableLimitInCents());
        verify(creditCardRepository).save(any(CreditCard.class));
    }

    @Test
    @DisplayName("Should throw exception when card name is duplicated")
    void shouldThrowExceptionWhenDuplicateName() {
        when(creditCardRepository.existsByNameAndUser(createForm.name(), user)).thenReturn(true);

        assertThrows(BusinessException.class, () -> creditCardService.create(createForm, user));
        verify(creditCardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update credit card limit and adjust available limit proportionally")
    void shouldUpdateCreditCardLimit() {
        creditCard.setAvailableLimitInCents(200000L);
        UpdateCreditCardForm updateForm = new UpdateCreditCardForm("Nubank Platinum", 600000L, null, null);

        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(creditCard);

        CreditCard updatedCard = creditCardService.update(creditCard, updateForm);

        assertEquals("Nubank Platinum", updatedCard.getName());
        assertEquals(600000L, updatedCard.getLimitInCents());
        assertEquals(300000L, updatedCard.getAvailableLimitInCents());
        verify(creditCardRepository).save(creditCard);
    }

    @Test
    @DisplayName("Should delete credit card when it has no invoices")
    void shouldDeleteCreditCard() {
        when(invoiceRepository.existsByCreditCardId(cardId)).thenReturn(false);

        creditCardService.delete(creditCard);

        verify(creditCardRepository).delete(creditCard);
    }

    @Test
    @DisplayName("Should throw exception when deleting credit card with generated invoices")
    void shouldThrowExceptionWhenDeletingCardWithInvoices() {
        when(invoiceRepository.existsByCreditCardId(cardId)).thenReturn(true);

        assertThrows(BusinessException.class, () -> creditCardService.delete(creditCard));
        verify(creditCardRepository, never()).delete(any());
    }

}
