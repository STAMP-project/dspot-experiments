package com.baeldung.web.controller;


import MediaType.TEXT_HTML;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


public class CustomMVCValidatorIntegrationTest {
    private MockMvc mockMvc;

    @Test
    public void givenPhonePageUri_whenMockMvc_thenReturnsPhonePage() throws Exception {
        this.mockMvc.perform(get("/validatePhone")).andExpect(view().name("phoneHome"));
    }

    @Test
    public void givenPhoneURIWithPostAndFormData_whenMockMVC_thenVerifyErrorResponse() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/addValidatePhone").accept(TEXT_HTML).param("phoneInput", "123")).andExpect(model().attributeHasFieldErrorCode("validatedPhone", "phone", "ContactNumberConstraint")).andExpect(view().name("phoneHome")).andExpect(status().isOk()).andDo(print());
    }
}

