package com.baeldung.accessparamsjs;


import MediaType.APPLICATION_JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ControllerUnitTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void whenRequestThymeleaf_thenStatusOk() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/index").accept(APPLICATION_JSON)).andExpect(status().isOk());
    }
}

