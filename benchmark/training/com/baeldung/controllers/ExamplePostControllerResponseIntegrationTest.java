package com.baeldung.controllers;


import com.baeldung.sampleapp.config.MainApplication;
import com.baeldung.services.ExampleService;
import com.baeldung.transfer.LoginForm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class ExamplePostControllerResponseIntegrationTest {
    MockMvc mockMvc;

    @Mock
    private ExampleService exampleService;

    @InjectMocks
    private ExamplePostController exampleController;

    private final String jsonBody = "{\"username\": \"username\", \"password\": \"password\"}";

    private LoginForm lf = new LoginForm();

    @Test
    public void requestBodyTest() {
        try {
            Mockito.when(exampleService.fakeAuthenticate(lf)).thenReturn(true);
            mockMvc.perform(post("/post/response").content(jsonBody).contentType("application/json")).andDo(print()).andExpect(status().isOk()).andExpect(content().json("{\"text\":\"Thanks For Posting!!!\"}"));
        } catch (Exception e) {
            System.out.println(("Exception: " + e));
        }
    }
}

