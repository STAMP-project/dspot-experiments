package org.springframework.samples.mvc.data;


import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;


public class CustomArgumentControllerTests {
    private MockMvc mockMvc;

    @Test
    public void param() throws Exception {
        this.mockMvc.perform(get("/data/custom")).andExpect(content().string("Got 'foo' request attribute value 'bar'"));
    }
}

