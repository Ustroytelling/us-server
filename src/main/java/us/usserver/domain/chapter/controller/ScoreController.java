package us.usserver.domain.chapter.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import us.usserver.domain.chapter.dto.PostScore;
import us.usserver.domain.chapter.service.ScoreService;
import us.usserver.global.response.ApiCsResponse;

@Tag(name = "평점 API")
@ResponseBody
@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
public class ScoreController {
    private final ScoreService scoreService;

    @Operation(summary = "챕터 평점 주기", description = "챕터에 대한 평점 1 ~ 10 까지 입력 가능")
    @ApiResponse(responseCode = "200", description = "성공")
    @PostMapping("/{chapterId}")
    public ApiCsResponse<Void> setScore(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long chapterId,
            @Validated @RequestBody PostScore score
    ) {
        scoreService.postScore(chapterId, memberId, score);
        return ApiCsResponse.success();
    }
}
