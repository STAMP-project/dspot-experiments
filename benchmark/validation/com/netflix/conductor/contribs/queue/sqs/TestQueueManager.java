/**
 * Copyright 2016 Netflix, Inc.
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
/**
 *
 */
package com.netflix.conductor.contribs.queue.sqs;


import Status.CANCELED;
import Status.COMPLETED;
import com.google.common.util.concurrent.Uninterruptibles;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.Task.Status;
import com.netflix.conductor.contribs.queue.QueueManager;
import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.core.events.queue.ObservableQueue;
import com.netflix.conductor.service.ExecutionService;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Viren
 */
public class TestQueueManager {
    private static SQSObservableQueue queue;

    private static ExecutionService es;

    private static final List<Message> messages = new LinkedList<>();

    private static final List<Task> updatedTasks = new LinkedList<>();

    @Test
    public void test() throws Exception {
        Map<Status, ObservableQueue> queues = new HashMap<>();
        queues.put(COMPLETED, TestQueueManager.queue);
        QueueManager qm = new QueueManager(queues, TestQueueManager.es);
        qm.updateByTaskRefName("v_0", "t0", new HashMap(), COMPLETED);
        Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(TestQueueManager.updatedTasks.stream().anyMatch(( task) -> task.getTaskId().equals("t0")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailure() throws Exception {
        Map<Status, ObservableQueue> queues = new HashMap<>();
        queues.put(COMPLETED, TestQueueManager.queue);
        QueueManager qm = new QueueManager(queues, TestQueueManager.es);
        qm.updateByTaskRefName("v_1", "t1", new HashMap(), CANCELED);
        Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testWithTaskId() throws Exception {
        Map<Status, ObservableQueue> queues = new HashMap<>();
        queues.put(COMPLETED, TestQueueManager.queue);
        QueueManager qm = new QueueManager(queues, TestQueueManager.es);
        qm.updateByTaskId("v_2", "t2", new HashMap(), COMPLETED);
        Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(TestQueueManager.updatedTasks.stream().anyMatch(( task) -> task.getTaskId().equals("t2")));
    }
}

