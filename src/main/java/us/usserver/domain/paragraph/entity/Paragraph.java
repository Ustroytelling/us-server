package us.usserver.domain.paragraph.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.global.BaseEntity;

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
public class Paragraph extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paragraph_id")
    private Long id;

    @NotBlank
    @Size(max = 300, min = 50)
    private String content;

    @Min(0)
    private int sequence;

    @NotNull
    private ParagraphStatus paragraphStatus;

    @ManyToOne
    @JoinColumn
    private Chapter chapter;

    @ManyToOne
    @JoinColumn
    private Author author;

    @OneToMany(mappedBy = "paragraph", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ParagraphLike> paragraphLikes = new HashSet<>();

    @OneToMany(mappedBy = "paragraph", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes = new HashSet<>();

    public void addParagraphLike(ParagraphLike paragraphLike) {
        this.paragraphLikes.add(paragraphLike);
    }
    public void removeParagraphLike(ParagraphLike paragraphLike) {
        this.paragraphLikes.remove(paragraphLike);
    }
    public void addVote(Vote vote) {
        this.votes.add(vote);
    }
    public void removeVote(Vote vote) {
        this.votes.remove(vote);
    }

    public void setSequenceForTest(int sequence) {
        this.sequence = sequence;
    }

    public void setParagraphStatusForTest(ParagraphStatus paragraphStatus) {
        this.paragraphStatus = paragraphStatus;
    }
}
