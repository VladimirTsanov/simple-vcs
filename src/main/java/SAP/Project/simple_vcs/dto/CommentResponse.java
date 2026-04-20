package SAP.Project.simple_vcs.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(Integer versionNum, String content, String versionStatus, String authorUsername, List<String> authorRoles, LocalDateTime createdAt) {
}
