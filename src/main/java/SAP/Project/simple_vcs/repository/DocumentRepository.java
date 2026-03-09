package SAP.Project.simple_vcs.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import SAP.Project.simple_vcs.entity.Document;
import SAP.Project.simple_vcs.entity.User;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByOwner(User owner);
    List<Document> findByTitleContainingIgnoreCase(String keyword);
}
