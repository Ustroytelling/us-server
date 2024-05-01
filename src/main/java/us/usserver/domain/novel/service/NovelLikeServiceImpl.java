package us.usserver.domain.novel.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.entity.NovelLike;
import us.usserver.domain.novel.repository.NovelLikeRepository;
import us.usserver.global.EntityFacade;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;

@Service
@Transactional
@RequiredArgsConstructor
public class NovelLikeServiceImpl implements NovelLikeService {
    private final EntityFacade entityFacade;
    private final NovelLikeRepository novelLikeRepository;

    @Override
//    @CacheEvict(cacheNames = "novelLikeCnt", key = "#novelId")
    public void setNovelLike(Long novelId, Long memberId) {
        Novel novel = entityFacade.getNovel(novelId);
        Author author = entityFacade.getAuthorByMemberId(memberId);

        if (novelLikeRepository.findFirstByNovelAndAuthor(novel, author).isPresent()) {
            throw new BaseException(ErrorCode.LIKE_DUPLICATED);
        }

        NovelLike novelLike = NovelLike.builder()
                .novel(novel)
                .author(author)
                .build();
        novelLikeRepository.save(novelLike);
    }

    @Override
//    @CacheEvict(cacheNames = "novelLikeCnt", key = "#novelId")
    public void deleteNovelLike(Long novelId, Long memberId) {
        Optional<NovelLike> novelLike = novelLikeRepository.findByNovelIdAndAuthorId(novelId, memberId);
        novelLike.ifPresent(novelLikeRepository::delete);
    }
}
