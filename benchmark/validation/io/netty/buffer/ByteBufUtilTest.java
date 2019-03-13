/**
 * Copyright 2014 The Netty Project
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


import CharsetUtil.ISO_8859_1;
import CharsetUtil.US_ASCII;
import CharsetUtil.UTF_16LE;
import CharsetUtil.UTF_8;
import io.netty.util.AsciiString;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class ByteBufUtilTest {
    @Test
    public void decodeRandomHexBytesWithEvenLength() {
        ByteBufUtilTest.decodeRandomHexBytes(256);
    }

    @Test
    public void decodeRandomHexBytesWithOddLength() {
        ByteBufUtilTest.decodeRandomHexBytes(257);
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeHexDumpWithOddLength() {
        ByteBufUtil.decodeHexDump("abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void decodeHexDumpWithInvalidChar() {
        ByteBufUtil.decodeHexDump("fg");
    }

    @Test
    public void equalsBufferSubsections() {
        byte[] b1 = new byte[128];
        byte[] b2 = new byte[256];
        Random rand = new Random();
        rand.nextBytes(b1);
        rand.nextBytes(b2);
        final int iB1 = (b1.length) / 2;
        final int iB2 = iB1 + (b1.length);
        final int length = (b1.length) - iB1;
        System.arraycopy(b1, iB1, b2, iB2, length);
        Assert.assertTrue(ByteBufUtil.equals(Unpooled.wrappedBuffer(b1), iB1, Unpooled.wrappedBuffer(b2), iB2, length));
    }

    @Test
    public void notEqualsBufferSubsections() {
        byte[] b1 = new byte[50];
        byte[] b2 = new byte[256];
        Random rand = new Random();
        rand.nextBytes(b1);
        rand.nextBytes(b2);
        final int iB1 = (b1.length) / 2;
        final int iB2 = iB1 + (b1.length);
        final int length = (b1.length) - iB1;
        System.arraycopy(b1, iB1, b2, iB2, length);
        // Randomly pick an index in the range that will be compared and make the value at that index differ between
        // the 2 arrays.
        int diffIndex = ByteBufUtilTest.random(rand, iB1, ((iB1 + length) - 1));
        ++(b1[diffIndex]);
        Assert.assertFalse(ByteBufUtil.equals(Unpooled.wrappedBuffer(b1), iB1, Unpooled.wrappedBuffer(b2), iB2, length));
    }

    @Test
    public void notEqualsBufferOverflow() {
        byte[] b1 = new byte[8];
        byte[] b2 = new byte[16];
        Random rand = new Random();
        rand.nextBytes(b1);
        rand.nextBytes(b2);
        final int iB1 = (b1.length) / 2;
        final int iB2 = iB1 + (b1.length);
        final int length = (b1.length) - iB1;
        System.arraycopy(b1, iB1, b2, iB2, (length - 1));
        Assert.assertFalse(ByteBufUtil.equals(Unpooled.wrappedBuffer(b1), iB1, Unpooled.wrappedBuffer(b2), iB2, ((Math.max(b1.length, b2.length)) * 2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void notEqualsBufferUnderflow() {
        byte[] b1 = new byte[8];
        byte[] b2 = new byte[16];
        Random rand = new Random();
        rand.nextBytes(b1);
        rand.nextBytes(b2);
        final int iB1 = (b1.length) / 2;
        final int iB2 = iB1 + (b1.length);
        final int length = (b1.length) - iB1;
        System.arraycopy(b1, iB1, b2, iB2, (length - 1));
        Assert.assertFalse(ByteBufUtil.equals(Unpooled.wrappedBuffer(b1), iB1, Unpooled.wrappedBuffer(b2), iB2, (-1)));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void writeShortBE() {
        int expected = 4660;
        ByteBuf buf = Unpooled.buffer(2).order(ByteOrder.BIG_ENDIAN);
        ByteBufUtil.writeShortBE(buf, expected);
        Assert.assertEquals(expected, buf.readShort());
        buf.resetReaderIndex();
        Assert.assertEquals(ByteBufUtil.swapShort(((short) (expected))), buf.readShortLE());
        buf.release();
        buf = Unpooled.buffer(2).order(ByteOrder.LITTLE_ENDIAN);
        ByteBufUtil.writeShortBE(buf, expected);
        Assert.assertEquals(((short) (expected)), buf.readShortLE());
        buf.resetReaderIndex();
        Assert.assertEquals(ByteBufUtil.swapShort(((short) (expected))), buf.readShort());
        buf.release();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void setShortBE() {
        int shortValue = 4660;
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[2]).order(ByteOrder.BIG_ENDIAN);
        ByteBufUtil.setShortBE(buf, 0, shortValue);
        Assert.assertEquals(shortValue, buf.readShort());
        buf.resetReaderIndex();
        Assert.assertEquals(ByteBufUtil.swapShort(((short) (shortValue))), buf.readShortLE());
        buf.release();
        buf = Unpooled.wrappedBuffer(new byte[2]).order(ByteOrder.LITTLE_ENDIAN);
        ByteBufUtil.setShortBE(buf, 0, shortValue);
        Assert.assertEquals(((short) (shortValue)), buf.readShortLE());
        buf.resetReaderIndex();
        Assert.assertEquals(ByteBufUtil.swapShort(((short) (shortValue))), buf.readShort());
        buf.release();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void writeMediumBE() {
        int mediumValue = 1193046;
        ByteBuf buf = Unpooled.buffer(4).order(ByteOrder.BIG_ENDIAN);
        ByteBufUtil.writeMediumBE(buf, mediumValue);
        Assert.assertEquals(mediumValue, buf.readMedium());
        buf.resetReaderIndex();
        Assert.assertEquals(ByteBufUtil.swapMedium(mediumValue), buf.readMediumLE());
        buf.release();
        buf = Unpooled.buffer(4).order(ByteOrder.LITTLE_ENDIAN);
        ByteBufUtil.writeMediumBE(buf, mediumValue);
        Assert.assertEquals(mediumValue, buf.readMediumLE());
        buf.resetReaderIndex();
        Assert.assertEquals(ByteBufUtil.swapMedium(mediumValue), buf.readMedium());
        buf.release();
    }

    @Test
    public void testWriteUsAscii() {
        String usAscii = "NettyRocks";
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(usAscii.getBytes(US_ASCII));
        ByteBuf buf2 = Unpooled.buffer(16);
        ByteBufUtil.writeAscii(buf2, usAscii);
        Assert.assertEquals(buf, buf2);
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUsAsciiSwapped() {
        String usAscii = "NettyRocks";
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(usAscii.getBytes(US_ASCII));
        SwappedByteBuf buf2 = new SwappedByteBuf(Unpooled.buffer(16));
        ByteBufUtil.writeAscii(buf2, usAscii);
        Assert.assertEquals(buf, buf2);
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUsAsciiWrapped() {
        String usAscii = "NettyRocks";
        ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.buffer(16));
        ByteBufUtilTest.assertWrapped(buf);
        buf.writeBytes(usAscii.getBytes(US_ASCII));
        ByteBuf buf2 = Unpooled.unreleasableBuffer(Unpooled.buffer(16));
        ByteBufUtilTest.assertWrapped(buf2);
        ByteBufUtil.writeAscii(buf2, usAscii);
        Assert.assertEquals(buf, buf2);
        buf.unwrap().release();
        buf2.unwrap().release();
    }

    @Test
    public void testWriteUsAsciiComposite() {
        String usAscii = "NettyRocks";
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(usAscii.getBytes(US_ASCII));
        ByteBuf buf2 = Unpooled.compositeBuffer().addComponent(Unpooled.buffer(8)).addComponent(Unpooled.buffer(24));
        // write some byte so we start writing with an offset.
        buf2.writeByte(1);
        ByteBufUtil.writeAscii(buf2, usAscii);
        // Skip the previously written byte.
        Assert.assertEquals(buf, buf2.skipBytes(1));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUsAsciiCompositeWrapped() {
        String usAscii = "NettyRocks";
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(usAscii.getBytes(US_ASCII));
        ByteBuf buf2 = new WrappedCompositeByteBuf(Unpooled.compositeBuffer().addComponent(Unpooled.buffer(8)).addComponent(Unpooled.buffer(24)));
        // write some byte so we start writing with an offset.
        buf2.writeByte(1);
        ByteBufUtil.writeAscii(buf2, usAscii);
        // Skip the previously written byte.
        Assert.assertEquals(buf, buf2.skipBytes(1));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8() {
        String usAscii = "Some UTF-8 like ?????";
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(usAscii.getBytes(UTF_8));
        ByteBuf buf2 = Unpooled.buffer(16);
        ByteBufUtil.writeUtf8(buf2, usAscii);
        Assert.assertEquals(buf, buf2);
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8Composite() {
        String utf8 = "Some UTF-8 like ?????";
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(utf8.getBytes(UTF_8));
        ByteBuf buf2 = Unpooled.compositeBuffer().addComponent(Unpooled.buffer(8)).addComponent(Unpooled.buffer(24));
        // write some byte so we start writing with an offset.
        buf2.writeByte(1);
        ByteBufUtil.writeUtf8(buf2, utf8);
        // Skip the previously written byte.
        Assert.assertEquals(buf, buf2.skipBytes(1));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8CompositeWrapped() {
        String utf8 = "Some UTF-8 like ?????";
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(utf8.getBytes(UTF_8));
        ByteBuf buf2 = new WrappedCompositeByteBuf(Unpooled.compositeBuffer().addComponent(Unpooled.buffer(8)).addComponent(Unpooled.buffer(24)));
        // write some byte so we start writing with an offset.
        buf2.writeByte(1);
        ByteBufUtil.writeUtf8(buf2, utf8);
        // Skip the previously written byte.
        Assert.assertEquals(buf, buf2.skipBytes(1));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8Surrogates() {
        // leading surrogate + trailing surrogate
        String surrogateString = new StringBuilder(2).append('a').append('\ud800').append('\udc00').append('b').toString();
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(surrogateString.getBytes(UTF_8));
        ByteBuf buf2 = Unpooled.buffer(16);
        ByteBufUtil.writeUtf8(buf2, surrogateString);
        Assert.assertEquals(buf, buf2);
        Assert.assertEquals(buf.readableBytes(), ByteBufUtil.utf8Bytes(surrogateString));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8InvalidOnlyTrailingSurrogate() {
        String surrogateString = new StringBuilder(2).append('a').append('\udc00').append('b').toString();
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(surrogateString.getBytes(UTF_8));
        ByteBuf buf2 = Unpooled.buffer(16);
        ByteBufUtil.writeUtf8(buf2, surrogateString);
        Assert.assertEquals(buf, buf2);
        Assert.assertEquals(buf.readableBytes(), ByteBufUtil.utf8Bytes(surrogateString));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8InvalidOnlyLeadingSurrogate() {
        String surrogateString = new StringBuilder(2).append('a').append('\ud800').append('b').toString();
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(surrogateString.getBytes(UTF_8));
        ByteBuf buf2 = Unpooled.buffer(16);
        ByteBufUtil.writeUtf8(buf2, surrogateString);
        Assert.assertEquals(buf, buf2);
        Assert.assertEquals(buf.readableBytes(), ByteBufUtil.utf8Bytes(surrogateString));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8InvalidSurrogatesSwitched() {
        String surrogateString = new StringBuilder(2).append('a').append('\udc00').append('\ud800').append('b').toString();
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(surrogateString.getBytes(UTF_8));
        ByteBuf buf2 = Unpooled.buffer(16);
        ByteBufUtil.writeUtf8(buf2, surrogateString);
        Assert.assertEquals(buf, buf2);
        Assert.assertEquals(buf.readableBytes(), ByteBufUtil.utf8Bytes(surrogateString));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8InvalidTwoLeadingSurrogates() {
        String surrogateString = new StringBuilder(2).append('a').append('\ud800').append('\ud800').append('b').toString();
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(surrogateString.getBytes(UTF_8));
        ByteBuf buf2 = Unpooled.buffer(16);
        ByteBufUtil.writeUtf8(buf2, surrogateString);
        Assert.assertEquals(buf, buf2);
        Assert.assertEquals(buf.readableBytes(), ByteBufUtil.utf8Bytes(surrogateString));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8InvalidTwoTrailingSurrogates() {
        String surrogateString = new StringBuilder(2).append('a').append('\udc00').append('\udc00').append('b').toString();
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(surrogateString.getBytes(UTF_8));
        ByteBuf buf2 = Unpooled.buffer(16);
        ByteBufUtil.writeUtf8(buf2, surrogateString);
        Assert.assertEquals(buf, buf2);
        Assert.assertEquals(buf.readableBytes(), ByteBufUtil.utf8Bytes(surrogateString));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8InvalidEndOnLeadingSurrogate() {
        String surrogateString = new StringBuilder(2).append('\ud800').toString();
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(surrogateString.getBytes(UTF_8));
        ByteBuf buf2 = Unpooled.buffer(16);
        ByteBufUtil.writeUtf8(buf2, surrogateString);
        Assert.assertEquals(buf, buf2);
        Assert.assertEquals(buf.readableBytes(), ByteBufUtil.utf8Bytes(surrogateString));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8InvalidEndOnTrailingSurrogate() {
        String surrogateString = new StringBuilder(2).append('\udc00').toString();
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(surrogateString.getBytes(UTF_8));
        ByteBuf buf2 = Unpooled.buffer(16);
        ByteBufUtil.writeUtf8(buf2, surrogateString);
        Assert.assertEquals(buf, buf2);
        Assert.assertEquals(buf.readableBytes(), ByteBufUtil.utf8Bytes(surrogateString));
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUsAsciiString() {
        AsciiString usAscii = new AsciiString("NettyRocks");
        ByteBuf buf = Unpooled.buffer(16);
        buf.writeBytes(usAscii.toString().getBytes(US_ASCII));
        ByteBuf buf2 = Unpooled.buffer(16);
        ByteBufUtil.writeAscii(buf2, usAscii);
        Assert.assertEquals(buf, buf2);
        buf.release();
        buf2.release();
    }

    @Test
    public void testWriteUtf8Wrapped() {
        String usAscii = "Some UTF-8 like ?????";
        ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.buffer(16));
        ByteBufUtilTest.assertWrapped(buf);
        buf.writeBytes(usAscii.getBytes(UTF_8));
        ByteBuf buf2 = Unpooled.unreleasableBuffer(Unpooled.buffer(16));
        ByteBufUtilTest.assertWrapped(buf2);
        ByteBufUtil.writeUtf8(buf2, usAscii);
        Assert.assertEquals(buf, buf2);
        buf.release();
        buf2.release();
    }

    @Test
    public void testDecodeUsAscii() {
        ByteBufUtilTest.testDecodeString("This is a test", US_ASCII);
    }

    @Test
    public void testDecodeUtf8() {
        ByteBufUtilTest.testDecodeString("Some UTF-8 like ?????", UTF_8);
    }

    @Test
    public void testToStringDoesNotThrowIndexOutOfBounds() {
        CompositeByteBuf buffer = Unpooled.compositeBuffer();
        try {
            byte[] bytes = "1234".getBytes(UTF_8);
            buffer.addComponent(Unpooled.buffer(bytes.length).writeBytes(bytes));
            buffer.addComponent(Unpooled.buffer(bytes.length).writeBytes(bytes));
            Assert.assertEquals("1234", buffer.toString(bytes.length, bytes.length, UTF_8));
        } finally {
            buffer.release();
        }
    }

    @Test
    public void testIsTextWithUtf8() {
        byte[][] validUtf8Bytes = new byte[][]{ "netty".getBytes(UTF_8), new byte[]{ ((byte) (36)) }, new byte[]{ ((byte) (194)), ((byte) (162)) }, new byte[]{ ((byte) (226)), ((byte) (130)), ((byte) (172)) }, new byte[]{ ((byte) (240)), ((byte) (144)), ((byte) (141)), ((byte) (136)) }, new byte[]{ ((byte) (36)), ((byte) (194)), ((byte) (162)), ((byte) (226)), ((byte) (130)), ((byte) (172)), ((byte) (240)), ((byte) (144)), ((byte) (141)), ((byte) (136)) }// multiple characters
        // multiple characters
        // multiple characters
         };
        for (byte[] bytes : validUtf8Bytes) {
            ByteBufUtilTest.assertIsText(bytes, true, UTF_8);
        }
        byte[][] invalidUtf8Bytes = new byte[][]{ new byte[]{ ((byte) (128)) }, new byte[]{ ((byte) (240)), ((byte) (130)), ((byte) (130)), ((byte) (172)) }// Overlong encodings
        // Overlong encodings
        // Overlong encodings
        , new byte[]{ ((byte) (194)) }, // not enough bytes
        new byte[]{ ((byte) (226)), ((byte) (130)) }, // not enough bytes
        new byte[]{ ((byte) (240)), ((byte) (144)), ((byte) (141)) }// not enough bytes
        // not enough bytes
        // not enough bytes
        , new byte[]{ ((byte) (194)), ((byte) (192)) }, // not correct bytes
        new byte[]{ ((byte) (226)), ((byte) (130)), ((byte) (192)) }// not correct bytes
        // not correct bytes
        // not correct bytes
        , new byte[]{ ((byte) (240)), ((byte) (144)), ((byte) (141)), ((byte) (192)) }// not correct bytes
        // not correct bytes
        // not correct bytes
        , new byte[]{ ((byte) (193)), ((byte) (128)) }, // out of lower bound
        new byte[]{ ((byte) (224)), ((byte) (128)), ((byte) (128)) }// out of lower bound
        // out of lower bound
        // out of lower bound
        , new byte[]{ ((byte) (237)), ((byte) (175)), ((byte) (128)) }// out of upper bound
        // out of upper bound
        // out of upper bound
         };
        for (byte[] bytes : invalidUtf8Bytes) {
            ByteBufUtilTest.assertIsText(bytes, false, UTF_8);
        }
    }

    @Test
    public void testIsTextWithoutOptimization() {
        byte[] validBytes = new byte[]{ ((byte) (1)), ((byte) (216)), ((byte) (55)), ((byte) (220)) };
        byte[] invalidBytes = new byte[]{ ((byte) (1)), ((byte) (216)) };
        ByteBufUtilTest.assertIsText(validBytes, true, UTF_16LE);
        ByteBufUtilTest.assertIsText(invalidBytes, false, UTF_16LE);
    }

    @Test
    public void testIsTextWithAscii() {
        byte[] validBytes = new byte[]{ ((byte) (0)), ((byte) (1)), ((byte) (55)), ((byte) (127)) };
        byte[] invalidBytes = new byte[]{ ((byte) (128)), ((byte) (255)) };
        ByteBufUtilTest.assertIsText(validBytes, true, US_ASCII);
        ByteBufUtilTest.assertIsText(invalidBytes, false, US_ASCII);
    }

    @Test
    public void testIsTextWithInvalidIndexAndLength() {
        ByteBuf buffer = Unpooled.buffer();
        try {
            buffer.writeBytes(new byte[4]);
            int[][] validIndexLengthPairs = new int[][]{ new int[]{ 4, 0 }, new int[]{ 0, 4 }, new int[]{ 1, 3 } };
            for (int[] pair : validIndexLengthPairs) {
                Assert.assertTrue(ByteBufUtil.isText(buffer, pair[0], pair[1], US_ASCII));
            }
            int[][] invalidIndexLengthPairs = new int[][]{ new int[]{ 4, 1 }, new int[]{ -1, 2 }, new int[]{ 3, -1 }, new int[]{ 3, -2 }, new int[]{ 5, 0 }, new int[]{ 1, 5 } };
            for (int[] pair : invalidIndexLengthPairs) {
                try {
                    ByteBufUtil.isText(buffer, pair[0], pair[1], US_ASCII);
                    Assert.fail("Expected IndexOutOfBoundsException");
                } catch (IndexOutOfBoundsException e) {
                    // expected
                }
            }
        } finally {
            buffer.release();
        }
    }

    @Test
    public void testUtf8Bytes() {
        final String s = "Some UTF-8 like ?????";
        ByteBufUtilTest.checkUtf8Bytes(s);
    }

    @Test
    public void testUtf8BytesWithSurrogates() {
        final String s = "a\ud800\udc00b";
        ByteBufUtilTest.checkUtf8Bytes(s);
    }

    @Test
    public void testUtf8BytesWithNonSurrogates3Bytes() {
        final String s = "a\ue000b";
        ByteBufUtilTest.checkUtf8Bytes(s);
    }

    @Test
    public void testUtf8BytesWithNonSurrogatesNonAscii() {
        final char nonAscii = ((char) (129));
        final String s = ("a" + nonAscii) + "b";
        ByteBufUtilTest.checkUtf8Bytes(s);
    }

    @Test
    public void testIsTextMultiThreaded() throws Throwable {
        final ByteBuf buffer = Unpooled.copiedBuffer("Hello, World!", ISO_8859_1);
        try {
            final AtomicInteger counter = new AtomicInteger(60000);
            final AtomicReference<Throwable> errorRef = new AtomicReference<Throwable>();
            List<Thread> threads = new ArrayList<Thread>();
            for (int i = 0; i < 10; i++) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (((errorRef.get()) == null) && ((counter.decrementAndGet()) > 0)) {
                                Assert.assertTrue(ByteBufUtil.isText(buffer, ISO_8859_1));
                            } 
                        } catch (Throwable cause) {
                            errorRef.compareAndSet(null, cause);
                        }
                    }
                });
                threads.add(thread);
            }
            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
            Throwable error = errorRef.get();
            if (error != null) {
                throw error;
            }
        } finally {
            buffer.release();
        }
    }
}

