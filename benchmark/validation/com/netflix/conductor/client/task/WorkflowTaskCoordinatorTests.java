/**
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.conductor.client.task;


import Task.Status.IN_PROGRESS;
import TaskResult.Status.FAILED;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Uninterruptibles;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Viren
 */
public class WorkflowTaskCoordinatorTests {
    @Test(expected = IllegalArgumentException.class)
    public void testNoWorkersException() {
        new WorkflowTaskCoordinator.Builder().build();
    }

    @Test
    public void testThreadPool() {
        Worker worker = Worker.create("test", TaskResult::new);
        WorkflowTaskCoordinator coordinator = new WorkflowTaskCoordinator.Builder().withWorkers(worker, worker, worker).withTaskClient(new TaskClient()).build();
        Assert.assertEquals((-1), coordinator.getThreadCount());// Not initialized yet

        coordinator.init();
        Assert.assertEquals(3, coordinator.getThreadCount());
        Assert.assertEquals(100, coordinator.getWorkerQueueSize());// 100 is the default value

        Assert.assertEquals(500, coordinator.getSleepWhenRetry());
        Assert.assertEquals(3, coordinator.getUpdateRetryCount());
        coordinator = new WorkflowTaskCoordinator.Builder().withWorkers(worker).withThreadCount(100).withWorkerQueueSize(400).withSleepWhenRetry(100).withUpdateRetryCount(10).withTaskClient(new TaskClient()).withWorkerNamePrefix("test-worker-").build();
        Assert.assertEquals(100, coordinator.getThreadCount());
        coordinator.init();
        Assert.assertEquals(100, coordinator.getThreadCount());
        Assert.assertEquals(400, coordinator.getWorkerQueueSize());
        Assert.assertEquals(100, coordinator.getSleepWhenRetry());
        Assert.assertEquals(10, coordinator.getUpdateRetryCount());
        Assert.assertEquals("test-worker-", coordinator.getWorkerNamePrefix());
    }

