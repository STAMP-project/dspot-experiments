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
package org.apache.hadoop.hdfs.shortcircuit;


import DFSClient.LOG;
import DFSConfigKeys.DFS_CHECKSUM_TYPE_KEY;
import DFSConfigKeys.DFS_DOMAIN_SOCKET_PATH_KEY;
import HdfsClientConfigKeys.Read.ShortCircuit.KEY;
import HdfsClientConfigKeys.Read.ShortCircuit.SKIP_CHECKSUM_KEY;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.AppendTestUtil;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtilClient;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.impl.TestBlockReaderLocal;
import org.apache.hadoop.hdfs.protocol.ClientDatanodeProtocol;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.security.token.block.BlockTokenIdentifier;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.net.unix.DomainSocket;
import org.apache.hadoop.net.unix.TemporarySocketDirectory;
import org.apache.hadoop.security.token.Token;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for short circuit read functionality using {@link BlockReaderLocal}.
 * When a block is being read by a client is on the local datanode, instead of
 * using {@link DataTransferProtocol} and connect to datanode,
 * the short circuit read allows reading the file directly
 * from the files on the local file system.
 */
public class TestShortCircuitLocalRead {
    private static TemporarySocketDirectory sockDir;

    static final long seed = 3735928559L;

    static final int blockSize = 5120;

    @Test(timeout = 60000)
    public void testFileLocalReadNoChecksum() throws Exception {
        doTestShortCircuitRead(true, ((3 * (TestShortCircuitLocalRead.blockSize)) + 100), 0);
    }

    @Test(timeout = 60000)
    public void testFileLocalReadChecksum() throws Exception {
        doTestShortCircuitRead(false, ((3 * (TestShortCircuitLocalRead.blockSize)) + 100), 0);
    }

    @Test(timeout = 60000)
    public void testSmallFileLocalRead() throws Exception {
        doTestShortCircuitRead(false, 13, 0);
        doTestShortCircuitRead(false, 13, 5);
        doTestShortCircuitRead(true, 13, 0);
        doTestShortCircuitRead(true, 13, 5);
    }

    @Test(timeout = 60000)
    public void testLocalReadLegacy() throws Exception {
        doTestShortCircuitReadLegacy(true, 13, 0, TestShortCircuitLocalRead.getCurrentUser(), TestShortCircuitLocalRead.getCurrentUser(), false);
    }

    /**
     * Try a short circuit from a reader that is not allowed to
     * to use short circuit. The test ensures reader falls back to non
     * shortcircuit reads when shortcircuit is disallowed.
     */
    @Test(timeout = 60000)
    public void testLocalReadFallback() throws Exception {
        doTestShortCircuitReadLegacy(true, 13, 0, TestShortCircuitLocalRead.getCurrentUser(), "notallowed", true);
    }

    @Test(timeout = 60000)
    public void testReadFromAnOffset() throws Exception {
        doTestShortCircuitRead(false, ((3 * (TestShortCircuitLocalRead.blockSize)) + 100), 777);
        doTestShortCircuitRead(true, ((3 * (TestShortCircuitLocalRead.blockSize)) + 100), 777);
    }

    @Test(timeout = 60000)
    public void testLongFile() throws Exception {
        doTestShortCircuitRead(false, ((10 * (TestShortCircuitLocalRead.blockSize)) + 100), 777);
        doTestShortCircuitRead(true, ((10 * (TestShortCircuitLocalRead.blockSize)) + 100), 777);
    }

