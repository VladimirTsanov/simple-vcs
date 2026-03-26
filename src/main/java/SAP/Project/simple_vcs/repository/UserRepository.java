package SAP.Project.simple_vcs.repository;

import SAP.Project.simple_vcs.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);

    boolean existsByUsername(@NotBlank(message = "Username cannot be blank") @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters") String username);

    boolean existsByEmail(@Email(message = "Must be a valid email") @NotBlank(message = "Email cannot be blank") String email);

    Optional<User> findByEmail(String email);
}
