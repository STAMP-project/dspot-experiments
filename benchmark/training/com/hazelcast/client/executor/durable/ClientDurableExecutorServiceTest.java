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
import com.hazelcast.client.test.executor.tasks.FailingCallable;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.durableexecutor.DurableExecutorService;
import com.hazelcast.durableexecutor.DurableExecutorServiceFuture;
import com.hazelcast.executor.ExecutorServiceTestSupport.BasicTestCallable;
import com.hazelcast.executor.ExecutorServiceTestSupport.SleepingTask;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RootCauseMatcher;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientDurableExecutorServiceTest {
    private static final String SINGLE_TASK = "singleTask";

    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private HazelcastInstance client;

    @Test
    public void testInvokeAll() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        List<BasicTestCallable> callables = Collections.emptyList();
        expectedException.expect(UnsupportedOperationException.class);
        service.invokeAll(callables);
    }

    @Test
    public void testInvokeAll_WithTimeout() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        List<BasicTestCallable> callables = Collections.emptyList();
        expectedException.expect(UnsupportedOperationException.class);
        service.invokeAll(callables, 1, TimeUnit.SECONDS);
    }

    @Test
    public void testInvokeAny() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        List<BasicTestCallable> callables = Collections.emptyList();
        expectedException.expect(UnsupportedOperationException.class);
        service.invokeAny(callables);
    }

    @Test
    public void testInvokeAny_WithTimeout() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        List<BasicTestCallable> callables = Collections.emptyList();
        expectedException.expect(UnsupportedOperationException.class);
        service.invokeAny(callables, 1, TimeUnit.SECONDS);
    }

    @Test
    public void testAwaitTermination() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        Assert.assertFalse(service.awaitTermination(1, TimeUnit.SECONDS));
    }

    @Test
    public void test_whenRingBufferIsFull_thenThrowRejectedExecutionException() throws Exception {
        String key = HazelcastTestSupport.randomString();
        DurableExecutorService service = client.getDurableExecutorService(((ClientDurableExecutorServiceTest.SINGLE_TASK) + (HazelcastTestSupport.randomString())));
        service.submitToKeyOwner(new SleepingTask(100), key);
        DurableExecutorServiceFuture<String> future = service.submitToKeyOwner(new BasicTestCallable(), key);
        expectedException.expect(new RootCauseMatcher(RejectedExecutionException.class));
        future.get();
    }

    @Test
    public void test_whenRingBufferIsFull_thenClientDurableExecutorServiceCompletedFutureIsReturned() throws Exception {
        final AtomicBoolean onResponse = new AtomicBoolean();
        final CountDownLatch onFailureLatch = new CountDownLatch(1);
        String key = HazelcastTestSupport.randomString();
        DurableExecutorService service = client.getDurableExecutorService(((ClientDurableExecutorServiceTest.SINGLE_TASK) + (HazelcastTestSupport.randomString())));
        service.submitToKeyOwner(new SleepingTask(100), key);
        DurableExecutorServiceFuture<String> future = service.submitToKeyOwner(new BasicTestCallable(), key);
        future.andThen(new com.hazelcast.core.ExecutionCallback<String>() {
            @Override
            public void onResponse(String response) {
                onResponse.set(true);
            }

            @Override
            public void onFailure(Throwable t) {
                onFailureLatch.countDown();
            }
        });
        try {
            future.get(1, TimeUnit.HOURS);
            Assert.fail("We expected that future.get() throws an ExecutionException!");
        } catch (ExecutionException ignored) {
        }
        // assert TaskId
        try {
            future.getTaskId();
            Assert.fail("We expected that future.getTaskId() throws an IllegalStateException!");
        } catch (IllegalStateException ignored) {
        }
        // assert states of ClientDurableExecutorServiceCompletedFuture
        Assert.assertFalse(future.cancel(false));
        Assert.assertFalse(future.cancel(true));
        Assert.assertFalse(future.isCancelled());
        Assert.assertTrue(future.isDone());
        // assert that onFailure() has been called and not onResponse()
        onFailureLatch.await();
        Assert.assertFalse(onResponse.get());
    }

    @Test
    public void testIsTerminated() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        Assert.assertFalse(service.isTerminated());
    }

    @Test
    public void testIsShutdown() {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        Assert.assertFalse(service.isShutdown());
    }

    @Test
    public void testShutdownNow() {
        final DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        service.shutdownNow();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            public void run() {
                Assert.assertTrue(service.isShutdown());
            }
        });
    }

    @Test
    public void testShutdownMultipleTimes() {
        final DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        service.shutdownNow();
        service.shutdown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            public void run() {
                Assert.assertTrue(service.isShutdown());
            }
        });
    }

    @Test
    public void testSubmitFailingCallableException() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        Future<String> failingFuture = service.submit(new FailingCallable());
        expectedException.expect(ExecutionException.class);
        failingFuture.get();
    }

    @Test
    public void testSubmitFailingCallableException_withExecutionCallback() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        final CountDownLatch latch = new CountDownLatch(1);
        service.submit(new FailingCallable()).andThen(new com.hazelcast.core.ExecutionCallback<String>() {
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

    @Test
    public void testSubmitFailingCallableReasonExceptionCause() throws Exception {
        DurableExecutorService service = client.getDurableExecutorService(HazelcastTestSupport.randomString());
        Future<String> future = service.submit(new FailingCallable());
        expectedException.expect(new RootCauseMatcher(IllegalStateException.class));
        future.get();
    }

    @Test
    public void testCallableSerializedOnce() throws Exception {
        String name = HazelcastTestSupport.randomString();
        DurableExecutorService service = client.getDurableExecutorService(name);
        ClientDurableExecutorServiceTest.SerializedCounterCallable counterCallable = new ClientDurableExecutorServiceTest.SerializedCounterCallable();
        Future future = service.submitToKeyOwner(counterCallable, name);
        Assert.assertEquals(2, future.get());
    }

    static class SerializedCounterCallable implements DataSerializable , Callable {
        int counter;

        public SerializedCounterCallable() {
        }

        @Override
        public Object call() {
            return counter;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeInt((++(counter)));
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            counter = (in.readInt()) + 1;
        }
    }
}

