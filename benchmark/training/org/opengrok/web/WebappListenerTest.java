package org.opengrok.web;


import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.Mockito;


public class WebappListenerTest {
    /**
     * simple smoke test for WebappListener request handling
     */
    @Test
    public void testRequest() {
        WebappListener wl = new WebappListener();
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        Mockito.when(req.getServletContext()).thenReturn(servletContext);
        ServletRequestEvent event = new ServletRequestEvent(servletContext, req);
        wl.requestInitialized(event);
        wl.requestDestroyed(event);
    }
}

