package org.stagemonitor.web.servlet.widget;


import io.opentracing.Span;
import java.util.Arrays;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.web.servlet.ServletPlugin;


public class SpanServletTest {
    private SpanServlet spanServlet;

    private String connectionId;

    private ServletPlugin servletPlugin;

    private ConfigurationRegistry configuration;

    private Span span;

    @Test
    public void testSpanBeforeRequest() throws Exception {
        reportSpan();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stagemonitor/spans");
        request.addParameter("connectionId", connectionId);
        MockHttpServletResponse response = new MockHttpServletResponse();
        spanServlet.service(request, response);
        Assert.assertEquals(spanAsJsonArray(), response.getContentAsString());
        Assert.assertEquals("application/json;charset=UTF-8", response.getHeader("content-type"));
    }

    @Test
    public void testTwoSpanBeforeRequest() throws Exception {
        reportSpan();
        final String span1 = spanAsJson();
        reportSpan();
        final String span2 = spanAsJson();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stagemonitor/spans");
        request.addParameter("connectionId", connectionId);
        MockHttpServletResponse response = new MockHttpServletResponse();
        spanServlet.service(request, response);
        Assert.assertEquals(Arrays.asList(span1, span2).toString(), response.getContentAsString());
        Assert.assertEquals("application/json;charset=UTF-8", response.getHeader("content-type"));
    }

    @Test
    public void testSpanAfterRequest() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stagemonitor/spans");
        request.addParameter("connectionId", connectionId);
        request.setAsyncSupported(false);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        performNonBlockingRequest(request, response);
        reportSpan();
        waitForResponse(response);
        Assert.assertEquals(spanAsJsonArray(), response.getContentAsString());
        Assert.assertEquals("application/json;charset=UTF-8", response.getHeader("content-type"));
    }

    @Test
    public void testSpanAfterRequestDifferentConnection() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stagemonitor/spans");
        request.addParameter("connectionId", UUID.randomUUID().toString());
        request.setAsyncSupported(true);
        MockHttpServletResponse response = new MockHttpServletResponse();
        performNonBlockingRequest(request, response);
        reportSpan();
        waitForResponse(response);
        Assert.assertEquals("[]", response.getContentAsString());
    }

    @Test
    public void testMissingConnectionId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stagemonitor/spans");
        MockHttpServletResponse response = new MockHttpServletResponse();
        spanServlet.service(request, response);
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testInvalidConnectionId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stagemonitor/spans");
        request.addParameter("connectionId", "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        spanServlet.service(request, response);
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void testWidgetDeactivated() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stagemonitor/spans");
        request.addParameter("connectionId", "");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Mockito.when(servletPlugin.isWidgetAndStagemonitorEndpointsAllowed(ArgumentMatchers.eq(request))).thenReturn(Boolean.FALSE);
        ConfigurationRegistry configuration = Mockito.mock(ConfigurationRegistry.class);
        Mockito.when(configuration.getConfig(ServletPlugin.class)).thenReturn(servletPlugin);
        new org.springframework.mock.web.MockFilterChain(spanServlet, new org.stagemonitor.web.servlet.filter.StagemonitorSecurityFilter(configuration)).doFilter(request, response);
        Assert.assertEquals(404, response.getStatus());
    }
}