    @Test
    public void testTaskException() {
        Worker worker = Worker.create("test", ( task) -> {
            throw new NoSuchMethodError();
        });
        TaskClient client = Mockito.mock(TaskClient.class);
        WorkflowTaskCoordinator coordinator = new WorkflowTaskCoordinator.Builder().withWorkers(worker).withThreadCount(1).withWorkerQueueSize(1).withSleepWhenRetry(100000).withUpdateRetryCount(1).withTaskClient(client).withWorkerNamePrefix("test-worker-").build();
        Mockito.when(client.batchPollTasksInDomain(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(ImmutableList.of(new Task()));
        Mockito.when(client.ack(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(true);
        CountDownLatch latch = new CountDownLatch(1);
        Mockito.doAnswer(( invocation) -> {
            Assert.assertEquals("test-worker-0", Thread.currentThread().getName());
            Object[] args = invocation.getArguments();
            TaskResult result = ((TaskResult) (args[0]));
            Assert.assertEquals(FAILED, result.getStatus());
            latch.countDown();
            return null;
        }).when(client).updateTask(ArgumentMatchers.any(), ArgumentMatchers.anyString());
        coordinator.init();
        Uninterruptibles.awaitUninterruptibly(latch);
        Mockito.verify(client).updateTask(ArgumentMatchers.any(), ArgumentMatchers.anyString());
    }

    @Test
    public void testNoOpWhenAckFailed() {
        Worker worker = Mockito.mock(Worker.class);
        Mockito.when(worker.getPollingInterval()).thenReturn(1000);
        Mockito.when(worker.getPollCount()).thenReturn(1);
        Mockito.when(worker.getTaskDefName()).thenReturn("test");
        Mockito.when(worker.preAck(ArgumentMatchers.any())).thenReturn(true);
        TaskClient client = Mockito.mock(TaskClient.class);
        WorkflowTaskCoordinator coordinator = new WorkflowTaskCoordinator.Builder().withWorkers(worker).withThreadCount(1).withWorkerQueueSize(1).withSleepWhenRetry(100000).withUpdateRetryCount(1).withTaskClient(client).withWorkerNamePrefix("test-worker-").build();
        Task testTask = new Task();
        testTask.setStatus(IN_PROGRESS);
        Mockito.when(client.batchPollTasksInDomain(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(ImmutableList.of(testTask));
        Mockito.when(client.ack(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(false);
        coordinator.init();
        // then worker.execute must not be called and task must be updated with IN_PROGRESS status
        Mockito.verify(worker, Mockito.never()).execute(ArgumentMatchers.any());
        Mockito.verify(client, Mockito.never()).updateTask(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testNoOpWhenAckThrowsException() {
        Worker worker = Mockito.mock(Worker.class);
        Mockito.when(worker.getPollingInterval()).thenReturn(1000);
        Mockito.when(worker.getPollCount()).thenReturn(1);
        Mockito.when(worker.getTaskDefName()).thenReturn("test");
        Mockito.when(worker.preAck(ArgumentMatchers.any())).thenReturn(true);
        TaskClient client = Mockito.mock(TaskClient.class);
        WorkflowTaskCoordinator coordinator = new WorkflowTaskCoordinator.Builder().withWorkers(worker).withThreadCount(1).withWorkerQueueSize(1).withSleepWhenRetry(100000).withUpdateRetryCount(1).withTaskClient(client).withWorkerNamePrefix("test-worker-").build();
        Task testTask = new Task();
        testTask.setStatus(IN_PROGRESS);
        Mockito.when(client.batchPollTasksInDomain(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(ImmutableList.of(testTask));
        Mockito.when(client.ack(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenThrow(new RuntimeException("Ack failed"));
        coordinator.init();
        // then worker.execute must not be called and task must be updated with IN_PROGRESS status
        Mockito.verify(worker, Mockito.never()).execute(ArgumentMatchers.any());
        Mockito.verify(client, Mockito.never()).updateTask(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testReturnTaskWhenRejectedExecutionExceptionThrown() {
        Task testTask = new Task();
        testTask.setStatus(IN_PROGRESS);
        Worker worker = Mockito.mock(Worker.class);
        Mockito.when(worker.getPollingInterval()).thenReturn(3000);
        Mockito.when(worker.getPollCount()).thenReturn(1);
        Mockito.when(worker.getTaskDefName()).thenReturn("test");
        Mockito.when(worker.preAck(ArgumentMatchers.any())).thenReturn(true);
        Mockito.when(worker.execute(ArgumentMatchers.any())).thenAnswer(( invocation) -> {
            // Sleep for 2 seconds to trigger RejectedExecutionException
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            return new TaskResult(testTask);
        });
        TaskClient client = Mockito.mock(TaskClient.class);
        WorkflowTaskCoordinator coordinator = new WorkflowTaskCoordinator.Builder().withWorkers(worker).withThreadCount(1).withWorkerQueueSize(1).withSleepWhenRetry(100000).withUpdateRetryCount(1).withTaskClient(client).withWorkerNamePrefix("test-worker-").build();
        Mockito.when(client.batchPollTasksInDomain(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(ImmutableList.of(testTask, testTask, testTask));
        Mockito.when(client.ack(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(true);
        CountDownLatch latch = new CountDownLatch(3);
        Mockito.doAnswer(( invocation) -> {
            Object[] args = invocation.getArguments();
            TaskResult result = ((TaskResult) (args[0]));
            Assert.assertEquals(TaskResult.Status.IN_PROGRESS, result.getStatus());
            latch.countDown();
            return null;
        }).when(client).updateTask(ArgumentMatchers.any(), ArgumentMatchers.anyString());
        coordinator.init();
        Uninterruptibles.awaitUninterruptibly(latch);
        // With worker queue set to 1, first two tasks can be submitted, and third one would get
        // RejectedExceptionExcpetion, so worker.execute() should be called twice.
        Mockito.verify(worker, Mockito.times(2)).execute(ArgumentMatchers.any());
        // task must be updated with IN_PROGRESS status three times, two from worker.execute() and
        // one from returnTask caused by RejectedExecutionException.
        Mockito.verify(client, Mockito.times(3)).updateTask(ArgumentMatchers.any(), ArgumentMatchers.anyString());
    }
}

