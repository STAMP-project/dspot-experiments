/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.spi.impl.operationexecutor.impl;


import OperationQueueImpl.TRIGGER_TASK;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ArrayBlockingQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class OperationQueueImplTest extends HazelcastTestSupport {
    private OperationQueueImpl operationQueue;

    private ArrayBlockingQueue<Object> normalQueue;

    private ArrayBlockingQueue<Object> priorityQueue;

    // ================== add =====================
    @Test(expected = NullPointerException.class)
    public void add_whenNull() {
        operationQueue.add(null, false);
    }

    @Test
    public void add_whenPriority() throws InterruptedException {
        Object task = new Object();
        operationQueue.add(task, true);
        Assert.assertEquals(1, operationQueue.prioritySize());
        Assert.assertEquals(1, operationQueue.normalSize());
        Assert.assertEquals(2, operationQueue.size());
        Assert.assertEquals(1, priorityQueue.size());
        Assert.assertEquals(1, normalQueue.size());
        Assert.assertSame(task, priorityQueue.iterator().next());
        Assert.assertSame(TRIGGER_TASK, normalQueue.iterator().next());
    }

    @Test
    public void add_whenNormal() {
        Object task = new Object();
        operationQueue.add(task, false);
        assertContent(normalQueue, task);
        assertEmpty(priorityQueue);
        Assert.assertEquals(0, operationQueue.prioritySize());
        Assert.assertEquals(1, operationQueue.normalSize());
        Assert.assertEquals(1, operationQueue.size());
    }

    // ================== take =====================
    @Test
    public void take_whenPriorityItemAvailable() throws InterruptedException {
        Object task1 = "task1";
        Object task2 = "task2";
        Object task3 = "task3";
        operationQueue.add(task1, true);
        operationQueue.add(task2, true);
        operationQueue.add(task3, true);
        Assert.assertSame(task1, operationQueue.take(false));
        Assert.assertSame(task2, operationQueue.take(false));
        Assert.assertSame(task3, operationQueue.take(false));
        Assert.assertEquals(3, operationQueue.size());
        Assert.assertEquals(0, operationQueue.prioritySize());
        Assert.assertEquals(3, operationQueue.normalSize());
    }

    /**
     * It could be that in the low priority query there are a bunch of useless trigger tasks preceding a regular tasks.
     */
    @Test
    public void take_whenLowPriority_andManyPrecedingTriggerTasks() throws InterruptedException {
        Object task1 = "task1";
        Object task2 = "task2";
        Object task3 = "task3";
        Object task4 = "task4";
        operationQueue.add(task1, true);
        operationQueue.add(task2, true);
        operationQueue.add(task3, true);
        operationQueue.add(task4, false);
        Assert.assertSame(task1, operationQueue.take(false));
        Assert.assertSame(task2, operationQueue.take(false));
        Assert.assertSame(task3, operationQueue.take(false));
        // at this moment there are 3 trigger tasks and 1 normal task
        Assert.assertEquals(4, operationQueue.size());
        // when we take the item
        Assert.assertSame(task4, operationQueue.take(false));
        // all the trigger tasks are drained
        Assert.assertEquals(0, operationQueue.size());
        Assert.assertEquals(0, operationQueue.prioritySize());
        Assert.assertEquals(0, operationQueue.normalSize());
    }

    @Test
    public void take_whenRegularItemAvailable() throws InterruptedException {
        Object task1 = "task1";
        Object task2 = "task2";
        Object task3 = "task3";
        operationQueue.add(task1, false);
        operationQueue.add(task2, false);
        operationQueue.add(task3, false);
        Assert.assertSame(task1, operationQueue.take(false));
        Assert.assertSame(task2, operationQueue.take(false));
        Assert.assertSame(task3, operationQueue.take(false));
        Assert.assertEquals(0, operationQueue.size());
        Assert.assertEquals(0, operationQueue.prioritySize());
        Assert.assertEquals(0, operationQueue.normalSize());
    }

    @Test
    public void take_whenPriorityAndRegularItemAvailable() throws InterruptedException {
        Object task1 = "task1";
        Object task2 = "task2";
        Object task3 = "task3";
        operationQueue.add(task1, true);
        operationQueue.add(task2, false);
        operationQueue.add(task3, true);
        Assert.assertSame(task1, operationQueue.take(true));
        Assert.assertSame(task3, operationQueue.take(true));
        Assert.assertEquals(0, operationQueue.prioritySize());
    }

    @Test
    public void take_whenPriority_andNoItemAvailable_thenBlockTillItemAvailable() throws InterruptedException {
        final Object task1 = "task1";
        final Object task2 = "task2";
        operationQueue.add(task1, false);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                HazelcastTestSupport.sleepSeconds(4);
                operationQueue.add(task2, true);
            }
        });
        Assert.assertSame(task2, operationQueue.take(true));
        Assert.assertEquals(0, operationQueue.prioritySize());
    }

    @Test
    public void take_priorityIsRetrievedFirst() throws InterruptedException {
        Object priorityTask1 = "priority1";
        Object priorityTask2 = "priority2";
        Object priorityTask3 = "priority4";
        Object normalTask1 = "normalTask1";
        Object normalTask2 = "normalTask2";
        Object normalTask3 = "normalTask3";
        operationQueue.add(priorityTask1, true);
        operationQueue.add(normalTask1, false);
        operationQueue.add(normalTask2, false);
        operationQueue.add(priorityTask2, true);
        operationQueue.add(normalTask3, false);
        operationQueue.add(priorityTask3, true);
        Assert.assertSame(priorityTask1, operationQueue.take(false));
        Assert.assertSame(priorityTask2, operationQueue.take(false));
        Assert.assertSame(priorityTask3, operationQueue.take(false));
        Assert.assertSame(normalTask1, operationQueue.take(false));
        Assert.assertSame(normalTask2, operationQueue.take(false));
        Assert.assertSame(normalTask3, operationQueue.take(false));
        assertEmpty(priorityQueue);
        // assertContent(normalQueue, OperationQueueImpl.TRIGGER_TASK);
    }
}

