/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode.checker;


import com.google.common.util.concurrent.ListenableFuture;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.util.FakeTimer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verify functionality of {@link ThrottledAsyncChecker}.
 */
public class TestThrottledAsyncChecker {
    public static final Logger LOG = LoggerFactory.getLogger(TestThrottledAsyncChecker.class);

    private static final long MIN_ERROR_CHECK_GAP = 1000;

    /**
     * Test various scheduling combinations to ensure scheduling and
     * throttling behave as expected.
     */
    @Test(timeout = 60000)
    public void testScheduler() throws Exception {
        final TestThrottledAsyncChecker.NoOpCheckable target1 = new TestThrottledAsyncChecker.NoOpCheckable();
        final TestThrottledAsyncChecker.NoOpCheckable target2 = new TestThrottledAsyncChecker.NoOpCheckable();
        final FakeTimer timer = new FakeTimer();
        ThrottledAsyncChecker<Boolean, Boolean> checker = new ThrottledAsyncChecker(timer, TestThrottledAsyncChecker.MIN_ERROR_CHECK_GAP, 0, getExecutorService());
        // check target1 and ensure we get back the expected result.
        Assert.assertTrue(checker.schedule(target1, true).isPresent());
        waitTestCheckableCheckCount(target1, 1L);
        // Check target1 again without advancing the timer. target1 should not
        // be checked again.
        Assert.assertFalse(checker.schedule(target1, true).isPresent());
        waitTestCheckableCheckCount(target1, 1L);
        // Schedule target2 scheduled without advancing the timer.
        // target2 should be checked as it has never been checked before.
        Assert.assertTrue(checker.schedule(target2, true).isPresent());
        waitTestCheckableCheckCount(target2, 1L);
        // Advance the timer but just short of the min gap.
        // Neither target1 nor target2 should be checked again.
        timer.advance(((TestThrottledAsyncChecker.MIN_ERROR_CHECK_GAP) - 1));
        Assert.assertFalse(checker.schedule(target1, true).isPresent());
        waitTestCheckableCheckCount(target1, 1L);
        Assert.assertFalse(checker.schedule(target2, true).isPresent());
        waitTestCheckableCheckCount(target2, 1L);
        // Advance the timer again.
        // Both targets should be checked now.
        timer.advance(TestThrottledAsyncChecker.MIN_ERROR_CHECK_GAP);
        Assert.assertTrue(checker.schedule(target1, true).isPresent());
        waitTestCheckableCheckCount(target1, 2L);
        Assert.assertTrue(checker.schedule(target2, true).isPresent());
        waitTestCheckableCheckCount(target2, 2L);
    }

    @Test(timeout = 60000)
    public void testConcurrentChecks() throws Exception {
        final TestThrottledAsyncChecker.StalledCheckable target = new TestThrottledAsyncChecker.StalledCheckable();
        final FakeTimer timer = new FakeTimer();
        ThrottledAsyncChecker<Boolean, Boolean> checker = new ThrottledAsyncChecker(timer, TestThrottledAsyncChecker.MIN_ERROR_CHECK_GAP, 0, getExecutorService());
        final Optional<ListenableFuture<Boolean>> olf1 = checker.schedule(target, true);
        final Optional<ListenableFuture<Boolean>> olf2 = checker.schedule(target, true);
        // Ensure that concurrent requests return the future object
        // for the first caller.
        Assert.assertTrue(olf1.isPresent());
        Assert.assertFalse(olf2.isPresent());
    }

    /**
     * Ensure that the context is passed through to the Checkable#check
     * method.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testContextIsPassed() throws Exception {
        final TestThrottledAsyncChecker.NoOpCheckable target1 = new TestThrottledAsyncChecker.NoOpCheckable();
        final FakeTimer timer = new FakeTimer();
        ThrottledAsyncChecker<Boolean, Boolean> checker = new ThrottledAsyncChecker(timer, TestThrottledAsyncChecker.MIN_ERROR_CHECK_GAP, 0, getExecutorService());
        Assert.assertTrue(checker.schedule(target1, true).isPresent());
        waitTestCheckableCheckCount(target1, 1L);
        timer.advance(((TestThrottledAsyncChecker.MIN_ERROR_CHECK_GAP) + 1));
        Assert.assertTrue(checker.schedule(target1, false).isPresent());
        waitTestCheckableCheckCount(target1, 2L);
    }

    /**
     * Ensure that an exception thrown by the check routine is
     * propagated.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testExceptionIsPropagated() throws Exception {
        final TestThrottledAsyncChecker.ThrowingCheckable target = new TestThrottledAsyncChecker.ThrowingCheckable();
        final FakeTimer timer = new FakeTimer();
        ThrottledAsyncChecker<Boolean, Boolean> checker = new ThrottledAsyncChecker(timer, TestThrottledAsyncChecker.MIN_ERROR_CHECK_GAP, 0, getExecutorService());
        final Optional<ListenableFuture<Boolean>> olf = checker.schedule(target, true);
        Assert.assertTrue(olf.isPresent());
        try {
            olf.get().get();
            Assert.fail("Failed to get expected ExecutionException");
        } catch (ExecutionException ee) {
            Assert.assertTrue(((ee.getCause()) instanceof TestThrottledAsyncChecker.DummyException));
        }
    }

    /**
     * Ensure that the exception from a failed check is cached
     * and returned without re-running the check when the minimum
     * gap has not elapsed.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testExceptionCaching() throws Exception {
        final TestThrottledAsyncChecker.ThrowingCheckable target1 = new TestThrottledAsyncChecker.ThrowingCheckable();
        final FakeTimer timer = new FakeTimer();
        ThrottledAsyncChecker<Boolean, Boolean> checker = new ThrottledAsyncChecker(timer, TestThrottledAsyncChecker.MIN_ERROR_CHECK_GAP, 0, getExecutorService());
        Assert.assertTrue(checker.schedule(target1, true).isPresent());
        waitTestCheckableCheckCount(target1, 1L);
        Assert.assertFalse(checker.schedule(target1, true).isPresent());
        waitTestCheckableCheckCount(target1, 1L);
    }

    private abstract static class TestCheckableBase implements Checkable<Boolean, Boolean> {
        protected final AtomicLong numChecks = new AtomicLong(0);

        public long getTotalChecks() {
            return numChecks.get();
        }

        public void incrTotalChecks() {
            numChecks.incrementAndGet();
        }
    }

    /**
     * A Checkable that just returns its input.
     */
    private static class NoOpCheckable extends TestThrottledAsyncChecker.TestCheckableBase {
        @Override
        public Boolean check(Boolean context) {
            incrTotalChecks();
            return context;
        }
    }

    /**
     * A Checkable that throws an exception when checked.
     */
    private static class ThrowingCheckable extends TestThrottledAsyncChecker.TestCheckableBase {
        @Override
        public Boolean check(Boolean context) throws TestThrottledAsyncChecker.DummyException {
            incrTotalChecks();
            throw new TestThrottledAsyncChecker.DummyException();
        }
    }

    private static class DummyException extends Exception {}

    /**
     * A checkable that hangs forever when checked.
     */
    private static class StalledCheckable implements Checkable<Boolean, Boolean> {
        @Override
        public Boolean check(Boolean ignored) throws InterruptedException {
            Thread.sleep(Long.MAX_VALUE);
            return false;// Unreachable.

        }
    }
}

