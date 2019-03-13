package org.baeldung.web.controller.status;


import org.baeldung.config.WebConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WebConfig.class)
@WebAppConfiguration
@AutoConfigureWebClient
public class ExampleControllerIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void whenGetRequestSentToController_thenReturnsStatusNotAcceptable() throws Exception {
        mockMvc.perform(get("/controller")).andExpect(status().isNotAcceptable());
    }

    @Test
    public void whenGetRequestSentToException_thenReturnsStatusForbidden() throws Exception {
        mockMvc.perform(get("/exception")).andExpect(status().isForbidden());
    }
}

