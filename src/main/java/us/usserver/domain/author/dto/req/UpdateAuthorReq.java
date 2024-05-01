package us.usserver.domain.author.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateAuthorReq(
        @Schema(description = "사용자 프로필 이미지", nullable = true, example = "증명사진.jpg")
        String profileImg,
        @Schema(description = "작가 닉네임", nullable = true, example = "소망123")
        @Size(max = 11)
        String nickname,
        @Schema(description = "작가 소개", nullable = true, example = "안녕하세요.")
        @Size(max = 100)
        String introduction,
        @Schema(description = "참여 소설 공개 여부", nullable = true, example = "True")
        Boolean participateNovelsPublic,
        @Schema(description = "소설 컬렉션 공개 여부", nullable = true, example = "False")
        Boolean collectionNovelsPublic
) {}
