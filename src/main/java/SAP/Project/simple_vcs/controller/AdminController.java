package SAP.Project.simple_vcs.controller;

import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.dto.UserResponseDto;
import SAP.Project.simple_vcs.entity.Role;
import SAP.Project.simple_vcs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<String> setUserStatus(@PathVariable Long userId, @RequestParam boolean active) {
        userService.setUserStatus(userId, active);
        return ResponseEntity.ok("User status updated successfully");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/users/{userId}/roles")
    public ResponseEntity<String> updateUserRoles(@PathVariable Long userId, @RequestBody Set<String> roleNames) {
        userService.updateUserRoles(userId, roleNames);
        return ResponseEntity.ok("Roles updated successfully");
    }

    @PostMapping("/roles")
    public ResponseEntity<Role> createRole(@RequestParam String name, @RequestParam String description) {
        return ResponseEntity.ok(userService.createRole(name, description));
    }
}
