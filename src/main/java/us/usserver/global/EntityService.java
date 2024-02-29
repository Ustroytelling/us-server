package us.usserver.global;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import us.usserver.domain.member.entity.Author;
import us.usserver.domain.member.repository.AuthorRepository;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.comment.Comment;
import us.usserver.domain.comment.repository.CommentJpaRepository;
import us.usserver.global.exception.*;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.novel.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.domain.paragraph.entity.Vote;
import us.usserver.domain.paragraph.repository.VoteJpaRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntityService {
    private final AuthorRepository authorRepository;
    private final MemberRepository memberRepository;
    private final NovelRepository novelRepository;
    private final ChapterRepository chapterRepository;
    private final ParagraphRepository paragraphRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final VoteJpaRepository voteJpaRepository;

    public Author getAuthor(Long authorId) {
        Optional<Author> authorById = authorRepository.getAuthorById(authorId);
        if (authorById.isEmpty()) {
            throw new AuthorNotFoundException(ExceptionMessage.AUTHOR_NOT_FOUND);
        }
        return authorById.get();
    }

    public Member getMember(Long memberId) {
        Optional<Member> memberById = memberRepository.getMemberById(memberId);
        if (memberById.isEmpty()) {
            throw new MemberNotFoundException(ExceptionMessage.MEMBER_NOT_FOUND);
        }
        return memberById.get();
    }

    public Novel getNovel(Long novelId) {
        Optional<Novel> novelById = novelRepository.getNovelById(novelId);
        if (novelById.isEmpty()) {
            throw new NovelNotFoundException(ExceptionMessage.NOVEL_NOT_FOUND);
        }
        return novelById.get();
    }

    public Chapter getChapter(Long chapterId) {
        Optional<Chapter> chapterById = chapterRepository.getChapterById(chapterId);
        if (chapterById.isEmpty()) {
            throw new ChapterNotFoundException(ExceptionMessage.CHAPTER_NOT_FOUND);
        }
        return chapterById.get();
    }

    public Paragraph getParagraph(Long paragraphId) {
        Optional<Paragraph> paragraphById = paragraphRepository.getParagraphById(paragraphId);
        if (paragraphById.isEmpty()) {
            throw new ParagraphNotFoundException(ExceptionMessage.PARAGRAPH_NOT_FOUND);
        }
        return paragraphById.get();
    }

    public Comment getComment(Long commentId) {
        Optional<Comment> commentById = commentJpaRepository.getCommentById(commentId);
        if (commentById.isEmpty()) {
            throw new CommentNotFoundException(ExceptionMessage.COMMENT_NOT_FOUND);
        }
        return commentById.get();
    }

    public Vote getVote(Long voteId) {
        Optional<Vote> voteById = voteJpaRepository.getVoteById(voteId);
        if (voteById.isEmpty()) {
            throw new VoteNotFoundException(ExceptionMessage.VOTE_NOT_FOUND);
        }
        return voteById.get();
    }
}
