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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import zipkin2.Span;
import zipkin2.TestObjects;


public class StrictTraceIdTest {
    @Test
    public void filterTraces_skipsOnNoClash() {
        Span oneOne = Span.newBuilder().traceId(1, 1).id(1).build();
        Span oneTwo = Span.newBuilder().traceId(1, 2).id(1).build();
        List<List<Span>> traces = Arrays.asList(Arrays.asList(oneOne), Arrays.asList(oneTwo));
        assertThat(StrictTraceId.filterTraces(ITSpanStore.requestBuilder().spanName("11").build()).map(traces)).isSameAs(traces);
    }

    @Test
    public void filterTraces_onSpanName() {
        assertThat(StrictTraceId.filterTraces(ITSpanStore.requestBuilder().spanName("11").build()).map(traces())).flatExtracting(( l) -> l).isEmpty();
        assertThat(StrictTraceId.filterTraces(ITSpanStore.requestBuilder().spanName("1").build()).map(traces())).containsExactly(traces().get(0));
    }

    @Test
    public void filterTraces_onTag() {
        assertThat(StrictTraceId.filterTraces(ITSpanStore.requestBuilder().parseAnnotationQuery("foo=0").build()).map(traces())).flatExtracting(( l) -> l).isEmpty();
        assertThat(StrictTraceId.filterTraces(ITSpanStore.requestBuilder().parseAnnotationQuery("foo=1").build()).map(traces())).containsExactly(traces().get(0));
    }

    @Test
    public void filterSpans() {
        assertThat(StrictTraceId.filterSpans(TestObjects.CLIENT_SPAN.traceId()).map(TestObjects.TRACE)).isEqualTo(TestObjects.TRACE);
        ArrayList<Span> trace = new ArrayList(TestObjects.TRACE);
        trace.set(1, TestObjects.CLIENT_SPAN.toBuilder().traceId(TestObjects.CLIENT_SPAN.traceId().substring(16)).build());
        assertThat(StrictTraceId.filterSpans(TestObjects.CLIENT_SPAN.traceId()).map(trace)).doesNotContain(TestObjects.CLIENT_SPAN);
    }

    @Test
    public void hasClashOnLowerTraceId() {
        Span oneOne = Span.newBuilder().traceId(1, 1).id(1).build();
        Span twoOne = Span.newBuilder().traceId(2, 1).id(1).build();
        Span zeroOne = Span.newBuilder().traceId(0, 1).id(1).build();
        Span oneTwo = Span.newBuilder().traceId(1, 2).id(1).build();
        assertThat(StrictTraceId.hasClashOnLowerTraceId(Arrays.asList(Arrays.asList(oneOne), Arrays.asList(oneTwo)))).isFalse();
        assertThat(StrictTraceId.hasClashOnLowerTraceId(Arrays.asList(Arrays.asList(oneOne), Arrays.asList(twoOne)))).isTrue();
        assertThat(StrictTraceId.hasClashOnLowerTraceId(Arrays.asList(Arrays.asList(oneOne), Arrays.asList(zeroOne)))).isTrue();
    }
}

