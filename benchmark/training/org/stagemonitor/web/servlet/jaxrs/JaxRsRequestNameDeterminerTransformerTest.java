package org.stagemonitor.web.servlet.jaxrs;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.tracing.MonitoredRequest;
import org.stagemonitor.tracing.RequestMonitor;
import org.stagemonitor.tracing.SpanCapturingReporter;
import org.stagemonitor.tracing.SpanContextInformation;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.web.servlet.ServletPlugin;


public class JaxRsRequestNameDeterminerTransformerTest {
    private JaxRsRequestNameDeterminerTransformerTest.TestResource resource = new JaxRsRequestNameDeterminerTransformerTest.TestResource();

    private SpanCapturingReporter spanCapturingReporter;

    private ConfigurationRegistry configuration = Mockito.mock(ConfigurationRegistry.class);

    private TracingPlugin tracingPlugin = Mockito.mock(TracingPlugin.class);

    private ServletPlugin servletPlugin = Mockito.mock(ServletPlugin.class);

    private CorePlugin corePlugin = Mockito.mock(CorePlugin.class);

    private RequestMonitor requestMonitor;

    private Metric2Registry registry = new Metric2Registry();

    @Test
    public void testSetNameForRestCalls() throws Exception {
        final MonitoredRequest request = new org.stagemonitor.tracing.MonitoredMethodRequest(configuration, "override me", () -> resource.getTestString());
        requestMonitor.monitor(request);
        final SpanContextInformation info = spanCapturingReporter.get();
        Assert.assertNotNull(info);
        Assert.assertEquals("Get Test String", info.getOperationName());
    }

    @Path("/")
    public class TestResource {
        @GET
        public String getTestString() {
            return "test";
        }
    }
}

