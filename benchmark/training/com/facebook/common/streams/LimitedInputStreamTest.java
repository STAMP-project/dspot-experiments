/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.streams;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class LimitedInputStreamTest {
    private static final int RANDOM_SEED = 1023;

    private static final int BYTES_LENGTH = 1024;

    private static final int LIMITED_LENGTH = (LimitedInputStreamTest.BYTES_LENGTH) / 2;

    private byte[] mData;

    private byte[] mReadBuffer;

    private byte[] mZeroTail;

    private ByteArrayInputStream mOriginalStream;

    private LimitedInputStream mLimitedStream;

    @Test
    public void testBasic() throws Exception {
        Assert.assertEquals(LimitedInputStreamTest.LIMITED_LENGTH, mLimitedStream.available());
        Assert.assertTrue(mLimitedStream.markSupported());
    }

    @Test
    public void testDoesReadSingleBytes() throws Exception {
        for (int i = 0; i < (LimitedInputStreamTest.LIMITED_LENGTH); ++i) {
            Assert.assertEquals((((int) (mData[i])) & 255), mLimitedStream.read());
        }
    }

    @Test
    public void testDoesNotReadTooMuch_singleBytes() throws Exception {
        for (int i = 0; i < (LimitedInputStreamTest.BYTES_LENGTH); ++i) {
            final int lastByte = mLimitedStream.read();
            Assert.assertEquals((i >= (LimitedInputStreamTest.LIMITED_LENGTH)), (lastByte == (-1)));
        }
        Assert.assertEquals(((LimitedInputStreamTest.BYTES_LENGTH) - (LimitedInputStreamTest.LIMITED_LENGTH)), mOriginalStream.available());
    }

    @Test
    public void testDoesReadMultipleBytes() throws Exception {
        Assert.assertEquals(LimitedInputStreamTest.LIMITED_LENGTH, mLimitedStream.read(mReadBuffer, 0, LimitedInputStreamTest.LIMITED_LENGTH));
        Assert.assertArrayEquals(Arrays.copyOfRange(mData, 0, LimitedInputStreamTest.LIMITED_LENGTH), Arrays.copyOfRange(mReadBuffer, 0, LimitedInputStreamTest.LIMITED_LENGTH));
        Assert.assertArrayEquals(mZeroTail, Arrays.copyOfRange(mReadBuffer, LimitedInputStreamTest.LIMITED_LENGTH, LimitedInputStreamTest.BYTES_LENGTH));
    }

    @Test
    public void testDoesNotReadTooMuch_multipleBytes() throws Exception {
        Assert.assertEquals(LimitedInputStreamTest.LIMITED_LENGTH, mLimitedStream.read(mReadBuffer, 0, LimitedInputStreamTest.BYTES_LENGTH));
        final byte[] readBufferCopy = Arrays.copyOf(mReadBuffer, mReadBuffer.length);
        Assert.assertEquals((-1), mLimitedStream.read(mReadBuffer, 0, LimitedInputStreamTest.BYTES_LENGTH));
        Assert.assertArrayEquals(readBufferCopy, mReadBuffer);
        Assert.assertEquals(((LimitedInputStreamTest.BYTES_LENGTH) - (LimitedInputStreamTest.LIMITED_LENGTH)), mOriginalStream.available());
    }

    @Test
    public void testSkip() throws Exception {
        Assert.assertEquals(((LimitedInputStreamTest.LIMITED_LENGTH) / 2), mLimitedStream.skip(((LimitedInputStreamTest.LIMITED_LENGTH) / 2)));
        Assert.assertEquals(((LimitedInputStreamTest.LIMITED_LENGTH) / 2), mLimitedStream.read(mReadBuffer));
        Assert.assertArrayEquals(Arrays.copyOfRange(mData, ((LimitedInputStreamTest.LIMITED_LENGTH) / 2), LimitedInputStreamTest.LIMITED_LENGTH), Arrays.copyOfRange(mReadBuffer, 0, ((LimitedInputStreamTest.LIMITED_LENGTH) / 2)));
    }

    @Test
    public void testDoesNotReadTooMuch_skip() throws Exception {
        Assert.assertEquals(LimitedInputStreamTest.LIMITED_LENGTH, mLimitedStream.skip(LimitedInputStreamTest.BYTES_LENGTH));
        Assert.assertEquals(0, mLimitedStream.skip(LimitedInputStreamTest.BYTES_LENGTH));
        Assert.assertEquals(((LimitedInputStreamTest.BYTES_LENGTH) - (LimitedInputStreamTest.LIMITED_LENGTH)), mOriginalStream.available());
    }

    @Test
    public void testDoesMark() throws Exception {
        mLimitedStream.mark(LimitedInputStreamTest.BYTES_LENGTH);
        mLimitedStream.read(mReadBuffer);
        final byte[] readBufferCopy = Arrays.copyOf(mReadBuffer, mReadBuffer.length);
        Arrays.fill(mReadBuffer, ((byte) (0)));
        mLimitedStream.reset();
        Assert.assertEquals(LimitedInputStreamTest.LIMITED_LENGTH, mLimitedStream.read(mReadBuffer));
        Assert.assertArrayEquals(readBufferCopy, mReadBuffer);
    }

    @Test
    public void testResetsMultipleTimes() throws Exception {
        mLimitedStream.mark(LimitedInputStreamTest.BYTES_LENGTH);
        mLimitedStream.read(mReadBuffer);
        final byte[] readBufferCopy = Arrays.copyOf(mReadBuffer, mReadBuffer.length);
        // first reset
        mLimitedStream.reset();
        Assert.assertEquals(LimitedInputStreamTest.LIMITED_LENGTH, mLimitedStream.read(mReadBuffer));
        // second reset
        Arrays.fill(mReadBuffer, ((byte) (0)));
        mLimitedStream.reset();
        Assert.assertEquals(LimitedInputStreamTest.LIMITED_LENGTH, mLimitedStream.read(mReadBuffer));
        Assert.assertArrayEquals(readBufferCopy, mReadBuffer);
    }

    @Test
    public void testDoesNotReadTooMuch_reset() throws Exception {
        mLimitedStream.mark(LimitedInputStreamTest.BYTES_LENGTH);
        mLimitedStream.read(mReadBuffer);
        mLimitedStream.reset();
        mLimitedStream.read(mReadBuffer);
        Assert.assertEquals(((LimitedInputStreamTest.BYTES_LENGTH) - (LimitedInputStreamTest.LIMITED_LENGTH)), mOriginalStream.available());
    }

    @Test(expected = IOException.class)
    public void testDoesNotRestIfNotMarked() throws Exception {
        mLimitedStream.read(mReadBuffer);
        mLimitedStream.reset();
    }

    @Test
    public void testMultipleMarks() throws IOException {
        mLimitedStream.mark(LimitedInputStreamTest.BYTES_LENGTH);
        Assert.assertEquals(((LimitedInputStreamTest.LIMITED_LENGTH) / 2), mLimitedStream.read(mReadBuffer, 0, ((LimitedInputStreamTest.LIMITED_LENGTH) / 2)));
        mLimitedStream.mark(LimitedInputStreamTest.BYTES_LENGTH);
        Assert.assertEquals(((LimitedInputStreamTest.LIMITED_LENGTH) / 2), mLimitedStream.read(mReadBuffer, ((LimitedInputStreamTest.LIMITED_LENGTH) / 2), ((LimitedInputStreamTest.LIMITED_LENGTH) / 2)));
        mLimitedStream.reset();
        Assert.assertEquals(((LimitedInputStreamTest.LIMITED_LENGTH) / 2), mLimitedStream.read(mReadBuffer));
        Assert.assertArrayEquals(Arrays.copyOfRange(mReadBuffer, 0, ((LimitedInputStreamTest.LIMITED_LENGTH) / 2)), Arrays.copyOfRange(mReadBuffer, ((LimitedInputStreamTest.LIMITED_LENGTH) / 2), LimitedInputStreamTest.LIMITED_LENGTH));
    }
}

