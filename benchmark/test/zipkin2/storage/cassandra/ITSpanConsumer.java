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
package zipkin2.storage.cassandra;


import Span.Kind.CLIENT;
import Span.Kind.SERVER;
import TestObjects.CLIENT_SPAN;
import java.io.IOException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.junit.Test;
import zipkin2.Span;
import zipkin2.TestObjects;


abstract class ITSpanConsumer {
    private CassandraStorage storage;

    /**
     * {@link Span#duration} == 0 is likely to be a mistake, and coerces to null. It is not helpful to
     * index rows who have no duration.
     */
    @Test
    public void doesntIndexSpansMissingDuration() throws IOException {
        Span span = Span.newBuilder().traceId("1").id("1").name("get").duration(0L).build();
        accept(storage.spanConsumer(), span);
        assertThat(ITSpanConsumer.rowCountForTraceByServiceSpan(storage)).isZero();
    }

    /**
     * Simulates a trace with a step pattern, where each span starts a millisecond after the prior
     * one. The consumer code optimizes index inserts to only represent the interval represented by
     * the trace as opposed to each individual timestamp.
     */
    @Test
    public void skipsRedundantIndexingInATrace() throws IOException {
        Span[] trace = new Span[101];
        trace[0] = CLIENT_SPAN.toBuilder().kind(SERVER).build();
        IntStream.range(0, 100).forEach(( i) -> trace[(i + 1)] = // all peer span timestamps happen a millisecond later
        Span.newBuilder().traceId(trace[0].traceId()).parentId(trace[0].id()).id(Long.toHexString(i)).name("get").kind(CLIENT).localEndpoint(TestObjects.FRONTEND).timestamp(((trace[0].timestamp()) + (i * 1000))).duration(10L).build());
        accept(storage.spanConsumer(), trace);
        assertThat(ITSpanConsumer.rowCountForTraceByServiceSpan(storage)).isGreaterThanOrEqualTo(4L);
        assertThat(ITSpanConsumer.rowCountForTraceByServiceSpan(storage)).isGreaterThanOrEqualTo(4L);
        // sanity check base case
        accept(storage.toBuilder().strictTraceId(false).build().spanConsumer(), trace);
        assertThat(ITSpanConsumer.rowCountForTraceByServiceSpan(storage)).isGreaterThanOrEqualTo(120L);// TODO: magic number

        assertThat(ITSpanConsumer.rowCountForTraceByServiceSpan(storage)).isGreaterThanOrEqualTo(120L);
    }

    @Test
    public void insertTags_SelectTags_CalculateCount() throws IOException {
        Span[] trace = new Span[101];
        trace[0] = CLIENT_SPAN.toBuilder().kind(SERVER).build();
        IntStream.range(0, 100).forEach(( i) -> trace[(i + 1)] = // all peer span timestamps happen a millisecond later
        Span.newBuilder().traceId(trace[0].traceId()).parentId(trace[0].id()).id(Long.toHexString(i)).name("get").kind(CLIENT).localEndpoint(TestObjects.FRONTEND).putTag("environment", "dev").putTag("a", "b").timestamp(((trace[0].timestamp()) + (i * 1000))).duration(10L).build());
        accept(storage.spanConsumer(), trace);
        assertThat(ITSpanConsumer.rowCountForTags(storage)).isEqualTo(1L);// Since tag {a,b} are not in the whitelist

        assertThat(ITSpanConsumer.getTagValue(storage, "environment")).isEqualTo("dev");
    }
}

