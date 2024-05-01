package us.usserver.domain.member.dto.member;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import us.usserver.domain.member.constant.OauthProvider;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class AppleMember implements OauthMember {
    @JsonProperty("sub")
    private String sub;

    @JsonProperty("email")
    private String email;

    @JsonProperty("name")
    private String name;

    @Override
    public String getSocialId() {
        return sub;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getNickname() {
        return name;
    }

    @Override
    public OauthProvider getOauthProvider() {
        return OauthProvider.APPLE;
    }
}
