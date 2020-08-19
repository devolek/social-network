package com.skillbox.socialnetwork.main.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.skillbox.socialnetwork.main.dto.GeoIP.GeoIP;
import com.skillbox.socialnetwork.main.dto.auth.request.RegisterRequestDto;
import com.skillbox.socialnetwork.main.dto.universal.Response;
import com.skillbox.socialnetwork.main.model.Person;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface PersonService {

    List<Person> getAll();

    Person findByEmail(String email);

    Person findById(Integer id);

    List<Person> search(String name, String surname,
                        Date dateFrom, Date dateTo,
                        String cityName, String countryName);

    void delete(Integer id);

    Response registration(RegisterRequestDto dto, GeoIP location) throws IOException, GeoIp2Exception;

    void logout(Person person);

    Person save(Person person);

    List<Person> findByCity(String city);

    void delete(Person person);

    void restore (String email);

    String getAuthUserEmail();
}