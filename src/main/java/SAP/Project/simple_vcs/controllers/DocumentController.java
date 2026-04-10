package SAP.Project.simple_vcs.controllers;

import SAP.Project.simple_vcs.dto.*;
import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.exception.InvalidStatusTransitionException;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.exception.VersionNotFoundException;
import SAP.Project.simple_vcs.security.CustomUserDetails;
import SAP.Project.simple_vcs.services.DocumentService;
import SAP.Project.simple_vcs.services.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    private final VersionService versionService;

    @PostMapping(value = "/new", consumes = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentResponse> createDocument(@RequestBody DocumentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws UserNotFoundException {
        Long authorId = userDetails.getUser().getId();
        Document doc = documentService.createDocument(request, authorId);
        DocumentResponse response = new DocumentResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getActiveVersion().getVersionNumber(),
                doc.getActiveVersion().getStatus().name(),
                doc.getActiveVersion().getAuthor().getUsername()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<DocumentResponse>> getAll() {
        List<DocumentResponse> response = documentService.getAllDocuments().stream()
                .map(doc -> new DocumentResponse(
                        doc.getId(),
                        doc.getTitle(),
                        doc.getActiveVersion().getVersionNumber(),
                        doc.getActiveVersion().getStatus().name(),
                        doc.getActiveVersion().getAuthor().getUsername()
                ))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(value = "/my")
    public ResponseEntity<List<DocumentResponse>> getMyDocuments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long authorId = userDetails.getUser().getId();
        List<DocumentResponse> response = documentService.getPersonalDocuments(authorId).stream()
                .map(doc -> new DocumentResponse(
                        doc.getId(),
                        doc.getTitle(),
                        doc.getActiveVersion().getVersionNumber(),
                        doc.getActiveVersion().getStatus().name(),
                        doc.getActiveVersion().getAuthor().getUsername()
                ))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{docId}/versions/{versionId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUTHOR', 'ROLE_REVIEWER')")
    public ResponseEntity<?> updateVersionStatus(@PathVariable Long docId,
            @PathVariable Long versionId,
            @RequestBody VersionStatusUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Version updated = versionService.updateVersionStatus(versionId, request.newStatus(),
                    userDetails.getUser().getId());
            VersionResponse response = new VersionResponse(
                    updated.getId(),
                    updated.getVersionNumber(),
                    updated.getContent(),
                    updated.getStatus().name(),
                    updated.getAuthor().getId()
            );
            return ResponseEntity.ok(response);
        } catch (InvalidStatusTransitionException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (VersionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
