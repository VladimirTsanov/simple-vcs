package SAP.Project.simple_vcs.repositories;

import SAP.Project.simple_vcs.entities.Document;
import SAP.Project.simple_vcs.entities.User;
import SAP.Project.simple_vcs.entities.Version;
import SAP.Project.simple_vcs.enums.VersionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VersionRepository extends JpaRepository<Version,Long> {
    List<Version> findByDocumentOrderByVersionNumberAsc(Document document);
    Optional<Version> findFirstByDocumentOrderByVersionNumberDesc(Document document);
    List<Version> findByDocumentAndStatus(Document document, VersionStatus status);
}
