package us.usserver.domain.authority.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.authority.entity.Stake;

import java.util.List;
import java.util.Optional;

@Repository
public interface StakeRepository extends JpaRepository<Stake, Long> {
    List<Stake> findAllByNovel(Novel novel);

    Optional<Stake> findByNovelAndAuthor(Novel novel, Author author);
}
