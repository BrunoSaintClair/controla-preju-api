package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateAccountForm;
import api.controla_preju.dtos.forms.UpdateAccountForm;
import api.controla_preju.dtos.forms.UpdateBalanceForm;
import api.controla_preju.dtos.forms.UpdateCanChangeBalanceForm;
import api.controla_preju.dtos.views.AccountDetailsView;
import api.controla_preju.dtos.views.CreatedAccountView;
import api.controla_preju.dtos.views.RevenueDetailsView;
import api.controla_preju.entities.User;
import api.controla_preju.services.AccountService;
import api.controla_preju.services.RevenueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final RevenueService revenueService;

    public AccountController(AccountService accountService, RevenueService revenueService) {
        this.accountService = accountService;
        this.revenueService = revenueService;
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
                .collect(Collectors.toList());
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
    public ResponseEntity<List<RevenueDetailsView>> getAllRevenuesByAccount(@PathVariable UUID accountId,
                                                                            @AuthenticationPrincipal(expression = "id") UUID userId) {
        List<RevenueDetailsView> revenues = revenueService.
                findAllByAccountId(accountId, userId)
                .stream()
                .map(RevenueDetailsView::new)
                .collect(Collectors.toList());

        if (revenues.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(revenues);
    }

}
