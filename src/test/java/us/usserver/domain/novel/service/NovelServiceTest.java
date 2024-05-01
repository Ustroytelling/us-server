package us.usserver.domain.novel.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.AuthorMother;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.entity.ReadNovel;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.chapter.ChapterMother;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.novel.NovelMother;
import us.usserver.domain.novel.constant.AgeRating;
import us.usserver.domain.novel.constant.Genre;
import us.usserver.domain.novel.constant.Hashtag;
import us.usserver.domain.novel.constant.NovelSize;
import us.usserver.domain.novel.dto.AuthorDescription;
import us.usserver.domain.novel.dto.MainNovelType;
import us.usserver.domain.novel.dto.NovelDetailInfo;
import us.usserver.domain.novel.dto.NovelInfo;
import us.usserver.domain.novel.dto.req.MoreNovelReq;
import us.usserver.domain.novel.dto.req.NovelBlueprint;
import us.usserver.domain.novel.dto.req.NovelSynopsis;
import us.usserver.domain.novel.dto.res.MainPageRes;
import us.usserver.domain.novel.dto.res.MoreNovelRes;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ExceptionMessage;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Rollback
@Transactional
@SpringBootTest
class NovelServiceTest {
    @Autowired
    private NovelService novelService;

    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ChapterRepository chapterRepository;

    private Novel novel;
    private Novel newNovel;
    private Member member;
    private Member newMember;
    private Author author;
    private Author newAuthor;

    @BeforeEach
    void setup() {
        member = MemberMother.generateMember();
        author = AuthorMother.generateAuthorWithMember(member);
        member.setAuthor(author);
        memberRepository.save(member);

        newMember = MemberMother.generateMember();
        newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);


        author.setMember(member);
        newAuthor.setMember(newMember);

        memberRepository.save(member);
        memberRepository.save(newMember);
        authorRepository.save(author);
        authorRepository.save(newAuthor);

        novel = NovelMother.generateNovel(author);
        newNovel = NovelMother.generateNovel(newAuthor);

