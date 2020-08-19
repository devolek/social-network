package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Integer> {

    NotificationSettings findByPersonId(int userId);
}
