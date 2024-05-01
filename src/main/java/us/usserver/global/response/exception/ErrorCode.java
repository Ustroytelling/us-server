package us.usserver.global.response.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 작가 관련
    AUTHOR_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionMessage.AUTHOR_NOT_FOUND),
    AUTHOR_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, ExceptionMessage.AUTHOR_NOT_AUTHORIZED),
    MAIN_AUTHOR_NOT_MATCHED(HttpStatus.BAD_REQUEST, ExceptionMessage.MAIN_AUTHOR_NOT_MATCHED),
    READ_NOVEL_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionMessage.READ_NOVEL_NOT_FOUND),
    // 챕터 관련
    CHAPTER_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionMessage.CHAPTER_NOT_FOUND),
    SCORE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, ExceptionMessage.SCORE_OUT_OF_RANGE),
    SCORE_ALREADY_ENTERED(HttpStatus.BAD_REQUEST, ExceptionMessage.SCORE_ALREADY_ENTERED),
    PREVIOUS_CHAPTER_IS_IN_PROGRESS(HttpStatus.BAD_REQUEST, ExceptionMessage.PREVIOUS_CHAPTER_IS_IN_PROGRESS),
    // 문단 관련
    PARAGRAPH_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionMessage.PARAGRAPH_NOT_FOUND),
    PARAGRAPH_LENGTH_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, ExceptionMessage.PARAGRAPH_LENGTH_OUT_OF_RANGE),
    PARAGRAPH_SPACE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, ExceptionMessage.PARAGRAPH_SPACE_OUT_OF_RANGE),
    PARAGRAPH_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionMessage.PARAGRAPH_LIKE_NOT_FOUND),

    // 소설 관련
    NOVEL_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionMessage.NOVEL_NOT_FOUND),
    // 댓글 관련
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionMessage.COMMENT_NOT_FOUND),
    COMMENT_LENGTH_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, ExceptionMessage.COMMENT_LENGTH_OUT_OF_RANGE),


    // 투표 관련
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionMessage.VOTE_NOT_FOUND),
    VOTE_ONLY_ONE_PARAGRAPH(HttpStatus.BAD_REQUEST, ExceptionMessage.VOTE_ONLY_ONE_PARAGRAPH),
    // 좋아요 관련
    LIKE_DUPLICATED(HttpStatus.BAD_REQUEST, ExceptionMessage.LIKE_DUPLICATED),
    COMMENT_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionMessage.COMMENT_LIKE_NOT_FOUND),

    // 멤버 관련
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionMessage.MEMBER_NOT_FOUND),
    FONT_SIZE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, ExceptionMessage.FONT_SIZE_OUT_OF_RANGE),
    // 토큰 관련
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, ExceptionMessage.TOKEN_EXPIRED),
    TOKEN_VERIFICATION(HttpStatus.UNAUTHORIZED, ExceptionMessage.TOKEN_VERIFICATION),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionMessage.TOKEN_NOT_FOUND),
    UNSUPPORTED_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, ExceptionMessage.UNSUPPORTED_SOCIAL_PROVIDER),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

    private final HttpStatus status;
    private final String message;
    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
