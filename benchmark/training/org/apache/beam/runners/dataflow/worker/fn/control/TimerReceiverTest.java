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


import BeamFnApi.Target;
import BoundedWindow.TIMESTAMP_MAX_VALUE;
import BoundedWindow.TIMESTAMP_MIN_VALUE;
import DataflowExecutionContext.DataflowStepContext;
import GlobalWindow.INSTANCE;
import ImmutableMap.Builder;
import PaneInfo.NO_FIRING;
import ProcessBundleDescriptors.ExecutableProcessBundleDescriptor;
import SdkHarnessClient.BundleProcessor;
import TimeDomain.EVENT_TIME;
import TimerInternals.TimerData;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import javax.annotation.Nullable;
import org.apache.beam.model.fnexecution.v1.BeamFnApi;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.runners.core.InMemoryTimerInternals;
import org.apache.beam.runners.core.StateInternals;
import org.apache.beam.runners.core.TimerInternals;
import org.apache.beam.runners.core.construction.PipelineTranslation;
import org.apache.beam.runners.core.construction.graph.ExecutableStage;
import org.apache.beam.runners.core.construction.graph.FusedPipeline;
import org.apache.beam.runners.core.construction.graph.GreedyPipelineFuser;
import org.apache.beam.runners.dataflow.worker.DataflowExecutionContext;
import org.apache.beam.runners.dataflow.worker.counters.NameContext;
import org.apache.beam.runners.fnexecution.GrpcFnServer;
import org.apache.beam.runners.fnexecution.data.GrpcDataService;
import org.apache.beam.runners.fnexecution.logging.GrpcLoggingService;
import org.apache.beam.runners.fnexecution.state.GrpcStateService;
import org.apache.beam.runners.fnexecution.state.StateRequestHandler;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.fn.data.FnDataReceiver;
import org.apache.beam.sdk.state.TimeDomain;
import org.apache.beam.sdk.state.Timer;
import org.apache.beam.sdk.state.TimerSpec;
import org.apache.beam.sdk.state.TimerSpecs;
import org.apache.beam.sdk.transforms.Impulse;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Optional;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class TimerReceiverTest implements Serializable {
    private transient GrpcFnServer<FnApiControlClientPoolService> controlServer;

    private transient GrpcFnServer<GrpcDataService> dataServer;

    private transient GrpcFnServer<GrpcLoggingService> loggingServer;

    private transient GrpcStateService stateDelegator;

    private transient SdkHarnessClient client;

    private transient ExecutorService sdkHarnessExecutor;

    private transient Future<?> sdkHarnessExecutorFuture;

    /* Tests that we can schedule a single timer to fire, and that it fires. */
    @Test
    public void testSingleTimerScheduling() throws Exception {
        final String timerId = "timerId";
        Pipeline p = Pipeline.create();
        PCollection<Integer> output = p.apply("impulse", Impulse.create()).apply("create", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], KV<String, Integer>>() {
            @ProcessElement
            public void process(ProcessContext ctxt) {
            }
        })).apply("timer", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Integer>() {
            @TimerId(timerId)
            private final TimerSpec spec = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void processElement(@TimerId(timerId)
            Timer timer, OutputReceiver<Integer> r) {
                timer.offset(Duration.standardSeconds(1)).setRelative();
            }

            @OnTimer(timerId)
            public void onTimer(@TimerId(timerId)
            Timer timer, TimeDomain timeDomain, OutputReceiver<Integer> r) {
                r.output(0);
            }
        }));
        RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(p);
        FusedPipeline fused = GreedyPipelineFuser.fuse(pipelineProto);
        Optional<ExecutableStage> optionalStage = Iterables.tryFind(fused.getFusedStages(), (ExecutableStage stage) -> !(stage.getTimers().isEmpty()));
        checkState(optionalStage.isPresent(), "Expected a stage with timers.");
        ExecutableStage stage = optionalStage.get();
        ProcessBundleDescriptors.ExecutableProcessBundleDescriptor descriptor = ProcessBundleDescriptors.fromExecutableStage("test_stage", stage, dataServer.getApiServiceDescriptor());
        TimerReceiver timerReceiver = Mockito.spy(new TimerReceiver(stage.getComponents(), TimerReceiverTest.buildDataflowStepContext(), TimerReceiverTest.buildStageBundleFactory(client, descriptor, stateDelegator)));
        Map<String, ProcessBundleDescriptors.TimerSpec> timerSpecMap = new HashMap<>();
        descriptor.getTimerSpecs().values().forEach(( transformTimerMap) -> {
            for (ProcessBundleDescriptors.TimerSpec timerSpec : transformTimerMap.values()) {
                timerSpecMap.put(timerSpec.timerId(), timerSpec);
            }
        });
        String timerOutputPCollection = timerSpecMap.get(timerId).outputCollectionId();
        String timerInputPCollection = timerSpecMap.get(timerId).inputCollectionId();
        // Arbitrary offset.
        long testTimerOffset = 123456;
        // Arbitrary key.
        Object timer = timerBytes("X", testTimerOffset);
        Object windowedTimer = WindowedValue.valueInGlobalWindow(timer);
        // Simulate the SDK Harness sending a timer element to the Runner Harness.
        Assert.assertTrue(timerReceiver.receive(timerOutputPCollection, windowedTimer));
        // Expect that we get a timer element when we finish.
        Object expected = WindowedValue.of(timer, TIMESTAMP_MIN_VALUE.plus(testTimerOffset), INSTANCE, NO_FIRING);
        Mockito.verify(timerReceiver, Mockito.never()).fireTimer(timerInputPCollection, ((WindowedValue<KV<Object, org.apache.beam.runners.core.construction.Timer>>) (expected)));
        // Simulate firing the timer. Expect that the fired timer is exactly the one we received
        // originally (with additional details).
        timerReceiver.finish();
        Mockito.verify(timerReceiver).fireTimer(timerInputPCollection, ((WindowedValue<KV<Object, org.apache.beam.runners.core.construction.Timer>>) (expected)));
    }

    /* Tests that we can schedule multiple timers to fire, and that they all fire. */
    @Test
    public void testMultiTimerScheduling() throws Exception {
        final String timerId1 = "timerId1";
        final String timerId2 = "timerId2";
        Pipeline p = Pipeline.create();
        PCollection<Integer> output = p.apply("impulse", Impulse.create()).apply("create", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], KV<String, Integer>>() {
            @ProcessElement
            public void process(ProcessContext ctxt) {
            }
        })).apply("timer", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Integer>() {
            @TimerId(timerId1)
            private final TimerSpec timer1 = TimerSpecs.timer(EVENT_TIME);

            @TimerId(timerId2)
            private final TimerSpec timer2 = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void processElement(@TimerId(timerId1)
            Timer timer1, @TimerId(timerId2)
            Timer timer2, OutputReceiver<Integer> r) {
                timer1.offset(Duration.standardSeconds(1)).setRelative();
                timer2.offset(Duration.standardSeconds(2)).setRelative();
            }

            @OnTimer(timerId1)
            public void onTimer1(@TimerId(timerId1)
            Timer timer, TimeDomain timeDomain, OutputReceiver<Integer> r) {
                r.output(1);
            }

            @OnTimer(timerId2)
            public void onTimer2(@TimerId(timerId2)
            Timer timer, TimeDomain timeDomain, OutputReceiver<Integer> r) {
                r.output(2);
            }
        }));
        RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(p);
        FusedPipeline fused = GreedyPipelineFuser.fuse(pipelineProto);
        Optional<ExecutableStage> optionalStage = Iterables.tryFind(fused.getFusedStages(), (ExecutableStage stage) -> !(stage.getTimers().isEmpty()));
        checkState(optionalStage.isPresent(), "Expected a stage with timers.");
        ExecutableStage stage = optionalStage.get();
        ProcessBundleDescriptors.ExecutableProcessBundleDescriptor descriptor = ProcessBundleDescriptors.fromExecutableStage("test_stage", stage, dataServer.getApiServiceDescriptor());
        TimerReceiver timerReceiver = Mockito.spy(new TimerReceiver(stage.getComponents(), TimerReceiverTest.buildDataflowStepContext(), TimerReceiverTest.buildStageBundleFactory(client, descriptor, stateDelegator)));
        Map<String, ProcessBundleDescriptors.TimerSpec> timerSpecMap = new HashMap<>();
        descriptor.getTimerSpecs().values().forEach(( transformTimerMap) -> {
            for (ProcessBundleDescriptors.TimerSpec timerSpec : transformTimerMap.values()) {
                timerSpecMap.put(timerSpec.timerId(), timerSpec);
            }
        });
        // Arbitrary offset.
        long testTimerOffset = 123456;
        // Arbitrary key.
        Object timer1 = timerBytes("X", testTimerOffset);
        Object windowedTimer1 = WindowedValue.valueInGlobalWindow(timer1);
        Object timer2 = timerBytes("Y", testTimerOffset);
        Object windowedTimer2 = WindowedValue.valueInGlobalWindow(timer2);
        // Simulate the SDK Harness sending a timer element to the Runner Harness.
        Assert.assertTrue(timerReceiver.receive(timerSpecMap.get(timerId1).outputCollectionId(), windowedTimer1));
        Assert.assertTrue(timerReceiver.receive(timerSpecMap.get(timerId2).outputCollectionId(), windowedTimer2));
        // Expect that we get a timer element when we finish.
        Object expectedTimer1 = WindowedValue.of(timer1, TIMESTAMP_MIN_VALUE.plus(testTimerOffset), INSTANCE, NO_FIRING);
        Object expectedTimer2 = WindowedValue.of(timer2, TIMESTAMP_MIN_VALUE.plus(testTimerOffset), INSTANCE, NO_FIRING);
        Mockito.verify(timerReceiver, Mockito.never()).fireTimer(timerSpecMap.get(timerId1).inputCollectionId(), ((WindowedValue<KV<Object, org.apache.beam.runners.core.construction.Timer>>) (expectedTimer1)));
        Mockito.verify(timerReceiver, Mockito.never()).fireTimer(timerSpecMap.get(timerId2).inputCollectionId(), ((WindowedValue<KV<Object, org.apache.beam.runners.core.construction.Timer>>) (expectedTimer2)));
        // Simulate firing the timer. Expect that the fired timer is exactly the one we received
        // originally (with additional details).
        timerReceiver.finish();
        Mockito.verify(timerReceiver).fireTimer(timerSpecMap.get(timerId1).inputCollectionId(), ((WindowedValue<KV<Object, org.apache.beam.runners.core.construction.Timer>>) (expectedTimer1)));
        Mockito.verify(timerReceiver).fireTimer(timerSpecMap.get(timerId2).inputCollectionId(), ((WindowedValue<KV<Object, org.apache.beam.runners.core.construction.Timer>>) (expectedTimer2)));
    }

    private static class SimpleStageBundleFactory implements StageBundleFactory {
        private final SdkHarnessClient client;

        private final BundleProcessor processor;

        private final ExecutableProcessBundleDescriptor processBundleDescriptor;

        SimpleStageBundleFactory(SdkHarnessClient client, SdkHarnessClient.BundleProcessor processor, ProcessBundleDescriptors.ExecutableProcessBundleDescriptor processBundleDescriptor) {
            this.client = client;
            this.processor = processor;
            this.processBundleDescriptor = processBundleDescriptor;
        }

        @Override
        public RemoteBundle getBundle(OutputReceiverFactory outputReceiverFactory, StateRequestHandler stateRequestHandler, BundleProgressHandler progressHandler) throws Exception {
            Builder<BeamFnApi.Target, RemoteOutputReceiver<?>> outputReceivers = ImmutableMap.builder();
            for (Map.Entry<BeamFnApi.Target, Coder<WindowedValue<?>>> targetCoder : processBundleDescriptor.getOutputTargetCoders().entrySet()) {
                BeamFnApi.Target target = targetCoder.getKey();
                Coder<WindowedValue<?>> coder = targetCoder.getValue();
                String bundleOutputPCollection = Iterables.getOnlyElement(processBundleDescriptor.getProcessBundleDescriptor().getTransformsOrThrow(target.getPrimitiveTransformReference()).getInputsMap().values());
                FnDataReceiver<WindowedValue<?>> outputReceiver = outputReceiverFactory.create(bundleOutputPCollection);
                outputReceivers.put(target, RemoteOutputReceiver.of(coder, outputReceiver));
            }
            return processor.newBundle(outputReceivers.build(), stateRequestHandler, progressHandler);
        }

        @Override
        public ExecutableProcessBundleDescriptor getProcessBundleDescriptor() {
            return processBundleDescriptor;
        }

        @Override
        public void close() throws Exception {
        }
    }

    private static class TestStepContext extends DataflowExecutionContext.DataflowStepContext {
        private InMemoryTimerInternals timerInternals;

        public TestStepContext(NameContext nameContext) {
            super(nameContext);
            timerInternals = new InMemoryTimerInternals();
        }

        @Nullable
        @Override
        public <W extends BoundedWindow> TimerData getNextFiredTimer(Coder<W> windowCoder) {
            try {
                timerInternals.advanceInputWatermark(TIMESTAMP_MAX_VALUE);
            } catch (Exception e) {
                throw new IllegalStateException("Exception thrown advancing watermark", e);
            }
            return timerInternals.removeNextEventTimer();
        }

        @Override
        public DataflowStepContext namespacedToUser() {
            return this;
        }

        @Override
        public TimerInternals timerInternals() {
            return timerInternals;
        }

        @Override
        public <W extends BoundedWindow> void setStateCleanupTimer(String timerId, W window, Coder<W> windowCoder, Instant cleanupTime) {
            throw new UnsupportedOperationException();
        }

        @Override
        public StateInternals stateInternals() {
            throw new UnsupportedOperationException();
        }
    }
}

