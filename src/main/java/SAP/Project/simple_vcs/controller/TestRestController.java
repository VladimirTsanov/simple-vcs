package SAP.Project.simple_vcs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestRestController {

    @GetMapping("/public")
    public ResponseEntity<?> getPublic() {
        return ResponseEntity.ok(Map.of("message", "This is public content"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> userEndpoint() {
        return ResponseEntity.ok(Map.of("message", "Hello user!"));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminEndpoint() {
        return ResponseEntity.ok(Map.of("message", "Hello, almighty admin!"));
    }
}
