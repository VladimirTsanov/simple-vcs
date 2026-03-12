package SAP.Project.simple_vcs.repository;

import SAP.Project.simple_vcs.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VersionRepository extends JpaRepository<Version,Long>{
    List<Version> findByDocumentIdOrderByVersionNumberDesc(Long documentId);
}
