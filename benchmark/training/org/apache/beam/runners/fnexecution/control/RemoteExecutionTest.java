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


import BoundedWindow.TIMESTAMP_MIN_VALUE;
import Coder.Context.NESTED;
import DoFn.FinishBundle;
import SimpleMonitoringInfoBuilder.ELEMENT_COUNT_URN;
import SimpleMonitoringInfoBuilder.FINISH_BUNDLE_MSECS_URN;
import SimpleMonitoringInfoBuilder.PROCESS_BUNDLE_MSECS_URN;
import SimpleMonitoringInfoBuilder.START_BUNDLE_MSECS_URN;
import TimeDomain.EVENT_TIME;
import TimeDomain.PROCESSING_TIME;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.ProcessBundleProgressResponse;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.ProcessBundleResponse;
import org.apache.beam.model.fnexecution.v1.BeamFnApi.Target;
import org.apache.beam.model.pipeline.v1.MetricsApi.MonitoringInfo;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.runners.core.construction.PipelineTranslation;
import org.apache.beam.runners.core.construction.graph.ExecutableStage;
import org.apache.beam.runners.core.construction.graph.FusedPipeline;
import org.apache.beam.runners.core.construction.graph.GreedyPipelineFuser;
import org.apache.beam.runners.core.metrics.MonitoringInfoMatchers;
import org.apache.beam.runners.core.metrics.SimpleMonitoringInfoBuilder;
import org.apache.beam.runners.fnexecution.GrpcFnServer;
import org.apache.beam.runners.fnexecution.control.ProcessBundleDescriptors.ExecutableProcessBundleDescriptor;
import org.apache.beam.runners.fnexecution.control.SdkHarnessClient.ActiveBundle;
import org.apache.beam.runners.fnexecution.control.SdkHarnessClient.BundleProcessor;
import org.apache.beam.runners.fnexecution.data.GrpcDataService;
import org.apache.beam.runners.fnexecution.logging.GrpcLoggingService;
import org.apache.beam.runners.fnexecution.state.GrpcStateService;
import org.apache.beam.runners.fnexecution.state.StateRequestHandler;
import org.apache.beam.runners.fnexecution.state.StateRequestHandlers;
import org.apache.beam.runners.fnexecution.state.StateRequestHandlers.BagUserStateHandler;
import org.apache.beam.runners.fnexecution.state.StateRequestHandlers.BagUserStateHandlerFactory;
import org.apache.beam.runners.fnexecution.state.StateRequestHandlers.SideInputHandler;
import org.apache.beam.runners.fnexecution.state.StateRequestHandlers.SideInputHandlerFactory;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.BigEndianLongCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderException;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.fn.data.FnDataReceiver;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.state.BagState;
import org.apache.beam.sdk.state.ReadableState;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.Timer;
import org.apache.beam.sdk.state.TimerSpec;
import org.apache.beam.sdk.state.TimerSpecs;
import org.apache.beam.sdk.testing.ResetDateTimeProvider;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.Impulse;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.ParDo.SingleOutput;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Optional;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterators;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.joda.time.DateTimeUtils;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the execution of a pipeline from specification time to executing a single fused stage,
 * going through pipeline fusion.
 */
@RunWith(JUnit4.class)
public class RemoteExecutionTest implements Serializable {
    @Rule
    public transient ResetDateTimeProvider resetDateTimeProvider = new ResetDateTimeProvider();

    private static final Logger LOG = LoggerFactory.getLogger(RemoteExecutionTest.class);

    private transient GrpcFnServer<FnApiControlClientPoolService> controlServer;

    private transient GrpcFnServer<GrpcDataService> dataServer;

    private transient GrpcFnServer<GrpcStateService> stateServer;

    private transient GrpcFnServer<GrpcLoggingService> loggingServer;

    private transient GrpcStateService stateDelegator;

    private transient SdkHarnessClient controlClient;

    private transient ExecutorService serverExecutor;

    private transient ExecutorService sdkHarnessExecutor;

    private transient Future<?> sdkHarnessExecutorFuture;

