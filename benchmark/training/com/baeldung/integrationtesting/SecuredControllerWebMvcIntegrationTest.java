package com.baeldung.integrationtesting;


import MediaType.APPLICATION_JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@WebMvcTest(SecuredController.class)
public class SecuredControllerWebMvcIntegrationTest {
    @Autowired
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

