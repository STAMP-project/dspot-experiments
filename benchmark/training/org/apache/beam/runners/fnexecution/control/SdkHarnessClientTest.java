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
package org.apache.beam.runners.fnexecution.control;


import BeamFnApi.InstructionRequest;
import BeamFnApi.Target;
import GlobalWindow.Coder.INSTANCE;
import StateDelegator.Registration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.beam.model.fnexecution.v1.BeamFnApi;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.InstructionResponse;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.ProcessBundleDescriptor;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.ProcessBundleResponse;
import org.apache.beam.model.pipeline.v1.Endpoints.ApiServiceDescriptor;
import org.apache.beam.runners.fnexecution.EmbeddedSdkHarness;
import org.apache.beam.runners.fnexecution.control.SdkHarnessClient.ActiveBundle;
import org.apache.beam.runners.fnexecution.control.SdkHarnessClient.BundleProcessor;
import org.apache.beam.runners.fnexecution.data.FnDataService;
import org.apache.beam.runners.fnexecution.data.RemoteInputDestination;
import org.apache.beam.runners.fnexecution.state.StateDelegator;
import org.apache.beam.runners.fnexecution.state.StateRequestHandler;
import org.apache.beam.sdk.coders.LengthPrefixCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.fn.data.CloseableFnDataReceiver;
import org.apache.beam.sdk.fn.data.FnDataReceiver;
import org.apache.beam.sdk.fn.data.InboundDataClient;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.util.WindowedValue.FullWindowedValueCoder;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link SdkHarnessClient}.
 */
@RunWith(JUnit4.class)
public class SdkHarnessClientTest {
    @Mock
    public FnApiControlClient fnApiControlClient;

    @Mock
    public FnDataService dataService;

    @Rule
    public EmbeddedSdkHarness harness = EmbeddedSdkHarness.create();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private SdkHarnessClient sdkHarnessClient;

    private ProcessBundleDescriptor descriptor;

    private String inputPCollection;

    private Target sdkGrpcReadTarget;

    private Target sdkGrpcWriteTarget;

    @Test
    public void testRegisterCachesBundleProcessors() throws Exception {
        CompletableFuture<InstructionResponse> registerResponseFuture = new CompletableFuture<>();
        Mockito.when(fnApiControlClient.handle(ArgumentMatchers.any(InstructionRequest.class))).thenReturn(registerResponseFuture);
        ProcessBundleDescriptor descriptor1 = ProcessBundleDescriptor.newBuilder().setId("descriptor1").build();
        ProcessBundleDescriptor descriptor2 = ProcessBundleDescriptor.newBuilder().setId("descriptor2").build();
        Map<String, RemoteInputDestination<WindowedValue<?>>> remoteInputs = Collections.singletonMap("inputPC", RemoteInputDestination.of(((FullWindowedValueCoder) (FullWindowedValueCoder.of(VarIntCoder.of(), INSTANCE))), sdkGrpcReadTarget));
        BundleProcessor processor1 = sdkHarnessClient.getProcessor(descriptor1, remoteInputs);
        BundleProcessor processor2 = sdkHarnessClient.getProcessor(descriptor2, remoteInputs);
        Assert.assertNotSame(processor1, processor2);
        // Ensure that caching works.
        Assert.assertSame(processor1, sdkHarnessClient.getProcessor(descriptor1, remoteInputs));
    }

    @Test
    public void testRegisterWithStateRequiresStateDelegator() throws Exception {
        CompletableFuture<InstructionResponse> registerResponseFuture = new CompletableFuture<>();
        Mockito.when(fnApiControlClient.handle(ArgumentMatchers.any(InstructionRequest.class))).thenReturn(registerResponseFuture);
        ProcessBundleDescriptor descriptor = ProcessBundleDescriptor.newBuilder().setId("test").setStateApiServiceDescriptor(ApiServiceDescriptor.newBuilder().setUrl("foo")).build();
        Map<String, RemoteInputDestination<WindowedValue<?>>> remoteInputs = Collections.singletonMap("inputPC", RemoteInputDestination.of(((FullWindowedValueCoder) (FullWindowedValueCoder.of(VarIntCoder.of(), INSTANCE))), sdkGrpcReadTarget));
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("containing a state");
        sdkHarnessClient.getProcessor(descriptor, remoteInputs);
    }

