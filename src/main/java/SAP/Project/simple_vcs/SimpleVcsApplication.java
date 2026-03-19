package SAP.Project.simple_vcs;


import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.entity.Role;
import SAP.Project.simple_vcs.repository.RoleRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class SimpleVcsApplication {

	public static void main(String[] args) {

        SpringApplication.run(SimpleVcsApplication.class, args);


	}

    @Bean
    CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setName("ADMIN");
                return roleRepository.save(role);
            });

            Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
                Role role = new Role();
                role.setName("USER");
                return roleRepository.save(role);
            });

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setRoles(Set.of(adminRole));
                admin.setActive(true);
                userRepository.save(admin);

                System.out.println("Admin user created: admin / admin123");
            }

            if (userRepository.findByUsername("user1").isEmpty()) {
                User firstuser = new User();
                firstuser.setUsername("user1");
                firstuser.setEmail("user1@example.com");
                firstuser.setPasswordHash(passwordEncoder.encode("user123"));
                firstuser.setRoles(Set.of(userRole));
                firstuser.setActive(true);
                userRepository.save(firstuser);

                System.out.println("First regular user created: user / user 123");
            }
        };
    }


}
