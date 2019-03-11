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
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.beam.runners.dataflow.worker.util.common.worker.OperationContext;
import org.apache.beam.runners.dataflow.worker.util.common.worker.TestOutputReceiver;
import org.apache.beam.runners.fnexecution.data.FnDataService;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.fn.IdGenerator;
import org.apache.beam.sdk.fn.data.CompletableFutureInboundDataClient;
import org.apache.beam.sdk.fn.data.FnDataReceiver;
import org.apache.beam.sdk.fn.data.InboundDataClient;
import org.apache.beam.sdk.fn.data.LogicalEndpoint;
import org.apache.beam.sdk.util.WindowedValue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.contains;


/**
 * Tests for {@link RemoteGrpcPortReadOperation}.
 */
@RunWith(JUnit4.class)
public class RemoteGrpcPortReadOperationTest {
    private static final Coder<WindowedValue<String>> CODER = WindowedValue.getFullCoder(StringUtf8Coder.of(), INSTANCE);

    private static final Target TARGET = Target.newBuilder().setPrimitiveTransformReference("1").setName("name").build();

    private static final String BUNDLE_ID = "999";

    private static final String BUNDLE_ID_2 = "222";

    @Mock
    private FnDataService beamFnDataService;

    @Mock
    private OperationContext operationContext;

    @Captor
    private ArgumentCaptor<FnDataReceiver<WindowedValue<String>>> consumerCaptor;

    @Mock
    private IdGenerator bundleIdSupplier;

    private RemoteGrpcPortReadOperation<String> operation;

    private TestOutputReceiver testReceiver;

    @Test
    public void testSupportsRestart() {
        Assert.assertTrue(operation.supportsRestart());
    }

    @Test
    public void testSuccessfulProcessing() throws Exception {
        InboundDataClient inboundDataClient = CompletableFutureInboundDataClient.create();
        Mockito.when(beamFnDataService.receive(ArgumentMatchers.any(), Matchers.<Coder<WindowedValue<String>>>any(), ArgumentMatchers.any())).thenReturn(inboundDataClient);
        Mockito.when(bundleIdSupplier.getId()).thenReturn(RemoteGrpcPortReadOperationTest.BUNDLE_ID);
        operation.start();
        Mockito.verify(beamFnDataService).receive(ArgumentMatchers.eq(LogicalEndpoint.of(RemoteGrpcPortReadOperationTest.BUNDLE_ID, RemoteGrpcPortReadOperationTest.TARGET)), ArgumentMatchers.eq(RemoteGrpcPortReadOperationTest.CODER), consumerCaptor.capture());
        Future<Void> operationFinish = Executors.newSingleThreadExecutor().submit(() -> {
            operation.finish();
            return null;
        });
        consumerCaptor.getValue().accept(valueInGlobalWindow("ABC"));
        consumerCaptor.getValue().accept(valueInGlobalWindow("DEF"));
        consumerCaptor.getValue().accept(valueInGlobalWindow("GHI"));
        // Purposefully sleep to show that the operation is still not done until the finish signal
        // is completed.
        Thread.sleep(100L);
        Assert.assertFalse(operationFinish.isDone());
        inboundDataClient.complete();
        operationFinish.get();
        Mockito.verify(bundleIdSupplier, Mockito.times(1)).getId();
        Assert.assertThat(testReceiver.outputElems, contains(valueInGlobalWindow("ABC"), valueInGlobalWindow("DEF"), valueInGlobalWindow("GHI")));
        // Ensure that the old bundle id is cleared.
        Mockito.when(bundleIdSupplier.getId()).thenReturn(RemoteGrpcPortReadOperationTest.BUNDLE_ID_2);
        operation.start();
        Mockito.verify(beamFnDataService).receive(ArgumentMatchers.eq(LogicalEndpoint.of(RemoteGrpcPortReadOperationTest.BUNDLE_ID_2, RemoteGrpcPortReadOperationTest.TARGET)), ArgumentMatchers.eq(RemoteGrpcPortReadOperationTest.CODER), consumerCaptor.capture());
    }

    @Test
    public void testStartAndAbort() throws Exception {
        InboundDataClient inboundDataClient = CompletableFutureInboundDataClient.create();
        Mockito.when(beamFnDataService.receive(ArgumentMatchers.any(), Matchers.<Coder<WindowedValue<String>>>any(), ArgumentMatchers.any())).thenReturn(inboundDataClient);
        Mockito.when(bundleIdSupplier.getId()).thenReturn(RemoteGrpcPortReadOperationTest.BUNDLE_ID);
        operation.start();
        Mockito.verify(beamFnDataService).receive(ArgumentMatchers.eq(LogicalEndpoint.of(RemoteGrpcPortReadOperationTest.BUNDLE_ID, RemoteGrpcPortReadOperationTest.TARGET)), ArgumentMatchers.eq(RemoteGrpcPortReadOperationTest.CODER), consumerCaptor.capture());
        Assert.assertFalse(inboundDataClient.isDone());
        operation.abort();
        Assert.assertTrue(inboundDataClient.isDone());
        Mockito.verify(bundleIdSupplier, Mockito.times(1)).getId();
    }
}

