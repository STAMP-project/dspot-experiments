package org.baeldung.web;


import org.baeldung.spring.ClientWebConfig;
import org.baeldung.spring.SecurityJavaConfig;
import org.baeldung.spring.WebConfig;
import org.baeldung.web.controller.AsyncController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { ClientWebConfig.class, SecurityJavaConfig.class, WebConfig.class })
public class AsyncControllerIntegrationTest {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    MockHttpSession session;

    @Mock
    AsyncController controller;

    private MockMvc mockMvc;

    @Test
    public void testAsync() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/async")).andExpect(status().is5xxServerError());
    }
}

