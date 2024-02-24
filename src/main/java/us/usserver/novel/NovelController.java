package us.usserver.novel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import us.usserver.global.ApiCsResponse;
import us.usserver.global.exception.MainAuthorIsNotMatchedException;
import us.usserver.global.exception.NovelNotFoundException;
import us.usserver.member.Member;
import us.usserver.novel.dto.AuthorDescription;
import us.usserver.novel.dto.NovelDetailInfo;
import us.usserver.novel.dto.NovelInfo;
import us.usserver.novel.dto.NovelSynopsis;
import us.usserver.global.exception.AuthorNotFoundException;
import us.usserver.novel.dto.*;

import java.net.URI;

@Tag(name = "소설 API")
@ResponseBody
@RestController
@RequestMapping("/novel")
@RequiredArgsConstructor
public class NovelController {
    //TODO: Swagger 공통적인 ApiResponse 적용 방법 찾아보기!
    private final NovelService novelService;

    @Operation(summary = "소설 생성", description = "작가가 소설을 생성하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "파티 생성 성공", content = @Content(schema = @Schema(implementation = NovelInfo.class))),
            @ApiResponse(responseCode = "400", description = "작가가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = AuthorNotFoundException.class)))
    })
    @PostMapping
    public ResponseEntity<ApiCsResponse<?>> createNovel(@AuthenticationPrincipal Member member, @Valid @RequestBody CreateNovelReq createNovelReq) {
        NovelInfo novelInfo = novelService.createNovel(member, createNovelReq);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .data(novelInfo)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "소설 정보 조회", description = "소설 기본 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소설 메인 페이지 조회",
                    content = @Content(schema = @Schema(implementation = NovelDetailInfo.class))),
            @ApiResponse(responseCode = "400", description = "소설이 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = NovelNotFoundException.class)))
    })
    @GetMapping("/{novelId}")
    public ResponseEntity<ApiCsResponse<?>> getNovelInfo(@PathVariable Long novelId) {
        NovelInfo novelInfo = novelService.getNovelInfo(novelId);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(novelInfo)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "소설 상세 정보 조회", description = "소설 상세 정보 조회(오른쪽 탭)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소설 상세 페이지 조회",
                    content = @Content(schema = @Schema(implementation = NovelDetailInfo.class))),
            @ApiResponse(responseCode = "400", description = "소설이 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = NovelNotFoundException.class)))
    })
    @GetMapping("/{novelId}/detail")
    public ResponseEntity<ApiCsResponse<?>> getNovelDetailInfo(@PathVariable Long novelId) {
        NovelDetailInfo detailInfo = novelService.getNovelDetailInfo(novelId);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(detailInfo)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "소설 줄거리 수정", description = "소설 줄거리 수정하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "줄거리 수정 성공",
                    content = @Content(schema = @Schema(implementation = NovelSynopsis.class))),
            @ApiResponse(responseCode = "400", description = "소설이 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = NovelNotFoundException.class))),
            @ApiResponse(responseCode = "406", description = "메인 작가가 아닙니다.",
                    content = @Content(schema = @Schema(implementation = MainAuthorIsNotMatchedException.class)))
    })
    @PatchMapping("/{novelId}/synopsis")
    public ResponseEntity<ApiCsResponse<?>> modifyNovelSynopsis(
            @PathVariable Long novelId,
            @Validated @RequestBody NovelSynopsis req
    ) {
        Long authorId = 500L; // TODO: 토큰에서 author 정보 가져올 예정
        String synopsis = novelService.modifyNovelSynopsis(novelId, authorId, req.getSynopsis());

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .data(synopsis)
                .build();
        return ResponseEntity.created(URI.create("")).body(response);
    }

    @Operation(summary = "앱 메인 페이지", description = "메인 페이지 컨텐츠를 불러온다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메인 페이지 불러오기 성공",
            content = @Content(schema = @Schema(implementation = GetMainPageResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ApiCsResponse<?>> getMainPageInfo(@AuthenticationPrincipal Member member) {
        GetMainPageResponse mainPageInfo = novelService.getMainPageInfo(member);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(mainPageInfo)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "우스 메인 홈", description = "메인 페이지 소설을 불러오는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소설 메인 페이지 load 성공",
            content = @Content(schema = @Schema(implementation = HomeNovelListResponse.class))),
            @ApiResponse(responseCode = "400", description = "작가가 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = AuthorNotFoundException.class)))
    })
    @GetMapping("/main")
    //TODO: 추후에 security+jwt 적용시 URL 변경 예정
    public ResponseEntity<ApiCsResponse<?>> getHomeNovelListInfo(@AuthenticationPrincipal Member member) {
        HomeNovelListResponse homeNovelList = novelService.homeNovelInfo(member);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(homeNovelList)
                .build();
        return ResponseEntity.ok(response);
    }

    //TODO: 경로 고민중..
    @Operation(summary = "소설 더보기", description = "실시간 업데이트, 신작 모아보기 더보기 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소설 더보기 load 성공",
                    content = @Content(schema = @Schema(implementation = NovelPageInfoResponse.class)))
    })

    @GetMapping("/main/more")
    public ResponseEntity<ApiCsResponse<?>> moreNovel(@Valid ConditionsOfPagination conditionsOfPagination) {
        NovelPageInfoResponse novelPageInfoResponse = novelService.moreNovel(conditionsOfPagination);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(novelPageInfoResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "읽은 소설 더보기", description = "내가 읽었던 소설 더보기 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "읽은 소설 더보기 laod 성공",
                    content = @Content(schema = @Schema(implementation = NovelPageInfoResponse.class)))
    })
    @GetMapping("/main/more/read")
    public ResponseEntity<ApiCsResponse<?>> readNovel(@AuthenticationPrincipal Member member,
                                                      @Valid ReadInfoOfNovel readInfoOfNovel) {
        NovelPageInfoResponse novelPageInfoResponse = novelService.readMoreNovel(member, readInfoOfNovel);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(novelPageInfoResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "소설 검색", description = "사용자 소설 검색 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소설 검색 성공",
            content = @Content(schema = @Schema(implementation = NovelPageInfoResponse.class)))
    })
    @GetMapping("/search")
    public ResponseEntity<ApiCsResponse<?>> searchNovel(@AuthenticationPrincipal Member member, @Valid SearchNovelReq searchNovelReq) {
        NovelPageInfoResponse novelPageInfoResponse = novelService.searchNovel(member, searchNovelReq);
        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(novelPageInfoResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "작가 소개 수정", description = "메인 작가 소개글 수정하ㅣㄱ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소설 검색 성공",
                    content = @Content(schema = @Schema(implementation = AuthorDescription.class)))
    })
    @PatchMapping("/{novelId}/author-description")
    public ResponseEntity<ApiCsResponse<?>> modifyAuthorDescription(
            @PathVariable Long novelId,
            @Validated @RequestBody AuthorDescription req
    ) {
        Long authorId = 500L; // TODO: 토큰에서 author 정보 가져올 예정
        AuthorDescription description = novelService.modifyAuthorDescription(novelId, authorId, req);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(description)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "검색 페이지 Keyword", description = "인기검색어, 최근 검색어 목록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 Keyword load 성공",
            content = @Content(schema = @Schema(implementation = SearchKeywordResponse.class)))
    })
    @GetMapping("/search-keyword")
    public ResponseEntity<ApiCsResponse<?>> getSearchWord(@AuthenticationPrincipal Member member) {
        SearchKeywordResponse searchWordResponse = novelService.searchKeyword(member);
        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(searchWordResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    
    @Operation(summary = "검색 페이지 Keyword Delete", description = "인기 검색어, 최근 검색어 목록 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 Keyword Delete 성공")
    })
    @DeleteMapping("/search-keyword")
    public ResponseEntity<ApiCsResponse<?>> deleteAllSearchWord(@AuthenticationPrincipal Member member) {
        novelService.deleteSearchKeyword(member);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
