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


import BeamFnApi.ProcessBundleDescriptor;
import BeamFnApi.ProcessBundleProgressResponse;
import BeamFnApi.RegisterRequest;
import Metrics.PTransform;
import Metrics.PTransform.Measured;
import Metrics.PTransform.ProcessedElements;
import Metrics.User.CounterData;
import Metrics.User.MetricName;
import ReadOperation.DEFAULT_PROGRESS_UPDATE_PERIOD_MS;
import RemoteGrpcPortRead.URN;
import RunnerApi.FunctionSpec;
import StateDelegator.Registration;
import com.google.api.services.dataflow.model.CounterUpdate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.InstructionRequest;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.InstructionResponse;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.Metrics;
import org.apache.beam.model.pipeline.v1.MetricsApi.Metric;
import org.apache.beam.model.pipeline.v1.MetricsApi.MonitoringInfo;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.dataflow.worker.DataflowExecutionContext.DataflowStepContext;
import org.apache.beam.runners.dataflow.worker.fn.data.RemoteGrpcPortWriteOperation;
import org.apache.beam.runners.dataflow.worker.util.CounterHamcrestMatchers;
import org.apache.beam.runners.dataflow.worker.util.common.worker.OperationContext;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ReadOperation;
import org.apache.beam.runners.fnexecution.control.InstructionRequestHandler;
import org.apache.beam.runners.fnexecution.state.StateDelegator;
import org.apache.beam.sdk.fn.IdGenerators;
import org.apache.beam.sdk.util.MoreFutures;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableTable;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link BeamFnMapTaskExecutor}.
 */
@RunWith(JUnit4.class)
public class BeamFnMapTaskExecutorTest {
    @Mock
    private OperationContext mockContext;

    @Mock
    private StateDelegator mockBeamFnStateDelegator;

    @Mock
    private Registration mockStateDelegatorRegistration;

    @Mock
    private ExecutionStateTracker executionStateTracker;

    // Hacks to cause progress tracking to be done
    @Mock
    private ReadOperation readOperation;

    @Mock
    private RemoteGrpcPortWriteOperation grpcPortWriteOperation;

    private static final String GRPC_READ_ID = "fake_grpc_read_id";

    private static final String FAKE_OUTPUT_NAME = "fake_output_name";

    private static final String FAKE_OUTPUT_PCOLLECTION_ID = "fake_pcollection_id";

    private static final PTransform FAKE_ELEMENT_COUNT_METRICS = PTransform.newBuilder().setProcessedElements(ProcessedElements.newBuilder().setMeasured(Measured.getDefaultInstance())).build();

