/**
 * Copyright 2015-2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.server.internal;


import SpanBytesEncoder.JSON_V2;
import SpringBootTest.WebEnvironment;
import com.linecorp.armeria.server.Server;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import java.util.Arrays;
import java.util.List;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import zipkin.server.ZipkinServer;
import zipkin2.Span;
import zipkin2.TestObjects;
import zipkin2.storage.InMemoryStorage;


@SpringBootTest(classes = ZipkinServer.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = "spring.config.name=zipkin-server")
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class ITZipkinMetricsHealth {
    @Autowired
    InMemoryStorage storage;

    @Autowired
    PrometheusMeterRegistry registry;

    @Autowired
    Server server;

    OkHttpClient client = new OkHttpClient.Builder().followRedirects(true).build();

    @Test
    public void healthIsOK() throws Exception {
        assertThat(get("/health").isSuccessful()).isTrue();
        // ensure we don't track health in prometheus
        assertThat(scrape()).doesNotContain("health");
    }

    @Test
    public void metricsIsOK() throws Exception {
        assertThat(get("/metrics").isSuccessful()).isTrue();
        // ensure we don't track metrics in prometheus
        assertThat(scrape()).doesNotContain("metrics");
    }

    @Test
    public void actuatorIsOK() throws Exception {
        assertThat(get("/actuator").isSuccessful()).isTrue();
        // ensure we don't track actuator in prometheus
        assertThat(scrape()).doesNotContain("actuator");
    }

    @Test
    public void prometheusIsOK() throws Exception {
        assertThat(get("/prometheus").isSuccessful()).isTrue();
        // ensure we don't track prometheus, UI requests in prometheus
        assertThat(scrape()).doesNotContain("prometheus").doesNotContain("uri=\"/zipkin").doesNotContain("uri=\"/\"");
    }

    @Test
    public void notFound_prometheus() throws Exception {
        assertThat(get("/doo-wop").isSuccessful()).isFalse();
        assertThat(scrape()).contains("uri=\"NOT_FOUND\"").doesNotContain("uri=\"/doo-wop");
    }

    @Test
    public void redirected_prometheus() throws Exception {
        assertThat(get("/").isSuccessful()).isTrue();// follows redirects

        assertThat(scrape()).contains("uri=\"REDIRECTION\"").contains("uri=\"/zipkin/index.html\"").doesNotContain("uri=\"/\"");
    }

    @Test
    public void apiTemplate_prometheus() throws Exception {
        List<Span> spans = Arrays.asList(TestObjects.LOTS_OF_SPANS[0]);
        byte[] body = JSON_V2.encodeList(spans);
        assertThat(post("/api/v2/spans", body).isSuccessful()).isTrue();
        assertThat(get(("/api/v2/trace/" + (TestObjects.LOTS_OF_SPANS[0].traceId()))).isSuccessful()).isTrue();
        assertThat(scrape()).contains("uri=\"/api/v2/trace/{traceId}\"").doesNotContain(TestObjects.LOTS_OF_SPANS[0].traceId());
    }

    @Test
    public void forwardedRoute_prometheus() throws Exception {
        assertThat(get("/zipkin/api/v2/services").isSuccessful()).isTrue();
        assertThat(scrape()).contains("uri=\"/api/v2/services\"").doesNotContain("uri=\"/zipkin/api/v2/services\"");
    }

    /**
     * Makes sure the prometheus filter doesn't count twice
     */
    @Test
    public void writeSpans_updatesPrometheusMetrics() throws Exception {
        List<Span> spans = Arrays.asList(TestObjects.LOTS_OF_SPANS[0], TestObjects.LOTS_OF_SPANS[1], TestObjects.LOTS_OF_SPANS[2]);
        byte[] body = JSON_V2.encodeList(spans);
        post("/api/v2/spans", body);
        post("/api/v2/spans", body);
        Thread.sleep(100);// sometimes travis flakes getting the "http.server.requests" timer

        double messagesCount = registry.counter("zipkin_collector.spans", "transport", "http").count();
        // Get the http count from the registry and it should match the summation previous count
        // and count of calls below
        long httpCount = registry.find("http.server.requests").tag("uri", "/api/v2/spans").timer().count();
        // ensure unscoped counter does not exist
        assertThat(scrape()).doesNotContain(("zipkin_collector_spans_total " + messagesCount)).contains(("zipkin_collector_spans_total{transport=\"http\",} " + messagesCount)).contains(("http_server_requests_seconds_count{method=\"POST\",status=\"202\",uri=\"/api/v2/spans\",} " + httpCount));
    }

    @Test
    public void writeSpans_updatesMetrics() throws Exception {
        List<Span> spans = Arrays.asList(TestObjects.LOTS_OF_SPANS[0], TestObjects.LOTS_OF_SPANS[1], TestObjects.LOTS_OF_SPANS[2]);
        byte[] body = JSON_V2.encodeList(spans);
        double messagesCount = registry.counter("zipkin_collector.messages", "transport", "http").count();
        double bytesCount = registry.counter("zipkin_collector.bytes", "transport", "http").count();
        double spansCount = registry.counter("zipkin_collector.spans", "transport", "http").count();
        post("/api/v2/spans", body);
        post("/api/v2/spans", body);
        String json = getAsString("/metrics");
        assertThat(ITZipkinMetricsHealth.readDouble(json, "$.['counter.zipkin_collector.messages.http']")).isEqualTo((messagesCount + 2.0));
        assertThat(ITZipkinMetricsHealth.readDouble(json, "$.['counter.zipkin_collector.bytes.http']")).isEqualTo((bytesCount + ((body.length) * 2)));
        assertThat(ITZipkinMetricsHealth.readDouble(json, "$.['gauge.zipkin_collector.message_bytes.http']")).isEqualTo(body.length);
        assertThat(ITZipkinMetricsHealth.readDouble(json, "$.['counter.zipkin_collector.spans.http']")).isEqualTo((spansCount + ((spans.size()) * 2)));
        assertThat(ITZipkinMetricsHealth.readDouble(json, "$.['gauge.zipkin_collector.message_spans.http']")).isEqualTo(spans.size());
    }

    @Test
    public void writeSpans_malformedUpdatesMetrics() throws Exception {
        byte[] body = new byte[]{ 'h', 'e', 'l', 'l', 'o' };
        Double messagesCount = registry.counter("zipkin_collector.messages", "transport", "http").count();
        Double messagesDroppedCount = registry.counter("zipkin_collector.messages_dropped", "transport", "http").count();
        post("/api/v2/spans", body);
        String json = getAsString("/metrics");
        assertThat(ITZipkinMetricsHealth.readDouble(json, "$.['counter.zipkin_collector.messages.http']")).isEqualTo((messagesCount + 1));
        assertThat(ITZipkinMetricsHealth.readDouble(json, "$.['counter.zipkin_collector.messages_dropped.http']")).isEqualTo((messagesDroppedCount + 1));
    }

    @Test
    public void readsHealth() throws Exception {
        String json = getAsString("/health");
        assertThat(ITZipkinMetricsHealth.readString(json, "$.status")).isIn("UP", "DOWN", "UNKNOWN");
        assertThat(ITZipkinMetricsHealth.readString(json, "$.zipkin.status")).isIn("UP", "DOWN", "UNKNOWN");
    }

    @Test
    public void writesSpans_readMetricsFormat() throws Exception {
        byte[] span = new byte[]{ 'z', 'i', 'p', 'k', 'i', 'n' };
        List<Span> spans = Arrays.asList(TestObjects.LOTS_OF_SPANS[0], TestObjects.LOTS_OF_SPANS[1], TestObjects.LOTS_OF_SPANS[2]);
        byte[] body = JSON_V2.encodeList(spans);
        post("/api/v2/spans", body);
        post("/api/v2/spans", body);
        post("/api/v2/spans", span);
        Thread.sleep(1500);
        String metrics = getAsString("/metrics");
        assertThat(ITZipkinMetricsHealth.readJson(metrics)).containsExactlyInAnyOrder("gauge.zipkin_collector.message_spans.http", "gauge.zipkin_collector.message_bytes.http", "counter.zipkin_collector.messages.http", "counter.zipkin_collector.bytes.http", "counter.zipkin_collector.spans.http", "counter.zipkin_collector.messages_dropped.http", "counter.zipkin_collector.spans_dropped.http");
    }
}

