package org.baeldung.converter.controller;


import SpringBootTest.WebEnvironment;
import org.baeldung.boot.Application;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
public class StringToEmployeeConverterControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getStringToEmployeeTest() throws Exception {
        mockMvc.perform(get("/string-to-employee?employee=1,2000")).andDo(print()).andExpect(jsonPath("$.id", CoreMatchers.is(1))).andExpect(jsonPath("$.salary", CoreMatchers.is(2000.0))).andExpect(status().isOk());
    }
}

