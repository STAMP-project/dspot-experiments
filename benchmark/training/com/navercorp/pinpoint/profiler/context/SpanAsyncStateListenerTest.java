/**
 * Copyright 2017 NAVER Corp.
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


import ListenableAsyncState.AsyncStateListener;
import com.navercorp.pinpoint.profiler.context.id.ListenableAsyncState;
import com.navercorp.pinpoint.profiler.context.id.TraceRoot;
import com.navercorp.pinpoint.profiler.context.storage.Storage;
import com.navercorp.pinpoint.profiler.context.storage.StorageFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalMatchers;
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
public class SpanAsyncStateListenerTest {
    @Mock
    private Span span;

    @Mock
    private TraceRoot traceRoot;

    @Mock
    private StorageFactory storageFactory;

    @Mock
    private Storage storage;

    @Test
    public void onComplete() throws Exception {
        Mockito.when(span.getTraceRoot()).thenReturn(traceRoot);
        SpanChunkFactory spanChunkFactory = AdditionalMatchers.or(((SpanChunkFactory) (ArgumentMatchers.isNull())), ((SpanChunkFactory) (ArgumentMatchers.any())));
        Mockito.when(storageFactory.createStorage(spanChunkFactory)).thenReturn(storage);
        ListenableAsyncState.AsyncStateListener listener = new SpanAsyncStateListener(span, storageFactory);
        listener.finish();
        Mockito.verify(span).isTimeRecording();
        Mockito.verify(storage).store(span);
        // 
        listener.finish();
        Mockito.verify(span).isTimeRecording();
        Mockito.verify(storage).store(span);
    }

    @Test
    public void onComplete_check_atomicity() throws Exception {
        Mockito.when(span.getTraceRoot()).thenReturn(traceRoot);
        SpanChunkFactory spanChunkFactory = AdditionalMatchers.or(((SpanChunkFactory) (ArgumentMatchers.isNull())), ((SpanChunkFactory) (ArgumentMatchers.any())));
        Mockito.when(storageFactory.createStorage(spanChunkFactory)).thenReturn(storage);
        ListenableAsyncState.AsyncStateListener listener = new SpanAsyncStateListener(span, storageFactory);
        listener.finish();
        listener.finish();
        Mockito.verify(span).isTimeRecording();
        Mockito.verify(storage).store(span);
    }
}

