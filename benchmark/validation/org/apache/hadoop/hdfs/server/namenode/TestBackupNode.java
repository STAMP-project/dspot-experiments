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
package org.apache.hadoop.hdfs.server.namenode;


import Checkpointer.LOG;
import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import DFSConfigKeys.DFS_BLOCKREPORT_INITIAL_DELAY_KEY;
import DFSConfigKeys.DFS_DATANODE_SCAN_PERIOD_HOURS_KEY;
import DFSConfigKeys.DFS_NAMENODE_BACKUP_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_TXNS_KEY;
import DFSConfigKeys.DFS_NAMENODE_EDITS_DIR_KEY;
import DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMENODE_KEYTAB_FILE_KEY;
import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY;
import DFSConfigKeys.DFS_NAMESERVICES;
import DFSConfigKeys.DFS_NAMESERVICE_ID;
import DFSConfigKeys.FS_DEFAULT_NAME_KEY;
import StartupOption.BACKUP;
import StartupOption.CHECKPOINT;
import StartupOption.REGULAR;
import UserGroupInformation.AuthenticationMethod.SIMPLE;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.HAUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.StartupOption;
import org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory;
import org.apache.hadoop.hdfs.server.namenode.FileJournalManager.EditLogFile;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.net.ServerSocketUtil;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


public class TestBackupNode {
    public static final Logger LOG = LoggerFactory.getLogger(TestBackupNode.class);

    static {
        GenericTestUtils.setLogLevel(Checkpointer.LOG, Level.TRACE);
        GenericTestUtils.setLogLevel(BackupImage.LOG, Level.TRACE);
    }

    static final String BASE_DIR = MiniDFSCluster.getBaseDirectory();

    static final long seed = 3735928559L;

    static final int blockSize = 4096;

    static final int fileSize = 8192;

    /**
     * Regression test for HDFS-9249.
     *  This test configures the primary name node with SIMPLE authentication,
     *  and configures the backup node with Kerberose authentication with
     *  invalid keytab settings.
     *
     *  This configuration causes the backup node to throw a NPE trying to abort
     *  the edit log.
     */
    @Test
    public void startBackupNodeWithIncorrectAuthentication() throws IOException {
        Configuration c = new HdfsConfiguration();
        StartupOption startupOpt = StartupOption.CHECKPOINT;
        String dirs = TestBackupNode.getBackupNodeDir(startupOpt, 1);
        c.set(FS_DEFAULT_NAME_KEY, ("hdfs://127.0.0.1:" + (ServerSocketUtil.getPort(0, 100))));
        c.set(DFS_NAMENODE_HTTP_ADDRESS_KEY, "127.0.0.1:0");
        c.set(DFS_BLOCKREPORT_INITIAL_DELAY_KEY, "0");
        c.setInt(DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, (-1));// disable block scanner

        c.setInt(DFS_NAMENODE_CHECKPOINT_TXNS_KEY, 1);
        c.set(DFS_NAMENODE_NAME_DIR_KEY, dirs);
        c.set(DFS_NAMENODE_EDITS_DIR_KEY, (("${" + (DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY)) + "}"));
        c.set(DFS_NAMENODE_BACKUP_ADDRESS_KEY, "127.0.0.1:0");
        c.set(DFS_NAMENODE_BACKUP_HTTP_ADDRESS_KEY, "127.0.0.1:0");
        NameNode nn;
        try {
            Configuration nnconf = new HdfsConfiguration(c);
            DFSTestUtil.formatNameNode(nnconf);
            nn = NameNode.createNameNode(new String[]{  }, nnconf);
        } catch (IOException e) {
            TestBackupNode.LOG.info("IOException is thrown creating name node");
            throw e;
        }
        c.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        c.set(DFS_NAMENODE_KEYTAB_FILE_KEY, "");
        BackupNode bn = null;
        try {
            bn = ((BackupNode) (NameNode.createNameNode(new String[]{ startupOpt.getName() }, c)));
            Assert.assertTrue("Namesystem in BackupNode should be null", ((bn.getNamesystem()) == null));
            Assert.fail("Incorrect authentication setting should throw IOException");
        } catch (IOException e) {
            TestBackupNode.LOG.info("IOException thrown.", e);
            Assert.assertTrue(e.getMessage().contains("Running in secure mode"));
        } finally {
            if (nn != null) {
                nn.stop();
            }
            if (bn != null) {
                bn.stop();
            }
            SecurityUtil.setAuthenticationMethod(SIMPLE, c);
            // reset security authentication
            UserGroupInformation.setConfiguration(c);
        }
    }

    @Test
    public void testCheckpointNode() throws Exception {
        testCheckpoint(CHECKPOINT);
    }

