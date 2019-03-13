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
package org.apache.hadoop.hbase.procedure2;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.procedure2.store.ProcedureStore;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static LockState.LOCK_ACQUIRED;
import static LockState.LOCK_YIELD_WAIT;


@Category({ MasterTests.class, SmallTests.class })
public class TestProcedureSuspended {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureSuspended.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestProcedureSuspended.class);

    private static final int PROCEDURE_EXECUTOR_SLOTS = 1;

    private static final Procedure NULL_PROC = null;

    private ProcedureExecutor<TestProcedureSuspended.TestProcEnv> procExecutor;

    private ProcedureStore procStore;

    private HBaseCommonTestingUtility htu;

    @Test
    public void testSuspendWhileHoldingLocks() {
        final AtomicBoolean lockA = new AtomicBoolean(false);
        final AtomicBoolean lockB = new AtomicBoolean(false);
        final TestProcedureSuspended.TestLockProcedure p1keyA = new TestProcedureSuspended.TestLockProcedure(lockA, "keyA", false, true);
        final TestProcedureSuspended.TestLockProcedure p2keyA = new TestProcedureSuspended.TestLockProcedure(lockA, "keyA", false, true);
        final TestProcedureSuspended.TestLockProcedure p3keyB = new TestProcedureSuspended.TestLockProcedure(lockB, "keyB", false, true);
        procExecutor.submitProcedure(p1keyA);
        procExecutor.submitProcedure(p2keyA);
        procExecutor.submitProcedure(p3keyB);
        // first run p1, p3 are able to run p2 is blocked by p1
        waitAndAssertTimestamp(p1keyA, 1, 1);
        waitAndAssertTimestamp(p2keyA, 0, (-1));
        waitAndAssertTimestamp(p3keyB, 1, 2);
        Assert.assertEquals(true, lockA.get());
        Assert.assertEquals(true, lockB.get());
        // release p3
        p3keyB.setThrowSuspend(false);
        procExecutor.getScheduler().addFront(p3keyB);
        waitAndAssertTimestamp(p1keyA, 1, 1);
        waitAndAssertTimestamp(p2keyA, 0, (-1));
        waitAndAssertTimestamp(p3keyB, 2, 3);
        Assert.assertEquals(true, lockA.get());
        // wait until p3 is fully completed
        ProcedureTestingUtility.waitProcedure(procExecutor, p3keyB);
        Assert.assertEquals(false, lockB.get());
        // rollback p2 and wait until is fully completed
        p1keyA.setTriggerRollback(true);
        procExecutor.getScheduler().addFront(p1keyA);
        ProcedureTestingUtility.waitProcedure(procExecutor, p1keyA);
        // p2 should start and suspend
        waitAndAssertTimestamp(p1keyA, 4, 60000);
        waitAndAssertTimestamp(p2keyA, 1, 7);
        waitAndAssertTimestamp(p3keyB, 2, 3);
        Assert.assertEquals(true, lockA.get());
        // wait until p2 is fully completed
        p2keyA.setThrowSuspend(false);
        procExecutor.getScheduler().addFront(p2keyA);
        ProcedureTestingUtility.waitProcedure(procExecutor, p2keyA);
        waitAndAssertTimestamp(p1keyA, 4, 60000);
        waitAndAssertTimestamp(p2keyA, 2, 8);
        waitAndAssertTimestamp(p3keyB, 2, 3);
        Assert.assertEquals(false, lockA.get());
        Assert.assertEquals(false, lockB.get());
    }

    @Test
    public void testYieldWhileHoldingLocks() {
        final AtomicBoolean lock = new AtomicBoolean(false);
        final TestProcedureSuspended.TestLockProcedure p1 = new TestProcedureSuspended.TestLockProcedure(lock, "key", true, false);
        final TestProcedureSuspended.TestLockProcedure p2 = new TestProcedureSuspended.TestLockProcedure(lock, "key", true, false);
        procExecutor.submitProcedure(p1);
        procExecutor.submitProcedure(p2);
        // try to execute a bunch of yield on p1, p2 should be blocked
        while ((p1.getTimestamps().size()) < 100)
            Threads.sleep(10);

        Assert.assertEquals(0, p2.getTimestamps().size());
        // wait until p1 is completed
        p1.setThrowYield(false);
        ProcedureTestingUtility.waitProcedure(procExecutor, p1);
        // try to execute a bunch of yield on p2
        while ((p2.getTimestamps().size()) < 100)
            Threads.sleep(10);

        Assert.assertEquals(((p1.getTimestamps().get(((p1.getTimestamps().size()) - 1)).longValue()) + 1), p2.getTimestamps().get(0).longValue());
        // wait until p2 is completed
        p1.setThrowYield(false);
        ProcedureTestingUtility.waitProcedure(procExecutor, p1);
    }

    public static class TestLockProcedure extends Procedure<TestProcedureSuspended.TestProcEnv> {
        private final ArrayList<Long> timestamps = new ArrayList<>();

        private final String key;

        private boolean triggerRollback = false;

        private boolean throwSuspend = false;

        private boolean throwYield = false;

        private AtomicBoolean lock = null;

        private boolean hasLock = false;

        public TestLockProcedure(final AtomicBoolean lock, final String key, final boolean throwYield, final boolean throwSuspend) {
            this.lock = lock;
            this.key = key;
            this.throwYield = throwYield;
            this.throwSuspend = throwSuspend;
        }

        public void setThrowYield(final boolean throwYield) {
            this.throwYield = throwYield;
        }

        public void setThrowSuspend(final boolean throwSuspend) {
            this.throwSuspend = throwSuspend;
        }

        public void setTriggerRollback(final boolean triggerRollback) {
            this.triggerRollback = triggerRollback;
        }

        @Override
        protected Procedure[] execute(final TestProcedureSuspended.TestProcEnv env) throws ProcedureSuspendedException, ProcedureYieldException {
            TestProcedureSuspended.LOG.info(((("EXECUTE " + (this)) + " suspend ") + ((lock) != null)));
            timestamps.add(env.nextTimestamp());
            if (triggerRollback) {
                setFailure(getClass().getSimpleName(), new Exception("injected failure"));
            } else
                if (throwYield) {
                    throw new ProcedureYieldException();
                } else
                    if (throwSuspend) {
                        throw new ProcedureSuspendedException();
                    }


            return null;
        }

        @Override
        protected void rollback(final TestProcedureSuspended.TestProcEnv env) {
            TestProcedureSuspended.LOG.info(("ROLLBACK " + (this)));
            timestamps.add(((env.nextTimestamp()) * 10000));
        }

        @Override
        protected LockState acquireLock(final TestProcedureSuspended.TestProcEnv env) {
            if (hasLock = lock.compareAndSet(false, true)) {
                TestProcedureSuspended.LOG.info(((("ACQUIRE LOCK " + (this)) + " ") + (hasLock)));
                return LOCK_ACQUIRED;
            }
            return LOCK_YIELD_WAIT;
        }

        @Override
        protected void releaseLock(final TestProcedureSuspended.TestProcEnv env) {
            TestProcedureSuspended.LOG.info(((("RELEASE LOCK " + (this)) + " ") + (hasLock)));
            lock.set(false);
        }

        @Override
        protected boolean holdLock(final TestProcedureSuspended.TestProcEnv env) {
            return true;
        }

        public ArrayList<Long> getTimestamps() {
            return timestamps;
        }

        @Override
        protected void toStringClassDetails(StringBuilder builder) {
            builder.append(getClass().getName());
            builder.append((("(" + (key)) + ")"));
        }

        @Override
        protected boolean abort(TestProcedureSuspended.TestProcEnv env) {
            return false;
        }

        @Override
        protected void serializeStateData(ProcedureStateSerializer serializer) throws IOException {
        }

        @Override
        protected void deserializeStateData(ProcedureStateSerializer serializer) throws IOException {
        }
    }

    private static class TestProcEnv {
        public final AtomicLong timestamp = new AtomicLong(0);

        public long nextTimestamp() {
            return timestamp.incrementAndGet();
        }
    }
}

