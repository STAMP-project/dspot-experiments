/**
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ethereum.datasource;


import WriteCache.CacheType;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.ethereum.datasource.inmem.HashMapDB;
import org.ethereum.db.StateSource;
import org.ethereum.mine.AnyFuture;
import org.ethereum.util.ALock;
import org.ethereum.util.Utils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing different sources and chain of sources in multi-thread environment
 */
public class MultiThreadSourcesTest {
    private class TestExecutor {
        private Source<byte[], byte[]> cache;

        boolean isCounting = false;

        boolean noDelete = false;

        boolean running = true;

        final CountDownLatch failSema = new CountDownLatch(1);

        final AtomicInteger putCnt = new AtomicInteger(1);

        final AtomicInteger delCnt = new AtomicInteger(1);

        final AtomicInteger checkCnt = new AtomicInteger(0);

        public TestExecutor(Source cache) {
            this.cache = cache;
        }

        public TestExecutor(Source cache, boolean isCounting) {
            this.cache = cache;
            this.isCounting = isCounting;
        }

        public void setNoDelete(boolean noDelete) {
            this.noDelete = noDelete;
        }

        final Thread readThread = new Thread(() -> {
            try {
                while (running) {
                    int curMax = (putCnt.get()) - 1;
                    if ((checkCnt.get()) >= curMax) {
                        Thread.sleep(10);
                        continue;
                    }
                    Assert.assertEquals(str(intToValue(curMax)), str(cache.get(intToKey(curMax))));
                    checkCnt.set(curMax);
                } 
            } catch (Throwable e) {
                e.printStackTrace();
                failSema.countDown();
            }
        });

        final Thread delThread = new Thread(() -> {
            try {
                while (running) {
                    int toDelete = delCnt.get();
                    int curMax = (putCnt.get()) - 1;
                    if ((toDelete > (checkCnt.get())) || (toDelete >= curMax)) {
                        Thread.sleep(10);
                        continue;
                    }
                    Assert.assertEquals(str(intToValue(toDelete)), str(cache.get(intToKey(toDelete))));
                    if (isCounting) {
                        for (int i = 0; i < (toDelete % 5); ++i) {
                            cache.delete(intToKey(toDelete));
                            Assert.assertEquals(str(intToValue(toDelete)), str(cache.get(intToKey(toDelete))));
                        }
                    }
                    cache.delete(intToKey(toDelete));
                    if (isCounting)
                        cache.flush();

                    Assert.assertNull(cache.get(intToKey(toDelete)));
                    delCnt.getAndIncrement();
                } 
            } catch (Throwable e) {
                e.printStackTrace();
                failSema.countDown();
            }
        });

        public void run(long timeout) {
            new Thread(() -> {
                try {
                    while (running) {
                        int curCnt = putCnt.get();
                        cache.put(intToKey(curCnt), intToValue(curCnt));
                        if (isCounting) {
                            for (int i = 0; i < (curCnt % 5); ++i) {
                                cache.put(intToKey(curCnt), intToValue(curCnt));
                            }
                        }
                        putCnt.getAndIncrement();
                        if (curCnt == 1) {
                            readThread.start();
                            if (!(noDelete)) {
                                delThread.start();
                            }
                        }
                    } 
                } catch (Throwable e) {
                    e.printStackTrace();
                    failSema.countDown();
                }
            }).start();
            new Thread(() -> {
                try {
                    while (running) {
                        Thread.sleep(10);
                        cache.flush();
                    } 
                } catch (Throwable e) {
                    e.printStackTrace();
                    failSema.countDown();
                }
            }).start();
            try {
                failSema.await(timeout, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                running = false;
                throw new RuntimeException("Thrown interrupted exception", ex);
            }
            // Shutdown carefully
            running = false;
            if ((failSema.getCount()) == 0) {
                throw new RuntimeException("Test failed.");
            } else {
                System.out.println(((("Test passed, put counter: " + (putCnt.get())) + ", delete counter: ") + (delCnt.get())));
            }
        }
    }

    private class TestExecutor1 {
        private Source<byte[], byte[]> cache;

        public int writerThreads = 4;

        public int readerThreads = 8;

        public int deleterThreads = 0;

        public int flusherThreads = 2;

        public int maxKey = 10000;

        boolean stopped;

