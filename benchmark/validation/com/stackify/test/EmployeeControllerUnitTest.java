package com.stackify.test;


import com.stackify.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class EmployeeControllerUnitTest {
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void whenCreateGetEmployee_thenOk() throws Exception {
        String employeeJson = "{\"name\":\"john\"}";
        this.mockMvc.perform(post("/employees").contentType(EmployeeControllerUnitTest.CONTENT_TYPE).content(employeeJson)).andExpect(status().isCreated());
        this.mockMvc.perform(get("/employees")).andExpect(status().isOk()).andExpect(content().contentType(EmployeeControllerUnitTest.CONTENT_TYPE)).andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].name", is("ana"))).andExpect(jsonPath("$[1].name", is("john")));
    }
}

