package SAP.Project.simple_vcs.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import SAP.Project.simple_vcs.entity.AuditLog;
import SAP.Project.simple_vcs.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll(); 
    }
}