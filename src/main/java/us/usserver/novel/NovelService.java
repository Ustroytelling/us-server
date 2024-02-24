package us.usserver.novel;

import org.springframework.stereotype.Service;
import us.usserver.member.Member;
import us.usserver.novel.dto.AuthorDescription;
import us.usserver.novel.dto.NovelDetailInfo;
import us.usserver.novel.dto.NovelInfo;
import us.usserver.novel.dto.*;


@Service
public interface NovelService {
    NovelInfo getNovelInfo(Long novelId);

    NovelDetailInfo getNovelDetailInfo(Long novelId);

    String modifyNovelSynopsis(Long novelId, Long authorId, String synopsis);

    AuthorDescription modifyAuthorDescription(Long novelId, Long authorId, AuthorDescription description);

    NovelInfo createNovel(Member member, CreateNovelReq createNovelReq);

    GetMainPageResponse getMainPageInfo(Member member);

    HomeNovelListResponse homeNovelInfo(Member member);

    NovelPageInfoResponse moreNovel(ConditionsOfPagination conditionsOfPagination);

    NovelPageInfoResponse readMoreNovel(Member member, ReadInfoOfNovel readInfoOfNovel);

    NovelPageInfoResponse searchNovel(Member member, SearchNovelReq searchNovelReq);

    SearchKeywordResponse searchKeyword(Member member);

    void deleteSearchKeyword(Member member);
}
