package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateCreditCardForm;
import api.controla_preju.dtos.forms.UpdateCreditCardForm;
import api.controla_preju.entities.CreditCard;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.CreditCardRepository;
import api.controla_preju.repositories.InvoiceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final InvoiceRepository invoiceRepository;

    public CreditCardService(CreditCardRepository creditCardRepository, InvoiceRepository invoiceRepository) {
        this.creditCardRepository = creditCardRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public CreditCard create(CreateCreditCardForm form, User owner) {
        if (creditCardRepository.existsByNameAndUser(form.name(), owner)) {
            throw new BusinessException("Já existe um cartão com este nome.");
        }
        CreditCard card = new CreditCard(form.name(), form.limitInCents(), form.closingDay(), form.dueDay(), owner);
        return creditCardRepository.save(card);
    }

    public CreditCard findById(UUID id, UUID userId) {
        CreditCard card = creditCardRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cartão não encontrado."));
        if (!card.getUser().getId().equals(userId)) {
            throw new AuthorizationException("Este cartão não pertence a você.");
        }
        return card;
    }

    public List<CreditCard> findAllByUserId(User user) {
        return creditCardRepository.findAllByUser(user);
    }

    @Transactional
    public CreditCard update(CreditCard card, UpdateCreditCardForm form) {
        if (form.name() != null) card.setName(form.name());
        if (form.closingDay() != null) card.setClosingDay(form.closingDay());
        if (form.dueDay() != null) card.setDueDay(form.dueDay());
        if (form.limitInCents() != null) {
            long limitDifference = form.limitInCents() - card.getLimitInCents();
            card.setLimitInCents(form.limitInCents());
            card.setAvailableLimitInCents(card.getAvailableLimitInCents() + limitDifference);
        }
        return creditCardRepository.save(card);
    }

    @Transactional
    public void delete(CreditCard card) {
        if (invoiceRepository.existsByCreditCardId(card.getId())) {
            throw new BusinessException("Não é possível excluir um cartão que possui faturas geradas.");
        }
        creditCardRepository.delete(card);
    }

    public CreditCard save(CreditCard card) {
        return creditCardRepository.save(card);
    }

}
