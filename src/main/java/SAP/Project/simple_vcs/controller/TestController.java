package SAP.Project.simple_vcs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Сървърът работи успешно! Вече имаме и първия контролер.";
    }
}