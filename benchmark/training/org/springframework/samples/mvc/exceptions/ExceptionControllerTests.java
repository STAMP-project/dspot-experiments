package org.springframework.samples.mvc.exceptions;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.samples.mvc.AbstractContextControllerTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringJUnit4ClassRunner.class)
public class ExceptionControllerTests extends AbstractContextControllerTests {
    private MockMvc mockMvc;

    @Test
    public void controllerExceptionHandler() throws Exception {
        this.mockMvc.perform(get("/exception")).andExpect(status().isOk()).andExpect(content().string("IllegalStateException handled!"));
    }

    @Test
    public void globalExceptionHandler() throws Exception {
        this.mockMvc.perform(get("/global-exception")).andExpect(status().isOk()).andExpect(content().string("Handled BusinessException"));
    }
}

