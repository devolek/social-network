package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.Dialog;
import com.skillbox.socialnetwork.main.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface DialogRepository extends JpaRepository<Dialog, Integer> {
    @Query("select d from Dialog d join d.people p where p=?1 and p.lastName like %?2%")
    List<Dialog> getDialogs(Person person, String query);

    @Query("select count(distinct m) from Message m " +
            "join Dialog d on m.dialog.id=d.id " +
            "where m.readStatus=com.skillbox.socialnetwork.main.model.enumerated.ReadStatus.SENT " +
            "and m.recipient=?1")
    int countUnreadMessages(Person person);

    @Modifying
    @Transactional
    @Query("update Dialog d set d.isFrozen=?2 where d.id=?1")
    void updateDialogStatus(int id, boolean isFrozen);
}
