package SAP.Project.simple_vcs.repository;

import SAP.Project.simple_vcs.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByVersionDocumentId(Long docId);

}