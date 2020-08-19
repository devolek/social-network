package com.skillbox.socialnetwork.main;

import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.repository.PersonRepository;
import com.skillbox.socialnetwork.main.security.jwt.JwtTokenProvider;
import com.skillbox.socialnetwork.main.service.PersonService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
public class AuthenticationTest extends AbstractMvcTest {
    @Autowired
    PersonService personService;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    PersonRepository personRepository;

    @Value("${test.user.email}")
    private String email;

    @Value("${test.user.password}")
    private String password;

    private String url;

    @Value("${port}")
    private String port;

    @Override
    protected void doInit() throws Exception {
        if (personRepository.findByEmail(email) != null) {
            deleteTestUser();
        }
        registerUser(email, password, "Валдис").getRequest();
        activateAccounts(personRepository, email);
    }


    @Test
    public void getUserProfileWithoutTokenIsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void loginTest() throws Exception {
        login(email, password)
                .andExpect(status().isOk());
    }

    @Test
    public void logoutTest() throws Exception {
        final String token = extractToken(login(email, password).andReturn());

        String emptyToken = mockMvc.perform(
                post("/api/v1/auth/logout")
                        .header("Authorization", token))
                .andReturn()
                .getResponse().getHeader("Authorization");

        assertNull(emptyToken);
    }

    @Test
    public void passwordRecoveryTest() throws Exception {
        MvcResult result = mockMvc.perform(
                put("/api/v1/account/password/recovery")
                        .content("{\"email\": \"" + email + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    public void passwordSetTest() throws Exception {

        MvcResult recovery = mockMvc.perform(
                put("/api/v1/account/password/recovery")
                        .content("{\"email\": \"" + email + "\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        url = recovery.getRequest().getScheme() + "://" + recovery.getRequest().getServerName() + ":" + port;

        Person user = personService.findByEmail(email);
        String token = tokenProvider.createToken(user.getEmail() + ":" + user.getConfirmationCode());
        password += "9";

        MvcResult result = mockMvc.perform(
                put("/api/v1/account/password/set")
                        .content("{\n" +
                                "\"token\": \"" + token + "\",\n" +
                                "\"password\": \"" + password + "\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("referer", url + "/change-password?token=" + token))
                .andReturn();

        System.out.println(url);

        assertEquals(200, result.getResponse().getStatus());

        login(email, password)
                .andExpect(status().isOk());
    }

    @Test
    public void loginWithWrongPassword() throws Exception {
        login(email, password + "wrong")
                .andExpect(status().is4xxClientError());
    }


    private void deleteTestUser() {
        Person user = personService.findByEmail(email);
        personRepository.delete(user);
    }
}
