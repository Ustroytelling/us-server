package us.usserver.domain.author.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.member.entity.Member;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> getAuthorById(Long id);

    Optional<Author> getAuthorByMember(Member member);

    Optional<Author> getAuthorByMemberId(Long memberId);
}
