package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    Person findByEmail(String email);

    Person findByConfirmationCode(String code);

    Person findPersonById(Integer id);

    List<Person> findPeopleByCity(String city);

    @Query("select p from Person p where " +
            "(:firstName is null or p.firstName like %:firstName%) and " +
            "(:lastName is null or p.lastName like %:lastName%) and " +
            "(:cityName is null or p.city like %:cityName%) and " +
            "(:countryName is null or p.country like %:countryName%) and " +
            "(p.birthDate is null or p.birthDate between :dateFrom and :dateTo) and " +
            "(p.status <> com.skillbox.socialnetwork.main.model.enumerated.Status.DELETED)")
    List<Person> search(@Param("firstName") String name, @Param("lastName") String surname,
                        @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
                        @Param("cityName") String cityName, @Param("countryName") String countryName);

}
