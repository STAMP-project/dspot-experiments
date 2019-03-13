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


import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY;
import HdfsClientConfigKeys.Retry.WINDOW_BASE_KEY;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileChecksum;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.datatransfer.TrustedChannelResolver;
import org.apache.hadoop.hdfs.protocol.datatransfer.sasl.DataTransferSaslUtil;
import org.apache.hadoop.hdfs.protocol.datatransfer.sasl.SaslDataTransferServer;
import org.apache.hadoop.hdfs.security.token.block.BlockTokenSecretManager;
import org.apache.hadoop.hdfs.security.token.block.DataEncryptionKey;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.GenericTestUtils.LogCapturer;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class TestEncryptedTransfer {
    {
        LogManager.getLogger(SaslDataTransferServer.class).setLevel(Level.DEBUG);
        LogManager.getLogger(DataTransferSaslUtil.class).setLevel(Level.DEBUG);
    }

    @Rule
    public Timeout timeout = new Timeout(300000);

    private static final Logger LOG = LoggerFactory.getLogger(TestEncryptedTransfer.class);

    private static final String PLAIN_TEXT = "this is very secret plain text";

    private static final Path TEST_PATH = new Path("/non-encrypted-file");

    private MiniDFSCluster cluster = null;

    private Configuration conf = null;

    private FileSystem fs = null;

    String resolverClazz;

    public TestEncryptedTransfer(String resolverClazz) {
        this.resolverClazz = resolverClazz;
    }

    @Test
    public void testEncryptedReadDefaultAlgorithmCipherSuite() throws IOException {
        testEncryptedRead("", "", false, false);
    }

    @Test
    public void testEncryptedReadWithRC4() throws IOException {
        testEncryptedRead("rc4", "", false, false);
    }

    @Test
    public void testEncryptedReadWithAES() throws IOException {
        testEncryptedRead("", "AES/CTR/NoPadding", true, false);
    }

    @Test
    public void testEncryptedReadAfterNameNodeRestart() throws IOException {
        testEncryptedRead("", "", false, true);
    }

    @Test
    public void testClientThatDoesNotSupportEncryption() throws IOException {
        // Set short retry timeouts so this test runs faster
        conf.setInt(WINDOW_BASE_KEY, 10);
        writeUnencryptedAndThenRestartEncryptedCluster();
        DFSClient client = DFSClientAdapter.getDFSClient(((DistributedFileSystem) (fs)));
        DFSClient spyClient = Mockito.spy(client);
        Mockito.doReturn(false).when(spyClient).shouldEncryptData();
        DFSClientAdapter.setDFSClient(((DistributedFileSystem) (fs)), spyClient);
        LogCapturer logs = GenericTestUtils.LogCapturer.captureLogs(LoggerFactory.getLogger(DataNode.class));
        try {
            Assert.assertEquals(TestEncryptedTransfer.PLAIN_TEXT, DFSTestUtil.readFile(fs, TestEncryptedTransfer.TEST_PATH));
            if (((resolverClazz) != null) && (!(resolverClazz.endsWith("TestTrustedChannelResolver")))) {
                Assert.fail("Should not have been able to read without encryption enabled.");
            }
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Could not obtain block:", ioe);
        } finally {
            logs.stopCapturing();
        }
        if ((resolverClazz) == null) {
            GenericTestUtils.assertMatches(logs.getOutput(), "Failed to read expected encryption handshake from client at");
        }
    }

    @Test
    public void testLongLivedReadClientAfterRestart() throws IOException {
        FileChecksum checksum = writeUnencryptedAndThenRestartEncryptedCluster();
        Assert.assertEquals(TestEncryptedTransfer.PLAIN_TEXT, DFSTestUtil.readFile(fs, TestEncryptedTransfer.TEST_PATH));
        Assert.assertEquals(checksum, fs.getFileChecksum(TestEncryptedTransfer.TEST_PATH));
        // Restart the NN and DN, after which the client's encryption key will no
        // longer be valid.
        cluster.restartNameNode();
        Assert.assertTrue(cluster.restartDataNode(0));
        Assert.assertEquals(TestEncryptedTransfer.PLAIN_TEXT, DFSTestUtil.readFile(fs, TestEncryptedTransfer.TEST_PATH));
        Assert.assertEquals(checksum, fs.getFileChecksum(TestEncryptedTransfer.TEST_PATH));
    }

    @Test
    public void testLongLivedWriteClientAfterRestart() throws IOException {
        setEncryptionConfigKeys();
        cluster = new MiniDFSCluster.Builder(conf).build();
        fs = TestEncryptedTransfer.getFileSystem(conf);
        TestEncryptedTransfer.writeTestDataToFile(fs);
        Assert.assertEquals(TestEncryptedTransfer.PLAIN_TEXT, DFSTestUtil.readFile(fs, TestEncryptedTransfer.TEST_PATH));
        // Restart the NN and DN, after which the client's encryption key will no
        // longer be valid.
        cluster.restartNameNode();
        Assert.assertTrue(cluster.restartDataNodes());
        cluster.waitActive();
        TestEncryptedTransfer.writeTestDataToFile(fs);
        Assert.assertEquals(((TestEncryptedTransfer.PLAIN_TEXT) + (TestEncryptedTransfer.PLAIN_TEXT)), DFSTestUtil.readFile(fs, TestEncryptedTransfer.TEST_PATH));
    }

    @Test
    public void testLongLivedClient() throws IOException, InterruptedException {
        FileChecksum checksum = writeUnencryptedAndThenRestartEncryptedCluster();
        BlockTokenSecretManager btsm = cluster.getNamesystem().getBlockManager().getBlockTokenSecretManager();
        btsm.setKeyUpdateIntervalForTesting((2 * 1000));
        btsm.setTokenLifetime((2 * 1000));
        btsm.clearAllKeysForTesting();
        Assert.assertEquals(TestEncryptedTransfer.PLAIN_TEXT, DFSTestUtil.readFile(fs, TestEncryptedTransfer.TEST_PATH));
        Assert.assertEquals(checksum, fs.getFileChecksum(TestEncryptedTransfer.TEST_PATH));
        // Sleep for 15 seconds, after which the encryption key will no longer be
        // valid. It needs to be a few multiples of the block token lifetime,
        // since several block tokens are valid at any given time (the current
        // and the last two, by default.)
        TestEncryptedTransfer.LOG.info("Sleeping so that encryption keys expire...");
        Thread.sleep((15 * 1000));
        TestEncryptedTransfer.LOG.info("Done sleeping.");
        Assert.assertEquals(TestEncryptedTransfer.PLAIN_TEXT, DFSTestUtil.readFile(fs, TestEncryptedTransfer.TEST_PATH));
        Assert.assertEquals(checksum, fs.getFileChecksum(TestEncryptedTransfer.TEST_PATH));
    }

    @Test
    public void testFileChecksumWithInvalidEncryptionKey() throws IOException, InterruptedException, TimeoutException {
        if ((resolverClazz) != null) {
            // TestTrustedChannelResolver does not use encryption keys.
            return;
        }
        setEncryptionConfigKeys();
        cluster = new MiniDFSCluster.Builder(conf).build();
        fs = TestEncryptedTransfer.getFileSystem(conf);
        DFSClient client = DFSClientAdapter.getDFSClient(((DistributedFileSystem) (fs)));
        DFSClient spyClient = Mockito.spy(client);
        DFSClientAdapter.setDFSClient(((DistributedFileSystem) (fs)), spyClient);
        TestEncryptedTransfer.writeTestDataToFile(fs);
        FileChecksum checksum = fs.getFileChecksum(TestEncryptedTransfer.TEST_PATH);
        BlockTokenSecretManager btsm = cluster.getNamesystem().getBlockManager().getBlockTokenSecretManager();
        // Reduce key update interval and token life for testing.
        btsm.setKeyUpdateIntervalForTesting((2 * 1000));
        btsm.setTokenLifetime((2 * 1000));
        btsm.clearAllKeysForTesting();
        // Wait until the encryption key becomes invalid.
        TestEncryptedTransfer.LOG.info("Wait until encryption keys become invalid...");
        DataEncryptionKey encryptionKey = spyClient.getEncryptionKey();
        List<DataNode> dataNodes = cluster.getDataNodes();
        for (DataNode dn : dataNodes) {
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return !(dn.getBlockPoolTokenSecretManager().get(encryptionKey.blockPoolId).hasKey(encryptionKey.keyId));
                }
            }, 100, (30 * 1000));
        }
        TestEncryptedTransfer.LOG.info("The encryption key is invalid on all nodes now.");
        fs.getFileChecksum(TestEncryptedTransfer.TEST_PATH);
        // verify that InvalidEncryptionKeyException is handled properly
        Assert.assertTrue(((client.getEncryptionKey()) == null));
        Mockito.verify(spyClient, Mockito.times(1)).clearDataEncryptionKey();
        // Retry the operation after clearing the encryption key
        FileChecksum verifyChecksum = fs.getFileChecksum(TestEncryptedTransfer.TEST_PATH);
        Assert.assertEquals(checksum, verifyChecksum);
    }

    @Test
    public void testLongLivedClientPipelineRecovery() throws IOException, InterruptedException, TimeoutException {
        if ((resolverClazz) != null) {
            // TestTrustedChannelResolver does not use encryption keys.
            return;
        }
        // use 4 datanodes to make sure that after 1 data node is stopped,
        // client only retries establishing pipeline with the 4th node.
        int numDataNodes = 4;
        // do not consider load factor when selecting a data node
        conf.setBoolean(DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY, false);
        setEncryptionConfigKeys();
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDataNodes).build();
        fs = TestEncryptedTransfer.getFileSystem(conf);
        DFSClient client = DFSClientAdapter.getDFSClient(((DistributedFileSystem) (fs)));
        DFSClient spyClient = Mockito.spy(client);
        DFSClientAdapter.setDFSClient(((DistributedFileSystem) (fs)), spyClient);
        TestEncryptedTransfer.writeTestDataToFile(fs);
        BlockTokenSecretManager btsm = cluster.getNamesystem().getBlockManager().getBlockTokenSecretManager();
        // Reduce key update interval and token life for testing.
        btsm.setKeyUpdateIntervalForTesting((2 * 1000));
        btsm.setTokenLifetime((2 * 1000));
        btsm.clearAllKeysForTesting();
        // Wait until the encryption key becomes invalid.
        TestEncryptedTransfer.LOG.info("Wait until encryption keys become invalid...");
        DataEncryptionKey encryptionKey = spyClient.getEncryptionKey();
        List<DataNode> dataNodes = cluster.getDataNodes();
        for (DataNode dn : dataNodes) {
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return !(dn.getBlockPoolTokenSecretManager().get(encryptionKey.blockPoolId).hasKey(encryptionKey.keyId));
                }
            }, 100, (30 * 1000));
        }
        TestEncryptedTransfer.LOG.info("The encryption key is invalid on all nodes now.");
        try (FSDataOutputStream out = fs.append(TestEncryptedTransfer.TEST_PATH)) {
            DFSOutputStream dfstream = ((DFSOutputStream) (out.getWrappedStream()));
            // shut down the first datanode in the pipeline.
            DatanodeInfo[] targets = dfstream.getPipeline();
            cluster.stopDataNode(targets[0].getXferAddr());
            // write data to induce pipeline recovery
            out.write(TestEncryptedTransfer.PLAIN_TEXT.getBytes());
            out.hflush();
            Assert.assertFalse("The first datanode in the pipeline was not replaced.", Arrays.asList(dfstream.getPipeline()).contains(targets[0]));
        }
        // verify that InvalidEncryptionKeyException is handled properly
        Mockito.verify(spyClient, Mockito.times(1)).clearDataEncryptionKey();
    }

    @Test
    public void testEncryptedWriteWithOneDn() throws IOException {
        testEncryptedWrite(1);
    }

    @Test
    public void testEncryptedWriteWithTwoDns() throws IOException {
        testEncryptedWrite(2);
    }

    @Test
    public void testEncryptedWriteWithMultipleDns() throws IOException {
        testEncryptedWrite(10);
    }

    @Test
    public void testEncryptedAppend() throws IOException {
        setEncryptionConfigKeys();
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
        fs = TestEncryptedTransfer.getFileSystem(conf);
        TestEncryptedTransfer.writeTestDataToFile(fs);
        Assert.assertEquals(TestEncryptedTransfer.PLAIN_TEXT, DFSTestUtil.readFile(fs, TestEncryptedTransfer.TEST_PATH));
        TestEncryptedTransfer.writeTestDataToFile(fs);
        Assert.assertEquals(((TestEncryptedTransfer.PLAIN_TEXT) + (TestEncryptedTransfer.PLAIN_TEXT)), DFSTestUtil.readFile(fs, TestEncryptedTransfer.TEST_PATH));
    }

    @Test
    public void testEncryptedAppendRequiringBlockTransfer() throws IOException {
        setEncryptionConfigKeys();
        // start up 4 DNs
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(4).build();
        fs = TestEncryptedTransfer.getFileSystem(conf);
        // Create a file with replication 3, so its block is on 3 / 4 DNs.
        TestEncryptedTransfer.writeTestDataToFile(fs);
        Assert.assertEquals(TestEncryptedTransfer.PLAIN_TEXT, DFSTestUtil.readFile(fs, TestEncryptedTransfer.TEST_PATH));
        // Shut down one of the DNs holding a block replica.
        FSDataInputStream in = fs.open(TestEncryptedTransfer.TEST_PATH);
        List<LocatedBlock> locatedBlocks = DFSTestUtil.getAllBlocks(in);
        in.close();
        Assert.assertEquals(1, locatedBlocks.size());
        Assert.assertEquals(3, locatedBlocks.get(0).getLocations().length);
        DataNode dn = cluster.getDataNode(locatedBlocks.get(0).getLocations()[0].getIpcPort());
        dn.shutdown();
        // Reopen the file for append, which will need to add another DN to the
        // pipeline and in doing so trigger a block transfer.
        TestEncryptedTransfer.writeTestDataToFile(fs);
        Assert.assertEquals(((TestEncryptedTransfer.PLAIN_TEXT) + (TestEncryptedTransfer.PLAIN_TEXT)), DFSTestUtil.readFile(fs, TestEncryptedTransfer.TEST_PATH));
    }

    static class TestTrustedChannelResolver extends TrustedChannelResolver {
        public boolean isTrusted() {
            return true;
        }

        public boolean isTrusted(InetAddress peerAddress) {
            return true;
        }
    }
}

