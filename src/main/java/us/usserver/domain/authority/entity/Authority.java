package us.usserver.domain.authority.entity;

import jakarta.persistence.*;
import lombok.*;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.novel.entity.Novel;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn
    private Novel novel;

    @Setter
    @ManyToOne
    @JoinColumn
    private Author author;

    public void takeNovel(Novel novel) {
        this.novel = novel;
        novel.getAuthorities().add(this);
    }
}
