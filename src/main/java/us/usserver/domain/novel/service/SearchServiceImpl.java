package us.usserver.domain.novel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import us.usserver.domain.novel.dto.NovelSimpleInfo;
import us.usserver.domain.novel.dto.SortColumn;
import us.usserver.domain.novel.dto.res.SearchNovelRes;
import us.usserver.domain.novel.dto.res.SearchPageRes;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.utils.RedisUtils;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final NovelRepository novelRepository;
    private final KomoranService komoranService;
    private final RedisUtils redisUtils;

    private static final Integer DEFAULT_PAGE_SIZE = 6;

    @Override
    @Transactional
    public SearchNovelRes searchNovel(Long memberId, String keyword, Integer nextPage) {
        PageRequest pageRequest = getPageRequest(nextPage);

        Set<String> keywords = komoranService.tokenizeKeyword(keyword);

        Slice<Novel> novelSlice = novelRepository.searchNovelList(keywords, pageRequest);
        Set<NovelSimpleInfo> novelSimpleInfos = novelSlice.map(NovelSimpleInfo::mapNovelToSimpleInfo).toSet();

        redisUtils.saveSearchLog(keyword, memberId);

        return new SearchNovelRes(novelSimpleInfos, novelSlice.getNumber() + 1, novelSlice.hasNext());
    }

    private PageRequest getPageRequest(int pageNum) {
        return PageRequest.of(
                pageNum,
                SearchServiceImpl.DEFAULT_PAGE_SIZE,
                Sort.by(Sort.Direction.DESC,SortColumn.createdAt.toString()));
    }

    @Override
    @Transactional
    public SearchPageRes getSearchPage(Long memberId) {
        return new SearchPageRes(
                redisUtils.getSearchLogs(memberId),
                redisUtils.getKeywordRanking()
        );
    }

    @Override
    @Transactional
    public void deleteSearchKeyword(Long memberId, String keyword) {
        redisUtils.deleteSearchLog(keyword, memberId);
    }
}
