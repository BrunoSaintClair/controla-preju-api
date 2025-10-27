package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateAccountForm;
import api.controla_preju.dtos.forms.UpdateAccountForm;
import api.controla_preju.dtos.forms.UpdateBalanceForm;
import api.controla_preju.dtos.forms.UpdateCanChangeBalanceForm;
import api.controla_preju.dtos.views.*;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.ExpenseCategory;
import api.controla_preju.entities.enums.PaymentMethod;
import api.controla_preju.entities.enums.RevenueCategory;
import api.controla_preju.services.AccountService;
import api.controla_preju.services.ExpenseService;
import api.controla_preju.services.RevenueService;
import api.controla_preju.services.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final RevenueService revenueService;
    private final ExpenseService expenseService;
    private final TransferService transferService;

    public AccountController(AccountService accountService, RevenueService revenueService,
                             ExpenseService expenseService, TransferService transferService) {
        this.accountService = accountService;
        this.revenueService = revenueService;
        this.expenseService = expenseService;
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<CreatedAccountView> create(@Valid @RequestBody CreateAccountForm form,
                                                     @AuthenticationPrincipal User authenticatedUser) {
        var newAccount = accountService.create(form, authenticatedUser);
        var response = new CreatedAccountView(newAccount);
        URI location = URI.create("/accounts/" + newAccount.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountDetailsView>> getAllByUser(@AuthenticationPrincipal(expression = "id") UUID userId){
        List<AccountDetailsView> accounts = accountService.findAllByUserId(userId).stream()
                .map(AccountDetailsView::new)
                .toList();
        if (accounts.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDetailsView> getById(@PathVariable UUID accountId,
                                                      @AuthenticationPrincipal(expression = "id") UUID userId){
        var account = accountService.findById(accountId, userId);
        var response = new AccountDetailsView(account);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> delete(@PathVariable UUID accountId,
                                       @AuthenticationPrincipal(expression = "id") UUID userId){
        var account = accountService.findById(accountId, userId);
        accountService.delete(account);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<AccountDetailsView> update(@PathVariable UUID accountId,
                                                     @Valid @RequestBody UpdateAccountForm form,
                                                     @AuthenticationPrincipal(expression = "id") UUID userId){
        var oldAccount = accountService.findById(accountId, userId);
        var updatedAccount = accountService.update(oldAccount, form);
        var response = new AccountDetailsView(updatedAccount);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/balance")
    public ResponseEntity<AccountDetailsView> updateBalance(@PathVariable UUID accountId,
                                                            @Valid @RequestBody UpdateBalanceForm form,
                                                            @AuthenticationPrincipal(expression = "id") UUID userId) {
        var account = accountService.findById(accountId, userId);
        var updatedAccount = accountService.updateBalance(account, form);
        var response = new AccountDetailsView(updatedAccount);
        return ResponseEntity.ok(response);
    }


    @PatchMapping("/{accountId}/can-change-balance")
    public ResponseEntity<AccountDetailsView> updateCanChangeBalance(@PathVariable UUID accountId,
                                                                     @Valid @RequestBody UpdateCanChangeBalanceForm form,
                                                                     @AuthenticationPrincipal(expression = "id") UUID userId){
        var account = accountService.findById(accountId, userId);
        var updatedAccount = accountService.updateCanChangeBalance(account, form);
        var response = new AccountDetailsView(updatedAccount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}/revenues")
    public ResponseEntity<List<RevenueDetailsView>> getRevenuesByAccount(
                                                        @PathVariable UUID accountId,
                                                        @RequestParam(required = false) Optional<Integer> year,
                                                        @RequestParam(required = false) Optional<Integer> month,
                                                        @RequestParam(required = false) Optional<Integer> day,
                                                        @RequestParam(required = false) Optional<RevenueCategory> category,
                                                        @AuthenticationPrincipal(expression = "id") UUID userId) {
        List<RevenueDetailsView> revenues = revenueService
                .findRevenuesByAccount(accountId, userId, year, month, day, category)
                .stream()
                .map(RevenueDetailsView::new)
                .toList();
        if (revenues.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(revenues);
    }

    @GetMapping("/{accountId}/expenses")
    public ResponseEntity<List<ExpenseDetailsView>> getExpensesByAccount(
                                                        @PathVariable UUID accountId,
                                                        @RequestParam(required = false) Optional<Integer> year,
                                                        @RequestParam(required = false) Optional<Integer> month,
                                                        @RequestParam(required = false) Optional<Integer> day,
                                                        @RequestParam(required = false) Optional<PaymentMethod> paymentMethod,
                                                        @RequestParam(required = false) Optional<ExpenseCategory> category,
                                                        @AuthenticationPrincipal(expression = "id") UUID userId) {
        List<ExpenseDetailsView> expenses = expenseService
                .findExpensesByAccount(accountId, userId, year, month, day, paymentMethod, category)
                .stream()
                .map(ExpenseDetailsView::new)
                .toList();
        if (expenses.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/{accountId}/transfers/sent")
    public ResponseEntity<List<TransferDetailsView>> getSentTransfersByAccount(
                                                        @PathVariable UUID accountId,
                                                        @AuthenticationPrincipal(expression = "id") UUID userId) {
        List<TransferDetailsView> transfers = transferService.findAllBySourceAccount(accountId, userId)
                .stream()
                .map(TransferDetailsView::new)
                .toList();
        if (transfers.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(transfers);
    }

    @GetMapping("/{accountId}/transfers/received")
    public ResponseEntity<List<TransferDetailsView>> getReceivedTransfersByAccount(
                                                        @PathVariable UUID accountId,
                                                        @AuthenticationPrincipal(expression = "id") UUID userId) {
        List<TransferDetailsView> transfers = transferService.findAllByDestinationAccount(accountId, userId)
                .stream()
                .map(TransferDetailsView::new)
                .toList();
        if (transfers.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(transfers);
    }

}
