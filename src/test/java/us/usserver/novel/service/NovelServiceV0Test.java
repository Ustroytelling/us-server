package us.usserver.novel.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Rollback;
import us.usserver.author.Author;
import us.usserver.author.AuthorMother;
import us.usserver.author.AuthorRepository;
import us.usserver.chapter.Chapter;
import us.usserver.chapter.ChapterMother;
import us.usserver.chapter.ChapterRepository;
import us.usserver.global.exception.MainAuthorIsNotMatchedException;
import us.usserver.global.exception.NovelNotFoundException;
import us.usserver.member.Member;
import us.usserver.member.MemberMother;
import us.usserver.member.MemberRepository;
import us.usserver.novel.Novel;
import us.usserver.novel.NovelMother;
import us.usserver.novel.repository.NovelJpaRepository;
import us.usserver.novel.dto.*;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Rollback
@Transactional
@SpringBootTest
class NovelServiceV0Test {
    @Autowired
    private NovelJpaRepository novelJpaRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private NovelServiceV0 novelServiceV0;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private Novel novel;
    private Novel dummyNovel;
    private Author author;
    private Author dummyAuthor;

    @BeforeEach
    void setup() {
        Member member1 = MemberMother.generateMember();
        Member member2 = MemberMother.generateMember();
        author = AuthorMother.generateAuthor();
        dummyAuthor = AuthorMother.generateAuthor();

        author.setMember(member1);
        dummyAuthor.setMember(member2);


        memberRepository.save(member1);
        memberRepository.save(member2);
        authorRepository.save(author);
        authorRepository.save(dummyAuthor);

        novel = NovelMother.generateNovel(author);
        dummyNovel = NovelMother.generateNovel(dummyAuthor);

        novelJpaRepository.save(novel);
        novelJpaRepository.save(dummyNovel);

    }

    @Test
    @DisplayName("소설 정보 확인")
    void getNovelInfo() {
        NovelInfo novelInfo = assertDoesNotThrow(
                () -> novelServiceV0.getNovelInfo(novel.getId()));

        assertThat(novelInfo.getNovelSharelUrl()).contains("/novel/" + novel.getId());
    }

    @Test
    @DisplayName("존재하지 않는 소설 정보 확인")
    void getNotExistNovel() {
        assertThrows(NovelNotFoundException.class,
                () -> novelServiceV0.getNovelInfo(novel.getId() + 9999));
    }

