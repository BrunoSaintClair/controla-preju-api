package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.PayInvoiceForm;
import api.controla_preju.dtos.views.InvoiceDetailsView;
import api.controla_preju.entities.User;
import api.controla_preju.services.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDetailsView> getById(@PathVariable UUID id, @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(new InvoiceDetailsView(invoiceService.findById(id, userId)));
    }

    @GetMapping("/credit-card/{creditCardId}")
    public ResponseEntity<List<InvoiceDetailsView>> getByCreditCard(@PathVariable UUID creditCardId, @AuthenticationPrincipal(expression = "id") UUID userId) {
        var invoices = invoiceService.findAllByCreditCardId(creditCardId, userId).stream().map(InvoiceDetailsView::new).toList();
        return ResponseEntity.ok(invoices);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Void> pay(@PathVariable UUID id, @Valid @RequestBody PayInvoiceForm form, @AuthenticationPrincipal User authenticatedUser) {
        invoiceService.payInvoice(id, form, authenticatedUser);
        return ResponseEntity.noContent().build();
    }

}