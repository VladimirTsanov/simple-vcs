package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.entity.Role;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.repository.UserRepository;
import SAP.Project.simple_vcs.repository.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,  RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public void registerUser(User user){
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        Role authorRole = roleRepository.findByName("ROLE_AUTHOR")
                        .orElseThrow(() -> new RuntimeException("Error: role AUTHOR is not found"));

        Set<Role> roles = user.getRoles();
        roles.add(authorRole);
        user.setRoles(roles);

        userRepository.save(user);
    }
}
