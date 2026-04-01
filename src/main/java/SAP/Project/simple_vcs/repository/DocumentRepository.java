package SAP.Project.simple_vcs.repository;

import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long> {
    Optional<Document> findByTitle(String title);
}
