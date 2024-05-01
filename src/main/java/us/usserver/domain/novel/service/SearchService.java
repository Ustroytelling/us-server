package us.usserver.domain.novel.service;

import org.springframework.stereotype.Service;
import us.usserver.domain.novel.dto.res.SearchNovelRes;
import us.usserver.domain.novel.dto.res.SearchPageRes;

@Service
public interface SearchService {
    SearchNovelRes searchNovel(Long memberId, String keyword, Integer nextPage);

    SearchPageRes getSearchPage(Long memberId);

    void deleteSearchKeyword(Long memberId, String keyword);
}
