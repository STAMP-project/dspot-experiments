/**
 * Copyright 2015 Netflix, Inc.
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
package com.netflix.eureka.util.batcher;


import ProcessingResult.TransientError;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Bak
 */
public class AcceptorExecutorTest {
    private static final long SERVER_UNAVAILABLE_SLEEP_TIME_MS = 1000;

    private static final long RETRY_SLEEP_TIME_MS = 100;

    private static final long MAX_BATCHING_DELAY_MS = 10;

    private static final int MAX_BUFFER_SIZE = 3;

    private static final int WORK_LOAD_SIZE = 2;

    private AcceptorExecutor<Integer, String> acceptorExecutor;

    @Test
    public void testTasksAreDispatchedToWorkers() throws Exception {
        acceptorExecutor.process(1, "Task1", ((System.currentTimeMillis()) + (60 * 1000)));
        TaskHolder<Integer, String> taskHolder = acceptorExecutor.requestWorkItem().poll(5, TimeUnit.SECONDS);
        AcceptorExecutorTest.verifyTaskHolder(taskHolder, 1, "Task1");
        acceptorExecutor.process(2, "Task2", ((System.currentTimeMillis()) + (60 * 1000)));
        List<TaskHolder<Integer, String>> taskHolders = acceptorExecutor.requestWorkItems().poll(5, TimeUnit.SECONDS);
        Assert.assertThat(taskHolders.size(), CoreMatchers.is(CoreMatchers.equalTo(1)));
        AcceptorExecutorTest.verifyTaskHolder(taskHolders.get(0), 2, "Task2");
    }

    @Test
    public void testBatchSizeIsConstrainedByConfiguredMaxSize() throws Exception {
        for (int i = 0; i <= (AcceptorExecutorTest.MAX_BUFFER_SIZE); i++) {
            acceptorExecutor.process(i, ("Task" + i), ((System.currentTimeMillis()) + (60 * 1000)));
        }
        List<TaskHolder<Integer, String>> taskHolders = acceptorExecutor.requestWorkItems().poll(5, TimeUnit.SECONDS);
        Assert.assertThat(taskHolders.size(), CoreMatchers.is(CoreMatchers.equalTo(AcceptorExecutorTest.WORK_LOAD_SIZE)));
    }

    @Test
    public void testNewTaskOverridesOldOne() throws Exception {
        acceptorExecutor.process(1, "Task1", ((System.currentTimeMillis()) + (60 * 1000)));
        acceptorExecutor.process(1, "Task1.1", ((System.currentTimeMillis()) + (60 * 1000)));
        TaskHolder<Integer, String> taskHolder = acceptorExecutor.requestWorkItem().poll(5, TimeUnit.SECONDS);
        AcceptorExecutorTest.verifyTaskHolder(taskHolder, 1, "Task1.1");
    }

    @Test
    public void testRepublishedTaskIsHandledFirst() throws Exception {
        acceptorExecutor.process(1, "Task1", ((System.currentTimeMillis()) + (60 * 1000)));
        acceptorExecutor.process(2, "Task2", ((System.currentTimeMillis()) + (60 * 1000)));
        TaskHolder<Integer, String> firstTaskHolder = acceptorExecutor.requestWorkItem().poll(5, TimeUnit.SECONDS);
        AcceptorExecutorTest.verifyTaskHolder(firstTaskHolder, 1, "Task1");
        acceptorExecutor.reprocess(firstTaskHolder, TransientError);
        TaskHolder<Integer, String> secondTaskHolder = acceptorExecutor.requestWorkItem().poll(5, TimeUnit.SECONDS);
        AcceptorExecutorTest.verifyTaskHolder(secondTaskHolder, 1, "Task1");
        TaskHolder<Integer, String> thirdTaskHolder = acceptorExecutor.requestWorkItem().poll(5, TimeUnit.SECONDS);
        AcceptorExecutorTest.verifyTaskHolder(thirdTaskHolder, 2, "Task2");
    }

    @Test
    public void testWhenBufferOverflowsOldestTasksAreRemoved() throws Exception {
        for (int i = 0; i <= (AcceptorExecutorTest.MAX_BUFFER_SIZE); i++) {
            acceptorExecutor.process(i, ("Task" + i), ((System.currentTimeMillis()) + (60 * 1000)));
        }
        // Task 0 should be dropped out
        TaskHolder<Integer, String> firstTaskHolder = acceptorExecutor.requestWorkItem().poll(5, TimeUnit.SECONDS);
        AcceptorExecutorTest.verifyTaskHolder(firstTaskHolder, 1, "Task1");
    }

    @Test
    public void testTasksAreDelayToMaximizeBatchSize() throws Exception {
        BlockingQueue<List<TaskHolder<Integer, String>>> taskQueue = acceptorExecutor.requestWorkItems();
        acceptorExecutor.process(1, "Task1", ((System.currentTimeMillis()) + (60 * 1000)));
        Thread.sleep(((AcceptorExecutorTest.MAX_BATCHING_DELAY_MS) / 2));
        acceptorExecutor.process(2, "Task2", ((System.currentTimeMillis()) + (60 * 1000)));
        List<TaskHolder<Integer, String>> taskHolders = taskQueue.poll(5, TimeUnit.SECONDS);
        Assert.assertThat(taskHolders.size(), CoreMatchers.is(CoreMatchers.equalTo(2)));
    }
}

