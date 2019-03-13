package org.stagemonitor.web.servlet.eum;


import B3HeaderFormat.B3Identifiers;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import javax.servlet.ServletException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.stagemonitor.tracing.B3HeaderFormat;
import org.stagemonitor.tracing.TracingPlugin;
import org.stagemonitor.tracing.reporter.ReportingSpanEventListener;
import org.stagemonitor.web.servlet.ServletPlugin;
import org.stagemonitor.web.servlet.eum.ClientSpanMetadataTagProcessor.ClientSpanMetadataDefinition;


public class ClientSpanServletTest {
    private MockTracer mockTracer;

    private ClientSpanServlet servlet;

    private ServletPlugin servletPlugin;

    private ReportingSpanEventListener reportingSpanEventListener;

    private TracingPlugin tracingPlugin;

    @Test
    public void testConvertWeaselBeaconToSpan_withPageLoadBeacon() throws IOException, ServletException {
        // Given
        final String userAgentHeader = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("user-agent", userAgentHeader);
        mockHttpServletRequest.setParameter("ty", "pl");
        mockHttpServletRequest.setParameter("r", "1496751574200");
        mockHttpServletRequest.setParameter("u", "http://localhost:9966/petclinic/");
        mockHttpServletRequest.setParameter("m_user", "tom.mason@example.com");
        mockHttpServletRequest.setParameter("ts", "-197");
        mockHttpServletRequest.setParameter("d", "518");
        mockHttpServletRequest.setParameter("sp", "1");
        mockHttpServletRequest.setParameter("m_bsp", "1");
        mockHttpServletRequest.setParameter("t_unl", "0");
        mockHttpServletRequest.setParameter("t_red", "0");
        mockHttpServletRequest.setParameter("t_apc", "5");
        mockHttpServletRequest.setParameter("t_dns", "0");
        mockHttpServletRequest.setParameter("t_tcp", "0");
        mockHttpServletRequest.setParameter("t_ssl", "2");
        mockHttpServletRequest.setParameter("t_req", "38");
        mockHttpServletRequest.setParameter("t_rsp", "4");
        mockHttpServletRequest.setParameter("t_pro", "471");
        mockHttpServletRequest.setParameter("t_loa", "5");
        mockHttpServletRequest.setParameter("t_fp", "151");
        // TODO, ignore for now
        mockHttpServletRequest.setParameter("k", "someKey");// not necessary

        mockHttpServletRequest.setParameter("t", "a6b5fd025be24191");// trace id -> opentracing does not specify this yet

        mockHttpServletRequest.setParameter("res", "{\"http://localhost:9966/petclinic/\":{\"webjars/\":{\"bootstrap/2.3.0/\":{\"css/bootstrap.min.css\":[\"-136,10,2,1,105939\"],\"img/glyphicons-halflings.png\":[\"-48,0,4,1,12799\"]},\"jquery\":{\"/2.0.3/jquery.js\":[\"-136,0,3,1,242142\"],\"-ui/1.10.3/\":{\"ui/jquery.ui.\":{\"core.js\":[\"-136,0,3,1,8198\"],\"datepicker.js\":[\"-136,0,3,1,76324\"]},\"themes/base/jquery-ui.css\":[\"-136,8,2,1,32456\"]}}},\"resources/\":{\"css/petclinic.css\":[\"-136,8,2,1,243\"],\"images/\":{\"banner-graphic.png\":[\"-136,0,1,1,13773\"],\"pets.png\":[\"-136,0,1,1,55318\"],\"spring-pivotal-logo.png\":[\"-136,0,1,1,2818\"]}},\"stagemonitor/\":{\"public/static/\":{\"rum/boomerang-56c823668fc.min.js\":[\"-136,0,3,1,12165\"],\"eum.debug.js\":[\"-32,12,3,3,23798\"]},\"static/stagemonitor\":{\".png\":[\"-136,16,1,3,1694\"],\"-modal.html\":[\"-33,9,5,3,10538\"]}}}}");
        // When
        servlet.doPost(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        Mockito.verify(reportingSpanEventListener, Mockito.never()).update(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        assertSoftly(( softly) -> {
            final List<MockSpan> finishedSpans = mockTracer.finishedSpans();
            softly.assertThat(finishedSpans).hasSize(1);
            MockSpan span = finishedSpans.get(0);
            softly.assertThat(span.operationName()).isEqualTo("/petclinic/");
            softly.assertThat(((span.finishMicros()) - (span.startMicros()))).isEqualTo(TimeUnit.MILLISECONDS.toMicros(518L));
            final long redirect = 0L;
            final long appCacheLookup = 5L;
            final long dns = 0L;
            final long tcp = 0L;
            final long ssl = 2L;
            final long request = 38L;
            final long response = 4L;
            softly.assertThat(span.tags()).containsEntry("type", "pageload").doesNotContainEntry(Tags.SAMPLING_PRIORITY.getKey(), 0).doesNotContainKey("user").containsEntry("user_agent.header", userAgentHeader).containsEntry("http.url", "http://localhost:9966/petclinic/").containsEntry("timing.unload", 0L).containsEntry("timing.redirect", redirect).containsEntry("timing.app_cache_lookup", appCacheLookup).containsEntry("timing.dns_lookup", dns).containsEntry("timing.tcp", tcp).containsEntry("timing.ssl", ssl).containsEntry("timing.request", request).containsEntry("timing.response", response).containsEntry("timing.processing", 471L).containsEntry("timing.load", 5L).containsEntry("timing.time_to_first_paint", 151L).containsEntry("timing.resource", ((((((redirect + appCacheLookup) + dns) + tcp) + ssl) + request) + response));
        });
    }

    @Test
    public void testConvertWeaselBeaconToSpan_withPageLoadBeacon_withBackendTraceId() throws IOException, ServletException {
        // Given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("ty", "pl");
        mockHttpServletRequest.setParameter("d", "518");
        mockHttpServletRequest.setParameter("r", "1496751574200");
        mockHttpServletRequest.setParameter("ts", "-197");
        mockHttpServletRequest.setParameter("t", "a6b5fd025be24191");// trace id -> opentracing does not specify this yet

        final String backendTraceId = "6210be349041096e";
        mockHttpServletRequest.setParameter("bt", backendTraceId);
        final String backendSpanId = "6210be349041096e";
        mockHttpServletRequest.setParameter("m_bs", backendSpanId);
        // When
        servlet.doPost(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        final java.util.List<MockSpan> finishedSpans = mockTracer.finishedSpans();
        assertThat(finishedSpans).hasSize(1);
        MockSpan span = finishedSpans.get(0);
        final String spanIdOfPageloadTrace = B3HeaderFormat.getB3Identifiers(mockTracer, span).getSpanId();
        final B3HeaderFormat.B3Identifiers traceIdsOfServerSpan = B3Identifiers.builder().traceId(backendTraceId).spanId(backendSpanId).build();
        final B3HeaderFormat.B3Identifiers newSpanIdentifiers = B3Identifiers.builder().traceId(backendTraceId).spanId(backendSpanId).parentSpanId(spanIdOfPageloadTrace).build();
        Mockito.verify(reportingSpanEventListener).update(ArgumentMatchers.eq(traceIdsOfServerSpan), ArgumentMatchers.eq(newSpanIdentifiers), ArgumentMatchers.eq(Collections.emptyMap()));
        assertSoftly(( softly) -> // TODO test via B3HeaderFormat.getTraceId(mockTracer, span); when https://github.com/opentracing/specification/issues/81 is resolved
        softly.assertThat(span.tags()).containsEntry("type", "pageload").containsEntry("trace_id", backendTraceId).doesNotContainEntry(Tags.SAMPLING_PRIORITY.getKey(), 0));
    }

    @Test
    public void testConvertWeaselBeaconToSpan_withNegativeRedirectTimeIsDiscarded() throws IOException, ServletException {
        // Given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("ty", "pl");
        mockHttpServletRequest.setParameter("r", "1496751574200");
        mockHttpServletRequest.setParameter("u", "http://localhost:9966/petclinic/");
        mockHttpServletRequest.setParameter("ts", "-197");
        mockHttpServletRequest.setParameter("d", "518");
        mockHttpServletRequest.setParameter("sp", "1");
        mockHttpServletRequest.setParameter("t_unl", "0");
        mockHttpServletRequest.setParameter("t_red", "-500");
        mockHttpServletRequest.setParameter("t_apc", "5");
        mockHttpServletRequest.setParameter("t_dns", "0");
        mockHttpServletRequest.setParameter("t_tcp", "0");
        mockHttpServletRequest.setParameter("t_ssl", "2");
        mockHttpServletRequest.setParameter("t_req", "38");
        mockHttpServletRequest.setParameter("t_rsp", "4");
        mockHttpServletRequest.setParameter("t_pro", "471");
        mockHttpServletRequest.setParameter("t_loa", "5");
        mockHttpServletRequest.setParameter("t_fp", "151");
        // When
        servlet.doPost(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertSpanIsDiscarded();
    }

    @Test
    public void testConvertWeaselBeaconToSpan_withDisabledSamplingFlagIsDiscarded() throws IOException, ServletException {
        // Given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("ty", "pl");
        mockHttpServletRequest.setParameter("r", "1496751574200");
        mockHttpServletRequest.setParameter("u", "http://localhost:9966/petclinic/");
        mockHttpServletRequest.setParameter("ts", "-197");
        mockHttpServletRequest.setParameter("d", "518");
        mockHttpServletRequest.setParameter("sp", "0");
        mockHttpServletRequest.setParameter("t_unl", "0");
        mockHttpServletRequest.setParameter("t_red", "500");
        mockHttpServletRequest.setParameter("t_apc", "5");
        mockHttpServletRequest.setParameter("t_dns", "0");
        mockHttpServletRequest.setParameter("t_tcp", "0");
        mockHttpServletRequest.setParameter("t_ssl", "2");
        mockHttpServletRequest.setParameter("t_req", "38");
        mockHttpServletRequest.setParameter("t_rsp", "4");
        mockHttpServletRequest.setParameter("t_pro", "471");
        mockHttpServletRequest.setParameter("t_loa", "5");
        mockHttpServletRequest.setParameter("t_fp", "151");
        mockHttpServletRequest.setParameter("t_fp", "151");
        // When
        servlet.doPost(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertSpanIsDiscarded();
    }

    @Test
    public void testConvertWeaselBeaconToSpan_withUnsampledBackendSpanFlagIsDiscarded() throws IOException, ServletException {
        // Given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("ty", "pl");
        mockHttpServletRequest.setParameter("r", "1496751574200");
        mockHttpServletRequest.setParameter("u", "http://localhost:9966/petclinic/");
        mockHttpServletRequest.setParameter("ts", "-197");
        mockHttpServletRequest.setParameter("d", "518");
        mockHttpServletRequest.setParameter("sp", "1");
        mockHttpServletRequest.setParameter("m_bsp", "0");
        mockHttpServletRequest.setParameter("t_unl", "0");
        mockHttpServletRequest.setParameter("t_red", "500");
        mockHttpServletRequest.setParameter("t_apc", "5");
        mockHttpServletRequest.setParameter("t_dns", "0");
        mockHttpServletRequest.setParameter("t_tcp", "0");
        mockHttpServletRequest.setParameter("t_ssl", "2");
        mockHttpServletRequest.setParameter("t_req", "38");
        mockHttpServletRequest.setParameter("t_rsp", "4");
        mockHttpServletRequest.setParameter("t_pro", "471");
        mockHttpServletRequest.setParameter("t_loa", "5");
        mockHttpServletRequest.setParameter("t_fp", "151");
        mockHttpServletRequest.setParameter("t_fp", "151");
        // When
        servlet.doPost(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertSpanIsDiscarded();
    }

    @Test
    public void testConvertWeaselBeaconToSpan_skipsTagProcessorsIfSpanIsNotSampled() throws IOException, ServletException {
        // Given
        Mockito.when(tracingPlugin.isSampled(ArgumentMatchers.any())).thenReturn(false);
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("ty", "pl");
        mockHttpServletRequest.setParameter("r", "1496751574200");
        mockHttpServletRequest.setParameter("u", "http://localhost:9966/petclinic/");
        mockHttpServletRequest.setParameter("m_user", "tom.mason@example.com");
        mockHttpServletRequest.setParameter("ts", "-197");
        mockHttpServletRequest.setParameter("d", "518");
        mockHttpServletRequest.setParameter("t_unl", "0");
        mockHttpServletRequest.setParameter("t_red", "0");
        mockHttpServletRequest.setParameter("t_apc", "5");
        mockHttpServletRequest.setParameter("t_dns", "0");
        mockHttpServletRequest.setParameter("t_tcp", "0");
        mockHttpServletRequest.setParameter("t_ssl", "0");
        mockHttpServletRequest.setParameter("t_req", "38");
        mockHttpServletRequest.setParameter("t_rsp", "4");
        mockHttpServletRequest.setParameter("t_pro", "471");
        mockHttpServletRequest.setParameter("t_loa", "5");
        mockHttpServletRequest.setParameter("t_fp", "151");
        // When
        servlet.doPost(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertSoftly(( softly) -> {
            final List<MockSpan> finishedSpans = mockTracer.finishedSpans();
            softly.assertThat(finishedSpans).hasSize(1);
            MockSpan span = finishedSpans.get(0);
            softly.assertThat(span.tags()).containsEntry("type", "pageload").doesNotContainKeys(TIMING_UNLOAD, TIMING_REDIRECT, TIMING_APP_CACHE_LOOKUP, TIMING_DNS_LOOKUP, TIMING_TCP, TIMING_REQUEST, TIMING_RESPONSE, TIMING_PROCESSING, TIMING_LOAD, TIMING_TIME_TO_FIRST_PAINT, TIMING_RESOURCE);
        });
    }

    @Test
    public void testConvertWeaselBeaconToSpan_withMetadata() throws IOException, ServletException {
        // Given - normal trace data
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("ty", "pl");
        mockHttpServletRequest.setParameter("r", "1496751574200");
        mockHttpServletRequest.setParameter("u", "http://localhost:9966/petclinic/");
        mockHttpServletRequest.setParameter("m_user", "tom.mason@example.com");
        mockHttpServletRequest.setParameter("ts", "-197");
        mockHttpServletRequest.setParameter("d", "518");
        mockHttpServletRequest.setParameter("t_unl", "0");
        mockHttpServletRequest.setParameter("t_red", "0");
        mockHttpServletRequest.setParameter("t_apc", "5");
        mockHttpServletRequest.setParameter("t_dns", "0");
        mockHttpServletRequest.setParameter("t_tcp", "0");
        mockHttpServletRequest.setParameter("t_ssl", "0");
        mockHttpServletRequest.setParameter("t_req", "38");
        mockHttpServletRequest.setParameter("t_rsp", "4");
        mockHttpServletRequest.setParameter("t_pro", "471");
        mockHttpServletRequest.setParameter("t_loa", "5");
        mockHttpServletRequest.setParameter("t_fp", "151");
        // Given - metadata
        mockHttpServletRequest.setParameter("m_username", "test string here");
        mockHttpServletRequest.setParameter("m_age", "26");
        mockHttpServletRequest.setParameter("m_is_access_allowed", "1");
        mockHttpServletRequest.setParameter("m_some_not_mapped_property", "this should not exist");
        // When
        final HashMap<String, ClientSpanMetadataDefinition> whitelistedValues = new HashMap<>();
        whitelistedValues.put("username", new ClientSpanMetadataDefinition("string"));
        whitelistedValues.put("age", new ClientSpanMetadataDefinition("number"));
        whitelistedValues.put("is_access_allowed", new ClientSpanMetadataDefinition("boolean"));
        whitelistedValues.put("parameter_not_sent", new ClientSpanMetadataDefinition("string"));
        Mockito.when(servletPlugin.getWhitelistedClientSpanTags()).thenReturn(whitelistedValues);
        servlet.doGet(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertSoftly(( softly) -> {
            final List<MockSpan> finishedSpans = mockTracer.finishedSpans();
            softly.assertThat(finishedSpans).hasSize(1);
            MockSpan span = finishedSpans.get(0);
            softly.assertThat(span.operationName()).isEqualTo("/petclinic/");
            softly.assertThat(span.tags()).doesNotContainEntry(Tags.SAMPLING_PRIORITY.getKey(), 0).containsEntry("type", "pageload").containsEntry("username", "test string here").containsEntry("age", 26.0).containsEntry("is_access_allowed", true).doesNotContainKey("parameter_not_sent").doesNotContainKey("some_not_mapped_property");
        });
    }

    @Test
    public void testConvertWeaselBeaconToSpan_withErrorBeacon() throws IOException, ServletException {
        // Given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("k", "someKey");
        mockHttpServletRequest.setParameter("s", "3776086ebf658768");
        mockHttpServletRequest.setParameter("t", "3776086ebf658768");
        mockHttpServletRequest.setParameter("ts", "1496753245024");
        mockHttpServletRequest.setParameter("ty", "err");
        mockHttpServletRequest.setParameter("pl", "e6bf60fdf2672398");
        mockHttpServletRequest.setParameter("l", "http://localhost:9966/petclinic/");
        mockHttpServletRequest.setParameter("e", "Uncaught null");
        mockHttpServletRequest.setParameter("st", "at http://localhost:9966/petclinic/ 301:34");
        mockHttpServletRequest.setParameter("c", "1");
        mockHttpServletRequest.setParameter("m_user", "tom.mason@example.com");
        // When
        servlet.doGet(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertSoftly(( softly) -> {
            final List<MockSpan> finishedSpans = mockTracer.finishedSpans();
            softly.assertThat(finishedSpans).hasSize(1);
            MockSpan span = finishedSpans.get(0);
            softly.assertThat(span.operationName()).isEqualTo("/petclinic/");
            softly.assertThat(((span.finishMicros()) - (span.startMicros()))).isZero();
            softly.assertThat(span.tags()).doesNotContainEntry(Tags.SAMPLING_PRIORITY.getKey(), 0).containsEntry("http.url", "http://localhost:9966/petclinic/").containsEntry("type", "js_error").containsEntry("exception.stack_trace", "at http://localhost:9966/petclinic/ 301:34").containsEntry("exception.message", "Uncaught null");
        });
    }

    @Test
    public void testConvertWeaselBeaconToSpan_withErrorBeaconTrimsTooLongStackTraces() throws IOException, ServletException {
        // Given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("k", "someKey");
        mockHttpServletRequest.setParameter("s", "3776086ebf658768");
        mockHttpServletRequest.setParameter("t", "3776086ebf658768");
        mockHttpServletRequest.setParameter("ts", "1496753245024");
        mockHttpServletRequest.setParameter("ty", "err");
        mockHttpServletRequest.setParameter("pl", "e6bf60fdf2672398");
        mockHttpServletRequest.setParameter("l", "http://localhost:9966/petclinic/");
        mockHttpServletRequest.setParameter("e", "Uncaught null");
        final StringBuilder stacktrace = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            stacktrace.append("at http://localhost:9966/petclinic/ 301:34\n");
        }
        mockHttpServletRequest.setParameter("st", stacktrace.toString());
        mockHttpServletRequest.setParameter("c", "1");
        mockHttpServletRequest.setParameter("m_user", "tom.mason@example.com");
        // When
        servlet.doGet(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertSoftly(( softly) -> {
            final List<MockSpan> finishedSpans = mockTracer.finishedSpans();
            softly.assertThat(finishedSpans).hasSize(1);
            MockSpan span = finishedSpans.get(0);
            softly.assertThat(span.operationName()).isEqualTo("/petclinic/");
            softly.assertThat(((span.finishMicros()) - (span.startMicros()))).isZero();
            softly.assertThat(span.tags()).doesNotContainEntry(Tags.SAMPLING_PRIORITY.getKey(), 0).containsEntry("http.url", "http://localhost:9966/petclinic/").containsEntry("type", "js_error").containsKey("exception.stack_trace").containsEntry("exception.message", "Uncaught null");
            softly.assertThat(((String) (span.tags().get("exception.stack_trace"))).length()).isLessThan(stacktrace.length()).isGreaterThan(0);
        });
    }

    @Test
    public void testConvertWeaselBeaconToSpan_withXHRBeacon() throws IOException, ServletException {
        // Given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("r", "1496994284184");
        mockHttpServletRequest.setParameter("k", "null");
        mockHttpServletRequest.setParameter("ts", "21793");
        mockHttpServletRequest.setParameter("d", "2084");
        mockHttpServletRequest.setParameter("ty", "xhr");
        mockHttpServletRequest.setParameter("pl", "d58cddae830273d1");
        mockHttpServletRequest.setParameter("l", "http://localhost:9966/petclinic/");
        mockHttpServletRequest.setParameter("m", "GET");
        mockHttpServletRequest.setParameter("u", "http://localhost:9966/petclinic/owners.html?lastName=");
        mockHttpServletRequest.setParameter("a", "1");
        mockHttpServletRequest.setParameter("st", "200");
        mockHttpServletRequest.setParameter("e", "undefined");
        mockHttpServletRequest.setParameter("m_user", "tom.mason@example.com");
        mockHttpServletRequest.setParameter("t", "2d371455215c504");
        mockHttpServletRequest.setParameter("s", "2d371455215c504");
        // When
        servlet.doGet(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertSoftly(( softly) -> {
            final List<MockSpan> finishedSpans = mockTracer.finishedSpans();
            softly.assertThat(finishedSpans).hasSize(1);
            MockSpan span = finishedSpans.get(0);
            softly.assertThat(span.operationName()).isEqualTo("/petclinic/owners.html");
            softly.assertThat(((span.finishMicros()) - (span.startMicros()))).isEqualTo(TimeUnit.MILLISECONDS.toMicros(2084L));
            softly.assertThat(span.tags()).doesNotContainEntry(Tags.SAMPLING_PRIORITY.getKey(), 0).containsEntry("type", "ajax").containsEntry("http.status_code", 200L).containsEntry("method", "GET").containsEntry("xhr.requested_url", "http://localhost:9966/petclinic/owners.html?lastName=").containsEntry("xhr.requested_from", "http://localhost:9966/petclinic/").containsEntry("xhr.async", true).containsEntry("duration_ms", 2084L).containsEntry("id", "2d371455215c504").containsEntry("trace_id", "2d371455215c504");
        });
    }

    @Test
    public void testWeaselBeaconXhrBeacon_withSampledFlag() throws IOException, ServletException {
        // Given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("t", "2d371455215c504");
        mockHttpServletRequest.setParameter("s", "2d371455215c504");
        mockHttpServletRequest.setParameter("ty", "xhr");
        mockHttpServletRequest.setParameter("sp", "0");
        // ignored
        mockHttpServletRequest.setParameter(WeaselClientSpanExtension.METADATA_BACKEND_SPAN_ID, "1");
        // When
        servlet.doGet(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertSoftly(( softly) -> {
            final List<MockSpan> finishedSpans = mockTracer.finishedSpans();
            softly.assertThat(finishedSpans).hasSize(1);
            MockSpan span = finishedSpans.get(0);
            softly.assertThat(span.tags()).containsEntry("id", "2d371455215c504").containsEntry("trace_id", "2d371455215c504").containsEntry(Tags.SAMPLING_PRIORITY.getKey(), 0);
        });
    }

    @Test
    public void testWeaselBeaconPageLoadBeacon_withSampledFlag() throws IOException, ServletException {
        // Given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("t", "2d371455215c504");
        mockHttpServletRequest.setParameter("s", "2d371455215c504");
        mockHttpServletRequest.setParameter("ty", ClientSpanServlet.TYPE_PAGE_LOAD);
        // ignored
        mockHttpServletRequest.setParameter("sp", "0");
        mockHttpServletRequest.setParameter(WeaselClientSpanExtension.METADATA_BACKEND_SPAN_SAMPLING_FLAG, "1");
        // When
        servlet.doGet(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertSoftly(( softly) -> {
            final List<MockSpan> finishedSpans = mockTracer.finishedSpans();
            softly.assertThat(finishedSpans).hasSize(1);
            MockSpan span = finishedSpans.get(0);
            softly.assertThat(span.tags()).containsEntry("id", "2d371455215c504").containsEntry("trace_id", "2d371455215c504").containsEntry(Tags.SAMPLING_PRIORITY.getKey(), 1);
        });
    }

    @Test
    public void testJsessionId() throws IOException, ServletException {
        // Given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("ty", "pl");
        mockHttpServletRequest.setParameter("r", "1496751574200");
        mockHttpServletRequest.setParameter("u", "http://localhost:9966/petclinic;jsessionid=xyz");
        mockHttpServletRequest.setParameter("d", "518");
        // When
        servlet.doPost(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertThat(mockTracer.finishedSpans().get(0).operationName()).isEqualTo("/petclinic");
    }

    @Test
    public void testQueryParameter() throws IOException, ServletException {
        // Given
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.setParameter("ty", "pl");
        mockHttpServletRequest.setParameter("r", "1496751574200");
        mockHttpServletRequest.setParameter("u", "http://localhost:9966/petclinic?jsessionid=xyz");
        mockHttpServletRequest.setParameter("d", "518");
        // When
        servlet.doPost(mockHttpServletRequest, new MockHttpServletResponse());
        // Then
        assertThat(mockTracer.finishedSpans().get(0).operationName()).isEqualTo("/petclinic");
    }
}

