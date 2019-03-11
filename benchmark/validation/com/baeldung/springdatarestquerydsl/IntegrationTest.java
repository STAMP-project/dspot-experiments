package com.baeldung.springdatarestquerydsl;


import MediaType.APPLICATION_JSON;
import com.baeldung.Application;
import java.nio.charset.Charset;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class IntegrationTest {
    final MediaType contentType = new MediaType(APPLICATION_JSON.getType(), APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Test
    public void givenRequestHasBeenMade_whenQueryOverNameAttribute_thenGetJohn() throws Exception {
        // Get John
        mockMvc.perform(get("/personQuery?name=John")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$", Matchers.hasSize(1))).andExpect(jsonPath("$[0].name", Matchers.is("John"))).andExpect(jsonPath("$[0].address.address", Matchers.is("Fake Street 1"))).andExpect(jsonPath("$[0].address.country", Matchers.is("Fake Country")));
    }

    @Test
    public void givenRequestHasBeenMade_whenQueryOverNameAttribute_thenGetLisa() throws Exception {
        // Get Lisa
        mockMvc.perform(get("/personQuery?name=Lisa")).andExpect(status().isOk()).andExpect(content().contentType(contentType)).andExpect(jsonPath("$", Matchers.hasSize(1))).andExpect(jsonPath("$[0].name", Matchers.is("Lisa"))).andExpect(jsonPath("$[0].address.address", Matchers.is("Real Street 1"))).andExpect(jsonPath("$[0].address.country", Matchers.is("Real Country")));
    }
}

