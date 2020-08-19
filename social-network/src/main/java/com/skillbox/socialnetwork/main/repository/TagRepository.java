package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    List<Tag> findAllByTagContaining(String name);

    Tag findFirstByTagIgnoreCase(String name);

    boolean existsByTagIgnoreCase(String name);
}
