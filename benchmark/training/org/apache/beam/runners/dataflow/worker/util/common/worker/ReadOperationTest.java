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
package org.apache.beam.runners.dataflow.worker.util.common.worker;


import LongCounterMean.ZERO;
import NativeReader.DynamicSplitRequest;
import NativeReader.DynamicSplitResult;
import NativeReader.DynamicSplitResultWithPosition;
import NativeReader.Progress;
import ReadOperation.UPDATE_ON_EACH_ITERATION;
import com.google.api.services.dataflow.model.ApproximateReportedProgress;
import com.google.api.services.dataflow.model.ApproximateSplitRequest;
import com.google.api.services.dataflow.model.Position;
import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.beam.runners.dataflow.worker.InMemoryReader;
import org.apache.beam.runners.dataflow.worker.NameContextsForTests;
import org.apache.beam.runners.dataflow.worker.ReaderTestUtils;
import org.apache.beam.runners.dataflow.worker.SourceTranslationUtils;
import org.apache.beam.runners.dataflow.worker.TestOperationContext;
import org.apache.beam.runners.dataflow.worker.counters.Counter.CounterUpdateExtractor;
import org.apache.beam.runners.dataflow.worker.counters.CounterName;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.apache.beam.runners.dataflow.worker.counters.NameContext;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.range.OffsetRangeTracker;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Preconditions;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.apache.beam.runners.dataflow.worker.util.common.worker.TestOutputReceiver.TestOutputCounter.getMeanByteCounterName;
import static org.apache.beam.runners.dataflow.worker.util.common.worker.TestOutputReceiver.TestOutputCounter.getObjectCounterName;


/**
 * Tests for ReadOperation.
 */
@RunWith(JUnit4.class)
public class ReadOperationTest {
    private static final String COUNTER_PREFIX = "test-";

    private final CounterSet counterSet = new CounterSet();

    private OperationContext context = TestOperationContext.create(counterSet, NameContext.create("test", "ReadOperation", "ReadOperation", "ReadOperation"));

    /**
     * Tests that a {@link ReadOperation} has expected counters, and that their values are reasonable.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testRunReadOperation() throws Exception {
        ExecutorTestUtils.TestReader reader = new ExecutorTestUtils.TestReader("hi", "there", "", "bob");
        TestOutputReceiver receiver = new TestOutputReceiver(counterSet, NameContext.create("test", "test_receiver", "test_receiver", "test_receiver"));
        ReadOperation readOperation = ReadOperation.forTest(reader, receiver, context);
        readOperation.start();
        readOperation.finish();
        Assert.assertThat(receiver.outputElems, Matchers.containsInAnyOrder(((Object) ("hi")), "there", "", "bob"));
        CounterUpdateExtractor<?> updateExtractor = Mockito.mock(CounterUpdateExtractor.class);
        counterSet.extractUpdates(false, updateExtractor);
        Mockito.verify(updateExtractor).longSum(ArgumentMatchers.eq(getObjectCounterName("test_receiver_out")), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(4L));
        Mockito.verify(updateExtractor).longMean(ArgumentMatchers.eq(getMeanByteCounterName("test_receiver_out")), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(ZERO.addValue(14L, 4)));
        Mockito.verify(updateExtractor).longSum(ArgumentMatchers.eq(CounterName.named("ReadOperation-ByteCount")), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(10L));
        Mockito.verifyNoMoreInteractions(updateExtractor);
    }

    private static class ManualScheduler {
        private CyclicBarrier beforeRun = /* test thread and loop thread */
        new CyclicBarrier(2);

        private CyclicBarrier afterRun = /* test thread and loop thread */
        new CyclicBarrier(2);

        private final ScheduledExecutorService realExecutor = Executors.newSingleThreadScheduledExecutor();

        private final ScheduledExecutorService executor;

