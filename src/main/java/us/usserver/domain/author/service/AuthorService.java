package us.usserver.domain.author.service;

import org.springframework.stereotype.Service;
import us.usserver.domain.author.dto.req.UpdateAuthorReq;

@Service
public interface AuthorService {
    void updateAuthor(Long memberId, UpdateAuthorReq updateAuthorReq);

    void changeFontSize(Long memberId, int fontSize);

    void changeParagraphSpace(Long memberId, int paragraphSpace);
}
