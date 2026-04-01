package SAP.Project.simple_vcs.config;

import SAP.Project.simple_vcs.entity.Role;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.repository.RoleRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import SAP.Project.simple_vcs.repository.DocumentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import SAP.Project.simple_vcs.entity.VersionStatus;

import java.util.ArrayList;
import java.util.Set;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.documentRepository = documentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_ADMIN");
            return roleRepository.save(role);
        });

        Role authorRole = roleRepository.findByName("ROLE_AUTHOR").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_AUTHOR");
            return roleRepository.save(role);
        });

        Role reviewerRole = roleRepository.findByName("ROLE_REVIEWER").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_REVIEWER");
            return roleRepository.save(role);
        });

        Role readerRole = roleRepository.findByName("ROLE_READER").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_READER");
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

        if (documentRepository.findByTitle("First Draft Document").isEmpty()) {
            User docAuthor = userRepository.findByUsername("user1").orElseThrow();

            Document doc1 = Document.builder()
                    .title("First Draft Document")
                    .build();

            Version version1 = Version.builder()
                    .document(doc1)
                    .versionNumber(1)
                    .content("Hello World!")
                    .status(VersionStatus.DRAFT)
                    .author(docAuthor)
                    .build();
            List<Version> versions = new ArrayList<>();
            versions.add(version1);
            doc1.setVersions(versions);
            doc1.setActiveVersion(version1);

            documentRepository.save(doc1);
        }

        if (documentRepository.findByTitle("Project Proposal").isEmpty()) {
            User docAuthor = userRepository.findByUsername("user1").orElseThrow();
            User docReviewer = userRepository.findByUsername("admin").orElseThrow();

            Document doc2 = Document.builder()
                    .title("Project Proposal")
                    .build();

            Version version2 = Version.builder()
                    .document(doc2)
                    .versionNumber(1)
                    .content("Hello World, again!")
                    .status(VersionStatus.PENDING_REVIEW)
                    .author(docAuthor)
                    .reviewer(docReviewer)
                    .build();

            List<Version> versions = new ArrayList<>();
            versions.add(version2);
            doc2.setVersions(versions);
            doc2.setActiveVersion(version2);

            documentRepository.save(doc2);
        }
    }
}
