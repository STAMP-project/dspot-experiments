package com.baeldung.controller.push;


import com.baeldung.spring.configuration.PushConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@SpringJUnitWebConfig(PushConfiguration.class)
public class PushControllerIntegrationTest {
    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @Test
    public void whenDemoWithPushGETisPerformed_thenRetrievedStatusOk() throws Exception {
        mockMvc.perform(get("/demoWithPush")).andExpect(status().isOk());
    }

    @Test
    public void whenDemoWithoutPushGETisPerformed_thenRetrievedStatusOk() throws Exception {
        mockMvc.perform(get("/demoWithoutPush")).andExpect(status().isOk());
    }
}

