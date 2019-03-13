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
package org.apache.hadoop.hbase.regionserver;


import ChunkCreator.instance;
import MemStoreLAB.CHUNK_POOL_MAXSIZE_KEY;
import MemStoreLAB.POOL_INITIAL_SIZE_DEFAULT;
import MemStoreLABImpl.CHUNK_SIZE_KEY;
import MemStoreLABImpl.MAX_ALLOC_DEFAULT;
import java.lang.management.ManagementFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ByteBufferKeyValue;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MultithreadedTestUtil;
import org.apache.hadoop.hbase.io.util.MemorySizeUtil;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Iterables;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.apache.hbase.thirdparty.com.google.common.collect.Maps;
import org.apache.hbase.thirdparty.com.google.common.primitives.Ints;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ChunkCreator.instance;
import static MemStoreLABImpl.MAX_ALLOC_DEFAULT;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestMemStoreLAB {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMemStoreLAB.class);

    private static final Configuration conf = new Configuration();

    private static final byte[] rk = Bytes.toBytes("r1");

    private static final byte[] cf = Bytes.toBytes("f");

    private static final byte[] q = Bytes.toBytes("q");

    /**
     * Test a bunch of random allocations
     */
    @Test
    public void testLABRandomAllocation() {
        Random rand = new Random();
        MemStoreLAB mslab = new MemStoreLABImpl();
        int expectedOff = 0;
        ByteBuffer lastBuffer = null;
        int lastChunkId = -1;
        // 100K iterations by 0-1K alloc -> 50MB expected
        // should be reasonable for unit test and also cover wraparound
        // behavior
        for (int i = 0; i < 100000; i++) {
            int valSize = rand.nextInt(3);
            KeyValue kv = new KeyValue(TestMemStoreLAB.rk, TestMemStoreLAB.cf, TestMemStoreLAB.q, new byte[valSize]);
            int size = kv.getSerializedSize();
            ByteBufferKeyValue newKv = ((ByteBufferKeyValue) (mslab.copyCellInto(kv)));
            if ((newKv.getBuffer()) != lastBuffer) {
                // since we add the chunkID at the 0th offset of the chunk and the
                // chunkid is an int we need to account for those 4 bytes
                expectedOff = Bytes.SIZEOF_INT;
                lastBuffer = newKv.getBuffer();
                int chunkId = newKv.getBuffer().getInt(0);
                Assert.assertTrue("chunkid should be different", (chunkId != lastChunkId));
                lastChunkId = chunkId;
            }
            Assert.assertEquals(expectedOff, newKv.getOffset());
            Assert.assertTrue("Allocation overruns buffer", (((newKv.getOffset()) + size) <= (newKv.getBuffer().capacity())));
            expectedOff += size;
        }
    }

    @Test
    public void testLABLargeAllocation() {
        MemStoreLAB mslab = new MemStoreLABImpl();
        KeyValue kv = new KeyValue(TestMemStoreLAB.rk, TestMemStoreLAB.cf, TestMemStoreLAB.q, new byte[(2 * 1024) * 1024]);
        Cell newCell = mslab.copyCellInto(kv);
        Assert.assertNull("2MB allocation shouldn't be satisfied by LAB.", newCell);
    }

    /**
     * Test allocation from lots of threads, making sure the results don't
     * overlap in any way
     */
    @Test
    public void testLABThreading() throws Exception {
        Configuration conf = new Configuration();
        MultithreadedTestUtil.TestContext ctx = new MultithreadedTestUtil.TestContext(conf);
        final AtomicInteger totalAllocated = new AtomicInteger();
        final MemStoreLAB mslab = new MemStoreLABImpl();
        List<List<TestMemStoreLAB.AllocRecord>> allocations = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            final List<TestMemStoreLAB.AllocRecord> allocsByThisThread = Lists.newLinkedList();
            allocations.add(allocsByThisThread);
            MultithreadedTestUtil.TestThread t = new MultithreadedTestUtil.RepeatingTestThread(ctx) {
                private Random r = new Random();

                @Override
                public void doAnAction() throws Exception {
                    int valSize = r.nextInt(3);
                    KeyValue kv = new KeyValue(TestMemStoreLAB.rk, TestMemStoreLAB.cf, TestMemStoreLAB.q, new byte[valSize]);
                    int size = kv.getSerializedSize();
                    ByteBufferKeyValue newCell = ((ByteBufferKeyValue) (mslab.copyCellInto(kv)));
                    totalAllocated.addAndGet(size);
                    allocsByThisThread.add(new TestMemStoreLAB.AllocRecord(newCell.getBuffer(), newCell.getOffset(), size));
                }
            };
            ctx.addThread(t);
        }
        ctx.startThreads();
        while (((totalAllocated.get()) < ((50 * 1024) * 1000)) && (ctx.shouldRun())) {
            Thread.sleep(10);
        } 
        ctx.stop();
        // Partition the allocations by the actual byte[] they point into,
        // make sure offsets are unique for each chunk
        Map<ByteBuffer, Map<Integer, TestMemStoreLAB.AllocRecord>> mapsByChunk = Maps.newHashMap();
        int sizeCounted = 0;
        for (TestMemStoreLAB.AllocRecord rec : Iterables.concat(allocations)) {
            sizeCounted += rec.size;
            if ((rec.size) == 0)
                continue;

            Map<Integer, TestMemStoreLAB.AllocRecord> mapForThisByteArray = mapsByChunk.get(rec.alloc);
            if (mapForThisByteArray == null) {
                mapForThisByteArray = Maps.newTreeMap();
                mapsByChunk.put(rec.alloc, mapForThisByteArray);
            }
            TestMemStoreLAB.AllocRecord oldVal = mapForThisByteArray.put(rec.offset, rec);
            Assert.assertNull(((("Already had an entry " + oldVal) + " for allocation ") + rec), oldVal);
        }
        Assert.assertEquals("Sanity check test", sizeCounted, totalAllocated.get());
        // Now check each byte array to make sure allocations don't overlap
        for (Map<Integer, TestMemStoreLAB.AllocRecord> allocsInChunk : mapsByChunk.values()) {
            // since we add the chunkID at the 0th offset of the chunk and the
            // chunkid is an int we need to account for those 4 bytes
            int expectedOff = Bytes.SIZEOF_INT;
            for (TestMemStoreLAB.AllocRecord alloc : allocsInChunk.values()) {
                Assert.assertEquals(expectedOff, alloc.offset);
                Assert.assertTrue("Allocation overruns buffer", (((alloc.offset) + (alloc.size)) <= (alloc.alloc.capacity())));
                expectedOff += alloc.size;
            }
        }
    }

    /**
     * Test frequent chunk retirement with chunk pool triggered by lots of threads, making sure
     * there's no memory leak (HBASE-16195)
     *
     * @throws Exception
     * 		if any error occurred
     */
    @Test
    public void testLABChunkQueue() throws Exception {
        ChunkCreator oldInstance = null;
        try {
            MemStoreLABImpl mslab = new MemStoreLABImpl();
            // by default setting, there should be no chunks initialized in the pool
            Assert.assertTrue(mslab.getPooledChunks().isEmpty());
            oldInstance = instance;
            instance = null;
            // reset mslab with chunk pool
            Configuration conf = HBaseConfiguration.create();
            conf.setDouble(CHUNK_POOL_MAXSIZE_KEY, 0.1);
            // set chunk size to default max alloc size, so we could easily trigger chunk retirement
            conf.setLong(CHUNK_SIZE_KEY, MAX_ALLOC_DEFAULT);
            // reconstruct mslab
            long globalMemStoreLimit = ((long) ((ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()) * (MemorySizeUtil.getGlobalMemStoreHeapPercent(conf, false))));
            ChunkCreator.initialize(MAX_ALLOC_DEFAULT, false, globalMemStoreLimit, 0.1F, POOL_INITIAL_SIZE_DEFAULT, null);
            ChunkCreator.clearDisableFlag();
            mslab = new MemStoreLABImpl(conf);
            // launch multiple threads to trigger frequent chunk retirement
            List<Thread> threads = new ArrayList<>();
            final KeyValue kv = new KeyValue(Bytes.toBytes("r"), Bytes.toBytes("f"), Bytes.toBytes("q"), new byte[(MAX_ALLOC_DEFAULT) - 32]);
            for (int i = 0; i < 10; i++) {
                threads.add(getChunkQueueTestThread(mslab, ("testLABChunkQueue-" + i), kv));
            }
            for (Thread thread : threads) {
                thread.start();
            }
            // let it run for some time
            Thread.sleep(1000);
            for (Thread thread : threads) {
                thread.interrupt();
            }
            boolean threadsRunning = true;
            boolean alive = false;
            while (threadsRunning) {
                alive = false;
                for (Thread thread : threads) {
                    if (thread.isAlive()) {
                        alive = true;
                        break;
                    }
                }
                if (!alive) {
                    threadsRunning = false;
                }
            } 
            // none of the chunkIds would have been returned back
            Assert.assertTrue("All the chunks must have been cleared", ((instance.numberOfMappedChunks()) != 0));
            int pooledChunksNum = mslab.getPooledChunks().size();
            // close the mslab
            mslab.close();
            // make sure all chunks where reclaimed back to pool
            int queueLength = mslab.getNumOfChunksReturnedToPool();
            Assert.assertTrue((("All chunks in chunk queue should be reclaimed or removed" + " after mslab closed but actually: ") + (pooledChunksNum - queueLength)), ((pooledChunksNum - queueLength) == 0));
        } finally {
            instance = oldInstance;
        }
    }

    private static class AllocRecord implements Comparable<TestMemStoreLAB.AllocRecord> {
        private final ByteBuffer alloc;

        private final int offset;

        private final int size;

        public AllocRecord(ByteBuffer alloc, int offset, int size) {
            super();
            this.alloc = alloc;
            this.offset = offset;
            this.size = size;
        }

        @Override
        public int compareTo(TestMemStoreLAB.AllocRecord e) {
            if ((alloc) != (e.alloc)) {
                throw new RuntimeException("Can only compare within a particular array");
            }
            return Ints.compare(this.offset, e.offset);
        }

        @Override
        public String toString() {
            return ((("AllocRecord(offset=" + (this.offset)) + ", size=") + (size)) + ")";
        }
    }
}

