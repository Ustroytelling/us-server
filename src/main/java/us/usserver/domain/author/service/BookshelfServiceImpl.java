package us.usserver.domain.author.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.entity.ReadNovel;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.novel.repository.NovelLikeRepository;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.repository.AuthorityRepository;
import us.usserver.domain.author.dto.res.BookshelfDefaultResponse;
import us.usserver.domain.author.dto.NovelPreview;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.EntityFacade;
import us.usserver.domain.novel.entity.NovelLike;
import us.usserver.domain.novel.entity.Novel;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookshelfServiceImpl implements BookshelfService {
    private final EntityFacade entityFacade;
    private final AuthorityRepository authorityRepository;
    private final NovelLikeRepository novelLikeRepository;
    private final NovelRepository novelRepository;

    @Value("${aws.public.ip}")
    private String accessIp;

    @Override
    public BookshelfDefaultResponse recentViewedNovels(Long memberId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        Set<ReadNovel> readNovels = author.getReadNovels();

        List<NovelPreview> novelPreviews = readNovels.stream()
                .map(ReadNovel::getNovel)
                .map(novel -> NovelPreview.fromNovel(novel, getTotalJoinedAuthor(novel), getShortcuts(novel)))
                .toList();

        return BookshelfDefaultResponse.builder().novelPreviews(novelPreviews).count(readNovels.size()).build();
    }

    @Override
    public void deleteRecentViewedNovels(Long memberId, Long readNovelId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        ReadNovel readNovel = entityFacade.getReadNovel(readNovelId);
        author.deleteReadNovel(readNovel);
    }

    @Override
    public BookshelfDefaultResponse createdNovels(Long memberId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        List<Novel> allByMainAuthor = novelRepository.findAllByMainAuthor(author);

        List<NovelPreview> novelPreviews = allByMainAuthor.stream()
                .filter(novel -> novel.getMainAuthor().equals(author))
                .map(novel -> NovelPreview.fromNovel(
                        novel,
                        getTotalJoinedAuthor(novel),
                        getShortcuts(novel)
                )).toList();

        return BookshelfDefaultResponse.builder().novelPreviews(novelPreviews).build();
    }

    @Override
    public void deleteCreatedNovels(Long memberId, Long novelId) {
    }

    @Override
    public BookshelfDefaultResponse joinedNovels(Long memberId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);

        List<Authority> authorities = authorityRepository.findAllByAuthor(author);
        List<NovelPreview> novelPreviews = authorities.stream()
                .map(authority -> NovelPreview.fromNovel(
                        authority.getNovel(),
                        getTotalJoinedAuthor(authority.getNovel()),
                        getShortcuts(authority.getNovel())
                )).toList();

        return BookshelfDefaultResponse.builder().novelPreviews(novelPreviews).build();
    }

    @Override
    public void deleteJoinedNovels(Long memberId, Long novelId) {
        // TODO: 정확한 삭제 지침 나오기 전까지 보류
    }

    @Override
    public BookshelfDefaultResponse likedNovels(Long memberId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);

        List<NovelLike> novelLikes = novelLikeRepository.findAllByAuthor(author);
        List<NovelPreview> novelPreviews = novelLikes.stream()
                .map(likedNovel -> NovelPreview.fromNovel(
                        likedNovel.getNovel(),
                        getTotalJoinedAuthor(likedNovel.getNovel()),
                        getShortcuts(likedNovel.getNovel())
                )).toList();

        return BookshelfDefaultResponse.builder().novelPreviews(novelPreviews).build();
    }

    @Override
    public void deleteLikedNovels(Long memberId, Long novelId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        Novel novel = entityFacade.getNovel(novelId);

        Optional<NovelLike> novelLike = novelLikeRepository.findFirstByNovelAndAuthor(novel, author);
        novelLike.ifPresent(novelLikeRepository::delete);
    }

    private Integer getTotalJoinedAuthor(Novel novel) {
        return authorityRepository.countAllByNovel(novel);
    }

    private String getShortcuts(Novel novel) {
        return "http://" + accessIp + ":8080/novel/" + novel.getId();
    }
}
