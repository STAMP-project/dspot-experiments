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
package com.hazelcast.spi.impl;


import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AbstractInvocationFuture_GetWithTimeoutTest extends AbstractInvocationFuture_AbstractTest {
    @Test(expected = NullPointerException.class)
    public void whenNullTimeunit() throws Exception {
        future.get(1, null);
    }

    @Test
    public void whenZeroTimeout_butResponseAvailable() throws Exception {
        complete(value);
        Future getFuture = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return future.get(0, TimeUnit.SECONDS);
            }
        });
        HazelcastTestSupport.assertCompletesEventually(getFuture);
        Assert.assertSame(value, getFuture.get());
    }

    @Test
    public void whenZeroTimeout_andNoResponseAvailable() throws Exception {
        Future getFuture = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return future.get(0, TimeUnit.SECONDS);
            }
        });
        HazelcastTestSupport.assertCompletesEventually(getFuture);
        try {
            getFuture.get();
            Assert.fail();
        } catch (ExecutionException e) {
            HazelcastTestSupport.assertInstanceOf(TimeoutException.class, e.getCause());
        }
        // we need to make sure the thread is removed from the waiters.
        Assert.assertSame(AbstractInvocationFuture.VOID, getState());
    }

    @Test
    public void whenResponseAvailable() throws Exception {
        complete(value);
        Future getFuture = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return future.get(10, TimeUnit.SECONDS);
            }
        });
        HazelcastTestSupport.assertCompletesEventually(getFuture);
        Assert.assertSame(value, getFuture.get());
    }

    @Test
    public void whenResultAlreadyAvailable_andInterruptFlagSet() throws Exception {
        complete(value);
        final AtomicBoolean interrupted = new AtomicBoolean();
        Future getFuture = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                // we set the interrupt flag.
                Thread.currentThread().interrupt();
                Object value = future.get(1, TimeUnit.SECONDS);
                // and then we check if the interrupt flag is still set
                interrupted.set(Thread.currentThread().isInterrupted());
                return value;
            }
        });
        HazelcastTestSupport.assertCompletesEventually(getFuture);
        Assert.assertSame(value, get());
        Assert.assertTrue(interrupted.get());
    }

    @Test
    public void whenResponseAvailableAfterSomeDelay() throws Exception {
        Future getFuture = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return future.get(60, TimeUnit.SECONDS);
            }
        });
        // wait till the thread is registered.
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertNotSame(AbstractInvocationFuture.VOID, getState());
            }
        });
        complete(value);
        HazelcastTestSupport.assertCompletesEventually(getFuture);
        Assert.assertSame(value, getFuture.get());
    }

    @Test
    public void whenTimeout() throws InterruptedException, ExecutionException {
        Future getFuture = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return future.get(1, TimeUnit.SECONDS);
            }
        });
        HazelcastTestSupport.assertCompletesEventually(getFuture);
        try {
            getFuture.get();
            Assert.fail();
        } catch (ExecutionException e) {
            HazelcastTestSupport.assertInstanceOf(TimeoutException.class, e.getCause());
        }
        // we need to make sure the thread is removed from the waiters.
        Assert.assertSame(AbstractInvocationFuture.VOID, getState());
    }

    @Test
    public void whenInterruptedWhileWaiting() throws Exception {
        final AtomicReference<Thread> thread = new AtomicReference<Thread>();
        final AtomicBoolean interrupted = new AtomicBoolean();
        Future getFuture = HazelcastTestSupport.spawn(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                thread.set(Thread.currentThread());
                try {
                    return future.get(1, TimeUnit.HOURS);
                } finally {
                    interrupted.set(Thread.currentThread().isInterrupted());
                }
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertNotSame(AbstractInvocationFuture.VOID, getState());
            }
        });
        HazelcastTestSupport.sleepSeconds(5);
        thread.get().interrupt();
        HazelcastTestSupport.assertCompletesEventually(getFuture);
        Assert.assertTrue(interrupted.get());
        try {
            get();
            Assert.fail();
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void whenMultipleGetters() throws Exception {
        List<Future> getFutures = new LinkedList<Future>();
        for (int k = 0; k < 10; k++) {
            getFutures.add(HazelcastTestSupport.spawn(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return future.get(1, TimeUnit.DAYS);
                }
            }));
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertNotSame(AbstractInvocationFuture.VOID, getState());
            }
        });
        HazelcastTestSupport.sleepSeconds(5);
        complete(value);
        for (Future getFuture : getFutures) {
            HazelcastTestSupport.assertCompletesEventually(getFuture);
            Assert.assertSame(value, get());
        }
        Assert.assertSame(value, getState());
    }

    @Test
    public void unregister() {
        for (int length = 1; length <= 5; length++) {
            for (int position = 0; position < length; position++) {
                unregister(length, position);
            }
        }
    }
}

