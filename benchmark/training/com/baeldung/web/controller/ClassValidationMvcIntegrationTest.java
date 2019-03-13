package com.baeldung.web.controller;


import MediaType.TEXT_HTML;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


public class ClassValidationMvcIntegrationTest {
    private MockMvc mockMvc;

    @Test
    public void givenMatchingEmailPassword_whenPostNewUserForm_thenOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user").accept(TEXT_HTML).param("email", "john@yahoo.com").param("verifyEmail", "john@yahoo.com").param("password", "pass").param("verifyPassword", "pass")).andExpect(model().attribute("message", "Valid form")).andExpect(view().name("userHome")).andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void givenNotMatchingEmailPassword_whenPostNewUserForm_thenOk() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user").accept(TEXT_HTML).param("email", "john@yahoo.com").param("verifyEmail", "john@yahoo.commmm").param("password", "pass").param("verifyPassword", "passsss")).andExpect(model().errorCount(2)).andExpect(view().name("userHome")).andExpect(status().isOk()).andDo(print());
    }
}

