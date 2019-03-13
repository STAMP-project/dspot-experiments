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


import EndOfPartitionEvent.INSTANCE;
import java.io.IOException;
import java.nio.ReadOnlyBufferException;
import org.apache.flink.runtime.io.network.api.serialization.EventSerializer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ReadOnlySlicedNetworkBuffer}.
 */
public class ReadOnlySlicedBufferTest {
    private static final int BUFFER_SIZE = 1024;

    private static final int DATA_SIZE = 10;

    private NetworkBuffer buffer;

    @Test
    public void testForwardsIsBuffer() throws IOException {
        Assert.assertEquals(buffer.isBuffer(), buffer.readOnlySlice().isBuffer());
        Assert.assertEquals(buffer.isBuffer(), buffer.readOnlySlice(1, 2).isBuffer());
        Buffer eventBuffer = EventSerializer.toBuffer(INSTANCE);
        Assert.assertEquals(eventBuffer.isBuffer(), eventBuffer.readOnlySlice().isBuffer());
        Assert.assertEquals(eventBuffer.isBuffer(), eventBuffer.readOnlySlice(1, 2).isBuffer());
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testTagAsEventThrows1() {
        buffer.readOnlySlice().tagAsEvent();
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testTagAsEventThrows2() {
        buffer.readOnlySlice(1, 2).tagAsEvent();
    }

    @Test
    public void testForwardsGetMemorySegment() {
        Assert.assertSame(buffer.getMemorySegment(), buffer.readOnlySlice().getMemorySegment());
        Assert.assertSame(buffer.getMemorySegment(), buffer.readOnlySlice(1, 2).getMemorySegment());
    }

    @Test
    public void testForwardsGetRecycler() {
        Assert.assertSame(buffer.getRecycler(), buffer.readOnlySlice().getRecycler());
        Assert.assertSame(buffer.getRecycler(), buffer.readOnlySlice(1, 2).getRecycler());
    }

    /**
     * Tests forwarding of both {@link ReadOnlySlicedNetworkBuffer#recycleBuffer()} and
     * {@link ReadOnlySlicedNetworkBuffer#isRecycled()}.
     */
    @Test
    public void testForwardsRecycleBuffer1() {
        ReadOnlySlicedNetworkBuffer slice = buffer.readOnlySlice();
        Assert.assertFalse(slice.isRecycled());
        slice.recycleBuffer();
        Assert.assertTrue(slice.isRecycled());
        Assert.assertTrue(buffer.isRecycled());
    }

    /**
     * Tests forwarding of both {@link ReadOnlySlicedNetworkBuffer#recycleBuffer()} and
     * {@link ReadOnlySlicedNetworkBuffer#isRecycled()}.
     */
    @Test
    public void testForwardsRecycleBuffer2() {
        ReadOnlySlicedNetworkBuffer slice = buffer.readOnlySlice(1, 2);
        Assert.assertFalse(slice.isRecycled());
        slice.recycleBuffer();
        Assert.assertTrue(slice.isRecycled());
        Assert.assertTrue(buffer.isRecycled());
    }

    /**
     * Tests forwarding of both {@link ReadOnlySlicedNetworkBuffer#recycleBuffer()} and
     * {@link ReadOnlySlicedNetworkBuffer#isRecycled()}.
     */
    @Test
    public void testForwardsRetainBuffer1() {
        ReadOnlySlicedNetworkBuffer slice = buffer.readOnlySlice();
        Assert.assertEquals(buffer.refCnt(), slice.refCnt());
        slice.retainBuffer();
        Assert.assertEquals(buffer.refCnt(), slice.refCnt());
    }

    /**
     * Tests forwarding of both {@link ReadOnlySlicedNetworkBuffer#retainBuffer()} and
     * {@link ReadOnlySlicedNetworkBuffer#isRecycled()}.
     */
    @Test
    public void testForwardsRetainBuffer2() {
        ReadOnlySlicedNetworkBuffer slice = buffer.readOnlySlice(1, 2);
        Assert.assertEquals(buffer.refCnt(), slice.refCnt());
        slice.retainBuffer();
        Assert.assertEquals(buffer.refCnt(), slice.refCnt());
    }

    @Test
    public void testCreateSlice1() {
        buffer.readByte();// so that we do not start at position 0

        ReadOnlySlicedNetworkBuffer slice1 = buffer.readOnlySlice();
        buffer.readByte();// should not influence the second slice at all

        ReadOnlySlicedNetworkBuffer slice2 = slice1.readOnlySlice();
        Assert.assertSame(buffer, slice2.unwrap().unwrap());
        Assert.assertSame(slice1.getMemorySegment(), slice2.getMemorySegment());
        Assert.assertEquals(1, slice1.getMemorySegmentOffset());
        Assert.assertEquals(slice1.getMemorySegmentOffset(), slice2.getMemorySegmentOffset());
        ReadOnlySlicedBufferTest.assertReadableBytes(slice1, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        ReadOnlySlicedBufferTest.assertReadableBytes(slice2, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void testCreateSlice2() {
        buffer.readByte();// so that we do not start at position 0

        ReadOnlySlicedNetworkBuffer slice1 = buffer.readOnlySlice();
        buffer.readByte();// should not influence the second slice at all

        ReadOnlySlicedNetworkBuffer slice2 = slice1.readOnlySlice(1, 2);
        Assert.assertSame(buffer, slice2.unwrap().unwrap());
        Assert.assertSame(slice1.getMemorySegment(), slice2.getMemorySegment());
        Assert.assertEquals(1, slice1.getMemorySegmentOffset());
        Assert.assertEquals(2, slice2.getMemorySegmentOffset());
        ReadOnlySlicedBufferTest.assertReadableBytes(slice1, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        ReadOnlySlicedBufferTest.assertReadableBytes(slice2, 2, 3);
    }

    @Test
    public void testCreateSlice3() {
        ReadOnlySlicedNetworkBuffer slice1 = buffer.readOnlySlice(1, 2);
        buffer.readByte();// should not influence the second slice at all

        ReadOnlySlicedNetworkBuffer slice2 = slice1.readOnlySlice();
        Assert.assertSame(buffer, slice2.unwrap().unwrap());
        Assert.assertSame(slice1.getMemorySegment(), slice2.getMemorySegment());
        Assert.assertEquals(1, slice1.getMemorySegmentOffset());
        Assert.assertEquals(1, slice2.getMemorySegmentOffset());
        ReadOnlySlicedBufferTest.assertReadableBytes(slice1, 1, 2);
        ReadOnlySlicedBufferTest.assertReadableBytes(slice2, 1, 2);
    }

    @Test
    public void testCreateSlice4() {
        ReadOnlySlicedNetworkBuffer slice1 = buffer.readOnlySlice(1, 5);
        buffer.readByte();// should not influence the second slice at all

        ReadOnlySlicedNetworkBuffer slice2 = slice1.readOnlySlice(1, 2);
        Assert.assertSame(buffer, slice2.unwrap().unwrap());
        Assert.assertSame(slice1.getMemorySegment(), slice2.getMemorySegment());
        Assert.assertEquals(1, slice1.getMemorySegmentOffset());
        Assert.assertEquals(2, slice2.getMemorySegmentOffset());
        ReadOnlySlicedBufferTest.assertReadableBytes(slice1, 1, 2, 3, 4, 5);
        ReadOnlySlicedBufferTest.assertReadableBytes(slice2, 2, 3);
    }

    @Test
    public void testGetMaxCapacity() {
        Assert.assertEquals(ReadOnlySlicedBufferTest.DATA_SIZE, buffer.readOnlySlice().getMaxCapacity());
        Assert.assertEquals(2, buffer.readOnlySlice(1, 2).getMaxCapacity());
    }

    /**
     * Tests the independence of the reader index via
     * {@link ReadOnlySlicedNetworkBuffer#setReaderIndex(int)} and
     * {@link ReadOnlySlicedNetworkBuffer#getReaderIndex()}.
     */
    @Test
    public void testGetSetReaderIndex1() {
        testGetSetReaderIndex(buffer.readOnlySlice());
    }

    /**
     * Tests the independence of the reader index via
     * {@link ReadOnlySlicedNetworkBuffer#setReaderIndex(int)} and
     * {@link ReadOnlySlicedNetworkBuffer#getReaderIndex()}.
     */
    @Test
    public void testGetSetReaderIndex2() {
        testGetSetReaderIndex(buffer.readOnlySlice(1, 2));
    }

    /**
     * Tests the independence of the writer index via
     * {@link ReadOnlySlicedNetworkBuffer#setSize(int)},
     * {@link ReadOnlySlicedNetworkBuffer#getSize()}, and
     * {@link ReadOnlySlicedNetworkBuffer#getSizeUnsafe()}.
     */
    @Test
    public void testGetSetSize1() {
        testGetSetSize(buffer.readOnlySlice(), ReadOnlySlicedBufferTest.DATA_SIZE);
    }

    /**
     * Tests the independence of the writer index via
     * {@link ReadOnlySlicedNetworkBuffer#setSize(int)},
     * {@link ReadOnlySlicedNetworkBuffer#getSize()}, and
     * {@link ReadOnlySlicedNetworkBuffer#getSizeUnsafe()}.
     */
    @Test
    public void testGetSetSize2() {
        testGetSetSize(buffer.readOnlySlice(1, 2), 2);
    }

    @Test
    public void testReadableBytes() {
        Assert.assertEquals(buffer.readableBytes(), buffer.readOnlySlice().readableBytes());
        Assert.assertEquals(2, buffer.readOnlySlice(1, 2).readableBytes());
    }

    @Test
    public void testGetNioBufferReadable1() {
        testGetNioBufferReadable(buffer.readOnlySlice(), ReadOnlySlicedBufferTest.DATA_SIZE);
    }

    @Test
    public void testGetNioBufferReadable2() {
        testGetNioBufferReadable(buffer.readOnlySlice(1, 2), 2);
    }

    @Test
    public void testGetNioBuffer1() {
        testGetNioBuffer(buffer.readOnlySlice(), ReadOnlySlicedBufferTest.DATA_SIZE);
    }

    @Test
    public void testGetNioBuffer2() {
        testGetNioBuffer(buffer.readOnlySlice(1, 2), 2);
    }

    @Test
    public void testGetNioBufferReadableThreadSafe1() {
        NetworkBufferTest.testGetNioBufferReadableThreadSafe(buffer.readOnlySlice());
    }

    @Test
    public void testGetNioBufferReadableThreadSafe2() {
        NetworkBufferTest.testGetNioBufferReadableThreadSafe(buffer.readOnlySlice(1, 2));
    }

    @Test
    public void testGetNioBufferThreadSafe1() {
        NetworkBufferTest.testGetNioBufferThreadSafe(buffer.readOnlySlice(), ReadOnlySlicedBufferTest.DATA_SIZE);
    }

    @Test
    public void testGetNioBufferThreadSafe2() {
        NetworkBufferTest.testGetNioBufferThreadSafe(buffer.readOnlySlice(1, 2), 2);
    }

    @Test
    public void testForwardsSetAllocator() {
        testForwardsSetAllocator(buffer.readOnlySlice());
        testForwardsSetAllocator(buffer.readOnlySlice(1, 2));
    }
}

