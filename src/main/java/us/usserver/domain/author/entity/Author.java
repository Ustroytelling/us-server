package us.usserver.domain.author.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.entity.Stake;
import us.usserver.domain.chapter.entity.Score;
import us.usserver.domain.comment.entity.Comment;
import us.usserver.domain.comment.entity.CommentLike;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.entity.NovelLike;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.entity.Vote;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    @Column(name = "member_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotBlank
    @Size(max = 11)
    private String nickname;

    @Size(max = 100)
    private String introduction;

    // TODO: 프로필 사진을 설정 하지 않았을 때 default 이미지 값을 Input 예정
    @Size(max = 500)
    private String profileImg;

    @Min(1) @Max(30)
    private Integer fontSize = 15;

    @Min(1) @Max(30)
    private Integer paragraphSpace = 16;

    @Setter
    private Boolean participateNovelsPublic;

    @Setter
    private Boolean collectionNovelsPublic;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private Set<ReadNovel> readNovels = new HashSet<>();

    @OneToMany(mappedBy = "mainAuthor", cascade = CascadeType.ALL)
    private Set<Novel> createNovels = new HashSet<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Paragraph> paragraphs = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Score> scores = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Stake> stakes = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Authority> authorities = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NovelLike> novelLikes = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public void setIdForTest(Long id) {
        this.id = id;
    }

    public void addAuthority(Authority authority) {
        authority.setAuthor(this);
        this.authorities.add(authority);
    }

    public void addReadNovel(ReadNovel readNovel) {
        this.readNovels.add(readNovel);
    }
    public void deleteReadNovel(ReadNovel readNovel) {
        this.readNovels.remove(readNovel);
    }
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
    public void changeIntroduction(String introduction) {
        this.introduction = introduction;
    }
    public void changeProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
    public void changeFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }
    public void changeParagraphSpace(Integer paragraphSpace) {
        this.paragraphSpace = paragraphSpace;
    }
    public void setMember(Member member) {
        this.member = member;
    }
}
