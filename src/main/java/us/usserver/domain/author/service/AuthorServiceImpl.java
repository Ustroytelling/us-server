package us.usserver.domain.author.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.dto.req.UpdateAuthorReq;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.global.EntityFacade;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;

@Transactional
@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final EntityFacade entityFacade;

    @Override
    public void updateAuthor(Long memberId, UpdateAuthorReq updateAuthorReq) {
        Member member = entityFacade.getMember(memberId);
        Author author = member.getAuthor();

        if (updateAuthorReq.nickname() != null) {
            author.changeNickname(updateAuthorReq.nickname());
        }
        if (updateAuthorReq.introduction() != null) {
            author.changeIntroduction(updateAuthorReq.introduction());
        }
        if (updateAuthorReq.profileImg() != null) {
            author.changeProfileImg(updateAuthorReq.profileImg());
        }
        if (updateAuthorReq.collectionNovelsPublic() != null) {
            author.setCollectionNovelsPublic(updateAuthorReq.collectionNovelsPublic());
        }
        if (updateAuthorReq.participateNovelsPublic() != null) {
            author.setParticipateNovelsPublic(updateAuthorReq.participateNovelsPublic());
        }
    }

    @Override
    public void changeFontSize(Long memberId, int fontSize) {
        if (fontSize < 1 || fontSize > 30) {
            throw new BaseException(ErrorCode.FONT_SIZE_OUT_OF_RANGE);
        }
        Member member = entityFacade.getMember(memberId);
        Author author = member.getAuthor();
        author.changeFontSize(fontSize);
    }

    @Override
    public void changeParagraphSpace(Long memberId, int paragraphSpace) {
        if (paragraphSpace < 1 || paragraphSpace > 30) {
            throw new BaseException(ErrorCode.PARAGRAPH_SPACE_OUT_OF_RANGE);
        }
        Member member = entityFacade.getMember(memberId);
        Author author = member.getAuthor();
        author.changeParagraphSpace(paragraphSpace);
    }
}
