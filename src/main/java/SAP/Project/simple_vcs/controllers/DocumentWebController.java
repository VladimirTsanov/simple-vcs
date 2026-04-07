package SAP.Project.simple_vcs.controllers;

import SAP.Project.simple_vcs.dto.DocumentRequest;
import SAP.Project.simple_vcs.dto.VersionRequest;
import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.exception.DocumentNotFoundException;
import SAP.Project.simple_vcs.exception.UserNotFoundException;
import SAP.Project.simple_vcs.security.CustomUserDetails;
import SAP.Project.simple_vcs.services.DocumentService;
import SAP.Project.simple_vcs.services.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;

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

    @PostMapping("/document/{id}/delete")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUTHOR')")
    public String deleteDocument(@PathVariable Long id) throws DocumentNotFoundException {
        documentService.deleteDocument(id);
        return "redirect:/";
    }
}