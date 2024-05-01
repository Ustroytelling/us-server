package us.usserver.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record GetCommentRes(
        @Schema(description = "댓글 목록", example = "[댓글정보1, 댓글정보2, 댓글정보3, ...]")
        List<CommentInfo> commentInfos
) {}
