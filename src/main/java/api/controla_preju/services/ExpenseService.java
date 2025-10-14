package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateExpenseForm;
import api.controla_preju.dtos.forms.UpdateExpenseForm;
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

    private final ExpenseRepository expenseRepository;
    private final AccountService accountService;

    public ExpenseService(ExpenseRepository expenseRepository, AccountService accountService) {
        this.expenseRepository = expenseRepository;
        this.accountService = accountService;
    }

    public Expense findById(UUID expenseId, UUID userId) {
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
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
        return expenseRepository.save(expense);
    }

    @Transactional
    public void delete(Expense expense) {
        Account account = expense.getAccount();
        account.setBalanceInCents(account.getBalanceInCents() + expense.getAmountInCents());
        expenseRepository.delete(expense);
    }

    @Transactional
    public Expense update(Expense expense, UpdateExpenseForm form) {
        Account account = expense.getAccount();
        long oldAmount = expense.getAmountInCents();

        if (form.title() != null) expense.setTitle(form.title());
        if (form.description() != null) expense.setDescription(form.description());
        if (form.amountInCents() != null) expense.setAmountInCents(form.amountInCents());
        if (form.paymentMethod() != null) expense.setPaymentMethod(form.paymentMethod());
        if (form.category() != null) expense.setCategory(form.category());
        if (form.createdAt() != null) expense.setCreatedAt(form.createdAt());

        long newAmount = expense.getAmountInCents();
        long difference = newAmount - oldAmount;
        account.setBalanceInCents(account.getBalanceInCents() - difference);

        return expenseRepository.save(expense);
    }

}
