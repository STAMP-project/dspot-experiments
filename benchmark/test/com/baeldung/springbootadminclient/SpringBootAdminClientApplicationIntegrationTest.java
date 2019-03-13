package com.baeldung.springbootadminclient;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class SpringBootAdminClientApplicationIntegrationTest {
    @Autowired
    Environment environment;

    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    @Test
    public void whenEnvironmentAvailable_ThenAdminServerPropertiesExist() {
        Assert.assertEquals(environment.getProperty("spring.boot.admin.url"), "http://localhost:8080");
        Assert.assertEquals(environment.getProperty("spring.boot.admin.username"), "admin");
        Assert.assertEquals(environment.getProperty("spring.boot.admin.password"), "admin");
    }

    @Test
    public void whenHttpBasicAttempted_ThenSuccess() throws Exception {
        mockMvc.perform(get("/env").with(httpBasic("client", "client")));
    }

    @Test
    public void whenInvalidHttpBasicAttempted_ThenUnauthorized() throws Exception {
        mockMvc.perform(get("/env").with(httpBasic("client", "invalid"))).andExpect(status().isUnauthorized());
    }
}

