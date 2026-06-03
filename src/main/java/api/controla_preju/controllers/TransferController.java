package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateTransferForm;
import api.controla_preju.dtos.forms.UpdateTransferForm;
import api.controla_preju.dtos.views.TransferDetailsView;
import api.controla_preju.services.TransferService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }


    @PostMapping
    public ResponseEntity<TransferDetailsView> create(@Valid @RequestBody CreateTransferForm form,
                                                      @AuthenticationPrincipal(expression = "id") UUID userId) {
        var newTransfer = transferService.create(form, userId);
        var response = new TransferDetailsView(newTransfer);
        URI location = URI.create("/transfers/" + newTransfer.getId());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{transferId}")
    public ResponseEntity<Void> delete(@PathVariable UUID transferId,
                                       @AuthenticationPrincipal(expression = "id") UUID userId) {
        var transfer = transferService.findById(transferId, userId);
        transferService.delete(transfer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{transferId}")
    public ResponseEntity<TransferDetailsView> getById(@PathVariable UUID transferId,
                                       @AuthenticationPrincipal(expression = "id") UUID userId) {
        var transfer = transferService.findById(transferId, userId);
        var response = new TransferDetailsView(transfer);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TransferDetailsView>> getAllByUser(@AuthenticationPrincipal(expression = "id") UUID userId,
                                                                  @RequestParam(required = false) Optional<Integer> year,
                                                                  @RequestParam(required = false) Optional<Integer> month,
                                                                  @RequestParam(required = false) Optional<Integer> day,
                                                                  @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var transfers = transferService.findAllByUserId(userId, year, month, day, pageable)
                .map(TransferDetailsView::new);
        return ResponseEntity.ok(transfers);
    }

    @PatchMapping("/{transferId}")
    public ResponseEntity<TransferDetailsView> update(@PathVariable UUID transferId,
                                                      @Valid @RequestBody UpdateTransferForm form,
                                                      @AuthenticationPrincipal(expression = "id") UUID userId) {
        var transfer = transferService.findById(transferId, userId);
        var updatedTransfer = transferService.update(transfer, form);
        var response = new TransferDetailsView(updatedTransfer);
        return ResponseEntity.ok(response);
    }

}
