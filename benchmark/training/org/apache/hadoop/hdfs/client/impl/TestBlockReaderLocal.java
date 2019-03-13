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
package org.apache.hadoop.hdfs.client.impl;


import BlockType.CONTIGUOUS;
import BlockType.STRIPED;
import DFSConfigKeys.DFS_CHECKSUM_TYPE_KEY;
import HdfsClientConfigKeys.DFS_DATANODE_READAHEAD_BYTES_DEFAULT;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.ChecksumException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.ReadStatistics;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.client.HdfsDataInputStream;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.net.unix.TemporarySocketDirectory;
import org.junit.Assert;
import org.junit.Test;


public class TestBlockReaderLocal {
    private static TemporarySocketDirectory sockDir;

    private static class BlockReaderLocalTest {
        static final int TEST_LENGTH = 12345;

        static final int BYTES_PER_CHECKSUM = 512;

        public void setConfiguration(HdfsConfiguration conf) {
            // default: no-op
        }

        public void setup(File blockFile, boolean usingChecksums) throws IOException {
            // default: no-op
        }

        public void doTest(BlockReaderLocal reader, byte[] original) throws IOException {
            // default: no-op
        }
    }

    private static class TestBlockReaderLocalImmediateClose extends TestBlockReaderLocal.BlockReaderLocalTest {}

