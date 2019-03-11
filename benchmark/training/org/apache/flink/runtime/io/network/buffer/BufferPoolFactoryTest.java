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
package org.apache.flink.runtime.io.network.buffer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.flink.core.memory.MemorySegment;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for the creation of {@link LocalBufferPool} instances from the {@link NetworkBufferPool}
 * factory.
 */
public class BufferPoolFactoryTest {
    private static final int numBuffers = 1024;

    private static final int memorySegmentSize = 128;

    private NetworkBufferPool networkBufferPool;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * Tests creating one buffer pool which requires more buffers than available.
     */
    @Test
    public void testRequireMoreThanPossible1() throws IOException {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Insufficient number of network buffers");
        networkBufferPool.createBufferPool(((networkBufferPool.getTotalNumberOfMemorySegments()) + 1), Integer.MAX_VALUE);
    }

    /**
     * Tests creating two buffer pools which together require more buffers than available.
     */
    @Test
    public void testRequireMoreThanPossible2() throws IOException {
        expectedException.expect(IOException.class);
        expectedException.expectMessage("Insufficient number of network buffers");
        networkBufferPool.createBufferPool((((BufferPoolFactoryTest.numBuffers) / 2) + 1), BufferPoolFactoryTest.numBuffers);
        networkBufferPool.createBufferPool((((BufferPoolFactoryTest.numBuffers) / 2) + 1), BufferPoolFactoryTest.numBuffers);
    }

    /**
     * Tests creating two buffer pools which together require as many buffers as available but where
     * there are less buffers available to the {@link NetworkBufferPool} at the time of the second
     * {@link LocalBufferPool} creation.
     */
    @Test
    public void testOverprovisioned() throws IOException {
        // note: this is also the minimum number of buffers reserved for pool2
        int buffersToTakeFromPool1 = ((BufferPoolFactoryTest.numBuffers) / 2) + 1;
        // note: this is also the minimum number of buffers reserved for pool1
        int buffersToTakeFromPool2 = (BufferPoolFactoryTest.numBuffers) - buffersToTakeFromPool1;
        List<Buffer> buffers = new ArrayList<>(BufferPoolFactoryTest.numBuffers);
        BufferPool bufferPool1 = null;
        BufferPool bufferPool2 = null;
        try {
            bufferPool1 = networkBufferPool.createBufferPool(buffersToTakeFromPool2, BufferPoolFactoryTest.numBuffers);
            // take more buffers than the minimum required
            for (int i = 0; i < buffersToTakeFromPool1; ++i) {
                Buffer buffer = bufferPool1.requestBuffer();
                Assert.assertNotNull(buffer);
                buffers.add(buffer);
            }
            Assert.assertEquals(buffersToTakeFromPool1, bufferPool1.bestEffortGetNumOfUsedBuffers());
            Assert.assertEquals(BufferPoolFactoryTest.numBuffers, bufferPool1.getNumBuffers());
            // create a second pool which requires more buffers than are available at the moment
            bufferPool2 = networkBufferPool.createBufferPool(buffersToTakeFromPool1, BufferPoolFactoryTest.numBuffers);
            Assert.assertEquals(bufferPool2.getNumberOfRequiredMemorySegments(), bufferPool2.getNumBuffers());
            Assert.assertEquals(bufferPool1.getNumberOfRequiredMemorySegments(), bufferPool1.getNumBuffers());
            Assert.assertNull(bufferPool1.requestBuffer());
            // take all remaining buffers
            for (int i = 0; i < buffersToTakeFromPool2; ++i) {
                Buffer buffer = bufferPool2.requestBuffer();
                Assert.assertNotNull(buffer);
                buffers.add(buffer);
            }
            Assert.assertEquals(buffersToTakeFromPool2, bufferPool2.bestEffortGetNumOfUsedBuffers());
            // we should be able to get one more but this is currently given out to bufferPool1 and taken by buffer1
            Assert.assertNull(bufferPool2.requestBuffer());
            // as soon as one excess buffer of bufferPool1 is recycled, it should be available for bufferPool2
            buffers.remove(0).recycleBuffer();
            // recycle returns the excess buffer to the network buffer pool
            Assert.assertEquals(1, networkBufferPool.getNumberOfAvailableMemorySegments());
            // verify the number of buffers taken from the pools
            Assert.assertEquals((buffersToTakeFromPool1 - 1), ((bufferPool1.bestEffortGetNumOfUsedBuffers()) + (bufferPool1.getNumberOfAvailableMemorySegments())));
            Assert.assertEquals(buffersToTakeFromPool2, ((bufferPool2.bestEffortGetNumOfUsedBuffers()) + (bufferPool2.getNumberOfAvailableMemorySegments())));
            Buffer buffer = bufferPool2.requestBuffer();
            Assert.assertNotNull(buffer);
            buffers.add(buffer);
            // verify the number of buffers taken from the pools
            Assert.assertEquals(0, networkBufferPool.getNumberOfAvailableMemorySegments());
            Assert.assertEquals((buffersToTakeFromPool1 - 1), ((bufferPool1.bestEffortGetNumOfUsedBuffers()) + (bufferPool1.getNumberOfAvailableMemorySegments())));
            Assert.assertEquals((buffersToTakeFromPool2 + 1), ((bufferPool2.bestEffortGetNumOfUsedBuffers()) + (bufferPool2.getNumberOfAvailableMemorySegments())));
        } finally {
            for (Buffer buffer : buffers) {
                buffer.recycleBuffer();
            }
            if (bufferPool1 != null) {
                bufferPool1.lazyDestroy();
            }
            if (bufferPool2 != null) {
                bufferPool2.lazyDestroy();
            }
        }
    }

