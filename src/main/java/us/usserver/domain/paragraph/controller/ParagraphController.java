package us.usserver.domain.paragraph.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import us.usserver.domain.paragraph.dto.ParagraphInVoting;
import us.usserver.domain.paragraph.dto.req.PostParagraphReq;
import us.usserver.domain.paragraph.dto.res.GetParagraphResponse;
import us.usserver.domain.paragraph.service.ParagraphService;
import us.usserver.global.response.ApiCsResponse;

import java.net.URI;

@Tag(name = "한줄 API")
@ResponseBody
@RestController
@RequestMapping("/paragraph")
@RequiredArgsConstructor
public class ParagraphController {
    private final ParagraphService paragraphService;

    @Operation(summary = "투표중인 한줄 불러오기", description = "뷰어에서 투표중인 한줄을 클릭하면 나오는 투표중 한줄 전부 불러오기")
    @ApiResponse(responseCode = "200", description = "불러오기 성공",
            content = @Content(schema = @Schema(implementation = GetParagraphResponse.class)))
    @GetMapping("/{chapterId}/voting")
    public ApiCsResponse<GetParagraphResponse> getParagraphsInVoting(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long chapterId
    ) {
        GetParagraphResponse paragraphs = paragraphService.getInVotingParagraphs(memberId, chapterId);

        return ApiCsResponse.success(paragraphs);
    }

    @Operation(summary = "한줄 작성하기", description = "다음 한줄에 대한 내 의견 작성")
    @ApiResponse(responseCode = "201", description = "작성 성공",
            content = @Content(schema = @Schema(implementation = ParagraphInVoting.class)))
    @PostMapping("/{chapterId}")
    public ResponseEntity<ApiCsResponse<ParagraphInVoting>> postParagraph(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long chapterId,
            @Validated @RequestBody PostParagraphReq req
    ) {
        ParagraphInVoting paragraph = paragraphService.postParagraph(memberId, chapterId, req);

        ApiCsResponse<ParagraphInVoting> response = ApiCsResponse.success(paragraph);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "한줄 선택하기", description = "다음으로 추가될 한줄을 선정(메인 작가만 가능)")
    @ApiResponse(responseCode = "201", description = "작성 성공")
    @PatchMapping("/{novelId}/{chapterId}/{paragraphId}")
    public ResponseEntity<ApiCsResponse<Void>> deleteParagraph(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long novelId,
            @PathVariable Long chapterId,
            @PathVariable Long paragraphId
    ) {
        paragraphService.selectParagraph(memberId, novelId, chapterId, paragraphId);

        ApiCsResponse<Void> response = ApiCsResponse.success();
        URI redirectUri = URI.create("/paragraph/" + chapterId);
        return ResponseEntity.created(redirectUri).body(response);
    }

    @Operation(summary = "한줄 삭제하기", description = "선택한 한줄 삭제")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{paragraphId}")
    public ResponseEntity<ApiCsResponse<Void>> selectParagraph(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long paragraphId
    ) {
        paragraphService.deleteParagraph(memberId, paragraphId);
        return ResponseEntity.ok(ApiCsResponse.success());
    }

    @Operation(summary = "한줄 신고하기", description = "아직 기능 미확정")
    @ApiResponse(responseCode = "201", description = "작성 성공")
    @PostMapping("/call/{paragraphId}") // 신고 하기
    public ResponseEntity<ApiCsResponse<Void>> reportParagraph(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long paragraphId
    ) {
        paragraphService.reportParagraph(memberId, paragraphId);

        ApiCsResponse<Void> response = ApiCsResponse.success();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
