package SAP.Project.simple_vcs.controllers;
import SAP.Project.simple_vcs.dto.CommentRequest;
import SAP.Project.simple_vcs.dto.CommentResponse;
import SAP.Project.simple_vcs.entity.Comment;
import SAP.Project.simple_vcs.exception.DocumentNotFoundException;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.security.CustomUserDetails;
import SAP.Project.simple_vcs.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents/{docId}/versions/{versionId}/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping(value = "/new")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUTHOR', 'ROLE_REVIEWER')")
    public ResponseEntity<CommentResponse> newComment(@RequestBody CommentRequest commentRequest,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails) throws
            UserNotFoundException, DocumentNotFoundException {

        Long authorId = userDetails.getUser().getId();

        Comment newComment = commentService.postComment(commentRequest, authorId);

        CommentResponse commentResponse = new CommentResponse(
            newComment.getVersion().getVersionNumber(),
            newComment.getContent(),
            newComment.getVersion().getStatus().toString(),
            newComment.getAuthor().getUsername(),
            newComment.getAuthor().getRoles().stream().map(role -> role.getName()).toList(),
            newComment.getCreatedAt()
        );
        return ResponseEntity.ok(commentResponse);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<CommentResponse>> getAllVersionComments(@PathVariable Long docId){
        List<Comment> comments = commentService.getAllCommentsByDocId(docId);
        List<CommentResponse> response = new ArrayList<>();
        for (Comment comment : comments) {
            response.add(new CommentResponse(
                    comment.getVersion().getVersionNumber(),
                    comment.getContent(),
                    comment.getVersion().getStatus().toString(),
                    comment.getAuthor().getUsername(),
                    comment.getAuthor().getRoles().stream().map(role -> role.getName()).toList(),
                    comment.getCreatedAt()
            ));
        }
        return ResponseEntity.ok(response);
    }
}
