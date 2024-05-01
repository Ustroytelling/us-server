package us.usserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginDto(
        @Schema(description = "각 유저의 id를 return", nullable = true, example = "17")
        Long memberId
) {}