    @Test
    public void testBoundedPools() throws IOException {
        BufferPool bufferPool = networkBufferPool.createBufferPool(1, 1);
        Assert.assertEquals(1, bufferPool.getNumBuffers());
        bufferPool = networkBufferPool.createBufferPool(1, 2);
        Assert.assertEquals(2, bufferPool.getNumBuffers());
    }

    @Test
    public void testSingleManagedPoolGetsAll() throws IOException {
        BufferPool bufferPool = networkBufferPool.createBufferPool(1, Integer.MAX_VALUE);
        Assert.assertEquals(networkBufferPool.getTotalNumberOfMemorySegments(), bufferPool.getNumBuffers());
    }

    @Test
    public void testSingleManagedPoolGetsAllExceptFixedOnes() throws IOException {
        BufferPool fixedBufferPool = networkBufferPool.createBufferPool(24, 24);
        BufferPool flexibleBufferPool = networkBufferPool.createBufferPool(1, Integer.MAX_VALUE);
        Assert.assertEquals(24, fixedBufferPool.getNumBuffers());
        Assert.assertEquals(((networkBufferPool.getTotalNumberOfMemorySegments()) - (fixedBufferPool.getNumBuffers())), flexibleBufferPool.getNumBuffers());
    }

    @Test
    public void testUniformDistribution() throws IOException {
        BufferPool first = networkBufferPool.createBufferPool(0, Integer.MAX_VALUE);
        Assert.assertEquals(networkBufferPool.getTotalNumberOfMemorySegments(), first.getNumBuffers());
        BufferPool second = networkBufferPool.createBufferPool(0, Integer.MAX_VALUE);
        Assert.assertEquals(((networkBufferPool.getTotalNumberOfMemorySegments()) / 2), first.getNumBuffers());
        Assert.assertEquals(((networkBufferPool.getTotalNumberOfMemorySegments()) / 2), second.getNumBuffers());
    }

    /**
     * Tests that buffers, once given to an initial buffer pool, get re-distributed to a second one
     * in case both buffer pools request half of the available buffer count.
     */
    @Test
    public void testUniformDistributionAllBuffers() throws IOException {
        BufferPool first = networkBufferPool.createBufferPool(((networkBufferPool.getTotalNumberOfMemorySegments()) / 2), Integer.MAX_VALUE);
        Assert.assertEquals(networkBufferPool.getTotalNumberOfMemorySegments(), first.getNumBuffers());
        BufferPool second = networkBufferPool.createBufferPool(((networkBufferPool.getTotalNumberOfMemorySegments()) / 2), Integer.MAX_VALUE);
        Assert.assertEquals(((networkBufferPool.getTotalNumberOfMemorySegments()) / 2), first.getNumBuffers());
        Assert.assertEquals(((networkBufferPool.getTotalNumberOfMemorySegments()) / 2), second.getNumBuffers());
    }

