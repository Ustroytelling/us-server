package us.usserver.domain.paragraph.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.entity.Vote;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>, VoteDSLRepository {
    Optional<Vote> getVoteById(Long voteId);

    Boolean existsByParagraphAndAuthor(Paragraph paragraph, Author author);

    List<Vote> findAllByAuthor(Author author);

    Optional<Vote> findByParagraphIdAndAuthorId(Long paragraphId, Long authorId);

    Integer countAllByParagraph(Paragraph paragraph);
}
