package com.baeldung.springbootadminserver;


import SpringBootTest.WebEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebSecurityConfigIntegrationTest {
    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @Test
    public void whenApplicationStarts_ThenGetLoginPageWithSuccess() throws Exception {
        mockMvc.perform(get("/login.html")).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void whenFormLoginAttempted_ThenSuccess() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("admin"));
    }

    @Test
    public void whenFormLoginWithSuccess_ThenApiEndpointsAreAccessible() throws Exception {
        mockMvc.perform(formLogin("/login").user("admin").password("admin"));
        mockMvc.perform(get("/api/applications/")).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void whenHttpBasicAttempted_ThenSuccess() throws Exception {
        mockMvc.perform(get("/env").with(httpBasic("admin", "admin")));
    }

    @Test
    public void whenInvalidHttpBasicAttempted_ThenUnauthorized() throws Exception {
        mockMvc.perform(get("/env").with(httpBasic("admin", "invalid"))).andExpect(status().isUnauthorized());
    }
}

