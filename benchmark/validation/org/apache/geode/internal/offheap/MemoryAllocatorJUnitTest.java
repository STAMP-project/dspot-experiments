/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.offheap;


import MemoryAllocatorImpl.FREE_OFF_HEAP_MEMORY_PROPERTY;
import MemoryBlock.State.UNUSED;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.geode.OutOfOffHeapMemoryException;
import org.apache.geode.cache.CacheClosedException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import static FreeListManager.HUGE_MULTIPLE;
import static FreeListManager.MAX_TINY;
import static FreeListManager.TINY_MULTIPLE;
import static OffHeapStoredObject.HEADER_SIZE;


public class MemoryAllocatorJUnitTest {
    private long expectedMemoryUsage;

    private boolean memoryUsageEventReceived;

    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void testNullGetAllocator() {
        try {
            MemoryAllocatorImpl.getAllocator();
            Assert.fail("expected CacheClosedException");
        } catch (CacheClosedException expected) {
        }
    }

    @Test
    public void testConstructor() {
        try {
            MemoryAllocatorImpl.createForUnitTest(null, null, null);
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testCreate() {
        System.setProperty(FREE_OFF_HEAP_MEMORY_PROPERTY, "false");
        {
            NullOutOfOffHeapMemoryListener listener = new NullOutOfOffHeapMemoryListener();
            NullOffHeapMemoryStats stats = new NullOffHeapMemoryStats();
            try {
                MemoryAllocatorImpl.createForUnitTest(listener, stats, 10, 950, 100, new SlabFactory() {
                    @Override
                    public Slab create(int size) {
                        throw new OutOfMemoryError("expected");
                    }
                });
            } catch (OutOfMemoryError expected) {
            }
            Assert.assertTrue(listener.isClosed());
            Assert.assertTrue(stats.isClosed());
        }
        {
            NullOutOfOffHeapMemoryListener listener = new NullOutOfOffHeapMemoryListener();
            NullOffHeapMemoryStats stats = new NullOffHeapMemoryStats();
            int MAX_SLAB_SIZE = 100;
            try {
                SlabFactory factory = new SlabFactory() {
                    private int createCount = 0;

                    @Override
                    public Slab create(int size) {
                        (createCount)++;
                        if ((createCount) == 1) {
                            return new SlabImpl(size);
                        } else {
                            throw new OutOfMemoryError("expected");
                        }
                    }
                };
                MemoryAllocatorImpl.createForUnitTest(listener, stats, 10, 950, MAX_SLAB_SIZE, factory);
            } catch (OutOfMemoryError expected) {
            }
            Assert.assertTrue(listener.isClosed());
            Assert.assertTrue(stats.isClosed());
        }
        {
            NullOutOfOffHeapMemoryListener listener = new NullOutOfOffHeapMemoryListener();
            NullOffHeapMemoryStats stats = new NullOffHeapMemoryStats();
            SlabFactory factory = new SlabFactory() {
                @Override
                public Slab create(int size) {
                    return new SlabImpl(size);
                }
            };
            MemoryAllocator ma = MemoryAllocatorImpl.createForUnitTest(listener, stats, 10, 950, 100, factory);
            try {
                Assert.assertFalse(listener.isClosed());
                Assert.assertFalse(stats.isClosed());
                ma.close();
                Assert.assertTrue(listener.isClosed());
                Assert.assertFalse(stats.isClosed());
                listener = new NullOutOfOffHeapMemoryListener();
                NullOffHeapMemoryStats stats2 = new NullOffHeapMemoryStats();
                {
                    SlabImpl slab = new SlabImpl(1024);
                    try {
                        MemoryAllocatorImpl.createForUnitTest(listener, stats2, new SlabImpl[]{ slab });
                    } catch (IllegalStateException expected) {
                        Assert.assertTrue(("unexpected message: " + (expected.getMessage())), expected.getMessage().equals("attempted to reuse existing off-heap memory even though new off-heap memory was allocated"));
                    } finally {
                        slab.free();
                    }
                    Assert.assertFalse(stats.isClosed());
                    Assert.assertTrue(listener.isClosed());
                    Assert.assertTrue(stats2.isClosed());
                }
                listener = new NullOutOfOffHeapMemoryListener();
                stats2 = new NullOffHeapMemoryStats();
                MemoryAllocator ma2 = MemoryAllocatorImpl.createForUnitTest(listener, stats2, 10, 950, 100, factory);
                Assert.assertSame(ma, ma2);
                Assert.assertTrue(stats.isClosed());
                Assert.assertFalse(listener.isClosed());
                Assert.assertFalse(stats2.isClosed());
                stats = stats2;
                ma.close();
                Assert.assertTrue(listener.isClosed());
                Assert.assertFalse(stats.isClosed());
            } finally {
                MemoryAllocatorImpl.freeOffHeapMemory();
            }
            Assert.assertTrue(stats.isClosed());
        }
    }

    @Test
    public void testBasics() {
        int BATCH_SIZE = 1;
        int TINY_MULTIPLE = TINY_MULTIPLE;
        int HUGE_MULTIPLE = HUGE_MULTIPLE;
        int perObjectOverhead = HEADER_SIZE;
        int maxTiny = (MAX_TINY) - perObjectOverhead;
        int minHuge = maxTiny + 1;
        int TOTAL_MEM = ((((maxTiny + perObjectOverhead) * BATCH_SIZE) + ((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, ((minHuge + 1) + perObjectOverhead))) * BATCH_SIZE)) + ((TINY_MULTIPLE + perObjectOverhead) * BATCH_SIZE))/* + (MIN_BIG_SIZE+perObjectOverhead)*BATCH_SIZE */
         + (MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, ((minHuge + perObjectOverhead) + 1)));
        SlabImpl slab = new SlabImpl(TOTAL_MEM);
        try {
            MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
            Assert.assertEquals(TOTAL_MEM, ma.getFreeMemory());
            Assert.assertEquals(TOTAL_MEM, ma.freeList.getFreeFragmentMemory());
            Assert.assertEquals(0, ma.freeList.getFreeTinyMemory());
            Assert.assertEquals(0, ma.freeList.getFreeHugeMemory());
            StoredObject tinymc = ma.allocate(maxTiny);
            Assert.assertEquals((TOTAL_MEM - (MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead)))), ma.getFreeMemory());
            Assert.assertEquals(((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead))) * (BATCH_SIZE - 1)), ma.freeList.getFreeTinyMemory());
            StoredObject hugemc = ma.allocate(minHuge);
            Assert.assertEquals(((TOTAL_MEM - (MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (minHuge + perObjectOverhead))))/* -round(BIG_MULTIPLE, maxBig+perObjectOverhead) */
             - (MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead)))), ma.getFreeMemory());
            long freeSlab = ma.freeList.getFreeFragmentMemory();
            long oldFreeHugeMemory = ma.freeList.getFreeHugeMemory();
            Assert.assertEquals(((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (minHuge + perObjectOverhead))) * (BATCH_SIZE - 1)), oldFreeHugeMemory);
            hugemc.release();
            Assert.assertEquals(MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (minHuge + perObjectOverhead)), ((ma.freeList.getFreeHugeMemory()) - oldFreeHugeMemory));
            Assert.assertEquals((TOTAL_MEM/* -round(BIG_MULTIPLE, maxBig+perObjectOverhead) */
             - (MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead)))), ma.getFreeMemory());
            Assert.assertEquals((TOTAL_MEM - (MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead)))), ma.getFreeMemory());
            long oldFreeTinyMemory = ma.freeList.getFreeTinyMemory();
            tinymc.release();
            Assert.assertEquals(MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead)), ((ma.freeList.getFreeTinyMemory()) - oldFreeTinyMemory));
            Assert.assertEquals(TOTAL_MEM, ma.getFreeMemory());
            // now lets reallocate from the free lists
            tinymc = ma.allocate(maxTiny);
            Assert.assertEquals(oldFreeTinyMemory, ma.freeList.getFreeTinyMemory());
            Assert.assertEquals((TOTAL_MEM - (MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead)))), ma.getFreeMemory());
            hugemc = ma.allocate(minHuge);
            Assert.assertEquals(oldFreeHugeMemory, ma.freeList.getFreeHugeMemory());
            Assert.assertEquals(((TOTAL_MEM - (MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (minHuge + perObjectOverhead))))/* -round(BIG_MULTIPLE, maxBig+perObjectOverhead) */
             - (MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead)))), ma.getFreeMemory());
            hugemc.release();
            Assert.assertEquals(MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (minHuge + perObjectOverhead)), ((ma.freeList.getFreeHugeMemory()) - oldFreeHugeMemory));
            Assert.assertEquals((TOTAL_MEM/* -round(BIG_MULTIPLE, maxBig+perObjectOverhead) */
             - (MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead)))), ma.getFreeMemory());
            Assert.assertEquals((TOTAL_MEM - (MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead)))), ma.getFreeMemory());
            tinymc.release();
            Assert.assertEquals(MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead)), ((ma.freeList.getFreeTinyMemory()) - oldFreeTinyMemory));
            Assert.assertEquals(TOTAL_MEM, ma.getFreeMemory());
            // None of the reallocates should have come from the slab.
            Assert.assertEquals(freeSlab, ma.freeList.getFreeFragmentMemory());
            tinymc = ma.allocate(1);
            Assert.assertEquals(MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (1 + perObjectOverhead)), tinymc.getSize());
            Assert.assertEquals((freeSlab - ((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (1 + perObjectOverhead))) * BATCH_SIZE)), ma.freeList.getFreeFragmentMemory());
            freeSlab = ma.freeList.getFreeFragmentMemory();
            tinymc.release();
            Assert.assertEquals(((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (maxTiny + perObjectOverhead))) + ((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (1 + perObjectOverhead))) * BATCH_SIZE)), ((ma.freeList.getFreeTinyMemory()) - oldFreeTinyMemory));
            hugemc = ma.allocate((minHuge + 1));
            Assert.assertEquals(MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, ((minHuge + 1) + perObjectOverhead)), hugemc.getSize());
            Assert.assertEquals(((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (minHuge + perObjectOverhead))) * (BATCH_SIZE - 1)), ma.freeList.getFreeHugeMemory());
            hugemc.release();
            Assert.assertEquals(((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (minHuge + perObjectOverhead))) * BATCH_SIZE), ma.freeList.getFreeHugeMemory());
            hugemc = ma.allocate(minHuge);
            Assert.assertEquals(((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (minHuge + perObjectOverhead))) * (BATCH_SIZE - 1)), ma.freeList.getFreeHugeMemory());
            if (BATCH_SIZE > 1) {
                StoredObject hugemc2 = ma.allocate(minHuge);
                Assert.assertEquals(((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (minHuge + perObjectOverhead))) * (BATCH_SIZE - 2)), ma.freeList.getFreeHugeMemory());
                hugemc2.release();
                Assert.assertEquals(((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (minHuge + perObjectOverhead))) * (BATCH_SIZE - 1)), ma.freeList.getFreeHugeMemory());
            }
            hugemc.release();
            Assert.assertEquals(((MemoryAllocatorJUnitTest.round(TINY_MULTIPLE, (minHuge + perObjectOverhead))) * BATCH_SIZE), ma.freeList.getFreeHugeMemory());
            // now that we do defragmentation the following allocate works.
            hugemc = ma.allocate((((minHuge + HUGE_MULTIPLE) + HUGE_MULTIPLE) - 1));
        } finally {
            MemoryAllocatorImpl.freeOffHeapMemory();
        }
    }

    @Test
    public void testChunkCreateDirectByteBuffer() {
        SlabImpl slab = new SlabImpl((1024 * 1024));
        try {
            MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
            ByteBuffer bb = ByteBuffer.allocate(1024);
            for (int i = 0; i < 1024; i++) {
                bb.put(((byte) (i)));
            }
            bb.position(0);
            OffHeapStoredObject c = ((OffHeapStoredObject) (ma.allocateAndInitialize(bb.array(), false, false)));
            Assert.assertEquals(1024, c.getDataSize());
            if (!(Arrays.equals(bb.array(), c.getRawBytes()))) {
                Assert.fail(((("arrays are not equal. Expected " + (Arrays.toString(bb.array()))) + " but found: ") + (Arrays.toString(c.getRawBytes()))));
            }
            ByteBuffer dbb = c.createDirectByteBuffer();
            Assert.assertEquals(true, dbb.isDirect());
            Assert.assertEquals(bb, dbb);
        } finally {
            MemoryAllocatorImpl.freeOffHeapMemory();
        }
    }

    @Test
    public void testDebugLog() {
        MemoryAllocatorImpl.debugLog("test debug log", false);
        MemoryAllocatorImpl.debugLog("test debug log", true);
    }

    @Test
    public void testGetLostChunks() {
        SlabImpl slab = new SlabImpl((1024 * 1024));
        try {
            MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
            Assert.assertEquals(Collections.emptyList(), ma.getLostChunks(null));
        } finally {
            MemoryAllocatorImpl.freeOffHeapMemory();
        }
    }

    @Test
    public void testFindSlab() {
        final int SLAB_SIZE = 1024 * 1024;
        SlabImpl slab = new SlabImpl(SLAB_SIZE);
        try {
            MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
            Assert.assertEquals(0, ma.findSlab(slab.getMemoryAddress()));
            Assert.assertEquals(0, ma.findSlab((((slab.getMemoryAddress()) + SLAB_SIZE) - 1)));
            try {
                ma.findSlab(((slab.getMemoryAddress()) - 1));
                Assert.fail("expected IllegalStateException");
            } catch (IllegalStateException expected) {
            }
            try {
                ma.findSlab(((slab.getMemoryAddress()) + SLAB_SIZE));
                Assert.fail("expected IllegalStateException");
            } catch (IllegalStateException expected) {
            }
        } finally {
            MemoryAllocatorImpl.freeOffHeapMemory();
        }
    }

    @Test
    public void testValidateAddressAndSize() {
        final int SLAB_SIZE = 1024 * 1024;
        SlabImpl slab = new SlabImpl(SLAB_SIZE);
        try {
            MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
            try {
                MemoryAllocatorImpl.validateAddress(0L);
                Assert.fail("expected IllegalStateException");
            } catch (IllegalStateException expected) {
                Assert.assertEquals(("Unexpected exception message: " + (expected.getMessage())), true, expected.getMessage().contains("addr was smaller than expected"));
            }
            try {
                MemoryAllocatorImpl.validateAddress(1L);
                Assert.fail("expected IllegalStateException");
            } catch (IllegalStateException expected) {
                Assert.assertEquals(("Unexpected exception message: " + (expected.getMessage())), true, expected.getMessage().contains("Valid addresses must be in one of the following ranges:"));
            }
            MemoryAllocatorImpl.validateAddressAndSizeWithinSlab(slab.getMemoryAddress(), SLAB_SIZE, false);
            MemoryAllocatorImpl.validateAddressAndSizeWithinSlab(slab.getMemoryAddress(), SLAB_SIZE, true);
            MemoryAllocatorImpl.validateAddressAndSizeWithinSlab(slab.getMemoryAddress(), (-1), true);
            try {
                MemoryAllocatorImpl.validateAddressAndSizeWithinSlab(((slab.getMemoryAddress()) - 1), SLAB_SIZE, true);
                Assert.fail("expected IllegalStateException");
            } catch (IllegalStateException expected) {
                Assert.assertEquals(("Unexpected exception message: " + (expected.getMessage())), true, expected.getMessage().equals(((" address 0x" + (Long.toString(((slab.getMemoryAddress()) - 1), 16))) + " does not address the original slab memory")));
            }
            try {
                MemoryAllocatorImpl.validateAddressAndSizeWithinSlab(slab.getMemoryAddress(), (SLAB_SIZE + 1), true);
                Assert.fail("expected IllegalStateException");
            } catch (IllegalStateException expected) {
                Assert.assertEquals(("Unexpected exception message: " + (expected.getMessage())), true, expected.getMessage().equals(((" address 0x" + (Long.toString(((slab.getMemoryAddress()) + SLAB_SIZE), 16))) + " does not address the original slab memory")));
            }
        } finally {
            MemoryAllocatorImpl.freeOffHeapMemory();
        }
    }

    @Test
    public void testMemoryInspection() {
        final int SLAB_SIZE = 1024 * 1024;
        SlabImpl slab = new SlabImpl(SLAB_SIZE);
        try {
            MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
            MemoryInspector inspector = ma.getMemoryInspector();
            Assert.assertNotNull(inspector);
            Assert.assertEquals(null, inspector.getFirstBlock());
            Assert.assertEquals(Collections.emptyList(), inspector.getSnapshot());
            Assert.assertEquals(Collections.emptyList(), inspector.getAllocatedBlocks());
            Assert.assertEquals(null, inspector.getBlockAfter(null));
            inspector.createSnapshot();
            // call this twice for code coverage
            inspector.createSnapshot();
            try {
                Assert.assertEquals(inspector.getAllBlocks(), inspector.getSnapshot());
                MemoryBlock firstBlock = inspector.getFirstBlock();
                Assert.assertNotNull(firstBlock);
                Assert.assertEquals((1024 * 1024), firstBlock.getBlockSize());
                Assert.assertEquals("N/A", firstBlock.getDataType());
                Assert.assertEquals((-1), firstBlock.getFreeListId());
                Assert.assertTrue(((firstBlock.getAddress()) > 0));
                Assert.assertNull(firstBlock.getNextBlock());
                Assert.assertEquals(0, firstBlock.getRefCount());
                Assert.assertEquals(0, firstBlock.getSlabId());
                Assert.assertEquals(UNUSED, firstBlock.getState());
                Assert.assertFalse(firstBlock.isCompressed());
                Assert.assertFalse(firstBlock.isSerialized());
                Assert.assertEquals(null, inspector.getBlockAfter(firstBlock));
            } finally {
                inspector.clearSnapshot();
            }
        } finally {
            MemoryAllocatorImpl.freeOffHeapMemory();
        }
    }

    @Test
    public void testClose() {
        System.setProperty(FREE_OFF_HEAP_MEMORY_PROPERTY, "false");
        SlabImpl slab = new SlabImpl((1024 * 1024));
        boolean freeSlab = true;
        SlabImpl[] slabs = new SlabImpl[]{ slab };
        try {
            MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), slabs);
            ma.close();
            ma.close();
            System.setProperty(FREE_OFF_HEAP_MEMORY_PROPERTY, "true");
            try {
                ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), slabs);
                ma.close();
                freeSlab = false;
                ma.close();
            } finally {
                System.clearProperty(FREE_OFF_HEAP_MEMORY_PROPERTY);
            }
        } finally {
            if (freeSlab) {
                MemoryAllocatorImpl.freeOffHeapMemory();
            }
        }
    }

    @Test
    public void testDefragmentation() {
        final int perObjectOverhead = HEADER_SIZE;
        final int BIG_ALLOC_SIZE = 150000;
        final int SMALL_ALLOC_SIZE = BIG_ALLOC_SIZE / 2;
        final int TOTAL_MEM = BIG_ALLOC_SIZE;
        SlabImpl slab = new SlabImpl(TOTAL_MEM);
        try {
            MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
            StoredObject bmc = ma.allocate((BIG_ALLOC_SIZE - perObjectOverhead));
            try {
                StoredObject smc = ma.allocate((SMALL_ALLOC_SIZE - perObjectOverhead));
                Assert.fail("Expected out of memory");
            } catch (OutOfOffHeapMemoryException expected) {
            }
            bmc.release();
            Assert.assertEquals(TOTAL_MEM, ma.freeList.getFreeMemory());
            StoredObject smc1 = ma.allocate((SMALL_ALLOC_SIZE - perObjectOverhead));
            StoredObject smc2 = ma.allocate((SMALL_ALLOC_SIZE - perObjectOverhead));
            smc2.release();
            Assert.assertEquals((TOTAL_MEM - SMALL_ALLOC_SIZE), ma.freeList.getFreeMemory());
            try {
                bmc = ma.allocate((BIG_ALLOC_SIZE - perObjectOverhead));
                Assert.fail("Expected out of memory");
            } catch (OutOfOffHeapMemoryException expected) {
            }
            smc1.release();
            Assert.assertEquals(TOTAL_MEM, ma.freeList.getFreeMemory());
            bmc = ma.allocate((BIG_ALLOC_SIZE - perObjectOverhead));
            bmc.release();
            Assert.assertEquals(TOTAL_MEM, ma.freeList.getFreeMemory());
            ArrayList<StoredObject> mcs = new ArrayList<StoredObject>();
            for (int i = 0; i < (BIG_ALLOC_SIZE / (8 + perObjectOverhead)); i++) {
                mcs.add(ma.allocate(8));
            }
            checkMcs(mcs);
            Assert.assertEquals(0, ma.freeList.getFreeMemory());
            try {
                ma.allocate(8);
                Assert.fail("expected out of memory");
            } catch (OutOfOffHeapMemoryException expected) {
            }
            mcs.remove(0).release();// frees 8+perObjectOverhead

            Assert.assertEquals((8 + perObjectOverhead), ma.freeList.getFreeMemory());
            mcs.remove(0).release();// frees 8+perObjectOverhead

            Assert.assertEquals(((8 + perObjectOverhead) * 2), ma.freeList.getFreeMemory());
            ma.allocate(16).release();// allocates and frees 16+perObjectOverhead; still have

            // perObjectOverhead
            Assert.assertEquals(((8 + perObjectOverhead) * 2), ma.freeList.getFreeMemory());
            mcs.remove(0).release();// frees 8+perObjectOverhead

            Assert.assertEquals(((8 + perObjectOverhead) * 3), ma.freeList.getFreeMemory());
            mcs.remove(0).release();// frees 8+perObjectOverhead

            Assert.assertEquals(((8 + perObjectOverhead) * 4), ma.freeList.getFreeMemory());
            // At this point I should have 8*4 + perObjectOverhead*4 of free memory
            ma.allocate(((8 * 4) + (perObjectOverhead * 3))).release();
            Assert.assertEquals(((8 + perObjectOverhead) * 4), ma.freeList.getFreeMemory());
            mcs.remove(0).release();// frees 8+perObjectOverhead

            Assert.assertEquals(((8 + perObjectOverhead) * 5), ma.freeList.getFreeMemory());
            // At this point I should have 8*5 + perObjectOverhead*5 of free memory
            try {
                ma.allocate((((8 * 5) + (perObjectOverhead * 4)) + 1));
                Assert.fail("expected out of memory");
            } catch (OutOfOffHeapMemoryException expected) {
            }
            mcs.remove(0).release();// frees 8+perObjectOverhead

            Assert.assertEquals(((8 + perObjectOverhead) * 6), ma.freeList.getFreeMemory());
            checkMcs(mcs);
            // At this point I should have 8*6 + perObjectOverhead*6 of free memory
            StoredObject mc24 = ma.allocate(24);
            checkMcs(mcs);
            Assert.assertEquals((((8 + perObjectOverhead) * 6) - (24 + perObjectOverhead)), ma.freeList.getFreeMemory());
            // At this point I should have 8*3 + perObjectOverhead*5 of free memory
            StoredObject mc16 = ma.allocate(16);
            checkMcs(mcs);
            Assert.assertEquals(((((8 + perObjectOverhead) * 6) - (24 + perObjectOverhead)) - (16 + perObjectOverhead)), ma.freeList.getFreeMemory());
            // At this point I should have 8*1 + perObjectOverhead*4 of free memory
            mcs.add(ma.allocate(8));
            checkMcs(mcs);
            Assert.assertEquals((((((8 + perObjectOverhead) * 6) - (24 + perObjectOverhead)) - (16 + perObjectOverhead)) - (8 + perObjectOverhead)), ma.freeList.getFreeMemory());
            // At this point I should have 8*0 + perObjectOverhead*3 of free memory
            StoredObject mcDO = ma.allocate((perObjectOverhead * 2));
            checkMcs(mcs);
            // At this point I should have 8*0 + perObjectOverhead*0 of free memory
            Assert.assertEquals(0, ma.freeList.getFreeMemory());
            try {
                ma.allocate(1);
                Assert.fail("expected out of memory");
            } catch (OutOfOffHeapMemoryException expected) {
            }
            checkMcs(mcs);
            Assert.assertEquals(0, ma.freeList.getFreeMemory());
            mcDO.release();
            Assert.assertEquals((perObjectOverhead * 3), ma.freeList.getFreeMemory());
            mcs.remove(((mcs.size()) - 1)).release();
            Assert.assertEquals(((perObjectOverhead * 3) + (8 + perObjectOverhead)), ma.freeList.getFreeMemory());
            mc16.release();
            Assert.assertEquals((((perObjectOverhead * 3) + (8 + perObjectOverhead)) + (16 + perObjectOverhead)), ma.freeList.getFreeMemory());
            mc24.release();
            Assert.assertEquals(((((perObjectOverhead * 3) + (8 + perObjectOverhead)) + (16 + perObjectOverhead)) + (24 + perObjectOverhead)), ma.freeList.getFreeMemory());
            long freeMem = ma.freeList.getFreeMemory();
            for (StoredObject mc : mcs) {
                mc.release();
                Assert.assertEquals((freeMem + (8 + perObjectOverhead)), ma.freeList.getFreeMemory());
                freeMem += 8 + perObjectOverhead;
            }
            mcs.clear();
            Assert.assertEquals(TOTAL_MEM, ma.freeList.getFreeMemory());
            bmc = ma.allocate((BIG_ALLOC_SIZE - perObjectOverhead));
            bmc.release();
        } finally {
            MemoryAllocatorImpl.freeOffHeapMemory();
        }
    }

    @Test
    public void testUsageEventListener() {
        final int perObjectOverhead = HEADER_SIZE;
        final int SMALL_ALLOC_SIZE = 1000;
        SlabImpl slab = new SlabImpl(3000);
        try {
            MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(new NullOutOfOffHeapMemoryListener(), new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
            MemoryUsageListener listener = new MemoryUsageListener() {
                @Override
                public void updateMemoryUsed(final long bytesUsed) {
                    MemoryAllocatorJUnitTest.this.memoryUsageEventReceived = true;
                    Assert.assertEquals(MemoryAllocatorJUnitTest.this.expectedMemoryUsage, bytesUsed);
                }
            };
            ma.addMemoryUsageListener(listener);
            this.expectedMemoryUsage = SMALL_ALLOC_SIZE;
            this.memoryUsageEventReceived = false;
            StoredObject smc = ma.allocate((SMALL_ALLOC_SIZE - perObjectOverhead));
            Assert.assertEquals(true, this.memoryUsageEventReceived);
            this.expectedMemoryUsage = SMALL_ALLOC_SIZE * 2;
            this.memoryUsageEventReceived = false;
            smc = ma.allocate((SMALL_ALLOC_SIZE - perObjectOverhead));
            Assert.assertEquals(true, this.memoryUsageEventReceived);
            MemoryUsageListener unaddedListener = new MemoryUsageListener() {
                @Override
                public void updateMemoryUsed(final long bytesUsed) {
                    throw new IllegalStateException("Should never be called");
                }
            };
            ma.removeMemoryUsageListener(unaddedListener);
            ma.removeMemoryUsageListener(listener);
            ma.removeMemoryUsageListener(unaddedListener);
            this.expectedMemoryUsage = SMALL_ALLOC_SIZE * 2;
            this.memoryUsageEventReceived = false;
            smc = ma.allocate((SMALL_ALLOC_SIZE - perObjectOverhead));
            Assert.assertEquals(false, this.memoryUsageEventReceived);
        } finally {
            MemoryAllocatorImpl.freeOffHeapMemory();
        }
    }

    @Test
    public void testOutOfOffHeapMemory() {
        final int perObjectOverhead = HEADER_SIZE;
        final int BIG_ALLOC_SIZE = 150000;
        final int SMALL_ALLOC_SIZE = BIG_ALLOC_SIZE / 2;
        final int TOTAL_MEM = BIG_ALLOC_SIZE;
        final SlabImpl slab = new SlabImpl(TOTAL_MEM);
        final AtomicReference<OutOfOffHeapMemoryException> ooom = new AtomicReference<OutOfOffHeapMemoryException>();
        final OutOfOffHeapMemoryListener oooml = new OutOfOffHeapMemoryListener() {
            @Override
            public void outOfOffHeapMemory(OutOfOffHeapMemoryException cause) {
                ooom.set(cause);
            }

            @Override
            public void close() {
            }
        };
        try {
            MemoryAllocatorImpl ma = MemoryAllocatorImpl.createForUnitTest(oooml, new NullOffHeapMemoryStats(), new SlabImpl[]{ slab });
            // make a big allocation
            StoredObject bmc = ma.allocate((BIG_ALLOC_SIZE - perObjectOverhead));
            Assert.assertNull(ooom.get());
            // drive the ma to ooom with small allocations
            try {
                StoredObject smc = ma.allocate((SMALL_ALLOC_SIZE - perObjectOverhead));
                Assert.fail("Expected out of memory");
            } catch (OutOfOffHeapMemoryException expected) {
            }
            Assert.assertNotNull(ooom.get());
            Assert.assertTrue(ooom.get().getMessage().contains("Out of off-heap memory. Could not allocate size of "));
        } finally {
            MemoryAllocatorImpl.freeOffHeapMemory();
        }
    }
}

