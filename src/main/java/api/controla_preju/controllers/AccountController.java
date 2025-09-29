package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateAccountForm;
import api.controla_preju.dtos.views.AccountDetailsView;
import api.controla_preju.dtos.views.CreatedAccountView;
import api.controla_preju.entities.User;
import api.controla_preju.services.AccountService;
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

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<CreatedAccountView> create(@Valid @RequestBody CreateAccountForm form,
                                                     @AuthenticationPrincipal User authenticatedUser) {
        var newAccount = accountService.create(form, authenticatedUser);
        var response = new CreatedAccountView(
                newAccount.getId(), newAccount.getName(),
                newAccount.getDescription(), newAccount.getType()
        );
        URI location = URI.create("/accounts/" + newAccount.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountDetailsView>> getAllByUser(@AuthenticationPrincipal(expression = "id") UUID userId){
        List<AccountDetailsView> accounts = accountService.findAllByUserId(userId).stream()
                .map(account -> new AccountDetailsView(
                        account.getId(),
                        account.getName(),
                        account.getDescription(),
                        account.getType(),
                        account.getBalanceInCents(),
                        account.getCreatedAt()
                ))
                .collect(Collectors.toList());
        if (accounts.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDetailsView> getById(@PathVariable UUID accountId,
                                                      @AuthenticationPrincipal(expression = "id") UUID userId){
        var account = accountService.findById(accountId, userId);
        var response = new AccountDetailsView(
                account.getId(), account.getName(), account.getDescription(), account.getType(),
                account.getBalanceInCents(), account.getCreatedAt()
        );
        return ResponseEntity.ok(response);
    }

}
