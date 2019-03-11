package org.stagemonitor.tracing.sampling;


import Tags.SAMPLING_PRIORITY;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.stagemonitor.tracing.AbstractRequestMonitorTest;


public class SamplePriorityDeterminingSpanEventListenerTest extends AbstractRequestMonitorTest {
    @Test
    public void testSetSamplePrioInPreInterceptor() throws Exception {
        Mockito.when(tracingPlugin.isSampled(ArgumentMatchers.any())).thenReturn(true);
        samplePriorityDeterminingSpanInterceptor.addPreInterceptor(new PreExecutionSpanInterceptor() {
            @Override
            public void interceptReport(PreExecutionInterceptorContext context) {
                context.shouldNotReport(getClass());
            }
        });
        requestMonitor.monitor(new org.stagemonitor.tracing.MonitoredMethodRequest(configuration, "testSetSamplePrioInPreInterceptor", () -> {
        }));
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        assertThat(mockTracer.finishedSpans().get(0).tags()).containsEntry(SAMPLING_PRIORITY.getKey(), 0);
    }
}

