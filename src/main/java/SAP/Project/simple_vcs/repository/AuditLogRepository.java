package SAP.Project.simple_vcs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import SAP.Project.simple_vcs.entity.AuditLog;
import SAP.Project.simple_vcs.entity.User;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserOrderByCreatedAtDesc(User user);
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<AuditLog> findByAction(String action);
}
