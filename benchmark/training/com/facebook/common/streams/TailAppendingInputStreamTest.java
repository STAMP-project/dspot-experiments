/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */
package com.facebook.common.streams;


import com.facebook.common.internal.ByteStreams;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class TailAppendingInputStreamTest {
    private static final int RANDOM_SEED = 1023;

    private static final int BYTES_LENGTH = 1024;

    private static final int TAIL_LENGTH = 1024;

    private static final int OUTPUT_LENGTH = (TailAppendingInputStreamTest.BYTES_LENGTH) + (TailAppendingInputStreamTest.TAIL_LENGTH);

    private byte[] mBytes;

    private byte[] mTail;

    private byte[] mOutputBuffer;

    private TailAppendingInputStream mTailAppendingInputStream;

    @Test
    public void testDoesReadSingleBytes() throws Exception {
        for (byte b : mBytes) {
            Assert.assertEquals((((int) (b)) & 255), mTailAppendingInputStream.read());
        }
        for (byte b : mTail) {
            Assert.assertEquals((((int) (b)) & 255), mTailAppendingInputStream.read());
        }
    }

    @Test
    public void testDoesNotReadTooMuch_singleBytes() throws Exception {
        for (int i = 0; i < ((mBytes.length) + (mTail.length)); ++i) {
            mTailAppendingInputStream.read();
        }
        Assert.assertEquals((-1), mTailAppendingInputStream.read());
    }

    @Test
    public void testDoesReadMultipleBytes() throws Exception {
        ByteStreams.readFully(mTailAppendingInputStream, mOutputBuffer, 0, TailAppendingInputStreamTest.OUTPUT_LENGTH);
        Assert.assertArrayEquals(mBytes, Arrays.copyOfRange(mOutputBuffer, 0, TailAppendingInputStreamTest.BYTES_LENGTH));
        Assert.assertArrayEquals(mTail, Arrays.copyOfRange(mOutputBuffer, TailAppendingInputStreamTest.BYTES_LENGTH, TailAppendingInputStreamTest.OUTPUT_LENGTH));
    }

    @Test
    public void testDoesNotReadTooMuch_multipleBytes() throws Exception {
        byte[] buffer = new byte[(TailAppendingInputStreamTest.OUTPUT_LENGTH) + 1];
        Assert.assertEquals(TailAppendingInputStreamTest.OUTPUT_LENGTH, ByteStreams.read(mTailAppendingInputStream, buffer, 0, ((TailAppendingInputStreamTest.OUTPUT_LENGTH) + 1)));
        Assert.assertEquals((-1), mTailAppendingInputStream.read());
    }

    @Test
    public void testUnalignedReads() throws IOException {
        Assert.assertEquals(128, mTailAppendingInputStream.read(mOutputBuffer, 256, 128));
        Assert.assertArrayEquals(Arrays.copyOfRange(mBytes, 0, 128), Arrays.copyOfRange(mOutputBuffer, 256, 384));
        Arrays.fill(mOutputBuffer, 256, 384, ((byte) (0)));
        for (byte b : mOutputBuffer) {
            Assert.assertEquals(0, b);
        }
        Assert.assertEquals(((TailAppendingInputStreamTest.BYTES_LENGTH) - 128), mTailAppendingInputStream.read(mOutputBuffer));
        Assert.assertArrayEquals(Arrays.copyOfRange(mBytes, 128, TailAppendingInputStreamTest.BYTES_LENGTH), Arrays.copyOfRange(mOutputBuffer, 0, ((TailAppendingInputStreamTest.BYTES_LENGTH) - 128)));
        Arrays.fill(mOutputBuffer, 0, ((TailAppendingInputStreamTest.BYTES_LENGTH) - 128), ((byte) (0)));
        for (byte b : mOutputBuffer) {
            Assert.assertEquals(0, b);
        }
        Assert.assertEquals(128, mTailAppendingInputStream.read(mOutputBuffer, 256, 128));
        Assert.assertArrayEquals(Arrays.copyOfRange(mTail, 0, 128), Arrays.copyOfRange(mOutputBuffer, 256, 384));
        Arrays.fill(mOutputBuffer, 256, 384, ((byte) (0)));
        for (byte b : mOutputBuffer) {
            Assert.assertEquals(0, b);
        }
        Assert.assertEquals(((TailAppendingInputStreamTest.TAIL_LENGTH) - 128), mTailAppendingInputStream.read(mOutputBuffer));
        Assert.assertArrayEquals(Arrays.copyOfRange(mTail, 128, TailAppendingInputStreamTest.TAIL_LENGTH), Arrays.copyOfRange(mOutputBuffer, 0, ((TailAppendingInputStreamTest.TAIL_LENGTH) - 128)));
        Arrays.fill(mOutputBuffer, 0, ((TailAppendingInputStreamTest.TAIL_LENGTH) - 128), ((byte) (0)));
        for (byte b : mOutputBuffer) {
            Assert.assertEquals(0, b);
        }
        Assert.assertEquals((-1), mTailAppendingInputStream.read());
    }

    @Test
    public void testMark() throws IOException {
        Assert.assertEquals(128, mTailAppendingInputStream.read(mOutputBuffer, 0, 128));
        mTailAppendingInputStream.mark(TailAppendingInputStreamTest.BYTES_LENGTH);
        Assert.assertEquals(TailAppendingInputStreamTest.BYTES_LENGTH, ByteStreams.read(mTailAppendingInputStream, mOutputBuffer, 0, TailAppendingInputStreamTest.BYTES_LENGTH));
        mTailAppendingInputStream.reset();
        for (byte b : Arrays.copyOfRange(mOutputBuffer, 0, TailAppendingInputStreamTest.BYTES_LENGTH)) {
            Assert.assertEquals((((int) (b)) & 255), mTailAppendingInputStream.read());
        }
    }
}

