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


import GlobalWindow.INSTANCE;
import NoopProfileScope.NOOP;
import Windmill.SourceState;
import Windmill.WorkItem;
import Windmill.WorkItemCommitRequest;
import WorkerCustomSources.DEFAULT_NUM_BUNDLES_LIMIT;
import WorkerCustomSources.MAX_UNBOUNDED_BUNDLE_READ_TIME;
import WorkerCustomSources.maxUnboundedBundleSize;
import com.google.api.client.util.Base64;
import com.google.api.services.dataflow.model.ApproximateReportedProgress;
import com.google.api.services.dataflow.model.DerivedSource;
import com.google.api.services.dataflow.model.ReportedParallelism;
import com.google.api.services.dataflow.model.Source;
import com.google.api.services.dataflow.model.SourceSplitResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.apache.beam.runners.core.metrics.ExecutionStateSampler;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.worker.StreamingModeExecutionContext.StreamingModeExecutionStateRegistry;
import org.apache.beam.runners.dataflow.worker.WorkerCustomSources.SplittableOnlyBoundedSource;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.apache.beam.runners.dataflow.worker.counters.NameContext;
import org.apache.beam.runners.dataflow.worker.testing.TestCountingSource;
import org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.BoundedSource.BoundedReader;
import org.apache.beam.sdk.io.CountingSource;
import org.apache.beam.sdk.io.OffsetBasedSource;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.ExpectedLogs;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.ValueWithRecordId;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Preconditions;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static BoundedReaderIterator.getReaderProgress;
import static BoundedReaderIterator.longToParallelism;
import static WorkerCustomSources.DEFAULT_NUM_BUNDLES_LIMIT;
import static WorkerCustomSources.maxUnboundedBundleSize;


/**
 * Tests for {@link WorkerCustomSources}.
 */
