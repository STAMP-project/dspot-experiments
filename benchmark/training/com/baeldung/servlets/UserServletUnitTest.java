package com.baeldung.servlets;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;


public class UserServletUnitTest {
    private static HttpServletRequest request;

    private static HttpServletResponse response;

    @Test
    public void givenHttpServletRequestMockInstance_whenCalledgetParameter_thenCalledAtLeastOnce() {
        UserServletUnitTest.request.getParameter("name");
        Mockito.verify(UserServletUnitTest.request, Mockito.atLeast(1)).getParameter("name");
    }

    @Test
    public void givenHttpServletRequestMockInstance_whenCalledgetParameter_thenOneAssertion() {
        Mockito.when(UserServletUnitTest.request.getParameter("name")).thenReturn("username");
        assertThat(UserServletUnitTest.request.getParameter("name")).isEqualTo("username");
    }

    @Test
    public void givenHttpServletResponseMockInstance_whenCalledgetContentType_thenCalledAtLeastOnce() {
        UserServletUnitTest.response.getContentType();
        Mockito.verify(UserServletUnitTest.response, Mockito.atLeast(1)).getContentType();
    }

    @Test
    public void givenHttpServletResponseMockInstance_whenCalledgetContentType_thenOneAssertion() {
        Mockito.when(UserServletUnitTest.response.getContentType()).thenReturn("text/html");
        assertThat(UserServletUnitTest.response.getContentType()).isEqualTo("text/html");
    }
}

