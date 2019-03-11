package org.stagemonitor.tracing;


import Tags.SAMPLING_PRIORITY;
import io.opentracing.mock.MockTracer;
import org.junit.Test;


public class AbstractExternalRequestTest {
    @Test
    public void testDontMonitorClientRootSpans() throws Exception {
        MockTracer tracer = new MockTracer();
        createScope().close();
        assertThat(tracer.finishedSpans().get(0).tags().get(SAMPLING_PRIORITY.getKey())).isEqualTo(0);
    }
}

