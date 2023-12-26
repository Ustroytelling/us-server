package us.usserver.global;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class ExceptionMessage {
    public static final String Novel_NOT_FOUND = "해당 소설이 존재 하지 않습니다.";
    public static final String Chapter_NOT_FOUND = "해당 화가 존재 하지 않습니다.";
    public static final String Paragraph_NOT_FOUND = "해당 한줄이 존재 하지 않습니다.";
    public static final String Member_NOT_FOUND = "해당 멤버가 존재 하지 않습니다.";
    public static final String Author_NOT_FOUND = "해당 작가가 존재 하지 않습니다.";
    public static final String Main_Author_NOT_MATCHED = "해당 작가와 메인 작가가 일치하지 않습니다.";
    public static final String Valid_ModelAttribute_NOT_FOUND = "ModelAttribute 값이 유효하지 않습니다.";
    public static final String Valid_RequestBody_NOT_FOUND = "RequestBody 값이 유효하지 않습니다.";
    public static final String Token_EXPIRED = "해당 토큰이 만료되었습니다.";
    public static final String Token_VERIFICATION = "해당 토큰이 유효하지 않습니다.";
    public static final String Token_NOT_FOUND = "해당 토큰이 존재 하지 않습니다.";
}
