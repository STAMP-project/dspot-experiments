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


import CreateFlag.APPEND;
import CreateFlag.NEW_BLOCK;
import DataNode.LOG;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.slf4j.event.Level.TRACE;


/**
 * This class implements some of tests posted in HADOOP-2658.
 */
public class TestFileAppend3 {
    {
        DFSTestUtil.setNameNodeLogLevel(Level.ALL);
        GenericTestUtils.setLogLevel(LOG, Level.ALL);
        GenericTestUtils.setLogLevel(DFSClient.LOG, Level.ALL);
        GenericTestUtils.setLogLevel(InterDatanodeProtocol.LOG, TRACE);
    }

    static final long BLOCK_SIZE = 64 * 1024;

    static final short REPLICATION = 3;

    static final int DATANODE_NUM = 5;

    private static Configuration conf;

    private static int buffersize;

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem fs;

    /**
     * TC1: Append on block boundary.
     *
     * @throws IOException
     * 		an exception might be thrown
     */
    @Test
    public void testTC1() throws Exception {
        final Path p = new Path("/TC1/foo");
        System.out.println(("p=" + p));
        // a. Create file and write one block of data. Close file.
        final int len1 = ((int) (TestFileAppend3.BLOCK_SIZE));
        {
            FSDataOutputStream out = TestFileAppend3.fs.create(p, false, TestFileAppend3.buffersize, TestFileAppend3.REPLICATION, TestFileAppend3.BLOCK_SIZE);
            AppendTestUtil.write(out, 0, len1);
            out.close();
        }
        // Reopen file to append. Append half block of data. Close file.
        final int len2 = ((int) (TestFileAppend3.BLOCK_SIZE)) / 2;
        {
            FSDataOutputStream out = TestFileAppend3.fs.append(p);
            AppendTestUtil.write(out, len1, len2);
            out.close();
        }
        // b. Reopen file and read 1.5 blocks worth of data. Close file.
        AppendTestUtil.check(TestFileAppend3.fs, p, (len1 + len2));
    }

    @Test
    public void testTC1ForAppend2() throws Exception {
        final Path p = new Path("/TC1/foo2");
        // a. Create file and write one block of data. Close file.
        final int len1 = ((int) (TestFileAppend3.BLOCK_SIZE));
        {
            FSDataOutputStream out = TestFileAppend3.fs.create(p, false, TestFileAppend3.buffersize, TestFileAppend3.REPLICATION, TestFileAppend3.BLOCK_SIZE);
            AppendTestUtil.write(out, 0, len1);
            out.close();
        }
        // Reopen file to append. Append half block of data. Close file.
        final int len2 = ((int) (TestFileAppend3.BLOCK_SIZE)) / 2;
        {
            FSDataOutputStream out = TestFileAppend3.fs.append(p, EnumSet.of(APPEND, NEW_BLOCK), 4096, null);
            AppendTestUtil.write(out, len1, len2);
            out.close();
        }
        // b. Reopen file and read 1.5 blocks worth of data. Close file.
        AppendTestUtil.check(TestFileAppend3.fs, p, (len1 + len2));
    }

    /**
     * TC2: Append on non-block boundary.
     *
     * @throws IOException
     * 		an exception might be thrown
     */
    @Test
    public void testTC2() throws Exception {
        final Path p = new Path("/TC2/foo");
        System.out.println(("p=" + p));
        // a. Create file with one and a half block of data. Close file.
        final int len1 = ((int) ((TestFileAppend3.BLOCK_SIZE) + ((TestFileAppend3.BLOCK_SIZE) / 2)));
        {
            FSDataOutputStream out = TestFileAppend3.fs.create(p, false, TestFileAppend3.buffersize, TestFileAppend3.REPLICATION, TestFileAppend3.BLOCK_SIZE);
            AppendTestUtil.write(out, 0, len1);
            out.close();
        }
        AppendTestUtil.check(TestFileAppend3.fs, p, len1);
        // Reopen file to append quarter block of data. Close file.
        final int len2 = ((int) (TestFileAppend3.BLOCK_SIZE)) / 4;
        {
            FSDataOutputStream out = TestFileAppend3.fs.append(p);
            AppendTestUtil.write(out, len1, len2);
            out.close();
        }
        // b. Reopen file and read 1.75 blocks of data. Close file.
        AppendTestUtil.check(TestFileAppend3.fs, p, (len1 + len2));
    }

