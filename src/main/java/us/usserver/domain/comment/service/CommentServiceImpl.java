package us.usserver.domain.comment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.comment.dto.CommentContent;
import us.usserver.domain.comment.dto.CommentInfo;
import us.usserver.domain.comment.dto.GetCommentRes;
import us.usserver.domain.comment.entity.Comment;
import us.usserver.domain.comment.repository.CommentRepository;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.global.EntityFacade;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final EntityFacade entityFacade;
    private final CommentRepository commentRepository;

    private static final int CommentPageSize = 10;

    @Override
    public GetCommentRes getCommentsOfNovel(Long novelId, int page) {

        Novel novel = entityFacade.getNovel(novelId);

        PageRequest pageRequest = PageRequest.of(page, CommentPageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<CommentInfo> commentInfos = commentRepository.findSliceByNovel(novel, pageRequest)
                .map(CommentInfo::mapCommentToCommentInfo).toList();
        return new GetCommentRes(commentInfos);

    }

    @Override
    public GetCommentRes getCommentsOfChapter(Long chapterId, int page) {
        Chapter chapter = entityFacade.getChapter(chapterId);

        PageRequest pageRequest = PageRequest.of(page, CommentPageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<CommentInfo> commentInfos = commentRepository.findSliceByChapter(chapter, pageRequest)
                .map(CommentInfo::mapCommentToCommentInfo).toList();

        return new GetCommentRes(commentInfos);
    }

    @Override
    public CommentInfo writeCommentOnNovel(Long novelId, Long memberId, CommentContent commentContent) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        Novel novel = entityFacade.getNovel(novelId);
        Integer ZeroLikeCnt = 0;

        if (commentContent.getContent().isEmpty() || commentContent.getContent().length() > 300) {
            throw new BaseException(ErrorCode.COMMENT_LENGTH_OUT_OF_RANGE);
        }

        Comment comment = commentRepository.save(Comment.builder()
                .content(commentContent.getContent())
                .author(author)
                .novel(novel)
                .chapter(null)
                .build());
        novel.getComments().add(comment);

        return CommentInfo.fromComment(comment, novel.getTitle(), ZeroLikeCnt);
    }

    @Override
    public CommentInfo writeCommentOnChapter(Long chapterId, Long memberId, CommentContent commentContent) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        Chapter chapter = entityFacade.getChapter(chapterId);
        Novel novel = chapter.getNovel();
        Integer ZeroLikeCnt = 0;

        if (commentContent.getContent().isEmpty() || commentContent.getContent().length() > 300) {
            throw new BaseException(ErrorCode.COMMENT_LENGTH_OUT_OF_RANGE);
        }

        Comment comment = commentRepository.save(Comment.builder()
                .content(commentContent.getContent())
                .author(author)
                .novel(novel)
                .chapter(chapter)
                .build());
        novel.getComments().add(comment);
        chapter.getComments().add(comment);

        return CommentInfo.fromComment(
                comment,
                chapter.getTitle(),
                ZeroLikeCnt
        );
    }

    @Override
    public GetCommentRes getCommentsByAuthor(Long memberId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        List<Comment> commentsByAuthor = commentRepository.findAllByAuthor(author);

        List<CommentInfo> commentInfos = commentsByAuthor.stream()
                .map(comment -> CommentInfo
                        .fromComment(
                                comment,
                                comment.getChapter() == null ? comment.getNovel().getTitle() : comment.getChapter().getTitle(),
                                comment.getCommentLikes().size()
                        ))
                .toList();

        return GetCommentRes.builder().commentInfos(commentInfos).build();
    }

    @Override
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = entityFacade.getComment(commentId);
        Author author = entityFacade.getAuthorByMemberId(memberId);

        if (!comment.getAuthor().getId().equals(author.getId())) {
            throw new BaseException(ErrorCode.AUTHOR_NOT_AUTHORIZED);
        }

        commentRepository.delete(comment);
    }
}
