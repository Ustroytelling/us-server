package us.usserver.infra.novel;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import us.usserver.domain.novel.constant.Hashtag;
import us.usserver.domain.novel.constant.NovelStatus;
import us.usserver.domain.novel.constant.Orders;
import us.usserver.domain.novel.dto.SearchNovelReq;
import us.usserver.domain.novel.dto.SortDto;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepositoryCustom;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static us.usserver.domain.novel.entity.QNovel.novel;

@Repository
@RequiredArgsConstructor
public class NovelRepositoryImpl implements NovelRepositoryCustom {
    private final JPAQueryFactory queryFactory;

//    @Override
//    public Slice<Novel> moreNovelList(Long lastNovelId, Pageable pageable) {
//        List<Novel> novels = getMoreNovel(lastNovelId, pageable);
//
//        return checkLastPage(pageable, novels);
//    }

    @Override
    public Slice<Novel> searchNovelList(SearchNovelReq searchNovelReq, Pageable pageable) {
        List<Novel> novels = getSearchNovel(searchNovelReq, pageable);

        return checkLastPage(pageable, novels);
    }

//    private List<Novel> getMoreNovel(Long lastNovelId, Pageable pageable) {
//        return queryFactory
//                .select(novel)
//                .from(novel)
//                .where(ltNovelId(lastNovelId))
////                .orderBy(pageable.getSort())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize()+1)
//                .fetch();
//    }

    private List<Novel> getSearchNovel(SearchNovelReq searchNovelReq, Pageable pageable) {
        return queryFactory
                .select(novel)
                .from(novel)
                .where(
                        ltNovelId(searchNovelReq.getLastNovelId()),
                        containsTitle(searchNovelReq.getTitle()),
                        containsHashtag(searchNovelReq.getHashtag()),
                        eqNovelStatus(searchNovelReq.getStatus()))
                .orderBy(novelSort(searchNovelReq.getSortDto()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .fetch();
    }

    private BooleanExpression ltNovelId(Long lastNovelId) {
        return lastNovelId == 0L ? null : novel.id.lt(lastNovelId);
    }

    private BooleanExpression containsTitle(String title) {
        return hasText(title) ? novel.title.contains(title) : null;
    }

    private BooleanExpression containsHashtag(Hashtag hashtag) {
        return hashtag == null ? null : novel.hashtags.contains(hashtag);
    }

    private BooleanExpression eqNovelStatus(NovelStatus status) {
        return status == null ? null : novel.novelStatus.eq(status);
    }

    private OrderSpecifier<?> novelSort(SortDto sortDto) {
        if (sortDto == null) {
            //Default:사전순
            return new OrderSpecifier<>(Order.ASC, novel.title);
        }

        Order direction = (sortDto.getOrders() == Orders.DESC) ? Order.DESC : Order.ASC;
        switch (sortDto.getSorts()) {
            case LATEST -> {
                //최신 업데이트순
                return new OrderSpecifier<>(direction, novel.updatedAt);
            }
            case NEW -> {
                //신작순
                return new OrderSpecifier<>(direction, novel.createdAt);
            }
            default -> {
                //조회순
                return new OrderSpecifier<>(direction, novel.hit);
            }
        }
    }

    private Slice<Novel> checkLastPage(Pageable pageable, List<Novel> content) {
        boolean hasNext = false;

        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
            content.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(content, pageable, hasNext);
    }
}
