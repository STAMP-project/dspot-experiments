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


import org.apache.flink.runtime.io.network.netty.NettyBufferPool;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link NetworkBuffer} class.
 */
public class NetworkBufferTest extends AbstractByteBufTest {
    /**
     * Upper limit for the max size that is sufficient for all the tests.
     */
    private static final int MAX_CAPACITY_UPPER_BOUND = (64 * 1024) * 1024;

    private static final NettyBufferPool NETTY_BUFFER_POOL = new NettyBufferPool(1);

    @Test
    public void testDataBufferIsBuffer() {
        Assert.assertFalse(NetworkBufferTest.newBuffer(1024, 1024, false).isBuffer());
    }

    @Test
    public void testEventBufferIsBuffer() {
        Assert.assertFalse(NetworkBufferTest.newBuffer(1024, 1024, false).isBuffer());
    }

    @Test
    public void testDataBufferTagAsEvent() {
        NetworkBufferTest.testTagAsEvent(true);
    }

    @Test
    public void testEventBufferTagAsEvent() {
        NetworkBufferTest.testTagAsEvent(false);
    }

    @Test
    public void testDataBufferGetMemorySegment() {
        NetworkBufferTest.testGetMemorySegment(true);
    }

    @Test
    public void testEventBufferGetMemorySegment() {
        NetworkBufferTest.testGetMemorySegment(false);
    }

    @Test
    public void testDataBufferGetRecycler() {
        NetworkBufferTest.testGetRecycler(true);
    }

    @Test
    public void testEventBufferGetRecycler() {
        NetworkBufferTest.testGetRecycler(false);
    }

    @Test
    public void testDataBufferRecycleBuffer() {
        NetworkBufferTest.testRecycleBuffer(true);
    }

    @Test
    public void testEventBufferRecycleBuffer() {
        NetworkBufferTest.testRecycleBuffer(false);
    }

    @Test
    public void testDataBufferRetainBuffer() {
        NetworkBufferTest.testRetainBuffer(true);
    }

    @Test
    public void testEventBufferRetainBuffer() {
        NetworkBufferTest.testRetainBuffer(false);
    }

    @Test
    public void testDataBufferCreateSlice1() {
        NetworkBufferTest.testCreateSlice1(true);
    }

    @Test
    public void testEventBufferCreateSlice1() {
        NetworkBufferTest.testCreateSlice1(false);
    }

    @Test
    public void testDataBufferCreateSlice2() {
        NetworkBufferTest.testCreateSlice2(true);
    }

    @Test
    public void testEventBufferCreateSlice2() {
        NetworkBufferTest.testCreateSlice2(false);
    }

    @Test
    public void testDataBufferGetMaxCapacity() {
        NetworkBufferTest.testGetMaxCapacity(true);
    }

    @Test
    public void testEventBufferGetMaxCapacity() {
        NetworkBufferTest.testGetMaxCapacity(false);
    }

    @Test
    public void testDataBufferGetSetReaderIndex() {
        NetworkBufferTest.testGetSetReaderIndex(true);
    }

    @Test
    public void testEventBufferGetSetReaderIndex() {
        NetworkBufferTest.testGetSetReaderIndex(false);
    }

    @Test
    public void testDataBufferSetGetSize() {
        NetworkBufferTest.testSetGetSize(true);
    }

    @Test
    public void testEventBufferSetGetSize() {
        NetworkBufferTest.testSetGetSize(false);
    }

    @Test
    public void testDataBufferReadableBytes() {
        NetworkBufferTest.testReadableBytes(true);
    }

    @Test
    public void testEventBufferReadableBytes() {
        NetworkBufferTest.testReadableBytes(false);
    }

    @Test
    public void testDataBufferGetNioBufferReadable() {
        testGetNioBufferReadable(true);
    }

    @Test
    public void testEventBufferGetNioBufferReadable() {
        testGetNioBufferReadable(false);
    }

    @Test
    public void testGetNioBufferReadableThreadSafe() {
        NetworkBuffer buffer = newBuffer(1024, 1024);
        NetworkBufferTest.testGetNioBufferReadableThreadSafe(buffer);
    }

    @Test
    public void testDataBufferGetNioBuffer() {
        testGetNioBuffer(true);
    }

    @Test
    public void testEventBufferGetNioBuffer() {
        testGetNioBuffer(false);
    }

    @Test
    public void testGetNioBufferThreadSafe() {
        NetworkBuffer buffer = newBuffer(1024, 1024);
        NetworkBufferTest.testGetNioBufferThreadSafe(buffer, 10);
    }

    @Test
    public void testDataBufferSetAllocator() {
        testSetAllocator(true);
    }

    @Test
    public void testEventBufferSetAllocator() {
        testSetAllocator(false);
    }
}

