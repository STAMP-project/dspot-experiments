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
package com.hazelcast.client.executor;


import com.hazelcast.client.executor.tasks.MapPutRunnable;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.client.test.executor.tasks.CancellationAwareTask;
import com.hazelcast.client.test.executor.tasks.FailingCallable;
import com.hazelcast.client.test.executor.tasks.SelectNoMembers;
import com.hazelcast.client.test.executor.tasks.SerializedCounterCallable;
import com.hazelcast.client.test.executor.tasks.TaskWithUnserializableResponse;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientExecutorServiceTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    private HazelcastInstance instance;

    @Test(expected = UnsupportedOperationException.class)
    public void testGetLocalExecutorStats() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        service.getLocalExecutorStats();
    }

    @Test
    public void testIsTerminated() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        Assert.assertFalse(service.isTerminated());
    }

    @Test
    public void testIsShutdown() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        Assert.assertFalse(service.isShutdown());
    }

    @Test
    public void testShutdownNow() {
        final IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        service.shutdownNow();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            public void run() {
                Assert.assertTrue(service.isShutdown());
            }
        });
    }

    @Test
    public void testShutdownMultipleTimes() {
        final IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        service.shutdownNow();
        service.shutdown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            public void run() {
                Assert.assertTrue(service.isShutdown());
            }
        });
    }

    @Test(expected = TimeoutException.class)
    public void testCancellationAwareTask_whenTimeOut() throws InterruptedException, ExecutionException, TimeoutException {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        CancellationAwareTask task = new CancellationAwareTask(Long.MAX_VALUE);
        Future future = service.submit(task);
        future.get(1, TimeUnit.SECONDS);
    }

    @Test
    public void testFutureAfterCancellationAwareTaskTimeOut() throws InterruptedException, ExecutionException {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        CancellationAwareTask task = new CancellationAwareTask(Long.MAX_VALUE);
        Future future = service.submit(task);
        try {
            future.get(1, TimeUnit.SECONDS);
        } catch (TimeoutException ignored) {
        }
        Assert.assertFalse(future.isDone());
        Assert.assertFalse(future.isCancelled());
    }

    @Test(expected = CancellationException.class)
    public void testGetFutureAfterCancel() throws InterruptedException, ExecutionException {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        CancellationAwareTask task = new CancellationAwareTask(Long.MAX_VALUE);
        Future future = service.submit(task);
        try {
            future.get(1, TimeUnit.SECONDS);
        } catch (TimeoutException ignored) {
        }
        future.cancel(true);
        future.get();
    }

    @Test(expected = ExecutionException.class)
    public void testSubmitFailingCallableException() throws InterruptedException, ExecutionException {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        Future<String> failingFuture = service.submit(new FailingCallable());
        failingFuture.get();
    }

    @Test
    public void testSubmitFailingCallableException_withExecutionCallback() throws InterruptedException {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        final CountDownLatch latch = new CountDownLatch(1);
        service.submit(new FailingCallable(), new com.hazelcast.core.ExecutionCallback<String>() {
            @Override
            public void onResponse(String response) {
            }

            @Override
            public void onFailure(Throwable t) {
                latch.countDown();
            }
        });
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test(expected = IllegalStateException.class)
    public void testSubmitFailingCallableReasonExceptionCause() throws Throwable {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        Future<String> failingFuture = service.submit(new FailingCallable());
        try {
            failingFuture.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = RejectedExecutionException.class)
    public void testExecute_withNoMemberSelected() {
        IExecutorService service = client.getExecutorService(HazelcastTestSupport.randomString());
        String mapName = HazelcastTestSupport.randomString();
        MemberSelector selector = new SelectNoMembers();
        service.execute(new MapPutRunnable(mapName), selector);
    }

    @Test
    public void testCallableSerializedOnce() throws InterruptedException, ExecutionException {
        String name = HazelcastTestSupport.randomString();
        IExecutorService service = client.getExecutorService(name);
        SerializedCounterCallable counterCallable = new SerializedCounterCallable();
        Future future = service.submitToKeyOwner(counterCallable, name);
        Assert.assertEquals(2, future.get());
    }

    @Test
    public void testCallableSerializedOnce_submitToAddress() throws InterruptedException, ExecutionException {
        String name = HazelcastTestSupport.randomString();
        IExecutorService service = client.getExecutorService(name);
        SerializedCounterCallable counterCallable = new SerializedCounterCallable();
        Future future = service.submitToMember(counterCallable, instance.getCluster().getLocalMember());
        Assert.assertEquals(2, future.get());
    }

    @Test(expected = HazelcastSerializationException.class)
    public void testUnserializableResponse_exceptionPropagatesToClient() throws Throwable {
        IExecutorService service = client.getExecutorService("executor");
        TaskWithUnserializableResponse counterCallable = new TaskWithUnserializableResponse();
        Future future = service.submit(counterCallable);
        try {
            future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = HazelcastSerializationException.class)
    public void testUnserializableResponse_exceptionPropagatesToClientCallback() throws Throwable {
        IExecutorService service = client.getExecutorService("executor");
        TaskWithUnserializableResponse counterCallable = new TaskWithUnserializableResponse();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicReference<Throwable> throwable = new AtomicReference<Throwable>();
        service.submit(counterCallable, new com.hazelcast.core.ExecutionCallback() {
            @Override
            public void onResponse(Object response) {
            }

            @Override
            public void onFailure(Throwable t) {
                throwable.set(t.getCause());
                countDownLatch.countDown();
            }
        });
        HazelcastTestSupport.assertOpenEventually(countDownLatch);
        throw throwable.get();
    }
}

