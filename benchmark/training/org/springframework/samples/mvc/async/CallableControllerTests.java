package org.springframework.samples.mvc.async;


import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.samples.mvc.AbstractContextControllerTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@RunWith(SpringJUnit4ClassRunner.class)
public class CallableControllerTests extends AbstractContextControllerTests {
    private MockMvc mockMvc;

    @Test
    public void responseBody() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/async/callable/response-body")).andExpect(request().asyncStarted()).andExpect(request().asyncResult("Callable result")).andReturn();
        this.mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()).andExpect(content().contentType("text/plain;charset=ISO-8859-1")).andExpect(content().string("Callable result"));
    }

    @Test
    public void view() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/async/callable/view")).andExpect(request().asyncStarted()).andExpect(request().asyncResult("views/html")).andReturn();
        this.mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()).andExpect(forwardedUrl("/WEB-INF/views/views/html.jsp")).andExpect(model().attribute("foo", "bar")).andExpect(model().attribute("fruit", "apple"));
    }

    @Test
    public void exception() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/async/callable/exception")).andExpect(request().asyncStarted()).andExpect(request().asyncResult(Matchers.instanceOf(IllegalStateException.class))).andReturn();
        this.mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()).andExpect(content().contentType("text/plain;charset=ISO-8859-1")).andExpect(content().string("Handled exception: Callable error"));
    }
}