    private static final RegisterRequest REGISTER_REQUEST = RegisterRequest.newBuilder().addProcessBundleDescriptor(ProcessBundleDescriptor.newBuilder().setId("555").putTransforms(BeamFnMapTaskExecutorTest.GRPC_READ_ID, RunnerApi.PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(URN)).putOutputs(BeamFnMapTaskExecutorTest.FAKE_OUTPUT_NAME, BeamFnMapTaskExecutorTest.FAKE_OUTPUT_PCOLLECTION_ID).build())).build();

    @Test(timeout = (ReadOperation.DEFAULT_PROGRESS_UPDATE_PERIOD_MS) * 10)
    public void testTentativeUserMetrics() throws Exception {
        final String stepName = "fakeStepNameWithUserMetrics";
        final String namespace = "sdk/whatever";
        final String name = "someCounter";
        final int counterValue = 42;
        final CountDownLatch progressSentLatch = new CountDownLatch(1);
        final CountDownLatch processBundleLatch = new CountDownLatch(1);
        final Metrics.User.MetricName metricName = MetricName.newBuilder().setNamespace(namespace).setName(name).build();
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
                        progressSentLatch.countDown();
                        return CompletableFuture.completedFuture(responseFor(request).setProcessBundleProgress(ProcessBundleProgressResponse.newBuilder().setMetrics(Metrics.newBuilder().putPtransforms(BeamFnMapTaskExecutorTest.GRPC_READ_ID, BeamFnMapTaskExecutorTest.FAKE_ELEMENT_COUNT_METRICS).putPtransforms(stepName, PTransform.newBuilder().addUser(Metrics.User.newBuilder().setMetricName(metricName).setCounterData(CounterData.newBuilder().setValue(counterValue))).build()))).build());
                    default :
                        // block forever
                        return new CompletableFuture<>();
                }
            }

            @Override
            public void close() {
            }
        };
        RegisterAndProcessBundleOperation processOperation = new RegisterAndProcessBundleOperation(IdGenerators.decrementingLongs(), instructionRequestHandler, mockBeamFnStateDelegator, BeamFnMapTaskExecutorTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        BeamFnMapTaskExecutor mapTaskExecutor = BeamFnMapTaskExecutor.forOperations(ImmutableList.of(readOperation, grpcPortWriteOperation, processOperation), executionStateTracker);
        // Launch the BeamFnMapTaskExecutor and wait until we are sure there has been one
        // tentative update
        CompletionStage<Void> doneFuture = MoreFutures.runAsync(mapTaskExecutor::execute);
        progressSentLatch.await();
        Iterable<CounterUpdate> metricsCounterUpdates = Collections.emptyList();
        while ((Iterables.size(metricsCounterUpdates)) == 0) {
            Thread.sleep(DEFAULT_PROGRESS_UPDATE_PERIOD_MS);
            metricsCounterUpdates = mapTaskExecutor.extractMetricUpdates();
        } 
        Assert.assertThat(metricsCounterUpdates, Matchers.contains(new CounterHamcrestMatchers.CounterUpdateIntegerValueMatcher(counterValue)));
        // Now let it finish and clean up
        processBundleLatch.countDown();
        MoreFutures.get(doneFuture);
    }

    /**
     * Tests that successive metric updates overwrite the previous.
     */
    @Test(timeout = (ReadOperation.DEFAULT_PROGRESS_UPDATE_PERIOD_MS) * 10)
    public void testTentativeUserMetricsOverwrite() throws Exception {
        final String stepName = "fakeStepNameWithUserMetrics";
        final String namespace = "sdk/whatever";
        final String name = "someCounter";
        final int firstCounterValue = 42;
        final int secondCounterValue = 77;
        final CountDownLatch progressSentTwiceLatch = new CountDownLatch(2);
        final CountDownLatch processBundleLatch = new CountDownLatch(1);
        final Metrics.User.MetricName metricName = MetricName.newBuilder().setNamespace(namespace).setName(name).build();
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
                        progressSentTwiceLatch.countDown();
                        return CompletableFuture.completedFuture(responseFor(request).setProcessBundleProgress(ProcessBundleProgressResponse.newBuilder().setMetrics(Metrics.newBuilder().putPtransforms(BeamFnMapTaskExecutorTest.GRPC_READ_ID, BeamFnMapTaskExecutorTest.FAKE_ELEMENT_COUNT_METRICS).putPtransforms(stepName, PTransform.newBuilder().addUser(Metrics.User.newBuilder().setMetricName(metricName).setCounterData(CounterData.newBuilder().setValue(((progressSentTwiceLatch.getCount()) > 0 ? firstCounterValue : secondCounterValue)))).build()))).build());
                    default :
                        // block forever
                        return new CompletableFuture<>();
                }
            }

            @Override
            public void close() {
            }
        };
        Mockito.when(grpcPortWriteOperation.processedElementsConsumer()).thenReturn(( elementsConsumed) -> {
        });
        RegisterAndProcessBundleOperation processOperation = new RegisterAndProcessBundleOperation(IdGenerators.decrementingLongs(), instructionRequestHandler, mockBeamFnStateDelegator, BeamFnMapTaskExecutorTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        BeamFnMapTaskExecutor mapTaskExecutor = BeamFnMapTaskExecutor.forOperations(ImmutableList.of(readOperation, grpcPortWriteOperation, processOperation), executionStateTracker);
        // Launch the BeamFnMapTaskExecutor and wait until we are sure there has been one
        // tentative update
        CompletionStage<Void> doneFuture = MoreFutures.runAsync(mapTaskExecutor::execute);
        progressSentTwiceLatch.await();
        Iterable<CounterUpdate> metricsCounterUpdates = Collections.emptyList();
        while ((Iterables.size(metricsCounterUpdates)) == 0) {
            Thread.sleep(DEFAULT_PROGRESS_UPDATE_PERIOD_MS);
            metricsCounterUpdates = mapTaskExecutor.extractMetricUpdates();
        } 
        Assert.assertThat(metricsCounterUpdates, Matchers.contains(new CounterHamcrestMatchers.CounterUpdateIntegerValueMatcher(secondCounterValue)));
        // Now let it finish and clean up
        processBundleLatch.countDown();
        MoreFutures.get(doneFuture);
    }

    @Test(timeout = (ReadOperation.DEFAULT_PROGRESS_UPDATE_PERIOD_MS) * 10)
    public void testFinalUserMetricsDeprecated() throws Exception {
        final String stepName = "fakeStepNameWithUserMetrics";
        final String namespace = "sdk/whatever";
        final String name = "someCounter";
        final int counterValue = 42;
        final int finalCounterValue = 77;
        final CountDownLatch progressSentLatch = new CountDownLatch(1);
        final CountDownLatch processBundleLatch = new CountDownLatch(1);
        final Metrics.User.MetricName metricName = MetricName.newBuilder().setNamespace(namespace).setName(name).build();
        InstructionRequestHandler instructionRequestHandler = new InstructionRequestHandler() {
            @Override
            public CompletionStage<InstructionResponse> handle(InstructionRequest request) {
                switch (request.getRequestCase()) {
                    case REGISTER :
                        return CompletableFuture.completedFuture(responseFor(request).build());
                    case PROCESS_BUNDLE :
                        return MoreFutures.supplyAsync(() -> {
                            processBundleLatch.await();
                            return responseFor(request).setProcessBundle(BeamFnApi.ProcessBundleResponse.newBuilder().setMetrics(Metrics.newBuilder().putPtransforms(BeamFnMapTaskExecutorTest.GRPC_READ_ID, BeamFnMapTaskExecutorTest.FAKE_ELEMENT_COUNT_METRICS).putPtransforms(stepName, Metrics.PTransform.newBuilder().addUser(Metrics.User.newBuilder().setMetricName(metricName).setCounterData(Metrics.User.CounterData.newBuilder().setValue(finalCounterValue))).build()))).build();
                        });
                    case PROCESS_BUNDLE_PROGRESS :
                        progressSentLatch.countDown();
                        return CompletableFuture.completedFuture(responseFor(request).setProcessBundleProgress(ProcessBundleProgressResponse.newBuilder().setMetrics(Metrics.newBuilder().putPtransforms(BeamFnMapTaskExecutorTest.GRPC_READ_ID, BeamFnMapTaskExecutorTest.FAKE_ELEMENT_COUNT_METRICS).putPtransforms(stepName, PTransform.newBuilder().addUser(Metrics.User.newBuilder().setMetricName(metricName).setCounterData(CounterData.newBuilder().setValue(counterValue))).build()))).build());
                    default :
                        // block forever
                        return new CompletableFuture<>();
                }
            }

            @Override
            public void close() {
            }
        };
        RegisterAndProcessBundleOperation processOperation = new RegisterAndProcessBundleOperation(IdGenerators.decrementingLongs(), instructionRequestHandler, mockBeamFnStateDelegator, BeamFnMapTaskExecutorTest.REGISTER_REQUEST, ImmutableMap.of(), ImmutableMap.of(), ImmutableMap.of(), ImmutableTable.of(), mockContext);
        BeamFnMapTaskExecutor mapTaskExecutor = BeamFnMapTaskExecutor.forOperations(ImmutableList.of(readOperation, grpcPortWriteOperation, processOperation), executionStateTracker);
        // Launch the BeamFnMapTaskExecutor and wait until we are sure there has been one
        // tentative update
        CompletionStage<Void> doneFuture = MoreFutures.runAsync(mapTaskExecutor::execute);
        progressSentLatch.await();
        // TODO: add ability to wait for tentative progress update
        Iterable<CounterUpdate> metricsCounterUpdates = Collections.emptyList();
        while ((Iterables.size(metricsCounterUpdates)) == 0) {
            Thread.sleep(DEFAULT_PROGRESS_UPDATE_PERIOD_MS);
            metricsCounterUpdates = mapTaskExecutor.extractMetricUpdates();
        } 
        // Get the final metrics
        processBundleLatch.countDown();
        MoreFutures.get(doneFuture);
        metricsCounterUpdates = mapTaskExecutor.extractMetricUpdates();
        Assert.assertThat(Iterables.size(metricsCounterUpdates), Matchers.equalTo(1));
        Assert.assertThat(metricsCounterUpdates, Matchers.contains(new CounterHamcrestMatchers.CounterUpdateIntegerValueMatcher(finalCounterValue)));
    }

    @Test(timeout = (ReadOperation.DEFAULT_PROGRESS_UPDATE_PERIOD_MS) * 60)
    public void testExtractCounterUpdatesReturnsValidProgressTrackerCounterUpdatesIfPresent() throws Exception {
        final String stepName = "fakeStepNameWithUserMetrics";
        final String namespace = "sdk/whatever";
        final String name = "someCounter";
        final int counterValue = 42;
        final int finalCounterValue = 77;
        final CountDownLatch progressSentLatch = new CountDownLatch(1);
        final CountDownLatch processBundleLatch = new CountDownLatch(1);
        final Metrics.User.MetricName metricName = MetricName.newBuilder().setNamespace(namespace).setName(name).build();
        final Metrics deprecatedMetrics = Metrics.newBuilder().putPtransforms(BeamFnMapTaskExecutorTest.GRPC_READ_ID, BeamFnMapTaskExecutorTest.FAKE_ELEMENT_COUNT_METRICS).putPtransforms(stepName, PTransform.newBuilder().addUser(Metrics.User.newBuilder().setMetricName(metricName).setCounterData(CounterData.newBuilder().setValue(finalCounterValue))).build()).build();
        final int expectedCounterValue = 5;
        final MonitoringInfo expectedMonitoringInfo = MonitoringInfo.newBuilder().setUrn("beam:metric:user:ExpectedCounter").setType("beam:metrics:sum_int_64").putLabels("PTRANSFORM", "ExpectedPTransform").setMetric(Metric.newBuilder().setCounterData(MetricsApi.CounterData.newBuilder().setInt64Value(expectedCounterValue).build()).build()).build();
        InstructionRequestHandler instructionRequestHandler = new InstructionRequestHandler() {
            @Override
            public CompletionStage<InstructionResponse> handle(InstructionRequest request) {
                switch (request.getRequestCase()) {
                    case REGISTER :
                        return CompletableFuture.completedFuture(responseFor(request).build());
                    case PROCESS_BUNDLE :
                        return MoreFutures.supplyAsync(() -> {
                            processBundleLatch.await();
                            return responseFor(request).setProcessBundle(BeamFnApi.ProcessBundleResponse.newBuilder().setMetrics(deprecatedMetrics).addMonitoringInfos(expectedMonitoringInfo)).build();
                        });
                    case PROCESS_BUNDLE_PROGRESS :
                        progressSentLatch.countDown();
                        return CompletableFuture.completedFuture(responseFor(request).setProcessBundleProgress(ProcessBundleProgressResponse.newBuilder().setMetrics(deprecatedMetrics).addMonitoringInfos(expectedMonitoringInfo)).build());
                    default :
                        throw new RuntimeException("Reached unexpected code path");
                }
            }

            @Override
            public void close() {
            }
        };
        Map<String, DataflowStepContext> stepContextMap = new HashMap<>();
        stepContextMap.put("ExpectedPTransform", generateDataflowStepContext("Expected"));
        RegisterAndProcessBundleOperation processOperation = new RegisterAndProcessBundleOperation(IdGenerators.decrementingLongs(), instructionRequestHandler, mockBeamFnStateDelegator, BeamFnMapTaskExecutorTest.REGISTER_REQUEST, ImmutableMap.of(), stepContextMap, ImmutableMap.of(), ImmutableTable.of(), mockContext);
        BeamFnMapTaskExecutor mapTaskExecutor = BeamFnMapTaskExecutor.forOperations(ImmutableList.of(readOperation, grpcPortWriteOperation, processOperation), executionStateTracker);
        // Launch the BeamFnMapTaskExecutor and wait until we are sure there has been one
        // tentative update
        CompletionStage<Void> doneFuture = MoreFutures.runAsync(mapTaskExecutor::execute);
        progressSentLatch.await();
        Iterable<CounterUpdate> metricsCounterUpdates = Collections.emptyList();
        while ((Iterables.size(metricsCounterUpdates)) == 0) {
            Thread.sleep(DEFAULT_PROGRESS_UPDATE_PERIOD_MS);
            metricsCounterUpdates = mapTaskExecutor.extractMetricUpdates();
        } 
        // Get the final metrics
        processBundleLatch.countDown();
        MoreFutures.get(doneFuture);
        metricsCounterUpdates = mapTaskExecutor.extractMetricUpdates();
        Assert.assertThat(Iterables.size(metricsCounterUpdates), Matchers.equalTo(1));
        CounterUpdate resultCounter = metricsCounterUpdates.iterator().next();
        Assert.assertTrue(new CounterHamcrestMatchers.CounterUpdateIntegerValueMatcher(expectedCounterValue).matches(resultCounter));
        Assert.assertEquals("ExpectedCounter", resultCounter.getStructuredNameAndMetadata().getName().getName());
    }
}

