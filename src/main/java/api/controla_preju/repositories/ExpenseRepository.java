package api.controla_preju.repositories;

import api.controla_preju.entities.Expense;
import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.entities.enums.TransactionStatus;
import api.controla_preju.repositories.jpa.ExpenseJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ExpenseRepository {

    private final ExpenseJpaRepository expenseJpaRepository;

    public ExpenseRepository(ExpenseJpaRepository expenseJpaRepository) {
        this.expenseJpaRepository = expenseJpaRepository;
    }

    public Optional<Expense> findById(UUID id) {
        return expenseJpaRepository.findById(id);
    }

    public Expense save(Expense expense){
        return expenseJpaRepository.save(expense);
    }

    public void delete(Expense expense) {
        expenseJpaRepository.delete(expense);
    }

    public Page<Expense> findAllByUserId(UUID userId, Pageable pageable){
        return expenseJpaRepository.findAllByUserId(userId, pageable);
    }

    public Page<Expense> findAllByAccountId(UUID accountId, Pageable pageable) {
        return expenseJpaRepository.findAllByAccountId(accountId, pageable);
    }

    public Page<Expense> findAllByAccountIdAndYearAndMonth(UUID accountId, int year, int month, Pageable pageable) {
        return expenseJpaRepository.findAllByAccountIdAndYearAndMonth(accountId, year, month, pageable);
    }

    public Page<Expense> findAllByAccountIdAndYearAndMonthAndDay(UUID accountId, int year, int month, int day, Pageable pageable) {
        return expenseJpaRepository.findAllByAccountIdAndYearAndMonthAndDay(accountId, year, month, day, pageable);
    }

    public Page<Expense> findAllByUserIdAndPaymentMethod(UUID userId, PaymentMethod paymentMethod, Pageable pageable) {
        return expenseJpaRepository.findAllByUserIdAndPaymentMethod(userId, paymentMethod, pageable);
    }

    public Page<Expense> findAllByAccountIdAndPaymentMethod(UUID accountId, PaymentMethod paymentMethod, Pageable pageable) {
        return expenseJpaRepository.findAllByAccountIdAndPaymentMethod(accountId, paymentMethod, pageable);
    }

    public Page<Expense> findAllByUserIdAndCategory(UUID userId, ExpenseCategory category, Pageable pageable) {
        return expenseJpaRepository.findAllByUserIdAndCategory(userId, category, pageable);
    }

    public Page<Expense> findAllByAccountIdAndCategory(UUID accountId, ExpenseCategory category, Pageable pageable) {
        return expenseJpaRepository.findAllByAccountIdAndCategory(accountId, category, pageable);
    }

    public boolean existsByAccountIdAndStatusAndAutomaticDebitTrue(UUID accountId, TransactionStatus status) {
        return expenseJpaRepository.existsByAccountIdAndStatusAndAutomaticDebitTrue(accountId, status);
    }

    public boolean existsDuplicate(String title, LocalDateTime createdAt, long amountInCents, ExpenseCategory category) {
        return expenseJpaRepository.existsByTitleAndCreatedAtAndAmountInCentsAndCategory(
                title, createdAt, amountInCents, category
        );
    }

    public List<Expense> findAllByStatusAndAutomaticDebitTrueAndCreatedAtBefore(TransactionStatus status,
                                                                                LocalDateTime dateTime) {
        return expenseJpaRepository.findAllByStatusAndAutomaticDebitTrueAndCreatedAtBefore(status, dateTime);
    }

    public List<Expense> findAllByStatusAndAutomaticDebitFalseAndCreatedAtBefore(TransactionStatus status,
                                                                                 LocalDateTime dateTime) {
        return expenseJpaRepository.findAllByStatusAndAutomaticDebitFalseAndCreatedAtBefore(status, dateTime);
    }

}
