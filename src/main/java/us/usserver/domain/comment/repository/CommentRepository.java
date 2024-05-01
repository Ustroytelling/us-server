package us.usserver.domain.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.comment.entity.Comment;
import us.usserver.domain.novel.entity.Novel;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryDSL {
    Optional<Comment> getCommentById(Long commentId);

    Slice<Comment> findSliceByNovel(Novel novel, Pageable pageable);
    Slice<Comment> findSliceByChapter(Chapter chapter, Pageable pageable);

    List<Comment> findAllByNovel(Novel novel);
    List<Comment> findAllByChapter(Chapter chapter);
    List<Comment> findAllByAuthor(Author author);

    Integer countAllByChapter(Chapter chapter);

}
