package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateRevenueForm;
import api.controla_preju.dtos.views.CreatedRevenueView;
import api.controla_preju.entities.User;
import api.controla_preju.services.RevenueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/revenues")
public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @PostMapping
    public ResponseEntity<CreatedRevenueView> create(@Valid @RequestBody CreateRevenueForm form,
                                                     @AuthenticationPrincipal User user){
        var newRevenue = revenueService.create(form, user);
        var response = new CreatedRevenueView(newRevenue);
        URI location = URI.create("/revenues/" + newRevenue.getId());
        return ResponseEntity.created(location).body(response);
    }

}