        public ManualScheduler() {
            executor = Mockito.mock(ScheduledExecutorService.class);
            Mockito.when(executor.scheduleAtFixedRate(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).then(( invocation) -> schedule(getArgumentAt(0, Runnable.class)));
        }

        public ScheduledExecutorService getExecutor() {
            return executor;
        }

        private ScheduledFuture<?> schedule(final Runnable runnable) {
            return realExecutor.schedule(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            beforeRun.await();
                            runnable.run();
                            afterRun.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            break;
                        }
                    } 
                }
            }, 0, TimeUnit.MILLISECONDS);
        }

        public void runOnce() throws Exception {
            beforeRun.await();
            afterRun.await();
        }
    }

    @Test
    public void testGetProgress() throws Exception {
        ReadOperationTest.MockReaderIterator iterator = new ReadOperationTest.MockReaderIterator(0, 5);
        ReadOperationTest.MockOutputReceiver receiver = new ReadOperationTest.MockOutputReceiver();
        ReadOperationTest.ManualScheduler scheduler = new ReadOperationTest.ManualScheduler();
        final ReadOperation readOperation = ReadOperation.forTest(new ReadOperationTest.MockReader(iterator), receiver, scheduler.getExecutor(), context);
        Thread thread = runReadLoopInThread(readOperation);
        for (int i = 0; i < 5; ++i) {
            // Reader currently blocked in start()/advance().
            // Ensure that getProgress() doesn't block while the reader advances.
            ApproximateReportedProgress progress = SourceTranslationUtils.readerProgressToCloudProgress(readOperation.getProgress());
            Long observedIndex = (progress == null) ? null : progress.getPosition().getRecordIndex().longValue();
            Assert.assertTrue(((("Actual: " + observedIndex) + " instead of ") + i), ((((i == 0) && (progress == null)) || (i == observedIndex)) || (i == (observedIndex + 1))));
            iterator.offerNext(i);
            // Now the reader is not blocked (instead the receiver is blocked): progress can be
            // updated. Wait for it to be updated.
            scheduler.runOnce();
            receiver.unblockProcess();
        }
        thread.join();
    }

    @Test
    public void testGetProgressIsNotStaleForSlowProcessedElements() throws Exception {
        ReadOperationTest.MockReaderIterator iterator = new ReadOperationTest.MockReaderIterator(0, 5);
        ReadOperationTest.MockOutputReceiver receiver = new ReadOperationTest.MockOutputReceiver();
        ReadOperationTest.ManualScheduler scheduler = new ReadOperationTest.ManualScheduler();
        final ReadOperation readOperation = ReadOperation.forTest(new ReadOperationTest.MockReader(iterator), receiver, scheduler.getExecutor(), context);
        Thread thread = runReadLoopInThread(readOperation);
        iterator.offerNext(0);// The reader will read 0, report progress 0 and be stuck processing it.

        // Wait for the progress thread to be triggered once.
        scheduler.runOnce();
        // Check that progress position is 0.
        ApproximateReportedProgress progress = SourceTranslationUtils.readerProgressToCloudProgress(readOperation.getProgress());
        Assert.assertEquals(0, progress.getPosition().getRecordIndex().longValue());
        // Quickly go through 1, 2, 3.
        receiver.unblockProcess();
        iterator.offerNext(1);
        receiver.unblockProcess();
        iterator.offerNext(2);
        receiver.unblockProcess();
        iterator.offerNext(3);
        // We are now blocked in processing 3.
        // Wait for the progress thread to be triggered at least once.
        scheduler.runOnce();
        // Check that the progress position is not stale.
        progress = SourceTranslationUtils.readerProgressToCloudProgress(readOperation.getProgress());
        Assert.assertEquals(3, progress.getPosition().getRecordIndex().longValue());
        // Complete the read.
        receiver.unblockProcess();
        iterator.offerNext(4);
        receiver.unblockProcess();
        thread.join();
    }

    @Test
    public void testCheckpoint() throws Exception {
        InMemoryReader<String> reader = new InMemoryReader(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"), 0, 10, StringUtf8Coder.of());
        final ReadOperation[] readOperationHolder = new ReadOperation[1];
        OutputReceiver receiver = new OutputReceiver() {
            @Override
            public void process(Object elem) throws Exception {
                ReadOperation readOperation = readOperationHolder[0];
                if ("1".equals(elem)) {
                    NativeReader.DynamicSplitResultWithPosition split = ((NativeReader.DynamicSplitResultWithPosition) (readOperation.requestCheckpoint()));
                    Assert.assertNotNull(split);
                    Assert.assertEquals(ReaderTestUtils.positionAtIndex(2L), SourceTranslationUtils.toCloudPosition(split.getAcceptedPosition()));
                    // Check that the progress has been recomputed.
                    ApproximateReportedProgress progress = SourceTranslationUtils.readerProgressToCloudProgress(readOperation.getProgress());
                    Assert.assertEquals(1, progress.getPosition().getRecordIndex().longValue());
                }
            }
        };
        ReadOperation readOperation = ReadOperation.forTest(reader, receiver, context);
        readOperation.setProgressUpdatePeriodMs(UPDATE_ON_EACH_ITERATION);
        readOperationHolder[0] = readOperation;
        // An unstarted ReadOperation refuses checkpoint requests.
        Assert.assertNull(readOperation.requestCheckpoint());
        readOperation.start();
        readOperation.finish();
        // Operation is now finished. Check that it refuses a checkpoint request.
        Assert.assertNull(readOperation.requestCheckpoint());
    }

    @Test
    public void testCheckpointDoesNotBlock() throws Exception {
        ReadOperationTest.MockReaderIterator iterator = new ReadOperationTest.MockReaderIterator(0, 10);
        ReadOperationTest.MockOutputReceiver receiver = new ReadOperationTest.MockOutputReceiver();
        ReadOperation readOperation = ReadOperation.forTest(new ReadOperationTest.MockReader(iterator), receiver, context);
        // We get the read loop started and then wait for it to block.  At that point, the iterator
        // should have a current of 1.  We then checkpoint without offering a new value.  That should
        // succeed with checkpoint position of 2.  By then offerring 1, then run loop should exit.
        Thread thread = runReadLoopInThread(readOperation);
        iterator.offerNext(0);
        iterator.blockInNextAdvance();
        receiver.unblockProcess();
        iterator.awaitBlockedInAdvance();
        NativeReader.DynamicSplitResultWithPosition split = ((NativeReader.DynamicSplitResultWithPosition) (readOperation.requestCheckpoint()));
        Assert.assertNotNull(split);
        Assert.assertEquals(ReaderTestUtils.positionAtIndex(2L), SourceTranslationUtils.toCloudPosition(split.getAcceptedPosition()));
        iterator.offerNext(1);
        receiver.unblockProcess();
        thread.join();
    }

    @Test
    public void testDynamicSplit() throws Exception {
        InMemoryReader<String> reader = new InMemoryReader(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"), 0, 10, StringUtf8Coder.of());
        final ReadOperation[] operationHolder = new ReadOperation[1];
        OutputReceiver receiver = new OutputReceiver() {
            @Override
            public void process(Object elem) throws Exception {
                ReadOperation readOperation = operationHolder[0];
                if ("1".equals(elem)) {
                    NativeReader.DynamicSplitResultWithPosition split = ((NativeReader.DynamicSplitResultWithPosition) (readOperation.requestDynamicSplit(ReaderTestUtils.splitRequestAtIndex(8L))));
                    Assert.assertNotNull(split);
                    Assert.assertEquals(ReaderTestUtils.positionAtIndex(8L), SourceTranslationUtils.toCloudPosition(split.getAcceptedPosition()));
                    // Check that the progress has been recomputed.
                    ApproximateReportedProgress progress = SourceTranslationUtils.readerProgressToCloudProgress(readOperation.getProgress());
                    Assert.assertEquals(1, progress.getPosition().getRecordIndex().longValue());
                } else
                    if ("3".equals(elem)) {
                        // Should accept a split at an earlier position than previously requested.
                        // Should reject a split at a later position than previously requested.
                        // Note that here we're testing our own MockReaderIterator class, so it's
                        // kind of pointless, but we're also testing that ReadOperation correctly
                        // relays the request to the iterator.
                        NativeReader.DynamicSplitResultWithPosition split = ((NativeReader.DynamicSplitResultWithPosition) (readOperation.requestDynamicSplit(ReaderTestUtils.splitRequestAtIndex(6L))));
                        Assert.assertNotNull(split);
                        Assert.assertEquals(ReaderTestUtils.positionAtIndex(6L), SourceTranslationUtils.toCloudPosition(split.getAcceptedPosition()));
                        split = ((NativeReader.DynamicSplitResultWithPosition) (readOperation.requestDynamicSplit(ReaderTestUtils.splitRequestAtIndex(6L))));
                        Assert.assertNull(split);
                    }

            }
        };
        ReadOperation readOperation = ReadOperation.forTest(reader, receiver, context);
        readOperation.setProgressUpdatePeriodMs(UPDATE_ON_EACH_ITERATION);
        operationHolder[0] = readOperation;
        // An unstarted ReadOperation refuses split requests.
        Assert.assertNull(readOperation.requestDynamicSplit(ReaderTestUtils.splitRequestAtIndex(8L)));
        readOperation.start();
        readOperation.finish();
        // Operation is now finished. Check that it refuses a split request.
        Assert.assertNull(readOperation.requestDynamicSplit(ReaderTestUtils.splitRequestAtIndex(5L)));
    }

    @Test
    public void testDynamicSplitDoesNotBlock() throws Exception {
        ReadOperationTest.MockReaderIterator iterator = new ReadOperationTest.MockReaderIterator(0, 10);
        ReadOperationTest.MockOutputReceiver receiver = new ReadOperationTest.MockOutputReceiver();
        ReadOperation readOperation = ReadOperation.forTest(new ReadOperationTest.MockReader(iterator), receiver, context);
        Thread thread = runReadLoopInThread(readOperation);
        iterator.offerNext(0);
        receiver.unblockProcess();
        // Read loop is blocked in next(). Do not offer another next item,
        // but check that we can still split while the read loop is blocked.
        NativeReader.DynamicSplitResultWithPosition split = ((NativeReader.DynamicSplitResultWithPosition) (readOperation.requestDynamicSplit(ReaderTestUtils.splitRequestAtIndex(5L))));
        Assert.assertNotNull(split);
        Assert.assertEquals(ReaderTestUtils.positionAtIndex(5L), SourceTranslationUtils.toCloudPosition(split.getAcceptedPosition()));
        for (int i = 1; i < 5; ++i) {
            iterator.offerNext(i);
            receiver.unblockProcess();
        }
        thread.join();
    }

    @Test
    public void testRaceBetweenCloseAndDynamicSplit() throws Exception {
        InMemoryReader<String> reader = new InMemoryReader(Arrays.asList("a", "b", "c", "d", "e"), 0, 5, StringUtf8Coder.of());
        TestOutputReceiver receiver = new TestOutputReceiver(StringUtf8Coder.of(), NameContextsForTests.nameContextForTest());
        final ReadOperation readOperation = ReadOperation.forTest(reader, receiver, context);
        readOperation.start();
        // Check that requestDynamicSplit is safe (no-op) if the operation is done with start()
        // but not yet done with finish()
        readOperation.requestDynamicSplit(ReaderTestUtils.splitRequestAtIndex(5L));
        readOperation.finish();
        // Check once more that requestDynamicSplit on a finished operation is also safe (no-op).
        readOperation.requestDynamicSplit(ReaderTestUtils.splitRequestAtIndex(5L));
    }

    @Test
    public void testAbortBetweenStartAndFinish() throws Exception {
        ReadOperationTest.MockOutputReceiver receiver = new ReadOperationTest.MockOutputReceiver();
        ExecutorTestUtils.TestReader reader = new ExecutorTestUtils.TestReader();
        ReadOperation op = ReadOperation.forTest(reader, receiver, context);
        op.start();
        op.abort();
        Assert.assertTrue(reader.aborted);
    }

    @Test
    public void testAbortAfterFinish() throws Exception {
        ReadOperationTest.MockOutputReceiver receiver = new ReadOperationTest.MockOutputReceiver();
        ExecutorTestUtils.TestReader reader = new ExecutorTestUtils.TestReader();
        ReadOperation op = ReadOperation.forTest(reader, receiver, context);
        op.start();
        op.finish();
        op.abort();
        Assert.assertTrue(reader.closed);
        Assert.assertTrue(reader.aborted);
    }

    private static class MockReaderIterator extends NativeReader.NativeReaderIterator<Integer> {
        private final OffsetRangeTracker tracker;

        private Exchanger<Integer> exchanger = new Exchanger<>();

        private int current;

        private volatile boolean isClosed;

        private boolean signalBlockedInAdvance = false;

        private CyclicBarrier blockedInAdvanceBarrier = /* test thread and loop thread */
        new CyclicBarrier(2);

        public MockReaderIterator(int from, int to) {
            this.tracker = new OffsetRangeTracker(from, to);
            this.current = from - 1;
        }

        @Override
        public boolean start() throws IOException {
            return advance();
        }

        /**
         * Not thread safe. Clients should avoid threads concurrently calling this method and
         * blockInNextAdvance, unless the thread in advance is blocked in or leaving exchangeCurrent.
         */
        @Override
        public boolean advance() throws IOException {
            if (!(tracker.tryReturnRecordAt(true, ((current) + 1)))) {
                return false;
            }
            ++(current);
            if (signalBlockedInAdvance) {
                signalBlockedInAdvance = false;
                try {
                    blockedInAdvanceBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }
            exchangeCurrent();
            return true;
        }

        private void exchangeCurrent() {
            try {
                current = exchanger.exchange(current);
            } catch (InterruptedException e) {
                throw new NoSuchElementException("interrupted");
            }
        }

        @Override
        public Integer getCurrent() {
            return current;
        }

        @Override
        public Progress getProgress() {
            Preconditions.checkState((!(isClosed)));
            return SourceTranslationUtils.cloudProgressToReaderProgress(new ApproximateReportedProgress().setPosition(new Position().setRecordIndex(((long) (current)))).setFractionConsumed(tracker.getFractionConsumed()));
        }

        @Override
        public DynamicSplitResult requestCheckpoint() {
            Preconditions.checkState((!(isClosed)));
            if (!(tracker.trySplitAtPosition(((current) + 1)))) {
                return null;
            }
            return new NativeReader.DynamicSplitResultWithPosition(SourceTranslationUtils.cloudPositionToReaderPosition(ReaderTestUtils.positionAtIndex(((current) + 1L))));
        }

        @Override
        public DynamicSplitResult requestDynamicSplit(NativeReader.DynamicSplitRequest splitRequest) {
            Preconditions.checkState((!(isClosed)));
            ApproximateSplitRequest approximateSplitRequest = SourceTranslationUtils.splitRequestToApproximateSplitRequest(splitRequest);
            int index = approximateSplitRequest.getPosition().getRecordIndex().intValue();
            if (!(tracker.trySplitAtPosition(index))) {
                return null;
            }
            return new NativeReader.DynamicSplitResultWithPosition(SourceTranslationUtils.cloudPositionToReaderPosition(approximateSplitRequest.getPosition()));
        }

        public int offerNext(int next) {
            try {
                return exchanger.exchange(next);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Not thread safe. Clients should avoid threads concurrently calling this method and advance,
         * unless the thread in advance is blocked in or leaving exchangeCurrent.
         */
        public void blockInNextAdvance() {
            signalBlockedInAdvance = true;
        }

        public void awaitBlockedInAdvance() {
            try {
                blockedInAdvanceBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException("interrupted");
            }
        }

        @Override
        public void close() throws IOException {
            isClosed = true;
        }
    }

    private static class MockReader extends NativeReader<Integer> {
        private NativeReaderIterator<Integer> iterator;

        private MockReader(NativeReaderIterator<Integer> iterator) {
            this.iterator = iterator;
        }

        @Override
        public NativeReaderIterator<Integer> iterator() throws IOException {
            return iterator;
        }
    }

    /**
     * A mock {@link OutputReceiver} that blocks the read loop in {@link ReadOperation}.
     */
    private static class MockOutputReceiver extends OutputReceiver {
        private Exchanger<Object> exchanger = new Exchanger<>();

        @Override
        public void process(Object elem) throws Exception {
            exchanger.exchange(null);
        }

        public void unblockProcess() {
            try {
                exchanger.exchange(null);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

