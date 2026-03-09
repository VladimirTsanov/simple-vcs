package SAP.Project.simple_vcs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import SAP.Project.simple_vcs.entity.Role;
import SAP.Project.simple_vcs.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByIsActiveTrue();
    List<User> findByRole(Role role);
}
