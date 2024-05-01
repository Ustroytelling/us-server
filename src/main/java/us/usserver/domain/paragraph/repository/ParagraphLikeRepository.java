package us.usserver.domain.paragraph.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.entity.ParagraphLike;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParagraphLikeRepository extends JpaRepository<ParagraphLike, Long> {
    List<ParagraphLike> findAllByParagraph(Paragraph paragraph);
    List<ParagraphLike> findAllByAuthor(Author author);
    Optional<ParagraphLike> findByParagraphAndAuthor(Paragraph paragraph, Author author);
    Optional<ParagraphLike> findByParagraphIdAndAuthorId(Long paragraphId, Long authorId);
    Boolean existsByParagraphAndAuthor(Paragraph paragraph, Author author);
}
