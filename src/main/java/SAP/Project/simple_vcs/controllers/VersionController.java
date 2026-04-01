package SAP.Project.simple_vcs.controllers;

import java.util.List;

import SAP.Project.simple_vcs.exception.DocumentNotFoundException;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("/new")
    public ResponseEntity<VersionResponse> createVersion(@RequestBody VersionRequest request,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails) throws UserNotFoundException, DocumentNotFoundException {
        Long authorId = userDetails.getUser().getId();
        Version version = versionService.createNewVersion(request, authorId);
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
    public ResponseEntity<List<VersionResponse>> getDocumentVersions(@PathVariable Long documentId) throws DocumentNotFoundException {
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