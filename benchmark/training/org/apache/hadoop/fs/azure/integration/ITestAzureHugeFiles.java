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
package org.apache.hadoop.fs.azure.integration;


import ContractTestUtils.NanoTimer;
import java.io.IOException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.azure.AbstractWasbTestBase;
import org.apache.hadoop.fs.azure.AzureBlobStorageTestAccount;
import org.apache.hadoop.fs.azure.NativeAzureFileSystem;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.io.IOUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Scale test which creates a huge file.
 *
 * <b>Important:</b> the order in which these tests execute is fixed to
 * alphabetical order. Test cases are numbered {@code test_123_} to impose
 * an ordering based on the numbers.
 *
 * Having this ordering allows the tests to assume that the huge file
 * exists. Even so: they should all have a {@link #assumeHugeFileExists()}
 * check at the start, in case an individual test is executed.
 *
 * <b>Ignore checkstyle complaints about naming: we need a scheme with visible
 * ordering.</b>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ITestAzureHugeFiles extends AbstractAzureScaleTest {
    private static final Logger LOG = LoggerFactory.getLogger(ITestAzureHugeFiles.class);

    private Path scaleTestDir;

    private Path hugefile;

    private Path hugefileRenamed;

    private AzureBlobStorageTestAccount testAccountForCleanup;

    private static final int UPLOAD_BLOCKSIZE = 64 * (Sizes.S_1K);

    private static final byte[] SOURCE_DATA;

    static {
        SOURCE_DATA = dataset(ITestAzureHugeFiles.UPLOAD_BLOCKSIZE, 0, Sizes.S_256);
    }

    private Path testPath;

    @Test
    public void test_010_CreateHugeFile() throws IOException {
        long filesize = AzureTestUtils.getTestPropertyBytes(getConfiguration(), AzureTestConstants.KEY_HUGE_FILESIZE, AzureTestConstants.DEFAULT_HUGE_FILESIZE);
        long filesizeMB = filesize / (Sizes.S_1M);
        // clean up from any previous attempts
        deleteHugeFile();
        describe("Creating file %s of size %d MB", hugefile, filesizeMB);
        // now do a check of available upload time, with a pessimistic bandwidth
        // (that of remote upload tests). If the test times out then not only is
        // the test outcome lost, as the follow-on tests continue, they will
        // overlap with the ongoing upload test, for much confusion.
        /* int timeout = getTestTimeoutSeconds();
        // assume 1 MB/s upload bandwidth
        int bandwidth = _1MB;
        long uploadTime = filesize / bandwidth;
        assertTrue(String.format("Timeout set in %s seconds is too low;" +
        " estimating upload time of %d seconds at 1 MB/s." +
        " Rerun tests with -D%s=%d",
        timeout, uploadTime, KEY_TEST_TIMEOUT, uploadTime * 2),
        uploadTime < timeout);
         */
        Assert.assertEquals(((((("File size set in " + (AzureTestConstants.KEY_HUGE_FILESIZE)) + " = ") + filesize) + " is not a multiple of ") + (ITestAzureHugeFiles.UPLOAD_BLOCKSIZE)), 0, (filesize % (ITestAzureHugeFiles.UPLOAD_BLOCKSIZE)));
        byte[] data = ITestAzureHugeFiles.SOURCE_DATA;
        long blocks = filesize / (ITestAzureHugeFiles.UPLOAD_BLOCKSIZE);
        long blocksPerMB = (Sizes.S_1M) / (ITestAzureHugeFiles.UPLOAD_BLOCKSIZE);
        // perform the upload.
        // there's lots of logging here, so that a tail -f on the output log
        // can give a view of what is happening.
        NativeAzureFileSystem fs = getFileSystem();
        ContractTestUtils.NanoTimer timer = new ContractTestUtils.NanoTimer();
        long blocksPer10MB = blocksPerMB * 10;
        fs.mkdirs(hugefile.getParent());
        try (FSDataOutputStream out = fs.create(hugefile, true, ITestAzureHugeFiles.UPLOAD_BLOCKSIZE, null)) {
            for (long block = 1; block <= blocks; block++) {
                out.write(data);
                long written = block * (ITestAzureHugeFiles.UPLOAD_BLOCKSIZE);
                // every 10 MB and on file upload @ 100%, print some stats
                if (((block % blocksPer10MB) == 0) || (written == filesize)) {
                    long percentage = (written * 100) / filesize;
                    double elapsedTime = (timer.elapsedTime()) / (Sizes.NANOSEC);
                    double writtenMB = (1.0 * written) / (Sizes.S_1M);
                    ITestAzureHugeFiles.LOG.info(String.format(("[%02d%%] Buffered %.2f MB out of %d MB;" + " elapsedTime=%.2fs; write to buffer bandwidth=%.2f MB/s"), percentage, writtenMB, filesizeMB, elapsedTime, (writtenMB / elapsedTime)));
                }
            }
            // now close the file
            ITestAzureHugeFiles.LOG.info("Closing stream {}", out);
            ContractTestUtils.NanoTimer closeTimer = new ContractTestUtils.NanoTimer();
            out.close();
            closeTimer.end("time to close() output stream");
        }
        timer.end("time to write %d MB in blocks of %d", filesizeMB, ITestAzureHugeFiles.UPLOAD_BLOCKSIZE);
        logFSState();
        bandwidth(timer, filesize);
        ContractTestUtils.assertPathExists(fs, "Huge file", hugefile);
        FileStatus status = fs.getFileStatus(hugefile);
        ContractTestUtils.assertIsFile(hugefile, status);
        Assert.assertEquals(("File size in " + status), filesize, status.getLen());
    }

    @Test
    public void test_040_PositionedReadHugeFile() throws Throwable {
        assumeHugeFileExists();
        describe("Positioned reads of file %s", hugefile);
        NativeAzureFileSystem fs = getFileSystem();
        FileStatus status = fs.getFileStatus(hugefile);
        long filesize = status.getLen();
        int ops = 0;
        final int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];
        long eof = filesize - 1;
        ContractTestUtils.NanoTimer timer = new ContractTestUtils.NanoTimer();
        ContractTestUtils.NanoTimer readAtByte0;
        ContractTestUtils.NanoTimer readAtByte0Again;
        ContractTestUtils.NanoTimer readAtEOF;
        try (FSDataInputStream in = openDataFile()) {
            readAtByte0 = new ContractTestUtils.NanoTimer();
            in.readFully(0, buffer);
            readAtByte0.end("time to read data at start of file");
            ops++;
            readAtEOF = new ContractTestUtils.NanoTimer();
            in.readFully((eof - bufferSize), buffer);
            readAtEOF.end("time to read data at end of file");
            ops++;
            readAtByte0Again = new ContractTestUtils.NanoTimer();
            in.readFully(0, buffer);
            readAtByte0Again.end("time to read data at start of file again");
            ops++;
            ITestAzureHugeFiles.LOG.info("Final stream state: {}", in);
        }
        long mb = Math.max((filesize / (Sizes.S_1M)), 1);
        logFSState();
        timer.end("time to performed positioned reads of %d MB ", mb);
        ITestAzureHugeFiles.LOG.info("Time per positioned read = {} nS", toHuman(timer.nanosPerOperation(ops)));
    }

    @Test
    public void test_050_readHugeFile() throws Throwable {
        assumeHugeFileExists();
        describe("Reading %s", hugefile);
        NativeAzureFileSystem fs = getFileSystem();
        FileStatus status = fs.getFileStatus(hugefile);
        long filesize = status.getLen();
        long blocks = filesize / (ITestAzureHugeFiles.UPLOAD_BLOCKSIZE);
        byte[] data = new byte[ITestAzureHugeFiles.UPLOAD_BLOCKSIZE];
        ContractTestUtils.NanoTimer timer = new ContractTestUtils.NanoTimer();
        try (FSDataInputStream in = openDataFile()) {
            for (long block = 0; block < blocks; block++) {
                in.readFully(data);
            }
            ITestAzureHugeFiles.LOG.info("Final stream state: {}", in);
        }
        long mb = Math.max((filesize / (Sizes.S_1M)), 1);
        timer.end("time to read file of %d MB ", mb);
        ITestAzureHugeFiles.LOG.info("Time per MB to read = {} nS", toHuman(timer.nanosPerOperation(mb)));
        bandwidth(timer, filesize);
        logFSState();
    }

    @Test
    public void test_060_openAndReadWholeFileBlocks() throws Throwable {
        FileStatus status = assumeHugeFileExists();
        int blockSize = Sizes.S_1M;
        describe("Open the test file and read it in blocks of size %d", blockSize);
        long len = status.getLen();
        FSDataInputStream in = openDataFile();
        NanoTimer timer2 = null;
        long blockCount = 0;
        long totalToRead = 0;
        int resetCount = 0;
        try {
            byte[] block = new byte[blockSize];
            timer2 = new NanoTimer();
            long count = 0;
            // implicitly rounding down here
            blockCount = len / blockSize;
            totalToRead = blockCount * blockSize;
            long minimumBandwidth = Sizes.S_128K;
            int maxResetCount = 4;
            resetCount = 0;
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
                        ITestAzureHugeFiles.LOG.debug(("Bytes in read #{}: {} , block bytes: {}," + (" remaining in block: {}" + " duration={} nS; ns/byte: {}, bandwidth={} MB/s")), reads, bytesRead, (blockSize - remaining), remaining, readTimer.duration(), readTimer.nanosPerOperation(bytesRead), readTimer.bandwidthDescription(bytesRead));
                    } else {
                        ITestAzureHugeFiles.LOG.warn("0 bytes returned by read() operation #{}", reads);
                    }
                } 
                blockTimer.end("Reading block %d in %d reads", blockId, reads);
                String bw = blockTimer.bandwidthDescription(blockSize);
                ITestAzureHugeFiles.LOG.info("Bandwidth of block {}: {} MB/s: ", blockId, bw);
                if ((ITestAzureHugeFiles.bandwidthInBytes(blockTimer, blockSize)) < minimumBandwidth) {
                    ITestAzureHugeFiles.LOG.warn("Bandwidth {} too low on block {}: resetting connection", bw, blockId);
                    Assert.assertTrue((((("Bandwidth of " + bw) + " too low after ") + resetCount) + " attempts"), (resetCount <= maxResetCount));
                    resetCount++;
                    // reset the connection
                }
            }
        } finally {
            IOUtils.closeStream(in);
        }
        timer2.end("Time to read %d bytes in %d blocks", totalToRead, blockCount);
        ITestAzureHugeFiles.LOG.info("Overall Bandwidth {} MB/s; reset connections {}", timer2.bandwidth(totalToRead), resetCount);
    }

    @Test
    public void test_100_renameHugeFile() throws Throwable {
        assumeHugeFileExists();
        describe("renaming %s to %s", hugefile, hugefileRenamed);
        NativeAzureFileSystem fs = getFileSystem();
        FileStatus status = fs.getFileStatus(hugefile);
        long filesize = status.getLen();
        fs.delete(hugefileRenamed, false);
        ContractTestUtils.NanoTimer timer = new ContractTestUtils.NanoTimer();
        fs.rename(hugefile, hugefileRenamed);
        long mb = Math.max((filesize / (Sizes.S_1M)), 1);
        timer.end("time to rename file of %d MB", mb);
        ITestAzureHugeFiles.LOG.info("Time per MB to rename = {} nS", toHuman(timer.nanosPerOperation(mb)));
        bandwidth(timer, filesize);
        logFSState();
        FileStatus destFileStatus = fs.getFileStatus(hugefileRenamed);
        Assert.assertEquals(filesize, destFileStatus.getLen());
        // rename back
        ContractTestUtils.NanoTimer timer2 = new ContractTestUtils.NanoTimer();
        fs.rename(hugefileRenamed, hugefile);
        timer2.end("Renaming back");
        ITestAzureHugeFiles.LOG.info("Time per MB to rename = {} nS", toHuman(timer2.nanosPerOperation(mb)));
        bandwidth(timer2, filesize);
    }

    @Test
    public void test_999_deleteHugeFiles() throws IOException {
        // mark the test account for cleanup after this test
        testAccountForCleanup = testAccount;
        deleteHugeFile();
        ContractTestUtils.NanoTimer timer2 = new ContractTestUtils.NanoTimer();
        NativeAzureFileSystem fs = getFileSystem();
        fs.delete(hugefileRenamed, false);
        timer2.end("time to delete %s", hugefileRenamed);
        rm(fs, testPath, true, false);
        assertPathDoesNotExist(fs, "deleted huge file", testPath);
    }
}