    @Test
    public void testTC2ForAppend2() throws Exception {
        final Path p = new Path("/TC2/foo2");
        // a. Create file with one and a half block of data. Close file.
        final int len1 = ((int) ((TestFileAppend3.BLOCK_SIZE) + ((TestFileAppend3.BLOCK_SIZE) / 2)));
        {
            FSDataOutputStream out = TestFileAppend3.fs.create(p, false, TestFileAppend3.buffersize, TestFileAppend3.REPLICATION, TestFileAppend3.BLOCK_SIZE);
            AppendTestUtil.write(out, 0, len1);
            out.close();
        }
        AppendTestUtil.check(TestFileAppend3.fs, p, len1);
        // Reopen file to append quarter block of data. Close file.
        final int len2 = ((int) (TestFileAppend3.BLOCK_SIZE)) / 4;
        {
            FSDataOutputStream out = TestFileAppend3.fs.append(p, EnumSet.of(APPEND, NEW_BLOCK), 4096, null);
            AppendTestUtil.write(out, len1, len2);
            out.close();
        }
        // b. Reopen file and read 1.75 blocks of data. Close file.
        AppendTestUtil.check(TestFileAppend3.fs, p, (len1 + len2));
        List<LocatedBlock> blocks = TestFileAppend3.fs.getClient().getLocatedBlocks(p.toString(), 0L).getLocatedBlocks();
        Assert.assertEquals(3, blocks.size());
        Assert.assertEquals(TestFileAppend3.BLOCK_SIZE, blocks.get(0).getBlockSize());
        Assert.assertEquals(((TestFileAppend3.BLOCK_SIZE) / 2), blocks.get(1).getBlockSize());
        Assert.assertEquals(((TestFileAppend3.BLOCK_SIZE) / 4), blocks.get(2).getBlockSize());
    }

    /**
     * TC5: Only one simultaneous append.
     *
     * @throws IOException
     * 		an exception might be thrown
     */
    @Test
    public void testTC5() throws Exception {
        final Path p = new Path("/TC5/foo");
        System.out.println(("p=" + p));
        // a. Create file on Machine M1. Write half block to it. Close file.
        {
            FSDataOutputStream out = TestFileAppend3.fs.create(p, false, TestFileAppend3.buffersize, TestFileAppend3.REPLICATION, TestFileAppend3.BLOCK_SIZE);
            AppendTestUtil.write(out, 0, ((int) ((TestFileAppend3.BLOCK_SIZE) / 2)));
            out.close();
        }
        // b. Reopen file in "append" mode on Machine M1.
        FSDataOutputStream out = TestFileAppend3.fs.append(p);
        // c. On Machine M2, reopen file in "append" mode. This should fail.
        try {
            AppendTestUtil.createHdfsWithDifferentUsername(TestFileAppend3.conf).append(p);
            Assert.fail("This should fail.");
        } catch (IOException ioe) {
            AppendTestUtil.LOG.info("GOOD: got an exception", ioe);
        }
        try {
            ((DistributedFileSystem) (AppendTestUtil.createHdfsWithDifferentUsername(TestFileAppend3.conf))).append(p, EnumSet.of(APPEND, NEW_BLOCK), 4096, null);
            Assert.fail("This should fail.");
        } catch (IOException ioe) {
            AppendTestUtil.LOG.info("GOOD: got an exception", ioe);
        }
        // d. On Machine M1, close file.
        out.close();
    }

