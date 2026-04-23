package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.dto.CommentRequest;
import SAP.Project.simple_vcs.entity.*;
import SAP.Project.simple_vcs.exception.AccessDeniedException;
import SAP.Project.simple_vcs.exception.DocumentNotFoundException;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.repository.CommentRepository;
import SAP.Project.simple_vcs.repository.DocumentRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private UserRepository userRepository;
    @Mock private DocumentRepository documentRepository;
    @Mock private AuditLogService auditLogService;

    @InjectMocks private CommentService commentService;

    @Test
    void postComment_onDraftVersion_throwsAccessDenied() {
        Document doc = docWithLatestVersionStatus(VersionStatus.DRAFT);
        User user = userWithRole("ROLE_AUTHOR");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThrows(AccessDeniedException.class,
                () -> commentService.postComment(new CommentRequest("hi", 1L), 2L));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void postComment_onPendingReview_asReviewer_success() throws Exception {
        Document doc = docWithLatestVersionStatus(VersionStatus.PENDING_REVIEW);
        User reviewer = userWithRole("ROLE_REVIEWER");
        Comment saved = new Comment();
        saved.setId(10L);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userRepository.findById(2L)).thenReturn(Optional.of(reviewer));
        when(commentRepository.save(any())).thenReturn(saved);

        Comment result = commentService.postComment(new CommentRequest("looks good", 1L), 2L);
        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void postComment_onPendingReview_asAdmin_success() throws Exception {
        Document doc = docWithLatestVersionStatus(VersionStatus.PENDING_REVIEW);
        User admin = userWithRole("ROLE_ADMIN");
        Comment saved = new Comment();
        saved.setId(11L);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(commentRepository.save(any())).thenReturn(saved);

        Comment result = commentService.postComment(new CommentRequest("approved", 1L), 2L);
        assertThat(result.getId()).isEqualTo(11L);
    }

    @Test
    void postComment_onPendingReview_asAuthor_throwsAccessDenied() {
        Document doc = docWithLatestVersionStatus(VersionStatus.PENDING_REVIEW);
        User author = userWithRole("ROLE_AUTHOR");

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userRepository.findById(2L)).thenReturn(Optional.of(author));

        assertThrows(AccessDeniedException.class,
                () -> commentService.postComment(new CommentRequest("hi", 1L), 2L));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void postComment_onApprovedVersion_asAnyUser_success() throws Exception {
        Document doc = docWithLatestVersionStatus(VersionStatus.APPROVED);
        User author = userWithRole("ROLE_AUTHOR");
        Comment saved = new Comment();
        saved.setId(12L);

        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userRepository.findById(2L)).thenReturn(Optional.of(author));
        when(commentRepository.save(any())).thenReturn(saved);

        Comment result = commentService.postComment(new CommentRequest("nice", 1L), 2L);
        assertThat(result.getId()).isEqualTo(12L);
    }

    @Test
    void postComment_documentNotFound_throwsException() {
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(DocumentNotFoundException.class,
                () -> commentService.postComment(new CommentRequest("hi", 99L), 2L));
    }

    @Test
    void postComment_userNotFound_throwsException() {
        Document doc = docWithLatestVersionStatus(VersionStatus.APPROVED);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> commentService.postComment(new CommentRequest("hi", 1L), 99L));
    }

    @Test
    void getAllCommentsByDocId_returnsList() {
        List<Comment> comments = List.of(new Comment(), new Comment());
        when(commentRepository.findAllByVersionDocumentId(5L)).thenReturn(comments);

        List<Comment> result = commentService.getAllCommentsByDocId(5L);
        assertThat(result).hasSize(2);
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private Document docWithLatestVersionStatus(VersionStatus status) {
        Version v = Version.builder()
                .id(1L)
                .versionNumber(1)
                .status(status)
                .build();
        Document doc = Document.builder().id(1L).title("Doc").build();
        doc.getVersions().add(v);
        return doc;
    }

    private User userWithRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        User user = new User();
        user.setId(2L);
        user.setUsername("testuser");
        user.setRoles(new HashSet<>(Set.of(role)));
        return user;
    }
}
