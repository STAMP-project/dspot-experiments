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
package org.apache.hadoop.fs.azurebfs;


import ContractTestUtils.NanoTimer;
import java.io.EOFException;
import java.util.concurrent.Callable;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.azure.NativeAzureFileSystem;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test random read operation.
 */
public class ITestAzureBlobFileSystemRandomRead extends AbstractAbfsScaleTest {
    private static final int KILOBYTE = 1024;

    private static final int MEGABYTE = (ITestAzureBlobFileSystemRandomRead.KILOBYTE) * (ITestAzureBlobFileSystemRandomRead.KILOBYTE);

    private static final long TEST_FILE_SIZE = 8 * (ITestAzureBlobFileSystemRandomRead.MEGABYTE);

    private static final int MAX_ELAPSEDTIMEMS = 20;

    private static final int SEQUENTIAL_READ_BUFFER_SIZE = 16 * (ITestAzureBlobFileSystemRandomRead.KILOBYTE);

    private static final int CREATE_BUFFER_SIZE = 26 * (ITestAzureBlobFileSystemRandomRead.KILOBYTE);

    private static final int SEEK_POSITION_ONE = 2 * (ITestAzureBlobFileSystemRandomRead.KILOBYTE);

    private static final int SEEK_POSITION_TWO = 5 * (ITestAzureBlobFileSystemRandomRead.KILOBYTE);

    private static final int SEEK_POSITION_THREE = 10 * (ITestAzureBlobFileSystemRandomRead.KILOBYTE);

    private static final int SEEK_POSITION_FOUR = 4100 * (ITestAzureBlobFileSystemRandomRead.KILOBYTE);

    private static final Path TEST_FILE_PATH = new Path("/TestRandomRead.txt");

    private static final String WASB = "WASB";

    private static final String ABFS = "ABFS";

    private static long testFileLength = 0;

    private static final Logger LOG = LoggerFactory.getLogger(ITestAzureBlobFileSystemRandomRead.class);

    public ITestAzureBlobFileSystemRandomRead() throws Exception {
        super();
    }

    @Test
    public void testBasicRead() throws Exception {
        assumeHugeFileExists();
        try (FSDataInputStream inputStream = this.getFileSystem().open(ITestAzureBlobFileSystemRandomRead.TEST_FILE_PATH)) {
            byte[] buffer = new byte[3 * (ITestAzureBlobFileSystemRandomRead.MEGABYTE)];
            // forward seek and read a kilobyte into first kilobyte of bufferV2
            inputStream.seek((5 * (ITestAzureBlobFileSystemRandomRead.MEGABYTE)));
            int numBytesRead = inputStream.read(buffer, 0, ITestAzureBlobFileSystemRandomRead.KILOBYTE);
            Assert.assertEquals("Wrong number of bytes read", ITestAzureBlobFileSystemRandomRead.KILOBYTE, numBytesRead);
            int len = ITestAzureBlobFileSystemRandomRead.MEGABYTE;
            int offset = (buffer.length) - len;
            // reverse seek and read a megabyte into last megabyte of bufferV1
            inputStream.seek((3 * (ITestAzureBlobFileSystemRandomRead.MEGABYTE)));
            numBytesRead = inputStream.read(buffer, offset, len);
            Assert.assertEquals("Wrong number of bytes read after seek", len, numBytesRead);
        }
    }

