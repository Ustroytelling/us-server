package us.usserver.novel;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import us.usserver.novel.dto.ConditionsOfPagination;
import us.usserver.novel.dto.SearchNovelReq;

@Repository
public interface NovelRepository {
    Novel save(Novel novel);

    void delete(Novel novel);

    Slice<Novel> searchNovelList(SearchNovelReq searchNovelReq, Pageable pageable);

    Slice<Novel> moreNovelList(ConditionsOfPagination novelMoreDto, Pageable pageable);

    Slice<Novel> getNovelList(Pageable pageable);

}
