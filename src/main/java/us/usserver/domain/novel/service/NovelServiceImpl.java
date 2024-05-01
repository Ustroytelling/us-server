package us.usserver.domain.novel.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.entity.ReadNovel;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.authority.dto.StakeInfo;
import us.usserver.domain.authority.dto.res.StakeInfoResponse;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.repository.AuthorityRepository;
import us.usserver.domain.authority.service.StakeService;
import us.usserver.domain.chapter.dto.ChapterInfo;
import us.usserver.domain.chapter.service.ChapterService;
import us.usserver.domain.novel.dto.*;
import us.usserver.domain.novel.dto.req.NovelBlueprint;
import us.usserver.domain.novel.dto.res.MainPageRes;
import us.usserver.domain.novel.dto.res.MoreNovelRes;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.EntityFacade;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NovelServiceImpl implements NovelService {
    private final EntityFacade entityFacade;
    private final StakeService stakeService;
    private final ChapterService chapterService;

    private final AuthorityRepository authorityRepository;
    private final NovelRepository novelRepository;
    private final AuthorRepository authorRepository;

    private static final Integer DEFAULT_PAGE_SIZE = 6;

    @Override
    @Transactional
    public NovelInfo createNovel(Long memberId, NovelBlueprint novelBlueprint) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        Novel novel = novelBlueprint.mapBlueprintToNovel(author);
        Novel saveNovel = novelRepository.save(novel);

        authorityRepository.save(
                Authority.builder().author(author).novel(novel).build());

        chapterService.createChapter(saveNovel.getId(), author.getId());
        return NovelInfo.mapNovelToNovelInfo(novel);
    }

    @Override
    @Transactional
    public NovelInfo getNovelInfo(Long novelId) {
        Novel novel = entityFacade.getNovel(novelId);
        return NovelInfo.mapNovelToNovelInfo(novel);
    }

    @Override
    @Transactional
    public NovelDetailInfo getNovelDetailInfo(Long novelId) {
        Novel novel = entityFacade.getNovel(novelId);
        StakeInfoResponse stakeResponse = stakeService.getStakeInfoOfNovel(novelId);

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
    @Transactional
    public String modifyNovelSynopsis(Long novelId, Long memberId, String synopsis) {
        Novel novel = entityFacade.getNovel(novelId);
        Author author = entityFacade.getAuthorByMemberId(memberId);

        if (!novel.getMainAuthor().getId().equals(author.getId())) {
            throw new BaseException(ErrorCode.MAIN_AUTHOR_NOT_MATCHED);
        }

        novel.changeSynopsis(synopsis);
        return synopsis;
    }

    @Override
    @Transactional
    public AuthorDescription modifyAuthorDescription(Long novelId, Long memberId, AuthorDescription req) {
        Novel novel = entityFacade.getNovel(novelId);
        Author author = entityFacade.getAuthorByMemberId(memberId);

        if (!novel.getMainAuthor().getId().equals(author.getId())) {
            throw new BaseException(ErrorCode.MAIN_AUTHOR_NOT_MATCHED);
        }

        novel.changeAuthorDescription(req.description());
        return req;
    }

    @Override
    @Transactional
    public MainPageRes getMainPage(Long memberId) {
        List<NovelInfo> readNovels;
        if (memberId == null) {
            readNovels =  Collections.emptyList();
        } else {
            readNovels = getReadNovels(memberId);
        }

        PageRequest realTimeUpdates = getPageRequest(0, DEFAULT_PAGE_SIZE, Sort.Direction.DESC, SortColumn.recentlyUpdated);
        PageRequest recentlyCreated = getPageRequest(0, DEFAULT_PAGE_SIZE, Sort.Direction.DESC, SortColumn.createdAt);
        PageRequest popular = getPageRequest(0, 3, Sort.Direction.DESC, SortColumn.hit);

        List<NovelInfo> realTimeUpdatesNovels = novelRepository.findSliceBy(realTimeUpdates)
                .map(NovelInfo::mapNovelToNovelInfo).toList();
        List<NovelInfo> recentlyCreatedNovels = novelRepository.findSliceBy(recentlyCreated)
                .map(NovelInfo::mapNovelToNovelInfo).toList();
        List<NovelInfo> popularNovels = novelRepository.findSliceBy(popular)
                .map(NovelInfo::mapNovelToNovelInfo).toList();

        return new MainPageRes(popularNovels, readNovels, realTimeUpdatesNovels, recentlyCreatedNovels);
    }

    @Override
    @Transactional
    public MoreNovelRes getMoreNovels(Long memberId, MainNovelType mainNovelType, Integer nextPage) {
        PageRequest pageRequest = switch (mainNovelType) {
            case NEW -> getPageRequest(nextPage, DEFAULT_PAGE_SIZE, Sort.Direction.DESC, SortColumn.createdAt);
            case UPDATE -> getPageRequest(nextPage, DEFAULT_PAGE_SIZE, Sort.Direction.DESC, SortColumn.recentlyUpdated);
            case POPULAR -> getPageRequest(nextPage, DEFAULT_PAGE_SIZE, Sort.Direction.DESC, SortColumn.hit);
        };

        Slice<Novel> novelSlice = novelRepository.findSliceBy(pageRequest);
        List<NovelInfo> novelInfos = novelSlice.map(NovelInfo::mapNovelToNovelInfo).toList();
        return new MoreNovelRes(novelInfos, novelSlice.getNumber() + 1, novelSlice.hasNext());
    }

    @Override
    @Transactional
    public MoreNovelRes readMoreNovel(Long memberId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);

        List<NovelInfo> novelInfos = author.getReadNovels().stream()
                .sorted(Comparator.comparing(ReadNovel::getReadDate).reversed())
                .map(ReadNovel::getNovel)
                .map(NovelInfo::mapNovelToNovelInfo)
                .toList();

        return new MoreNovelRes(novelInfos, 0, Boolean.FALSE);
    }

    private PageRequest getPageRequest(int pageNum, int pageSize, Sort.Direction direction, SortColumn sortColumn) {
        return PageRequest.of(pageNum, pageSize, Sort.by(direction, sortColumn.toString()));
    }

    private List<NovelInfo> getReadNovels(Long memberId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        return author.getReadNovels().stream()
                .sorted(Comparator.comparing(ReadNovel::getReadDate).reversed())
                .limit(6)
                .map(ReadNovel::getNovel)
                .map(NovelInfo::mapNovelToNovelInfo)
                .toList();
    }
}
