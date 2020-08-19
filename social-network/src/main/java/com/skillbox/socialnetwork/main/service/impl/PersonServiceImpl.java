package com.skillbox.socialnetwork.main.service.impl;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.skillbox.socialnetwork.main.aspect.MethodLogWithTime;
import com.skillbox.socialnetwork.main.dto.GeoIP.GeoIP;
import com.skillbox.socialnetwork.main.dto.auth.request.RegisterRequestDto;
import com.skillbox.socialnetwork.main.dto.universal.Response;
import com.skillbox.socialnetwork.main.dto.universal.ResponseFactory;
import com.skillbox.socialnetwork.main.exception.InvalidRequestException;
import com.skillbox.socialnetwork.main.exception.not.found.PersonNotFoundException;
import com.skillbox.socialnetwork.main.model.NotificationSettings;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.Role;
import com.skillbox.socialnetwork.main.model.enumerated.ERole;
import com.skillbox.socialnetwork.main.model.enumerated.Permission;
import com.skillbox.socialnetwork.main.model.enumerated.Status;
import com.skillbox.socialnetwork.main.repository.NotificationSettingsRepository;
import com.skillbox.socialnetwork.main.repository.PersonRepository;
import com.skillbox.socialnetwork.main.repository.RoleRepository;
import com.skillbox.socialnetwork.main.service.PersonService;
import com.skillbox.socialnetwork.main.util.LastOnlineTimeAdjuster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository repository, BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository, NotificationSettingsRepository notificationSettingsRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.notificationSettingsRepository = notificationSettingsRepository;
    }

    @Override
    public List<Person> getAll() {
        return repository.findAll();
    }

    @Override
    public Person findByEmail(String email) {
        Person person = repository.findByEmail(email);
        if (person == null) {
            throw new PersonNotFoundException(email);
        }
        return person;
    }

    @Override
    public Person findById(Integer id) {
        Person person = repository.findPersonById(id);
        if (person == null || person.getStatus().equals(Status.DELETED)) {
            throw new PersonNotFoundException("Данный пользователь удалил свою страницу.");
        }
        return person;
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Search person result")
    public List<Person> search(String name, String surname, Date dateFrom, Date dateTo, String cityName, String countryName) {
        return repository.search(name, surname, dateFrom, dateTo, cityName, countryName);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    @MethodLogWithTime(fullMessage = "New account registered")
    public Response registration(RegisterRequestDto dto, GeoIP location) throws RuntimeException, IOException, GeoIp2Exception {
        checkUserLogin(dto.getEmail());
        checkUserRegisterPassword(dto.getPassword1(), dto.getPassword2());

        Person person = new Person();
        person.setEmail(dto.getEmail());
        person.setPassword(passwordEncoder.encode(dto.getPassword1()));
        person.setFirstName(dto.getFirstName());
        person.setLastName(dto.getLastName());
        person.setConfirmationCode(dto.getCode());
        person.setCity(location.getCity());
        person.setCountry(location.getCountry());
        person.setIsBlocked(false);
        person.setMessagesPermission(Permission.ALL);
        person.setStatus(Status.REGISTERED);
        person.setIsApproved(false);
        person.setRoles(getBasePermission());
        person.setLastOnlineTime(new Date());
        person.setPhoto("/static/img/user/default-avatar.png");
        repository.save(person);

        //установка настроек оповещений
        NotificationSettings settings = new NotificationSettings(person, true, true, true, true, true, true);
        notificationSettingsRepository.save(settings);

        return ResponseFactory.responseOk();
    }

    private void checkUserRegisterPassword(String password1, String password2) throws RuntimeException {
        if (!password1.equals(password2)) {
            throw new InvalidRequestException("Пароли не совпадают");
        }
    }

    private void checkUserLogin(String email) throws RuntimeException {
        if (repository.findByEmail(email) != null) {
            throw new InvalidRequestException("Данный email уже зарегистрирован");
        }
    }

    private List<Role> getBasePermission() {
        Role roleUser = roleRepository.findByName(ERole.ROLE_USER);
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);
        return userRoles;
    }

    @MethodLogWithTime(userAuth = true, fullMessage = "Logout made")
    public void logout(Person person) {
        LastOnlineTimeAdjuster.refreshLastOnlineTime(person);
    }

    @Override
    public Person save(Person person) {
        return repository.save(person);
    }

    @Override
    public List<Person> findByCity(String city) {
        return repository.findPeopleByCity(city);
    }

    @Override
    @MethodLogWithTime(userAuth = true, fullMessage = "Account deleted")
    public void delete(Person person) {
        person.setConfirmationCode(person.getEmail());
        String dummyEmail = (100000 * Math.random()) + "@deleted.com";
        for (;;) {
            if (repository.findByEmail(dummyEmail) == null)
                break;
            dummyEmail = (100000 * Math.random()) + "@deleted.com";
        }
        person.setEmail(dummyEmail);
        person.setPassword("12345678");
        person.setFirstName("DELETED");
        person.setLastName("");
        person.setCity("");
        person.setCountry("");
        person.setStatus(Status.DELETED);
        person.setLastOnlineTime(new Date());
        person.setPhoto("/static/img/user/deleted-user.png");
        repository.save(person);
    }

    @Override
    @MethodLogWithTime(fullMessage = "Account restored")
    public void restore(String email) {
        Person person = repository.findByConfirmationCode(email);
        person.setStatus(Status.REGISTERED);
        person.setEmail(email);
        person.setPhoto("/static/img/user/default-avatar.png");
    }

    @Override
    public String getAuthUserEmail()
    {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
