package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateExpenseForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Expense;
import api.controla_preju.entities.User;
import api.controla_preju.repositories.ExpenseRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    private final ExpenseRepository expensesRepository;
    private final AccountService accountService;

    public ExpenseService(ExpenseRepository expensesRepository, AccountService accountService) {
        this.expensesRepository = expensesRepository;
        this.accountService = accountService;
    }

    @Transactional
    public Expense create(@Valid CreateExpenseForm form, User owner) {
        Account account = accountService.findById(form.accountId(), owner.getId());

        Expense expense = new Expense(
                form.title(),
                form.description(),
                form.amountInCents(),
                form.paymentMethod(),
                form.category(),
                form.createdAt(),
                account
        );

        account.setBalanceInCents(account.getBalanceInCents() - form.amountInCents());
        return expensesRepository.save(expense);
    }

}
