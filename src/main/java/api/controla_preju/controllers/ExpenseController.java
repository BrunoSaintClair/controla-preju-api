package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateExpenseForm;
import api.controla_preju.dtos.views.CreatedExpenseView;
import api.controla_preju.entities.User;
import api.controla_preju.services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expensesService;

    public ExpenseController(ExpenseService expensesService) {
        this.expensesService = expensesService;
    }

    @PostMapping
    public ResponseEntity<CreatedExpenseView> create(@Valid @RequestBody CreateExpenseForm form,
                                                     @AuthenticationPrincipal User user) {
        var newExpense = expensesService.create(form, user);
        var response = new CreatedExpenseView(newExpense);
        URI location = URI.create("/expenses/" + newExpense.getId());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> delete(@PathVariable UUID expenseId,
                                       @AuthenticationPrincipal(expression = "id") UUID userId) {
        var expense = expensesService.findById(expenseId, userId);
        expensesService.delete(expense);
        return ResponseEntity.noContent().build();
    }

}
