/**
 * Copyright 2017 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.internal;


import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.ZipException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link GzipInflatingBuffer}.
 */
@RunWith(JUnit4.class)
public class GzipInflatingBufferTest {
    private static final String UNCOMPRESSABLE_FILE = "/io/grpc/internal/uncompressable.bin";

    private static final int GZIP_HEADER_MIN_SIZE = 10;

    private static final int GZIP_TRAILER_SIZE = 8;

    private static final int GZIP_HEADER_FLAG_INDEX = 3;

    public static final int GZIP_MAGIC = 35615;

    private static final int FTEXT = 1;

    private static final int FHCRC = 2;

    private static final int FEXTRA = 4;

    private static final int FNAME = 8;

    private static final int FCOMMENT = 16;

    private static final int TRUNCATED_DATA_SIZE = 10;

    private byte[] originalData;

    private byte[] gzippedData;

    private byte[] gzipHeader;

    private byte[] deflatedBytes;

    private byte[] gzipTrailer;

    private byte[] truncatedData;

    private byte[] gzippedTruncatedData;

    private GzipInflatingBuffer gzipInflatingBuffer;

    @Test
    public void gzipInflateWorks() throws Exception {
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertEquals(gzippedData.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
    }

    @Test
    public void splitGzipStreamWorks() throws Exception {
        int initialBytes = 100;
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData, 0, initialBytes));
        byte[] b = new byte[originalData.length];
        int n = gzipInflatingBuffer.inflateBytes(b, 0, originalData.length);
        Assert.assertTrue("inflated bytes expected", (n > 0));
        Assert.assertTrue("gzipInflatingBuffer is not stalled", gzipInflatingBuffer.isStalled());
        Assert.assertEquals(initialBytes, gzipInflatingBuffer.getAndResetBytesConsumed());
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData, initialBytes, ((gzippedData.length) - initialBytes)));
        int bytesRemaining = (originalData.length) - n;
        Assert.assertEquals(bytesRemaining, gzipInflatingBuffer.inflateBytes(b, n, bytesRemaining));
        Assert.assertEquals(((gzippedData.length) - initialBytes), gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
    }

    @Test
    public void inflateBytesObeysOffsetAndLength() throws Exception {
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        int offset = 10;
        int length = 100;
        byte[] b = new byte[(offset + length) + offset];
        Assert.assertEquals(length, gzipInflatingBuffer.inflateBytes(b, offset, length));
        Assert.assertTrue("bytes written before offset", Arrays.equals(new byte[offset], Arrays.copyOfRange(b, 0, offset)));
        Assert.assertTrue("inflated data does not match", Arrays.equals(Arrays.copyOfRange(originalData, 0, length), Arrays.copyOfRange(b, offset, (offset + length))));
        Assert.assertTrue("bytes written beyond length", Arrays.equals(new byte[offset], Arrays.copyOfRange(b, (offset + length), ((offset + length) + offset))));
    }

    @Test
    public void concatenatedStreamsWorks() throws Exception {
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedTruncatedData));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedTruncatedData));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertEquals(gzippedData.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
        Assert.assertEquals(truncatedData.length, gzipInflatingBuffer.inflateBytes(b, 0, truncatedData.length));
        Assert.assertEquals(gzippedTruncatedData.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertEquals(gzippedData.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
        Assert.assertEquals(truncatedData.length, gzipInflatingBuffer.inflateBytes(b, 0, truncatedData.length));
        Assert.assertEquals(gzippedTruncatedData.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
    }

    @Test
    public void requestingTooManyBytesStillReturnsEndOfBlock() throws Exception {
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        int len = 2 * (originalData.length);
        byte[] b = new byte[len];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, len));
        Assert.assertEquals(gzippedData.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue(gzipInflatingBuffer.isStalled());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, Arrays.copyOf(b, originalData.length)));
    }

    @Test
    public void closeStopsDecompression() throws Exception {
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        byte[] b = new byte[1];
        gzipInflatingBuffer.inflateBytes(b, 0, 1);
        gzipInflatingBuffer.close();
        try {
            gzipInflatingBuffer.inflateBytes(b, 0, 1);
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException expectedException) {
            Assert.assertEquals("GzipInflatingBuffer is closed", expectedException.getMessage());
        }
    }

    @Test
    public void isStalledReturnsTrueAtEndOfStream() throws Exception {
        int bytesToWithhold = 10;
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        byte[] b = new byte[originalData.length];
        gzipInflatingBuffer.inflateBytes(b, 0, ((originalData.length) - bytesToWithhold));
        Assert.assertFalse("gzipInflatingBuffer is stalled", gzipInflatingBuffer.isStalled());
        gzipInflatingBuffer.inflateBytes(b, ((originalData.length) - bytesToWithhold), bytesToWithhold);
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
        Assert.assertTrue("gzipInflatingBuffer is not stalled", gzipInflatingBuffer.isStalled());
    }

    @Test
    public void isStalledReturnsFalseBetweenStreams() throws Exception {
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
        Assert.assertFalse("gzipInflatingBuffer is stalled", gzipInflatingBuffer.isStalled());
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
        Assert.assertTrue("gzipInflatingBuffer is not stalled", gzipInflatingBuffer.isStalled());
    }

    @Test
    public void isStalledReturnsFalseBetweenSmallStreams() throws Exception {
        // Use small streams to make sure that they all fit in the inflater buffer
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedTruncatedData));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedTruncatedData));
        byte[] b = new byte[truncatedData.length];
        Assert.assertEquals(truncatedData.length, gzipInflatingBuffer.inflateBytes(b, 0, truncatedData.length));
        Assert.assertTrue("inflated data does not match", Arrays.equals(truncatedData, b));
        Assert.assertFalse("gzipInflatingBuffer is stalled", gzipInflatingBuffer.isStalled());
        Assert.assertEquals(truncatedData.length, gzipInflatingBuffer.inflateBytes(b, 0, truncatedData.length));
        Assert.assertTrue("inflated data does not match", Arrays.equals(truncatedData, b));
        Assert.assertTrue("gzipInflatingBuffer is not stalled", gzipInflatingBuffer.isStalled());
    }

    @Test
    public void isStalledReturnsTrueWithPartialNextHeaderAvailable() throws Exception {
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedTruncatedData));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(new byte[1]));
        byte[] b = new byte[truncatedData.length];
        Assert.assertEquals(truncatedData.length, gzipInflatingBuffer.inflateBytes(b, 0, truncatedData.length));
        Assert.assertTrue("inflated data does not match", Arrays.equals(truncatedData, b));
        Assert.assertTrue("gzipInflatingBuffer is not stalled", gzipInflatingBuffer.isStalled());
        Assert.assertTrue("partial data expected", gzipInflatingBuffer.hasPartialData());
    }

    @Test
    public void isStalledWorksWithAllHeaderFlags() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((((((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FTEXT)) | (GzipInflatingBufferTest.FHCRC)) | (GzipInflatingBufferTest.FEXTRA)) | (GzipInflatingBufferTest.FNAME)) | (GzipInflatingBufferTest.FCOMMENT)));
        int len = 1025;
        byte[] fExtraLen = new byte[]{ ((byte) (len)), ((byte) (len >> 8)) };
        byte[] fExtra = new byte[len];
        byte[] zeroTerminatedBytes = new byte[len];
        for (int i = 0; i < (len - 1); i++) {
            zeroTerminatedBytes[i] = 1;
        }
        ByteArrayOutputStream newHeader = new ByteArrayOutputStream();
        newHeader.write(gzipHeader);
        newHeader.write(fExtraLen);
        newHeader.write(fExtra);
        newHeader.write(zeroTerminatedBytes);// FNAME

        newHeader.write(zeroTerminatedBytes);// FCOMMENT

        byte[] headerCrc16 = getHeaderCrc16Bytes(newHeader.toByteArray());
        Assert.assertTrue("gzipInflatingBuffer is not stalled", gzipInflatingBuffer.isStalled());
        addInTwoChunksAndVerifyIsStalled(gzipHeader);
        addInTwoChunksAndVerifyIsStalled(fExtraLen);
        addInTwoChunksAndVerifyIsStalled(fExtra);
        addInTwoChunksAndVerifyIsStalled(zeroTerminatedBytes);
        addInTwoChunksAndVerifyIsStalled(zeroTerminatedBytes);
        addInTwoChunksAndVerifyIsStalled(headerCrc16);
        byte[] b = new byte[originalData.length];
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        addInTwoChunksAndVerifyIsStalled(gzipTrailer);
    }

    @Test
    public void hasPartialData() throws Exception {
        Assert.assertFalse("no partial data expected", gzipInflatingBuffer.hasPartialData());
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(new byte[1]));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
        Assert.assertTrue("partial data expected", gzipInflatingBuffer.hasPartialData());
    }

    @Test
    public void hasPartialDataWithoutGzipTrailer() throws Exception {
        Assert.assertFalse("no partial data expected", gzipInflatingBuffer.hasPartialData());
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
        Assert.assertTrue("partial data expected", gzipInflatingBuffer.hasPartialData());
    }

    @Test
    public void inflatingCompleteGzipStreamConsumesTrailer() throws Exception {
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
        Assert.assertFalse("no partial data expected", gzipInflatingBuffer.hasPartialData());
    }

    @Test
    public void bytesConsumedForPartiallyInflatedBlock() throws Exception {
        int bytesToWithhold = 1;
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedTruncatedData));
        byte[] b = new byte[truncatedData.length];
        Assert.assertEquals(((truncatedData.length) - bytesToWithhold), gzipInflatingBuffer.inflateBytes(b, 0, ((truncatedData.length) - bytesToWithhold)));
        Assert.assertEquals((((gzippedTruncatedData.length) - bytesToWithhold) - (GzipInflatingBufferTest.GZIP_TRAILER_SIZE)), gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertEquals(bytesToWithhold, gzipInflatingBuffer.inflateBytes(b, ((truncatedData.length) - bytesToWithhold), bytesToWithhold));
        Assert.assertEquals((bytesToWithhold + (GzipInflatingBufferTest.GZIP_TRAILER_SIZE)), gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(truncatedData, b));
    }

    @Test
    public void getAndResetCompressedBytesConsumedReportsHeaderFlagBytes() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((((((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FTEXT)) | (GzipInflatingBufferTest.FHCRC)) | (GzipInflatingBufferTest.FEXTRA)) | (GzipInflatingBufferTest.FNAME)) | (GzipInflatingBufferTest.FCOMMENT)));
        int len = 1025;
        byte[] fExtraLen = new byte[]{ ((byte) (len)), ((byte) (len >> 8)) };
        byte[] fExtra = new byte[len];
        byte[] zeroTerminatedBytes = new byte[len];
        for (int i = 0; i < (len - 1); i++) {
            zeroTerminatedBytes[i] = 1;
        }
        ByteArrayOutputStream newHeader = new ByteArrayOutputStream();
        newHeader.write(gzipHeader);
        newHeader.write(fExtraLen);
        newHeader.write(fExtra);
        newHeader.write(zeroTerminatedBytes);// FNAME

        newHeader.write(zeroTerminatedBytes);// FCOMMENT

        byte[] headerCrc16 = getHeaderCrc16Bytes(newHeader.toByteArray());
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(0, gzipInflatingBuffer.inflateBytes(b, 0, 1));
        Assert.assertEquals(0, gzipInflatingBuffer.getAndResetBytesConsumed());
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        Assert.assertEquals(0, gzipInflatingBuffer.inflateBytes(b, 0, 1));
        Assert.assertEquals(gzipHeader.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(fExtraLen));
        Assert.assertEquals(0, gzipInflatingBuffer.inflateBytes(b, 0, 1));
        Assert.assertEquals(fExtraLen.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(fExtra));
        Assert.assertEquals(0, gzipInflatingBuffer.inflateBytes(b, 0, 1));
        Assert.assertEquals(fExtra.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(zeroTerminatedBytes));
        Assert.assertEquals(0, gzipInflatingBuffer.inflateBytes(b, 0, 1));
        Assert.assertEquals(zeroTerminatedBytes.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(zeroTerminatedBytes));
        Assert.assertEquals(0, gzipInflatingBuffer.inflateBytes(b, 0, 1));
        Assert.assertEquals(zeroTerminatedBytes.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(headerCrc16));
        Assert.assertEquals(0, gzipInflatingBuffer.inflateBytes(b, 0, 1));
        Assert.assertEquals(headerCrc16.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertEquals(deflatedBytes.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        Assert.assertEquals(0, gzipInflatingBuffer.inflateBytes(b, 0, 1));
        Assert.assertEquals(gzipTrailer.length, gzipInflatingBuffer.getAndResetBytesConsumed());
    }

    @Test
    public void getAndResetDeflatedBytesConsumedExcludesGzipMetadata() throws Exception {
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzippedData));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertEquals((((gzippedData.length) - (GzipInflatingBufferTest.GZIP_HEADER_MIN_SIZE)) - (GzipInflatingBufferTest.GZIP_TRAILER_SIZE)), gzipInflatingBuffer.getAndResetDeflatedBytesConsumed());
    }

    @Test
    public void wrongHeaderMagicShouldFail() throws Exception {
        gzipHeader[1] = ((byte) (~((GzipInflatingBufferTest.GZIP_MAGIC) >> 8)));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        try {
            byte[] b = new byte[1];
            gzipInflatingBuffer.inflateBytes(b, 0, 1);
            Assert.fail("Expected ZipException");
        } catch (ZipException expectedException) {
            Assert.assertEquals("Not in GZIP format", expectedException.getMessage());
        }
    }

    @Test
    public void wrongHeaderCompressionMethodShouldFail() throws Exception {
        gzipHeader[2] = 7;// Should be 8

        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        try {
            byte[] b = new byte[1];
            gzipInflatingBuffer.inflateBytes(b, 0, 1);
            Assert.fail("Expected ZipException");
        } catch (ZipException expectedException) {
            Assert.assertEquals("Unsupported compression method", expectedException.getMessage());
        }
    }

    @Test
    public void allHeaderFlagsWork() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((((((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FTEXT)) | (GzipInflatingBufferTest.FHCRC)) | (GzipInflatingBufferTest.FEXTRA)) | (GzipInflatingBufferTest.FNAME)) | (GzipInflatingBufferTest.FCOMMENT)));
        int len = 1025;
        byte[] fExtraLen = new byte[]{ ((byte) (len)), ((byte) (len >> 8)) };
        byte[] fExtra = new byte[len];
        byte[] zeroTerminatedBytes = new byte[len];
        for (int i = 0; i < (len - 1); i++) {
            zeroTerminatedBytes[i] = 1;
        }
        ByteArrayOutputStream newHeader = new ByteArrayOutputStream();
        newHeader.write(gzipHeader);
        newHeader.write(fExtraLen);
        newHeader.write(fExtra);
        newHeader.write(zeroTerminatedBytes);// FNAME

        newHeader.write(zeroTerminatedBytes);// FCOMMENT

        byte[] headerCrc16 = getHeaderCrc16Bytes(newHeader.toByteArray());
        newHeader.write(headerCrc16);
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(newHeader.toByteArray()));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        byte[] b = new byte[originalData.length];
        gzipInflatingBuffer.inflateBytes(b, 0, originalData.length);
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
    }

    @Test
    public void headerFTextFlagIsIgnored() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FTEXT)));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertEquals(gzippedData.length, gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
    }

    @Test
    public void headerFhcrcFlagWorks() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FHCRC)));
        byte[] headerCrc16 = getHeaderCrc16Bytes(gzipHeader);
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(headerCrc16));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertEquals(((gzippedData.length) + (headerCrc16.length)), gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
    }

    @Test
    public void headerInvalidFhcrcFlagFails() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FHCRC)));
        byte[] headerCrc16 = getHeaderCrc16Bytes(gzipHeader);
        headerCrc16[0] = ((byte) (~(headerCrc16[0])));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(headerCrc16));
        try {
            byte[] b = new byte[1];
            gzipInflatingBuffer.inflateBytes(b, 0, 1);
            Assert.fail("Expected ZipException");
        } catch (ZipException expectedException) {
            Assert.assertEquals("Corrupt GZIP header", expectedException.getMessage());
        }
    }

    @Test
    public void headerFExtraFlagWorks() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FEXTRA)));
        int len = 1025;
        byte[] fExtraLen = new byte[]{ ((byte) (len)), ((byte) (len >> 8)) };
        byte[] fExtra = new byte[len];
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(fExtraLen));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(fExtra));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertEquals((((gzippedData.length) + (fExtraLen.length)) + (fExtra.length)), gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
    }

    @Test
    public void headerFExtraFlagWithZeroLenWorks() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FEXTRA)));
        byte[] fExtraLen = new byte[2];
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(fExtraLen));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertEquals(((gzippedData.length) + (fExtraLen.length)), gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
    }

    @Test
    public void headerFExtraFlagWithMissingExtraLenFails() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FEXTRA)));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        try {
            byte[] b = new byte[originalData.length];
            gzipInflatingBuffer.inflateBytes(b, 0, originalData.length);
            Assert.fail("Expected DataFormatException");
        } catch (DataFormatException expectedException) {
            Assert.assertTrue("wrong exception message", expectedException.getMessage().startsWith("Inflater data format exception:"));
        }
    }

    @Test
    public void headerFExtraFlagWithMissingExtraBytesFails() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FEXTRA)));
        int len = 5;
        byte[] fExtraLen = new byte[]{ ((byte) (len)), ((byte) (len >> 8)) };
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(fExtraLen));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        try {
            byte[] b = new byte[originalData.length];
            gzipInflatingBuffer.inflateBytes(b, 0, originalData.length);
            Assert.fail("Expected DataFormatException");
        } catch (DataFormatException expectedException) {
            Assert.assertTrue("wrong exception message", expectedException.getMessage().startsWith("Inflater data format exception:"));
        }
    }

    @Test
    public void headerFNameFlagWorks() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FNAME)));
        int len = 1025;
        byte[] zeroTerminatedBytes = new byte[len];
        for (int i = 0; i < (len - 1); i++) {
            zeroTerminatedBytes[i] = 1;
        }
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(zeroTerminatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertEquals(((gzippedData.length) + len), gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
    }

    @Test
    public void headerFNameFlagWithMissingBytesFail() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FNAME)));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        try {
            byte[] b = new byte[originalData.length];
            gzipInflatingBuffer.inflateBytes(b, 0, originalData.length);
            Assert.fail("Expected DataFormatException");
        } catch (DataFormatException expectedException) {
            Assert.assertTrue("wrong exception message", expectedException.getMessage().startsWith("Inflater data format exception:"));
        }
    }

    @Test
    public void headerFCommentFlagWorks() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FCOMMENT)));
        int len = 1025;
        byte[] zeroTerminatedBytes = new byte[len];
        for (int i = 0; i < (len - 1); i++) {
            zeroTerminatedBytes[i] = 1;
        }
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(zeroTerminatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        byte[] b = new byte[originalData.length];
        Assert.assertEquals(originalData.length, gzipInflatingBuffer.inflateBytes(b, 0, originalData.length));
        Assert.assertEquals(((gzippedData.length) + len), gzipInflatingBuffer.getAndResetBytesConsumed());
        Assert.assertTrue("inflated data does not match", Arrays.equals(originalData, b));
    }

    @Test
    public void headerFCommentFlagWithMissingBytesFail() throws Exception {
        gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX] = ((byte) ((gzipHeader[GzipInflatingBufferTest.GZIP_HEADER_FLAG_INDEX]) | (GzipInflatingBufferTest.FCOMMENT)));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        try {
            byte[] b = new byte[originalData.length];
            gzipInflatingBuffer.inflateBytes(b, 0, originalData.length);
            Assert.fail("Expected DataFormatException");
        } catch (DataFormatException expectedException) {
            Assert.assertTrue("wrong exception message", expectedException.getMessage().startsWith("Inflater data format exception:"));
        }
    }

    @Test
    public void wrongTrailerCrcShouldFail() throws Exception {
        gzipTrailer[0] = ((byte) (~(gzipTrailer[0])));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        try {
            byte[] b = new byte[originalData.length];
            gzipInflatingBuffer.inflateBytes(b, 0, originalData.length);
            Assert.fail("Expected ZipException");
        } catch (ZipException expectedException) {
            Assert.assertEquals("Corrupt GZIP trailer", expectedException.getMessage());
        }
    }

    @Test
    public void wrongTrailerISizeShouldFail() throws Exception {
        gzipTrailer[((GzipInflatingBufferTest.GZIP_TRAILER_SIZE) - 1)] = ((byte) (~(gzipTrailer[((GzipInflatingBufferTest.GZIP_TRAILER_SIZE) - 1)])));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(deflatedBytes));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipTrailer));
        try {
            byte[] b = new byte[originalData.length];
            gzipInflatingBuffer.inflateBytes(b, 0, originalData.length);
            Assert.fail("Expected ZipException");
        } catch (ZipException expectedException) {
            Assert.assertEquals("Corrupt GZIP trailer", expectedException.getMessage());
        }
    }

    @Test
    public void invalidDeflateBlockShouldFail() throws Exception {
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(gzipHeader));
        gzipInflatingBuffer.addGzippedBytes(ReadableBuffers.wrap(new byte[10]));
        try {
            byte[] b = new byte[originalData.length];
            gzipInflatingBuffer.inflateBytes(b, 0, originalData.length);
            Assert.fail("Expected DataFormatException");
        } catch (DataFormatException expectedException) {
            Assert.assertTrue("wrong exception message", expectedException.getMessage().startsWith("Inflater data format exception:"));
        }
    }
}

