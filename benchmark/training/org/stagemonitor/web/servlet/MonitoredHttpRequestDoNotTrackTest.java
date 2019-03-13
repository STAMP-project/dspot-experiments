package org.stagemonitor.web.servlet;


import Tags.SAMPLING_PRIORITY;
import io.opentracing.Scope;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.tracing.SpanContextInformation;
import org.stagemonitor.tracing.wrapper.SpanWrapper;
import org.stagemonitor.tracing.wrapper.SpanWrappingTracer;


public class MonitoredHttpRequestDoNotTrackTest {
    private ServletPlugin servletPlugin;

    private ConfigurationRegistry configuration;

    private SpanWrappingTracer tracer;

    @Test
    public void testHonorDoNotTrack() throws Exception {
        Mockito.when(servletPlugin.isHonorDoNotTrackHeader()).thenReturn(true);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("dnt", "1");
        Scope activeScope = createScope();
        SpanWrapper span = SpanContextInformation.getCurrent().getSpanWrapper();
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isEqualTo(0);
        activeScope.close();
    }

    @Test
    public void testDoNotTrackDisabled() throws Exception {
        Mockito.when(servletPlugin.isHonorDoNotTrackHeader()).thenReturn(true);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("dnt", "0");
        Scope activeScope = createScope();
        SpanWrapper span = SpanContextInformation.getCurrent().getSpanWrapper();
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isNotEqualTo(0);
        activeScope.close();
    }

    @Test
    public void testNoDoNotTrackHeader() throws Exception {
        Mockito.when(servletPlugin.isHonorDoNotTrackHeader()).thenReturn(true);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        Scope activeScope = createScope();
        SpanWrapper span = SpanContextInformation.getCurrent().getSpanWrapper();
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isNotEqualTo(0);
        activeScope.close();
    }

    @Test
    public void testDontHonorDoNotTrack() throws Exception {
        Mockito.when(servletPlugin.isHonorDoNotTrackHeader()).thenReturn(false);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("dnt", "1");
        Scope activeScope = createScope();
        SpanWrapper span = SpanContextInformation.getCurrent().getSpanWrapper();
        assertThat(span.getNumberTag(SAMPLING_PRIORITY.getKey())).isNotEqualTo(0);
        activeScope.close();
    }
}

