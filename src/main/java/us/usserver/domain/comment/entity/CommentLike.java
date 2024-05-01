package us.usserver.domain.comment.entity;

import jakarta.persistence.*;
import lombok.*;
import us.usserver.domain.author.entity.Author;

@Entity
@Getter
@Builder
@EqualsAndHashCode // remove 때 equals 비교를 위해 사용
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
