package io.hawt.web.auth;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;


public class RedirectorTest {
    private Redirector redirector;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private ServletContext servletContext;

    @Test
    public void shouldRedirectToRelativeUrlByDefault() throws Exception {
        // given
        Mockito.when(servletContext.getInitParameter("scheme")).thenReturn(null);
        // when
        redirector.doRedirect(request, response, "/path");
        // then
        Mockito.verify(response).sendRedirect("/context-path/application-context-path/path");
    }

    @Test
    public void shouldRedirectToAbsoluteUrlWhenSchemeIsConfigured() throws Exception {
        // given
        Mockito.when(servletContext.getInitParameter("scheme")).thenReturn("https");
        // when
        redirector.doRedirect(request, response, "/path");
        // then
        Mockito.verify(response).sendRedirect("https://server01:9000/context-path/application-context-path/path");
    }
}

