package SAP.Project.simple_vcs.dto;

public record VersionResponse(Long id, Integer versionNumber, String content, String status, Long authorId) {
}