package com.skillbox.socialnetwork.main;

import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.repository.PersonRepository;
import com.skillbox.socialnetwork.main.service.FriendsService;
import com.skillbox.socialnetwork.main.service.PersonService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
public class FriendsControllerTest extends AbstractMvcTest {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    FriendsService friendsService;

    @Autowired
    PersonService personService;

    @Value("${test.user.email}")
    private String email;

    @Value("${test.user.password}")
    private String password;

    @Value("${test.user.friendEmail}")
    private String friendEmail;

    @Value("${test.user.myFriendsFriendEmail}")
    private String myFriendsFriendEmail;

    @Value("${test.user.userFromMyCityEmail}")
    private String userFromMyCityEmail;

    @Override
    protected void doInit() throws Exception {
        personRepository.deleteAll();

        registerUser(email, password, "Ivan");
        registerUser(friendEmail, password, "Roman");
        registerUser(myFriendsFriendEmail, password, "Semen");
        registerUser(userFromMyCityEmail, password, "Karen");
        activateAccounts(personRepository, email, friendEmail, myFriendsFriendEmail, userFromMyCityEmail);
        Person user = personRepository.findByEmail(email);
        Person userFromMyCity = personRepository.findByEmail(userFromMyCityEmail);
        user.setCity("Москва");
        userFromMyCity.setCity("Москва");
        friendsService.addFriend(personRepository.findByEmail(friendEmail),
                personRepository.findByEmail(myFriendsFriendEmail));
        friendsService.addFriend(personRepository.findByEmail(myFriendsFriendEmail),
                personRepository.findByEmail(friendEmail));
        personService.save(user);
        personService.save(userFromMyCity);
    }

    @Test
    public void addFriendTest() throws Exception {
        Integer friendId = personRepository.findByEmail(friendEmail).getId();
        Integer userId = personRepository.findByEmail(email).getId();
        Integer myFfId = personRepository.findByEmail(myFriendsFriendEmail).getId();
        Integer userFromMyCity = personRepository.findByEmail(userFromMyCityEmail).getId();
        final String userToken = extractToken(login(email, password).andReturn());
        final String friendToken = extractToken(login(friendEmail, password).andReturn());

        mockMvc.perform(post("/api/v1/friends/" + friendId)
                .header("Authorization", userToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/friends/request")
                .header("Authorization", friendToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data..id", Matchers.contains(userId)));

        mockMvc.perform(post("/api/v1/friends/" + userId)
                .header("Authorization", friendToken))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/friends/")
                .header("Authorization", friendToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(myFfId))
                .andExpect(jsonPath("$.data[1].id").value(userId));

        mockMvc.perform(post("/api/v1/is/friends/")
                .header("Authorization", friendToken)
                .param("idList",
                        String.valueOf(myFfId),
                        String.valueOf(userId)
                ))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..user_id", Matchers.containsInAnyOrder(userId, myFfId)));

        mockMvc.perform(get("/api/v1/friends/recommendations/")
                .header("Authorization", userToken)
                .param("offset", "0")
                .param("itemPerPage", "20")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data..id", Matchers.not(Matchers.contains(friendId))))
                .andExpect(jsonPath("$.data..id", Matchers.containsInAnyOrder(myFfId, userFromMyCity)));

        mockMvc.perform(delete("/api/v1/friends/" + userId)
                .header("Authorization", friendToken))
                .andDo(print())
                .andExpect(status().isOk());

    }
}
