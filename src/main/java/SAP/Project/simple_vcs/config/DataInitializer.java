package SAP.Project.simple_vcs.config;

import SAP.Project.simple_vcs.entity.Role;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.repository.RoleRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ADMIN");
            return roleRepository.save(role);
        });

        Role authorRole = roleRepository.findByName("AUTHOR").orElseGet(() -> {
            Role role = new Role();
            role.setName("AUTHOR");
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


        }

        if (userRepository.findByUsername("user1").isEmpty()) {
            User firstuser = new User();
            firstuser.setUsername("user1");
            firstuser.setEmail("user1@example.com");
            firstuser.setPasswordHash(passwordEncoder.encode("user123"));
            firstuser.setRoles(Set.of(authorRole));
            firstuser.setActive(true);
            userRepository.save(firstuser);


        }
    }
}
