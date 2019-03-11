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
package org.apache.flink.streaming.api.operators.async;


import AsyncDataStream.OutputMode;
import AsyncDataStream.OutputMode.ORDERED;
import AsyncDataStream.OutputMode.UNORDERED;
import IntSerializer.INSTANCE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.testutils.CheckedThread;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.checkpoint.CheckpointMetaData;
import org.apache.flink.runtime.checkpoint.CheckpointOptions;
import org.apache.flink.runtime.checkpoint.OperatorSubtaskState;
import org.apache.flink.runtime.checkpoint.TaskStateSnapshot;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.io.network.api.CheckpointBarrier;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.operators.testutils.MockEnvironment;
import org.apache.flink.runtime.state.TestTaskStateManager;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.functions.async.AsyncFunction;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.streaming.api.graph.StreamConfig;
import org.apache.flink.streaming.api.operators.Output;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.streaming.runtime.tasks.OneInputStreamTask;
import org.apache.flink.streaming.runtime.tasks.OneInputStreamTaskTestHarness;
import org.apache.flink.streaming.runtime.tasks.ProcessingTimeCallback;
import org.apache.flink.streaming.runtime.tasks.ProcessingTimeService;
import org.apache.flink.streaming.runtime.tasks.StreamTask;
import org.apache.flink.streaming.runtime.tasks.StreamTaskTestHarness;
import org.apache.flink.streaming.runtime.tasks.TestProcessingTimeService;
import org.apache.flink.streaming.util.MockStreamConfig;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.TestHarnessUtil;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for {@link AsyncWaitOperator}. These test that:
 *
 * <ul>
 *     <li>Process StreamRecords and Watermarks in ORDERED mode</li>
 *     <li>Process StreamRecords and Watermarks in UNORDERED mode</li>
 *     <li>AsyncWaitOperator in operator chain</li>
 *     <li>Snapshot state and restore state</li>
 * </ul>
 */
public class AsyncWaitOperatorTest extends TestLogger {
    private static final long TIMEOUT = 1000L;

    private static class MyAsyncFunction extends RichAsyncFunction<Integer, Integer> {
        private static final long serialVersionUID = 8522411971886428444L;

        private static final long TERMINATION_TIMEOUT = 5000L;

        private static final int THREAD_POOL_SIZE = 10;

        static ExecutorService executorService;

        static int counter = 0;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            synchronized(AsyncWaitOperatorTest.MyAsyncFunction.class) {
                if ((AsyncWaitOperatorTest.MyAsyncFunction.counter) == 0) {
                    AsyncWaitOperatorTest.MyAsyncFunction.executorService = Executors.newFixedThreadPool(AsyncWaitOperatorTest.MyAsyncFunction.THREAD_POOL_SIZE);
                }
                ++(AsyncWaitOperatorTest.MyAsyncFunction.counter);
            }
        }

        @Override
        public void close() throws Exception {
            super.close();
            freeExecutor();
        }

        private void freeExecutor() {
            synchronized(AsyncWaitOperatorTest.MyAsyncFunction.class) {
                --(AsyncWaitOperatorTest.MyAsyncFunction.counter);
                if ((AsyncWaitOperatorTest.MyAsyncFunction.counter) == 0) {
                    AsyncWaitOperatorTest.MyAsyncFunction.executorService.shutdown();
                    try {
                        if (!(AsyncWaitOperatorTest.MyAsyncFunction.executorService.awaitTermination(AsyncWaitOperatorTest.MyAsyncFunction.TERMINATION_TIMEOUT, TimeUnit.MILLISECONDS))) {
                            AsyncWaitOperatorTest.MyAsyncFunction.executorService.shutdownNow();
                        }
                    } catch (InterruptedException interrupted) {
                        AsyncWaitOperatorTest.MyAsyncFunction.executorService.shutdownNow();
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        @Override
        public void asyncInvoke(final Integer input, final ResultFuture<Integer> resultFuture) throws Exception {
            AsyncWaitOperatorTest.MyAsyncFunction.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    resultFuture.complete(Collections.singletonList((input * 2)));
                }
            });
        }
    }

