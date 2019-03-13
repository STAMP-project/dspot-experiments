/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.test;


import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.profiler.context.DefaultLocalAsyncId;
import com.navercorp.pinpoint.profiler.context.Span;
import com.navercorp.pinpoint.profiler.context.SpanChunk;
import com.navercorp.pinpoint.profiler.context.id.DefaultTraceId;
import com.navercorp.pinpoint.profiler.context.id.TraceRoot;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class OrderedSpanRecorderTest {
    private static final int UNSET_ASYNC_ID = -1;

    private static final short UNSET_ASYNC_SEQUENCE = -1;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OrderedSpanRecorder recorder = new OrderedSpanRecorder();

    private final String agentId = "agentId";

    @Test
    public void testOrderingWithSameEventTime() {
        // given
        final long startTime = 100;
        final long spanId = 1L;
        TraceId traceId = new DefaultTraceId(agentId, startTime, 0, (-1L), spanId, ((short) (0)));
        final TraceRoot traceRoot = new com.navercorp.pinpoint.profiler.context.id.DefaultTraceRoot(traceId, agentId, startTime, 0);
        Span span = createSpan(traceRoot, startTime);
        SpanChunk event = wrapSpanChunk(traceRoot, createSpanEvent(traceRoot, 0, ((short) (0))));
        SpanChunk event1 = wrapSpanChunk(traceRoot, createSpanEvent(traceRoot, 0, ((short) (1))));
        SpanChunk event2 = wrapSpanChunk(traceRoot, createSpanEvent(traceRoot, 0, ((short) (2))));
        SpanChunk asyncEvent1_1 = wrapSpanChunk(traceRoot, createAsyncSpanEvent(traceRoot, 0, ((short) (0))), new DefaultLocalAsyncId(1, ((short) (1))));
        SpanChunk asyncEvent1_2 = wrapSpanChunk(traceRoot, createAsyncSpanEvent(traceRoot, 0, ((short) (1))), new DefaultLocalAsyncId(1, ((short) (1))));
        SpanChunk asyncEvent2 = wrapSpanChunk(traceRoot, createAsyncSpanEvent(traceRoot, 0, ((short) (0))), new DefaultLocalAsyncId(2, ((short) (1))));
        @SuppressWarnings("unchecked")
        final List<?> expectedOrder = Arrays.asList(span, event, event1, event2, asyncEvent1_1, asyncEvent1_2, asyncEvent2);
        // when
        @SuppressWarnings("unchecked")
        final List<?> listToBeHandled = Arrays.asList(span, event, event1, event2, asyncEvent1_1, asyncEvent1_2, asyncEvent2);
        Collections.shuffle(listToBeHandled);
        for (Object base : listToBeHandled) {
            this.recorder.handleSend(base);
        }
        // then
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.recorder.print(new PrintStream(baos));
        this.logger.debug(baos.toString());
        for (Object expectedBase : expectedOrder) {
            Object actualBase = this.recorder.pop();
            Assert.assertSame(expectedBase, actualBase);
        }
        Assert.assertNull(this.recorder.pop());
    }

    @Test
    public void testMultipleAsyncSpanEvents() {
        // given
        final long startTime1 = 100;
        final long spanId = 1L;
        TraceId traceId1 = new DefaultTraceId(agentId, startTime1, 0, (-1L), spanId, ((short) (0)));
        final TraceRoot traceRoot1 = new com.navercorp.pinpoint.profiler.context.id.DefaultTraceRoot(traceId1, agentId, startTime1, 0);
        final long startTime2 = startTime1 + 10L;
        final long spanId2 = 2L;
        final TraceId traceId2 = new DefaultTraceId(agentId, startTime2, 0, (-1L), spanId2, ((short) (0)));
        final TraceRoot traceRoot2 = new com.navercorp.pinpoint.profiler.context.id.DefaultTraceRoot(traceId2, agentId, startTime2, 0);
        Span span = createSpan(traceRoot1, startTime1);
        SpanChunk event1 = wrapSpanChunk(traceRoot1, createSpanEvent(traceRoot1, 0, ((short) (0))));
        SpanChunk asyncEvent1_1_1 = wrapSpanChunk(traceRoot1, createAsyncSpanEvent(traceRoot1, 0, ((short) (0))), new DefaultLocalAsyncId(1, ((short) (1))));
        SpanChunk asyncEvent1_1_2 = wrapSpanChunk(traceRoot1, createAsyncSpanEvent(traceRoot1, 0, ((short) (1))), new DefaultLocalAsyncId(1, ((short) (1))));
        SpanChunk asyncEvent1_2_1 = wrapSpanChunk(traceRoot1, createAsyncSpanEvent(traceRoot1, 0, ((short) (0))), new DefaultLocalAsyncId(1, ((short) (2))));
        SpanChunk event2 = wrapSpanChunk(traceRoot2, createSpanEvent(traceRoot2, 0, ((short) (1))));
        SpanChunk asyncEvent2_1 = wrapSpanChunk(traceRoot2, createAsyncSpanEvent(traceRoot2, 0, ((short) (0))), new DefaultLocalAsyncId(2, ((short) (1))));
        SpanChunk asyncEvent2_2 = wrapSpanChunk(traceRoot2, createAsyncSpanEvent(traceRoot2, 0, ((short) (0))), new DefaultLocalAsyncId(2, ((short) (2))));
        @SuppressWarnings("unchecked")
        final List<?> expectedOrder = Arrays.asList(span, event1, event2, asyncEvent1_1_1, asyncEvent1_1_2, asyncEvent1_2_1, asyncEvent2_1, asyncEvent2_2);
        // when
        @SuppressWarnings("unchecked")
        final List<?> listToBeHandled = Arrays.asList(span, event1, asyncEvent1_1_1, asyncEvent1_1_2, asyncEvent1_2_1, event2, asyncEvent2_1, asyncEvent2_2);
        Collections.shuffle(listToBeHandled);
        for (Object base : listToBeHandled) {
            this.recorder.handleSend(base);
        }
        // then
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.recorder.print(new PrintStream(baos));
        this.logger.debug(baos.toString());
        for (Object expectedBase : expectedOrder) {
            Object actualBase = this.recorder.pop();
            Assert.assertSame(expectedBase, actualBase);
        }
        Assert.assertNull(this.recorder.pop());
    }

    @Test
    public void testMultipleSpanOrdering() {
        // given
        final long startTime1 = 100;
        final long spanId1 = 1L;
        final TraceId traceId1 = new DefaultTraceId(agentId, startTime1, 0, (-1L), spanId1, ((short) (0)));
        final TraceRoot traceRoot1 = new com.navercorp.pinpoint.profiler.context.id.DefaultTraceRoot(traceId1, agentId, startTime1, 0);
        final long startTime2 = startTime1 + 10L;
        final long spanId2 = 2L;
        final TraceId traceId2 = new DefaultTraceId(agentId, startTime2, 0, (-1L), spanId2, ((short) (0)));
        final TraceRoot traceRoot2 = new com.navercorp.pinpoint.profiler.context.id.DefaultTraceRoot(traceId2, agentId, startTime2, 0);
        Span span1 = createSpan(traceRoot1, startTime1);
        SpanChunk event1_0 = wrapSpanChunk(traceRoot1, createSpanEvent(traceRoot1, 1, ((short) (0))));
        SpanChunk event1_1 = wrapSpanChunk(traceRoot1, createSpanEvent(traceRoot1, 2, ((short) (1))));
        SpanChunk asyncEvent1_0 = wrapSpanChunk(traceRoot1, createAsyncSpanEvent(traceRoot1, 1, ((short) (0))), new DefaultLocalAsyncId(1, ((short) (1))));
        SpanChunk asyncEvent1_1 = wrapSpanChunk(traceRoot1, createAsyncSpanEvent(traceRoot1, 2, ((short) (1))), new DefaultLocalAsyncId(1, ((short) (1))));
        Span span2 = createSpan(traceRoot2, startTime2);
        SpanChunk event2_0 = wrapSpanChunk(traceRoot2, createSpanEvent(traceRoot2, 0, ((short) (0))));
        SpanChunk event2_1 = wrapSpanChunk(traceRoot2, createSpanEvent(traceRoot2, 1, ((short) (1))));
        SpanChunk asyncEvent2_0 = wrapSpanChunk(traceRoot2, createAsyncSpanEvent(traceRoot2, 0, ((short) (0))), new DefaultLocalAsyncId(2, ((short) (1))));
        @SuppressWarnings("unchecked")
        final List<?> expectedOrder = Arrays.asList(span1, event1_0, event1_1, span2, event2_0, event2_1, asyncEvent1_0, asyncEvent1_1, asyncEvent2_0);
        // when
        @SuppressWarnings("unchecked")
        final List<?> listToBeHandled = Arrays.asList(span1, event1_0, event1_1, span2, event2_0, event2_1, asyncEvent1_0, asyncEvent1_1, asyncEvent2_0);
        Collections.shuffle(listToBeHandled);
        for (Object base : listToBeHandled) {
            this.recorder.handleSend(base);
        }
        // then
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.recorder.print(new PrintStream(baos));
        this.logger.debug(baos.toString());
        for (Object expectedBase : expectedOrder) {
            Object actualBase = this.recorder.pop();
            Assert.assertSame(expectedBase, actualBase);
        }
        Assert.assertNull(this.recorder.pop());
    }
}

