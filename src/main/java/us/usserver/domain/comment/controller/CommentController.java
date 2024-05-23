package us.usserver.domain.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import us.usserver.domain.comment.dto.CommentContent;
import us.usserver.domain.comment.dto.CommentInfo;
import us.usserver.domain.comment.dto.GetCommentRes;
import us.usserver.domain.comment.service.CommentService;
import us.usserver.global.response.ApiCsResponse;

import java.net.URI;

@Tag(name = "댓글 API")
@ResponseBody
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "소설의 댓글 불러오기", description = "한 소설에서 작성된 댓글 전부 불러오기")
    @ApiResponse(responseCode = "200", description = "댓글 불러오기 성공",
            content = @Content(schema = @Schema(implementation = GetCommentRes.class)))
    @GetMapping("/novel/{novelId}")
    public ApiCsResponse<GetCommentRes> getCommentsOfNovel(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long novelId,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {
        GetCommentRes comments = commentService.getCommentsOfNovel(novelId, page, memberId);
        return ApiCsResponse.success(comments);
    }

    @Operation(summary = "회차의 댓글 불러오기", description = "한 회차에서 작성된 댓글 전부 불러오기")
    @ApiResponse(responseCode = "200", description = "댓글 불러오기 성공",
            content = @Content(schema = @Schema(implementation = GetCommentRes.class)))
    @GetMapping("/chapter/{chapterId}")
    public ApiCsResponse<GetCommentRes> getCommentsOfChapter(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long chapterId,
            @RequestParam(name = "page", defaultValue = "0") Integer page) {
        GetCommentRes comments = commentService.getCommentsOfChapter(chapterId, page, memberId);
        return ApiCsResponse.success(comments);
    }

    @Operation(summary = "소설에 댓글 작성하기", description = "한 소설에 대한 댓글 작성하기")
    @ApiResponse(responseCode = "201", description = "댓글 작성하기 성공",
            content = @Content(schema = @Schema(implementation = CommentInfo.class)))
    @PostMapping("/novel/{novelId}")
    public ResponseEntity<ApiCsResponse<CommentInfo>> postCommentOnNovel(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long novelId,
            @Validated @RequestBody CommentContent commentContent
    ) {
        CommentInfo comment = commentService.writeCommentOnNovel(novelId, memberId, commentContent);

        ApiCsResponse<CommentInfo> response = ApiCsResponse.success(comment);
        URI redirectUri = URI.create("/novel/" + novelId);

        return ResponseEntity.created(redirectUri).body(response);
    }

    @Operation(summary = "회차에 댓글 작성하기", description = "한 회차에 대한 댓글 작성하기")
    @ApiResponse(responseCode = "201", description = "댓글 작성하기 성공",
            content = @Content(schema = @Schema(implementation = CommentInfo.class)))
    @PostMapping("/chapter/{chapterId}")
    public ResponseEntity<ApiCsResponse<CommentInfo>> postCommentOnChapter(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long chapterId,
            @Validated @RequestBody CommentContent commentContent
    ) {
        CommentInfo comment = commentService.writeCommentOnChapter(chapterId, memberId, commentContent);

        ApiCsResponse<CommentInfo> response = ApiCsResponse.success(comment);
        URI redirectUri = URI.create("/chapter/" + chapterId);
        return ResponseEntity.created(redirectUri).body(response);
    }

    @Operation(summary = "내가 작성한 댓글 불러오기", description = "내가 작성한 모든 댓글 불러오기")
    @ApiResponse(responseCode = "200", description = "댓글 불러오기 성공",
            content = @Content(schema = @Schema(implementation = GetCommentRes.class)))
    @GetMapping("/author")
    public ApiCsResponse<GetCommentRes> getCommentsOfAuthor(@AuthenticationPrincipal Long memberId) {
        GetCommentRes comments = commentService.getCommentsByAuthor(memberId);
        return ApiCsResponse.success(comments);
    }

    @Operation(summary = "작성한 댓글 삭제하기", description = "내가 작성한 댓글 삭제하기")
    @ApiResponse(responseCode = "204", description = "댓글 삭제하기 성공",
            content = @Content(schema = @Schema(implementation = String.class)))
    @DeleteMapping("/{commentId}")
    public ApiCsResponse<Void> deleteComment(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId, memberId);
        return ApiCsResponse.success();
    }
}
