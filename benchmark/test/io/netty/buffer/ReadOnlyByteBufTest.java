/**
 * Copyright 2012 The Netty Project
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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests read-only channel buffers
 */
public class ReadOnlyByteBufTest {
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullInConstructor() {
        new ReadOnlyByteBuf(null);
    }

    @Test
    public void testUnmodifiableBuffer() {
        Assert.assertTrue(((Unpooled.unmodifiableBuffer(Unpooled.buffer(1))) instanceof ReadOnlyByteBuf));
    }

    @Test
    public void testUnwrap() {
        ByteBuf buf = Unpooled.buffer(1);
        Assert.assertSame(buf, Unpooled.unmodifiableBuffer(buf).unwrap());
    }

    @Test
    public void shouldHaveSameByteOrder() {
        ByteBuf buf = Unpooled.buffer(1);
        Assert.assertSame(Unpooled.BIG_ENDIAN, Unpooled.unmodifiableBuffer(buf).order());
        buf = buf.order(Unpooled.LITTLE_ENDIAN);
        Assert.assertSame(Unpooled.LITTLE_ENDIAN, Unpooled.unmodifiableBuffer(buf).order());
    }

    @Test
    public void shouldReturnReadOnlyDerivedBuffer() {
        ByteBuf buf = Unpooled.unmodifiableBuffer(Unpooled.buffer(1));
        Assert.assertTrue(((buf.duplicate()) instanceof ReadOnlyByteBuf));
        Assert.assertTrue(((buf.slice()) instanceof ReadOnlyByteBuf));
        Assert.assertTrue(((buf.slice(0, 1)) instanceof ReadOnlyByteBuf));
        Assert.assertTrue(((buf.duplicate()) instanceof ReadOnlyByteBuf));
    }

    @Test
    public void shouldReturnWritableCopy() {
        ByteBuf buf = Unpooled.unmodifiableBuffer(Unpooled.buffer(1));
        Assert.assertFalse(((buf.copy()) instanceof ReadOnlyByteBuf));
    }