@RunWith(JUnit4.class)
public class WorkerCustomSourcesTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public ExpectedLogs logged = ExpectedLogs.none(WorkerCustomSources.class);

    @Test
    public void testSplitAndReadBundlesBack() throws Exception {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        Source source = WorkerCustomSourcesTest.translateIOToCloudSource(CountingSource.upTo(10L), options);
        List<WindowedValue<Integer>> elems = WorkerCustomSourcesTest.readElemsFromSource(options, source);
        Assert.assertEquals(10L, elems.size());
        for (long i = 0; i < 10L; i++) {
            Assert.assertEquals(valueInGlobalWindow(i), elems.get(((int) (i))));
        }
        SourceSplitResponse response = /* desiredBundleSizeBytes for two longs */
        /* numBundles limit */
        /* API limit */
        WorkerCustomSourcesTest.performSplit(source, options, 16L, null, null);
        Assert.assertEquals("SOURCE_SPLIT_OUTCOME_SPLITTING_HAPPENED", response.getOutcome());
        List<DerivedSource> bundles = response.getBundles();
        Assert.assertEquals(5, bundles.size());
        for (int i = 0; i < 5; ++i) {
            DerivedSource bundle = bundles.get(i);
            Assert.assertEquals("SOURCE_DERIVATION_MODE_INDEPENDENT", bundle.getDerivationMode());
            Source bundleSource = bundle.getSource();
            Assert.assertTrue(bundleSource.getDoesNotNeedSplitting());
            bundleSource.setCodec(source.getCodec());
            List<WindowedValue<Integer>> xs = WorkerCustomSourcesTest.readElemsFromSource(options, bundleSource);
            MatcherAssert.assertThat(("Failed on bundle " + i), xs, Matchers.contains(valueInGlobalWindow((0L + (2 * i))), valueInGlobalWindow((1L + (2 * i)))));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProgressAndSourceSplitTranslation() throws Exception {
        // Same as previous test, but now using BasicSerializableSourceFormat wrappers.
        // We know that the underlying reader behaves correctly (because of the previous test),
        // now check that we are wrapping it correctly.
        DataflowPipelineOptions options = PipelineOptionsFactory.create().as(DataflowPipelineOptions.class);
        NativeReader<WindowedValue<Integer>> reader = // executionContext
        ((NativeReader<WindowedValue<Integer>>) (ReaderRegistry.defaultRegistry().create(WorkerCustomSourcesTest.translateIOToCloudSource(CountingSource.upTo(10), options), options, null, TestOperationContext.create())));
        try (NativeReader.NativeReaderIterator<WindowedValue<Integer>> iterator = reader.iterator()) {
            Assert.assertTrue(iterator.start());
            Assert.assertEquals(valueInGlobalWindow(0L), iterator.getCurrent());
            Assert.assertEquals(0.0, SourceTranslationUtils.readerProgressToCloudProgress(iterator.getProgress()).getFractionConsumed().doubleValue(), 1.0E-6);
            Assert.assertTrue(iterator.advance());
            Assert.assertEquals(valueInGlobalWindow(1L), iterator.getCurrent());
            Assert.assertEquals(0.1, SourceTranslationUtils.readerProgressToCloudProgress(iterator.getProgress()).getFractionConsumed().doubleValue(), 1.0E-6);
            Assert.assertTrue(iterator.advance());
            Assert.assertEquals(valueInGlobalWindow(2L), iterator.getCurrent());
            Assert.assertNull(iterator.requestDynamicSplit(ReaderTestUtils.splitRequestAtFraction(0)));
            Assert.assertNull(iterator.requestDynamicSplit(ReaderTestUtils.splitRequestAtFraction(0.1F)));
            WorkerCustomSources.BoundedSourceSplit<Integer> sourceSplit = ((WorkerCustomSources.BoundedSourceSplit<Integer>) (iterator.requestDynamicSplit(ReaderTestUtils.splitRequestAtFraction(0.5F))));
            Assert.assertNotNull(sourceSplit);
            MatcherAssert.assertThat(readFromSource(sourceSplit.primary, options), Matchers.contains(0L, 1L, 2L, 3L, 4L));
            MatcherAssert.assertThat(readFromSource(sourceSplit.residual, options), Matchers.contains(5L, 6L, 7L, 8L, 9L));
            sourceSplit = ((WorkerCustomSources.BoundedSourceSplit<Integer>) (iterator.requestDynamicSplit(ReaderTestUtils.splitRequestAtFraction(0.8F))));
            Assert.assertNotNull(sourceSplit);
            MatcherAssert.assertThat(readFromSource(sourceSplit.primary, options), Matchers.contains(0L, 1L, 2L, 3L));
            MatcherAssert.assertThat(readFromSource(sourceSplit.residual, options), Matchers.contains(4L));
            Assert.assertTrue(iterator.advance());
            Assert.assertEquals(valueInGlobalWindow(3L), iterator.getCurrent());
            Assert.assertFalse(iterator.advance());
        }
    }

    /**
     * A source that cannot do anything. Intended to be overridden for testing of individual methods.
     */
    private static class MockSource extends BoundedSource<Integer> {
        @Override
        public List<? extends BoundedSource<Integer>> split(long desiredBundleSizeBytes, PipelineOptions options) throws Exception {
            return Arrays.asList(this);
        }

        @Override
        public void validate() {
        }

        @Override
        public long getEstimatedSizeBytes(PipelineOptions options) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BoundedReader<Integer> createReader(PipelineOptions options) throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "<unknown>";
        }

        @Override
        public Coder<Integer> getDefaultOutputCoder() {
            return BigEndianIntegerCoder.of();
        }
    }

    private static class SourceProducingInvalidSplits extends WorkerCustomSourcesTest.MockSource {
        private String description;

        private String errorMessage;

        private SourceProducingInvalidSplits(String description, String errorMessage) {
            this.description = description;
            this.errorMessage = errorMessage;
        }

        @Override
        public List<? extends BoundedSource<Integer>> split(long desiredBundleSizeBytes, PipelineOptions options) throws Exception {
            Preconditions.checkState(((errorMessage) == null), "Unexpected invalid source");
            return Arrays.asList(new WorkerCustomSourcesTest.SourceProducingInvalidSplits("goodBundle", null), new WorkerCustomSourcesTest.SourceProducingInvalidSplits("badBundle", "intentionally invalid"));
        }

        @Override
        public void validate() {
            Preconditions.checkState(((errorMessage) == null), errorMessage);
        }

        @Override
        public String toString() {
            return description;
        }
    }

    @Test
    public void testSplittingProducedInvalidSource() throws Exception {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        Source cloudSource = WorkerCustomSourcesTest.translateIOToCloudSource(new WorkerCustomSourcesTest.SourceProducingInvalidSplits("original", null), options);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(Matchers.allOf(Matchers.containsString("Splitting a valid source produced an invalid source"), Matchers.containsString("original"), Matchers.containsString("badBundle")));
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(Matchers.containsString("intentionally invalid")));
        /* desiredBundleSizeBytes */
        /* numBundles limit */
        /* API limit */
        WorkerCustomSourcesTest.performSplit(cloudSource, options, null, null, null);
    }

    private static class FailingReader extends BoundedSource.BoundedReader<Integer> {
        private BoundedSource<Integer> source;

        private FailingReader(BoundedSource<Integer> source) {
            this.source = source;
        }

        @Override
        public BoundedSource<Integer> getCurrentSource() {
            return source;
        }

        @Override
        public boolean start() throws IOException {
            throw new IOException("Intentional error");
        }

        @Override
        public boolean advance() throws IOException {
            throw new IllegalStateException("Should have failed in start()");
        }

        @Override
        public Integer getCurrent() throws NoSuchElementException {
            throw new IllegalStateException("Should have failed in start()");
        }

        @Override
        public Instant getCurrentTimestamp() throws NoSuchElementException {
            throw new IllegalStateException("Should have failed in start()");
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public Double getFractionConsumed() {
            return null;
        }

        @Override
        public BoundedSource<Integer> splitAtFraction(double fraction) {
            return null;
        }
    }

    private static class SourceProducingFailingReader extends WorkerCustomSourcesTest.MockSource {
        @Override
        public BoundedReader<Integer> createReader(PipelineOptions options) throws IOException {
            return new WorkerCustomSourcesTest.FailingReader(this);
        }

        @Override
        public String toString() {
            return "Some description";
        }
    }

    @Test
    public void testFailureToStartReadingIncludesSourceDetails() throws Exception {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        Source source = WorkerCustomSourcesTest.translateIOToCloudSource(new WorkerCustomSourcesTest.SourceProducingFailingReader(), options);
        // Unfortunately Hamcrest doesn't have a matcher that can match on the exception's
        // printStackTrace(), however we just want to verify that the error and source description
        // would be contained in the exception *somewhere*, not necessarily in the top-level
        // Exception object. So instead we use Throwables.getStackTraceAsString and match on that.
        try {
            WorkerCustomSourcesTest.readElemsFromSource(options, source);
            Assert.fail("Expected to fail");
        } catch (Exception e) {
            MatcherAssert.assertThat(getStackTraceAsString(e), Matchers.allOf(Matchers.containsString("Intentional error"), Matchers.containsString("Some description")));
        }
    }

    @Test
    public void testUnboundedSplits() throws Exception {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        Source source = serializeToCloudSource(new TestCountingSource(Integer.MAX_VALUE), options);
        List<String> serializedSplits = getStrings(source.getSpec(), WorkerCustomSources.SERIALIZED_SOURCE_SPLITS, null);
        Assert.assertEquals(20, serializedSplits.size());
        for (String serializedSplit : serializedSplits) {
            Assert.assertTrue(((deserializeFromByteArray(Base64.decodeBase64(serializedSplit), "source")) instanceof TestCountingSource));
        }
    }

    @Test
    public void testReadUnboundedReader() throws Exception {
        CounterSet counterSet = new CounterSet();
        StreamingModeExecutionStateRegistry executionStateRegistry = new StreamingModeExecutionStateRegistry(null);
        StreamingModeExecutionContext context = /* stateNameMap= */
        /* stateCache= */
        new StreamingModeExecutionContext(counterSet, "computationId", new ReaderCache(), ImmutableMap.of(), null, StreamingStepMetricsContainer.createRegistry(), new org.apache.beam.runners.dataflow.worker.DataflowExecutionContext.DataflowExecutionStateTracker(ExecutionStateSampler.newForTest(), executionStateRegistry.getState(NameContext.forStage("stageName"), "other", null, NOOP), counterSet, PipelineOptionsFactory.create(), "test-work-item-id"), executionStateRegistry, Long.MAX_VALUE);
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        options.setNumWorkers(5);
        ByteString state = ByteString.EMPTY;
        /* Incremented in inner loop */
        for (int i = 0; i < (10 * (maxUnboundedBundleSize));) {
            // Initialize streaming context with state from previous iteration.
            // input watermark
            // output watermark
            // synchronized processing time
            // StateReader
            // StateFetcher
            context.start("key", // Source state.
            // Required proto field, unused.
            // key is zero-padded index.
            WorkItem.newBuilder().setKey(ByteString.copyFromUtf8("0000000000000001")).setWorkToken(0).setSourceState(SourceState.newBuilder().setState(state).build()).build(), new Instant(0), null, null, null, null, WorkItemCommitRequest.newBuilder());
            @SuppressWarnings({ "unchecked", "rawtypes" })
            NativeReader<WindowedValue<ValueWithRecordId<KV<Integer, Integer>>>> reader = ((NativeReader) (WorkerCustomSources.create(((CloudObject) (serializeToCloudSource(new TestCountingSource(Integer.MAX_VALUE), options).getSpec())), options, context)));
            // Verify data.
            Instant beforeReading = Instant.now();
            int numReadOnThisIteration = 0;
            for (WindowedValue<ValueWithRecordId<KV<Integer, Integer>>> value : ReaderUtils.readAllFromReader(reader)) {
                Assert.assertEquals(KV.of(0, i), value.getValue().getValue());
                Assert.assertArrayEquals(encodeToByteArray(KvCoder.of(VarIntCoder.of(), VarIntCoder.of()), KV.of(0, i)), value.getValue().getId());
                MatcherAssert.assertThat(value.getWindows(), Matchers.contains(((BoundedWindow) (INSTANCE))));
                Assert.assertEquals(i, value.getTimestamp().getMillis());
                i++;
                numReadOnThisIteration++;
            }
            Instant afterReading = Instant.now();
            MatcherAssert.assertThat(getStandardSeconds(), Matchers.lessThanOrEqualTo(((MAX_UNBOUNDED_BUNDLE_READ_TIME.getStandardSeconds()) + 1)));
            MatcherAssert.assertThat(numReadOnThisIteration, Matchers.lessThanOrEqualTo(maxUnboundedBundleSize));
            // Extract and verify state modifications.
            context.flushState();
            state = context.getOutputBuilder().getSourceStateUpdates().getState();
            // CountingSource's watermark is the last record + 1.  i is now one past the last record,
            // so the expected watermark is i millis.
            Assert.assertEquals(TimeUnit.MILLISECONDS.toMicros(i), context.getOutputBuilder().getSourceWatermark());
            Assert.assertEquals(1, context.getOutputBuilder().getSourceStateUpdates().getFinalizeIdsList().size());
            Assert.assertNotNull(context.getCachedReader());
            Assert.assertEquals(7L, context.getBacklogBytes());
        }
    }

    @Test
    public void testLargeSerializedSizeResplits() throws Exception {
        final long apiSizeLimitForTest = 5 * 1024;
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        // Figure out how many splits of CountingSource are needed to exceed the API limits, using an
        // extra factor of 2 to ensure that we go over the limits.
        BoundedSource<Long> justForSizing = CountingSource.upTo(1000000L);
        long size = DataflowApiUtils.computeSerializedSizeBytes(WorkerCustomSourcesTest.translateIOToCloudSource(justForSizing, options));
        long numberToSplitToExceedLimit = (2 * apiSizeLimitForTest) / size;
        checkState((numberToSplitToExceedLimit < (DEFAULT_NUM_BUNDLES_LIMIT)), ("This test expects the number of splits to be less than %s " + "to avoid using SplittableOnlyBoundedSource"), DEFAULT_NUM_BUNDLES_LIMIT);
        // Generate a CountingSource and split it into the desired number of splits
        // (desired size = 8 bytes, 1 long), triggering the re-split with a larger bundle size.
        Source source = WorkerCustomSourcesTest.translateIOToCloudSource(CountingSource.upTo(numberToSplitToExceedLimit), options);
        SourceSplitResponse split = /* numBundles limit */
        WorkerCustomSourcesTest.performSplit(source, options, 8L, null, apiSizeLimitForTest);
        logged.verifyWarn("too large for the Google Cloud Dataflow API");
        logged.verifyWarn(String.format("%d bundles", numberToSplitToExceedLimit));
        MatcherAssert.assertThat(((long) (split.getBundles().size())), Matchers.lessThan(numberToSplitToExceedLimit));
    }

    @Test
    public void testLargeNumberOfSplitsReturnsSplittableOnlyBoundedSources() throws Exception {
        final long apiSizeLimitForTest = 500 * 1024;
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        // Generate a CountingSource and split it into the desired number of splits
        // (desired size = 1 byte), triggering the re-split with a larger bundle size.
        // Thus below we expect to produce 451 splits.
        Source source = WorkerCustomSourcesTest.translateIOToCloudSource(CountingSource.upTo(451), options);
        SourceSplitResponse split = /* numBundles limit */
        WorkerCustomSourcesTest.performSplit(source, options, 1L, null, apiSizeLimitForTest);
        Assert.assertEquals(DEFAULT_NUM_BUNDLES_LIMIT, split.getBundles().size());
        // We expect that we would have the 100 splits that were generated from the initial
        // splitting done by CountingSource. The splits should encompass the counting sources for
        // 0-99, 100-199, 200-299, 300-355, 356, 357, ... 451
        for (int i = 0; i <= 3; ++i) {
            DerivedSource derivedSource = split.getBundles().get(i);
            // Make sure that we are setting the flag telling Dataflow that we need further splits.
            Assert.assertFalse(derivedSource.getSource().getDoesNotNeedSplitting());
            Object deserializedSource = WorkerCustomSources.deserializeFromCloudSource(derivedSource.getSource().getSpec());
            Assert.assertTrue((deserializedSource instanceof SplittableOnlyBoundedSource));
            SplittableOnlyBoundedSource<?> splittableOnlySource = ((SplittableOnlyBoundedSource<?>) (deserializedSource));
            List<? extends BoundedSource<?>> splitSources = splittableOnlySource.split(1L, options);
            int expectedNumSplits = (i < 3) ? 100 : 55;
            Assert.assertEquals(expectedNumSplits, splitSources.size());
            for (int j = 0; j < (splitSources.size()); ++j) {
                Assert.assertTrue(((splitSources.get(j)) instanceof OffsetBasedSource));
                OffsetBasedSource<?> offsetBasedSource = ((OffsetBasedSource<?>) (splitSources.get(j)));
                Assert.assertEquals(((i * 100) + j), offsetBasedSource.getStartOffset());
                Assert.assertEquals((((i * 100) + j) + 1), offsetBasedSource.getEndOffset());
            }
        }
        for (int i = 4; i < (DEFAULT_NUM_BUNDLES_LIMIT); ++i) {
            DerivedSource derivedSource = split.getBundles().get(i);
            // Make sure that we are not setting the flag telling Dataflow that we need further splits
            // for the individual counting sources
            Assert.assertTrue(derivedSource.getSource().getDoesNotNeedSplitting());
            Object deserializedSource = WorkerCustomSources.deserializeFromCloudSource(derivedSource.getSource().getSpec());
            Assert.assertTrue((deserializedSource instanceof OffsetBasedSource));
            OffsetBasedSource<?> offsetBasedSource = ((OffsetBasedSource<?>) (deserializedSource));
            Assert.assertEquals((351 + i), offsetBasedSource.getStartOffset());
            Assert.assertEquals(((351 + i) + 1), offsetBasedSource.getEndOffset());
        }
    }

    @Test
    public void testOversplittingDesiredBundleSizeScaledFirst() throws Exception {
        // Create a source that greatly oversplits but with coalescing/compression it would still fit
        // under the API limit. Test that the API limit gets applied first, so oversplitting is
        // reduced.
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        Source source = WorkerCustomSourcesTest.translateIOToCloudSource(CountingSource.upTo(8000), options);
        // Without either limit, produces 1000 bundles, total size ~500kb.
        // With only numBundles limit 100, produces 100 bundles, total size ~72kb.
        // With only apiSize limit = 10kb, 72 bundles, total size ~40kb (over the limit but oh well).
        // With numBundles limit 100 and apiSize limit 10kb, should produce 72 bundles.
        // On the other hand, if the numBundles limit of 100 was applied first, we'd get 100 bundles.
        SourceSplitResponse bundledWithOnlyNumBundlesLimit = /* numBundles limit */
        /* API size limit */
        WorkerCustomSourcesTest.performSplit(source, options, 8L, 100, (10000 * 1024L));
        Assert.assertEquals(100, bundledWithOnlyNumBundlesLimit.getBundles().size());
        MatcherAssert.assertThat(DataflowApiUtils.computeSerializedSizeBytes(bundledWithOnlyNumBundlesLimit), Matchers.greaterThan((10 * 1024L)));
        SourceSplitResponse bundledWithOnlySizeLimit = /* numBundles limit */
        /* API size limit */
        WorkerCustomSourcesTest.performSplit(source, options, 8L, 1000000, (10 * 1024L));
        int numBundlesWithOnlySizeLimit = bundledWithOnlySizeLimit.getBundles().size();
        MatcherAssert.assertThat(numBundlesWithOnlySizeLimit, Matchers.lessThan(100));
        SourceSplitResponse bundledWithSizeLimit = WorkerCustomSourcesTest.performSplit(source, options, 8L, 100, (10 * 1024L));
        Assert.assertEquals(numBundlesWithOnlySizeLimit, bundledWithSizeLimit.getBundles().size());
    }

    @Test
    public void testTooLargeSplitResponseFails() throws Exception {
        DataflowPipelineOptions options = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
        Source source = WorkerCustomSourcesTest.translateIOToCloudSource(CountingSource.upTo(1000), options);
        expectedException.expectMessage("[0, 1000)");
        expectedException.expectMessage("larger than the limit 100");
        WorkerCustomSourcesTest.performSplit(source, options, 8L, 10, 100L);
    }

    private static class TestBoundedReader extends BoundedReader<Void> {
        @Nullable
        private final Object fractionConsumed;

        private final Object splitPointsConsumed;

        private final Object splitPointsRemaining;

        public TestBoundedReader(@Nullable
        Object fractionConsumed, Object splitPointsConsumed, Object splitPointsRemaining) {
            this.fractionConsumed = fractionConsumed;
            this.splitPointsConsumed = splitPointsConsumed;
            this.splitPointsRemaining = splitPointsRemaining;
        }

        @Override
        public BoundedSource<Void> getCurrentSource() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean start() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean advance() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Void getCurrent() throws NoSuchElementException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nullable
        public Double getFractionConsumed() {
            if (((fractionConsumed) instanceof Number) || ((fractionConsumed) == null)) {
                return ((Number) (fractionConsumed)).doubleValue();
            } else {
                throw ((RuntimeException) (fractionConsumed));
            }
        }

        @Override
        public long getSplitPointsConsumed() {
            if ((splitPointsConsumed) instanceof Number) {
                return ((Number) (splitPointsConsumed)).longValue();
            } else {
                throw ((RuntimeException) (splitPointsConsumed));
            }
        }

        @Override
        public long getSplitPointsRemaining() {
            if ((splitPointsRemaining) instanceof Number) {
                return ((Number) (splitPointsRemaining)).longValue();
            } else {
                throw ((RuntimeException) (splitPointsRemaining));
            }
        }
    }

    @Test
    public void testLongToParallelism() {
        // Invalid values should return null
        Assert.assertNull(BoundedReaderIterator.longToParallelism((-10)));
        Assert.assertNull(BoundedReaderIterator.longToParallelism((-1)));
        // Valid values should be finite and non-negative
        ReportedParallelism p = BoundedReaderIterator.longToParallelism(0);
        Assert.assertEquals(p.getValue(), 0.0, 1.0E-6);
        p = BoundedReaderIterator.longToParallelism(100);
        Assert.assertEquals(p.getValue(), 100.0, 1.0E-6);
        p = longToParallelism(Long.MAX_VALUE);
        Assert.assertEquals(p.getValue(), Long.MAX_VALUE, 1.0E-6);
    }

    @Test
    public void testGetReaderProgress() {
        ApproximateReportedProgress progress = getReaderProgress(new WorkerCustomSourcesTest.TestBoundedReader(0.75, 1, 2));
        Assert.assertEquals(0.75, progress.getFractionConsumed(), 1.0E-6);
        Assert.assertEquals(1.0, progress.getConsumedParallelism().getValue(), 1.0E-6);
        Assert.assertEquals(2.0, progress.getRemainingParallelism().getValue(), 1.0E-6);
        progress = BoundedReaderIterator.getReaderProgress(new WorkerCustomSourcesTest.TestBoundedReader(null, (-1), 4));
        Assert.assertNull(progress.getFractionConsumed());
        Assert.assertNull(progress.getConsumedParallelism());
        Assert.assertEquals(4.0, progress.getRemainingParallelism().getValue(), 1.0E-6);
        progress = BoundedReaderIterator.getReaderProgress(new WorkerCustomSourcesTest.TestBoundedReader(null, (-1), (-2)));
        Assert.assertNull(progress.getFractionConsumed());
        Assert.assertNull(progress.getConsumedParallelism());
        Assert.assertNull(progress.getRemainingParallelism());
    }

    @Test
    public void testGetReaderProgressThrowing() {
        // Fraction throws, remaining and consumed still okay.
        RuntimeException fractionError = new UnsupportedOperationException("fraction");
        ApproximateReportedProgress progress = getReaderProgress(new WorkerCustomSourcesTest.TestBoundedReader(fractionError, 1, 2));
        Assert.assertNull(progress.getFractionConsumed());
        Assert.assertEquals(1.0, progress.getConsumedParallelism().getValue(), 1.0E-6);
        Assert.assertEquals(2.0, progress.getRemainingParallelism().getValue(), 1.0E-6);
        logged.verifyWarn("fraction");
        // Consumed throws, fraction and remaining still okay.
        RuntimeException consumedError = new UnsupportedOperationException("consumed parallelism");
        progress = BoundedReaderIterator.getReaderProgress(new WorkerCustomSourcesTest.TestBoundedReader(0.75, consumedError, 3));
        Assert.assertEquals(0.75, progress.getFractionConsumed(), 1.0E-6);
        Assert.assertNull(progress.getConsumedParallelism());
        Assert.assertEquals(3.0, progress.getRemainingParallelism().getValue(), 1.0E-6);
        logged.verifyWarn("consumed parallelism");
        // Remaining throws, consumed and remaining still okay.
        RuntimeException remainingError = new UnsupportedOperationException("remaining parallelism");
        progress = BoundedReaderIterator.getReaderProgress(new WorkerCustomSourcesTest.TestBoundedReader(0.5, 5, remainingError));
        Assert.assertEquals(0.5, progress.getFractionConsumed(), 1.0E-6);
        Assert.assertEquals(5.0, progress.getConsumedParallelism().getValue(), 1.0E-6);
        Assert.assertNull(progress.getRemainingParallelism());
        logged.verifyWarn("remaining parallelism");
    }
}

