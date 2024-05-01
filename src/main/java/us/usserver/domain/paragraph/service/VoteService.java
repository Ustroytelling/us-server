package us.usserver.domain.paragraph.service;

import org.springframework.stereotype.Service;
import us.usserver.domain.member.entity.Member;

@Service
public interface VoteService {
    void voting(Long memberId, Long paragraphId);

    void unvoting(Long memberId, Long paragraphId);
}
