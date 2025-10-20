package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateExpenseForm;
import api.controla_preju.dtos.forms.UpdateExpenseForm;
import api.controla_preju.dtos.views.CreatedExpenseView;
import api.controla_preju.dtos.views.ExpenseDetailsView;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<CreatedExpenseView> create(@Valid @RequestBody CreateExpenseForm form,
                                                     @AuthenticationPrincipal User user) {
        var newExpense = expenseService.create(form, user);
        var response = new CreatedExpenseView(newExpense);
        URI location = URI.create("/expenses/" + newExpense.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDetailsView>> getAllByUser(
                                                @AuthenticationPrincipal(expression = "id") UUID userId,
                                                @RequestParam(required = false) Optional<PaymentMethod> paymentMethod) {

        var expenses = expenseService.findAllByUserId(userId, paymentMethod)
                .stream()
                .map(ExpenseDetailsView::new)
                .toList();
        if (expenses.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> delete(@PathVariable UUID expenseId,
                                       @AuthenticationPrincipal(expression = "id") UUID userId) {
        var expense = expenseService.findById(expenseId, userId);
        expenseService.delete(expense);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseDetailsView> getById(@PathVariable UUID expenseId,
                                                      @AuthenticationPrincipal(expression = "id") UUID userId) {
        var expense = expenseService.findById(expenseId, userId);
        var response = new ExpenseDetailsView(expense);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{expenseId}")
    public ResponseEntity<ExpenseDetailsView> update(@PathVariable UUID expenseId,
                                                     @Valid @RequestBody UpdateExpenseForm form,
                                                     @AuthenticationPrincipal(expression = "id") UUID userId){
        var expense = expenseService.findById(expenseId, userId);
        var updatedExpense = expenseService.update(expense, form);
        var response = new ExpenseDetailsView(updatedExpense);
        return ResponseEntity.ok(response);
    }

}
