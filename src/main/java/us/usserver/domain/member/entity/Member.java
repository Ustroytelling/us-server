package us.usserver.domain.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.domain.author.entity.Author;
import us.usserver.global.BaseEntity;
import us.usserver.domain.member.constant.Role;
import us.usserver.domain.member.constant.OauthProvider;
import us.usserver.domain.member.constant.Gender;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Author author;

    @NotBlank
    private String socialId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @NotBlank
    private String email;

    @NotNull
    private Integer age;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    private Member(String socialId, OauthProvider oauthProvider, String email, Integer age, Gender gender, Role role) {
        this.socialId = socialId;
        this.oauthProvider = oauthProvider;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.role = role;
    }

    public static Member createMemberInSocialLogin(String socialId, OauthProvider oauthProvider, String email, Integer age, Gender gender, Role role) {
        return new Member(socialId, oauthProvider, email, age, gender, role);
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
