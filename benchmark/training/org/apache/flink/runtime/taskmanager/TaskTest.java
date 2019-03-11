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


import BlobServerOptions.CLEANUP_INTERVAL;
import BlobServerOptions.STORAGE_DIRECTORY;
import ExecutionState.CANCELED;
import ExecutionState.CANCELING;
import ExecutionState.CREATED;
import ExecutionState.DEPLOYING;
import ExecutionState.FAILED;
import ExecutionState.FINISHED;
import ExecutionState.RUNNING;
import ExecutionState.SCHEDULED;
import FlinkUserCodeClassLoaders.ResolveOrder;
import IOManager.IOMode.SYNC;
import TaskManagerOptions.TASK_CANCELLATION_INTERVAL;
import TaskManagerOptions.TASK_CANCELLATION_TIMEOUT;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.blob.BlobCacheService;
import org.apache.flink.runtime.blob.BlobServer;
import org.apache.flink.runtime.blob.PermanentBlobCache;
import org.apache.flink.runtime.blob.PermanentBlobKey;
import org.apache.flink.runtime.blob.TransientBlobCache;
import org.apache.flink.runtime.blob.VoidBlobStore;
import org.apache.flink.runtime.broadcast.BroadcastVariableManager;
import org.apache.flink.runtime.clusterframework.types.AllocationID;
import org.apache.flink.runtime.concurrent.Executors;
import org.apache.flink.runtime.execution.CancelTaskException;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.execution.ExecutionState;
import org.apache.flink.runtime.execution.librarycache.BlobLibraryCacheManager;
import org.apache.flink.runtime.execution.librarycache.LibraryCacheManager;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.executiongraph.JobInformation;
import org.apache.flink.runtime.executiongraph.TaskInformation;
import org.apache.flink.runtime.filecache.FileCache;
import org.apache.flink.runtime.io.disk.iomanager.IOManager;
import org.apache.flink.runtime.io.network.NetworkEnvironment;
import org.apache.flink.runtime.io.network.TaskEventDispatcher;
import org.apache.flink.runtime.io.network.netty.PartitionProducerStateChecker;
import org.apache.flink.runtime.io.network.partition.NoOpResultPartitionConsumableNotifier;
import org.apache.flink.runtime.io.network.partition.ResultPartitionConsumableNotifier;
import org.apache.flink.runtime.io.network.partition.ResultPartitionID;
import org.apache.flink.runtime.io.network.partition.ResultPartitionManager;
import org.apache.flink.runtime.io.network.partition.consumer.SingleInputGate;
import org.apache.flink.runtime.jobgraph.IntermediateDataSetID;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.memory.MemoryManager;
import org.apache.flink.runtime.metrics.groups.TaskIOMetricGroup;
import org.apache.flink.runtime.metrics.groups.TaskMetricGroup;
import org.apache.flink.runtime.operators.testutils.MockInputSplitProvider;
import org.apache.flink.runtime.query.TaskKvStateRegistry;
import org.apache.flink.runtime.state.TestTaskStateManager;
import org.apache.flink.runtime.taskexecutor.TestGlobalAggregateManager;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.util.TestingTaskManagerRuntimeInfo;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.SerializedValue;
import org.apache.flink.util.TestLogger;
import org.apache.flink.util.WrappingRuntimeException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for the Task, which make sure that correct state transitions happen,
 * and failures are correctly handled.
 */
public class TaskTest extends TestLogger {
    private static OneShotLatch awaitLatch;

    private static OneShotLatch triggerLatch;

    @ClassRule
    public static final TemporaryFolder TEMPORARY_FOLDER = new TemporaryFolder();

    @Test
    public void testRegularExecution() throws Exception {
        final TaskTest.QueuedNoOpTaskManagerActions taskManagerActions = new TaskTest.QueuedNoOpTaskManagerActions();
        final Task task = new TaskTest.TaskBuilder().setTaskManagerActions(taskManagerActions).build();
        // task should be new and perfect
        Assert.assertEquals(CREATED, task.getExecutionState());
        Assert.assertFalse(task.isCanceledOrFailed());
        Assert.assertNull(task.getFailureCause());
        // go into the run method. we should switch to DEPLOYING, RUNNING, then
        // FINISHED, and all should be good
        task.run();
        // verify final state
        Assert.assertEquals(FINISHED, task.getExecutionState());
        Assert.assertFalse(task.isCanceledOrFailed());
        Assert.assertNull(task.getFailureCause());
        Assert.assertNull(task.getInvokable());
        taskManagerActions.validateListenerMessage(RUNNING, task, null);
        taskManagerActions.validateListenerMessage(FINISHED, task, null);
    }

    @Test
    public void testCancelRightAway() throws Exception {
        final Task task = new TaskTest.TaskBuilder().build();
        task.cancelExecution();
        Assert.assertEquals(CANCELING, task.getExecutionState());
        task.run();
        // verify final state
        Assert.assertEquals(CANCELED, task.getExecutionState());
        Assert.assertNull(task.getInvokable());
    }

