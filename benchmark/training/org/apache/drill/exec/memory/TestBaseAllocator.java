/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.memory;


import io.netty.buffer.DrillBuf;
import io.netty.buffer.DrillBuf.TransferResult;
import org.apache.drill.categories.MemoryTest;
import org.apache.drill.exec.exception.OutOfMemoryException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MemoryTest.class)
public class TestBaseAllocator {
    // private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestBaseAllocator.class);
    private static final int MAX_ALLOCATION = 8 * 1024;

    /* // ---------------------------------------- DEBUG -----------------------------------

    @After
    public void checkBuffers() {
    final int bufferCount = UnsafeDirectLittleEndian.getBufferCount();
    if (bufferCount != 0) {
    UnsafeDirectLittleEndian.logBuffers(logger);
    UnsafeDirectLittleEndian.releaseBuffers();
    }

    assertEquals(0, bufferCount);
    }

    //  @AfterClass
    //  public static void dumpBuffers() {
    //    UnsafeDirectLittleEndian.logBuffers(logger);
    //  }

    // ---------------------------------------- DEBUG ------------------------------------
     */
    @Test
    public void test_privateMax() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            final DrillBuf drillBuf1 = rootAllocator.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 2));
            Assert.assertNotNull("allocation failed", drillBuf1);
            try (final BufferAllocator childAllocator = rootAllocator.newChildAllocator("noLimits", 0, TestBaseAllocator.MAX_ALLOCATION)) {
                final DrillBuf drillBuf2 = childAllocator.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 2));
                Assert.assertNotNull("allocation failed", drillBuf2);
                drillBuf2.release();
            }
            drillBuf1.release();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testRootAllocator_closeWithOutstanding() throws Exception {
        try {
            try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
                final DrillBuf drillBuf = rootAllocator.buffer(512);
                Assert.assertNotNull("allocation failed", drillBuf);
            }
        } finally {
            /* We expect there to be one unreleased underlying buffer because we're closing
            without releasing it.
             */
            /* // ------------------------------- DEBUG ---------------------------------
            final int bufferCount = UnsafeDirectLittleEndian.getBufferCount();
            UnsafeDirectLittleEndian.releaseBuffers();
            assertEquals(1, bufferCount);
            // ------------------------------- DEBUG ---------------------------------
             */
        }
    }

    @Test
    public void testRootAllocator_getEmpty() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            final DrillBuf drillBuf = rootAllocator.buffer(0);
            Assert.assertNotNull("allocation failed", drillBuf);
            Assert.assertEquals("capacity was non-zero", 0, drillBuf.capacity());
            drillBuf.release();
        }
    }

    @Test
    public void testAllocator_transferOwnership() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            final BufferAllocator childAllocator1 = rootAllocator.newChildAllocator("changeOwnership1", 0, TestBaseAllocator.MAX_ALLOCATION);
            final BufferAllocator childAllocator2 = rootAllocator.newChildAllocator("changeOwnership2", 0, TestBaseAllocator.MAX_ALLOCATION);
            final DrillBuf drillBuf1 = childAllocator1.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 4));
            rootAllocator.verify();
            TransferResult transferOwnership = drillBuf1.transferOwnership(childAllocator2);
            final boolean allocationFit = transferOwnership.allocationFit;
            rootAllocator.verify();
            Assert.assertTrue(allocationFit);
            drillBuf1.release();
            childAllocator1.close();
            rootAllocator.verify();
            transferOwnership.buffer.release();
            childAllocator2.close();
        }
    }

    @Test
    public void testAllocator_shareOwnership() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            final BufferAllocator childAllocator1 = rootAllocator.newChildAllocator("shareOwnership1", 0, TestBaseAllocator.MAX_ALLOCATION);
            final BufferAllocator childAllocator2 = rootAllocator.newChildAllocator("shareOwnership2", 0, TestBaseAllocator.MAX_ALLOCATION);
            final DrillBuf drillBuf1 = childAllocator1.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 4));
            rootAllocator.verify();
            // share ownership of buffer.
            final DrillBuf drillBuf2 = drillBuf1.retain(childAllocator2);
            rootAllocator.verify();
            Assert.assertNotNull(drillBuf2);
            Assert.assertNotEquals(drillBuf2, drillBuf1);
            // release original buffer (thus transferring ownership to allocator 2. (should leave allocator 1 in empty state)
            drillBuf1.release();
            rootAllocator.verify();
            childAllocator1.close();
            rootAllocator.verify();
            final BufferAllocator childAllocator3 = rootAllocator.newChildAllocator("shareOwnership3", 0, TestBaseAllocator.MAX_ALLOCATION);
            final DrillBuf drillBuf3 = drillBuf1.retain(childAllocator3);
            Assert.assertNotNull(drillBuf3);
            Assert.assertNotEquals(drillBuf3, drillBuf1);
            Assert.assertNotEquals(drillBuf3, drillBuf2);
            rootAllocator.verify();
            drillBuf2.release();
            rootAllocator.verify();
            childAllocator2.close();
            rootAllocator.verify();
            drillBuf3.release();
            rootAllocator.verify();
            childAllocator3.close();
        }
    }

    @Test
    public void testRootAllocator_createChildAndUse() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            try (final BufferAllocator childAllocator = rootAllocator.newChildAllocator("createChildAndUse", 0, TestBaseAllocator.MAX_ALLOCATION)) {
                final DrillBuf drillBuf = childAllocator.buffer(512);
                Assert.assertNotNull("allocation failed", drillBuf);
                drillBuf.release();
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testRootAllocator_createChildDontClose() throws Exception {
        try {
            try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
                final BufferAllocator childAllocator = rootAllocator.newChildAllocator("createChildDontClose", 0, TestBaseAllocator.MAX_ALLOCATION);
                final DrillBuf drillBuf = childAllocator.buffer(512);
                Assert.assertNotNull("allocation failed", drillBuf);
            }
        } finally {
            /* We expect one underlying buffer because we closed a child allocator without
            releasing the buffer allocated from it.
             */
            /* // ------------------------------- DEBUG ---------------------------------
            final int bufferCount = UnsafeDirectLittleEndian.getBufferCount();
            UnsafeDirectLittleEndian.releaseBuffers();
            assertEquals(1, bufferCount);
            // ------------------------------- DEBUG ---------------------------------
             */
        }
    }

    @Test
    public void testAllocator_manyAllocations() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            try (final BufferAllocator childAllocator = rootAllocator.newChildAllocator("manyAllocations", 0, TestBaseAllocator.MAX_ALLOCATION)) {
                TestBaseAllocator.allocateAndFree(childAllocator);
            }
        }
    }

    @Test
    public void testAllocator_overAllocate() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            try (final BufferAllocator childAllocator = rootAllocator.newChildAllocator("overAllocate", 0, TestBaseAllocator.MAX_ALLOCATION)) {
                TestBaseAllocator.allocateAndFree(childAllocator);
                try {
                    childAllocator.buffer(((TestBaseAllocator.MAX_ALLOCATION) + 1));
                    Assert.fail("allocated memory beyond max allowed");
                } catch (OutOfMemoryException e) {
                    // expected
                }
            }
        }
    }

    @Test
    public void testAllocator_overAllocateParent() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            try (final BufferAllocator childAllocator = rootAllocator.newChildAllocator("overAllocateParent", 0, TestBaseAllocator.MAX_ALLOCATION)) {
                final DrillBuf drillBuf1 = rootAllocator.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 2));
                Assert.assertNotNull("allocation failed", drillBuf1);
                final DrillBuf drillBuf2 = childAllocator.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 2));
                Assert.assertNotNull("allocation failed", drillBuf2);
                try {
                    childAllocator.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 4));
                    Assert.fail("allocated memory beyond max allowed");
                } catch (OutOfMemoryException e) {
                    // expected
                }
                drillBuf1.release();
                drillBuf2.release();
            }
        }
    }

    @Test
    public void testAllocator_createSlices() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            TestBaseAllocator.testAllocator_sliceUpBufferAndRelease(rootAllocator, rootAllocator);
            try (final BufferAllocator childAllocator = rootAllocator.newChildAllocator("createSlices", 0, TestBaseAllocator.MAX_ALLOCATION)) {
                TestBaseAllocator.testAllocator_sliceUpBufferAndRelease(rootAllocator, childAllocator);
            }
            rootAllocator.verify();
            TestBaseAllocator.testAllocator_sliceUpBufferAndRelease(rootAllocator, rootAllocator);
            try (final BufferAllocator childAllocator = rootAllocator.newChildAllocator("createSlices", 0, TestBaseAllocator.MAX_ALLOCATION)) {
                try (final BufferAllocator childAllocator2 = childAllocator.newChildAllocator("createSlices", 0, TestBaseAllocator.MAX_ALLOCATION)) {
                    final DrillBuf drillBuf1 = childAllocator2.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 8));
                    @SuppressWarnings("unused")
                    final DrillBuf drillBuf2 = drillBuf1.slice(((TestBaseAllocator.MAX_ALLOCATION) / 16), ((TestBaseAllocator.MAX_ALLOCATION) / 16));
                    TestBaseAllocator.testAllocator_sliceUpBufferAndRelease(rootAllocator, childAllocator);
                    drillBuf1.release();
                    rootAllocator.verify();
                }
                rootAllocator.verify();
                TestBaseAllocator.testAllocator_sliceUpBufferAndRelease(rootAllocator, childAllocator);
            }
            rootAllocator.verify();
        }
    }

    @Test
    public void testAllocator_sliceRanges() throws Exception {
        // final AllocatorOwner allocatorOwner = new NamedOwner("sliceRanges");
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            // Populate a buffer with byte values corresponding to their indices.
            final DrillBuf drillBuf = rootAllocator.buffer(256);
            Assert.assertEquals(256, drillBuf.capacity());
            Assert.assertEquals(0, drillBuf.readerIndex());
            Assert.assertEquals(0, drillBuf.readableBytes());
            Assert.assertEquals(0, drillBuf.writerIndex());
            Assert.assertEquals(256, drillBuf.writableBytes());
            final DrillBuf slice3 = ((DrillBuf) (drillBuf.slice()));
            Assert.assertEquals(0, slice3.readerIndex());
            Assert.assertEquals(0, slice3.readableBytes());
            Assert.assertEquals(0, slice3.writerIndex());
            // assertEquals(256, slice3.capacity());
            // assertEquals(256, slice3.writableBytes());
            for (int i = 0; i < 256; ++i) {
                drillBuf.writeByte(i);
            }
            Assert.assertEquals(0, drillBuf.readerIndex());
            Assert.assertEquals(256, drillBuf.readableBytes());
            Assert.assertEquals(256, drillBuf.writerIndex());
            Assert.assertEquals(0, drillBuf.writableBytes());
            final DrillBuf slice1 = ((DrillBuf) (drillBuf.slice()));
            Assert.assertEquals(0, slice1.readerIndex());
            Assert.assertEquals(256, slice1.readableBytes());
            for (int i = 0; i < 10; ++i) {
                Assert.assertEquals(i, slice1.readByte());
            }
            Assert.assertEquals((256 - 10), slice1.readableBytes());
            for (int i = 0; i < 256; ++i) {
                Assert.assertEquals(((byte) (i)), slice1.getByte(i));
            }
            final DrillBuf slice2 = ((DrillBuf) (drillBuf.slice(25, 25)));
            Assert.assertEquals(0, slice2.readerIndex());
            Assert.assertEquals(25, slice2.readableBytes());
            for (int i = 25; i < 50; ++i) {
                Assert.assertEquals(i, slice2.readByte());
            }
            /* for(int i = 256; i > 0; --i) {
            slice3.writeByte(i - 1);
            }
            for(int i = 0; i < 256; ++i) {
            assertEquals(255 - i, slice1.getByte(i));
            }
             */
            drillBuf.release();// all the derived buffers share this fate

        }
    }

    @Test
    public void testAllocator_slicesOfSlices() throws Exception {
        // final AllocatorOwner allocatorOwner = new NamedOwner("slicesOfSlices");
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            // Populate a buffer with byte values corresponding to their indices.
            final DrillBuf drillBuf = rootAllocator.buffer(256);
            for (int i = 0; i < 256; ++i) {
                drillBuf.writeByte(i);
            }
            // Slice it up.
            final DrillBuf slice0 = drillBuf.slice(0, drillBuf.capacity());
            for (int i = 0; i < 256; ++i) {
                Assert.assertEquals(((byte) (i)), drillBuf.getByte(i));
            }
            final DrillBuf slice10 = slice0.slice(10, ((drillBuf.capacity()) - 10));
            for (int i = 10; i < 256; ++i) {
                Assert.assertEquals(((byte) (i)), slice10.getByte((i - 10)));
            }
            final DrillBuf slice20 = slice10.slice(10, ((drillBuf.capacity()) - 20));
            for (int i = 20; i < 256; ++i) {
                Assert.assertEquals(((byte) (i)), slice20.getByte((i - 20)));
            }
            final DrillBuf slice30 = slice20.slice(10, ((drillBuf.capacity()) - 30));
            for (int i = 30; i < 256; ++i) {
                Assert.assertEquals(((byte) (i)), slice30.getByte((i - 30)));
            }
            drillBuf.release();
        }
    }

    @Test
    public void testAllocator_transferSliced() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            final BufferAllocator childAllocator1 = rootAllocator.newChildAllocator("transferSliced1", 0, TestBaseAllocator.MAX_ALLOCATION);
            final BufferAllocator childAllocator2 = rootAllocator.newChildAllocator("transferSliced2", 0, TestBaseAllocator.MAX_ALLOCATION);
            final DrillBuf drillBuf1 = childAllocator1.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 8));
            final DrillBuf drillBuf2 = childAllocator2.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 8));
            final DrillBuf drillBuf1s = drillBuf1.slice(0, ((drillBuf1.capacity()) / 2));
            final DrillBuf drillBuf2s = drillBuf2.slice(0, ((drillBuf2.capacity()) / 2));
            rootAllocator.verify();
            TransferResult result1 = drillBuf2s.transferOwnership(childAllocator1);
            rootAllocator.verify();
            TransferResult result2 = drillBuf1s.transferOwnership(childAllocator2);
            rootAllocator.verify();
            result1.buffer.release();
            result2.buffer.release();
            drillBuf1s.release();// releases drillBuf1

            drillBuf2s.release();// releases drillBuf2

            childAllocator1.close();
            childAllocator2.close();
        }
    }

    @Test
    public void testAllocator_shareSliced() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            final BufferAllocator childAllocator1 = rootAllocator.newChildAllocator("transferSliced", 0, TestBaseAllocator.MAX_ALLOCATION);
            final BufferAllocator childAllocator2 = rootAllocator.newChildAllocator("transferSliced", 0, TestBaseAllocator.MAX_ALLOCATION);
            final DrillBuf drillBuf1 = childAllocator1.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 8));
            final DrillBuf drillBuf2 = childAllocator2.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 8));
            final DrillBuf drillBuf1s = drillBuf1.slice(0, ((drillBuf1.capacity()) / 2));
            final DrillBuf drillBuf2s = drillBuf2.slice(0, ((drillBuf2.capacity()) / 2));
            rootAllocator.verify();
            final DrillBuf drillBuf2s1 = drillBuf2s.retain(childAllocator1);
            final DrillBuf drillBuf1s2 = drillBuf1s.retain(childAllocator2);
            rootAllocator.verify();
            drillBuf1s.release();// releases drillBuf1

            drillBuf2s.release();// releases drillBuf2

            rootAllocator.verify();
            drillBuf2s1.release();// releases the shared drillBuf2 slice

            drillBuf1s2.release();// releases the shared drillBuf1 slice

            childAllocator1.close();
            childAllocator2.close();
        }
    }

    @Test
    public void testAllocator_transferShared() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            final BufferAllocator childAllocator1 = rootAllocator.newChildAllocator("transferShared1", 0, TestBaseAllocator.MAX_ALLOCATION);
            final BufferAllocator childAllocator2 = rootAllocator.newChildAllocator("transferShared2", 0, TestBaseAllocator.MAX_ALLOCATION);
            final BufferAllocator childAllocator3 = rootAllocator.newChildAllocator("transferShared3", 0, TestBaseAllocator.MAX_ALLOCATION);
            final DrillBuf drillBuf1 = childAllocator1.buffer(((TestBaseAllocator.MAX_ALLOCATION) / 8));
            boolean allocationFit;
            DrillBuf drillBuf2 = drillBuf1.retain(childAllocator2);
            rootAllocator.verify();
            Assert.assertNotNull(drillBuf2);
            Assert.assertNotEquals(drillBuf2, drillBuf1);
            TransferResult result = drillBuf1.transferOwnership(childAllocator3);
            allocationFit = result.allocationFit;
            final DrillBuf drillBuf3 = result.buffer;
            Assert.assertTrue(allocationFit);
            rootAllocator.verify();
            // Since childAllocator3 now has childAllocator1's buffer, 1, can close
            drillBuf1.release();
            childAllocator1.close();
            rootAllocator.verify();
            drillBuf2.release();
            childAllocator2.close();
            rootAllocator.verify();
            final BufferAllocator childAllocator4 = rootAllocator.newChildAllocator("transferShared4", 0, TestBaseAllocator.MAX_ALLOCATION);
            TransferResult result2 = drillBuf3.transferOwnership(childAllocator4);
            allocationFit = result.allocationFit;
            final DrillBuf drillBuf4 = result2.buffer;
            Assert.assertTrue(allocationFit);
            rootAllocator.verify();
            drillBuf3.release();
            childAllocator3.close();
            rootAllocator.verify();
            drillBuf4.release();
            childAllocator4.close();
            rootAllocator.verify();
        }
    }

    @Test
    public void testAllocator_unclaimedReservation() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            try (final BufferAllocator childAllocator1 = rootAllocator.newChildAllocator("unclaimedReservation", 0, TestBaseAllocator.MAX_ALLOCATION)) {
                try (final AllocationReservation reservation = childAllocator1.newReservation()) {
                    Assert.assertTrue(reservation.add(64));
                }
                rootAllocator.verify();
            }
        }
    }

    @Test
    public void testAllocator_claimedReservation() throws Exception {
        try (final RootAllocator rootAllocator = new RootAllocator(TestBaseAllocator.MAX_ALLOCATION)) {
            try (final BufferAllocator childAllocator1 = rootAllocator.newChildAllocator("claimedReservation", 0, TestBaseAllocator.MAX_ALLOCATION)) {
                try (final AllocationReservation reservation = childAllocator1.newReservation()) {
                    Assert.assertTrue(reservation.add(32));
                    Assert.assertTrue(reservation.add(32));
                    final DrillBuf drillBuf = reservation.allocateBuffer();
                    Assert.assertEquals(64, drillBuf.capacity());
                    rootAllocator.verify();
                    drillBuf.release();
                    rootAllocator.verify();
                }
                rootAllocator.verify();
            }
        }
    }

    @Test
    public void multiple() throws Exception {
        final String owner = "test";
        try (RootAllocator allocator = new RootAllocator(Long.MAX_VALUE)) {
            final int op = 100000;
            BufferAllocator frag1 = allocator.newChildAllocator(owner, 1500000, Long.MAX_VALUE);
            BufferAllocator frag2 = allocator.newChildAllocator(owner, 500000, Long.MAX_VALUE);
            allocator.verify();
            BufferAllocator allocator11 = frag1.newChildAllocator(owner, op, Long.MAX_VALUE);
            DrillBuf b11 = allocator11.buffer(1000000);
            allocator.verify();
            BufferAllocator allocator12 = frag1.newChildAllocator(owner, op, Long.MAX_VALUE);
            DrillBuf b12 = allocator12.buffer(500000);
            allocator.verify();
            BufferAllocator allocator21 = frag1.newChildAllocator(owner, op, Long.MAX_VALUE);
            allocator.verify();
            BufferAllocator allocator22 = frag2.newChildAllocator(owner, op, Long.MAX_VALUE);
            DrillBuf b22 = allocator22.buffer(2000000);
            allocator.verify();
            BufferAllocator frag3 = allocator.newChildAllocator(owner, 1000000, Long.MAX_VALUE);
            allocator.verify();
            BufferAllocator allocator31 = frag3.newChildAllocator(owner, op, Long.MAX_VALUE);
            DrillBuf b31a = allocator31.buffer(200000);
            allocator.verify();
            // Previously running operator completes
            b22.release();
            allocator.verify();
            allocator22.close();
            b31a.release();
            allocator31.close();
            b12.release();
            allocator12.close();
            allocator21.close();
            b11.release();
            allocator11.close();
            frag1.close();
            frag2.close();
            frag3.close();
        }
    }
}

