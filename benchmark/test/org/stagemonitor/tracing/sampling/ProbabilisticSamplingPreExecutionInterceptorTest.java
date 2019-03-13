package org.stagemonitor.tracing.sampling;


import SimpleSource.NAME;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.tracing.SpanContextInformation;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.tracing.wrapper.SpanWrapper;


public class ProbabilisticSamplingPreExecutionInterceptorTest {
    private ProbabilisticSamplingPreExecutionInterceptor interceptor;

    private TracingPlugin tracingPlugin;

    private PreExecutionInterceptorContext context;

    private SpanContextInformation spanContext;

    private ConfigurationRegistry configuration;

    @Test
    public void testNeverReportSpan() throws Exception {
        tracingPlugin.getDefaultRateLimitSpansPercentOption().update(0.0, NAME);
        interceptor.interceptReport(context);
        Assert.assertFalse(context.isReport());
        interceptor.interceptReport(context);
        Assert.assertFalse(context.isReport());
    }

    @Test
    public void testAlwaysReportSpan() throws Exception {
        tracingPlugin.getDefaultRateLimitSpansPercentOption().update(1.0, NAME);
        interceptor.interceptReport(context);
        Assert.assertTrue(context.isReport());
    }

    @Test
    public void testValidationFailed() throws Exception {
        assertThatThrownBy(() -> tracingPlugin.getDefaultRateLimitSpansPercentOption().update(10.0, SimpleSource.NAME)).isInstanceOf(IllegalArgumentException.class);
        assertThat(tracingPlugin.getDefaultRateLimitSpansPercentOption().getValue()).isEqualTo(1.0);
    }

    @Test
    public void testValidationFailedPerTypeOption() throws Exception {
        assertThatThrownBy(() -> tracingPlugin.getRateLimitSpansPerMinutePercentPerTypeOption().update(singletonMap("jdbc", 10.0), SimpleSource.NAME)).isInstanceOf(IllegalArgumentException.class);
        assertThat(tracingPlugin.getRateLimitSpansPerMinutePercentPerTypeOption().getValue()).isEqualTo(Collections.emptyMap());
    }

    @Test
    public void testSample51Percent() throws Exception {
        tracingPlugin.getDefaultRateLimitSpansPercentOption().update(0.51, NAME);
        int reports = 0;
        for (int i = 0; i < 100; i++) {
            final PreExecutionInterceptorContext context = new PreExecutionInterceptorContext(spanContext);
            interceptor.interceptReport(context);
            if (context.isReport()) {
                reports++;
            }
        }
        assertThat(reports).isEqualTo(51);
    }

    @Test
    public void testReportSpanGenericType() throws Exception {
        Mockito.when(spanContext.getOperationType()).thenReturn("jdbc");
        tracingPlugin.getDefaultRateLimitSpansPercentOption().update(0.0, NAME);
        tracingPlugin.getRateLimitSpansPerMinutePercentPerTypeOption().update(Collections.singletonMap("http", 1.0), NAME);
        interceptor.interceptReport(context);
        Assert.assertFalse(context.isReport());
    }

    @Test
    public void testReportSpanType() throws Exception {
        interceptor = new ProbabilisticSamplingPreExecutionInterceptor() {
            @Override
            protected boolean isRoot(SpanWrapper span) {
                return false;
            }
        };
        interceptor.init(configuration);
        Mockito.when(spanContext.getOperationType()).thenReturn("http");
        tracingPlugin.getDefaultRateLimitSpansPercentOption().update(0.0, NAME);
        tracingPlugin.getRateLimitSpansPerMinutePercentPerTypeOption().update(Collections.singletonMap("http", 1.0), NAME);
        interceptor.interceptReport(context);
        Assert.assertTrue(context.isReport());
    }

    @Test
    public void dontMakeSamplingDecisionsForNonRootTraces() throws Exception {
        interceptor = new ProbabilisticSamplingPreExecutionInterceptor() {
            @Override
            protected boolean isRoot(SpanWrapper span) {
                return false;
            }
        };
        interceptor.init(configuration);
        tracingPlugin.getDefaultRateLimitSpansPercentOption().update(0.0, NAME);
        interceptor.interceptReport(context);
        Assert.assertTrue(context.isReport());
    }

    @Test
    public void makeSamplingDecisionsForRootTraces() throws Exception {
        interceptor = new ProbabilisticSamplingPreExecutionInterceptor() {
            @Override
            protected boolean isRoot(SpanWrapper span) {
                return true;
            }
        };
        interceptor.init(configuration);
        tracingPlugin.getDefaultRateLimitSpansPercentOption().update(0.0, NAME);
        interceptor.interceptReport(context);
        Assert.assertFalse(context.isReport());
    }
}

