package api.controla_preju.repositories;

import api.controla_preju.entities.Expense;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.repositories.jpa.ExpenseJpaRepository;
import org.springframework.stereotype.Repository;

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

    public List<Expense> findAllByUserId(UUID userId){
        return expenseJpaRepository.findAllByUserId(userId);
    }

    public List<Expense> findAllByAccountId(UUID accountId) {
        return expenseJpaRepository.findAllByAccountId(accountId);
    }

    public List<Expense> findAllByAccountIdAndYearAndMonth(UUID accountId, int year, int month) {
        return expenseJpaRepository.findAllByAccountIdAndYearAndMonth(accountId, year, month);
    }

    public List<Expense> findAllByAccountIdAndYearAndMonthAndDay(UUID accountId, int year, int month, int day) {
        return expenseJpaRepository.findAllByAccountIdAndYearAndMonthAndDay(accountId, year, month, day);
    }

    public List<Expense> findAllByUserIdAndPaymentMethod(UUID userId, PaymentMethod paymentMethod) {
        return expenseJpaRepository.findAllByUserIdAndPaymentMethod(userId, paymentMethod);
    }

    public List<Expense> findAllByAccountIdAndPaymentMethod(UUID accountId, PaymentMethod paymentMethod) {
        return expenseJpaRepository.findAllByAccountIdAndPaymentMethod(accountId, paymentMethod);
    }

}
