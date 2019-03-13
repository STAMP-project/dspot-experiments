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
package com.hazelcast.scheduledexecutor;


import com.hazelcast.spi.impl.executionservice.impl.DelegatingTaskScheduler;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DelegatingScheduledFutureStripperTest {
    private ScheduledExecutorService scheduler;

    private DelegatingTaskScheduler taskScheduler;

    @Test(expected = NullPointerException.class)
    public void constructWithNull() {
        new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Object>(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    @SuppressWarnings("ConstantConditions")
    public void compareTo() {
        ScheduledFuture<Integer> future = new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Integer>(scheduler.schedule(new DelegatingScheduledFutureStripperTest.SimpleCallableTestTask(), 0, TimeUnit.SECONDS));
        future.compareTo(null);
    }

    @Test
    public void getDelay() {
        ScheduledFuture<Integer> future = new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Integer>(scheduler.schedule(new DelegatingScheduledFutureStripperTest.SimpleCallableTestTask(), 0, TimeUnit.SECONDS));
        Assert.assertEquals(0, future.getDelay(TimeUnit.SECONDS));
        future = new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Integer>(scheduler.schedule(new DelegatingScheduledFutureStripperTest.SimpleCallableTestTask(), 10, TimeUnit.SECONDS));
        Assert.assertEquals(10, future.getDelay(TimeUnit.SECONDS), 1);
    }

    @Test
    public void cancel() throws Exception {
        ScheduledFuture<Object> outer = DelegatingScheduledFutureStripperTest.createScheduledFutureMock();
        ScheduledFuture<Object> inner = DelegatingScheduledFutureStripperTest.createScheduledFutureMock();
        Mockito.when(outer.get()).thenReturn(inner);
        new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Object>(outer).cancel(true);
        Mockito.verify(inner).cancel(ArgumentMatchers.eq(true));
    }

    @Test
    public void cancel_twice() {
        ScheduledFuture<Future<Integer>> original = taskScheduler.schedule(new DelegatingScheduledFutureStripperTest.SimpleCallableTestTask(), 10, TimeUnit.SECONDS);
        ScheduledFuture stripper = new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Future<Integer>>(original);
        stripper.cancel(true);
        stripper.cancel(true);
    }

    @Test
    public void isDone() throws Exception {
        ScheduledFuture<Object> outer = DelegatingScheduledFutureStripperTest.createScheduledFutureMock();
        ScheduledFuture<Object> inner = DelegatingScheduledFutureStripperTest.createScheduledFutureMock();
        Mockito.when(outer.get()).thenReturn(inner);
        Mockito.when(outer.isDone()).thenReturn(true);
        Mockito.when(inner.isDone()).thenReturn(false);
        Assert.assertFalse(new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Object>(outer).isDone());
        Mockito.when(outer.isDone()).thenReturn(true);
        Mockito.when(inner.isDone()).thenReturn(true);
        Assert.assertTrue(new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Object>(outer).isDone());
    }

    @Test
    public void isCancelled() throws Exception {
        ScheduledFuture<Object> outer = DelegatingScheduledFutureStripperTest.createScheduledFutureMock();
        ScheduledFuture<Object> inner = DelegatingScheduledFutureStripperTest.createScheduledFutureMock();
        Mockito.when(outer.get()).thenReturn(inner);
        Mockito.when(outer.isCancelled()).thenReturn(false);
        Mockito.when(inner.isCancelled()).thenReturn(false);
        Assert.assertFalse(new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Object>(outer).isCancelled());
        Mockito.when(outer.isCancelled()).thenReturn(true);
        Mockito.when(inner.isCancelled()).thenReturn(false);
        Assert.assertTrue(new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Object>(outer).isCancelled());
        Mockito.when(outer.isCancelled()).thenReturn(false);
        Mockito.when(inner.isCancelled()).thenReturn(true);
        Assert.assertTrue(new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Object>(outer).isCancelled());
    }

    @Test
    public void get() throws Exception {
        ScheduledFuture<Future<Integer>> original = taskScheduler.schedule(new DelegatingScheduledFutureStripperTest.SimpleCallableTestTask(), 0, TimeUnit.SECONDS);
        ScheduledFuture stripper = new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Future<Integer>>(original);
        Assert.assertNotNull(original.get());
        Assert.assertEquals(5, stripper.get());
    }

    @Test(expected = InterruptedException.class)
    public void get_interrupted() throws Exception {
        ScheduledFuture<Object> outer = DelegatingScheduledFutureStripperTest.createScheduledFutureMock();
        ScheduledFuture<Object> inner = DelegatingScheduledFutureStripperTest.createScheduledFutureMock();
        Mockito.when(outer.get()).thenThrow(new InterruptedException());
        Mockito.when(inner.get()).thenReturn(2);
        new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Object>(outer).get();
    }

    @Test(expected = ExecutionException.class)
    public void get_executionExc() throws Exception {
        ScheduledFuture<Object> outer = DelegatingScheduledFutureStripperTest.createScheduledFutureMock();
        ScheduledFuture<Object> inner = DelegatingScheduledFutureStripperTest.createScheduledFutureMock();
        Mockito.when(outer.get()).thenThrow(new ExecutionException(new NullPointerException()));
        Mockito.when(inner.get()).thenReturn(2);
        new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Object>(outer).get();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void get_unsupported() throws Exception {
        ScheduledFuture<Integer> future = scheduler.schedule(new DelegatingScheduledFutureStripperTest.SimpleCallableTestTask(), 0, TimeUnit.SECONDS);
        new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Integer>(future).get(1, TimeUnit.SECONDS);
    }

    @Test
    public void equals() {
        ScheduledFuture<Future<Integer>> original = taskScheduler.schedule(new DelegatingScheduledFutureStripperTest.SimpleCallableTestTask(), 0, TimeUnit.SECONDS);
        ScheduledFuture<Future<Integer>> joker = taskScheduler.schedule(new DelegatingScheduledFutureStripperTest.SimpleCallableTestTask(), 1, TimeUnit.SECONDS);
        ScheduledFuture testA = new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Future<Integer>>(original);
        ScheduledFuture testB = new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Future<Integer>>(original);
        ScheduledFuture testC = new com.hazelcast.scheduledexecutor.impl.DelegatingScheduledFutureStripper<Future<Integer>>(joker);
        Assert.assertNotNull(testA);
        Assert.assertEquals(testA, testA);
        Assert.assertEquals(testA, testB);
        Assert.assertNotEquals(testA, testC);
    }

    private static class SimpleCallableTestTask implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            return 5;
        }
    }

    private static class SimpleRunnableTestTask implements Runnable {
        @Override
        public void run() {
        }
    }
}

