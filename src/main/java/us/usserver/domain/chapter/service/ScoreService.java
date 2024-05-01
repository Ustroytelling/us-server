package us.usserver.domain.chapter.service;

import org.springframework.stereotype.Service;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.dto.PostScore;

@Service
public interface ScoreService {
    void postScore(Long chapterId, Long memberId, PostScore postScore);

    Double getChapterScore(Chapter chapter);
}
