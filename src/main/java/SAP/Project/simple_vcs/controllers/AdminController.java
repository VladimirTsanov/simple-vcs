package SAP.Project.simple_vcs.controllers;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import SAP.Project.simple_vcs.entity.Role;
import SAP.Project.simple_vcs.services.AuditLogService; // <-- Added Import
import SAP.Project.simple_vcs.services.DocumentService;
import SAP.Project.simple_vcs.services.UserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminController {
    
    private final UserService userService;
    private final DocumentService documentService;
    private final AuditLogService auditLogService; // <-- Injected AuditLogService

    @GetMapping("/admin/users")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", userService.getAllRoles());
        model.addAttribute("documents", documentService.getAllDocuments());
        return "admin_dashboard";
    }

    @PostMapping("/admin/users/{userId}/status")
    public String setUserStatus(@PathVariable Long userId, @RequestParam boolean active) {
        userService.setUserStatus(userId, active);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{userId}/roles")
    public String updateUserRoles(@PathVariable Long userId, @RequestParam Set<String> roleNames) {
        userService.updateUserRoles(userId, roleNames);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/roles")
    public ResponseEntity<Role> createRole(@RequestParam String name, @RequestParam String description) {
        return ResponseEntity.ok(userService.createRole(name, description));
    }
    
    @DeleteMapping("/admin/roles/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Long roleId) {
        userService.deleteRole(roleId);
        return ResponseEntity.ok("Role deleted successfully");
    }

    
    @GetMapping("/admin/audit-logs")
    public String viewAuditLogs(Model model) {
        model.addAttribute("logs", auditLogService.getAllAuditLogs());
        return "audit_logs"; 
    }
}