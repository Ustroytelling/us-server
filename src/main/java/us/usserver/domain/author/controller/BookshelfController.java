package us.usserver.domain.author.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import us.usserver.domain.author.dto.res.BookshelfDefaultResponse;
import us.usserver.domain.author.service.BookshelfService;
import us.usserver.global.response.ApiCsResponse;

@Tag(name = "보관함(소설) API")
@ResponseBody
@RestController
@RequestMapping("/bookshelf")
@RequiredArgsConstructor
public class BookshelfController {
    private final BookshelfService bookshelfService;

    @Operation(summary = "최근에 본 소설 조회", description = "보관함 내 최근에 본 소설을 조회하는 기능")
    @ApiResponse(responseCode = "200", description = "최근에 본 소설 조회 성공",
            content = @Content(schema = @Schema(implementation = BookshelfDefaultResponse.class)))
    @GetMapping("/viewed") // 내가 최근에 본 소설
    public ApiCsResponse<BookshelfDefaultResponse> recentViewedNovels(@AuthenticationPrincipal Long memberId) {
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.recentViewedNovels(memberId);
        return ApiCsResponse.success(bookshelfDefaultResponse);
    }

    @DeleteMapping("/viewed/{readNovelId}") // 내가 최근에 본 소설 삭제
    public ApiCsResponse<Void> deleteRecentViewedNovels(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long readNovelId
    ) {
        bookshelfService.deleteRecentViewedNovels(memberId, readNovelId);
        return ApiCsResponse.success();
    }

    @Operation(summary = "내가 생성한 소설 조회", description = "보관함 내 내가 생성한 소설을 조회하는 기능")
    @ApiResponse(responseCode = "200", description = "내가 생성한 소설 조회 성공",
            content = @Content(schema = @Schema(implementation = BookshelfDefaultResponse.class)))
    @GetMapping("/created") // 내가 생성한 소설
    public ApiCsResponse<BookshelfDefaultResponse> createdNovels(@AuthenticationPrincipal Long memberId) {
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.createdNovels(memberId);
        return ApiCsResponse.success(bookshelfDefaultResponse);
    }

    @DeleteMapping("/created/{novelId}") // 내가 생성한 소설 삭제
    public ApiCsResponse<Void> deleteCreatedNovels(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long novelId
    ) {
        bookshelfService.deleteCreatedNovels(memberId, novelId);
        return ApiCsResponse.success();
    }


    @Operation(summary = "내가 참여한 소설 조회", description = "보관함 내 내가 참여한 소설을 조회하는 기능")
    @ApiResponse(responseCode = "200", description = "내가 참여한 소설 조회 성공",
            content = @Content(schema = @Schema(implementation = BookshelfDefaultResponse.class)))
    @GetMapping("/joined") // 내가 참여한 소설
    public ApiCsResponse<BookshelfDefaultResponse> joinedNovels(@AuthenticationPrincipal Long memberId) {
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.joinedNovels(memberId);
        return ApiCsResponse.success(bookshelfDefaultResponse);
    }

    @DeleteMapping("/joined/{novelId}") // 내가 참여한 소설 삭제
    public ApiCsResponse<Void> deleteJoinedNovels(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long novelId
    ) {
        bookshelfService.deleteJoinedNovels(memberId, novelId);
        return ApiCsResponse.success();
    }

    @Operation(summary = "내가 좋아요한 소설 조회", description = "보관함 내 내가 좋아요한 소설을 조회하는 기능")
    @ApiResponse(responseCode = "200", description = "내가 좋아요한 소설 조회 성공",
            content = @Content(schema = @Schema(implementation = BookshelfDefaultResponse.class)))
    @GetMapping("/liked") // 내가 좋아요 한 소설
    public ApiCsResponse<BookshelfDefaultResponse> likedNovels(@AuthenticationPrincipal Long memberId) {
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.likedNovels(memberId);
        return ApiCsResponse.success(bookshelfDefaultResponse);
    }

    @DeleteMapping("/liked/{novelId}") // 내가 좋아요 한 소설 삭제
    public ApiCsResponse<Void> deleteLikedNovels(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long novelId
    ) {
        bookshelfService.deleteLikedNovels(memberId, novelId);
        return ApiCsResponse.success();
    }
}
