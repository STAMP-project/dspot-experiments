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
package com.navercorp.pinpoint.profiler.context.recorder;


import com.navercorp.pinpoint.profiler.context.AsyncContextFactory;
import com.navercorp.pinpoint.profiler.context.SpanEvent;
import com.navercorp.pinpoint.profiler.context.id.Shared;
import com.navercorp.pinpoint.profiler.context.id.TraceRoot;
import com.navercorp.pinpoint.profiler.metadata.SqlMetaDataService;
import com.navercorp.pinpoint.profiler.metadata.StringMetaDataService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
@RunWith(MockitoJUnitRunner.class)
public class WrappedSpanEventRecorderTest {
    @Mock
    private TraceRoot traceRoot;

    @Mock
    private Shared shared;

    @Mock
    private AsyncContextFactory asyncContextFactory;

    @Mock
    private StringMetaDataService stringMetaDataService;

    @Mock
    private SqlMetaDataService sqlMetaDataService;

    @Test
    public void testSetExceptionInfo_RootMarkError() throws Exception {
        Mockito.when(traceRoot.getShared()).thenReturn(shared);
        SpanEvent spanEvent = new SpanEvent();
        WrappedSpanEventRecorder recorder = new WrappedSpanEventRecorder(traceRoot, asyncContextFactory, stringMetaDataService, sqlMetaDataService);
        recorder.setWrapped(spanEvent);
        final String exceptionMessage1 = "exceptionMessage1";
        final Exception exception1 = new Exception(exceptionMessage1);
        recorder.recordException(false, exception1);
        Assert.assertEquals("Exception recoding", exceptionMessage1, spanEvent.getExceptionInfo().getStringValue());
        Mockito.verify(shared, Mockito.never()).maskErrorCode(ArgumentMatchers.anyInt());
        final String exceptionMessage2 = "exceptionMessage2";
        final Exception exception2 = new Exception(exceptionMessage2);
        recorder.recordException(true, exception2);
        Assert.assertEquals("Exception recoding", exceptionMessage2, spanEvent.getExceptionInfo().getStringValue());
        Mockito.verify(shared, Mockito.only()).maskErrorCode(1);
    }

    @Test
    public void testRecordAPIId() throws Exception {
        SpanEvent spanEvent = new SpanEvent();
        WrappedSpanEventRecorder recorder = new WrappedSpanEventRecorder(traceRoot, asyncContextFactory, stringMetaDataService, sqlMetaDataService);
        recorder.setWrapped(spanEvent);
        final int API_ID = 1000;
        recorder.recordApiId(API_ID);
        Assert.assertEquals("API ID", spanEvent.getApiId(), API_ID);
    }
}

