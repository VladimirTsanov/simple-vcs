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
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        
        
        User actor = userRepository.findByEmail(currentUserEmail);

       
        if (actor != null) {
            AuditLog log = new AuditLog();
            log.setUser(actor);
            log.setAction(action);
            log.setEntityType(entityType);
            
            
            log.setEntityId(Math.toIntExact(entityId)); 
            
            log.setDetails(details);
            
            auditLogRepository.save(log);
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