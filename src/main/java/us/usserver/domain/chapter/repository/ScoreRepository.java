package us.usserver.domain.chapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.entity.Score;

import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    Optional<Score> findScoreByAuthorAndChapter(Author author, Chapter chapter);

    @Query("SELECT AVG(s.score) FROM Score s WHERE s.chapter = :chapter")
    Double findAverageScoreByChapter(Chapter chapter);
}