    @Test(timeout = 60000)
    public void testDeprecatedGetBlockLocalPathInfoRpc() throws IOException {
        final Configuration conf = new Configuration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).format(true).build();
        cluster.waitActive();
        FileSystem fs = cluster.getFileSystem();
        try {
            DFSTestUtil.createFile(fs, new Path("/tmp/x"), 16, ((short) (1)), 23);
            LocatedBlocks lb = cluster.getNameNode().getRpcServer().getBlockLocations("/tmp/x", 0, 16);
            // Create a new block object, because the block inside LocatedBlock at
            // namenode is of type BlockInfo.
            ExtendedBlock blk = new ExtendedBlock(lb.get(0).getBlock());
            Token<BlockTokenIdentifier> token = lb.get(0).getBlockToken();
            final DatanodeInfo dnInfo = lb.get(0).getLocations()[0];
            ClientDatanodeProtocol proxy = DFSUtilClient.createClientDatanodeProtocolProxy(dnInfo, conf, 60000, false);
            try {
                proxy.getBlockLocalPathInfo(blk, token);
                Assert.fail((("The call should have failed as this user " + " is not configured in ") + (DFSConfigKeys.DFS_BLOCK_LOCAL_PATH_ACCESS_USER_KEY)));
            } catch (IOException ex) {
                Assert.assertTrue(ex.getMessage().contains(("not configured in " + (DFSConfigKeys.DFS_BLOCK_LOCAL_PATH_ACCESS_USER_KEY))));
            }
        } finally {
            fs.close();
            cluster.shutdown();
        }
    }

    @Test(timeout = 60000)
    public void testSkipWithVerifyChecksum() throws IOException {
        int size = TestShortCircuitLocalRead.blockSize;
        Configuration conf = new Configuration();
        conf.setBoolean(KEY, true);
        conf.setBoolean(SKIP_CHECKSUM_KEY, false);
        conf.set(DFS_DOMAIN_SOCKET_PATH_KEY, new File(TestShortCircuitLocalRead.sockDir.getDir(), "testSkipWithVerifyChecksum._PORT.sock").getAbsolutePath());
        DomainSocket.disableBindPathValidation();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).format(true).build();
        FileSystem fs = cluster.getFileSystem();
        try {
            // check that / exists
            Path path = new Path("/");
            Assert.assertTrue("/ should be a directory", fs.getFileStatus(path).isDirectory());
            byte[] fileData = AppendTestUtil.randomBytes(TestShortCircuitLocalRead.seed, (size * 3));
            // create a new file in home directory. Do not close it.
            Path file1 = new Path("filelocal.dat");
            FSDataOutputStream stm = TestShortCircuitLocalRead.createFile(fs, file1, 1);
            // write to file
            stm.write(fileData);
            stm.close();
            // now test the skip function
            FSDataInputStream instm = fs.open(file1);
            byte[] actual = new byte[fileData.length];
            // read something from the block first, otherwise BlockReaderLocal.skip()
            // will not be invoked
            int nread = instm.read(actual, 0, 3);
            long skipped = (2 * size) + 3;
            instm.seek(skipped);
            nread = instm.read(actual, ((int) (skipped + nread)), 3);
            instm.close();
        } finally {
            fs.close();
            cluster.shutdown();
        }
    }

    @Test(timeout = 120000)
    public void testHandleTruncatedBlockFile() throws IOException {
        MiniDFSCluster cluster = null;
        HdfsConfiguration conf = new HdfsConfiguration();
        conf.setBoolean(KEY, true);
        conf.setBoolean(SKIP_CHECKSUM_KEY, false);
        conf.set(DFS_DOMAIN_SOCKET_PATH_KEY, new File(TestShortCircuitLocalRead.sockDir.getDir(), "testHandleTruncatedBlockFile._PORT.sock").getAbsolutePath());
        conf.set(DFS_CHECKSUM_TYPE_KEY, "CRC32C");
        final Path TEST_PATH = new Path("/a");
        final Path TEST_PATH2 = new Path("/b");
        final long RANDOM_SEED = 4567L;
        final long RANDOM_SEED2 = 4568L;
        FSDataInputStream fsIn = null;
        final int TEST_LENGTH = 3456;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).build();
            cluster.waitActive();
            FileSystem fs = cluster.getFileSystem();
            DFSTestUtil.createFile(fs, TEST_PATH, TEST_LENGTH, ((short) (1)), RANDOM_SEED);
            DFSTestUtil.createFile(fs, TEST_PATH2, TEST_LENGTH, ((short) (1)), RANDOM_SEED2);
            fsIn = cluster.getFileSystem().open(TEST_PATH2);
            byte[] original = new byte[TEST_LENGTH];
            IOUtils.readFully(fsIn, original, 0, TEST_LENGTH);
            fsIn.close();
            fsIn = null;
            try {
                DFSTestUtil.waitReplication(fs, TEST_PATH, ((short) (1)));
            } catch (InterruptedException e) {
                Assert.fail((("unexpected InterruptedException during " + "waitReplication: ") + e));
            } catch (TimeoutException e) {
                Assert.fail((("unexpected TimeoutException during " + "waitReplication: ") + e));
            }
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, TEST_PATH);
            File dataFile = cluster.getBlockFile(0, block);
            cluster.shutdown();
            cluster = null;
            try (RandomAccessFile raf = new RandomAccessFile(dataFile, "rw")) {
                raf.setLength(0);
            }
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(1).format(false).build();
            cluster.waitActive();
            fs = cluster.getFileSystem();
            fsIn = fs.open(TEST_PATH);
            try {
                byte[] buf = new byte[100];
                fsIn.seek(2000);
                fsIn.readFully(buf, 0, buf.length);
                Assert.fail(("shouldn't be able to read from corrupt 0-length " + "block file."));
            } catch (IOException e) {
                LOG.error("caught exception ", e);
            }
            fsIn.close();
            fsIn = null;
            // We should still be able to read the other file.
            // This is important because it indicates that we detected that the
            // previous block was corrupt, rather than blaming the problem on
            // communication.
            fsIn = fs.open(TEST_PATH2);
            byte[] buf = new byte[original.length];
            fsIn.readFully(buf, 0, buf.length);
            TestBlockReaderLocal.assertArrayRegionsEqual(original, 0, buf, 0, original.length);
            fsIn.close();
            fsIn = null;
        } finally {
            if (fsIn != null)
                fsIn.close();

            if (cluster != null)
                cluster.shutdown();

        }
    }

    @Test(timeout = 60000)
    public void testReadWithRemoteBlockReader2() throws IOException, InterruptedException {
        doTestShortCircuitReadWithRemoteBlockReader2(((3 * (TestShortCircuitLocalRead.blockSize)) + 100), TestShortCircuitLocalRead.getCurrentUser(), 0, false);
    }
}

