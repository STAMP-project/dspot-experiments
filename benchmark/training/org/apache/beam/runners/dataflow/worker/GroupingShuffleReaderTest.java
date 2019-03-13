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


import Experiment.IntertransformIO;
import NativeReader.DynamicSplitResult;
import com.google.api.client.util.Base64;
import com.google.api.services.dataflow.model.ApproximateReportedProgress;
import com.google.api.services.dataflow.model.Position;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.beam.runners.core.metrics.ExecutionStateSampler;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.dataflow.options.DataflowPipelineDebugOptions;
import org.apache.beam.runners.dataflow.worker.GroupingShuffleReader.GroupingShuffleReaderIterator;
import org.apache.beam.runners.dataflow.worker.counters.CounterName;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ByteArrayShufflePosition;
import org.apache.beam.runners.dataflow.worker.util.common.worker.NativeReader;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ShuffleEntry;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ShuffleReadCounterFactory;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.IterableCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.util.common.Reiterable;
import org.apache.beam.sdk.util.common.Reiterator;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for GroupingShuffleReader.
 */
@RunWith(JUnit4.class)
public class GroupingShuffleReaderTest {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private static final List<KV<Integer, List<KV<Integer, Integer>>>> NO_KVS = Collections.emptyList();

    private static final Instant timestamp = new Instant(123000);

    private static final IntervalWindow window = new IntervalWindow(GroupingShuffleReaderTest.timestamp, GroupingShuffleReaderTest.timestamp.plus(1000));

    private final ExecutionStateSampler sampler = ExecutionStateSampler.newForTest();

    private final ExecutionStateTracker tracker = new ExecutionStateTracker(sampler);

    private Closeable trackerCleanup;

    // As Shuffle records, {@code KV} is encoded as 10 records. Each records uses an integer as key
    // (4 bytes), and a {@code KV} of an integer key and value (each 4 bytes).
    // Overall {@code KV}s have a byte size of 25 * 4 = 100. Note that we also encode the
    // timestamp into the secondary key adding another 100 bytes.
    private static final List<KV<Integer, List<KV<Integer, Integer>>>> KVS = Arrays.asList(KV.of(1, Arrays.asList(KV.of(1, 11), KV.of(2, 12))), KV.of(2, Arrays.asList(KV.of(1, 21), KV.of(2, 22))), KV.of(3, Arrays.asList(KV.of(1, 31))), KV.of(4, Arrays.asList(KV.of(1, 41), KV.of(2, 42), KV.of(3, 43), KV.of(4, 44))), KV.of(5, Arrays.asList(KV.of(1, 51))));

    private static final String MOCK_STAGE_NAME = "mockStageName";

    private static final String MOCK_ORIGINAL_NAME_FOR_EXECUTING_STEP1 = "mockOriginalName1";

    private static final String MOCK_ORIGINAL_NAME_FOR_EXECUTING_STEP2 = "mockOriginalName2";

    private static final String MOCK_SYSTEM_NAME = "mockSystemName";

    private static final String MOCK_USER_NAME = "mockUserName";

    private static final String ORIGINAL_SHUFFLE_STEP_NAME = "originalName";

    /**
     * How many of the values with each key are to be read. Note that the order matters as the
     * conversion to ordinal is used below.
     */
    private enum ValuesToRead {

        /**
         * Don't even ask for the values iterator.
         */
        SKIP_VALUES,
        /**
         * Get the iterator, but don't read any values.
         */
        READ_NO_VALUES,
        /**
         * Read just the first value.
         */
        READ_ONE_VALUE,
        /**
         * Read all the values.
         */
        READ_ALL_VALUES,
        /**
         * Read all the values twice.
         */
        READ_ALL_VALUES_TWICE;}

