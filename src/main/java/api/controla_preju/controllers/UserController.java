package api.controla_preju.controllers;

import api.controla_preju.dtos.forms.CreateUserForm;
import api.controla_preju.dtos.views.CreatedUserView;
import api.controla_preju.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

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

}
