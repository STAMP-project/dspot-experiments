package com.baeldung.integrationtesting;


import MediaType.APPLICATION_JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SecuredControllerSpringBootIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Test
    public void givenRequestOnPrivateService_shouldFailWith401() throws Exception {
        mvc.perform(get("/private/hello").contentType(APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @WithMockUser("spring")
    @Test
    public void givenAuthRequestOnPrivateService_shouldSucceedWith200() throws Exception {
        mvc.perform(get("/private/hello").contentType(APPLICATION_JSON)).andExpect(status().isOk());
    }
}

