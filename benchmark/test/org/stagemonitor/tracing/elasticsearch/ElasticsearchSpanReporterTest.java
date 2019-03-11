package org.stagemonitor.tracing.elasticsearch;


import ElasticsearchUpdateSpanReporter.BulkUpdateOutputStreamHandler;
import SpanUtils.CALL_TREE_ASCII;
import SpanUtils.CALL_TREE_JSON;
import Tags.SAMPLING_PRIORITY;
import io.opentracing.mock.MockSpan;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.stagemonitor.tracing.SpanContextInformation;
import org.stagemonitor.tracing.reporter.SpanReporter;
import org.stagemonitor.util.StringUtils;


public class ElasticsearchSpanReporterTest extends AbstractElasticsearchSpanReporterTest {
    private ElasticsearchSpanReporter reporter;

    @Test
    public void testReportSpan() throws Exception {
        final SpanContextInformation spanContext = reportSpanWithCallTree(1000, "Report Me");
        final List<MockSpan> sampledSpans = getSampledSpans();
        assertThat(sampledSpans).hasSize(1);
        Mockito.verify(httpClient).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertTrue(reporter.isActive(spanContext));
    }

    @Test
    public void testLogReportSpan() throws Exception {
        Mockito.when(elasticsearchTracingPlugin.isOnlyLogElasticsearchSpanReports()).thenReturn(true);
        final SpanContextInformation spanContext = reportSpanWithCallTree(1000, "Report Me");
        final List<MockSpan> sampledSpans = getSampledSpans();
        assertThat(sampledSpans).hasSize(1);
        Mockito.verify(httpClient, Mockito.times(0)).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(spanLogger).info(ArgumentMatchers.startsWith((("{\"index\":{\"_index\":\"stagemonitor-spans-" + (StringUtils.getLogstashStyleDate())) + "\",\"_type\":\"spans\"}}\n")));
        Assert.assertTrue(reporter.isActive(spanContext));
    }

    @Test
    public void testReportSpanDontReport() throws Exception {
        final SpanContextInformation info = reportSpanWithCallTree(1, "Regular Foo");
        Assert.assertTrue(reporter.isActive(info));
        Assert.assertEquals(0, tags.get(SAMPLING_PRIORITY.getKey()));
    }

    @Test
    public void testElasticsearchExcludeCallTree() throws Exception {
        Mockito.when(tracingPlugin.getExcludeCallTreeFromReportWhenFasterThanXPercentOfRequests()).thenReturn(1.0);
        reportSpanWithCallTree(1000, "Report Me");
        reportSpanWithCallTree(500, "Report Me");
        reportSpanWithCallTree(250, "Report Me");
        final List<MockSpan> sampledSpans = getSampledSpans();
        assertThat(sampledSpans).hasSize(3);
        sampledSpans.forEach(( span) -> assertThat(span.tags()).doesNotContainKeys(SpanUtils.CALL_TREE_ASCII, SpanUtils.CALL_TREE_JSON));
    }

    @Test
    public void testElasticsearchDontExcludeCallTree() throws Exception {
        Mockito.when(tracingPlugin.getExcludeCallTreeFromReportWhenFasterThanXPercentOfRequests()).thenReturn(0.0);
        reportSpanWithCallTree(250, "Report Me");
        reportSpanWithCallTree(500, "Report Me");
        reportSpanWithCallTree(1000, "Report Me");
        final List<MockSpan> sampledSpans = getSampledSpans();
        assertThat(sampledSpans).hasSize(3);
        sampledSpans.forEach(( span) -> assertThat(span.tags()).containsKeys(SpanUtils.CALL_TREE_ASCII, SpanUtils.CALL_TREE_JSON));
    }

    @Test
    public void testElasticsearchExcludeFastCallTree() throws Exception {
        Mockito.when(tracingPlugin.getExcludeCallTreeFromReportWhenFasterThanXPercentOfRequests()).thenReturn(0.85);
        reportSpanWithCallTree(1000, "Report Me");
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        assertThat(mockTracer.finishedSpans().get(0).tags()).containsKeys(CALL_TREE_ASCII, CALL_TREE_JSON);
        mockTracer.reset();
        reportSpanWithCallTree(250, "Report Me");
        assertThat(mockTracer.finishedSpans()).hasSize(1);
        assertThat(mockTracer.finishedSpans().get(0).tags()).doesNotContainKeys(CALL_TREE_ASCII, CALL_TREE_JSON);
    }

    @Test
    public void testElasticsearchDontExcludeSlowCallTree() throws Exception {
        Mockito.when(tracingPlugin.getExcludeCallTreeFromReportWhenFasterThanXPercentOfRequests()).thenReturn(0.85);
        reportSpanWithCallTree(250, "Report Me");
        reportSpanWithCallTree(1000, "Report Me");
        assertThat(getSampledSpans()).hasSize(2);
    }

    @Test
    public void testInterceptorServiceLoader() throws Exception {
        Mockito.when(tracingPlugin.getExcludeCallTreeFromReportWhenFasterThanXPercentOfRequests()).thenReturn(0.0);
        reportSpanWithCallTree(250, "Report Me");
        final List<MockSpan> sampledSpans = getSampledSpans();
        assertThat(sampledSpans).hasSize(1);
        assertThat(sampledSpans.get(0).tags()).containsEntry("serviceLoaderWorks", true);
    }

    @Test
    public void testLoadedViaServiceLoader() throws Exception {
        assertThat(StreamSupport.stream(ServiceLoader.load(SpanReporter.class).spliterator(), false).filter(( reporter) -> reporter instanceof ElasticsearchSpanReporter)).hasSize(1);
    }

    @Test
    public void testToBulkUpdateBytes() throws Exception {
        final ElasticsearchUpdateSpanReporter.BulkUpdateOutputStreamHandler bulkUpdateOutputStreamHandler = new ElasticsearchUpdateSpanReporter.BulkUpdateOutputStreamHandler("test-id", Collections.singletonMap("foo", "bar"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bulkUpdateOutputStreamHandler.withHttpURLConnection(output);
        assertThat(output.toString()).isEqualTo(("{\"update\":{\"_id\":\"test-id\"}}\n" + "{\"doc\":{\"foo\":\"bar\"}}\n"));
    }
}

