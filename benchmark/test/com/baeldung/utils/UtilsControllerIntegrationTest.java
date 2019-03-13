package com.baeldung.utils;


import com.baeldung.utils.controller.UtilsController;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.web.servlet.MockMvc;


public class UtilsControllerIntegrationTest {
    @InjectMocks
    private UtilsController utilsController;

    private MockMvc mockMvc;

    @Test
    public void givenParameter_setRequestParam_andSetSessionAttribute() throws Exception {
        String param = "testparam";
        this.mockMvc.perform(post("/setParam").param("param", param).sessionAttr("parameter", param)).andExpect(status().isOk());
    }
}

