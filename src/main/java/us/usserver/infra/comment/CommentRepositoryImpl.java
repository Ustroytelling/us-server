package us.usserver.infra.comment;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.comment.entity.Comment;
import us.usserver.domain.comment.repository.CommentRepositoryDSL;

import java.util.List;

import static us.usserver.domain.chapter.entity.QChapter.chapter;
import static us.usserver.domain.comment.entity.QComment.comment;


@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryDSL {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> getTop3CommentOfChapter(Chapter chapter) {
        return queryFactory
                .select(comment)
                .from(comment)
                .where(eqChapterId(chapter.getId()))
                .orderBy(commentLikes())
                .limit(3)
                .fetch();

    }

    private BooleanExpression eqChapterId(Long chapterId) {
        return chapter.id.eq(chapterId);
    }

    public OrderSpecifier<?> commentLikes() {
        return new OrderSpecifier<>(Order.DESC, comment.commentLikes.size());
    }
}
