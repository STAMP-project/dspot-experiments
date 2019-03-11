package org.stagemonitor.tracing;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class RequestMonitorTest extends AbstractRequestMonitorTest {
    @Test
    public void testDeactivated() throws Exception {
        Mockito.doReturn(false).when(corePlugin).isStagemonitorActive();
        final SpanContextInformation spanContext = requestMonitor.monitor(createMonitoredRequest());
        Assert.assertNull(spanContext);
    }

    @Test
    public void testRecordException() throws Exception {
        final MonitoredRequest monitoredRequest = createMonitoredRequest();
        Mockito.doThrow(new RuntimeException("test")).when(monitoredRequest).execute();
        try {
            requestMonitor.monitor(monitoredRequest);
        } catch (Exception e) {
        }
        Assert.assertEquals("java.lang.RuntimeException", tags.get("exception.class"));
        Assert.assertEquals("test", tags.get("exception.message"));
        Assert.assertNotNull(tags.get("exception.stack_trace"));
    }

    @Test
    public void testInternalMetricsDeactive() throws Exception {
        internalMonitoringTestHelper(false);
    }

    @Test
    public void testInternalMetricsActive() throws Exception {
        Mockito.doReturn(true).when(corePlugin).isInternalMonitoringActive();
        requestMonitor.monitor(createMonitoredRequest());
        Mockito.verify(registry).timer(name("internal_overhead_request_monitor").build());
    }
}