    @Test
    public void testUniformDistributionBounded1() throws IOException {
        BufferPool first = networkBufferPool.createBufferPool(0, networkBufferPool.getTotalNumberOfMemorySegments());
        Assert.assertEquals(networkBufferPool.getTotalNumberOfMemorySegments(), first.getNumBuffers());
        BufferPool second = networkBufferPool.createBufferPool(0, networkBufferPool.getTotalNumberOfMemorySegments());
        Assert.assertEquals(((networkBufferPool.getTotalNumberOfMemorySegments()) / 2), first.getNumBuffers());
        Assert.assertEquals(((networkBufferPool.getTotalNumberOfMemorySegments()) / 2), second.getNumBuffers());
    }

    @Test
    public void testUniformDistributionBounded2() throws IOException {
        BufferPool first = networkBufferPool.createBufferPool(0, 10);
        Assert.assertEquals(10, first.getNumBuffers());
        BufferPool second = networkBufferPool.createBufferPool(0, 10);
        Assert.assertEquals(10, first.getNumBuffers());
        Assert.assertEquals(10, second.getNumBuffers());
    }

    @Test
    public void testUniformDistributionBounded3() throws IOException {
        NetworkBufferPool globalPool = new NetworkBufferPool(3, 128);
        try {
            BufferPool first = globalPool.createBufferPool(0, 10);
            Assert.assertEquals(3, first.getNumBuffers());
            BufferPool second = globalPool.createBufferPool(0, 10);
            // the order of which buffer pool received 2 or 1 buffer is undefined
            Assert.assertEquals(3, ((first.getNumBuffers()) + (second.getNumBuffers())));
            Assert.assertNotEquals(3, first.getNumBuffers());
            Assert.assertNotEquals(3, second.getNumBuffers());
            BufferPool third = globalPool.createBufferPool(0, 10);
            Assert.assertEquals(1, first.getNumBuffers());
            Assert.assertEquals(1, second.getNumBuffers());
            Assert.assertEquals(1, third.getNumBuffers());
            // similar to #verifyAllBuffersReturned()
            String msg = "Wrong number of available segments after creating buffer pools.";
            Assert.assertEquals(msg, 3, globalPool.getNumberOfAvailableMemorySegments());
        } finally {
            // in case buffers have actually been requested, we must release them again
            globalPool.destroyAllBufferPools();
            globalPool.destroy();
        }
    }

    /**
     * Tests the interaction of requesting memory segments and creating local buffer pool and
     * verifies the number of assigned buffers match after redistributing buffers because of newly
     * requested memory segments or new buffer pools created.
     */
    @Test
    public void testUniformDistributionBounded4() throws IOException {
        NetworkBufferPool globalPool = new NetworkBufferPool(10, 128);
        try {
            BufferPool first = globalPool.createBufferPool(0, 10);
            Assert.assertEquals(10, first.getNumBuffers());
            List<MemorySegment> segmentList1 = globalPool.requestMemorySegments(2);
            Assert.assertEquals(2, segmentList1.size());
            Assert.assertEquals(8, first.getNumBuffers());
            BufferPool second = globalPool.createBufferPool(0, 10);
            Assert.assertEquals(4, first.getNumBuffers());
            Assert.assertEquals(4, second.getNumBuffers());
            List<MemorySegment> segmentList2 = globalPool.requestMemorySegments(2);
            Assert.assertEquals(2, segmentList2.size());
            Assert.assertEquals(3, first.getNumBuffers());
            Assert.assertEquals(3, second.getNumBuffers());
            List<MemorySegment> segmentList3 = globalPool.requestMemorySegments(2);
            Assert.assertEquals(2, segmentList3.size());
            Assert.assertEquals(2, first.getNumBuffers());
            Assert.assertEquals(2, second.getNumBuffers());
            String msg = "Wrong number of available segments after creating buffer pools and requesting segments.";
            Assert.assertEquals(msg, 4, globalPool.getNumberOfAvailableMemorySegments());
            globalPool.recycleMemorySegments(segmentList1);
            Assert.assertEquals(msg, 6, globalPool.getNumberOfAvailableMemorySegments());
            Assert.assertEquals(3, first.getNumBuffers());
            Assert.assertEquals(3, second.getNumBuffers());
            globalPool.recycleMemorySegments(segmentList2);
            Assert.assertEquals(msg, 8, globalPool.getNumberOfAvailableMemorySegments());
            Assert.assertEquals(4, first.getNumBuffers());
            Assert.assertEquals(4, second.getNumBuffers());
            globalPool.recycleMemorySegments(segmentList3);
            Assert.assertEquals(msg, 10, globalPool.getNumberOfAvailableMemorySegments());
            Assert.assertEquals(5, first.getNumBuffers());
            Assert.assertEquals(5, second.getNumBuffers());
        } finally {
            globalPool.destroyAllBufferPools();
            globalPool.destroy();
        }
    }

