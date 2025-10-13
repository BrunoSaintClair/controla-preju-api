package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateRevenueForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Revenue;
import api.controla_preju.entities.User;
import api.controla_preju.repositories.RevenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RevenueService {

    private final RevenueRepository revenueRepository;
    private final AccountService accountService;

    public RevenueService(RevenueRepository revenueRepository, AccountService accountService) {
        this.revenueRepository = revenueRepository;
        this.accountService = accountService;
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

        return revenueRepository.save(revenue);
    }

}
