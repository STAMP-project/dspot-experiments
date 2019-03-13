package org.stagemonitor.tracing.tracing.jaeger;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.stagemonitor.core.CorePlugin;
import org.stagemonitor.core.MeasurementSession;
import org.stagemonitor.core.Stagemonitor;
import org.stagemonitor.tracing.mdc.MDCSpanEventListener;
import org.stagemonitor.tracing.wrapper.SpanWrapper;


public class MDCSpanEventListenerTest {
    private MDCSpanEventListener mdcSpanInterceptor;

    private CorePlugin corePlugin;

    private SpanWrapper spanWrapper;

    @Test
    public void testMdc() throws Exception {
        Stagemonitor.reset(new MeasurementSession("MDCSpanEventListenerTest", "testHost", "testInstance"));
        Mockito.when(corePlugin.getMeasurementSession()).thenReturn(new MeasurementSession("MDCSpanEventListenerTest", "testHost", "testInstance"));
        mdcSpanInterceptor.onStart(spanWrapper);
        Assert.assertNotNull(MDC.get("spanId"));
        Assert.assertNotNull(MDC.get("traceId"));
        Assert.assertNull(MDC.get("parentId"));
        mdcSpanInterceptor.onFinish(spanWrapper, null, 0);
        assertThat(MDC.getCopyOfContextMap()).isEmpty();
    }

    @Test
    public void testMdcStagemonitorNotStarted() throws Exception {
        final MeasurementSession measurementSession = new MeasurementSession("MDCSpanEventListenerTest", "testHost", null);
        Stagemonitor.reset(measurementSession);
        Mockito.when(corePlugin.getMeasurementSession()).thenReturn(measurementSession);
        mdcSpanInterceptor.onStart(spanWrapper);
        Assert.assertEquals("testHost", MDC.get("host"));
        Assert.assertEquals("MDCSpanEventListenerTest", MDC.get("application"));
        Assert.assertNull(MDC.get("instance"));
        Assert.assertNull(MDC.get("spanId"));
        Assert.assertNull(MDC.get("traceId"));
        Assert.assertNull(MDC.get("parentId"));
        mdcSpanInterceptor.onFinish(spanWrapper, null, 0);
    }

    @Test
    public void testMDCStagemonitorDeactivated() throws Exception {
        Mockito.when(corePlugin.isStagemonitorActive()).thenReturn(false);
        Mockito.when(corePlugin.getMeasurementSession()).thenReturn(new MeasurementSession("MDCSpanEventListenerTest", "testHost", null));
        mdcSpanInterceptor.onStart(spanWrapper);
        Assert.assertNull(MDC.getCopyOfContextMap());
    }
}

