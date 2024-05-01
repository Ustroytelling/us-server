package us.usserver.domain.author.service;

import org.springframework.stereotype.Service;
import us.usserver.domain.author.dto.res.BookshelfDefaultResponse;

@Service
public interface BookshelfService {
    BookshelfDefaultResponse recentViewedNovels(Long memberId);
    void deleteRecentViewedNovels(Long memberId, Long readNovelId);

    BookshelfDefaultResponse createdNovels(Long memberId);
    void deleteCreatedNovels(Long memberId, Long novelId);

    BookshelfDefaultResponse joinedNovels(Long memberId);
    void deleteJoinedNovels(Long memberId, Long novelId);

    BookshelfDefaultResponse likedNovels(Long memberId);
    void deleteLikedNovels(Long memberId, Long novelId);
}