        Map<byte[], byte[]> map = Collections.synchronizedMap(new org.ethereum.util.ByteArrayMap<byte[]>());

        ReadWriteLock rwLock = new ReentrantReadWriteLock();

        ALock rLock = new ALock(rwLock.readLock());

        ALock wLock = new ALock(rwLock.writeLock());

        public TestExecutor1(Source<byte[], byte[]> cache) {
            this.cache = cache;
        }

        class Writer implements Runnable {
            public void run() {
                Random rnd = new Random(0);
                while (!(stopped)) {
                    byte[] key = MultiThreadSourcesTest.key(rnd.nextInt(maxKey));
                    try (ALock l = wLock.lock()) {
                        map.put(key, key);
                        cache.put(key, key);
                    }
                    Utils.sleep(rnd.nextInt(1));
                } 
            }
        }

        class Reader implements Runnable {
            public void run() {
                Random rnd = new Random(0);
                while (!(stopped)) {
                    byte[] key = MultiThreadSourcesTest.key(rnd.nextInt(maxKey));
                    try (ALock l = rLock.lock()) {
                        byte[] expected = map.get(key);
                        byte[] actual = cache.get(key);
                        Assert.assertArrayEquals(expected, actual);
                    }
                } 
            }
        }

        class Deleter implements Runnable {
            public void run() {
                Random rnd = new Random(0);
                while (!(stopped)) {
                    byte[] key = MultiThreadSourcesTest.key(rnd.nextInt(maxKey));
                    try (ALock l = wLock.lock()) {
                        map.remove(key);
                        cache.delete(key);
                    }
                } 
            }
        }

        class Flusher implements Runnable {
            public void run() {
                Random rnd = new Random(0);
                while (!(stopped)) {
                    Utils.sleep(rnd.nextInt(50));
                    cache.flush();
                } 
            }
        }

        public void start(long time) throws InterruptedException, ExecutionException, TimeoutException {
            List<Callable<Object>> all = new ArrayList<>();
            for (int i = 0; i < (writerThreads); i++) {
                all.add(Executors.callable(new MultiThreadSourcesTest.TestExecutor1.Writer()));
            }
            for (int i = 0; i < (readerThreads); i++) {
                all.add(Executors.callable(new MultiThreadSourcesTest.TestExecutor1.Reader()));
            }
            for (int i = 0; i < (deleterThreads); i++) {
                all.add(Executors.callable(new MultiThreadSourcesTest.TestExecutor1.Deleter()));
            }
            for (int i = 0; i < (flusherThreads); i++) {
                all.add(Executors.callable(new MultiThreadSourcesTest.TestExecutor1.Flusher()));
            }
            ExecutorService executor = Executors.newFixedThreadPool(all.size());
            ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executor);
            AnyFuture<Object> anyFuture = new AnyFuture();
            for (Callable<Object> callable : all) {
                ListenableFuture<Object> future = listeningExecutorService.submit(callable);
                anyFuture.add(future);
            }
            try {
                anyFuture.get(time, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                System.out.println("Passed.");
            }
            stopped = true;
        }
    }

    @Test
    public void testWriteCache() throws InterruptedException {
        Source<byte[], byte[]> src = new HashMapDB();
        final WriteCache writeCache = new WriteCache.BytesKey<>(src, WriteCache.CacheType.SIMPLE);
        MultiThreadSourcesTest.TestExecutor testExecutor = new MultiThreadSourcesTest.TestExecutor(writeCache);
        testExecutor.run(5);
    }

    @Test
    public void testReadCache() throws InterruptedException {
        Source<byte[], byte[]> src = new HashMapDB();
        final ReadCache readCache = new ReadCache.BytesKey<>(src);
        MultiThreadSourcesTest.TestExecutor testExecutor = new MultiThreadSourcesTest.TestExecutor(readCache);
        testExecutor.run(5);
    }

    @Test
    public void testReadWriteCache() throws InterruptedException {
        Source<byte[], byte[]> src = new HashMapDB();
        final ReadWriteCache readWriteCache = new ReadWriteCache.BytesKey<>(src, WriteCache.CacheType.SIMPLE);
        MultiThreadSourcesTest.TestExecutor testExecutor = new MultiThreadSourcesTest.TestExecutor(readWriteCache);
        testExecutor.run(5);
    }

