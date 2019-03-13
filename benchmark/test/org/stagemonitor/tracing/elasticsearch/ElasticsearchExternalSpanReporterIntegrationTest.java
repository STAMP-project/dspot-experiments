package org.stagemonitor.tracing.elasticsearch;


import com.fasterxml.jackson.databind.JsonNode;
import io.opentracing.mock.MockTracer;
import io.opentracing.util.ThreadLocalScopeManager;
import org.junit.Test;
import org.mockito.Mockito;
import org.stagemonitor.AbstractElasticsearchTest;
import org.stagemonitor.configuration.ConfigurationRegistry;
import org.stagemonitor.tracing.SpanContextInformation;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.tracing.tracing.B3Propagator;


public class ElasticsearchExternalSpanReporterIntegrationTest extends AbstractElasticsearchTest {
    protected ElasticsearchSpanReporter reporter;

    protected TracingPlugin tracingPlugin;

    protected ConfigurationRegistry configuration;

    protected final MockTracer mockTracer = new MockTracer(new ThreadLocalScopeManager(), new B3Propagator());

    @Test
    public void reportTemplateCreated() throws Exception {
        final JsonNode template = elasticsearchClient.getJson("/_template/stagemonitor-spans").get("stagemonitor-spans");
        assertThat(template.get("index_patterns").get(0).asText()).isEqualTo("stagemonitor-spans-*");
        assertThat(template.get("mappings").get("_default_").get("_all").get("enabled").asBoolean()).as(template.toString()).isEqualTo(false);
    }

    @Test
    public void reportSpan() throws Exception {
        reporter.report(Mockito.mock(SpanContextInformation.class), getSpan(100));
        elasticsearchClient.waitForCompletion();
        refresh();
        final JsonNode hits = elasticsearchClient.getJson("/stagemonitor-spans*/_search").get("hits");
        assertThat(hits.get("total").intValue()).as(hits.toString()).isEqualTo(1);
        final JsonNode spanJson = hits.get("hits").elements().next().get("_source");
        assertThat(spanJson.get("type").asText()).as(spanJson.toString()).isEqualTo("jdbc");
        assertThat(spanJson.get("method").asText()).as(spanJson.toString()).isEqualTo("SELECT");
        assertThat(spanJson.get("db.statement")).as(spanJson.toString()).isNotNull();
        assertThat(spanJson.get("db.statement").asText()).as(spanJson.toString()).isEqualTo("SELECT * from STAGEMONITOR where 1 < 2");
        assertThat(spanJson.get("duration_ms").asInt()).as(spanJson.toString()).isEqualTo(100);
        assertThat(spanJson.get("name").asText()).as(spanJson.toString()).isEqualTo("ElasticsearchExternalSpanReporterIntegrationTest#test");
    }
}

