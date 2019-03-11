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
package zipkin2.internal;


import Kind.CLIENT;
import Kind.SERVER;
import Trace.CLEANUP_COMPARATOR;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import zipkin2.Endpoint;
import zipkin2.Span;


public class TraceTest {
    /**
     * Some don't propagate the server's parent ID which creates a race condition. Try to unwind it.
     *
     * <p>See https://github.com/openzipkin/zipkin/pull/1745
     */
    @Test
    public void backfillsMissingParentIdOnSharedSpan() {
        List<Span> trace = // below the parent ID is null as it wasn't propagated
        Arrays.asList(TraceTest.span("a", null, "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, "frontend", null, false), TraceTest.span("a", null, "b", SERVER, "backend", null, true));
        assertThat(Trace.merge(trace)).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(TraceTest.span("a", null, "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, "frontend", null, false), TraceTest.span("a", "a", "b", SERVER, "backend", null, true));
    }

    @Test
    public void backfillsMissingSharedFlag() {
        List<Span> trace = // below the shared flag was forgotten
        Arrays.asList(TraceTest.span("a", null, "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, "frontend", "1.2.3.4", false), TraceTest.span("a", "a", "b", SERVER, "backend", "5.6.7.8", false));
        assertThat(Trace.merge(trace)).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(TraceTest.span("a", null, "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, "frontend", "1.2.3.4", false), TraceTest.span("a", "a", "b", SERVER, "backend", "5.6.7.8", true));
    }

    /**
     * Some truncate an incoming trace ID to 64-bits.
     */
    @Test
    public void choosesBestTraceId() {
        List<Span> trace = Arrays.asList(TraceTest.span("7180c278b62e8f6a216a2aea45d08fc9", null, "a", SERVER, "frontend", null, false), TraceTest.span("7180c278b62e8f6a216a2aea45d08fc9", "a", "b", CLIENT, "frontend", null, false), TraceTest.span("216a2aea45d08fc9", "a", "b", SERVER, "backend", null, true));
        assertThat(Trace.merge(trace)).flatExtracting(Span::traceId).containsExactly("7180c278b62e8f6a216a2aea45d08fc9", "7180c278b62e8f6a216a2aea45d08fc9", "7180c278b62e8f6a216a2aea45d08fc9");
    }

    /**
     * Let's pretend people use crappy data, but only on the first hop.
     */
    @Test
    public void mergesWhenMissingEndpoints() {
        List<Span> trace = Arrays.asList(Span.newBuilder().traceId("a").id("a").putTag("service", "frontend").putTag("span.kind", "SERVER").build(), Span.newBuilder().traceId("a").parentId("a").id("b").putTag("service", "frontend").putTag("span.kind", "CLIENT").timestamp(1L).build(), TraceTest.span("a", "a", "b", SERVER, "backend", null, true), Span.newBuilder().traceId("a").parentId("a").id("b").duration(10L).build());
        assertThat(Trace.merge(trace)).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(Span.newBuilder().traceId("a").id("a").putTag("service", "frontend").putTag("span.kind", "SERVER").build(), Span.newBuilder().traceId("a").parentId("a").id("b").putTag("service", "frontend").putTag("span.kind", "CLIENT").timestamp(1L).duration(10L).build(), TraceTest.span("a", "a", "b", SERVER, "backend", null, true));
    }

    /**
     * If a client request is proxied by something that does transparent retried. It can be the case
     * that two servers share the same ID (accidentally!)
     */
    @Test
    public void doesntMergeSharedSpansOnDifferentIPs() {
        List<Span> trace = Arrays.asList(TraceTest.span("a", null, "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, "frontend", null, false).toBuilder().timestamp(1L).addAnnotation(3L, "brave.flush").build(), TraceTest.span("a", "a", "b", SERVER, "backend", "1.2.3.4", true), TraceTest.span("a", "a", "b", SERVER, "backend", "1.2.3.5", true), TraceTest.span("a", "a", "b", CLIENT, "frontend", null, false).toBuilder().duration(10L).build());
        assertThat(Trace.merge(trace)).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(TraceTest.span("a", null, "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, "frontend", null, false).toBuilder().timestamp(1L).duration(10L).addAnnotation(3L, "brave.flush").build(), TraceTest.span("a", "a", "b", SERVER, "backend", "1.2.3.4", true), TraceTest.span("a", "a", "b", SERVER, "backend", "1.2.3.5", true));
    }

    // Same as above, but the late reported data has no parent id or endpoint
    @Test
    public void putsRandomDataOnFirstSpanWithEndpoint() {
        List<Span> trace = Arrays.asList(TraceTest.span("a", null, "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, null, null, false), TraceTest.span("a", "a", "b", null, "frontend", null, false).toBuilder().timestamp(1L).addAnnotation(3L, "brave.flush").build(), TraceTest.span("a", "a", "b", SERVER, "backend", "1.2.3.4", true), TraceTest.span("a", "a", "b", SERVER, "backend", "1.2.3.5", true), TraceTest.span("a", "a", "b", null, null, null, false).toBuilder().duration(10L).build());
        assertThat(Trace.merge(trace)).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(TraceTest.span("a", null, "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, "frontend", null, false).toBuilder().timestamp(1L).duration(10L).addAnnotation(3L, "brave.flush").build(), TraceTest.span("a", "a", "b", SERVER, "backend", "1.2.3.4", true), TraceTest.span("a", "a", "b", SERVER, "backend", "1.2.3.5", true));
    }

    // not a good idea to send parts of a local endpoint separately, but this helps ensure data isn't
    // accidentally partitioned in a overly fine grain
    @Test
    public void mergesIncompleteEndpoints() {
        List<Span> trace = Arrays.asList(TraceTest.span("a", null, "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, null, "1.2.3.4", false), TraceTest.span("a", "a", "b", SERVER, null, "1.2.3.5", true), TraceTest.span("a", "a", "b", SERVER, "backend", null, true));
        assertThat(Trace.merge(trace)).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(TraceTest.span("a", null, "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, "frontend", "1.2.3.4", false), TraceTest.span("a", "a", "b", SERVER, "backend", "1.2.3.5", true));
    }

    @Test
    public void deletesSelfReferencingParentId() {
        List<Span> trace = Arrays.asList(TraceTest.span("a", "a", "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, "frontend", null, false));
        assertThat(Trace.merge(trace)).usingFieldByFieldElementComparator().containsExactlyInAnyOrder(TraceTest.span("a", null, "a", SERVER, "frontend", null, false), TraceTest.span("a", "a", "b", CLIENT, "frontend", null, false));
    }

    @Test
    public void worksWhenMissingParentSpan() {
        String missingParentId = "a";
        List<Span> trace = Arrays.asList(TraceTest.span("a", missingParentId, "b", SERVER, "backend", "1.2.3.4", false), TraceTest.span("a", missingParentId, "c", SERVER, "backend", null, false));
        assertThat(Trace.merge(trace)).containsExactlyElementsOf(trace);
    }

    // some instrumentation don't add shared flag to servers
    @Test
    public void cleanupComparator_ordersClientFirst() {
        List<Span> trace = Arrays.asList(TraceTest.span("a", "a", "b", SERVER, "backend", "1.2.3.5", false), TraceTest.span("a", "a", "b", CLIENT, "frontend", null, false));
        Collections.sort(trace, CLEANUP_COMPARATOR);
        assertThat(trace.get(0).kind()).isEqualTo(CLIENT);
    }

    /**
     * Comparators are meant to be transitive. This exploits edge cases to fool our comparator.
     */
    @Test
    public void cleanupComparator_transitiveKindComparison() {
        List<Span> trace = new ArrayList<>();
        Endpoint aEndpoint = Endpoint.newBuilder().serviceName("a").build();
        Endpoint bEndpoint = Endpoint.newBuilder().serviceName("b").build();
        Span template = Span.newBuilder().traceId("a").id("a").build();
        // If there is a transitive ordering problem, TimSort will throw an IllegalArgumentException
        // when there are at least 32 elements.
        for (int i = 0, length = 7; i < length; i++) {
            trace.add(template.toBuilder().shared(true).localEndpoint(bEndpoint).build());
            trace.add(template.toBuilder().kind(CLIENT).localEndpoint(bEndpoint).build());
            trace.add(template.toBuilder().localEndpoint(aEndpoint).build());
            trace.add(template);
            trace.add(template.toBuilder().kind(CLIENT).localEndpoint(aEndpoint).build());
        }
        Collections.sort(trace, CLEANUP_COMPARATOR);
        assertThat(new java.util.LinkedHashSet(trace)).extracting(Span::shared, Span::kind, ( s) -> s.localServiceName()).containsExactly(tuple(null, CLIENT, "a"), tuple(null, CLIENT, "b"), tuple(null, null, null), tuple(null, null, "a"), tuple(true, null, "b"));
    }
}

