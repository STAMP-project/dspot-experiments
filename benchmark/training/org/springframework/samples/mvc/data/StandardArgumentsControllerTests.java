package org.springframework.samples.mvc.data;


import MediaType.TEXT_PLAIN;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;


public class StandardArgumentsControllerTests {
    private MockMvc mockMvc;

    @Test
    public void request() throws Exception {
        this.mockMvc.perform(get("/data/standard/request")).andExpect(content().string(Matchers.startsWith("request = org.springframework.mock.web.MockHttpServletRequest@")));
    }

    @Test
    public void requestReader() throws Exception {
        this.mockMvc.perform(post("/data/standard/request/reader").contentType(TEXT_PLAIN).content("foo".getBytes())).andExpect(content().string("Read char request body = foo"));
    }

    @Test
    public void requestIs() throws Exception {
        this.mockMvc.perform(post("/data/standard/request/is").contentType(TEXT_PLAIN).content("foo".getBytes())).andExpect(content().string("Read binary request body = foo"));
    }

    @Test
    public void response() throws Exception {
        this.mockMvc.perform(get("/data/standard/response")).andExpect(content().string(Matchers.startsWith("response = org.springframework.mock.web.MockHttpServletResponse@")));
    }

    @Test
    public void writer() throws Exception {
        this.mockMvc.perform(get("/data/standard/response/writer")).andExpect(content().string("Wrote char response using Writer"));
    }

    @Test
    public void os() throws Exception {
        this.mockMvc.perform(get("/data/standard/response/os")).andExpect(content().string("Wrote binary response using OutputStream"));
    }

    @Test
    public void session() throws Exception {
        this.mockMvc.perform(get("/data/standard/session")).andExpect(content().string(Matchers.startsWith("session=org.springframework.mock.web.MockHttpSession@")));
    }
}

