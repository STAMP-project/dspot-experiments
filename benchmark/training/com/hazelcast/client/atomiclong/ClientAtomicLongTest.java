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
package com.hazelcast.client.atomiclong;


import com.hazelcast.client.UndefinedErrorCodeException;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IFunction;
import com.hazelcast.test.ExpectedRuntimeException;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientAtomicLongTest extends HazelcastTestSupport {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    private IAtomicLong l;

    @Test
    public void test() throws Exception {
        Assert.assertEquals(0, l.getAndAdd(2));
        Assert.assertEquals(2, l.get());
        l.set(5);
        Assert.assertEquals(5, l.get());
        Assert.assertEquals(8, l.addAndGet(3));
        Assert.assertFalse(l.compareAndSet(7, 4));
        Assert.assertEquals(8, l.get());
        Assert.assertTrue(l.compareAndSet(8, 4));
        Assert.assertEquals(4, l.get());
        Assert.assertEquals(3, l.decrementAndGet());
        Assert.assertEquals(3, l.getAndIncrement());
        Assert.assertEquals(4, l.getAndSet(9));
        Assert.assertEquals(10, l.incrementAndGet());
    }

    @Test
    public void testAsync() throws Exception {
        ICompletableFuture<Long> future = l.getAndAddAsync(10);
        Assert.assertEquals(0, future.get().longValue());
        ICompletableFuture<Boolean> booleanFuture = l.compareAndSetAsync(10, 42);
        Assert.assertTrue(booleanFuture.get());
        future = l.getAsync();
        Assert.assertEquals(42, future.get().longValue());
        future = l.incrementAndGetAsync();
        Assert.assertEquals(43, future.get().longValue());
        future = l.addAndGetAsync((-13));
        Assert.assertEquals(30, future.get().longValue());
        future = l.alterAndGetAsync(new ClientAtomicLongTest.AddOneFunction());
        Assert.assertEquals(31, future.get().longValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void apply_whenCalledWithNullFunction() {
        IAtomicLong ref = client.getAtomicLong("apply_whenCalledWithNullFunction");
        ref.apply(null);
    }

    @Test
    public void apply() {
        IAtomicLong ref = client.getAtomicLong("apply");
        Assert.assertEquals(new Long(1), ref.apply(new ClientAtomicLongTest.AddOneFunction()));
        Assert.assertEquals(0, ref.get());
    }

    @Test
    public void applyAsync() throws InterruptedException, ExecutionException {
        IAtomicLong ref = client.getAtomicLong("apply");
        ICompletableFuture<Long> future = ref.applyAsync(new ClientAtomicLongTest.AddOneFunction());
        Assert.assertEquals(new Long(1), future.get());
        Assert.assertEquals(0, ref.get());
    }

    @Test
    public void applyBooleanAsync() throws InterruptedException, ExecutionException {
        final CountDownLatch cdl = new CountDownLatch(1);
        final IAtomicLong ref = client.getAtomicLong("apply");
        ICompletableFuture<Void> incAndGetFuture = ref.setAsync(1);
        final AtomicBoolean failed = new AtomicBoolean(true);
        incAndGetFuture.andThen(new com.hazelcast.core.ExecutionCallback<Void>() {
            @Override
            public void onResponse(Void response) {
                ICompletableFuture<Boolean> future = ref.applyAsync(new ClientAtomicLongTest.FilterOnesFunction());
                try {
                    Assert.assertEquals(Boolean.TRUE, future.get());
                    failed.set(false);
                    cdl.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
        if (cdl.await(15, TimeUnit.SECONDS)) {
            Assert.assertEquals(1, ref.get());
            Assert.assertFalse(failed.get());
        } else {
            Assert.fail("Timeout after 15 seconds");
        }
    }

    @Test
    public void apply_whenException() {
        IAtomicLong ref = client.getAtomicLong("apply_whenException");
        ref.set(1);
        try {
            ref.apply(new ClientAtomicLongTest.FailingFunction());
            Assert.fail();
        } catch (UndefinedErrorCodeException expected) {
            Assert.assertEquals(expected.getOriginClassName(), ExpectedRuntimeException.class.getName());
        }
        Assert.assertEquals(1, ref.get());
    }

    @Test
    public void applyAsync_whenException() {
        IAtomicLong ref = client.getAtomicLong("applyAsync_whenException");
        ref.set(1);
        try {
            ICompletableFuture<Long> future = ref.applyAsync(new ClientAtomicLongTest.FailingFunction());
            future.get();
        } catch (InterruptedException e) {
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertEquals(e.getCause().getClass(), UndefinedErrorCodeException.class);
            Assert.assertEquals(getOriginClassName(), ExpectedRuntimeException.class.getName());
        }
        Assert.assertEquals(1, ref.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void alter_whenCalledWithNullFunction() {
        IAtomicLong ref = client.getAtomicLong("alter_whenCalledWithNullFunction");
        ref.alter(null);
    }

    @Test
    public void alter_whenException() {
        IAtomicLong ref = client.getAtomicLong("alter_whenException");
        ref.set(10);
        try {
            ref.alter(new ClientAtomicLongTest.FailingFunction());
            Assert.fail();
        } catch (UndefinedErrorCodeException expected) {
            Assert.assertEquals(expected.getOriginClassName(), ExpectedRuntimeException.class.getName());
        }
        Assert.assertEquals(10, ref.get());
    }

    @Test
    public void alterAsync_whenException() {
        IAtomicLong ref = client.getAtomicLong("alterAsync_whenException");
        ref.set(10);
        try {
            ICompletableFuture<Void> future = ref.alterAsync(new ClientAtomicLongTest.FailingFunction());
            future.get();
        } catch (InterruptedException e) {
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertEquals(e.getCause().getClass(), UndefinedErrorCodeException.class);
            Assert.assertEquals(getOriginClassName(), ExpectedRuntimeException.class.getName());
        }
        Assert.assertEquals(10, ref.get());
    }

    @Test
    public void alter() {
        IAtomicLong ref = client.getAtomicLong("alter");
        ref.set(10);
        ref.alter(new ClientAtomicLongTest.AddOneFunction());
        Assert.assertEquals(11, ref.get());
    }

    @Test
    public void alterAsync() throws InterruptedException, ExecutionException {
        IAtomicLong ref = client.getAtomicLong("alterAsync");
        ref.set(10);
        ICompletableFuture<Void> future = ref.alterAsync(new ClientAtomicLongTest.AddOneFunction());
        future.get();
        Assert.assertEquals(11, ref.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void alterAndGet_whenCalledWithNullFunction() {
        IAtomicLong ref = client.getAtomicLong("alterAndGet_whenCalledWithNullFunction");
        ref.alterAndGet(null);
    }

    @Test
    public void alterAndGet_whenException() {
        IAtomicLong ref = client.getAtomicLong("alterAndGet_whenException");
        ref.set(10);
        try {
            ref.alterAndGet(new ClientAtomicLongTest.FailingFunction());
            Assert.fail();
        } catch (UndefinedErrorCodeException expected) {
            Assert.assertEquals(expected.getOriginClassName(), ExpectedRuntimeException.class.getName());
        }
        Assert.assertEquals(10, ref.get());
    }

    @Test
    public void alterAndGetAsync_whenException() {
        IAtomicLong ref = client.getAtomicLong("alterAndGetAsync_whenException");
        ref.set(10);
        try {
            ICompletableFuture<Long> future = ref.alterAndGetAsync(new ClientAtomicLongTest.FailingFunction());
            future.get();
        } catch (InterruptedException e) {
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertEquals(e.getCause().getClass(), UndefinedErrorCodeException.class);
            Assert.assertEquals(getOriginClassName(), ExpectedRuntimeException.class.getName());
        }
        Assert.assertEquals(10, ref.get());
    }

    @Test
    public void alterAndGet() {
        IAtomicLong ref = client.getAtomicLong("alterAndGet");
        ref.set(10);
        Assert.assertEquals(11, ref.alterAndGet(new ClientAtomicLongTest.AddOneFunction()));
        Assert.assertEquals(11, ref.get());
    }

    @Test
    public void alterAndGetAsync() throws InterruptedException, ExecutionException {
        IAtomicLong ref = client.getAtomicLong("alterAndGetAsync");
        ICompletableFuture<Void> future = ref.setAsync(10);
        future.get();
        Assert.assertEquals(11, ref.alterAndGetAsync(new ClientAtomicLongTest.AddOneFunction()).get().longValue());
        Assert.assertEquals(11, ref.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAndAlter_whenCalledWithNullFunction() {
        IAtomicLong ref = client.getAtomicLong("getAndAlter_whenCalledWithNullFunction");
        ref.getAndAlter(null);
    }

    @Test
    public void getAndAlter_whenException() {
        IAtomicLong ref = client.getAtomicLong("getAndAlter_whenException");
        ref.set(10);
        try {
            ref.getAndAlter(new ClientAtomicLongTest.FailingFunction());
            Assert.fail();
        } catch (UndefinedErrorCodeException expected) {
            Assert.assertEquals(expected.getOriginClassName(), ExpectedRuntimeException.class.getName());
        }
        Assert.assertEquals(10, ref.get());
    }

    @Test
    public void getAndAlterAsync_whenException() {
        IAtomicLong ref = client.getAtomicLong("getAndAlterAsync_whenException");
        ref.set(10);
        try {
            ICompletableFuture<Long> future = ref.getAndAlterAsync(new ClientAtomicLongTest.FailingFunction());
            future.get();
            Assert.fail();
        } catch (InterruptedException e) {
            Assert.assertEquals(e.getCause().getClass().getName(), UndefinedErrorCodeException.class.getName());
            Assert.assertEquals(getOriginClassName(), ExpectedRuntimeException.class.getName());
        } catch (ExecutionException e) {
            Assert.assertEquals(e.getCause().getClass().getName(), UndefinedErrorCodeException.class.getName());
            Assert.assertEquals(getOriginClassName(), ExpectedRuntimeException.class.getName());
        }
        Assert.assertEquals(10, ref.get());
    }

    @Test
    public void getAndAlter() {
        IAtomicLong ref = client.getAtomicLong("getAndAlter");
        ref.set(10);
        Assert.assertEquals(10, ref.getAndAlter(new ClientAtomicLongTest.AddOneFunction()));
        Assert.assertEquals(11, ref.get());
    }

    @Test
    public void getAndAlterAsync() throws InterruptedException, ExecutionException {
        IAtomicLong ref = client.getAtomicLong("getAndAlterAsync");
        ref.set(10);
        ICompletableFuture<Long> future = ref.getAndAlterAsync(new ClientAtomicLongTest.AddOneFunction());
        Assert.assertEquals(10, future.get().longValue());
        Assert.assertEquals(11, ref.get());
    }

    private static class AddOneFunction implements IFunction<Long, Long> {
        @Override
        public Long apply(Long input) {
            return input + 1;
        }
    }

    private static class FilterOnesFunction implements IFunction<Long, Boolean> {
        @Override
        public Boolean apply(Long input) {
            return input.equals(1L);
        }
    }

    private static class FailingFunction implements IFunction<Long, Long> {
        @Override
        public Long apply(Long input) {
            throw new ExpectedRuntimeException();
        }
    }
}

