package com.baeldung.thymeleaf.controller;


import com.baeldung.thymeleaf.config.InitSecurity;
import com.baeldung.thymeleaf.config.WebApp;
import com.baeldung.thymeleaf.config.WebMVCConfig;
import com.baeldung.thymeleaf.config.WebMVCSecurity;
import javax.servlet.Filter;
import org.hamcrest.Matchers;
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
public class FragmentsIntegrationTest {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    MockHttpSession session;

    private MockMvc mockMvc;

    @Autowired
    private Filter springSecurityFilterChain;

    @Test
    public void whenAccessingFragmentsRoute_thenViewHasExpectedContent() throws Exception {
        this.mockMvc.perform(get("/fragments").with(testUser())).andDo(print()).andExpect(status().isOk()).andExpect(content().string(Matchers.containsString("<title>Thymeleaf Fragments: home</title>")));
    }

    @Test
    public void whenAccessingParamsRoute_thenViewHasExpectedContent() throws Exception {
        this.mockMvc.perform(get("/params").with(testUser())).andDo(print()).andExpect(status().isOk()).andExpect(content().string(Matchers.containsString("<span>Name</span>")));
    }

    @Test
    public void whenAccessingMarkupRoute_thenViewHasExpectedContent() throws Exception {
        this.mockMvc.perform(get("/markup").with(testUser())).andDo(print()).andExpect(status().isOk()).andExpect(content().string(Matchers.containsString("<div class=\"another\">This is another sidebar</div>")));
    }

    @Test
    public void whenAccessingOtherRoute_thenViewHasExpectedContent() throws Exception {
        this.mockMvc.perform(get("/other").with(testUser())).andDo(print()).andExpect(status().isOk()).andExpect(content().string(Matchers.containsString("<td>John Smith</td>")));
    }
}