    @Test
    public void testBlockReaderLocalImmediateClose() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalImmediateClose(), true, 0);
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalImmediateClose(), false, 0);
    }

    private static class TestBlockReaderSimpleReads extends TestBlockReaderLocal.BlockReaderLocalTest {
        @Override
        public void doTest(BlockReaderLocal reader, byte[] original) throws IOException {
            byte[] buf = new byte[TestBlockReaderLocal.BlockReaderLocalTest.TEST_LENGTH];
            reader.readFully(buf, 0, 512);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 0, buf, 0, 512);
            reader.readFully(buf, 512, 512);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 512, buf, 512, 512);
            reader.readFully(buf, 1024, 513);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 1024, buf, 1024, 513);
            reader.readFully(buf, 1537, 514);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 1537, buf, 1537, 514);
            // Readahead is always at least the size of one chunk in this test.
            Assert.assertTrue(((reader.getMaxReadaheadLength()) >= (TestBlockReaderLocal.BlockReaderLocalTest.BYTES_PER_CHECKSUM)));
        }
    }

    @Test
    public void testBlockReaderSimpleReads() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderSimpleReads(), true, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderSimpleReadsShortReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderSimpleReads(), true, ((TestBlockReaderLocal.BlockReaderLocalTest.BYTES_PER_CHECKSUM) - 1));
    }

    @Test
    public void testBlockReaderSimpleReadsNoChecksum() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderSimpleReads(), false, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderSimpleReadsNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderSimpleReads(), true, 0);
    }

    @Test
    public void testBlockReaderSimpleReadsNoChecksumNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderSimpleReads(), false, 0);
    }

    private static class TestBlockReaderLocalArrayReads2 extends TestBlockReaderLocal.BlockReaderLocalTest {
        @Override
        public void doTest(BlockReaderLocal reader, byte[] original) throws IOException {
            byte[] buf = new byte[TestBlockReaderLocal.BlockReaderLocalTest.TEST_LENGTH];
            reader.readFully(buf, 0, 10);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 0, buf, 0, 10);
            reader.readFully(buf, 10, 100);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 10, buf, 10, 100);
            reader.readFully(buf, 110, 700);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 110, buf, 110, 700);
            reader.readFully(buf, 810, 1);// from offset 810 to offset 811

            reader.readFully(buf, 811, 5);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 811, buf, 811, 5);
            reader.readFully(buf, 816, 900);// skip from offset 816 to offset 1716

            reader.readFully(buf, 1716, 5);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 1716, buf, 1716, 5);
        }
    }

    @Test
    public void testBlockReaderLocalArrayReads2() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalArrayReads2(), true, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalArrayReads2NoChecksum() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalArrayReads2(), false, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalArrayReads2NoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalArrayReads2(), true, 0);
    }

    @Test
    public void testBlockReaderLocalArrayReads2NoChecksumNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalArrayReads2(), false, 0);
    }

    private static class TestBlockReaderLocalByteBufferReads extends TestBlockReaderLocal.BlockReaderLocalTest {
        @Override
        public void doTest(BlockReaderLocal reader, byte[] original) throws IOException {
            ByteBuffer buf = ByteBuffer.wrap(new byte[TestBlockReaderLocal.BlockReaderLocalTest.TEST_LENGTH]);
            TestBlockReaderLocal.readFully(reader, buf, 0, 10);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 0, buf.array(), 0, 10);
            TestBlockReaderLocal.readFully(reader, buf, 10, 100);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 10, buf.array(), 10, 100);
            TestBlockReaderLocal.readFully(reader, buf, 110, 700);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 110, buf.array(), 110, 700);
            reader.skip(1);// skip from offset 810 to offset 811

            TestBlockReaderLocal.readFully(reader, buf, 811, 5);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 811, buf.array(), 811, 5);
        }
    }

    @Test
    public void testBlockReaderLocalByteBufferReads() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalByteBufferReads(), true, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalByteBufferReadsNoChecksum() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalByteBufferReads(), false, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalByteBufferReadsNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalByteBufferReads(), true, 0);
    }

    @Test
    public void testBlockReaderLocalByteBufferReadsNoChecksumNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalByteBufferReads(), false, 0);
    }

    /**
     * Test reads that bypass the bounce buffer (because they are aligned
     * and bigger than the readahead).
     */
    private static class TestBlockReaderLocalByteBufferFastLaneReads extends TestBlockReaderLocal.BlockReaderLocalTest {
        @Override
        public void doTest(BlockReaderLocal reader, byte[] original) throws IOException {
            ByteBuffer buf = ByteBuffer.allocateDirect(TestBlockReaderLocal.BlockReaderLocalTest.TEST_LENGTH);
            TestBlockReaderLocal.readFully(reader, buf, 0, 5120);
            buf.flip();
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 0, DFSTestUtil.asArray(buf), 0, 5120);
            reader.skip(1537);
            TestBlockReaderLocal.readFully(reader, buf, 0, 1);
            buf.flip();
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 6657, DFSTestUtil.asArray(buf), 0, 1);
            reader.forceAnchorable();
            TestBlockReaderLocal.readFully(reader, buf, 0, 5120);
            buf.flip();
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 6658, DFSTestUtil.asArray(buf), 0, 5120);
            reader.forceUnanchorable();
            TestBlockReaderLocal.readFully(reader, buf, 0, 513);
            buf.flip();
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 11778, DFSTestUtil.asArray(buf), 0, 513);
            reader.skip(3);
            TestBlockReaderLocal.readFully(reader, buf, 0, 50);
            buf.flip();
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 12294, DFSTestUtil.asArray(buf), 0, 50);
        }
    }

    @Test
    public void testBlockReaderLocalByteBufferFastLaneReads() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalByteBufferFastLaneReads(), true, (2 * (TestBlockReaderLocal.BlockReaderLocalTest.BYTES_PER_CHECKSUM)));
    }

    @Test
    public void testBlockReaderLocalByteBufferFastLaneReadsNoChecksum() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalByteBufferFastLaneReads(), false, (2 * (TestBlockReaderLocal.BlockReaderLocalTest.BYTES_PER_CHECKSUM)));
    }

    @Test
    public void testBlockReaderLocalByteBufferFastLaneReadsNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalByteBufferFastLaneReads(), true, 0);
    }

    @Test
    public void testBlockReaderLocalByteBufferFastLaneReadsNoChecksumNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalByteBufferFastLaneReads(), false, 0);
    }

    private static class TestBlockReaderLocalReadCorruptStart extends TestBlockReaderLocal.BlockReaderLocalTest {
        boolean usingChecksums = false;

        @Override
        public void setup(File blockFile, boolean usingChecksums) throws IOException {
            RandomAccessFile bf = null;
            this.usingChecksums = usingChecksums;
            try {
                bf = new RandomAccessFile(blockFile, "rw");
                bf.write(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
            } finally {
                if (bf != null)
                    bf.close();

            }
        }

        public void doTest(BlockReaderLocal reader, byte[] original) throws IOException {
            byte[] buf = new byte[TestBlockReaderLocal.BlockReaderLocalTest.TEST_LENGTH];
            if (usingChecksums) {
                try {
                    reader.readFully(buf, 0, 10);
                    Assert.fail("did not detect corruption");
                } catch (IOException e) {
                    // expected
                }
            } else {
                reader.readFully(buf, 0, 10);
            }
        }
    }

    @Test
    public void testBlockReaderLocalReadCorruptStart() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalReadCorruptStart(), true, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    private static class TestBlockReaderLocalReadCorrupt extends TestBlockReaderLocal.BlockReaderLocalTest {
        boolean usingChecksums = false;

        @Override
        public void setup(File blockFile, boolean usingChecksums) throws IOException {
            RandomAccessFile bf = null;
            this.usingChecksums = usingChecksums;
            try {
                bf = new RandomAccessFile(blockFile, "rw");
                bf.seek(1539);
                bf.write(new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
            } finally {
                if (bf != null)
                    bf.close();

            }
        }

        public void doTest(BlockReaderLocal reader, byte[] original) throws IOException {
            byte[] buf = new byte[TestBlockReaderLocal.BlockReaderLocalTest.TEST_LENGTH];
            try {
                reader.readFully(buf, 0, 10);
                TestBlockReaderLocal.assertArrayRegionsEqual(original, 0, buf, 0, 10);
                reader.readFully(buf, 10, 100);
                TestBlockReaderLocal.assertArrayRegionsEqual(original, 10, buf, 10, 100);
                reader.readFully(buf, 110, 700);
                TestBlockReaderLocal.assertArrayRegionsEqual(original, 110, buf, 110, 700);
                reader.skip(1);// skip from offset 810 to offset 811

                reader.readFully(buf, 811, 5);
                TestBlockReaderLocal.assertArrayRegionsEqual(original, 811, buf, 811, 5);
                reader.readFully(buf, 816, 900);
                if (usingChecksums) {
                    // We should detect the corruption when using a checksum file.
                    Assert.fail("did not detect corruption");
                }
            } catch (ChecksumException e) {
                if (!(usingChecksums)) {
                    Assert.fail(("didn't expect to get ChecksumException: not " + "using checksums."));
                }
            }
        }
    }

    @Test
    public void testBlockReaderLocalReadCorrupt() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalReadCorrupt(), true, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalReadCorruptNoChecksum() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalReadCorrupt(), false, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalReadCorruptNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalReadCorrupt(), true, 0);
    }

    @Test
    public void testBlockReaderLocalReadCorruptNoChecksumNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalReadCorrupt(), false, 0);
    }

    private static class TestBlockReaderLocalWithMlockChanges extends TestBlockReaderLocal.BlockReaderLocalTest {
        @Override
        public void setup(File blockFile, boolean usingChecksums) throws IOException {
        }

        @Override
        public void doTest(BlockReaderLocal reader, byte[] original) throws IOException {
            ByteBuffer buf = ByteBuffer.wrap(new byte[TestBlockReaderLocal.BlockReaderLocalTest.TEST_LENGTH]);
            reader.skip(1);
            TestBlockReaderLocal.readFully(reader, buf, 1, 9);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 1, buf.array(), 1, 9);
            TestBlockReaderLocal.readFully(reader, buf, 10, 100);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 10, buf.array(), 10, 100);
            reader.forceAnchorable();
            TestBlockReaderLocal.readFully(reader, buf, 110, 700);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 110, buf.array(), 110, 700);
            reader.forceUnanchorable();
            reader.skip(1);// skip from offset 810 to offset 811

            TestBlockReaderLocal.readFully(reader, buf, 811, 5);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 811, buf.array(), 811, 5);
        }
    }

    @Test
    public void testBlockReaderLocalWithMlockChanges() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalWithMlockChanges(), true, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalWithMlockChangesNoChecksum() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalWithMlockChanges(), false, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalWithMlockChangesNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalWithMlockChanges(), true, 0);
    }

    @Test
    public void testBlockReaderLocalWithMlockChangesNoChecksumNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalWithMlockChanges(), false, 0);
    }

    private static class TestBlockReaderLocalOnFileWithoutChecksum extends TestBlockReaderLocal.BlockReaderLocalTest {
        @Override
        public void setConfiguration(HdfsConfiguration conf) {
            conf.set(DFS_CHECKSUM_TYPE_KEY, "NULL");
        }

        @Override
        public void doTest(BlockReaderLocal reader, byte[] original) throws IOException {
            Assert.assertTrue((!(reader.getVerifyChecksum())));
            ByteBuffer buf = ByteBuffer.wrap(new byte[TestBlockReaderLocal.BlockReaderLocalTest.TEST_LENGTH]);
            reader.skip(1);
            TestBlockReaderLocal.readFully(reader, buf, 1, 9);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 1, buf.array(), 1, 9);
            TestBlockReaderLocal.readFully(reader, buf, 10, 100);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 10, buf.array(), 10, 100);
            reader.forceAnchorable();
            TestBlockReaderLocal.readFully(reader, buf, 110, 700);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 110, buf.array(), 110, 700);
            reader.forceUnanchorable();
            reader.skip(1);// skip from offset 810 to offset 811

            TestBlockReaderLocal.readFully(reader, buf, 811, 5);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 811, buf.array(), 811, 5);
        }
    }

    private static class TestBlockReaderLocalReadZeroBytes extends TestBlockReaderLocal.BlockReaderLocalTest {
        @Override
        public void doTest(BlockReaderLocal reader, byte[] original) throws IOException {
            byte[] emptyArr = new byte[0];
            Assert.assertEquals(0, reader.read(emptyArr, 0, 0));
            ByteBuffer emptyBuf = ByteBuffer.wrap(emptyArr);
            Assert.assertEquals(0, reader.read(emptyBuf));
            reader.skip(1);
            Assert.assertEquals(0, reader.read(emptyArr, 0, 0));
            Assert.assertEquals(0, reader.read(emptyBuf));
            reader.skip(((TestBlockReaderLocal.BlockReaderLocalTest.TEST_LENGTH) - 1));
            Assert.assertEquals((-1), reader.read(emptyArr, 0, 0));
            Assert.assertEquals((-1), reader.read(emptyBuf));
        }
    }

    @Test
    public void testBlockReaderLocalOnFileWithoutChecksum() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalOnFileWithoutChecksum(), true, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalOnFileWithoutChecksumNoChecksum() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalOnFileWithoutChecksum(), false, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalOnFileWithoutChecksumNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalOnFileWithoutChecksum(), true, 0);
    }

    @Test
    public void testBlockReaderLocalOnFileWithoutChecksumNoChecksumNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalOnFileWithoutChecksum(), false, 0);
    }

    @Test
    public void testBlockReaderLocalReadZeroBytes() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalReadZeroBytes(), true, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalReadZeroBytesNoChecksum() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalReadZeroBytes(), false, DFS_DATANODE_READAHEAD_BYTES_DEFAULT);
    }

    @Test
    public void testBlockReaderLocalReadZeroBytesNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalReadZeroBytes(), true, 0);
    }

    @Test
    public void testBlockReaderLocalReadZeroBytesNoChecksumNoReadahead() throws IOException {
        runBlockReaderLocalTest(new TestBlockReaderLocal.TestBlockReaderLocalReadZeroBytes(), false, 0);
    }

    @Test(timeout = 60000)
    public void TestStatisticsForShortCircuitLocalRead() throws Exception {
        testStatistics(true);
    }

    @Test(timeout = 60000)
    public void TestStatisticsForLocalRead() throws Exception {
        testStatistics(false);
    }

    @Test(timeout = 60000)
    public void testStatisticsForErasureCodingRead() throws IOException {
        HdfsConfiguration conf = new HdfsConfiguration();
        final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();
        final int numDataNodes = (ecPolicy.getNumDataUnits()) + (ecPolicy.getNumParityUnits());
        // The length of test file is one full strip + one partial stripe. And
        // it is not bound to the stripe cell size.
        final int length = ((ecPolicy.getCellSize()) * (numDataNodes + 1)) + 123;
        final long randomSeed = 4567L;
        final short repl = 1;
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDataNodes).build()) {
            cluster.waitActive();
            DistributedFileSystem fs = cluster.getFileSystem();
            fs.enableErasureCodingPolicy(ecPolicy.getName());
            Path ecDir = new Path("/ec");
            fs.mkdirs(ecDir);
            fs.setErasureCodingPolicy(ecDir, ecPolicy.getName());
            Path nonEcDir = new Path("/noEc");
            fs.mkdirs(nonEcDir);
            byte[] buf = new byte[length];
            Path nonEcFile = new Path(nonEcDir, "file1");
            DFSTestUtil.createFile(fs, nonEcFile, length, repl, randomSeed);
            try (HdfsDataInputStream in = ((HdfsDataInputStream) (fs.open(nonEcFile)))) {
                IOUtils.readFully(in, buf, 0, length);
                ReadStatistics stats = in.getReadStatistics();
                Assert.assertEquals(CONTIGUOUS, stats.getBlockType());
                Assert.assertEquals(length, stats.getTotalBytesRead());
                Assert.assertEquals(length, stats.getTotalLocalBytesRead());
            }
            Path ecFile = new Path(ecDir, "file2");
            DFSTestUtil.createFile(fs, ecFile, length, repl, randomSeed);
            // Shutdown a DataNode that holds a data block, to trigger EC decoding.
            final BlockLocation[] locs = fs.getFileBlockLocations(ecFile, 0, length);
            final String[] nodes = locs[0].getNames();
            cluster.stopDataNode(nodes[0]);
            try (HdfsDataInputStream in = ((HdfsDataInputStream) (fs.open(ecFile)))) {
                IOUtils.readFully(in, buf, 0, length);
                ReadStatistics stats = in.getReadStatistics();
                Assert.assertEquals(STRIPED, stats.getBlockType());
                Assert.assertEquals(length, stats.getTotalLocalBytesRead());
                Assert.assertEquals(length, stats.getTotalBytesRead());
                Assert.assertTrue(((stats.getTotalEcDecodingTimeMillis()) > 0));
            }
        }
    }
}

