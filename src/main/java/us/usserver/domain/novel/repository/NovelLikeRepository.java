package us.usserver.domain.novel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.entity.NovelLike;

import java.util.List;
import java.util.Optional;

@Repository
public interface NovelLikeRepository extends JpaRepository<NovelLike, Long>, NovelLikeRepositoryCustom {
    List<NovelLike> findAllByNovel(Novel novel);

    List<NovelLike> findAllByAuthor(Author author);

    Optional<NovelLike> findFirstByNovelAndAuthor(Novel novel, Author author);

    Optional<NovelLike> findByNovelIdAndAuthorId(Long novelId, Long AuthorId);

    Boolean existsNovelLikeByAuthorIdAndNovelId(Long authorId, Long novelId);

    Integer countAllByNovel(Novel novel);
}
