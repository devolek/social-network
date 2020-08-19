package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.Friendship;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.enumerated.FriendshipCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    @Query("select f from Friendship f where f.srcPerson=?1 and f.dstPerson=?2 and f.status.code=?3")
    Friendship findRelation(Person srcPerson, Person dstPerson, FriendshipCode code);

    @Query("select f from Friendship f where f.srcPerson.id=?1 and f.dstPerson.id=?2 and f.status.code<>'BLOCKED'")
    Friendship findNonBLockedRelation(Integer srcPersonId, Integer dstPersonId);


    @Query("select f from Friendship f where f.srcPerson=?1 and f.status.code='FRIEND'")
    List<Friendship> findAllFriends(Person person);

    @Query("select f from Friendship f where f.dstPerson=?1 and f.status.code='REQUEST'")
    List<Friendship> findAllRequests(Person person);

    @Query("select case when count(f)>0 then true else false end from Friendship f " +
            "where f.srcPerson=?1 and f.dstPerson=?2 and f.status.code='BLOCKED'")
    Boolean isBlocked(Person srcPerson, Person dstPerson);

    @Query("select case when count(f)>0 then true else false end from Friendship f " +
            "where f.srcPerson=?2 and f.dstPerson=?1 and f.status.code='BLOCKED'")
    Boolean areYouBlocked(Person authorizedUser, Person dst);

    @Query("select case when count(f)>0 then true else false end from Friendship f " +
            "where f.srcPerson=?1 and f.dstPerson=?2 and f.status.code='FRIEND'")
    Boolean isFriend(Person srcPerson, Person dstPerson);

    @Query("select p from Person p join Friendship f on p=f.dstPerson " +
            "where f.status.code='BLOCKED' and f.srcPerson=?1")
    Set<Person> getUsersBlockedByYou(Person person);

    @Query("select p from Person p join Friendship f on p=f.srcPerson " +
            "where f.status.code='BLOCKED' and f.dstPerson=?1")
    Set<Person> getUsersThatBlockedYou(Person person);

}
