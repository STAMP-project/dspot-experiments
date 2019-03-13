/**
 * Copyright 2014 The gRPC Authors
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
package io.grpc.internal;


import SharedResourceHolder.DESTROY_DELAY_SECONDS;
import io.grpc.internal.SharedResourceHolder.Resource;
import java.util.LinkedList;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Unit tests for {@link SharedResourceHolder}.
 */
@RunWith(JUnit4.class)
public class SharedResourceHolderTest {
    private final LinkedList<SharedResourceHolderTest.MockScheduledFuture<?>> scheduledDestroyTasks = new LinkedList<>();

    private SharedResourceHolder holder;

    private static class ResourceInstance {
        volatile boolean closed;
    }

    private static class ResourceFactory implements Resource<SharedResourceHolderTest.ResourceInstance> {
        @Override
        public SharedResourceHolderTest.ResourceInstance create() {
            return new SharedResourceHolderTest.ResourceInstance();
        }

        @Override
        public void close(SharedResourceHolderTest.ResourceInstance instance) {
            instance.closed = true;
        }
    }

    // Defines two kinds of resources
    private static final Resource<SharedResourceHolderTest.ResourceInstance> SHARED_FOO = new SharedResourceHolderTest.ResourceFactory();

    private static final Resource<SharedResourceHolderTest.ResourceInstance> SHARED_BAR = new SharedResourceHolderTest.ResourceFactory();

    @Test
    public void destroyResourceWhenRefCountReachesZero() {
        SharedResourceHolderTest.ResourceInstance foo1 = holder.getInternal(SharedResourceHolderTest.SHARED_FOO);
        SharedResourceHolderTest.ResourceInstance sharedFoo = foo1;
        SharedResourceHolderTest.ResourceInstance foo2 = holder.getInternal(SharedResourceHolderTest.SHARED_FOO);
        Assert.assertSame(sharedFoo, foo2);
        SharedResourceHolderTest.ResourceInstance bar1 = holder.getInternal(SharedResourceHolderTest.SHARED_BAR);
        SharedResourceHolderTest.ResourceInstance sharedBar = bar1;
        foo2 = holder.releaseInternal(SharedResourceHolderTest.SHARED_FOO, foo2);
        // foo refcount not reached 0, thus shared foo is not closed
        Assert.assertTrue(scheduledDestroyTasks.isEmpty());
        Assert.assertFalse(sharedFoo.closed);
        Assert.assertNull(foo2);
        foo1 = holder.releaseInternal(SharedResourceHolderTest.SHARED_FOO, foo1);
        Assert.assertNull(foo1);
        // foo refcount has reached 0, a destroying task is scheduled
        Assert.assertEquals(1, scheduledDestroyTasks.size());
        SharedResourceHolderTest.MockScheduledFuture<?> scheduledDestroyTask = scheduledDestroyTasks.poll();
        Assert.assertEquals(DESTROY_DELAY_SECONDS, scheduledDestroyTask.getDelay(TimeUnit.SECONDS));
        // Simluate that the destroyer executes the foo destroying task
        scheduledDestroyTask.runTask();
        Assert.assertTrue(sharedFoo.closed);
        // After the destroying, obtaining a foo will get a different instance
        SharedResourceHolderTest.ResourceInstance foo3 = holder.getInternal(SharedResourceHolderTest.SHARED_FOO);
        Assert.assertNotSame(sharedFoo, foo3);
        bar1 = holder.releaseInternal(SharedResourceHolderTest.SHARED_BAR, bar1);
        // bar refcount has reached 0, a destroying task is scheduled
        Assert.assertEquals(1, scheduledDestroyTasks.size());
        scheduledDestroyTask = scheduledDestroyTasks.poll();
        Assert.assertEquals(DESTROY_DELAY_SECONDS, scheduledDestroyTask.getDelay(TimeUnit.SECONDS));
        // Simulate that the destroyer executes the bar destroying task
        scheduledDestroyTask.runTask();
        Assert.assertTrue(sharedBar.closed);
    }

