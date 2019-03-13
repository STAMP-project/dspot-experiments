package com.baeldung.validator;


import DirtiesContext.MethodMode;
import MediaType.APPLICATION_JSON;
import SpringBootTest.WebEnvironment;
import com.baeldung.SpringDataRestApplication;
import com.baeldung.models.WebsiteUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringDataRestApplication.class, webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class SpringDataRestValidatorIntegrationTest {
    public static final String URL = "http://localhost";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Test
    public void whenStartingApplication_thenCorrectStatusCode() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().is2xxSuccessful());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    public void whenAddingNewCorrectUser_thenCorrectStatusCodeAndResponse() throws Exception {
        WebsiteUser user = new WebsiteUser();
        user.setEmail("john.doe@john.com");
        user.setName("John Doe");
        mockMvc.perform(post("/users", user).contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(user))).andExpect(status().is2xxSuccessful()).andExpect(redirectedUrl("http://localhost/users/1"));
    }

    @Test
    public void whenAddingNewUserWithoutName_thenErrorStatusCodeAndResponse() throws Exception {
        WebsiteUser user = new WebsiteUser();
        user.setEmail("john.doe@john.com");
        mockMvc.perform(post("/users", user).contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(user))).andExpect(status().isNotAcceptable()).andExpect(redirectedUrl(null));
    }

    @Test
    public void whenAddingNewUserWithEmptyName_thenErrorStatusCodeAndResponse() throws Exception {
        WebsiteUser user = new WebsiteUser();
        user.setEmail("john.doe@john.com");
        user.setName("");
        mockMvc.perform(post("/users", user).contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(user))).andExpect(status().isNotAcceptable()).andExpect(redirectedUrl(null));
    }

    @Test
    public void whenAddingNewUserWithoutEmail_thenErrorStatusCodeAndResponse() throws Exception {
        WebsiteUser user = new WebsiteUser();
        user.setName("John Doe");
        mockMvc.perform(post("/users", user).contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(user))).andExpect(status().isNotAcceptable()).andExpect(redirectedUrl(null));
    }

    @Test
    public void whenAddingNewUserWithEmptyEmail_thenErrorStatusCodeAndResponse() throws Exception {
        WebsiteUser user = new WebsiteUser();
        user.setName("John Doe");
        user.setEmail("");
        mockMvc.perform(post("/users", user).contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(user))).andExpect(status().isNotAcceptable()).andExpect(redirectedUrl(null));
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    public void whenDeletingCorrectUser_thenCorrectStatusCodeAndResponse() throws Exception {
        WebsiteUser user = new WebsiteUser();
        user.setEmail("john.doe@john.com");
        user.setName("John Doe");
        mockMvc.perform(post("/users", user).contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(user))).andExpect(status().is2xxSuccessful()).andExpect(redirectedUrl("http://localhost/users/1"));
        mockMvc.perform(delete("/users/1").contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(user))).andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
    public void whenSearchingByEmail_thenCorrectStatusCodeAndResponse() throws Exception {
        WebsiteUser user = new WebsiteUser();
        user.setEmail("john.doe@john.com");
        user.setName("John Doe");
        mockMvc.perform(post("/users", user).contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(user))).andExpect(status().is2xxSuccessful()).andExpect(redirectedUrl("http://localhost/users/1"));
        mockMvc.perform(get("/users/search/byEmail").param("email", user.getEmail()).contentType(APPLICATION_JSON)).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void whenSearchingByEmailWithOriginalMethodName_thenErrorStatusCodeAndResponse() throws Exception {
        WebsiteUser user = new WebsiteUser();
        user.setEmail("john.doe@john.com");
        user.setName("John Doe");
        mockMvc.perform(post("/users", user).contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(user))).andExpect(status().is2xxSuccessful()).andExpect(redirectedUrl("http://localhost/users/1"));
        mockMvc.perform(get("/users/search/findByEmail").param("email", user.getEmail()).contentType(APPLICATION_JSON)).andExpect(status().isNotFound());
    }
}

