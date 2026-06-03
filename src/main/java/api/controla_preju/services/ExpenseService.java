package api.controla_preju.services;

import api.controla_preju.dtos.forms.CreateExpenseForm;
import api.controla_preju.dtos.forms.UpdateExpenseForm;
import api.controla_preju.entities.*;
import api.controla_preju.entities.enums.AccountType;
import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.entities.enums.TransactionStatus;
import api.controla_preju.exceptions.AuthorizationException;
import api.controla_preju.exceptions.BusinessException;
import api.controla_preju.repositories.AccountRepository;
import api.controla_preju.repositories.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final CreditCardService creditCardService;
    private final InvoiceService invoiceService;
    private final EmailService emailService;
    private final TransactionTemplate transactionTemplate;

    public ExpenseService(ExpenseRepository expenseRepository, AccountService accountService,
                          AccountRepository accountRepository, CreditCardService creditCardService,
                          InvoiceService invoiceService, EmailService emailService,
                          PlatformTransactionManager transactionManager) {
        this.expenseRepository = expenseRepository;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.creditCardService = creditCardService;
        this.invoiceService = invoiceService;
        this.emailService = emailService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public Expense findById(UUID expenseId, UUID userId) {
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new EntityNotFoundException("Despesa não encontrada."));
        boolean ownsAccount = expense.getAccount() != null && expense.getAccount().getUser().getId().equals(userId);
        boolean ownsCreditCard = expense.getInvoice() != null && expense.getInvoice().getCreditCard().getUser().getId().equals(userId);

        if (!ownsAccount && !ownsCreditCard) {
            throw new AuthorizationException("Esta despesa não pertence a você.");
        }
        return expense;
    }

    @Transactional
    public List<Expense> create(CreateExpenseForm form, User owner) {
        if (form.paymentMethod() == PaymentMethod.CREDIT_CARD) {
            return processCreditCardExpense(form, owner);
        } else {
            return List.of(processStandardExpense(form, owner));
        }
    }

    private List<Expense> processCreditCardExpense(CreateExpenseForm form, User owner) {
        if (form.creditCardId() == null) throw new BusinessException("ID do cartão de crédito é obrigatório.");

        CreditCard card = creditCardService.findById(form.creditCardId(), owner.getId());
        if (card.getAvailableLimitInCents() < form.amountInCents()) throw new BusinessException("Limite disponível insuficiente.");

        int installments = (form.installments() != null && form.installments() > 0) ? form.installments() : 1;
        long amountPerInstallment = form.amountInCents() / installments;
        long remainder = form.amountInCents() % installments;

        card.subtractLimit(form.amountInCents());
        creditCardService.save(card);

        List<Expense> expenses = new ArrayList<>();
        for (int i = 0; i < installments; i++) {
            long currentInstallmentAmount = amountPerInstallment + (i == 0 ? remainder : 0);
            Invoice invoice = invoiceService.getOrCreateInvoiceForFutureMonth(card, form.createdAt(), i);
            invoice.addAmount(currentInstallmentAmount);
            invoiceService.save(invoice);

            String title = installments > 1 ? form.title() + " (" + (i + 1) + "/" + installments + ")" : form.title();
            Expense expense = new Expense(
                    title, form.description(), currentInstallmentAmount, form.paymentMethod(),
                    form.category(), form.createdAt(), TransactionStatus.COMPLETED, false,
                    invoice, null
            );
            expenses.add(expenseRepository.save(expense));
        }
        return expenses;
    }

    private Expense processStandardExpense(CreateExpenseForm form, User owner) {
        if (form.accountId() == null) throw new BusinessException("ID da conta é obrigatório.");
        if (expenseRepository.existsDuplicate(form.title(), form.createdAt(), form.amountInCents(), form.category())) {
            throw new BusinessException("Uma despesa com exatamente os mesmos dados já foi registrada.");
        }
        if (form.status() == TransactionStatus.COMPLETED && form.createdAt().isAfter(LocalDateTime.now().plusMinutes(2))) {
            throw new BusinessException("Transações com data futura devem ser registradas como PENDENTES.");
        }

        Account account = accountService.findById(form.accountId(), owner.getId());

        if (form.status() == TransactionStatus.COMPLETED) {
            if (account.getBalanceInCents() < form.amountInCents()) throw new BusinessException("Saldo insuficiente.");
            if (form.automaticDebit()) throw new BusinessException("Despesas automáticas não podem ser COMPLETED.");
            account.setBalanceInCents(account.getBalanceInCents() - form.amountInCents());
        }

        if (form.automaticDebit()) {
            if (form.createdAt().isBefore(LocalDateTime.now())) throw new BusinessException("Data de criação deve ser futura.");
            if (account.getType() == AccountType.WALLET) throw new BusinessException("Carteira não suporta débito automático.");
        }

        Expense expense = new Expense(
                form.title(), form.description(), form.amountInCents(),
                form.paymentMethod(), form.category(), form.createdAt(),
                form.status(), form.automaticDebit(), null, account);
        return expenseRepository.save(expense);
    }

    @Transactional
    public void delete(Expense expense) {
        if (expense.getPaymentMethod() == PaymentMethod.CREDIT_CARD) {
            Invoice invoice = expense.getInvoice();
            invoice.subtractAmount(expense.getAmountInCents());
            invoice.getCreditCard().restoreLimit(expense.getAmountInCents());
        } else if (expense.getStatus() == TransactionStatus.COMPLETED) {
            Account account = expense.getAccount();
            account.setBalanceInCents(account.getBalanceInCents() + expense.getAmountInCents());
        }
        expenseRepository.delete(expense);
    }

    @Transactional
    public Expense update(Expense expense, UpdateExpenseForm form) {
        if (expense.getPaymentMethod() == PaymentMethod.CREDIT_CARD) {
            if (form.amountInCents() != null || form.paymentMethod() != null) {
                throw new BusinessException("Não é possível alterar valor ou método de uma despesa de crédito. Exclua e recrie.");
            }
            if (form.title() != null) expense.setTitle(form.title());
            if (form.description() != null) expense.setDescription(form.description());
            if (form.category() != null) expense.setCategory(form.category());
            return expenseRepository.save(expense);
        }

        Account account = expense.getAccount();
        TransactionStatus oldStatus = expense.getStatus();
        long oldAmount = expense.getAmountInCents();

        if (form.title() != null) expense.setTitle(form.title());
        if (form.description() != null) expense.setDescription(form.description());
        if (form.amountInCents() != null) expense.setAmountInCents(form.amountInCents());
        if (form.paymentMethod() != null) expense.setPaymentMethod(form.paymentMethod());
        if (form.category() != null) expense.setCategory(form.category());
        if (form.createdAt() != null) expense.setCreatedAt(form.createdAt());
        if (form.status() != null) expense.setStatus(form.status());
        if (form.automaticDebit() != null) expense.setAutomaticDebit(form.automaticDebit());

        long effectiveOldImpact = (oldStatus == TransactionStatus.COMPLETED) ? oldAmount : 0;
        long effectiveNewImpact = (expense.getStatus() == TransactionStatus.COMPLETED) ? expense.getAmountInCents() : 0;
        long differenceToSubtract = effectiveNewImpact - effectiveOldImpact;

        if (differenceToSubtract != 0) {
            long newBalance = account.getBalanceInCents() - differenceToSubtract;
            if (newBalance < 0) throw new BusinessException("Saldo insuficiente.");
            account.setBalanceInCents(newBalance);
        }

        return expenseRepository.save(expense);
    }

    public Page<Expense> findAllByUserId(UUID userId, Optional<PaymentMethod> paymentMethod, Optional<ExpenseCategory> category, Pageable pageable) {
        if (paymentMethod.isPresent()) return expenseRepository.findAllByUserIdAndPaymentMethod(userId, paymentMethod.get(), pageable);
        if (category.isPresent()) return expenseRepository.findAllByUserIdAndCategory(userId, category.get(), pageable);
        return expenseRepository.findAllByUserId(userId, pageable);
    }

    public Page<Expense> findExpensesByAccount(UUID accountId, UUID userId,
                                               Optional<Integer> year, Optional<Integer> month,
                                               Optional<Integer> day, Optional<PaymentMethod> paymentMethod,
                                               Optional<ExpenseCategory> category, Pageable pageable) {
        accountService.findById(accountId, userId);

        if (paymentMethod.isPresent()) {
            return expenseRepository.findAllByAccountIdAndPaymentMethod(accountId, paymentMethod.get(), pageable);
        }
        if (category.isPresent()) {
            return expenseRepository.findAllByAccountIdAndCategory(accountId, category.get(), pageable);
        }
        if (year.isPresent() && month.isPresent() && day.isPresent()) {
            return expenseRepository.findAllByAccountIdAndYearAndMonthAndDay(accountId, year.get(), month.get(), day.get(), pageable);
        }
        if (year.isPresent() && month.isPresent()) {
            return expenseRepository.findAllByAccountIdAndYearAndMonth(accountId, year.get(), month.get(), pageable);
        }
        if (year.isEmpty() && month.isEmpty() && day.isEmpty()) {
            return expenseRepository.findAllByAccountId(accountId, pageable);
        }

        throw new BusinessException("Combinação de filtros inválida ou não suportada.");
    }

    public void processAutomaticDebits() {
        LocalDateTime now = LocalDateTime.now();
        List<Expense> pendingExpenses = expenseRepository.findAllByStatusAndAutomaticDebitTrueAndCreatedAtBefore(TransactionStatus.PENDING, now);
        Map<User, List<Expense>> failedExpensesByUser = new HashMap<>();

        for (Expense expense : pendingExpenses) {
            transactionTemplate.executeWithoutResult(status -> {
                try {
                    Account account = expense.getAccount();
                    int updatedRows = accountRepository.subtractBalanceIfSufficient(account.getId(), expense.getAmountInCents());
                    if (updatedRows > 0) {
                        expense.setStatus(TransactionStatus.COMPLETED);
                        expense.setAutomaticDebit(false);
                    } else {
                        expense.setStatus(TransactionStatus.FAILED);
                        expense.setAutomaticDebit(false);
                        failedExpensesByUser.computeIfAbsent(account.getUser(), k -> new ArrayList<>()).add(expense);
                    }
                    expenseRepository.save(expense);
                } catch (Exception e) {
                    status.setRollbackOnly();
                }
            });
        }

        for (Map.Entry<User, List<Expense>> entry : failedExpensesByUser.entrySet()) {
            try {
                emailService.sendFailedAutomaticDebitsEmail(entry.getKey(), entry.getValue());
            } catch (Exception ignored) {
            }
        }
    }

}
