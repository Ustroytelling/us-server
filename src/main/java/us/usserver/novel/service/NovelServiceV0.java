package us.usserver.novel.service;

import com.querydsl.core.types.Order;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import us.usserver.author.Author;
import us.usserver.author.AuthorRepository;
import us.usserver.authority.Authority;
import us.usserver.authority.AuthorityRepository;
import us.usserver.chapter.ChapterService;
import us.usserver.chapter.dto.ChapterInfo;
import us.usserver.global.EntityService;
import us.usserver.global.ExceptionMessage;
import us.usserver.global.exception.AuthorNotFoundException;
import us.usserver.global.exception.MainAuthorIsNotMatchedException;
import us.usserver.member.Member;
import us.usserver.novel.Novel;
import us.usserver.novel.NovelRepository;
import us.usserver.novel.NovelService;
import us.usserver.novel.dto.*;
import us.usserver.novel.novelEnum.Orders;
import us.usserver.novel.novelEnum.SortColumn;
import us.usserver.novel.novelEnum.Sorts;
import us.usserver.stake.StakeService;
import us.usserver.stake.dto.GetStakeResponse;
import us.usserver.stake.dto.StakeInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NovelServiceV0 implements NovelService {
    private final EntityService entityService;
    private final StakeService stakeService;
    private final ChapterService chapterService;

    private final AuthorityRepository authorityRepository;
    private final AuthorRepository authorRepository;
    private final NovelRepository novelRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final Integer RECENT_KEYWORD_SIZE = 10;
    private static final Integer DEFAULT_PAGE_SIZE = 6;

    @Override
    public NovelInfo createNovel(Member member, CreateNovelReq createNovelReq) {
        Author author = getAuthor(member);

        Novel novel = createNovelReq.toEntity(author);
        Novel saveNovel = novelRepository.save(novel);

        author.getCreatedNovels().add(novel);
        authorityRepository.save(Authority.builder().author(author).novel(novel).build());

        chapterService.createChapter(saveNovel.getId(), author.getId());
        return NovelInfo.mapNovelToNovelInfo(novel);

//        return NovelInfo.builder() TODO: 리펙토링으로 삭제 예정
//                .title(novel.getTitle())
//                .createdAuthor(AuthorInfo.fromAuthor(author))
//                .genre(novel.getGenre())
//                .hashtag(novel.getHashtags())
//                .joinedAuthorCnt(1)
//                .commentCnt(0)
//                .likeCnt(0)
//                .build();
    }

    @Override
    public NovelInfo getNovelInfo(Long novelId) {
        Novel novel = entityService.getNovel(novelId);
        return NovelInfo.mapNovelToNovelInfo(novel);
//        AuthorInfo authorInfo = AuthorInfo.fromAuthor(novel.getMainAuthor()); TODO: 리펙토링으로 삭제 예정
//
//        return NovelInfo.builder()
//                .title(novel.getTitle())
//                .createdAuthor(authorInfo)
//                .genre(novel.getGenre())
//                .hashtag(novel.getHashtags())
//                .joinedAuthorCnt(authorityRepository.countAllByNovel(novel))
//                .commentCnt(commentJpaRepository.countAllByNovel(novel))
//                .likeCnt(novelLikeRepository.countAllByNovel(novel))
//                .novelSharelUrl("http://localhost:8080/novel/" + novel.getId())
//                .build();
    }

    @Override
    public NovelDetailInfo getNovelDetailInfo(Long novelId) {
        Novel novel = entityService.getNovel(novelId);
        GetStakeResponse stakeResponse = stakeService.getStakeInfoOfNovel(novelId);
        List<StakeInfo> stakeInfos = stakeResponse.getStakeInfos();
        List<ChapterInfo> chapterInfos = chapterService.getChaptersOfNovel(novel);

        return NovelDetailInfo.builder()
                .title(novel.getTitle())
                .thumbnail(novel.getThumbnail())
                .synopsis(novel.getSynopsis())
                .authorName(novel.getMainAuthor().getNickname())
                .authorIntroduction(novel.getAuthorDescription())
                .ageRating(novel.getAgeRating())
                .genre(novel.getGenre())
                .hashtags(novel.getHashtags())
                .stakeInfos(stakeInfos)
                .chapterInfos(chapterInfos)
                .build();
    }

    @Override
    public String modifyNovelSynopsis(Long novelId, Long authorId, String synopsis) {
        Novel novel = entityService.getNovel(novelId);
        Author author = entityService.getAuthor(authorId);

        if (!novel.getMainAuthor().getId().equals(author.getId())) {
            throw new MainAuthorIsNotMatchedException(ExceptionMessage.Main_Author_NOT_MATCHED);
        }

        novel.setSynopsis(synopsis);
        return synopsis;
    }

    @Override
    public AuthorDescription modifyAuthorDescription(Long novelId, Long authorId, AuthorDescription req) {
        Novel novel = entityService.getNovel(novelId);
        Author author = entityService.getAuthor(authorId);

        if (!novel.getMainAuthor().getId().equals(author.getId())) {
            throw new MainAuthorIsNotMatchedException(ExceptionMessage.Main_Author_NOT_MATCHED);
        }

        novel.setAuthorDescription(req.getDescription());
        return req;
    }

    @Override
    public GetMainPageResponse getMainPageInfo(Member member) {
        Author author = getAuthor(member);

        PageRequest realTimeUpdates = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, SortColumn.updatedAt.toString()));
        PageRequest recentlyCreated = PageRequest.of(0, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, SortColumn.createdAt.toString()));
        PageRequest popular = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, SortColumn.hit.toString()));

        Slice<NovelInfo> realTimeUpdatesNovel = novelRepository
                .getNovelList(realTimeUpdates)
                .map(NovelInfo::mapNovelToNovelInfo);

        Slice<NovelInfo> recentlyCreatedNovel = novelRepository
                .getNovelList(recentlyCreated)
                .map(NovelInfo::mapNovelToNovelInfo);

        GetMainPageResponse.builder().readNovels()
    }

    @Override
    public HomeNovelListResponse homeNovelInfo(Member member) {
        Author author = getAuthor(member);
        ConditionsOfPagination realTimeNovels = ConditionsOfPagination.builder()
                .lastNovelId(501L)
                .sortDto(SortDto.builder().sorts(Sorts.LATEST).orders(Orders.DESC).build())
                .build();

        ConditionsOfPagination newNovels = ConditionsOfPagination.builder()
                .lastNovelId(501L)
                .sortDto(SortDto.builder().sorts(Sorts.NEW).orders(Orders.DESC).build())
                .build();

        return HomeNovelListResponse.builder()
                .realTimeNovels(mapNovelsToNovelInfos(novelRepository.moreNovelList(realTimeNovels, getPageRequest(realTimeNovels))))
                .newNovels(mapNovelsToNovelInfos(novelRepository.moreNovelList(newNovels, getPageRequest(newNovels))))
                .readNovels((author == null) ? null : author.getViewedNovels()
                        .stream().limit(8)
                        .sorted(Comparator.comparing(Novel::getId))
                        .map(NovelInfo::mapNovelToNovelInfo)
                        .toList())
                .build();
    }

    @Override
    public NovelPageInfoResponse moreNovel(ConditionsOfPagination conditionsOfPagination) {
        PageRequest pageable = getPageRequest(conditionsOfPagination);
        Slice<Novel> novelSlice = novelRepository.moreNovelList(conditionsOfPagination, pageable);

        return getNovelPageInfoResponse(novelSlice, conditionsOfPagination.getSortDto());
    }
    @Override
    public NovelPageInfoResponse readMoreNovel(Member member, ReadInfoOfNovel readInfoOfNovel){
        Author author = getAuthor(member);

        if (author != null) {
            int author_readNovel_cnt = author.getViewedNovels().size();
            int getSize = readInfoOfNovel.getGetNovelSize() + readInfoOfNovel.getSize();
            int endPoint = Math.min(author_readNovel_cnt, getSize);

            List<Novel> novelList = author
                    .getViewedNovels()
                    .stream()
                    .sorted(Comparator.comparing(Novel::getId))
                    .toList()
                    .subList(readInfoOfNovel.getGetNovelSize(), endPoint);
            boolean hasNext = getSize == endPoint;

            return NovelPageInfoResponse.builder()
                    .novelList(novelList.stream().map(NovelInfo::mapNovelToNovelInfo).toList())
                    .lastNovelId(novelList.get(novelList.size()-1).getId())
                    .hasNext(hasNext)
                    .sorts(null)
                    .build();
        } else {
            return null;
        }
    }

    private PageRequest getPageRequest(ConditionsOfPagination conditions) {
        if (conditions.getSize() == null) {
            conditions.setSize(6);
        }
        return PageRequest.ofSize(conditions.getSize());
    }

    private NovelPageInfoResponse getNovelPageInfoResponse(Slice<Novel> novelSlice, SortDto novelMoreDto) {
        Long newLastNovelId = getLastNovelId(novelSlice);

        return NovelPageInfoResponse
                .builder()
                .novelList(mapNovelsToNovelInfos(novelSlice))
                .lastNovelId(newLastNovelId)
                .hasNext(novelSlice.hasNext())
                .sorts(novelMoreDto.getSorts())
                .build();
    }

    private Long getLastNovelId(Slice<Novel> novelSlice){
        return novelSlice.isEmpty() ? null : novelSlice.getContent().get(novelSlice.getNumberOfElements() - 1).getId();
    }

    @Override
    public NovelPageInfoResponse searchNovel(Member member, SearchNovelReq searchNovelReq) {
        Author author = getAuthor(member);
        PageRequest pageable = PageRequest.ofSize(searchNovelReq.getSize());
        if (searchNovelReq.getTitle() != null && searchNovelReq.getLastNovelId() == 0L) {
            increaseKeywordScore(searchNovelReq.getTitle());
            recentKeyword((author == null) ? null : author.getId(), searchNovelReq.getTitle());
        }
        Slice<Novel> novelSlice = novelRepository.searchNovelList(searchNovelReq, pageable);

        return getNovelPageInfoResponse(novelSlice, searchNovelReq.getSortDto());
    }

    @Override
    public SearchKeywordResponse searchKeyword(Member member) {
        Author author = getAuthor(member);

        //최신 검색어
        ListOperations<String, String> opsForList = redisTemplate.opsForList();
        ZSetOperations<String, String> opsForZSet = redisTemplate.opsForZSet();

        //인기 검색어
        String hot_keyword = "ranking";
        Set<ZSetOperations.TypedTuple<String>> rankingTuples = opsForZSet.reverseRangeWithScores(hot_keyword, 0, 9);

        return SearchKeywordResponse.builder()
                .recentSearch(opsForList.range(String.valueOf(author.getId()), 0, 9))
                .hotSearch(rankingTuples.stream().map(set -> set.getValue()).collect(Collectors.toList()))
                .build();
    }

    @Override
    public void deleteSearchKeyword(Member member) {
        Author author = getAuthor(member);

        String key = String.valueOf(author.getId());
        Long size = redisTemplate.opsForList().size(key);

        redisTemplate.opsForList().rightPop(key, size);
    }

    private void increaseKeywordScore(String keyword) {
        int score = 0;

        try {
            redisTemplate.opsForZSet().incrementScore("ranking", keyword,1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        redisTemplate.opsForZSet().incrementScore("ranking", keyword, score);
    }

    private void recentKeyword(Long authorId, String keyword) {
        if (authorId == null) {
            return;
        }

        String key = String.valueOf(authorId);
        String equalWord = null;
        ListOperations<String, String> list = redisTemplate.opsForList();

        for (int i = 0; i < list.size(key); i++) {
            String frontWord = list.leftPop(key);
            if (frontWord.equals(keyword)) {
                equalWord = frontWord;
            } else{
                list.rightPush(key, frontWord);
            }
        }
        if (equalWord != null) {
            list.leftPush(key, equalWord);
            return;
        }

        Long size = list.size(key);
        if (size == (long) RECENT_KEYWORD_SIZE) {
            list.rightPop(key);
        }
        list.leftPush(key, keyword);
    }

    private Author getAuthor(Member member) {
        if (member == null) {
            return null;
        }
        return authorRepository.getAuthorByMemberId(member.getId()).orElseThrow(() -> new AuthorNotFoundException(ExceptionMessage.Author_NOT_FOUND));
    }

    private List<NovelInfo> mapNovelsToNovelInfos(Slice<Novel> novels) {
        return novels.getContent()
                .stream().map(NovelInfo::mapNovelToNovelInfo)
                .toList();
    }
}
