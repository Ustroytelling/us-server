package us.usserver.global.response.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class ExceptionMessage {
    public static final String NOVEL_NOT_FOUND = "해당 소설이 존재 하지 않습니다.";
    public static final String CHAPTER_NOT_FOUND = "해당 화가 존재 하지 않습니다.";
    public static final String PARAGRAPH_NOT_FOUND = "해당 한줄이 존재 하지 않습니다.";
    public static final String MEMBER_NOT_FOUND = "해당 멤버가 존재 하지 않습니다.";
    public static final String AUTHOR_NOT_FOUND = "해당 작가가 존재 하지 않습니다.";
    public static final String COMMENT_NOT_FOUND = "해당 댓글이 존재 하지 않습니다.";
    public static final String VOTE_NOT_FOUND = "해당 투표가 존재 하지 않습니다.";
    public static final String VALID_MODEL_ATTRIBUTE_NOT_FOUND = "ModelAttribute 값이 유효하지 않습니다.";
    public static final String VALID_REQUEST_BODY_NOT_FOUND = "RequestBody 값이 유효하지 않습니다.";
    public static final String MAIN_AUTHOR_NOT_MATCHED = "해당 작가와 메인 작가가 일치 하지 않습니다.";
    public static final String AUTHOR_NOT_AUTHORIZED = "허가 되지 않은 작가입니다.";
    public static final String PARAGRAPH_LENGTH_OUT_OF_RANGE = "해당 한줄의 길이가 범위를 벗어났습니다. (50 ~ 300자)";
    public static final String COMMENT_LENGTH_OUT_OF_RANGE = "해당 댓글의 길이가 범위를 벗어났습니다. (1 ~ 300자)";
    public static final String SCORE_OUT_OF_RANGE = "1 ~ 10 사이의 평점을 입력해 주세요.";
    public static final String VOTE_ONLY_ONE_PARAGRAPH = "하나의 한줄에만 투표할 수 있습니다.";
    public static final String LIKE_DUPLICATED = "이미 좋아요를 누르셨습니다.";
    public static final String TOKEN_EXPIRED = "해당 토큰이 만료되었습니다.";
    public static final String TOKEN_VERIFICATION = "해당 토큰이 유효하지 않습니다.";
    public static final String TOKEN_NOT_FOUND = "해당 토큰이 존재 하지 않습니다.";
    public static final String FONT_SIZE_OUT_OF_RANGE = "폰트 크기는 1 ~ 30 사이입니다.";
    public static final String PARAGRAPH_SPACE_OUT_OF_RANGE = "단락 크기는 1 ~ 30 사이입니다.";
    public static final String UNSUPPORTED_SOCIAL_PROVIDER = "지원하는 소셜 프로바이더가 아닙니다.";
    public static final String PREVIOUS_CHAPTER_IS_IN_PROGRESS = "이전 화가 아직 작성중입니다.";
    public static final String LIKE_ONLY_SELECTED_PARAGRAPH = "좋아요는 선택된 한줄에만 가능합니다.";
    public static final String READ_NOVEL_NOT_FOUND = "해당 읽은 소설이 존재하지 않습니다.";
    public static final String SCORE_ALREADY_ENTERED = "평점이 이미 입력되었습니다.";
    public static final String PAGE_INDEX_OUT_OF_RANGE = "페이지 인덱스는 0 ~ INTMAX 사이입니다.";
    public static final String COMMENT_LIKE_NOT_FOUND = "좋아요 하지 않은 댓글입니다.";
    public static final String PARAGRAPH_LIKE_NOT_FOUND = "좋아요 하지 않은 단락입니다.";
}
