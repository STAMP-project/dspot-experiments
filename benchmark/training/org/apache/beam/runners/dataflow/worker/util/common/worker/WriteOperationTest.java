/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.dataflow.worker.util.common.worker;


import InitializationState.ABORTED;
import InitializationState.FINISHED;
import Sink.SinkWriter;
import java.io.Closeable;
import java.io.IOException;
import org.apache.beam.runners.dataflow.worker.NameContextsForTests;
import org.apache.beam.runners.dataflow.worker.TestOperationContext;
import org.apache.beam.runners.dataflow.worker.counters.Counter.CounterUpdateExtractor;
import org.apache.beam.runners.dataflow.worker.counters.CounterName;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.apache.beam.runners.dataflow.worker.counters.NameContext;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 * Tests for WriteOperation.
 */
@RunWith(JUnit4.class)
public class WriteOperationTest {
    private final CounterSet counterSet = new CounterSet();

    private final OperationContext context = TestOperationContext.create(counterSet, NameContext.create("test", "WriteOperation", "WriteOperation", "WriteOperation"));

    @Test
    @SuppressWarnings("unchecked")
    public void testRunWriteOperation() throws Exception {
        ExecutorTestUtils.TestSink sink = new ExecutorTestUtils.TestSink();
        WriteOperation writeOperation = WriteOperation.forTest(sink, context);
        writeOperation.start();
        writeOperation.process("hi");
        writeOperation.process("there");
        writeOperation.process("");
        writeOperation.process("bob");
        writeOperation.finish();
        Assert.assertThat(sink.outputElems, CoreMatchers.hasItems("hi", "there", "", "bob"));
        CounterUpdateExtractor<?> updateExtractor = Mockito.mock(CounterUpdateExtractor.class);
        counterSet.extractUpdates(false, updateExtractor);
        Mockito.verify(updateExtractor).longSum(ArgumentMatchers.eq(CounterName.named("WriteOperation-ByteCount")), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(10L));
        Mockito.verifyNoMoreInteractions(updateExtractor);
    }

    @Test
    public void testStartAbort() throws Exception {
        Sink<Object> mockSink = Mockito.mock(Sink.class);
        Sink.SinkWriter<Object> mockWriter = Mockito.mock(SinkWriter.class);
        Mockito.when(mockSink.writer()).thenReturn(mockWriter);
        WriteOperation writeOperation = WriteOperation.forTest(mockSink, context);
        writeOperation.start();
        writeOperation.abort();
        Assert.assertThat(writeOperation.initializationState, Matchers.equalTo(ABORTED));
        Mockito.verify(mockWriter).abort();
    }

    @Test
    public void testStartFinishAbort() throws Exception {
        Sink<Object> mockSink = Mockito.mock(Sink.class);
        Sink.SinkWriter<Object> mockWriter = Mockito.mock(SinkWriter.class);
        Mockito.when(mockSink.writer()).thenReturn(mockWriter);
        WriteOperation writeOperation = WriteOperation.forTest(mockSink, context);
        writeOperation.start();
        writeOperation.finish();
        Assert.assertThat(writeOperation.initializationState, Matchers.equalTo(FINISHED));
        writeOperation.abort();
        Assert.assertThat(writeOperation.initializationState, Matchers.equalTo(ABORTED));
        Mockito.verify(mockWriter).close();// finish called close

        Mockito.verify(mockWriter, Mockito.never()).abort();// so abort is not called on the writer

    }

    @Test
    public void testStartFinishFailureAbort() throws Exception {
        Sink<Object> mockSink = Mockito.mock(Sink.class);
        Sink.SinkWriter<Object> mockWriter = Mockito.mock(SinkWriter.class);
        Mockito.when(mockSink.writer()).thenReturn(mockWriter);
        WriteOperation writeOperation = WriteOperation.forTest(mockSink, context);
        Mockito.doThrow(new IOException("Expected failure to close")).when(mockWriter).close();
        writeOperation.start();
        try {
            writeOperation.finish();
            Assert.fail("Expected exception from finish");
        } catch (Exception e) {
            // expected exception
        }
        Assert.assertThat(writeOperation.initializationState, Matchers.equalTo(FINISHED));
        writeOperation.abort();
        Assert.assertThat(writeOperation.initializationState, Matchers.equalTo(ABORTED));
        Mockito.verify(mockWriter).close();// finish called close

        Mockito.verify(mockWriter, Mockito.never()).abort();// so abort is not called on the writer

    }

    @Test
    public void testAbortWithoutStart() throws Exception {
        Sink<Object> mockSink = Mockito.mock(Sink.class);
        WriteOperation writeOperation = WriteOperation.forTest(mockSink, context);
        writeOperation.abort();
        Assert.assertThat(writeOperation.initializationState, Matchers.equalTo(ABORTED));
    }

    @Test
    public void testWriteOperationContext() throws Exception {
        OperationContext context = Mockito.mock(OperationContext.class);
        Sink sink = Mockito.mock(Sink.class);
        Sink.SinkWriter sinkWriter = Mockito.mock(SinkWriter.class);
        Mockito.when(sink.writer()).thenReturn(sinkWriter);
        Mockito.when(sinkWriter.add("hello")).thenReturn(10L);
        Mockito.when(context.counterFactory()).thenReturn(counterSet);
        Mockito.when(context.nameContext()).thenReturn(NameContextsForTests.nameContextForTest());
        WriteOperation operation = WriteOperation.forTest(sink, context);
        Closeable startCloseable = Mockito.mock(Closeable.class);
        Closeable processCloseable = Mockito.mock(Closeable.class);
        Closeable finishCloseable = Mockito.mock(Closeable.class);
        Mockito.when(context.enterStart()).thenReturn(startCloseable);
        Mockito.when(context.enterProcess()).thenReturn(processCloseable);
        Mockito.when(context.enterFinish()).thenReturn(finishCloseable);
        operation.start();
        operation.process("hello");
        operation.finish();
        InOrder inOrder = Mockito.inOrder(sink, sinkWriter, context, startCloseable, processCloseable, finishCloseable);
        inOrder.verify(context).enterStart();
        inOrder.verify(sink).writer();
        inOrder.verify(startCloseable).close();
        inOrder.verify(context).enterProcess();
        inOrder.verify(sinkWriter).add("hello");
        inOrder.verify(processCloseable).close();
        inOrder.verify(context).enterFinish();
        inOrder.verify(sinkWriter).close();
        inOrder.verify(finishCloseable).close();
    }
}

