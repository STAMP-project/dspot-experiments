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
package zipkin2.storage.cassandra.v1;


import Tables.TABLE_AUTOCOMPLETE_TAGS;
import TestObjects.CLIENT_SPAN;
import java.io.IOException;
import org.junit.Test;
import zipkin2.Span;
import zipkin2.TestObjects;


abstract class ITSpanConsumer {
    private CassandraStorage storage;

    /**
     * Core/Boundary annotations like "sr" aren't queryable, and don't add value to users. Address
     * annotations, like "sa", don't have string values, so are similarly not queryable. Skipping
     * indexing of such annotations dramatically reduces the load on cassandra and size of indexes.
     */
    @Test
    public void doesntIndexCoreOrNonStringAnnotations() throws IOException {
        accept(storage.spanConsumer(), CLIENT_SPAN);
        assertThat(storage.session().execute("SELECT blobastext(annotation) from annotations_index").all()).extracting(( r) -> r.getString(0)).containsExactlyInAnyOrder("frontend:http.path", "frontend:http.path:/api", "frontend:clnt/finagle.version:6.45.0", "frontend:foo", "frontend:clnt/finagle.version");
    }

    /**
     * Simulates a trace with a step pattern, where each span starts a millisecond after the prior
     * one. The consumer code optimizes index inserts to only represent the interval represented by
     * the trace as opposed to each individual timestamp.
     */
    @Test
    public void skipsRedundantIndexingInATrace() throws IOException {
        Span[] trace = new Span[101];
        trace[0] = TestObjects.CLIENT_SPAN;
        for (int i = 0; i < 100; i++) {
            trace[(i + 1)] = // child span timestamps happen 1 ms later
            Span.newBuilder().traceId(trace[0].traceId()).parentId(trace[0].id()).id((i + 1)).name(String.valueOf((i + 1))).timestamp(((trace[0].timestamp()) + (i * 1000))).addAnnotation(((trace[0].annotations().get(0).timestamp()) + (i * 1000)), "bar").build();
        }
        accept(storage.spanConsumer(), trace);
        assertThat(rowCount("annotations_index")).isEqualTo(5L);
        assertThat(rowCount("service_span_name_index")).isEqualTo(2L);
        assertThat(rowCount("service_name_index")).isEqualTo(2L);
        // redundant store doesn't change the indexes
        accept(storage.spanConsumer(), trace);
        assertThat(rowCount("annotations_index")).isEqualTo(5L);
        assertThat(rowCount("service_span_name_index")).isEqualTo(2L);
        assertThat(rowCount("service_name_index")).isEqualTo(2L);
    }

    @Test
    public void insertTags_SelectTags_CalculateCount() throws IOException {
        Span[] trace = new Span[2];
        trace[0] = TestObjects.CLIENT_SPAN;
        trace[1] = // child span timestamps happen 1 ms later
        Span.newBuilder().traceId(trace[0].traceId()).parentId(trace[0].id()).id(1).name("1").putTag("environment", "dev").putTag("a", "b").timestamp(((trace[0].timestamp()) * 1000)).addAnnotation(((trace[0].annotations().get(0).timestamp()) + 1000), "bar").build();
        accept(storage.spanConsumer(), trace);
        assertThat(rowCount(TABLE_AUTOCOMPLETE_TAGS)).isGreaterThanOrEqualTo(1L);
        assertThat(ITSpanConsumer.getTagValue(storage, "environment")).isEqualTo("dev");
    }
}