    @Test
    public void testFailExternallyRightAway() throws Exception {
        final Task task = new TaskTest.TaskBuilder().build();
        task.failExternally(new Exception("fail externally"));
        Assert.assertEquals(FAILED, task.getExecutionState());
        task.run();
        // verify final state
        Assert.assertEquals(FAILED, task.getExecutionState());
    }

    @Test
    public void testLibraryCacheRegistrationFailed() throws Exception {
        final TaskTest.QueuedNoOpTaskManagerActions taskManagerActions = new TaskTest.QueuedNoOpTaskManagerActions();
        final Task task = // inactive manager
        new TaskTest.TaskBuilder().setTaskManagerActions(taskManagerActions).setLibraryCacheManager(Mockito.mock(LibraryCacheManager.class)).build();
        // task should be new and perfect
        Assert.assertEquals(CREATED, task.getExecutionState());
        Assert.assertFalse(task.isCanceledOrFailed());
        Assert.assertNull(task.getFailureCause());
        // should fail
        task.run();
        // verify final state
        Assert.assertEquals(FAILED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        Assert.assertNotNull(task.getFailureCause());
        Assert.assertNotNull(task.getFailureCause().getMessage());
        Assert.assertTrue(task.getFailureCause().getMessage().contains("classloader"));
        Assert.assertNull(task.getInvokable());
        taskManagerActions.validateListenerMessage(FAILED, task, new Exception("No user code classloader available."));
    }

    @Test
    public void testExecutionFailsInBlobsMissing() throws Exception {
        final PermanentBlobKey missingKey = new PermanentBlobKey();
        final Configuration config = new Configuration();
        config.setString(STORAGE_DIRECTORY, TaskTest.TEMPORARY_FOLDER.newFolder().getAbsolutePath());
        config.setLong(CLEANUP_INTERVAL, 1L);
        final BlobServer blobServer = new BlobServer(config, new VoidBlobStore());
        blobServer.start();
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", blobServer.getPort());
        final PermanentBlobCache permanentBlobCache = new PermanentBlobCache(config, new VoidBlobStore(), serverAddress);
        final BlobLibraryCacheManager libraryCacheManager = new BlobLibraryCacheManager(permanentBlobCache, ResolveOrder.CHILD_FIRST, new String[0]);
        final Task task = new TaskTest.TaskBuilder().setRequiredJarFileBlobKeys(Collections.singletonList(missingKey)).setLibraryCacheManager(libraryCacheManager).build();
        // task should be new and perfect
        Assert.assertEquals(CREATED, task.getExecutionState());
        Assert.assertFalse(task.isCanceledOrFailed());
        Assert.assertNull(task.getFailureCause());
        // should fail
        task.run();
        // verify final state
        Assert.assertEquals(FAILED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        Assert.assertNotNull(task.getFailureCause());
        Assert.assertNotNull(task.getFailureCause().getMessage());
        Assert.assertTrue(task.getFailureCause().getMessage().contains("Failed to fetch BLOB"));
        Assert.assertNull(task.getInvokable());
    }

    @Test
    public void testExecutionFailsInNetworkRegistration() throws Exception {
        // mock a network manager that rejects registration
        final ResultPartitionManager partitionManager = Mockito.mock(ResultPartitionManager.class);
        final ResultPartitionConsumableNotifier consumableNotifier = new NoOpResultPartitionConsumableNotifier();
        final PartitionProducerStateChecker partitionProducerStateChecker = Mockito.mock(PartitionProducerStateChecker.class);
        final TaskEventDispatcher taskEventDispatcher = Mockito.mock(TaskEventDispatcher.class);
        final NetworkEnvironment network = Mockito.mock(NetworkEnvironment.class);
        Mockito.when(network.getResultPartitionManager()).thenReturn(partitionManager);
        Mockito.when(network.getDefaultIOMode()).thenReturn(SYNC);
        Mockito.when(network.getTaskEventDispatcher()).thenReturn(taskEventDispatcher);
        Mockito.doThrow(new RuntimeException("buffers")).when(network).registerTask(ArgumentMatchers.any(Task.class));
        final TaskTest.QueuedNoOpTaskManagerActions taskManagerActions = new TaskTest.QueuedNoOpTaskManagerActions();
        final Task task = new TaskTest.TaskBuilder().setTaskManagerActions(taskManagerActions).setConsumableNotifier(consumableNotifier).setPartitionProducerStateChecker(partitionProducerStateChecker).setNetworkEnvironment(network).build();
        // should fail
        task.run();
        // verify final state
        Assert.assertEquals(FAILED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        Assert.assertTrue(task.getFailureCause().getMessage().contains("buffers"));
        taskManagerActions.validateListenerMessage(FAILED, task, new RuntimeException("buffers"));
    }

    @Test
    public void testInvokableInstantiationFailed() throws Exception {
        final TaskTest.QueuedNoOpTaskManagerActions taskManagerActions = new TaskTest.QueuedNoOpTaskManagerActions();
        final Task task = new TaskTest.TaskBuilder().setTaskManagerActions(taskManagerActions).setInvokable(TaskTest.InvokableNonInstantiable.class).build();
        // should fail
        task.run();
        // verify final state
        Assert.assertEquals(FAILED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        Assert.assertTrue(task.getFailureCause().getMessage().contains("instantiate"));
        taskManagerActions.validateListenerMessage(FAILED, task, new FlinkException("Could not instantiate the task's invokable class."));
    }

    @Test
    public void testExecutionFailsInInvoke() throws Exception {
        final TaskTest.QueuedNoOpTaskManagerActions taskManagerActions = new TaskTest.QueuedNoOpTaskManagerActions();
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableWithExceptionInInvoke.class).setTaskManagerActions(taskManagerActions).build();
        task.run();
        Assert.assertEquals(FAILED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        Assert.assertNotNull(task.getFailureCause());
        Assert.assertNotNull(task.getFailureCause().getMessage());
        Assert.assertTrue(task.getFailureCause().getMessage().contains("test"));
        taskManagerActions.validateListenerMessage(RUNNING, task, null);
        taskManagerActions.validateListenerMessage(FAILED, task, new Exception("test"));
    }

    @Test
    public void testFailWithWrappedException() throws Exception {
        final TaskTest.QueuedNoOpTaskManagerActions taskManagerActions = new TaskTest.QueuedNoOpTaskManagerActions();
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.FailingInvokableWithChainedException.class).setTaskManagerActions(taskManagerActions).build();
        task.run();
        Assert.assertEquals(FAILED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        final Throwable cause = task.getFailureCause();
        Assert.assertTrue((cause instanceof IOException));
        taskManagerActions.validateListenerMessage(RUNNING, task, null);
        taskManagerActions.validateListenerMessage(FAILED, task, new IOException("test"));
    }

    @Test
    public void testCancelDuringInvoke() throws Exception {
        final TaskTest.QueuedNoOpTaskManagerActions taskManagerActions = new TaskTest.QueuedNoOpTaskManagerActions();
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableBlockingInInvoke.class).setTaskManagerActions(taskManagerActions).build();
        // run the task asynchronous
        task.startTaskThread();
        // wait till the task is in invoke
        TaskTest.awaitLatch.await();
        task.cancelExecution();
        Assert.assertTrue((((task.getExecutionState()) == (ExecutionState.CANCELING)) || ((task.getExecutionState()) == (ExecutionState.CANCELED))));
        task.getExecutingThread().join();
        Assert.assertEquals(CANCELED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        Assert.assertNull(task.getFailureCause());
        taskManagerActions.validateListenerMessage(RUNNING, task, null);
        taskManagerActions.validateListenerMessage(CANCELED, task, null);
    }

    @Test
    public void testFailExternallyDuringInvoke() throws Exception {
        final TaskTest.QueuedNoOpTaskManagerActions taskManagerActions = new TaskTest.QueuedNoOpTaskManagerActions();
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableBlockingInInvoke.class).setTaskManagerActions(taskManagerActions).build();
        // run the task asynchronous
        task.startTaskThread();
        // wait till the task is in invoke
        TaskTest.awaitLatch.await();
        task.failExternally(new Exception("test"));
        task.getExecutingThread().join();
        Assert.assertEquals(FAILED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        Assert.assertTrue(task.getFailureCause().getMessage().contains("test"));
        taskManagerActions.validateListenerMessage(RUNNING, task, null);
        taskManagerActions.validateListenerMessage(FAILED, task, new Exception("test"));
    }

    @Test
    public void testCanceledAfterExecutionFailedInInvoke() throws Exception {
        final TaskTest.QueuedNoOpTaskManagerActions taskManagerActions = new TaskTest.QueuedNoOpTaskManagerActions();
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableWithExceptionInInvoke.class).setTaskManagerActions(taskManagerActions).build();
        task.run();
        // this should not overwrite the failure state
        task.cancelExecution();
        Assert.assertEquals(FAILED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        Assert.assertTrue(task.getFailureCause().getMessage().contains("test"));
        taskManagerActions.validateListenerMessage(RUNNING, task, null);
        taskManagerActions.validateListenerMessage(FAILED, task, new Exception("test"));
    }

    @Test
    public void testExecutionFailsAfterCanceling() throws Exception {
        final TaskTest.QueuedNoOpTaskManagerActions taskManagerActions = new TaskTest.QueuedNoOpTaskManagerActions();
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableWithExceptionOnTrigger.class).setTaskManagerActions(taskManagerActions).build();
        // run the task asynchronous
        task.startTaskThread();
        // wait till the task is in invoke
        TaskTest.awaitLatch.await();
        task.cancelExecution();
        Assert.assertEquals(CANCELING, task.getExecutionState());
        // this causes an exception
        TaskTest.triggerLatch.trigger();
        task.getExecutingThread().join();
        // we should still be in state canceled
        Assert.assertEquals(CANCELED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        Assert.assertNull(task.getFailureCause());
        taskManagerActions.validateListenerMessage(RUNNING, task, null);
        taskManagerActions.validateListenerMessage(CANCELED, task, null);
    }

    @Test
    public void testExecutionFailsAfterTaskMarkedFailed() throws Exception {
        final TaskTest.QueuedNoOpTaskManagerActions taskManagerActions = new TaskTest.QueuedNoOpTaskManagerActions();
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableWithExceptionOnTrigger.class).setTaskManagerActions(taskManagerActions).build();
        // run the task asynchronous
        task.startTaskThread();
        // wait till the task is in invoke
        TaskTest.awaitLatch.await();
        task.failExternally(new Exception("external"));
        Assert.assertEquals(FAILED, task.getExecutionState());
        // this causes an exception
        TaskTest.triggerLatch.trigger();
        task.getExecutingThread().join();
        Assert.assertEquals(FAILED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        Assert.assertTrue(task.getFailureCause().getMessage().contains("external"));
        taskManagerActions.validateListenerMessage(RUNNING, task, null);
        taskManagerActions.validateListenerMessage(FAILED, task, new Exception("external"));
    }

    @Test
    public void testCancelTaskException() throws Exception {
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableWithCancelTaskExceptionInInvoke.class).build();
        // Cause CancelTaskException.
        TaskTest.triggerLatch.trigger();
        task.run();
        Assert.assertEquals(CANCELED, task.getExecutionState());
    }

    @Test
    public void testCancelTaskExceptionAfterTaskMarkedFailed() throws Exception {
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableWithCancelTaskExceptionInInvoke.class).build();
        task.startTaskThread();
        // Wait till the task is in invoke.
        TaskTest.awaitLatch.await();
        task.failExternally(new Exception("external"));
        Assert.assertEquals(FAILED, task.getExecutionState());
        // Either we cause the CancelTaskException or the TaskCanceler
        // by interrupting the invokable.
        TaskTest.triggerLatch.trigger();
        task.getExecutingThread().join();
        Assert.assertEquals(FAILED, task.getExecutionState());
        Assert.assertTrue(task.isCanceledOrFailed());
        Assert.assertTrue(task.getFailureCause().getMessage().contains("external"));
    }

    @Test
    public void testOnPartitionStateUpdate() throws Exception {
        final IntermediateDataSetID resultId = new IntermediateDataSetID();
        final ResultPartitionID partitionId = new ResultPartitionID();
        final SingleInputGate inputGate = Mockito.mock(SingleInputGate.class);
        Mockito.when(inputGate.getConsumedResultId()).thenReturn(resultId);
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableBlockingInInvoke.class).build();
        // Set the mock input gate
        setInputGate(task, inputGate);
        // Expected task state for each producer state
        final Map<ExecutionState, ExecutionState> expected = new java.util.HashMap(ExecutionState.values().length);
        // Fail the task for unexpected states
        for (ExecutionState state : ExecutionState.values()) {
            expected.put(state, FAILED);
        }
        expected.put(RUNNING, RUNNING);
        expected.put(SCHEDULED, RUNNING);
        expected.put(DEPLOYING, RUNNING);
        expected.put(FINISHED, RUNNING);
        expected.put(CANCELED, CANCELING);
        expected.put(CANCELING, CANCELING);
        expected.put(FAILED, CANCELING);
        for (ExecutionState state : ExecutionState.values()) {
            setState(task, RUNNING);
            task.onPartitionStateUpdate(resultId, partitionId, state);
            ExecutionState newTaskState = task.getExecutionState();
            Assert.assertEquals(expected.get(state), newTaskState);
        }
        Mockito.verify(inputGate, Mockito.times(4)).retriggerPartitionRequest(ArgumentMatchers.eq(partitionId.getPartitionId()));
    }

    /**
     * Tests the trigger partition state update future completions.
     */
    @Test
    public void testTriggerPartitionStateUpdate() throws Exception {
        final IntermediateDataSetID resultId = new IntermediateDataSetID();
        final ResultPartitionID partitionId = new ResultPartitionID();
        final PartitionProducerStateChecker partitionChecker = Mockito.mock(PartitionProducerStateChecker.class);
        final TaskEventDispatcher taskEventDispatcher = Mockito.mock(TaskEventDispatcher.class);
        final ResultPartitionConsumableNotifier consumableNotifier = new NoOpResultPartitionConsumableNotifier();
        final NetworkEnvironment network = Mockito.mock(NetworkEnvironment.class);
        Mockito.when(network.getResultPartitionManager()).thenReturn(Mockito.mock(ResultPartitionManager.class));
        Mockito.when(network.getDefaultIOMode()).thenReturn(SYNC);
        Mockito.when(network.createKvStateTaskRegistry(ArgumentMatchers.any(JobID.class), ArgumentMatchers.any(JobVertexID.class))).thenReturn(Mockito.mock(TaskKvStateRegistry.class));
        Mockito.when(network.getTaskEventDispatcher()).thenReturn(taskEventDispatcher);
        // Test all branches of trigger partition state check
        {
            // Reset latches
            setup();
            // PartitionProducerDisposedException
            final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableBlockingInInvoke.class).setNetworkEnvironment(network).setConsumableNotifier(consumableNotifier).setPartitionProducerStateChecker(partitionChecker).setExecutor(Executors.directExecutor()).build();
            final CompletableFuture<ExecutionState> promise = new CompletableFuture<>();
            Mockito.when(partitionChecker.requestPartitionProducerState(ArgumentMatchers.eq(task.getJobID()), ArgumentMatchers.eq(resultId), ArgumentMatchers.eq(partitionId))).thenReturn(promise);
            task.triggerPartitionProducerStateCheck(task.getJobID(), resultId, partitionId);
            promise.completeExceptionally(new org.apache.flink.runtime.jobmanager.PartitionProducerDisposedException(partitionId));
            Assert.assertEquals(CANCELING, task.getExecutionState());
        }
        {
            // Reset latches
            setup();
            // Any other exception
            final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableBlockingInInvoke.class).setNetworkEnvironment(network).setConsumableNotifier(consumableNotifier).setPartitionProducerStateChecker(partitionChecker).setExecutor(Executors.directExecutor()).build();
            final CompletableFuture<ExecutionState> promise = new CompletableFuture<>();
            Mockito.when(partitionChecker.requestPartitionProducerState(ArgumentMatchers.eq(task.getJobID()), ArgumentMatchers.eq(resultId), ArgumentMatchers.eq(partitionId))).thenReturn(promise);
            task.triggerPartitionProducerStateCheck(task.getJobID(), resultId, partitionId);
            promise.completeExceptionally(new RuntimeException("Any other exception"));
            Assert.assertEquals(FAILED, task.getExecutionState());
        }
        {
            // Reset latches
            setup();
            // TimeoutException handled special => retry
            // Any other exception
            final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableBlockingInInvoke.class).setNetworkEnvironment(network).setConsumableNotifier(consumableNotifier).setPartitionProducerStateChecker(partitionChecker).setExecutor(Executors.directExecutor()).build();
            final SingleInputGate inputGate = Mockito.mock(SingleInputGate.class);
            Mockito.when(inputGate.getConsumedResultId()).thenReturn(resultId);
            try {
                task.startTaskThread();
                TaskTest.awaitLatch.await();
                setInputGate(task, inputGate);
                CompletableFuture<ExecutionState> promise = new CompletableFuture<>();
                Mockito.when(partitionChecker.requestPartitionProducerState(ArgumentMatchers.eq(task.getJobID()), ArgumentMatchers.eq(resultId), ArgumentMatchers.eq(partitionId))).thenReturn(promise);
                task.triggerPartitionProducerStateCheck(task.getJobID(), resultId, partitionId);
                promise.completeExceptionally(new TimeoutException());
                Assert.assertEquals(RUNNING, task.getExecutionState());
                Mockito.verify(inputGate, Mockito.times(1)).retriggerPartitionRequest(ArgumentMatchers.eq(partitionId.getPartitionId()));
            } finally {
                task.getExecutingThread().interrupt();
                task.getExecutingThread().join();
            }
        }
        {
            // Reset latches
            setup();
            // Success
            final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableBlockingInInvoke.class).setNetworkEnvironment(network).setConsumableNotifier(consumableNotifier).setPartitionProducerStateChecker(partitionChecker).setExecutor(Executors.directExecutor()).build();
            final SingleInputGate inputGate = Mockito.mock(SingleInputGate.class);
            Mockito.when(inputGate.getConsumedResultId()).thenReturn(resultId);
            try {
                task.startTaskThread();
                TaskTest.awaitLatch.await();
                setInputGate(task, inputGate);
                CompletableFuture<ExecutionState> promise = new CompletableFuture<>();
                Mockito.when(partitionChecker.requestPartitionProducerState(ArgumentMatchers.eq(task.getJobID()), ArgumentMatchers.eq(resultId), ArgumentMatchers.eq(partitionId))).thenReturn(promise);
                task.triggerPartitionProducerStateCheck(task.getJobID(), resultId, partitionId);
                promise.complete(RUNNING);
                Assert.assertEquals(RUNNING, task.getExecutionState());
                Mockito.verify(inputGate, Mockito.times(1)).retriggerPartitionRequest(ArgumentMatchers.eq(partitionId.getPartitionId()));
            } finally {
                task.getExecutingThread().interrupt();
                task.getExecutingThread().join();
            }
        }
    }

    /**
     * Tests that interrupt happens via watch dog if canceller is stuck in cancel.
     * Task cancellation blocks the task canceller. Interrupt after cancel via
     * cancellation watch dog.
     */
    @Test
    public void testWatchDogInterruptsTask() throws Exception {
        final TaskManagerActions taskManagerActions = new TaskTest.ProhibitFatalErrorTaskManagerActions();
        final Configuration config = new Configuration();
        config.setLong(TASK_CANCELLATION_INTERVAL.key(), 5);
        config.setLong(TASK_CANCELLATION_TIMEOUT.key(), (60 * 1000));
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableBlockingInCancel.class).setTaskManagerConfig(config).setTaskManagerActions(taskManagerActions).build();
        task.startTaskThread();
        TaskTest.awaitLatch.await();
        task.cancelExecution();
        task.getExecutingThread().join();
    }

    /**
     * The invoke() method holds a lock (trigger awaitLatch after acquisition)
     * and cancel cannot complete because it also tries to acquire the same lock.
     * This is resolved by the watch dog, no fatal error.
     */
    @Test
    public void testInterruptibleSharedLockInInvokeAndCancel() throws Exception {
        final TaskManagerActions taskManagerActions = new TaskTest.ProhibitFatalErrorTaskManagerActions();
        final Configuration config = new Configuration();
        config.setLong(TASK_CANCELLATION_INTERVAL, 5);
        config.setLong(TASK_CANCELLATION_TIMEOUT, 50);
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableInterruptibleSharedLockInInvokeAndCancel.class).setTaskManagerConfig(config).setTaskManagerActions(taskManagerActions).build();
        task.startTaskThread();
        TaskTest.awaitLatch.await();
        task.cancelExecution();
        task.getExecutingThread().join();
    }

    /**
     * The invoke() method blocks infinitely, but cancel() does not block. Only
     * resolved by a fatal error.
     */
    @Test
    public void testFatalErrorAfterUnInterruptibleInvoke() throws Exception {
        final TaskTest.AwaitFatalErrorTaskManagerActions taskManagerActions = new TaskTest.AwaitFatalErrorTaskManagerActions();
        final Configuration config = new Configuration();
        config.setLong(TASK_CANCELLATION_INTERVAL, 5);
        config.setLong(TASK_CANCELLATION_TIMEOUT, 50);
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableUnInterruptibleBlockingInvoke.class).setTaskManagerConfig(config).setTaskManagerActions(taskManagerActions).build();
        try {
            task.startTaskThread();
            TaskTest.awaitLatch.await();
            task.cancelExecution();
            // wait for the notification of notifyFatalError
            taskManagerActions.latch.await();
        } finally {
            // Interrupt again to clean up Thread
            TaskTest.triggerLatch.trigger();
            task.getExecutingThread().interrupt();
            task.getExecutingThread().join();
        }
    }

    /**
     * Tests that the task configuration is respected and overwritten by the execution config.
     */
    @Test
    public void testTaskConfig() throws Exception {
        long interval = 28218123;
        long timeout = interval + 19292;
        final Configuration config = new Configuration();
        config.setLong(TASK_CANCELLATION_INTERVAL, interval);
        config.setLong(TASK_CANCELLATION_TIMEOUT, timeout);
        final ExecutionConfig executionConfig = new ExecutionConfig();
        executionConfig.setTaskCancellationInterval((interval + 1337));
        executionConfig.setTaskCancellationTimeout((timeout - 1337));
        final Task task = new TaskTest.TaskBuilder().setInvokable(TaskTest.InvokableBlockingInInvoke.class).setTaskManagerConfig(config).setExecutionConfig(executionConfig).build();
        Assert.assertEquals(interval, task.getTaskCancellationInterval());
        Assert.assertEquals(timeout, task.getTaskCancellationTimeout());
        task.startTaskThread();
        TaskTest.awaitLatch.await();
        Assert.assertEquals(executionConfig.getTaskCancellationInterval(), task.getTaskCancellationInterval());
        Assert.assertEquals(executionConfig.getTaskCancellationTimeout(), task.getTaskCancellationTimeout());
        task.getExecutingThread().interrupt();
        task.getExecutingThread().join();
    }

    // ------------------------------------------------------------------------
    // customized TaskManagerActions
    // ------------------------------------------------------------------------
    /**
     * Customized TaskManagerActions that queues all calls of updateTaskExecutionState.
     */
    private static class QueuedNoOpTaskManagerActions extends NoOpTaskManagerActions {
        private final BlockingQueue<TaskExecutionState> queue = new LinkedBlockingDeque<>();

        @Override
        public void updateTaskExecutionState(TaskExecutionState taskExecutionState) {
            queue.offer(taskExecutionState);
        }

        private void validateListenerMessage(ExecutionState state, Task task, Throwable error) {
            try {
                // we may have to wait for a bit to give the actors time to receive the message
                // and put it into the queue
                final TaskExecutionState taskState = queue.take();
                Assert.assertNotNull("There is no additional listener message", state);
                Assert.assertEquals(task.getJobID(), taskState.getJobID());
                Assert.assertEquals(task.getExecutionId(), taskState.getID());
                Assert.assertEquals(state, taskState.getExecutionState());
                final Throwable t = taskState.getError(getClass().getClassLoader());
                if (error == null) {
                    Assert.assertNull(t);
                } else {
                    Assert.assertEquals(error.toString(), t.toString());
                }
            } catch (InterruptedException e) {
                Assert.fail("interrupted");
            }
        }
    }

    /**
     * Customized TaskManagerActions that ensures no call of notifyFatalError.
     */
    private static class ProhibitFatalErrorTaskManagerActions extends NoOpTaskManagerActions {
        @Override
        public void notifyFatalError(String message, Throwable cause) {
            throw new RuntimeException("Unexpected FatalError notification");
        }
    }

    /**
     * Customized TaskManagerActions that waits for a call of notifyFatalError.
     */
    private static class AwaitFatalErrorTaskManagerActions extends NoOpTaskManagerActions {
        private final OneShotLatch latch = new OneShotLatch();

        @Override
        public void notifyFatalError(String message, Throwable cause) {
            latch.trigger();
        }
    }

    private final class TaskBuilder {
        private Class<? extends AbstractInvokable> invokable;

        private TaskManagerActions taskManagerActions;

        private LibraryCacheManager libraryCacheManager;

        private ResultPartitionConsumableNotifier consumableNotifier;

        private PartitionProducerStateChecker partitionProducerStateChecker;

        private NetworkEnvironment networkEnvironment;

        private Executor executor;

        private Configuration taskManagerConfig;

        private ExecutionConfig executionConfig;

        private Collection<PermanentBlobKey> requiredJarFileBlobKeys;

        {
            invokable = TaskTest.TestInvokableCorrect.class;
            taskManagerActions = Mockito.mock(TaskManagerActions.class);
            libraryCacheManager = Mockito.mock(LibraryCacheManager.class);
            Mockito.when(libraryCacheManager.getClassLoader(ArgumentMatchers.any(JobID.class))).thenReturn(getClass().getClassLoader());
            consumableNotifier = new NoOpResultPartitionConsumableNotifier();
            partitionProducerStateChecker = Mockito.mock(PartitionProducerStateChecker.class);
            final ResultPartitionManager partitionManager = Mockito.mock(ResultPartitionManager.class);
            final TaskEventDispatcher taskEventDispatcher = Mockito.mock(TaskEventDispatcher.class);
            networkEnvironment = Mockito.mock(NetworkEnvironment.class);
            Mockito.when(networkEnvironment.getResultPartitionManager()).thenReturn(partitionManager);
            Mockito.when(networkEnvironment.getDefaultIOMode()).thenReturn(SYNC);
            Mockito.when(networkEnvironment.createKvStateTaskRegistry(ArgumentMatchers.any(JobID.class), ArgumentMatchers.any(JobVertexID.class))).thenReturn(Mockito.mock(TaskKvStateRegistry.class));
            Mockito.when(networkEnvironment.getTaskEventDispatcher()).thenReturn(taskEventDispatcher);
            executor = TestingUtils.defaultExecutor();
            taskManagerConfig = new Configuration();
            executionConfig = new ExecutionConfig();
            requiredJarFileBlobKeys = Collections.emptyList();
        }

        TaskTest.TaskBuilder setInvokable(Class<? extends AbstractInvokable> invokable) {
            this.invokable = invokable;
            return this;
        }

        TaskTest.TaskBuilder setTaskManagerActions(TaskManagerActions taskManagerActions) {
            this.taskManagerActions = taskManagerActions;
            return this;
        }

        TaskTest.TaskBuilder setLibraryCacheManager(LibraryCacheManager libraryCacheManager) {
            this.libraryCacheManager = libraryCacheManager;
            return this;
        }

        TaskTest.TaskBuilder setConsumableNotifier(ResultPartitionConsumableNotifier consumableNotifier) {
            this.consumableNotifier = consumableNotifier;
            return this;
        }

        TaskTest.TaskBuilder setPartitionProducerStateChecker(PartitionProducerStateChecker partitionProducerStateChecker) {
            this.partitionProducerStateChecker = partitionProducerStateChecker;
            return this;
        }

        TaskTest.TaskBuilder setNetworkEnvironment(NetworkEnvironment networkEnvironment) {
            this.networkEnvironment = networkEnvironment;
            return this;
        }

        TaskTest.TaskBuilder setExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        TaskTest.TaskBuilder setTaskManagerConfig(Configuration taskManagerConfig) {
            this.taskManagerConfig = taskManagerConfig;
            return this;
        }

        TaskTest.TaskBuilder setExecutionConfig(ExecutionConfig executionConfig) {
            this.executionConfig = executionConfig;
            return this;
        }

        TaskTest.TaskBuilder setRequiredJarFileBlobKeys(Collection<PermanentBlobKey> requiredJarFileBlobKeys) {
            this.requiredJarFileBlobKeys = requiredJarFileBlobKeys;
            return this;
        }

        private Task build() throws Exception {
            final JobID jobId = new JobID();
            final JobVertexID jobVertexId = new JobVertexID();
            final ExecutionAttemptID executionAttemptId = new ExecutionAttemptID();
            final SerializedValue<ExecutionConfig> serializedExecutionConfig = new SerializedValue(executionConfig);
            final JobInformation jobInformation = new JobInformation(jobId, "Test Job", serializedExecutionConfig, new Configuration(), requiredJarFileBlobKeys, Collections.emptyList());
            final TaskInformation taskInformation = new TaskInformation(jobVertexId, "Test Task", 1, 1, invokable.getName(), new Configuration());
            final BlobCacheService blobCacheService = new BlobCacheService(Mockito.mock(PermanentBlobCache.class), Mockito.mock(TransientBlobCache.class));
            final TaskMetricGroup taskMetricGroup = Mockito.mock(TaskMetricGroup.class);
            Mockito.when(taskMetricGroup.getIOMetricGroup()).thenReturn(Mockito.mock(TaskIOMetricGroup.class));
            return new Task(jobInformation, taskInformation, executionAttemptId, new AllocationID(), 0, 0, Collections.emptyList(), Collections.emptyList(), 0, Mockito.mock(MemoryManager.class), Mockito.mock(IOManager.class), networkEnvironment, Mockito.mock(BroadcastVariableManager.class), new TestTaskStateManager(), taskManagerActions, new MockInputSplitProvider(), new TestCheckpointResponder(), new TestGlobalAggregateManager(), blobCacheService, libraryCacheManager, Mockito.mock(FileCache.class), new TestingTaskManagerRuntimeInfo(taskManagerConfig), taskMetricGroup, consumableNotifier, partitionProducerStateChecker, executor);
        }
    }

    // ------------------------------------------------------------------------
    // test task classes
    // ------------------------------------------------------------------------
    private static final class TestInvokableCorrect extends AbstractInvokable {
        public TestInvokableCorrect(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() {
        }

        @Override
        public void cancel() {
            Assert.fail("This should not be called");
        }
    }

    private abstract static class InvokableNonInstantiable extends AbstractInvokable {
        public InvokableNonInstantiable(Environment environment) {
            super(environment);
        }
    }

    private static final class InvokableWithExceptionInInvoke extends AbstractInvokable {
        public InvokableWithExceptionInInvoke(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            throw new Exception("test");
        }
    }

    private static final class FailingInvokableWithChainedException extends AbstractInvokable {
        public FailingInvokableWithChainedException(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() {
            throw new TaskTest.TestWrappedException(new IOException("test"));
        }

        @Override
        public void cancel() {
        }
    }

    private static final class InvokableBlockingInInvoke extends AbstractInvokable {
        public InvokableBlockingInInvoke(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            TaskTest.awaitLatch.trigger();
            // block forever
            synchronized(this) {
                wait();
            }
        }
    }

    /**
     * {@link AbstractInvokable} which throws {@link RuntimeException} on invoke.
     */
    public static final class InvokableWithExceptionOnTrigger extends AbstractInvokable {
        public InvokableWithExceptionOnTrigger(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() {
            TaskTest.awaitLatch.trigger();
            // make sure that the interrupt call does not
            // grab us out of the lock early
            while (true) {
                try {
                    TaskTest.triggerLatch.await();
                    break;
                } catch (InterruptedException e) {
                    // fall through the loop
                }
            } 
            throw new RuntimeException("test");
        }
    }

    /**
     * {@link AbstractInvokable} which throws {@link CancelTaskException} on invoke.
     */
    public static final class InvokableWithCancelTaskExceptionInInvoke extends AbstractInvokable {
        public InvokableWithCancelTaskExceptionInInvoke(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() {
            TaskTest.awaitLatch.trigger();
            try {
                TaskTest.triggerLatch.await();
            } catch (Throwable ignored) {
            }
            throw new CancelTaskException();
        }
    }

    /**
     * {@link AbstractInvokable} which blocks in cancel.
     */
    public static final class InvokableBlockingInCancel extends AbstractInvokable {
        public InvokableBlockingInCancel(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() {
            TaskTest.awaitLatch.trigger();
            try {
                TaskTest.triggerLatch.await();// await cancel

                synchronized(this) {
                    wait();
                }
            } catch (InterruptedException ignored) {
                synchronized(this) {
                    notifyAll();// notify all that are stuck in cancel

                }
            }
        }

        @Override
        public void cancel() throws Exception {
            synchronized(this) {
                TaskTest.triggerLatch.trigger();
                wait();
            }
        }
    }

    /**
     * {@link AbstractInvokable} which blocks in cancel and is interruptible.
     */
    public static final class InvokableInterruptibleSharedLockInInvokeAndCancel extends AbstractInvokable {
        private final Object lock = new Object();

        public InvokableInterruptibleSharedLockInInvokeAndCancel(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            synchronized(lock) {
                TaskTest.awaitLatch.trigger();
                wait();
            }
        }

        @Override
        public void cancel() {
            synchronized(lock) {
                // do nothing but a placeholder
                TaskTest.triggerLatch.trigger();
            }
        }
    }

    /**
     * {@link AbstractInvokable} which blocks in cancel and is not interruptible.
     */
    public static final class InvokableUnInterruptibleBlockingInvoke extends AbstractInvokable {
        public InvokableUnInterruptibleBlockingInvoke(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() {
            while (!(TaskTest.triggerLatch.isTriggered())) {
                try {
                    synchronized(this) {
                        TaskTest.awaitLatch.trigger();
                        wait();
                    }
                } catch (InterruptedException ignored) {
                }
            } 
        }

        @Override
        public void cancel() {
        }
    }

    // ------------------------------------------------------------------------
    // test exceptions
    // ------------------------------------------------------------------------
    private static class TestWrappedException extends WrappingRuntimeException {
        private static final long serialVersionUID = 1L;

        TestWrappedException(@Nonnull
        Throwable cause) {
            super(cause);
        }
    }
}

