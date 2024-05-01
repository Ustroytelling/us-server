package us.usserver.domain.comment.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import us.usserver.domain.comment.service.CommentLikeService;
import us.usserver.global.response.ApiCsResponse;

@Tag(name = "댓글 좋아요 API")
@ResponseBody
@RestController
@RequestMapping("/like/comment")
@RequiredArgsConstructor
public class CommentLikeController {
    private final CommentLikeService commentLikeService;

    @Operation(summary = "댓글 좋아요", description = "댓글 하트 누르기")
    @ApiResponse(responseCode = "200", description = "좋아요 성공")
    @PostMapping("/{commentId}")
    public ApiCsResponse<Void> setLike(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long commentId
    ) {
        commentLikeService.postLike(commentId, memberId);
        return ApiCsResponse.success();
    }

    @Operation(summary = "댓글 좋아요 취소", description = "댓글 하트 다시 누르기")
    @ApiResponse(responseCode = "200", description = "취소 완료")
    @DeleteMapping("/{commentId}")
    public ApiCsResponse<Void> deleteLike(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long commentId
    ) {
        commentLikeService.deleteLike(commentId, memberId);
        return ApiCsResponse.success();
    }
}
