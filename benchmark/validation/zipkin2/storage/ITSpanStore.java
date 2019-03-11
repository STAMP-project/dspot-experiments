/**
 * Copyright 2015-2018 The OpenZipkin Authors
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
package zipkin2.storage;


import QueryRequest.Builder;
import Span.Kind.CLIENT;
import Span.Kind.SERVER;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.Test;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.TestObjects;


/**
 * Base test for {@link SpanStore}.
 *
 * <p>Subtypes should create a connection to a real backend, even if that backend is in-process.
 */
public abstract class ITSpanStore {
    @Test
    public void getTrace_considersBitsAbove64bit() throws IOException {
        // 64-bit trace ID
        Span span1 = Span.newBuilder().traceId(TestObjects.CLIENT_SPAN.traceId().substring(16)).id("1").build();
        // 128-bit trace ID prefixed by above
        Span span2 = Span.newBuilder().traceId(TestObjects.CLIENT_SPAN.traceId()).id("2").build();
        // Different 128-bit trace ID prefixed by above
        Span span3 = Span.newBuilder().traceId(("1" + (span1.traceId()))).id("3").build();
        accept(span1, span2, span3);
        for (Span span : Arrays.asList(span1, span2, span3)) {
            assertThat(store().getTrace(span.traceId()).execute()).containsOnly(span);
        }
    }