    @Test
    public void testNewBundleNoDataDoesNotCrash() throws Exception {
        CompletableFuture<InstructionResponse> processBundleResponseFuture = new CompletableFuture<>();
        Mockito.when(fnApiControlClient.handle(ArgumentMatchers.any(InstructionRequest.class))).thenReturn(new CompletableFuture()).thenReturn(processBundleResponseFuture);
        FullWindowedValueCoder<String> coder = FullWindowedValueCoder.of(StringUtf8Coder.of(), Coder.INSTANCE);
        BundleProcessor processor = sdkHarnessClient.getProcessor(descriptor, Collections.singletonMap("inputPC", RemoteInputDestination.of(((FullWindowedValueCoder) (coder)), sdkGrpcReadTarget)));
        Mockito.when(dataService.send(ArgumentMatchers.any(), ArgumentMatchers.eq(coder))).thenReturn(Mockito.mock(CloseableFnDataReceiver.class));
        try (ActiveBundle activeBundle = processor.newBundle(Collections.emptyMap(), BundleProgressHandler.ignored())) {
            // Correlating the ProcessBundleRequest and ProcessBundleResponse is owned by the underlying
            // FnApiControlClient. The SdkHarnessClient owns just wrapping the request and unwrapping
            // the response.
            // 
            // Currently there are no fields so there's nothing to check. This test is formulated
            // to match the pattern it should have if/when the response is meaningful.
            BeamFnApi.ProcessBundleResponse response = ProcessBundleResponse.getDefaultInstance();
            processBundleResponseFuture.complete(BeamFnApi.InstructionResponse.newBuilder().setProcessBundle(response).build());
        }
    }

    @Test
    public void testNewBundleAndProcessElements() throws Exception {
        SdkHarnessClient client = harness.client();
        BundleProcessor processor = client.getProcessor(descriptor, Collections.singletonMap("inputPC", RemoteInputDestination.of(((FullWindowedValueCoder) (FullWindowedValueCoder.of(StringUtf8Coder.of(), Coder.INSTANCE))), sdkGrpcReadTarget)));
        Collection<WindowedValue<String>> outputs = new ArrayList<>();
        try (ActiveBundle activeBundle = processor.newBundle(Collections.singletonMap(sdkGrpcWriteTarget, RemoteOutputReceiver.of(FullWindowedValueCoder.of(LengthPrefixCoder.of(StringUtf8Coder.of()), Coder.INSTANCE), outputs::add)), BundleProgressHandler.ignored())) {
            FnDataReceiver<WindowedValue<?>> bundleInputReceiver = Iterables.getOnlyElement(activeBundle.getInputReceivers().values());
            bundleInputReceiver.accept(WindowedValue.valueInGlobalWindow("foo"));
            bundleInputReceiver.accept(WindowedValue.valueInGlobalWindow("bar"));
            bundleInputReceiver.accept(WindowedValue.valueInGlobalWindow("baz"));
        }
        // The bundle can be a simple function of some sort, but needs to be complete.
        Assert.assertThat(outputs, Matchers.containsInAnyOrder(WindowedValue.valueInGlobalWindow("spam"), WindowedValue.valueInGlobalWindow("ham"), WindowedValue.valueInGlobalWindow("eggs")));
    }

    @Test
    public void handleCleanupWhenInputSenderFails() throws Exception {
        Exception testException = new Exception();
        InboundDataClient mockOutputReceiver = Mockito.mock(InboundDataClient.class);
        CloseableFnDataReceiver mockInputSender = Mockito.mock(CloseableFnDataReceiver.class);
        CompletableFuture<InstructionResponse> processBundleResponseFuture = new CompletableFuture<>();
        Mockito.when(fnApiControlClient.handle(ArgumentMatchers.any(InstructionRequest.class))).thenReturn(new CompletableFuture()).thenReturn(processBundleResponseFuture);
        FullWindowedValueCoder<String> coder = FullWindowedValueCoder.of(StringUtf8Coder.of(), Coder.INSTANCE);
        BundleProcessor processor = sdkHarnessClient.getProcessor(descriptor, Collections.singletonMap("inputPC", RemoteInputDestination.of(((FullWindowedValueCoder) (coder)), sdkGrpcReadTarget)));
        Mockito.when(dataService.receive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockOutputReceiver);
        Mockito.when(dataService.send(ArgumentMatchers.any(), ArgumentMatchers.eq(coder))).thenReturn(mockInputSender);
        Mockito.doThrow(testException).when(mockInputSender).close();
        RemoteOutputReceiver mockRemoteOutputReceiver = Mockito.mock(RemoteOutputReceiver.class);
        BundleProgressHandler mockProgressHandler = Mockito.mock(BundleProgressHandler.class);
        try {
            try (ActiveBundle activeBundle = processor.newBundle(ImmutableMap.of(sdkGrpcWriteTarget, mockRemoteOutputReceiver), mockProgressHandler)) {
                // We shouldn't be required to complete the process bundle response future.
            }
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertEquals(testException, e);
            Mockito.verify(mockOutputReceiver).cancel();
            Mockito.verifyNoMoreInteractions(mockOutputReceiver);
        }
    }

