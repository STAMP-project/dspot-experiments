package com.weibo.api.motan.protocol.v2motan;


import org.junit.Assert;
import org.junit.Test;


/**
 * Created on 2018/4/10
 *
 * @unknown luominggang
Description:
 */
public class GrowableByteBufferTest {
    @Test
    public void testZigzag() {
        GrowableByteBuffer buffer = new GrowableByteBuffer(16);
        for (int i = 1; i != -2147483648; i <<= 1) {
            assertZigzag32(i, buffer);
        }
        for (int i = -1; i != 0; i <<= 1) {
            assertZigzag32(i, buffer);
        }
        assertZigzag32(-1, buffer);
        assertZigzag32(0, buffer);
        for (long i = 1; i != -9223372036854775808L; i <<= 1) {
            assertZigzag64(i, buffer);
        }
        for (long i = -1; i != 0; i <<= 1) {
            assertZigzag64(i, buffer);
        }
        assertZigzag64(-1L, buffer);
        assertZigzag64(0L, buffer);
        int times = 128;
        int intBase = 1678;
        buffer = new GrowableByteBuffer((times * 8));
        for (int i = 0; i < times; i++) {
            buffer.putZigzag32((intBase * i));
        }
        buffer.flip();
        for (int i = 0; i < times; i++) {
            Assert.assertEquals((i * intBase), buffer.getZigZag32());
        }
        Assert.assertEquals(0, buffer.remaining());
        buffer.clear();
        long longBase = 7289374928L;
        buffer = new GrowableByteBuffer((times * 8));
        for (int i = 0; i < times; i++) {
            buffer.putZigzag64((longBase * i));
        }
        buffer.flip();
        for (int i = 0; i < times; i++) {
            Assert.assertEquals((i * longBase), buffer.getZigZag64());
        }
        Assert.assertEquals(0, buffer.remaining());
        buffer.clear();
    }

    @Test
    public void testGetAndPut() {
        GrowableByteBuffer buffer = new GrowableByteBuffer(16);
        buffer.put(((byte) (1)));
        buffer.flip();
        Assert.assertEquals(((byte) (1)), buffer.get());
        Assert.assertEquals(0, buffer.remaining());
        Assert.assertEquals(1, buffer.position());
        buffer.clear();
        buffer.putShort(((short) (1)));
        buffer.flip();
        Assert.assertEquals(((short) (1)), buffer.getShort());
        Assert.assertEquals(0, buffer.remaining());
        Assert.assertEquals(2, buffer.position());
        buffer.clear();
        buffer.putInt(1);
        buffer.flip();
        Assert.assertEquals(1, buffer.getInt());
        Assert.assertEquals(0, buffer.remaining());
        Assert.assertEquals(4, buffer.position());
        buffer.clear();
        buffer.putLong(1L);
        buffer.flip();
        Assert.assertEquals(1L, buffer.getLong());
        Assert.assertEquals(0, buffer.remaining());
        Assert.assertEquals(8, buffer.position());
        buffer.clear();
        buffer.putFloat(3.1415925F);
        buffer.flip();
        Assert.assertEquals(Float.floatToRawIntBits(3.1415925F), Float.floatToRawIntBits(buffer.getFloat()));
        Assert.assertEquals(0, buffer.remaining());
        Assert.assertEquals(4, buffer.position());
        buffer.clear();
        buffer.putDouble(3.1415926);
        buffer.flip();
        Assert.assertEquals(Double.doubleToRawLongBits(3.1415926), Double.doubleToRawLongBits(buffer.getDouble()));
        Assert.assertEquals(0, buffer.remaining());
        Assert.assertEquals(8, buffer.position());
        buffer.clear();
        byte[] bs = new byte[]{ 1, 2, 3, 4, 5, 6 };
        byte[] dst = new byte[bs.length];
        buffer.put(bs);
        buffer.flip();
        buffer.get(dst);
        Assert.assertArrayEquals(bs, dst);
        Assert.assertEquals(0, buffer.remaining());
        Assert.assertEquals(bs.length, buffer.position());
    }

    @Test
    public void testGetAndPutWithIndex() {
        GrowableByteBuffer buffer = new GrowableByteBuffer(1);
        int index = 0;
        buffer.position(1);
        buffer.put(index, ((byte) (1)));
        buffer.flip();
        Assert.assertEquals(((byte) (1)), buffer.get(index));
        Assert.assertEquals(0, buffer.position());
        buffer.clear();
        buffer.position(2);
        buffer.putShort(index, ((short) (1)));
        buffer.flip();
        Assert.assertEquals(((short) (1)), buffer.getShort(index));
        Assert.assertEquals(0, buffer.position());
        buffer.clear();
        buffer.position(4);
        buffer.putInt(index, 1);
        buffer.flip();
        Assert.assertEquals(1, buffer.getInt(index));
        Assert.assertEquals(0, buffer.position());
        buffer.clear();
        buffer.position(8);
        buffer.putLong(index, 1L);
        buffer.flip();
        Assert.assertEquals(1L, buffer.getLong(index));
        Assert.assertEquals(0, buffer.position());
        buffer.clear();
        buffer.position(4);
        buffer.putFloat(index, 3.1415925F);
        buffer.flip();
        Assert.assertEquals(Float.floatToRawIntBits(3.1415925F), Float.floatToRawIntBits(buffer.getFloat(index)));
        Assert.assertEquals(0, buffer.position());
        buffer.clear();
        buffer.position(8);
        buffer.putDouble(index, 3.1415926);
        buffer.flip();
        Assert.assertEquals(Double.doubleToRawLongBits(3.1415926), Double.doubleToRawLongBits(buffer.getDouble(index)));
        Assert.assertEquals(0, buffer.position());
        buffer.clear();
    }
}

