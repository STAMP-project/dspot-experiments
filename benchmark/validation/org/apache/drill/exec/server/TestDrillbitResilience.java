/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.server;


import ExternalSortBatch.INTERRUPTION_AFTER_SETUP;
import ExternalSortBatch.INTERRUPTION_AFTER_SORT;
import QueryState.COMPLETED;
import QueryType.SQL;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.util.Pair;
import org.apache.drill.categories.SlowTest;
import org.apache.drill.common.concurrent.ExtendedLatch;
import org.apache.drill.common.exceptions.UserException;
import org.apache.drill.common.util.RepeatTestRule.Repeat;
import org.apache.drill.exec.ExecConstants;
import org.apache.drill.exec.ZookeeperHelper;
import org.apache.drill.exec.client.DrillClient;
import org.apache.drill.exec.physical.impl.ScreenCreator;
import org.apache.drill.exec.physical.impl.mergereceiver.MergingRecordBatch;
import org.apache.drill.exec.physical.impl.partitionsender.PartitionerDecorator;
import org.apache.drill.exec.physical.impl.unorderedreceiver.UnorderedReceiverBatch;
import org.apache.drill.exec.physical.impl.xsort.ExternalSortBatch;
import org.apache.drill.exec.planner.sql.DrillSqlWorker;
import org.apache.drill.exec.proto.GeneralRPCProtos.Ack;
import org.apache.drill.exec.proto.UserBitShared.QueryId;
import org.apache.drill.exec.proto.UserBitShared.QueryResult.QueryState;
import org.apache.drill.exec.rpc.ConnectionThrottle;
import org.apache.drill.exec.rpc.DrillRpcFuture;
import org.apache.drill.exec.rpc.RpcException;
import org.apache.drill.exec.rpc.user.QueryDataBatch;
import org.apache.drill.exec.rpc.user.UserResultsListener;
import org.apache.drill.exec.store.pojo.PojoRecordReader;
import org.apache.drill.exec.testing.Controls;
import org.apache.drill.exec.util.Pointer;
import org.apache.drill.exec.work.foreman.ForemanException;
import org.apache.drill.exec.work.foreman.ForemanSetupException;
import org.apache.drill.exec.work.foreman.FragmentsRunner;
import org.apache.drill.exec.work.fragment.FragmentExecutor;
import org.apache.drill.shaded.guava.com.google.common.base.Preconditions;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.DrillTest;
import org.apache.drill.test.QueryTestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test how resilient drillbits are to throwing exceptions during various phases of query
 * execution by injecting exceptions at various points, and to cancellations in various phases.
 */
@Category({ SlowTest.class })
public class TestDrillbitResilience extends DrillTest {
    private static final Logger logger = LoggerFactory.getLogger(TestDrillbitResilience.class);

    private static ZookeeperHelper zkHelper;

    private static RemoteServiceSet remoteServiceSet;

    private static final Map<String, Drillbit> drillbits = new HashMap<>();

    private static DrillClient drillClient;

    /**
     * The number of times test (that are repeated) should be repeated.
     */
    private static final int NUM_RUNS = 3;

    /**
     * Note: Counting sys.memory executes a fragment on every drillbit. This is a better check in comparison to
     * counting sys.drillbits.
     */
    private static final String TEST_QUERY = "select * from sys.memory";

    /* Canned drillbit names. */
    private static final String DRILLBIT_ALPHA = "alpha";

    private static final String DRILLBIT_BETA = "beta";

    private static final String DRILLBIT_GAMMA = "gamma";

    @Test
    public void settingNoOpInjectionsAndQuery() {
        final long before = TestDrillbitResilience.countAllocatedMemory();
        final String controls = Controls.newBuilder().addExceptionOnBit(getClass(), "noop", RuntimeException.class, TestDrillbitResilience.getEndpoint(TestDrillbitResilience.DRILLBIT_BETA)).build();
        TestDrillbitResilience.setControls(controls);
        final TestDrillbitResilience.WaitUntilCompleteListener listener = new TestDrillbitResilience.WaitUntilCompleteListener();
        QueryTestUtil.testWithListener(TestDrillbitResilience.drillClient, SQL, TestDrillbitResilience.TEST_QUERY, listener);
        final Pair<QueryState, Exception> pair = listener.waitForCompletion();
        TestDrillbitResilience.assertStateCompleted(pair, COMPLETED);
        final long after = TestDrillbitResilience.countAllocatedMemory();
        Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
    }

    @Test
    @Repeat(count = TestDrillbitResilience.NUM_RUNS)
    public void foreman_runTryBeginning() {
        final long before = TestDrillbitResilience.countAllocatedMemory();
        TestDrillbitResilience.testForeman("run-try-beginning");
        final long after = TestDrillbitResilience.countAllocatedMemory();
        Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
    }

