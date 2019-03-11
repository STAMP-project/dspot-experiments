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
package org.apache.beam.runners.dataflow.worker.fn.control;


import BeamFnApi.Metrics.PTransform;
import BeamFnApi.Metrics.User.CounterData;
import BeamFnApi.Metrics.User.MetricName;
import BeamFnApi.ProcessBundleDescriptor;
import BeamFnApi.ProcessBundleProgressResponse;
import BeamFnApi.ProcessBundleRequest;
import BeamFnApi.ProcessBundleResponse;
import BeamFnApi.RegisterRequest;
import BeamFnApi.RegisterResponse;
import ByteString.EMPTY;
import GlobalWindow.INSTANCE;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.apache.beam.model.fnexecution.v1.BeamFnApi;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.InstructionRequest;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.InstructionRequest.RequestCase;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.InstructionResponse;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.StateAppendRequest;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.StateClearRequest;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.StateGetRequest;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.StateKey;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.StateRequest;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.StateResponse;
import org.apache.beam.runners.core.InMemoryMultimapSideInputView;
import org.apache.beam.runners.core.InMemoryStateInternals;
import org.apache.beam.runners.core.SideInputReader;
import org.apache.beam.runners.dataflow.worker.DataflowExecutionContext.DataflowStepContext;
import org.apache.beam.runners.dataflow.worker.DataflowPortabilityPCollectionView;
import org.apache.beam.runners.dataflow.worker.util.common.worker.OperationContext;
import org.apache.beam.runners.fnexecution.control.InstructionRequestHandler;
import org.apache.beam.runners.fnexecution.state.StateDelegator;
import org.apache.beam.runners.fnexecution.state.StateRequestHandler;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.fn.IdGenerator;
import org.apache.beam.sdk.fn.IdGenerators;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.util.MoreFutures;
import org.apache.beam.sdk.util.ThrowingRunnable;
import org.apache.beam.sdk.util.WindowedValue.FullWindowedValueCoder;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableTable;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link RegisterAndProcessBundleOperation}.
 */
@RunWith(JUnit4.class)
@SuppressWarnings("FutureReturnValueIgnored")
public class RegisterAndProcessBundleOperationTest {
    private static final RegisterRequest REGISTER_REQUEST = RegisterRequest.newBuilder().addProcessBundleDescriptor(ProcessBundleDescriptor.newBuilder().setId("555")).build();

    @Mock
    private OperationContext mockContext;

    @Mock
    private StateDelegator mockBeamFnStateDelegator;

    @Captor
    private ArgumentCaptor<StateRequestHandler> stateHandlerCaptor;

    private AtomicInteger stateServiceRegisterCounter;

    private AtomicInteger stateServiceDeregisterCounter;

    private AtomicInteger stateServiceAbortCounter;

    @Test
    public void testSupportsRestart() {
        supportsRestart();
    }

