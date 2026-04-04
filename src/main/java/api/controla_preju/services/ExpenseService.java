package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateExpenseForm;
import api.controla_preju.dtos.forms.UpdateExpenseForm;
import api.controla_preju.entities.Account;
import api.controla_preju.entities.Expense;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.entities.enums.TransactionStatus;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public Expense create(CreateExpenseForm form, User owner) {
        if (expenseRepository.existsDuplicate(form.title(), form.createdAt(), form.amountInCents(), form.category())) {
            throw new BusinessException("Uma despesa com exatamente os mesmos dados já foi registrada.");
        }

        Account account = accountService.findById(form.accountId(), owner.getId());

        if (form.status() == TransactionStatus.COMPLETED) {
            if (account.getBalanceInCents() < form.amountInCents()) {
                throw new BusinessException("Saldo insuficiente para registrar a despesa.");
            }
            account.setBalanceInCents(account.getBalanceInCents() - form.amountInCents());
        }

        Expense expense = new Expense(
                form.title(),
                form.description(),
                form.amountInCents(),
                form.paymentMethod(),
                form.category(),
                form.createdAt(),
                form.status(),
                account
        );

        return expenseRepository.save(expense);
    }

    @Transactional
    public void delete(Expense expense) {
        if (expense.getStatus() == TransactionStatus.COMPLETED) {
            Account account = expense.getAccount();
            account.setBalanceInCents(account.getBalanceInCents() + expense.getAmountInCents());
        }
        expenseRepository.delete(expense);
    }

    @Transactional
    public Expense update(Expense expense, UpdateExpenseForm form) {
        Account account = expense.getAccount();

        TransactionStatus oldStatus = expense.getStatus();
        long oldAmount = expense.getAmountInCents();

        if (form.title() != null) {
            if (form.title().isBlank()) {
                throw new BusinessException("O título da despesa não pode ser vazio.");
            }
            expense.setTitle(form.title());
        }
        if (form.description() != null) {
            if (form.description().isBlank()) {
                throw new BusinessException("A descrição da despesa não pode ser vazia.");
            }
            expense.setDescription(form.description());
        }
        if (form.amountInCents() != null) expense.setAmountInCents(form.amountInCents());
        if (form.paymentMethod() != null) expense.setPaymentMethod(form.paymentMethod());
        if (form.category() != null) expense.setCategory(form.category());
        if (form.createdAt() != null) expense.setCreatedAt(form.createdAt());
        if (form.status() != null) expense.setStatus(form.status());

        long effectiveOldImpact = (oldStatus == TransactionStatus.COMPLETED) ? oldAmount : 0;
        long effectiveNewImpact = (expense.getStatus() == TransactionStatus.COMPLETED) ? expense.getAmountInCents() : 0;

        long differenceToSubtract = effectiveNewImpact - effectiveOldImpact;

        if (differenceToSubtract != 0) {
            long newBalance = account.getBalanceInCents() - differenceToSubtract;
            if (newBalance < 0) {
                throw new BusinessException("Saldo insuficiente para efetivar a despesa.");
            }
            account.setBalanceInCents(newBalance);
        }

        return expenseRepository.save(expense);
    }

    public List<Expense> findAllByUserId(UUID userId, Optional<PaymentMethod> paymentMethod,
                                         Optional<ExpenseCategory> category) {
        if (paymentMethod.isPresent()) {
            return expenseRepository.findAllByUserIdAndPaymentMethod(userId, paymentMethod.get());
        }
        if (category.isPresent()) {
            return expenseRepository.findAllByUserIdAndCategory(userId, category.get());
        }
        return expenseRepository.findAllByUserId(userId);
    }

    public List<Expense> findExpensesByAccount(UUID accountId, UUID userId,
                                               Optional<Integer> year, Optional<Integer> month,
                                               Optional<Integer> day, Optional<PaymentMethod> paymentMethod,
                                               Optional<ExpenseCategory> category) {
        accountService.findById(accountId, userId);

        if (paymentMethod.isPresent()) {
            return expenseRepository.findAllByAccountIdAndPaymentMethod(accountId, paymentMethod.get());
        }
        if (category.isPresent()) {
            return expenseRepository.findAllByAccountIdAndCategory(accountId, category.get());
        }
        if (year.isPresent() && month.isPresent() && day.isPresent()) {
            return expenseRepository.findAllByAccountIdAndYearAndMonthAndDay(accountId, year.get(), month.get(), day.get());
        }
        if (year.isPresent() && month.isPresent()) {
            return expenseRepository.findAllByAccountIdAndYearAndMonth(accountId, year.get(), month.get());
        }
        if (year.isEmpty() && month.isEmpty() && day.isEmpty()) {
            return expenseRepository.findAllByAccountId(accountId);
        }
        throw new BusinessException("Combinação de filtros inválida ou não suportada.");
    }

}
