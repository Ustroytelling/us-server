package us.usserver.domain.member.service;

import org.springframework.stereotype.Service;
import us.usserver.domain.member.entity.Member;

@Service
public interface MemberService {
    void logout(String accessToken);
    void withdraw(Member member);
    Member getMyInfo(String socialId);

}