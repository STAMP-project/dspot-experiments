package com.baeldung.servletinitializer;


import com.baeldung.servletinitializer.WarInitializerApplication.WarInitializerController;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = WarInitializerController.class)
public class WarInitializerApplicationIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenContextRootUrlIsAccessed_thenStatusIsOk() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().is(200));
    }

    @Test
    public void whenContextRootUrlIsAccesed_thenCorrectStringIsReturned() throws Exception {
        mockMvc.perform(get("/")).andExpect(content().string(CoreMatchers.containsString("WarInitializerApplication is up and running!")));
    }
}

