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
package org.apache.flink.runtime.taskmanager;


import ExecutionState.FINISHED;
import ExecutionState.RUNNING;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.checkpoint.CheckpointMetaData;
import org.apache.flink.runtime.checkpoint.CheckpointMetrics;
import org.apache.flink.runtime.checkpoint.CheckpointOptions;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.execution.ExecutionState;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.jobgraph.tasks.StoppableTask;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TaskAsyncCallTest extends TestLogger {
    /**
     * Number of expected checkpoints.
     */
    private static int numCalls;

    /**
     * Triggered at the beginning of {@link CheckpointsInOrderInvokable#invoke()}.
     */
    private static OneShotLatch awaitLatch;

    /**
     * Triggered when {@link CheckpointsInOrderInvokable#triggerCheckpoint(CheckpointMetaData, CheckpointOptions)}
     * was called {@link #numCalls} times.
     */
    private static OneShotLatch triggerLatch;

    /**
     * Triggered when {@link CheckpointsInOrderInvokable#notifyCheckpointComplete(long)}
     * was called {@link #numCalls} times.
     */
    private static OneShotLatch notifyCheckpointCompleteLatch;

    /**
     * Triggered on {@link ContextClassLoaderInterceptingInvokable#stop()}}.
     */
    private static OneShotLatch stopLatch;

    private static final List<ClassLoader> classLoaders = Collections.synchronizedList(new ArrayList<>());

    // ------------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------------
    @Test
    public void testCheckpointCallsInOrder() throws Exception {
        Task task = createTask(TaskAsyncCallTest.CheckpointsInOrderInvokable.class);
        try (TaskAsyncCallTest.TaskCleaner ignored = new TaskAsyncCallTest.TaskCleaner(task)) {
            task.startTaskThread();
            TaskAsyncCallTest.awaitLatch.await();
            for (int i = 1; i <= (TaskAsyncCallTest.numCalls); i++) {
                task.triggerCheckpointBarrier(i, 156865867234L, CheckpointOptions.forCheckpointWithDefaultLocation());
            }
            TaskAsyncCallTest.triggerLatch.await();
            Assert.assertFalse(task.isCanceledOrFailed());
            ExecutionState currentState = task.getExecutionState();
            Assert.assertThat(currentState, Matchers.isOneOf(RUNNING, FINISHED));
        }
    }

    @Test
    public void testMixedAsyncCallsInOrder() throws Exception {
        Task task = createTask(TaskAsyncCallTest.CheckpointsInOrderInvokable.class);
        try (TaskAsyncCallTest.TaskCleaner ignored = new TaskAsyncCallTest.TaskCleaner(task)) {
            task.startTaskThread();
            TaskAsyncCallTest.awaitLatch.await();
            for (int i = 1; i <= (TaskAsyncCallTest.numCalls); i++) {
                task.triggerCheckpointBarrier(i, 156865867234L, CheckpointOptions.forCheckpointWithDefaultLocation());
                task.notifyCheckpointComplete(i);
            }
            TaskAsyncCallTest.triggerLatch.await();
            Assert.assertFalse(task.isCanceledOrFailed());
            ExecutionState currentState = task.getExecutionState();
            Assert.assertThat(currentState, Matchers.isOneOf(RUNNING, FINISHED));
        }
    }

    @Test
    public void testThrowExceptionIfStopInvokedWithNotStoppableTask() throws Exception {
        Task task = createTask(TaskAsyncCallTest.CheckpointsInOrderInvokable.class);
        try (TaskAsyncCallTest.TaskCleaner ignored = new TaskAsyncCallTest.TaskCleaner(task)) {
            task.startTaskThread();
            TaskAsyncCallTest.awaitLatch.await();
            try {
                task.stopExecution();
                Assert.fail("Expected exception not thrown");
            } catch (UnsupportedOperationException e) {
                Assert.assertThat(e.getMessage(), Matchers.containsString("Stopping not supported by task"));
            }
        }
    }

    /**
     * Asserts that {@link AbstractInvokable#triggerCheckpoint(CheckpointMetaData, CheckpointOptions)},
     * {@link AbstractInvokable#notifyCheckpointComplete(long)}, and {@link StoppableTask#stop()} are
     * invoked by a thread whose context class loader is set to the user code class loader.
     */
    @Test
    public void testSetsUserCodeClassLoader() throws Exception {
        TaskAsyncCallTest.numCalls = 1;
        Task task = createTask(TaskAsyncCallTest.ContextClassLoaderInterceptingInvokable.class);
        try (TaskAsyncCallTest.TaskCleaner ignored = new TaskAsyncCallTest.TaskCleaner(task)) {
            task.startTaskThread();
            TaskAsyncCallTest.awaitLatch.await();
            task.triggerCheckpointBarrier(1, 1, CheckpointOptions.forCheckpointWithDefaultLocation());
            task.notifyCheckpointComplete(1);
            task.stopExecution();
            TaskAsyncCallTest.triggerLatch.await();
            TaskAsyncCallTest.notifyCheckpointCompleteLatch.await();
            TaskAsyncCallTest.stopLatch.await();
            Assert.assertThat(TaskAsyncCallTest.classLoaders, Matchers.hasSize(Matchers.greaterThanOrEqualTo(3)));
            Assert.assertThat(TaskAsyncCallTest.classLoaders, Matchers.everyItem(Matchers.instanceOf(TaskAsyncCallTest.TestUserCodeClassLoader.class)));
        }
    }

    public static class CheckpointsInOrderInvokable extends AbstractInvokable {
        private volatile long lastCheckpointId = 0;

        private volatile Exception error;

        public CheckpointsInOrderInvokable(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            TaskAsyncCallTest.awaitLatch.trigger();
            // wait forever (until canceled)
            synchronized(this) {
                while ((error) == null) {
                    wait();
                } 
            }
            if ((error) != null) {
                // exit method prematurely due to error but make sure that the tests can finish
                TaskAsyncCallTest.triggerLatch.trigger();
                TaskAsyncCallTest.notifyCheckpointCompleteLatch.trigger();
                TaskAsyncCallTest.stopLatch.trigger();
                throw error;
            }
        }

        @Override
        public boolean triggerCheckpoint(CheckpointMetaData checkpointMetaData, CheckpointOptions checkpointOptions) {
            (lastCheckpointId)++;
            if ((checkpointMetaData.getCheckpointId()) == (lastCheckpointId)) {
                if ((lastCheckpointId) == (TaskAsyncCallTest.numCalls)) {
                    TaskAsyncCallTest.triggerLatch.trigger();
                }
            } else
                if ((this.error) == null) {
                    this.error = new Exception("calls out of order");
                    synchronized(this) {
                        notifyAll();
                    }
                }

            return true;
        }

        @Override
        public void triggerCheckpointOnBarrier(CheckpointMetaData checkpointMetaData, CheckpointOptions checkpointOptions, CheckpointMetrics checkpointMetrics) throws Exception {
            throw new UnsupportedOperationException("Should not be called");
        }

        @Override
        public void abortCheckpointOnBarrier(long checkpointId, Throwable cause) {
            throw new UnsupportedOperationException("Should not be called");
        }

        @Override
        public void notifyCheckpointComplete(long checkpointId) {
            if ((checkpointId != (lastCheckpointId)) && ((this.error) == null)) {
                this.error = new Exception("calls out of order");
                synchronized(this) {
                    notifyAll();
                }
            } else
                if ((lastCheckpointId) == (TaskAsyncCallTest.numCalls)) {
                    TaskAsyncCallTest.notifyCheckpointCompleteLatch.trigger();
                }

        }
    }

    /**
     * This is an {@link AbstractInvokable} that stores the context class loader of the invoking
     * thread in a static field so that tests can assert on the class loader instances.
     *
     * @see #testSetsUserCodeClassLoader()
     */
    public static class ContextClassLoaderInterceptingInvokable extends TaskAsyncCallTest.CheckpointsInOrderInvokable implements StoppableTask {
        public ContextClassLoaderInterceptingInvokable(Environment environment) {
            super(environment);
        }

        @Override
        public boolean triggerCheckpoint(CheckpointMetaData checkpointMetaData, CheckpointOptions checkpointOptions) {
            TaskAsyncCallTest.classLoaders.add(Thread.currentThread().getContextClassLoader());
            return super.triggerCheckpoint(checkpointMetaData, checkpointOptions);
        }

        @Override
        public void notifyCheckpointComplete(long checkpointId) {
            TaskAsyncCallTest.classLoaders.add(Thread.currentThread().getContextClassLoader());
            super.notifyCheckpointComplete(checkpointId);
        }

        @Override
        public void stop() {
            TaskAsyncCallTest.classLoaders.add(Thread.currentThread().getContextClassLoader());
            TaskAsyncCallTest.stopLatch.trigger();
        }
    }

    /**
     * A {@link ClassLoader} that delegates everything to {@link ClassLoader#getSystemClassLoader()}.
     *
     * @see #testSetsUserCodeClassLoader()
     */
    private static class TestUserCodeClassLoader extends ClassLoader {
        public TestUserCodeClassLoader() {
            super(ClassLoader.getSystemClassLoader());
        }
    }

    private static class TaskCleaner implements AutoCloseable {
        private final Task task;

        private TaskCleaner(Task task) {
            this.task = task;
        }

        @Override
        public void close() throws Exception {
            task.cancelExecution();
            task.getExecutingThread().join(5000);
        }
    }
}

