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
package org.apache.flink.streaming.connectors.kafka.internals;


import KafkaTopicPartitionStateSentinel.EARLIEST_OFFSET;
import KafkaTopicPartitionStateSentinel.GROUP_OFFSET;
import KafkaTopicPartitionStateSentinel.LATEST_OFFSET;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.flink.core.testutils.CheckedThread;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.connectors.kafka.testutils.TestSourceContext;
import org.apache.flink.streaming.runtime.tasks.ProcessingTimeService;
import org.apache.flink.streaming.runtime.tasks.TestProcessingTimeService;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link AbstractFetcher}.
 */
@SuppressWarnings("serial")
public class AbstractFetcherTest {
    @Test
    public void testIgnorePartitionStateSentinelInSnapshot() throws Exception {
        final String testTopic = "test topic name";
        Map<KafkaTopicPartition, Long> originalPartitions = new HashMap<>();
        originalPartitions.put(new KafkaTopicPartition(testTopic, 1), LATEST_OFFSET);
        originalPartitions.put(new KafkaTopicPartition(testTopic, 2), GROUP_OFFSET);
        originalPartitions.put(new KafkaTopicPartition(testTopic, 3), EARLIEST_OFFSET);
        TestSourceContext<Long> sourceContext = new TestSourceContext<>();
        AbstractFetcherTest.TestFetcher<Long> fetcher = new AbstractFetcherTest.TestFetcher(sourceContext, originalPartitions, null, null, new TestProcessingTimeService(), 0);
        synchronized(sourceContext.getCheckpointLock()) {
            HashMap<KafkaTopicPartition, Long> currentState = fetcher.snapshotCurrentState();
            fetcher.commitInternalOffsetsToKafka(currentState, new KafkaCommitCallback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onException(Throwable cause) {
                    throw new RuntimeException("Callback failed", cause);
                }
            });
            Assert.assertTrue(fetcher.getLastCommittedOffsets().isPresent());
            Assert.assertEquals(Collections.emptyMap(), fetcher.getLastCommittedOffsets().get());
        }
    }

    // ------------------------------------------------------------------------
    // Record emitting tests
    // ------------------------------------------------------------------------
    @Test
    public void testSkipCorruptedRecord() throws Exception {
        final String testTopic = "test topic name";
        Map<KafkaTopicPartition, Long> originalPartitions = new HashMap<>();
        originalPartitions.put(new KafkaTopicPartition(testTopic, 1), LATEST_OFFSET);
        TestSourceContext<Long> sourceContext = new TestSourceContext<>();
        AbstractFetcherTest.TestFetcher<Long> fetcher = /* periodic watermark assigner */
        /* punctuated watermark assigner */
        new AbstractFetcherTest.TestFetcher(sourceContext, originalPartitions, null, null, new TestProcessingTimeService(), 0);
        final KafkaTopicPartitionState<Object> partitionStateHolder = fetcher.subscribedPartitionStates().get(0);
        fetcher.emitRecord(1L, partitionStateHolder, 1L);
        fetcher.emitRecord(2L, partitionStateHolder, 2L);
        Assert.assertEquals(2L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(2L, partitionStateHolder.getOffset());
        // emit null record
        fetcher.emitRecord(null, partitionStateHolder, 3L);
        Assert.assertEquals(2L, sourceContext.getLatestElement().getValue().longValue());// the null record should be skipped

        Assert.assertEquals(3L, partitionStateHolder.getOffset());// the offset in state still should have advanced

    }

    @Test
    public void testSkipCorruptedRecordWithPunctuatedWatermarks() throws Exception {
        final String testTopic = "test topic name";
        Map<KafkaTopicPartition, Long> originalPartitions = new HashMap<>();
        originalPartitions.put(new KafkaTopicPartition(testTopic, 1), LATEST_OFFSET);
        TestSourceContext<Long> sourceContext = new TestSourceContext<>();
        TestProcessingTimeService processingTimeProvider = new TestProcessingTimeService();
        AbstractFetcherTest.TestFetcher<Long> fetcher = /* periodic watermark assigner */
        /* punctuated watermark assigner */
        new AbstractFetcherTest.TestFetcher(sourceContext, originalPartitions, null, new org.apache.flink.util.SerializedValue<org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks<Long>>(new AbstractFetcherTest.PunctuatedTestExtractor()), processingTimeProvider, 0);
        final KafkaTopicPartitionState<Object> partitionStateHolder = fetcher.subscribedPartitionStates().get(0);
        // elements generate a watermark if the timestamp is a multiple of three
        fetcher.emitRecord(1L, partitionStateHolder, 1L);
        fetcher.emitRecord(2L, partitionStateHolder, 2L);
        fetcher.emitRecord(3L, partitionStateHolder, 3L);
        Assert.assertEquals(3L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(3L, sourceContext.getLatestElement().getTimestamp());
        Assert.assertTrue(sourceContext.hasWatermark());
        Assert.assertEquals(3L, sourceContext.getLatestWatermark().getTimestamp());
        Assert.assertEquals(3L, partitionStateHolder.getOffset());
        // emit null record
        fetcher.emitRecord(null, partitionStateHolder, 4L);
        // no elements or watermarks should have been collected
        Assert.assertEquals(3L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(3L, sourceContext.getLatestElement().getTimestamp());
        Assert.assertFalse(sourceContext.hasWatermark());
        // the offset in state still should have advanced
        Assert.assertEquals(4L, partitionStateHolder.getOffset());
    }

    @Test
    public void testSkipCorruptedRecordWithPeriodicWatermarks() throws Exception {
        final String testTopic = "test topic name";
        Map<KafkaTopicPartition, Long> originalPartitions = new HashMap<>();
        originalPartitions.put(new KafkaTopicPartition(testTopic, 1), LATEST_OFFSET);
        TestSourceContext<Long> sourceContext = new TestSourceContext<>();
        TestProcessingTimeService processingTimeProvider = new TestProcessingTimeService();
        AbstractFetcherTest.TestFetcher<Long> fetcher = /* periodic watermark assigner */
        /* punctuated watermark assigner */
        new AbstractFetcherTest.TestFetcher(sourceContext, originalPartitions, new org.apache.flink.util.SerializedValue<org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks<Long>>(new AbstractFetcherTest.PeriodicTestExtractor()), null, processingTimeProvider, 10);
        final KafkaTopicPartitionState<Object> partitionStateHolder = fetcher.subscribedPartitionStates().get(0);
        // elements generate a watermark if the timestamp is a multiple of three
        fetcher.emitRecord(1L, partitionStateHolder, 1L);
        fetcher.emitRecord(2L, partitionStateHolder, 2L);
        fetcher.emitRecord(3L, partitionStateHolder, 3L);
        Assert.assertEquals(3L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(3L, sourceContext.getLatestElement().getTimestamp());
        Assert.assertEquals(3L, partitionStateHolder.getOffset());
        // advance timer for watermark emitting
        processingTimeProvider.setCurrentTime(10L);
        Assert.assertTrue(sourceContext.hasWatermark());
        Assert.assertEquals(3L, sourceContext.getLatestWatermark().getTimestamp());
        // emit null record
        fetcher.emitRecord(null, partitionStateHolder, 4L);
        // no elements should have been collected
        Assert.assertEquals(3L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(3L, sourceContext.getLatestElement().getTimestamp());
        // the offset in state still should have advanced
        Assert.assertEquals(4L, partitionStateHolder.getOffset());
        // no watermarks should be collected
        processingTimeProvider.setCurrentTime(20L);
        Assert.assertFalse(sourceContext.hasWatermark());
    }

    // ------------------------------------------------------------------------
    // Timestamps & watermarks tests
    // ------------------------------------------------------------------------
    @Test
    public void testPunctuatedWatermarks() throws Exception {
        final String testTopic = "test topic name";
        Map<KafkaTopicPartition, Long> originalPartitions = new HashMap<>();
        originalPartitions.put(new KafkaTopicPartition(testTopic, 7), LATEST_OFFSET);
        originalPartitions.put(new KafkaTopicPartition(testTopic, 13), LATEST_OFFSET);
        originalPartitions.put(new KafkaTopicPartition(testTopic, 21), LATEST_OFFSET);
        TestSourceContext<Long> sourceContext = new TestSourceContext<>();
        TestProcessingTimeService processingTimeProvider = new TestProcessingTimeService();
        AbstractFetcherTest.TestFetcher<Long> fetcher = /* periodic watermark assigner */
        new AbstractFetcherTest.TestFetcher(sourceContext, originalPartitions, null, new org.apache.flink.util.SerializedValue<org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks<Long>>(new AbstractFetcherTest.PunctuatedTestExtractor()), processingTimeProvider, 0);
        final KafkaTopicPartitionState<Object> part1 = fetcher.subscribedPartitionStates().get(0);
        final KafkaTopicPartitionState<Object> part2 = fetcher.subscribedPartitionStates().get(1);
        final KafkaTopicPartitionState<Object> part3 = fetcher.subscribedPartitionStates().get(2);
        // elements generate a watermark if the timestamp is a multiple of three
        // elements for partition 1
        fetcher.emitRecord(1L, part1, 1L);
        fetcher.emitRecord(2L, part1, 2L);
        fetcher.emitRecord(3L, part1, 3L);
        Assert.assertEquals(3L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(3L, sourceContext.getLatestElement().getTimestamp());
        Assert.assertFalse(sourceContext.hasWatermark());
        // elements for partition 2
        fetcher.emitRecord(12L, part2, 1L);
        Assert.assertEquals(12L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(12L, sourceContext.getLatestElement().getTimestamp());
        Assert.assertFalse(sourceContext.hasWatermark());
        // elements for partition 3
        fetcher.emitRecord(101L, part3, 1L);
        fetcher.emitRecord(102L, part3, 2L);
        Assert.assertEquals(102L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(102L, sourceContext.getLatestElement().getTimestamp());
        // now, we should have a watermark
        Assert.assertTrue(sourceContext.hasWatermark());
        Assert.assertEquals(3L, sourceContext.getLatestWatermark().getTimestamp());
        // advance partition 3
        fetcher.emitRecord(1003L, part3, 3L);
        fetcher.emitRecord(1004L, part3, 4L);
        fetcher.emitRecord(1005L, part3, 5L);
        Assert.assertEquals(1005L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(1005L, sourceContext.getLatestElement().getTimestamp());
        // advance partition 1 beyond partition 2 - this bumps the watermark
        fetcher.emitRecord(30L, part1, 4L);
        Assert.assertEquals(30L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(30L, sourceContext.getLatestElement().getTimestamp());
        Assert.assertTrue(sourceContext.hasWatermark());
        Assert.assertEquals(12L, sourceContext.getLatestWatermark().getTimestamp());
        // advance partition 2 again - this bumps the watermark
        fetcher.emitRecord(13L, part2, 2L);
        Assert.assertFalse(sourceContext.hasWatermark());
        fetcher.emitRecord(14L, part2, 3L);
        Assert.assertFalse(sourceContext.hasWatermark());
        fetcher.emitRecord(15L, part2, 3L);
        Assert.assertTrue(sourceContext.hasWatermark());
        Assert.assertEquals(15L, sourceContext.getLatestWatermark().getTimestamp());
    }

    @Test
    public void testPeriodicWatermarks() throws Exception {
        final String testTopic = "test topic name";
        Map<KafkaTopicPartition, Long> originalPartitions = new HashMap<>();
        originalPartitions.put(new KafkaTopicPartition(testTopic, 7), LATEST_OFFSET);
        originalPartitions.put(new KafkaTopicPartition(testTopic, 13), LATEST_OFFSET);
        originalPartitions.put(new KafkaTopicPartition(testTopic, 21), LATEST_OFFSET);
        TestSourceContext<Long> sourceContext = new TestSourceContext<>();
        TestProcessingTimeService processingTimeService = new TestProcessingTimeService();
        AbstractFetcherTest.TestFetcher<Long> fetcher = /* punctuated watermarks assigner */
        new AbstractFetcherTest.TestFetcher(sourceContext, originalPartitions, new org.apache.flink.util.SerializedValue<org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks<Long>>(new AbstractFetcherTest.PeriodicTestExtractor()), null, processingTimeService, 10);
        final KafkaTopicPartitionState<Object> part1 = fetcher.subscribedPartitionStates().get(0);
        final KafkaTopicPartitionState<Object> part2 = fetcher.subscribedPartitionStates().get(1);
        final KafkaTopicPartitionState<Object> part3 = fetcher.subscribedPartitionStates().get(2);
        // elements generate a watermark if the timestamp is a multiple of three
        // elements for partition 1
        fetcher.emitRecord(1L, part1, 1L);
        fetcher.emitRecord(2L, part1, 2L);
        fetcher.emitRecord(3L, part1, 3L);
        Assert.assertEquals(3L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(3L, sourceContext.getLatestElement().getTimestamp());
        // elements for partition 2
        fetcher.emitRecord(12L, part2, 1L);
        Assert.assertEquals(12L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(12L, sourceContext.getLatestElement().getTimestamp());
        // elements for partition 3
        fetcher.emitRecord(101L, part3, 1L);
        fetcher.emitRecord(102L, part3, 2L);
        Assert.assertEquals(102L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(102L, sourceContext.getLatestElement().getTimestamp());
        processingTimeService.setCurrentTime(10);
        // now, we should have a watermark (this blocks until the periodic thread emitted the watermark)
        Assert.assertEquals(3L, sourceContext.getLatestWatermark().getTimestamp());
        // advance partition 3
        fetcher.emitRecord(1003L, part3, 3L);
        fetcher.emitRecord(1004L, part3, 4L);
        fetcher.emitRecord(1005L, part3, 5L);
        Assert.assertEquals(1005L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(1005L, sourceContext.getLatestElement().getTimestamp());
        // advance partition 1 beyond partition 2 - this bumps the watermark
        fetcher.emitRecord(30L, part1, 4L);
        Assert.assertEquals(30L, sourceContext.getLatestElement().getValue().longValue());
        Assert.assertEquals(30L, sourceContext.getLatestElement().getTimestamp());
        processingTimeService.setCurrentTime(20);
        // this blocks until the periodic thread emitted the watermark
        Assert.assertEquals(12L, sourceContext.getLatestWatermark().getTimestamp());
        // advance partition 2 again - this bumps the watermark
        fetcher.emitRecord(13L, part2, 2L);
        fetcher.emitRecord(14L, part2, 3L);
        fetcher.emitRecord(15L, part2, 3L);
        processingTimeService.setCurrentTime(30);
        // this blocks until the periodic thread emitted the watermark
        long watermarkTs = sourceContext.getLatestWatermark().getTimestamp();
        Assert.assertTrue(((watermarkTs >= 13L) && (watermarkTs <= 15L)));
    }

    @Test
    public void testPeriodicWatermarksWithNoSubscribedPartitionsShouldYieldNoWatermarks() throws Exception {
        final String testTopic = "test topic name";
        Map<KafkaTopicPartition, Long> originalPartitions = new HashMap<>();
        TestSourceContext<Long> sourceContext = new TestSourceContext<>();
        TestProcessingTimeService processingTimeProvider = new TestProcessingTimeService();
        AbstractFetcherTest.TestFetcher<Long> fetcher = /* punctuated watermarks assigner */
        new AbstractFetcherTest.TestFetcher(sourceContext, originalPartitions, new org.apache.flink.util.SerializedValue<org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks<Long>>(new AbstractFetcherTest.PeriodicTestExtractor()), null, processingTimeProvider, 10);
        processingTimeProvider.setCurrentTime(10);
        // no partitions; when the periodic watermark emitter fires, no watermark should be emitted
        Assert.assertFalse(sourceContext.hasWatermark());
        // counter-test that when the fetcher does actually have partitions,
        // when the periodic watermark emitter fires again, a watermark really is emitted
        fetcher.addDiscoveredPartitions(Collections.singletonList(new KafkaTopicPartition(testTopic, 0)));
        fetcher.emitRecord(100L, fetcher.subscribedPartitionStates().get(0), 3L);
        processingTimeProvider.setCurrentTime(20);
        Assert.assertEquals(100, sourceContext.getLatestWatermark().getTimestamp());
    }

    @Test
    public void testConcurrentPartitionsDiscoveryAndLoopFetching() throws Exception {
        // test data
        final KafkaTopicPartition testPartition = new KafkaTopicPartition("test", 42);
        // ----- create the test fetcher -----
        @SuppressWarnings("unchecked")
        SourceContext<String> sourceContext = new TestSourceContext();
        Map<KafkaTopicPartition, Long> partitionsWithInitialOffsets = Collections.singletonMap(testPartition, GROUP_OFFSET);
        final OneShotLatch fetchLoopWaitLatch = new OneShotLatch();
        final OneShotLatch stateIterationBlockLatch = new OneShotLatch();
        final AbstractFetcherTest.TestFetcher<String> fetcher = /* periodic assigner */
        /* punctuated assigner */
        new AbstractFetcherTest.TestFetcher(sourceContext, partitionsWithInitialOffsets, null, null, new TestProcessingTimeService(), 10, fetchLoopWaitLatch, stateIterationBlockLatch);
        // ----- run the fetcher -----
        final CheckedThread checkedThread = new CheckedThread() {
            @Override
            public void go() throws Exception {
                fetcher.runFetchLoop();
            }
        };
        checkedThread.start();
        // wait until state iteration begins before adding discovered partitions
        fetchLoopWaitLatch.await();
        fetcher.addDiscoveredPartitions(Collections.singletonList(testPartition));
        stateIterationBlockLatch.trigger();
        checkedThread.sync();
    }

    // ------------------------------------------------------------------------
    // Test mocks
    // ------------------------------------------------------------------------
    private static final class TestFetcher<T> extends AbstractFetcher<T, Object> {
        Optional<Map<KafkaTopicPartition, Long>> lastCommittedOffsets = Optional.empty();

        private final OneShotLatch fetchLoopWaitLatch;

        private final OneShotLatch stateIterationBlockLatch;

        TestFetcher(SourceContext<T> sourceContext, Map<KafkaTopicPartition, Long> assignedPartitionsWithStartOffsets, org.apache.flink.util.SerializedValue<org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks<T>> watermarksPeriodic, org.apache.flink.util.SerializedValue<org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks<T>> watermarksPunctuated, ProcessingTimeService processingTimeProvider, long autoWatermarkInterval) throws Exception {
            this(sourceContext, assignedPartitionsWithStartOffsets, watermarksPeriodic, watermarksPunctuated, processingTimeProvider, autoWatermarkInterval, null, null);
        }

        TestFetcher(SourceContext<T> sourceContext, Map<KafkaTopicPartition, Long> assignedPartitionsWithStartOffsets, org.apache.flink.util.SerializedValue<org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks<T>> watermarksPeriodic, org.apache.flink.util.SerializedValue<org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks<T>> watermarksPunctuated, ProcessingTimeService processingTimeProvider, long autoWatermarkInterval, OneShotLatch fetchLoopWaitLatch, OneShotLatch stateIterationBlockLatch) throws Exception {
            super(sourceContext, assignedPartitionsWithStartOffsets, watermarksPeriodic, watermarksPunctuated, processingTimeProvider, autoWatermarkInterval, AbstractFetcherTest.TestFetcher.class.getClassLoader(), new UnregisteredMetricsGroup(), false);
            this.fetchLoopWaitLatch = fetchLoopWaitLatch;
            this.stateIterationBlockLatch = stateIterationBlockLatch;
        }

        /**
         * Emulation of partition's iteration which is required for
         * {@link AbstractFetcherTest#testConcurrentPartitionsDiscoveryAndLoopFetching}.
         */
        @Override
        public void runFetchLoop() throws Exception {
            if ((fetchLoopWaitLatch) != null) {
                for (KafkaTopicPartitionState ignored : AbstractFetcherTest.TestFetcher.subscribedPartitionStates()) {
                    fetchLoopWaitLatch.trigger();
                    stateIterationBlockLatch.await();
                }
            } else {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public void cancel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object createKafkaPartitionHandle(KafkaTopicPartition partition) {
            return new Object();
        }

        @Override
        protected void doCommitInternalOffsetsToKafka(Map<KafkaTopicPartition, Long> offsets, @Nonnull
        KafkaCommitCallback callback) throws Exception {
            lastCommittedOffsets = Optional.of(offsets);
            callback.onSuccess();
        }

        public Optional<Map<KafkaTopicPartition, Long>> getLastCommittedOffsets() {
            return lastCommittedOffsets;
        }
    }

    // ------------------------------------------------------------------------
    private static class PeriodicTestExtractor implements org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks<Long> {
        private volatile long maxTimestamp = Long.MIN_VALUE;

        @Override
        public long extractTimestamp(Long element, long previousElementTimestamp) {
            maxTimestamp = Math.max(maxTimestamp, element);
            return element;
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(maxTimestamp);
        }
    }

    private static class PunctuatedTestExtractor implements org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks<Long> {
        @Override
        public long extractTimestamp(Long element, long previousElementTimestamp) {
            return element;
        }

        @Nullable
        @Override
        public Watermark checkAndGetNextWatermark(Long lastElement, long extractedTimestamp) {
            return (extractedTimestamp % 3) == 0 ? new Watermark(extractedTimestamp) : null;
        }
    }
}

