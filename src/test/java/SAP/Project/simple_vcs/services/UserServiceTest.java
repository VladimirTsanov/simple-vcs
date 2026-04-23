package SAP.Project.simple_vcs.services;

import SAP.Project.simple_vcs.dto.UserRegistrationDto;
import SAP.Project.simple_vcs.dto.UserResponseDto;
import SAP.Project.simple_vcs.entity.Role;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.exception.UserAlreadyExistsException;
import SAP.Project.simple_vcs.repository.RoleRepository;
import SAP.Project.simple_vcs.repository.UserRepository;
import SAP.Project.simple_vcs.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuditLogService auditLogService;

    @InjectMocks private UserService userService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // -------------------------------------------------------------------------
    // registerUser
    // -------------------------------------------------------------------------

    @Test
    void registerUser_success_assignsDefaultRoleAndEncodesPassword() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");
        dto.setPassword("secret123");

        Role authorRole = roleWithName("ROLE_AUTHOR");
        User saved = new User();
        saved.setId(1L);
        saved.setUsername("alice");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_AUTHOR")).thenReturn(Optional.of(authorRole));
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(saved);

        userService.registerUser(dto);

        verify(passwordEncoder).encode("secret123");
        verify(userRepository).save(argThat(u -> u.getRoles().contains(authorRole)));
        verify(auditLogService).logActionWithActor(eq(saved), eq("REGISTER"), eq("User"), eq(1L), any());
    }

    @Test
    void registerUser_duplicateUsername_throwsUserAlreadyExistsException() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");
        dto.setPassword("secret123");

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_duplicateEmail_throwsUserAlreadyExistsException() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");
        dto.setPassword("secret123");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_defaultRoleNotFound_throwsRuntimeException() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("alice");
        dto.setEmail("alice@example.com");
        dto.setPassword("secret123");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_AUTHOR")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.registerUser(dto));
        verify(userRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // setUserStatus
    // -------------------------------------------------------------------------

    @Test
    void setUserStatus_activateOtherUser_success() {
        User target = new User();
        target.setId(2L);
        target.setUsername("bob");

        mockSecurityContext("alice");
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(userRepository.save(any())).thenReturn(target);

        userService.setUserStatus(2L, true);

        assertThat(target.isActive()).isTrue();
        verify(userRepository).save(target);
    }

    @Test
    void setUserStatus_deactivateSelf_throwsRuntimeException() {
        User target = new User();
        target.setId(1L);
        target.setUsername("admin");

        mockSecurityContext("admin");
        when(userRepository.findById(1L)).thenReturn(Optional.of(target));

        assertThrows(RuntimeException.class, () -> userService.setUserStatus(1L, false));
        verify(userRepository, never()).save(any());
    }

    @Test
    void setUserStatus_userNotFound_throwsRuntimeException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.setUserStatus(99L, false));
    }

    // -------------------------------------------------------------------------
    // updateUserRoles
    // -------------------------------------------------------------------------

    @Test
    void updateUserRoles_success() {
        User user = new User();
        user.setId(1L);
        Role adminRole = roleWithName("ROLE_ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(userRepository.save(any())).thenReturn(user);

        userService.updateUserRoles(1L, Set.of("ROLE_ADMIN"));

        assertThat(user.getRoles()).containsExactly(adminRole);
    }

    @Test
    void updateUserRoles_roleNotFound_throwsRuntimeException() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.updateUserRoles(1L, Set.of("ROLE_UNKNOWN")));
    }

    // -------------------------------------------------------------------------
    // createRole
    // -------------------------------------------------------------------------

    @Test
    void createRole_success() {
        Role saved = roleWithName("ROLE_EDITOR");
        saved.setId(5L);

        when(roleRepository.findByName("ROLE_EDITOR")).thenReturn(Optional.empty());
        when(roleRepository.save(any())).thenReturn(saved);

        Role result = userService.createRole("ROLE_EDITOR", "An editor role");
        assertThat(result.getName()).isEqualTo("ROLE_EDITOR");
    }

    @Test
    void createRole_alreadyExists_throwsRuntimeException() {
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(roleWithName("ROLE_ADMIN")));

        assertThrows(RuntimeException.class, () -> userService.createRole("ROLE_ADMIN", "desc"));
        verify(roleRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // deleteRole
    // -------------------------------------------------------------------------

    @Test
    void deleteRole_success() {
        when(roleRepository.existsById(3)).thenReturn(true);

        userService.deleteRole(3L);

        verify(roleRepository).deleteById(3);
    }

    @Test
    void deleteRole_notFound_throwsRuntimeException() {
        when(roleRepository.existsById(99)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.deleteRole(99L));
        verify(roleRepository, never()).deleteById(any());
    }

    // -------------------------------------------------------------------------
    // getAllUsers
    // -------------------------------------------------------------------------

    @Test
    void getAllUsers_returnsMappedDtos() {
        User u = new User();
        u.setId(1L);
        u.setUsername("alice");
        u.setEmail("alice@example.com");
        u.setActive(true);
        u.setRoles(new HashSet<>(Collections.singletonList(roleWithName("ROLE_AUTHOR"))));

        when(userRepository.findAll()).thenReturn(List.of(u));

        List<UserResponseDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("alice");
        assertThat(result.get(0).getRoles()).containsExactly("ROLE_AUTHOR");
    }

    // -------------------------------------------------------------------------
    // loadUserByUsername
    // -------------------------------------------------------------------------

    @Test
    void loadUserByUsername_found_returnsCustomUserDetails() {
        User u = new User();
        u.setId(1L);
        u.setEmail("alice@example.com");
        u.setPasswordHash("hashed");
        u.setRoles(new HashSet<>());

        when(userRepository.findByEmail("alice@example.com")).thenReturn(u);

        var result = userService.loadUserByUsername("alice@example.com");

        assertThat(result).isInstanceOf(CustomUserDetails.class);
    }

    @Test
    void loadUserByUsername_notFound_throwsUsernameNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("ghost@example.com"));
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private Role roleWithName(String name) {
        Role role = new Role();
        role.setName(name);
        return role;
    }

    private void mockSecurityContext(String username) {
        Authentication auth = mock(Authentication.class);
        SecurityContext ctx = mock(SecurityContext.class);
        when(auth.getName()).thenReturn(username);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }
}
