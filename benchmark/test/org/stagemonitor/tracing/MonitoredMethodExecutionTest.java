package org.stagemonitor.tracing;


import io.opentracing.mock.MockTracer;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.core.metrics.MetricsReporterTestHelper;
import org.stagemonitor.core.metrics.metrics2.Metric2Registry;
import org.stagemonitor.tracing.utils.SpanUtils;
import org.stagemonitor.tracing.wrapper.SpanWrappingTracer;


public class MonitoredMethodExecutionTest {
    private final Metric2Registry registry = new Metric2Registry();

    private MonitoredMethodExecutionTest.TestObject testObject;

    private ConfigurationRegistry configuration;

    private Map<String, Object> tags;

    private SpanWrappingTracer tracer;

    private MockTracer mockTracer;

    @Test
    public void testDoubleForwarding() throws Exception {
        testObject.monitored1();
        assertThat(mockTracer.finishedSpans()).hasSize(3);
        Assert.assertEquals("monitored1()", mockTracer.finishedSpans().get(2).operationName());
        Assert.assertEquals(tags.toString(), "1", tags.get(((SpanUtils.PARAMETERS_PREFIX) + "arg0")));
        Assert.assertEquals(tags.toString(), "test", tags.get(((SpanUtils.PARAMETERS_PREFIX) + "arg1")));
        assertThat(registry.getTimers()).containsKey(name("response_time").operationName("monitored1()").operationType("method_invocation").build());
        assertThat(registry.getTimers()).containsKey(name("response_time").operationName("monitored2()").operationType("method_invocation").build());
        assertThat(registry.getTimers()).containsKey(name("response_time").operationName("monitored3()").operationType("method_invocation").build());
        assertThat(registry.getTimers()).doesNotContainKey(name("response_time").operationName("notMonitored()").operationType("method_invocation").build());
    }

    @Test
    public void testNormalForwarding() throws Exception {
        testObject.monitored3();
        assertThat(registry.getTimers()).doesNotContainKey(name("response_time").operationName("monitored1()").operationType("method_invocation").build());
        assertThat(registry.getTimers()).doesNotContainKey(name("response_time").operationName("monitored2()").operationType("method_invocation").build());
        assertThat(registry.getTimers()).containsKey(name("response_time").operationName("monitored3()").operationType("method_invocation").build());
        assertThat(registry.getTimers()).doesNotContainKey(name("response_time").operationName("notMonitored()").operationType("method_invocation").build());
    }

    private class TestObject {
        private final RequestMonitor requestMonitor;

        private TestObject(RequestMonitor requestMonitor) {
            this.requestMonitor = requestMonitor;
        }

        private void monitored1() throws Exception {
            requestMonitor.monitor(new MonitoredMethodRequest(configuration, "monitored1()", this::monitored2, MetricsReporterTestHelper.<String, Object>map("arg0", 1).add("arg1", "test")));
        }

        private void monitored2() throws Exception {
            requestMonitor.monitor(new MonitoredMethodRequest(configuration, "monitored2()", this::monitored3));
        }

        private void monitored3() throws Exception {
            requestMonitor.monitor(new MonitoredMethodRequest(configuration, "monitored3()", this::notMonitored));
        }

        private int notMonitored() {
            return 1;
        }
    }
}

