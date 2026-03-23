package SAP.Project.simple_vcs.dto;

public record VersionRequest(Long documentId, String content, Long authorId) {
}