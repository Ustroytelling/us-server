package us.usserver.domain.paragraph.service;

import org.springframework.stereotype.Service;

@Service
public interface ParagraphLikeService {
    void setParagraphLike(Long paragraphId, Long memberId);

    void deleteParagraphLike(Long paragraphId, Long memberId);
}
