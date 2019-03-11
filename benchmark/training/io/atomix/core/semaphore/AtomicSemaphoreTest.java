/**
 * Copyright 2018-present Open Networking Foundation
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
package io.atomix.core.semaphore;


import io.atomix.core.AbstractPrimitiveTest;
import io.atomix.core.Atomix;
import io.atomix.core.semaphore.impl.AbstractAtomicSemaphoreService;
import io.atomix.core.semaphore.impl.AtomicSemaphoreProxy;
import io.atomix.core.semaphore.impl.AtomicSemaphoreServiceConfig;
import io.atomix.primitive.PrimitiveException;
import io.atomix.storage.buffer.Buffer;
import io.atomix.storage.buffer.HeapBuffer;
import io.atomix.utils.time.Version;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Semaphore test.
 */
public class AtomicSemaphoreTest extends AbstractPrimitiveTest {
    @Test(timeout = 30000)
    public void testInit() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphore semaphore100 = atomix.atomicSemaphoreBuilder("test-semaphore-init-100").withProtocol(protocol()).withInitialCapacity(100).build();
        AtomicSemaphore semaphore0 = atomix.atomicSemaphoreBuilder("test-semaphore-init-0").withProtocol(protocol()).build();
        Assert.assertEquals(100, semaphore100.availablePermits());
        Assert.assertEquals(0, semaphore0.availablePermits());
        AtomicSemaphore semaphoreNoInit = atomix.atomicSemaphoreBuilder("test-semaphore-init-100").withProtocol(protocol()).build();
        Assert.assertEquals(100, semaphoreNoInit.availablePermits());
    }

    @Test(timeout = 30000)
    public void testAcquireRelease() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphore semaphore = atomix.atomicSemaphoreBuilder("test-semaphore-base").withProtocol(protocol()).withInitialCapacity(10).build();
        Assert.assertEquals(10, semaphore.availablePermits());
        semaphore.acquire();
        Assert.assertEquals(9, semaphore.availablePermits());
        semaphore.acquire(9);
        Assert.assertEquals(0, semaphore.availablePermits());
        semaphore.release();
        Assert.assertEquals(1, semaphore.availablePermits());
        semaphore.release(100);
        Assert.assertEquals(101, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testIncreaseReduceDrain() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphore semaphore = atomix.atomicSemaphoreBuilder("test-semaphore-ird").withProtocol(protocol()).withInitialCapacity((-10)).build();
        Assert.assertEquals((-10), semaphore.availablePermits());
        Assert.assertEquals(10, semaphore.increasePermits(20));
        Assert.assertEquals((-10), semaphore.reducePermits(20));
        Assert.assertEquals((-10), semaphore.drainPermits());
        Assert.assertEquals(0, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testOverflow() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphore semaphore = atomix.atomicSemaphoreBuilder("test-semaphore-overflow").withProtocol(protocol()).withInitialCapacity(Integer.MAX_VALUE).build();
        Assert.assertEquals(Integer.MAX_VALUE, semaphore.increasePermits(10));
        semaphore.release(10);
        Assert.assertEquals(Integer.MAX_VALUE, semaphore.availablePermits());
        Assert.assertEquals(Integer.MAX_VALUE, semaphore.drainPermits());
        semaphore.reducePermits(Integer.MAX_VALUE);
        semaphore.reducePermits(Integer.MAX_VALUE);
        Assert.assertEquals(Integer.MIN_VALUE, semaphore.availablePermits());
    }

    @Test(timeout = 10000)
    public void testTimeout() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphore semaphore = atomix.atomicSemaphoreBuilder("test-semaphore-timeout").withProtocol(protocol()).withInitialCapacity(10).build();
        Assert.assertFalse(semaphore.tryAcquire(11).isPresent());
        Assert.assertTrue(semaphore.tryAcquire(10).isPresent());
        long start = System.currentTimeMillis();
        Assert.assertFalse(semaphore.tryAcquire(Duration.ofSeconds(1)).isPresent());
        long end = System.currentTimeMillis();
        Assert.assertTrue(((end - start) >= 1000));
        Assert.assertTrue(((end - start) < 1100));
        semaphore.release();
        start = System.currentTimeMillis();
        Assert.assertTrue(semaphore.tryAcquire(Duration.ofMillis(100)).isPresent());
        end = System.currentTimeMillis();
        Assert.assertTrue(((end - start) < 100));
        CompletableFuture<Optional<Version>> future = semaphore.async().tryAcquire(Duration.ofSeconds(1));
        semaphore.release();
        Assert.assertTrue(future.get(10, TimeUnit.SECONDS).isPresent());
    }

    @Test(timeout = 30000)
    public void testReleaseSession() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphoreProxy semaphore = ((AtomicSemaphoreProxy) (atomix.atomicSemaphoreBuilder("test-semaphore-releaseSession").withProtocol(protocol()).withInitialCapacity(10).build().async()));
        AtomicSemaphoreProxy semaphore2 = ((AtomicSemaphoreProxy) (atomix.atomicSemaphoreBuilder("test-semaphore-releaseSession").withProtocol(protocol()).withInitialCapacity(10).build().async()));
        Assert.assertEquals(10, semaphore2.drainPermits().get(10, TimeUnit.SECONDS).intValue());
        Map<Long, Integer> status = semaphore.holderStatus().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(1, status.size());
        status.values().forEach(( permits) -> Assert.assertEquals(10, permits.intValue()));
        semaphore2.close().get(10, TimeUnit.SECONDS);
        Map<Long, Integer> status2 = semaphore.holderStatus().get(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, status2.size());
        Assert.assertEquals(10, semaphore.availablePermits().get(10, TimeUnit.SECONDS).intValue());
    }

    @Test(timeout = 30000)
    public void testHolderStatus() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphoreProxy semaphore = ((AtomicSemaphoreProxy) (atomix.atomicSemaphoreBuilder("test-semaphore-holders").withProtocol(protocol()).withInitialCapacity(10).build().async()));
        AtomicSemaphoreProxy semaphore2 = ((AtomicSemaphoreProxy) (atomix.atomicSemaphoreBuilder("test-semaphore-holders").withProtocol(protocol()).withInitialCapacity(10).build().async()));
        Assert.assertEquals(0, semaphore.holderStatus().get().size());
        semaphore.acquire().get(10, TimeUnit.SECONDS);
        semaphore2.acquire().get(10, TimeUnit.SECONDS);
        Map<Long, Integer> status = semaphore.holderStatus().get();
        Assert.assertEquals(2, status.size());
        status.values().forEach(( permits) -> Assert.assertEquals(1, permits.intValue()));
        semaphore.acquire().get(10, TimeUnit.SECONDS);
        semaphore2.acquire().get(10, TimeUnit.SECONDS);
        Map<Long, Integer> status2 = semaphore.holderStatus().get();
        Assert.assertEquals(2, status2.size());
        status2.values().forEach(( permits) -> Assert.assertEquals(2, permits.intValue()));
        semaphore.release(2).get(10, TimeUnit.SECONDS);
        semaphore2.release(2).get(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, semaphore.holderStatus().get().size());
    }

    @Test(timeout = 30000)
    public void testBlocking() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphore semaphore = atomix.atomicSemaphoreBuilder("test-semaphore-blocking").withProtocol(protocol()).withInitialCapacity(10).build();
        semaphore.acquire();
        Assert.assertEquals(9, semaphore.availablePermits());
        semaphore.release();
        Assert.assertEquals(10, semaphore.availablePermits());
        semaphore.increasePermits(10);
        Assert.assertEquals(20, semaphore.availablePermits());
        semaphore.reducePermits(10);
        Assert.assertEquals(10, semaphore.availablePermits());
        Assert.assertFalse(semaphore.tryAcquire(11).isPresent());
        Assert.assertFalse(semaphore.tryAcquire(11, Duration.ofMillis(1)).isPresent());
        Assert.assertTrue(semaphore.tryAcquire(5).isPresent());
        Assert.assertEquals(5, semaphore.drainPermits());
        AtomicSemaphore semaphore2 = atomix.atomicSemaphoreBuilder("test-semaphore-blocking").withProtocol(protocol()).withInitialCapacity(10).build();
        Assert.assertEquals(0, semaphore2.availablePermits());
        semaphore.close();
        Assert.assertEquals(10, semaphore2.availablePermits());
    }

    @Test(timeout = 10000)
    public void testQueue() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphore semaphore = atomix.atomicSemaphoreBuilder("test-semaphore-queue").withProtocol(protocol()).withInitialCapacity(10).build();
        CompletableFuture<Version> future20 = semaphore.async().acquire(20);
        CompletableFuture<Version> future11 = semaphore.async().acquire(11);
        semaphore.increasePermits(1);
        Assert.assertNotNull(future11);
        Assert.assertFalse(future20.isDone());
        Assert.assertEquals(0, semaphore.availablePermits());
        CompletableFuture<Version> future21 = semaphore.async().acquire(21);
        CompletableFuture<Version> future5 = semaphore.async().acquire(5);
        // wakeup 5 and 20
        semaphore.release(25);
        future5.get(10, TimeUnit.SECONDS);
        future20.get(10, TimeUnit.SECONDS);
        Assert.assertFalse(future21.isDone());
    }

    @Test(timeout = 30000)
    public void testExpire() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphore semaphore = atomix.atomicSemaphoreBuilder("test-semaphore-expire").withProtocol(protocol()).withInitialCapacity(10).build();
        Assert.assertEquals(10, semaphore.availablePermits());
        CompletableFuture<Optional<Version>> future = semaphore.async().tryAcquire(11, Duration.ofMillis(500));
        Thread.sleep(500);
        Assert.assertFalse(future.get(10, TimeUnit.SECONDS).isPresent());
        Assert.assertEquals(10, semaphore.availablePermits());
    }

    @Test(timeout = 10000)
    public void testInterrupt() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphore semaphore = atomix.atomicSemaphoreBuilder("test-semaphore-interrupt").withProtocol(protocol()).withInitialCapacity(10).build();
        AtomicSemaphore semaphore2 = atomix.atomicSemaphoreBuilder("test-semaphore-interrupt").withProtocol(protocol()).withInitialCapacity(10).build();
        AtomicBoolean interrupted = new AtomicBoolean();
        Thread t = new Thread(() -> {
            try {
                semaphore.acquire(11);
            } catch (PrimitiveException e) {
                synchronized(interrupted) {
                    interrupted.set(true);
                    interrupted.notifyAll();
                }
            }
        });
        t.start();
        synchronized(interrupted) {
            t.interrupt();
            interrupted.wait();
        }
        Assert.assertTrue(interrupted.get());
        semaphore2.increasePermits(1);
        // wait asynchronous release.
        Thread.sleep(1000);
        Assert.assertEquals(11, semaphore.availablePermits());
    }

    @Test(timeout = 30000)
    public void testBlockTimeout() throws Exception {
        Atomix atomix = atomix();
        AtomicSemaphore semaphore = atomix.atomicSemaphoreBuilder("test-semaphore-block-timeout").withProtocol(protocol()).withInitialCapacity(10).build();
        AtomicSemaphore semaphore2 = atomix.atomicSemaphoreBuilder("test-semaphore-block-timeout").withProtocol(protocol()).withInitialCapacity(10).build();
        Object timedout = new Object();
        Thread t = new Thread(() -> {
            try {
                semaphore.acquire(11);
            } catch (PrimitiveException e) {
                synchronized(timedout) {
                    timedout.notifyAll();
                }
            }
        });
        t.start();
        synchronized(timedout) {
            timedout.wait();
        }
        semaphore2.increasePermits(1);
        // wait asynchronous release.
        Thread.sleep(1000);
        Assert.assertEquals(11, semaphore.availablePermits());
    }

    @Test(timeout = 60000)
    public void testExpireRace() throws Exception {
        int testCount = 10000;
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        Atomix atomix = atomix();
        List<Future<?>> taskFuture = new ArrayList<>(threads);
        AtomicInteger acquired = new AtomicInteger();
        for (int i = 0; i < threads; i++) {
            taskFuture.add(executorService.submit(() -> {
                AtomicSemaphore semaphore = atomix.atomicSemaphoreBuilder("test-semaphore-race").withProtocol(protocol()).withInitialCapacity(testCount).build();
                while ((acquired.get()) < testCount) {
                    semaphore.tryAcquire(Duration.ofMillis(1)).ifPresent(( v) -> acquired.incrementAndGet());
                } 
            }));
        }
        for (Future<?> future : taskFuture) {
            future.get();
        }
        executorService.shutdown();
    }

    @Test
    public void testSnapshot() throws Exception {
        AbstractAtomicSemaphoreService service = new io.atomix.core.semaphore.impl.DefaultAtomicSemaphoreService(new AtomicSemaphoreServiceConfig().setInitialCapacity(10));
        Field available = AbstractAtomicSemaphoreService.class.getDeclaredField("available");
        available.setAccessible(true);
        Field holders = AbstractAtomicSemaphoreService.class.getDeclaredField("holders");
        holders.setAccessible(true);
        Field waiterQueue = AbstractAtomicSemaphoreService.class.getDeclaredField("waiterQueue");
        waiterQueue.setAccessible(true);
        Field timers = AbstractAtomicSemaphoreService.class.getDeclaredField("timers");
        timers.setAccessible(true);
        available.set(service, 10);
        Map<Long, Integer> holdersMap = new HashMap<>();
        holdersMap.put(((long) (1)), 2);
        holdersMap.put(((long) (3)), 4);
        holdersMap.put(((long) (5)), 6);
        holders.set(service, holdersMap);
        // Class<?> waiter = Class.forName("io.atomix.core.semaphore.impl.DistributedSemaphoreService$Waiter");
        // LinkedList<Object> waiterLinkedList = new LinkedList<>();
        // 
        // waiterLinkedList.add(waiter.getConstructors()[0].newInstance(service,10L, 20L, 30L, 40, Long.MAX_VALUE));
        // waiterQueue.set(service, waiterLinkedList);
        Buffer buffer = HeapBuffer.allocate();
        service.backup(new io.atomix.primitive.service.impl.DefaultBackupOutput(buffer, service.serializer()));
        AbstractAtomicSemaphoreService serviceRestore = new io.atomix.core.semaphore.impl.DefaultAtomicSemaphoreService(new AtomicSemaphoreServiceConfig().setInitialCapacity(10));
        serviceRestore.restore(new io.atomix.primitive.service.impl.DefaultBackupInput(buffer.flip(), service.serializer()));
        Assert.assertEquals(10, available.get(serviceRestore));
        Assert.assertEquals(holdersMap, holders.get(serviceRestore));
        // assertEquals(waiterQueue.get(serviceRestore), waiterLinkedList);
        // assertEquals(1, ((Map) (timers.get(serviceRestore))).keySet().size());
    }
}

