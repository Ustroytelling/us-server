package us.usserver.infra.novel;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepositoryCustom;

import java.util.List;
import java.util.Set;

import static us.usserver.domain.novel.entity.QNovel.novel;

@Repository
@RequiredArgsConstructor
public class NovelRepositoryImpl implements NovelRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Novel> searchNovelList(Set<String> keywords, Pageable pageable) {
        List<Novel> novelList = queryFactory
                .select(novel) // TODO: 나중에 DTO 로 조회할 것
                .from(novel)
                .where(searchByKeywords(keywords))
//                .orderBy(pageable.getSort()) TODO: 정렬 방법 정할 것
//                TODO: MariaDB에서 Full Text Index와 MATCH() ... AGAINST() 구문을 사용하여 텍스트 검색을 수행하고, 검색 결과의 관련성을 평가할 수 있다는데 확인해 볼 것
                // 마치 ts_rank와 유사한 기능
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return getNovelSlice(novelList, pageable);
    }

    private Predicate searchByKeywords(Set<String> keywords) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String keyword : keywords) {
            booleanBuilder.or(novel.title.contains(keyword));
            booleanBuilder.or(novel.mainAuthor.nickname.contains(keyword));
        }
        return booleanBuilder;
    }

    private Slice<Novel> getNovelSlice(List<Novel> novelList, Pageable pageable) {
        boolean hasNext = false;

        if (novelList.size() > pageable.getPageSize()) {
            hasNext = true;
            novelList.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(novelList, pageable, hasNext);
    }
}
