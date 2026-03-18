package SAP.Project.simple_vcs.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @GetMapping("/view")
    public String view() {
        return "Всеки логнат потребител вижда това.";
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Само Админ може да трие
    public String delete(@PathVariable Long id) {
        return "Документът е изтрит успешно от Админ.";
    }
}