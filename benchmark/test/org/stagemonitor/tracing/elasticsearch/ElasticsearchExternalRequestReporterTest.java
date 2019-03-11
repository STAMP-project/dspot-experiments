package org.stagemonitor.tracing.elasticsearch;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ElasticsearchExternalRequestReporterTest extends AbstractElasticsearchSpanReporterTest {
    private ElasticsearchSpanReporter reporter;

    @Test
    public void testReportSpan() throws Exception {
        Mockito.when(elasticsearchTracingPlugin.isOnlyLogElasticsearchSpanReports()).thenReturn(false);
        reportSpan();
        Mockito.verify(httpClient).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertTrue(reporter.isActive(null));
        verifyTimerCreated(1);
    }

    @Test
    public void doNotReportSpan() throws Exception {
        Mockito.when(elasticsearchTracingPlugin.isOnlyLogElasticsearchSpanReports()).thenReturn(false);
        Mockito.when(elasticsearchClient.isElasticsearchAvailable()).thenReturn(false);
        Mockito.when(corePlugin.getElasticsearchUrl()).thenReturn(null);
        reportSpan();
        Mockito.verify(httpClient, Mockito.times(0)).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(spanLogger, Mockito.times(0)).info(ArgumentMatchers.anyString());
        Assert.assertFalse(reporter.isActive(null));
        verifyTimerCreated(1);
    }

    @Test
    public void testLogReportSpan() throws Exception {
        Mockito.when(elasticsearchTracingPlugin.isOnlyLogElasticsearchSpanReports()).thenReturn(true);
        reportSpan();
        Mockito.verify(httpClient, Mockito.times(0)).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(spanLogger).info(ArgumentMatchers.startsWith("{\"index\":{\"_index\":\"stagemonitor-spans-"));
    }

    @Test
    public void reportSpanRateLimited() throws Exception {
        Mockito.when(tracingPlugin.getDefaultRateLimitSpansPerMinute()).thenReturn(1.0);
        reportSpan();
        Mockito.verify(httpClient).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        reportSpan();
        Mockito.verifyNoMoreInteractions(spanLogger);
        verifyTimerCreated(2);
    }

    @Test
    public void excludeExternalRequestsFasterThan() throws Exception {
        Mockito.when(tracingPlugin.getExcludeExternalRequestsFasterThan()).thenReturn(100.0);
        reportSpan(100);
        Mockito.verify(httpClient).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        reportSpan(99);
        Mockito.verifyNoMoreInteractions(spanLogger);
        verifyTimerCreated(2);
    }

    @Test
    public void testElasticsearchExcludeFastCallTree() throws Exception {
        Mockito.when(tracingPlugin.getExcludeExternalRequestsWhenFasterThanXPercent()).thenReturn(0.85);
        reportSpan(1000);
        Mockito.verify(httpClient).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        reportSpan(250);
        Mockito.verifyNoMoreInteractions(spanLogger);
        verifyTimerCreated(2);
    }

    @Test
    public void testElasticsearchDontExcludeSlowCallTree() throws Exception {
        Mockito.when(tracingPlugin.getExcludeExternalRequestsWhenFasterThanXPercent()).thenReturn(0.85);
        reportSpan(250);
        reportSpan(1000);
        Mockito.verify(httpClient, Mockito.times(2)).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        verifyTimerCreated(2);
    }
}

