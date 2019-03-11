/**
 * Copyright 2015 The Netty Project
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
package io.netty.buffer;


import PooledByteBufAllocator.DEFAULT;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class PooledByteBufAllocatorTest extends AbstractByteBufAllocatorTest<PooledByteBufAllocator> {
    @Test
    public void testPooledUnsafeHeapBufferAndUnsafeDirectBuffer() {
        PooledByteBufAllocator allocator = newAllocator(true);
        ByteBuf directBuffer = allocator.directBuffer();
        AbstractByteBufAllocatorTest.assertInstanceOf(directBuffer, (PlatformDependent.hasUnsafe() ? PooledUnsafeDirectByteBuf.class : PooledDirectByteBuf.class));
        directBuffer.release();
        ByteBuf heapBuffer = allocator.heapBuffer();
        AbstractByteBufAllocatorTest.assertInstanceOf(heapBuffer, (PlatformDependent.hasUnsafe() ? PooledUnsafeHeapByteBuf.class : PooledHeapByteBuf.class));
        heapBuffer.release();
    }

    @Test
    public void testWithoutUseCacheForAllThreads() {
        Assert.assertFalse(((Thread.currentThread()) instanceof FastThreadLocalThread));
        PooledByteBufAllocator pool = /* preferDirect= */
        /* nHeapArena= */
        /* nDirectArena= */
        /* pageSize= */
        /* maxOrder= */
        /* tinyCacheSize= */
        /* smallCacheSize= */
        /* normalCacheSize= */
        /* useCacheForAllThreads= */
        new PooledByteBufAllocator(false, 1, 1, 8192, 11, 0, 0, 0, false);
        ByteBuf buf = pool.buffer(1);
        buf.release();
    }

    @Test
    public void testArenaMetricsNoCache() {
        PooledByteBufAllocatorTest.testArenaMetrics0(new PooledByteBufAllocator(true, 2, 2, 8192, 11, 0, 0, 0), 100, 0, 100, 100);
    }

    @Test
    public void testArenaMetricsCache() {
        PooledByteBufAllocatorTest.testArenaMetrics0(new PooledByteBufAllocator(true, 2, 2, 8192, 11, 1000, 1000, 1000), 100, 1, 1, 0);
    }

    @Test
    public void testArenaMetricsNoCacheAlign() {
        Assume.assumeTrue(PooledByteBufAllocator.isDirectMemoryCacheAlignmentSupported());
        PooledByteBufAllocatorTest.testArenaMetrics0(new PooledByteBufAllocator(true, 2, 2, 8192, 11, 0, 0, 0, true, 64), 100, 0, 100, 100);
    }

    @Test
    public void testArenaMetricsCacheAlign() {
        Assume.assumeTrue(PooledByteBufAllocator.isDirectMemoryCacheAlignmentSupported());
        PooledByteBufAllocatorTest.testArenaMetrics0(new PooledByteBufAllocator(true, 2, 2, 8192, 11, 1000, 1000, 1000, true, 64), 100, 1, 1, 0);
    }

    @Test
    public void testPoolChunkListMetric() {
        for (PoolArenaMetric arenaMetric : DEFAULT.metric().heapArenas()) {
            PooledByteBufAllocatorTest.assertPoolChunkListMetric(arenaMetric);
        }
    }

    @Test
    public void testSmallSubpageMetric() {
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(true, 1, 1, 8192, 11, 0, 0, 0);
        ByteBuf buffer = allocator.heapBuffer(500);
        try {
            PoolArenaMetric metric = allocator.metric().heapArenas().get(0);
            PoolSubpageMetric subpageMetric = metric.smallSubpages().get(0);
            Assert.assertEquals(1, ((subpageMetric.maxNumElements()) - (subpageMetric.numAvailable())));
        } finally {
            buffer.release();
        }
    }

    @Test
    public void testTinySubpageMetric() {
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(true, 1, 1, 8192, 11, 0, 0, 0);
        ByteBuf buffer = allocator.heapBuffer(1);
        try {
            PoolArenaMetric metric = allocator.metric().heapArenas().get(0);
            PoolSubpageMetric subpageMetric = metric.tinySubpages().get(0);
            Assert.assertEquals(1, ((subpageMetric.maxNumElements()) - (subpageMetric.numAvailable())));
        } finally {
            buffer.release();
        }
    }

    @Test
    public void testAllocNotNull() {
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(true, 1, 1, 8192, 11, 0, 0, 0);
        // Huge allocation
        PooledByteBufAllocatorTest.testAllocNotNull(allocator, ((allocator.metric().chunkSize()) + 1));
        // Normal allocation
        PooledByteBufAllocatorTest.testAllocNotNull(allocator, 1024);
        // Small allocation
        PooledByteBufAllocatorTest.testAllocNotNull(allocator, 512);
        // Tiny allocation
        PooledByteBufAllocatorTest.testAllocNotNull(allocator, 1);
    }

    @Test
    public void testFreePoolChunk() {
        int chunkSize = (16 * 1024) * 1024;
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(true, 1, 0, 8192, 11, 0, 0, 0);
        ByteBuf buffer = allocator.heapBuffer(chunkSize);
        List<PoolArenaMetric> arenas = allocator.metric().heapArenas();
        Assert.assertEquals(1, arenas.size());
        List<PoolChunkListMetric> lists = arenas.get(0).chunkLists();
        Assert.assertEquals(6, lists.size());
        Assert.assertFalse(lists.get(0).iterator().hasNext());
        Assert.assertFalse(lists.get(1).iterator().hasNext());
        Assert.assertFalse(lists.get(2).iterator().hasNext());
        Assert.assertFalse(lists.get(3).iterator().hasNext());
        Assert.assertFalse(lists.get(4).iterator().hasNext());
        // Must end up in the 6th PoolChunkList
        Assert.assertTrue(lists.get(5).iterator().hasNext());
        Assert.assertTrue(buffer.release());
        // Should be completely removed and so all PoolChunkLists must be empty
        Assert.assertFalse(lists.get(0).iterator().hasNext());
        Assert.assertFalse(lists.get(1).iterator().hasNext());
        Assert.assertFalse(lists.get(2).iterator().hasNext());
        Assert.assertFalse(lists.get(3).iterator().hasNext());
        Assert.assertFalse(lists.get(4).iterator().hasNext());
        Assert.assertFalse(lists.get(5).iterator().hasNext());
    }

    @Test(timeout = 4000)
    public void testThreadCacheDestroyedByThreadCleaner() throws InterruptedException {
        PooledByteBufAllocatorTest.testThreadCacheDestroyed(false);
    }

    @Test(timeout = 4000)
    public void testThreadCacheDestroyedAfterExitRun() throws InterruptedException {
        PooledByteBufAllocatorTest.testThreadCacheDestroyed(true);
    }

    @Test(timeout = 3000)
    public void testNumThreadCachesWithNoDirectArenas() throws InterruptedException {
        int numHeapArenas = 1;
        final PooledByteBufAllocator allocator = new PooledByteBufAllocator(numHeapArenas, 0, 8192, 1);
        PooledByteBufAllocatorTest.ThreadCache tcache0 = PooledByteBufAllocatorTest.createNewThreadCache(allocator);
        Assert.assertEquals(1, allocator.metric().numThreadLocalCaches());
        PooledByteBufAllocatorTest.ThreadCache tcache1 = PooledByteBufAllocatorTest.createNewThreadCache(allocator);
        Assert.assertEquals(2, allocator.metric().numThreadLocalCaches());
        tcache0.destroy();
        Assert.assertEquals(1, allocator.metric().numThreadLocalCaches());
        tcache1.destroy();
        Assert.assertEquals(0, allocator.metric().numThreadLocalCaches());
    }

    @Test(timeout = 3000)
    public void testThreadCacheToArenaMappings() throws InterruptedException {
        int numArenas = 2;
        final PooledByteBufAllocator allocator = new PooledByteBufAllocator(numArenas, numArenas, 8192, 1);
        PooledByteBufAllocatorTest.ThreadCache tcache0 = PooledByteBufAllocatorTest.createNewThreadCache(allocator);
        PooledByteBufAllocatorTest.ThreadCache tcache1 = PooledByteBufAllocatorTest.createNewThreadCache(allocator);
        Assert.assertEquals(2, allocator.metric().numThreadLocalCaches());
        Assert.assertEquals(1, allocator.metric().heapArenas().get(0).numThreadCaches());
        Assert.assertEquals(1, allocator.metric().heapArenas().get(1).numThreadCaches());
        Assert.assertEquals(1, allocator.metric().directArenas().get(0).numThreadCaches());
        Assert.assertEquals(1, allocator.metric().directArenas().get(0).numThreadCaches());
        tcache1.destroy();
        Assert.assertEquals(1, allocator.metric().numThreadLocalCaches());
        Assert.assertEquals(1, allocator.metric().heapArenas().get(0).numThreadCaches());
        Assert.assertEquals(0, allocator.metric().heapArenas().get(1).numThreadCaches());
        Assert.assertEquals(1, allocator.metric().directArenas().get(0).numThreadCaches());
        Assert.assertEquals(0, allocator.metric().directArenas().get(1).numThreadCaches());
        PooledByteBufAllocatorTest.ThreadCache tcache2 = PooledByteBufAllocatorTest.createNewThreadCache(allocator);
        Assert.assertEquals(2, allocator.metric().numThreadLocalCaches());
        Assert.assertEquals(1, allocator.metric().heapArenas().get(0).numThreadCaches());
        Assert.assertEquals(1, allocator.metric().heapArenas().get(1).numThreadCaches());
        Assert.assertEquals(1, allocator.metric().directArenas().get(0).numThreadCaches());
        Assert.assertEquals(1, allocator.metric().directArenas().get(1).numThreadCaches());
        tcache0.destroy();
        Assert.assertEquals(1, allocator.metric().numThreadLocalCaches());
        tcache2.destroy();
        Assert.assertEquals(0, allocator.metric().numThreadLocalCaches());
        Assert.assertEquals(0, allocator.metric().heapArenas().get(0).numThreadCaches());
        Assert.assertEquals(0, allocator.metric().heapArenas().get(1).numThreadCaches());
        Assert.assertEquals(0, allocator.metric().directArenas().get(0).numThreadCaches());
        Assert.assertEquals(0, allocator.metric().directArenas().get(1).numThreadCaches());
    }

    private interface ThreadCache {
        void destroy() throws InterruptedException;
    }

    @Test
    public void testConcurrentUsage() throws Throwable {
        long runningTime = TimeUnit.MILLISECONDS.toNanos(SystemPropertyUtil.getLong("io.netty.buffer.PooledByteBufAllocatorTest.testConcurrentUsageTime", 15000));
        // We use no caches and only one arena to maximize the chance of hitting the race-condition we
        // had before.
        ByteBufAllocator allocator = new PooledByteBufAllocator(true, 1, 1, 8192, 11, 0, 0, 0);
        List<PooledByteBufAllocatorTest.AllocationThread> threads = new ArrayList<PooledByteBufAllocatorTest.AllocationThread>();
        try {
            for (int i = 0; i < 512; i++) {
                PooledByteBufAllocatorTest.AllocationThread thread = new PooledByteBufAllocatorTest.AllocationThread(allocator);
                thread.start();
                threads.add(thread);
            }
            long start = System.nanoTime();
            while (!(PooledByteBufAllocatorTest.isExpired(start, runningTime))) {
                PooledByteBufAllocatorTest.checkForErrors(threads);
                Thread.sleep(100);
            } 
        } finally {
            // First mark all AllocationThreads to complete their work and then wait until these are complete
            // and rethrow if there was any error.
            for (PooledByteBufAllocatorTest.AllocationThread t : threads) {
                t.markAsFinished();
            }
            for (PooledByteBufAllocatorTest.AllocationThread t : threads) {
                t.joinAndCheckForError();
            }
        }
    }

    private static final class AllocationThread extends Thread {
        private static final int[] ALLOCATION_SIZES = new int[16 * 1024];

        static {
            for (int i = 0; i < (PooledByteBufAllocatorTest.AllocationThread.ALLOCATION_SIZES.length); i++) {
                PooledByteBufAllocatorTest.AllocationThread.ALLOCATION_SIZES[i] = i;
            }
        }

        private final Queue<ByteBuf> buffers = new ConcurrentLinkedQueue<ByteBuf>();

        private final ByteBufAllocator allocator;

        private final AtomicReference<Object> finish = new AtomicReference<Object>();

        AllocationThread(ByteBufAllocator allocator) {
            this.allocator = allocator;
        }

        @Override
        public void run() {
            try {
                int idx = 0;
                while ((finish.get()) == null) {
                    for (int i = 0; i < 10; i++) {
                        buffers.add(allocator.directBuffer(PooledByteBufAllocatorTest.AllocationThread.ALLOCATION_SIZES[Math.abs(((idx++) % (PooledByteBufAllocatorTest.AllocationThread.ALLOCATION_SIZES.length)))], Integer.MAX_VALUE));
                    }
                    releaseBuffers();
                } 
            } catch (Throwable cause) {
                finish.set(cause);
            } finally {
                releaseBuffers();
            }
        }

        private void releaseBuffers() {
            for (; ;) {
                ByteBuf buf = buffers.poll();
                if (buf == null) {
                    break;
                }
                buf.release();
            }
        }

        boolean isFinished() {
            return (finish.get()) != null;
        }

        void markAsFinished() {
            finish.compareAndSet(null, Boolean.TRUE);
        }

        void joinAndCheckForError() throws Throwable {
            try {
                // Mark as finish if not already done but ensure we not override the previous set error.
                join();
            } finally {
                releaseBuffers();
            }
            checkForError();
        }

        void checkForError() throws Throwable {
            Object obj = finish.get();
            if (obj instanceof Throwable) {
                throw ((Throwable) (obj));
            }
        }
    }
}

