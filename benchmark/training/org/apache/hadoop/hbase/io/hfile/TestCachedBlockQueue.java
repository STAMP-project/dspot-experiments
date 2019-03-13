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
package org.apache.hadoop.hbase.io.hfile;


import java.nio.ByteBuffer;
import junit.framework.TestCase;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.ClassRule;
import org.junit.experimental.categories.Category;

import static BlockType.DATA;
import static MemoryType.EXCLUSIVE;


@Category({ IOTests.class, SmallTests.class })
public class TestCachedBlockQueue extends TestCase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCachedBlockQueue.class);

    public void testQueue() throws Exception {
        TestCachedBlockQueue.CachedBlock cb1 = new TestCachedBlockQueue.CachedBlock(1000, "cb1", 1);
        TestCachedBlockQueue.CachedBlock cb2 = new TestCachedBlockQueue.CachedBlock(1500, "cb2", 2);
        TestCachedBlockQueue.CachedBlock cb3 = new TestCachedBlockQueue.CachedBlock(1000, "cb3", 3);
        TestCachedBlockQueue.CachedBlock cb4 = new TestCachedBlockQueue.CachedBlock(1500, "cb4", 4);
        TestCachedBlockQueue.CachedBlock cb5 = new TestCachedBlockQueue.CachedBlock(1000, "cb5", 5);
        TestCachedBlockQueue.CachedBlock cb6 = new TestCachedBlockQueue.CachedBlock(1750, "cb6", 6);
        TestCachedBlockQueue.CachedBlock cb7 = new TestCachedBlockQueue.CachedBlock(1000, "cb7", 7);
        TestCachedBlockQueue.CachedBlock cb8 = new TestCachedBlockQueue.CachedBlock(1500, "cb8", 8);
        TestCachedBlockQueue.CachedBlock cb9 = new TestCachedBlockQueue.CachedBlock(1000, "cb9", 9);
        TestCachedBlockQueue.CachedBlock cb10 = new TestCachedBlockQueue.CachedBlock(1500, "cb10", 10);
        LruCachedBlockQueue queue = new LruCachedBlockQueue(10000, 1000);
        queue.add(cb1);
        queue.add(cb2);
        queue.add(cb3);
        queue.add(cb4);
        queue.add(cb5);
        queue.add(cb6);
        queue.add(cb7);
        queue.add(cb8);
        queue.add(cb9);
        queue.add(cb10);
        // We expect cb1 through cb8 to be in the queue
        long expectedSize = (((((((heapSize()) + (heapSize())) + (heapSize())) + (heapSize())) + (heapSize())) + (heapSize())) + (heapSize())) + (heapSize());
        TestCase.assertEquals(queue.heapSize(), expectedSize);
        for (int i = 1; i <= 8; i++) {
            TestCase.assertEquals(queue.pollLast().getCacheKey().getHfileName(), ("cb" + i));
        }
    }

    public void testQueueSmallBlockEdgeCase() throws Exception {
        TestCachedBlockQueue.CachedBlock cb1 = new TestCachedBlockQueue.CachedBlock(1000, "cb1", 1);
        TestCachedBlockQueue.CachedBlock cb2 = new TestCachedBlockQueue.CachedBlock(1500, "cb2", 2);
        TestCachedBlockQueue.CachedBlock cb3 = new TestCachedBlockQueue.CachedBlock(1000, "cb3", 3);
        TestCachedBlockQueue.CachedBlock cb4 = new TestCachedBlockQueue.CachedBlock(1500, "cb4", 4);
        TestCachedBlockQueue.CachedBlock cb5 = new TestCachedBlockQueue.CachedBlock(1000, "cb5", 5);
        TestCachedBlockQueue.CachedBlock cb6 = new TestCachedBlockQueue.CachedBlock(1750, "cb6", 6);
        TestCachedBlockQueue.CachedBlock cb7 = new TestCachedBlockQueue.CachedBlock(1000, "cb7", 7);
        TestCachedBlockQueue.CachedBlock cb8 = new TestCachedBlockQueue.CachedBlock(1500, "cb8", 8);
        TestCachedBlockQueue.CachedBlock cb9 = new TestCachedBlockQueue.CachedBlock(1000, "cb9", 9);
        TestCachedBlockQueue.CachedBlock cb10 = new TestCachedBlockQueue.CachedBlock(1500, "cb10", 10);
        LruCachedBlockQueue queue = new LruCachedBlockQueue(10000, 1000);
        queue.add(cb1);
        queue.add(cb2);
        queue.add(cb3);
        queue.add(cb4);
        queue.add(cb5);
        queue.add(cb6);
        queue.add(cb7);
        queue.add(cb8);
        queue.add(cb9);
        queue.add(cb10);
        TestCachedBlockQueue.CachedBlock cb0 = new TestCachedBlockQueue.CachedBlock((10 + (PER_BLOCK_OVERHEAD)), "cb0", 0);
        queue.add(cb0);
        // This is older so we must include it, but it will not end up kicking
        // anything out because (heapSize - cb8.heapSize + cb0.heapSize < maxSize)
        // and we must always maintain heapSize >= maxSize once we achieve it.
        // We expect cb0 through cb8 to be in the queue
        long expectedSize = ((((((((heapSize()) + (heapSize())) + (heapSize())) + (heapSize())) + (heapSize())) + (heapSize())) + (heapSize())) + (heapSize())) + (heapSize());
        TestCase.assertEquals(queue.heapSize(), expectedSize);
        for (int i = 0; i <= 8; i++) {
            TestCase.assertEquals(queue.pollLast().getCacheKey().getHfileName(), ("cb" + i));
        }
    }

    private static class CachedBlock extends org.apache.hadoop.hbase.io.hfile.LruCachedBlock {
        public CachedBlock(final long heapSize, String name, long accessTime) {
            super(new BlockCacheKey(name, 0), new Cacheable() {
                @Override
                public long heapSize() {
                    return ((int) (heapSize - (PER_BLOCK_OVERHEAD)));
                }

                @Override
                public int getSerializedLength() {
                    return 0;
                }

                @Override
                public void serialize(ByteBuffer destination, boolean includeNextBlockMetadata) {
                }

                @Override
                public CacheableDeserializer<Cacheable> getDeserializer() {
                    // TODO Auto-generated method stub
                    return null;
                }

                @Override
                public BlockType getBlockType() {
                    return DATA;
                }

                @Override
                public MemoryType getMemoryType() {
                    return EXCLUSIVE;
                }
            }, accessTime, false);
        }
    }
}

