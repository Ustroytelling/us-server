package us.usserver.domain.paragraph.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.domain.author.entity.Author;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paragraph_like_id")
    private Long id;

    @ManyToOne
    @JoinColumn
    private Author author;

    @ManyToOne
    @JoinColumn
    private Paragraph paragraph;
}
