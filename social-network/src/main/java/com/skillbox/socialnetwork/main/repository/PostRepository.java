package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findPostById(Integer id);

    @Query(value = "select p from Post p where " +
            "p.isBlocked = false and p.author not in ?1 and p.author.status<>'DELETED' order by p.time desc")
    List<Post> getFeedsWithBlocked(Set<Person> blockedUsers, Pageable pageable);

    @Query(value = "select p from Post p where " +
            "p.isBlocked = false and p.author.status<>'DELETED' order by p.time desc")
    List<Post> getFeeds(Pageable pageable);

    @Query(value = "select count(p) from Post p where " +
            "p.isBlocked = false and p.author.status<>'DELETED' order by p.time desc")
    int countPosts();

    @Query(value = "select count(p) from Post p where " +
            "p.isBlocked = false and p.author not in ?1 and p.author.status<>'DELETED' order by p.time desc")
    int countPostsWithBlockedUsers(Set<Person> users);

    @Query(value = "select p from Post p join p.tags t where p.author.status<>'DELETED' and " +
            "(p.postText like %?1% or p.title like %?1%) and " +
            "(p.time between ?2 and ?3) and " +
            "(p.author.firstName like %?4% or p.author.lastName like %?4%) and " +
            "p.isBlocked = false and t.tag in ?5 group by p order by p.time desc")
    List<Post> searchPostsWithTags(String text, Date dateFrom, Date dateTo, String author, List<String> tags);

    @Query(value = "select p from Post p where p.author.status<>'DELETED' and " +
            "(p.postText like %?1% or p.title like %?1%) and " +
            "(p.time between ?2 and ?3) and " +
            "(p.author.firstName like %?4% or p.author.lastName like %?4%) and " +
            "p.isBlocked = false order by p.time desc")
    List<Post> searchPosts(String text, Date dateFrom, Date dateTo, String author);

}
