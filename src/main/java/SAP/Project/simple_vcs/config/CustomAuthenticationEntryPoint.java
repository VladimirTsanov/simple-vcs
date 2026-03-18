package SAP.Project.simple_vcs.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

       // response.addHeader("WWW-Authenticate", "Basic realm=\"SimpleVCS\"");//САМО ЗА ТЕСТВАНЕ<НЕ ПРИ РЕАЛНА ИМПЛЕМЕНТАЦИЯ>(Браузъра пази инфото и автоматично ще го попълни, ние НЕ искаме това)

        response.getWriter().write("{\"error\": \"Невалидно потребителско име или парола!\"}");
    }
}