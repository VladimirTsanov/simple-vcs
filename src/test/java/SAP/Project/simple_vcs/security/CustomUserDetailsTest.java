package SAP.Project.simple_vcs.security;

import SAP.Project.simple_vcs.entity.Role;
import SAP.Project.simple_vcs.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    @Test
    void getAuthorities_roleAlreadyHasRolePrefix_keepsSamePrefix() {
        CustomUserDetails details = detailsWithRoles("ROLE_ADMIN");

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void getAuthorities_roleWithoutRolePrefix_prefixIsAdded() {
        CustomUserDetails details = detailsWithRoles("ADMIN");

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void getAuthorities_multipleRoles_allCorrectlyPrefixed() {
        CustomUserDetails details = detailsWithRoles("ROLE_AUTHOR", "REVIEWER");

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();

        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_AUTHOR", "ROLE_REVIEWER");
    }

    @Test
    void isEnabled_activeUser_returnsTrue() {
        User user = new User();
        user.setActive(true);

        assertThat(new CustomUserDetails(user).isEnabled()).isTrue();
    }

    @Test
    void isEnabled_inactiveUser_returnsFalse() {
        User user = new User();
        user.setActive(false);

        assertThat(new CustomUserDetails(user).isEnabled()).isFalse();
    }

    @Test
    void getPassword_returnsPasswordHash() {
        User user = new User();
        user.setPasswordHash("hashed_password");

        assertThat(new CustomUserDetails(user).getPassword()).isEqualTo("hashed_password");
    }

    @Test
    void getUsername_returnsUsername() {
        User user = new User();
        user.setUsername("alice");

        assertThat(new CustomUserDetails(user).getUsername()).isEqualTo("alice");
    }

    @Test
    void isAccountNonExpired_alwaysTrue() {
        assertThat(new CustomUserDetails(new User()).isAccountNonExpired()).isTrue();
    }

    @Test
    void isAccountNonLocked_alwaysTrue() {
        assertThat(new CustomUserDetails(new User()).isAccountNonLocked()).isTrue();
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private CustomUserDetails detailsWithRoles(String... roleNames) {
        Set<Role> roles = new HashSet<>();
        for (String name : roleNames) {
            Role role = new Role();
            role.setName(name);
            roles.add(role);
        }
        User user = new User();
        user.setRoles(roles);
        return new CustomUserDetails(user);
    }
}
