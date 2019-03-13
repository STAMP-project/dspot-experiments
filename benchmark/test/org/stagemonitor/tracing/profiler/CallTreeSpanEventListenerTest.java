package org.stagemonitor.tracing.profiler;


import SpanUtils.CALL_TREE_ASCII;
import SpanUtils.CALL_TREE_JSON;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.tracing.SpanContextInformation;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.tracing.wrapper.SpanWrapper;


public class CallTreeSpanEventListenerTest {
    private TracingPlugin tracingPlugin;

    private SpanWrapper span;

    private ConfigurationRegistry configurationRegistry;

    @Test
    public void testProfileThisExecutionDeactive() throws Exception {
        Mockito.doReturn(0.0).when(tracingPlugin).getProfilerRateLimitPerMinute();
        final SpanContextInformation spanContext = invokeEventListener();
        assertThat(spanContext.getCallTree()).isNull();
    }

    @Test
    public void testProfileThisExecutionAlwaysActive() throws Exception {
        Mockito.doReturn(1000000.0).when(tracingPlugin).getProfilerRateLimitPerMinute();
        final SpanContextInformation spanContext = invokeEventListener();
        assertThat(spanContext.getCallTree()).isNotNull();
        assertThat(span.getStringTag(CALL_TREE_JSON)).isNotNull();
        assertThat(span.getStringTag(CALL_TREE_ASCII)).isNotNull();
    }

    @Test
    public void testExcludeCallTreeTags() throws Exception {
        Mockito.doReturn(1000000.0).when(tracingPlugin).getProfilerRateLimitPerMinute();
        Mockito.when(tracingPlugin.getExcludedTags()).thenReturn(Arrays.asList(CALL_TREE_JSON, CALL_TREE_ASCII));
        final SpanContextInformation spanContext = invokeEventListener();
        assertThat(spanContext.getCallTree()).isNotNull();
        assertThat(span.getStringTag(CALL_TREE_JSON)).isNull();
        assertThat(span.getStringTag(CALL_TREE_ASCII)).isNull();
    }

    @Test
    public void testDontActivateProfilerWhenSpanIsNotSampled() throws Exception {
        Mockito.doReturn(1000000.0).when(tracingPlugin).getProfilerRateLimitPerMinute();
        final SpanContextInformation spanContext = invokeEventListener(false);
        assertThat(spanContext.getCallTree()).isNull();
    }

    @Test
    public void testRateLimiting() throws Exception {
        Mockito.when(tracingPlugin.getProfilerRateLimitPerMinute()).thenReturn(1.0);
        final SpanContextInformation spanContext1 = invokeEventListener();
        Assert.assertNotNull(spanContext1.getCallTree());
        final SpanContextInformation spanContext2 = invokeEventListener();
        Assert.assertNotNull(spanContext2.getCallTree());
    }
}

