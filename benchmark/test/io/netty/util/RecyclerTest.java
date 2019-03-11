/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util;


import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class RecyclerTest {
    @Test(timeout = 5000L)
    public void testThreadCanBeCollectedEvenIfHandledObjectIsReferenced() throws Exception {
        final Recycler<RecyclerTest.HandledObject> recycler = RecyclerTest.newRecycler(1024);
        final AtomicBoolean collected = new AtomicBoolean();
        final AtomicReference<RecyclerTest.HandledObject> reference = new AtomicReference<RecyclerTest.HandledObject>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                RecyclerTest.HandledObject object = recycler.get();
                reference.set(object);
            }
        }) {
            @Override
            protected void finalize() throws Throwable {
                super.finalize();
                collected.set(true);
            }
        };
        Assert.assertFalse(collected.get());
        thread.start();
        thread.join();
        // Null out so it can be collected.
        thread = null;
        // Loop until the Thread was collected. If we can not collect it the Test will fail due of a timeout.
        while (!(collected.get())) {
            System.gc();
            System.runFinalization();
            Thread.sleep(50);
        } 
        // Now call recycle after the Thread was collected to ensure this still works...
        reference.getAndSet(null).recycle();
    }

    @Test(expected = IllegalStateException.class)
    public void testMultipleRecycle() {
        Recycler<RecyclerTest.HandledObject> recycler = RecyclerTest.newRecycler(1024);
        RecyclerTest.HandledObject object = recycler.get();
        object.recycle();
        object.recycle();
    }

    @Test(expected = IllegalStateException.class)
    public void testMultipleRecycleAtDifferentThread() throws InterruptedException {
        Recycler<RecyclerTest.HandledObject> recycler = RecyclerTest.newRecycler(1024);
        final RecyclerTest.HandledObject object = recycler.get();
        final AtomicReference<IllegalStateException> exceptionStore = new AtomicReference<IllegalStateException>();
        final Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                object.recycle();
            }
        });
        thread1.start();
        thread1.join();
        final Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    object.recycle();
                } catch (IllegalStateException e) {
                    exceptionStore.set(e);
                }
            }
        });
        thread2.start();
        thread2.join();
        IllegalStateException exception = exceptionStore.get();
        if (exception != null) {
            throw exception;
        }
    }

    @Test
    public void testRecycle() {
        Recycler<RecyclerTest.HandledObject> recycler = RecyclerTest.newRecycler(1024);
        RecyclerTest.HandledObject object = recycler.get();
        object.recycle();
        RecyclerTest.HandledObject object2 = recycler.get();
        Assert.assertSame(object, object2);
        object2.recycle();
    }

    @Test
    public void testRecycleDisable() {
        Recycler<RecyclerTest.HandledObject> recycler = RecyclerTest.newRecycler((-1));
        RecyclerTest.HandledObject object = recycler.get();
        object.recycle();
        RecyclerTest.HandledObject object2 = recycler.get();
        Assert.assertNotSame(object, object2);
        object2.recycle();
    }

    /**
     * Test to make sure bug #2848 never happens again
     * https://github.com/netty/netty/issues/2848
     */
    @Test
    public void testMaxCapacity() {
        RecyclerTest.testMaxCapacity(300);
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            RecyclerTest.testMaxCapacity(((rand.nextInt(1000)) + 256));// 256 - 1256

        }
    }

    @Test
    public void testRecycleAtDifferentThread() throws Exception {
        final Recycler<RecyclerTest.HandledObject> recycler = new Recycler<RecyclerTest.HandledObject>(256, 10, 2, 10) {
            @Override
            protected RecyclerTest.HandledObject newObject(Recycler.Handle<RecyclerTest.HandledObject> handle) {
                return new RecyclerTest.HandledObject(handle);
            }
        };
        final RecyclerTest.HandledObject o = recycler.get();
        final RecyclerTest.HandledObject o2 = recycler.get();
        final Thread thread = new Thread() {
            @Override
            public void run() {
                o.recycle();
                o2.recycle();
            }
        };
        thread.start();
        thread.join();
        Assert.assertSame(recycler.get(), o);
        Assert.assertNotSame(recycler.get(), o2);
    }

    @Test
    public void testMaxCapacityWithRecycleAtDifferentThread() throws Exception {
        final int maxCapacity = 4;// Choose the number smaller than WeakOrderQueue.LINK_CAPACITY

        final Recycler<RecyclerTest.HandledObject> recycler = RecyclerTest.newRecycler(maxCapacity);
        // Borrow 2 * maxCapacity objects.
        // Return the half from the same thread.
        // Return the other half from the different thread.
        final RecyclerTest.HandledObject[] array = new RecyclerTest.HandledObject[maxCapacity * 3];
        for (int i = 0; i < (array.length); i++) {
            array[i] = recycler.get();
        }
        for (int i = 0; i < maxCapacity; i++) {
            array[i].recycle();
        }
        final Thread thread = new Thread() {
            @Override
            public void run() {
                for (int i = maxCapacity; i < (array.length); i++) {
                    array[i].recycle();
                }
            }
        };
        thread.start();
        thread.join();
        Assert.assertEquals(maxCapacity, recycler.threadLocalCapacity());
        Assert.assertEquals(1, recycler.threadLocalSize());
        for (int i = 0; i < (array.length); i++) {
            recycler.get();
        }
        Assert.assertEquals(maxCapacity, recycler.threadLocalCapacity());
        Assert.assertEquals(0, recycler.threadLocalSize());
    }

    @Test
    public void testDiscardingExceedingElementsWithRecycleAtDifferentThread() throws Exception {
        final int maxCapacity = 32;
        final AtomicInteger instancesCount = new AtomicInteger(0);
        final Recycler<RecyclerTest.HandledObject> recycler = new Recycler<RecyclerTest.HandledObject>(maxCapacity, 2) {
            @Override
            protected RecyclerTest.HandledObject newObject(Recycler.Handle<RecyclerTest.HandledObject> handle) {
                instancesCount.incrementAndGet();
                return new RecyclerTest.HandledObject(handle);
            }
        };
        // Borrow 2 * maxCapacity objects.
        final RecyclerTest.HandledObject[] array = new RecyclerTest.HandledObject[maxCapacity * 2];
        for (int i = 0; i < (array.length); i++) {
            array[i] = recycler.get();
        }
        Assert.assertEquals(array.length, instancesCount.get());
        // Reset counter.
        instancesCount.set(0);
        // Recycle from other thread.
        final Thread thread = new Thread() {
            @Override
            public void run() {
                for (RecyclerTest.HandledObject object : array) {
                    object.recycle();
                }
            }
        };
        thread.start();
        thread.join();
        Assert.assertEquals(0, instancesCount.get());
        // Borrow 2 * maxCapacity objects. Half of them should come from
        // the recycler queue, the other half should be freshly allocated.
        for (int i = 0; i < (array.length); i++) {
            recycler.get();
        }
        // The implementation uses maxCapacity / 2 as limit per WeakOrderQueue
        Assert.assertTrue(((((((("The instances count (" + (instancesCount.get())) + ") must be <= array.length (") + (array.length)) + ") - maxCapacity (") + maxCapacity) + ") / 2 as we not pool all new handles") + " internally"), (((array.length) - (maxCapacity / 2)) <= (instancesCount.get())));
    }

    static final class HandledObject {
        Recycler.Handle<RecyclerTest.HandledObject> handle;

        HandledObject(Recycler.Handle<RecyclerTest.HandledObject> handle) {
            this.handle = handle;
        }

        void recycle() {
            handle.recycle(this);
        }
    }
}

