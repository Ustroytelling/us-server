package us.usserver.domain.paragraph.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.paragraph.entity.Paragraph;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {
    Optional<Paragraph> getParagraphById(Long paragraphId);

    List<Paragraph> findAllByChapter(Chapter chapter);

    List<Paragraph> findAllByAuthor(Author author);

    Integer countParagraphsByChapter(Chapter chapter);
}
