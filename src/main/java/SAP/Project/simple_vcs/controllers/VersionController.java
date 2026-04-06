package SAP.Project.simple_vcs.controllers;

import SAP.Project.simple_vcs.dto.DiffResponse;
import SAP.Project.simple_vcs.dto.VersionResponse;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.exception.AccessDeniedException;
import SAP.Project.simple_vcs.exception.DocumentNotFoundException;
import SAP.Project.simple_vcs.exception.VersionNotFoundException;
import SAP.Project.simple_vcs.security.CustomUserDetails;
import SAP.Project.simple_vcs.services.VersionDiffService;
import SAP.Project.simple_vcs.services.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/versions")
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;
    private final VersionDiffService diffService;

    // Пълна история на документа (за вашата задача)
   /* @GetMapping("/document/{id}/history")
    public ResponseEntity<List<VersionResponse>> getHistory(@PathVariable Long id) throws DocumentNotFoundException {
        List<VersionResponse> response = versionService.getVersionsForDocument(id).stream()
                .map(v -> new VersionResponse(
                        v.getId(),
                        v.getVersionNumber(),
                        v.getContent(),
                        v.getStatus().name(),
                        v.getAuthor().getId()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }*/

    @GetMapping("/document/{id}/history")
    public ResponseEntity<List<VersionResponse>> getHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws DocumentNotFoundException, AccessDeniedException {

        // 1. Взимаме данните за потребителя
        Long userId = userDetails.getUser().getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 2. Викаме сигурния метод в сървиса
        List<Version> versions = versionService.getVersionsForDocument(id, userId, isAdmin);

        // 3. ПРЕВРЪЩАМЕ ЕНТИТИТАТА В DTO (Това оправя червения текст накрая)
        List<VersionResponse> response = versions.stream()
                .map(v -> new VersionResponse(
                        v.getId(),
                        v.getVersionNumber(),
                        v.getContent(),
                        v.getStatus().name(),
                        v.getAuthor().getId()
                ))
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Сравнение на две версии (бонус задачата)
    @GetMapping("/compare/{oldId}/{newId}")
    public ResponseEntity<List<DiffResponse>> compare(@PathVariable Long oldId, @PathVariable Long newId)
            throws VersionNotFoundException {
        return ResponseEntity.ok(diffService.compareVersions(oldId, newId));
    }
}