package SAP.Project.simple_vcs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index");
        registry.addViewController("/documents").setViewName("documents");
        registry.addViewController("/my-documents").setViewName("my_documents");
        registry.addViewController("/login").setViewName("login_register");
        registry.addViewController("/new-document").setViewName("new_document");
        registry.addViewController("/register").setViewName("login_register");
        registry.addViewController("/admin").setViewName("admin");

        registry.addViewController("/file-info").setViewName("file_template");
    }
}