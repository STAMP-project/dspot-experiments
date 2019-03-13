package com.baeldung.sessionattrs;


import SpringBootTest.WebEnvironment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class TodoControllerWithScopedProxyIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void whenFirstRequest_thenContainsUnintializedTodo() throws Exception {
        MvcResult result = mockMvc.perform(get("/scopedproxy/form")).andExpect(status().isOk()).andExpect(model().attributeExists("todo")).andReturn();
        TodoItem item = ((TodoItem) (get("todo")));
        Assert.assertFalse(StringUtils.isEmpty(item.getDescription()));
    }

    @Test
    public void whenSubmit_thenSubsequentFormRequestContainsMostRecentTodo() throws Exception {
        mockMvc.perform(post("/scopedproxy/form").param("description", "newtodo")).andExpect(status().is3xxRedirection()).andReturn();
        MvcResult result = mockMvc.perform(get("/scopedproxy/form")).andExpect(status().isOk()).andExpect(model().attributeExists("todo")).andReturn();
        TodoItem item = ((TodoItem) (get("todo")));
        Assert.assertEquals("newtodo", item.getDescription());
    }
}

