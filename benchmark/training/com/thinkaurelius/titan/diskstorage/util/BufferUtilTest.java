package com.thinkaurelius.titan.diskstorage.util;


import com.google.common.primitives.Longs;
import com.thinkaurelius.titan.diskstorage.StaticBuffer;
import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class BufferUtilTest {
    private static final Random random = new Random();

    @Test
    public void testCompareRandom() {
        int trials = 100000;
        for (int t = 0; t < trials; t++) {
            long val1 = Math.abs(BufferUtilTest.random.nextLong());
            long val2 = Math.abs(BufferUtilTest.random.nextLong());
            StaticBuffer b1 = BufferUtil.getLongBuffer(val1);
            StaticBuffer b2 = BufferUtil.getLongBuffer(val2);
            // Compare
            Assert.assertEquals(((val1 + " : ") + val2), Math.signum(Longs.compare(val1, val2)), Math.signum(b1.compareTo(b2)), 0.01);
            Assert.assertEquals(Math.signum(Longs.compare(val2, val1)), Math.signum(b2.compareTo(b1)), 0.01);
            Assert.assertEquals(0, b1.compareTo(b1));
            ByteBuffer bb1 = BufferUtilTest.of(val1);
            ByteBuffer bb2 = BufferUtilTest.of(val2);
            Assert.assertEquals(((val1 + " : ") + val2), Math.signum(Longs.compare(val1, val2)), Math.signum(ByteBufferUtil.compare(bb1, bb2)), 0.01);
            Assert.assertEquals(Math.signum(Longs.compare(val2, val1)), Math.signum(ByteBufferUtil.compare(bb2, bb1)), 0.01);
            Assert.assertEquals(0, ByteBufferUtil.compare(bb1, bb1));
            // Mixed Equals
            if (0.5 < (Math.random()))
                val2 = val1;

            ByteBuffer bb = BufferUtilTest.of(val2);
            Assert.assertEquals((val1 == val2), BufferUtil.equals(b1, bb));
        }
    }

    @Test
    public void testNextBigger() {
        int trials = 100000;
        for (int t = 0; t < trials; t++) {
            long val = (BufferUtilTest.random.nextLong()) >>> 1;
            assert val >= 0;
            StaticBuffer b = BufferUtil.getLongBuffer(val);
            Assert.assertEquals(val, b.getLong(0));
            StaticBuffer bn = BufferUtil.nextBiggerBuffer(b);
            Assert.assertEquals(8, bn.length());
            Assert.assertEquals((val + 1), bn.getLong(0));
        }
        try {
            StaticBuffer b = BufferUtil.getLongBuffer((-1));
            BufferUtil.nextBiggerBuffer(b);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        StaticBuffer b = BufferUtil.getLongBuffer((-1));
        StaticBuffer bn = BufferUtil.nextBiggerBufferAllowOverflow(b);
        Assert.assertEquals(8, bn.length());
        Assert.assertTrue(BufferUtil.zeroBuffer(8).equals(bn));
    }

    @Test
    public void staticArrayBufferTest() {
        long[] values = new long[]{ 2342342342L, 2342, 0, -1, -214252345234L };
        byte[] array = new byte[(values.length) * 8];
        for (int i = 0; i < (values.length); i++) {
            StaticArrayBuffer.putLong(array, (i * 8), values[i]);
        }
        for (int i = 0; i < (values.length); i++) {
            Assert.assertEquals(values[i], StaticArrayBuffer.getLong(array, (i * 8)));
        }
    }
}

