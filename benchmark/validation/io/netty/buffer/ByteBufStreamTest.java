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


import java.io.EOFException;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests channel buffer streams
 */
public class ByteBufStreamTest {
    @Test
    public void testAll() throws Exception {
        ByteBuf buf = Unpooled.buffer(0, 65536);
        try {
            new ByteBufOutputStream(null);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        ByteBufOutputStream out = new ByteBufOutputStream(buf);
        try {
            Assert.assertSame(buf, out.buffer());
            out.writeBoolean(true);
            out.writeBoolean(false);
            out.writeByte(42);
            out.writeByte(224);
            out.writeBytes("Hello, World!");
            out.writeChars("Hello, World");
            out.writeChar('!');
            out.writeDouble(42.0);
            out.writeFloat(42.0F);
            out.writeInt(42);
            out.writeLong(42);
            out.writeShort(42);
            out.writeShort(49152);
            out.writeUTF("Hello, World!");
            out.writeBytes("The first line\r\r\n");
            out.write(EMPTY_BYTES);
            out.write(new byte[]{ 1, 2, 3, 4 });
            out.write(new byte[]{ 1, 3, 3, 4 }, 0, 0);
        } finally {
            out.close();
        }
        try {
            new ByteBufInputStream(null, true);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            new ByteBufInputStream(null, 0, true);
            Assert.fail();
        } catch (NullPointerException e) {
            // Expected
        }
        try {
            new ByteBufInputStream(buf.retainedSlice(), (-1), true);
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            new ByteBufInputStream(buf.retainedSlice(), ((buf.capacity()) + 1), true);
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }
        ByteBufInputStream in = new ByteBufInputStream(buf, true);
        try {
            Assert.assertTrue(in.markSupported());
            in.mark(Integer.MAX_VALUE);
            Assert.assertEquals(buf.writerIndex(), in.skip(Long.MAX_VALUE));
            Assert.assertFalse(buf.isReadable());
            in.reset();
            Assert.assertEquals(0, buf.readerIndex());
            Assert.assertEquals(4, in.skip(4));
            Assert.assertEquals(4, buf.readerIndex());
            in.reset();
            Assert.assertTrue(in.readBoolean());
            Assert.assertFalse(in.readBoolean());
            Assert.assertEquals(42, in.readByte());
            Assert.assertEquals(224, in.readUnsignedByte());
            byte[] tmp = new byte[13];
            in.readFully(tmp);
            Assert.assertEquals("Hello, World!", new String(tmp, "ISO-8859-1"));
            Assert.assertEquals('H', in.readChar());
            Assert.assertEquals('e', in.readChar());
            Assert.assertEquals('l', in.readChar());
            Assert.assertEquals('l', in.readChar());
            Assert.assertEquals('o', in.readChar());
            Assert.assertEquals(',', in.readChar());
            Assert.assertEquals(' ', in.readChar());
            Assert.assertEquals('W', in.readChar());
            Assert.assertEquals('o', in.readChar());
            Assert.assertEquals('r', in.readChar());
            Assert.assertEquals('l', in.readChar());
            Assert.assertEquals('d', in.readChar());
            Assert.assertEquals('!', in.readChar());
            Assert.assertEquals(42.0, in.readDouble(), 0.0);
            Assert.assertEquals(42.0F, in.readFloat(), 0.0);
            Assert.assertEquals(42, in.readInt());
            Assert.assertEquals(42, in.readLong());
            Assert.assertEquals(42, in.readShort());
            Assert.assertEquals(49152, in.readUnsignedShort());
            Assert.assertEquals("Hello, World!", in.readUTF());
            Assert.assertEquals("The first line", in.readLine());
            Assert.assertEquals("", in.readLine());
            Assert.assertEquals(4, in.read(tmp));
            Assert.assertEquals(1, tmp[0]);
            Assert.assertEquals(2, tmp[1]);
            Assert.assertEquals(3, tmp[2]);
            Assert.assertEquals(4, tmp[3]);
            Assert.assertEquals((-1), in.read());
            Assert.assertEquals((-1), in.read(tmp));
            try {
                in.readByte();
                Assert.fail();
            } catch (EOFException e) {
                // Expected
            }
            try {
                in.readFully(tmp, 0, (-1));
                Assert.fail();
            } catch (IndexOutOfBoundsException e) {
                // Expected
            }
            try {
                in.readFully(tmp);
                Assert.fail();
            } catch (EOFException e) {
                // Expected
            }
        } finally {
            // Ownership was transferred to the ByteBufOutputStream, before we close we must retain the underlying
            // buffer.
            buf.retain();
            in.close();
        }
        Assert.assertEquals(buf.readerIndex(), in.readBytes());
        buf.release();
    }

    @Test
    public void testReadLine() throws Exception {
        Charset utf8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.buffer();
        ByteBufInputStream in = new ByteBufInputStream(buf, true);
        String s = in.readLine();
        Assert.assertNull(s);
        int charCount = 7;// total chars in the string below without new line characters

        byte[] abc = "\na\n\nb\r\nc\nd\ne".getBytes(utf8);
        buf.writeBytes(abc);
        in.mark(charCount);
        Assert.assertEquals("", in.readLine());
        Assert.assertEquals("a", in.readLine());
        Assert.assertEquals("", in.readLine());
        Assert.assertEquals("b", in.readLine());
        Assert.assertEquals("c", in.readLine());
        Assert.assertEquals("d", in.readLine());
        Assert.assertEquals("e", in.readLine());
        Assert.assertNull(in.readLine());
        in.reset();
        int count = 0;
        while ((in.readLine()) != null) {
            ++count;
            if (count > charCount) {
                Assert.fail("readLine() should have returned null");
            }
        } 
        Assert.assertEquals(charCount, count);
        in.close();
    }
}

