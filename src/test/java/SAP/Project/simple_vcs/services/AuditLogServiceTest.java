package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.entity.AuditLog;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.repository.AuditLogRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock private AuditLogRepository auditLogRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private AuditLogService auditLogService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // -------------------------------------------------------------------------
    // logAction
    // -------------------------------------------------------------------------

    @Test
    void logAction_userFoundByEmail_savesLog() {
        User actor = new User();
        actor.setId(1L);

        mockSecurityContext("actor@example.com");
        when(userRepository.findByEmail("actor@example.com")).thenReturn(actor);

        auditLogService.logAction("CREATE", "Document", 5L, "details");

        verify(auditLogRepository).save(argThat(log ->
                log.getUser() == actor &&
                "CREATE".equals(log.getAction()) &&
                "Document".equals(log.getEntityType()) &&
                log.getEntityId() == 5
        ));
    }

    @Test
    void logAction_emailLookupFails_fallsBackToUsername_savesLog() {
        User actor = new User();
        actor.setId(2L);

        mockSecurityContext("someuser");
        when(userRepository.findByEmail("someuser")).thenReturn(null);
        when(userRepository.findByUsername("someuser")).thenReturn(Optional.of(actor));

        auditLogService.logAction("DELETE", "Version", 3L, "details");

        verify(auditLogRepository).save(argThat(log -> log.getUser() == actor));
    }

    @Test
    void logAction_userNotFoundByEitherLookup_doesNotSave() {
        mockSecurityContext("ghost");
        when(userRepository.findByEmail("ghost")).thenReturn(null);
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        auditLogService.logAction("UPDATE", "Document", 1L, "details");

        verify(auditLogRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // logActionWithActor
    // -------------------------------------------------------------------------

    @Test
    void logActionWithActor_alwaysSavesLog() {
        User actor = new User();
        actor.setId(1L);

        auditLogService.logActionWithActor(actor, "REGISTER", "User", 1L, "self-registered");

        verify(auditLogRepository).save(argThat(log ->
                log.getUser() == actor &&
                "REGISTER".equals(log.getAction()) &&
                "User".equals(log.getEntityType()) &&
                log.getEntityId() == 1
        ));
    }

    // -------------------------------------------------------------------------
    // getAllAuditLogs
    // -------------------------------------------------------------------------

    @Test
    void getAllAuditLogs_returnsSortedDescendingById() {
        AuditLog logA = new AuditLog();
        logA.setId(1L);
        AuditLog logB = new AuditLog();
        logB.setId(3L);
        AuditLog logC = new AuditLog();
        logC.setId(2L);

        when(auditLogRepository.findAll()).thenReturn(new java.util.ArrayList<>(List.of(logA, logB, logC)));

        List<AuditLog> result = auditLogService.getAllAuditLogs();

        assertThat(result).extracting(AuditLog::getId)
                .containsExactly(3L, 2L, 1L);
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private void mockSecurityContext(String principalName) {
        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        when(auth.getName()).thenReturn(principalName);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
