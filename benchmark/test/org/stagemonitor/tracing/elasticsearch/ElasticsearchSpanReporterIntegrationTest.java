package org.stagemonitor.tracing.elasticsearch;


import SpanUtils.OPERATION_TYPE;
import Tags.SPAN_KIND;
import Tags.SPAN_KIND_SERVER;
import com.fasterxml.jackson.databind.JsonNode;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.stagemonitor.AbstractElasticsearchTest;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.tracing.B3HeaderFormat;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.tracing.utils.SpanUtils;


public class ElasticsearchSpanReporterIntegrationTest extends AbstractElasticsearchTest {
    protected ElasticsearchSpanReporter reporter;

    protected TracingPlugin tracingPlugin;

    protected ConfigurationRegistry configuration;

    private Tracer tracer;

    @Test
    public void reportSpan() throws Exception {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("attr.Color", "Blue");
        parameters.put("attr", "bla");
        parameters.put("foo", "bar");
        final Span span = tracer.buildSpan("Test#test").withTag(SPAN_KIND.getKey(), SPAN_KIND_SERVER).start();
        SpanUtils.setParameters(span, parameters);
        span.setTag(OPERATION_TYPE, "method_invocation");
        span.setTag("foo.bar", "baz");
        span.finish();
        elasticsearchClient.waitForCompletion();
        refresh();
        final JsonNode hits = elasticsearchClient.getJson("/stagemonitor-spans*/_search").get("hits");
        assertThat(hits.get("total").intValue()).as(hits.toString()).isEqualTo(1);
        validateSpanJson(hits.get("hits").elements().next().get("_source"));
    }

    @Test
    public void testUpdateSpan() throws Exception {
        final Span span = tracer.buildSpan("Test#test").withTag(SPAN_KIND.getKey(), SPAN_KIND_SERVER).start();
        span.finish();
        elasticsearchClient.waitForCompletion();
        refresh();
        reporter.updateSpan(B3HeaderFormat.getB3Identifiers(tracer, span), null, Collections.singletonMap("foo", "bar"));
        refresh();
        final JsonNode hits = elasticsearchClient.getJson("/stagemonitor-spans*/_search").get("hits");
        assertThat(hits.get("total").intValue()).as(hits.toString()).isEqualTo(1);
        final JsonNode spanJson = hits.get("hits").elements().next().get("_source");
        assertThat(spanJson.get("foo").asText()).as(spanJson.toString()).isEqualTo("bar");
    }

    @Test
    public void testUpdateNotYetExistentSpan_eventuallyUpdates() throws Exception {
        final Span span = tracer.buildSpan("Test#test").withTag(SPAN_KIND.getKey(), SPAN_KIND_SERVER).start();
        reporter.updateSpan(B3HeaderFormat.getB3Identifiers(tracer, span), null, Collections.singletonMap("foo", "bar"));
        span.finish();
        elasticsearchClient.waitForCompletion();
        refresh();
        reporter.getUpdateReporter().flush();
        refresh();
        final JsonNode hits = elasticsearchClient.getJson("/stagemonitor-spans*/_search").get("hits");
        assertThat(hits.get("total").intValue()).as(hits.toString()).isEqualTo(1);
        final JsonNode spanJson = hits.get("hits").elements().next().get("_source");
        assertThat(spanJson.get("foo").asText()).as(spanJson.toString()).isEqualTo("bar");
    }
}

