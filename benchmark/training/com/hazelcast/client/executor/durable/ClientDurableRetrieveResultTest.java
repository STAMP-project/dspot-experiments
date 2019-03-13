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
package com.hazelcast.client.executor.durable;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.durableexecutor.DurableExecutorService;
import com.hazelcast.durableexecutor.DurableExecutorServiceFuture;
import com.hazelcast.durableexecutor.StaleTaskIdException;
import com.hazelcast.executor.ExecutorServiceTestSupport.BasicTestCallable;
import com.hazelcast.executor.ExecutorServiceTestSupport.SleepingTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientDurableRetrieveResultTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    private HazelcastInstance instance1;

    private HazelcastInstance instance2;

    @Test
    public void testDisposeResult() throws Exception {
        String name = HazelcastTestSupport.randomString();
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        DurableExecutorService executorService = client.getDurableExecutorService(name);
        BasicTestCallable task = new BasicTestCallable();
        DurableExecutorServiceFuture<String> future = executorService.submitToKeyOwner(task, key);
        future.get();
        executorService.disposeResult(future.getTaskId());
        Future<Object> resultFuture = executorService.retrieveResult(future.getTaskId());
        Assert.assertNull(resultFuture.get());
    }

    @Test
    public void testRetrieveAndDispose_WhenClientDown() throws Exception {
        String name = HazelcastTestSupport.randomString();
        DurableExecutorService executorService = client.getDurableExecutorService(name);
        SleepingTask task = new SleepingTask(4);
        long taskId = executorService.submit(task).getTaskId();
        client.shutdown();
        client = hazelcastFactory.newHazelcastClient();
        executorService = client.getDurableExecutorService(name);
        Future<Boolean> future = executorService.retrieveAndDisposeResult(taskId);
        Assert.assertTrue(future.get());
        Future<Object> resultFuture = executorService.retrieveResult(taskId);
        Assert.assertNull(resultFuture.get());
    }

    @Test
    public void testRetrieveAndDispose_WhenOwnerMemberDown() throws Exception {
        String name = HazelcastTestSupport.randomString();
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance2);
        DurableExecutorService executorService = client.getDurableExecutorService(name);
        SleepingTask task = new SleepingTask(4);
        long taskId = executorService.submitToKeyOwner(task, key).getTaskId();
        instance2.shutdown();
        Future<Boolean> future = executorService.retrieveAndDisposeResult(taskId);
        Assert.assertTrue(future.get());
        Future<Boolean> resultFuture = executorService.retrieveResult(taskId);
        Assert.assertNull(resultFuture.get());
    }

    @Test
    public void testRetrieve_WhenSubmitterMemberDown() throws Exception {
        String name = HazelcastTestSupport.randomString();
        DurableExecutorService executorService = client.getDurableExecutorService(name);
        SleepingTask task = new SleepingTask(4);
        long taskId = executorService.submit(task).getTaskId();
        client.shutdown();
        client = hazelcastFactory.newHazelcastClient();
        executorService = client.getDurableExecutorService(name);
        Future<Boolean> future = executorService.retrieveResult(taskId);
        Assert.assertTrue(future.get());
    }

    @Test
    public void testRetrieve_WhenOwnerMemberDown() throws Exception {
        String name = HazelcastTestSupport.randomString();
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance2);
        DurableExecutorService executorService = client.getDurableExecutorService(name);
        SleepingTask task = new SleepingTask(4);
        long taskId = executorService.submitToKeyOwner(task, key).getTaskId();
        instance2.shutdown();
        Future<Boolean> future = executorService.retrieveResult(taskId);
        Assert.assertTrue(future.get());
    }

    @Test
    public void testRetrieve_WhenResultOverwritten() throws Exception {
        String name = HazelcastTestSupport.randomString();
        DurableExecutorService executorService = client.getDurableExecutorService(name);
        DurableExecutorServiceFuture<String> future = executorService.submitToKeyOwner(new BasicTestCallable(), name);
        long taskId = future.getTaskId();
        future.get();
        for (int i = 0; i < (DurableExecutorConfig.DEFAULT_RING_BUFFER_CAPACITY); i++) {
            executorService.submitToKeyOwner(new BasicTestCallable(), name);
        }
        Future<Object> resultFuture = executorService.retrieveResult(taskId);
        try {
            resultFuture.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof StaleTaskIdException));
        }
    }
}

