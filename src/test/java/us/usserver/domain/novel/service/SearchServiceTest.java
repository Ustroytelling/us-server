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
import us.usserver.domain.novel.dto.*;
import us.usserver.domain.novel.dto.req.MoreNovelReq;
import us.usserver.domain.novel.dto.req.NovelBlueprint;
import us.usserver.domain.novel.dto.req.NovelSynopsis;
import us.usserver.domain.novel.dto.req.SearchKeyword;
import us.usserver.domain.novel.dto.res.MainPageRes;
import us.usserver.domain.novel.dto.res.MoreNovelRes;
import us.usserver.domain.novel.dto.res.SearchNovelRes;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ExceptionMessage;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Rollback
@Transactional
@SpringBootTest
class SearchServiceTest {
    @Autowired
    private NovelService novelService;

    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private MemberRepository memberRepository;

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
    @DisplayName("소설 검색 TEST")
    void getNovelInfo_1() {
        // given
        SearchKeyword searchKeyword = SearchKeyword.builder().keyword(novel.getTitle().substring(1)).nextPage(0).build();

        // when
        SearchNovelRes searchNovelRes = novelService.searchNovel(member.getId(), searchKeyword);

        // then
        assertThat(searchNovelRes.novelSimpleInfos().size()).isNotZero();
    }

    @Test
    @DisplayName("소설 검색 페이징(By 제목) TEST")
    void getNovelInfo_2() {
        // given
        Novel novel1 = NovelMother.generateNovel(author);
        Novel novel2 = NovelMother.generateNovel(author);
        Novel novel3 = NovelMother.generateNovel(author);
        Novel novel4 = NovelMother.generateNovel(author);
        Novel novel5 = NovelMother.generateNovel(author);
        Novel novel6 = NovelMother.generateNovel(author);
        Novel novel7 = NovelMother.generateNovel(author);
        Novel novel8 = NovelMother.generateNovel(author);
        Novel novel9 = NovelMother.generateNovel(author);
        Novel novel10 = NovelMother.generateNovel(author);
        Novel novel11 = NovelMother.generateNovel(author);
        Novel novel12 = NovelMother.generateNovel(author);
        Novel novel13 = NovelMother.generateNovel(author);
        novel1.setTitleForTest("전생회전");
        novel2.setTitleForTest("전생회귀");
        novel3.setTitleForTest("나혼자만 전생자");
        novel4.setTitleForTest("전생하는 법");
        novel5.setTitleForTest("나루토");
        novel6.setTitleForTest("게임 속 바바리안으로 살아남기");
        novel7.setTitleForTest("전생 배드로");
        novel8.setTitleForTest("광마 회귀");
        novel9.setTitleForTest("화산 귀환");
        novel10.setTitleForTest("화산 전생");
        novel11.setTitleForTest("전생 공주");
        novel12.setTitleForTest("전생 왕자");

        SearchKeyword searchKeyword1 = SearchKeyword.builder().keyword("전생").nextPage(0).build();
        SearchKeyword searchKeyword2 = SearchKeyword.builder().keyword("전생").nextPage(1).build();

        // when
        novelRepository.save(novel1);
        novelRepository.save(novel2);
        novelRepository.save(novel3);
        novelRepository.save(novel4);
        novelRepository.save(novel5);
        novelRepository.save(novel6);
        novelRepository.save(novel7);
        novelRepository.save(novel8);
        novelRepository.save(novel9);
        novelRepository.save(novel10);
        novelRepository.save(novel11);
        novelRepository.save(novel12);
        novelRepository.save(novel13);
        SearchNovelRes searchNovelRes1 = novelService.searchNovel(member.getId(), searchKeyword1);
        SearchNovelRes searchNovelRes2 = novelService.searchNovel(member.getId(), searchKeyword2);

        // then
        for (NovelSimpleInfo novelSimpleInfo : searchNovelRes1.novelSimpleInfos()) {
            if (!novelSimpleInfo.title().contains("전생") && !novelSimpleInfo.createdAuthor().contains("전생"))
                fail();
        }
        for (NovelSimpleInfo novelSimpleInfo : searchNovelRes2.novelSimpleInfos()) {
            if (!novelSimpleInfo.title().contains("전생") && !novelSimpleInfo.createdAuthor().contains("전생"))
                fail();
        }
    }

    @Test
    @DisplayName("소설 검색 페이징(By 작가 이름) TEST")
    void getNovelInfo_3() {
        // given
        author.setNicknameForTest("소설킹");
        author = authorRepository.save(author);
        Novel novel1 = NovelMother.generateNovel(author);
        Novel novel2 = NovelMother.generateNovel(author);
        Novel novel3 = NovelMother.generateNovel(author);
        Novel novel4 = NovelMother.generateNovel(author);
        Novel novel5 = NovelMother.generateNovel(author);
        Novel novel6 = NovelMother.generateNovel(author);
        Novel novel7 = NovelMother.generateNovel(author);

        SearchKeyword searchKeyword1 = SearchKeyword.builder().keyword("소설").nextPage(0).build();
        SearchKeyword searchKeyword2 = SearchKeyword.builder().keyword("소설").nextPage(1).build();

        // when
        novelRepository.save(novel1);
        novelRepository.save(novel2);
        novelRepository.save(novel3);
        novelRepository.save(novel4);
        novelRepository.save(novel5);
        novelRepository.save(novel6);
        novelRepository.save(novel7);
        SearchNovelRes searchNovelRes1 = novelService.searchNovel(member.getId(), searchKeyword1);
        SearchNovelRes searchNovelRes2 = novelService.searchNovel(member.getId(), searchKeyword2);

        // then
        assertThat(searchNovelRes1.novelSimpleInfos().size()).isEqualTo(6);
        assertThat(searchNovelRes2.novelSimpleInfos().size()).isOne();
    }
}