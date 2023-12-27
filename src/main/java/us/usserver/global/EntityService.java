package us.usserver.global;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import us.usserver.author.Author;
import us.usserver.author.AuthorRepository;
import us.usserver.chapter.Chapter;
import us.usserver.chapter.ChapterRepository;
import us.usserver.global.exception.*;
import us.usserver.member.Member;
import us.usserver.member.MemberRepository;
import us.usserver.novel.Novel;
import us.usserver.novel.NovelRepository;
import us.usserver.paragraph.Paragraph;
import us.usserver.paragraph.ParagraphRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntityService {
    private final AuthorRepository authorRepository;
    private final NovelRepository novelRepository;
    private final MemberRepository memberRepository;
    private final ChapterRepository chapterRepository;
    private final ParagraphRepository paragraphRepository;

    public Author getAuthor(Long authorId) {
        Optional<Author> authorById = authorRepository.getAuthorById(authorId);
        if (authorById.isEmpty()) {
            throw new AuthorNotFoundException(ExceptionMessage.Author_NOT_FOUND);
        }
        return authorById.get();
    }

    public Member getMember(Long memberId) {
        Optional<Member> memberById = memberRepository.getMemberById(memberId);
        if (memberById.isEmpty()) {
            throw new MemberNotFoundException(ExceptionMessage.Member_NOT_FOUND);
        }
        return memberById.get();
    }

    public Novel getNovel(Long novelId) {
        Optional<Novel> novelById = novelRepository.getNovelById(novelId);
        if (novelById.isEmpty()) {
            throw new NovelNotFoundException(ExceptionMessage.Novel_NOT_FOUND);
        }
        return novelById.get();
    }

    public Chapter getChapter(Long chapterId) {
        Optional<Chapter> chapterById = chapterRepository.getChapterById(chapterId);
        if (chapterById.isEmpty()) {
            throw new ChapterNotFoundException(ExceptionMessage.Chapter_NOT_FOUND);
        }
        return chapterById.get();
    }

    public Paragraph getParagraph(Long paragraphId) {
        Optional<Paragraph> paragraphById = paragraphRepository.getParagraphById(paragraphId);
        if (paragraphById.isEmpty()) {
            throw new ParagraphNotFoundException(ExceptionMessage.Paragraph_NOT_FOUND);
        }
        return paragraphById.get();
    }
}
