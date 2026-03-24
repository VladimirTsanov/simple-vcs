package SAP.Project.simple_vcs.controllers;

import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.payload.RegistrationRequest;
import SAP.Project.simple_vcs.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class AuthController{
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", consumes = {
            org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            org.springframework.http.MediaType.APPLICATION_JSON_VALUE
    })
    public ResponseEntity<?> registerUser(@RequestBody RegistrationRequest request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword());
        user.setEmail(request.getEmail());

        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(java.net.URI.create("/login.html?registered=true"))
                .build();
    }
}
