package com.baeldung.mvc.velocity.test;


import com.baeldung.mvc.velocity.spring.config.WebConfig;
import com.baeldung.mvc.velocity.test.config.TestConfig;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


// @ContextConfiguration(locations = {"classpath:mvc-servlet.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class, WebConfig.class })
@WebAppConfiguration
public class DataContentControllerIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void whenCallingList_ThenModelAndContentOK() throws Exception {
        mockMvc.perform(get("/list")).andExpect(status().isOk()).andExpect(view().name("list")).andExpect(model().attribute("tutorials", Matchers.hasSize(2))).andExpect(model().attribute("tutorials", Matchers.hasItem(Matchers.allOf(Matchers.hasProperty("tutId", Matchers.is(1)), Matchers.hasProperty("author", Matchers.is("GuavaAuthor")), Matchers.hasProperty("title", Matchers.is("Guava")))))).andExpect(model().attribute("tutorials", Matchers.hasItem(Matchers.allOf(Matchers.hasProperty("tutId", Matchers.is(2)), Matchers.hasProperty("author", Matchers.is("AndroidAuthor")), Matchers.hasProperty("title", Matchers.is("Android"))))));
        mockMvc.perform(get("/list")).andExpect(xpath("//table").exists());
        mockMvc.perform(get("/list")).andExpect(xpath("//td[@id='tutId_1']").exists());
    }

    @Test
    public void whenCallingIndex_thenViewOK() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index")).andExpect(model().size(0));
    }
}

