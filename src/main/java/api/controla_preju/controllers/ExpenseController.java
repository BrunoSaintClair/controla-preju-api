package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateExpenseForm;
import api.controla_preju.dtos.forms.UpdateExpenseForm;
import api.controla_preju.dtos.views.CreatedExpenseView;
import api.controla_preju.dtos.views.ExpenseDetailsView;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<CreatedExpenseView>> create(@Valid @RequestBody CreateExpenseForm form,
                                                           @AuthenticationPrincipal User user) {
        var newExpenses = expenseService.create(form, user);
        var response = newExpenses.stream().map(CreatedExpenseView::new).toList();
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseDetailsView>> getAllByUser(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestParam(required = false) Optional<PaymentMethod> paymentMethod,
            @RequestParam(required = false) Optional<ExpenseCategory> category,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        var expenses = expenseService.findAllByUserId(userId, paymentMethod, category, pageable)
                .map(ExpenseDetailsView::new);
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
