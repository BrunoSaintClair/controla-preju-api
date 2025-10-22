package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateTransferForm;
import api.controla_preju.dtos.views.TransferDetailsView;
import api.controla_preju.services.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
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

}
