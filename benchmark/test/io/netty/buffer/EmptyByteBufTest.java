/**
 * Copyright 2013 The Netty Project
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


import EmptyByteBuf.EMPTY_BYTE_BUF_HASH_CODE;
import Unpooled.EMPTY_BUFFER;
import UnpooledByteBufAllocator.DEFAULT;
import org.junit.Assert;
import org.junit.Test;

import static UnpooledByteBufAllocator.DEFAULT;


public class EmptyByteBufTest {
    @Test
    public void testIsWritable() {
        EmptyByteBuf empty = new EmptyByteBuf(DEFAULT);
        Assert.assertFalse(empty.isWritable());
        Assert.assertFalse(empty.isWritable(1));
    }

    @Test
    public void testWriteEmptyByteBuf() {
        EmptyByteBuf empty = new EmptyByteBuf(DEFAULT);
        empty.writeBytes(EMPTY_BUFFER);// Ok

        ByteBuf nonEmpty = DEFAULT.buffer().writeBoolean(false);
        try {
            empty.writeBytes(nonEmpty);
            Assert.fail();
        } catch (IndexOutOfBoundsException ignored) {
            // Ignore.
        } finally {
            nonEmpty.release();
        }
    }

    @Test
    public void testIsReadable() {
        EmptyByteBuf empty = new EmptyByteBuf(DEFAULT);
        Assert.assertFalse(empty.isReadable());
        Assert.assertFalse(empty.isReadable(1));
    }

    @Test
    public void testArray() {
        EmptyByteBuf empty = new EmptyByteBuf(DEFAULT);
        Assert.assertThat(empty.hasArray(), is(true));
        Assert.assertThat(empty.array().length, is(0));
        Assert.assertThat(empty.arrayOffset(), is(0));
    }

    @Test
    public void testNioBuffer() {
        EmptyByteBuf empty = new EmptyByteBuf(DEFAULT);
        Assert.assertThat(empty.nioBufferCount(), is(1));
        Assert.assertThat(empty.nioBuffer().position(), is(0));
        Assert.assertThat(empty.nioBuffer().limit(), is(0));
        Assert.assertThat(empty.nioBuffer(), is(sameInstance(empty.nioBuffer())));
        Assert.assertThat(empty.nioBuffer(), is(sameInstance(empty.internalNioBuffer(empty.readerIndex(), 0))));
    }

    @Test
    public void testMemoryAddress() {
        EmptyByteBuf empty = new EmptyByteBuf(DEFAULT);
        if (empty.hasMemoryAddress()) {
            Assert.assertThat(empty.memoryAddress(), is(not(0L)));
        } else {
            try {
                empty.memoryAddress();
                Assert.fail();
            } catch (UnsupportedOperationException ignored) {
                // Ignore.
            }
        }
    }

    @Test
    public void consistentEqualsAndHashCodeWithAbstractBytebuf() {
        ByteBuf empty = new EmptyByteBuf(DEFAULT);
        ByteBuf emptyAbstract = new UnpooledHeapByteBuf(DEFAULT, 0, 0);
        Assert.assertEquals(emptyAbstract, empty);
        Assert.assertEquals(emptyAbstract.hashCode(), empty.hashCode());
        Assert.assertEquals(EMPTY_BYTE_BUF_HASH_CODE, empty.hashCode());
        Assert.assertTrue(emptyAbstract.release());
        Assert.assertFalse(empty.release());
    }
}

