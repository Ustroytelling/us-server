package us.usserver.domain.paragraph.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.repository.AuthorityRepository;
import us.usserver.domain.authority.service.StakeService;
import us.usserver.domain.chapter.dto.ChapterStatus;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.domain.paragraph.dto.ParagraphInVoting;
import us.usserver.domain.paragraph.dto.ParagraphSelected;
import us.usserver.domain.paragraph.dto.ParagraphsOfChapter;
import us.usserver.domain.paragraph.dto.req.PostParagraphReq;
import us.usserver.domain.paragraph.dto.res.GetParagraphResponse;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.repository.ParagraphLikeRepository;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.domain.paragraph.repository.VoteRepository;
import us.usserver.global.EntityFacade;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ParagraphServiceImpl implements ParagraphService {
    private final EntityFacade entityFacade;
    private final StakeService stakeService;

    private final ParagraphRepository paragraphRepository;
    private final VoteRepository voteRepository;
    private final AuthorityRepository authorityRepository;
    private final ParagraphLikeRepository paragraphLikeRepository;


    @Override
    public ParagraphsOfChapter getParagraphs(Long authorId, Long chapterId) {
        Author author = entityFacade.getAuthor(authorId);
        Chapter chapter = entityFacade.getChapter(chapterId);

        List<Paragraph> paragraphs = paragraphRepository.findAllByChapter(chapter);
        if (paragraphs.isEmpty()) {
            return getInitialChParagraph();
        } else if (chapter.getStatus() == ChapterStatus.COMPLETED) {
            return getCompletedChParagraph(paragraphs, author);
        } else {
            return getInProgressChParagraph(paragraphs, author);
        }
    }

    @Override
    public GetParagraphResponse getInVotingParagraphs(Long memberId, Long chapterId) {
        Chapter chapter = entityFacade.getChapter(chapterId);
        Author author = entityFacade.getAuthorByMemberId(memberId);
        List<Paragraph> paragraphs = paragraphRepository.findAllByChapter(chapter);

        List<ParagraphInVoting> paragraphInVotings = paragraphs.stream().filter(paragraph -> paragraph.getParagraphStatus().equals(ParagraphStatus.IN_VOTING))
                .map(paragraph -> ParagraphInVoting.fromParagraph(
                        paragraph,
                        voteRepository.countAllByParagraph(paragraph),
                        voteRepository.existsByParagraphAndAuthor(paragraph, author))) // TODO: 이렇게 하는거보다 그냥 내가 투표한 한줄을 찾는 쿼리 딱 한줄만 보내는게 나을듯
                .toList();

        return GetParagraphResponse.builder().paragraphInVotings(paragraphInVotings).build();
    }

    @Override
    public ParagraphInVoting postParagraph(Long memberId, Long chapterId, PostParagraphReq req) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        Chapter chapter = entityFacade.getChapter(chapterId);
        int nextChapterCnt = paragraphRepository.countParagraphsByChapter(chapter) + 1;

        if (req.content().length() > 300 || req.content().length() < 50) {
            throw new BaseException(ErrorCode.PARAGRAPH_LENGTH_OUT_OF_RANGE);
        }

        Paragraph paragraph = paragraphRepository.save(
                Paragraph.builder()
                        .content(req.content())
                        .sequence(nextChapterCnt)
                        .paragraphStatus(ParagraphStatus.IN_VOTING)
                        .chapter(chapter)
                        .author(author)
                        .build()
        );

        chapter.getParagraphs().add(paragraph);
        return ParagraphInVoting.builder()
                .content(paragraph.getContent())
                .sequence(paragraph.getSequence())
                .voteCnt(0)
                .status(paragraph.getParagraphStatus())
                .authorName(paragraph.getAuthor().getNickname())
                .createdAt(paragraph.getCreatedAt())
                .updatedAt(paragraph.getUpdatedAt())
                .build();
    }

    @Override
    public void selectParagraph(Long memberId, Long novelId, Long chapterId, Long paragraphId) {
        Novel novel = entityFacade.getNovel(novelId);
        Chapter chapter = entityFacade.getChapter(chapterId);
        Paragraph paragraph = entityFacade.getParagraph(paragraphId);
        Author author = entityFacade.getAuthorByMemberId(memberId);

        if (!novel.getMainAuthor().getId().equals(author.getId())) {
            throw new BaseException(ErrorCode.MAIN_AUTHOR_NOT_MATCHED);
        }
        if (!novel.getChapters().contains(chapter)) {
            throw new BaseException(ErrorCode.CHAPTER_NOT_FOUND);
        }
        if (!chapter.getParagraphs().contains(paragraph)) {
            throw new BaseException(ErrorCode.PARAGRAPH_NOT_FOUND);
        }

        addAuthority(author, novel);

        paragraph.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        stakeService.setStakeInfoOfNovel(novel);

        // 선택 되지 않은 paragraph 들의 status 변경
        List<Paragraph> paragraphs = paragraphRepository.findAllByChapter(chapter);
        for (Paragraph p : paragraphs) {
            if (p.getParagraphStatus() == ParagraphStatus.IN_VOTING) {
                p.setParagraphStatusForTest(ParagraphStatus.UNSELECTED);
            }
        }
    }

    @Override
    public void reportParagraph(Long memberId, Long paragraphId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        Paragraph paragraph = entityFacade.getParagraph(paragraphId);
    }

    private ParagraphsOfChapter getInitialChParagraph() {
        return ParagraphsOfChapter.builder()
                .selectedParagraphs(Collections.emptyList())
                .myParagraph(null)
                .bestParagraph(null)
                .build();
    }

    private ParagraphsOfChapter getCompletedChParagraph(List<Paragraph> paragraphs, Author author) {
        List<ParagraphSelected> selectedParagraphs = paragraphs.stream()
                .filter(paragraph -> paragraph.getParagraphStatus() == ParagraphStatus.SELECTED)
                .map(paragraph -> ParagraphSelected.fromParagraph(paragraph, isLikedParagraph(paragraph, author)))
                .toList();
        return ParagraphsOfChapter.builder()
                .selectedParagraphs(selectedParagraphs)
                .myParagraph(null)
                .bestParagraph(null)
                .build();
    }

    private ParagraphsOfChapter getInProgressChParagraph(List<Paragraph> paragraphs, Author author) {
        List<ParagraphSelected> selectedParagraphs = new ArrayList<>();
        ParagraphInVoting myParagraph = null, bestParagraph = null;

        int maxVoteCnt = -1, voteCnt;
        for (Paragraph paragraph : paragraphs) {
            ParagraphStatus status = paragraph.getParagraphStatus();
            voteCnt = voteRepository.countAllByParagraph(paragraph);

            if (status == ParagraphStatus.IN_VOTING && // 내가 쓴 한줄
                            paragraph.getAuthor().getId().equals(author.getId())) {
                myParagraph = ParagraphInVoting.fromParagraph(paragraph, voteCnt, voteRepository.existsByParagraphAndAuthor(paragraph, author));
            }
            if (status == ParagraphStatus.IN_VOTING && // 베스트 한줄
                            voteCnt > maxVoteCnt) {
                bestParagraph = ParagraphInVoting.fromParagraph(paragraph, voteCnt, voteRepository.existsByParagraphAndAuthor(paragraph, author));
                maxVoteCnt = voteCnt;
            }
            if (status == ParagraphStatus.SELECTED) { // 이미 선정된 한줄
                selectedParagraphs.add(ParagraphSelected.fromParagraph(paragraph, isLikedParagraph(paragraph, author)));
            }
        }

        return ParagraphsOfChapter.builder()
                .selectedParagraphs(selectedParagraphs)
                .myParagraph(myParagraph)
                .bestParagraph(bestParagraph)
                .build();
    }

    private void addAuthority(Author author, Novel novel) {
        List<Authority> authorities = authorityRepository.findAllByAuthor(author);
        boolean isAuthorized = authorities.stream().anyMatch(authority -> Objects.equals(authority.getNovel().getId(), novel.getId()));

        if (!isAuthorized) {
            Authority authority = new Authority();
            author.addAuthority(authority);
            authority.setAuthor(author);
            authority.takeNovel(novel);
            authorityRepository.save(authority);
        }
    }

    private boolean isLikedParagraph(Paragraph paragraph, Author author) {
        return paragraphLikeRepository.existsByParagraphAndAuthor(paragraph, author);
    }
}
