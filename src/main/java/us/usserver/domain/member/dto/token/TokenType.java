package us.usserver.domain.member.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {
    REFRESH_TOKEN("RefreshToken"),
    ACCESS_TOKEN("AccessToken");

    private final String name;
}
