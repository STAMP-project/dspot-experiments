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
package com.hazelcast.durableexecutor;


import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.executor.ExecutorServiceTestSupport;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
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
public class DurableRetrieveResultTest extends ExecutorServiceTestSupport {
    @Test
    public void testRetrieveResult_WhenNewNodesJoin() throws InterruptedException, ExecutionException {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        DurableExecutorService executorService = instance1.getDurableExecutorService(HazelcastTestSupport.randomString());
        ExecutorServiceTestSupport.SleepingTask task = new ExecutorServiceTestSupport.SleepingTask(5);
        DurableExecutorServiceFuture<Boolean> future = executorService.submit(task);
        factory.newHazelcastInstance();
        factory.newHazelcastInstance();
        Assert.assertTrue(future.get());
        Future<Boolean> retrievedFuture = executorService.retrieveAndDisposeResult(future.getTaskId());
        Assert.assertTrue(retrievedFuture.get());
    }

    @Test
    public void testDisposeResult() throws Exception {
        String key = HazelcastTestSupport.randomString();
        String name = HazelcastTestSupport.randomString();
        HazelcastInstance instance = createHazelcastInstance();
        DurableExecutorService executorService = instance.getDurableExecutorService(name);
        ExecutorServiceTestSupport.BasicTestCallable task = new ExecutorServiceTestSupport.BasicTestCallable();
        DurableExecutorServiceFuture<String> future = executorService.submitToKeyOwner(task, key);
        future.get();
        executorService.disposeResult(future.getTaskId());
        Future<Object> resultFuture = executorService.retrieveResult(future.getTaskId());
        Assert.assertNull(resultFuture.get());
    }

    @Test
    public void testRetrieveAndDispose_WhenSubmitterMemberDown() throws Exception {
        String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        factory.newHazelcastInstance();
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance2);
        DurableExecutorService executorService = instance1.getDurableExecutorService(name);
        ExecutorServiceTestSupport.SleepingTask task = new ExecutorServiceTestSupport.SleepingTask(4);
        long taskId = executorService.submitToKeyOwner(task, key).getTaskId();
        instance1.shutdown();
        executorService = instance2.getDurableExecutorService(name);
        Future<Boolean> future = executorService.retrieveAndDisposeResult(taskId);
        Assert.assertTrue(future.get());
        Future<Object> resultFuture = executorService.retrieveResult(taskId);
        Assert.assertNull(resultFuture.get());
    }

    @Test
    public void testRetrieveAndDispose_WhenOwnerMemberDown() throws Exception {
        String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        factory.newHazelcastInstance();
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        DurableExecutorService executorService = instance1.getDurableExecutorService(name);
        ExecutorServiceTestSupport.SleepingTask task = new ExecutorServiceTestSupport.SleepingTask(4);
        long taskId = executorService.submitToKeyOwner(task, key).getTaskId();
        instance1.shutdown();
        executorService = instance2.getDurableExecutorService(name);
        Future<Boolean> future = executorService.retrieveAndDisposeResult(taskId);
        Assert.assertTrue(future.get());
        Future<Object> resultFuture = executorService.retrieveResult(taskId);
        Assert.assertNull(resultFuture.get());
    }

    @Test
    public void testSingleExecution_WhenMigratedAfterCompletion_WhenOwnerMemberKilled() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance[] instances = factory.newInstances();
        HazelcastInstance first = instances[0];
        HazelcastInstance second = instances[1];
        HazelcastTestSupport.waitAllForSafeState(instances);
        String key = HazelcastTestSupport.generateKeyOwnedBy(first);
        String runCounterName = "runCount";
        IAtomicLong runCount = second.getAtomicLong(runCounterName);
        String name = HazelcastTestSupport.randomString();
        DurableExecutorService executorService = first.getDurableExecutorService(name);
        ExecutorServiceTestSupport.IncrementAtomicLongRunnable task = new ExecutorServiceTestSupport.IncrementAtomicLongRunnable(runCounterName);
        DurableExecutorServiceFuture future = executorService.submitToKeyOwner(task, key);
        future.get();// Wait for it to finish

        // Avoid race between PutResult & SHUTDOWN
        HazelcastTestSupport.sleepSeconds(3);
        first.getLifecycleService().terminate();
        executorService = second.getDurableExecutorService(name);
        Future<Object> newFuture = executorService.retrieveResult(future.getTaskId());
        newFuture.get();// Make sure its completed

        Assert.assertEquals(1, runCount.get());
    }

    @Test
    public void testRetrieve_WhenSubmitterMemberDown() throws Exception {
        String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        factory.newHazelcastInstance();
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance2);
        DurableExecutorService executorService = instance1.getDurableExecutorService(name);
        ExecutorServiceTestSupport.SleepingTask task = new ExecutorServiceTestSupport.SleepingTask(4);
        long taskId = executorService.submitToKeyOwner(task, key).getTaskId();
        instance1.shutdown();
        executorService = instance2.getDurableExecutorService(name);
        Future<Boolean> future = executorService.retrieveResult(taskId);
        Assert.assertTrue(future.get());
    }

    @Test
    public void testRetrieve_WhenOwnerMemberDown() throws Exception {
        String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        factory.newHazelcastInstance();
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        DurableExecutorService executorService = instance1.getDurableExecutorService(name);
        ExecutorServiceTestSupport.SleepingTask task = new ExecutorServiceTestSupport.SleepingTask(4);
        long taskId = executorService.submitToKeyOwner(task, key).getTaskId();
        instance1.shutdown();
        executorService = instance2.getDurableExecutorService(name);
        Future<Boolean> future = executorService.retrieveResult(taskId);
        Assert.assertTrue(future.get());
    }

    @Test
    public void testRetrieve_WhenResultOverwritten() throws Exception {
        String name = HazelcastTestSupport.randomString();
        Config config = new Config();
        config.getDurableExecutorConfig(name).setCapacity(1).setDurability(0);
        HazelcastInstance instance = createHazelcastInstance(config);
        DurableExecutorService executorService = instance.getDurableExecutorService(name);
        DurableExecutorServiceFuture<String> future = executorService.submitToKeyOwner(new ExecutorServiceTestSupport.BasicTestCallable(), name);
        long taskId = future.getTaskId();
        future.get();
        executorService.submitToKeyOwner(new ExecutorServiceTestSupport.BasicTestCallable(), name);
        Future<Object> resultFuture = executorService.retrieveResult(taskId);
        try {
            resultFuture.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof StaleTaskIdException));
        }
    }
}