    @Test
    public void testTC5ForAppend2() throws Exception {
        final Path p = new Path("/TC5/foo2");
        // a. Create file on Machine M1. Write half block to it. Close file.
        {
            FSDataOutputStream out = TestFileAppend3.fs.create(p, false, TestFileAppend3.buffersize, TestFileAppend3.REPLICATION, TestFileAppend3.BLOCK_SIZE);
            AppendTestUtil.write(out, 0, ((int) ((TestFileAppend3.BLOCK_SIZE) / 2)));
            out.close();
        }
        // b. Reopen file in "append" mode on Machine M1.
        FSDataOutputStream out = TestFileAppend3.fs.append(p, EnumSet.of(APPEND, NEW_BLOCK), 4096, null);
        // c. On Machine M2, reopen file in "append" mode. This should fail.
        try {
            ((DistributedFileSystem) (AppendTestUtil.createHdfsWithDifferentUsername(TestFileAppend3.conf))).append(p, EnumSet.of(APPEND, NEW_BLOCK), 4096, null);
            Assert.fail("This should fail.");
        } catch (IOException ioe) {
            AppendTestUtil.LOG.info("GOOD: got an exception", ioe);
        }
        try {
            AppendTestUtil.createHdfsWithDifferentUsername(TestFileAppend3.conf).append(p);
            Assert.fail("This should fail.");
        } catch (IOException ioe) {
            AppendTestUtil.LOG.info("GOOD: got an exception", ioe);
        }
        // d. On Machine M1, close file.
        out.close();
    }

    @Test
    public void testTC7() throws Exception {
        testTC7(false);
    }

    @Test
    public void testTC7ForAppend2() throws Exception {
        testTC7(true);
    }

    @Test
    public void testTC11() throws Exception {
        testTC11(false);
    }

    @Test
    public void testTC11ForAppend2() throws Exception {
        testTC11(true);
    }

    @Test
    public void testTC12() throws Exception {
        testTC12(false);
    }

    @Test
    public void testTC12ForAppend2() throws Exception {
        testTC12(true);
    }

    @Test
    public void testSmallAppendRace() throws Exception {
        final Path file = new Path("/testSmallAppendRace");
        final String fName = file.toUri().getPath();
        // Create the file and write a small amount of data.
        FSDataOutputStream stm = TestFileAppend3.fs.create(file);
        AppendTestUtil.write(stm, 0, 123);
        stm.close();
        // Introduce a delay between getFileInfo and calling append() against NN.
        final DFSClient client = DFSClientAdapter.getDFSClient(TestFileAppend3.fs);
        DFSClient spyClient = Mockito.spy(client);
        Mockito.when(spyClient.getFileInfo(fName)).thenAnswer(new Answer<HdfsFileStatus>() {
            @Override
            public HdfsFileStatus answer(InvocationOnMock invocation) {
                try {
                    HdfsFileStatus stat = client.getFileInfo(fName);
                    Thread.sleep(100);
                    return stat;
                } catch (Exception e) {
                    return null;
                }
            }
        });
        DFSClientAdapter.setDFSClient(TestFileAppend3.fs, spyClient);
        // Create two threads for doing appends to the same file.
        Thread worker1 = new Thread() {
            @Override
            public void run() {
                try {
                    doSmallAppends(file, TestFileAppend3.fs, 20);
                } catch (IOException e) {
                }
            }
        };
        Thread worker2 = new Thread() {
            @Override
            public void run() {
                try {
                    doSmallAppends(file, TestFileAppend3.fs, 20);
                } catch (IOException e) {
                }
            }
        };
        worker1.start();
        worker2.start();
        // append will fail when the file size crosses the checksum chunk boundary,
        // if append was called with a stale file stat.
        doSmallAppends(file, TestFileAppend3.fs, 20);
    }

    @Test
    public void testAppendToPartialChunk() throws IOException {
        testAppendToPartialChunk(false);
    }

    @Test
    public void testAppendToPartialChunkforAppend2() throws IOException {
        testAppendToPartialChunk(true);
    }
}

