package us.usserver.domain.comment.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.comment.entity.Comment;
import us.usserver.domain.comment.entity.CommentLike;
import us.usserver.domain.comment.repository.CommentLikeRepository;
import us.usserver.global.EntityFacade;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {
    private final EntityFacade entityFacade;
    private final CommentLikeRepository commentLikeRepository;

    @Override
    @Transactional
    public void postLike(Long commentId, Long memberId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        Comment comment = entityFacade.getComment(commentId);

        CommentLike commentLike = CommentLike.builder().comment(comment).author(author).build();
        comment.getCommentLikes().add(commentLike);
    }

    @Override
    @Transactional
    public void deleteLike(Long commentId, Long memberId) {
        Author author = entityFacade.getAuthorByMemberId(memberId);
        Comment comment = entityFacade.getComment(commentId);

        CommentLike commentLike = commentLikeRepository.findByComment(comment)
                .orElseThrow(() -> new BaseException(ErrorCode.COMMENT_LIKE_NOT_FOUND));

        if (!Objects.equals(commentLike.getAuthor().getId(), author.getId())) {
            throw new BaseException(ErrorCode.AUTHOR_NOT_AUTHORIZED);
        }
        commentLikeRepository.delete(commentLike);
        comment.getCommentLikes().remove(commentLike); // EqualsAndHashCode 재정의 완료
    }
}
