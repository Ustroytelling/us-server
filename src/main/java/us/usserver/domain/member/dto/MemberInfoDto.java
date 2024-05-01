package us.usserver.domain.member.dto;

import us.usserver.domain.member.constant.Role;

public record MemberInfoDto(
        Long memberId,
        Role role,
        String accessToken,
        String refreshToken
) {}
