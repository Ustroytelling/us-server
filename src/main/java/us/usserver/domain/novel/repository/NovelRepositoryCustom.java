package us.usserver.domain.novel.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.NoRepositoryBean;
import us.usserver.domain.novel.entity.Novel;

import java.util.Set;

@NoRepositoryBean
public interface NovelRepositoryCustom {
    Slice<Novel> searchNovelList(Set<String> keywords, Pageable pageable);
}