    /**
     * Tests can use this listener to wait, until the submitted query completes or fails, by
     * calling #waitForCompletion.
     */
    private static class WaitUntilCompleteListener implements UserResultsListener {
        private final ExtendedLatch latch = new ExtendedLatch(1);// to signal completion


        protected QueryId queryId = null;

        protected volatile Pointer<Exception> ex = new Pointer();

        protected volatile QueryState state = null;

        /**
         * Method that sets the exception if the condition is not met.
         */
        protected final void check(final boolean condition, final String format, final Object... args) {
            if (!condition) {
                ex.value = new IllegalStateException(String.format(format, args));
            }
        }

        /**
         * Method that cancels and resumes the query, in order.
         */
        protected final void cancelAndResume() {
            Preconditions.checkNotNull(queryId);
            final ExtendedLatch trigger = new ExtendedLatch(1);
            new TestDrillbitResilience.CancellingThread(queryId, ex, trigger).start();
            new TestDrillbitResilience.ResumingThread(queryId, ex, trigger).start();
        }

        @Override
        public void queryIdArrived(final QueryId queryId) {
            this.queryId = queryId;
        }

        @Override
        public void submissionFailed(final UserException ex) {
            this.ex.value = ex;
            state = QueryState.FAILED;
            latch.countDown();
        }

        @Override
        public void queryCompleted(final QueryState state) {
            this.state = state;
            latch.countDown();
        }

        @Override
        public void dataArrived(final QueryDataBatch result, final ConnectionThrottle throttle) {
            result.release();
        }

        public final Pair<QueryState, Exception> waitForCompletion() {
            latch.awaitUninterruptibly();
            return new Pair(state, ex.value);
        }
    }

    private static class ListenerThatCancelsQueryAfterFirstBatchOfData extends TestDrillbitResilience.WaitUntilCompleteListener {
        private boolean cancelRequested = false;

        @Override
        public void dataArrived(final QueryDataBatch result, final ConnectionThrottle throttle) {
            if (!(cancelRequested)) {
                check(((queryId) != null), "Query id should not be null, since we have waited long enough.");
                new TestDrillbitResilience.CancellingThread(queryId, ex, null).start();
                cancelRequested = true;
            }
            result.release();
        }
    }

    /**
     * Thread that cancels the given query id. After the cancel is acknowledged, the latch is counted down.
     */
    private static class CancellingThread extends Thread {
        private final QueryId queryId;

        private final Pointer<Exception> ex;

        private final ExtendedLatch latch;

        public CancellingThread(final QueryId queryId, final Pointer<Exception> ex, final ExtendedLatch latch) {
            this.queryId = queryId;
            this.ex = ex;
            this.latch = latch;
        }

        @Override
        public void run() {
            final DrillRpcFuture<Ack> cancelAck = TestDrillbitResilience.drillClient.cancelQuery(queryId);
            try {
                cancelAck.checkedGet();
            } catch (final RpcException ex) {
                this.ex.value = ex;
            }
            if ((latch) != null) {
                latch.countDown();
            }
        }
    }

    /**
     * Thread that resumes the given query id. After the latch is counted down, the resume signal is sent, until then
     * the thread waits without interruption.
     */
    private static class ResumingThread extends Thread {
        private final QueryId queryId;

        private final Pointer<Exception> ex;

        private final ExtendedLatch latch;

        public ResumingThread(final QueryId queryId, final Pointer<Exception> ex, final ExtendedLatch latch) {
            this.queryId = queryId;
            this.ex = ex;
            this.latch = latch;
        }

        @Override
        public void run() {
            latch.awaitUninterruptibly();
            final DrillRpcFuture<Ack> resumeAck = TestDrillbitResilience.drillClient.resumeQuery(queryId);
            try {
                resumeAck.checkedGet();
            } catch (final RpcException ex) {
                this.ex.value = ex;
            }
        }
    }

    // To test pause and resume. Test hangs and times out if resume did not happen.
    @Test
    public void passThrough() {
        final long before = TestDrillbitResilience.countAllocatedMemory();
        final TestDrillbitResilience.WaitUntilCompleteListener listener = new TestDrillbitResilience.WaitUntilCompleteListener() {
            @Override
            public void queryIdArrived(final QueryId queryId) {
                super.queryIdArrived(queryId);
                final ExtendedLatch trigger = new ExtendedLatch(1);
                new TestDrillbitResilience.ResumingThread(queryId, ex, trigger).start();
                trigger.countDown();
            }
        };
        final String controls = Controls.newBuilder().addPause(PojoRecordReader.class, "read-next").build();
        TestDrillbitResilience.setControls(controls);
        QueryTestUtil.testWithListener(TestDrillbitResilience.drillClient, SQL, TestDrillbitResilience.TEST_QUERY, listener);
        final Pair<QueryState, Exception> result = listener.waitForCompletion();
        TestDrillbitResilience.assertStateCompleted(result, COMPLETED);
        final long after = TestDrillbitResilience.countAllocatedMemory();
        Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
    }

