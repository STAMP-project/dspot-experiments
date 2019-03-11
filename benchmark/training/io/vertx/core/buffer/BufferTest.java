/**
 * Copyright (c) 2014 Red Hat, Inc. and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.buffer;


import Buffer.factory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.IllegalReferenceCountException;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.test.core.TestUtils;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class BufferTest {
    private static final int MEDIUM_MAX_VALUE = 2 << 23;

    private static final Function<byte[], Buffer> PADDED_BUFFER_FACTORY = ( arr) -> Buffer.buffer(BufferTest.paddedByteBuf(5, arr));

    @Test
    public void testConstructorArguments() throws Exception {
        TestUtils.assertIllegalArgumentException(() -> Buffer.buffer((-1)));
        TestUtils.assertNullPointerException(() -> Buffer.buffer(((byte[]) (null))));
        TestUtils.assertNullPointerException(() -> Buffer.buffer(((String) (null))));
        TestUtils.assertNullPointerException(() -> Buffer.buffer(((ByteBuf) (null))));
        TestUtils.assertNullPointerException(() -> Buffer.buffer(null, "UTF-8"));
        TestUtils.assertNullPointerException(() -> Buffer.buffer("", null));
    }

    // https://github.com/vert-x/vert.x/issues/561
    @Test
    public void testSetGetInt() throws Exception {
        final int size = 10;
        Buffer buffer = Buffer.buffer(size);
        for (int i = 0; i < size; i++) {
            buffer.setInt((i * 4), ((i + 1) * 10));
        }
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(((i + 1) * 10), buffer.getInt((i * 4)));
        }
    }

    @Test
    public void testAppendBuff() throws Exception {
        testAppendBuff(Buffer::buffer);
    }

    @Test
    public void testAppendBuff2() throws Exception {
        testAppendBuff(BufferTest.PADDED_BUFFER_FACTORY);
    }

    @Test
    public void testAppendBytes() throws Exception {
        int bytesLen = 100;
        byte[] bytes = TestUtils.randomByteArray(bytesLen);
        Buffer b = Buffer.buffer();
        b.appendBytes(bytes);
        Assert.assertEquals(b.length(), bytes.length);
        Assert.assertTrue(TestUtils.byteArraysEqual(bytes, b.getBytes()));
        b.appendBytes(bytes);
        Assert.assertEquals(b.length(), (2 * (bytes.length)));
        TestUtils.assertNullPointerException(() -> b.appendBytes(null));
    }

    @Test
    public void testAppendBytesWithOffsetAndLen() throws Exception {
        int bytesLen = 100;
        byte[] bytes = TestUtils.randomByteArray(bytesLen);
        int len = bytesLen - 2;
        Buffer b = Buffer.buffer();
        b.appendBytes(bytes, 1, len);
        Assert.assertEquals(b.length(), len);
        byte[] copy = new byte[len];
        System.arraycopy(bytes, 1, copy, 0, len);
        Assert.assertTrue(TestUtils.byteArraysEqual(copy, b.getBytes()));
        b.appendBytes(bytes, 1, len);
        Assert.assertEquals(b.length(), (2 * len));
        TestUtils.assertNullPointerException(() -> b.appendBytes(null, 1, len));
    }

    @Test
    public void testAppendBufferWithOffsetAndLen() throws Exception {
        testAppendBufferWithOffsetAndLen(Buffer::buffer);
    }

    @Test
    public void testAppendBufferWithOffsetAndLen2() throws Exception {
        testAppendBufferWithOffsetAndLen(BufferTest.PADDED_BUFFER_FACTORY);
    }

    @Test
    public void testAppendByte() throws Exception {
        int bytesLen = 100;
        byte[] bytes = TestUtils.randomByteArray(bytesLen);
        Buffer b = Buffer.buffer();
        for (int i = 0; i < bytesLen; i++) {
            b.appendByte(bytes[i]);
        }
        Assert.assertEquals(b.length(), bytes.length);
        Assert.assertTrue(TestUtils.byteArraysEqual(bytes, b.getBytes()));
        for (int i = 0; i < bytesLen; i++) {
            b.appendByte(bytes[i]);
        }
        Assert.assertEquals(b.length(), (2 * (bytes.length)));
    }

    @Test
    public void testAppendByte2() throws Exception {
        int bytesLen = 100;
        Buffer b = Buffer.buffer(TestUtils.randomByteArray(bytesLen));
        b.setByte(b.length(), ((byte) (9)));
    }

    @Test
    public void testAppendUnsignedByte() {
        Buffer b = Buffer.buffer(TestUtils.randomByteArray(100));
        b.appendUnsignedByte(((short) ((Byte.MAX_VALUE) + ((Byte.MAX_VALUE) / 2))));
        Assert.assertEquals(101, b.length());
    }

    @Test
    public void testAppendShort() {
        Buffer b = Buffer.buffer(TestUtils.randomByteArray(100));
        b.appendShort(Short.MAX_VALUE);
        Assert.assertEquals(102, b.length());
        b.appendShortLE(Short.MAX_VALUE);
        Assert.assertEquals(104, b.length());
    }

    @Test
    public void testAppendUnsignedShort() {
        Buffer b = Buffer.buffer(TestUtils.randomByteArray(100));
        b.appendUnsignedShort(((Short.MAX_VALUE) + ((Short.MAX_VALUE) / 2)));
        Assert.assertEquals(102, b.length());
        b.appendUnsignedShortLE(((Short.MAX_VALUE) + ((Short.MAX_VALUE) / 2)));
        Assert.assertEquals(104, b.length());
    }

    @Test
    public void testAppendInt() {
        Buffer b = Buffer.buffer(TestUtils.randomByteArray(100));
        b.appendInt(Integer.MAX_VALUE);
        Assert.assertEquals(104, b.length());
        b.appendIntLE(Integer.MAX_VALUE);
        Assert.assertEquals(108, b.length());
    }

    @Test
    public void testAppendUnsignedInt() {
        Buffer b = Buffer.buffer(TestUtils.randomByteArray(100));
        b.appendUnsignedInt(((Integer.MAX_VALUE) + (((long) (Integer.MAX_VALUE)) / 2)));
        Assert.assertEquals(104, b.length());
        b.appendUnsignedIntLE(((Integer.MAX_VALUE) + (((long) (Integer.MAX_VALUE)) / 2)));
        Assert.assertEquals(108, b.length());
    }

    @Test
    public void testAppendMedium() {
        Buffer b = Buffer.buffer(TestUtils.randomByteArray(100));
        b.appendMedium(BufferTest.MEDIUM_MAX_VALUE);
        Assert.assertEquals(103, b.length());
        b.appendMediumLE(BufferTest.MEDIUM_MAX_VALUE);
        Assert.assertEquals(106, b.length());
    }

    @Test
    public void testAppendLong() {
        Buffer b = Buffer.buffer(TestUtils.randomByteArray(100));
        b.appendLong(Long.MAX_VALUE);
        Assert.assertEquals(108, b.length());
        b.appendLongLE(Long.MAX_VALUE);
        Assert.assertEquals(116, b.length());
    }

    @Test
    public void testAppendString1() throws Exception {
        String str = TestUtils.randomUnicodeString(100);
        byte[] sb = str.getBytes("UTF-8");
        Buffer b = Buffer.buffer();
        b.appendString(str);
        Assert.assertEquals(b.length(), sb.length);
        Assert.assertTrue(str.equals(b.toString("UTF-8")));
        Assert.assertTrue(str.equals(b.toString(StandardCharsets.UTF_8)));
        TestUtils.assertNullPointerException(() -> b.appendString(null));
        TestUtils.assertNullPointerException(() -> b.appendString(null, "UTF-8"));
        TestUtils.assertNullPointerException(() -> b.appendString("", null));
    }

    @Test
    public void testAppendString2() throws Exception {
        // TODO
    }

    @Test
    public void testLE() {
        checkBEAndLE(2, Buffer.buffer().appendShort(Short.MAX_VALUE), Buffer.buffer().appendShortLE(Short.MAX_VALUE));
        checkBEAndLE(2, Buffer.buffer().appendUnsignedShort(Short.MAX_VALUE), Buffer.buffer().appendUnsignedShortLE(Short.MAX_VALUE));
        checkBEAndLE(3, Buffer.buffer().appendMedium(((Integer.MAX_VALUE) / 2)), Buffer.buffer().appendMediumLE(((Integer.MAX_VALUE) / 2)));
        checkBEAndLE(4, Buffer.buffer().appendInt(Integer.MAX_VALUE), Buffer.buffer().appendIntLE(Integer.MAX_VALUE));
        checkBEAndLE(4, Buffer.buffer().appendUnsignedInt(Integer.MAX_VALUE), Buffer.buffer().appendUnsignedIntLE(Integer.MAX_VALUE));
        checkBEAndLE(8, Buffer.buffer().appendLong(Long.MAX_VALUE), Buffer.buffer().appendLongLE(Long.MAX_VALUE));
    }

    @Test
    public void testGetOutOfBounds() throws Exception {
        int bytesLen = 100;
        byte[] bytes = TestUtils.randomByteArray(bytesLen);
        Buffer b = Buffer.buffer(bytes);
        TestUtils.assertIndexOutOfBoundsException(() -> b.getByte(bytesLen));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getByte((bytesLen + 1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getByte((bytesLen + 100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getByte((-1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getByte((-100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getInt(bytesLen));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getInt((bytesLen + 1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getInt((bytesLen + 100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getInt((-1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getInt((-100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getLong(bytesLen));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getLong((bytesLen + 1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getLong((bytesLen + 100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getLong((-1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getLong((-100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getFloat(bytesLen));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getFloat((bytesLen + 1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getFloat((bytesLen + 100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getFloat((-1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getFloat((-100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getDouble(bytesLen));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getDouble((bytesLen + 1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getDouble((bytesLen + 100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getDouble((-1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getDouble((-100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getShort(bytesLen));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getShort((bytesLen + 1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getShort((bytesLen + 100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getShort((-1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getShort((-100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getBytes((bytesLen + 1), (bytesLen + 1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getBytes((bytesLen + 100), (bytesLen + 100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getBytes((-1), (-1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getBytes((-100), (-100)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getString((-1), bytesLen));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getString(0, (bytesLen + 1)));
        TestUtils.assertIllegalArgumentException(() -> b.getString(2, 1));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getString((-1), bytesLen, "UTF-8"));
        TestUtils.assertIndexOutOfBoundsException(() -> b.getString(0, (bytesLen + 1), "UTF-8"));
        TestUtils.assertIllegalArgumentException(() -> b.getString(2, 1, "UTF-8"));
    }

    @Test
    public void testSetOutOfBounds() throws Exception {
        Buffer b = Buffer.buffer(numSets);
        TestUtils.assertIndexOutOfBoundsException(() -> b.setByte((-1), ((byte) (0))));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setInt((-1), 0));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setLong((-1), 0));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setDouble((-1), 0));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setFloat((-1), 0));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setShort((-1), ((short) (0))));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setBuffer((-1), b));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setBuffer(0, b, (-1), 0));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setBuffer(0, b, 0, (-1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setBytes((-1), TestUtils.randomByteArray(1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setBytes((-1), TestUtils.randomByteArray(1), (-1), 0));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setBytes((-1), TestUtils.randomByteArray(1), 0, (-1)));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setString((-1), ""));
        TestUtils.assertIndexOutOfBoundsException(() -> b.setString((-1), "", "UTF-8"));
    }

    @Test
    public void testGetByte() throws Exception {
        int bytesLen = 100;
        byte[] bytes = TestUtils.randomByteArray(bytesLen);
        Buffer b = Buffer.buffer(bytes);
        for (int i = 0; i < bytesLen; i++) {
            Assert.assertEquals(bytes[i], b.getByte(i));
        }
    }

    @Test
    public void testGetUnsignedByte() throws Exception {
        int bytesLen = 100;
        byte[] bytes = TestUtils.randomByteArray(bytesLen);
        Buffer b = Buffer.buffer(bytes);
        for (int i = 0; i < bytesLen; i++) {
            Assert.assertEquals(Byte.toUnsignedLong(bytes[i]), b.getUnsignedByte(i));
        }
    }

    @Test
    public void testGetInt() throws Exception {
        testGetSetInt(false);
    }

    @Test
    public void testGetIntLE() throws Exception {
        testGetSetInt(true);
    }

    @Test
    public void testGetUnsignedInt() throws Exception {
        testGetSetUnsignedInt(false);
    }

    @Test
    public void testGetUnsignedIntLE() throws Exception {
        testGetSetUnsignedInt(true);
    }

    @Test
    public void testGetMedium() throws Exception {
        testGetSetMedium(false);
    }

    @Test
    public void testGetMediumLE() throws Exception {
        testGetSetMedium(true);
    }

    @Test
    public void testGetUnsignedMedium() throws Exception {
        testGetSetUnsignedMedium(false);
    }

    @Test
    public void testGetUnsignedMediumLE() throws Exception {
        testGetSetUnsignedMedium(true);
    }

    @Test
    public void testGetLong() throws Exception {
        testGetSetLong(false);
    }

    @Test
    public void testGetLongLE() throws Exception {
        testGetSetLong(true);
    }

    @Test
    public void testGetFloat() throws Exception {
        int numFloats = 100;
        Buffer b = Buffer.buffer((numFloats * 4));
        for (int i = 0; i < numFloats; i++) {
            b.setFloat((i * 4), i);
        }
        for (int i = 0; i < numFloats; i++) {
            Assert.assertEquals(((float) (i)), b.getFloat((i * 4)), 0);
        }
    }

    @Test
    public void testGetDouble() throws Exception {
        int numDoubles = 100;
        Buffer b = Buffer.buffer((numDoubles * 8));
        for (int i = 0; i < numDoubles; i++) {
            b.setDouble((i * 8), i);
        }
        for (int i = 0; i < numDoubles; i++) {
            Assert.assertEquals(((double) (i)), b.getDouble((i * 8)), 0);
        }
    }

    @Test
    public void testGetShort() throws Exception {
        testGetSetShort(false);
    }

    @Test
    public void testGetShortLE() throws Exception {
        testGetSetShort(true);
    }

    @Test
    public void testGetUnsignedShort() throws Exception {
        testGetSetUnsignedShort(false);
    }

    @Test
    public void testGetUnsignedShortLE() throws Exception {
        testGetSetUnsignedShort(true);
    }

    @Test
    public void testGetString() throws Exception {
        String str = TestUtils.randomAlphaString(100);
        Buffer b = Buffer.buffer(str, "UTF-8");// encode ascii as UTF-8 so one byte per char

        Assert.assertEquals(100, b.length());
        String substr = b.getString(10, 20);
        Assert.assertEquals(str.substring(10, 20), substr);
        substr = b.getString(10, 20, "UTF-8");
        Assert.assertEquals(str.substring(10, 20), substr);
    }

    @Test
    public void testGetBytes() throws Exception {
        byte[] bytes = TestUtils.randomByteArray(100);
        Buffer b = Buffer.buffer(bytes);
        Assert.assertTrue(TestUtils.byteArraysEqual(bytes, b.getBytes()));
    }

    @Test
    public void testGetBytes2() throws Exception {
        byte[] bytes = TestUtils.randomByteArray(100);
        Buffer b = Buffer.buffer(bytes);
        byte[] sub = new byte[(bytes.length) / 2];
        System.arraycopy(bytes, ((bytes.length) / 4), sub, 0, ((bytes.length) / 2));
        Assert.assertTrue(TestUtils.byteArraysEqual(sub, b.getBytes(((bytes.length) / 4), (((bytes.length) / 4) + ((bytes.length) / 2)))));
    }

    @Test
    public void testGetBytesWithByteArray() throws Exception {
        byte[] bytes = TestUtils.randomByteArray(100);
        Buffer b = Buffer.buffer(bytes);
        byte[] sub = new byte[(bytes.length) / 2];
        System.arraycopy(bytes, ((bytes.length) / 4), sub, 0, ((bytes.length) / 2));
        byte[] result = new byte[(bytes.length) / 2];
        b.getBytes(((bytes.length) / 4), (((bytes.length) / 4) + ((bytes.length) / 2)), result);
        Assert.assertTrue(TestUtils.byteArraysEqual(sub, result));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBytesWithTooSmallByteArray() throws Exception {
        byte[] bytes = TestUtils.randomByteArray(100);
        Buffer b = Buffer.buffer(bytes);
        byte[] result = new byte[(bytes.length) / 4];
        b.getBytes(((bytes.length) / 4), (((bytes.length) / 4) + ((bytes.length) / 2)), result);
    }

    @Test
    public void testGetBytesWithByteArrayFull() throws Exception {
        byte[] bytes = TestUtils.randomByteArray(100);
        Buffer b = Buffer.buffer(bytes);
        byte[] sub = new byte[bytes.length];
        System.arraycopy(bytes, ((bytes.length) / 4), sub, 12, ((bytes.length) / 2));
        byte[] result = new byte[bytes.length];
        b.getBytes(((bytes.length) / 4), (((bytes.length) / 4) + ((bytes.length) / 2)), result, 12);
        Assert.assertTrue(TestUtils.byteArraysEqual(sub, result));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBytesWithBadOffset() throws Exception {
        byte[] bytes = TestUtils.randomByteArray(100);
        Buffer b = Buffer.buffer(bytes);
        byte[] result = new byte[(bytes.length) / 2];
        b.getBytes(((bytes.length) / 4), (((bytes.length) / 4) + ((bytes.length) / 2)), result, (-1));
    }

    private final int numSets = 100;

    @Test
    public void testSetInt() throws Exception {
        testSetInt(Buffer.buffer(((numSets) * 4)));
    }

    @Test
    public void testSetIntExpandBuffer() throws Exception {
        testSetInt(Buffer.buffer());
    }

    @Test
    public void testSetUnsignedInt() throws Exception {
        testSetUnsignedInt(Buffer.buffer(((numSets) * 4)));
    }

    @Test
    public void testSetUnsignedIntExpandBuffer() throws Exception {
        testSetUnsignedInt(Buffer.buffer());
    }

    @Test
    public void testSetLong() throws Exception {
        testSetLong(Buffer.buffer(((numSets) * 8)));
    }

    @Test
    public void testSetLongExpandBuffer() throws Exception {
        testSetLong(Buffer.buffer());
    }

    @Test
    public void testSetByte() throws Exception {
        testSetByte(Buffer.buffer(numSets));
    }

    @Test
    public void testSetByteExpandBuffer() throws Exception {
        testSetByte(Buffer.buffer());
    }

    @Test
    public void testSetUnsignedByte() throws Exception {
        testSetUnsignedByte(Buffer.buffer(numSets));
    }

    @Test
    public void testSetUnsignedByteExpandBuffer() throws Exception {
        testSetUnsignedByte(Buffer.buffer());
    }

    @Test
    public void testSetFloat() throws Exception {
        testSetFloat(Buffer.buffer(((numSets) * 4)));
    }

    @Test
    public void testSetFloatExpandBuffer() throws Exception {
        testSetFloat(Buffer.buffer());
    }

    @Test
    public void testSetDouble() throws Exception {
        testSetDouble(Buffer.buffer(((numSets) * 8)));
    }

    @Test
    public void testSetDoubleExpandBuffer() throws Exception {
        testSetDouble(Buffer.buffer());
    }

    @Test
    public void testSetShort() throws Exception {
        testSetShort(Buffer.buffer(((numSets) * 2)));
    }

    @Test
    public void testSetShortExpandBuffer() throws Exception {
        testSetShort(Buffer.buffer());
    }

    @Test
    public void testSetUnsignedShort() throws Exception {
        testSetUnsignedShort(Buffer.buffer(((numSets) * 2)));
    }

    @Test
    public void testSetUnsignedShortExpandBuffer() throws Exception {
        testSetUnsignedShort(Buffer.buffer());
    }

    @Test
    public void testSetBytesBuffer() throws Exception {
        testSetBytesBuffer(Buffer.buffer(150), Buffer::buffer);
        TestUtils.assertNullPointerException(() -> Buffer.buffer(150).setBytes(0, ((ByteBuffer) (null))));
    }

    @Test
    public void testSetBytesBuffer2() throws Exception {
        testSetBytesBuffer(Buffer.buffer(150), BufferTest.PADDED_BUFFER_FACTORY);
        TestUtils.assertNullPointerException(() -> Buffer.buffer(150).setBytes(0, ((ByteBuffer) (null))));
    }

    @Test
    public void testSetBytesBufferExpandBuffer() throws Exception {
        testSetShort(Buffer.buffer());
    }

    @Test
    public void testSetBytesWithOffsetAndLen() throws Exception {
        int bytesLen = 100;
        byte[] bytes = TestUtils.randomByteArray(bytesLen);
        int len = bytesLen - 2;
        Buffer b = Buffer.buffer();
        b.setByte(0, ((byte) ('0')));
        b.setBytes(1, bytes, 1, len);
        Assert.assertEquals(b.length(), (len + 1));
        byte[] copy = new byte[len];
        System.arraycopy(bytes, 1, copy, 0, len);
        Assert.assertTrue(TestUtils.byteArraysEqual(copy, b.getBytes(1, b.length())));
        b.setBytes(b.length(), bytes, 1, len);
        Assert.assertEquals(b.length(), ((2 * len) + 1));
        TestUtils.assertNullPointerException(() -> Buffer.buffer(150).setBytes(0, ((byte[]) (null))));
        TestUtils.assertNullPointerException(() -> Buffer.buffer(150).setBytes(0, null, 1, len));
    }

    @Test
    public void testSetBufferWithOffsetAndLen() throws Exception {
        testSetBufferWithOffsetAndLen(Buffer::buffer);
    }

    @Test
    public void testSetBufferWithOffsetAndLen2() throws Exception {
        testSetBufferWithOffsetAndLen(BufferTest.PADDED_BUFFER_FACTORY);
    }

    @Test
    public void testSetBytesString() throws Exception {
        testSetBytesString(Buffer.buffer(150));
    }

    @Test
    public void testSetBytesStringExpandBuffer() throws Exception {
        testSetBytesString(Buffer.buffer());
    }

    @Test
    public void testToString() throws Exception {
        String str = TestUtils.randomUnicodeString(100);
        Buffer buff = Buffer.buffer(str);
        Assert.assertEquals(str, buff.toString());
        // TODO toString with encoding
    }

    @Test
    public void testCopy() throws Exception {
        Buffer buff = TestUtils.randomBuffer(100);
        Assert.assertEquals(buff, buff.copy());
        Buffer copy = buff.getBuffer(0, buff.length());
        Assert.assertEquals(buff, copy);
        // Make sure they don't share underlying buffer
        Buffer copy2 = buff.copy();
        buff.setInt(0, 1);
        Assert.assertEquals(copy, copy2);
    }

    @Test
    public void testCreateBuffers() throws Exception {
        Buffer buff = Buffer.buffer(1000);
        Assert.assertEquals(0, buff.length());
        String str = TestUtils.randomUnicodeString(100);
        buff = Buffer.buffer(str);
        Assert.assertEquals(buff.length(), str.getBytes("UTF-8").length);
        Assert.assertEquals(str, buff.toString());
        // TODO create with string with encoding
        byte[] bytes = TestUtils.randomByteArray(100);
        buff = Buffer.buffer(bytes);
        Assert.assertEquals(buff.length(), bytes.length);
        Assert.assertEquals(Buffer.buffer(bytes), Buffer.buffer(buff.getBytes()));
    }

    @Test
    public void testSlice1() throws Exception {
        Buffer buff = TestUtils.randomBuffer(100);
        Buffer sliced = buff.slice();
        Assert.assertEquals(buff, sliced);
        long rand = TestUtils.randomLong();
        sliced.setLong(0, rand);
        Assert.assertEquals(rand, buff.getLong(0));
        buff.appendString(TestUtils.randomUnicodeString(100));
        Assert.assertEquals(100, sliced.length());
    }

    @Test
    public void testSlice2() throws Exception {
        Buffer buff = TestUtils.randomBuffer(100);
        Buffer sliced = buff.slice(10, 20);
        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(buff.getByte((10 + i)), sliced.getByte(i));
        }
        long rand = TestUtils.randomLong();
        sliced.setLong(0, rand);
        Assert.assertEquals(rand, buff.getLong(10));
        buff.appendString(TestUtils.randomUnicodeString(100));
        Assert.assertEquals(10, sliced.length());
    }

    @Test
    public void testToJsonObject() throws Exception {
        JsonObject obj = new JsonObject();
        obj.put("wibble", "wibble_value");
        obj.put("foo", 5);
        obj.put("bar", true);
        Buffer buff = Buffer.buffer(obj.encode());
        Assert.assertEquals(obj, buff.toJsonObject());
        buff = Buffer.buffer(TestUtils.randomAlphaString(10));
        try {
            buff.toJsonObject();
            Assert.fail();
        } catch (DecodeException ignore) {
        }
    }

    @Test
    public void testToJsonArray() throws Exception {
        JsonArray arr = new JsonArray();
        arr.add("wibble");
        arr.add(5);
        arr.add(true);
        Buffer buff = Buffer.buffer(arr.encode());
        Assert.assertEquals(arr, buff.toJsonArray());
        buff = Buffer.buffer(TestUtils.randomAlphaString(10));
        try {
            buff.toJsonObject();
            Assert.fail();
        } catch (DecodeException ignore) {
        }
    }

    @Test
    public void testLength() throws Exception {
        byte[] bytes = TestUtils.randomByteArray(100);
        Buffer buffer = Buffer.buffer(bytes);
        Assert.assertEquals(100, Buffer.buffer(buffer.getByteBuf()).length());
    }

    @Test
    public void testLength2() throws Exception {
        byte[] bytes = TestUtils.randomByteArray(100);
        Assert.assertEquals(90, Buffer.buffer(Unpooled.copiedBuffer(bytes).slice(10, 90)).length());
    }

    @Test
    public void testAppendDoesNotModifyByteBufIndex() throws Exception {
        ByteBuf buf = Unpooled.copiedBuffer("foobar".getBytes());
        Assert.assertEquals(0, buf.readerIndex());
        Assert.assertEquals(6, buf.writerIndex());
        Buffer buffer = Buffer.buffer(buf);
        Buffer other = Buffer.buffer("prefix");
        other.appendBuffer(buffer);
        Assert.assertEquals(0, buf.readerIndex());
        Assert.assertEquals(6, buf.writerIndex());
        Assert.assertEquals(other.toString(), "prefixfoobar");
    }

    @Test
    public void testDirect() {
        Buffer buff = factory.directBuffer("hello world".getBytes());
        Assert.assertEquals("hello world", buff.toString());
        buff.appendString(" foobar");
        Assert.assertEquals("hello world foobar", buff.toString());
        ByteBuf bb = buff.getByteBuf().unwrap();
        Assert.assertTrue(bb.isDirect());
        Assert.assertTrue(bb.release());
        try {
            // Check it's deallocated
            buff.toString();
            Assert.fail();
        } catch (IllegalReferenceCountException e) {
        }
    }
}

