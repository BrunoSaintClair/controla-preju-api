package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateExpenseForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Expense;
import api.controla_preju.entities.User;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.repositories.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseRepository expensesRepository;
    private final AccountService accountService;

    public ExpenseService(ExpenseRepository expensesRepository, AccountService accountService) {
        this.expensesRepository = expensesRepository;
        this.accountService = accountService;
    }

    public Expense findById(UUID expenseId, UUID userId) {
        Optional<Expense> optionalExpense = expensesRepository.findById(expenseId);
        if (optionalExpense.isEmpty()) throw new EntityNotFoundException("Despesa não encontrada.");

        Expense expense = optionalExpense.get();
        if (!expense.getAccount().getUser().getId().equals(userId)) {
            throw new AuthorizationException("Esta despesa não pertence ao usuário que está efetuando a requisição.");
        }
        return expense;
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

    @Transactional
    public void delete(Expense expense) {
        Account account = expense.getAccount();
        account.setBalanceInCents(account.getBalanceInCents() + expense.getAmountInCents());
        expensesRepository.delete(expense);
    }

}