    @Test
    public void testAsyncWriteCache() throws InterruptedException, ExecutionException, TimeoutException {
        Source<byte[], byte[]> src = new HashMapDB();
        AsyncWriteCache<byte[], byte[]> cache = new AsyncWriteCache<byte[], byte[]>(src) {
            @Override
            protected WriteCache<byte[], byte[]> createCache(Source<byte[], byte[]> source) {
                return new WriteCache.BytesKey<byte[]>(source, CacheType.SIMPLE) {
                    @Override
                    public boolean flush() {
                        // System.out.println("Flushing started");
                        boolean ret = super.flush();
                        // System.out.println("Flushing complete");
                        return ret;
                    }
                };
            }
        };
        // TestExecutor testExecutor = new TestExecutor(cache);
        MultiThreadSourcesTest.TestExecutor1 testExecutor = new MultiThreadSourcesTest.TestExecutor1(cache);
        testExecutor.start(5);
    }

    @Test
    public void testStateSource() throws Exception {
        HashMapDB<byte[]> src = new HashMapDB();
        // LevelDbDataSource ldb = new LevelDbDataSource("test");
        // ldb.init();
        StateSource stateSource = new StateSource(src, false);
        stateSource.getReadCache().withMaxCapacity(10);
        MultiThreadSourcesTest.TestExecutor1 testExecutor = new MultiThreadSourcesTest.TestExecutor1(stateSource);
        testExecutor.start(10);
    }

    volatile int maxConcurrency = 0;

    volatile int maxWriteConcurrency = 0;

    volatile int maxReadWriteConcurrency = 0;

    @Test
    public void testStateSourceConcurrency() throws Exception {
        HashMapDB<byte[]> src = new HashMapDB<byte[]>() {
            AtomicInteger concurrentReads = new AtomicInteger(0);

            AtomicInteger concurrentWrites = new AtomicInteger(0);

            void checkConcurrency(boolean write) {
                maxConcurrency = Math.max(((concurrentReads.get()) + (concurrentWrites.get())), maxConcurrency);
                if (write) {
                    maxWriteConcurrency = Math.max(concurrentWrites.get(), maxWriteConcurrency);
                } else {
                    maxReadWriteConcurrency = Math.max(concurrentWrites.get(), maxReadWriteConcurrency);
                }
            }

            @Override
            public void put(byte[] key, byte[] val) {
                int i1 = concurrentWrites.incrementAndGet();
                checkConcurrency(true);
                super.put(key, val);
                int i2 = concurrentWrites.getAndDecrement();
            }

            @Override
            public byte[] get(byte[] key) {
                // Utils.sleep(60_000);
                int i1 = concurrentReads.incrementAndGet();
                checkConcurrency(false);
                Utils.sleep(1);
                byte[] ret = super.get(key);
                int i2 = concurrentReads.getAndDecrement();
                return ret;
            }

            @Override
            public void delete(byte[] key) {
                int i1 = concurrentWrites.incrementAndGet();
                checkConcurrency(true);
                super.delete(key);
                int i2 = concurrentWrites.getAndDecrement();
            }
        };
        final StateSource stateSource = new StateSource(src, false);
        stateSource.getReadCache().withMaxCapacity(10);
        new Thread() {
            @Override
            public void run() {
                stateSource.get(MultiThreadSourcesTest.key(1));
            }
        }.start();
        stateSource.get(MultiThreadSourcesTest.key(2));
        MultiThreadSourcesTest.TestExecutor1 testExecutor = new MultiThreadSourcesTest.TestExecutor1(stateSource);
        // testExecutor.writerThreads = 0;
        testExecutor.start(3);
        System.out.println(((((("maxConcurrency = " + (maxConcurrency)) + ", maxWriteConcurrency = ") + (maxWriteConcurrency)) + ", maxReadWriteConcurrency = ") + (maxReadWriteConcurrency)));
    }

    @Test
    public void testCountingWriteCache() throws InterruptedException {
        Source<byte[], byte[]> parentSrc = new HashMapDB();
        Source<byte[], byte[]> src = new CountingBytesSource(parentSrc);
        final WriteCache writeCache = new WriteCache.BytesKey<>(src, WriteCache.CacheType.COUNTING);
        MultiThreadSourcesTest.TestExecutor testExecutor = new MultiThreadSourcesTest.TestExecutor(writeCache, true);
        testExecutor.run(10);
    }
}

