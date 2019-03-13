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
package org.apache.hadoop.fs.azure;


import java.io.IOException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.azure.integration.AbstractAzureScaleTest;
import org.apache.hadoop.fs.azure.integration.AzureTestUtils;
import org.apache.hadoop.fs.contract.ContractTestUtils.NanoTimer;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test semantics and performance of the original block blob input stream
 * (KEY_INPUT_STREAM_VERSION=1) and the new
 * <code>BlockBlobInputStream</code> (KEY_INPUT_STREAM_VERSION=2).
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ITestBlockBlobInputStream extends AbstractAzureScaleTest {
    private static final Logger LOG = LoggerFactory.getLogger(ITestBlockBlobInputStream.class);

    private static final int KILOBYTE = 1024;

    private static final int MEGABYTE = (ITestBlockBlobInputStream.KILOBYTE) * (ITestBlockBlobInputStream.KILOBYTE);

    private static final int TEST_FILE_SIZE = 6 * (ITestBlockBlobInputStream.MEGABYTE);

    private static final Path TEST_FILE_PATH = new Path("TestBlockBlobInputStream.txt");

    private AzureBlobStorageTestAccount accountUsingInputStreamV1;

    private AzureBlobStorageTestAccount accountUsingInputStreamV2;

    private long testFileLength;

    private FileStatus testFileStatus;

    private Path hugefile;

    @Test
    public void test_0100_CreateHugeFile() throws IOException {
        createTestFileAndSetLength();
    }

    @Test
    public void test_0200_BasicReadTest() throws Exception {
        assumeHugeFileExists();
        try (FSDataInputStream inputStreamV1 = accountUsingInputStreamV1.getFileSystem().open(ITestBlockBlobInputStream.TEST_FILE_PATH);FSDataInputStream inputStreamV2 = accountUsingInputStreamV2.getFileSystem().open(ITestBlockBlobInputStream.TEST_FILE_PATH)) {
            byte[] bufferV1 = new byte[3 * (ITestBlockBlobInputStream.MEGABYTE)];
            byte[] bufferV2 = new byte[bufferV1.length];
            // v1 forward seek and read a kilobyte into first kilobyte of bufferV1
            inputStreamV1.seek((5 * (ITestBlockBlobInputStream.MEGABYTE)));
            int numBytesReadV1 = inputStreamV1.read(bufferV1, 0, ITestBlockBlobInputStream.KILOBYTE);
            Assert.assertEquals(ITestBlockBlobInputStream.KILOBYTE, numBytesReadV1);
            // v2 forward seek and read a kilobyte into first kilobyte of bufferV2
            inputStreamV2.seek((5 * (ITestBlockBlobInputStream.MEGABYTE)));
            int numBytesReadV2 = inputStreamV2.read(bufferV2, 0, ITestBlockBlobInputStream.KILOBYTE);
            Assert.assertEquals(ITestBlockBlobInputStream.KILOBYTE, numBytesReadV2);
            Assert.assertArrayEquals(bufferV1, bufferV2);
            int len = ITestBlockBlobInputStream.MEGABYTE;
            int offset = (bufferV1.length) - len;
            // v1 reverse seek and read a megabyte into last megabyte of bufferV1
            inputStreamV1.seek((3 * (ITestBlockBlobInputStream.MEGABYTE)));
            numBytesReadV1 = inputStreamV1.read(bufferV1, offset, len);
            Assert.assertEquals(len, numBytesReadV1);
            // v2 reverse seek and read a megabyte into last megabyte of bufferV2
            inputStreamV2.seek((3 * (ITestBlockBlobInputStream.MEGABYTE)));
            numBytesReadV2 = inputStreamV2.read(bufferV2, offset, len);
            Assert.assertEquals(len, numBytesReadV2);
            Assert.assertArrayEquals(bufferV1, bufferV2);
        }
    }

    @Test
    public void test_0201_RandomReadTest() throws Exception {
        assumeHugeFileExists();
        try (FSDataInputStream inputStreamV1 = accountUsingInputStreamV1.getFileSystem().open(ITestBlockBlobInputStream.TEST_FILE_PATH);FSDataInputStream inputStreamV2 = accountUsingInputStreamV2.getFileSystem().open(ITestBlockBlobInputStream.TEST_FILE_PATH)) {
            final int bufferSize = 4 * (ITestBlockBlobInputStream.KILOBYTE);
            byte[] bufferV1 = new byte[bufferSize];
            byte[] bufferV2 = new byte[bufferV1.length];
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            inputStreamV1.seek(0);
            inputStreamV2.seek(0);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            int seekPosition = 2 * (ITestBlockBlobInputStream.KILOBYTE);
            inputStreamV1.seek(seekPosition);
            inputStreamV2.seek(seekPosition);
            inputStreamV1.seek(0);
            inputStreamV2.seek(0);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            seekPosition = 5 * (ITestBlockBlobInputStream.KILOBYTE);
            inputStreamV1.seek(seekPosition);
            inputStreamV2.seek(seekPosition);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            seekPosition = 10 * (ITestBlockBlobInputStream.KILOBYTE);
            inputStreamV1.seek(seekPosition);
            inputStreamV2.seek(seekPosition);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            seekPosition = 4100 * (ITestBlockBlobInputStream.KILOBYTE);
            inputStreamV1.seek(seekPosition);
            inputStreamV2.seek(seekPosition);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
        }
    }

    /**
     * Validates the implementation of InputStream.markSupported.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void test_0301_MarkSupportedV1() throws IOException {
        validateMarkSupported(accountUsingInputStreamV1.getFileSystem());
    }

    /**
     * Validates the implementation of InputStream.markSupported.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void test_0302_MarkSupportedV2() throws IOException {
        validateMarkSupported(accountUsingInputStreamV1.getFileSystem());
    }

    /**
     * Validates the implementation of InputStream.mark and reset
     * for version 1 of the block blob input stream.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test_0303_MarkAndResetV1() throws Exception {
        validateMarkAndReset(accountUsingInputStreamV1.getFileSystem());
    }

    /**
     * Validates the implementation of InputStream.mark and reset
     * for version 2 of the block blob input stream.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test_0304_MarkAndResetV2() throws Exception {
        validateMarkAndReset(accountUsingInputStreamV2.getFileSystem());
    }

    /**
     * Validates the implementation of Seekable.seekToNewSource, which should
     * return false for version 1 of the block blob input stream.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void test_0305_SeekToNewSourceV1() throws IOException {
        validateSeekToNewSource(accountUsingInputStreamV1.getFileSystem());
    }

    /**
     * Validates the implementation of Seekable.seekToNewSource, which should
     * return false for version 2 of the block blob input stream.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void test_0306_SeekToNewSourceV2() throws IOException {
        validateSeekToNewSource(accountUsingInputStreamV2.getFileSystem());
    }

    /**
     * Validates the implementation of InputStream.skip and ensures there is no
     * network I/O for version 1 of the block blob input stream.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test_0307_SkipBoundsV1() throws Exception {
        validateSkipBounds(accountUsingInputStreamV1.getFileSystem());
    }

    /**
     * Validates the implementation of InputStream.skip and ensures there is no
     * network I/O for version 2 of the block blob input stream.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test_0308_SkipBoundsV2() throws Exception {
        validateSkipBounds(accountUsingInputStreamV2.getFileSystem());
    }

    /**
     * Validates the implementation of Seekable.seek and ensures there is no
     * network I/O for forward seek.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test_0309_SeekBoundsV1() throws Exception {
        validateSeekBounds(accountUsingInputStreamV1.getFileSystem());
    }

    /**
     * Validates the implementation of Seekable.seek and ensures there is no
     * network I/O for forward seek.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test_0310_SeekBoundsV2() throws Exception {
        validateSeekBounds(accountUsingInputStreamV2.getFileSystem());
    }

    /**
     * Validates the implementation of Seekable.seek, Seekable.getPos,
     * and InputStream.available.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test_0311_SeekAndAvailableAndPositionV1() throws Exception {
        validateSeekAndAvailableAndPosition(accountUsingInputStreamV1.getFileSystem());
    }

    /**
     * Validates the implementation of Seekable.seek, Seekable.getPos,
     * and InputStream.available.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void test_0312_SeekAndAvailableAndPositionV2() throws Exception {
        validateSeekAndAvailableAndPosition(accountUsingInputStreamV2.getFileSystem());
    }

    /**
     * Validates the implementation of InputStream.skip, Seekable.getPos,
     * and InputStream.available.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void test_0313_SkipAndAvailableAndPositionV1() throws IOException {
        validateSkipAndAvailableAndPosition(accountUsingInputStreamV1.getFileSystem());
    }

    /**
     * Validates the implementation of InputStream.skip, Seekable.getPos,
     * and InputStream.available.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void test_0314_SkipAndAvailableAndPositionV2() throws IOException {
        validateSkipAndAvailableAndPosition(accountUsingInputStreamV1.getFileSystem());
    }

    /**
     * Ensures parity in the performance of sequential read for
     * version 1 and version 2 of the block blob input stream.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void test_0315_SequentialReadPerformance() throws IOException {
        assumeHugeFileExists();
        final int maxAttempts = 10;
        final double maxAcceptableRatio = 1.01;
        double v1ElapsedMs = 0;
        double v2ElapsedMs = 0;
        double ratio = Double.MAX_VALUE;
        for (int i = 0; (i < maxAttempts) && (ratio >= maxAcceptableRatio); i++) {
            v1ElapsedMs = sequentialRead(1, accountUsingInputStreamV1.getFileSystem(), false);
            v2ElapsedMs = sequentialRead(2, accountUsingInputStreamV2.getFileSystem(), false);
            ratio = v2ElapsedMs / v1ElapsedMs;
            ITestBlockBlobInputStream.LOG.info(String.format("v1ElapsedMs=%1$d, v2ElapsedMs=%2$d, ratio=%3$.2f", ((long) (v1ElapsedMs)), ((long) (v2ElapsedMs)), ratio));
        }
        Assert.assertTrue(String.format(("Performance of version 2 is not acceptable: v1ElapsedMs=%1$d," + " v2ElapsedMs=%2$d, ratio=%3$.2f"), ((long) (v1ElapsedMs)), ((long) (v2ElapsedMs)), ratio), (ratio < maxAcceptableRatio));
    }

    /**
     * Ensures parity in the performance of sequential read after reverse seek for
     * version 2 of the block blob input stream.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void test_0316_SequentialReadAfterReverseSeekPerformanceV2() throws IOException {
        assumeHugeFileExists();
        final int maxAttempts = 10;
        final double maxAcceptableRatio = 1.01;
        double beforeSeekElapsedMs = 0;
        double afterSeekElapsedMs = 0;
        double ratio = Double.MAX_VALUE;
        for (int i = 0; (i < maxAttempts) && (ratio >= maxAcceptableRatio); i++) {
            beforeSeekElapsedMs = sequentialRead(2, accountUsingInputStreamV2.getFileSystem(), false);
            afterSeekElapsedMs = sequentialRead(2, accountUsingInputStreamV2.getFileSystem(), true);
            ratio = afterSeekElapsedMs / beforeSeekElapsedMs;
            ITestBlockBlobInputStream.LOG.info(String.format("beforeSeekElapsedMs=%1$d, afterSeekElapsedMs=%2$d, ratio=%3$.2f", ((long) (beforeSeekElapsedMs)), ((long) (afterSeekElapsedMs)), ratio));
        }
        Assert.assertTrue(String.format(("Performance of version 2 after reverse seek is not acceptable:" + (" beforeSeekElapsedMs=%1$d, afterSeekElapsedMs=%2$d," + " ratio=%3$.2f")), ((long) (beforeSeekElapsedMs)), ((long) (afterSeekElapsedMs)), ratio), (ratio < maxAcceptableRatio));
    }

    @Test
    public void test_0317_RandomReadPerformance() throws IOException {
        assumeHugeFileExists();
        final int maxAttempts = 10;
        final double maxAcceptableRatio = 0.1;
        double v1ElapsedMs = 0;
        double v2ElapsedMs = 0;
        double ratio = Double.MAX_VALUE;
        for (int i = 0; (i < maxAttempts) && (ratio >= maxAcceptableRatio); i++) {
            v1ElapsedMs = randomRead(1, accountUsingInputStreamV1.getFileSystem());
            v2ElapsedMs = randomRead(2, accountUsingInputStreamV2.getFileSystem());
            ratio = v2ElapsedMs / v1ElapsedMs;
            ITestBlockBlobInputStream.LOG.info(String.format("v1ElapsedMs=%1$d, v2ElapsedMs=%2$d, ratio=%3$.2f", ((long) (v1ElapsedMs)), ((long) (v2ElapsedMs)), ratio));
        }
        Assert.assertTrue(String.format(("Performance of version 2 is not acceptable: v1ElapsedMs=%1$d," + " v2ElapsedMs=%2$d, ratio=%3$.2f"), ((long) (v1ElapsedMs)), ((long) (v2ElapsedMs)), ratio), (ratio < maxAcceptableRatio));
    }

    @Test
    public void test_999_DeleteHugeFiles() throws IOException {
        try {
            NanoTimer timer = new NanoTimer();
            NativeAzureFileSystem fs = getFileSystem();
            fs.delete(ITestBlockBlobInputStream.TEST_FILE_PATH, false);
            timer.end("time to delete %s", ITestBlockBlobInputStream.TEST_FILE_PATH);
        } finally {
            // clean up the test account
            AzureTestUtils.cleanupTestAccount(accountUsingInputStreamV1);
        }
    }
}

