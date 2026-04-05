package SAP.Project.simple_vcs.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private boolean active;
    private Set<String> roles;
}