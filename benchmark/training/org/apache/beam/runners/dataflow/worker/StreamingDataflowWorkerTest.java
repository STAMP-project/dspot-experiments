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
package org.apache.beam.runners.dataflow.worker;


import ByteString.EMPTY;
import Coder.Context.OUTER;
import Environments.JAVA_SDK_HARNESS_ENVIRONMENT;
import GetDataResponse.Builder;
import PaneInfo.NO_FIRING;
import PaneInfoCoder.INSTANCE;
import StreamingDataflowWorker.ComputationState;
import TimestampCombiner.EARLIEST;
import Timing.ON_TIME;
import ValueWithRecordId.ValueWithRecordIdCoder;
import Windmill.ReportStatsRequest;
import Windmill.TagBag;
import Windmill.TagValue;
import Windmill.Value;
import com.google.api.services.dataflow.model.CounterUpdate;
import com.google.api.services.dataflow.model.InstructionInput;
import com.google.api.services.dataflow.model.InstructionOutput;
import com.google.api.services.dataflow.model.ParDoInstruction;
import com.google.api.services.dataflow.model.ParallelInstruction;
import com.google.api.services.dataflow.model.ReadInstruction;
import com.google.api.services.dataflow.model.StreamingConfigTask;
import com.google.api.services.dataflow.model.WorkItem;
import com.google.api.services.dataflow.model.WorkItemStatus;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.beam.runners.core.construction.SdkComponents;
import org.apache.beam.runners.core.construction.WindowingStrategyTranslation;
import org.apache.beam.runners.dataflow.internal.CustomSources;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.util.CloudObjects;
import org.apache.beam.runners.dataflow.worker.counters.DataflowCounterUpdateExtractor;
import org.apache.beam.runners.dataflow.worker.options.StreamingDataflowWorkerOptions;
import org.apache.beam.runners.dataflow.worker.testing.RestoreDataflowLoggingMDC;
import org.apache.beam.runners.dataflow.worker.testing.TestCountingSource;
import org.apache.beam.runners.dataflow.worker.util.BoundedQueueExecutor;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.ComputationGetDataRequest;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.ComputationGetDataResponse;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.GetDataRequest;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.GetDataResponse;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.GetWorkResponse;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.KeyedGetDataRequest;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.KeyedGetDataResponse;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.Timer;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.WatermarkHold;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.WorkItemCommitRequest;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CollectionCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.ListCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.util.StringUtils;
import org.apache.beam.sdk.util.VarInt;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.util.WindowedValue.FullWindowedValueCoder;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.ValueWithRecordId;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Optional;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.beam.vendor.guava.v20_0.com.google.common.primitives.UnsignedLong;
import org.apache.beam.vendor.guava.v20_0.com.google.common.util.concurrent.Uninterruptibles;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.Statement;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static StreamingDataflowWorker.MAX_SINK_BYTES;
import static WorkerCustomSources.maxUnboundedBundleSize;


/**
 * Unit tests for {@link StreamingDataflowWorker}.
 */
@RunWith(Parameterized.class)
public class StreamingDataflowWorkerTest {
    private final boolean streamingEngine;

    public StreamingDataflowWorkerTest(Boolean streamingEngine) {
        this.streamingEngine = streamingEngine;
    }

    private static final Logger LOG = LoggerFactory.getLogger(StreamingDataflowWorkerTest.class);

    private static final IntervalWindow DEFAULT_WINDOW = new IntervalWindow(new Instant(1234), new Duration(1000));

    private static final IntervalWindow WINDOW_AT_ZERO = new IntervalWindow(new Instant(0), new Instant(1000));

    private static final IntervalWindow WINDOW_AT_ONE_SECOND = new IntervalWindow(new Instant(1000), new Instant(2000));

    private static final Coder<IntervalWindow> DEFAULT_WINDOW_CODER = IntervalWindow.getCoder();

    private static final Coder<Collection<IntervalWindow>> DEFAULT_WINDOW_COLLECTION_CODER = CollectionCoder.of(StreamingDataflowWorkerTest.DEFAULT_WINDOW_CODER);

    // Default values that are unimportant for correctness, but must be consistent
    // between pieces of this test suite
    private static final String DEFAULT_COMPUTATION_ID = "computation";

    private static final String DEFAULT_MAP_STAGE_NAME = "computation";

    private static final String DEFAULT_MAP_SYSTEM_NAME = "computation";

    private static final String DEFAULT_OUTPUT_ORIGINAL_NAME = "originalName";

    private static final String DEFAULT_OUTPUT_SYSTEM_NAME = "systemName";

    private static final String DEFAULT_PARDO_SYSTEM_NAME = "parDo";

    private static final String DEFAULT_PARDO_ORIGINAL_NAME = "parDoOriginalName";

    private static final String DEFAULT_PARDO_USER_NAME = "parDoUserName";

    private static final String DEFAULT_PARDO_STATE_FAMILY = "parDoStateFamily";

    private static final String DEFAULT_SOURCE_SYSTEM_NAME = "source";

    private static final String DEFAULT_SOURCE_ORIGINAL_NAME = "sourceOriginalName";

    private static final String DEFAULT_SINK_SYSTEM_NAME = "sink";

    private static final String DEFAULT_SINK_ORIGINAL_NAME = "sinkOriginalName";

    private static final String DEFAULT_SOURCE_COMPUTATION_ID = "upstream";

    private static final String DEFAULT_KEY_STRING = "key";

    private static final ByteString DEFAULT_KEY_BYTES = ByteString.copyFromUtf8(StreamingDataflowWorkerTest.DEFAULT_KEY_STRING);

    private static final String DEFAULT_DATA_STRING = "data";

    private static final String DEFAULT_DESTINATION_STREAM_ID = "out";

    @Rule
    public StreamingDataflowWorkerTest.BlockingFn blockingFn = new StreamingDataflowWorkerTest.BlockingFn();

    @Rule
    public TestRule restoreMDC = new RestoreDataflowLoggingMDC();

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();

    WorkUnitClient mockWorkUnitClient = Mockito.mock(WorkUnitClient.class);

    private final Supplier<Long> idGenerator = new Supplier<Long>() {
        private final AtomicLong idGenerator = new AtomicLong(1L);

        @Override
        public Long get() {
            return idGenerator.getAndIncrement();
        }
    };

    private static final Function<GetDataRequest, GetDataResponse> EMPTY_DATA_RESPONDER = (GetDataRequest request) -> {
        GetDataResponse.Builder builder = GetDataResponse.newBuilder();
        for (ComputationGetDataRequest compRequest : request.getRequestsList()) {
            ComputationGetDataResponse.Builder compBuilder = builder.addDataBuilder().setComputationId(compRequest.getComputationId());
            for (KeyedGetDataRequest keyRequest : compRequest.getRequestsList()) {
                KeyedGetDataResponse.Builder keyBuilder = compBuilder.addDataBuilder().setKey(keyRequest.getKey());
                keyBuilder.addAllValues(keyRequest.getValuesToFetchList());
                keyBuilder.addAllBags(keyRequest.getBagsToFetchList());
                keyBuilder.addAllWatermarkHolds(keyRequest.getWatermarkHoldsToFetchList());
            }
        }
        return builder.build();
    };