        novelRepository.save(novel);
        novelRepository.save(newNovel);
    }

    @Test
    @DisplayName("소설 정보 확인")
    void getNovelInfo() {
        NovelInfo novelInfo = assertDoesNotThrow(
                () -> novelService.getNovelInfo(novel.getId()));

        assertThat(novelInfo.novelSharelUrl()).contains("");
    }

    @Test
    @DisplayName("존재하지 않는 소설 정보 확인")
    void getNotExistNovel() {
        // when
        BaseException baseException = assertThrows(BaseException.class,
                () -> novelService.getNovelInfo(novel.getId() + 9999));

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.NOVEL_NOT_FOUND);
    }

    @Test
    @DisplayName("소설 상세 정보 확인")
    void getNovelDetailInfo() {
        NovelDetailInfo novelDetailInfo = assertDoesNotThrow(
                () -> novelService.getNovelDetailInfo(novel.getId()));

        assertThat(novelDetailInfo.title()).isEqualTo(novel.getTitle());
        assertThat(novelDetailInfo.thumbnail()).isEqualTo(novel.getThumbnail());
        assertThat(novelDetailInfo.synopsis()).isEqualTo(novel.getSynopsis());
        assertThat(novelDetailInfo.authorName()).isEqualTo(novel.getMainAuthor().getNickname());
        assertThat(novelDetailInfo.authorIntroduction()).isEqualTo(novel.getAuthorDescription());
        assertThat(novelDetailInfo.ageRating()).isEqualTo(novel.getAgeRating());
        assertThat(novelDetailInfo.genre()).isEqualTo(novel.getGenre());
        assertThat(novelDetailInfo.hashtags()).isEqualTo(novel.getHashtags());
        assertThat(novelDetailInfo.stakeInfos()).isEqualTo(Collections.emptyList());
        assertThat(novelDetailInfo.chapterInfos()).isEqualTo(Collections.emptyList());
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
        novelRepository.save(novel);

        NovelDetailInfo novelDetailInfo = assertDoesNotThrow(
                () -> novelService.getNovelDetailInfo(novel.getId()));

        // then
        assertThat(novelDetailInfo.title()).isEqualTo(novel.getTitle());
        assertThat(novelDetailInfo.thumbnail()).isEqualTo(novel.getThumbnail());
        assertThat(novelDetailInfo.synopsis()).isEqualTo(novel.getSynopsis());
        assertThat(novelDetailInfo.authorName()).isEqualTo(novel.getMainAuthor().getNickname());
        assertThat(novelDetailInfo.authorIntroduction()).isEqualTo(novel.getAuthorDescription());
        assertThat(novelDetailInfo.ageRating()).isEqualTo(novel.getAgeRating());
        assertThat(novelDetailInfo.genre()).isEqualTo(novel.getGenre());
        assertThat(novelDetailInfo.hashtags()).isEqualTo(novel.getHashtags());
        assertThat(novelDetailInfo.stakeInfos()).isEqualTo(Collections.emptyList());
        assertThat(novelDetailInfo.chapterInfos().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("쇼셜 소개 수정")
    void modifyNovelSynopsis() {
        NovelSynopsis synopsisRequest = NovelSynopsis.builder().synopsis("소설 소개는 적당한 길이로 해야합니다.").build();
        String synopsisResponse = assertDoesNotThrow(
                () -> novelService.modifyNovelSynopsis(novel.getId(), author.getId(), synopsisRequest.synopsis()));

        assertThat(synopsisRequest.synopsis()).isEqualTo(synopsisResponse);
    }
    
    @Test
    @DisplayName("권한 없는 작가의 소설 소개 수정")
    void modifySysnopsisNotAuthority() {
        // given
        NovelSynopsis synopsisRequest = NovelSynopsis.builder().synopsis("소설 소개는 적당한 길이로 해야합니다.").build();

        // when
        BaseException baseException = assertThrows(BaseException.class,
                () -> novelService.modifyNovelSynopsis(novel.getId(), newAuthor.getId(), synopsisRequest.synopsis()));

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.MAIN_AUTHOR_NOT_MATCHED);

    }

    @Test
    @DisplayName("작가 소개 수정")
    void modifyAuthorDescription() {
        AuthorDescription descriptionRequest = AuthorDescription.builder().description("작가 소개 수정본").build();
        AuthorDescription desriptionResponse = assertDoesNotThrow(
                () -> novelService.modifyAuthorDescription(novel.getId(), author.getId(), descriptionRequest));

        assertThat(descriptionRequest.description()).isEqualTo(desriptionResponse.description());
    }

    @Test
    @DisplayName("권한 없는 작가의 작가 소개 수정")
    void modifyDescriptionNotAuthority() {
        // given
        AuthorDescription authorDescription = AuthorDescription.builder().description("작가 소개 수정본").build();


        // when
        BaseException baseException = assertThrows(BaseException.class,
                () -> novelService.modifyAuthorDescription(novel.getId(), newAuthor.getId(), authorDescription));

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.MAIN_AUTHOR_NOT_MATCHED);
    }


    @Test
    @DisplayName("소설 생성 성공")
    void create_novel_success() {
        //given
        NovelBlueprint novelBlueprint = NovelBlueprint.builder()
                .title("TEST TITLE")
                .thumbnail("TEST THUMBNAIL")
                .synopsis("TEST SYNOPSIS")
                .authorDescription("TEST AUTHORDESCRIPTION")
                .hashtag(Collections.singleton(Hashtag.판타지))
                .genre(Genre.판타지)
                .ageRating(AgeRating.GENERAL)
                .novelSize(NovelSize.LONG)
                .build();

        //when
        NovelInfo novelInfo = novelService.createNovel(member.getId(), novelBlueprint);

        //then
        assertThat(novelInfo).isNotNull();
        assertThat(novelInfo.title()).isEqualTo("TEST TITLE");
        assertThat(novelInfo.hashtag()).isEqualTo(Collections.singleton(Hashtag.판타지));
        assertThat(novelInfo.genre()).isEqualTo(Genre.판타지);
        assertThat(novelInfo.createdAuthor().id()).isEqualTo(author.getId());
        assertThat(novelInfo.joinedAuthorCnt()).isZero();
        assertThat(novelInfo.commentCnt()).isZero();
    }

    @Test
    @DisplayName("소설 생성 실패")
    void create_novel_fail() {
        //given
        Member newMember = MemberMother.generateMember();
        NovelBlueprint novelBlueprint = NovelBlueprint.builder()
                .title("TEST TITLE")
                .thumbnail("TEST THUMBNAIL")
                .synopsis("TEST SYNOPSIS")
                .authorDescription("TEST AUTHORDESCRIPTION")
                .hashtag(Collections.singleton(Hashtag.판타지))
                .genre(Genre.판타지)
                .ageRating(AgeRating.GENERAL)
                .novelSize(NovelSize.LONG)
                .build();

        //when then
        Assertions.assertThrows(BaseException.class,
                () -> novelService.createNovel(newMember.getId(), novelBlueprint));
        Assertions.assertThrows(BaseException.class,
                () -> novelService.createNovel(null, novelBlueprint));
    }

    /*
    @Test
    @DisplayName("소설 검색")
    void 소설검색_제목_성공() {
        //given
        SearchKeyword searchNovelReq1 = SearchKeyword.builder()
                .authorId(author.getId())
                .title("TITLE")
                .hashtag(null)
                .status(null)
                .lastNovelId(0L)
                .size(5)
                .sortDto(new SortDto(Sorts.HIT, Orders.DESC))
                .build();

        SearchKeyword searchNovelReq2 = SearchKeyword.builder()
                .authorId(author.getId())
                .title("TITLE")
                .hashtag(null)
                .status(null)
                .lastNovelId(0L)
                .size(5)
                .sortDto(new SortDto(Sorts.LATEST, Orders.ASC))
                .build();


        //when
        NovelPageInfoRes novelPageInfoResponse1 = novelServiceV0.searchNovel(searchNovelReq1);
        NovelPageInfoRes novelPageInfoResponse2 = novelServiceV0.searchNovel(searchNovelReq2);

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
        SearchKeyword searchNovelReq1 = SearchKeyword.builder()
                .authorId(author.getId())
                .title("TITLE")
                .hashtag(null)
                .status(null)
                .lastNovelId(0L)
                .size(5)
                .sortDto(new SortDto(Sorts.HIT, Orders.DESC))
                .build();

        SearchKeyword searchNovelReq2 = SearchKeyword.builder()
                .authorId(author.getId())
                .title("TITLE2")
                .hashtag(null)
                .status(null)
                .lastNovelId(0L)
                .size(5)
                .sortDto(new SortDto(Sorts.LATEST, Orders.ASC))
                .build();
        SearchKeyword searchNovelReq3 = SearchKeyword.builder()
                .authorId(author.getId())
                .title("TITLE")
                .hashtag(null)
                .status(null)
                .lastNovelId(0L)
                .size(5)
                .sortDto(new SortDto(Sorts.LATEST, Orders.ASC))
                .build();

        //when
        NovelPageInfoRes novelPageInfoResponse1 = novelServiceV0.searchNovel(searchNovelReq1);
        NovelPageInfoRes novelPageInfoResponse2 = novelServiceV0.searchNovel(searchNovelReq2);
        NovelPageInfoRes novelPageInfoResponse3 = novelServiceV0.searchNovel(searchNovelReq3);

        SearchPageRes searchKeywordResponse = novelServiceV0.searchKeyword();

        //then
        assertThat(searchKeywordResponse.getHotSearch().size()).isEqualTo(2);
        assertThat(searchKeywordResponse.getHotSearch().get(0)).isEqualTo("TITLE");
        assertThat(searchKeywordResponse.getHotSearch().get(1)).isEqualTo("TITLE2");

        assertThat(searchKeywordResponse.getRecentSearch().size()).isEqualTo(2);
        assertThat(searchKeywordResponse.getRecentSearch().get(0)).isEqualTo("TITLE");
        assertThat(searchKeywordResponse.getRecentSearch().get(1)).isEqualTo("TITLE2");
    } */

    @Test
    @DisplayName("메인 페이지 로드 테스트")
    void get_main_page() {
        // given
        Novel newNovel = NovelMother.generateNovel(author);
        for (int i=0; i<100; i++)
            newNovel.upHitCnt();
        Chapter newChapter = ChapterMother.generateChapter(novel);
        author.addReadNovel(ReadNovel.builder().author(author).novel(novel).readDate(LocalDateTime.now()).build());
        novel.addChapter(newChapter);

        //when
        authorRepository.save(author);
        novelRepository.save(newNovel);
        novelRepository.save(novel);
        chapterRepository.save(newChapter);
        MainPageRes mainPageRes = novelService.getMainPage(member.getId());

        //then
        assertThat(mainPageRes.readNovels().get(0).id()).isEqualTo(novel.getId());
        assertThat(mainPageRes.popularNovels().get(0).id()).isEqualTo(newNovel.getId());
        assertThat(mainPageRes.realTimeUpdateNovels().get(0).id()).isEqualTo(novel.getId());
        assertThat(mainPageRes.recentlyCreatedNovels().get(0).id()).isEqualTo(newNovel.getId());
    }

    @Test
    @DisplayName("신작 소설 더보기")
    void get_more_novel_NEW() {
        //given
        Novel novel1 = NovelMother.generateNovel(author);
        Novel novel2 = NovelMother.generateNovel(author);
        Novel novel3 = NovelMother.generateNovel(author);
        Novel novel4 = NovelMother.generateNovel(author);
        Novel novel5 = NovelMother.generateNovel(author);
        Novel novel6 = NovelMother.generateNovel(author);
        Novel novel7 = NovelMother.generateNovel(author);
        Novel novel8 = NovelMother.generateNovel(author);

        //when
        novelRepository.save(novel1);
        novelRepository.save(novel2);
        novelRepository.save(novel3);
        novelRepository.save(novel4);
        novelRepository.save(novel5);
        novelRepository.save(novel6);
        novelRepository.save(novel7);
        novelRepository.save(novel8);
        MoreNovelRes moreNovelRes1 = novelService.getMoreNovels(member.getId(), MainNovelType.NEW, 0);
        MoreNovelRes moreNovelRes2 = novelService.getMoreNovels(member.getId(), MainNovelType.NEW, 1);

        //then
        assertThat(moreNovelRes1.novelList().get(0).id()).isEqualTo(novel8.getId());
        assertThat(moreNovelRes1.novelList().get(5).id()).isEqualTo(novel3.getId());
        assertThat(moreNovelRes1.nextPage()).isOne();
        assertThat(moreNovelRes1.hasNext()).isTrue();
        assertThat(moreNovelRes1.novelList().size()).isEqualTo(6);

        assertThat(moreNovelRes2.nextPage()).isEqualTo(2);
        assertThat(moreNovelRes2.hasNext()).isFalse();
        assertThat(moreNovelRes2.novelList().size()).isEqualTo(4); // setup 메서드 2개 포함
    }

    @Test
    @DisplayName("인기 소설 더보기")
    void get_more_novel_POPULAR() {
        //given
        Novel novel1 = NovelMother.generateNovel(author);
        Novel novel2 = NovelMother.generateNovel(author);
        Novel novel3 = NovelMother.generateNovel(author);
        Novel novel4 = NovelMother.generateNovel(author);
        Novel novel5 = NovelMother.generateNovel(author);
        Novel novel6 = NovelMother.generateNovel(author);
        Novel novel7 = NovelMother.generateNovel(author);
        Novel novel8 = NovelMother.generateNovel(author);
        for (int i=0; i<6; i++) novel1.upHitCnt();
        for (int i=0; i<5; i++) novel2.upHitCnt();
        for (int i=0; i<4; i++) novel3.upHitCnt();
        for (int i=0; i<3; i++) novel4.upHitCnt();
        for (int i=0; i<2; i++) novel5.upHitCnt();
        for (int i=0; i<1; i++) novel6.upHitCnt();

        MoreNovelReq moreNovelReq1 = MoreNovelReq.builder()
                .mainNovelType(MainNovelType.POPULAR).nextPage(0).build();
        MoreNovelReq moreNovelReq2 = MoreNovelReq.builder()
                .mainNovelType(MainNovelType.POPULAR).nextPage(1).build();

        //when
        novelRepository.save(novel1);
        novelRepository.save(novel2);
        novelRepository.save(novel3);
        novelRepository.save(novel4);
        novelRepository.save(novel5);
        novelRepository.save(novel6);
        novelRepository.save(novel7);
        novelRepository.save(novel8);
        MoreNovelRes moreNovelRes1 = novelService.getMoreNovels(member.getId(), MainNovelType.POPULAR, 0);
        MoreNovelRes moreNovelRes2 = novelService.getMoreNovels(member.getId(), MainNovelType.POPULAR, 1);

        //then
        assertThat(moreNovelRes1.novelList().get(0).id()).isEqualTo(novel1.getId());
        assertThat(moreNovelRes1.novelList().get(1).id()).isEqualTo(novel2.getId());
        assertThat(moreNovelRes1.novelList().get(2).id()).isEqualTo(novel3.getId());
        assertThat(moreNovelRes1.novelList().get(3).id()).isEqualTo(novel4.getId());
        assertThat(moreNovelRes1.novelList().get(4).id()).isEqualTo(novel5.getId());
        assertThat(moreNovelRes1.novelList().get(5).id()).isEqualTo(novel6.getId());

        assertThat(moreNovelRes1.nextPage()).isOne();
        assertThat(moreNovelRes1.hasNext()).isTrue();
        assertThat(moreNovelRes1.novelList().size()).isEqualTo(6);

        assertThat(moreNovelRes2.nextPage()).isEqualTo(2);
        assertThat(moreNovelRes2.hasNext()).isFalse();
    }

    @Test
    @DisplayName("실시간 업데이트 소설 더보기")
    void get_more_novel_UPDATE() {
        //given
        Novel novel1 = NovelMother.generateNovel(author);
        Novel novel2 = NovelMother.generateNovel(author);
        Novel novel3 = NovelMother.generateNovel(author);
        Novel novel4 = NovelMother.generateNovel(author);
        Novel novel5 = NovelMother.generateNovel(author);
        Novel novel6 = NovelMother.generateNovel(author);
        Novel novel7 = NovelMother.generateNovel(author);
        Novel novel8 = NovelMother.generateNovel(author);
        Chapter chapter1 = ChapterMother.generateChapter(novel1);
        novel1.addChapter(chapter1);
        novel2.addChapter(chapter1);
        novel3.addChapter(chapter1);
        novel4.addChapter(chapter1);
        novel5.addChapter(chapter1);
        novel6.addChapter(chapter1);

        MoreNovelReq moreNovelReq1 = MoreNovelReq.builder()
                .mainNovelType(MainNovelType.UPDATE).nextPage(0).build();
        MoreNovelReq moreNovelReq2 = MoreNovelReq.builder()
                .mainNovelType(MainNovelType.UPDATE).nextPage(1).build();

        //when
        novelRepository.save(novel1);
        novelRepository.save(novel2);
        novelRepository.save(novel3);
        novelRepository.save(novel4);
        novelRepository.save(novel5);
        novelRepository.save(novel6);
        novelRepository.save(novel7);
        novelRepository.save(novel8);
        chapterRepository.save(chapter1);
        MoreNovelRes moreNovelRes1 = novelService.getMoreNovels(member.getId(), MainNovelType.UPDATE, 0);
        MoreNovelRes moreNovelRes2 = novelService.getMoreNovels(member.getId(), MainNovelType.UPDATE, 1);

        //then
        moreNovelRes1.novelList()
                        .forEach(novelInfo ->
                                assertThat(novelInfo.id()).isIn(
                                        novel6.getId(),
                                        novel5.getId(),
                                        novel4.getId(),
                                        novel3.getId(),
                                        novel2.getId(),
                                        novel1.getId()
                                )
                        );
        assertThat(moreNovelRes1.nextPage()).isOne();
        assertThat(moreNovelRes1.hasNext()).isTrue();
        assertThat(moreNovelRes1.novelList().size()).isEqualTo(6);

        assertThat(moreNovelRes2.nextPage()).isEqualTo(2);
        assertThat(moreNovelRes2.hasNext()).isFalse();
    }
}