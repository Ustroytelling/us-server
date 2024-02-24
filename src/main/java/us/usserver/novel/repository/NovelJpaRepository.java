package us.usserver.novel.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import us.usserver.novel.Novel;

import java.util.Optional;

@Repository
public interface NovelJpaRepository extends JpaRepository<Novel, Long> {
    Optional<Novel> getNovelById(Long id);

    Slice<Novel> findSliceBy(Pageable pageable);
}
