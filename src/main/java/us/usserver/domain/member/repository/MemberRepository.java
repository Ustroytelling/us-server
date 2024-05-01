package us.usserver.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.constant.OauthProvider;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByOauthProviderAndSocialId(OauthProvider oauthProvider, String socialId);

    Optional<Member> getMemberById(Long memberId);

    Optional<Member> findBySocialId(String socialId);
}
