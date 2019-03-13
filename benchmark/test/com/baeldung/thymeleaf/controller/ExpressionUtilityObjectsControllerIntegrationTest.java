package com.baeldung.thymeleaf.controller;


import com.baeldung.thymeleaf.config.InitSecurity;
import com.baeldung.thymeleaf.config.WebApp;
import com.baeldung.thymeleaf.config.WebMVCConfig;
import com.baeldung.thymeleaf.config.WebMVCSecurity;
import javax.servlet.Filter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebApp.class, WebMVCConfig.class, WebMVCSecurity.class, InitSecurity.class })
public class ExpressionUtilityObjectsControllerIntegrationTest {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    MockHttpSession session;

    private MockMvc mockMvc;

    @Autowired
    private Filter springSecurityFilterChain;

    @Test
    public void testGetObjects() throws Exception {
        mockMvc.perform(get("/objects").with(testUser()).with(csrf())).andExpect(status().isOk()).andExpect(view().name("objects.html"));
    }

    @Test
    public void testDates() throws Exception {
        mockMvc.perform(get("/dates").with(testUser()).with(csrf())).andExpect(status().isOk()).andExpect(view().name("dates.html"));
    }

    @Test
    public void testTeachers() throws Exception {
        mockMvc.perform(get("/listTeachers").with(testUser()).with(csrf())).andExpect(status().isOk()).andExpect(view().name("listTeachers.html"));
    }
}