    @Test
    @DisplayName("소설 상세 정보 확인")
    void getNovelDetailInfo() {
        NovelDetailInfo novelDetailInfo = assertDoesNotThrow(
                () -> novelServiceV0.getNovelDetailInfo(novel.getId()));

        assertThat(novelDetailInfo.getTitle()).isEqualTo(novel.getTitle());
        assertThat(novelDetailInfo.getThumbnail()).isEqualTo(novel.getThumbnail());
        assertThat(novelDetailInfo.getSynopsis()).isEqualTo(novel.getSynopsis());
        assertThat(novelDetailInfo.getAuthorName()).isEqualTo(novel.getMainAuthor().getNickname());
        assertThat(novelDetailInfo.getAuthorIntroduction()).isEqualTo(novel.getAuthorDescription());
        assertThat(novelDetailInfo.getAgeRating()).isEqualTo(novel.getAgeRating());
        assertThat(novelDetailInfo.getGenre()).isEqualTo(novel.getGenre());
        assertThat(novelDetailInfo.getHashtags()).isEqualTo(novel.getHashtags());
        assertThat(novelDetailInfo.getStakeInfos()).isEqualTo(Collections.emptyList());
        assertThat(novelDetailInfo.getChapterInfos()).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("소설 상세 정보 확인 - Chapter 정보 추가")
    void getNovelDetailInfo2() {
        // given
        Chapter chapter1 = ChapterMother.generateChapter(novel);
        Chapter chapter2 = ChapterMother.generateChapter(novel);

        // when
        chapterRepository.save(chapter1);
        chapterRepository.save(chapter2);
        novel.getChapters().add(chapter1);
        novel.getChapters().add(chapter2);
        novelJpaRepository.save(novel);

        NovelDetailInfo novelDetailInfo = assertDoesNotThrow(
                () -> novelServiceV0.getNovelDetailInfo(novel.getId()));

        // then
        assertThat(novelDetailInfo.getTitle()).isEqualTo(novel.getTitle());
        assertThat(novelDetailInfo.getThumbnail()).isEqualTo(novel.getThumbnail());
        assertThat(novelDetailInfo.getSynopsis()).isEqualTo(novel.getSynopsis());
        assertThat(novelDetailInfo.getAuthorName()).isEqualTo(novel.getMainAuthor().getNickname());
        assertThat(novelDetailInfo.getAuthorIntroduction()).isEqualTo(novel.getAuthorDescription());
        assertThat(novelDetailInfo.getAgeRating()).isEqualTo(novel.getAgeRating());
        assertThat(novelDetailInfo.getGenre()).isEqualTo(novel.getGenre());
        assertThat(novelDetailInfo.getHashtags()).isEqualTo(novel.getHashtags());
        assertThat(novelDetailInfo.getStakeInfos()).isEqualTo(Collections.emptyList());
        assertThat(novelDetailInfo.getChapterInfos().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("쇼셜 소개 수정")
    void modifyNovelSynopsis() {
        NovelSynopsis synopsisRequest = NovelMother.generateSysnopsis();
        String synopsisResponse = assertDoesNotThrow(
                () -> novelServiceV0.modifyNovelSynopsis(novel.getId(), author.getId(), synopsisRequest.getSynopsis()));

        assertThat(synopsisRequest.getSynopsis()).isEqualTo(synopsisResponse);
    }
    
    @Test
    @DisplayName("권한 없는 작가의 소설 소개 수정")
    void modifySysnopsisNotAuthority() {
        NovelSynopsis synopsisRequest = NovelMother.generateSysnopsis();
        assertThrows(MainAuthorIsNotMatchedException.class,
                () -> novelServiceV0.modifyNovelSynopsis(novel.getId(), dummyAuthor.getId(), synopsisRequest.getSynopsis()));
    }

    @Test
    @DisplayName("작가 소개 수정")
    void modifyAuthorDescription() {
        AuthorDescription descriptionRequest = NovelMother.generateDescription();
        AuthorDescription desriptionResponse = assertDoesNotThrow(
                () -> novelServiceV0.modifyAuthorDescription(novel.getId(), author.getId(), descriptionRequest));

        assertThat(descriptionRequest.getDescription()).isEqualTo(desriptionResponse.getDescription());
    }

    @Test
    @DisplayName("권한 없는 작가의 작가 소개 수정")
    void modifyDescriptionNotAuthority() {
        AuthorDescription authorDescription = NovelMother.generateDescription();
        assertThrows(MainAuthorIsNotMatchedException.class,
                () -> novelServiceV0.modifyAuthorDescription(novel.getId(), dummyAuthor.getId(), authorDescription));

    }


    /*
    @Test
    @DisplayName("소설 생성")
    void 소설생성_성공() {
        //given
        CreateNovelReq createNovelReq = CreateNovelReq.builder()
                .title("TEST TITLE")
                .thumbnail("TEST THUMBNAIL")
                .synopsis("TEST SYNOPSIS")
                .authorDescription("TEST AUTHORDESCRIPTION")
                .hashtag(Collections.singleton(Hashtag.HASHTAG1))
                .genre(Genre.FANTASY)
                .ageRating(AgeRating.GENERAL)
                .novelSize(NovelSize.LONG)
                .build();
        //when
        Novel createNovel = novelServiceV0.createNovel(createNovelReq);

        //then
        assertThat(createNovel).isNotNull();
        assertThat(createNovel.getTitle()).isEqualTo("TEST TITLE");
        assertThat(createNovel.getThumbnail()).isEqualTo("TEST THUMBNAIL");
        assertThat(createNovel.getSynopsis()).isEqualTo("TEST SYNOPSIS");
        assertThat(createNovel.getAuthorDescription()).isEqualTo("TEST AUTHORDESCRIPTION");
        assertThat(createNovel.getHashtags()).isEqualTo(Collections.singleton(Hashtag.HASHTAG1));
        assertThat(createNovel.getGenre()).isEqualTo(Genre.FANTASY);
        assertThat(createNovel.getAgeRating()).isEqualTo(AgeRating.GENERAL);
        assertThat(createNovel.getNovelSize()).isEqualTo(NovelSize.LONG);
    }

    @Test
    @DisplayName("소설 검색")
    void 소설검색_제목_성공() {
        //given
        SearchNovelReq searchNovelReq1 = SearchNovelReq.builder()
                .authorId(author.getId())
                .title("TITLE")
                .hashtag(null)
                .status(null)
                .lastNovelId(0L)
                .size(5)
                .sortDto(new SortDto(Sorts.HIT, Orders.DESC))
                .build();

        SearchNovelReq searchNovelReq2 = SearchNovelReq.builder()
                .authorId(author.getId())
                .title("TITLE")
                .hashtag(null)
                .status(null)
                .lastNovelId(0L)
                .size(5)
                .sortDto(new SortDto(Sorts.LATEST, Orders.ASC))
                .build();


        //when
        NovelPageInfoResponse novelPageInfoResponse1 = novelServiceV0.searchNovel(searchNovelReq1);
        NovelPageInfoResponse novelPageInfoResponse2 = novelServiceV0.searchNovel(searchNovelReq2);

        //then
        assertThat(novelPageInfoResponse1.getNovelList().size()).isEqualTo(2);
        assertThat(novelPageInfoResponse1.getNovelList().get(0).getTitle()).isEqualTo("TITLE2");
        assertThat(novelPageInfoResponse1.getNovelList().get(1).getTitle()).isEqualTo("TITLE");

        assertThat(novelPageInfoResponse2.getNovelList().size()).isEqualTo(2);
        assertThat(novelPageInfoResponse2.getNovelList().get(0).getTitle()).isEqualTo("TITLE");
        assertThat(novelPageInfoResponse2.getNovelList().get(1).getTitle()).isEqualTo("TITLE2");
    }

    @Test
    @DisplayName("검색 키워드")
    void 소설검색_키워드_성공() {
        //given
        SearchNovelReq searchNovelReq1 = SearchNovelReq.builder()
                .authorId(author.getId())
                .title("TITLE")
                .hashtag(null)
                .status(null)
                .lastNovelId(0L)
                .size(5)
                .sortDto(new SortDto(Sorts.HIT, Orders.DESC))
                .build();

        SearchNovelReq searchNovelReq2 = SearchNovelReq.builder()
                .authorId(author.getId())
                .title("TITLE2")
                .hashtag(null)
                .status(null)
                .lastNovelId(0L)
                .size(5)
                .sortDto(new SortDto(Sorts.LATEST, Orders.ASC))
                .build();
        SearchNovelReq searchNovelReq3 = SearchNovelReq.builder()
                .authorId(author.getId())
                .title("TITLE")
                .hashtag(null)
                .status(null)
                .lastNovelId(0L)
                .size(5)
                .sortDto(new SortDto(Sorts.LATEST, Orders.ASC))
                .build();

        //when
        NovelPageInfoResponse novelPageInfoResponse1 = novelServiceV0.searchNovel(searchNovelReq1);
        NovelPageInfoResponse novelPageInfoResponse2 = novelServiceV0.searchNovel(searchNovelReq2);
        NovelPageInfoResponse novelPageInfoResponse3 = novelServiceV0.searchNovel(searchNovelReq3);

        SearchKeywordResponse searchKeywordResponse = novelServiceV0.searchKeyword();

        //then
        assertThat(searchKeywordResponse.getHotSearch().size()).isEqualTo(2);
        assertThat(searchKeywordResponse.getHotSearch().get(0)).isEqualTo("TITLE");
        assertThat(searchKeywordResponse.getHotSearch().get(1)).isEqualTo("TITLE2");

        assertThat(searchKeywordResponse.getRecentSearch().size()).isEqualTo(2);
        assertThat(searchKeywordResponse.getRecentSearch().get(0)).isEqualTo("TITLE");
        assertThat(searchKeywordResponse.getRecentSearch().get(1)).isEqualTo("TITLE2");
    }

    @Test
    @DisplayName("메인화면 소설 List")
    @Transactional
    void 메인_소설_성공() {
        //when
        HomeNovelListResponse homeNovelListResponse = novelServiceV0.homeNovelInfo();

        //then
        assertThat(homeNovelListResponse.getRealTimeNovels().size()).isEqualTo(3);
        assertThat(homeNovelListResponse.getRealTimeNovels().get(0).getTitle()).isEqualTo("TITLE3");

        assertThat(homeNovelListResponse.getNewNovels().size()).isEqualTo(3);
        assertThat(homeNovelListResponse.getNewNovels().get(0).getTitle()).isEqualTo("TITLE3");
        assertThat(homeNovelListResponse.getNewNovels().get(1).getTitle()).isEqualTo("TITLE2");

        assertThat(homeNovelListResponse.getReadNovels().size()).isEqualTo(2);
        assertThat(homeNovelListResponse.getReadNovels().get(0).getTitle()).isEqualTo("TITLE");
        assertThat(homeNovelListResponse.getReadNovels().get(1).getTitle()).isEqualTo("TITLE2");
    }

    @Test
    @DisplayName("메인화면 소설 더보기")
    @Transactional
    void 메인_소설_더보기_실시간업데이트_성공() {
        //given
        HomeNovelListResponse homeNovelListResponse = novelServiceV0.homeNovelInfo();
        Novel realTimeNovel = homeNovelListResponse.getRealTimeNovels().get(homeNovelListResponse.getRealTimeNovels().size() - 1);
        ConditionsOfPagination moreInfoOfNovel = ConditionsOfPagination.builder()
                .lastNovelId(realTimeNovel.getId())
                .size(3)
                .sortDto(SortDto.builder().sorts(Sorts.LATEST).orders(Orders.DESC).build())
                .build();
        //when
        NovelPageInfoResponse novelPageInfoResponse = novelServiceV0.moreNovel(moreInfoOfNovel);

        //then
        assertThat(novelPageInfoResponse.getNovelList().size()).isEqualTo(2);
        assertThat(novelPageInfoResponse.getHasNext()).isFalse();
        assertThat(novelPageInfoResponse.getSorts()).isEqualTo(Sorts.LATEST);
    }

    @Test
    @DisplayName("메인화면 소설 더보기")
    void 메인_소설_더보기_신작_성공() {
        //given
        HomeNovelListResponse homeNovelListResponse = novelServiceV0.homeNovelInfo();
        Novel newNovel = homeNovelListResponse.getNewNovels().get(homeNovelListResponse.getNewNovels().size() - 1);
        ConditionsOfPagination moreInfoOfNovel = ConditionsOfPagination.builder()
                .lastNovelId(newNovel.getId())
                .size(3)
                .sortDto(SortDto.builder().sorts(Sorts.NEW).orders(Orders.DESC).build())
                .build();
        //when
        NovelPageInfoResponse novelPageInfoResponse = novelServiceV0.moreNovel(moreInfoOfNovel);

        //then
        assertThat(novelPageInfoResponse.getNovelList().size()).isEqualTo(2);
        assertThat(novelPageInfoResponse.getHasNext()).isFalse();
        assertThat(novelPageInfoResponse.getSorts()).isEqualTo(Sorts.NEW);
    }

    @Test
    @DisplayName("메인화면 소설 더보기")
    @Transactional
    void 메인_소설_더보기_읽은소설_성공() {
        //given
        ReadInfoOfNovel readInfoOfNovel = ReadInfoOfNovel.builder()
                .getNovelSize(0)
                .size(3)
                .build();
        //when
        NovelPageInfoResponse novelPageInfoResponse = novelServiceV0.readMoreNovel(readInfoOfNovel);

        //then
        assertThat(novelPageInfoResponse.getNovelList().size()).isEqualTo(2);
        assertThat(novelPageInfoResponse.getNovelList().get(0).getId()).isEqualTo(1L);
        assertThat(novelPageInfoResponse.getHasNext()).isFalse();
    }
    */
}