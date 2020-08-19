package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.CommentLike;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {
    CommentLike findCommentLikeById(Integer id);
    CommentLike findCommentLikeByCommentAndPerson(PostComment comment, Person person);
}
