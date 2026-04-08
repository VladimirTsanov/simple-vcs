package SAP.Project.simple_vcs.dto;

import SAP.Project.simple_vcs.entity.VersionStatus;

public record VersionStatusUpdateRequest(VersionStatus newStatus) {}
