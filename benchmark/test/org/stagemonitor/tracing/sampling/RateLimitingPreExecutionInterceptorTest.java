package org.stagemonitor.tracing.sampling;


import SimpleSource.NAME;
import org.junit.Assert;
import org.junit.Test;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.tracing.SpanContextInformation;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.tracing.wrapper.SpanWrapper;


public class RateLimitingPreExecutionInterceptorTest {
    private RateLimitingPreExecutionInterceptor interceptor;

    private TracingPlugin tracingPlugin;

    private PreExecutionInterceptorContext context;

    private SpanContextInformation spanContext;

    private ConfigurationRegistry configuration;

    @Test
    public void testNeverReportSpan() throws Exception {
        tracingPlugin.getDefaultRateLimitSpansPerMinuteOption().update(0.0, NAME);
        interceptor.interceptReport(context);
        Assert.assertFalse(context.isReport());
        interceptor.interceptReport(context);
        Assert.assertFalse(context.isReport());
    }

    @Test
    public void testAlwaysReportSpan() throws Exception {
        tracingPlugin.getDefaultRateLimitSpansPerMinuteOption().update(1000000.0, NAME);
        interceptor.interceptReport(context);
        Assert.assertTrue(context.isReport());
    }

    @Test
    public void testRateNotExceededThenExceededSpan() throws Exception {
        tracingPlugin.getDefaultRateLimitSpansPerMinuteOption().update(60.0, NAME);
        interceptor.interceptReport(context);
        Assert.assertTrue(context.isReport());
        interceptor.interceptReport(context);
        Assert.assertFalse(context.isReport());
    }

    @Test
    public void testRateExceededThenNotExceededSpan() throws Exception {
        tracingPlugin.getDefaultRateLimitSpansPerMinuteOption().update(60.0, NAME);
        interceptor.interceptReport(context);
        Assert.assertTrue(context.isReport());
        interceptor.interceptReport(context);
        Assert.assertFalse(context.isReport());
    }

    @Test
    public void dontMakeSamplingDecisionsForNonRootTraces() throws Exception {
        interceptor = new RateLimitingPreExecutionInterceptor() {
            @Override
            protected boolean isRoot(SpanWrapper span) {
                return false;
            }
        };
        interceptor.init(configuration);
        tracingPlugin.getDefaultRateLimitSpansPerMinuteOption().update(0.0, NAME);
        interceptor.interceptReport(context);
        Assert.assertTrue(context.isReport());
    }

    @Test
    public void makeSamplingDecisionsForRootTraces() throws Exception {
        interceptor = new RateLimitingPreExecutionInterceptor() {
            @Override
            protected boolean isRoot(SpanWrapper span) {
                return true;
            }
        };
        interceptor.init(configuration);
        tracingPlugin.getDefaultRateLimitSpansPerMinuteOption().update(0.0, NAME);
        interceptor.interceptReport(context);
        Assert.assertFalse(context.isReport());
    }
}

