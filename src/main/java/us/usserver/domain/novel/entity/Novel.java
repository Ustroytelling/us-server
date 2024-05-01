package us.usserver.domain.novel.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.entity.Stake;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.comment.entity.Comment;
import us.usserver.domain.novel.constant.*;
import us.usserver.global.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Novel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "novel_id")
    private Long id;

    @NotBlank @Size(max = 16)
    private String title;

    @NotBlank @Size(max = 500)
    private String thumbnail;

    @NotBlank @Size(max = 300)
    private String synopsis;

    @NotBlank @Size(max = 300)
    private String authorDescription;

    @NotNull @Enumerated(EnumType.STRING)
    private Genre genre;

    @NotNull @Enumerated(EnumType.STRING)
    private AgeRating ageRating;

    @NotNull @Enumerated(EnumType.STRING)
    private NovelStatus status;

    @NotNull
    private Integer hit;

    private Float score;

    @NotNull
    private Integer participantCnt;

    @NotNull
    private LocalDateTime recentlyUpdated;

    @NotNull @Enumerated(EnumType.STRING)
    private NovelSize novelSize;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Hashtag> hashtags;

    @ManyToOne @JoinColumn(name = "author_id")
    private Author mainAuthor;

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL)
    private List<Chapter> chapters = new ArrayList<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL)
    private List<Stake> stakes = new ArrayList<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL)
    private List<Authority> authorities = new ArrayList<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL)
    private List<NovelLike> novelLikes = new ArrayList<>();

    public void changeSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    public void changeAuthorDescription(String authorDescription) {
        this.authorDescription = authorDescription;
    }
    public void upHitCnt() {
        this.hit += 1;
    }

    public void addChapter(Chapter chapter) {
        this.chapters.add(chapter);
        this.recentlyUpdated = LocalDateTime.now();
    }

    public void setTitleForTest(String title) {
        this.title = title;
    }
}