    @Test
    public void testBasicHarness() throws Exception {
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(StringUtf8Coder.of()), makeSinkInstruction(StringUtf8Coder.of(), 0));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        StreamingDataflowWorkerOptions options = createTestingPipelineOptions(server);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, options, true);
        worker.start();
        final int numIters = 2000;
        for (int i = 0; i < numIters; ++i) {
            server.addWorkToOffer(makeInput(i, TimeUnit.MILLISECONDS.toMicros(i)));
        }
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(numIters);
        worker.stop();
        for (int i = 0; i < numIters; ++i) {
            Assert.assertTrue(result.containsKey(((long) (i))));
            Assert.assertEquals(makeExpectedOutput(i, TimeUnit.MILLISECONDS.toMicros(i)).build(), result.get(((long) (i))));
        }
    }

    @Test
    public void testBasic() throws Exception {
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(StringUtf8Coder.of()), makeSinkInstruction(StringUtf8Coder.of(), 0));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        server.setIsReady(false);
        StreamingConfigTask streamingConfig = new StreamingConfigTask();
        streamingConfig.setStreamingComputationConfigs(ImmutableList.of(makeDefaultStreamingComputationConfig(instructions)));
        streamingConfig.setWindmillServiceEndpoint("foo");
        WorkItem workItem = new WorkItem();
        workItem.setStreamingConfigTask(streamingConfig);
        Mockito.when(mockWorkUnitClient.getGlobalStreamingConfigWorkItem()).thenReturn(Optional.of(workItem));
        StreamingDataflowWorkerOptions options = createTestingPipelineOptions(server);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, options, true);
        worker.start();
        final int numIters = 2000;
        for (int i = 0; i < numIters; ++i) {
            server.addWorkToOffer(makeInput(i, TimeUnit.MILLISECONDS.toMicros(i)));
        }
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(numIters);
        worker.stop();
        for (int i = 0; i < numIters; ++i) {
            Assert.assertTrue(result.containsKey(((long) (i))));
            Assert.assertEquals(makeExpectedOutput(i, TimeUnit.MILLISECONDS.toMicros(i)).build(), result.get(((long) (i))));
        }
    }

    static class BlockingFn extends DoFn<String, String> implements TestRule {
        public static CountDownLatch blocker = new CountDownLatch(1);

        public static Semaphore counter = new Semaphore(0);

        public static AtomicInteger callCounter = new AtomicInteger(0);

        @ProcessElement
        public void processElement(ProcessContext c) throws InterruptedException {
            StreamingDataflowWorkerTest.BlockingFn.callCounter.incrementAndGet();
            StreamingDataflowWorkerTest.BlockingFn.counter.release();
            StreamingDataflowWorkerTest.BlockingFn.blocker.await();
            c.output(c.element());
        }

        @Override
        public Statement apply(final Statement base, final Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    StreamingDataflowWorkerTest.BlockingFn.blocker = new CountDownLatch(1);
                    StreamingDataflowWorkerTest.BlockingFn.counter = new Semaphore(0);
                    StreamingDataflowWorkerTest.BlockingFn.callCounter = new AtomicInteger();
                    base.evaluate();
                }
            };
        }
    }

    @Test
    public void testIgnoreRetriedKeys() throws Exception {
        final int numIters = 4;
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(StringUtf8Coder.of()), makeDoFnInstruction(blockingFn, 0, StringUtf8Coder.of()), makeSinkInstruction(StringUtf8Coder.of(), 0));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        StreamingDataflowWorkerOptions options = createTestingPipelineOptions(server);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, options, true);
        worker.start();
        for (int i = 0; i < numIters; ++i) {
            server.addWorkToOffer(makeInput(i, TimeUnit.MILLISECONDS.toMicros(i)));
        }
        // Wait for keys to schedule.  They will be blocked.
        StreamingDataflowWorkerTest.BlockingFn.counter.acquire(numIters);
        // Re-add the work, it should be ignored due to the keys being active.
        for (int i = 0; i < numIters; ++i) {
            // Same work token.
            server.addWorkToOffer(makeInput(i, TimeUnit.MILLISECONDS.toMicros(i)));
        }
        // Give all added calls a chance to run.
        server.waitForEmptyWorkQueue();
        for (int i = 0; i < numIters; ++i) {
            // Different work token same keys.
            server.addWorkToOffer(makeInput((i + numIters), TimeUnit.MILLISECONDS.toMicros(i), keyStringForIndex(i)));
        }
        // Give all added calls a chance to run.
        server.waitForEmptyWorkQueue();
        // Release the blocked calls.
        StreamingDataflowWorkerTest.BlockingFn.blocker.countDown();
        // Verify the output
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits((numIters * 2));
        for (int i = 0; i < numIters; ++i) {
            Assert.assertTrue(result.containsKey(((long) (i))));
            Assert.assertEquals(makeExpectedOutput(i, TimeUnit.MILLISECONDS.toMicros(i)).build(), result.get(((long) (i))));
            Assert.assertTrue(result.containsKey((((long) (i)) + numIters)));
            Assert.assertEquals(makeExpectedOutput((i + numIters), TimeUnit.MILLISECONDS.toMicros(i), keyStringForIndex(i), keyStringForIndex(i)).build(), result.get((((long) (i)) + numIters)));
        }
        // Re-add the work, it should process due to the keys no longer being active.
        for (int i = 0; i < numIters; ++i) {
            server.addWorkToOffer(makeInput((i + (numIters * 2)), TimeUnit.MILLISECONDS.toMicros(i), keyStringForIndex(i)));
        }
        result = server.waitForAndGetCommits(numIters);
        worker.stop();
        for (int i = 0; i < numIters; ++i) {
            Assert.assertTrue(result.containsKey((((long) (i)) + (numIters * 2))));
            Assert.assertEquals(makeExpectedOutput((i + (numIters * 2)), TimeUnit.MILLISECONDS.toMicros(i), keyStringForIndex(i), keyStringForIndex(i)).build(), result.get((((long) (i)) + (numIters * 2))));
        }
    }

    @Test(timeout = 10000)
    public void testNumberOfWorkerHarnessThreadsIsHonored() throws Exception {
        int expectedNumberOfThreads = 5;
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(StringUtf8Coder.of()), makeDoFnInstruction(blockingFn, 0, StringUtf8Coder.of()), makeSinkInstruction(StringUtf8Coder.of(), 0));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        StreamingDataflowWorkerOptions options = createTestingPipelineOptions(server);
        options.setNumberOfWorkerHarnessThreads(expectedNumberOfThreads);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, options, true);
        worker.start();
        for (int i = 0; i < (expectedNumberOfThreads * 2); ++i) {
            server.addWorkToOffer(makeInput(i, TimeUnit.MILLISECONDS.toMicros(i)));
        }
        // This will fail to complete if the number of threads is less than the amount of work.
        // Forcing this test to timeout.
        StreamingDataflowWorkerTest.BlockingFn.counter.acquire(expectedNumberOfThreads);
        // Attempt to acquire an additional permit, if we were able to then that means
        // too many items were being processed concurrently.
        if (StreamingDataflowWorkerTest.BlockingFn.counter.tryAcquire(500, TimeUnit.MILLISECONDS)) {
            Assert.fail(((((("Expected number of threads " + expectedNumberOfThreads) + " does not match actual ") + "number of work items processed concurrently ") + (StreamingDataflowWorkerTest.BlockingFn.callCounter.get())) + "."));
        }
        StreamingDataflowWorkerTest.BlockingFn.blocker.countDown();
    }

    static class KeyTokenInvalidFn extends DoFn<KV<String, String>, KV<String, String>> {
        static boolean thrown = false;

        @ProcessElement
        public void processElement(ProcessContext c) {
            if (!(StreamingDataflowWorkerTest.KeyTokenInvalidFn.thrown)) {
                StreamingDataflowWorkerTest.KeyTokenInvalidFn.thrown = true;
                throw new KeyTokenInvalidException("key");
            } else {
                c.output(c.element());
            }
        }
    }

    @Test
    public void testKeyTokenInvalidException() throws Exception {
        if (streamingEngine) {
            // TODO: This test needs to be adapted to work with streamingEngine=true.
            return;
        }
        KvCoder<String, String> kvCoder = KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of());
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(kvCoder), makeDoFnInstruction(new StreamingDataflowWorkerTest.KeyTokenInvalidFn(), 0, kvCoder), makeSinkInstruction(kvCoder, 1));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        server.addWorkToOffer(makeInput(0, 0, "key"));
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, createTestingPipelineOptions(server), true);
        worker.start();
        server.waitForEmptyWorkQueue();
        server.addWorkToOffer(makeInput(1, 0, "key"));
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(1);
        Assert.assertEquals(makeExpectedOutput(1, 0, "key", "key").build(), result.get(1L));
        Assert.assertEquals(1, result.size());
    }

    static class LargeCommitFn extends DoFn<KV<String, String>, KV<String, String>> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            if (c.element().getKey().equals("large_key")) {
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < 100; ++i)
                    s.append("large_commit");

                c.output(KV.of(c.element().getKey(), s.toString()));
            } else {
                c.output(c.element());
            }
        }
    }

    @Test
    public void testKeyCommitTooLargeException() throws Exception {
        KvCoder<String, String> kvCoder = KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of());
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(kvCoder), makeDoFnInstruction(new StreamingDataflowWorkerTest.LargeCommitFn(), 0, kvCoder), makeSinkInstruction(kvCoder, 1));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        server.setExpectedExceptionCount(1);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, createTestingPipelineOptions(server), true);
        worker.setMaxWorkItemCommitBytes(1000);
        worker.start();
        server.addWorkToOffer(makeInput(1, 0, "large_key"));
        server.addWorkToOffer(makeInput(2, 0, "key"));
        server.waitForEmptyWorkQueue();
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(1);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(makeExpectedOutput(2, 0, "key", "key").build(), result.get(2L));
        Assert.assertTrue(result.containsKey(1L));
        Assert.assertEquals("large_key", result.get(1L).getKey().toStringUtf8());
        Assert.assertTrue(((result.get(1L).getSerializedSize()) > 1000));
        // Spam worker updates a few times.
        int maxTries = 10;
        while ((--maxTries) > 0) {
            worker.reportPeriodicWorkerUpdates();
            Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
        } 
        // We should see an exception reported for the large commit but not the small one.
        ArgumentCaptor<WorkItemStatus> workItemStatusCaptor = ArgumentCaptor.forClass(WorkItemStatus.class);
        Mockito.verify(mockWorkUnitClient, Mockito.atLeast(2)).reportWorkItemStatus(workItemStatusCaptor.capture());
        List<WorkItemStatus> capturedStatuses = workItemStatusCaptor.getAllValues();
        boolean foundErrors = false;
        for (WorkItemStatus status : capturedStatuses) {
            if (!(status.getErrors().isEmpty())) {
                Assert.assertFalse(foundErrors);
                foundErrors = true;
                String errorMessage = status.getErrors().get(0).getMessage();
                Assert.assertThat(errorMessage, Matchers.containsString("KeyCommitTooLargeException"));
            }
        }
        Assert.assertTrue(foundErrors);
    }

    @Test
    public void testKeyCommitTooLargeException_StreamingEngine() throws Exception {
        KvCoder<String, String> kvCoder = KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of());
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(kvCoder), makeDoFnInstruction(new StreamingDataflowWorkerTest.LargeCommitFn(), 0, kvCoder), makeSinkInstruction(kvCoder, 1));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        server.setExpectedExceptionCount(1);
        StreamingDataflowWorkerOptions options = createTestingPipelineOptions(server, "--experiments=enable_streaming_engine");
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, options, true);
        worker.setMaxWorkItemCommitBytes(1000);
        worker.start();
        server.addWorkToOffer(makeInput(1, 0, "large_key"));
        server.addWorkToOffer(makeInput(2, 0, "key"));
        server.waitForEmptyWorkQueue();
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(1);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(makeExpectedOutput(2, 0, "key", "key").build(), result.get(2L));
        Assert.assertTrue(result.containsKey(1L));
        Assert.assertEquals("large_key", result.get(1L).getKey().toStringUtf8());
        Assert.assertTrue(((result.get(1L).getSerializedSize()) > 1000));
        // Spam worker updates a few times.
        int maxTries = 10;
        while ((--maxTries) > 0) {
            worker.reportPeriodicWorkerUpdates();
            Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
        } 
        // We should see an exception reported for the large commit but not the small one.
        ArgumentCaptor<WorkItemStatus> workItemStatusCaptor = ArgumentCaptor.forClass(WorkItemStatus.class);
        Mockito.verify(mockWorkUnitClient, Mockito.atLeast(2)).reportWorkItemStatus(workItemStatusCaptor.capture());
        List<WorkItemStatus> capturedStatuses = workItemStatusCaptor.getAllValues();
        boolean foundErrors = false;
        for (WorkItemStatus status : capturedStatuses) {
            if (!(status.getErrors().isEmpty())) {
                Assert.assertFalse(foundErrors);
                foundErrors = true;
                String errorMessage = status.getErrors().get(0).getMessage();
                Assert.assertThat(errorMessage, Matchers.containsString("KeyCommitTooLargeException"));
            }
        }
        Assert.assertTrue(foundErrors);
    }

    static class ChangeKeysFn extends DoFn<KV<String, String>, KV<String, String>> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            KV<String, String> elem = c.element();
            c.output(KV.of((((elem.getKey()) + "_") + (elem.getValue())), elem.getValue()));
        }
    }

    @Test
    public void testKeyChange() throws Exception {
        KvCoder<String, String> kvCoder = KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of());
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(kvCoder), makeDoFnInstruction(new StreamingDataflowWorkerTest.ChangeKeysFn(), 0, kvCoder), makeSinkInstruction(kvCoder, 1));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        server.addWorkToOffer(makeInput(0, 0));
        server.addWorkToOffer(makeInput(1, TimeUnit.MILLISECONDS.toMicros(1)));
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, createTestingPipelineOptions(server), true);
        worker.start();
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(2);
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(makeExpectedOutput(i, TimeUnit.MILLISECONDS.toMicros(i), keyStringForIndex(i), (((keyStringForIndex(i)) + "_data") + i)).build(), result.get(((long) (i))));
        }
    }

    static class TestExceptionFn extends DoFn<String, String> {
        @ProcessElement
        public void processElement(ProcessContext c) throws Exception {
            if (firstTime) {
                firstTime = false;
                try {
                    throw new Exception("Exception!");
                } catch (Exception e) {
                    throw new Exception("Another exception!", e);
                }
            }
        }

        boolean firstTime = true;
    }

    @Test(timeout = 30000)
    public void testExceptions() throws Exception {
        if (streamingEngine) {
            // TODO: This test needs to be adapted to work with streamingEngine=true.
            return;
        }
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(StringUtf8Coder.of()), makeDoFnInstruction(new StreamingDataflowWorkerTest.TestExceptionFn(), 0, StringUtf8Coder.of()), makeSinkInstruction(StringUtf8Coder.of(), 1));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        server.setExpectedExceptionCount(1);
        String keyString = keyStringForIndex(0);
        server.addWorkToOffer(buildInput((((((((((((((((((((((("work {" + "  computation_id: \"") + (StreamingDataflowWorkerTest.DEFAULT_COMPUTATION_ID)) + "\"") + "  input_data_watermark: 0") + "  work {") + "    key: \"") + keyString) + "\"") + "    sharding_key: 1") + "    work_token: 0") + "    cache_token: 1") + "    message_bundles {") + "      source_computation_id: \"") + (StreamingDataflowWorkerTest.DEFAULT_SOURCE_COMPUTATION_ID)) + "\"") + "      messages {") + "        timestamp: 0") + "        data: \"0\"") + "      }") + "    }") + "  }") + "}"), CoderUtils.encodeToByteArray(CollectionCoder.of(IntervalWindow.getCoder()), Arrays.asList(StreamingDataflowWorkerTest.DEFAULT_WINDOW))));
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, createTestingPipelineOptions(server), true);
        worker.start();
        server.waitForEmptyWorkQueue();
        // Wait until the worker has given up.
        int maxTries = 10;
        while (((maxTries--) > 0) && (!(worker.workExecutorIsEmpty()))) {
            Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
        } 
        Assert.assertTrue(worker.workExecutorIsEmpty());
        // Spam worker updates a few times.
        maxTries = 10;
        while ((maxTries--) > 0) {
            worker.reportPeriodicWorkerUpdates();
            Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
        } 
        // We should see our update only one time with the exceptions we are expecting.
        ArgumentCaptor<WorkItemStatus> workItemStatusCaptor = ArgumentCaptor.forClass(WorkItemStatus.class);
        Mockito.verify(mockWorkUnitClient, Mockito.atLeast(1)).reportWorkItemStatus(workItemStatusCaptor.capture());
        List<WorkItemStatus> capturedStatuses = workItemStatusCaptor.getAllValues();
        boolean foundErrors = false;
        int lastUpdateWithoutErrors = 0;
        int lastUpdateWithErrors = 0;
        for (WorkItemStatus status : capturedStatuses) {
            if (status.getErrors().isEmpty()) {
                lastUpdateWithoutErrors++;
                continue;
            }
            lastUpdateWithErrors++;
            Assert.assertFalse(foundErrors);
            foundErrors = true;
            String stacktrace = status.getErrors().get(0).getMessage();
            Assert.assertThat(stacktrace, Matchers.containsString("Exception!"));
            Assert.assertThat(stacktrace, Matchers.containsString("Another exception!"));
            Assert.assertThat(stacktrace, Matchers.containsString("processElement"));
        }
        Assert.assertTrue(foundErrors);
        // The last update we see should not have any errors. This indicates we've retried the workitem.
        Assert.assertTrue((lastUpdateWithoutErrors > lastUpdateWithErrors));
        // Confirm we've received the expected stats. There is no guarantee stats will only be reported
        // once.
        Assert.assertThat(server.getStatsReceived().size(), Matchers.greaterThanOrEqualTo(1));
        Windmill.ReportStatsRequest stats = server.getStatsReceived().get(0);
        Assert.assertEquals(StreamingDataflowWorkerTest.DEFAULT_COMPUTATION_ID, stats.getComputationId());
        Assert.assertEquals(keyString, stats.getKey().toStringUtf8());
        Assert.assertEquals(0, stats.getWorkToken());
        Assert.assertEquals(1, stats.getShardingKey());
    }

    @Test
    public void testAssignWindows() throws Exception {
        Duration gapDuration = Duration.standardSeconds(1);
        CloudObject spec = CloudObject.forClassName("AssignWindowsDoFn");
        SdkComponents sdkComponents = SdkComponents.create();
        sdkComponents.registerEnvironment(JAVA_SDK_HARNESS_ENVIRONMENT);
        addString(spec, PropertyNames.SERIALIZED_FN, StringUtils.byteArrayToJsonString(WindowingStrategyTranslation.toMessageProto(WindowingStrategy.of(FixedWindows.of(gapDuration)), sdkComponents).toByteArray()));
        ParallelInstruction addWindowsInstruction = new ParallelInstruction().setSystemName("AssignWindows").setName("AssignWindows").setOriginalName("AssignWindowsOriginal").setParDo(new ParDoInstruction().setInput(new InstructionInput().setProducerInstructionIndex(0).setOutputNum(0)).setNumOutputs(1).setUserFn(spec)).setOutputs(Arrays.asList(new InstructionOutput().setOriginalName(StreamingDataflowWorkerTest.DEFAULT_OUTPUT_ORIGINAL_NAME).setSystemName(StreamingDataflowWorkerTest.DEFAULT_OUTPUT_SYSTEM_NAME).setName("output").setCodec(/* sdkComponents= */
        CloudObjects.asCloudObject(WindowedValue.getFullCoder(StringUtf8Coder.of(), IntervalWindow.getCoder()), null))));
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(StringUtf8Coder.of()), addWindowsInstruction, makeSinkInstruction(StringUtf8Coder.of(), 1));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        int timestamp1 = 0;
        int timestamp2 = 1000000;
        server.addWorkToOffer(makeInput(timestamp1, timestamp1));
        server.addWorkToOffer(makeInput(timestamp2, timestamp2));
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, createTestingPipelineOptions(server), false);
        worker.start();
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(2);
        Assert.assertThat(result.get(((long) (timestamp1))), Matchers.equalTo(setMessagesMetadata(NO_FIRING, intervalWindowBytes(StreamingDataflowWorkerTest.WINDOW_AT_ZERO), makeExpectedOutput(timestamp1, timestamp1)).build()));
        Assert.assertThat(result.get(((long) (timestamp2))), Matchers.equalTo(setMessagesMetadata(NO_FIRING, intervalWindowBytes(StreamingDataflowWorkerTest.WINDOW_AT_ONE_SECOND), makeExpectedOutput(timestamp2, timestamp2)).build()));
    }

    // Runs a merging windows test verifying stored state, holds and timers.
    @Test
    public void testMergeWindows() throws Exception {
        Coder<KV<String, String>> kvCoder = KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of());
        Coder<WindowedValue<KV<String, String>>> windowedKvCoder = FullWindowedValueCoder.of(kvCoder, IntervalWindow.getCoder());
        KvCoder<String, List<String>> groupedCoder = KvCoder.of(StringUtf8Coder.of(), ListCoder.of(StringUtf8Coder.of()));
        Coder<WindowedValue<KV<String, List<String>>>> windowedGroupedCoder = FullWindowedValueCoder.of(groupedCoder, IntervalWindow.getCoder());
        CloudObject spec = CloudObject.forClassName("MergeWindowsDoFn");
        SdkComponents sdkComponents = SdkComponents.create();
        sdkComponents.registerEnvironment(JAVA_SDK_HARNESS_ENVIRONMENT);
        addString(spec, PropertyNames.SERIALIZED_FN, StringUtils.byteArrayToJsonString(WindowingStrategyTranslation.toMessageProto(WindowingStrategy.of(FixedWindows.of(Duration.standardSeconds(1))).withTimestampCombiner(EARLIEST), sdkComponents).toByteArray()));
        addObject(spec, WorkerPropertyNames.INPUT_CODER, /* sdkComponents= */
        CloudObjects.asCloudObject(windowedKvCoder, null));
        ParallelInstruction mergeWindowsInstruction = new ParallelInstruction().setSystemName("MergeWindows-System").setName("MergeWindowsStep").setOriginalName("MergeWindowsOriginal").setParDo(new ParDoInstruction().setInput(new InstructionInput().setProducerInstructionIndex(0).setOutputNum(0)).setNumOutputs(1).setUserFn(spec)).setOutputs(Arrays.asList(new InstructionOutput().setOriginalName(StreamingDataflowWorkerTest.DEFAULT_OUTPUT_ORIGINAL_NAME).setSystemName(StreamingDataflowWorkerTest.DEFAULT_OUTPUT_SYSTEM_NAME).setName("output").setCodec(/* sdkComponents= */
        CloudObjects.asCloudObject(windowedGroupedCoder, null))));
        List<ParallelInstruction> instructions = Arrays.asList(makeWindowingSourceInstruction(kvCoder), mergeWindowsInstruction, makeSinkInstruction(groupedCoder, 1));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, createTestingPipelineOptions(server), false);
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("MergeWindowsStep", "MergeWindows");
        worker.addStateNameMappings(nameMap);
        worker.start();
        server.addWorkToOffer(buildInput((((((((((((((((((((((((("work {" + "  computation_id: \"") + (StreamingDataflowWorkerTest.DEFAULT_COMPUTATION_ID)) + "\"") + "  input_data_watermark: 0") + "  work {") + "    key: \"") + (StreamingDataflowWorkerTest.DEFAULT_KEY_STRING)) + "\"") + "    sharding_key: 1") + "    cache_token: 1") + "    work_token: 1") + "    message_bundles {") + "      source_computation_id: \"") + (StreamingDataflowWorkerTest.DEFAULT_SOURCE_COMPUTATION_ID)) + "\"") + "      messages {") + "        timestamp: 0") + "        data: \"") + (dataStringForIndex(0))) + "\"") + "      }") + "    }") + "  }") + "}"), intervalWindowBytes(StreamingDataflowWorkerTest.WINDOW_AT_ZERO)));
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(1);
        Iterable<CounterUpdate> counters = worker.buildCounters();
        // These tags and data are opaque strings and this is a change detector test.
        // The "/u" indicates the user's namespace, versus "/s" for system namespace
        String window = "/gAAAAAAAA-joBw/";
        String timerTagPrefix = ("/s" + window) + "+0";
        ByteString bufferTag = ByteString.copyFromUtf8((window + "+ubuf"));
        ByteString paneInfoTag = ByteString.copyFromUtf8((window + "+upane"));
        String watermarkDataHoldTag = window + "+uhold";
        String watermarkExtraHoldTag = window + "+uextra";
        String stateFamily = "MergeWindows";
        ByteString bufferData = ByteString.copyFromUtf8("data0");
        // Encoded form for Iterable<String>: -1, true, 'data0', false
        ByteString outputData = ByteString.copyFrom(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), 1, 5, 100, 97, 116, 97, 48, 0 });
        // These values are not essential to the change detector test
        long timerTimestamp = 999000L;
        WorkItemCommitRequest actualOutput = result.get(1L);
        // Set timer
        verifyTimers(actualOutput, buildWatermarkTimer(timerTagPrefix, 999));
        Assert.assertThat(actualOutput.getBagUpdatesList(), Matchers.contains(Matchers.equalTo(TagBag.newBuilder().setTag(bufferTag).setStateFamily(stateFamily).addValues(bufferData).build())));
        verifyHolds(actualOutput, buildHold(watermarkDataHoldTag, 0, false));
        // No state reads
        Assert.assertEquals(0L, DataflowCounterUpdateExtractor.splitIntToLong(StreamingDataflowWorkerTest.getCounter(counters, "WindmillStateBytesRead").getInteger()));
        // Timer + buffer + watermark hold
        Assert.assertEquals(Windmill.WorkItemCommitRequest.newBuilder(actualOutput).clearCounterUpdates().clearOutputMessages().build().getSerializedSize(), DataflowCounterUpdateExtractor.splitIntToLong(StreamingDataflowWorkerTest.getCounter(counters, "WindmillStateBytesWritten").getInteger()));
        // Input messages
        // proto overhead
        Assert.assertEquals(((((VarInt.getLength(0L)) + (dataStringForIndex(0).length())) + (addPaneTag(NO_FIRING, intervalWindowBytes(StreamingDataflowWorkerTest.WINDOW_AT_ZERO)).size())) + 5L), DataflowCounterUpdateExtractor.splitIntToLong(StreamingDataflowWorkerTest.getCounter(counters, "WindmillShuffleBytesRead").getInteger()));
        Windmill.GetWorkResponse.Builder getWorkResponse = Windmill.GetWorkResponse.newBuilder();
        getWorkResponse.addWorkBuilder().setComputationId(StreamingDataflowWorkerTest.DEFAULT_COMPUTATION_ID).setInputDataWatermark((timerTimestamp + 1000)).addWorkBuilder().setKey(ByteString.copyFromUtf8(StreamingDataflowWorkerTest.DEFAULT_KEY_STRING)).setShardingKey(1).setWorkToken(2).setCacheToken(1).getTimersBuilder().addTimers(buildWatermarkTimer(timerTagPrefix, timerTimestamp));
        server.addWorkToOffer(getWorkResponse.build());
        long expectedBytesRead = 0L;
        Windmill.GetDataResponse.Builder dataResponse = Windmill.GetDataResponse.newBuilder();
        Windmill.KeyedGetDataResponse.Builder dataBuilder = dataResponse.addDataBuilder().setComputationId(StreamingDataflowWorkerTest.DEFAULT_COMPUTATION_ID).addDataBuilder().setKey(ByteString.copyFromUtf8(StreamingDataflowWorkerTest.DEFAULT_KEY_STRING));
        dataBuilder.addBagsBuilder().setTag(bufferTag).setStateFamily(stateFamily).addValues(bufferData);
        dataBuilder.addWatermarkHoldsBuilder().setTag(ByteString.copyFromUtf8(watermarkDataHoldTag)).setStateFamily(stateFamily).addTimestamps(0);
        dataBuilder.addWatermarkHoldsBuilder().setTag(ByteString.copyFromUtf8(watermarkExtraHoldTag)).setStateFamily(stateFamily).addTimestamps(0);
        dataBuilder.addValuesBuilder().setTag(paneInfoTag).setStateFamily(stateFamily).getValueBuilder().setTimestamp(0).setData(EMPTY);
        server.addDataToOffer(dataResponse.build());
        expectedBytesRead += dataBuilder.build().getSerializedSize();
        result = server.waitForAndGetCommits(1);
        counters = worker.buildCounters();
        actualOutput = result.get(2L);
        Assert.assertEquals(1, actualOutput.getOutputMessagesCount());
        Assert.assertEquals(StreamingDataflowWorkerTest.DEFAULT_DESTINATION_STREAM_ID, actualOutput.getOutputMessages(0).getDestinationStreamId());
        Assert.assertEquals(StreamingDataflowWorkerTest.DEFAULT_KEY_STRING, actualOutput.getOutputMessages(0).getBundles(0).getKey().toStringUtf8());
        Assert.assertEquals(0, actualOutput.getOutputMessages(0).getBundles(0).getMessages(0).getTimestamp());
        Assert.assertEquals(outputData, actualOutput.getOutputMessages(0).getBundles(0).getMessages(0).getData());
        ByteString metadata = actualOutput.getOutputMessages(0).getBundles(0).getMessages(0).getMetadata();
        InputStream inStream = metadata.newInput();
        Assert.assertEquals(PaneInfo.createPane(true, true, ON_TIME), INSTANCE.decode(inStream));
        Assert.assertEquals(Arrays.asList(StreamingDataflowWorkerTest.WINDOW_AT_ZERO), StreamingDataflowWorkerTest.DEFAULT_WINDOW_COLLECTION_CODER.decode(inStream, OUTER));
        // Data was deleted
        Assert.assertThat(("" + (actualOutput.getValueUpdatesList())), actualOutput.getValueUpdatesList(), Matchers.contains(Matchers.equalTo(TagValue.newBuilder().setTag(paneInfoTag).setStateFamily(stateFamily).setValue(Value.newBuilder().setTimestamp(Long.MAX_VALUE).setData(EMPTY)).build())));
        Assert.assertThat(("" + (actualOutput.getBagUpdatesList())), actualOutput.getBagUpdatesList(), Matchers.contains(Matchers.equalTo(TagBag.newBuilder().setTag(bufferTag).setStateFamily(stateFamily).setDeleteAll(true).build())));
        verifyHolds(actualOutput, buildHold(watermarkDataHoldTag, (-1), true), buildHold(watermarkExtraHoldTag, (-1), true));
        // State reads for windowing
        Assert.assertEquals(expectedBytesRead, DataflowCounterUpdateExtractor.splitIntToLong(StreamingDataflowWorkerTest.getCounter(counters, "WindmillStateBytesRead").getInteger()));
        // State updates to clear state
        Assert.assertEquals(Windmill.WorkItemCommitRequest.newBuilder(actualOutput).clearCounterUpdates().clearOutputMessages().build().getSerializedSize(), DataflowCounterUpdateExtractor.splitIntToLong(StreamingDataflowWorkerTest.getCounter(counters, "WindmillStateBytesWritten").getInteger()));
        // No input messages
        Assert.assertEquals(0L, DataflowCounterUpdateExtractor.splitIntToLong(StreamingDataflowWorkerTest.getCounter(counters, "WindmillShuffleBytesRead").getInteger()));
    }

    static class Action {
        public Action(org.apache.beam.runners.dataflow.worker.windmill.Windmill.GetWorkResponse response) {
            this.response = response;
        }

        StreamingDataflowWorkerTest.Action withHolds(WatermarkHold... holds) {
            this.expectedHolds = holds;
            return this;
        }

        StreamingDataflowWorkerTest.Action withTimers(Timer... timers) {
            this.expectedTimers = timers;
            return this;
        }

        org.apache.beam.runners.dataflow.worker.windmill.Windmill.GetWorkResponse response;

        Timer[] expectedTimers = new Timer[]{  };

        WatermarkHold[] expectedHolds = new WatermarkHold[]{  };
    }

    @Test
    public void testMergeSessionWindows() throws Exception {
        // Test a single late window.
        runMergeSessionsActions(Arrays.asList(new StreamingDataflowWorkerTest.Action(buildSessionInput(1, 40, 0, Arrays.asList(1L), Collections.EMPTY_LIST)).withHolds(buildHold("/gAAAAAAAAAsK/+uhold", (-1), true), buildHold("/gAAAAAAAAAsK/+uextra", (-1), true)).withTimers(buildWatermarkTimer("/s/gAAAAAAAAAsK/+0", 3600010))));
        // Test the behavior with an:
        // - on time window that is triggered due to watermark advancement
        // - a late window that is triggered immediately due to count
        // - extending the on time window to a timestamp beyond the watermark
        // - merging the late window with the on time window through the addition of intermediate
        // elements
        runMergeSessionsActions(Arrays.asList(new StreamingDataflowWorkerTest.Action(buildSessionInput(1, 0, 0, Arrays.asList(1L), Collections.EMPTY_LIST)).withHolds(buildHold("/gAAAAAAAAAsK/+uhold", 10, false)).withTimers(buildWatermarkTimer("/s/gAAAAAAAAAsK/+0", 10), buildWatermarkTimer("/s/gAAAAAAAAAsK/+0", 3600010)), new StreamingDataflowWorkerTest.Action(buildSessionInput(2, 30, 0, Collections.EMPTY_LIST, Arrays.asList(buildWatermarkTimer("/s/gAAAAAAAAAsK/+0", 10)))).withTimers(buildWatermarkTimer("/s/gAAAAAAAAAsK/+0", 3600010)).withHolds(buildHold("/gAAAAAAAAAsK/+uhold", (-1), true), buildHold("/gAAAAAAAAAsK/+uextra", (-1), true)), new StreamingDataflowWorkerTest.Action(buildSessionInput(3, 30, 0, Arrays.asList(8L), Collections.EMPTY_LIST)).withTimers(buildWatermarkTimer("/s/gAAAAAAAABIR/+0", 3600017), buildWatermarkTimer("/s/gAAAAAAAAAsK/+0", 10, true), buildWatermarkTimer("/s/gAAAAAAAAAsK/+0", 3600010, true)).withHolds(buildHold("/gAAAAAAAAAsK/+uhold", (-1), true), buildHold("/gAAAAAAAAAsK/+uextra", (-1), true)), new StreamingDataflowWorkerTest.Action(buildSessionInput(4, 30, 0, Arrays.asList(31L), Collections.EMPTY_LIST)).withTimers(buildWatermarkTimer("/s/gAAAAAAAACkK/+0", 3600040), buildWatermarkTimer("/s/gAAAAAAAACkK/+0", 40)).withHolds(buildHold("/gAAAAAAAACkK/+uhold", 40, false)), new StreamingDataflowWorkerTest.Action(buildSessionInput(5, 30, 0, Arrays.asList(17L, 23L), Collections.EMPTY_LIST)).withTimers(buildWatermarkTimer("/s/gAAAAAAAACkK/+0", 3600040, true), buildWatermarkTimer("/s/gAAAAAAAACkK/+0", 40, true), buildWatermarkTimer("/s/gAAAAAAAABIR/+0", 3600017, true), buildWatermarkTimer("/s/gAAAAAAAABIR/+0", 17, true), buildWatermarkTimer("/s/gAAAAAAAACko/+0", 40), buildWatermarkTimer("/s/gAAAAAAAACko/+0", 3600040)).withHolds(buildHold("/gAAAAAAAACkK/+uhold", (-1), true), buildHold("/gAAAAAAAACkK/+uextra", (-1), true), buildHold("/gAAAAAAAAAsK/+uhold", 40, true), buildHold("/gAAAAAAAAAsK/+uextra", 3600040, true)), new StreamingDataflowWorkerTest.Action(buildSessionInput(6, 50, 0, Collections.EMPTY_LIST, Arrays.asList(buildWatermarkTimer("/s/gAAAAAAAACko/+0", 40)))).withTimers(buildWatermarkTimer("/s/gAAAAAAAACko/+0", 3600040)).withHolds(buildHold("/gAAAAAAAAAsK/+uhold", (-1), true), buildHold("/gAAAAAAAAAsK/+uextra", (-1), true))));
    }

    static class PrintFn extends DoFn<ValueWithRecordId<KV<Integer, Integer>>, String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            KV<Integer, Integer> elem = c.element().getValue();
            c.output((((elem.getKey()) + ":") + (elem.getValue())));
        }
    }

    @Test
    public void testUnboundedSources() throws Exception {
        List<Integer> finalizeTracker = Lists.newArrayList();
        TestCountingSource.setFinalizeTracker(finalizeTracker);
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(makeUnboundedSourcePipeline(), createTestingPipelineOptions(server), false);
        worker.start();
        // Test new key.
        server.addWorkToOffer(buildInput(("work {" + (((((((("  computation_id: \"computation\"" + "  input_data_watermark: 0") + "  work {") + "    key: \"0000000000000001\"") + "    sharding_key: 1") + "    work_token: 1") + "    cache_token: 1") + "  }") + "}")), null));
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(1);
        Iterable<CounterUpdate> counters = worker.buildCounters();
        Windmill.WorkItemCommitRequest commit = result.get(1L);
        UnsignedLong finalizeId = UnsignedLong.fromLongBits(commit.getSourceStateUpdates().getFinalizeIds(0));
        Assert.assertThat(commit, Matchers.equalTo(setMessagesMetadata(NO_FIRING, CoderUtils.encodeToByteArray(CollectionCoder.of(GlobalWindow.Coder.INSTANCE), Arrays.asList(GlobalWindow.INSTANCE)), parseCommitRequest((((("key: \"0000000000000001\" " + ((((((((((((((((("sharding_key: 1 " + "work_token: 1 ") + "cache_token: 1 ") + "source_backlog_bytes: 7 ") + "output_messages {") + "  destination_stream_id: \"out\"") + "  bundles {") + "    key: \"0000000000000001\"") + "    messages {") + "      timestamp: 0") + "      data: \"0:0\"") + "    }") + "    messages_ids: \"\"") + "  }") + "} ") + "source_state_updates {") + "  state: \"\u0000\"") + "  finalize_ids: ")) + finalizeId) + "} ") + "source_watermark: 1000"))).build()));
        Assert.assertEquals(18L, DataflowCounterUpdateExtractor.splitIntToLong(StreamingDataflowWorkerTest.getCounter(counters, "dataflow_input_size-computation").getInteger()));
        // Test same key continuing. The counter is done.
        server.addWorkToOffer(buildInput(((((("work {" + ((((((((("  computation_id: \"computation\"" + "  input_data_watermark: 0") + "  work {") + "    key: \"0000000000000001\"") + "    sharding_key: 1") + "    work_token: 2") + "    cache_token: 1") + "    source_state {") + "      state: \"\u0001\"") + "      finalize_ids: ")) + finalizeId) + "    } ") + "  }") + "}"), null));
        result = server.waitForAndGetCommits(1);
        counters = worker.buildCounters();
        commit = result.get(2L);
        finalizeId = UnsignedLong.fromLongBits(commit.getSourceStateUpdates().getFinalizeIds(0));
        Assert.assertThat(commit, Matchers.equalTo(parseCommitRequest((((("key: \"0000000000000001\" " + (((((("sharding_key: 1 " + "work_token: 2 ") + "cache_token: 1 ") + "source_backlog_bytes: 7 ") + "source_state_updates {") + "  state: \"\u0000\"") + "  finalize_ids: ")) + finalizeId) + "} ") + "source_watermark: 1000")).build()));
        Assert.assertThat(finalizeTracker, Matchers.contains(0));
        Assert.assertEquals(null, StreamingDataflowWorkerTest.getCounter(counters, "dataflow_input_size-computation"));
        // Test recovery (on a new key so fresh reader state). Counter is done.
        server.addWorkToOffer(buildInput(("work {" + ((((((((((("  computation_id: \"computation\"" + "  input_data_watermark: 0") + "  work {") + "    key: \"0000000000000002\"") + "    sharding_key: 2") + "    work_token: 3") + "    cache_token: 2") + "    source_state {") + "      state: \"\u0000\"") + "    } ") + "  }") + "}")), null));
        result = server.waitForAndGetCommits(1);
        counters = worker.buildCounters();
        commit = result.get(3L);
        finalizeId = UnsignedLong.fromLongBits(commit.getSourceStateUpdates().getFinalizeIds(0));
        Assert.assertThat(commit, Matchers.equalTo(parseCommitRequest((((("key: \"0000000000000002\" " + (((((("sharding_key: 2 " + "work_token: 3 ") + "cache_token: 2 ") + "source_backlog_bytes: 7 ") + "source_state_updates {") + "  state: \"\u0000\"") + "  finalize_ids: ")) + finalizeId) + "} ") + "source_watermark: 1000")).build()));
        Assert.assertEquals(null, StreamingDataflowWorkerTest.getCounter(counters, "dataflow_input_size-computation"));
    }

    @Test
    public void testUnboundedSourcesDrain() throws Exception {
        List<Integer> finalizeTracker = Lists.newArrayList();
        TestCountingSource.setFinalizeTracker(finalizeTracker);
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(makeUnboundedSourcePipeline(), createTestingPipelineOptions(server), true);
        worker.start();
        // Test new key.
        server.addWorkToOffer(buildInput(("work {" + (((((((("  computation_id: \"computation\"" + "  input_data_watermark: 0") + "  work {") + "    key: \"0000000000000001\"") + "    sharding_key: 1") + "    work_token: 2") + "    cache_token: 3") + "  }") + "}")), null));
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(1);
        Windmill.WorkItemCommitRequest commit = result.get(2L);
        UnsignedLong finalizeId = UnsignedLong.fromLongBits(commit.getSourceStateUpdates().getFinalizeIds(0));
        Assert.assertThat(commit, Matchers.equalTo(setMessagesMetadata(NO_FIRING, CoderUtils.encodeToByteArray(CollectionCoder.of(GlobalWindow.Coder.INSTANCE), Arrays.asList(GlobalWindow.INSTANCE)), parseCommitRequest((((("key: \"0000000000000001\" " + ((((((((((((((((("sharding_key: 1 " + "work_token: 2 ") + "cache_token: 3 ") + "source_backlog_bytes: 7 ") + "output_messages {") + "  destination_stream_id: \"out\"") + "  bundles {") + "    key: \"0000000000000001\"") + "    messages {") + "      timestamp: 0") + "      data: \"0:0\"") + "    }") + "    messages_ids: \"\"") + "  }") + "} ") + "source_state_updates {") + "  state: \"\u0000\"") + "  finalize_ids: ")) + finalizeId) + "} ") + "source_watermark: 1000"))).build()));
        // Test drain work item.
        server.addWorkToOffer(buildInput(((((("work {" + ((((((((("  computation_id: \"computation\"" + "  input_data_watermark: 0") + "  work {") + "    key: \"0000000000000001\"") + "    sharding_key: 1") + "    work_token: 3") + "    cache_token: 3") + "    source_state {") + "      only_finalize: true") + "      finalize_ids: ")) + finalizeId) + "    }") + "  }") + "}"), null));
        result = server.waitForAndGetCommits(1);
        commit = result.get(3L);
        Assert.assertThat(commit, Matchers.equalTo(parseCommitRequest(("key: \"0000000000000001\" " + ((((("sharding_key: 1 " + "work_token: 3 ") + "cache_token: 3 ") + "source_state_updates {") + "  only_finalize: true") + "} "))).build()));
        Assert.assertThat(finalizeTracker, Matchers.contains(0));
    }

    private static class MockWork extends StreamingDataflowWorker.Work {
        public MockWork(long workToken) {
            super(Windmill.WorkItem.newBuilder().setKey(EMPTY).setWorkToken(workToken).build());
        }

        @Override
        public void run() {
        }
    }

    @Test
    public void testActiveWork() throws Exception {
        BoundedQueueExecutor mockExecutor = Mockito.mock(BoundedQueueExecutor.class);
        StreamingDataflowWorker.ComputationState computationState = new StreamingDataflowWorker.ComputationState("computation", defaultMapTask(Arrays.asList(makeSourceInstruction(StringUtf8Coder.of()))), mockExecutor, ImmutableMap.of());
        ByteString key1 = ByteString.copyFromUtf8("key1");
        ByteString key2 = ByteString.copyFromUtf8("key2");
        StreamingDataflowWorkerTest.MockWork m1 = new StreamingDataflowWorkerTest.MockWork(1);
        computationState.activateWork(key1, m1);
        Mockito.verify(mockExecutor).execute(m1);
        computationState.completeWork(key1, 1);
        Mockito.verifyNoMoreInteractions(mockExecutor);
        // Verify work queues.
        StreamingDataflowWorkerTest.MockWork m2 = new StreamingDataflowWorkerTest.MockWork(2);
        computationState.activateWork(key1, m2);
        Mockito.verify(mockExecutor).execute(m2);
        StreamingDataflowWorkerTest.MockWork m3 = new StreamingDataflowWorkerTest.MockWork(3);
        computationState.activateWork(key1, m3);
        Mockito.verifyNoMoreInteractions(mockExecutor);
        // Verify another key is a separate queue.
        StreamingDataflowWorkerTest.MockWork m4 = new StreamingDataflowWorkerTest.MockWork(4);
        computationState.activateWork(key2, m4);
        Mockito.verify(mockExecutor).execute(m4);
        computationState.completeWork(key2, 4);
        Mockito.verifyNoMoreInteractions(mockExecutor);
        computationState.completeWork(key1, 2);
        Mockito.verify(mockExecutor).forceExecute(m3);
        computationState.completeWork(key1, 3);
        Mockito.verifyNoMoreInteractions(mockExecutor);
        StreamingDataflowWorkerTest.MockWork m5 = new StreamingDataflowWorkerTest.MockWork(5);
        computationState.activateWork(key1, m5);
        Mockito.verify(mockExecutor).execute(m5);
        computationState.completeWork(key1, 5);
        Mockito.verifyNoMoreInteractions(mockExecutor);
    }

    static class TestExceptionInvalidatesCacheFn extends DoFn<ValueWithRecordId<KV<Integer, Integer>>, String> {
        static boolean thrown = false;

        @StateId("int")
        private final StateSpec<ValueState<Integer>> counter = StateSpecs.value(VarIntCoder.of());

        @ProcessElement
        public void processElement(ProcessContext c, @StateId("int")
        ValueState<Integer> state) throws Exception {
            KV<Integer, Integer> elem = c.element().getValue();
            if ((elem.getValue()) == 0) {
                StreamingDataflowWorkerTest.LOG.error("**** COUNTER 0 ****");
                Assert.assertEquals(null, state.read());
                state.write(42);
                Assert.assertEquals(((Integer) (42)), state.read());
            } else
                if ((elem.getValue()) == 1) {
                    StreamingDataflowWorkerTest.LOG.error("**** COUNTER 1 ****");
                    Assert.assertEquals(((Integer) (42)), state.read());
                } else
                    if ((elem.getValue()) == 2) {
                        if (!(StreamingDataflowWorkerTest.TestExceptionInvalidatesCacheFn.thrown)) {
                            StreamingDataflowWorkerTest.LOG.error("**** COUNTER 2 (will throw) ****");
                            StreamingDataflowWorkerTest.TestExceptionInvalidatesCacheFn.thrown = true;
                            throw new Exception("Exception!");
                        }
                        StreamingDataflowWorkerTest.LOG.error("**** COUNTER 2 (retry) ****");
                        Assert.assertEquals(((Integer) (42)), state.read());
                    } else {
                        throw new RuntimeException("only expecting values [0,2]");
                    }


            c.output((((elem.getKey()) + ":") + (elem.getValue())));
        }
    }

    @Test
    public void testExceptionInvalidatesCache() throws Exception {
        DataflowPipelineOptions options = PipelineOptionsFactory.create().as(DataflowPipelineOptions.class);
        options.setNumWorkers(1);
        // We'll need to force the system to limit bundles to one message at a time.
        int originalMaxUnboundedBundleSize = maxUnboundedBundleSize;
        maxUnboundedBundleSize = 1;
        try {
            // Sequence is as follows:
            // 01. GetWork[0] (token 0)
            // 02. Create counter reader
            // 03. Counter yields 0
            // 04. GetData[0] (state as null)
            // 05. Read state as null
            // 06. Set state as 42
            // 07. THROW on taking counter reader checkpoint
            // 08. Create counter reader
            // 09. Counter yields 0
            // 10. GetData[1] (state as null)
            // 11. Read state as null (*** not 42 ***)
            // 12. Take counter reader checkpoint as 0
            // 13. CommitWork[0] (message 0:0, state 42, checkpoint 0)
            // 14. GetWork[1] (token 1, checkpoint as 0)
            // 15. Counter yields 1
            // 16. Read (cached) state as 42
            // 17. Take counter reader checkpoint 1
            // 18. CommitWork[1] (message 0:1, checkpoint 1)
            // 19. GetWork[2] (token 2, checkpoint as 1)
            // 20. Counter yields 2
            // 21. THROW on processElement
            // 22. Recreate reader from checkpoint 1
            // 23. Counter yields 2 (*** not eof ***)
            // 24. GetData[2] (state as 42)
            // 25. Read state as 42
            // 26. Take counter reader checkpoint 2
            // 27. CommitWork[2] (message 0:2, checkpoint 2)
            CloudObject codec = /* sdkComponents= */
            CloudObjects.asCloudObject(WindowedValue.getFullCoder(ValueWithRecordIdCoder.of(KvCoder.of(VarIntCoder.of(), VarIntCoder.of())), GlobalWindow.Coder.INSTANCE), null);
            TestCountingSource counter = new TestCountingSource(3).withThrowOnFirstSnapshot(true);
            List<ParallelInstruction> instructions = Arrays.asList(new ParallelInstruction().setOriginalName("OriginalReadName").setSystemName("Read").setName(StreamingDataflowWorkerTest.DEFAULT_PARDO_USER_NAME).setRead(new ReadInstruction().setSource(CustomSources.serializeToCloudSource(counter, options).setCodec(codec))).setOutputs(Arrays.asList(new InstructionOutput().setName("read_output").setOriginalName(StreamingDataflowWorkerTest.DEFAULT_OUTPUT_ORIGINAL_NAME).setSystemName(StreamingDataflowWorkerTest.DEFAULT_OUTPUT_SYSTEM_NAME).setCodec(codec))), makeDoFnInstruction(new StreamingDataflowWorkerTest.TestExceptionInvalidatesCacheFn(), 0, StringUtf8Coder.of(), WindowingStrategy.globalDefault()), makeSinkInstruction(StringUtf8Coder.of(), 1, GlobalWindow.Coder.INSTANCE));
            FakeWindmillServer server = new FakeWindmillServer(errorCollector);
            server.setExpectedExceptionCount(2);
            StreamingDataflowWorker worker = /* publishCounters */
            makeWorker(instructions, createTestingPipelineOptions(server), true);
            worker.setRetryLocallyDelayMs(100);
            worker.start();
            // Three GetData requests
            for (int i = 0; i < 3; i++) {
                ByteString state;
                if ((i == 0) || (i == 1)) {
                    state = ByteString.EMPTY;
                } else {
                    state = ByteString.copyFrom(new byte[]{ 42 });
                }
                Windmill.GetDataResponse.Builder dataResponse = Windmill.GetDataResponse.newBuilder();
                dataResponse.addDataBuilder().setComputationId(StreamingDataflowWorkerTest.DEFAULT_COMPUTATION_ID).addDataBuilder().setKey(ByteString.copyFromUtf8("0000000000000001")).addValuesBuilder().setTag(ByteString.copyFromUtf8("//+uint")).setStateFamily(StreamingDataflowWorkerTest.DEFAULT_PARDO_STATE_FAMILY).getValueBuilder().setTimestamp(0).setData(state);
                server.addDataToOffer(dataResponse.build());
            }
            // Three GetWork requests and commits
            for (int i = 0; i < 3; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append("work {\n");
                sb.append("  computation_id: \"computation\"\n");
                sb.append("  input_data_watermark: 0\n");
                sb.append("  work {\n");
                sb.append("    key: \"0000000000000001\"\n");
                sb.append("    sharding_key: 1\n");
                sb.append("    work_token: ");
                sb.append(i);
                sb.append("    cache_token: 1");
                sb.append("\n");
                if (i > 0) {
                    int previousCheckpoint = i - 1;
                    sb.append("    source_state {\n");
                    sb.append("      state: \"");
                    sb.append(((char) (previousCheckpoint)));
                    sb.append("\"\n");
                    // We'll elide the finalize ids since it's not necessary to trigger the finalizer
                    // for this test.
                    sb.append("    }\n");
                }
                sb.append("  }\n");
                sb.append("}\n");
                server.addWorkToOffer(buildInput(sb.toString(), null));
                Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(1);
                Windmill.WorkItemCommitRequest commit = result.get(((long) (i)));
                UnsignedLong finalizeId = UnsignedLong.fromLongBits(commit.getSourceStateUpdates().getFinalizeIds(0));
                sb = new StringBuilder();
                sb.append("key: \"0000000000000001\"\n");
                sb.append("sharding_key: 1\n");
                sb.append("work_token: ");
                sb.append(i);
                sb.append("\n");
                sb.append("cache_token: 1\n");
                sb.append("output_messages {\n");
                sb.append("  destination_stream_id: \"out\"\n");
                sb.append("  bundles {\n");
                sb.append("    key: \"0000000000000001\"\n");
                int messageNum = i;
                sb.append("    messages {\n");
                sb.append("      timestamp: ");
                sb.append((messageNum * 1000));
                sb.append("\n");
                sb.append("      data: \"0:");
                sb.append(messageNum);
                sb.append("\"\n");
                sb.append("    }\n");
                sb.append("    messages_ids: \"\"\n");
                sb.append("  }\n");
                sb.append("}\n");
                if (i == 0) {
                    sb.append("value_updates {\n");
                    sb.append("  tag: \"//+uint\"\n");
                    sb.append("  value {\n");
                    sb.append("    timestamp: 0\n");
                    sb.append("    data: \"");
                    sb.append(((char) (42)));
                    sb.append("\"\n");
                    sb.append("  }\n");
                    sb.append("  state_family: \"parDoStateFamily\"\n");
                    sb.append("}\n");
                }
                int sourceState = i;
                sb.append("source_state_updates {\n");
                sb.append("  state: \"");
                sb.append(((char) (sourceState)));
                sb.append("\"\n");
                sb.append("  finalize_ids: ");
                sb.append(finalizeId);
                sb.append("}\n");
                sb.append("source_watermark: ");
                sb.append(((sourceState + 1) * 1000));
                sb.append("\n");
                sb.append("source_backlog_bytes: 7\n");
                // The commit will include a timer to clean up state - this timer is irrelevant
                // for the current test.
                Assert.assertThat(setValuesTimestamps(commit.toBuilder().clearOutputTimers()).build(), Matchers.equalTo(setMessagesMetadata(NO_FIRING, CoderUtils.encodeToByteArray(CollectionCoder.of(GlobalWindow.Coder.INSTANCE), ImmutableList.of(GlobalWindow.INSTANCE)), parseCommitRequest(sb.toString())).build()));
            }
        } finally {
            maxUnboundedBundleSize = originalMaxUnboundedBundleSize;
        }
    }

    private static class FanoutFn extends DoFn<String, String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            StringBuilder builder = new StringBuilder(1000000);
            for (int i = 0; i < 1000000; i++) {
                builder.append(' ');
            }
            String largeString = builder.toString();
            for (int i = 0; i < 3000; i++) {
                c.output(largeString);
            }
        }
    }

    @Test
    public void testHugeCommits() throws Exception {
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(StringUtf8Coder.of()), makeDoFnInstruction(new StreamingDataflowWorkerTest.FanoutFn(), 0, StringUtf8Coder.of()), makeSinkInstruction(StringUtf8Coder.of(), 0));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        StreamingDataflowWorkerOptions options = createTestingPipelineOptions(server);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, options, true);
        worker.start();
        server.addWorkToOffer(makeInput(0, TimeUnit.MILLISECONDS.toMicros(0)));
        server.waitForAndGetCommits(0);
        worker.stop();
    }

    private static class SlowDoFn extends DoFn<String, String> {
        @ProcessElement
        public void processElement(ProcessContext c) throws Exception {
            Thread.sleep(1000);
            c.output(c.element());
        }
    }

    @Test
    public void testActiveWorkRefresh() throws Exception {
        if (streamingEngine) {
            // TODO: This test needs to be adapted to work with streamingEngine=true.
            return;
        }
        List<ParallelInstruction> instructions = Arrays.asList(makeSourceInstruction(StringUtf8Coder.of()), makeDoFnInstruction(new StreamingDataflowWorkerTest.SlowDoFn(), 0, StringUtf8Coder.of()), makeSinkInstruction(StringUtf8Coder.of(), 0));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        StreamingDataflowWorkerOptions options = createTestingPipelineOptions(server);
        options.setActiveWorkRefreshPeriodMillis(100);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, options, true);
        worker.start();
        server.addWorkToOffer(makeInput(0, TimeUnit.MILLISECONDS.toMicros(0)));
        server.waitForAndGetCommits(1);
        worker.stop();
        // This graph will not normally produce any GetData calls, so all such calls are from active
        // work refreshes.
        Assert.assertThat(server.numGetDataRequests(), Matchers.greaterThan(0));
    }

    /**
     * For each input element, emits a large string.
     */
    private static class InflateDoFn extends DoFn<ValueWithRecordId<KV<Integer, Integer>>, String> {
        final int inflatedSize;

        /**
         * For each input elements, outputs a string of this length
         */
        InflateDoFn(int inflatedSize) {
            this.inflatedSize = inflatedSize;
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            char[] chars = new char[inflatedSize];
            Arrays.fill(chars, ' ');
            c.output(new String(chars));
        }
    }

    @Test
    public void testLimitOnOutputBundleSize() throws Exception {
        // This verifies that ReadOperation, StreamingModeExecutionContext, and windmill sinks
        // coordinate to limit size of an output bundle.
        List<Integer> finalizeTracker = Lists.newArrayList();
        TestCountingSource.setFinalizeTracker(finalizeTracker);
        final int numMessagesInCustomSourceShard = 100000;// 100K input messages.

        final int inflatedSizePerMessage = 10000;// x10k => 1GB total output size.

        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(makeUnboundedSourcePipeline(numMessagesInCustomSourceShard, new StreamingDataflowWorkerTest.InflateDoFn(inflatedSizePerMessage)), createTestingPipelineOptions(server), false);
        worker.start();
        // Test new key.
        server.addWorkToOffer(buildInput(("work {" + (((((((("  computation_id: \"computation\"" + "  input_data_watermark: 0") + "  work {") + "    key: \"0000000000000001\"") + "    sharding_key: 1") + "    work_token: 1") + "    cache_token: 1") + "  }") + "}")), null));
        // Matcher to ensure that commit size is within 10% of max bundle size.
        Matcher<Integer> isWithinBundleSizeLimits = Matchers.both(Matchers.greaterThan((((MAX_SINK_BYTES) * 9) / 10))).and(Matchers.lessThan((((MAX_SINK_BYTES) * 11) / 10)));
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(1);
        Windmill.WorkItemCommitRequest commit = result.get(1L);
        Assert.assertThat(commit.getSerializedSize(), isWithinBundleSizeLimits);
        // Try another bundle
        server.addWorkToOffer(buildInput(("work {" + (((((((("  computation_id: \"computation\"" + "  input_data_watermark: 0") + "  work {") + "    key: \"0000000000000001\"") + "    sharding_key: 1") + "    work_token: 2") + "    cache_token: 1") + "  }") + "}")), null));
        result = server.waitForAndGetCommits(1);
        commit = result.get(2L);
        Assert.assertThat(commit.getSerializedSize(), isWithinBundleSizeLimits);
    }

    @Test
    public void testLimitOnOutputBundleSizeWithMultipleSinks() throws Exception {
        // Same as testLimitOnOutputBundleSize(), but with 3 sinks for the stage rather than one.
        // Verifies that output bundle size has same limit even with multiple sinks.
        List<Integer> finalizeTracker = Lists.newArrayList();
        TestCountingSource.setFinalizeTracker(finalizeTracker);
        final int numMessagesInCustomSourceShard = 100000;// 100K input messages.

        final int inflatedSizePerMessage = 10000;// x10k => 1GB total output size.

        List<ParallelInstruction> instructions = new ArrayList<>();
        instructions.addAll(makeUnboundedSourcePipeline(numMessagesInCustomSourceShard, new StreamingDataflowWorkerTest.InflateDoFn(inflatedSizePerMessage)));
        // add two more sinks
        instructions.add(makeSinkInstruction(((StreamingDataflowWorkerTest.DEFAULT_DESTINATION_STREAM_ID) + "-1"), StringUtf8Coder.of(), 1, GlobalWindow.Coder.INSTANCE));
        instructions.add(makeSinkInstruction(((StreamingDataflowWorkerTest.DEFAULT_DESTINATION_STREAM_ID) + "-2"), StringUtf8Coder.of(), 1, GlobalWindow.Coder.INSTANCE));
        FakeWindmillServer server = new FakeWindmillServer(errorCollector);
        StreamingDataflowWorker worker = /* publishCounters */
        makeWorker(instructions, createTestingPipelineOptions(server), true);
        worker.start();
        // Test new key.
        server.addWorkToOffer(buildInput(("work {" + (((((((("  computation_id: \"computation\"" + "  input_data_watermark: 0") + "  work {") + "    key: \"0000000000000001\"") + "    sharding_key: 1") + "    work_token: 1") + "    cache_token: 1") + "  }") + "}")), null));
        // Matcher to ensure that commit size is within 10% of max bundle size.
        Matcher<Integer> isWithinBundleSizeLimits = Matchers.both(Matchers.greaterThan((((MAX_SINK_BYTES) * 9) / 10))).and(Matchers.lessThan((((MAX_SINK_BYTES) * 11) / 10)));
        Map<Long, Windmill.WorkItemCommitRequest> result = server.waitForAndGetCommits(1);
        Windmill.WorkItemCommitRequest commit = result.get(1L);
        Assert.assertThat(commit.getSerializedSize(), isWithinBundleSizeLimits);
        // Try another bundle
        server.addWorkToOffer(buildInput(("work {" + (((((((("  computation_id: \"computation\"" + "  input_data_watermark: 0") + "  work {") + "    key: \"0000000000000001\"") + "    sharding_key: 1") + "    work_token: 2") + "    cache_token: 1") + "  }") + "}")), null));
        result = server.waitForAndGetCommits(1);
        commit = result.get(2L);
        Assert.assertThat(commit.getSerializedSize(), isWithinBundleSizeLimits);
    }
}

