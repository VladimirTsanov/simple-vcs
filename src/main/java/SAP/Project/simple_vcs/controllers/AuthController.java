package SAP.Project.simple_vcs.controllers;

import SAP.Project.simple_vcs.dto.UserRegistrationDto;
import SAP.Project.simple_vcs.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping(value = "/register", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {

        userService.registerUser(registrationDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration Successful");
        response.put("redirectUrl", "/login.html?registered=true");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}