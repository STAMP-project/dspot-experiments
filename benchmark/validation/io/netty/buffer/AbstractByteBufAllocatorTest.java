/**
 * Copyright 2017 The Netty Project
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


import io.netty.util.internal.PlatformDependent;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractByteBufAllocatorTest<T extends AbstractByteBufAllocator> extends ByteBufAllocatorTest {
    @Test
    public void testCalculateNewCapacity() {
        testCalculateNewCapacity(true);
        testCalculateNewCapacity(false);
    }

    @Test
    public void testUnsafeHeapBufferAndUnsafeDirectBuffer() {
        T allocator = newUnpooledAllocator();
        ByteBuf directBuffer = allocator.directBuffer();
        AbstractByteBufAllocatorTest.assertInstanceOf(directBuffer, (PlatformDependent.hasUnsafe() ? UnpooledUnsafeDirectByteBuf.class : UnpooledDirectByteBuf.class));
        directBuffer.release();
        ByteBuf heapBuffer = allocator.heapBuffer();
        AbstractByteBufAllocatorTest.assertInstanceOf(heapBuffer, (PlatformDependent.hasUnsafe() ? UnpooledUnsafeHeapByteBuf.class : UnpooledHeapByteBuf.class));
        heapBuffer.release();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUsedDirectMemory() {
        T allocator = newAllocator(true);
        ByteBufAllocatorMetric metric = metric();
        Assert.assertEquals(0, metric.usedDirectMemory());
        ByteBuf buffer = directBuffer(1024, 4096);
        int capacity = buffer.capacity();
        Assert.assertEquals(expectedUsedMemory(allocator, capacity), metric.usedDirectMemory());
        // Double the size of the buffer
        buffer.capacity((capacity << 1));
        capacity = buffer.capacity();
        Assert.assertEquals(buffer.toString(), expectedUsedMemory(allocator, capacity), metric.usedDirectMemory());
        buffer.release();
        Assert.assertEquals(expectedUsedMemoryAfterRelease(allocator, capacity), metric.usedDirectMemory());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUsedHeapMemory() {
        T allocator = newAllocator(true);
        ByteBufAllocatorMetric metric = metric();
        Assert.assertEquals(0, metric.usedHeapMemory());
        ByteBuf buffer = heapBuffer(1024, 4096);
        int capacity = buffer.capacity();
        Assert.assertEquals(expectedUsedMemory(allocator, capacity), metric.usedHeapMemory());
        // Double the size of the buffer
        buffer.capacity((capacity << 1));
        capacity = buffer.capacity();
        Assert.assertEquals(expectedUsedMemory(allocator, capacity), metric.usedHeapMemory());
        buffer.release();
        Assert.assertEquals(expectedUsedMemoryAfterRelease(allocator, capacity), metric.usedHeapMemory());
    }
}

