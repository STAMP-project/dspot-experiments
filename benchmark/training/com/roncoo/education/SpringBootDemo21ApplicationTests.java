package com.roncoo.education;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootDemo21ApplicationTests {
    private MockMvc mvc;

    @Test
    public void contextLoads() throws Exception {
        RequestBuilder request = get("/index");
        mvc.perform(request).andExpect(status().isOk()).andExpect(content().string("hello world"));
        request = get("/index/get").param("name", "??");
        mvc.perform(request).andExpect(status().isOk()).andExpect(content().string("{\"name\":\"\u65e0\u5883\",\"title\":\"hello world\"}"));
    }
}

