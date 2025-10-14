package api.controla_preju.repositories;

import api.controla_preju.entities.Expense;
import api.controla_preju.repositories.jpa.ExpenseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ExpenseRepository {

    private final ExpenseJpaRepository expensesJpaRepository;

    public ExpenseRepository(ExpenseJpaRepository expensesJpaRepository) {
        this.expensesJpaRepository = expensesJpaRepository;
    }

    public Expense save(Expense expense){
        return expensesJpaRepository.save(expense);
    }

}
