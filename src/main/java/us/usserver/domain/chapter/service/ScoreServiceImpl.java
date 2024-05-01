package us.usserver.domain.chapter.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.chapter.dto.PostScore;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.entity.Score;
import us.usserver.domain.chapter.repository.ScoreRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.global.EntityFacade;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ScoreServiceImpl implements ScoreService {
    private final EntityFacade entityFacade;
    private final ScoreRepository scoreRepository;

    @Override
    public void postScore(Long chapterId, Long memberId, PostScore postScore) {
        // TODO: 스코어가 등록 되었을 때 소설에 전파해서 전체 점수 조정 로직 수정
        Author author = entityFacade.getAuthorByMemberId(memberId);
        Chapter chapter = entityFacade.getChapter(chapterId);

        if (postScore.getScore() > 10 || postScore.getScore() < 1) {
            throw new BaseException(ErrorCode.SCORE_OUT_OF_RANGE);
        }
        Optional<Score> scoreByAuthorAndChapter = scoreRepository.findScoreByAuthorAndChapter(author, chapter);
        if (scoreByAuthorAndChapter.isPresent()) {
            throw new BaseException(ErrorCode.SCORE_ALREADY_ENTERED);
        }

        Score score = Score.builder()
                .score(postScore.getScore()).author(author).chapter(chapter).build();
        scoreRepository.save(score);
    }

    @Override
    public Double getChapterScore(Chapter chapter) {
        return scoreRepository.findAverageScoreByChapter(chapter);
    }
}
