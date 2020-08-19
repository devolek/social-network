package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipStatusRepo extends JpaRepository<FriendshipStatus, Integer> {
//    @Query(value = "SELECT ")
//    List<Person> findAllByCode(Person person);
}
