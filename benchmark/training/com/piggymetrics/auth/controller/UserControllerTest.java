package com.piggymetrics.auth.controller;


import MediaType.APPLICATION_JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piggymetrics.auth.domain.User;
import com.piggymetrics.auth.service.UserService;
import com.sun.security.auth.UserPrincipal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    private UserController accountController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @Test
    public void shouldCreateNewUser() throws Exception {
        final User user = new User();
        user.setUsername("test");
        user.setPassword("password");
        String json = UserControllerTest.mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(APPLICATION_JSON).content(json)).andExpect(status().isOk());
    }

    @Test
    public void shouldFailWhenUserIsNotValid() throws Exception {
        final User user = new User();
        user.setUsername("t");
        user.setPassword("p");
        mockMvc.perform(post("/users")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnCurrentUser() throws Exception {
        mockMvc.perform(get("/users/current").principal(new UserPrincipal("test"))).andExpect(jsonPath("$.name").value("test")).andExpect(status().isOk());
    }
}

