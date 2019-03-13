package com.baeldung.web.controller;


import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;


public class SimpleBookRestControllerIntegrationTest {
    private MockMvc mockMvc;

    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    @Test
    public void givenBookId_whenMockMVC_thenVerifyResponse() throws Exception {
        this.mockMvc.perform(get("/books/42")).andExpect(status().isOk()).andExpect(content().contentType(SimpleBookRestControllerIntegrationTest.CONTENT_TYPE)).andExpect(jsonPath("$.id").value(42));
    }
}

