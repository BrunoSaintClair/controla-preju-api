package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateCreditCardForm;
import api.controla_preju.dtos.forms.UpdateCreditCardForm;
import api.controla_preju.dtos.views.CreditCardDetailsView;
import api.controla_preju.entities.User;
import api.controla_preju.services.CreditCardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/credit-cards")
public class CreditCardController {

    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @PostMapping
    public ResponseEntity<CreditCardDetailsView> create(@Valid @RequestBody CreateCreditCardForm form, @AuthenticationPrincipal User authenticatedUser) {
        var card = creditCardService.create(form, authenticatedUser);
        URI location = URI.create("/credit-cards/" + card.getId());
        return ResponseEntity.created(location).body(new CreditCardDetailsView(card));
    }

    @GetMapping
    public ResponseEntity<List<CreditCardDetailsView>> getAllByUser(@AuthenticationPrincipal User authenticatedUser) {
        var cards = creditCardService.findAllByUserId(authenticatedUser).stream().map(CreditCardDetailsView::new).toList();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditCardDetailsView> getById(@PathVariable UUID id, @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(new CreditCardDetailsView(creditCardService.findById(id, userId)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CreditCardDetailsView> update(@PathVariable UUID id, @Valid @RequestBody UpdateCreditCardForm form, @AuthenticationPrincipal(expression = "id") UUID userId) {
        var card = creditCardService.findById(id, userId);
        var updatedCard = creditCardService.update(card, form);
        return ResponseEntity.ok(new CreditCardDetailsView(updatedCard));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal(expression = "id") UUID userId) {
        var card = creditCardService.findById(id, userId);
        creditCardService.delete(card);
        return ResponseEntity.noContent().build();
    }

}