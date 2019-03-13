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


import GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS;
import com.hazelcast.config.Config;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.Member;
import com.hazelcast.core.PartitionAware;
import com.hazelcast.executor.ExecutorServiceTestSupport;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.executor.ExecutorServiceTestSupport.BasicTestCallable.RESULT;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DurableExecutorServiceTest extends ExecutorServiceTestSupport {
    private static final int NODE_COUNT = 3;

    private static final int TASK_COUNT = 1000;

    @Test(expected = UnsupportedOperationException.class)
    public void testInvokeAll() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        DurableExecutorService service = instance.getDurableExecutorService(HazelcastTestSupport.randomString());
        List<ExecutorServiceTestSupport.BasicTestCallable> callables = Collections.emptyList();
        service.invokeAll(callables);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInvokeAll_WithTimeout() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        DurableExecutorService service = instance.getDurableExecutorService(HazelcastTestSupport.randomString());
        List<ExecutorServiceTestSupport.BasicTestCallable> callables = Collections.emptyList();
        service.invokeAll(callables, 1, TimeUnit.SECONDS);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInvokeAny() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        DurableExecutorService service = instance.getDurableExecutorService(HazelcastTestSupport.randomString());
        List<ExecutorServiceTestSupport.BasicTestCallable> callables = Collections.emptyList();
        service.invokeAny(callables);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testInvokeAny_WithTimeout() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        DurableExecutorService service = instance.getDurableExecutorService(HazelcastTestSupport.randomString());
        List<ExecutorServiceTestSupport.BasicTestCallable> callables = Collections.emptyList();
        service.invokeAny(callables, 1, TimeUnit.SECONDS);
    }

    @Test
    public void testAwaitTermination() throws Exception {
        HazelcastInstance instance = createHazelcastInstance();
        DurableExecutorService service = instance.getDurableExecutorService(HazelcastTestSupport.randomString());
        Assert.assertFalse(service.awaitTermination(1, TimeUnit.SECONDS));
    }

    @Test
    public void testFullRingBuffer() throws Exception {
        String name = HazelcastTestSupport.randomString();
        String key = HazelcastTestSupport.randomString();
        Config config = new Config();
        config.getDurableExecutorConfig(name).setCapacity(1);
        HazelcastInstance instance = createHazelcastInstance(config);
        DurableExecutorService service = instance.getDurableExecutorService(name);
        service.submitToKeyOwner(new ExecutorServiceTestSupport.SleepingTask(100), key);
        DurableExecutorServiceFuture<String> future = service.submitToKeyOwner(new ExecutorServiceTestSupport.BasicTestCallable(), key);
        try {
            future.get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof RejectedExecutionException));
        }
    }

    @Test
    public void test_registerCallback_beforeFutureIsCompletedOnOtherNode() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        Assert.assertTrue(instance1.getCountDownLatch("latch").trySetCount(1));
        String name = HazelcastTestSupport.randomString();
        DurableExecutorService executorService = instance2.getDurableExecutorService(name);
        DurableExecutorServiceTest.ICountDownLatchAwaitCallable task = new DurableExecutorServiceTest.ICountDownLatchAwaitCallable("latch");
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        ICompletableFuture<Boolean> future = executorService.submitToKeyOwner(task, key);
        ExecutorServiceTestSupport.CountingDownExecutionCallback<Boolean> callback = new ExecutorServiceTestSupport.CountingDownExecutionCallback<Boolean>(1);
        future.andThen(callback);
        instance1.getCountDownLatch("latch").countDown();
        Assert.assertTrue(future.get());
        HazelcastTestSupport.assertOpenEventually(callback.getLatch());
    }

    @Test
    public void test_registerCallback_afterFutureIsCompletedOnOtherNode() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        String name = HazelcastTestSupport.randomString();
        DurableExecutorService executorService = instance2.getDurableExecutorService(name);
        ExecutorServiceTestSupport.BasicTestCallable task = new ExecutorServiceTestSupport.BasicTestCallable();
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        ICompletableFuture<String> future = executorService.submitToKeyOwner(task, key);
        Assert.assertEquals(RESULT, future.get());
        ExecutorServiceTestSupport.CountingDownExecutionCallback<String> callback = new ExecutorServiceTestSupport.CountingDownExecutionCallback<String>(1);
        future.andThen(callback);
        HazelcastTestSupport.assertOpenEventually(callback.getLatch(), 10);
    }

    @Test
    public void test_registerCallback_multipleTimes_futureIsCompletedOnOtherNode() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        Assert.assertTrue(instance1.getCountDownLatch("latch").trySetCount(1));
        String name = HazelcastTestSupport.randomString();
        DurableExecutorService executorService = instance2.getDurableExecutorService(name);
        DurableExecutorServiceTest.ICountDownLatchAwaitCallable task = new DurableExecutorServiceTest.ICountDownLatchAwaitCallable("latch");
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        ICompletableFuture<Boolean> future = executorService.submitToKeyOwner(task, key);
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorServiceTestSupport.CountingDownExecutionCallback<Boolean> callback = new ExecutorServiceTestSupport.CountingDownExecutionCallback<Boolean>(latch);
        future.andThen(callback);
        future.andThen(callback);
        instance1.getCountDownLatch("latch").countDown();
        Assert.assertTrue(future.get());
        HazelcastTestSupport.assertOpenEventually(latch, 10);
    }

    @Test
    public void testSubmitFailingCallableException_withExecutionCallback() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance = factory.newHazelcastInstance();
        DurableExecutorService service = instance.getDurableExecutorService(HazelcastTestSupport.randomString());
        ExecutorServiceTestSupport.CountingDownExecutionCallback<String> callback = new ExecutorServiceTestSupport.CountingDownExecutionCallback<String>(1);
        service.submit(new ExecutorServiceTestSupport.FailingTestTask()).andThen(callback);
        HazelcastTestSupport.assertOpenEventually(callback.getLatch());
        Assert.assertTrue(((callback.getResult()) instanceof Throwable));
    }

    /* ############ submit runnable ############ */
    @Test
    public void testManagedContextAndLocal() throws Exception {
        Config config = new Config();
        config.addDurableExecutorConfig(new DurableExecutorConfig("test").setPoolSize(1));
        final AtomicBoolean initialized = new AtomicBoolean();
        config.setManagedContext(new ManagedContext() {
            @Override
            public Object initialize(Object obj) {
                if (obj instanceof DurableExecutorServiceTest.RunnableWithManagedContext) {
                    initialized.set(true);
                }
                return obj;
            }
        });
        HazelcastInstance instance = createHazelcastInstance(config);
        DurableExecutorService executor = instance.getDurableExecutorService("test");
        DurableExecutorServiceTest.RunnableWithManagedContext task = new DurableExecutorServiceTest.RunnableWithManagedContext();
        executor.submit(task).get();
        Assert.assertTrue("The task should have been initialized by the ManagedContext", initialized.get());
    }

    @Test
    public void testExecuteOnKeyOwner() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance2);
        String instanceName = instance2.getName();
        ICountDownLatch latch = instance2.getCountDownLatch(instanceName);
        latch.trySetCount(1);
        DurableExecutorService durableExecutorService = instance1.getDurableExecutorService(HazelcastTestSupport.randomString());
        durableExecutorService.executeOnKeyOwner(new DurableExecutorServiceTest.InstanceAsserterRunnable(instanceName), key);
        latch.await(30, TimeUnit.SECONDS);
    }

    @Test
    public void hazelcastInstanceAwareAndLocal() throws Exception {
        Config config = new Config();
        config.addDurableExecutorConfig(new DurableExecutorConfig("test").setPoolSize(1));
        HazelcastInstance instance = createHazelcastInstance(config);
        DurableExecutorService executor = instance.getDurableExecutorService("test");
        DurableExecutorServiceTest.HazelcastInstanceAwareRunnable task = new DurableExecutorServiceTest.HazelcastInstanceAwareRunnable();
        // if setHazelcastInstance() not called we expect a RuntimeException
        executor.submit(task).get();
    }

    @Test
    public void testExecuteMultipleNode() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(DurableExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances();
        for (int i = 0; i < (DurableExecutorServiceTest.NODE_COUNT); i++) {
            DurableExecutorService service = instances[i].getDurableExecutorService("testExecuteMultipleNode");
            int rand = new Random().nextInt(100);
            Future<Integer> future = service.submit(new ExecutorServiceTestSupport.IncrementAtomicLongRunnable("count"), rand);
            Assert.assertEquals(Integer.valueOf(rand), future.get(10, TimeUnit.SECONDS));
        }
        IAtomicLong count = instances[0].getAtomicLong("count");
        Assert.assertEquals(DurableExecutorServiceTest.NODE_COUNT, count.get());
    }

    @Test
    public void testSubmitToKeyOwnerRunnable() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(DurableExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances();
        final AtomicInteger nullResponseCount = new AtomicInteger(0);
        final CountDownLatch responseLatch = new CountDownLatch(DurableExecutorServiceTest.NODE_COUNT);
        ExecutionCallback callback = new ExecutionCallback() {
            public void onResponse(Object response) {
                if (response == null) {
                    nullResponseCount.incrementAndGet();
                }
                responseLatch.countDown();
            }

            public void onFailure(Throwable t) {
            }
        };
        for (int i = 0; i < (DurableExecutorServiceTest.NODE_COUNT); i++) {
            HazelcastInstance instance = instances[i];
            DurableExecutorService service = instance.getDurableExecutorService("testSubmitToKeyOwnerRunnable");
            Member localMember = instance.getCluster().getLocalMember();
            String uuid = localMember.getUuid();
            Runnable runnable = new ExecutorServiceTestSupport.IncrementAtomicLongIfMemberUUIDNotMatchRunnable(uuid, "testSubmitToKeyOwnerRunnable");
            int key = findNextKeyForMember(instance, localMember);
            service.submitToKeyOwner(runnable, key).andThen(callback);
        }
        HazelcastTestSupport.assertOpenEventually(responseLatch);
        Assert.assertEquals(0, instances[0].getAtomicLong("testSubmitToKeyOwnerRunnable").get());
        Assert.assertEquals(DurableExecutorServiceTest.NODE_COUNT, nullResponseCount.get());
    }

    /**
     * Submit a null task has to raise a NullPointerException.
     */
    @Test(expected = NullPointerException.class)
    @SuppressWarnings("ConstantConditions")
    public void submitNullTask() {
        DurableExecutorService executor = createSingleNodeDurableExecutorService("submitNullTask");
        executor.submit(((Callable) (null)));
    }

    /**
     * Run a basic task.
     */
    @Test
    public void testBasicTask() throws Exception {
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        DurableExecutorService executor = createSingleNodeDurableExecutorService("testBasicTask");
        Future future = executor.submit(task);
        Assert.assertEquals(future.get(), RESULT);
    }

    @Test
    public void testSubmitMultipleNode() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(DurableExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances();
        for (int i = 0; i < (DurableExecutorServiceTest.NODE_COUNT); i++) {
            DurableExecutorService service = instances[i].getDurableExecutorService("testSubmitMultipleNode");
            Future future = service.submit(new ExecutorServiceTestSupport.IncrementAtomicLongCallable("testSubmitMultipleNode"));
            Assert.assertEquals(((long) (i + 1)), future.get());
        }
    }

    /* ############ submit callable ############ */
    @Test
    public void testSubmitToKeyOwnerCallable() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(DurableExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances();
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < (DurableExecutorServiceTest.NODE_COUNT); i++) {
            HazelcastInstance instance = instances[i];
            DurableExecutorService service = instance.getDurableExecutorService("testSubmitToKeyOwnerCallable");
            Member localMember = instance.getCluster().getLocalMember();
            int key = findNextKeyForMember(instance, localMember);
            Future future = service.submitToKeyOwner(new ExecutorServiceTestSupport.MemberUUIDCheckCallable(localMember.getUuid()), key);
            futures.add(future);
        }
        for (Future future : futures) {
            Assert.assertTrue(((Boolean) (future.get(10, TimeUnit.SECONDS))));
        }
    }

    @Test
    public void testSubmitToKeyOwnerCallable_withCallback() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(DurableExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances();
        ExecutorServiceTestSupport.BooleanSuccessResponseCountingCallback callback = new ExecutorServiceTestSupport.BooleanSuccessResponseCountingCallback(DurableExecutorServiceTest.NODE_COUNT);
        for (int i = 0; i < (DurableExecutorServiceTest.NODE_COUNT); i++) {
            HazelcastInstance instance = instances[i];
            DurableExecutorService service = instance.getDurableExecutorService("testSubmitToKeyOwnerCallable");
            Member localMember = instance.getCluster().getLocalMember();
            int key = findNextKeyForMember(instance, localMember);
            service.submitToKeyOwner(new ExecutorServiceTestSupport.MemberUUIDCheckCallable(localMember.getUuid()), key).andThen(callback);
        }
        HazelcastTestSupport.assertOpenEventually(callback.getResponseLatch());
        Assert.assertEquals(DurableExecutorServiceTest.NODE_COUNT, callback.getSuccessResponseCount());
    }

    @Test
    public void testIsDoneMethod() throws Exception {
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        DurableExecutorService executor = createSingleNodeDurableExecutorService("isDoneMethod");
        Future future = executor.submit(task);
        assertResult(future, RESULT);
    }

    /**
     * Repeatedly runs tasks and check for isDone() status after get().
     * Test for the issue 129.
     */
    @Test
    public void testIsDoneMethodAfterGet() throws Exception {
        DurableExecutorService executor = createSingleNodeDurableExecutorService("isDoneMethodAfterGet");
        for (int i = 0; i < (DurableExecutorServiceTest.TASK_COUNT); i++) {
            Callable<String> task1 = new ExecutorServiceTestSupport.BasicTestCallable();
            Callable<String> task2 = new ExecutorServiceTestSupport.BasicTestCallable();
            Future future1 = executor.submit(task1);
            Future future2 = executor.submit(task2);
            assertResult(future2, RESULT);
            assertResult(future1, RESULT);
        }
    }

    @Test
    public void testMultipleFutureGetInvocations() throws Exception {
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        DurableExecutorService executor = createSingleNodeDurableExecutorService("isTwoGetFromFuture");
        Future<String> future = executor.submit(task);
        assertResult(future, RESULT);
        assertResult(future, RESULT);
        assertResult(future, RESULT);
        assertResult(future, RESULT);
    }

    @Test
    public void testIssue292() {
        ExecutorServiceTestSupport.CountingDownExecutionCallback<Member> callback = new ExecutorServiceTestSupport.CountingDownExecutionCallback<Member>(1);
        createSingleNodeDurableExecutorService("testIssue292").submit(new ExecutorServiceTestSupport.MemberCheck()).andThen(callback);
        HazelcastTestSupport.assertOpenEventually(callback.getLatch());
        Assert.assertTrue(((callback.getResult()) instanceof Member));
    }

    /**
     * Execute a task that is executing something else inside (nested execution).
     */
    @Test
    public void testNestedExecution() {
        Callable<String> task = new ExecutorServiceTestSupport.NestedExecutorTask();
        DurableExecutorService executor = createSingleNodeDurableExecutorService("testNestedExecution");
        Future future = executor.submit(task);
        HazelcastTestSupport.assertCompletesEventually(future);
    }

    /**
     * Shutdown-related method behaviour when the cluster is running.
     */
    @Test
    public void testShutdownBehaviour() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        factory.newHazelcastInstance();
        DurableExecutorService executor = instance1.getDurableExecutorService("testShutdownBehaviour");
        // fresh instance, is not shutting down
        Assert.assertFalse(executor.isShutdown());
        Assert.assertFalse(executor.isTerminated());
        executor.shutdown();
        Assert.assertTrue(executor.isShutdown());
        Assert.assertTrue(executor.isTerminated());
        // shutdownNow() should return an empty list and be ignored
        List<Runnable> pending = executor.shutdownNow();
        Assert.assertTrue(pending.isEmpty());
        Assert.assertTrue(executor.isShutdown());
        Assert.assertTrue(executor.isTerminated());
        // awaitTermination() should return immediately false
        try {
            boolean terminated = executor.awaitTermination(60L, TimeUnit.SECONDS);
            Assert.assertFalse(terminated);
        } catch (InterruptedException ie) {
            Assert.fail("InterruptedException");
        }
        Assert.assertTrue(executor.isShutdown());
        Assert.assertTrue(executor.isTerminated());
    }

    /**
     * Shutting down the cluster should act as the ExecutorService shutdown.
     */
    @Test(expected = RejectedExecutionException.class)
    public void testClusterShutdown() {
        ExecutorService executor = createSingleNodeDurableExecutorService("testClusterShutdown");
        shutdownNodeFactory();
        HazelcastTestSupport.sleepSeconds(2);
        Assert.assertNotNull(executor);
        Assert.assertTrue(executor.isShutdown());
        Assert.assertTrue(executor.isTerminated());
        // new tasks must be rejected
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        executor.submit(task);
    }

    // @Repeat(100)
    @Test
    public void testStatsIssue2039() throws Exception {
        Config config = new Config();
        String name = "testStatsIssue2039";
        config.addDurableExecutorConfig(new DurableExecutorConfig(name).setPoolSize(1).setCapacity(1));
        HazelcastInstance instance = createHazelcastInstance(config);
        DurableExecutorService executorService = instance.getDurableExecutorService(name);
        executorService.execute(new DurableExecutorServiceTest.SleepLatchRunnable());
        HazelcastTestSupport.assertOpenEventually(DurableExecutorServiceTest.SleepLatchRunnable.startLatch, 30);
        Future rejected = executorService.submit(new DurableExecutorServiceTest.EmptyRunnable());
        try {
            rejected.get(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            boolean isRejected = (e.getCause()) instanceof RejectedExecutionException;
            if (!isRejected) {
                Assert.fail(e.toString());
            }
        } finally {
            DurableExecutorServiceTest.SleepLatchRunnable.sleepLatch.countDown();
        }
        // FIXME as soon as executorService.getLocalExecutorStats() is implemented
        // LocalExecutorStats stats = executorService.getLocalExecutorStats();
        // assertEquals(2, stats.getStartedTaskCount());
        // assertEquals(0, stats.getPendingTaskCount());
    }

    @Test
    public void testLongRunningCallable() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        Config config = new Config();
        long callTimeoutMillis = 3000;
        config.setProperty(OPERATION_CALL_TIMEOUT_MILLIS.getName(), String.valueOf(callTimeoutMillis));
        HazelcastInstance hz1 = factory.newHazelcastInstance(config);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        String key = HazelcastTestSupport.generateKeyOwnedBy(hz2);
        DurableExecutorService executor = hz1.getDurableExecutorService("test");
        Future<Boolean> future = executor.submitToKeyOwner(new ExecutorServiceTestSupport.SleepingTask(((TimeUnit.MILLISECONDS.toSeconds(callTimeoutMillis)) * 3)), key);
        Boolean result = future.get(1, TimeUnit.MINUTES);
        Assert.assertTrue(result);
    }

    private static class InstanceAsserterRunnable implements HazelcastInstanceAware , Serializable , Runnable {
        transient HazelcastInstance instance;

        String instanceName;

        public InstanceAsserterRunnable() {
        }

        public InstanceAsserterRunnable(String instanceName) {
            this.instanceName = instanceName;
        }

        @Override
        public void run() {
            if (instanceName.equals(instance.getName())) {
                instance.getCountDownLatch(instanceName).countDown();
            }
        }

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            instance = hazelcastInstance;
        }
    }

    private static class RunnableWithManagedContext implements Serializable , Runnable {
        @Override
        public void run() {
        }
    }

    static class HazelcastInstanceAwareRunnable implements HazelcastInstanceAware , Serializable , Runnable {
        private transient boolean initializeCalled = false;

        @Override
        public void run() {
            if (!(initializeCalled)) {
                throw new RuntimeException("The setHazelcastInstance should have been called");
            }
        }

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            initializeCalled = true;
        }
    }

    static class LatchRunnable implements Serializable , Runnable {
        static CountDownLatch latch;

        final int executionTime = 200;

        @Override
        public void run() {
            try {
                Thread.sleep(executionTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DurableExecutorServiceTest.LatchRunnable.latch.countDown();
        }
    }

    static class ICountDownLatchAwaitCallable implements HazelcastInstanceAware , Serializable , Callable<Boolean> {
        private final String name;

        private HazelcastInstance instance;

        public ICountDownLatchAwaitCallable(String name) {
            this.name = name;
        }

        @Override
        public Boolean call() throws Exception {
            return instance.getCountDownLatch(name).await(100, TimeUnit.SECONDS);
        }

        @Override
        public void setHazelcastInstance(HazelcastInstance instance) {
            this.instance = instance;
        }
    }

    static class SleepLatchRunnable implements PartitionAware , Serializable , Runnable {
        static CountDownLatch startLatch;

        static CountDownLatch sleepLatch;

        public SleepLatchRunnable() {
            DurableExecutorServiceTest.SleepLatchRunnable.startLatch = new CountDownLatch(1);
            DurableExecutorServiceTest.SleepLatchRunnable.sleepLatch = new CountDownLatch(1);
        }

        @Override
        public void run() {
            DurableExecutorServiceTest.SleepLatchRunnable.startLatch.countDown();
            HazelcastTestSupport.assertOpenEventually(DurableExecutorServiceTest.SleepLatchRunnable.sleepLatch);
        }

        @Override
        public Object getPartitionKey() {
            return "key";
        }
    }

    static class EmptyRunnable implements PartitionAware , Serializable , Runnable {
        @Override
        public void run() {
        }

        @Override
        public Object getPartitionKey() {
            return "key";
        }
    }
}