    /**
     * Validates the implementation of random read in ABFS
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testRandomRead() throws Exception {
        Assume.assumeFalse("This test does not support namespace enabled account", this.getFileSystem().getIsNamespaceEnabled());
        assumeHugeFileExists();
        try (FSDataInputStream inputStreamV1 = this.getFileSystem().open(ITestAzureBlobFileSystemRandomRead.TEST_FILE_PATH);FSDataInputStream inputStreamV2 = this.getWasbFileSystem().open(ITestAzureBlobFileSystemRandomRead.TEST_FILE_PATH)) {
            final int bufferSize = 4 * (ITestAzureBlobFileSystemRandomRead.KILOBYTE);
            byte[] bufferV1 = new byte[bufferSize];
            byte[] bufferV2 = new byte[bufferV1.length];
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            inputStreamV1.seek(0);
            inputStreamV2.seek(0);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            inputStreamV1.seek(ITestAzureBlobFileSystemRandomRead.SEEK_POSITION_ONE);
            inputStreamV2.seek(ITestAzureBlobFileSystemRandomRead.SEEK_POSITION_ONE);
            inputStreamV1.seek(0);
            inputStreamV2.seek(0);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            inputStreamV1.seek(ITestAzureBlobFileSystemRandomRead.SEEK_POSITION_TWO);
            inputStreamV2.seek(ITestAzureBlobFileSystemRandomRead.SEEK_POSITION_TWO);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            inputStreamV1.seek(ITestAzureBlobFileSystemRandomRead.SEEK_POSITION_THREE);
            inputStreamV2.seek(ITestAzureBlobFileSystemRandomRead.SEEK_POSITION_THREE);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
            inputStreamV1.seek(ITestAzureBlobFileSystemRandomRead.SEEK_POSITION_FOUR);
            inputStreamV2.seek(ITestAzureBlobFileSystemRandomRead.SEEK_POSITION_FOUR);
            verifyConsistentReads(inputStreamV1, inputStreamV2, bufferV1, bufferV2);
        }
    }

    /**
     * Validates the implementation of Seekable.seekToNewSource
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSeekToNewSource() throws Exception {
        assumeHugeFileExists();
        try (FSDataInputStream inputStream = this.getFileSystem().open(ITestAzureBlobFileSystemRandomRead.TEST_FILE_PATH)) {
            Assert.assertFalse(inputStream.seekToNewSource(0));
        }
    }

    /**
     * Validates the implementation of InputStream.skip and ensures there is no
     * network I/O for AbfsInputStream
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSkipBounds() throws Exception {
        assumeHugeFileExists();
        try (FSDataInputStream inputStream = this.getFileSystem().open(ITestAzureBlobFileSystemRandomRead.TEST_FILE_PATH)) {
            ContractTestUtils.NanoTimer timer = new ContractTestUtils.NanoTimer();
            long skipped = inputStream.skip((-1));
            Assert.assertEquals(0, skipped);
            skipped = inputStream.skip(0);
            Assert.assertEquals(0, skipped);
            Assert.assertTrue(((ITestAzureBlobFileSystemRandomRead.testFileLength) > 0));
            skipped = inputStream.skip(ITestAzureBlobFileSystemRandomRead.testFileLength);
            Assert.assertEquals(ITestAzureBlobFileSystemRandomRead.testFileLength, skipped);
            intercept(EOFException.class, new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return inputStream.skip(1);
                }
            });
            long elapsedTimeMs = timer.elapsedTimeMs();
            Assert.assertTrue(String.format("There should not be any network I/O (elapsedTimeMs=%1$d).", elapsedTimeMs), (elapsedTimeMs < (ITestAzureBlobFileSystemRandomRead.MAX_ELAPSEDTIMEMS)));
        }
    }

    /**
     * Validates the implementation of Seekable.seek and ensures there is no
     * network I/O for forward seek.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testValidateSeekBounds() throws Exception {
        assumeHugeFileExists();
        try (FSDataInputStream inputStream = this.getFileSystem().open(ITestAzureBlobFileSystemRandomRead.TEST_FILE_PATH)) {
            ContractTestUtils.NanoTimer timer = new ContractTestUtils.NanoTimer();
            inputStream.seek(0);
            Assert.assertEquals(0, inputStream.getPos());
            intercept(EOFException.class, FSExceptionMessages.NEGATIVE_SEEK, new Callable<FSDataInputStream>() {
                @Override
                public FSDataInputStream call() throws Exception {
                    inputStream.seek((-1));
                    return inputStream;
                }
            });
            Assert.assertTrue(("Test file length only " + (ITestAzureBlobFileSystemRandomRead.testFileLength)), ((ITestAzureBlobFileSystemRandomRead.testFileLength) > 0));
            inputStream.seek(ITestAzureBlobFileSystemRandomRead.testFileLength);
            Assert.assertEquals(ITestAzureBlobFileSystemRandomRead.testFileLength, inputStream.getPos());
            intercept(EOFException.class, FSExceptionMessages.CANNOT_SEEK_PAST_EOF, new Callable<FSDataInputStream>() {
                @Override
                public FSDataInputStream call() throws Exception {
                    inputStream.seek(((ITestAzureBlobFileSystemRandomRead.testFileLength) + 1));
                    return inputStream;
                }
            });
            long elapsedTimeMs = timer.elapsedTimeMs();
            Assert.assertTrue(String.format("There should not be any network I/O (elapsedTimeMs=%1$d).", elapsedTimeMs), (elapsedTimeMs < (ITestAzureBlobFileSystemRandomRead.MAX_ELAPSEDTIMEMS)));
        }
    }

    /**
     * Validates the implementation of Seekable.seek, Seekable.getPos,
     * and InputStream.available.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSeekAndAvailableAndPosition() throws Exception {
        assumeHugeFileExists();
        try (FSDataInputStream inputStream = this.getFileSystem().open(ITestAzureBlobFileSystemRandomRead.TEST_FILE_PATH)) {
            byte[] expected1 = new byte[]{ ((byte) ('a')), ((byte) ('b')), ((byte) ('c')) };
            byte[] expected2 = new byte[]{ ((byte) ('d')), ((byte) ('e')), ((byte) ('f')) };
            byte[] expected3 = new byte[]{ ((byte) ('b')), ((byte) ('c')), ((byte) ('d')) };
            byte[] expected4 = new byte[]{ ((byte) ('g')), ((byte) ('h')), ((byte) ('i')) };
            byte[] buffer = new byte[3];
            int bytesRead = inputStream.read(buffer);
            Assert.assertEquals(buffer.length, bytesRead);
            Assert.assertArrayEquals(expected1, buffer);
            Assert.assertEquals(buffer.length, inputStream.getPos());
            Assert.assertEquals(((ITestAzureBlobFileSystemRandomRead.testFileLength) - (inputStream.getPos())), inputStream.available());
            bytesRead = inputStream.read(buffer);
            Assert.assertEquals(buffer.length, bytesRead);
            Assert.assertArrayEquals(expected2, buffer);
            Assert.assertEquals((2 * (buffer.length)), inputStream.getPos());
            Assert.assertEquals(((ITestAzureBlobFileSystemRandomRead.testFileLength) - (inputStream.getPos())), inputStream.available());
            // reverse seek
            int seekPos = 0;
            inputStream.seek(seekPos);
            bytesRead = inputStream.read(buffer);
            Assert.assertEquals(buffer.length, bytesRead);
            Assert.assertArrayEquals(expected1, buffer);
            Assert.assertEquals(((buffer.length) + seekPos), inputStream.getPos());
            Assert.assertEquals(((ITestAzureBlobFileSystemRandomRead.testFileLength) - (inputStream.getPos())), inputStream.available());
            // reverse seek
            seekPos = 1;
            inputStream.seek(seekPos);
            bytesRead = inputStream.read(buffer);
            Assert.assertEquals(buffer.length, bytesRead);
            Assert.assertArrayEquals(expected3, buffer);
            Assert.assertEquals(((buffer.length) + seekPos), inputStream.getPos());
            Assert.assertEquals(((ITestAzureBlobFileSystemRandomRead.testFileLength) - (inputStream.getPos())), inputStream.available());
            // forward seek
            seekPos = 6;
            inputStream.seek(seekPos);
            bytesRead = inputStream.read(buffer);
            Assert.assertEquals(buffer.length, bytesRead);
            Assert.assertArrayEquals(expected4, buffer);
            Assert.assertEquals(((buffer.length) + seekPos), inputStream.getPos());
            Assert.assertEquals(((ITestAzureBlobFileSystemRandomRead.testFileLength) - (inputStream.getPos())), inputStream.available());
        }
    }

    /**
     * Validates the implementation of InputStream.skip, Seekable.getPos,
     * and InputStream.available.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSkipAndAvailableAndPosition() throws Exception {
        assumeHugeFileExists();
        try (FSDataInputStream inputStream = this.getFileSystem().open(ITestAzureBlobFileSystemRandomRead.TEST_FILE_PATH)) {
            byte[] expected1 = new byte[]{ ((byte) ('a')), ((byte) ('b')), ((byte) ('c')) };
            byte[] expected2 = new byte[]{ ((byte) ('d')), ((byte) ('e')), ((byte) ('f')) };
            byte[] expected3 = new byte[]{ ((byte) ('b')), ((byte) ('c')), ((byte) ('d')) };
            byte[] expected4 = new byte[]{ ((byte) ('g')), ((byte) ('h')), ((byte) ('i')) };
            Assert.assertEquals(ITestAzureBlobFileSystemRandomRead.testFileLength, inputStream.available());
            Assert.assertEquals(0, inputStream.getPos());
            int n = 3;
            long skipped = inputStream.skip(n);
            Assert.assertEquals(skipped, inputStream.getPos());
            Assert.assertEquals(((ITestAzureBlobFileSystemRandomRead.testFileLength) - (inputStream.getPos())), inputStream.available());
            Assert.assertEquals(skipped, n);
            byte[] buffer = new byte[3];
            int bytesRead = inputStream.read(buffer);
            Assert.assertEquals(buffer.length, bytesRead);
            Assert.assertArrayEquals(expected2, buffer);
            Assert.assertEquals(((buffer.length) + skipped), inputStream.getPos());
            Assert.assertEquals(((ITestAzureBlobFileSystemRandomRead.testFileLength) - (inputStream.getPos())), inputStream.available());
            // does skip still work after seek?
            int seekPos = 1;
            inputStream.seek(seekPos);
            bytesRead = inputStream.read(buffer);
            Assert.assertEquals(buffer.length, bytesRead);
            Assert.assertArrayEquals(expected3, buffer);
            Assert.assertEquals(((buffer.length) + seekPos), inputStream.getPos());
            Assert.assertEquals(((ITestAzureBlobFileSystemRandomRead.testFileLength) - (inputStream.getPos())), inputStream.available());
            long currentPosition = inputStream.getPos();
            n = 2;
            skipped = inputStream.skip(n);
            Assert.assertEquals((currentPosition + skipped), inputStream.getPos());
            Assert.assertEquals(((ITestAzureBlobFileSystemRandomRead.testFileLength) - (inputStream.getPos())), inputStream.available());
            Assert.assertEquals(skipped, n);
            bytesRead = inputStream.read(buffer);
            Assert.assertEquals(buffer.length, bytesRead);
            Assert.assertArrayEquals(expected4, buffer);
            Assert.assertEquals((((buffer.length) + skipped) + currentPosition), inputStream.getPos());
            Assert.assertEquals(((ITestAzureBlobFileSystemRandomRead.testFileLength) - (inputStream.getPos())), inputStream.available());
        }
    }

    /**
     * Ensures parity in the performance of sequential read after reverse seek for
     * abfs of the AbfsInputStream.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSequentialReadAfterReverseSeekPerformance() throws Exception {
        assumeHugeFileExists();
        final int maxAttempts = 10;
        final double maxAcceptableRatio = 1.01;
        double beforeSeekElapsedMs = 0;
        double afterSeekElapsedMs = 0;
        double ratio = Double.MAX_VALUE;
        for (int i = 0; (i < maxAttempts) && (ratio >= maxAcceptableRatio); i++) {
            beforeSeekElapsedMs = sequentialRead(ITestAzureBlobFileSystemRandomRead.ABFS, this.getFileSystem(), false);
            afterSeekElapsedMs = sequentialRead(ITestAzureBlobFileSystemRandomRead.ABFS, this.getFileSystem(), true);
            ratio = afterSeekElapsedMs / beforeSeekElapsedMs;
            ITestAzureBlobFileSystemRandomRead.LOG.info(String.format("beforeSeekElapsedMs=%1$d, afterSeekElapsedMs=%2$d, ratio=%3$.2f", ((long) (beforeSeekElapsedMs)), ((long) (afterSeekElapsedMs)), ratio));
        }
        Assert.assertTrue(String.format(("Performance of ABFS stream after reverse seek is not acceptable:" + (" beforeSeekElapsedMs=%1$d, afterSeekElapsedMs=%2$d," + " ratio=%3$.2f")), ((long) (beforeSeekElapsedMs)), ((long) (afterSeekElapsedMs)), ratio), (ratio < maxAcceptableRatio));
    }

    @Test
    public void testRandomReadPerformance() throws Exception {
        Assume.assumeFalse("This test does not support namespace enabled account", this.getFileSystem().getIsNamespaceEnabled());
        createTestFile();
        assumeHugeFileExists();
        final AzureBlobFileSystem abFs = this.getFileSystem();
        final NativeAzureFileSystem wasbFs = this.getWasbFileSystem();
        final int maxAttempts = 10;
        final double maxAcceptableRatio = 1.025;
        double v1ElapsedMs = 0;
        double v2ElapsedMs = 0;
        double ratio = Double.MAX_VALUE;
        for (int i = 0; (i < maxAttempts) && (ratio >= maxAcceptableRatio); i++) {
            v1ElapsedMs = randomRead(1, wasbFs);
            v2ElapsedMs = randomRead(2, abFs);
            ratio = v2ElapsedMs / v1ElapsedMs;
            ITestAzureBlobFileSystemRandomRead.LOG.info(String.format("v1ElapsedMs=%1$d, v2ElapsedMs=%2$d, ratio=%3$.2f", ((long) (v1ElapsedMs)), ((long) (v2ElapsedMs)), ratio));
        }
        Assert.assertTrue(String.format(("Performance of version 2 is not acceptable: v1ElapsedMs=%1$d," + " v2ElapsedMs=%2$d, ratio=%3$.2f"), ((long) (v1ElapsedMs)), ((long) (v2ElapsedMs)), ratio), (ratio < maxAcceptableRatio));
    }
}

