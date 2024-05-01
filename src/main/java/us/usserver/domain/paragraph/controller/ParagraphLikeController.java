package us.usserver.domain.paragraph.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import us.usserver.domain.paragraph.service.ParagraphLikeService;
import us.usserver.global.response.ApiCsResponse;

@Tag(name = "한줄 좋아요 API")
@ResponseBody
@RestController
@RequestMapping("/like/paragraph")
@RequiredArgsConstructor
public class ParagraphLikeController {
    private final ParagraphLikeService paragraphLikeService;

    @Operation(summary = "한줄 좋아요", description = "특정 한줄에 좋아요 누르기")
    @ApiResponse(responseCode = "200", description = "좋아요 성공")
    @PostMapping("/{paragraphId}")
    public ApiCsResponse<Void> setLike(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long paragraphId
    ) {
        paragraphLikeService.setParagraphLike(paragraphId, memberId);
        return ApiCsResponse.success();
    }

    @Operation(summary = "한줄 좋아요 취소", description = "특정 한줄에 좋아요 취소하기")
    @ApiResponse(responseCode = "200", description = "좋아요 취소")
    @DeleteMapping("/{paragraphId}")
    public ApiCsResponse<Void> deleteLike(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long paragraphId
    ) {
        paragraphLikeService.deleteParagraphLike(paragraphId, memberId);
        return ApiCsResponse.success();
    }
}
