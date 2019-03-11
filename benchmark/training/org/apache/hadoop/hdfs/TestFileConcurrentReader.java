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
package org.apache.hadoop.hdfs;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import LeaseManager.LOG;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.event.Level;


/**
 * This class tests the cases of a concurrent reads/writes to a file;
 * ie, one writer and one or more readers can see unfinsihed blocks
 */
public class TestFileConcurrentReader {
    private enum SyncType {

        SYNC,
        APPEND;}

    private static final Logger LOG = Logger.getLogger(TestFileConcurrentReader.class);

    {
        GenericTestUtils.setLogLevel(LeaseManager.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(FSNamesystem.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(DFSClient.LOG, Level.TRACE);
    }

    static final long seed = 3735928559L;

    static final int blockSize = 8192;

    private static final int DEFAULT_WRITE_SIZE = 1024 + 1;

    private static final int SMALL_WRITE_SIZE = 61;

    private Configuration conf;

    private MiniDFSCluster cluster;

    private FileSystem fileSystem;

    /**
     * Test that that writes to an incomplete block are available to a reader
     */
    @Test(timeout = 30000)
    public void testUnfinishedBlockRead() throws IOException {
        // create a new file in the root, write data, do no close
        Path file1 = new Path("/unfinished-block");
        FSDataOutputStream stm = TestFileCreation.createFile(fileSystem, file1, 1);
        // write partial block and sync
        int partialBlockSize = (TestFileConcurrentReader.blockSize) / 2;
        writeFileAndSync(stm, partialBlockSize);
        // Make sure a client can read it before it is closed
        checkCanRead(fileSystem, file1, partialBlockSize);
        stm.close();
    }

    /**
     * test case: if the BlockSender decides there is only one packet to send,
     * the previous computation of the pktSize based on transferToAllowed
     * would result in too small a buffer to do the buffer-copy needed
     * for partial chunks.
     */
    @Test(timeout = 30000)
    public void testUnfinishedBlockPacketBufferOverrun() throws IOException {
        // check that / exists
        Path path = new Path("/");
        System.out.println((("Path : \"" + (path.toString())) + "\""));
        // create a new file in the root, write data, do no close
        Path file1 = new Path("/unfinished-block");
        final FSDataOutputStream stm = TestFileCreation.createFile(fileSystem, file1, 1);
        // write partial block and sync
        final int bytesPerChecksum = conf.getInt("io.bytes.per.checksum", 512);
        final int partialBlockSize = bytesPerChecksum - 1;
        writeFileAndSync(stm, partialBlockSize);
        // Make sure a client can read it before it is closed
        checkCanRead(fileSystem, file1, partialBlockSize);
        stm.close();
    }

    // use a small block size and a large write so that DN is busy creating
    // new blocks.  This makes it almost 100% sure we can reproduce
    // case of client getting a DN that hasn't yet created the blocks
    @Test(timeout = 30000)
    public void testImmediateReadOfNewFile() throws IOException {
        final int blockSize = 64 * 1024;
        final int writeSize = 10 * blockSize;
        Configuration conf = new Configuration();
        conf.setLong(DFS_BLOCK_SIZE_KEY, blockSize);
        init(conf);
        final int requiredSuccessfulOpens = 100;
        final Path file = new Path("/file1");
        final AtomicBoolean openerDone = new AtomicBoolean(false);
        final AtomicReference<String> errorMessage = new AtomicReference<String>();
        final FSDataOutputStream out = fileSystem.create(file);
        final Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!(openerDone.get())) {
                        out.write(DFSTestUtil.generateSequentialBytes(0, writeSize));
                        out.hflush();
                    } 
                } catch (IOException e) {
                    TestFileConcurrentReader.LOG.warn("error in writer", e);
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        TestFileConcurrentReader.LOG.error("unable to close file");
                    }
                }
            }
        });
        Thread opener = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < requiredSuccessfulOpens; i++) {
                        fileSystem.open(file).close();
                    }
                    openerDone.set(true);
                } catch (IOException e) {
                    openerDone.set(true);
                    errorMessage.set(String.format("got exception : %s", StringUtils.stringifyException(e)));
                } catch (Exception e) {
                    openerDone.set(true);
                    errorMessage.set(String.format("got exception : %s", StringUtils.stringifyException(e)));
                    writer.interrupt();
                    Assert.fail("here");
                }
            }
        });
        writer.start();
        opener.start();
        try {
            writer.join();
            opener.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Assert.assertNull(errorMessage.get(), errorMessage.get());
    }

    // for some reason, using tranferTo evokes the race condition more often
    // so test separately
    @Test(timeout = 30000)
    public void testUnfinishedBlockCRCErrorTransferTo() throws IOException {
        runTestUnfinishedBlockCRCError(true, TestFileConcurrentReader.SyncType.SYNC, TestFileConcurrentReader.DEFAULT_WRITE_SIZE);
    }

    @Test(timeout = 30000)
    public void testUnfinishedBlockCRCErrorTransferToVerySmallWrite() throws IOException {
        runTestUnfinishedBlockCRCError(true, TestFileConcurrentReader.SyncType.SYNC, TestFileConcurrentReader.SMALL_WRITE_SIZE);
    }

    @Test(timeout = 30000)
    public void testUnfinishedBlockCRCErrorNormalTransfer() throws IOException {
        runTestUnfinishedBlockCRCError(false, TestFileConcurrentReader.SyncType.SYNC, TestFileConcurrentReader.DEFAULT_WRITE_SIZE);
    }

    @Test(timeout = 30000)
    public void testUnfinishedBlockCRCErrorNormalTransferVerySmallWrite() throws IOException {
        runTestUnfinishedBlockCRCError(false, TestFileConcurrentReader.SyncType.SYNC, TestFileConcurrentReader.SMALL_WRITE_SIZE);
    }
}

