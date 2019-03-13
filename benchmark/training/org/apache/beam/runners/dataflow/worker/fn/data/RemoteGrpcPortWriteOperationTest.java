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
package org.apache.beam.runners.dataflow.worker.fn.data;


import BeamFnApi.Target;
import GlobalWindow.Coder.INSTANCE;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.apache.beam.runners.dataflow.worker.util.common.worker.OperationContext;
import org.apache.beam.runners.fnexecution.data.FnDataService;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.fn.IdGenerator;
import org.apache.beam.sdk.fn.data.CloseableFnDataReceiver;
import org.apache.beam.sdk.fn.data.LogicalEndpoint;
import org.apache.beam.sdk.util.WindowedValue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static RemoteGrpcPortWriteOperation.MAX_BUFFER_MILLIS;
import static org.hamcrest.Matchers.contains;


/**
 * Tests for {@link RemoteGrpcPortWriteOperation}.
 */
@RunWith(JUnit4.class)
public class RemoteGrpcPortWriteOperationTest {
    private static final Coder<WindowedValue<String>> CODER = WindowedValue.getFullCoder(StringUtf8Coder.of(), INSTANCE);

    private static final Target TARGET = Target.newBuilder().setPrimitiveTransformReference("1").setName("name").build();

    private static final String BUNDLE_ID = "999";

    private static final String BUNDLE_ID_2 = "222";

    @Mock
    IdGenerator bundleIdSupplier;

    @Mock
    private FnDataService beamFnDataService;

    @Mock
    private OperationContext operationContext;

    private RemoteGrpcPortWriteOperation<String> operation;

    @Test
    public void testSupportsRestart() {
        Assert.assertTrue(operation.supportsRestart());
    }

    @Test
    public void testSuccessfulProcessing() throws Exception {
        RemoteGrpcPortWriteOperationTest.RecordingConsumer<WindowedValue<String>> recordingConsumer = new RemoteGrpcPortWriteOperationTest.RecordingConsumer<>();
        Mockito.when(beamFnDataService.send(ArgumentMatchers.any(), Matchers.<Coder<WindowedValue<String>>>any())).thenReturn(recordingConsumer);
        Mockito.when(bundleIdSupplier.getId()).thenReturn(RemoteGrpcPortWriteOperationTest.BUNDLE_ID);
        operation.start();
        Mockito.verify(beamFnDataService).send(LogicalEndpoint.of(RemoteGrpcPortWriteOperationTest.BUNDLE_ID, RemoteGrpcPortWriteOperationTest.TARGET), RemoteGrpcPortWriteOperationTest.CODER);
        Assert.assertFalse(recordingConsumer.closed);
        operation.process(valueInGlobalWindow("ABC"));
        operation.process(valueInGlobalWindow("DEF"));
        operation.process(valueInGlobalWindow("GHI"));
        Assert.assertFalse(recordingConsumer.closed);
        operation.finish();
        Assert.assertTrue(recordingConsumer.closed);
        Mockito.verify(bundleIdSupplier, Mockito.times(1)).getId();
        Assert.assertThat(recordingConsumer, contains(valueInGlobalWindow("ABC"), valueInGlobalWindow("DEF"), valueInGlobalWindow("GHI")));
        // Ensure that the old bundle id is cleared.
        Mockito.when(bundleIdSupplier.getId()).thenReturn(RemoteGrpcPortWriteOperationTest.BUNDLE_ID_2);
        Mockito.when(beamFnDataService.send(ArgumentMatchers.any(), Matchers.<Coder<WindowedValue<String>>>any())).thenReturn(recordingConsumer);
        operation.start();
        Mockito.verify(beamFnDataService).send(LogicalEndpoint.of(RemoteGrpcPortWriteOperationTest.BUNDLE_ID_2, RemoteGrpcPortWriteOperationTest.TARGET), RemoteGrpcPortWriteOperationTest.CODER);
        Mockito.verifyNoMoreInteractions(beamFnDataService);
    }

