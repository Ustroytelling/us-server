package us.usserver.domain.chapter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.entity.ReadNovel;
import us.usserver.domain.chapter.dto.ChapterStatus;
import us.usserver.domain.chapter.dto.ChapterDetailInfo;
import us.usserver.domain.chapter.dto.ChapterInfo;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.chapter.repository.ScoreRepository;
import us.usserver.domain.comment.dto.CommentInfo;
import us.usserver.domain.comment.entity.Comment;
import us.usserver.domain.comment.repository.CommentRepository;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.paragraph.dto.ParagraphsOfChapter;
import us.usserver.domain.paragraph.service.ParagraphService;
import us.usserver.global.EntityFacade;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;
import us.usserver.global.response.exception.ExceptionMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service

@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {
    private final EntityFacade entityFacade;
    private final ParagraphService paragraphService;

    private final ChapterRepository chapterRepository;
    private final CommentRepository commentRepository;
    private final ScoreRepository scoreRepository;

    @Override
    @Transactional
    public List<ChapterInfo> getChaptersOfNovel(Novel novel) {
        List<Chapter> chapters = chapterRepository.findAllByNovelOrderByPart(novel);

        return chapters.stream()
                .map(ChapterInfo::fromChapter)
                .toList();
    }

    @Override
    @Transactional
    public ChapterDetailInfo getChapterDetailInfo(Long novelId, Long memberId, Long chapterId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        Chapter chapter = entityFacade.getChapter(chapterId);
        Novel novel = entityFacade.getNovel(novelId);
        ParagraphsOfChapter paragraphs = paragraphService.getParagraphs(author.getId(), chapterId);

        List<Chapter> chapters = chapterRepository.findAllByNovelOrderByPart(novel);
        Integer commentCnt = commentRepository.countAllByChapter(chapter);
        List<Comment> comments = commentRepository.getTop3CommentOfChapter(chapter);
        List<CommentInfo> commentInfos = comments.stream().map(comment -> CommentInfo.fromComment(comment, chapter.getTitle(), comment.getCommentLikes().size(), memberId)).toList();
        Double score = scoreRepository.findAverageScoreByChapter(chapter);

        Integer part = chapter.getPart();
        Integer prevPart = part - 1, nextPart = part + 1;
        if (part == 1) {
            prevPart = null;
        }
        if (part == chapters.size()) {
            nextPart = null;
        }
        if (score == null) {
            score = 0.0;
        }

        novel.upHitCnt();
        author.addReadNovel(ReadNovel.builder().author(author).novel(novel).readDate(LocalDateTime.now()).build()); // Set 이라 중복 검사를 하지 않아도 됨
        return ChapterDetailInfo.builder()
                .part(part)
                .title(chapter.getTitle())
                .status(chapter.getStatus())
                .score(score)
                .myParagraph(paragraphs.getMyParagraph())
                .bestParagraph(paragraphs.getBestParagraph())
                .selectedParagraphs(paragraphs.getSelectedParagraphs())
                .prevPart(prevPart)
                .nextPart(nextPart)
                .commentCnt(commentCnt)
                .fontSize(author.getFontSize())
                .paragraphSpace(author.getParagraphSpace())
                .bestComments(commentInfos)
                .mainAuthorId(novel.getMainAuthor().getId())
                .build();
    }

    @Override
    @Transactional
    public void createChapter(Long novelId, Long memberId) {
        Novel novel = entityFacade.getNovel(novelId);
        Author author = entityFacade.getAuthorByMemberId(memberId);
        if (!novel.getMainAuthor().getId().equals(author.getId())) {
            throw new BaseException(ErrorCode.MAIN_AUTHOR_NOT_MATCHED);
        }

        List<Chapter> chapters = novel.getChapters();
        Chapter chapter;
        if (chapters.isEmpty()) {
            chapter = Chapter.createChapter(
                    makeTitle(novel.getTitle(), 1), 1, ChapterStatus.IN_PROGRESS, novel);
        } else {
            Chapter prevChapter = chapters.get(chapters.size() - 1);
            if (prevChapter.getStatus() == ChapterStatus.IN_PROGRESS) {
                throw new IllegalArgumentException(ExceptionMessage.PREVIOUS_CHAPTER_IS_IN_PROGRESS);
            }
            Integer currPart = prevChapter.getPart() + 1;
            chapter = Chapter.createChapter(
                            makeTitle(novel.getTitle(), currPart), currPart, ChapterStatus.IN_PROGRESS, novel);
        }
        novel.addChapter(chapter);
        chapterRepository.save(chapter);
    }

    @Override
    @Transactional
    public void finishChapter(Long chapterId, Long memberId) {
        Chapter chapter = entityFacade.getChapter(chapterId);

        if (!Objects.equals(chapter.getNovel().getMainAuthor().getId(), memberId)) {
            throw new BaseException(ErrorCode.MAIN_AUTHOR_NOT_MATCHED);
        }
        chapter.changeStatus(ChapterStatus.COMPLETED);
    }

    private String makeTitle(String novelName, Integer part) {
        return novelName + " " + part + "화";
    }
}
