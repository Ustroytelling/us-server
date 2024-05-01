package us.usserver.domain.author.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.entity.ReadNovel;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadNovelRepository extends JpaRepository<ReadNovel, Long> {
    Optional<ReadNovel> findReadNovelById(Long id);

    List<ReadNovel> findAllByAuthor(Author author);
}
