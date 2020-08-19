package com.skillbox.socialnetwork.main;

import com.skillbox.socialnetwork.main.model.Person;
import com.skillbox.socialnetwork.main.model.Post;
import com.skillbox.socialnetwork.main.repository.LikeRepository;
import com.skillbox.socialnetwork.main.repository.PersonRepository;
import com.skillbox.socialnetwork.main.repository.PostRepository;
import com.skillbox.socialnetwork.main.service.LikeService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
public class LikeTest extends AbstractMvcTest {

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    LikeService likeService;

    @Value("${test.user.email}")
    private String firstUserEmail;

    @Value("${test.user.password}")
    private String password;

    @Value("${test.user.friendEmail}")
    private String secondUserEmail;

    Person firstUser;
    Person secondUser;
    Post post;

    @Override
    protected void doInit() throws Exception {
        if (personRepository.findByEmail(firstUserEmail) == null)
            registerUser(firstUserEmail, password, "First");
        if (personRepository.findByEmail(secondUserEmail) == null)
            registerUser(secondUserEmail, password, "Second");
        activateAccounts(personRepository, firstUserEmail, secondUserEmail);
        firstUser = personRepository.findByEmail(firstUserEmail);
        secondUser = personRepository.findByEmail(secondUserEmail);
        createPost(secondUser);
        post = postRepository.findAll().get(0);
    }

    @Test
    public void putLike() throws Exception {
        final String firstUserToken = extractToken(login(firstUserEmail, password).andReturn());
        final String secondUserToken = extractToken(login(secondUserEmail, password).andReturn());


        String putLikeRequest = "{\n" +
                "  \"item_id\": " + post.getId() + ",\n " +
                "  \"type\": \"Post\"\n" +
                "}";
        mockMvc.perform(put("/api/v1/likes")
                .header("Authorization", firstUserToken)
                .content(putLikeRequest)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    private void createPost(Person author) {
        Post post = new Post();
        post.setAuthor(author);
        post.setIsBlocked(false);
        post.setPostText("Text for this perfect post for testing");
        post.setTime(new Date());
        post.setTitle("Test post title");
        postRepository.save(post);
    }
}
