package SAP.Project.simple_vcs.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import SAP.Project.simple_vcs.entity.AuditLog;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.repository.AuditLogRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void logAction(String action, String entityType, Long entityId, String details) {
        String principalName = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Try finding by email first
        User actor = userRepository.findByEmail(principalName);
        
        // If not found by email, try finding by username
        if (actor == null) {
            actor = userRepository.findByUsername(principalName).orElse(null);
        }

        if (actor != null) {
            AuditLog log = new AuditLog();
            log.setUser(actor);
            log.setAction(action);
            log.setEntityType(entityType);
            log.setEntityId(Math.toIntExact(entityId)); 
            log.setDetails(details);
            
            auditLogRepository.save(log);
        } else {
            System.err.println("WARNING: Could not find user '" + principalName + "' to create Audit Log.");
        }
    }

    @Transactional
    public void logActionWithActor(User actor, String action, String entityType, Long entityId, String details) {
        AuditLog log = new AuditLog();
        log.setUser(actor);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(Math.toIntExact(entityId)); 
        log.setDetails(details);
        
        auditLogRepository.save(log);
    }
}