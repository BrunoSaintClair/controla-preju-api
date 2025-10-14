package api.controla_preju.repositories.jpa;

import api.controla_preju.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExpenseJpaRepository extends JpaRepository<Expense, UUID> {

}
