/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.util;


import ByteArrayManager.Impl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hdfs.util.ByteArrayManager.Counter;
import org.apache.hadoop.hdfs.util.ByteArrayManager.CounterMap;
import org.apache.hadoop.hdfs.util.ByteArrayManager.FixedLengthManager;
import org.apache.hadoop.hdfs.util.ByteArrayManager.ManagerMap;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ByteArrayManager.MIN_ARRAY_LENGTH;
import static java.lang.Thread.State.RUNNABLE;
import static java.lang.Thread.State.TERMINATED;
import static java.lang.Thread.State.TIMED_WAITING;
import static java.lang.Thread.State.WAITING;


/**
 * Test {@link ByteArrayManager}.
 */
public class TestByteArrayManager {
    static {
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(ByteArrayManager.class), Level.ALL);
    }

    static final Logger LOG = LoggerFactory.getLogger(TestByteArrayManager.class);

    private static final Comparator<Future<Integer>> CMP = new Comparator<Future<Integer>>() {
        @Override
        public int compare(Future<Integer> left, Future<Integer> right) {
            try {
                return (left.get().intValue()) - (right.get().intValue());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Test
    public void testCounter() throws Exception {
        final long countResetTimePeriodMs = 200L;
        final Counter c = new Counter(countResetTimePeriodMs);
        final int n = (ThreadLocalRandom.current().nextInt(512)) + 512;
        final List<Future<Integer>> futures = new ArrayList<Future<Integer>>(n);
        final ExecutorService pool = Executors.newFixedThreadPool(32);
        try {
            // increment
            for (int i = 0; i < n; i++) {
                futures.add(pool.submit(new Callable<Integer>() {
                    @Override
                    public Integer call() throws Exception {
                        return ((int) (c.increment()));
                    }
                }));
            }
            // sort and wait for the futures
            Collections.sort(futures, TestByteArrayManager.CMP);
        } finally {
            pool.shutdown();
        }
        // check futures
        Assert.assertEquals(n, futures.size());
        for (int i = 0; i < n; i++) {
            Assert.assertEquals((i + 1), futures.get(i).get().intValue());
        }
        Assert.assertEquals(n, c.getCount());
        // test auto-reset
        Thread.sleep((countResetTimePeriodMs + 100));
        Assert.assertEquals(1, c.increment());
    }

    @Test
    public void testAllocateRecycle() throws Exception {
        final int countThreshold = 4;
        final int countLimit = 8;
        final long countResetTimePeriodMs = 200L;
        final ByteArrayManager.Impl bam = new ByteArrayManager.Impl(new ByteArrayManager.Conf(countThreshold, countLimit, countResetTimePeriodMs));
        final CounterMap counters = bam.getCounters();
        final ManagerMap managers = bam.getManagers();
        final int[] uncommonArrays = new int[]{ 0, 1, 2, 4, 8, 16, 32, 64 };
        final int arrayLength = 1024;
        final TestByteArrayManager.Allocator allocator = new TestByteArrayManager.Allocator(bam);
        final TestByteArrayManager.Recycler recycler = new TestByteArrayManager.Recycler(bam);
        try {
            {
                // allocate within threshold
                for (int i = 0; i < countThreshold; i++) {
                    allocator.submit(arrayLength);
                }
                TestByteArrayManager.waitForAll(allocator.futures);
                Assert.assertEquals(countThreshold, counters.get(arrayLength, false).getCount());
                Assert.assertNull(managers.get(arrayLength, false));
                for (int n : uncommonArrays) {
                    Assert.assertNull(counters.get(n, false));
                    Assert.assertNull(managers.get(n, false));
                }
            }
            {
                // recycle half of the arrays
                for (int i = 0; i < (countThreshold / 2); i++) {
                    recycler.submit(TestByteArrayManager.removeLast(allocator.futures).get());
                }
                for (Future<Integer> f : recycler.furtures) {
                    Assert.assertEquals((-1), f.get().intValue());
                }
                recycler.furtures.clear();
            }
            {
                // allocate one more
                allocator.submit(arrayLength).get();
                Assert.assertEquals((countThreshold + 1), counters.get(arrayLength, false).getCount());
                Assert.assertNotNull(managers.get(arrayLength, false));
            }
            {
                // recycle the remaining arrays
                final int n = allocator.recycleAll(recycler);
                recycler.verify(n);
            }
            {
                // allocate until the maximum.
                for (int i = 0; i < countLimit; i++) {
                    allocator.submit(arrayLength);
                }
                TestByteArrayManager.waitForAll(allocator.futures);
                // allocate one more should be blocked
                final TestByteArrayManager.AllocatorThread t = new TestByteArrayManager.AllocatorThread(arrayLength, bam);
                t.start();
                // check if the thread is waiting, timed wait or runnable.
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(100);
                    final Thread.State threadState = t.getState();
                    if (((threadState != (RUNNABLE)) && (threadState != (WAITING))) && (threadState != (TIMED_WAITING))) {
                        Assert.fail(("threadState = " + threadState));
                    }
                }
                // recycle an array
                recycler.submit(TestByteArrayManager.removeLast(allocator.futures).get());
                Assert.assertEquals(1, TestByteArrayManager.removeLast(recycler.furtures).get().intValue());
                // check if the thread is unblocked
                Thread.sleep(100);
                Assert.assertEquals(TERMINATED, t.getState());
                // recycle the remaining, the recycle should be full.
                Assert.assertEquals((countLimit - 1), allocator.recycleAll(recycler));
                recycler.submit(t.array);
                recycler.verify(countLimit);
                // recycle one more; it should not increase the free queue size
                Assert.assertEquals(countLimit, bam.release(new byte[arrayLength]));
            }
        } finally {
            allocator.pool.shutdown();
            recycler.pool.shutdown();
        }
    }

    static class AllocatorThread extends Thread {
        private final ByteArrayManager bam;

        private final int arrayLength;

        private byte[] array;

        AllocatorThread(int arrayLength, ByteArrayManager bam) {
            this.bam = bam;
            this.arrayLength = arrayLength;
        }

        @Override
        public void run() {
            try {
                array = bam.newByteArray(arrayLength);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Allocator {
        private final ByteArrayManager bam;

        final ExecutorService pool = Executors.newFixedThreadPool(8);

        final List<Future<byte[]>> futures = new LinkedList<Future<byte[]>>();

        Allocator(ByteArrayManager bam) {
            this.bam = bam;
        }

        Future<byte[]> submit(final int arrayLength) {
            final Future<byte[]> f = pool.submit(new Callable<byte[]>() {
                @Override
                public byte[] call() throws Exception {
                    final byte[] array = bam.newByteArray(arrayLength);
                    Assert.assertEquals(arrayLength, array.length);
                    return array;
                }
            });
            futures.add(f);
            return f;
        }

        int recycleAll(TestByteArrayManager.Recycler recycler) throws Exception {
            final int n = futures.size();
            for (Future<byte[]> f : futures) {
                recycler.submit(f.get());
            }
            futures.clear();
            return n;
        }
    }

    static class Recycler {
        private final ByteArrayManager bam;

        final ExecutorService pool = Executors.newFixedThreadPool(8);

        final List<Future<Integer>> furtures = new LinkedList<Future<Integer>>();

        Recycler(ByteArrayManager bam) {
            this.bam = bam;
        }

        Future<Integer> submit(final byte[] array) {
            final Future<Integer> f = pool.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return bam.release(array);
                }
            });
            furtures.add(f);
            return f;
        }

        void verify(final int expectedSize) throws Exception {
            Assert.assertEquals(expectedSize, furtures.size());
            Collections.sort(furtures, TestByteArrayManager.CMP);
            for (int i = 0; i < (furtures.size()); i++) {
                Assert.assertEquals((i + 1), furtures.get(i).get().intValue());
            }
            furtures.clear();
        }
    }

    @Test
    public void testByteArrayManager() throws Exception {
        final int countThreshold = 32;
        final int countLimit = 64;
        final long countResetTimePeriodMs = 10000L;
        final ByteArrayManager.Impl bam = new ByteArrayManager.Impl(new ByteArrayManager.Conf(countThreshold, countLimit, countResetTimePeriodMs));
        final CounterMap counters = bam.getCounters();
        final ManagerMap managers = bam.getManagers();
        final ExecutorService pool = Executors.newFixedThreadPool(128);
        final TestByteArrayManager.Runner[] runners = new TestByteArrayManager.Runner[TestByteArrayManager.Runner.NUM_RUNNERS];
        final Thread[] threads = new Thread[runners.length];
        final int num = 1 << 10;
        for (int i = 0; i < (runners.length); i++) {
            runners[i] = new TestByteArrayManager.Runner(i, countThreshold, countLimit, pool, i, bam);
            threads[i] = runners[i].start(num);
        }
        final List<Exception> exceptions = new ArrayList<Exception>();
        final Thread randomRecycler = new Thread() {
            @Override
            public void run() {
                TestByteArrayManager.LOG.info("randomRecycler start");
                for (int i = 0; shouldRun(); i++) {
                    final int j = ThreadLocalRandom.current().nextInt(runners.length);
                    try {
                        runners[j].recycle();
                    } catch (Exception e) {
                        e.printStackTrace();
                        exceptions.add(new Exception(((this) + " has an exception"), e));
                    }
                    if ((i & 255) == 0) {
                        TestByteArrayManager.LOG.info(("randomRecycler sleep, i=" + i));
                        TestByteArrayManager.sleepMs(100);
                    }
                }
                TestByteArrayManager.LOG.info("randomRecycler done");
            }

            boolean shouldRun() {
                for (int i = 0; i < (runners.length); i++) {
                    if (threads[i].isAlive()) {
                        return true;
                    }
                    if (!(runners[i].isEmpty())) {
                        return true;
                    }
                }
                return false;
            }
        };
        randomRecycler.start();
        randomRecycler.join();
        Assert.assertTrue(exceptions.isEmpty());
        Assert.assertNull(counters.get(0, false));
        for (int i = 1; i < (runners.length); i++) {
            if (!(runners[i].assertionErrors.isEmpty())) {
                for (AssertionError e : runners[i].assertionErrors) {
                    TestByteArrayManager.LOG.error(("AssertionError " + i), e);
                }
                Assert.fail(((runners[i].assertionErrors.size()) + " AssertionError(s)"));
            }
            final int arrayLength = TestByteArrayManager.Runner.index2arrayLength(i);
            final boolean exceedCountThreshold = (counters.get(arrayLength, false).getCount()) > countThreshold;
            final FixedLengthManager m = managers.get(arrayLength, false);
            if (exceedCountThreshold) {
                Assert.assertNotNull(m);
            } else {
                Assert.assertNull(m);
            }
        }
    }

    static class Runner implements Runnable {
        static final int NUM_RUNNERS = 5;

        static int index2arrayLength(int index) {
            return (MIN_ARRAY_LENGTH) << (index - 1);
        }

        private final ByteArrayManager bam;

        final int maxArrayLength;

        final int countThreshold;

        final int maxArrays;

        final ExecutorService pool;

        final List<Future<byte[]>> arrays = new ArrayList<Future<byte[]>>();

        final AtomicInteger count = new AtomicInteger();

        final int p;

        private int n;

        final List<AssertionError> assertionErrors = new ArrayList<AssertionError>();

        Runner(int index, int countThreshold, int maxArrays, ExecutorService pool, int p, ByteArrayManager bam) {
            this.maxArrayLength = TestByteArrayManager.Runner.index2arrayLength(index);
            this.countThreshold = countThreshold;
            this.maxArrays = maxArrays;
            this.pool = pool;
            this.p = p;
            this.bam = bam;
        }

        boolean isEmpty() {
            synchronized(arrays) {
                return arrays.isEmpty();
            }
        }

        Future<byte[]> submitAllocate() {
            count.incrementAndGet();
            final Future<byte[]> f = pool.submit(new Callable<byte[]>() {
                @Override
                public byte[] call() throws Exception {
                    final int lower = ((maxArrayLength) == (ByteArrayManager.MIN_ARRAY_LENGTH)) ? 0 : (maxArrayLength) >> 1;
                    final int arrayLength = ((ThreadLocalRandom.current().nextInt(((maxArrayLength) - lower))) + lower) + 1;
                    final byte[] array = bam.newByteArray(arrayLength);
                    try {
                        Assert.assertEquals(((("arrayLength=" + arrayLength) + ", lower=") + lower), maxArrayLength, array.length);
                    } catch (AssertionError e) {
                        assertionErrors.add(e);
                    }
                    return array;
                }
            });
            synchronized(arrays) {
                arrays.add(f);
            }
            return f;
        }

        Future<byte[]> removeFirst() throws Exception {
            synchronized(arrays) {
                return TestByteArrayManager.remove(arrays, 0);
            }
        }

        void recycle() throws Exception {
            final Future<byte[]> f = removeFirst();
            if (f != null) {
                TestByteArrayManager.printf("randomRecycler: ");
                try {
                    recycle(f.get(10, TimeUnit.MILLISECONDS));
                } catch (TimeoutException e) {
                    recycle(new byte[maxArrayLength]);
                    TestByteArrayManager.printf("timeout, new byte[%d]\n", maxArrayLength);
                }
            }
        }

        int recycle(final byte[] array) {
            return bam.release(array);
        }

        Future<Integer> submitRecycle(final byte[] array) {
            count.decrementAndGet();
            final Future<Integer> f = pool.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return recycle(array);
                }
            });
            return f;
        }

        @Override
        public void run() {
            for (int i = 0; i < (n); i++) {
                final boolean isAllocate = (ThreadLocalRandom.current().nextInt(TestByteArrayManager.Runner.NUM_RUNNERS)) < (p);
                if (isAllocate) {
                    submitAllocate();
                } else {
                    try {
                        final Future<byte[]> f = removeFirst();
                        if (f != null) {
                            submitRecycle(f.get());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Assert.fail((((this) + " has ") + e));
                    }
                }
                if ((i & 255) == 0) {
                    TestByteArrayManager.sleepMs(100);
                }
            }
        }

        Thread start(int n) {
            this.n = n;
            final Thread t = new Thread(this);
            t.start();
            return t;
        }

        @Override
        public String toString() {
            return ((((getClass().getSimpleName()) + ": max=") + (maxArrayLength)) + ", count=") + (count);
        }
    }

    static class NewByteArrayWithLimit extends ByteArrayManager {
        private final int maxCount;

        private int count = 0;

        NewByteArrayWithLimit(int maxCount) {
            this.maxCount = maxCount;
        }

        @Override
        public synchronized byte[] newByteArray(int size) throws InterruptedException {
            for (; (count) >= (maxCount);) {
                wait();
            }
            (count)++;
            return new byte[size];
        }

        @Override
        public synchronized int release(byte[] array) {
            if ((count) == (maxCount)) {
                notifyAll();
            }
            (count)--;
            return 0;
        }
    }
}