    @Test
    public void testBufferRedistributionMixed1() throws IOException {
        // try multiple times for various orders during redistribution
        for (int i = 0; i < 1000; ++i) {
            BufferPool first = networkBufferPool.createBufferPool(0, 10);
            Assert.assertEquals(10, first.getNumBuffers());
            BufferPool second = networkBufferPool.createBufferPool(0, 10);
            Assert.assertEquals(10, first.getNumBuffers());
            Assert.assertEquals(10, second.getNumBuffers());
            BufferPool third = networkBufferPool.createBufferPool(0, Integer.MAX_VALUE);
            // note: exact buffer distribution depends on the order during the redistribution
            for (BufferPool bp : new BufferPool[]{ first, second, third }) {
                int size = ((networkBufferPool.getTotalNumberOfMemorySegments()) * (Math.min(networkBufferPool.getTotalNumberOfMemorySegments(), bp.getMaxNumberOfMemorySegments()))) / ((networkBufferPool.getTotalNumberOfMemorySegments()) + 20);
                if (((bp.getNumBuffers()) != size) && ((bp.getNumBuffers()) != (size + 1))) {
                    Assert.fail(("wrong buffer pool size after redistribution: " + (bp.getNumBuffers())));
                }
            }
            BufferPool fourth = networkBufferPool.createBufferPool(0, Integer.MAX_VALUE);
            // note: exact buffer distribution depends on the order during the redistribution
            for (BufferPool bp : new BufferPool[]{ first, second, third, fourth }) {
                int size = ((networkBufferPool.getTotalNumberOfMemorySegments()) * (Math.min(networkBufferPool.getTotalNumberOfMemorySegments(), bp.getMaxNumberOfMemorySegments()))) / ((2 * (networkBufferPool.getTotalNumberOfMemorySegments())) + 20);
                if (((bp.getNumBuffers()) != size) && ((bp.getNumBuffers()) != (size + 1))) {
                    Assert.fail(("wrong buffer pool size after redistribution: " + (bp.getNumBuffers())));
                }
            }
            verifyAllBuffersReturned();
            setupNetworkBufferPool();
        }
    }

    @Test
    public void testAllDistributed() throws IOException {
        // try multiple times for various orders during redistribution
        for (int i = 0; i < 1000; ++i) {
            Random random = new Random();
            List<BufferPool> pools = new ArrayList<>();
            int numPools = (BufferPoolFactoryTest.numBuffers) / 32;
            long maxTotalUsed = 0;
            for (int j = 0; j < numPools; j++) {
                int numRequiredBuffers = random.nextInt((7 + 1));
                // make unbounded buffers more likely:
                int maxUsedBuffers = (random.nextBoolean()) ? Integer.MAX_VALUE : Math.max(1, ((random.nextInt(10)) + numRequiredBuffers));
                pools.add(networkBufferPool.createBufferPool(numRequiredBuffers, maxUsedBuffers));
                maxTotalUsed = Math.min(BufferPoolFactoryTest.numBuffers, (maxTotalUsed + maxUsedBuffers));
                // after every iteration, all buffers (up to maxTotalUsed) must be distributed
                int numDistributedBuffers = 0;
                for (BufferPool pool : pools) {
                    numDistributedBuffers += pool.getNumBuffers();
                }
                Assert.assertEquals(maxTotalUsed, numDistributedBuffers);
            }
            verifyAllBuffersReturned();
            setupNetworkBufferPool();
        }
    }

    @Test
    public void testCreateDestroy() throws IOException {
        BufferPool first = networkBufferPool.createBufferPool(0, Integer.MAX_VALUE);
        Assert.assertEquals(networkBufferPool.getTotalNumberOfMemorySegments(), first.getNumBuffers());
        BufferPool second = networkBufferPool.createBufferPool(0, Integer.MAX_VALUE);
        Assert.assertEquals(((networkBufferPool.getTotalNumberOfMemorySegments()) / 2), first.getNumBuffers());
        Assert.assertEquals(((networkBufferPool.getTotalNumberOfMemorySegments()) / 2), second.getNumBuffers());
        first.lazyDestroy();
        Assert.assertEquals(networkBufferPool.getTotalNumberOfMemorySegments(), second.getNumBuffers());
    }
}

