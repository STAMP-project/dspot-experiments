/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.crypto;


import FSExceptionMessages.NEGATIVE_SEEK;
import ReadOption.SKIP_CHECKSUMS;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import org.apache.hadoop.fs.HasEnhancedByteBufferAccess;
import org.apache.hadoop.fs.PositionedReadable;
import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class CryptoStreamsTestBase {
    protected static final Logger LOG = LoggerFactory.getLogger(CryptoStreamsTestBase.class);

    protected static CryptoCodec codec;

    protected static final byte[] key = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 16, 17, 18, 19, 20, 21, 22 };

    protected static final byte[] iv = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8 };

    protected static final int count = 10000;

    protected static int defaultBufferSize = 8192;

    protected static int smallBufferSize = 1024;

    private byte[] data;

    private int dataLen;

    /**
     * Test crypto reading with different buffer size.
     */
    @Test(timeout = 120000)
    public void testRead() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.defaultBufferSize);
        writeData(out);
        // Default buffer size
        InputStream in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        readCheck(in);
        in.close();
        // Small buffer size
        in = getInputStream(CryptoStreamsTestBase.smallBufferSize);
        readCheck(in);
        in.close();
    }

    /**
     * Test crypto writing with different buffer size.
     */
    @Test(timeout = 120000)
    public void testWrite() throws Exception {
        // Default buffer size
        writeCheck(CryptoStreamsTestBase.defaultBufferSize);
        // Small buffer size
        writeCheck(CryptoStreamsTestBase.smallBufferSize);
    }

    /**
     * Test crypto with different IV.
     */
    @Test(timeout = 120000)
    public void testCryptoIV() throws Exception {
        byte[] iv1 = CryptoStreamsTestBase.iv.clone();
        // Counter base: Long.MAX_VALUE
        setCounterBaseForIV(iv1, Long.MAX_VALUE);
        cryptoCheck(iv1);
        // Counter base: Long.MAX_VALUE - 1
        setCounterBaseForIV(iv1, ((Long.MAX_VALUE) - 1));
        cryptoCheck(iv1);
        // Counter base: Integer.MAX_VALUE
        setCounterBaseForIV(iv1, Integer.MAX_VALUE);
        cryptoCheck(iv1);
        // Counter base: 0
        setCounterBaseForIV(iv1, 0);
        cryptoCheck(iv1);
        // Counter base: -1
        setCounterBaseForIV(iv1, (-1));
        cryptoCheck(iv1);
    }

    /**
     * Test hflush/hsync of crypto output stream, and with different buffer size.
     */
    @Test(timeout = 120000)
    public void testSyncable() throws IOException {
        syncableCheck();
    }

    /**
     * Test positioned read.
     */
    @Test(timeout = 120000)
    public void testPositionedRead() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.defaultBufferSize);
        writeData(out);
        InputStream in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        // Pos: 1/3 dataLen
        positionedReadCheck(in, ((dataLen) / 3));
        // Pos: 1/2 dataLen
        positionedReadCheck(in, ((dataLen) / 2));
        in.close();
    }

    /**
     * Test read fully
     */
    @Test(timeout = 120000)
    public void testReadFully() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.defaultBufferSize);
        writeData(out);
        InputStream in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        final int len1 = (dataLen) / 4;
        // Read len1 bytes
        byte[] readData = new byte[len1];
        readAll(in, readData, 0, len1);
        byte[] expectedData = new byte[len1];
        System.arraycopy(data, 0, expectedData, 0, len1);
        Assert.assertArrayEquals(readData, expectedData);
        // Pos: 1/3 dataLen
        readFullyCheck(in, ((dataLen) / 3));
        // Read len1 bytes
        readData = new byte[len1];
        readAll(in, readData, 0, len1);
        expectedData = new byte[len1];
        System.arraycopy(data, len1, expectedData, 0, len1);
        Assert.assertArrayEquals(readData, expectedData);
        // Pos: 1/2 dataLen
        readFullyCheck(in, ((dataLen) / 2));
        // Read len1 bytes
        readData = new byte[len1];
        readAll(in, readData, 0, len1);
        expectedData = new byte[len1];
        System.arraycopy(data, (2 * len1), expectedData, 0, len1);
        Assert.assertArrayEquals(readData, expectedData);
        in.close();
    }

    /**
     * Test seek to different position.
     */
    @Test(timeout = 120000)
    public void testSeek() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.defaultBufferSize);
        writeData(out);
        InputStream in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        // Pos: 1/3 dataLen
        seekCheck(in, ((dataLen) / 3));
        // Pos: 0
        seekCheck(in, 0);
        // Pos: 1/2 dataLen
        seekCheck(in, ((dataLen) / 2));
        final long pos = getPos();
        // Pos: -3
        try {
            seekCheck(in, (-3));
            Assert.fail("Seek to negative offset should fail.");
        } catch (EOFException e) {
            GenericTestUtils.assertExceptionContains(NEGATIVE_SEEK, e);
        }
        Assert.assertEquals(pos, ((Seekable) (in)).getPos());
        // Pos: dataLen + 3
        try {
            seekCheck(in, ((dataLen) + 3));
            Assert.fail("Seek after EOF should fail.");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Cannot seek after EOF", e);
        }
        Assert.assertEquals(pos, ((Seekable) (in)).getPos());
        in.close();
    }

    /**
     * Test get position.
     */
    @Test(timeout = 120000)
    public void testGetPos() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.defaultBufferSize);
        writeData(out);
        // Default buffer size
        InputStream in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        byte[] result = new byte[dataLen];
        int n1 = readAll(in, result, 0, ((dataLen) / 3));
        Assert.assertEquals(n1, ((Seekable) (in)).getPos());
        int n2 = readAll(in, result, n1, ((dataLen) - n1));
        Assert.assertEquals((n1 + n2), ((Seekable) (in)).getPos());
        in.close();
    }

    @Test(timeout = 120000)
    public void testAvailable() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.defaultBufferSize);
        writeData(out);
        // Default buffer size
        InputStream in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        byte[] result = new byte[dataLen];
        int n1 = readAll(in, result, 0, ((dataLen) / 3));
        Assert.assertEquals(in.available(), ((dataLen) - n1));
        int n2 = readAll(in, result, n1, ((dataLen) - n1));
        Assert.assertEquals(in.available(), (((dataLen) - n1) - n2));
        in.close();
    }

    /**
     * Test skip.
     */
    @Test(timeout = 120000)
    public void testSkip() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.defaultBufferSize);
        writeData(out);
        // Default buffer size
        InputStream in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        byte[] result = new byte[dataLen];
        int n1 = readAll(in, result, 0, ((dataLen) / 3));
        Assert.assertEquals(n1, ((Seekable) (in)).getPos());
        long skipped = in.skip(((dataLen) / 3));
        int n2 = readAll(in, result, 0, dataLen);
        Assert.assertEquals(dataLen, ((n1 + skipped) + n2));
        byte[] readData = new byte[n2];
        System.arraycopy(result, 0, readData, 0, n2);
        byte[] expectedData = new byte[n2];
        System.arraycopy(data, ((dataLen) - n2), expectedData, 0, n2);
        Assert.assertArrayEquals(readData, expectedData);
        try {
            skipped = in.skip((-3));
            Assert.fail("Skip Negative length should fail.");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("Negative skip length", e);
        }
        // Skip after EOF
        skipped = in.skip(3);
        Assert.assertEquals(skipped, 0);
        in.close();
    }

    /**
     * Test byte buffer read with different buffer size.
     */
    @Test(timeout = 120000)
    public void testByteBufferRead() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.defaultBufferSize);
        writeData(out);
        // Default buffer size, initial buffer position is 0
        InputStream in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        ByteBuffer buf = ByteBuffer.allocate(((dataLen) + 100));
        byteBufferReadCheck(in, buf, 0);
        in.close();
        // Default buffer size, initial buffer position is not 0
        in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        buf.clear();
        byteBufferReadCheck(in, buf, 11);
        in.close();
        // Small buffer size, initial buffer position is 0
        in = getInputStream(CryptoStreamsTestBase.smallBufferSize);
        buf.clear();
        byteBufferReadCheck(in, buf, 0);
        in.close();
        // Small buffer size, initial buffer position is not 0
        in = getInputStream(CryptoStreamsTestBase.smallBufferSize);
        buf.clear();
        byteBufferReadCheck(in, buf, 11);
        in.close();
        // Direct buffer, default buffer size, initial buffer position is 0
        in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        buf = ByteBuffer.allocateDirect(((dataLen) + 100));
        byteBufferReadCheck(in, buf, 0);
        in.close();
        // Direct buffer, default buffer size, initial buffer position is not 0
        in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        buf.clear();
        byteBufferReadCheck(in, buf, 11);
        in.close();
        // Direct buffer, small buffer size, initial buffer position is 0
        in = getInputStream(CryptoStreamsTestBase.smallBufferSize);
        buf.clear();
        byteBufferReadCheck(in, buf, 0);
        in.close();
        // Direct buffer, small buffer size, initial buffer position is not 0
        in = getInputStream(CryptoStreamsTestBase.smallBufferSize);
        buf.clear();
        byteBufferReadCheck(in, buf, 11);
        in.close();
    }

    @Test(timeout = 120000)
    public void testCombinedOp() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.defaultBufferSize);
        writeData(out);
        final int len1 = (dataLen) / 8;
        final int len2 = (dataLen) / 10;
        InputStream in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        // Read len1 data.
        byte[] readData = new byte[len1];
        readAll(in, readData, 0, len1);
        byte[] expectedData = new byte[len1];
        System.arraycopy(data, 0, expectedData, 0, len1);
        Assert.assertArrayEquals(readData, expectedData);
        long pos = getPos();
        Assert.assertEquals(len1, pos);
        // Seek forward len2
        ((Seekable) (in)).seek((pos + len2));
        // Skip forward len2
        long n = in.skip(len2);
        Assert.assertEquals(len2, n);
        // Pos: 1/4 dataLen
        positionedReadCheck(in, ((dataLen) / 4));
        // Pos should be len1 + len2 + len2
        pos = getPos();
        Assert.assertEquals(((len1 + len2) + len2), pos);
        // Read forward len1
        ByteBuffer buf = ByteBuffer.allocate(len1);
        int nRead = read(buf);
        Assert.assertEquals(nRead, buf.position());
        readData = new byte[nRead];
        buf.rewind();
        buf.get(readData);
        expectedData = new byte[nRead];
        System.arraycopy(data, ((int) (pos)), expectedData, 0, nRead);
        Assert.assertArrayEquals(readData, expectedData);
        long lastPos = pos;
        // Pos should be lastPos + nRead
        pos = getPos();
        Assert.assertEquals((lastPos + nRead), pos);
        // Pos: 1/3 dataLen
        positionedReadCheck(in, ((dataLen) / 3));
        // Read forward len1
        readData = new byte[len1];
        readAll(in, readData, 0, len1);
        expectedData = new byte[len1];
        System.arraycopy(data, ((int) (pos)), expectedData, 0, len1);
        Assert.assertArrayEquals(readData, expectedData);
        lastPos = pos;
        // Pos should be lastPos + len1
        pos = getPos();
        Assert.assertEquals((lastPos + len1), pos);
        // Read forward len1
        buf = ByteBuffer.allocate(len1);
        nRead = read(buf);
        Assert.assertEquals(nRead, buf.position());
        readData = new byte[nRead];
        buf.rewind();
        buf.get(readData);
        expectedData = new byte[nRead];
        System.arraycopy(data, ((int) (pos)), expectedData, 0, nRead);
        Assert.assertArrayEquals(readData, expectedData);
        lastPos = pos;
        // Pos should be lastPos + nRead
        pos = getPos();
        Assert.assertEquals((lastPos + nRead), pos);
        // ByteBuffer read after EOF
        seek(dataLen);
        buf.clear();
        n = ((org.apache.hadoop.fs.ByteBufferReadable) (in)).read(buf);
        Assert.assertEquals(n, (-1));
        in.close();
    }

    @Test(timeout = 120000)
    public void testSeekToNewSource() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.defaultBufferSize);
        writeData(out);
        InputStream in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        final int len1 = (dataLen) / 8;
        byte[] readData = new byte[len1];
        readAll(in, readData, 0, len1);
        // Pos: 1/3 dataLen
        seekToNewSourceCheck(in, ((dataLen) / 3));
        // Pos: 0
        seekToNewSourceCheck(in, 0);
        // Pos: 1/2 dataLen
        seekToNewSourceCheck(in, ((dataLen) / 2));
        // Pos: -3
        try {
            seekToNewSourceCheck(in, (-3));
            Assert.fail("Seek to negative offset should fail.");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains(("Cannot seek to negative " + "offset"), e);
        }
        // Pos: dataLen + 3
        try {
            seekToNewSourceCheck(in, ((dataLen) + 3));
            Assert.fail("Seek after EOF should fail.");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains(("Attempted to read past " + "end of file"), e);
        }
        in.close();
    }

    @Test(timeout = 120000)
    public void testHasEnhancedByteBufferAccess() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.defaultBufferSize);
        writeData(out);
        InputStream in = getInputStream(CryptoStreamsTestBase.defaultBufferSize);
        final int len1 = (dataLen) / 8;
        // ByteBuffer size is len1
        ByteBuffer buffer = ((HasEnhancedByteBufferAccess) (in)).read(getBufferPool(), len1, EnumSet.of(SKIP_CHECKSUMS));
        int n1 = buffer.remaining();
        byte[] readData = new byte[n1];
        buffer.get(readData);
        byte[] expectedData = new byte[n1];
        System.arraycopy(data, 0, expectedData, 0, n1);
        Assert.assertArrayEquals(readData, expectedData);
        releaseBuffer(buffer);
        // Read len1 bytes
        readData = new byte[len1];
        readAll(in, readData, 0, len1);
        expectedData = new byte[len1];
        System.arraycopy(data, n1, expectedData, 0, len1);
        Assert.assertArrayEquals(readData, expectedData);
        // ByteBuffer size is len1
        buffer = ((HasEnhancedByteBufferAccess) (in)).read(getBufferPool(), len1, EnumSet.of(SKIP_CHECKSUMS));
        int n2 = buffer.remaining();
        readData = new byte[n2];
        buffer.get(readData);
        expectedData = new byte[n2];
        System.arraycopy(data, (n1 + len1), expectedData, 0, n2);
        Assert.assertArrayEquals(readData, expectedData);
        releaseBuffer(buffer);
        in.close();
    }

    /**
     * Test unbuffer.
     */
    @Test(timeout = 120000)
    public void testUnbuffer() throws Exception {
        OutputStream out = getOutputStream(CryptoStreamsTestBase.smallBufferSize);
        writeData(out);
        // Test buffered read
        try (InputStream in = getInputStream(CryptoStreamsTestBase.smallBufferSize)) {
            // Test unbuffer after buffered read
            readCheck(in);
            unbuffer();
            if (in instanceof Seekable) {
                // Test buffered read again after unbuffer
                // Must seek to the beginning first
                seek(0);
                readCheck(in);
            }
            // Test close after unbuffer
            unbuffer();
            // The close will be called when exiting this try-with-resource block
        }
        // Test pread
        try (InputStream in = getInputStream(CryptoStreamsTestBase.smallBufferSize)) {
            if (in instanceof PositionedReadable) {
                PositionedReadable pin = ((PositionedReadable) (in));
                // Test unbuffer after pread
                preadCheck(pin);
                unbuffer();
                // Test pread again after unbuffer
                preadCheck(pin);
                // Test close after unbuffer
                unbuffer();
                // The close will be called when exiting this try-with-resource block
            }
        }
    }
}

