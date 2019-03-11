package io.requery.example.springboot;


import MediaType.APPLICATION_JSON_UTF8;
import SpringBootTest.WebEnvironment;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.requery.example.springboot.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ExampleSpringbootApplicationTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    private User testUser;

    private ObjectMapper objectMapper;

    @Test
    public void createUser() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUser);
        postUserAndExpectSame(userJson);
    }

    @Test
    public void findUserById() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUser);
        mockMvc.perform(post("/user").contentType(APPLICATION_JSON_UTF8).content(userJson));
        mockMvc.perform(get(("/user" + 1)).contentType(APPLICATION_JSON_UTF8).content(userJson));
    }

    @Test
    public void updateUser() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUser);
        postUserAndExpectSame(userJson);
        testUser.setFirstName("Henry");
        userJson = objectMapper.writeValueAsString(testUser);
        postUserAndExpectSame(userJson);
    }
}

