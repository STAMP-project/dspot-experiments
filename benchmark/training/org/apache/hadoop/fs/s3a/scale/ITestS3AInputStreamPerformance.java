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
package org.apache.hadoop.fs.s3a.scale;


import S3AInputPolicy.Normal;
import S3AInputPolicy.Random;
import S3AInputPolicy.Sequential;
import S3AInstrumentation.InputStreamStatistics;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.S3AInputStream;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Look at the performance of S3a operations.
 */
public class ITestS3AInputStreamPerformance extends S3AScaleTestBase {
    private static final Logger LOG = LoggerFactory.getLogger(ITestS3AInputStreamPerformance.class);

    private S3AFileSystem s3aFS;

    private Path testData;

    private FileStatus testDataStatus;

    private FSDataInputStream in;

    private InputStreamStatistics streamStatistics;

    public static final int BLOCK_SIZE = 32 * 1024;

    public static final int BIG_BLOCK_SIZE = 256 * 1024;

    /**
     * Tests only run if the there is a named test file that can be read.
     */
    private boolean testDataAvailable = true;

    private String assumptionMessage = "test file";

    @Test
    public void testTimeToOpenAndReadWholeFileBlocks() throws Throwable {
        requireCSVTestData();
        int blockSize = ITestS3AInputStreamPerformance._1MB;
        describe("Open the test file %s and read it in blocks of size %d", testData, blockSize);
        long len = testDataStatus.getLen();
        in = openTestFile();
        byte[] block = new byte[blockSize];
        NanoTimer timer2 = new NanoTimer();
        long count = 0;
        // implicitly rounding down here
        long blockCount = len / blockSize;
        long totalToRead = blockCount * blockSize;
        long minimumBandwidth = 128 * 1024;
        int maxResetCount = 4;
        int resetCount = 0;
        for (long i = 0; i < blockCount; i++) {
            int offset = 0;
            int remaining = blockSize;
            long blockId = i + 1;
            NanoTimer blockTimer = new NanoTimer();
            int reads = 0;
            while (remaining > 0) {
                NanoTimer readTimer = new NanoTimer();
                int bytesRead = in.read(block, offset, remaining);
                reads++;
                if (bytesRead == 1) {
                    break;
                }
                remaining -= bytesRead;
                offset += bytesRead;
                count += bytesRead;
                readTimer.end();
                if (bytesRead != 0) {
                    ITestS3AInputStreamPerformance.LOG.debug(("Bytes in read #{}: {} , block bytes: {}," + (" remaining in block: {}" + " duration={} nS; ns/byte: {}, bandwidth={} MB/s")), reads, bytesRead, (blockSize - remaining), remaining, readTimer.duration(), readTimer.nanosPerOperation(bytesRead), readTimer.bandwidthDescription(bytesRead));
                } else {
                    ITestS3AInputStreamPerformance.LOG.warn("0 bytes returned by read() operation #{}", reads);
                }
            } 
            blockTimer.end("Reading block %d in %d reads", blockId, reads);
            String bw = blockTimer.bandwidthDescription(blockSize);
            ITestS3AInputStreamPerformance.LOG.info("Bandwidth of block {}: {} MB/s: ", blockId, bw);
            if ((ITestS3AInputStreamPerformance.bandwidth(blockTimer, blockSize)) < minimumBandwidth) {
                ITestS3AInputStreamPerformance.LOG.warn("Bandwidth {} too low on block {}: resetting connection", bw, blockId);
                Assert.assertTrue((((("Bandwidth of " + bw) + " too low after  ") + resetCount) + " attempts"), (resetCount <= maxResetCount));
                resetCount++;
                // reset the connection
                getS3AInputStream(in).resetConnection();
            }
        }
        timer2.end("Time to read %d bytes in %d blocks", totalToRead, blockCount);
        ITestS3AInputStreamPerformance.LOG.info("Overall Bandwidth {} MB/s; reset connections {}", timer2.bandwidth(totalToRead), resetCount);
        logStreamStatistics();
    }

