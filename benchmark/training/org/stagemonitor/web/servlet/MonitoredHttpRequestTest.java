package org.stagemonitor.web.servlet;


import Tags.ERROR;
import Tags.HTTP_URL;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.tracing.utils.SpanUtils;

import static junit.framework.Assert.assertNull;


public class MonitoredHttpRequestTest {
    private ConfigurationRegistry configuration;

    private MockTracer tracer;

    private ServletPlugin servletPlugin;

    @Test
    public void testGetRequestName() throws Exception {
        final MonitoredHttpRequest monitoredHttpRequest = createMonitoredHttpRequest(new MockHttpServletRequest("GET", "/test.js"));
        Assert.assertEquals("GET *.js", monitoredHttpRequest.getRequestName());
    }

    @Test
    public void testCreateSpan() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test.js");
        request.addParameter("foo", "bar");
        request.addParameter("bla", "blubb");
        request.addParameter("pwd", "secret");
        request.addParameter("creditCard", "123456789");
        request.addHeader("Cookie", "foobar");
        request.addHeader("Accept", "application/json");
        final MonitoredHttpRequest monitoredHttpRequest = createMonitoredHttpRequest(request);
        monitoredHttpRequest.createScope().close();
        Assert.assertEquals(1, tracer.finishedSpans().size());
        final MockSpan mockSpan = tracer.finishedSpans().get(0);
        Assert.assertEquals("/test.js", mockSpan.tags().get(HTTP_URL.getKey()));
        Assert.assertEquals("GET *.js", mockSpan.operationName());
        Assert.assertEquals("GET", mockSpan.tags().get("method"));
        Assert.assertEquals("application/json", mockSpan.tags().get(((SpanUtils.HTTP_HEADERS_PREFIX) + "accept")));
        Assert.assertFalse(mockSpan.tags().containsKey(((SpanUtils.HTTP_HEADERS_PREFIX) + "cookie")));
        Assert.assertFalse(mockSpan.tags().containsKey(((SpanUtils.HTTP_HEADERS_PREFIX) + "Cookie")));
        Assert.assertEquals("bar", mockSpan.tags().get(((SpanUtils.PARAMETERS_PREFIX) + "foo")));
        Assert.assertEquals("blubb", mockSpan.tags().get(((SpanUtils.PARAMETERS_PREFIX) + "bla")));
        Assert.assertEquals("XXXX", mockSpan.tags().get(((SpanUtils.PARAMETERS_PREFIX) + "pwd")));
        Assert.assertEquals("XXXX", mockSpan.tags().get(((SpanUtils.PARAMETERS_PREFIX) + "creditCard")));
        Assert.assertFalse(mockSpan.tags().containsKey(ERROR.getKey()));
    }

    @Test
    public void testNumberFormatExceptionCreateSpan() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test.js");
        request.addHeader("spanid", "<script>alert(1);</script>");
        request.addHeader("traceid", "42");
        final MonitoredHttpRequest monitoredHttpRequest = createMonitoredHttpRequest(request);
        monitoredHttpRequest.createScope().close();
        Assert.assertEquals(1, tracer.finishedSpans().size());
        final MockSpan mockSpan = tracer.finishedSpans().get(0);
        Assert.assertEquals("/test.js", mockSpan.tags().get(HTTP_URL.getKey()));
        Assert.assertEquals("GET *.js", mockSpan.operationName());
        Assert.assertEquals("GET", mockSpan.tags().get("method"));
        Assert.assertFalse(mockSpan.tags().containsKey(ERROR.getKey()));
    }

    @Test
    public void testReferringSite() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test.js");
        request.addHeader("Referer", "https://www.github.com/stagemonitor/stagemonitor");
        final MonitoredHttpRequest monitoredHttpRequest = createMonitoredHttpRequest(request);
        monitoredHttpRequest.createScope().close();
        Assert.assertEquals(1, tracer.finishedSpans().size());
        final MockSpan mockSpan = tracer.finishedSpans().get(0);
        Assert.assertEquals("www.github.com", mockSpan.tags().get("http.referring_site"));
    }

    @Test
    public void testReferringSameHostSite() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test.js");
        request.addHeader("Referer", "https://www.myapp.com:8080/categories");
        request.setServerName("www.myapp.com");
        final MonitoredHttpRequest monitoredHttpRequest = createMonitoredHttpRequest(request);
        monitoredHttpRequest.createScope().close();
        Assert.assertEquals(1, tracer.finishedSpans().size());
        final MockSpan mockSpan = tracer.finishedSpans().get(0);
        assertNull(mockSpan.tags().get("http.referring_site"));
    }

    @Test
    public void testParseUserAgent() throws Exception {
        Mockito.doReturn(true).when(servletPlugin).isParseUserAgent();
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test.js");
        request.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        final MonitoredHttpRequest monitoredHttpRequest = createMonitoredHttpRequest(request);
        monitoredHttpRequest.createScope().close();
        Assert.assertEquals(1, tracer.finishedSpans().size());
        final MockSpan mockSpan = tracer.finishedSpans().get(0);
        assertThat(mockSpan.tags()).containsEntry("user_agent.browser", "Chrome");
    }

    @Test
    public void testGetClientIp() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.1.1.3");
        request.addHeader("x-forwarded-for", "10.1.1.1, 10.1.1.2, 10.1.1.3");
        assertThat(MonitoredHttpRequest.getClientIp(request)).isEqualTo("10.1.1.1");
    }
}

