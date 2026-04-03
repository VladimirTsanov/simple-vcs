package SAP.Project.simple_vcs.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class WebController {
    @GetMapping("/")
    public String home() {
        return "index"; // This looks for src/main/resources/templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // This looks for src/main/resources/templates/login.html
    }

    @GetMapping("/file_template")
    public String file_template() {
        return "file_template"; // This looks for src/main/resources/templates/login.html
    }
}
