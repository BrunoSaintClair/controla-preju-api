package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateRevenueForm;
import api.controla_preju.dtos.forms.UpdateRevenueForm;
import api.controla_preju.dtos.views.CreatedRevenueView;
import api.controla_preju.dtos.views.RevenueDetailsView;
import api.controla_preju.entities.User;
import api.controla_preju.entities.enums.RevenueCategory;
import api.controla_preju.services.RevenueService;
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

    @GetMapping
    public ResponseEntity<Page<RevenueDetailsView>> getAllByUser(
                                                    @AuthenticationPrincipal(expression = "id") UUID userId,
                                                    @RequestParam(required = false) Optional<RevenueCategory> category,
                                                    @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var revenues = revenueService.findAllByUserId(userId, category, pageable)
                .map(RevenueDetailsView::new);
        return ResponseEntity.ok(revenues);
    }

    @DeleteMapping("/{revenueId}")
    public ResponseEntity<Void> delete(@PathVariable UUID revenueId,
                                       @AuthenticationPrincipal(expression = "id") UUID userId) {
        var revenue = revenueService.findById(revenueId, userId);
        revenueService.delete(revenue);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{revenueId}")
    public ResponseEntity<RevenueDetailsView> getById(@PathVariable UUID revenueId,
                                                      @AuthenticationPrincipal(expression = "id") UUID userId) {
        var revenue = revenueService.findById(revenueId, userId);
        var response = new RevenueDetailsView(revenue);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{revenueId}")
    public ResponseEntity<RevenueDetailsView> update(@PathVariable UUID revenueId,
                                                     @Valid @RequestBody UpdateRevenueForm form,
                                                     @AuthenticationPrincipal(expression = "id") UUID userId) {
        var revenue = revenueService.findById(revenueId, userId);
        var updatedRevenue = revenueService.update(revenue, form);
        var response = new RevenueDetailsView(updatedRevenue);
        return ResponseEntity.ok(response);
    }

}