    /**
     * Ensure that the backupnode will tail edits from the NN
     * and keep in sync, even while the NN rolls, checkpoints
     * occur, etc.
     */
    @Test
    public void testBackupNodeTailsEdits() throws Exception {
        Configuration conf = new HdfsConfiguration();
        HAUtil.setAllowStandbyReads(conf, true);
        MiniDFSCluster cluster = null;
        FileSystem fileSys = null;
        BackupNode backup = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).build();
            fileSys = cluster.getFileSystem();
            backup = startBackupNode(conf, BACKUP, 1);
            BackupImage bnImage = ((BackupImage) (backup.getFSImage()));
            testBNInSync(cluster, backup, 1);
            // Force a roll -- BN should roll with NN.
            NameNode nn = cluster.getNameNode();
            NamenodeProtocols nnRpc = nn.getRpcServer();
            nnRpc.rollEditLog();
            Assert.assertEquals(bnImage.getEditLog().getCurSegmentTxId(), nn.getFSImage().getEditLog().getCurSegmentTxId());
            // BN should stay in sync after roll
            testBNInSync(cluster, backup, 2);
            long nnImageBefore = nn.getFSImage().getStorage().getMostRecentCheckpointTxId();
            // BN checkpoint
            backup.doCheckpoint();
            // NN should have received a new image
            long nnImageAfter = nn.getFSImage().getStorage().getMostRecentCheckpointTxId();
            Assert.assertTrue(((("nn should have received new checkpoint. before: " + nnImageBefore) + " after: ") + nnImageAfter), (nnImageAfter > nnImageBefore));
            // BN should stay in sync after checkpoint
            testBNInSync(cluster, backup, 3);
            // Stop BN
            StorageDirectory sd = bnImage.getStorage().getStorageDir(0);
            backup.stop();
            backup = null;
            // When shutting down the BN, it shouldn't finalize logs that are
            // still open on the NN
            EditLogFile editsLog = FSImageTestUtil.findLatestEditsLog(sd);
            Assert.assertEquals(editsLog.getFirstTxId(), nn.getFSImage().getEditLog().getCurSegmentTxId());
            Assert.assertTrue(("Should not have finalized " + editsLog), editsLog.isInProgress());
            // do some edits
            Assert.assertTrue(fileSys.mkdirs(new Path("/edit-while-bn-down")));
            // start a new backup node
            backup = startBackupNode(conf, BACKUP, 1);
            testBNInSync(cluster, backup, 4);
            Assert.assertNotNull(backup.getNamesystem().getFileInfo("/edit-while-bn-down", false, false, false));
            // Trigger an unclean shutdown of the backup node. Backup node will not
            // unregister from the active when this is done simulating a node crash.
            backup.stop(false);
            // do some edits on the active. This should go through without failing.
            // This will verify that active is still up and can add entries to
            // master editlog.
            Assert.assertTrue(fileSys.mkdirs(new Path("/edit-while-bn-down-2")));
        } finally {
            TestBackupNode.LOG.info("Shutting down...");
            if (backup != null)
                backup.stop();

            if (fileSys != null)
                fileSys.close();

            if (cluster != null)
                cluster.shutdown();

        }
        assertStorageDirsMatch(cluster.getNameNode(), backup);
    }

    @Test
    public void testBackupNode() throws Exception {
        testCheckpoint(BACKUP);
    }

    /**
     * Verify that a file can be read both from NameNode and BackupNode.
     */
    @Test
    public void testCanReadData() throws IOException {
        Path file1 = new Path("/fileToRead.dat");
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = null;
        FileSystem fileSys = null;
        BackupNode backup = null;
        try {
            // Start NameNode and BackupNode
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(true).build();
            fileSys = cluster.getFileSystem();
            long txid = cluster.getNameNodeRpc().getTransactionID();
            backup = startBackupNode(conf, BACKUP, 1);
            waitCheckpointDone(cluster, txid);
            // Setup dual NameNode configuration for DataNodes
            String rpcAddrKeyPreffix = (DFSConfigKeys.DFS_NAMENODE_RPC_ADDRESS_KEY) + ".bnCluster";
            String nnAddr = cluster.getNameNode().getNameNodeAddressHostPortString();
            conf.get(DFS_NAMENODE_RPC_ADDRESS_KEY);
            String bnAddr = backup.getNameNodeAddressHostPortString();
            conf.set(DFS_NAMESERVICES, "bnCluster");
            conf.set(DFS_NAMESERVICE_ID, "bnCluster");
            conf.set(((DFSConfigKeys.DFS_HA_NAMENODES_KEY_PREFIX) + ".bnCluster"), "nnActive, nnBackup");
            conf.set((rpcAddrKeyPreffix + ".nnActive"), nnAddr);
            conf.set((rpcAddrKeyPreffix + ".nnBackup"), bnAddr);
            cluster.startDataNodes(conf, 3, true, REGULAR, null);
            DFSTestUtil.createFile(fileSys, file1, TestBackupNode.fileSize, TestBackupNode.fileSize, TestBackupNode.blockSize, ((short) (3)), TestBackupNode.seed);
            // Read the same file from file systems pointing to NN and BN
            FileSystem bnFS = FileSystem.get(new Path(("hdfs://" + bnAddr)).toUri(), conf);
            String nnData = DFSTestUtil.readFile(fileSys, file1);
            String bnData = DFSTestUtil.readFile(bnFS, file1);
            Assert.assertEquals("Data read from BackupNode and NameNode is not the same.", nnData, bnData);
        } catch (IOException e) {
            TestBackupNode.LOG.error("Error in TestBackupNode: ", e);
            Assert.assertTrue(e.getLocalizedMessage(), false);
        } finally {
            if (fileSys != null)
                fileSys.close();

            if (backup != null)
                backup.stop();

            if (cluster != null)
                cluster.shutdown();

        }
    }
}

