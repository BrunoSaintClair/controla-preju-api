package api.controla_preju.repositories;

import api.controla_preju.entities.Expense;
import api.controla_preju.repositories.jpa.ExpenseJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ExpenseRepository {

    private final ExpenseJpaRepository expensesJpaRepository;

    public ExpenseRepository(ExpenseJpaRepository expensesJpaRepository) {
        this.expensesJpaRepository = expensesJpaRepository;
    }

    public Optional<Expense> findById(UUID id) {
        return expensesJpaRepository.findById(id);
    }

    public Expense save(Expense expense){
        return expensesJpaRepository.save(expense);
    }

    public void delete(Expense expense) {
        expensesJpaRepository.delete(expense);
    }

}
