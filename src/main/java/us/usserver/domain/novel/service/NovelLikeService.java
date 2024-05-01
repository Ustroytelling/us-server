package us.usserver.domain.novel.service;

import org.springframework.stereotype.Service;

@Service
public interface NovelLikeService {
    void setNovelLike(Long novelId, Long memberId);

    void deleteNovelLike(Long novelId, Long memberId);
}
