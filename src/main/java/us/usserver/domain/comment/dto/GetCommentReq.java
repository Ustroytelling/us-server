package us.usserver.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record GetCommentReq(
        @Schema(description = "다음 댓글 페이지", nullable = true, defaultValue = "0")
        Integer nextPage
) {}