    @Test
    public void testExecution() throws Exception {
        Pipeline p = Pipeline.create();
        // Force the output to be materialized
        // Use some unknown coders
        p.apply("impulse", Impulse.create()).apply("create", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], String>() {
            @ProcessElement
            public void process(ProcessContext ctxt) {
                ctxt.output("zero");
                ctxt.output("one");
                ctxt.output("two");
            }
        })).apply("len", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<String, Long>() {
            @ProcessElement
            public void process(ProcessContext ctxt) {
                ctxt.output(((long) (ctxt.element().length())));
            }
        })).apply("addKeys", WithKeys.of("foo")).setCoder(KvCoder.of(StringUtf8Coder.of(), BigEndianLongCoder.of())).apply("gbk", GroupByKey.create());
        RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(p);
        FusedPipeline fused = GreedyPipelineFuser.fuse(pipelineProto);
        checkState(((fused.getFusedStages().size()) == 1), "Expected exactly one fused stage");
        ExecutableStage stage = fused.getFusedStages().iterator().next();
        ExecutableProcessBundleDescriptor descriptor = ProcessBundleDescriptors.fromExecutableStage("my_stage", stage, dataServer.getApiServiceDescriptor());
        BundleProcessor processor = controlClient.getProcessor(descriptor.getProcessBundleDescriptor(), descriptor.getRemoteInputDestinations());
        Map<Target, ? super Coder<WindowedValue<?>>> outputTargets = descriptor.getOutputTargetCoders();
        Map<Target, Collection<? super WindowedValue<?>>> outputValues = new HashMap<>();
        Map<Target, RemoteOutputReceiver<?>> outputReceivers = new HashMap<>();
        for (Map.Entry<Target, ? super Coder<WindowedValue<?>>> targetCoder : outputTargets.entrySet()) {
            List<? super WindowedValue<?>> outputContents = Collections.synchronizedList(new ArrayList<>());
            outputValues.put(targetCoder.getKey(), outputContents);
            outputReceivers.put(targetCoder.getKey(), RemoteOutputReceiver.of(((Coder) (targetCoder.getValue())), ((FnDataReceiver<? super WindowedValue<?>>) (outputContents::add))));
        }
        // The impulse example
        try (ActiveBundle bundle = processor.newBundle(outputReceivers, BundleProgressHandler.ignored())) {
            Iterables.getOnlyElement(bundle.getInputReceivers().values()).accept(WindowedValue.valueInGlobalWindow(new byte[0]));
        }
        for (Collection<? super WindowedValue<?>> windowedValues : outputValues.values()) {
            Assert.assertThat(windowedValues, Matchers.containsInAnyOrder(WindowedValue.valueInGlobalWindow(kvBytes("foo", 4)), WindowedValue.valueInGlobalWindow(kvBytes("foo", 3)), WindowedValue.valueInGlobalWindow(kvBytes("foo", 3))));
        }
    }

    @Test
    public void testBundleProcessorThrowsExecutionExceptionWhenUserCodeThrows() throws Exception {
        Pipeline p = Pipeline.create();
        p.apply("impulse", Impulse.create()).apply("create", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], KV<String, String>>() {
            @ProcessElement
            public void process(ProcessContext ctxt) throws Exception {
                String element = CoderUtils.decodeFromByteArray(StringUtf8Coder.of(), ctxt.element());
                if (element.equals("X")) {
                    throw new Exception("testBundleExecutionFailure");
                }
                ctxt.output(KV.of(element, element));
            }
        })).apply("gbk", GroupByKey.create());
        RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(p);
        FusedPipeline fused = GreedyPipelineFuser.fuse(pipelineProto);
        checkState(((fused.getFusedStages().size()) == 1), "Expected exactly one fused stage");
        ExecutableStage stage = fused.getFusedStages().iterator().next();
        ExecutableProcessBundleDescriptor descriptor = ProcessBundleDescriptors.fromExecutableStage("my_stage", stage, dataServer.getApiServiceDescriptor());
        BundleProcessor processor = controlClient.getProcessor(descriptor.getProcessBundleDescriptor(), descriptor.getRemoteInputDestinations());
        Map<Target, ? super Coder<WindowedValue<?>>> outputTargets = descriptor.getOutputTargetCoders();
        Map<Target, Collection<? super WindowedValue<?>>> outputValues = new HashMap<>();
        Map<Target, RemoteOutputReceiver<?>> outputReceivers = new HashMap<>();
        for (Map.Entry<Target, ? super Coder<WindowedValue<?>>> targetCoder : outputTargets.entrySet()) {
            List<? super WindowedValue<?>> outputContents = Collections.synchronizedList(new ArrayList<>());
            outputValues.put(targetCoder.getKey(), outputContents);
            outputReceivers.put(targetCoder.getKey(), RemoteOutputReceiver.of(((Coder) (targetCoder.getValue())), ((FnDataReceiver<? super WindowedValue<?>>) (outputContents::add))));
        }
        try (ActiveBundle bundle = processor.newBundle(outputReceivers, BundleProgressHandler.ignored())) {
            Iterables.getOnlyElement(bundle.getInputReceivers().values()).accept(WindowedValue.valueInGlobalWindow(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "Y")));
        }
        try {
            try (ActiveBundle bundle = processor.newBundle(outputReceivers, BundleProgressHandler.ignored())) {
                Iterables.getOnlyElement(bundle.getInputReceivers().values()).accept(WindowedValue.valueInGlobalWindow(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "X")));
            }
            // Fail the test if we reach this point and never threw the exception.
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(e.getMessage().contains("testBundleExecutionFailure"));
        }
        try (ActiveBundle bundle = processor.newBundle(outputReceivers, BundleProgressHandler.ignored())) {
            Iterables.getOnlyElement(bundle.getInputReceivers().values()).accept(WindowedValue.valueInGlobalWindow(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "Z")));
        }
        for (Collection<? super WindowedValue<?>> windowedValues : outputValues.values()) {
            Assert.assertThat(windowedValues, Matchers.containsInAnyOrder(WindowedValue.valueInGlobalWindow(kvBytes("Y", "Y")), WindowedValue.valueInGlobalWindow(kvBytes("Z", "Z"))));
        }
    }

    @Test
    public void testExecutionWithSideInput() throws Exception {
        Pipeline p = Pipeline.create();
        PCollection<String> input = p.apply("impulse", Impulse.create()).apply("create", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], String>() {
            @ProcessElement
            public void process(ProcessContext ctxt) {
                ctxt.output("zero");
                ctxt.output("one");
                ctxt.output("two");
            }
        })).setCoder(StringUtf8Coder.of());
        PCollectionView<Iterable<String>> view = input.apply("createSideInput", View.asIterable());
        // Force the output to be materialized
        input.apply("readSideInput", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<String, KV<String, String>>() {
            @ProcessElement
            public void processElement(ProcessContext context) {
                for (String value : context.sideInput(view)) {
                    context.output(KV.of(context.element(), value));
                }
            }
        }).withSideInputs(view)).setCoder(KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of())).apply("gbk", GroupByKey.create());
        RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(p);
        FusedPipeline fused = GreedyPipelineFuser.fuse(pipelineProto);
        Optional<ExecutableStage> optionalStage = Iterables.tryFind(fused.getFusedStages(), (ExecutableStage stage) -> !(stage.getSideInputs().isEmpty()));
        checkState(optionalStage.isPresent(), "Expected a stage with side inputs.");
        ExecutableStage stage = optionalStage.get();
        ExecutableProcessBundleDescriptor descriptor = ProcessBundleDescriptors.fromExecutableStage("test_stage", stage, dataServer.getApiServiceDescriptor(), stateServer.getApiServiceDescriptor());
        BundleProcessor processor = controlClient.getProcessor(descriptor.getProcessBundleDescriptor(), descriptor.getRemoteInputDestinations(), stateDelegator);
        Map<Target, Coder<WindowedValue<?>>> outputTargets = descriptor.getOutputTargetCoders();
        Map<Target, Collection<WindowedValue<?>>> outputValues = new HashMap<>();
        Map<Target, RemoteOutputReceiver<?>> outputReceivers = new HashMap<>();
        for (Map.Entry<Target, Coder<WindowedValue<?>>> targetCoder : outputTargets.entrySet()) {
            List<WindowedValue<?>> outputContents = Collections.synchronizedList(new ArrayList<>());
            outputValues.put(targetCoder.getKey(), outputContents);
            outputReceivers.put(targetCoder.getKey(), RemoteOutputReceiver.of(targetCoder.getValue(), outputContents::add));
        }
        Iterable<byte[]> sideInputData = Arrays.asList(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "A"), CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "B"), CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "C"));
        StateRequestHandler stateRequestHandler = StateRequestHandlers.forSideInputHandlerFactory(descriptor.getSideInputSpecs(), new SideInputHandlerFactory() {
            @Override
            public <T, V, W extends BoundedWindow> SideInputHandler<V, W> forSideInput(String pTransformId, String sideInputId, RunnerApi.FunctionSpec accessPattern, Coder<T> elementCoder, Coder<W> windowCoder) {
                return new SideInputHandler<V, W>() {
                    @Override
                    public Iterable<V> get(byte[] key, W window) {
                        return ((Iterable) (sideInputData));
                    }

                    @Override
                    public Coder<V> resultCoder() {
                        return getValueCoder();
                    }
                };
            }
        });
        BundleProgressHandler progressHandler = BundleProgressHandler.ignored();
        try (ActiveBundle bundle = processor.newBundle(outputReceivers, stateRequestHandler, progressHandler)) {
            Iterables.getOnlyElement(bundle.getInputReceivers().values()).accept(WindowedValue.valueInGlobalWindow(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "X")));
            Iterables.getOnlyElement(bundle.getInputReceivers().values()).accept(WindowedValue.valueInGlobalWindow(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "Y")));
        }
        for (Collection<WindowedValue<?>> windowedValues : outputValues.values()) {
            Assert.assertThat(windowedValues, Matchers.containsInAnyOrder(WindowedValue.valueInGlobalWindow(kvBytes("X", "A")), WindowedValue.valueInGlobalWindow(kvBytes("X", "B")), WindowedValue.valueInGlobalWindow(kvBytes("X", "C")), WindowedValue.valueInGlobalWindow(kvBytes("Y", "A")), WindowedValue.valueInGlobalWindow(kvBytes("Y", "B")), WindowedValue.valueInGlobalWindow(kvBytes("Y", "C"))));
        }
    }

    @Test
    public void testMetrics() throws Exception {
        final String processUserCounterName = "processUserCounter";
        final String startUserCounterName = "startUserCounter";
        final String finishUserCounterName = "finishUserCounter";
        Pipeline p = Pipeline.create();
        // TODO(BEAM-6597): Remove sleeps in this test after collecting MonitoringInfos in
        // ProcessBundleProgressResponses. Use CountDownLatches to wait in start, finish and process
        // functions and open the latches when valid metrics are seen in the progress responses.
        PCollection<String> input = p.apply("impulse", Impulse.create()).apply("create", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], String>() {
            private boolean emitted = false;

            private Counter startCounter = Metrics.counter(RemoteExecutionTest.class, startUserCounterName);

            @StartBundle
            public void startBundle() throws InterruptedException {
                Thread.sleep(1000);
                startCounter.inc(10);
            }

            @SuppressWarnings("unused")
            @ProcessElement
            public void processElement(ProcessContext ctxt) throws InterruptedException {
                // TODO(BEAM-6467): Impulse is producing two elements instead of one.
                // So add this check to only emit these three elemenets.
                if (!(emitted)) {
                    ctxt.output("zero");
                    ctxt.output("one");
                    ctxt.output("two");
                    Thread.sleep(1000);
                    Metrics.counter(RemoteExecutionTest.class, processUserCounterName).inc();
                }
                emitted = true;
            }

            @org.apache.beam.sdk.transforms.DoFn.FinishBundle
            public void finishBundle() throws InterruptedException {
                Thread.sleep(1000);
                Metrics.counter(RemoteExecutionTest.class, finishUserCounterName).inc(100);
            }
        })).setCoder(StringUtf8Coder.of());
        SingleOutput<String, String> pardo = ParDo.of(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void process(ProcessContext ctxt) {
                // Output the element twice to keep unique numbers in asserts, 6 output elements.
                ctxt.output(ctxt.element());
                ctxt.output(ctxt.element());
            }
        });
        input.apply("processA", pardo).setCoder(StringUtf8Coder.of());
        input.apply("processB", pardo).setCoder(StringUtf8Coder.of());
        RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(p);
        FusedPipeline fused = GreedyPipelineFuser.fuse(pipelineProto);
        Optional<ExecutableStage> optionalStage = Iterables.tryFind(fused.getFusedStages(), (ExecutableStage stage) -> true);
        checkState(optionalStage.isPresent(), "Expected a stage with side inputs.");
        ExecutableStage stage = optionalStage.get();
        ExecutableProcessBundleDescriptor descriptor = ProcessBundleDescriptors.fromExecutableStage("test_stage", stage, dataServer.getApiServiceDescriptor(), stateServer.getApiServiceDescriptor());
        BundleProcessor processor = controlClient.getProcessor(descriptor.getProcessBundleDescriptor(), descriptor.getRemoteInputDestinations(), stateDelegator);
        Map<Target, Coder<WindowedValue<?>>> outputTargets = descriptor.getOutputTargetCoders();
        Map<Target, Collection<WindowedValue<?>>> outputValues = new HashMap<>();
        Map<Target, RemoteOutputReceiver<?>> outputReceivers = new HashMap<>();
        for (Map.Entry<Target, Coder<WindowedValue<?>>> targetCoder : outputTargets.entrySet()) {
            List<WindowedValue<?>> outputContents = Collections.synchronizedList(new ArrayList<>());
            outputValues.put(targetCoder.getKey(), outputContents);
            outputReceivers.put(targetCoder.getKey(), RemoteOutputReceiver.of(targetCoder.getValue(), outputContents::add));
        }
        Iterable<byte[]> sideInputData = Arrays.asList(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "A"), CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "B"), CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "C"));
        StateRequestHandler stateRequestHandler = StateRequestHandlers.forSideInputHandlerFactory(descriptor.getSideInputSpecs(), new SideInputHandlerFactory() {
            @Override
            public <T, V, W extends BoundedWindow> SideInputHandler<V, W> forSideInput(String pTransformId, String sideInputId, RunnerApi.FunctionSpec accessPattern, Coder<T> elementCoder, Coder<W> windowCoder) {
                return new SideInputHandler<V, W>() {
                    @Override
                    public Iterable<V> get(byte[] key, W window) {
                        return ((Iterable) (sideInputData));
                    }

                    @Override
                    public Coder<V> resultCoder() {
                        return getValueCoder();
                    }
                };
            }
        });
        String testPTransformId = "create/ParMultiDo(Anonymous)";
        BundleProgressHandler progressHandler = new BundleProgressHandler() {
            @Override
            public void onProgress(ProcessBundleProgressResponse progress) {
            }

            @Override
            public void onCompleted(ProcessBundleResponse response) {
                List<Matcher<MonitoringInfo>> matchers = new ArrayList<Matcher<MonitoringInfo>>();
                SimpleMonitoringInfoBuilder builder = new SimpleMonitoringInfoBuilder();
                builder.setUrnForUserMetric(RemoteExecutionTest.class.getName(), processUserCounterName);
                builder.setPTransformLabel("create/ParMultiDo(Anonymous)");
                builder.setInt64Value(1);
                matchers.add(MonitoringInfoMatchers.matchSetFields(builder.build()));
                builder = new SimpleMonitoringInfoBuilder();
                builder.setUrnForUserMetric(RemoteExecutionTest.class.getName(), startUserCounterName);
                builder.setPTransformLabel("create/ParMultiDo(Anonymous)");
                builder.setInt64Value(10);
                matchers.add(MonitoringInfoMatchers.matchSetFields(builder.build()));
                builder = new SimpleMonitoringInfoBuilder();
                builder.setUrnForUserMetric(RemoteExecutionTest.class.getName(), finishUserCounterName);
                builder.setPTransformLabel("create/ParMultiDo(Anonymous)");
                builder.setInt64Value(100);
                matchers.add(MonitoringInfoMatchers.matchSetFields(builder.build()));
                // The element counter should be counted only once for the pcollection.
                // So there should be only two elements.
                builder = new SimpleMonitoringInfoBuilder();
                builder.setUrn(ELEMENT_COUNT_URN);
                builder.setPCollectionLabel("impulse.out");
                builder.setInt64Value(2);
                matchers.add(MonitoringInfoMatchers.matchSetFields(builder.build()));
                builder = new SimpleMonitoringInfoBuilder();
                builder.setUrn(ELEMENT_COUNT_URN);
                builder.setPCollectionLabel("create/ParMultiDo(Anonymous).output");
                builder.setInt64Value(3);
                matchers.add(MonitoringInfoMatchers.matchSetFields(builder.build()));
                // Verify that the element count is not double counted if two PCollections consume it.
                builder = new SimpleMonitoringInfoBuilder();
                builder.setUrn(ELEMENT_COUNT_URN);
                builder.setPCollectionLabel("processA/ParMultiDo(Anonymous).output");
                builder.setInt64Value(6);
                matchers.add(MonitoringInfoMatchers.matchSetFields(builder.build()));
                builder = new SimpleMonitoringInfoBuilder();
                builder.setUrn(ELEMENT_COUNT_URN);
                builder.setPCollectionLabel("processB/ParMultiDo(Anonymous).output");
                builder.setInt64Value(6);
                matchers.add(MonitoringInfoMatchers.matchSetFields(builder.build()));
                // Check for execution time metrics for the testPTransformId
                builder = new SimpleMonitoringInfoBuilder();
                builder.setUrn(START_BUNDLE_MSECS_URN);
                builder.setInt64TypeUrn();
                builder.setPTransformLabel(testPTransformId);
                matchers.add(Matchers.allOf(MonitoringInfoMatchers.matchSetFields(builder.build()), MonitoringInfoMatchers.valueGreaterThan(0)));
                // Check for execution time metrics for the testPTransformId
                builder = new SimpleMonitoringInfoBuilder();
                builder.setUrn(PROCESS_BUNDLE_MSECS_URN);
                builder.setInt64TypeUrn();
                builder.setPTransformLabel(testPTransformId);
                matchers.add(Matchers.allOf(MonitoringInfoMatchers.matchSetFields(builder.build()), MonitoringInfoMatchers.valueGreaterThan(0)));
                builder = new SimpleMonitoringInfoBuilder();
                builder.setUrn(FINISH_BUNDLE_MSECS_URN);
                builder.setInt64TypeUrn();
                builder.setPTransformLabel(testPTransformId);
                matchers.add(Matchers.allOf(MonitoringInfoMatchers.matchSetFields(builder.build()), MonitoringInfoMatchers.valueGreaterThan(0)));
                for (Matcher<MonitoringInfo> matcher : matchers) {
                    Assert.assertThat(response.getMonitoringInfosList(), Matchers.hasItem(matcher));
                }
            }
        };
        try (ActiveBundle bundle = processor.newBundle(outputReceivers, stateRequestHandler, progressHandler)) {
            Iterables.getOnlyElement(bundle.getInputReceivers().values()).accept(WindowedValue.valueInGlobalWindow(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "X")));
            Iterables.getOnlyElement(bundle.getInputReceivers().values()).accept(WindowedValue.valueInGlobalWindow(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "Y")));
        }
    }

    @Test
    public void testExecutionWithUserState() throws Exception {
        Pipeline p = Pipeline.create();
        final String stateId = "foo";
        final String stateId2 = "foo2";
        // Force the output to be materialized
        p.apply("impulse", Impulse.create()).apply("create", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], KV<String, String>>() {
            @ProcessElement
            public void process(ProcessContext ctxt) {
            }
        })).setCoder(KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of())).apply("userState", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<KV<String, String>, KV<String, String>>() {
            @StateId(stateId)
            private final StateSpec<BagState<String>> bufferState = StateSpecs.bag(StringUtf8Coder.of());

            @StateId(stateId2)
            private final StateSpec<BagState<String>> bufferState2 = StateSpecs.bag(StringUtf8Coder.of());

            @ProcessElement
            public void processElement(@Element
            KV<String, String> element, @StateId(stateId)
            BagState<String> state, @StateId(stateId2)
            BagState<String> state2, OutputReceiver<KV<String, String>> r) {
                ReadableState<Boolean> isEmpty = state.isEmpty();
                for (String value : state.read()) {
                    r.output(KV.of(element.getKey(), value));
                }
                state.add(element.getValue());
                state2.clear();
            }
        })).apply("gbk", GroupByKey.create());
        RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(p);
        FusedPipeline fused = GreedyPipelineFuser.fuse(pipelineProto);
        Optional<ExecutableStage> optionalStage = Iterables.tryFind(fused.getFusedStages(), (ExecutableStage stage) -> !(stage.getUserStates().isEmpty()));
        checkState(optionalStage.isPresent(), "Expected a stage with user state.");
        ExecutableStage stage = optionalStage.get();
        ExecutableProcessBundleDescriptor descriptor = ProcessBundleDescriptors.fromExecutableStage("test_stage", stage, dataServer.getApiServiceDescriptor(), stateServer.getApiServiceDescriptor());
        BundleProcessor processor = controlClient.getProcessor(descriptor.getProcessBundleDescriptor(), descriptor.getRemoteInputDestinations(), stateDelegator);
        Map<Target, Coder<WindowedValue<?>>> outputTargets = descriptor.getOutputTargetCoders();
        Map<Target, Collection<WindowedValue<?>>> outputValues = new HashMap<>();
        Map<Target, RemoteOutputReceiver<?>> outputReceivers = new HashMap<>();
        for (Map.Entry<Target, Coder<WindowedValue<?>>> targetCoder : outputTargets.entrySet()) {
            List<WindowedValue<?>> outputContents = Collections.synchronizedList(new ArrayList<>());
            outputValues.put(targetCoder.getKey(), outputContents);
            outputReceivers.put(targetCoder.getKey(), RemoteOutputReceiver.of(targetCoder.getValue(), outputContents::add));
        }
        Map<String, List<ByteString>> userStateData = ImmutableMap.of(stateId, new ArrayList(Arrays.asList(ByteString.copyFrom(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "A", NESTED)), ByteString.copyFrom(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "B", NESTED)), ByteString.copyFrom(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "C", NESTED)))), stateId2, new ArrayList(Arrays.asList(ByteString.copyFrom(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "D", NESTED)))));
        StateRequestHandler stateRequestHandler = StateRequestHandlers.forBagUserStateHandlerFactory(descriptor, new BagUserStateHandlerFactory() {
            @Override
            public <K, V, W extends BoundedWindow> BagUserStateHandler<K, V, W> forUserState(String pTransformId, String userStateId, Coder<K> keyCoder, Coder<V> valueCoder, Coder<W> windowCoder) {
                return new BagUserStateHandler<K, V, W>() {
                    @Override
                    public Iterable<V> get(K key, W window) {
                        return ((Iterable) (userStateData.get(userStateId)));
                    }

                    @Override
                    public void append(K key, W window, Iterator<V> values) {
                        Iterators.addAll(userStateData.get(userStateId), ((Iterator) (values)));
                    }

                    @Override
                    public void clear(K key, W window) {
                        userStateData.get(userStateId).clear();
                    }
                };
            }
        });
        try (ActiveBundle bundle = processor.newBundle(outputReceivers, stateRequestHandler, BundleProgressHandler.ignored())) {
            Iterables.getOnlyElement(bundle.getInputReceivers().values()).accept(WindowedValue.valueInGlobalWindow(kvBytes("X", "Y")));
        }
        for (Collection<WindowedValue<?>> windowedValues : outputValues.values()) {
            Assert.assertThat(windowedValues, Matchers.containsInAnyOrder(WindowedValue.valueInGlobalWindow(kvBytes("X", "A")), WindowedValue.valueInGlobalWindow(kvBytes("X", "B")), WindowedValue.valueInGlobalWindow(kvBytes("X", "C"))));
        }
        Assert.assertThat(userStateData.get(stateId), IsIterableContainingInOrder.contains(ByteString.copyFrom(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "A", NESTED)), ByteString.copyFrom(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "B", NESTED)), ByteString.copyFrom(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "C", NESTED)), ByteString.copyFrom(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "Y", NESTED))));
        Assert.assertThat(userStateData.get(stateId2), IsEmptyIterable.emptyIterable());
    }

    @Test
    public void testExecutionWithTimer() throws Exception {
        Pipeline p = Pipeline.create();
        final String timerId = "foo";
        final String timerId2 = "foo2";
        // Force the output to be materialized
        p.apply("impulse", Impulse.create()).apply("create", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], KV<String, String>>() {
            @ProcessElement
            public void process(ProcessContext ctxt) {
            }
        })).setCoder(KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of())).apply("timer", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<KV<String, String>, KV<String, String>>() {
            @TimerId("event")
            private final TimerSpec eventTimerSpec = TimerSpecs.timer(EVENT_TIME);

            @TimerId("processing")
            private final TimerSpec processingTimerSpec = TimerSpecs.timer(PROCESSING_TIME);

            @ProcessElement
            public void processElement(ProcessContext context, @TimerId("event")
            Timer eventTimeTimer, @TimerId("processing")
            Timer processingTimeTimer) {
                context.output(KV.of(("main" + (context.element().getKey())), ""));
                eventTimeTimer.set(context.timestamp().plus(1L));
                processingTimeTimer.offset(Duration.millis(2L));
                processingTimeTimer.setRelative();
            }

            @OnTimer("event")
            public void eventTimer(OnTimerContext context, @TimerId("event")
            Timer eventTimeTimer, @TimerId("processing")
            Timer processingTimeTimer) {
                context.output(KV.of("event", ""));
                eventTimeTimer.set(context.timestamp().plus(11L));
                processingTimeTimer.offset(Duration.millis(12L));
                processingTimeTimer.setRelative();
            }

            @OnTimer("processing")
            public void processingTimer(OnTimerContext context, @TimerId("event")
            Timer eventTimeTimer, @TimerId("processing")
            Timer processingTimeTimer) {
                context.output(KV.of("processing", ""));
                eventTimeTimer.set(context.timestamp().plus(21L));
                processingTimeTimer.offset(Duration.millis(22L));
                processingTimeTimer.setRelative();
            }
        })).apply("gbk", GroupByKey.create());
        RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(p);
        FusedPipeline fused = GreedyPipelineFuser.fuse(pipelineProto);
        Optional<ExecutableStage> optionalStage = Iterables.tryFind(fused.getFusedStages(), (ExecutableStage stage) -> !(stage.getTimers().isEmpty()));
        checkState(optionalStage.isPresent(), "Expected a stage with timers.");
        ExecutableStage stage = optionalStage.get();
        ExecutableProcessBundleDescriptor descriptor = ProcessBundleDescriptors.fromExecutableStage("test_stage", stage, dataServer.getApiServiceDescriptor(), stateServer.getApiServiceDescriptor());
        BundleProcessor processor = controlClient.getProcessor(descriptor.getProcessBundleDescriptor(), descriptor.getRemoteInputDestinations(), stateDelegator);
        Map<Target, Coder<WindowedValue<?>>> outputTargets = descriptor.getOutputTargetCoders();
        Map<Target, Collection<WindowedValue<?>>> outputValues = new HashMap<>();
        Map<Target, RemoteOutputReceiver<?>> outputReceivers = new HashMap<>();
        for (Map.Entry<Target, Coder<WindowedValue<?>>> targetCoder : outputTargets.entrySet()) {
            List<WindowedValue<?>> outputContents = Collections.synchronizedList(new ArrayList<>());
            outputValues.put(targetCoder.getKey(), outputContents);
            outputReceivers.put(targetCoder.getKey(), RemoteOutputReceiver.of(targetCoder.getValue(), outputContents::add));
        }
        String eventTimeInputPCollectionId = null;
        Target eventTimeOutputTarget = null;
        String processingTimeInputPCollectionId = null;
        Target processingTimeOutputTarget = null;
        for (Map<String, ProcessBundleDescriptors.TimerSpec> timerSpecs : descriptor.getTimerSpecs().values()) {
            for (ProcessBundleDescriptors.TimerSpec timerSpec : timerSpecs.values()) {
                if (EVENT_TIME.equals(timerSpec.getTimerSpec().getTimeDomain())) {
                    eventTimeInputPCollectionId = timerSpec.inputCollectionId();
                    eventTimeOutputTarget = timerSpec.outputTarget();
                } else
                    if (PROCESSING_TIME.equals(timerSpec.getTimerSpec().getTimeDomain())) {
                        processingTimeInputPCollectionId = timerSpec.inputCollectionId();
                        processingTimeOutputTarget = timerSpec.outputTarget();
                    } else {
                        Assert.fail(String.format("Unknown timer specification %s", timerSpec));
                    }

            }
        }
        // Set the current system time to a fixed value to get stable values for processing time timer
        // output.
        DateTimeUtils.setCurrentMillisFixed(TIMESTAMP_MIN_VALUE.getMillis());
        try (ActiveBundle bundle = processor.newBundle(outputReceivers, StateRequestHandler.unsupported(), BundleProgressHandler.ignored())) {
            bundle.getInputReceivers().get(stage.getInputPCollection().getId()).accept(WindowedValue.valueInGlobalWindow(kvBytes("X", "X")));
            bundle.getInputReceivers().get(eventTimeInputPCollectionId).accept(WindowedValue.valueInGlobalWindow(timerBytes("Y", 100L)));
            bundle.getInputReceivers().get(processingTimeInputPCollectionId).accept(WindowedValue.valueInGlobalWindow(timerBytes("Z", 200L)));
        }
        Set<Target> timerOutputTargets = ImmutableSet.of(eventTimeOutputTarget, processingTimeOutputTarget);
        Target mainOutputTarget = Iterables.getOnlyElement(Sets.difference(descriptor.getOutputTargetCoders().keySet(), timerOutputTargets));
        Assert.assertThat(outputValues.get(mainOutputTarget), Matchers.containsInAnyOrder(WindowedValue.valueInGlobalWindow(kvBytes("mainX", "")), WindowedValue.valueInGlobalWindow(kvBytes("event", "")), WindowedValue.valueInGlobalWindow(kvBytes("processing", ""))));
        Assert.assertThat(timerStructuralValues(outputValues.get(eventTimeOutputTarget)), Matchers.containsInAnyOrder(timerStructuralValue(WindowedValue.valueInGlobalWindow(timerBytes("X", 1L))), timerStructuralValue(WindowedValue.valueInGlobalWindow(timerBytes("Y", 11L))), timerStructuralValue(WindowedValue.valueInGlobalWindow(timerBytes("Z", 21L)))));
        Assert.assertThat(timerStructuralValues(outputValues.get(processingTimeOutputTarget)), Matchers.containsInAnyOrder(timerStructuralValue(WindowedValue.valueInGlobalWindow(timerBytes("X", 2L))), timerStructuralValue(WindowedValue.valueInGlobalWindow(timerBytes("Y", 12L))), timerStructuralValue(WindowedValue.valueInGlobalWindow(timerBytes("Z", 22L)))));
    }

    @Test
    public void testExecutionWithMultipleStages() throws Exception {
        Pipeline p = Pipeline.create();
        Function<String, PCollection<String>> pCollectionGenerator = ( suffix) -> p.apply(("impulse" + suffix), Impulse.create()).apply(("create" + suffix), ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], String>() {
            @ProcessElement
            public void process(ProcessContext c) {
                try {
                    c.output(CoderUtils.decodeFromByteArray(StringUtf8Coder.of(), c.element()));
                } catch (CoderException e) {
                    throw new RuntimeException(e);
                }
            }
        })).setCoder(StringUtf8Coder.of()).apply(ParDo.of(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void processElement(ProcessContext c) {
                c.output((("stream" + suffix) + (c.element())));
            }
        }));
        PCollection<String> input1 = pCollectionGenerator.apply("1");
        PCollection<String> input2 = pCollectionGenerator.apply("2");
        PCollection<String> outputMerged = PCollectionList.of(input1).and(input2).apply(Flatten.pCollections());
        outputMerged.apply("createKV", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<String, KV<String, String>>() {
            @ProcessElement
            public void process(ProcessContext c) {
                c.output(KV.of(c.element(), ""));
            }
        })).setCoder(KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of())).apply("gbk", GroupByKey.create());
        RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(p);
        FusedPipeline fused = GreedyPipelineFuser.fuse(pipelineProto);
        Set<ExecutableStage> stages = fused.getFusedStages();
        Assert.assertThat(stages.size(), Matchers.equalTo(2));
        List<WindowedValue<?>> outputValues = Collections.synchronizedList(new ArrayList<>());
        for (ExecutableStage stage : stages) {
            ExecutableProcessBundleDescriptor descriptor = ProcessBundleDescriptors.fromExecutableStage(stage.toString(), stage, dataServer.getApiServiceDescriptor(), stateServer.getApiServiceDescriptor());
            BundleProcessor processor = controlClient.getProcessor(descriptor.getProcessBundleDescriptor(), descriptor.getRemoteInputDestinations(), stateDelegator);
            Map<Target, Coder<WindowedValue<?>>> outputTargets = descriptor.getOutputTargetCoders();
            Map<Target, RemoteOutputReceiver<?>> outputReceivers = new HashMap<>();
            for (Map.Entry<Target, Coder<WindowedValue<?>>> targetCoder : outputTargets.entrySet()) {
                outputReceivers.putIfAbsent(targetCoder.getKey(), RemoteOutputReceiver.of(targetCoder.getValue(), outputValues::add));
            }
            try (ActiveBundle bundle = processor.newBundle(outputReceivers, StateRequestHandler.unsupported(), BundleProgressHandler.ignored())) {
                bundle.getInputReceivers().get(stage.getInputPCollection().getId()).accept(WindowedValue.valueInGlobalWindow(CoderUtils.encodeToByteArray(StringUtf8Coder.of(), "X")));
            }
        }
        Assert.assertThat(outputValues, Matchers.containsInAnyOrder(WindowedValue.valueInGlobalWindow(kvBytes("stream1X", "")), WindowedValue.valueInGlobalWindow(kvBytes("stream2X", ""))));
    }
}

