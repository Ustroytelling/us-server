package us.usserver.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.usserver.domain.comment.entity.Comment;
import us.usserver.domain.comment.entity.CommentLike;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>, CommentLikeRepositoryDSL {
    Optional<CommentLike> findByComment(Comment comment);
}
