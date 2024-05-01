package us.usserver.domain.authority.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.novel.entity.Novel;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Integer countAllByNovel(Novel novel);

    List<Authority> findAllByAuthor(Author author);

    List<Authority> findAllByNovel(Novel novel);
}
