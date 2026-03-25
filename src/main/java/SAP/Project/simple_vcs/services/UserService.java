package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.dto.UserRegistrationDto;
import SAP.Project.simple_vcs.dto.UserResponseDto;
import SAP.Project.simple_vcs.entity.Role;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.repository.RoleRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional // Ensures database integrity for all methods
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already taken.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setActive(true);

        // Default Role Assignment: Using ROLE_USER (standard)
        // Swap to ROLE_AUTHOR if your project requirements specifically demand it.
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default Role not found."));
        user.setRoles(new HashSet<>(Collections.singletonList(defaultRole)));

        userRepository.save(user);
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public void setUserStatus(Long userId, boolean isActive) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Security Check: Prevents an Admin from locking themselves out
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        if (targetUser.getUsername().equals(currentUsername) && !isActive) {
            throw new RuntimeException("Security Risk: You cannot deactivate your own admin account.");
        }

        targetUser.setActive(isActive);
        userRepository.save(targetUser);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    public void updateUserRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> newRoles = roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new RuntimeException("Role " + name + " not found")))
                .collect(Collectors.toSet());

        user.setRoles(newRoles);
        userRepository.save(user);
    }

    public Role createRole(String name, String description) {
        if(roleRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Role already exists");
        }
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return roleRepository.save(role);
    }

    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(Math.toIntExact(roleId))) {
            throw new RuntimeException("Role not found");
        }
        roleRepository.deleteById(Math.toIntExact(roleId));
    }

    private UserResponseDto mapToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return dto;
    }
}