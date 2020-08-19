package com.skillbox.socialnetwork.main.util;

import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class LastOnlineTimeAdjuster {


    private static PersonRepository personRepository;

    @Autowired
    public LastOnlineTimeAdjuster(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public static void refreshLastOnlineTime(Person person) {
        person.setLastOnlineTime(new Date());
        personRepository.save(person);
    }
}
