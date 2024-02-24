package us.usserver.bookshelf.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.author.Author;
import us.usserver.authority.Authority;
import us.usserver.authority.AuthorityRepository;
import us.usserver.bookshelf.BookshelfService;
import us.usserver.bookshelf.dto.BookshelfDefaultResponse;
import us.usserver.bookshelf.dto.NovelPreview;
import us.usserver.chapter.Chapter;
import us.usserver.global.EntityService;
import us.usserver.like.novel.NovelLike;
import us.usserver.like.novel.NovelLikeRepository;
import us.usserver.novel.Novel;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookshelfServiceV1 implements BookshelfService {
    private final EntityService entityService;
    private final AuthorityRepository authorityRepository;
    private final NovelLikeRepository novelLikeRepository;

    @Value("${aws.public.ip}")
    private String publicIp;


    @Override
    public BookshelfDefaultResponse recentViewedNovels(Long authorId) {
        Author author = entityService.getAuthor(authorId);
        List<Novel> viewedNovels = author.getViewedNovels();

        List<NovelPreview> novelPreviews = viewedNovels.stream()
                .map(novel -> NovelPreview.fromNovel(
                        novel,
                        getTotalJoinedAuthor(novel),
                        getShortcutToChapter(novel, null) // TODO: 최근 본 소설에서 몇 화(Chapter)를 봤는지 기억해야하는 기능이 필요
                )).toList();

        return BookshelfDefaultResponse.builder().novelPreviews(novelPreviews).build();
    }

    @Override
    public void deleteRecentViewedNovels(Long authorId, Long novelId) {
        Author author = entityService.getAuthor(authorId);
        Novel novel = entityService.getNovel(novelId);

        author.getViewedNovels().remove(novel);
    }

    @Override
    public BookshelfDefaultResponse createdNovels(Long authorId) {
        Author author = entityService.getAuthor(authorId);

        List<Novel> createdNovels = author.getCreatedNovels();
        List<NovelPreview> novelPreviews = createdNovels.stream()
                .map(novel -> NovelPreview.fromNovel(
                        novel,
                        getTotalJoinedAuthor(novel),
                        getShortcutToNovel(novel)
                )).toList();

        return BookshelfDefaultResponse.builder().novelPreviews(novelPreviews).build();
    }

    @Override
    public void deleteCreatedNovels(Long authorId, Long novelId) {
    }

    @Override
    public BookshelfDefaultResponse joinedNovels(Long authorId) {
        Author author = entityService.getAuthor(authorId);

        List<Authority> authorities = authorityRepository.findAllByAuthor(author);
        List<NovelPreview> novelPreviews = authorities.stream()
                .map(authority -> NovelPreview.fromNovel(
                        authority.getNovel(),
                        getTotalJoinedAuthor(authority.getNovel()),
                        getShortcutToNovel(authority.getNovel())
                )).toList();

        return BookshelfDefaultResponse.builder().novelPreviews(novelPreviews).build();
    }

    @Override
    public void deleteJoinedNovels(Long authorId, Long novelId) {

    }

    @Override
    public BookshelfDefaultResponse likedNovels(Long authorId) {
        Author author = entityService.getAuthor(authorId);

        List<NovelLike> novelLikes = novelLikeRepository.findAllByAuthor(author);
        List<NovelPreview> novelPreviews = novelLikes.stream()
                .map(likedNovel -> NovelPreview.fromNovel(
                        likedNovel.getNovel(),
                        getTotalJoinedAuthor(likedNovel.getNovel()),
                        getShortcutToNovel(likedNovel.getNovel())
                )).toList();

        return BookshelfDefaultResponse.builder().novelPreviews(novelPreviews).build();
    }

    @Override
    public void deleteLikedNovels(Long authorId, Long novelId) {
        Author author = entityService.getAuthor(authorId);
        Novel novel = entityService.getNovel(novelId);

        Optional<NovelLike> novelLike = novelLikeRepository.findAnyByNovelAndAuthor(novel, author);
        novelLike.ifPresent(novelLikeRepository::delete);
    }

    private Integer getTotalJoinedAuthor(Novel novel){
            return authorityRepository.countAllByNovel(novel);
    }

    private String getShortcutToNovel(Novel novel) {
        return "http://" + publicIp + ":8080/novel/" + novel.getId();
    }

    private String getShortcutToChapter(Novel novel, Chapter chapter) { // TODO: URL 숨기기 필요
        return "http://" + publicIp + ":8080/chapter/" + novel.getId() + chapter.getId();
    }
}
