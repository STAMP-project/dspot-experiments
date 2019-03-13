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
package com.hazelcast.executor;


import GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS;
import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.core.MultiExecutionCallback;
import com.hazelcast.core.PartitionAware;
import com.hazelcast.monitor.LocalExecutorStats;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.executor.ExecutorServiceTestSupport.CountDownLatchAwaitingCallable.RESULT;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ExecutorServiceTest extends ExecutorServiceTestSupport {
    private static final int NODE_COUNT = 3;

    private static final int TASK_COUNT = 1000;

    // we need to make sure that if a deserialization exception is encounter, the exception isn't lost but always
    // is send to the caller.
    @Test
    public void whenDeserializationFails_thenExceptionPropagatedToCaller() throws Exception {
        HazelcastInstance hz = createHazelcastInstance();
        IExecutorService executor = hz.getExecutorService("executor");
        Future<String> future = executor.submit(new ExecutorServiceTest.CallableWithDeserializationError());
        try {
            future.get();
            Assert.fail();
        } catch (ExecutionException e) {
            HazelcastTestSupport.assertInstanceOf(HazelcastSerializationException.class, e.getCause());
        }
    }

    public static class CallableWithDeserializationError implements DataSerializable , Callable {
        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            in.readInt();
        }

        @Override
        public Object call() throws Exception {
            return null;
        }
    }

    /* ############ andThen(Callback) ############ */
    @Test
    public void testPreregisteredExecutionCallbackCompletableFuture() {
        ExecutionService es = getExecutionService(createHazelcastInstance());
        CountDownLatch callableLatch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Future<String> future = executorService.submit(new ExecutorServiceTestSupport.CountDownLatchAwaitingCallable(callableLatch));
            ExecutorServiceTestSupport.CountingDownExecutionCallback<String> callback = new ExecutorServiceTestSupport.CountingDownExecutionCallback<String>(1);
            ICompletableFuture<String> completableFuture = es.asCompletableFuture(future);
            completableFuture.andThen(callback);
            callableLatch.countDown();
            HazelcastTestSupport.assertOpenEventually(callback.getLatch());
            Assert.assertEquals(RESULT, callback.getResult());
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void testMultiPreregisteredExecutionCallbackCompletableFuture() {
        ExecutionService es = getExecutionService(createHazelcastInstance());
        CountDownLatch callableLatch = new CountDownLatch(1);
        CountDownLatch callbackLatch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Future<String> future = executorService.submit(new ExecutorServiceTestSupport.CountDownLatchAwaitingCallable(callableLatch));
            ExecutorServiceTestSupport.CountingDownExecutionCallback<String> callback1 = new ExecutorServiceTestSupport.CountingDownExecutionCallback<String>(callbackLatch);
            ExecutorServiceTestSupport.CountingDownExecutionCallback<String> callback2 = new ExecutorServiceTestSupport.CountingDownExecutionCallback<String>(callbackLatch);
            ICompletableFuture<String> completableFuture = es.asCompletableFuture(future);
            completableFuture.andThen(callback1);
            completableFuture.andThen(callback2);
            callableLatch.countDown();
            HazelcastTestSupport.assertOpenEventually(callbackLatch);
            Assert.assertEquals(RESULT, callback1.getResult());
            Assert.assertEquals(RESULT, callback2.getResult());
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void testPostRegisteredExecutionCallbackCompletableFuture() throws Exception {
        ExecutionService es = getExecutionService(createHazelcastInstance());
        CountDownLatch callableLatch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Future<String> future = executorService.submit(new ExecutorServiceTestSupport.CountDownLatchAwaitingCallable(callableLatch));
            ICompletableFuture<String> completableFuture = es.asCompletableFuture(future);
            callableLatch.countDown();
            future.get();
            ExecutorServiceTestSupport.CountingDownExecutionCallback<String> callback = new ExecutorServiceTestSupport.CountingDownExecutionCallback<String>(1);
            completableFuture.andThen(callback);
            try {
                HazelcastTestSupport.assertOpenEventually(callback.getLatch());
                Assert.assertEquals(RESULT, callback.getResult());
            } catch (AssertionError error) {
                System.out.println(callback.getLatch().getCount());
                System.out.println(callback.getResult());
                throw error;
            }
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void testMultiPostRegisteredExecutionCallbackCompletableFuture() throws Exception {
        ExecutionService es = getExecutionService(createHazelcastInstance());
        CountDownLatch callableLatch = new CountDownLatch(1);
        CountDownLatch callbackLatch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            Future<String> future = executorService.submit(new ExecutorServiceTestSupport.CountDownLatchAwaitingCallable(callableLatch));
            ICompletableFuture<String> completableFuture = es.asCompletableFuture(future);
            callableLatch.countDown();
            future.get();
            ExecutorServiceTestSupport.CountingDownExecutionCallback<String> callback1 = new ExecutorServiceTestSupport.CountingDownExecutionCallback<String>(callbackLatch);
            completableFuture.andThen(callback1);
            ExecutorServiceTestSupport.CountingDownExecutionCallback<String> callback2 = new ExecutorServiceTestSupport.CountingDownExecutionCallback<String>(callbackLatch);
            completableFuture.andThen(callback2);
            HazelcastTestSupport.assertOpenEventually(callbackLatch);
            Assert.assertEquals(RESULT, callback1.getResult());
            Assert.assertEquals(RESULT, callback2.getResult());
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void test_registerCallback_beforeFutureIsCompletedOnOtherNode() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        Assert.assertTrue(instance1.getCountDownLatch("latch").trySetCount(1));
        String name = HazelcastTestSupport.randomString();
        IExecutorService executorService = instance2.getExecutorService(name);
        ExecutorServiceTest.ICountDownLatchAwaitCallable task = new ExecutorServiceTest.ICountDownLatchAwaitCallable("latch");
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        ICompletableFuture<Boolean> future = ((ICompletableFuture<Boolean>) (executorService.submitToKeyOwner(task, key)));
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
        IExecutorService executorService = instance2.getExecutorService(name);
        ExecutorServiceTestSupport.BasicTestCallable task = new ExecutorServiceTestSupport.BasicTestCallable();
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        ICompletableFuture<String> future = ((ICompletableFuture<String>) (executorService.submitToKeyOwner(task, key)));
        Assert.assertEquals(ExecutorServiceTestSupport.BasicTestCallable.RESULT, future.get());
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
        IExecutorService executorService = instance2.getExecutorService(name);
        ExecutorServiceTest.ICountDownLatchAwaitCallable task = new ExecutorServiceTest.ICountDownLatchAwaitCallable("latch");
        String key = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        ICompletableFuture<Boolean> future = ((ICompletableFuture<Boolean>) (executorService.submitToKeyOwner(task, key)));
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
        IExecutorService service = instance.getExecutorService(HazelcastTestSupport.randomString());
        ExecutorServiceTestSupport.CountingDownExecutionCallback<String> callback = new ExecutorServiceTestSupport.CountingDownExecutionCallback<String>(1);
        service.submit(new ExecutorServiceTestSupport.FailingTestTask(), callback);
        HazelcastTestSupport.assertOpenEventually(callback.getLatch());
        Assert.assertTrue(((callback.getResult()) instanceof Throwable));
    }

    /* ############ submit(Runnable) ############ */
    @Test(expected = RejectedExecutionException.class)
    public void testEmptyMemberSelector() {
        HazelcastInstance instance = createHazelcastInstance();
        String name = HazelcastTestSupport.randomString();
        IExecutorService executorService = instance.getExecutorService(name);
        ExecutorServiceTest.HazelcastInstanceAwareRunnable task = new ExecutorServiceTest.HazelcastInstanceAwareRunnable();
        executorService.execute(task, new MemberSelector() {
            @Override
            public boolean select(Member member) {
                return false;
            }
        });
    }

    @Test
    public void testManagedContextAndLocal() throws Exception {
        Config config = new Config();
        config.addExecutorConfig(new ExecutorConfig("test", 1));
        final AtomicBoolean initialized = new AtomicBoolean();
        config.setManagedContext(new ManagedContext() {
            @Override
            public Object initialize(Object obj) {
                if (obj instanceof ExecutorServiceTest.RunnableWithManagedContext) {
                    initialized.set(true);
                }
                return obj;
            }
        });
        HazelcastInstance instance = createHazelcastInstance(config);
        IExecutorService executor = instance.getExecutorService("test");
        ExecutorServiceTest.RunnableWithManagedContext task = new ExecutorServiceTest.RunnableWithManagedContext();
        executor.submit(task).get();
        Assert.assertTrue("The task should have been initialized by the ManagedContext", initialized.get());
    }

    static class RunnableWithManagedContext implements Serializable , Runnable {
        @Override
        public void run() {
        }
    }

    @Test
    public void hazelcastInstanceAwareAndLocal() throws Exception {
        Config config = new Config();
        config.addExecutorConfig(new ExecutorConfig("test", 1));
        HazelcastInstance instance = createHazelcastInstance(config);
        IExecutorService executor = instance.getExecutorService("test");
        ExecutorServiceTest.HazelcastInstanceAwareRunnable task = new ExecutorServiceTest.HazelcastInstanceAwareRunnable();
        // if 'setHazelcastInstance' not called we expect a RuntimeException
        executor.submit(task).get();
    }

    static class HazelcastInstanceAwareRunnable implements HazelcastInstanceAware , Serializable , Runnable {
        private transient boolean initializeCalled;

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

    @Test
    public void testExecuteMultipleNode() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            IExecutorService service = instances[i].getExecutorService("testExecuteMultipleNode");
            int rand = new Random().nextInt(100);
            Future<Integer> future = service.submit(new ExecutorServiceTestSupport.IncrementAtomicLongRunnable("count"), rand);
            Assert.assertEquals(Integer.valueOf(rand), future.get(10, TimeUnit.SECONDS));
        }
        IAtomicLong count = instances[0].getAtomicLong("count");
        Assert.assertEquals(ExecutorServiceTest.NODE_COUNT, count.get());
    }

    @Test
    public void testSubmitToKeyOwnerRunnable() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        final AtomicInteger nullResponseCount = new AtomicInteger(0);
        final CountDownLatch responseLatch = new CountDownLatch(ExecutorServiceTest.NODE_COUNT);
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
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            HazelcastInstance instance = instances[i];
            IExecutorService service = instance.getExecutorService("testSubmitToKeyOwnerRunnable");
            Member localMember = instance.getCluster().getLocalMember();
            int key = findNextKeyForMember(instance, localMember);
            service.submitToKeyOwner(new ExecutorServiceTestSupport.IncrementAtomicLongIfMemberUUIDNotMatchRunnable(localMember.getUuid(), "testSubmitToKeyOwnerRunnable"), key, callback);
        }
        HazelcastTestSupport.assertOpenEventually(responseLatch);
        Assert.assertEquals(0, instances[0].getAtomicLong("testSubmitToKeyOwnerRunnable").get());
        Assert.assertEquals(ExecutorServiceTest.NODE_COUNT, nullResponseCount.get());
    }

    @Test
    public void testSubmitToMemberRunnable() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        final AtomicInteger nullResponseCount = new AtomicInteger(0);
        final CountDownLatch responseLatch = new CountDownLatch(ExecutorServiceTest.NODE_COUNT);
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
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            HazelcastInstance instance = instances[i];
            IExecutorService service = instance.getExecutorService("testSubmitToMemberRunnable");
            Member localMember = instance.getCluster().getLocalMember();
            service.submitToMember(new ExecutorServiceTestSupport.IncrementAtomicLongIfMemberUUIDNotMatchRunnable(localMember.getUuid(), "testSubmitToMemberRunnable"), localMember, callback);
        }
        HazelcastTestSupport.assertOpenEventually(responseLatch);
        Assert.assertEquals(0, instances[0].getAtomicLong("testSubmitToMemberRunnable").get());
        Assert.assertEquals(ExecutorServiceTest.NODE_COUNT, nullResponseCount.get());
    }

    @Test
    public void testSubmitToMembersRunnable() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        ExecutorServiceTestSupport.ResponseCountingMultiExecutionCallback callback = new ExecutorServiceTestSupport.ResponseCountingMultiExecutionCallback(ExecutorServiceTest.NODE_COUNT);
        int sum = 0;
        Set<Member> membersSet = instances[0].getCluster().getMembers();
        Member[] members = membersSet.toArray(new Member[0]);
        Random random = new Random();
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            IExecutorService service = instances[i].getExecutorService("testSubmitToMembersRunnable");
            int n = (random.nextInt(ExecutorServiceTest.NODE_COUNT)) + 1;
            sum += n;
            Member[] m = new Member[n];
            System.arraycopy(members, 0, m, 0, n);
            service.submitToMembers(new ExecutorServiceTestSupport.IncrementAtomicLongRunnable("testSubmitToMembersRunnable"), Arrays.asList(m), callback);
        }
        HazelcastTestSupport.assertOpenEventually(callback.getLatch());
        IAtomicLong result = instances[0].getAtomicLong("testSubmitToMembersRunnable");
        Assert.assertEquals(sum, result.get());
        Assert.assertEquals(sum, callback.getCount());
    }

    @Test
    public void testSubmitToAllMembersRunnable() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        final AtomicInteger nullResponseCount = new AtomicInteger(0);
        final CountDownLatch responseLatch = new CountDownLatch(((ExecutorServiceTest.NODE_COUNT) * (ExecutorServiceTest.NODE_COUNT)));
        MultiExecutionCallback callback = new MultiExecutionCallback() {
            public void onResponse(Member member, Object value) {
                if (value == null) {
                    nullResponseCount.incrementAndGet();
                }
                responseLatch.countDown();
            }

            public void onComplete(Map<Member, Object> values) {
            }
        };
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            IExecutorService service = instances[i].getExecutorService("testSubmitToAllMembersRunnable");
            service.submitToAllMembers(new ExecutorServiceTestSupport.IncrementAtomicLongRunnable("testSubmitToAllMembersRunnable"), callback);
        }
        Assert.assertTrue(responseLatch.await(30, TimeUnit.SECONDS));
        IAtomicLong result = instances[0].getAtomicLong("testSubmitToAllMembersRunnable");
        Assert.assertEquals(((ExecutorServiceTest.NODE_COUNT) * (ExecutorServiceTest.NODE_COUNT)), result.get());
        Assert.assertEquals(((ExecutorServiceTest.NODE_COUNT) * (ExecutorServiceTest.NODE_COUNT)), nullResponseCount.get());
    }

    /* ############ submit(Callable) ############ */
    /**
     * Submit a null task must raise a NullPointerException
     */
    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void submitNullTask() {
        ExecutorService executor = createSingleNodeExecutorService("submitNullTask");
        executor.submit(((Callable) (null)));
    }

    /**
     * Run a basic task
     */
    @Test
    public void testBasicTask() throws Exception {
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        ExecutorService executor = createSingleNodeExecutorService("testBasicTask");
        Future future = executor.submit(task);
        Assert.assertEquals(future.get(), ExecutorServiceTestSupport.BasicTestCallable.RESULT);
    }

    @Test
    public void testSubmitMultipleNode() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            IExecutorService service = instances[i].getExecutorService("testSubmitMultipleNode");
            Future future = service.submit(new ExecutorServiceTestSupport.IncrementAtomicLongCallable("testSubmitMultipleNode"));
            Assert.assertEquals(((long) (i + 1)), future.get());
        }
    }

    @Test
    public void testSubmitToKeyOwnerCallable() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            HazelcastInstance instance = instances[i];
            IExecutorService service = instance.getExecutorService("testSubmitToKeyOwnerCallable");
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
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        ExecutorServiceTestSupport.BooleanSuccessResponseCountingCallback callback = new ExecutorServiceTestSupport.BooleanSuccessResponseCountingCallback(ExecutorServiceTest.NODE_COUNT);
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            HazelcastInstance instance = instances[i];
            IExecutorService service = instance.getExecutorService("testSubmitToKeyOwnerCallable");
            Member localMember = instance.getCluster().getLocalMember();
            int key = findNextKeyForMember(instance, localMember);
            service.submitToKeyOwner(new ExecutorServiceTestSupport.MemberUUIDCheckCallable(localMember.getUuid()), key, callback);
        }
        HazelcastTestSupport.assertOpenEventually(callback.getResponseLatch());
        Assert.assertEquals(ExecutorServiceTest.NODE_COUNT, callback.getSuccessResponseCount());
    }

    @Test
    public void testSubmitToMemberCallable() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            HazelcastInstance instance = instances[i];
            IExecutorService service = instance.getExecutorService("testSubmitToMemberCallable");
            String memberUuid = instance.getCluster().getLocalMember().getUuid();
            Future future = service.submitToMember(new ExecutorServiceTestSupport.MemberUUIDCheckCallable(memberUuid), instance.getCluster().getLocalMember());
            futures.add(future);
        }
        for (Future future : futures) {
            Assert.assertTrue(((Boolean) (future.get(10, TimeUnit.SECONDS))));
        }
    }

    @Test
    public void testSubmitToMemberCallable_withCallback() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        ExecutorServiceTestSupport.BooleanSuccessResponseCountingCallback callback = new ExecutorServiceTestSupport.BooleanSuccessResponseCountingCallback(ExecutorServiceTest.NODE_COUNT);
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            HazelcastInstance instance = instances[i];
            IExecutorService service = instance.getExecutorService("testSubmitToMemberCallable");
            String memberUuid = instance.getCluster().getLocalMember().getUuid();
            service.submitToMember(new ExecutorServiceTestSupport.MemberUUIDCheckCallable(memberUuid), instance.getCluster().getLocalMember(), callback);
        }
        HazelcastTestSupport.assertOpenEventually(callback.getResponseLatch());
        Assert.assertEquals(ExecutorServiceTest.NODE_COUNT, callback.getSuccessResponseCount());
    }

    @Test
    public void testSubmitToMembersCallable() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        final AtomicInteger count = new AtomicInteger(0);
        final CountDownLatch latch = new CountDownLatch(ExecutorServiceTest.NODE_COUNT);
        MultiExecutionCallback callback = new MultiExecutionCallback() {
            public void onResponse(Member member, Object value) {
                count.incrementAndGet();
            }

            public void onComplete(Map<Member, Object> values) {
                latch.countDown();
            }
        };
        int sum = 0;
        Set<Member> membersSet = instances[0].getCluster().getMembers();
        Member[] members = membersSet.toArray(new Member[0]);
        Random random = new Random();
        String name = "testSubmitToMembersCallable";
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            IExecutorService service = instances[i].getExecutorService(name);
            int n = (random.nextInt(ExecutorServiceTest.NODE_COUNT)) + 1;
            sum += n;
            Member[] m = new Member[n];
            System.arraycopy(members, 0, m, 0, n);
            service.submitToMembers(new ExecutorServiceTestSupport.IncrementAtomicLongCallable(name), Arrays.asList(m), callback);
        }
        HazelcastTestSupport.assertOpenEventually(latch);
        IAtomicLong result = instances[0].getAtomicLong(name);
        Assert.assertEquals(sum, result.get());
        Assert.assertEquals(sum, count.get());
    }

    @Test
    public void testSubmitToAllMembersCallable() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(ExecutorServiceTest.NODE_COUNT);
        HazelcastInstance[] instances = factory.newInstances(new Config());
        final AtomicInteger count = new AtomicInteger(0);
        final CountDownLatch countDownLatch = new CountDownLatch(((ExecutorServiceTest.NODE_COUNT) * (ExecutorServiceTest.NODE_COUNT)));
        MultiExecutionCallback callback = new MultiExecutionCallback() {
            public void onResponse(Member member, Object value) {
                count.incrementAndGet();
                countDownLatch.countDown();
            }

            public void onComplete(Map<Member, Object> values) {
            }
        };
        for (int i = 0; i < (ExecutorServiceTest.NODE_COUNT); i++) {
            IExecutorService service = instances[i].getExecutorService("testSubmitToAllMembersCallable");
            service.submitToAllMembers(new ExecutorServiceTestSupport.IncrementAtomicLongCallable("testSubmitToAllMembersCallable"), callback);
        }
        HazelcastTestSupport.assertOpenEventually(countDownLatch);
        IAtomicLong result = instances[0].getAtomicLong("testSubmitToAllMembersCallable");
        Assert.assertEquals(((ExecutorServiceTest.NODE_COUNT) * (ExecutorServiceTest.NODE_COUNT)), result.get());
        Assert.assertEquals(((ExecutorServiceTest.NODE_COUNT) * (ExecutorServiceTest.NODE_COUNT)), count.get());
    }

    /* ############ cancellation ############ */
    @Test
    public void testCancellationAwareTask() throws Exception {
        ExecutorServiceTestSupport.SleepingTask task = new ExecutorServiceTestSupport.SleepingTask(10);
        ExecutorService executor = createSingleNodeExecutorService("testCancellationAwareTask");
        Future future = executor.submit(task);
        try {
            future.get(2, TimeUnit.SECONDS);
            Assert.fail("Should throw TimeoutException!");
        } catch (TimeoutException expected) {
        }
        Assert.assertFalse(future.isDone());
        Assert.assertTrue(future.cancel(true));
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
        try {
            future.get();
            Assert.fail("Should not complete the task successfully");
        } catch (CancellationException expected) {
        } catch (Exception e) {
            Assert.fail(("Unexpected exception " + e));
        }
    }

    @Test
    public void testCancellationAwareTask2() {
        Callable task1 = new ExecutorServiceTestSupport.SleepingTask(Integer.MAX_VALUE);
        ExecutorService executor = createSingleNodeExecutorService("testCancellationAwareTask", 1);
        Future future1 = executor.submit(task1);
        try {
            future1.get(2, TimeUnit.SECONDS);
            Assert.fail("SleepingTask should not return response");
        } catch (TimeoutException ignored) {
        } catch (Exception e) {
            if ((e.getCause()) instanceof RejectedExecutionException) {
                Assert.fail("SleepingTask is rejected!");
            }
        }
        Assert.assertFalse(future1.isDone());
        Callable task2 = new ExecutorServiceTestSupport.BasicTestCallable();
        Future future2 = executor.submit(task2);
        Assert.assertFalse(future2.isDone());
        Assert.assertTrue(future2.cancel(true));
        Assert.assertTrue(future2.isCancelled());
        Assert.assertTrue(future2.isDone());
        try {
            future2.get();
            Assert.fail("Should not complete the task successfully");
        } catch (CancellationException expected) {
        } catch (Exception e) {
            Assert.fail(("Unexpected exception " + e));
        }
    }

    /* ############ future ############ */
    /**
     * Test the method isDone()
     */
    @Test
    public void testIsDoneMethod() throws Exception {
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        IExecutorService executor = createSingleNodeExecutorService("isDoneMethod");
        Future future = executor.submit(task);
        assertResult(future, ExecutorServiceTestSupport.BasicTestCallable.RESULT);
    }

    /**
     * Test for the issue 129.
     * Repeatedly runs tasks and check for isDone() status after get().
     */
    @Test
    public void testIsDoneMethod2() throws Exception {
        ExecutorService executor = createSingleNodeExecutorService("isDoneMethod2");
        for (int i = 0; i < (ExecutorServiceTest.TASK_COUNT); i++) {
            Callable<String> task1 = new ExecutorServiceTestSupport.BasicTestCallable();
            Callable<String> task2 = new ExecutorServiceTestSupport.BasicTestCallable();
            Future future1 = executor.submit(task1);
            Future future2 = executor.submit(task2);
            assertResult(future2, ExecutorServiceTestSupport.BasicTestCallable.RESULT);
            assertResult(future1, ExecutorServiceTestSupport.BasicTestCallable.RESULT);
        }
    }

    /**
     * Test multiple Future.get() invocation
     */
    @Test
    public void testMultipleFutureGets() throws Exception {
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        ExecutorService executor = createSingleNodeExecutorService("isTwoGetFromFuture");
        Future<String> future = executor.submit(task);
        assertResult(future, ExecutorServiceTestSupport.BasicTestCallable.RESULT);
        assertResult(future, ExecutorServiceTestSupport.BasicTestCallable.RESULT);
        assertResult(future, ExecutorServiceTestSupport.BasicTestCallable.RESULT);
        assertResult(future, ExecutorServiceTestSupport.BasicTestCallable.RESULT);
    }

    @Test
    public void testIssue292() {
        ExecutorServiceTestSupport.CountingDownExecutionCallback<Member> callback = new ExecutorServiceTestSupport.CountingDownExecutionCallback<Member>(1);
        createSingleNodeExecutorService("testIssue292").submit(new ExecutorServiceTestSupport.MemberCheck(), callback);
        HazelcastTestSupport.assertOpenEventually(callback.getLatch());
        Assert.assertTrue(((callback.getResult()) instanceof Member));
    }

    /**
     * Execute a task that is executing something else inside.
     * Nested Execution.
     */
    @Test
    public void testNestedExecution() {
        Callable<String> task = new ExecutorServiceTestSupport.NestedExecutorTask();
        ExecutorService executor = createSingleNodeExecutorService("testNestedExecution");
        Future future = executor.submit(task);
        HazelcastTestSupport.assertCompletesEventually(future);
    }

    /**
     * invokeAll tests
     */
    @Test
    public void testInvokeAll() throws Exception {
        ExecutorService executor = createSingleNodeExecutorService("testInvokeAll");
        Assert.assertFalse(executor.isShutdown());
        // only one task
        ArrayList<Callable<String>> tasks = new ArrayList<Callable<String>>();
        tasks.add(new ExecutorServiceTestSupport.BasicTestCallable());
        List<Future<String>> futures = executor.invokeAll(tasks);
        Assert.assertEquals(futures.size(), 1);
        Assert.assertEquals(futures.get(0).get(), ExecutorServiceTestSupport.BasicTestCallable.RESULT);
        // more tasks
        tasks.clear();
        for (int i = 0; i < (ExecutorServiceTest.TASK_COUNT); i++) {
            tasks.add(new ExecutorServiceTestSupport.BasicTestCallable());
        }
        futures = executor.invokeAll(tasks);
        Assert.assertEquals(futures.size(), ExecutorServiceTest.TASK_COUNT);
        for (int i = 0; i < (ExecutorServiceTest.TASK_COUNT); i++) {
            Assert.assertEquals(futures.get(i).get(), ExecutorServiceTestSupport.BasicTestCallable.RESULT);
        }
    }

    @Test
    public void testInvokeAllTimeoutCancelled() throws Exception {
        ExecutorService executor = createSingleNodeExecutorService("testInvokeAll");
        Assert.assertFalse(executor.isShutdown());
        // only one task
        ArrayList<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        tasks.add(new ExecutorServiceTestSupport.SleepingTask(0));
        List<Future<Boolean>> futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
        Assert.assertEquals(futures.size(), 1);
        Assert.assertEquals(futures.get(0).get(), Boolean.TRUE);
        // more tasks
        tasks.clear();
        for (int i = 0; i < (ExecutorServiceTest.TASK_COUNT); i++) {
            tasks.add(new ExecutorServiceTestSupport.SleepingTask((i < 2 ? 0 : 20)));
        }
        futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
        Assert.assertEquals(futures.size(), ExecutorServiceTest.TASK_COUNT);
        for (int i = 0; i < (ExecutorServiceTest.TASK_COUNT); i++) {
            if (i < 2) {
                Assert.assertEquals(futures.get(i).get(), Boolean.TRUE);
            } else {
                boolean excepted = false;
                try {
                    futures.get(i).get();
                } catch (CancellationException e) {
                    excepted = true;
                }
                Assert.assertTrue(excepted);
            }
        }
    }

    @Test
    public void testInvokeAllTimeoutSuccess() throws Exception {
        ExecutorService executor = createSingleNodeExecutorService("testInvokeAll");
        Assert.assertFalse(executor.isShutdown());
        // only one task
        ArrayList<Callable<String>> tasks = new ArrayList<Callable<String>>();
        tasks.add(new ExecutorServiceTestSupport.BasicTestCallable());
        List<Future<String>> futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
        Assert.assertEquals(futures.size(), 1);
        Assert.assertEquals(futures.get(0).get(), ExecutorServiceTestSupport.BasicTestCallable.RESULT);
        // more tasks
        tasks.clear();
        for (int i = 0; i < (ExecutorServiceTest.TASK_COUNT); i++) {
            tasks.add(new ExecutorServiceTestSupport.BasicTestCallable());
        }
        futures = executor.invokeAll(tasks, 15, TimeUnit.SECONDS);
        Assert.assertEquals(futures.size(), ExecutorServiceTest.TASK_COUNT);
        for (int i = 0; i < (ExecutorServiceTest.TASK_COUNT); i++) {
            Assert.assertEquals(futures.get(i).get(), ExecutorServiceTestSupport.BasicTestCallable.RESULT);
        }
    }

    /**
     * Shutdown-related method behaviour when the cluster is running
     */
    @Test
    public void testShutdownBehaviour() {
        ExecutorService executor = createSingleNodeExecutorService("testShutdownBehaviour");
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
     * Shutting down the cluster should act as the ExecutorService shutdown
     */
    @Test(expected = RejectedExecutionException.class)
    public void test_whenClusterShutdown_thenNewTasksShouldBeRejected() {
        ExecutorService executor = createSingleNodeExecutorService("testClusterShutdownTaskRejection");
        shutdownNodeFactory();
        HazelcastTestSupport.sleepSeconds(2);
        Assert.assertNotNull(executor);
        Assert.assertTrue(executor.isShutdown());
        Assert.assertTrue(executor.isTerminated());
        // new tasks must be rejected
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        executor.submit(task);
    }

    @Test
    public void testClusterShutdown_whenMultipleNodes_thenAllExecutorsAreShutdown() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        final ExecutorService es1 = instance1.getExecutorService("testClusterShutdown");
        final ExecutorService es2 = instance1.getExecutorService("testClusterShutdown");
        Assert.assertFalse(es1.isTerminated());
        Assert.assertFalse(es2.isTerminated());
        // we shutdown the ExecutorService on the first instance
        es1.shutdown();
        // the ExecutorService on the second instance should be shutdown via a ShutdownOperation
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertTrue(es2.isTerminated());
            }
        });
    }

    @Test
    public void testStatsIssue2039() throws Exception {
        Config config = new Config();
        String name = "testStatsIssue2039";
        config.addExecutorConfig(new ExecutorConfig(name).setQueueCapacity(1).setPoolSize(1));
        HazelcastInstance instance = createHazelcastInstance(config);
        IExecutorService executorService = instance.getExecutorService(name);
        executorService.execute(new ExecutorServiceTest.SleepLatchRunnable());
        HazelcastTestSupport.assertOpenEventually(ExecutorServiceTest.SleepLatchRunnable.startLatch, 30);
        Future waitingInQueue = executorService.submit(new ExecutorServiceTest.EmptyRunnable());
        Future rejected = executorService.submit(new ExecutorServiceTest.EmptyRunnable());
        try {
            rejected.get(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            boolean isRejected = (e.getCause()) instanceof RejectedExecutionException;
            if (!isRejected) {
                Assert.fail(e.toString());
            }
        } finally {
            ExecutorServiceTest.SleepLatchRunnable.sleepLatch.countDown();
        }
        waitingInQueue.get(1, TimeUnit.MINUTES);
        LocalExecutorStats stats = executorService.getLocalExecutorStats();
        Assert.assertEquals(2, stats.getStartedTaskCount());
        Assert.assertEquals(0, stats.getPendingTaskCount());
    }

    @Test
    public void testExecutorServiceStats() throws Exception {
        int i = 10;
        LocalExecutorStats stats = executeTasksForStats("testExecutorServiceStats", i, true);
        Assert.assertEquals((i + 1), stats.getStartedTaskCount());
        Assert.assertEquals(i, stats.getCompletedTaskCount());
        Assert.assertEquals(0, stats.getPendingTaskCount());
        Assert.assertEquals(1, stats.getCancelledTaskCount());
    }

    @Test
    public void testExecutorServiceStats_whenStatsAreDisabled() throws Exception {
        LocalExecutorStats stats = executeTasksForStats("testExecutorServiceStats_whenStatsDisabled", 10, false);
        Assert.assertEquals(0, stats.getStartedTaskCount());
        Assert.assertEquals(0, stats.getCompletedTaskCount());
        Assert.assertEquals(0, stats.getPendingTaskCount());
        Assert.assertEquals(0, stats.getCancelledTaskCount());
    }

    @Test
    public void testLongRunningCallable() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        Config config = new Config();
        long callTimeoutMillis = 4000;
        config.setProperty(OPERATION_CALL_TIMEOUT_MILLIS.getName(), String.valueOf(callTimeoutMillis));
        HazelcastInstance hz1 = factory.newHazelcastInstance(config);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        IExecutorService executor = hz1.getExecutorService("test");
        Future<Boolean> future = executor.submitToMember(new ExecutorServiceTestSupport.SleepingTask(((TimeUnit.MILLISECONDS.toSeconds(callTimeoutMillis)) * 3)), hz2.getCluster().getLocalMember());
        Boolean result = future.get(1, TimeUnit.MINUTES);
        Assert.assertTrue(result);
    }

    static class ICountDownLatchAwaitCallable implements HazelcastInstanceAware , Serializable , Callable<Boolean> {
        private String name;

        private HazelcastInstance instance;

        ICountDownLatchAwaitCallable(String name) {
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

    static class SleepLatchRunnable implements Serializable , Runnable {
        static CountDownLatch startLatch;

        static CountDownLatch sleepLatch;

        SleepLatchRunnable() {
            ExecutorServiceTest.SleepLatchRunnable.startLatch = new CountDownLatch(1);
            ExecutorServiceTest.SleepLatchRunnable.sleepLatch = new CountDownLatch(1);
        }

        @Override
        public void run() {
            ExecutorServiceTest.SleepLatchRunnable.startLatch.countDown();
            HazelcastTestSupport.assertOpenEventually(ExecutorServiceTest.SleepLatchRunnable.sleepLatch);
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

    @Test(expected = HazelcastSerializationException.class)
    public void testUnserializableResponse_exceptionPropagatesToCaller() throws Throwable {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        IExecutorService service = instance1.getExecutorService("executor");
        ExecutorServiceTest.TaskWithUnserialazableResponse counterCallable = new ExecutorServiceTest.TaskWithUnserialazableResponse();
        Future future = service.submitToMember(counterCallable, instance2.getCluster().getLocalMember());
        try {
            future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = HazelcastSerializationException.class)
    public void testUnserializableResponse_exceptionPropagatesToCallerCallback() throws Throwable {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        IExecutorService service = instance1.getExecutorService("executor");
        ExecutorServiceTest.TaskWithUnserialazableResponse counterCallable = new ExecutorServiceTest.TaskWithUnserialazableResponse();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference<Throwable> throwable = new AtomicReference<Throwable>();
        service.submitToMember(counterCallable, instance2.getCluster().getLocalMember(), new ExecutionCallback() {
            @Override
            public void onResponse(Object response) {
            }

            @Override
            public void onFailure(Throwable t) {
                throwable.set(t);
                countDownLatch.countDown();
            }
        });
        HazelcastTestSupport.assertOpenEventually(countDownLatch);
        throw throwable.get();
    }

    private static class TaskWithUnserialazableResponse implements Serializable , Callable {
        @Override
        public Object call() throws Exception {
            return new Object();
        }
    }
}

