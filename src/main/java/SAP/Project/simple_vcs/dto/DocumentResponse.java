package SAP.Project.simple_vcs.dto;

public record DocumentResponse(Long id, String title, Integer version, String status, String authorUsername) {
}
