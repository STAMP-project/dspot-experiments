package org.springframework.samples.mvc.async;


import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.samples.mvc.AbstractContextControllerTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;


@RunWith(SpringJUnit4ClassRunner.class)
public class DeferredResultControllerTests extends AbstractContextControllerTests {
    private MockMvc mockMvc;

    @Test
    public void responseBody() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/async/deferred-result/response-body")).andExpect(status().isOk()).andExpect(request().asyncStarted()).andExpect(request().asyncResult("Deferred result")).andReturn();
        this.mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()).andExpect(content().contentType("text/plain;charset=ISO-8859-1")).andExpect(content().string("Deferred result"));
    }

    @Test
    public void view() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/async/deferred-result/model-and-view")).andExpect(status().isOk()).andExpect(request().asyncStarted()).andExpect(request().asyncResult(Matchers.instanceOf(ModelAndView.class))).andReturn();
        this.mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()).andExpect(forwardedUrl("/WEB-INF/views/views/html.jsp")).andExpect(model().attributeExists("javaBean"));
    }

    @Test
    public void exception() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get("/async/deferred-result/exception")).andExpect(status().isOk()).andExpect(request().asyncStarted()).andExpect(request().asyncResult(Matchers.instanceOf(IllegalStateException.class))).andReturn();
        this.mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isOk()).andExpect(content().contentType("text/plain;charset=ISO-8859-1")).andExpect(content().string("Handled exception: DeferredResult error"));
    }
}

