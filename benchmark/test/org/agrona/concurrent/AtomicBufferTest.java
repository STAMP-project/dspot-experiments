/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package org.agrona.concurrent;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;


@RunWith(Theories.class)
public class AtomicBufferTest {
    private static final ByteOrder BYTE_ORDER = ByteOrder.nativeOrder();

    private static final int BUFFER_CAPACITY = 4096;

    private static final int INDEX = 8;

    private static final byte BYTE_VALUE = 1;

    private static final short SHORT_VALUE = (Byte.MAX_VALUE) + 2;

    private static final char CHAR_VALUE = '8';

    private static final int INT_VALUE = (Short.MAX_VALUE) + 3;

    private static final float FLOAT_VALUE = (Short.MAX_VALUE) + 4.0F;

    private static final long LONG_VALUE = (Integer.MAX_VALUE) + 5L;

    private static final double DOUBLE_VALUE = (Integer.MAX_VALUE) + 7.0;

    @DataPoint
    public static final AtomicBuffer BYTE_ARRAY_BACKED = new UnsafeBuffer(new byte[AtomicBufferTest.BUFFER_CAPACITY], 0, AtomicBufferTest.BUFFER_CAPACITY);

    @DataPoint
    public static final AtomicBuffer HEAP_BYTE_BUFFER = new UnsafeBuffer(ByteBuffer.allocate(AtomicBufferTest.BUFFER_CAPACITY), 0, AtomicBufferTest.BUFFER_CAPACITY);

    @DataPoint
    public static final AtomicBuffer DIRECT_BYTE_BUFFER = new UnsafeBuffer(ByteBuffer.allocateDirect(AtomicBufferTest.BUFFER_CAPACITY), 0, AtomicBufferTest.BUFFER_CAPACITY);

    @DataPoint
    public static final AtomicBuffer HEAP_BYTE_BUFFER_SLICE = new UnsafeBuffer(((ByteBuffer) (ByteBuffer.allocate(((AtomicBufferTest.BUFFER_CAPACITY) * 2)).position(AtomicBufferTest.BUFFER_CAPACITY))).slice());

    @DataPoint
    public static final AtomicBuffer HEAP_BYTE_BUFFER_READ_ONLY = new UnsafeBuffer(ByteBuffer.allocate(AtomicBufferTest.BUFFER_CAPACITY).asReadOnlyBuffer(), 0, AtomicBufferTest.BUFFER_CAPACITY);

    @Test
    public void sharedBuffer() {
        final ByteBuffer bb = ByteBuffer.allocateDirect(1024);
        final UnsafeBuffer ub1 = new UnsafeBuffer(bb, 0, 512);
        final UnsafeBuffer ub2 = new UnsafeBuffer(bb, 512, 512);
        ub1.putLong(AtomicBufferTest.INDEX, AtomicBufferTest.LONG_VALUE);
        ub2.putLong(AtomicBufferTest.INDEX, 9876543210L);
        Assert.assertThat(ub1.getLong(AtomicBufferTest.INDEX), CoreMatchers.is(AtomicBufferTest.LONG_VALUE));
    }

    @Test
    public void shouldVerifyBufferAlignment() {
        final AtomicBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));
        try {
            buffer.verifyAlignment();
        } catch (final IllegalStateException ex) {
            Assert.fail(("All buffers should be aligned " + ex));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenBufferNotAligned() {
        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        byteBuffer.position(1);
        final UnsafeBuffer buffer = new UnsafeBuffer(byteBuffer.slice());
        buffer.verifyAlignment();
    }
}

