package org.stagemonitor.web.servlet.spring;


import Tags.HTTP_URL;
import Tags.SAMPLING_PRIORITY;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.tracing.RequestMonitor;
import org.stagemonitor.tracing.SpanContextInformation;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.web.servlet.MonitoredHttpRequest;
import org.stagemonitor.web.servlet.ServletPlugin;


public class SpringRequestMonitorTest {
    private MockHttpServletRequest mvcRequest = new MockHttpServletRequest("GET", "/test/requestName");

    private MockHttpServletRequest nonMvcRequest = new MockHttpServletRequest("GET", "/META-INF/resources/stagemonitor/static/jquery.js");

    private ConfigurationRegistry configuration = Mockito.mock(ConfigurationRegistry.class);

    private TracingPlugin tracingPlugin = Mockito.mock(TracingPlugin.class);

    private ServletPlugin servletPlugin = Mockito.mock(ServletPlugin.class);

    private CorePlugin corePlugin = Mockito.mock(CorePlugin.class);

    private RequestMonitor requestMonitor;

    private Metric2Registry registry = new Metric2Registry();

    private HandlerMapping getRequestNameHandlerMapping;

    private DispatcherServlet dispatcherServlet;

    private Map<String, Object> tags = new HashMap<>();

    // the purpose of this class is to obtain a instance to a Method,
    // because Method objects can't be mocked as they are final
    private static class TestController {
        public void testGetRequestName() {
        }
    }

    @Test
    public void testRequestMonitorMvcRequest() throws Exception {
        Mockito.when(servletPlugin.isMonitorOnlySpringMvcRequests()).thenReturn(false);
        MonitoredHttpRequest monitoredRequest = createMonitoredHttpRequest(mvcRequest);
        final SpanContextInformation spanContext = requestMonitor.monitor(monitoredRequest);
        Assert.assertEquals("Test Get Request Name", spanContext.getOperationName());
        Assert.assertEquals(1, registry.timer(getResponseTimeMetricName(spanContext.getOperationName(), "http")).getCount());
        Assert.assertEquals("Test Get Request Name", spanContext.getOperationName());
        Assert.assertEquals("/test/requestName", tags.get(HTTP_URL.getKey()));
        Assert.assertEquals("GET", tags.get("method"));
        assertThat(registry.getTimers()).containsKey(name("response_time").operationName("Test Get Request Name").operationType("http").build());
    }

    @Test
    public void testRequestMonitorNonMvcRequestDoMonitor() throws Exception {
        Mockito.when(servletPlugin.isMonitorOnlySpringMvcRequests()).thenReturn(false);
        final MonitoredHttpRequest monitoredRequest = createMonitoredHttpRequest(nonMvcRequest);
        SpanContextInformation spanContext = requestMonitor.monitor(monitoredRequest);
        Assert.assertEquals("GET *.js", spanContext.getOperationName());
        Assert.assertEquals("GET *.js", spanContext.getOperationName());
        assertThat(registry.getTimers()).doesNotContainKey(getResponseTimeMetricName(spanContext.getOperationName(), "http"));
        assertThat(registry.getTimers()).containsKey(getResponseTimeMetricName("All", "http"));
        Mockito.verify(monitoredRequest, Mockito.times(1)).getRequestName();
        assertThat(tags.get(SAMPLING_PRIORITY.getKey())).isNotEqualTo(0);
    }

    @Test
    public void testRequestMonitorNonMvcRequestDontMonitor() throws Exception {
        Mockito.when(servletPlugin.isMonitorOnlySpringMvcRequests()).thenReturn(true);
        final MonitoredHttpRequest monitoredRequest = createMonitoredHttpRequest(nonMvcRequest);
        SpanContextInformation spanContext = requestMonitor.monitor(monitoredRequest);
        Assert.assertNull(spanContext.getOperationName());
        Assert.assertNull(registry.getTimers().get(name("response_time").operationName("GET *.js").operationType("http").build()));
        assertThat(tags.get(SAMPLING_PRIORITY.getKey())).isEqualTo(0);
    }

    @Test
    public void testGetRequestNameFromHandler() throws Exception {
        requestMonitor.monitorStart(createMonitoredHttpRequest(mvcRequest));
        final SpanContextInformation spanContext = SpanContextInformation.getCurrent();
        Assert.assertNotNull(spanContext);
        try {
            dispatcherServlet.service(mvcRequest, new MockHttpServletResponse());
        } finally {
            requestMonitor.monitorStop();
        }
        Assert.assertEquals("Test Get Request Name", spanContext.getOperationName());
    }
}

