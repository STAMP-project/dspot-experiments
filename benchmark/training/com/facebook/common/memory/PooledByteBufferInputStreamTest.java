/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.memory;


import com.facebook.imagepipeline.testing.TrivialPooledByteBuffer;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests for {@link PooledByteBufferInputStream}
 */
@RunWith(RobolectricTestRunner.class)
public class PooledByteBufferInputStreamTest {
    private static final byte[] BYTES = new byte[]{ 1, 123, -20, 3, 6, 23, 1 };

    private PooledByteBufferInputStream mStream;

    @Test
    public void testBasic() {
        Assert.assertEquals(0, mStream.mOffset);
        Assert.assertEquals(0, mStream.mMark);
        Assert.assertEquals(PooledByteBufferInputStreamTest.BYTES.length, mStream.available());
        Assert.assertTrue(mStream.markSupported());
    }

    @Test
    public void testMark() {
        mStream.skip(2);
        mStream.mark(0);
        Assert.assertEquals(2, mStream.mMark);
        mStream.read();
        Assert.assertEquals(2, mStream.mMark);
        mStream.mark(0);
        Assert.assertEquals(3, mStream.mMark);
    }

    @Test
    public void testReset() {
        mStream.skip(2);
        mStream.reset();
        Assert.assertEquals(0, mStream.mOffset);
    }

    @Test
    public void testAvailable() {
        Assert.assertEquals(PooledByteBufferInputStreamTest.BYTES.length, mStream.available());
        mStream.skip(3);
        Assert.assertEquals(((PooledByteBufferInputStreamTest.BYTES.length) - 3), mStream.available());
        mStream.skip(PooledByteBufferInputStreamTest.BYTES.length);
        Assert.assertEquals(0, mStream.available());
    }

    @Test
    public void testSkip() {
        Assert.assertEquals(2, mStream.skip(2));
        Assert.assertEquals(2, mStream.mOffset);
        Assert.assertEquals(3, mStream.skip(3));
        Assert.assertEquals(5, mStream.mOffset);
        Assert.assertEquals(((PooledByteBufferInputStreamTest.BYTES.length) - 5), mStream.skip(PooledByteBufferInputStreamTest.BYTES.length));
        Assert.assertEquals(0, mStream.skip(PooledByteBufferInputStreamTest.BYTES.length));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSkipNegative() {
        mStream.skip((-4));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testReadWithErrors() {
        mStream.read(new byte[64], 10, 55);
    }

    @Test
    public void testRead_SingleByte() {
        for (int i = 0; i < (PooledByteBufferInputStreamTest.BYTES.length); ++i) {
            Assert.assertEquals((((int) (PooledByteBufferInputStreamTest.BYTES[i])) & 255), mStream.read());
        }
        Assert.assertEquals((-1), mStream.read());
    }

    @Test
    public void testRead_ToByteArray() {
        byte[] buf = new byte[64];
        Assert.assertEquals(0, mStream.read(buf, 0, 0));
        Assert.assertEquals(0, mStream.mOffset);
        Assert.assertEquals(3, mStream.read(buf, 0, 3));
        Assert.assertEquals(3, mStream.mOffset);
        PooledByteBufferInputStreamTest.assertArrayEquals(PooledByteBufferInputStreamTest.BYTES, buf, 3);
        for (int i = 3; i < (buf.length); ++i) {
            Assert.assertEquals(0, buf[i]);
        }
        int available = (PooledByteBufferInputStreamTest.BYTES.length) - (mStream.mOffset);
        Assert.assertEquals(available, mStream.read(buf, 3, (available + 1)));
        Assert.assertEquals(PooledByteBufferInputStreamTest.BYTES.length, mStream.mOffset);
        PooledByteBufferInputStreamTest.assertArrayEquals(PooledByteBufferInputStreamTest.BYTES, buf, available);
        Assert.assertEquals((-1), mStream.read(buf, 0, 1));
        Assert.assertEquals(PooledByteBufferInputStreamTest.BYTES.length, mStream.mOffset);
    }

    @Test
    public void testRead_ToByteArray2() {
        byte[] buf = new byte[(PooledByteBufferInputStreamTest.BYTES.length) + 10];
        Assert.assertEquals(PooledByteBufferInputStreamTest.BYTES.length, mStream.read(buf));
        PooledByteBufferInputStreamTest.assertArrayEquals(PooledByteBufferInputStreamTest.BYTES, buf, PooledByteBufferInputStreamTest.BYTES.length);
    }

    @Test
    public void testRead_ToByteArray3() {
        byte[] buf = new byte[(PooledByteBufferInputStreamTest.BYTES.length) - 1];
        Assert.assertEquals(buf.length, mStream.read(buf));
        Assert.assertEquals(buf.length, mStream.mOffset);
        PooledByteBufferInputStreamTest.assertArrayEquals(PooledByteBufferInputStreamTest.BYTES, buf, buf.length);
    }

    @Test
    public void testCreateEmptyStream() throws Exception {
        PooledByteBufferInputStream is = new PooledByteBufferInputStream(new TrivialPooledByteBuffer(new byte[]{  }));
        Assert.assertEquals((-1), is.read());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatingStreamAfterClose() {
        PooledByteBuffer buffer = new TrivialPooledByteBuffer(new byte[]{  });
        buffer.close();
        new PooledByteBufferInputStream(buffer);
    }
}