    @Test
    public void testReadEmptyShuffleData() throws Exception {
        /* do not sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.NO_KVS, false, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES);
        /* sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.NO_KVS, true, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES);
    }

    @Test
    public void testReadEmptyShuffleDataSkippingValues() throws Exception {
        /* do not sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.NO_KVS, false, GroupingShuffleReaderTest.ValuesToRead.SKIP_VALUES);
        /* sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.NO_KVS, true, GroupingShuffleReaderTest.ValuesToRead.SKIP_VALUES);
    }

    @Test
    public void testReadNonEmptyShuffleData() throws Exception {
        /* do not sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.KVS, false, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES);
        /* sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.KVS, true, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES);
    }

    @Test
    public void testReadNonEmptyShuffleDataTwice() throws Exception {
        /* do not sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.KVS, false, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES_TWICE);
        /* sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.KVS, true, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES_TWICE);
    }

    @Test
    public void testReadNonEmptyShuffleDataReadingOneValue() throws Exception {
        /* do not sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.KVS, false, GroupingShuffleReaderTest.ValuesToRead.READ_ONE_VALUE);
        /* sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.KVS, true, GroupingShuffleReaderTest.ValuesToRead.READ_ONE_VALUE);
    }

    @Test
    public void testReadNonEmptyShuffleDataReadingNoValues() throws Exception {
        /* do not sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.KVS, false, GroupingShuffleReaderTest.ValuesToRead.READ_NO_VALUES);
        /* sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.KVS, true, GroupingShuffleReaderTest.ValuesToRead.READ_NO_VALUES);
    }

    @Test
    public void testReadNonEmptyShuffleDataSkippingValues() throws Exception {
        /* do not sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.KVS, false, GroupingShuffleReaderTest.ValuesToRead.SKIP_VALUES);
        /* sort values */
        runTestReadFromShuffle(GroupingShuffleReaderTest.KVS, true, GroupingShuffleReaderTest.ValuesToRead.SKIP_VALUES);
    }

    @Test
    public void testBytesReadNonEmptyShuffleDataUnsorted() throws Exception {
        /* do not sort values */
        runTestBytesReadCounter(GroupingShuffleReaderTest.KVS, false, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES, 200L);
    }

    @Test
    public void testBytesReadNonEmptyShuffleDataSorted() throws Exception {
        /* sort values */
        runTestBytesReadCounter(GroupingShuffleReaderTest.KVS, true, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES, 200L);
    }

    @Test
    public void testBytesReadNonEmptyShuffleDataTwiceUnsorted() throws Exception {
        /* do not sort values */
        runTestBytesReadCounter(GroupingShuffleReaderTest.KVS, false, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES_TWICE, 200L);
    }

    @Test
    public void testBytesReadNonEmptyShuffleDataTwiceSorted() throws Exception {
        /* sort values */
        runTestBytesReadCounter(GroupingShuffleReaderTest.KVS, true, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES_TWICE, 200L);
    }

    @Test
    public void testBytesReadNonEmptyShuffleDataReadingOneValueUnsorted() throws Exception {
        /* do not sort values */
        runTestBytesReadCounter(GroupingShuffleReaderTest.KVS, false, GroupingShuffleReaderTest.ValuesToRead.READ_ONE_VALUE, 200L);
    }

    @Test
    public void testBytesReadNonEmptyShuffleDataReadingOneValueSorted() throws Exception {
        /* sort values */
        runTestBytesReadCounter(GroupingShuffleReaderTest.KVS, true, GroupingShuffleReaderTest.ValuesToRead.READ_ONE_VALUE, 200L);
    }

    @Test
    public void testBytesReadNonEmptyShuffleDataSkippingValuesUnsorted() throws Exception {
        /* do not sort values */
        runTestBytesReadCounter(GroupingShuffleReaderTest.KVS, false, GroupingShuffleReaderTest.ValuesToRead.SKIP_VALUES, 200L);
    }

    @Test
    public void testBytesReadNonEmptyShuffleDataSkippingValuesSorted() throws Exception {
        /* sort values */
        runTestBytesReadCounter(GroupingShuffleReaderTest.KVS, true, GroupingShuffleReaderTest.ValuesToRead.SKIP_VALUES, 200L);
    }

    @Test
    public void testBytesReadEmptyShuffleData() throws Exception {
        /* do not sort values */
        runTestBytesReadCounter(GroupingShuffleReaderTest.NO_KVS, false, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES, 0L);
        /* sort values */
        runTestBytesReadCounter(GroupingShuffleReaderTest.NO_KVS, true, GroupingShuffleReaderTest.ValuesToRead.READ_ALL_VALUES, 0L);
    }

