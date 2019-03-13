package com.baeldung.spring.servlets;


import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class WelcomeServletIntegrationTest {
    @Test
    public void whenRequested_thenRedirectedToCorrectUrl() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/welcome");
        request.addParameter("name", "Dennis");
        WelcomeServlet servlet = new WelcomeServlet();
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.doGet(request, response);
        Assertions.assertEquals("/redirected", response.getRedirectedUrl());
        Assertions.assertEquals(302, response.getStatus());
    }
}