    // DRILL-2383: Completion TC 1: success
    @Test
    public void successfullyCompletes() {
        final long before = TestDrillbitResilience.countAllocatedMemory();
        final TestDrillbitResilience.WaitUntilCompleteListener listener = new TestDrillbitResilience.WaitUntilCompleteListener();
        QueryTestUtil.testWithListener(TestDrillbitResilience.drillClient, SQL, TestDrillbitResilience.TEST_QUERY, listener);
        final Pair<QueryState, Exception> result = listener.waitForCompletion();
        TestDrillbitResilience.assertStateCompleted(result, COMPLETED);
        final long after = TestDrillbitResilience.countAllocatedMemory();
        Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
    }

    // DRILL-2383: Completion TC 2: failed query - before query is executed - while sql parsing
    @Test
    public void failsWhenParsing() {
        final long before = TestDrillbitResilience.countAllocatedMemory();
        final String exceptionDesc = "sql-parsing";
        final Class<? extends Throwable> exceptionClass = ForemanSetupException.class;
        // Inject the failure twice since there can be retry after first failure introduced in DRILL-6762. Retry is because
        // of version mismatch in local and remote function registry which syncs up lazily.
        final String controls = Controls.newBuilder().addException(DrillSqlWorker.class, exceptionDesc, exceptionClass, 0, 2).build();
        TestDrillbitResilience.assertFailsWithException(controls, exceptionClass, exceptionDesc);
        final long after = TestDrillbitResilience.countAllocatedMemory();
        Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
    }

    // DRILL-2383: Completion TC 3: failed query - before query is executed - while sending fragments to other
    // drillbits
    @Test
    public void failsWhenSendingFragments() {
        final long before = TestDrillbitResilience.countAllocatedMemory();
        final String exceptionDesc = "send-fragments";
        final Class<? extends Throwable> exceptionClass = ForemanException.class;
        final String controls = Controls.newBuilder().addException(FragmentsRunner.class, exceptionDesc, exceptionClass).build();
        TestDrillbitResilience.assertFailsWithException(controls, exceptionClass, exceptionDesc);
        final long after = TestDrillbitResilience.countAllocatedMemory();
        Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
    }

    // DRILL-2383: Completion TC 4: failed query - during query execution
    @Test
    public void failsDuringExecution() {
        final long before = TestDrillbitResilience.countAllocatedMemory();
        final String exceptionDesc = "fragment-execution";
        final Class<? extends Throwable> exceptionClass = IOException.class;
        final String controls = Controls.newBuilder().addException(FragmentExecutor.class, exceptionDesc, exceptionClass).build();
        TestDrillbitResilience.assertFailsWithException(controls, exceptionClass, exceptionDesc);
        final long after = TestDrillbitResilience.countAllocatedMemory();
        Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
    }

    /**
     * Test canceling query interrupts currently blocked FragmentExecutor threads waiting for some event to happen.
     * Specifically tests canceling fragment which has {@link MergingRecordBatch} blocked waiting for data.
     */
    @Test
    @Repeat(count = TestDrillbitResilience.NUM_RUNS)
    public void interruptingBlockedMergingRecordBatch() {
        final long before = TestDrillbitResilience.countAllocatedMemory();
        final String control = Controls.newBuilder().addPause(MergingRecordBatch.class, "waiting-for-data", 1).build();
        TestDrillbitResilience.interruptingBlockedFragmentsWaitingForData(control);
        final long after = TestDrillbitResilience.countAllocatedMemory();
        Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
    }

    /**
     * Test canceling query interrupts currently blocked FragmentExecutor threads waiting for some event to happen.
     * Specifically tests canceling fragment which has {@link UnorderedReceiverBatch} blocked waiting for data.
     */
    @Test
    @Repeat(count = TestDrillbitResilience.NUM_RUNS)
    public void interruptingBlockedUnorderedReceiverBatch() {
        final long before = TestDrillbitResilience.countAllocatedMemory();
        final String control = Controls.newBuilder().addPause(UnorderedReceiverBatch.class, "waiting-for-data", 1).build();
        TestDrillbitResilience.interruptingBlockedFragmentsWaitingForData(control);
        final long after = TestDrillbitResilience.countAllocatedMemory();
        Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
    }

