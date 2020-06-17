package com.skillbox.socialnetwork.main.service.impl;

import com.skillbox.socialnetwork.main.dto.auth.request.RegisterRequestDto;
import com.skillbox.socialnetwork.main.dto.universal.BaseResponse;
import com.skillbox.socialnetwork.main.dto.universal.MessageResponseDto;
import com.skillbox.socialnetwork.main.dto.universal.Response;
import com.skillbox.socialnetwork.main.exception.not.found.PersonNotFoundException;
import com.skillbox.socialnetwork.main.exception.user.input.UserInputException;
import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.Role;
import com.skillbox.socialnetwork.main.model.enumerated.ERole;
import com.skillbox.socialnetwork.main.model.enumerated.Permission;
import com.skillbox.socialnetwork.main.repository.PersonRepository;
import com.skillbox.socialnetwork.main.repository.RoleRepository;
import com.skillbox.socialnetwork.main.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository repository, BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Person> getAll() {
        return repository.findAll();
    }

    @Override
    public Person findByEmail(String email) {
        Person person = repository.findByEmail(email);
        if(person == null){
            throw new PersonNotFoundException("Данный email не найден", "invalid_request");
        }
        return person;
    }

    @Override
    public Person findById(Integer id) {
        return repository.findPersonById(id);
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public Response registration(RegisterRequestDto dto) throws RuntimeException{
        checkUserLogin(dto.getEmail());
        checkUserRegisterPassword(dto.getPassword1(), dto.getPassword2());

        Person person = new Person();
        person.setEmail(dto.getEmail());
        person.setPassword(passwordEncoder.encode(dto.getPassword1()));
        person.setFirstName(dto.getFirstName());
        person.setLastName(dto.getLastName());
        person.setConfirmationCode(dto.getCode());
        person.setIsBlocked(false);
        person.setMessagesPermission(Permission.ALL);
        person.setIsApproved(true);
        person.setRoles(getBasePermission());
        person.setLastOnlineTime(new Date());
        person.setPhoto("/static/img/user/default-avatar.png");
        repository.save(person);

        return new BaseResponse(new MessageResponseDto("ok"));
    }

    private void checkUserRegisterPassword(String password1, String password2) throws RuntimeException{
        if(!password1.equals(password2)){
            throw new UserInputException("invalid_request", "Пароль указан некорректно");
        }
    }

    private void checkUserLogin(String email) throws RuntimeException{
        if(repository.findByEmail(email) != null){
            throw new PersonNotFoundException("invalid_request", "Данный email уже зарегистрирован");
        }
    }

    private List<Role> getBasePermission() {
        Role roleUser = roleRepository.findByName(ERole.ROLE_USER);
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);
        return userRoles;
    }

    public void logout(Person person) {
        person.setLastOnlineTime(new Date());
        log.info("IN logout - user: {} logged out", repository.save(person));
    }

    @Override
    public Person save(Person person) {
        return repository.save(person);
    }


}
