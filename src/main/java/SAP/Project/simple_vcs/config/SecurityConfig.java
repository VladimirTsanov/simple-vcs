package SAP.Project.simple_vcs.config;

import SAP.Project.simple_vcs.security.CustomAccessDeniedHandler;
import SAP.Project.simple_vcs.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/", "/index.html", "/login", "/login.html",
                                "/register", "/register.html").permitAll()

                        .requestMatchers("/css/**", "/js/**", "/images/**", "/app.js").permitAll()

                        .requestMatchers("/documents", "/documents.html").permitAll()

                        .requestMatchers("/admin", "/admin.html", "/api/admin/**").hasRole("ADMIN")

                        .requestMatchers("/my-documents", "/my_documents.html", "/api/user/**").hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                )

                // 3. Exception Handling (Connects to your custom handlers)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // 4. Session Management
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 5. Form Login (Using the paths from Code 1)
                .formLogin(form -> form
                        .loginPage("/login.html")
                        .loginProcessingUrl("/api/public/login")
                        .defaultSuccessUrl("/my-documents", true)
                        .failureUrl("/login.html?error=true")
                        .permitAll()
                )

                // 6. Logout (Cleaned up version)
                .logout(logout -> logout
                        .logoutUrl("/api/public/logout")
                        .logoutSuccessUrl("/login.html?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // Enable Frames for H2 Console
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}