    @Test
    public void cancelDestroyTask() {
        SharedResourceHolderTest.ResourceInstance foo1 = holder.getInternal(SharedResourceHolderTest.SHARED_FOO);
        SharedResourceHolderTest.ResourceInstance sharedFoo = foo1;
        foo1 = holder.releaseInternal(SharedResourceHolderTest.SHARED_FOO, foo1);
        // A destroying task for foo is scheduled
        SharedResourceHolderTest.MockScheduledFuture<?> scheduledDestroyTask = scheduledDestroyTasks.poll();
        Assert.assertFalse(scheduledDestroyTask.cancelled);
        // obtaining a foo before the destroying task is executed will cancel the destroy
        SharedResourceHolderTest.ResourceInstance foo2 = holder.getInternal(SharedResourceHolderTest.SHARED_FOO);
        Assert.assertTrue(scheduledDestroyTask.cancelled);
        Assert.assertTrue(scheduledDestroyTasks.isEmpty());
        Assert.assertFalse(sharedFoo.closed);
        // And it will be the same foo instance
        Assert.assertSame(sharedFoo, foo2);
        // Release it and the destroying task is scheduled again
        foo2 = holder.releaseInternal(SharedResourceHolderTest.SHARED_FOO, foo2);
        scheduledDestroyTask = scheduledDestroyTasks.poll();
        Assert.assertFalse(scheduledDestroyTask.cancelled);
        scheduledDestroyTask.runTask();
        Assert.assertTrue(sharedFoo.closed);
    }

    @Test
    public void releaseWrongInstance() {
        SharedResourceHolderTest.ResourceInstance uncached = new SharedResourceHolderTest.ResourceInstance();
        try {
            holder.releaseInternal(SharedResourceHolderTest.SHARED_FOO, uncached);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        SharedResourceHolderTest.ResourceInstance cached = holder.getInternal(SharedResourceHolderTest.SHARED_FOO);
        try {
            holder.releaseInternal(SharedResourceHolderTest.SHARED_FOO, uncached);
            Assert.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        holder.releaseInternal(SharedResourceHolderTest.SHARED_FOO, cached);
    }

    @Test
    public void overreleaseInstance() {
        SharedResourceHolderTest.ResourceInstance foo1 = holder.getInternal(SharedResourceHolderTest.SHARED_FOO);
        holder.releaseInternal(SharedResourceHolderTest.SHARED_FOO, foo1);
        try {
            holder.releaseInternal(SharedResourceHolderTest.SHARED_FOO, foo1);
            Assert.fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    private class MockExecutorFactory implements SharedResourceHolder.ScheduledExecutorFactory {
        @Override
        public ScheduledExecutorService createScheduledExecutor() {
            ScheduledExecutorService mockExecutor = Mockito.mock(ScheduledExecutorService.class);
            Mockito.when(mockExecutor.schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenAnswer(new Answer<SharedResourceHolderTest.MockScheduledFuture<Void>>() {
                @Override
                public SharedResourceHolderTest.MockScheduledFuture<Void> answer(InvocationOnMock invocation) {
                    Object[] args = invocation.getArguments();
                    Runnable command = ((Runnable) (args[0]));
                    long delay = ((Long) (args[1]));
                    TimeUnit unit = ((TimeUnit) (args[2]));
                    SharedResourceHolderTest.MockScheduledFuture<Void> future = new SharedResourceHolderTest.MockScheduledFuture<>(command, delay, unit);
                    scheduledDestroyTasks.add(future);
                    return future;
                }
            });
            return mockExecutor;
        }
    }

    private static class MockScheduledFuture<V> implements ScheduledFuture<V> {
        private boolean cancelled;

        private boolean finished;

        final Runnable command;

        final long delay;

        final TimeUnit unit;

        MockScheduledFuture(Runnable command, long delay, TimeUnit unit) {
            this.command = command;
            this.delay = delay;
            this.unit = unit;
        }

        void runTask() {
            command.run();
            finished = true;
        }

        @Override
        public boolean cancel(boolean interrupt) {
            if ((cancelled) || (finished)) {
                return false;
            }
            cancelled = true;
            return true;
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public long getDelay(TimeUnit targetUnit) {
            return targetUnit.convert(this.delay, this.unit);
        }

        @Override
        public int compareTo(Delayed o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDone() {
            return (cancelled) || (finished);
        }

        @Override
        public V get() {
            throw new UnsupportedOperationException();
        }

        @Override
        public V get(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }
    }
}

