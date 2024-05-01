package us.usserver.domain.comment.repository;

import org.springframework.data.repository.NoRepositoryBean;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.comment.entity.Comment;

import java.util.List;

@NoRepositoryBean
public interface CommentRepositoryDSL {
    List<Comment> getTop3CommentOfChapter(Chapter chapter);
}
