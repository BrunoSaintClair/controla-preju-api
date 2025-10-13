package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateRevenueForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Revenue;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.repositories.RevenueRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class RevenueService {

    private final RevenueRepository revenueRepository;
    private final AccountService accountService;

    public RevenueService(RevenueRepository revenueRepository, AccountService accountService) {
        this.revenueRepository = revenueRepository;
        this.accountService = accountService;
    }

    public Revenue findById(UUID revenueId, UUID userId) {
        Optional<Revenue> optionalRevenue = revenueRepository.findById(revenueId);
        if (optionalRevenue.isEmpty()) {
            throw new EntityNotFoundException("Receita não encontrada.");
        }

        Revenue revenue = optionalRevenue.get();
        if (!revenue.getAccount().getUser().getId().equals(userId)) {
            throw new AuthorizationException("Esta receita não pertence ao usuário que está efetuando a requisição.");
        }

        return revenue;
    }

    @Transactional
    public Revenue create(CreateRevenueForm form, User owner){
        Account account = accountService.findById(form.accountId(), owner.getId());

        Revenue revenue = new Revenue(
                form.title(),
                form.description(),
                form.amountInCents(),
                form.category(),
                form.createdAt(),
                account
        );

        account.setBalanceInCents(account.getBalanceInCents() + revenue.getAmountInCents());
        return revenueRepository.save(revenue);
    }

    @Transactional
    public void delete(Revenue revenue) {
        Account account = revenue.getAccount();
        account.setBalanceInCents(account.getBalanceInCents() - revenue.getAmountInCents());
        revenueRepository.delete(revenue);
    }

    public List<Revenue> findAllByAccountId(UUID accountId, UUID userId) {
        accountService.findById(accountId, userId);
        return revenueRepository.findAllByAccountId(accountId);
    }

}
