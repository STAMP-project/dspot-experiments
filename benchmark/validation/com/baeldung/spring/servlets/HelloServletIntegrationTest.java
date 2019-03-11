package com.baeldung.spring.servlets;


import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class HelloServletIntegrationTest {
    @Test
    public void whenRequested_thenForwardToCorrectUrl() throws IOException, ServletException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/hello");
        request.addParameter("name", "Dennis");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HelloServlet servlet = new HelloServlet();
        servlet.doGet(request, response);
        Assertions.assertEquals("/forwarded", response.getForwardedUrl());
        Assertions.assertEquals(200, response.getStatus());
    }
}

