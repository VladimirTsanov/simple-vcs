package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.dto.CommentRequest;
import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.exception.DocumentNotFoundException;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.exception.VersionNotFoundException;
import SAP.Project.simple_vcs.exception.AccessDeniedException;
import SAP.Project.simple_vcs.repository.CommentRepository;
import SAP.Project.simple_vcs.entity.Comment;
import SAP.Project.simple_vcs.entity.VersionStatus;
import SAP.Project.simple_vcs.repository.DocumentRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import SAP.Project.simple_vcs.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;
    private final AuditLogService auditLogService;

    public Comment postComment(CommentRequest commentRequest, Long authorId) throws UserNotFoundException,
            DocumentNotFoundException {
        Document document = documentRepository.findById(commentRequest.documentId())
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));

        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException("User with id" + authorId + " not found"));

        Version documentLastVersion = document.getVersions().getFirst();

        if (documentLastVersion.getStatus() == VersionStatus.DRAFT) {
            auditLogService.logActionWithActor(user, "UNAUTHORIZED_COMMENT_ATTEMPT", "Version",
                    documentLastVersion.getId(),
                    "Attempted to comment on DRAFT version of document " + document.getId());
            throw new AccessDeniedException("Commenting is completely disabled for DRAFT versions.");
        }

        if (documentLastVersion.getStatus() == VersionStatus.PENDING_REVIEW) {
            boolean isAllowed = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals("ROLE_ADMIN") || role.getName().equals("ROLE_REVIEWER"));
            if (!isAllowed) {
                auditLogService.logActionWithActor(user, "UNAUTHORIZED_COMMENT_ATTEMPT", "Version",
                        documentLastVersion.getId(),
                        "Non-reviewer attempted to comment on PENDING_REVIEW version of document "
                                + document.getId());
                throw new AccessDeniedException(
                        "Only Reviewers and Admins can comment on a version \"pending review\"");
            }
        }

        Comment newComment = new Comment();
        newComment.setAuthor(user);
        newComment.setVersion(documentLastVersion);
        newComment.setContent(commentRequest.content());

        Comment saved = commentRepository.save(newComment);
        auditLogService.logActionWithActor(user, "CREATE", "Comment", saved.getId(),
                "Commented on version " + documentLastVersion.getVersionNumber()
                        + " of document " + document.getId());
        return saved;
    }

    public List<Comment> getAllCommentsByDocId(Long docId) {
        return commentRepository.findAllByVersionDocumentId(docId);
    }
}
