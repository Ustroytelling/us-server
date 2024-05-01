package us.usserver.domain.novel.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import us.usserver.domain.novel.service.NovelLikeService;
import us.usserver.global.response.ApiCsResponse;

@Tag(name = "소설 좋아요(구독) API")
@ResponseBody
@RestController
@RequestMapping("/like/novel")
@RequiredArgsConstructor
public class NovelLikeController {
    private final NovelLikeService novelLikeService;

    @Operation(summary = "소설 좋아요", description = "소설 메인 옆 하트 누르기")
    @ApiResponse(responseCode = "200", description = "좋아요 성공")
    @PostMapping("/{novelId}")
    public ApiCsResponse<Void> setLike(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long novelId
    ) {
        novelLikeService.setNovelLike(novelId, memberId);
        return ApiCsResponse.success();
    }

    @Operation(summary = "소설 좋아요 취소", description = "소설 메인 옆 하트 다시 누르기")
    @ApiResponse(responseCode = "200", description = "좋아요 취소")
    @DeleteMapping("/{novelId}")
    public ApiCsResponse<Void> deleteLike(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long novelId
    ) {
        novelLikeService.deleteNovelLike(novelId, memberId);
        return ApiCsResponse.success();
    }
}
