package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.Notification;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.enumerated.NotificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    //стандартный метод репозитория удаления по ID не работал

    @Query("delete from Notification n where n = ?1")
    @Transactional
    @Modifying
    void deleteNotification(Notification notification);

    @Query("select case when count(n)>0 then true else false end from Notification n where n.person=?2 and n.entityAuthor=?1 and n.type=?3 and function('date', ?4)=function('date', n.sentTime) ")
    boolean alreadyExists(Person src, Person dst, NotificationCode code, Date date);
}
