package us.usserver.domain.novel.service;

import org.springframework.stereotype.Service;
import us.usserver.domain.novel.dto.*;
import us.usserver.domain.novel.dto.req.NovelBlueprint;
import us.usserver.domain.novel.dto.res.MainPageRes;
import us.usserver.domain.novel.dto.res.MoreNovelRes;


@Service
public interface NovelService {
    NovelInfo getNovelInfo(Long memberId, Long novelId);

    NovelDetailInfo getNovelDetailInfo(Long memberId, Long novelId);

    String modifyNovelSynopsis(Long novelId, Long memberId, String synopsis);

    AuthorDescription modifyAuthorDescription(Long novelId, Long memberId, AuthorDescription description);

    NovelInfo createNovel(Long memberId, NovelBlueprint novelBlueprint);

    MainPageRes getMainPage(Long memberId);

    MoreNovelRes getMoreNovels(Long memberId, MainNovelType mainNovelType, Integer nextPage);

    MoreNovelRes readMoreNovel(Long memberId);
}
