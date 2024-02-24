package us.usserver.novel.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import us.usserver.novel.Novel;
import us.usserver.novel.NovelRepository;
import us.usserver.novel.dto.ConditionsOfPagination;
import us.usserver.novel.dto.SearchNovelReq;
import us.usserver.novel.dto.SortDto;
import us.usserver.novel.novelEnum.Hashtag;
import us.usserver.novel.novelEnum.NovelStatus;
import us.usserver.novel.novelEnum.Orders;

import java.util.List;

import static org.springframework.util.StringUtils.hasText;
import static us.usserver.novel.QNovel.novel;

@Repository
@RequiredArgsConstructor
public class NovelRepositoryImpl implements NovelRepository {

    private final JPAQueryFactory queryFactory;
    private final NovelJpaRepository novelJpaRepository;

    @Override
    public Novel save(Novel novel) {
        return novelJpaRepository.save(novel);
    }

    @Override
    public void delete(Novel novel) {
        novelJpaRepository.delete(novel);
    }

    @Override
    public Slice<Novel> moreNovelList(ConditionsOfPagination conditionsOfPagination, Pageable pageable) {
        List<Novel> novels = getMoreNovel(conditionsOfPagination, pageable);

        return checkLastPage(pageable, novels);
    }

    @Override
    public Slice<Novel> getNovelList(Pageable pageable) {
        return novelJpaRepository.findSliceBy(pageable);
    }


    @Override
    public Slice<Novel> searchNovelList(SearchNovelReq searchNovelReq, Pageable pageable) {
        List<Novel> novels = getSearchNovel(searchNovelReq, pageable);

        return checkLastPage(pageable, novels);
    }

    private List<Novel> getMoreNovel(ConditionsOfPagination conditionsOfPagination, Pageable pageable) {
        return queryFactory
                .select(novel)
                .from(novel)
                .where(ltNovelId(conditionsOfPagination.getLastNovelId()))
                .orderBy(novelSort(conditionsOfPagination.getSortDto()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1)
                .fetch();
    }

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
                .limit(pageable.getPageSize() + 1)
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
            return new OrderSpecifier<>(Order.ASC, novel.title);
        }

        Order direction = (sortDto.getOrders() == Orders.DESC) ? Order.DESC : Order.ASC;
        switch (sortDto.getSorts()) {
            case CREATED_AT, LATEST -> { return new OrderSpecifier<>(direction, novel.createdAt); }
            case UPDATED_AT, NEW -> { return new OrderSpecifier<>(direction, novel.updatedAt); }
            case HIT -> { return new OrderSpecifier<>(direction, novel.hit); }
            default -> { return new OrderSpecifier<>(Order.ASC, novel.title); }
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
