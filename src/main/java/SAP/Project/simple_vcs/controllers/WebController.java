package SAP.Project.simple_vcs.controllers;

import SAP.Project.simple_vcs.dto.UserRegistrationDto;
import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.entity.VersionStatus;
import SAP.Project.simple_vcs.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final DocumentService documentService;

    @GetMapping("/")
    public String home(Model model, @org.springframework.security.core.annotation.AuthenticationPrincipal SAP.Project.simple_vcs.security.CustomUserDetails userDetails) {
        if (userDetails != null) {
            Long userId = userDetails.getUser().getId();
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isReviewer = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_REVIEWER"));
            if (isAdmin || isReviewer) {
                model.addAttribute("allDocuments", documentService.getAllDocuments());
            } else {
                boolean isAuthor = userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_AUTHOR"));
                boolean isReaderOnly = !isAuthor && userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_READER"));

                java.util.List<Document> personal =
                        new java.util.ArrayList<>(documentService.getPersonalDocuments(userId));
                java.util.List<Document> shared =
                        documentService.getDocumentsSharedWithUser(userId).stream()
                                .filter(d -> personal.stream().noneMatch(p -> p.getId().equals(d.getId())))
                                .collect(java.util.stream.Collectors.toList());

                if (isReaderOnly) {
                    model.addAttribute("myDocuments", personal.stream()
                            .filter(WebController::hasApprovedActive)
                            .collect(java.util.stream.Collectors.toList()));
                    model.addAttribute("sharedDocuments", shared.stream()
                            .filter(WebController::hasApprovedActive)
                            .collect(java.util.stream.Collectors.toList()));
                } else {
                    model.addAttribute("myDocuments", personal);
                    model.addAttribute("sharedDocuments", shared);
                }
            }
        }
        return "index";
    }

    private static boolean hasApprovedActive(Document d) {
        return d.getActiveVersion() != null
                && d.getActiveVersion().getStatus() == VersionStatus.APPROVED;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "login_register";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "login_register";
    }

    @GetMapping("/file-info")
    public String file_template() {
        return "file_template";
    }


    @GetMapping("/new-document")
    public String register() {
        return "document_creation";
    }



}