    @Test
    public void testStartAndAbort() throws Exception {
        RemoteGrpcPortWriteOperationTest.RecordingConsumer<WindowedValue<String>> recordingConsumer = new RemoteGrpcPortWriteOperationTest.RecordingConsumer<>();
        Mockito.when(beamFnDataService.send(ArgumentMatchers.any(), Matchers.<Coder<WindowedValue<String>>>any())).thenReturn(recordingConsumer);
        Mockito.when(bundleIdSupplier.getId()).thenReturn(RemoteGrpcPortWriteOperationTest.BUNDLE_ID);
        operation.start();
        Mockito.verify(beamFnDataService).send(LogicalEndpoint.of(RemoteGrpcPortWriteOperationTest.BUNDLE_ID, RemoteGrpcPortWriteOperationTest.TARGET), RemoteGrpcPortWriteOperationTest.CODER);
        Assert.assertFalse(recordingConsumer.closed);
        operation.process(valueInGlobalWindow("ABC"));
        operation.process(valueInGlobalWindow("DEF"));
        operation.process(valueInGlobalWindow("GHI"));
        operation.abort();
        Assert.assertTrue(recordingConsumer.closed);
        Mockito.verify(bundleIdSupplier, Mockito.times(1)).getId();
        Mockito.verifyNoMoreInteractions(beamFnDataService);
    }

    @Test
    public void testBufferRateLimiting() throws Exception {
        AtomicInteger processedElements = new AtomicInteger();
        AtomicInteger currentTimeMillis = new AtomicInteger(10000);
        final int START_BUFFER_SIZE = 3;
        final int STEADY_BUFFER_SIZE = 10;
        final int FIRST_ELEMENT_DURATION = (((int) (MAX_BUFFER_MILLIS)) / START_BUFFER_SIZE) + 1;
        final int STEADY_ELEMENT_DURATION = (((int) (MAX_BUFFER_MILLIS)) / STEADY_BUFFER_SIZE) + 1;
        operation = new RemoteGrpcPortWriteOperation(beamFnDataService, RemoteGrpcPortWriteOperationTest.TARGET, bundleIdSupplier, RemoteGrpcPortWriteOperationTest.CODER, operationContext, () -> ((long) (currentTimeMillis.get())));
        RemoteGrpcPortWriteOperationTest.RecordingConsumer<WindowedValue<String>> recordingConsumer = new RemoteGrpcPortWriteOperationTest.RecordingConsumer<>();
        Mockito.when(beamFnDataService.send(ArgumentMatchers.any(), Matchers.<Coder<WindowedValue<String>>>any())).thenReturn(recordingConsumer);
        Mockito.when(bundleIdSupplier.getId()).thenReturn(RemoteGrpcPortWriteOperationTest.BUNDLE_ID);
        Consumer<Integer> processedElementConsumer = operation.processedElementsConsumer();
        operation.start();
        // Never wait before sending the first element.
        Assert.assertFalse(operation.shouldWait());
        operation.process(valueInGlobalWindow("first"));
        // After sending the first element, wait until it's processed before sending another.
        Assert.assertTrue(operation.shouldWait());
        // Once we've processed the element, we can send the second.
        currentTimeMillis.getAndAdd(FIRST_ELEMENT_DURATION);
        processedElementConsumer.accept(1);
        Assert.assertFalse(operation.shouldWait());
        operation.process(valueInGlobalWindow("second"));
        // Send elements until the buffer is full.
        for (int i = 2; i < (START_BUFFER_SIZE + 1); i++) {
            Assert.assertFalse(operation.shouldWait());
            operation.process(valueInGlobalWindow(("element" + i)));
        }
        // The buffer is full.
        Assert.assertTrue(operation.shouldWait());
        // Now finish processing the second element.
        currentTimeMillis.getAndAdd(STEADY_ELEMENT_DURATION);
        processedElementConsumer.accept(2);
        // That was faster, so our buffer quota is larger.
        for (int i = START_BUFFER_SIZE + 1; i < (STEADY_BUFFER_SIZE + 2); i++) {
            Assert.assertFalse(operation.shouldWait());
            operation.process(valueInGlobalWindow(("element" + i)));
        }
        // The buffer is full again.
        Assert.assertTrue(operation.shouldWait());
        // As elements are consumed, we can keep adding more.
        for (int i = (START_BUFFER_SIZE + STEADY_BUFFER_SIZE) + 2; i < 100; i++) {
            currentTimeMillis.getAndAdd(STEADY_ELEMENT_DURATION);
            processedElementConsumer.accept(i);
            Assert.assertFalse(operation.shouldWait());
            operation.process(valueInGlobalWindow(("element" + i)));
        }
        operation.finish();
    }

    private static class RecordingConsumer<T> extends ArrayList<T> implements CloseableFnDataReceiver<T> {
        private boolean closed;

        @Override
        public void close() throws Exception {
            closed = true;
        }

        @Override
        public void flush() throws Exception {
        }

        @Override
        public synchronized void accept(T t) throws Exception {
            if (closed) {
                throw new IllegalStateException(("Consumer is closed but attempting to consume " + t));
            }
            add(t);
        }
    }
}

