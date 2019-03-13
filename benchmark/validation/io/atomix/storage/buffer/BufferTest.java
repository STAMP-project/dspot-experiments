/**
 * Copyright 2015-present Open Networking Foundation
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
package io.atomix.storage.buffer;


import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;


/**
 * Base buffer test.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public abstract class BufferTest {
    @Test
    public void testPosition() {
        Buffer buffer = createBuffer(8);
        Assert.assertEquals(0, buffer.position());
        buffer.writeInt(10);
        Assert.assertEquals(4, buffer.position());
        buffer.position(0);
        Assert.assertEquals(0, buffer.position());
        Assert.assertEquals(10, buffer.readInt());
    }

    @Test
    public void testFlip() {
        Buffer buffer = createBuffer(8);
        buffer.writeInt(10);
        Assert.assertEquals(4, buffer.position());
        Assert.assertEquals(8, buffer.capacity());
        Assert.assertEquals((-1), buffer.limit());
        Assert.assertEquals(8, buffer.capacity());
        buffer.flip();
        Assert.assertEquals(4, buffer.limit());
        Assert.assertEquals(0, buffer.position());
    }

    @Test
    public void testLimit() {
        Buffer buffer = createBuffer(8);
        Assert.assertEquals(0, buffer.position());
        Assert.assertEquals((-1), buffer.limit());
        Assert.assertEquals(8, buffer.capacity());
        buffer.limit(4);
        Assert.assertEquals(4, buffer.limit());
        Assert.assertTrue(buffer.hasRemaining());
        buffer.writeInt(10);
        Assert.assertEquals(0, buffer.remaining());
        Assert.assertFalse(buffer.hasRemaining());
    }

    @Test
    public void testClear() {
        Buffer buffer = createBuffer(8);
        buffer.limit(6);
        Assert.assertEquals(6, buffer.limit());
        buffer.writeInt(10);
        Assert.assertEquals(4, buffer.position());
        buffer.clear();
        Assert.assertEquals((-1), buffer.limit());
        Assert.assertEquals(8, buffer.capacity());
        Assert.assertEquals(0, buffer.position());
    }

    @Test
    public void testMarkReset() {
        Assert.assertTrue(createBuffer(12).writeInt(10).mark().writeBoolean(true).reset().readBoolean());
    }

    @Test(expected = BufferUnderflowException.class)
    public void testReadIntThrowsBufferUnderflowWithNoRemainingBytesRelative() {
        createBuffer(4, 4).writeInt(10).readInt();
    }

    @Test(expected = BufferUnderflowException.class)
    public void testReadIntThrowsBufferUnderflowWithNoRemainingBytesAbsolute() {
        createBuffer(4, 4).readInt(2);
    }

    @Test(expected = BufferOverflowException.class)
    public void testWriteIntThrowsBufferOverflowWithNoRemainingBytesRelative() {
        createBuffer(4, 4).writeInt(10).writeInt(20);
    }

    @Test(expected = BufferOverflowException.class)
    public void testReadIntThrowsBufferOverflowWithNoRemainingBytesAbsolute() {
        createBuffer(4, 4).writeInt(4, 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testReadIntThrowsIndexOutOfBounds() {
        createBuffer(4, 4).readInt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testWriteIntThrowsIndexOutOfBounds() {
        createBuffer(4, 4).writeInt(10, 10);
    }

    @Test
    public void testWriteReadByteRelative() {
        Assert.assertEquals(10, createBuffer(16).writeByte(10).flip().readByte());
    }

    @Test
    public void testWriteReadByteAbsolute() {
        Assert.assertEquals(10, createBuffer(16).writeByte(4, 10).readByte(4));
    }

    @Test
    public void testWriteReadUnsignedByteRelative() {
        Assert.assertEquals(10, createBuffer(16).writeUnsignedByte(10).flip().readUnsignedByte());
    }

    @Test
    public void testWriteReadUnsignedByteAbsolute() {
        Assert.assertEquals(10, createBuffer(16).writeUnsignedByte(4, 10).readUnsignedByte(4));
    }

    @Test
    public void testWriteReadShortRelative() {
        Assert.assertEquals(10, createBuffer(16).writeShort(((short) (10))).flip().readShort());
    }

    @Test
    public void testWriteReadShortAbsolute() {
        Assert.assertEquals(10, createBuffer(16).writeShort(4, ((short) (10))).readShort(4));
    }

    @Test
    public void testWriteReadUnsignedShortRelative() {
        Assert.assertEquals(10, createBuffer(16).writeUnsignedShort(((short) (10))).flip().readUnsignedShort());
    }

    @Test
    public void testWriteReadUnsignedShortAbsolute() {
        Assert.assertEquals(10, createBuffer(16).writeUnsignedShort(4, ((short) (10))).readUnsignedShort(4));
    }

    @Test
    public void testWriteReadIntRelative() {
        Assert.assertEquals(10, createBuffer(16).writeInt(10).flip().readInt());
    }

    @Test
    public void testWriteReadUnsignedIntAbsolute() {
        Assert.assertEquals(10, createBuffer(16).writeUnsignedInt(4, 10).readUnsignedInt(4));
    }

    @Test
    public void testWriteReadUnsignedIntRelative() {
        Assert.assertEquals(10, createBuffer(16).writeUnsignedInt(10).flip().readUnsignedInt());
    }

    @Test
    public void testWriteReadIntAbsolute() {
        Assert.assertEquals(10, createBuffer(16).writeInt(4, 10).readInt(4));
    }

    @Test
    public void testWriteReadLongRelative() {
        Assert.assertEquals(12345, createBuffer(16).writeLong(12345).flip().readLong());
    }

    @Test
    public void testWriteReadLongAbsolute() {
        Assert.assertEquals(12345, createBuffer(16).writeLong(4, 12345).readLong(4));
    }

    @Test
    public void testWriteReadFloatRelative() {
        Assert.assertEquals(10.6F, createBuffer(16).writeFloat(10.6F).flip().readFloat(), 0.001);
    }

    @Test
    public void testWriteReadFloatAbsolute() {
        Assert.assertEquals(10.6F, createBuffer(16).writeFloat(4, 10.6F).readFloat(4), 0.001);
    }

    @Test
    public void testWriteReadDoubleRelative() {
        Assert.assertEquals(10.6, createBuffer(16).writeDouble(10.6).flip().readDouble(), 0.001);
    }

    @Test
    public void testWriteReadDoubleAbsolute() {
        Assert.assertEquals(10.6, createBuffer(16).writeDouble(4, 10.6).readDouble(4), 0.001);
    }

    @Test
    public void testWriteReadBooleanRelative() {
        Assert.assertTrue(createBuffer(16).writeBoolean(true).flip().readBoolean());
    }

    @Test
    public void testWriteReadBooleanAbsolute() {
        Assert.assertTrue(createBuffer(16).writeBoolean(4, true).readBoolean(4));
    }

    @Test
    public void testWriteReadStringRelative() {
        Buffer buffer = createBuffer(38).writeString("Hello world!").writeString("Hello world again!").flip();
        Assert.assertEquals("Hello world!", buffer.readString());
        Assert.assertEquals("Hello world again!", buffer.readString());
    }

    @Test
    public void testWriteReadStringAbsolute() {
        Buffer buffer = createBuffer(46).writeString(4, "Hello world!").writeString(20, "Hello world again!");
        Assert.assertEquals("Hello world!", buffer.readString(4));
        Assert.assertEquals("Hello world again!", buffer.readString(20));
    }

    @Test
    public void testWriteReadUTF8Relative() {
        Buffer buffer = createBuffer(38).writeUTF8("Hello world!").writeUTF8("Hello world again!").flip();
        Assert.assertEquals("Hello world!", buffer.readUTF8());
        Assert.assertEquals("Hello world again!", buffer.readUTF8());
    }

    @Test
    public void testWriteReadUTF8Absolute() {
        Buffer buffer = createBuffer(46).writeUTF8(4, "Hello world!").writeUTF8(20, "Hello world again!");
        Assert.assertEquals("Hello world!", buffer.readUTF8(4));
        Assert.assertEquals("Hello world again!", buffer.readUTF8(20));
    }

    @Test
    public void testReadWriter() {
        Buffer writeBuffer = createBuffer(8).writeLong(10).flip();
        Buffer readBuffer = createBuffer(8);
        writeBuffer.read(readBuffer);
        Assert.assertEquals(10, readBuffer.flip().readLong());
    }

    @Test
    public void testWriteReadSwappedIntRelative() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeInt(10).flip().readInt());
    }

    @Test
    public void testWriteReadSwappedIntAbsolute() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeInt(4, 10).readInt(4));
    }

    @Test
    public void testAbsoluteSlice() {
        Buffer buffer = createBuffer(1024);
        buffer.writeLong(10).writeLong(11).rewind();
        Buffer slice = buffer.slice(8, 1016);
        Assert.assertEquals(0, slice.position());
        Assert.assertEquals(11, slice.readLong());
    }

    @Test
    public void testRelativeSliceWithoutLength() {
        Buffer buffer = createBuffer(1024, 1024);
        buffer.writeLong(10).writeLong(11).writeLong(12).rewind();
        Assert.assertEquals(10, buffer.readLong());
        Buffer slice = buffer.slice();
        Assert.assertEquals(0, slice.position());
        Assert.assertEquals((-1), slice.limit());
        Assert.assertEquals(1016, slice.capacity());
        Assert.assertEquals(1016, slice.maxCapacity());
        Assert.assertEquals(11, slice.readLong());
        Assert.assertEquals(11, slice.readLong(0));
        slice.close();
        Buffer slice2 = buffer.skip(8).slice();
        Assert.assertEquals(0, slice2.position());
        Assert.assertEquals((-1), slice2.limit());
        Assert.assertEquals(1008, slice2.capacity());
        Assert.assertEquals(1008, slice2.maxCapacity());
        Assert.assertEquals(12, slice2.readLong());
        Assert.assertEquals(12, slice2.readLong(0));
    }

    @Test
    public void testRelativeSliceWithLength() {
        Buffer buffer = createBuffer(1024);
        buffer.writeLong(10).writeLong(11).writeLong(12).rewind();
        Assert.assertEquals(10, buffer.readLong());
        Buffer slice = buffer.slice(8);
        Assert.assertEquals(0, slice.position());
        Assert.assertEquals(11, slice.readLong());
        Assert.assertEquals(11, slice.readLong(0));
        slice.close();
        Buffer slice2 = buffer.skip(8).slice(8);
        Assert.assertEquals(0, slice2.position());
        Assert.assertEquals(12, slice2.readLong());
        Assert.assertEquals(12, slice2.readLong(0));
        slice2.close();
    }

    @Test
    public void testSliceOfSlice() {
        Buffer buffer = createBuffer(1024);
        buffer.writeLong(10).writeLong(11).writeLong(12).rewind();
        Assert.assertEquals(10, buffer.readLong());
        Buffer slice = buffer.slice();
        Assert.assertEquals(11, slice.readLong());
        Buffer sliceOfSlice = slice.slice();
        Assert.assertEquals(12, sliceOfSlice.readLong());
        Assert.assertEquals(8, sliceOfSlice.position());
    }

    @Test
    public void testSliceWithLimit() {
        Buffer buffer = createBuffer(1024).limit(16);
        buffer.writeLong(10);
        Buffer slice = buffer.slice();
        Assert.assertEquals(0, slice.position());
        Assert.assertEquals(8, slice.capacity());
        Assert.assertEquals(8, slice.maxCapacity());
        Assert.assertEquals(8, slice.remaining());
    }

    @Test
    public void testSliceWithLittleRemaining() {
        Buffer buffer = createBuffer(1024, 2048);
        buffer.position(1020);
        Buffer slice = buffer.slice(8);
        Assert.assertEquals(0, slice.position());
        Assert.assertEquals((-1), slice.limit());
    }

    @Test
    public void testCompact() {
        Buffer buffer = createBuffer(1024);
        buffer.position(100).writeLong(1234).position(100).compact();
        Assert.assertEquals(0, buffer.position());
        Assert.assertEquals(1234, buffer.readLong());
    }

    @Test
    public void testSwappedPosition() {
        Buffer buffer = createBuffer(8).order(ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(0, buffer.position());
        buffer.writeInt(10);
        Assert.assertEquals(4, buffer.position());
        buffer.position(0);
        Assert.assertEquals(0, buffer.position());
        Assert.assertEquals(10, buffer.readInt());
    }

    @Test
    public void testSwappedFlip() {
        Buffer buffer = createBuffer(8).order(ByteOrder.LITTLE_ENDIAN);
        buffer.writeInt(10);
        Assert.assertEquals(4, buffer.position());
        Assert.assertEquals(8, buffer.capacity());
        Assert.assertEquals((-1), buffer.limit());
        Assert.assertEquals(8, buffer.capacity());
        buffer.flip();
        Assert.assertEquals(4, buffer.limit());
        Assert.assertEquals(0, buffer.position());
    }

    @Test
    public void testSwappedLimit() {
        Buffer buffer = createBuffer(8).order(ByteOrder.LITTLE_ENDIAN);
        Assert.assertEquals(0, buffer.position());
        Assert.assertEquals((-1), buffer.limit());
        Assert.assertEquals(8, buffer.capacity());
        buffer.limit(4);
        Assert.assertEquals(4, buffer.limit());
        Assert.assertTrue(buffer.hasRemaining());
        buffer.writeInt(10);
        Assert.assertEquals(0, buffer.remaining());
        Assert.assertFalse(buffer.hasRemaining());
    }

    @Test
    public void testSwappedClear() {
        Buffer buffer = createBuffer(8).order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(6);
        Assert.assertEquals(6, buffer.limit());
        buffer.writeInt(10);
        Assert.assertEquals(4, buffer.position());
        buffer.clear();
        Assert.assertEquals((-1), buffer.limit());
        Assert.assertEquals(8, buffer.capacity());
        Assert.assertEquals(0, buffer.position());
    }

    @Test
    public void testSwappedMarkReset() {
        Assert.assertTrue(createBuffer(12).order(ByteOrder.LITTLE_ENDIAN).writeInt(10).mark().writeBoolean(true).reset().readBoolean());
    }

    @Test(expected = BufferUnderflowException.class)
    public void testSwappedReadIntThrowsBufferUnderflowWithNoRemainingBytesRelative() {
        createBuffer(4, 4).order(ByteOrder.LITTLE_ENDIAN).writeInt(10).readInt();
    }

    @Test(expected = BufferUnderflowException.class)
    public void testSwappedReadIntThrowsBufferUnderflowWithNoRemainingBytesAbsolute() {
        createBuffer(4, 4).order(ByteOrder.LITTLE_ENDIAN).readInt(2);
    }

    @Test(expected = BufferOverflowException.class)
    public void testSwappedWriteIntThrowsBufferOverflowWithNoRemainingBytesRelative() {
        createBuffer(4, 4).order(ByteOrder.LITTLE_ENDIAN).writeInt(10).writeInt(20);
    }

    @Test(expected = BufferOverflowException.class)
    public void testSwappedReadIntThrowsBufferOverflowWithNoRemainingBytesAbsolute() {
        createBuffer(4, 4).order(ByteOrder.LITTLE_ENDIAN).writeInt(4, 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSwappedReadIntThrowsIndexOutOfBounds() {
        createBuffer(4, 4).order(ByteOrder.LITTLE_ENDIAN).readInt(10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSwappedWriteIntThrowsIndexOutOfBounds() {
        createBuffer(4, 4).order(ByteOrder.LITTLE_ENDIAN).writeInt(10, 10);
    }

    @Test
    public void testSwappedWriteReadByteRelative() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeByte(10).flip().readByte());
    }

    @Test
    public void testSwappedWriteReadByteAbsolute() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeByte(4, 10).readByte(4));
    }

    @Test
    public void testSwappedWriteReadUnsignedByteRelative() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeUnsignedByte(10).flip().readUnsignedByte());
    }

    @Test
    public void testSwappedWriteReadUnsignedByteAbsolute() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeUnsignedByte(4, 10).readUnsignedByte(4));
    }

    @Test
    public void testSwappedWriteReadShortRelative() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeShort(((short) (10))).flip().readShort());
    }

    @Test
    public void testSwappedWriteReadShortAbsolute() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeShort(4, ((short) (10))).readShort(4));
    }

    @Test
    public void testSwappedWriteReadUnsignedShortRelative() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeUnsignedShort(((short) (10))).flip().readUnsignedShort());
    }

    @Test
    public void testSwappedWriteReadUnsignedShortAbsolute() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeUnsignedShort(4, ((short) (10))).readUnsignedShort(4));
    }

    @Test
    public void testSwappedWriteReadIntRelative() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeInt(10).flip().readInt());
    }

    @Test
    public void testSwappedWriteReadUnsignedIntAbsolute() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeUnsignedInt(4, 10).readUnsignedInt(4));
    }

    @Test
    public void testSwappedWriteReadUnsignedIntRelative() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeUnsignedInt(10).flip().readUnsignedInt());
    }

    @Test
    public void testSwappedWriteReadIntAbsolute() {
        Assert.assertEquals(10, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeInt(4, 10).readInt(4));
    }

    @Test
    public void testSwappedWriteReadLongRelative() {
        Assert.assertEquals(12345, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeLong(12345).flip().readLong());
    }

    @Test
    public void testSwappedWriteReadLongAbsolute() {
        Assert.assertEquals(12345, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeLong(4, 12345).readLong(4));
    }

    @Test
    public void testSwappedWriteReadFloatRelative() {
        Assert.assertEquals(10.6F, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeFloat(10.6F).flip().readFloat(), 0.001);
    }

    @Test
    public void testSwappedWriteReadFloatAbsolute() {
        Assert.assertEquals(10.6F, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeFloat(4, 10.6F).readFloat(4), 0.001);
    }

    @Test
    public void testSwappedWriteReadDoubleRelative() {
        Assert.assertEquals(10.6, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeDouble(10.6).flip().readDouble(), 0.001);
    }

    @Test
    public void testSwappedWriteReadDoubleAbsolute() {
        Assert.assertEquals(10.6, createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeDouble(4, 10.6).readDouble(4), 0.001);
    }

    @Test
    public void testSwappedWriteReadBooleanRelative() {
        Assert.assertTrue(createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeBoolean(true).flip().readBoolean());
    }

    @Test
    public void testSwappedWriteReadBooleanAbsolute() {
        Assert.assertTrue(createBuffer(16).order(ByteOrder.LITTLE_ENDIAN).writeBoolean(4, true).readBoolean(4));
    }

    @Test
    public void testSwappedReadWriter() {
        Buffer writeBuffer = createBuffer(8).order(ByteOrder.LITTLE_ENDIAN).writeLong(10).flip();
        Buffer readBuffer = createBuffer(8).order(ByteOrder.LITTLE_ENDIAN);
        writeBuffer.read(readBuffer);
        Assert.assertEquals(10, readBuffer.flip().readLong());
    }

    @Test
    public void testSwappedAbsoluteSlice() {
        Buffer buffer = createBuffer(1024).order(ByteOrder.LITTLE_ENDIAN);
        buffer.writeLong(10).writeLong(11).rewind();
        Buffer slice = buffer.slice(8, 1016);
        Assert.assertEquals(0, slice.position());
        Assert.assertEquals(11, slice.readLong());
    }

    @Test
    public void testSwappedRelativeSliceWithoutLength() {
        Buffer buffer = createBuffer(1024, 1024).order(ByteOrder.LITTLE_ENDIAN);
        buffer.writeLong(10).writeLong(11).writeLong(12).rewind();
        Assert.assertEquals(10, buffer.readLong());
        Buffer slice = buffer.slice();
        Assert.assertEquals(0, slice.position());
        Assert.assertEquals((-1), slice.limit());
        Assert.assertEquals(1016, slice.capacity());
        Assert.assertEquals(1016, slice.maxCapacity());
        Assert.assertEquals(11, slice.readLong());
        Assert.assertEquals(11, slice.readLong(0));
        slice.close();
        Buffer slice2 = buffer.skip(8).slice();
        Assert.assertEquals(0, slice2.position());
        Assert.assertEquals((-1), slice2.limit());
        Assert.assertEquals(1008, slice2.capacity());
        Assert.assertEquals(1008, slice2.maxCapacity());
        Assert.assertEquals(12, slice2.readLong());
        Assert.assertEquals(12, slice2.readLong(0));
    }

    @Test
    public void testSwappedRelativeSliceWithLength() {
        Buffer buffer = createBuffer(1024).order(ByteOrder.LITTLE_ENDIAN);
        buffer.writeLong(10).writeLong(11).writeLong(12).rewind();
        Assert.assertEquals(10, buffer.readLong());
        Buffer slice = buffer.slice(8);
        Assert.assertEquals(0, slice.position());
        Assert.assertEquals(11, slice.readLong());
        Assert.assertEquals(11, slice.readLong(0));
        slice.close();
        Buffer slice2 = buffer.skip(8).slice(8);
        Assert.assertEquals(0, slice2.position());
        Assert.assertEquals(12, slice2.readLong());
        Assert.assertEquals(12, slice2.readLong(0));
        slice2.close();
    }

    @Test
    public void testSwappedSliceOfSlice() {
        Buffer buffer = createBuffer(1024).order(ByteOrder.LITTLE_ENDIAN);
        buffer.writeLong(10).writeLong(11).writeLong(12).rewind();
        Assert.assertEquals(10, buffer.readLong());
        Buffer slice = buffer.slice();
        Assert.assertEquals(11, slice.readLong());
        Buffer sliceOfSlice = slice.slice();
        Assert.assertEquals(12, sliceOfSlice.readLong());
        Assert.assertEquals(8, sliceOfSlice.position());
    }

    @Test
    public void testSwappedSliceWithLimit() {
        Buffer buffer = createBuffer(1024).order(ByteOrder.LITTLE_ENDIAN).limit(16);
        buffer.writeLong(10);
        Buffer slice = buffer.slice();
        Assert.assertEquals(0, slice.position());
        Assert.assertEquals(8, slice.capacity());
        Assert.assertEquals(8, slice.maxCapacity());
        Assert.assertEquals(8, slice.remaining());
    }

    @Test
    public void testSwappedSliceWithLittleRemaining() {
        Buffer buffer = createBuffer(1024, 2048).order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(1020);
        Buffer slice = buffer.slice(8);
        Assert.assertEquals(0, slice.position());
        Assert.assertEquals((-1), slice.limit());
    }

    @Test
    public void testSwappedCompact() {
        Buffer buffer = createBuffer(1024).order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(100).writeLong(1234).position(100).compact();
        Assert.assertEquals(0, buffer.position());
        Assert.assertEquals(1234, buffer.readLong());
    }

    @Test
    public void testCapacity0Read() {
        Buffer buffer = createBuffer(0, 1024);
        Assert.assertEquals(0, buffer.readLong());
    }

    @Test
    public void testCapacity0Write() {
        Buffer buffer = createBuffer(0, 1024);
        buffer.writeLong(10);
        Assert.assertEquals(10, buffer.readLong(0));
    }
}

