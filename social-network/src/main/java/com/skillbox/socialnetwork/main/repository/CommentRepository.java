package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<PostComment, Integer> {
    PostComment findPostCommentById(int id);

    @Query(value = "select * from post_comments ORDER BY time desc limit ?1, ?2", nativeQuery = true)
    List<PostComment> limitQuery(Integer offset, Integer limit);

}