    @Test
    public void testRegisterOnlyOnFirstBundle() throws Exception {
        List<BeamFnApi.InstructionRequest> requests = new ArrayList<>();
        IdGenerator idGenerator = makeIdGeneratorStartingFrom(777L);
        RegisterAndProcessBundleOperation operation = new RegisterAndProcessBundleOperation(idGenerator, new InstructionRequestHandler() {
            @Override
            public CompletionStage<InstructionResponse> handle(InstructionRequest request) {
                requests.add(request);
                switch (request.getRequestCase()) {
                    case REGISTER :
                        return CompletableFuture.completedFuture(responseFor(request).setRegister(RegisterResponse.getDefaultInstance()).build());
                    case PROCESS_BUNDLE :
                        return CompletableFuture.completedFuture(responseFor(request).setProcessBundle(ProcessBundleResponse.getDefaultInstance()).build());
                    default :
                        // block forever on other requests
                        return new CompletableFuture<>();
                }
            }

            @Override
            public void close() {
            }
        }, mockBeamFnStateDelegator, RegisterAndProcessBundleOperationTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        // Ensure that the first time we start we send the register and process bundle requests
        Assert.assertThat(requests, Matchers.empty());
        operation.start();
        Assert.assertEquals(requests.get(0), BeamFnApi.InstructionRequest.newBuilder().setInstructionId("777").setRegister(RegisterAndProcessBundleOperationTest.REGISTER_REQUEST).build());
        Assert.assertEquals(requests.get(1), BeamFnApi.InstructionRequest.newBuilder().setInstructionId("778").setProcessBundle(ProcessBundleRequest.newBuilder().setProcessBundleDescriptorReference("555")).build());
        operation.finish();
        // Ensure on restart that we only send the process bundle request
        operation.start();
        Assert.assertEquals(requests.get(2), BeamFnApi.InstructionRequest.newBuilder().setInstructionId("779").setProcessBundle(ProcessBundleRequest.newBuilder().setProcessBundleDescriptorReference("555")).build());
        operation.finish();
    }

    @Test
    public void testTentativeUserMetrics() throws Exception {
        IdGenerator idGenerator = makeIdGeneratorStartingFrom(777L);
        CountDownLatch processBundleLatch = new CountDownLatch(1);
        final String stepName = "fakeStepNameWithUserMetrics";
        final String namespace = "sdk/whatever";
        final String name = "someCounter";
        final long counterValue = 42;
        final BeamFnApi.Metrics.User.MetricName metricName = MetricName.newBuilder().setNamespace(namespace).setName(name).build();
        InstructionRequestHandler instructionRequestHandler = new InstructionRequestHandler() {
            @Override
            public CompletionStage<InstructionResponse> handle(InstructionRequest request) {
                switch (request.getRequestCase()) {
                    case REGISTER :
                        return CompletableFuture.completedFuture(responseFor(request).build());
                    case PROCESS_BUNDLE :
                        return MoreFutures.supplyAsync(() -> {
                            processBundleLatch.await();
                            return responseFor(request).setProcessBundle(BeamFnApi.ProcessBundleResponse.getDefaultInstance()).build();
                        });
                    case PROCESS_BUNDLE_PROGRESS :
                        return CompletableFuture.completedFuture(responseFor(request).setProcessBundleProgress(ProcessBundleProgressResponse.newBuilder().setMetrics(BeamFnApi.Metrics.newBuilder().putPtransforms(stepName, PTransform.newBuilder().addUser(BeamFnApi.Metrics.User.newBuilder().setMetricName(metricName).setCounterData(CounterData.newBuilder().setValue(counterValue))).build()))).build());
                    default :
                        // block forever
                        return new CompletableFuture<>();
                }
            }

            @Override
            public void close() {
            }
        };
        RegisterAndProcessBundleOperation operation = new RegisterAndProcessBundleOperation(idGenerator, instructionRequestHandler, mockBeamFnStateDelegator, RegisterAndProcessBundleOperationTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        operation.start();
        BeamFnApi.Metrics metrics = MoreFutures.get(operation.getProcessBundleProgress()).getMetrics();
        Assert.assertThat(metrics.getPtransformsOrThrow(stepName).getUserCount(), Matchers.equalTo(1));
        BeamFnApi.Metrics.User userMetric = metrics.getPtransformsOrThrow(stepName).getUser(0);
        Assert.assertThat(userMetric.getMetricName(), Matchers.equalTo(metricName));
        Assert.assertThat(userMetric.getCounterData().getValue(), Matchers.equalTo(counterValue));
        processBundleLatch.countDown();
        operation.finish();
    }

    @Test
    public void testFinalUserMetrics() throws Exception {
        List<BeamFnApi.InstructionRequest> requests = new ArrayList<>();
        IdGenerator idGenerator = makeIdGeneratorStartingFrom(777L);
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch processBundleLatch = new CountDownLatch(1);
        final String stepName = "fakeStepNameWithUserMetrics";
        final String namespace = "sdk/whatever";
        final String name = "someCounter";
        final long counterValue = 42;
        final long finalCounterValue = 77;
        final BeamFnApi.Metrics.User.MetricName metricName = MetricName.newBuilder().setNamespace(namespace).setName(name).build();
        InstructionRequestHandler instructionRequestHandler = new InstructionRequestHandler() {
            @Override
            public CompletionStage<InstructionResponse> handle(InstructionRequest request) {
                switch (request.getRequestCase()) {
                    case REGISTER :
                        return CompletableFuture.completedFuture(responseFor(request).build());
                    case PROCESS_BUNDLE :
                        return MoreFutures.supplyAsync(() -> {
                            processBundleLatch.await();
                            return responseFor(request).setProcessBundle(BeamFnApi.ProcessBundleResponse.newBuilder().setMetrics(BeamFnApi.Metrics.newBuilder().putPtransforms(stepName, BeamFnApi.Metrics.PTransform.newBuilder().addUser(BeamFnApi.Metrics.User.newBuilder().setMetricName(metricName).setCounterData(BeamFnApi.Metrics.User.CounterData.newBuilder().setValue(finalCounterValue))).build()))).build();
                        });
                    case PROCESS_BUNDLE_PROGRESS :
                        return CompletableFuture.completedFuture(responseFor(request).setProcessBundleProgress(ProcessBundleProgressResponse.newBuilder().setMetrics(BeamFnApi.Metrics.newBuilder().putPtransforms(stepName, PTransform.newBuilder().addUser(BeamFnApi.Metrics.User.newBuilder().setMetricName(metricName).setCounterData(CounterData.newBuilder().setValue(counterValue))).build()))).build());
                    default :
                        // block forever
                        return new CompletableFuture<>();
                }
            }

            @Override
            public void close() {
            }
        };
        RegisterAndProcessBundleOperation operation = new RegisterAndProcessBundleOperation(idGenerator, instructionRequestHandler, mockBeamFnStateDelegator, RegisterAndProcessBundleOperationTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        operation.start();
        // Force some intermediate metrics to test crosstalk is not introduced
        BeamFnApi.Metrics metrics = MoreFutures.get(operation.getProcessBundleProgress()).getMetrics();
        BeamFnApi.Metrics.User userMetric = metrics.getPtransformsOrThrow(stepName).getUser(0);
        Assert.assertThat(userMetric.getMetricName(), Matchers.equalTo(metricName));
        Assert.assertThat(userMetric.getCounterData().getValue(), Matchers.not(Matchers.equalTo(finalCounterValue)));
        processBundleLatch.countDown();
        operation.finish();
        metrics = MoreFutures.get(operation.getFinalMetrics());
        userMetric = metrics.getPtransformsOrThrow(stepName).getUser(0);
        Assert.assertThat(userMetric.getMetricName(), Matchers.equalTo(metricName));
        Assert.assertThat(userMetric.getCounterData().getValue(), Matchers.equalTo(finalCounterValue));
    }

    @Test
    public void testProcessingBundleBlocksOnFinish() throws Exception {
        List<BeamFnApi.InstructionRequest> requests = new ArrayList<>();
        IdGenerator idGenerator = makeIdGeneratorStartingFrom(777L);
        ExecutorService executorService = Executors.newCachedThreadPool();
        RegisterAndProcessBundleOperation operation = new RegisterAndProcessBundleOperation(idGenerator, new InstructionRequestHandler() {
            @Override
            public CompletionStage<InstructionResponse> handle(InstructionRequest request) {
                requests.add(request);
                switch (request.getRequestCase()) {
                    case REGISTER :
                        return CompletableFuture.completedFuture(InstructionResponse.newBuilder().setInstructionId(request.getInstructionId()).build());
                    case PROCESS_BUNDLE :
                        CompletableFuture<InstructionResponse> responseFuture = new CompletableFuture<>();
                        executorService.submit(() -> {
                            // Purposefully sleep simulating SDK harness doing work
                            Thread.sleep(100);
                            responseFuture.complete(InstructionResponse.newBuilder().setInstructionId(request.getInstructionId()).setProcessBundle(ProcessBundleResponse.getDefaultInstance()).build());
                            completeFuture(request, responseFuture);
                            return null;
                        });
                        return responseFuture;
                    default :
                        // Anything else hangs; nothing else should be blocking
                        return new CompletableFuture<>();
                }
            }

            @Override
            public void close() {
            }
        }, mockBeamFnStateDelegator, RegisterAndProcessBundleOperationTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        operation.start();
        // This method blocks till the requests are completed
        operation.finish();
        // Ensure that the messages were received
        Assert.assertEquals(requests.get(0), BeamFnApi.InstructionRequest.newBuilder().setInstructionId("777").setRegister(RegisterAndProcessBundleOperationTest.REGISTER_REQUEST).build());
        Assert.assertEquals(requests.get(1), BeamFnApi.InstructionRequest.newBuilder().setInstructionId("778").setProcessBundle(ProcessBundleRequest.newBuilder().setProcessBundleDescriptorReference("555")).build());
    }

    @Test
    public void testProcessingBundleHandlesUserStateRequests() throws Exception {
        IdGenerator idGenerator = makeIdGeneratorStartingFrom(777L);
        ExecutorService executorService = Executors.newCachedThreadPool();
        InMemoryStateInternals<ByteString> stateInternals = InMemoryStateInternals.forKey(EMPTY);
        DataflowStepContext mockStepContext = Mockito.mock(DataflowStepContext.class);
        DataflowStepContext mockUserStepContext = Mockito.mock(DataflowStepContext.class);
        Mockito.when(mockStepContext.namespacedToUser()).thenReturn(mockUserStepContext);
        Mockito.when(mockUserStepContext.stateInternals()).thenReturn(stateInternals);
        InstructionRequestHandler instructionRequestHandler = new InstructionRequestHandler() {
            @Override
            public CompletionStage<InstructionResponse> handle(InstructionRequest request) {
                switch (request.getRequestCase()) {
                    case REGISTER :
                        return CompletableFuture.completedFuture(responseFor(request).build());
                    case PROCESS_BUNDLE :
                        return MoreFutures.supplyAsync(() -> {
                            StateRequest partialRequest = StateRequest.newBuilder().setStateKey(StateKey.newBuilder().setBagUserState(StateKey.BagUserState.newBuilder().setPtransformId("testPTransformId").setWindow(ByteString.EMPTY).setUserStateId("testUserStateId"))).buildPartial();
                            StateRequest get = partialRequest.toBuilder().setGet(StateGetRequest.getDefaultInstance()).build();
                            StateRequest clear = partialRequest.toBuilder().setClear(StateClearRequest.getDefaultInstance()).build();
                            StateRequest append = partialRequest.toBuilder().setAppend(StateAppendRequest.newBuilder().setData(ByteString.copyFromUtf8("ABC"))).build();
                            StateRequestHandler stateHandler = stateHandlerCaptor.getValue();
                            StateResponse.Builder getWhenEmptyResponse = MoreFutures.get(stateHandler.handle(get));
                            assertEquals(ByteString.EMPTY, getWhenEmptyResponse.getGet().getData());
                            StateResponse.Builder appendWhenEmptyResponse = MoreFutures.get(stateHandler.handle(append));
                            assertNotNull(appendWhenEmptyResponse);
                            StateResponse.Builder appendWhenEmptyResponse2 = MoreFutures.get(stateHandler.handle(append));
                            assertNotNull(appendWhenEmptyResponse2);
                            StateResponse.Builder getWhenHasValueResponse = MoreFutures.get(stateHandler.handle(get));
                            assertEquals(ByteString.copyFromUtf8("ABC").concat(ByteString.copyFromUtf8("ABC")), getWhenHasValueResponse.getGet().getData());
                            StateResponse.Builder clearResponse = MoreFutures.get(stateHandler.handle(clear));
                            assertNotNull(clearResponse);
                            return responseFor(request).setProcessBundle(BeamFnApi.ProcessBundleResponse.getDefaultInstance()).build();
                        });
                    default :
                        // block forever
                        return new CompletableFuture<>();
                }
            }

            @Override
            public void close() {
            }
        };
        RegisterAndProcessBundleOperation operation = new RegisterAndProcessBundleOperation(idGenerator, instructionRequestHandler, mockBeamFnStateDelegator, RegisterAndProcessBundleOperationTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of("testPTransformId", mockStepContext), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        operation.start();
        Mockito.verify(mockBeamFnStateDelegator).registerForProcessBundleInstructionId(ArgumentMatchers.eq("778"), stateHandlerCaptor.capture());
        // This method blocks till the requests are completed
        operation.finish();
        // Ensure that the number of reigstrations matches the number of deregistrations
        Assert.assertEquals(stateServiceRegisterCounter.get(), stateServiceDeregisterCounter.get());
        Assert.assertEquals(0, stateServiceAbortCounter.get());
    }

    @Test
    public void testProcessingBundleHandlesMultimapSideInputRequests() throws Exception {
        IdGenerator idGenerator = makeIdGeneratorStartingFrom(777L);
        ExecutorService executorService = Executors.newCachedThreadPool();
        DataflowStepContext mockStepContext = Mockito.mock(DataflowStepContext.class);
        DataflowStepContext mockUserStepContext = Mockito.mock(DataflowStepContext.class);
        Mockito.when(mockStepContext.namespacedToUser()).thenReturn(mockUserStepContext);
        CountDownLatch waitForStateHandler = new CountDownLatch(1);
        // Issues state calls to the Runner after a process bundle request is sent.
        InstructionRequestHandler fakeClient = new InstructionRequestHandler() {
            @Override
            public CompletionStage<InstructionResponse> handle(InstructionRequest request) {
                switch (request.getRequestCase()) {
                    case REGISTER :
                        return CompletableFuture.completedFuture(responseFor(request).build());
                    case PROCESS_BUNDLE :
                        return MoreFutures.supplyAsync(() -> {
                            StateKey getKey = StateKey.newBuilder().setMultimapSideInput(StateKey.MultimapSideInput.newBuilder().setPtransformId("testPTransformId").setSideInputId("testSideInputId").setWindow(ByteString.copyFrom(CoderUtils.encodeToByteArray(GlobalWindow.Coder.INSTANCE, GlobalWindow.INSTANCE))).setKey(ByteString.copyFrom(CoderUtils.encodeToByteArray(ByteArrayCoder.of(), "ABC".getBytes(StandardCharsets.UTF_8), Coder.Context.NESTED)))).build();
                            StateRequest getRequest = StateRequest.newBuilder().setStateKey(getKey).setGet(StateGetRequest.getDefaultInstance()).build();
                            waitForStateHandler.await();
                            StateRequestHandler stateHandler = stateHandlerCaptor.getValue();
                            StateResponse.Builder getResponse = MoreFutures.get(stateHandler.handle(getRequest));
                            assertEquals(encodeAndConcat(Arrays.asList("X", "Y", "Z"), StringUtf8Coder.of()), getResponse.getGet().getData());
                            return responseFor(request).setProcessBundle(BeamFnApi.ProcessBundleResponse.getDefaultInstance()).build();
                        });
                    default :
                        // block forever on other request types
                        return new CompletableFuture<>();
                }
            }

            @Override
            public void close() {
            }
        };
        SideInputReader fakeSideInputReader = new SideInputReader() {
            @Nullable
            @Override
            public <T> T get(PCollectionView<T> view, BoundedWindow window) {
                Assert.assertEquals(INSTANCE, window);
                Assert.assertEquals("testSideInputId", view.getTagInternal().getId());
                return ((T) (InMemoryMultimapSideInputView.fromIterable(ByteArrayCoder.of(), ImmutableList.of(KV.of("ABC".getBytes(StandardCharsets.UTF_8), "X"), KV.of("ABC".getBytes(StandardCharsets.UTF_8), "Y"), KV.of("ABC".getBytes(StandardCharsets.UTF_8), "Z")))));
            }

            @Override
            public <T> boolean contains(PCollectionView<T> view) {
                return "testSideInputId".equals(view.getTagInternal().getId());
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
        RegisterAndProcessBundleOperation operation = new RegisterAndProcessBundleOperation(idGenerator, fakeClient, mockBeamFnStateDelegator, RegisterAndProcessBundleOperationTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of("testPTransformId", mockStepContext), ImmutableMap.of("testPTransformId", fakeSideInputReader), ImmutableTable.of("testPTransformId", "testSideInputId", DataflowPortabilityPCollectionView.with(new org.apache.beam.sdk.values.TupleTag("testSideInputId"), FullWindowedValueCoder.of(KvCoder.of(ByteArrayCoder.of(), StringUtf8Coder.of()), GlobalWindow.Coder.INSTANCE))), mockContext);
        operation.start();
        Mockito.verify(mockBeamFnStateDelegator).registerForProcessBundleInstructionId(ArgumentMatchers.eq("778"), stateHandlerCaptor.capture());
        waitForStateHandler.countDown();
        // This method blocks till the requests are completed
        operation.finish();
        // Ensure that the number of reigstrations matches the number of deregistrations
        Assert.assertEquals(stateServiceRegisterCounter.get(), stateServiceDeregisterCounter.get());
        Assert.assertEquals(0, stateServiceAbortCounter.get());
    }

    @Test
    public void testAbortCancelsAndCleansUpDuringRegister() throws Exception {
        IdGenerator idGenerator = makeIdGeneratorStartingFrom(777L);
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch waitForAbortToComplete = new CountDownLatch(1);
        AtomicReference<ThrowingRunnable> abortReference = new AtomicReference<>();
        RegisterAndProcessBundleOperation operation = new RegisterAndProcessBundleOperation(idGenerator, new InstructionRequestHandler() {
            @Override
            public CompletionStage<InstructionResponse> handle(InstructionRequest request) {
                CompletableFuture<InstructionResponse> responseFuture = new CompletableFuture<>();
                if ((request.getRequestCase()) == (RequestCase.PROCESS_BUNDLE)) {
                    executorService.submit(((Callable<Void>) (() -> {
                        abortReference.get().run();
                        waitForAbortToComplete.countDown();
                        return null;
                    })));
                } else {
                    completeFuture(request, responseFuture);
                }
                return responseFuture;
            }

            @Override
            public void close() {
            }
        }, mockBeamFnStateDelegator, RegisterAndProcessBundleOperationTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        abortReference.set(operation::abort);
        operation.start();
        waitForAbortToComplete.await();
        // Ensure that the number of registrations matches the number of aborts
        Assert.assertEquals(stateServiceRegisterCounter.get(), stateServiceAbortCounter.get());
        Assert.assertEquals(0, stateServiceDeregisterCounter.get());
    }

    @Test
    public void testAbortCancelsAndCleansUpDuringProcessBundle() throws Exception {
        IdGenerator idGenerator = makeIdGeneratorStartingFrom(777L);
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch waitForAbortToComplete = new CountDownLatch(1);
        AtomicReference<ThrowingRunnable> abortReference = new AtomicReference<>();
        RegisterAndProcessBundleOperation operation = new RegisterAndProcessBundleOperation(idGenerator, new InstructionRequestHandler() {
            @Override
            public CompletionStage<InstructionResponse> handle(InstructionRequest request) {
                CompletableFuture<InstructionResponse> responseFuture = new CompletableFuture<>();
                if ((request.getRequestCase()) == (RequestCase.PROCESS_BUNDLE)) {
                    executorService.submit(((Callable<Void>) (() -> {
                        abortReference.get().run();
                        waitForAbortToComplete.countDown();
                        return null;
                    })));
                } else {
                    completeFuture(request, responseFuture);
                }
                return responseFuture;
            }

            @Override
            public void close() {
            }
        }, mockBeamFnStateDelegator, RegisterAndProcessBundleOperationTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        abortReference.set(operation::abort);
        operation.start();
        waitForAbortToComplete.await();
        // Ensure that the number of registrations matches the number of aborts
        Assert.assertEquals(stateServiceRegisterCounter.get(), stateServiceAbortCounter.get());
        Assert.assertEquals(0, stateServiceDeregisterCounter.get());
    }

    @Test
    public void testGetProcessBundleProgressReturnsDefaultInstanceIfNoBundleIdCached() throws Exception {
        InstructionRequestHandler mockInstructionRequestHandler = Mockito.mock(InstructionRequestHandler.class);
        RegisterAndProcessBundleOperation operation = new RegisterAndProcessBundleOperation(IdGenerators.decrementingLongs(), mockInstructionRequestHandler, mockBeamFnStateDelegator, RegisterAndProcessBundleOperationTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        Assert.assertEquals(org.apache.beam.model.fnexecution.v1.BeamFnApi.ProcessBundleProgressResponse.getDefaultInstance(), MoreFutures.get(operation.getProcessBundleProgress()));
    }

    @Test
    public void testGetProcessBundleProgressFetchesProgressResponseWhenBundleIdCached() throws Exception {
        InstructionRequestHandler mockInstructionRequestHandler = Mockito.mock(InstructionRequestHandler.class);
        RegisterAndProcessBundleOperation operation = new RegisterAndProcessBundleOperation(IdGenerators.decrementingLongs(), mockInstructionRequestHandler, mockBeamFnStateDelegator, RegisterAndProcessBundleOperationTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        operation.getProcessBundleInstructionId();// this generates and caches bundleId

        org.apache.beam.model.fnexecution.v1.BeamFnApi.ProcessBundleProgressResponse expectedResult = org.apache.beam.model.fnexecution.v1.BeamFnApi.ProcessBundleProgressResponse.newBuilder().build();
        InstructionResponse instructionResponse = InstructionResponse.newBuilder().setProcessBundleProgress(expectedResult).build();
        CompletableFuture resultFuture = CompletableFuture.completedFuture(instructionResponse);
        Mockito.when(mockInstructionRequestHandler.handle(ArgumentMatchers.any())).thenReturn(resultFuture);
        final org.apache.beam.model.fnexecution.v1.BeamFnApi.ProcessBundleProgressResponse result = MoreFutures.get(operation.getProcessBundleProgress());
        Assert.assertSame("Return value from mockInstructionRequestHandler", expectedResult, result);
    }
}

