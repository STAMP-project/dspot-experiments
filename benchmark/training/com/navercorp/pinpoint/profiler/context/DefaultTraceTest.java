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
package com.navercorp.pinpoint.profiler.context;


import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.profiler.context.id.Shared;
import com.navercorp.pinpoint.profiler.context.id.TraceRoot;
import com.navercorp.pinpoint.profiler.metadata.SqlMetaDataService;
import com.navercorp.pinpoint.profiler.metadata.StringMetaDataService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author emeroad
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultTraceTest {
    private final String agentId = "agentId";

    private final long agentStartTime = System.currentTimeMillis();

    @Mock
    private TraceRoot traceRoot;

    @Mock
    private Shared shared;

    @Mock
    private StringMetaDataService stringMetaDataService;

    @Mock
    private SqlMetaDataService sqlMetaDataService;

    @Mock
    private AsyncContextFactory asyncContextFactory;

    @Test
    public void testPushPop() {
        Trace trace = newTrace();
        trace.traceBlockBegin();
        trace.traceBlockBegin();
        trace.traceBlockEnd();
        trace.traceBlockEnd();
        trace.close();
    }

    @Test
    public void testPreviousSpanEvent() {
        Trace trace = newTrace();
        SpanEventRecorder recorder1 = trace.traceBlockBegin();
        recorder1.attachFrameObject("1");
        SpanEventRecorder recorder2 = trace.traceBlockBegin();
        recorder2.attachFrameObject("2");
        trace.traceBlockEnd();
        // access the previous SpanEvent
        Assert.assertEquals(recorder1.getFrameObject(), "1");
        trace.traceBlockEnd();
        trace.close();
    }

    @Test
    public void overflow() {
        Trace trace = newTrace(2);
        SpanEventRecorder recorder1 = trace.traceBlockBegin();
        SpanEventRecorder recorder2 = trace.traceBlockBegin();
        SpanEventRecorder recorder3 = trace.traceBlockBegin();
        // overflow
        SpanEventRecorder recorder4 = trace.traceBlockBegin();
        trace.traceBlockEnd();
        trace.traceBlockEnd();
        trace.traceBlockEnd();
        trace.traceBlockEnd();
        trace.close();
    }

    @Test
    public void overflowUnlimit() {
        Trace trace = newTrace((-1));
        for (int i = 0; i < 256; i++) {
            trace.traceBlockBegin();
        }
        for (int i = 0; i < 256; i++) {
            trace.traceBlockEnd();
        }
    }

    @Test
    public void close() {
        Trace trace = newTrace();
        trace.close();
        // Already closed
        SpanEventRecorder recorder1 = trace.traceBlockBegin();
        trace.traceBlockEnd();
    }

    @Test
    public void notEmpty() {
        Trace trace = newTrace();
        SpanEventRecorder recorder1 = trace.traceBlockBegin();
        trace.close();
    }
}

