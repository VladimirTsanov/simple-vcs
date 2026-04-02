package SAP.Project.simple_vcs.controllers;

import SAP.Project.simple_vcs.dto.*;
import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.security.CustomUserDetails;
import SAP.Project.simple_vcs.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping(value = "/new")
    public ResponseEntity<DocumentResponse> createDocument(@RequestBody DocumentRequest request, @AuthenticationPrincipal CustomUserDetails userDetails ) throws UserNotFoundException {
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
}
