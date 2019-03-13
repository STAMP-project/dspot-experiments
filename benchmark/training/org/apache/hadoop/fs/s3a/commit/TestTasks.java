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
package org.apache.hadoop.fs.s3a.commit;


import Tasks.FailureTask;
import Tasks.Task;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.hadoop.test.HadoopTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test Tasks class.
 */
@RunWith(Parameterized.class)
public class TestTasks extends HadoopTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(TestTasks.class);

    public static final int ITEM_COUNT = 16;

    private static final int FAILPOINT = 8;

    private final int numThreads;

    /**
     * Thread pool for task execution.
     */
    private ExecutorService threadPool;

    private final TestTasks.CounterTask failingTask = new TestTasks.CounterTask("failing committer", TestTasks.FAILPOINT, TestTasks.Item::commit);

    private final TestTasks.FailureCounter failures = new TestTasks.FailureCounter("failures", 0, null);

    private final TestTasks.CounterTask reverter = new TestTasks.CounterTask("reverter", 0, TestTasks.Item::revert);

    private final TestTasks.CounterTask aborter = new TestTasks.CounterTask("aborter", 0, TestTasks.Item::abort);

    private List<TestTasks.Item> items;

    /**
     * Construct the parameterized test.
     *
     * @param numThreads
     * 		number of threads
     */
    public TestTasks(int numThreads) {
        this.numThreads = numThreads;
    }

    @Test
    public void testSimpleInvocation() throws Throwable {
        TestTasks.CounterTask t = new TestTasks.CounterTask("simple", 0, TestTasks.Item::commit);
        assertRun(builder(), t);
        t.assertInvoked("", TestTasks.ITEM_COUNT);
    }

    @Test
    public void testFailNoStoppingSuppressed() throws Throwable {
        assertFailed(builder().suppressExceptions(), failingTask);
        failingTask.assertInvoked("Continued through operations", TestTasks.ITEM_COUNT);
        items.forEach(TestTasks.Item::assertCommittedOrFailed);
    }

    @Test
    public void testFailFastSuppressed() throws Throwable {
        assertFailed(builder().suppressExceptions().stopOnFailure(), failingTask);
        if (isParallel()) {
            failingTask.assertInvokedAtLeast("stop fast", TestTasks.FAILPOINT);
        } else {
            failingTask.assertInvoked("stop fast", TestTasks.FAILPOINT);
        }
    }

    @Test
    public void testFailedCallAbortSuppressed() throws Throwable {
        assertFailed(builder().stopOnFailure().suppressExceptions().abortWith(aborter), failingTask);
        failingTask.assertInvokedAtLeast("success", TestTasks.FAILPOINT);
        if (!(isParallel())) {
            aborter.assertInvokedAtLeast("abort", 1);
            // all uncommitted items were aborted
            items.stream().filter(( i) -> !(i.committed)).map(TestTasks.Item::assertAborted);
            items.stream().filter(( i) -> i.committed).forEach(( i) -> assertFalse(i.toString(), i.aborted));
        }
    }

    @Test
    public void testFailedCalledWhenNotStoppingSuppressed() throws Throwable {
        assertFailed(builder().suppressExceptions().onFailure(failures), failingTask);
        failingTask.assertInvokedAtLeast("success", TestTasks.FAILPOINT);
        // only one failure was triggered
        failures.assertInvoked("failure event", 1);
    }

    @Test
    public void testFailFastCallRevertSuppressed() throws Throwable {
        assertFailed(builder().stopOnFailure().revertWith(reverter).abortWith(aborter).suppressExceptions().onFailure(failures), failingTask);
        failingTask.assertInvokedAtLeast("success", TestTasks.FAILPOINT);
        if (!(isParallel())) {
            aborter.assertInvokedAtLeast("abort", 1);
            // all uncommitted items were aborted
            items.stream().filter(( i) -> !(i.committed)).filter(( i) -> !(i.failed)).forEach(TestTasks.Item::assertAborted);
        }
        // all committed were reverted
        items.stream().filter(( i) -> (i.committed) && (!(i.failed))).forEach(TestTasks.Item::assertReverted);
        // all reverted items are committed
        items.stream().filter(( i) -> i.reverted).forEach(TestTasks.Item::assertCommitted);
        // only one failure was triggered
        failures.assertInvoked("failure event", 1);
    }

    @Test
    public void testFailSlowCallRevertSuppressed() throws Throwable {
        assertFailed(builder().suppressExceptions().revertWith(reverter).onFailure(failures), failingTask);
        failingTask.assertInvokedAtLeast("success", TestTasks.FAILPOINT);
        // all committed were reverted
        // identify which task failed from the set
        int failing = failures.getItem().id;
        items.stream().filter(( i) -> (i.id) != failing).filter(( i) -> i.committed).forEach(TestTasks.Item::assertReverted);
        // all reverted items are committed
        items.stream().filter(( i) -> i.reverted).forEach(TestTasks.Item::assertCommitted);
        // only one failure was triggered
        failures.assertInvoked("failure event", 1);
    }

    @Test
    public void testFailFastExceptions() throws Throwable {
        intercept(IOException.class, () -> builder().stopOnFailure().run(failingTask));
        if (isParallel()) {
            failingTask.assertInvokedAtLeast("stop fast", TestTasks.FAILPOINT);
        } else {
            failingTask.assertInvoked("stop fast", TestTasks.FAILPOINT);
        }
    }

    @Test
    public void testFailSlowExceptions() throws Throwable {
        intercept(IOException.class, () -> builder().run(failingTask));
        failingTask.assertInvoked("continued through operations", TestTasks.ITEM_COUNT);
        items.forEach(TestTasks.Item::assertCommittedOrFailed);
    }

    @Test
    public void testFailFastExceptionsWithAbortFailure() throws Throwable {
        TestTasks.CounterTask failFirst = new TestTasks.CounterTask("task", 1, TestTasks.Item::commit);
        TestTasks.CounterTask a = new TestTasks.CounterTask("aborter", 1, TestTasks.Item::abort);
        intercept(IOException.class, () -> builder().stopOnFailure().abortWith(a).run(failFirst));
        if (!(isParallel())) {
            // expect the other tasks to be aborted
            a.assertInvokedAtLeast("abort", ((TestTasks.ITEM_COUNT) - 1));
        }
    }

    @Test
    public void testFailFastExceptionsWithAbortFailureStopped() throws Throwable {
        TestTasks.CounterTask failFirst = new TestTasks.CounterTask("task", 1, TestTasks.Item::commit);
        TestTasks.CounterTask a = new TestTasks.CounterTask("aborter", 1, TestTasks.Item::abort);
        intercept(IOException.class, () -> builder().stopOnFailure().stopAbortsOnFailure().abortWith(a).run(failFirst));
        if (!(isParallel())) {
            // expect the other tasks to be aborted
            a.assertInvoked("abort", 1);
        }
    }

    /**
     * Fail the last one committed, all the rest will be reverted.
     * The actual ID of the last task has to be picke dup from the
     * failure callback, as in the pool it may be one of any.
     */
    @Test
    public void testRevertAllSuppressed() throws Throwable {
        TestTasks.CounterTask failLast = new TestTasks.CounterTask("task", TestTasks.ITEM_COUNT, TestTasks.Item::commit);
        assertFailed(builder().suppressExceptions().stopOnFailure().revertWith(reverter).abortWith(aborter).onFailure(failures), failLast);
        failLast.assertInvoked("success", TestTasks.ITEM_COUNT);
        int abCount = aborter.getCount();
        int revCount = reverter.getCount();
        assertEquals(TestTasks.ITEM_COUNT, ((1 + abCount) + revCount));
        // identify which task failed from the set
        int failing = failures.getItem().id;
        // all committed were reverted
        items.stream().filter(( i) -> (i.id) != failing).filter(( i) -> i.committed).forEach(TestTasks.Item::assertReverted);
        items.stream().filter(( i) -> (i.id) != failing).filter(( i) -> !(i.committed)).forEach(TestTasks.Item::assertAborted);
        // all reverted items are committed
        items.stream().filter(( i) -> i.reverted).forEach(TestTasks.Item::assertCommitted);
        // only one failure was triggered
        failures.assertInvoked("failure event", 1);
    }

    /**
     * The Item which tasks process.
     */
    private final class Item {
        private final int id;

        private final String text;

        private volatile boolean committed;

        private volatile boolean aborted;

        private volatile boolean reverted;

        private volatile boolean failed;

        private Item(int item, String text) {
            this.id = item;
            this.text = text;
        }

        boolean commit() {
            committed = true;
            return true;
        }

        boolean abort() {
            aborted = true;
            return true;
        }

        boolean revert() {
            reverted = true;
            return true;
        }

        boolean fail() {
            failed = true;
            return true;
        }

        public TestTasks.Item assertCommitted() {
            assertTrue((((toString()) + " was not committed in\n") + (itemsToString())), committed);
            return this;
        }

        public TestTasks.Item assertCommittedOrFailed() {
            assertTrue((((toString()) + " was not committed nor failed in\n") + (itemsToString())), ((committed) || (failed)));
            return this;
        }

        public TestTasks.Item assertAborted() {
            assertTrue((((toString()) + " was not aborted in\n") + (itemsToString())), aborted);
            return this;
        }

        public TestTasks.Item assertReverted() {
            assertTrue((((toString()) + " was not reverted in\n") + (itemsToString())), reverted);
            return this;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Item{");
            sb.append(String.format("[%02d]", id));
            sb.append(", committed=").append(committed);
            sb.append(", aborted=").append(aborted);
            sb.append(", reverted=").append(reverted);
            sb.append(", failed=").append(failed);
            sb.append(", text=").append(text);
            sb.append('}');
            return sb.toString();
        }
    }

    /**
     * Class which can count invocations and, if limit > 0, will raise
     * an exception on the specific invocation of {@link #note(Object)}
     * whose count == limit.
     */
    private class BaseCounter {
        private final AtomicInteger counter = new AtomicInteger(0);

        private final int limit;

        private final String name;

        private TestTasks.Item item;

        private final Optional<Function<TestTasks.Item, Boolean>> action;

        /**
         * Base counter, tracks items.
         *
         * @param name
         * 		name for string/exception/logs.
         * @param limit
         * 		limit at which an exception is raised, 0 == never
         * @param action
         * 		optional action to invoke after the increment,
         * 		before limit check
         */
        BaseCounter(String name, int limit, Function<TestTasks.Item, Boolean> action) {
            this.name = name;
            this.limit = limit;
            this.action = Optional.ofNullable(action);
        }

        /**
         * Apply the action to an item; log at info afterwards with both the
         * before and after string values of the item.
         *
         * @param i
         * 		item to process.
         * @throws IOException
         * 		failure in the action
         */
        void process(TestTasks.Item i) throws IOException {
            this.item = i;
            int count = counter.incrementAndGet();
            if ((limit) == count) {
                i.fail();
                TestTasks.LOG.info("{}: Failed {}", this, i);
                throw new IOException(String.format("%s: Limit %d reached for %s", this, limit, i));
            }
            String before = i.toString();
            action.map(( a) -> a.apply(i));
            TestTasks.LOG.info("{}: {} -> {}", this, before, i);
        }

        int getCount() {
            return counter.get();
        }

        TestTasks.Item getItem() {
            return item;
        }

        void assertInvoked(String text, int expected) {
            assertEquals((((toString()) + ": ") + text), expected, getCount());
        }

        void assertInvokedAtLeast(String text, int expected) {
            int actual = getCount();
            assertTrue((((((((((toString()) + ": ") + text) + "-expected ") + expected) + " invocations, but got ") + actual) + " in ") + (itemsToString())), (expected <= actual));
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BaseCounter{");
            sb.append("name='").append(name).append('\'');
            sb.append(", count=").append(counter.get());
            sb.append(", limit=").append(limit);
            sb.append(", item=").append(item);
            sb.append('}');
            return sb.toString();
        }
    }

    private final class CounterTask extends TestTasks.BaseCounter implements Task<TestTasks.Item, IOException> {
        private CounterTask(String name, int limit, Function<TestTasks.Item, Boolean> action) {
            super(name, limit, action);
        }

        @Override
        public void run(TestTasks.Item item) throws IOException {
            process(item);
        }
    }

    private final class FailureCounter extends TestTasks.BaseCounter implements FailureTask<TestTasks.Item, IOException> {
        private Exception exception;

        private FailureCounter(String name, int limit, Function<TestTasks.Item, Boolean> action) {
            super(name, limit, action);
        }

        @Override
        public void run(TestTasks.Item item, Exception ex) throws IOException {
            process(item);
            this.exception = ex;
        }

        private Exception getException() {
            return exception;
        }
    }
}

