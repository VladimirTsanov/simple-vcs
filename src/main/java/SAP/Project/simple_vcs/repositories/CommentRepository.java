package SAP.Project.simple_vcs.repositories;

import SAP.Project.simple_vcs.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByVersionIdOrderByCreatedAtAsc(Long versionId);
}