    /**
     * A special {@link AsyncFunction} without issuing
     * {@link ResultFuture#complete} until the latch counts to zero.
     * This function is used in the testStateSnapshotAndRestore, ensuring
     * that {@link StreamElementQueueEntry} can stay
     * in the {@link StreamElementQueue} to be
     * snapshotted while checkpointing.
     */
    private static class LazyAsyncFunction extends AsyncWaitOperatorTest.MyAsyncFunction {
        private static final long serialVersionUID = 3537791752703154670L;

        private static CountDownLatch latch;

        public LazyAsyncFunction() {
            AsyncWaitOperatorTest.LazyAsyncFunction.latch = new CountDownLatch(1);
        }

        @Override
        public void asyncInvoke(final Integer input, final ResultFuture<Integer> resultFuture) throws Exception {
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        AsyncWaitOperatorTest.LazyAsyncFunction.latch.await();
                    } catch (InterruptedException e) {
                        // do nothing
                    }
                    resultFuture.complete(Collections.singletonList(input));
                }
            });
        }

        public static void countDown() {
            AsyncWaitOperatorTest.LazyAsyncFunction.latch.countDown();
        }
    }

    /**
     * A special {@link LazyAsyncFunction} for timeout handling.
     * Complete the result future with 3 times the input when the timeout occurred.
     */
    private static class IgnoreTimeoutLazyAsyncFunction extends AsyncWaitOperatorTest.LazyAsyncFunction {
        private static final long serialVersionUID = 1428714561365346128L;

        @Override
        public void timeout(Integer input, ResultFuture<Integer> resultFuture) throws Exception {
            resultFuture.complete(Collections.singletonList((input * 3)));
        }
    }

    /**
     * A {@link Comparator} to compare {@link StreamRecord} while sorting them.
     */
    private class StreamRecordComparator implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            if ((o1 instanceof Watermark) || (o2 instanceof Watermark)) {
                return 0;
            } else {
                StreamRecord<Integer> sr0 = ((StreamRecord<Integer>) (o1));
                StreamRecord<Integer> sr1 = ((StreamRecord<Integer>) (o2));
                if ((sr0.getTimestamp()) != (sr1.getTimestamp())) {
                    return ((int) ((sr0.getTimestamp()) - (sr1.getTimestamp())));
                }
                int comparison = sr0.getValue().compareTo(sr1.getValue());
                if (comparison != 0) {
                    return comparison;
                } else {
                    return (sr0.getValue()) - (sr1.getValue());
                }
            }
        }
    }

    /**
     * Test the AsyncWaitOperator with ordered mode and event time.
     */
    @Test
    public void testEventTimeOrdered() throws Exception {
        testEventTime(ORDERED);
    }

    /**
     * Test the AsyncWaitOperator with unordered mode and event time.
     */
    @Test
    public void testWaterMarkUnordered() throws Exception {
        testEventTime(UNORDERED);
    }

    /**
     * Test the AsyncWaitOperator with ordered mode and processing time.
     */
    @Test
    public void testProcessingTimeOrdered() throws Exception {
        testProcessingTime(ORDERED);
    }

    /**
     * Test the AsyncWaitOperator with unordered mode and processing time.
     */
    @Test
    public void testProcessingUnordered() throws Exception {
        testProcessingTime(UNORDERED);
    }

    /**
     * Tests that the AsyncWaitOperator works together with chaining.
     */
    @Test
    public void testOperatorChainWithProcessingTime() throws Exception {
        JobVertex chainedVertex = createChainedVertex(false);
        final OneInputStreamTaskTestHarness<Integer, Integer> testHarness = new OneInputStreamTaskTestHarness(OneInputStreamTask::new, 1, 1, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);
        testHarness.setupOutputForSingletonOperatorChain();
        testHarness.taskConfig = chainedVertex.getConfiguration();
        final StreamConfig streamConfig = testHarness.getStreamConfig();
        final StreamConfig operatorChainStreamConfig = new StreamConfig(chainedVertex.getConfiguration());
        final AsyncWaitOperator<Integer, Integer> headOperator = operatorChainStreamConfig.getStreamOperator(AsyncWaitOperatorTest.class.getClassLoader());
        streamConfig.setStreamOperator(headOperator);
        testHarness.invoke();
        testHarness.waitForTaskRunning();
        long initialTimestamp = 0L;
        testHarness.processElement(new StreamRecord(5, initialTimestamp));
        testHarness.processElement(new StreamRecord(6, (initialTimestamp + 1L)));
        testHarness.processElement(new StreamRecord(7, (initialTimestamp + 2L)));
        testHarness.processElement(new StreamRecord(8, (initialTimestamp + 3L)));
        testHarness.processElement(new StreamRecord(9, (initialTimestamp + 4L)));
        testHarness.endInput();
        testHarness.waitForTaskCompletion();
        ConcurrentLinkedQueue<Object> expectedOutput = new ConcurrentLinkedQueue<>();
        expectedOutput.add(new StreamRecord(22, initialTimestamp));
        expectedOutput.add(new StreamRecord(26, (initialTimestamp + 1L)));
        expectedOutput.add(new StreamRecord(30, (initialTimestamp + 2L)));
        expectedOutput.add(new StreamRecord(34, (initialTimestamp + 3L)));
        expectedOutput.add(new StreamRecord(38, (initialTimestamp + 4L)));
        TestHarnessUtil.assertOutputEqualsSorted("Test for chained operator with AsyncWaitOperator failed", expectedOutput, testHarness.getOutput(), new AsyncWaitOperatorTest.StreamRecordComparator());
    }

    @Test
    public void testStateSnapshotAndRestore() throws Exception {
        final OneInputStreamTaskTestHarness<Integer, Integer> testHarness = new OneInputStreamTaskTestHarness(OneInputStreamTask::new, 1, 1, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);
        testHarness.setupOutputForSingletonOperatorChain();
        AsyncWaitOperator<Integer, Integer> operator = new AsyncWaitOperator(new AsyncWaitOperatorTest.LazyAsyncFunction(), AsyncWaitOperatorTest.TIMEOUT, 3, OutputMode.ORDERED);
        final StreamConfig streamConfig = testHarness.getStreamConfig();
        OperatorID operatorID = new OperatorID(42L, 4711L);
        streamConfig.setStreamOperator(operator);
        streamConfig.setOperatorID(operatorID);
        final TestTaskStateManager taskStateManagerMock = testHarness.getTaskStateManager();
        taskStateManagerMock.setWaitForReportLatch(new OneShotLatch());
        testHarness.invoke();
        testHarness.waitForTaskRunning();
        final OneInputStreamTask<Integer, Integer> task = testHarness.getTask();
        final long initialTime = 0L;
        testHarness.processElement(new StreamRecord(1, (initialTime + 1)));
        testHarness.processElement(new StreamRecord(2, (initialTime + 2)));
        testHarness.processElement(new StreamRecord(3, (initialTime + 3)));
        testHarness.processElement(new StreamRecord(4, (initialTime + 4)));
        testHarness.waitForInputProcessing();
        final long checkpointId = 1L;
        final long checkpointTimestamp = 1L;
        final CheckpointMetaData checkpointMetaData = new CheckpointMetaData(checkpointId, checkpointTimestamp);
        task.triggerCheckpoint(checkpointMetaData, CheckpointOptions.forCheckpointWithDefaultLocation());
        taskStateManagerMock.getWaitForReportLatch().await();
        Assert.assertEquals(checkpointId, taskStateManagerMock.getReportedCheckpointId());
        AsyncWaitOperatorTest.LazyAsyncFunction.countDown();
        testHarness.endInput();
        testHarness.waitForTaskCompletion();
        // set the operator state from previous attempt into the restored one
        TaskStateSnapshot subtaskStates = taskStateManagerMock.getLastJobManagerTaskStateSnapshot();
        final OneInputStreamTaskTestHarness<Integer, Integer> restoredTaskHarness = new OneInputStreamTaskTestHarness(OneInputStreamTask::new, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO);
        restoredTaskHarness.setTaskStateSnapshot(checkpointId, subtaskStates);
        restoredTaskHarness.setupOutputForSingletonOperatorChain();
        AsyncWaitOperator<Integer, Integer> restoredOperator = new AsyncWaitOperator(new AsyncWaitOperatorTest.MyAsyncFunction(), AsyncWaitOperatorTest.TIMEOUT, 6, OutputMode.ORDERED);
        restoredTaskHarness.getStreamConfig().setStreamOperator(restoredOperator);
        restoredTaskHarness.getStreamConfig().setOperatorID(operatorID);
        restoredTaskHarness.invoke();
        restoredTaskHarness.waitForTaskRunning();
        final OneInputStreamTask<Integer, Integer> restoredTask = restoredTaskHarness.getTask();
        restoredTaskHarness.processElement(new StreamRecord(5, (initialTime + 5)));
        restoredTaskHarness.processElement(new StreamRecord(6, (initialTime + 6)));
        restoredTaskHarness.processElement(new StreamRecord(7, (initialTime + 7)));
        // trigger the checkpoint while processing stream elements
        restoredTask.triggerCheckpoint(new CheckpointMetaData(checkpointId, checkpointTimestamp), CheckpointOptions.forCheckpointWithDefaultLocation());
        restoredTaskHarness.processElement(new StreamRecord(8, (initialTime + 8)));
        restoredTaskHarness.endInput();
        restoredTaskHarness.waitForTaskCompletion();
        ConcurrentLinkedQueue<Object> expectedOutput = new ConcurrentLinkedQueue<>();
        expectedOutput.add(new StreamRecord(2, (initialTime + 1)));
        expectedOutput.add(new StreamRecord(4, (initialTime + 2)));
        expectedOutput.add(new StreamRecord(6, (initialTime + 3)));
        expectedOutput.add(new StreamRecord(8, (initialTime + 4)));
        expectedOutput.add(new StreamRecord(10, (initialTime + 5)));
        expectedOutput.add(new StreamRecord(12, (initialTime + 6)));
        expectedOutput.add(new StreamRecord(14, (initialTime + 7)));
        expectedOutput.add(new StreamRecord(16, (initialTime + 8)));
        // remove CheckpointBarrier which is not expected
        Iterator<Object> iterator = restoredTaskHarness.getOutput().iterator();
        while (iterator.hasNext()) {
            if ((iterator.next()) instanceof CheckpointBarrier) {
                iterator.remove();
            }
        } 
        TestHarnessUtil.assertOutputEquals("StateAndRestored Test Output was not correct.", expectedOutput, restoredTaskHarness.getOutput());
    }

    @Test
    public void testAsyncTimeoutFailure() throws Exception {
        testAsyncTimeout(new AsyncWaitOperatorTest.LazyAsyncFunction(), Optional.of(TimeoutException.class), new StreamRecord(2, 5L));
    }

    @Test
    public void testAsyncTimeoutIgnore() throws Exception {
        testAsyncTimeout(new AsyncWaitOperatorTest.IgnoreTimeoutLazyAsyncFunction(), Optional.empty(), new StreamRecord(3, 0L), new StreamRecord(2, 5L));
    }

    /**
     * Test case for FLINK-5638: Tests that the async wait operator can be closed even if the
     * emitter is currently waiting on the checkpoint lock (e.g. in the case of two chained async
     * wait operators where the latter operator's queue is currently full).
     *
     * <p>Note that this test does not enforce the exact strict ordering because with the fix it is no
     * longer possible. However, it provokes the described situation without the fix.
     */
    @Test(timeout = 10000L)
    public void testClosingWithBlockedEmitter() throws Exception {
        final Object lock = new Object();
        ArgumentCaptor<Throwable> failureReason = ArgumentCaptor.forClass(Throwable.class);
        MockEnvironment environment = createMockEnvironment();
        StreamTask<?, ?> containingTask = Mockito.mock(StreamTask.class);
        Mockito.when(containingTask.getEnvironment()).thenReturn(environment);
        Mockito.when(containingTask.getCheckpointLock()).thenReturn(lock);
        Mockito.when(containingTask.getProcessingTimeService()).thenReturn(new TestProcessingTimeService());
        StreamConfig streamConfig = new MockStreamConfig();
        streamConfig.setTypeSerializerIn1(INSTANCE);
        final OneShotLatch closingLatch = new OneShotLatch();
        final OneShotLatch outputLatch = new OneShotLatch();
        Output<StreamRecord<Integer>> output = Mockito.mock(Output.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertTrue("Output should happen under the checkpoint lock.", Thread.currentThread().holdsLock(lock));
                outputLatch.trigger();
                // wait until we're in the closing method of the operator
                while (!(closingLatch.isTriggered())) {
                    lock.wait();
                } 
                return null;
            }
        }).when(output).collect(ArgumentMatchers.any(StreamRecord.class));
        AsyncWaitOperator<Integer, Integer> operator = new AsyncWaitOperatorTest.TestAsyncWaitOperator(new AsyncWaitOperatorTest.MyAsyncFunction(), 1000L, 1, OutputMode.ORDERED, closingLatch);
        operator.setup(containingTask, streamConfig, output);
        operator.open();
        synchronized(lock) {
            operator.processElement(new StreamRecord(42));
        }
        outputLatch.await();
        synchronized(lock) {
            operator.close();
        }
    }

    /**
     * Testing async wait operator which introduces a latch to synchronize the execution with the
     * emitter.
     */
    private static final class TestAsyncWaitOperator<IN, OUT> extends AsyncWaitOperator<IN, OUT> {
        private static final long serialVersionUID = -8528791694746625560L;

        private final transient OneShotLatch closingLatch;

        public TestAsyncWaitOperator(AsyncFunction<IN, OUT> asyncFunction, long timeout, int capacity, AsyncDataStream.OutputMode outputMode, OneShotLatch closingLatch) {
            super(asyncFunction, timeout, capacity, outputMode);
            this.closingLatch = Preconditions.checkNotNull(closingLatch);
        }

        @Override
        public void close() throws Exception {
            closingLatch.trigger();
            checkpointingLock.notifyAll();
            super.close();
        }
    }

    /**
     * FLINK-5652
     * Tests that registered timers are properly canceled upon completion of a
     * {@link StreamRecordQueueEntry} in order to avoid resource leaks because TriggerTasks hold
     * a reference on the StreamRecordQueueEntry.
     */
    @Test
    public void testTimeoutCleanup() throws Exception {
        final Object lock = new Object();
        final long timeout = 100000L;
        final long timestamp = 1L;
        Environment environment = createMockEnvironment();
        ScheduledFuture<?> scheduledFuture = Mockito.mock(ScheduledFuture.class);
        ProcessingTimeService processingTimeService = Mockito.mock(ProcessingTimeService.class);
        Mockito.when(processingTimeService.getCurrentProcessingTime()).thenReturn(timestamp);
        Mockito.doReturn(scheduledFuture).when(processingTimeService).registerTimer(ArgumentMatchers.anyLong(), ArgumentMatchers.any(ProcessingTimeCallback.class));
        StreamTask<?, ?> containingTask = Mockito.mock(StreamTask.class);
        Mockito.when(containingTask.getEnvironment()).thenReturn(environment);
        Mockito.when(containingTask.getCheckpointLock()).thenReturn(lock);
        Mockito.when(containingTask.getProcessingTimeService()).thenReturn(processingTimeService);
        StreamConfig streamConfig = new MockStreamConfig();
        streamConfig.setTypeSerializerIn1(INSTANCE);
        Output<StreamRecord<Integer>> output = Mockito.mock(Output.class);
        AsyncWaitOperator<Integer, Integer> operator = new AsyncWaitOperator(new AsyncFunction<Integer, Integer>() {
            private static final long serialVersionUID = -3718276118074877073L;

            @Override
            public void asyncInvoke(Integer input, ResultFuture<Integer> resultFuture) throws Exception {
                resultFuture.complete(Collections.singletonList(input));
            }
        }, timeout, 1, OutputMode.UNORDERED);
        operator.setup(containingTask, streamConfig, output);
        operator.open();
        final StreamRecord<Integer> streamRecord = new StreamRecord(42, timestamp);
        synchronized(lock) {
            // processing an element will register a timeout
            operator.processElement(streamRecord);
        }
        synchronized(lock) {
            // closing the operator waits until all inputs have been processed
            operator.close();
        }
        // check that we actually outputted the result of the single input
        Mockito.verify(output).collect(ArgumentMatchers.eq(streamRecord));
        Mockito.verify(processingTimeService).registerTimer(ArgumentMatchers.eq(((processingTimeService.getCurrentProcessingTime()) + timeout)), ArgumentMatchers.any(ProcessingTimeCallback.class));
        // check that we have cancelled our registered timeout
        Mockito.verify(scheduledFuture).cancel(ArgumentMatchers.eq(true));
    }

    /**
     * FLINK-6435
     *
     * <p>Tests that a user exception triggers the completion of a StreamElementQueueEntry and does not wait to until
     * another StreamElementQueueEntry is properly completed before it is collected.
     */
    @Test(timeout = 2000)
    public void testOrderedWaitUserExceptionHandling() throws Exception {
        testUserExceptionHandling(ORDERED);
    }

    /**
     * FLINK-6435
     *
     * <p>Tests that a user exception triggers the completion of a StreamElementQueueEntry and does not wait to until
     * another StreamElementQueueEntry is properly completed before it is collected.
     */
    @Test(timeout = 2000)
    public void testUnorderedWaitUserExceptionHandling() throws Exception {
        testUserExceptionHandling(UNORDERED);
    }

    /**
     * AsyncFunction which completes the result with an {@link Exception}.
     */
    private static class UserExceptionAsyncFunction implements AsyncFunction<Integer, Integer> {
        private static final long serialVersionUID = 6326568632967110990L;

        @Override
        public void asyncInvoke(Integer input, ResultFuture<Integer> resultFuture) throws Exception {
            resultFuture.completeExceptionally(new Exception("Test exception"));
        }
    }

    /**
     * FLINK-6435
     *
     * <p>Tests that timeout exceptions are properly handled in ordered output mode. The proper handling means that
     * a StreamElementQueueEntry is completed in case of a timeout exception.
     */
    @Test
    public void testOrderedWaitTimeoutHandling() throws Exception {
        testTimeoutExceptionHandling(ORDERED);
    }

    /**
     * FLINK-6435
     *
     * <p>Tests that timeout exceptions are properly handled in ordered output mode. The proper handling means that
     * a StreamElementQueueEntry is completed in case of a timeout exception.
     */
    @Test
    public void testUnorderedWaitTimeoutHandling() throws Exception {
        testTimeoutExceptionHandling(UNORDERED);
    }

    /**
     * Tests that the AysncWaitOperator can restart if checkpointed queue was full.
     *
     * <p>See FLINK-7949
     */
    @Test(timeout = 10000)
    public void testRestartWithFullQueue() throws Exception {
        int capacity = 10;
        // 1. create the snapshot which contains capacity + 1 elements
        final CompletableFuture<Void> trigger = new CompletableFuture<>();
        final AsyncWaitOperatorTest.ControllableAsyncFunction<Integer> controllableAsyncFunction = new AsyncWaitOperatorTest.ControllableAsyncFunction<>(trigger);
        final OneInputStreamOperatorTestHarness<Integer, Integer> snapshotHarness = new OneInputStreamOperatorTestHarness(// the NoOpAsyncFunction is like a blocking function
        new AsyncWaitOperator(controllableAsyncFunction, 1000L, capacity, OutputMode.ORDERED), IntSerializer.INSTANCE);
        snapshotHarness.open();
        final OperatorSubtaskState snapshot;
        final ArrayList<Integer> expectedOutput = new ArrayList<>((capacity + 1));
        try {
            synchronized(snapshotHarness.getCheckpointLock()) {
                for (int i = 0; i < capacity; i++) {
                    snapshotHarness.processElement(i, 0L);
                    expectedOutput.add(i);
                }
            }
            expectedOutput.add(capacity);
            final OneShotLatch lastElement = new OneShotLatch();
            final CheckedThread lastElementWriter = new CheckedThread() {
                @Override
                public void go() throws Exception {
                    synchronized(snapshotHarness.getCheckpointLock()) {
                        lastElement.trigger();
                        snapshotHarness.processElement(capacity, 0L);
                    }
                }
            };
            lastElementWriter.start();
            lastElement.await();
            synchronized(snapshotHarness.getCheckpointLock()) {
                // execute the snapshot within the checkpoint lock, because then it is guaranteed
                // that the lastElementWriter has written the exceeding element
                snapshot = snapshotHarness.snapshot(0L, 0L);
            }
            // trigger the computation to make the close call finish
            trigger.complete(null);
        } finally {
            synchronized(snapshotHarness.getCheckpointLock()) {
                snapshotHarness.close();
            }
        }
        // 2. restore the snapshot and check that we complete
        final OneInputStreamOperatorTestHarness<Integer, Integer> recoverHarness = new OneInputStreamOperatorTestHarness(new AsyncWaitOperator(new AsyncWaitOperatorTest.ControllableAsyncFunction(CompletableFuture.completedFuture(null)), 1000L, capacity, OutputMode.ORDERED), IntSerializer.INSTANCE);
        recoverHarness.initializeState(snapshot);
        synchronized(recoverHarness.getCheckpointLock()) {
            recoverHarness.open();
        }
        synchronized(recoverHarness.getCheckpointLock()) {
            recoverHarness.close();
        }
        final ConcurrentLinkedQueue<Object> output = recoverHarness.getOutput();
        Assert.assertThat(output.size(), Matchers.equalTo((capacity + 1)));
        final ArrayList<Integer> outputElements = new ArrayList<>((capacity + 1));
        for (int i = 0; i < (capacity + 1); i++) {
            StreamRecord<Integer> streamRecord = ((StreamRecord<Integer>) (output.poll()));
            outputElements.add(streamRecord.getValue());
        }
        Assert.assertThat(outputElements, Matchers.equalTo(expectedOutput));
    }

    private static class ControllableAsyncFunction<IN> implements AsyncFunction<IN, IN> {
        private static final long serialVersionUID = -4214078239267288636L;

        private transient CompletableFuture<Void> trigger;

        private ControllableAsyncFunction(CompletableFuture<Void> trigger) {
            this.trigger = Preconditions.checkNotNull(trigger);
        }

        @Override
        public void asyncInvoke(IN input, ResultFuture<IN> resultFuture) throws Exception {
            trigger.thenAccept(( v) -> resultFuture.complete(Collections.singleton(input)));
        }
    }

    private static class NoOpAsyncFunction<IN, OUT> implements AsyncFunction<IN, OUT> {
        private static final long serialVersionUID = -3060481953330480694L;

        @Override
        public void asyncInvoke(IN input, ResultFuture<OUT> resultFuture) throws Exception {
            // no op
        }
    }
}

