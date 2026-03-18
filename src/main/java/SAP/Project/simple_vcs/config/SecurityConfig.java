package SAP.Project.simple_vcs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final CustomAccessDeniedHandler accessDeniedHandler;

    private final CustomAuthenticationEntryPoint customEntryPoint;

    // Конструктор за инжектиране (IntelliJ ще ти помогне тук)
    public SecurityConfig(CustomAuthenticationEntryPoint customEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.customEntryPoint = customEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Изключваме за по-лесни тестове от конзола
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // Разрешаваме регистрацията за всички
                        .requestMatchers("/h2-console/**").permitAll() // Пускаме H2 без парола
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Задача: Рестрикция за Админ
                        .requestMatchers("/documents/**").authenticated() // Само за логнати
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customEntryPoint) // За нелогнати (401)
                        .accessDeniedHandler(accessDeniedHandler)    // За логнати без права (403)
                )
                .formLogin(Customizer.withDefaults()) // Задача: Login функционалност
                .logout(logout -> logout
                        .logoutUrl("/logout") // Това е адресът за изход
                        .logoutSuccessUrl("/login?logout") // Къде отиваме след успех
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .httpBasic(basic -> basic.authenticationEntryPoint(customEntryPoint)); // Позволява логин през конзолни инструменти

        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())); // За H2 конзолата

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Задача: Password hashing (ползва се от Колега 1)
    }
}