    @Test
    public void testReadFromShuffleDataAndFailToSplit() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        BatchModeExecutionContext context = BatchModeExecutionContext.forTesting(options, "testStage");
        final int kFirstShard = 0;
        TestShuffleReader shuffleReader = new TestShuffleReader();
        final int kNumRecords = 2;
        for (int i = 0; i < kNumRecords; ++i) {
            byte[] key = CoderUtils.encodeToByteArray(BigEndianIntegerCoder.of(), i);
            shuffleReader.addEntry(new ShuffleEntry(GroupingShuffleReaderTest.fabricatePosition(kFirstShard, key), key, GroupingShuffleReaderTest.EMPTY_BYTE_ARRAY, key));
        }
        // Note that TestShuffleReader start/end positions are in the
        // space of keys not the positions (TODO: should probably always
        // use positions instead).
        String stop = Base64.encodeBase64URLSafeString(GroupingShuffleReaderTest.fabricatePosition(kNumRecords).getPosition());
        TestOperationContext operationContext = TestOperationContext.create();
        GroupingShuffleReader<Integer, Integer> groupingShuffleReader = /* do not sort values */
        new GroupingShuffleReader(options, null, null, stop, WindowedValue.getFullCoder(KvCoder.of(BigEndianIntegerCoder.of(), IterableCoder.of(BigEndianIntegerCoder.of())), IntervalWindow.getCoder()), context, operationContext, ShuffleReadCounterFactory.INSTANCE, false);
        Assert.assertFalse(shuffleReader.isClosed());
        try (GroupingShuffleReaderIterator<Integer, Integer> iter = groupingShuffleReader.iterator(shuffleReader)) {
            // Poke the iterator so we can test dynamic splitting.
            Assert.assertTrue(iter.start());
            // Cannot split since the value provided is past the current stop position.
            Assert.assertNull(iter.requestDynamicSplit(ReaderTestUtils.splitRequestAtPosition(makeShufflePosition((kNumRecords + 1), null))));
            byte[] key = CoderUtils.encodeToByteArray(BigEndianIntegerCoder.of(), 0);
            // Cannot split since the split position is identical with the position of the record
            // that was just returned.
            Assert.assertNull(iter.requestDynamicSplit(ReaderTestUtils.splitRequestAtPosition(makeShufflePosition(kFirstShard, key))));
            // Cannot split since the requested split position comes before current position
            Assert.assertNull(iter.requestDynamicSplit(ReaderTestUtils.splitRequestAtPosition(makeShufflePosition(kFirstShard, null))));
            int numRecordsReturned = 1;// including start() above.

            for (; iter.advance(); ++numRecordsReturned) {
                iter.getCurrent().getValue();// ignored

            }
            Assert.assertEquals(kNumRecords, numRecordsReturned);
            // Cannot split since all input was consumed.
            Assert.assertNull(iter.requestDynamicSplit(ReaderTestUtils.splitRequestAtPosition(makeShufflePosition(kFirstShard, null))));
        }
        Assert.assertTrue(shuffleReader.isClosed());
    }

    @Test
    public void testConsumedParallelism() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        BatchModeExecutionContext context = BatchModeExecutionContext.forTesting(options, "testStage");
        final int kFirstShard = 0;
        TestShuffleReader shuffleReader = new TestShuffleReader();
        final int kNumRecords = 5;
        for (int i = 0; i < kNumRecords; ++i) {
            byte[] key = CoderUtils.encodeToByteArray(BigEndianIntegerCoder.of(), i);
            ShuffleEntry entry = new ShuffleEntry(GroupingShuffleReaderTest.fabricatePosition(kFirstShard, i), key, GroupingShuffleReaderTest.EMPTY_BYTE_ARRAY, key);
            shuffleReader.addEntry(entry);
        }
        TestOperationContext operationContext = TestOperationContext.create();
        GroupingShuffleReader<Integer, Integer> groupingShuffleReader = /* do not sort values */
        new GroupingShuffleReader(options, null, null, null, WindowedValue.getFullCoder(KvCoder.of(BigEndianIntegerCoder.of(), IterableCoder.of(BigEndianIntegerCoder.of())), IntervalWindow.getCoder()), context, operationContext, ShuffleReadCounterFactory.INSTANCE, false);
        Assert.assertFalse(shuffleReader.isClosed());
        try (GroupingShuffleReaderIterator<Integer, Integer> iter = groupingShuffleReader.iterator(shuffleReader)) {
            // Iterator hasn't started; consumed parallelism is 0.
            Assert.assertEquals(0.0, ReaderTestUtils.consumedParallelismFromProgress(iter.getProgress()), 0);
            // The only way to set a stop *position* in tests is via a split. To do that,
            // we must call hasNext() first.
            // Should return entry at key 0.
            Assert.assertTrue(iter.start());
            // Iterator just started; consumed parallelism is 0.
            Assert.assertEquals(0.0, SourceTranslationUtils.readerProgressToCloudProgress(iter.getProgress()).getConsumedParallelism().getValue(), 0);
            Assert.assertNotNull(iter.requestDynamicSplit(ReaderTestUtils.splitRequestAtPosition(makeShufflePosition(GroupingShuffleReaderTest.fabricatePosition(kFirstShard, 2).immediateSuccessor().getPosition()))));
            // Split does not affect consumed parallelism; consumed parallelism is still 0.
            Assert.assertEquals(0.0, ReaderTestUtils.consumedParallelismFromProgress(iter.getProgress()), 0);
            // Should return entry at key 1.
            Assert.assertTrue(iter.advance());
            Assert.assertEquals(1.0, ReaderTestUtils.consumedParallelismFromProgress(iter.getProgress()), 0);
            // Should return entry at key 2 (last key, because the stop position
            // is its immediate successor.) Consumed parallelism increments by one to 2.
            Assert.assertTrue(iter.advance());
            Assert.assertEquals(2.0, ReaderTestUtils.consumedParallelismFromProgress(iter.getProgress()), 0);
            // Iterator advanced by one and consumes one more split point (total consumed: 3).
            Assert.assertFalse(iter.advance());
            Assert.assertEquals(3.0, ReaderTestUtils.consumedParallelismFromProgress(iter.getProgress()), 0);
        }
        Assert.assertTrue(shuffleReader.isClosed());
    }

    @Test
    public void testReadFromShuffleAndDynamicSplit() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        BatchModeExecutionContext context = BatchModeExecutionContext.forTesting(options, "testStage");
        TestOperationContext operationContext = TestOperationContext.create();
        GroupingShuffleReader<Integer, Integer> groupingShuffleReader = /* do not sort values */
        new GroupingShuffleReader(options, null, null, null, WindowedValue.getFullCoder(KvCoder.of(BigEndianIntegerCoder.of(), IterableCoder.of(BigEndianIntegerCoder.of())), IntervalWindow.getCoder()), context, operationContext, ShuffleReadCounterFactory.INSTANCE, false);
        groupingShuffleReader.perOperationPerDatasetBytesCounter = counterFactory().longSum(CounterName.named("dax-shuffle-test-wf-read-bytes"));
        TestShuffleReader shuffleReader = new TestShuffleReader();
        final int kNumRecords = 10;
        final int kFirstShard = 0;
        final int kSecondShard = 1;
        // Setting up two shards with kNumRecords each; keys are unique
        // (hence groups of values for the same key are singletons)
        // therefore each record comes with a unique position constructed.
        for (int i = 0; i < kNumRecords; ++i) {
            byte[] keyByte = CoderUtils.encodeToByteArray(BigEndianIntegerCoder.of(), i);
            ShuffleEntry entry = new ShuffleEntry(GroupingShuffleReaderTest.fabricatePosition(kFirstShard, keyByte), keyByte, GroupingShuffleReaderTest.EMPTY_BYTE_ARRAY, keyByte);
            shuffleReader.addEntry(entry);
        }
        for (int i = kNumRecords; i < (2 * kNumRecords); ++i) {
            byte[] keyByte = CoderUtils.encodeToByteArray(BigEndianIntegerCoder.of(), i);
            ShuffleEntry entry = new ShuffleEntry(GroupingShuffleReaderTest.fabricatePosition(kSecondShard, keyByte), keyByte, GroupingShuffleReaderTest.EMPTY_BYTE_ARRAY, keyByte);
            shuffleReader.addEntry(entry);
        }
        int i = 0;
        Assert.assertFalse(shuffleReader.isClosed());
        try (GroupingShuffleReaderIterator<Integer, Integer> iter = groupingShuffleReader.iterator(shuffleReader)) {
            // Poke the iterator so we can test dynamic splitting.
            Assert.assertTrue(iter.start());
            ++i;
            Assert.assertNull(iter.requestDynamicSplit(ReaderTestUtils.splitRequestAtPosition(new Position())));
            // Split at the shard boundary
            NativeReader.DynamicSplitResult dynamicSplitResult = iter.requestDynamicSplit(ReaderTestUtils.splitRequestAtPosition(makeShufflePosition(kSecondShard, null)));
            Assert.assertNotNull(dynamicSplitResult);
            Assert.assertEquals(Base64.encodeBase64URLSafeString(GroupingShuffleReaderTest.fabricatePosition(kSecondShard).getPosition()), ReaderTestUtils.positionFromSplitResult(dynamicSplitResult).getShufflePosition());
            for (; iter.advance(); ++i) {
                // iter.getCurrent() is supposed to be side-effect-free and give the same result if called
                // repeatedly. Test that this is indeed the case.
                iter.getCurrent();
                iter.getCurrent();
                KV<Integer, Reiterable<Integer>> elem = iter.getCurrent().getValue();
                int key = elem.getKey();
                Assert.assertEquals(key, i);
                Reiterable<Integer> valuesIterable = elem.getValue();
                Reiterator<Integer> valuesIterator = valuesIterable.iterator();
                int j = 0;
                while (valuesIterator.hasNext()) {
                    Assert.assertTrue(valuesIterator.hasNext());
                    Assert.assertTrue(valuesIterator.hasNext());
                    int value = valuesIterator.next();
                    Assert.assertEquals(value, i);
                    ++j;
                } 
                Assert.assertFalse(valuesIterator.hasNext());
                Assert.assertFalse(valuesIterator.hasNext());
                Assert.assertEquals(1, j);
            }
            Assert.assertFalse(iter.advance());
        }
        Assert.assertTrue(shuffleReader.isClosed());
        Assert.assertEquals(i, kNumRecords);
        // There are 10 Shuffle records that each encode an integer key (4 bytes) and integer value (4
        // bytes). We therefore expect to read 80 bytes.
        Assert.assertEquals(80L, ((long) (groupingShuffleReader.perOperationPerDatasetBytesCounter.getAggregate())));
    }

    @Test
    public void testGetApproximateProgress() throws Exception {
        // Store the positions of all KVs returned.
        List<ByteArrayShufflePosition> positionsList = new ArrayList<>();
        PipelineOptions options = PipelineOptionsFactory.create();
        BatchModeExecutionContext context = BatchModeExecutionContext.forTesting(options, "testStage");
        TestOperationContext operationContext = TestOperationContext.create();
        GroupingShuffleReader<Integer, Integer> groupingShuffleReader = /* do not sort values */
        new GroupingShuffleReader(options, null, null, null, WindowedValue.getFullCoder(KvCoder.of(BigEndianIntegerCoder.of(), IterableCoder.of(BigEndianIntegerCoder.of())), IntervalWindow.getCoder()), context, operationContext, ShuffleReadCounterFactory.INSTANCE, false);
        TestShuffleReader shuffleReader = new TestShuffleReader();
        final int kNumRecords = 10;
        for (int i = 0; i < kNumRecords; ++i) {
            ByteArrayShufflePosition position = GroupingShuffleReaderTest.fabricatePosition(i);
            byte[] keyByte = CoderUtils.encodeToByteArray(BigEndianIntegerCoder.of(), i);
            positionsList.add(position);
            ShuffleEntry entry = new ShuffleEntry(position, keyByte, GroupingShuffleReaderTest.EMPTY_BYTE_ARRAY, keyByte);
            shuffleReader.addEntry(entry);
        }
        Assert.assertFalse(shuffleReader.isClosed());
        try (GroupingShuffleReaderIterator<Integer, Integer> iter = groupingShuffleReader.iterator(shuffleReader)) {
            Integer i = 0;
            for (boolean more = iter.start(); more; more = iter.advance()) {
                ApproximateReportedProgress progress = SourceTranslationUtils.readerProgressToCloudProgress(iter.getProgress());
                Assert.assertNotNull(progress.getPosition().getShufflePosition());
                // Compare returned position with the expected position.
                Assert.assertEquals(positionsList.get(i).encodeBase64(), progress.getPosition().getShufflePosition());
                WindowedValue<KV<Integer, Reiterable<Integer>>> elem = iter.getCurrent();
                Assert.assertEquals(i, elem.getValue().getKey());
                i++;
            }
            Assert.assertFalse(iter.advance());
            // Cannot split since all input was consumed.
            Position proposedSplitPosition = new Position();
            String stop = Base64.encodeBase64URLSafeString(GroupingShuffleReaderTest.fabricatePosition(0).getPosition());
            proposedSplitPosition.setShufflePosition(stop);
            Assert.assertNull(iter.requestDynamicSplit(SourceTranslationUtils.toDynamicSplitRequest(ReaderTestUtils.approximateSplitRequestAtPosition(proposedSplitPosition))));
        }
        Assert.assertTrue(shuffleReader.isClosed());
    }

    @Test
    public void testShuffleReadCounterMultipleExecutingSteps() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        options.as(DataflowPipelineDebugOptions.class).setExperiments(Lists.newArrayList(IntertransformIO.getName()));
        BatchModeExecutionContext context = BatchModeExecutionContext.forTesting(options, "testStage");
        final int kFirstShard = 0;
        TestShuffleReader shuffleReader = new TestShuffleReader();
        final int kNumRecords = 10;
        for (int i = 0; i < kNumRecords; ++i) {
            byte[] key = CoderUtils.encodeToByteArray(BigEndianIntegerCoder.of(), i);
            shuffleReader.addEntry(new ShuffleEntry(GroupingShuffleReaderTest.fabricatePosition(kFirstShard, key), key, GroupingShuffleReaderTest.EMPTY_BYTE_ARRAY, key));
        }
        TestShuffleReadCounterFactory shuffleReadCounterFactory = new TestShuffleReadCounterFactory();
        // Note that TestShuffleReader start/end positions are in the
        // space of keys not the positions (TODO: should probably always
        // use positions instead).
        String stop = Base64.encodeBase64URLSafeString(GroupingShuffleReaderTest.fabricatePosition(kNumRecords).getPosition());
        TestOperationContext operationContext = TestOperationContext.create();
        GroupingShuffleReader<Integer, Integer> groupingShuffleReader = /* do not sort values */
        new GroupingShuffleReader(options, null, null, stop, WindowedValue.getFullCoder(KvCoder.of(BigEndianIntegerCoder.of(), IterableCoder.of(BigEndianIntegerCoder.of())), IntervalWindow.getCoder()), context, operationContext, shuffleReadCounterFactory, false);
        Assert.assertFalse(shuffleReader.isClosed());
        try (GroupingShuffleReaderIterator<Integer, Integer> iter = groupingShuffleReader.iterator(shuffleReader)) {
            // Poke the iterator so we can test dynamic splitting.
            Assert.assertTrue(iter.start());
            int numRecordsReturned = 1;// including start() above.

            for (; iter.advance(); ++numRecordsReturned) {
                if (numRecordsReturned > 5) {
                    setCurrentExecutionState(GroupingShuffleReaderTest.MOCK_ORIGINAL_NAME_FOR_EXECUTING_STEP2);
                }
                iter.getCurrent().getValue();// ignored

            }
            Assert.assertEquals(kNumRecords, numRecordsReturned);
        }
        Assert.assertTrue(shuffleReader.isClosed());
        Map<String, Long> expectedReadBytesMap = new HashMap<>();
        expectedReadBytesMap.put(GroupingShuffleReaderTest.MOCK_ORIGINAL_NAME_FOR_EXECUTING_STEP1, 48L);
        expectedReadBytesMap.put(GroupingShuffleReaderTest.MOCK_ORIGINAL_NAME_FOR_EXECUTING_STEP2, 32L);
        expectShuffleReadCounterEquals(shuffleReadCounterFactory, expectedReadBytesMap);
    }
}

