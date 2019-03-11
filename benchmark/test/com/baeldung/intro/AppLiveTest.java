package com.baeldung.intro;


import MediaType.APPLICATION_JSON;
import SpringBootTest.WebEnvironment;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class AppLiveTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void getIndex() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string(Matchers.equalTo("Index Page")));
    }

    @Test
    public void getLocal() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/local").accept(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string(Matchers.equalTo("/local")));
    }
}

