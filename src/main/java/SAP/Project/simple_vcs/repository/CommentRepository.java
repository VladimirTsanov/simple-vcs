package SAP.Project.simple_vcs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import SAP.Project.simple_vcs.entity.Comment;
import SAP.Project.simple_vcs.entity.User;
import SAP.Project.simple_vcs.entity.Version;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByVersionOrderByCreatedAtAsc(Version version);
    List<Comment> findByAuthor(User author);
}