    @Test
    public void shouldForwardReadCallsBlindly() throws Exception {
        ByteBuf buf = Mockito.mock(ByteBuf.class);
        Mockito.when(buf.order()).thenReturn(Unpooled.BIG_ENDIAN);
        Mockito.when(buf.maxCapacity()).thenReturn(65536);
        Mockito.when(buf.readerIndex()).thenReturn(0);
        Mockito.when(buf.writerIndex()).thenReturn(0);
        Mockito.when(buf.capacity()).thenReturn(0);
        Mockito.when(buf.getBytes(1, ((GatheringByteChannel) (null)), 2)).thenReturn(3);
        Mockito.when(buf.getBytes(4, ((OutputStream) (null)), 5)).thenReturn(buf);
        Mockito.when(buf.getBytes(6, ((byte[]) (null)), 7, 8)).thenReturn(buf);
        Mockito.when(buf.getBytes(9, ((ByteBuf) (null)), 10, 11)).thenReturn(buf);
        Mockito.when(buf.getBytes(12, ((ByteBuffer) (null)))).thenReturn(buf);
        Mockito.when(buf.getByte(13)).thenReturn(Byte.valueOf(((byte) (14))));
        Mockito.when(buf.getShort(15)).thenReturn(Short.valueOf(((short) (16))));
        Mockito.when(buf.getUnsignedMedium(17)).thenReturn(18);
        Mockito.when(buf.getInt(19)).thenReturn(20);
        Mockito.when(buf.getLong(21)).thenReturn(22L);
        ByteBuffer bb = ByteBuffer.allocate(100);
        Mockito.when(buf.nioBuffer(23, 24)).thenReturn(bb);
        Mockito.when(buf.capacity()).thenReturn(27);
        ByteBuf roBuf = Unpooled.unmodifiableBuffer(buf);
        Assert.assertEquals(3, roBuf.getBytes(1, ((GatheringByteChannel) (null)), 2));
        roBuf.getBytes(4, ((OutputStream) (null)), 5);
        roBuf.getBytes(6, ((byte[]) (null)), 7, 8);
        roBuf.getBytes(9, ((ByteBuf) (null)), 10, 11);
        roBuf.getBytes(12, ((ByteBuffer) (null)));
        Assert.assertEquals(((byte) (14)), roBuf.getByte(13));
        Assert.assertEquals(((short) (16)), roBuf.getShort(15));
        Assert.assertEquals(18, roBuf.getUnsignedMedium(17));
        Assert.assertEquals(20, roBuf.getInt(19));
        Assert.assertEquals(22L, roBuf.getLong(21));
        ByteBuffer roBB = roBuf.nioBuffer(23, 24);
        Assert.assertEquals(100, roBB.capacity());
        Assert.assertTrue(roBB.isReadOnly());
        Assert.assertEquals(27, roBuf.capacity());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectDiscardReadBytes() {
        Unpooled.unmodifiableBuffer(Unpooled.EMPTY_BUFFER).discardReadBytes();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetByte() {
        Unpooled.unmodifiableBuffer(Unpooled.EMPTY_BUFFER).setByte(0, ((byte) (0)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetShort() {
        Unpooled.unmodifiableBuffer(Unpooled.EMPTY_BUFFER).setShort(0, ((short) (0)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetMedium() {
        Unpooled.unmodifiableBuffer(Unpooled.EMPTY_BUFFER).setMedium(0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetInt() {
        Unpooled.unmodifiableBuffer(Unpooled.EMPTY_BUFFER).setInt(0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetLong() {
        Unpooled.unmodifiableBuffer(Unpooled.EMPTY_BUFFER).setLong(0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetBytes1() throws IOException {
        Unpooled.unmodifiableBuffer(Unpooled.EMPTY_BUFFER).setBytes(0, ((InputStream) (null)), 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetBytes2() throws IOException {
        Unpooled.unmodifiableBuffer(Unpooled.EMPTY_BUFFER).setBytes(0, ((ScatteringByteChannel) (null)), 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetBytes3() {
        Unpooled.unmodifiableBuffer(Unpooled.EMPTY_BUFFER).setBytes(0, ((byte[]) (null)), 0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetBytes4() {
        Unpooled.unmodifiableBuffer(Unpooled.EMPTY_BUFFER).setBytes(0, ((ByteBuf) (null)), 0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetBytes5() {
        Unpooled.unmodifiableBuffer(Unpooled.EMPTY_BUFFER).setBytes(0, ((ByteBuffer) (null)));
    }

    @Test
    public void shouldIndicateNotWritable() {
        Assert.assertFalse(Unpooled.unmodifiableBuffer(Unpooled.buffer(1)).isWritable());
    }

    @Test
    public void shouldIndicateNotWritableAnyNumber() {
        Assert.assertFalse(Unpooled.unmodifiableBuffer(Unpooled.buffer(1)).isWritable(1));
    }

    @Test
    public void ensureWritableIntStatusShouldFailButNotThrow() {
        ReadOnlyByteBufTest.ensureWritableIntStatusShouldFailButNotThrow(false);
    }

    @Test
    public void ensureWritableForceIntStatusShouldFailButNotThrow() {
        ReadOnlyByteBufTest.ensureWritableIntStatusShouldFailButNotThrow(true);
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void ensureWritableShouldThrow() {
        ByteBuf buf = Unpooled.buffer(1);
        ByteBuf readOnly = buf.asReadOnly();
        try {
            readOnly.ensureWritable(1);
            Assert.fail();
        } finally {
            buf.release();
        }
    }

    @Test
    public void asReadOnly() {
        ByteBuf buf = Unpooled.buffer(1);
        ByteBuf readOnly = buf.asReadOnly();
        Assert.assertTrue(readOnly.isReadOnly());
        Assert.assertSame(readOnly, readOnly.asReadOnly());
        readOnly.release();
    }
}

