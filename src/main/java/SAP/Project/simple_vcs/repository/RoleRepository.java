package SAP.Project.simple_vcs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import SAP.Project.simple_vcs.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
