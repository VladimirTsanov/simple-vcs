package SAP.Project.simple_vcs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.entity.Version;
import SAP.Project.simple_vcs.enums.VersionStatus;

public interface VersionRepository extends JpaRepository<Version, Long> {
    List<Version> findByDocumentOrderByVersionNumberAsc(Document document);
    Optional<Version> findTopByDocumentOrderByVersionNumberDesc(Document document);
    List<Version> findByDocumentAndStatus(Document document, VersionStatus status);
    List<Version> findByAuthor(User author);
    List<Version> findByReviewerAndStatus(User reviewer, VersionStatus status);
}
