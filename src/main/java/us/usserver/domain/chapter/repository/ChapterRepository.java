package us.usserver.domain.chapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.novel.entity.Novel;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findAllByNovel(Novel novel);

    List<Chapter> findAllByNovelOrderByPart(Novel novel);

    Optional<Chapter> getChapterById(Long chapterId);

    Optional<Chapter> getChapterByTitle(String title);

    Integer countChapterByNovel(Novel novel);
}
