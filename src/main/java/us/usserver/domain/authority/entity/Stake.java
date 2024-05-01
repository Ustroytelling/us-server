package us.usserver.domain.authority.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import us.usserver.domain.author.entity.Author;
import us.usserver.global.BaseEntity;
import us.usserver.domain.novel.entity.Novel;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stake extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stake_id")
    private Long id;

    @Setter
    @NotNull
    private Float percentage;

    @ManyToOne
    @JoinColumn
    private Novel novel;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
}
