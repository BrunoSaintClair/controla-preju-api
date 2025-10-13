package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateUserForm;
import api.controla_preju.dtos.views.CreatedUserView;
import api.controla_preju.dtos.views.UserDetailsView;
import api.controla_preju.entities.User;
import api.controla_preju.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<CreatedUserView> create(@Valid @RequestBody CreateUserForm form){
        var newUser = userService.create(form);
        var response = new CreatedUserView(newUser);
        URI location = URI.create("/users/" + newUser.getId());
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/deactivate")
    public ResponseEntity<Void> deactivate(@AuthenticationPrincipal User user) {
        userService.deactivate(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reactivate")
    public ResponseEntity<Void> reactivate(@AuthenticationPrincipal User user) {
        userService.reactivate(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<UserDetailsView> getById(@AuthenticationPrincipal(expression = "id") UUID id){
        var user = userService.findById(id);
        var response = new UserDetailsView(user);
        return ResponseEntity.ok(response);
    }

}
