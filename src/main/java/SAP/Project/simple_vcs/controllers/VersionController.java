package SAP.Project.simple_vcs.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import SAP.Project.simple_vcs.dto.VersionRequest;
import SAP.Project.simple_vcs.dto.VersionResponse;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.services.VersionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/versions")
@RequiredArgsConstructor
public class VersionController {
    
    private final VersionService versionService;

    @PostMapping
    public ResponseEntity<VersionResponse> createVersion(@RequestBody VersionRequest request) {
        Version version = versionService.createNewVersion(request);
        VersionResponse response = new VersionResponse(
                version.getId(),
                version.getVersionNumber(),
                version.getContent(),
                version.getStatus().name(),
                version.getAuthor().getId()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<VersionResponse>> getDocumentVersions(@PathVariable Long documentId) {
        List<VersionResponse> response = versionService.getVersionsForDocument(documentId).stream()
                .map(v -> new VersionResponse(
                        v.getId(),
                        v.getVersionNumber(),
                        v.getContent(),
                        v.getStatus().name(),
                        v.getAuthor().getId()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }
}