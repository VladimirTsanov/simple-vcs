package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.dto.VersionRequest;
import SAP.Project.simple_vcs.entity.*;
import SAP.Project.simple_vcs.exception.*;
import SAP.Project.simple_vcs.repository.DocumentRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import SAP.Project.simple_vcs.repository.VersionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VersionServiceTest {

    @Mock private VersionRepository versionRepository;
    @Mock private DocumentRepository documentRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuditLogService auditLogService;

    @InjectMocks private VersionService versionService;

    // -------------------------------------------------------------------------
    // createNewVersion
    // -------------------------------------------------------------------------

    @Test
    void createNewVersion_success() throws Exception {
        Document doc = Document.builder().id(1L).title("Doc").build();
        doc.getVersions().add(Version.builder().versionNumber(1).build()); // existing version

        User author = new User();
        author.setId(2L);

        VersionRequest request = new VersionRequest(1L, "content");
        Version saved = Version.builder().id(10L).versionNumber(2).status(VersionStatus.DRAFT).build();

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userRepository.findById(2L)).thenReturn(Optional.of(author));
        when(versionRepository.save(any())).thenReturn(saved);

        Version result = versionService.createNewVersion(request, 2L);

        assertThat(result.getVersionNumber()).isEqualTo(2);
        assertThat(result.getStatus()).isEqualTo(VersionStatus.DRAFT);
        verify(auditLogService).logActionWithActor(eq(author), eq("CREATE"), eq("Version"), eq(10L), any());
    }

    @Test
    void createNewVersion_documentNotFound() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(DocumentNotFoundException.class,
                () -> versionService.createNewVersion(new VersionRequest(99L, "c"), 1L));
    }

    @Test
    void createNewVersion_userNotFound() {
        Document doc = Document.builder().id(1L).title("Doc").build();
        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class,
                () -> versionService.createNewVersion(new VersionRequest(1L, "c"), 99L));
    }

    // -------------------------------------------------------------------------
    // getVersionsForDocument (simple overload)
    // -------------------------------------------------------------------------

    @Test
    void getVersionsForDocument_success() throws Exception {
        Document doc = Document.builder().id(1L).title("Doc").build();
        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));

        List<Version> result = versionService.getVersionsForDocument(1L);
        assertThat(result).isSameAs(doc.getVersions());
    }

    @Test
    void getVersionsForDocument_documentNotFound() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(DocumentNotFoundException.class,
                () -> versionService.getVersionsForDocument(99L));
    }

    // -------------------------------------------------------------------------
    // getVersionById
    // -------------------------------------------------------------------------

    @Test
    void getVersionById_success() throws Exception {
        Version v = Version.builder().id(5L).build();
        when(versionRepository.findById(5L)).thenReturn(Optional.of(v));
        assertThat(versionService.getVersionById(5L)).isSameAs(v);
    }

    @Test
    void getVersionById_notFound() {
        when(versionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(VersionNotFoundException.class, () -> versionService.getVersionById(99L));
    }

    // -------------------------------------------------------------------------
    // updateVersionStatus — DRAFT transitions
    // -------------------------------------------------------------------------

    @Test
    void updateVersionStatus_draftToPending_asAuthor_allowed() throws Exception {
        User author = userWithRole("ROLE_AUTHOR");
        Version v = versionWithStatus(VersionStatus.DRAFT);
        mockForStatusUpdate(v, author);
        when(versionRepository.save(v)).thenReturn(v);

        Version result = versionService.updateVersionStatus(1L, VersionStatus.PENDING_REVIEW, 2L);
        assertThat(result.getStatus()).isEqualTo(VersionStatus.PENDING_REVIEW);
    }

    @Test
    void updateVersionStatus_draftToPending_asReviewer_allowed() throws Exception {
        User reviewer = userWithRole("ROLE_REVIEWER");
        Version v = versionWithStatus(VersionStatus.DRAFT);
        mockForStatusUpdate(v, reviewer);
        when(versionRepository.save(v)).thenReturn(v);

        versionService.updateVersionStatus(1L, VersionStatus.PENDING_REVIEW, 2L);
        assertThat(v.getStatus()).isEqualTo(VersionStatus.PENDING_REVIEW);
    }

    @Test
    void updateVersionStatus_draftToPending_asAdmin_allowed() throws Exception {
        User admin = userWithRole("ROLE_ADMIN");
        Version v = versionWithStatus(VersionStatus.DRAFT);
        mockForStatusUpdate(v, admin);
        when(versionRepository.save(v)).thenReturn(v);

        versionService.updateVersionStatus(1L, VersionStatus.PENDING_REVIEW, 2L);
        assertThat(v.getStatus()).isEqualTo(VersionStatus.PENDING_REVIEW);
    }

    @Test
    void updateVersionStatus_draftToApproved_asAuthor_throwsException() {
        User author = userWithRole("ROLE_AUTHOR");
        Version v = versionWithStatus(VersionStatus.DRAFT);
        mockForStatusUpdate(v, author);

        assertThrows(InvalidStatusTransitionException.class,
                () -> versionService.updateVersionStatus(1L, VersionStatus.APPROVED, 2L));
    }

    // -------------------------------------------------------------------------
    // updateVersionStatus — PENDING_REVIEW transitions
    // -------------------------------------------------------------------------

    @Test
    void updateVersionStatus_pendingToApproved_asReviewer_setsReviewerAndActiveVersion() throws Exception {
        User reviewer = userWithRole("ROLE_REVIEWER");
        Document doc = Document.builder().id(10L).title("Doc").build();
        Version v = Version.builder().id(1L).status(VersionStatus.PENDING_REVIEW).document(doc).build();

        when(versionRepository.findById(1L)).thenReturn(Optional.of(v));
        when(userRepository.findById(2L)).thenReturn(Optional.of(reviewer));
        when(versionRepository.save(v)).thenReturn(v);
        when(documentRepository.save(doc)).thenReturn(doc);

        versionService.updateVersionStatus(1L, VersionStatus.APPROVED, 2L);

        assertThat(v.getStatus()).isEqualTo(VersionStatus.APPROVED);
        assertThat(v.getReviewer()).isEqualTo(reviewer);
        assertThat(doc.getActiveVersion()).isEqualTo(v);
    }

    @Test
    void updateVersionStatus_pendingToApproved_asAdmin_setsReviewerAndActiveVersion() throws Exception {
        User admin = userWithRole("ROLE_ADMIN");
        Document doc = Document.builder().id(10L).title("Doc").build();
        Version v = Version.builder().id(1L).status(VersionStatus.PENDING_REVIEW).document(doc).build();

        when(versionRepository.findById(1L)).thenReturn(Optional.of(v));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(versionRepository.save(v)).thenReturn(v);
        when(documentRepository.save(doc)).thenReturn(doc);

        versionService.updateVersionStatus(1L, VersionStatus.APPROVED, 2L);

        assertThat(v.getReviewer()).isEqualTo(admin);
        assertThat(doc.getActiveVersion()).isEqualTo(v);
    }

    @Test
    void updateVersionStatus_pendingToRejected_asReviewer_setsReviewer() throws Exception {
        User reviewer = userWithRole("ROLE_REVIEWER");
        Version v = versionWithStatus(VersionStatus.PENDING_REVIEW);
        mockForStatusUpdate(v, reviewer);
        when(versionRepository.save(v)).thenReturn(v);

        versionService.updateVersionStatus(1L, VersionStatus.REJECTED, 2L);

        assertThat(v.getStatus()).isEqualTo(VersionStatus.REJECTED);
        assertThat(v.getReviewer()).isEqualTo(reviewer);
    }

    @Test
    void updateVersionStatus_pendingToRejected_asAuthor_throwsException() {
        User author = userWithRole("ROLE_AUTHOR");
        Version v = versionWithStatus(VersionStatus.PENDING_REVIEW);
        mockForStatusUpdate(v, author);

        assertThrows(InvalidStatusTransitionException.class,
                () -> versionService.updateVersionStatus(1L, VersionStatus.REJECTED, 2L));
    }

    // -------------------------------------------------------------------------
    // updateVersionStatus — REJECTED transitions
    // -------------------------------------------------------------------------

    @Test
    void updateVersionStatus_rejectedToDraft_asReviewer_allowed() throws Exception {
        User reviewer = userWithRole("ROLE_REVIEWER");
        Version v = versionWithStatus(VersionStatus.REJECTED);
        mockForStatusUpdate(v, reviewer);
        when(versionRepository.save(v)).thenReturn(v);

        versionService.updateVersionStatus(1L, VersionStatus.DRAFT, 2L);
        assertThat(v.getStatus()).isEqualTo(VersionStatus.DRAFT);
    }

    @Test
    void updateVersionStatus_rejectedToDraft_asAuthor_throwsException() {
        User author = userWithRole("ROLE_AUTHOR");
        Version v = versionWithStatus(VersionStatus.REJECTED);
        mockForStatusUpdate(v, author);

        assertThrows(InvalidStatusTransitionException.class,
                () -> versionService.updateVersionStatus(1L, VersionStatus.DRAFT, 2L));
    }

    // -------------------------------------------------------------------------
    // updateVersionStatus — APPROVED is terminal
    // -------------------------------------------------------------------------

    @Test
    void updateVersionStatus_approvedToAny_throwsException() {
        User admin = userWithRole("ROLE_ADMIN");
        Version v = versionWithStatus(VersionStatus.APPROVED);
        mockForStatusUpdate(v, admin);

        assertThrows(InvalidStatusTransitionException.class,
                () -> versionService.updateVersionStatus(1L, VersionStatus.DRAFT, 2L));
    }

    // -------------------------------------------------------------------------
    // getVersionsForDocument(id, userId, isAdmin)
    // -------------------------------------------------------------------------

    @Test
    void getVersionsForDocument_withAccess_asAdmin_alwaysAllowed() throws Exception {
        Document doc = Document.builder().id(1L).title("Doc").build();
        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));

        List<Version> result = versionService.getVersionsForDocument(1L, 99L, true);
        assertThat(result).isSameAs(doc.getVersions());
    }

    @Test
    void getVersionsForDocument_withAccess_asOwner_allowed() throws Exception {
        User owner = new User();
        owner.setId(5L);
        Version v = Version.builder().author(owner).build();
        Document doc = Document.builder().id(1L).title("Doc").build();
        doc.getVersions().add(v);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));

        List<Version> result = versionService.getVersionsForDocument(1L, 5L, false);
        assertThat(result).isSameAs(doc.getVersions());
    }

    @Test
    void getVersionsForDocument_withAccess_unauthorized_throwsAccessDenied() {
        User otherUser = new User();
        otherUser.setId(7L);
        Version v = Version.builder().author(otherUser).build();
        Document doc = Document.builder().id(1L).title("Doc").build();
        doc.getVersions().add(v);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));

        assertThrows(AccessDeniedException.class,
                () -> versionService.getVersionsForDocument(1L, 99L, false));
        verify(auditLogService).logAction(eq("UNAUTHORIZED_HISTORY_ACCESS"), eq("Document"), eq(1L), any());
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private User userWithRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        User user = new User();
        user.setId(2L);
        user.setUsername("testuser");
        user.setRoles(new HashSet<>(Set.of(role)));
        return user;
    }

    private Version versionWithStatus(VersionStatus status) {
        Document doc = Document.builder().id(10L).title("Doc").build();
        return Version.builder().id(1L).status(status).document(doc).build();
    }

    private void mockForStatusUpdate(Version v, User user) {
        when(versionRepository.findById(1L)).thenReturn(Optional.of(v));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
    }
}
