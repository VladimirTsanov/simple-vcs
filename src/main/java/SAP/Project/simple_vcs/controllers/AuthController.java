package SAP.Project.simple_vcs.controllers;

import SAP.Project.simple_vcs.dto.UserRegistrationDto;
import SAP.Project.simple_vcs.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        // No try-catch needed!
        // If this fails, the GlobalExceptionHandler catches it automatically.
        userService.registerUser(registrationDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(URI.create("/login.html?registered=true"))
                .build();
    }
}