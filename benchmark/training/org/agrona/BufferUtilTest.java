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
package org.agrona;


import java.nio.ByteBuffer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class BufferUtilTest {
    @Test(expected = IllegalArgumentException.class)
    public void shouldDetectNonPowerOfTwoAlignment() {
        BufferUtil.allocateDirectAligned(1, 3);
    }

    @Test
    public void shouldAlignToWordBoundary() {
        final int capacity = 128;
        final ByteBuffer byteBuffer = BufferUtil.allocateDirectAligned(capacity, BitUtil.SIZE_OF_LONG);
        final long address = BufferUtil.address(byteBuffer);
        Assert.assertTrue(BitUtil.isAligned(address, BitUtil.SIZE_OF_LONG));
        Assert.assertThat(byteBuffer.capacity(), CoreMatchers.is(capacity));
    }

    @Test
    public void shouldAlignToCacheLineBoundary() {
        final int capacity = 128;
        final ByteBuffer byteBuffer = BufferUtil.allocateDirectAligned(capacity, BitUtil.CACHE_LINE_LENGTH);
        final long address = BufferUtil.address(byteBuffer);
        Assert.assertTrue(BitUtil.isAligned(address, BitUtil.CACHE_LINE_LENGTH));
        Assert.assertThat(byteBuffer.capacity(), CoreMatchers.is(capacity));
    }
}

