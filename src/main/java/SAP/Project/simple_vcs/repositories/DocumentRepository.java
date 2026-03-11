package SAP.Project.simple_vcs.repositories;

import SAP.Project.simple_vcs.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long> {
    List<Document> findByTitleContainingIgnoreCase(String title);
}
