package SAP.Project.simple_vcs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/documents").setViewName("forward:/documents.html");
        registry.addViewController("/my-documents").setViewName("forward:/my_documents.html");        registry.addViewController("/login").setViewName("forward:/login.html");
        registry.addViewController("/register").setViewName("forward:/register.html");
        registry.addViewController("/admin").setViewName("forward:/admin.html");
    }
}