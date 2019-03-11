package com.baeldung.springbootsecuritycors;


import com.baeldung.springbootsecuritycors.basicauth.SpringBootSecurityApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootSecurityApplication.class })
public class ResourceControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void givenPreFlightRequest_whenPerfomed_shouldReturnOK() throws Exception {
        mockMvc.perform(options("/user").header("Access-Control-Request-Method", "GET").header("Origin", "http://localhost:4200")).andExpect(status().isOk());
    }
}

