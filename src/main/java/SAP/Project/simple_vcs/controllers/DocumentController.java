package SAP.Project.simple_vcs.controllers;

import SAP.Project.simple_vcs.dto.*;
import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;
    @PostMapping
    public ResponseEntity<DocumentResponse> createDocument(@RequestBody DocumentRequest request) {
        Document doc = documentService.createDocument(request);
        DocumentResponse response = new DocumentResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getActiveVersion().getVersionNumber(),
                doc.getActiveVersion().getStatus().name()
        );
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAll() {
        List<DocumentResponse> response = documentService.getAllDocuments().stream()
                .map(doc -> new DocumentResponse(
                        doc.getId(),
                        doc.getTitle(),
                        doc.getActiveVersion().getVersionNumber(),
                        doc.getActiveVersion().getStatus().name()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }
}
