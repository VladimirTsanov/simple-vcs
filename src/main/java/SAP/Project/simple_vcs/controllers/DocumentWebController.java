package SAP.Project.simple_vcs.controllers;

import SAP.Project.simple_vcs.dto.DocumentRequest;
import SAP.Project.simple_vcs.dto.VersionRequest;
import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.entity.VersionStatus;
import SAP.Project.simple_vcs.exception.DocumentNotFoundException;
import SAP.Project.simple_vcs.exception.InvalidStatusTransitionException;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.exception.VersionNotFoundException;
import SAP.Project.simple_vcs.security.CustomUserDetails;
import SAP.Project.simple_vcs.services.DocumentService;
import SAP.Project.simple_vcs.services.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DocumentWebController {

    private final DocumentService documentService;
    private final VersionService versionService;

    @PostMapping("/document/new")
    public String createDocument(@RequestParam String title,
            @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws UserNotFoundException {
        Long authorId = userDetails.getUser().getId();
        DocumentRequest request = new DocumentRequest(title, content);
        documentService.createDocument(request, authorId);
        return "redirect:/";
    }

    @GetMapping("/document/{id}")
    public String viewDocument(@PathVariable Long id, Model model) throws DocumentNotFoundException {
        Document document = documentService.getDocumentById(id);
        model.addAttribute("document", document);
        return "file_template";
    }

    @PostMapping("/document/{id}/version/new")
    public String createVersion(@PathVariable Long id,
            @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails userDetails)
            throws UserNotFoundException, DocumentNotFoundException {
        Long authorId = userDetails.getUser().getId();
        VersionRequest request = new VersionRequest(id, content);
        versionService.createNewVersion(request, authorId);
        return "redirect:/document/" + id;
    }

    @PostMapping("/document/{docId}/version/{versionId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUTHOR', 'ROLE_REVIEWER')")
    public String updateVersionStatus(@PathVariable Long docId,
            @PathVariable Long versionId,
            @RequestParam VersionStatus newStatus,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            versionService.updateVersionStatus(versionId, newStatus, userDetails.getUser().getId());
        } catch (InvalidStatusTransitionException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (VersionNotFoundException | UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/document/" + docId;
    }

    @PostMapping("/document/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUTHOR')")
    public String deleteDocument(@PathVariable Long id) throws DocumentNotFoundException {
        documentService.deleteDocument(id);
        return "redirect:/";
    }
}