    @Test
    public void getTrace_returnsEmptyOnNotFound() throws IOException {
        assertThat(store().getTrace(TestObjects.CLIENT_SPAN.traceId()).execute()).isEmpty();
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getTrace(TestObjects.CLIENT_SPAN.traceId()).execute()).containsExactly(TestObjects.CLIENT_SPAN);
        assertThat(store().getTrace(TestObjects.CLIENT_SPAN.traceId().substring(16)).execute()).isEmpty();
    }

    /**
     * This would only happen when the store layer is bootstrapping, or has been purged.
     */
    @Test
    public void allShouldWorkWhenEmpty() throws IOException {
        QueryRequest.Builder q = ITSpanStore.requestBuilder().serviceName("service");
        assertThat(store().getTraces(q.build()).execute()).isEmpty();
        assertThat(store().getTraces(q.spanName("methodcall").build()).execute()).isEmpty();
        assertThat(store().getTraces(q.parseAnnotationQuery("custom").build()).execute()).isEmpty();
        assertThat(store().getTraces(q.parseAnnotationQuery("BAH=BEH").build()).execute()).isEmpty();
    }

    /**
     * This is unlikely and means instrumentation sends empty spans by mistake.
     */
    @Test
    public void allShouldWorkWhenNoIndexableDataYet() throws IOException {
        accept(Span.newBuilder().traceId("1").id("1").build());
        allShouldWorkWhenEmpty();
    }

    @Test
    public void getTraces_considersBitsAbove64bit() throws IOException {
        // 64-bit trace ID
        Span span1 = Span.newBuilder().traceId(TestObjects.CLIENT_SPAN.traceId().substring(16)).id("1").putTag("foo", "1").timestamp(((TestObjects.TODAY) * 1000L)).localEndpoint(TestObjects.FRONTEND).build();
        // 128-bit trace ID prefixed by above
        Span span2 = span1.toBuilder().traceId(TestObjects.CLIENT_SPAN.traceId()).putTag("foo", "2").build();
        // Different 128-bit trace ID prefixed by above
        Span span3 = span1.toBuilder().traceId(("1" + (span1.traceId()))).putTag("foo", "3").build();
        accept(span1, span2, span3);
        for (Span span : Arrays.asList(span1, span2, span3)) {
            assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery(("foo=" + (span.tags().get("foo")))).build()).execute()).flatExtracting(( t) -> t).containsExactly(span);
        }
    }

    @Test
    public void getTraces_filteringMatchesMostRecentTraces() throws Exception {
        List<Endpoint> endpoints = IntStream.rangeClosed(1, 10).mapToObj(( i) -> Endpoint.newBuilder().serviceName(("service" + i)).ip("127.0.0.1").build()).collect(Collectors.toList());
        long gapBetweenSpans = 100;
        Span[] earlySpans = IntStream.rangeClosed(1, 10).mapToObj(( i) -> Span.newBuilder().name("early").traceId(Integer.toHexString(i)).id(Integer.toHexString(i)).timestamp((((TestObjects.TODAY) - i) * 1000L)).duration(1L).localEndpoint(endpoints.get((i - 1))).build()).toArray(Span[]::new);
        Span[] lateSpans = IntStream.rangeClosed(1, 10).mapToObj(( i) -> Span.newBuilder().name("late").traceId(Integer.toHexString((i + 10))).id(Integer.toHexString((i + 10))).timestamp(((((TestObjects.TODAY) + gapBetweenSpans) - i) * 1000L)).duration(1L).localEndpoint(endpoints.get((i - 1))).build()).toArray(Span[]::new);
        accept(earlySpans);
        accept(lateSpans);
        List<Span>[] earlyTraces = Stream.of(earlySpans).map(Collections::singletonList).toArray(List[]::new);
        List<Span>[] lateTraces = Stream.of(lateSpans).map(Collections::singletonList).toArray(List[]::new);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().build()).execute()).hasSize(20);
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().limit(10).build()).execute())).containsExactly(lateTraces);
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().endTs(((TestObjects.TODAY) + gapBetweenSpans)).lookback(gapBetweenSpans).build()).execute())).containsExactly(lateTraces);
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().endTs(TestObjects.TODAY).build()).execute())).containsExactly(earlyTraces);
    }

    @Test
    public void getTraces_localServiceName() throws Exception {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName(("frontend" + 1)).build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").build()).execute()).flatExtracting(( l) -> l).contains(TestObjects.CLIENT_SPAN);
    }

    @Test
    public void getTraces_spanName() throws Exception {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().spanName(((TestObjects.CLIENT_SPAN.name()) + 1)).build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().spanName(TestObjects.CLIENT_SPAN.name()).build()).execute()).flatExtracting(( l) -> l).contains(TestObjects.CLIENT_SPAN);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().spanName("frontend").spanName(TestObjects.CLIENT_SPAN.name()).build()).execute()).flatExtracting(( l) -> l).contains(TestObjects.CLIENT_SPAN);
    }

    @Test
    public void getTraces_spanName_128() throws Exception {
        // add a trace with the same trace ID truncated to 64 bits, except the span name.
        accept(TestObjects.CLIENT_SPAN.toBuilder().traceId(TestObjects.CLIENT_SPAN.traceId().substring(16)).name("bar").build());
        getTraces_spanName();
    }

    @Test
    public void getTraces_tags() throws Exception {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().annotationQuery(Collections.singletonMap("foo", "bar")).build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().annotationQuery(TestObjects.CLIENT_SPAN.tags()).build()).execute()).flatExtracting(( l) -> l).contains(TestObjects.CLIENT_SPAN);
    }

    @Test
    public void getTraces_minDuration() throws Exception {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().minDuration(((TestObjects.CLIENT_SPAN.durationAsLong()) + 1)).build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().minDuration(TestObjects.CLIENT_SPAN.duration()).build()).execute()).flatExtracting(( l) -> l).contains(TestObjects.CLIENT_SPAN);
    }

    @Test
    public void getTraces_maxDuration() throws Exception {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().minDuration(((TestObjects.CLIENT_SPAN.duration()) - 2)).maxDuration(((TestObjects.CLIENT_SPAN.duration()) - 1)).build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().minDuration(TestObjects.CLIENT_SPAN.duration()).maxDuration(TestObjects.CLIENT_SPAN.duration()).build()).execute()).flatExtracting(( l) -> l).contains(TestObjects.CLIENT_SPAN);
    }

    /**
     * While large spans are discouraged, and maybe not indexed, we should be able to read them back.
     */
    @Test
    public void readsBackLargeValues() throws IOException {
        char[] kilobyteOfText = new char[1024];
        Arrays.fill(kilobyteOfText, 'a');
        // Make a span that's over 1KiB in size
        Span span = Span.newBuilder().traceId("1").id("1").name("big").timestamp((((TestObjects.TODAY) * 1000L) + 100L)).duration(200L).localEndpoint(TestObjects.FRONTEND).putTag("a", new String(kilobyteOfText)).build();
        accept(span);
        // read back to ensure the data wasn't truncated
        assertThat(store().getTraces(ITSpanStore.requestBuilder().build()).execute()).containsExactly(Arrays.asList(span));
        assertThat(store().getTrace(span.traceId()).execute()).containsExactly(span);
    }

    /**
     * Not a good span name, but better to test it than break mysteriously
     */
    @Test
    public void spanNameIsJson() throws IOException {
        String json = "{\"foo\":\"bar\"}";
        Span withJsonSpanName = TestObjects.CLIENT_SPAN.toBuilder().name(json).build();
        accept(withJsonSpanName);
        QueryRequest query = ITSpanStore.requestBuilder().serviceName("frontend").spanName(json).build();
        assertThat(store().getTraces(query).execute()).extracting(( t) -> t.get(0).name()).containsExactly(json);
    }

    /**
     * Dots in tag names can create problems in storage which tries to make a tree out of them
     */
    @Test
    public void tagsWithNestedDots() throws IOException {
        Span tagsWithNestedDots = TestObjects.CLIENT_SPAN.toBuilder().putTag("http.path", "/api").putTag("http.path.morepath", "/api/api").build();
        accept(tagsWithNestedDots);
        assertThat(store().getTrace(tagsWithNestedDots.traceId()).execute()).containsExactly(tagsWithNestedDots);
    }

    /**
     * Formerly, a bug was present where cassandra didn't index more than bucket count traces per
     * millisecond. This stores a lot of spans to ensure indexes work under high-traffic scenarios.
     */
    @Test
    public void getTraces_manyTraces() throws IOException {
        int traceCount = 1000;
        Span span = TestObjects.LOTS_OF_SPANS[0];
        Map.Entry<String, String> tag = span.tags().entrySet().iterator().next();
        accept(Arrays.copyOfRange(TestObjects.LOTS_OF_SPANS, 0, traceCount));
        assertThat(store().getTraces(ITSpanStore.requestBuilder().limit(traceCount).build()).execute()).hasSize(traceCount);
        QueryRequest.Builder builder = ITSpanStore.requestBuilder().limit(traceCount).serviceName(span.localServiceName());
        assertThat(store().getTraces(builder.build()).execute()).hasSize(traceCount);
        assertThat(store().getTraces(builder.spanName(span.name()).build()).execute()).hasSize(traceCount);
        assertThat(store().getTraces(builder.parseAnnotationQuery((((tag.getKey()) + "=") + (tag.getValue()))).build()).execute()).hasSize(traceCount);
    }

    /**
     * Shows that duration queries go against the root span, not the child
     */
    @Test
    public void getTraces_duration() throws IOException {
        setupDurationData();
        QueryRequest.Builder q = ITSpanStore.requestBuilder().endTs(TestObjects.TODAY).lookback(TestObjects.DAY);// instead of since epoch

        QueryRequest query;
        // Min duration is inclusive and is applied by service.
        query = q.serviceName("service1").minDuration(200000L).build();
        assertThat(store().getTraces(query).execute()).extracting(( t) -> t.get(0).traceId()).containsExactly("0000000000000001");
        query = q.serviceName("service3").minDuration(200000L).build();
        assertThat(store().getTraces(query).execute()).extracting(( t) -> t.get(0).traceId()).containsExactly("0000000000000002");
        // Duration bounds aren't limited to root spans: they apply to all spans by service in a trace
        query = q.serviceName("service2").minDuration(50000L).maxDuration(150000L).build();
        // service2 root of trace 3, but middle of 1 and 2.
        assertThat(store().getTraces(query).execute()).extracting(( t) -> t.get(0).traceId()).containsExactlyInAnyOrder("0000000000000003", "0000000000000002", "0000000000000001");
        // Span name should apply to the duration filter
        query = q.serviceName("service2").spanName("zip").maxDuration(50000L).build();
        assertThat(store().getTraces(query).execute()).extracting(( t) -> t.get(0).traceId()).containsExactly("0000000000000003");
        // Max duration should filter our longer spans from the same service
        query = q.serviceName("service2").minDuration(50000L).maxDuration(50000L).build();
        assertThat(store().getTraces(query).execute()).extracting(( t) -> t.get(0).traceId()).containsExactly("0000000000000003");
    }

    /**
     * Spans and traces are meaningless unless they have a timestamp. While unlikely, this could
     * happen if a binary annotation is logged before a timestamped one is.
     */
    @Test
    public void getTraces_absentWhenNoTimestamp() throws IOException {
        // Index the service name but no timestamp of any sort
        accept(Span.newBuilder().traceId(TestObjects.CLIENT_SPAN.traceId()).id(TestObjects.CLIENT_SPAN.id()).name(TestObjects.CLIENT_SPAN.name()).localEndpoint(TestObjects.CLIENT_SPAN.localEndpoint()).build());
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").spanName(TestObjects.CLIENT_SPAN.name()).build()).execute()).isEmpty();
        // now store the timestamped span
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").build()).execute()).isNotEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").spanName(TestObjects.CLIENT_SPAN.name()).build()).execute()).isNotEmpty();
    }

    @Test
    public void getTraces_annotation() throws IOException {
        accept(TestObjects.CLIENT_SPAN);
        // fetch by time based annotation, find trace
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery(TestObjects.CLIENT_SPAN.annotations().get(0).value()).build()).execute()).isNotEmpty();
        // should find traces by a tag
        Map.Entry<String, String> tag = TestObjects.CLIENT_SPAN.tags().entrySet().iterator().next();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery((((tag.getKey()) + "=") + (tag.getValue()))).build()).execute()).isNotEmpty();
    }

    @Test
    public void getTraces_multipleAnnotationsBecomeAndFilter() throws IOException {
        Span foo = Span.newBuilder().traceId("1").name("call1").id(1).timestamp((((TestObjects.TODAY) + 1) * 1000L)).localEndpoint(TestObjects.FRONTEND).addAnnotation((((TestObjects.TODAY) + 1) * 1000L), "foo").build();
        // would be foo bar, except lexicographically bar precedes foo
        Span barAndFoo = Span.newBuilder().traceId("2").name("call2").id(2).timestamp((((TestObjects.TODAY) + 2) * 1000L)).localEndpoint(TestObjects.FRONTEND).addAnnotation((((TestObjects.TODAY) + 2) * 1000L), "bar").addAnnotation((((TestObjects.TODAY) + 2) * 1000L), "foo").build();
        Span fooAndBazAndQux = Span.newBuilder().traceId("3").name("call3").id(3).timestamp((((TestObjects.TODAY) + 3) * 1000L)).localEndpoint(TestObjects.FRONTEND).addAnnotation((((TestObjects.TODAY) + 3) * 1000L), "foo").putTag("baz", "qux").build();
        Span barAndFooAndBazAndQux = Span.newBuilder().traceId("4").name("call4").id(4).timestamp((((TestObjects.TODAY) + 4) * 1000L)).localEndpoint(TestObjects.FRONTEND).addAnnotation((((TestObjects.TODAY) + 4) * 1000L), "bar").addAnnotation((((TestObjects.TODAY) + 4) * 1000L), "foo").putTag("baz", "qux").build();
        accept(foo, barAndFoo, fooAndBazAndQux, barAndFooAndBazAndQux);
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery("foo").build()).execute())).containsExactly(Arrays.asList(foo), Arrays.asList(barAndFoo), Arrays.asList(fooAndBazAndQux), Arrays.asList(barAndFooAndBazAndQux));
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery("foo and bar").build()).execute())).containsExactly(Arrays.asList(barAndFoo), Arrays.asList(barAndFooAndBazAndQux));
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery("foo and bar and baz=qux").build()).execute())).containsExactly(Arrays.asList(barAndFooAndBazAndQux));
        // ensure we can search only by tag key
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery("baz").build()).execute())).containsExactly(Arrays.asList(fooAndBazAndQux), Arrays.asList(barAndFooAndBazAndQux));
    }

    /**
     * This test makes sure that annotation queries pay attention to which host recorded data
     */
    @Test
    public void getTraces_differentiateOnServiceName() throws IOException {
        Span trace1 = Span.newBuilder().traceId("1").name("1").id(1).kind(CLIENT).timestamp((((TestObjects.TODAY) + 1) * 1000L)).duration(3000L).localEndpoint(TestObjects.FRONTEND).addAnnotation(((((TestObjects.TODAY) + 1) * 1000L) + 500), "web").putTag("local", "web").putTag("web-b", "web").build();
        Span trace1Server = Span.newBuilder().traceId("1").name("1").id(1).kind(SERVER).shared(true).localEndpoint(TestObjects.BACKEND).timestamp((((TestObjects.TODAY) + 2) * 1000L)).duration(1000L).build();
        Span trace2 = Span.newBuilder().traceId("2").name("2").id(2).timestamp((((TestObjects.TODAY) + 11) * 1000L)).duration(3000L).kind(CLIENT).localEndpoint(TestObjects.BACKEND).addAnnotation(((((TestObjects.TODAY) + 11) * 1000) + 500), "app").putTag("local", "app").putTag("app-b", "app").build();
        Span trace2Server = Span.newBuilder().traceId("2").name("2").id(2).shared(true).kind(SERVER).localEndpoint(TestObjects.FRONTEND).timestamp((((TestObjects.TODAY) + 12) * 1000L)).duration(1000L).build();
        accept(trace1, trace1Server, trace2, trace2Server);
        // Sanity check
        assertThat(store().getTrace(trace1.traceId()).execute()).containsExactlyInAnyOrder(trace1, trace1Server);
        assertThat(ITSpanStore.sortTrace(store().getTrace(trace2.traceId()).execute())).containsExactly(trace2, trace2Server);
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().build()).execute())).containsExactly(Arrays.asList(trace1, trace1Server), Arrays.asList(trace2, trace2Server));
        // We only return traces where the service specified caused the data queried.
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery("web").build()).execute())).containsExactly(Arrays.asList(trace1, trace1Server));
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("backend").parseAnnotationQuery("web").build()).execute()).isEmpty();
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().serviceName("backend").parseAnnotationQuery("app").build()).execute())).containsExactly(Arrays.asList(trace2, trace2Server));
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery("app").build()).execute()).isEmpty();
        // tags are returned on annotation queries
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery("web-b").build()).execute())).containsExactly(Arrays.asList(trace1, trace1Server));
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("backend").parseAnnotationQuery("web-b").build()).execute()).isEmpty();
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().serviceName("backend").parseAnnotationQuery("app-b").build()).execute())).containsExactly(Arrays.asList(trace2, trace2Server));
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery("app-b").build()).execute()).isEmpty();
        // We only return traces where the service specified caused the tag queried.
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery("local=web").build()).execute())).containsExactly(Arrays.asList(trace1, trace1Server));
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("backend").parseAnnotationQuery("local=web").build()).execute()).isEmpty();
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().serviceName("backend").parseAnnotationQuery("local=app").build()).execute())).containsExactly(Arrays.asList(trace2, trace2Server));
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery("local=app").build()).execute()).isEmpty();
    }

    /**
     * Make sure empty binary annotation values don't crash
     */
    @Test
    public void getTraces_tagWithEmptyValue() throws IOException {
        Span span = Span.newBuilder().traceId("1").name("call1").id(1).timestamp((((TestObjects.TODAY) + 1) * 1000)).localEndpoint(TestObjects.FRONTEND).putTag("empty", "").build();
        accept(span);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").build()).execute()).containsExactly(Arrays.asList(span));
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").parseAnnotationQuery("empty").build()).execute()).containsExactly(Arrays.asList(span));
        assertThat(store().getTrace(span.traceId()).execute()).containsExactly(span);
    }

    /**
     * limit should apply to traces closest to endTs
     */
    @Test
    public void getTraces_limit() throws IOException {
        Span span1 = Span.newBuilder().traceId("a").id("1").timestamp((((TestObjects.TODAY) + 1) * 1000L)).localEndpoint(TestObjects.FRONTEND).build();
        Span span2 = span1.toBuilder().traceId("b").timestamp((((TestObjects.TODAY) + 2) * 1000L)).build();
        accept(span1, span2);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").limit(1).build()).execute()).extracting(( t) -> t.get(0).id()).containsExactly(span2.id());
    }

    /**
     * Traces whose root span has timestamps between (endTs - lookback) and endTs are returned
     */
    @Test
    public void getTraces_endTsAndLookback() throws IOException {
        Span span1 = Span.newBuilder().traceId("a").id("1").timestamp((((TestObjects.TODAY) + 1) * 1000L)).localEndpoint(TestObjects.FRONTEND).build();
        Span span2 = span1.toBuilder().traceId("b").timestamp((((TestObjects.TODAY) + 2) * 1000L)).build();
        accept(span1, span2);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().endTs(TestObjects.TODAY).build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().endTs(((TestObjects.TODAY) + 1)).build()).execute()).extracting(( t) -> t.get(0).id()).containsExactly(span1.id());
        assertThat(store().getTraces(ITSpanStore.requestBuilder().endTs(((TestObjects.TODAY) + 2)).build()).execute()).extracting(( t) -> t.get(0).id()).containsExactlyInAnyOrder(span1.id(), span2.id());
        assertThat(store().getTraces(ITSpanStore.requestBuilder().endTs(((TestObjects.TODAY) + 3)).build()).execute()).extracting(( t) -> t.get(0).id()).containsExactlyInAnyOrder(span1.id(), span2.id());
        assertThat(store().getTraces(ITSpanStore.requestBuilder().endTs(TestObjects.TODAY).build()).execute()).isEmpty();
        assertThat(store().getTraces(ITSpanStore.requestBuilder().endTs(((TestObjects.TODAY) + 1)).lookback(1).build()).execute()).extracting(( t) -> t.get(0).id()).containsExactly(span1.id());
        assertThat(store().getTraces(ITSpanStore.requestBuilder().endTs(((TestObjects.TODAY) + 2)).lookback(1).build()).execute()).extracting(( t) -> t.get(0).id()).containsExactlyInAnyOrder(span1.id(), span2.id());
        assertThat(store().getTraces(ITSpanStore.requestBuilder().endTs(((TestObjects.TODAY) + 3)).lookback(1).build()).execute()).extracting(( t) -> t.get(0).id()).containsExactlyInAnyOrder(span2.id());
    }

    // Bugs have happened in the past where trace limit was mistaken for span count.
    @Test
    public void traceWithManySpans() throws IOException {
        Span[] trace = new Span[101];
        trace[0] = Span.newBuilder().traceId("f66529c8cc356aa0").id("93288b4644570496").name("get").timestamp(((TestObjects.TODAY) * 1000)).duration((350 * 1000L)).kind(SERVER).localEndpoint(TestObjects.BACKEND).build();
        IntStream.range(1, trace.length).forEach(( i) -> trace[i] = Span.newBuilder().traceId(trace[0].traceId()).parentId(trace[0].id()).id(i).name("foo").timestamp((((TODAY) + i) * 1000)).duration(10L).localEndpoint(BACKEND).build());
        accept(trace);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().build()).execute()).flatExtracting(( t) -> t).containsExactlyInAnyOrder(trace);
        assertThat(store().getTrace(trace[0].traceId()).execute()).containsExactlyInAnyOrder(trace);
    }

    @Test
    public void getServiceNames_includesLocalServiceName() throws Exception {
        assertThat(store().getServiceNames().execute()).isEmpty();
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getServiceNames().execute()).contains("frontend");
    }

    @Test
    public void getServiceNames_includesRemoteServiceName() throws Exception {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getServiceNames().execute()).contains(TestObjects.CLIENT_SPAN.remoteServiceName());
    }

    /**
     * Our storage services aren't 100% consistent on this. we should reconsider at some point
     */
    @Test
    public void getSpanNames_mapsNameToRemoteServiceName() throws Exception {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getSpanNames(TestObjects.CLIENT_SPAN.remoteServiceName()).execute()).contains(TestObjects.CLIENT_SPAN.name());
    }

    @Test
    public void getSpanNames() throws Exception {
        assertThat(store().getSpanNames("frontend").execute()).isEmpty();
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getSpanNames(("frontend" + 1)).execute()).isEmpty();
        assertThat(store().getSpanNames("frontend").execute()).contains(TestObjects.CLIENT_SPAN.name());
    }

    @Test
    public void getSpanNames_allReturned() throws IOException {
        // Assure a default spanstore limit isn't hit by assuming if 50 are returned, all are returned
        List<String> spanNames = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            String suffix = (i < 10) ? "0" + i : String.valueOf(i);
            accept(TestObjects.CLIENT_SPAN.toBuilder().id((i + 1)).name(("yak" + suffix)).build());
            spanNames.add(("yak" + suffix));
        }
        assertThat(store().getSpanNames("frontend").execute()).containsExactlyInAnyOrderElementsOf(spanNames);
    }

    @Test
    public void getAllServiceNames_noServiceName() throws IOException {
        accept(Span.newBuilder().traceId("a").id("a").build());
        assertThat(store().getServiceNames().execute()).isEmpty();
    }

    @Test
    public void getSpanNames_noSpanName() throws IOException {
        accept(Span.newBuilder().traceId("a").id("a").localEndpoint(TestObjects.FRONTEND).build());
        assertThat(store().getSpanNames("frontend").execute()).isEmpty();
    }

    @Test
    public void spanNamesGoLowercase() throws IOException {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("frontend").spanName("GeT").build()).execute()).hasSize(1);
    }

    @Test
    public void serviceNamesGoLowercase() throws IOException {
        accept(TestObjects.CLIENT_SPAN);
        assertThat(store().getSpanNames("FrOnTeNd").execute()).containsExactly("get");
        assertThat(store().getTraces(ITSpanStore.requestBuilder().serviceName("FrOnTeNd").build()).execute()).hasSize(1);
    }

    /**
     * Ensure complete traces are aggregated, even if they complete after endTs
     */
    @Test
    public void getTraces_endTsInsideTheTrace() throws IOException {
        accept(TestObjects.TRACE);
        assertThat(ITSpanStore.sortTraces(store().getTraces(ITSpanStore.requestBuilder().endTs(((TestObjects.TRACE_STARTTS) + 100)).lookback(200).build()).execute())).containsOnly(TestObjects.TRACE);
    }
}