    /**
     * Tests interrupting the fragment thread that is running {@link PartitionSenderRootExec}.
     * {@link PartitionSenderRootExec} spawns threads for partitioner. Interrupting fragment thread should also interrupt
     * the partitioner threads.
     */
    @Test
    @Repeat(count = TestDrillbitResilience.NUM_RUNS)
    public void interruptingPartitionerThreadFragment() {
        try {
            TestDrillbitResilience.setSessionOption(ExecConstants.SLICE_TARGET, "1");
            TestDrillbitResilience.setSessionOption(HASHAGG.getOptionName(), "true");
            TestDrillbitResilience.setSessionOption(PARTITION_SENDER_SET_THREADS.getOptionName(), "6");
            final long before = TestDrillbitResilience.countAllocatedMemory();
            final String controls = Controls.newBuilder().addLatch(PartitionerDecorator.class, "partitioner-sender-latch").addPause(PartitionerDecorator.class, "wait-for-fragment-interrupt", 1).build();
            final String query = "SELECT sales_city, COUNT(*) cnt FROM cp.`region.json` GROUP BY sales_city";
            TestDrillbitResilience.assertCancelledWithoutException(controls, new TestDrillbitResilience.ListenerThatCancelsQueryAfterFirstBatchOfData(), query);
            final long after = TestDrillbitResilience.countAllocatedMemory();
            Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
        } finally {
            TestDrillbitResilience.resetSessionOption(ExecConstants.SLICE_TARGET);
            TestDrillbitResilience.resetSessionOption(HASHAGG.getOptionName());
            TestDrillbitResilience.resetSessionOption(PARTITION_SENDER_SET_THREADS.getOptionName());
        }
    }

    @Test
    @Repeat(count = TestDrillbitResilience.NUM_RUNS)
    public void memoryLeaksWhenCancelled() {
        TestDrillbitResilience.setSessionOption(ExecConstants.SLICE_TARGET, "10");
        final long before = TestDrillbitResilience.countAllocatedMemory();
        try {
            final String controls = Controls.newBuilder().addPause(ScreenCreator.class, "sending-data", 1).build();
            String query = null;
            try {
                query = BaseTestQuery.getFile("queries/tpch/09.sql");
                query = query.substring(0, ((query.length()) - 1));// drop the ";"

            } catch (final IOException e) {
                Assert.fail(("Failed to get query file: " + e));
            }
            final TestDrillbitResilience.WaitUntilCompleteListener listener = new TestDrillbitResilience.WaitUntilCompleteListener() {
                private volatile boolean cancelRequested = false;

                @Override
                public void dataArrived(final QueryDataBatch result, final ConnectionThrottle throttle) {
                    if (!(cancelRequested)) {
                        check(((queryId) != null), "Query id should not be null, since we have waited long enough.");
                        cancelAndResume();
                        cancelRequested = true;
                    }
                    result.release();
                }
            };
            TestDrillbitResilience.assertCancelledWithoutException(controls, listener, query);
            final long after = TestDrillbitResilience.countAllocatedMemory();
            Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
        } finally {
            TestDrillbitResilience.setSessionOption(ExecConstants.SLICE_TARGET, Long.toString(ExecConstants.SLICE_TARGET_DEFAULT));
        }
    }

    // DRILL-3065
    @Test
    public void failsAfterMSorterSorting() {
        // Note: must use an input table that returns more than one
        // batch. The sort uses an optimization for single-batch inputs
        // which bypasses the code where this partiucular fault is
        // injected.
        final String query = "select n_name from cp.`tpch/lineitem.parquet` order by n_name";
        final Class<? extends Exception> typeOfException = RuntimeException.class;
        final long before = TestDrillbitResilience.countAllocatedMemory();
        final String controls = Controls.newBuilder().addException(ExternalSortBatch.class, INTERRUPTION_AFTER_SORT, typeOfException).build();
        TestDrillbitResilience.assertFailsWithException(controls, typeOfException, INTERRUPTION_AFTER_SORT, query);
        final long after = TestDrillbitResilience.countAllocatedMemory();
        Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
    }

    // DRILL-3085
    @Test
    public void failsAfterMSorterSetup() {
        // Note: must use an input table that returns more than one
        // batch. The sort uses an optimization for single-batch inputs
        // which bypasses the code where this partiucular fault is
        // injected.
        final String query = "select n_name from cp.`tpch/lineitem.parquet` order by n_name";
        final Class<? extends Exception> typeOfException = RuntimeException.class;
        final long before = TestDrillbitResilience.countAllocatedMemory();
        final String controls = Controls.newBuilder().addException(ExternalSortBatch.class, INTERRUPTION_AFTER_SETUP, typeOfException).build();
        TestDrillbitResilience.assertFailsWithException(controls, typeOfException, INTERRUPTION_AFTER_SETUP, query);
        final long after = TestDrillbitResilience.countAllocatedMemory();
        Assert.assertEquals(String.format("We are leaking %d bytes", (after - before)), before, after);
    }
}

