package com.baeldung.web.controller;


import com.baeldung.spring.web.config.WebConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebConfig.class)
public class EmployeeMvcIntegrationTest {
    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @Test
    public void whenEmployeeGETisPerformed_thenRetrievedStatusAndViewNameAndAttributeAreCorrect() throws Exception {
        mockMvc.perform(get("/employee")).andExpect(status().isOk()).andExpect(view().name("employeeHome")).andExpect(model().attributeExists("employee")).andDo(print());
    }
}