    @Test
    public void handleCleanupWithStateWhenInputSenderFails() throws Exception {
        Exception testException = new Exception();
        InboundDataClient mockOutputReceiver = Mockito.mock(InboundDataClient.class);
        CloseableFnDataReceiver mockInputSender = Mockito.mock(CloseableFnDataReceiver.class);
        StateDelegator mockStateDelegator = Mockito.mock(StateDelegator.class);
        StateDelegator.Registration mockStateRegistration = Mockito.mock(Registration.class);
        Mockito.when(mockStateDelegator.registerForProcessBundleInstructionId(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockStateRegistration);
        StateRequestHandler mockStateHandler = Mockito.mock(StateRequestHandler.class);
        BundleProgressHandler mockProgressHandler = Mockito.mock(BundleProgressHandler.class);
        CompletableFuture<InstructionResponse> processBundleResponseFuture = new CompletableFuture<>();
        Mockito.when(fnApiControlClient.handle(ArgumentMatchers.any(InstructionRequest.class))).thenReturn(new CompletableFuture()).thenReturn(processBundleResponseFuture);
        FullWindowedValueCoder<String> coder = FullWindowedValueCoder.of(StringUtf8Coder.of(), Coder.INSTANCE);
        BundleProcessor processor = sdkHarnessClient.getProcessor(descriptor, Collections.singletonMap(inputPCollection, RemoteInputDestination.of(((FullWindowedValueCoder) (coder)), sdkGrpcReadTarget)), mockStateDelegator);
        Mockito.when(dataService.receive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockOutputReceiver);
        Mockito.when(dataService.send(ArgumentMatchers.any(), ArgumentMatchers.eq(coder))).thenReturn(mockInputSender);
        Mockito.doThrow(testException).when(mockInputSender).close();
        RemoteOutputReceiver mockRemoteOutputReceiver = Mockito.mock(RemoteOutputReceiver.class);
        try {
            try (ActiveBundle activeBundle = processor.newBundle(ImmutableMap.of(sdkGrpcWriteTarget, mockRemoteOutputReceiver), mockStateHandler, mockProgressHandler)) {
                // We shouldn't be required to complete the process bundle response future.
            }
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertEquals(testException, e);
            Mockito.verify(mockStateRegistration).abort();
            Mockito.verify(mockOutputReceiver).cancel();
            Mockito.verifyNoMoreInteractions(mockStateRegistration, mockOutputReceiver);
        }
    }

    @Test
    public void handleCleanupWhenProcessingBundleFails() throws Exception {
        Exception testException = new Exception();
        InboundDataClient mockOutputReceiver = Mockito.mock(InboundDataClient.class);
        CloseableFnDataReceiver mockInputSender = Mockito.mock(CloseableFnDataReceiver.class);
        CompletableFuture<InstructionResponse> processBundleResponseFuture = new CompletableFuture<>();
        Mockito.when(fnApiControlClient.handle(ArgumentMatchers.any(InstructionRequest.class))).thenReturn(new CompletableFuture()).thenReturn(processBundleResponseFuture);
        FullWindowedValueCoder<String> coder = FullWindowedValueCoder.of(StringUtf8Coder.of(), Coder.INSTANCE);
        BundleProcessor processor = sdkHarnessClient.getProcessor(descriptor, Collections.singletonMap("inputPC", RemoteInputDestination.of(((FullWindowedValueCoder) (coder)), sdkGrpcReadTarget)));
        Mockito.when(dataService.receive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockOutputReceiver);
        Mockito.when(dataService.send(ArgumentMatchers.any(), ArgumentMatchers.eq(coder))).thenReturn(mockInputSender);
        RemoteOutputReceiver mockRemoteOutputReceiver = Mockito.mock(RemoteOutputReceiver.class);
        BundleProgressHandler mockProgressHandler = Mockito.mock(BundleProgressHandler.class);
        try {
            try (ActiveBundle activeBundle = processor.newBundle(ImmutableMap.of(sdkGrpcWriteTarget, mockRemoteOutputReceiver), mockProgressHandler)) {
                processBundleResponseFuture.completeExceptionally(testException);
            }
            Assert.fail("Exception expected");
        } catch (ExecutionException e) {
            Assert.assertEquals(testException, e.getCause());
            Mockito.verify(mockOutputReceiver).cancel();
            Mockito.verifyNoMoreInteractions(mockOutputReceiver);
        }
    }

    @Test
    public void handleCleanupWithStateWhenProcessingBundleFails() throws Exception {
        Exception testException = new Exception();
        InboundDataClient mockOutputReceiver = Mockito.mock(InboundDataClient.class);
        CloseableFnDataReceiver mockInputSender = Mockito.mock(CloseableFnDataReceiver.class);
        StateDelegator mockStateDelegator = Mockito.mock(StateDelegator.class);
        StateDelegator.Registration mockStateRegistration = Mockito.mock(Registration.class);
        Mockito.when(mockStateDelegator.registerForProcessBundleInstructionId(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockStateRegistration);
        StateRequestHandler mockStateHandler = Mockito.mock(StateRequestHandler.class);
        BundleProgressHandler mockProgressHandler = Mockito.mock(BundleProgressHandler.class);
        CompletableFuture<InstructionResponse> processBundleResponseFuture = new CompletableFuture<>();
        Mockito.when(fnApiControlClient.handle(ArgumentMatchers.any(InstructionRequest.class))).thenReturn(new CompletableFuture()).thenReturn(processBundleResponseFuture);
        FullWindowedValueCoder<String> coder = FullWindowedValueCoder.of(StringUtf8Coder.of(), Coder.INSTANCE);
        BundleProcessor processor = sdkHarnessClient.getProcessor(descriptor, Collections.singletonMap(inputPCollection, RemoteInputDestination.of(((FullWindowedValueCoder) (coder)), sdkGrpcReadTarget)), mockStateDelegator);
        Mockito.when(dataService.receive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockOutputReceiver);
        Mockito.when(dataService.send(ArgumentMatchers.any(), ArgumentMatchers.eq(coder))).thenReturn(mockInputSender);
        RemoteOutputReceiver mockRemoteOutputReceiver = Mockito.mock(RemoteOutputReceiver.class);
        try {
            try (ActiveBundle activeBundle = processor.newBundle(ImmutableMap.of(sdkGrpcWriteTarget, mockRemoteOutputReceiver), mockStateHandler, mockProgressHandler)) {
                processBundleResponseFuture.completeExceptionally(testException);
            }
            Assert.fail("Exception expected");
        } catch (ExecutionException e) {
            Assert.assertEquals(testException, e.getCause());
            Mockito.verify(mockStateRegistration).abort();
            Mockito.verify(mockOutputReceiver).cancel();
            Mockito.verifyNoMoreInteractions(mockStateRegistration, mockOutputReceiver);
        }
    }

    @Test
    public void handleCleanupWhenAwaitingOnClosingOutputReceivers() throws Exception {
        Exception testException = new Exception();
        InboundDataClient mockOutputReceiver = Mockito.mock(InboundDataClient.class);
        CloseableFnDataReceiver mockInputSender = Mockito.mock(CloseableFnDataReceiver.class);
        CompletableFuture<InstructionResponse> processBundleResponseFuture = new CompletableFuture<>();
        Mockito.when(fnApiControlClient.handle(ArgumentMatchers.any(InstructionRequest.class))).thenReturn(new CompletableFuture()).thenReturn(processBundleResponseFuture);
        FullWindowedValueCoder<String> coder = FullWindowedValueCoder.of(StringUtf8Coder.of(), Coder.INSTANCE);
        BundleProcessor processor = sdkHarnessClient.getProcessor(descriptor, Collections.singletonMap("inputPC", RemoteInputDestination.of(((FullWindowedValueCoder) (coder)), sdkGrpcReadTarget)));
        Mockito.when(dataService.receive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockOutputReceiver);
        Mockito.when(dataService.send(ArgumentMatchers.any(), ArgumentMatchers.eq(coder))).thenReturn(mockInputSender);
        Mockito.doThrow(testException).when(mockOutputReceiver).awaitCompletion();
        RemoteOutputReceiver mockRemoteOutputReceiver = Mockito.mock(RemoteOutputReceiver.class);
        BundleProgressHandler mockProgressHandler = Mockito.mock(BundleProgressHandler.class);
        try {
            try (ActiveBundle activeBundle = processor.newBundle(ImmutableMap.of(sdkGrpcWriteTarget, mockRemoteOutputReceiver), mockProgressHandler)) {
                // Correlating the ProcessBundleRequest and ProcessBundleResponse is owned by the underlying
                // FnApiControlClient. The SdkHarnessClient owns just wrapping the request and unwrapping
                // the response.
                // 
                // Currently there are no fields so there's nothing to check. This test is formulated
                // to match the pattern it should have if/when the response is meaningful.
                BeamFnApi.ProcessBundleResponse response = BeamFnApi.ProcessBundleResponse.getDefaultInstance();
                processBundleResponseFuture.complete(BeamFnApi.InstructionResponse.newBuilder().setProcessBundle(response).build());
            }
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertEquals(testException, e);
        }
    }

    @Test
    public void handleCleanupWithStateWhenAwaitingOnClosingOutputReceivers() throws Exception {
        Exception testException = new Exception();
        InboundDataClient mockOutputReceiver = Mockito.mock(InboundDataClient.class);
        CloseableFnDataReceiver mockInputSender = Mockito.mock(CloseableFnDataReceiver.class);
        StateDelegator mockStateDelegator = Mockito.mock(StateDelegator.class);
        StateDelegator.Registration mockStateRegistration = Mockito.mock(Registration.class);
        Mockito.when(mockStateDelegator.registerForProcessBundleInstructionId(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockStateRegistration);
        StateRequestHandler mockStateHandler = Mockito.mock(StateRequestHandler.class);
        BundleProgressHandler mockProgressHandler = Mockito.mock(BundleProgressHandler.class);
        CompletableFuture<InstructionResponse> processBundleResponseFuture = new CompletableFuture<>();
        Mockito.when(fnApiControlClient.handle(ArgumentMatchers.any(InstructionRequest.class))).thenReturn(new CompletableFuture()).thenReturn(processBundleResponseFuture);
        FullWindowedValueCoder<String> coder = FullWindowedValueCoder.of(StringUtf8Coder.of(), Coder.INSTANCE);
        BundleProcessor processor = sdkHarnessClient.getProcessor(descriptor, Collections.singletonMap(inputPCollection, RemoteInputDestination.of(((FullWindowedValueCoder) (coder)), sdkGrpcReadTarget)), mockStateDelegator);
        Mockito.when(dataService.receive(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockOutputReceiver);
        Mockito.when(dataService.send(ArgumentMatchers.any(), ArgumentMatchers.eq(coder))).thenReturn(mockInputSender);
        Mockito.doThrow(testException).when(mockOutputReceiver).awaitCompletion();
        RemoteOutputReceiver mockRemoteOutputReceiver = Mockito.mock(RemoteOutputReceiver.class);
        try {
            try (ActiveBundle activeBundle = processor.newBundle(ImmutableMap.of(sdkGrpcWriteTarget, mockRemoteOutputReceiver), mockStateHandler, mockProgressHandler)) {
                // Correlating the ProcessBundleRequest and ProcessBundleResponse is owned by the underlying
                // FnApiControlClient. The SdkHarnessClient owns just wrapping the request and unwrapping
                // the response.
                // 
                // Currently there are no fields so there's nothing to check. This test is formulated
                // to match the pattern it should have if/when the response is meaningful.
                BeamFnApi.ProcessBundleResponse response = BeamFnApi.ProcessBundleResponse.getDefaultInstance();
                processBundleResponseFuture.complete(BeamFnApi.InstructionResponse.newBuilder().setProcessBundle(response).build());
            }
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertEquals(testException, e);
        }
    }

    private static class TestFn extends DoFn<String, String> {
        @ProcessElement
        public void processElement(ProcessContext context) {
            if ("foo".equals(context.element())) {
                context.output("spam");
            } else
                if ("bar".equals(context.element())) {
                    context.output("ham");
                } else {
                    context.output("eggs");
                }

        }
    }
}