    @Test
    public void testLazySeekEnabled() throws Throwable {
        describe("Verify that seeks do not trigger any IO");
        in = openTestFile();
        long len = testDataStatus.getLen();
        NanoTimer timer = new NanoTimer();
        long blockCount = len / (ITestS3AInputStreamPerformance.BLOCK_SIZE);
        for (long i = 0; i < blockCount; i++) {
            in.seek((((in.getPos()) + (ITestS3AInputStreamPerformance.BLOCK_SIZE)) - 1));
        }
        in.seek(0);
        blockCount++;
        timer.end("Time to execute %d seeks", blockCount);
        logTimePerIOP("seek()", timer, blockCount);
        logStreamStatistics();
        assertOpenOperationCount(0);
        assertEquals("bytes read", 0, streamStatistics.bytesRead);
    }

    @Test
    public void testReadaheadOutOfRange() throws Throwable {
        try {
            in = openTestFile();
            in.setReadahead((-1L));
            fail(("Stream should have rejected the request " + (in)));
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testReadWithNormalPolicy() throws Throwable {
        describe("Read big blocks with a big readahead");
        executeSeekReadSequence(ITestS3AInputStreamPerformance.BIG_BLOCK_SIZE, ((ITestS3AInputStreamPerformance.BIG_BLOCK_SIZE) * 2), Normal);
        assertStreamOpenedExactlyOnce();
    }

    @Test
    public void testDecompressionSequential128K() throws Throwable {
        describe("Decompress with a 128K readahead");
        executeDecompression((128 * 1024), Sequential);
        assertStreamOpenedExactlyOnce();
    }

    public static final int _4K = 4 * 1024;

    public static final int _8K = 8 * 1024;

    public static final int _16K = 16 * 1024;

    public static final int _32K = 32 * 1024;

    public static final int _64K = 64 * 1024;

    public static final int _128K = 128 * 1024;

    public static final int _256K = 256 * 1024;

    public static final int _1MB = 1024 * 1024;

    public static final int _2MB = 2 * (ITestS3AInputStreamPerformance._1MB);

    public static final int _10MB = (ITestS3AInputStreamPerformance._1MB) * 10;

    public static final int _5MB = (ITestS3AInputStreamPerformance._1MB) * 5;

    private static final int[][] RANDOM_IO_SEQUENCE = new int[][]{ new int[]{ ITestS3AInputStreamPerformance._2MB, ITestS3AInputStreamPerformance._128K }, new int[]{ ITestS3AInputStreamPerformance._128K, ITestS3AInputStreamPerformance._128K }, new int[]{ ITestS3AInputStreamPerformance._5MB, ITestS3AInputStreamPerformance._64K }, new int[]{ ITestS3AInputStreamPerformance._1MB, ITestS3AInputStreamPerformance._1MB } };

    @Test
    public void testRandomIORandomPolicy() throws Throwable {
        executeRandomIO(Random, ((long) (ITestS3AInputStreamPerformance.RANDOM_IO_SEQUENCE.length)));
        assertEquals(("streams aborted in " + (streamStatistics)), 0, streamStatistics.aborted);
    }

    @Test
    public void testRandomIONormalPolicy() throws Throwable {
        long expectedOpenCount = ITestS3AInputStreamPerformance.RANDOM_IO_SEQUENCE.length;
        executeRandomIO(Normal, expectedOpenCount);
        assertEquals(("streams aborted in " + (streamStatistics)), 1, streamStatistics.aborted);
        assertEquals(("policy changes in " + (streamStatistics)), 2, streamStatistics.policySetCount);
        assertEquals(("input policy in " + (streamStatistics)), Random.ordinal(), streamStatistics.inputPolicy);
    }

    @Test
    public void testRandomReadOverBuffer() throws Throwable {
        describe(("read over a buffer, making sure that the requests" + " spans readahead ranges"));
        int datasetLen = ITestS3AInputStreamPerformance._32K;
        S3AFileSystem fs = getFileSystem();
        Path dataFile = path("testReadOverBuffer.bin");
        byte[] sourceData = dataset(datasetLen, 0, 64);
        // relies on the field 'fs' referring to the R/W FS
        writeDataset(fs, dataFile, sourceData, datasetLen, ITestS3AInputStreamPerformance._16K, true);
        byte[] buffer = new byte[datasetLen];
        int readahead = ITestS3AInputStreamPerformance._8K;
        int halfReadahead = ITestS3AInputStreamPerformance._4K;
        in = openDataFile(fs, dataFile, Random, readahead);
        ITestS3AInputStreamPerformance.LOG.info("Starting initial reads");
        S3AInputStream s3aStream = getS3aStream();
        assertEquals(readahead, s3aStream.getReadahead());
        byte[] oneByte = new byte[1];
        assertEquals(1, in.read(0, oneByte, 0, 1));
        // make some assertions about the current state
        assertEquals(("remaining in\n" + (in)), (readahead - 1), s3aStream.remainingInCurrentRequest());
        assertEquals(("range start in\n" + (in)), 0, s3aStream.getContentRangeStart());
        assertEquals(("range finish in\n" + (in)), readahead, s3aStream.getContentRangeFinish());
        assertStreamOpenedExactlyOnce();
        describe("Starting sequence of positioned read calls over\n%s", in);
        NanoTimer readTimer = new NanoTimer();
        int currentPos = halfReadahead;
        int offset = currentPos;
        int bytesRead = 0;
        int readOps = 0;
        // make multiple read() calls
        while (bytesRead < halfReadahead) {
            int length = (buffer.length) - offset;
            int read = in.read(currentPos, buffer, offset, length);
            bytesRead += read;
            offset += read;
            readOps++;
            assertEquals(((((((((("open operations on request #" + readOps) + " after reading ") + bytesRead) + " current position in stream ") + currentPos) + " in\n") + fs) + "\n ") + (in)), 1, streamStatistics.openOperations);
            for (int i = currentPos; i < (currentPos + read); i++) {
                assertEquals(("Wrong value from byte " + i), sourceData[i], buffer[i]);
            }
            currentPos += read;
        } 
        assertStreamOpenedExactlyOnce();
        // assert at the end of the original block
        assertEquals(readahead, currentPos);
        readTimer.end("read %d in %d operations", bytesRead, readOps);
        ITestS3AInputStreamPerformance.bandwidth(readTimer, bytesRead);
        ITestS3AInputStreamPerformance.LOG.info("Time per byte(): {} nS", toHuman(readTimer.nanosPerOperation(bytesRead)));
        ITestS3AInputStreamPerformance.LOG.info("Time per read(): {} nS", toHuman(readTimer.nanosPerOperation(readOps)));
        describe("read last byte");
        // read one more
        int read = in.read(currentPos, buffer, bytesRead, 1);
        assertTrue("-1 from last read", (read >= 0));
        assertOpenOperationCount(2);
        assertEquals("Wrong value from read ", sourceData[currentPos], ((int) (buffer[currentPos])));
        currentPos++;
        // now scan all the way to the end of the file, using single byte read()
        // calls
        describe("read() to EOF over \n%s", in);
        long readCount = 0;
        NanoTimer timer = new NanoTimer();
        ITestS3AInputStreamPerformance.LOG.info("seeking");
        in.seek(currentPos);
        ITestS3AInputStreamPerformance.LOG.info("reading");
        while (currentPos < datasetLen) {
            int r = in.read();
            assertTrue(((("Negative read() at position " + currentPos) + " in\n") + (in)), (r >= 0));
            buffer[currentPos] = ((byte) (r));
            assertEquals(("Wrong value from read from\n" + (in)), sourceData[currentPos], r);
            currentPos++;
            readCount++;
        } 
        timer.end("read %d bytes", readCount);
        ITestS3AInputStreamPerformance.bandwidth(timer, readCount);
        ITestS3AInputStreamPerformance.LOG.info("Time per read(): {} nS", toHuman(timer.nanosPerOperation(readCount)));
        assertEquals(("last read in " + (in)), (-1), in.read());
    }
}

