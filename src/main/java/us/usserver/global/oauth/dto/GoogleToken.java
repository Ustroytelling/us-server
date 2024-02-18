package us.usserver.global.oauth.dto;

import lombok.Data;

@Data
public class GoogleToken {
    private String token_type;
    private String access_token;
    private String refresh_token;
    private Long expires_in;
    private String scope;
}
