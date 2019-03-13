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
package org.apache.hadoop.hdfs.server.datanode;


import java.io.File;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtilClient;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.test.GenericTestUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static DataNodeLayoutVersion.CURRENT_LAYOUT_VERSION;


/**
 * Ensure that the DataNode correctly handles rolling upgrade
 * finalize and rollback.
 */
public class TestDataNodeRollingUpgrade {
    private static final Logger LOG = LoggerFactory.getLogger(TestDataNodeRollingUpgrade.class);

    private static final short REPL_FACTOR = 1;

    private static final int BLOCK_SIZE = 1024 * 1024;

    private static final long FILE_SIZE = TestDataNodeRollingUpgrade.BLOCK_SIZE;

    private static final long SEED = 464384013L;

    Configuration conf;

    MiniDFSCluster cluster = null;

    DistributedFileSystem fs = null;

    DataNode dn0 = null;

    NameNode nn = null;

    String blockPoolId = null;

    @Test(timeout = 600000)
    public void testDatanodeRollingUpgradeWithFinalize() throws Exception {
        try {
            startCluster();
            rollingUpgradeAndFinalize();
            // Do it again
            rollingUpgradeAndFinalize();
        } finally {
            shutdownCluster();
        }
    }

    @Test(timeout = 600000)
    public void testDatanodeRUwithRegularUpgrade() throws Exception {
        try {
            startCluster();
            rollingUpgradeAndFinalize();
            MiniDFSCluster.DataNodeProperties dn = cluster.stopDataNode(0);
            cluster.restartNameNode(0, true, "-upgrade");
            cluster.restartDataNode(dn, true);
            cluster.waitActive();
            fs = cluster.getFileSystem(0);
            Path testFile3 = new Path((("/" + (GenericTestUtils.getMethodName())) + ".03.dat"));
            DFSTestUtil.createFile(fs, testFile3, TestDataNodeRollingUpgrade.FILE_SIZE, TestDataNodeRollingUpgrade.REPL_FACTOR, TestDataNodeRollingUpgrade.SEED);
            cluster.getFileSystem().finalizeUpgrade();
        } finally {
            shutdownCluster();
        }
    }

    @Test(timeout = 600000)
    public void testDatanodeRollingUpgradeWithRollback() throws Exception {
        try {
            startCluster();
            // Create files in DFS.
            Path testFile1 = new Path((("/" + (GenericTestUtils.getMethodName())) + ".01.dat"));
            DFSTestUtil.createFile(fs, testFile1, TestDataNodeRollingUpgrade.FILE_SIZE, TestDataNodeRollingUpgrade.REPL_FACTOR, TestDataNodeRollingUpgrade.SEED);
            String fileContents1 = DFSTestUtil.readFile(fs, testFile1);
            startRollingUpgrade();
            File blockFile = getBlockForFile(testFile1, true);
            File trashFile = getTrashFileForBlock(blockFile, false);
            deleteAndEnsureInTrash(testFile1, blockFile, trashFile);
            // Now perform a rollback to restore DFS to the pre-rollback state.
            rollbackRollingUpgrade();
            // Ensure that block was restored from trash
            ensureTrashRestored(blockFile, trashFile);
            // Ensure that files exist and restored file contents are the same.
            assert fs.exists(testFile1);
            String fileContents2 = DFSTestUtil.readFile(fs, testFile1);
            Assert.assertThat(fileContents1, Is.is(fileContents2));
        } finally {
            shutdownCluster();
        }
    }

    // Test DatanodeXceiver has correct peer-dataxceiver pairs for sending OOB message
    @Test(timeout = 600000)
    public void testDatanodePeersXceiver() throws Exception {
        try {
            startCluster();
            // Create files in DFS.
            String testFile1 = ("/" + (GenericTestUtils.getMethodName())) + ".01.dat";
            String testFile2 = ("/" + (GenericTestUtils.getMethodName())) + ".02.dat";
            String testFile3 = ("/" + (GenericTestUtils.getMethodName())) + ".03.dat";
            DFSClient client1 = new DFSClient(DFSUtilClient.getNNAddress(conf), conf);
            DFSClient client2 = new DFSClient(DFSUtilClient.getNNAddress(conf), conf);
            DFSClient client3 = new DFSClient(DFSUtilClient.getNNAddress(conf), conf);
            DFSOutputStream s1 = ((DFSOutputStream) (client1.create(testFile1, true)));
            DFSOutputStream s2 = ((DFSOutputStream) (client2.create(testFile2, true)));
            DFSOutputStream s3 = ((DFSOutputStream) (client3.create(testFile3, true)));
            byte[] toWrite = new byte[(1024 * 1024) * 8];
            Random rb = new Random(1111);
            rb.nextBytes(toWrite);
            s1.write(toWrite, 0, ((1024 * 1024) * 8));
            s1.flush();
            s2.write(toWrite, 0, ((1024 * 1024) * 8));
            s2.flush();
            s3.write(toWrite, 0, ((1024 * 1024) * 8));
            s3.flush();
            Assert.assertTrue(((dn0.getXferServer().getNumPeersXceiver()) == (dn0.getXferServer().getNumPeersXceiver())));
            s1.close();
            s2.close();
            s3.close();
            Assert.assertTrue(((dn0.getXferServer().getNumPeersXceiver()) == (dn0.getXferServer().getNumPeersXceiver())));
            client1.close();
            client2.close();
            client3.close();
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Support for layout version change with rolling upgrade was
     * added by HDFS-6800 and HDFS-6981.
     */
    @Test(timeout = 300000)
    public void testWithLayoutChangeAndFinalize() throws Exception {
        final long seed = 1611526157;
        try {
            startCluster();
            Path[] paths = new Path[3];
            File[] blockFiles = new File[3];
            // Create two files in DFS.
            for (int i = 0; i < 2; ++i) {
                paths[i] = new Path((((("/" + (GenericTestUtils.getMethodName())) + ".") + i) + ".dat"));
                DFSTestUtil.createFile(fs, paths[i], TestDataNodeRollingUpgrade.BLOCK_SIZE, ((short) (2)), seed);
            }
            startRollingUpgrade();
            // Delete the first file. The DN will save its block files in trash.
            blockFiles[0] = getBlockForFile(paths[0], true);
            File trashFile0 = getTrashFileForBlock(blockFiles[0], false);
            deleteAndEnsureInTrash(paths[0], blockFiles[0], trashFile0);
            // Restart the DN with a new layout version to trigger layout upgrade.
            TestDataNodeRollingUpgrade.LOG.info("Shutting down the Datanode");
            MiniDFSCluster.DataNodeProperties dnprop = cluster.stopDataNode(0);
            DFSTestUtil.addDataNodeLayoutVersion(((CURRENT_LAYOUT_VERSION) - 1), "Test Layout for TestDataNodeRollingUpgrade");
            TestDataNodeRollingUpgrade.LOG.info("Restarting the DataNode");
            cluster.restartDataNode(dnprop, true);
            cluster.waitActive();
            dn0 = cluster.getDataNodes().get(0);
            TestDataNodeRollingUpgrade.LOG.info("The DN has been restarted");
            Assert.assertFalse(trashFile0.exists());
            Assert.assertFalse(dn0.getStorage().getBPStorage(blockPoolId).isTrashAllowed(blockFiles[0]));
            // Ensure that the block file for the first file was moved from 'trash' to 'previous'.
            Assert.assertTrue(isBlockFileInPrevious(blockFiles[0]));
            Assert.assertFalse(isTrashRootPresent());
            // Delete the second file. Ensure that its block file is in previous.
            blockFiles[1] = getBlockForFile(paths[1], true);
            fs.delete(paths[1], false);
            Assert.assertTrue(isBlockFileInPrevious(blockFiles[1]));
            Assert.assertFalse(isTrashRootPresent());
            // Finalize and ensure that neither block file exists in trash or previous.
            finalizeRollingUpgrade();
            Assert.assertFalse(isTrashRootPresent());
            Assert.assertFalse(isBlockFileInPrevious(blockFiles[0]));
            Assert.assertFalse(isBlockFileInPrevious(blockFiles[1]));
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Support for layout version change with rolling upgrade was
     * added by HDFS-6800 and HDFS-6981.
     */
    @Test(timeout = 300000)
    public void testWithLayoutChangeAndRollback() throws Exception {
        final long seed = 1611526157;
        try {
            startCluster();
            Path[] paths = new Path[3];
            File[] blockFiles = new File[3];
            // Create two files in DFS.
            for (int i = 0; i < 2; ++i) {
                paths[i] = new Path((((("/" + (GenericTestUtils.getMethodName())) + ".") + i) + ".dat"));
                DFSTestUtil.createFile(fs, paths[i], TestDataNodeRollingUpgrade.BLOCK_SIZE, ((short) (1)), seed);
            }
            startRollingUpgrade();
            // Delete the first file. The DN will save its block files in trash.
            blockFiles[0] = getBlockForFile(paths[0], true);
            File trashFile0 = getTrashFileForBlock(blockFiles[0], false);
            deleteAndEnsureInTrash(paths[0], blockFiles[0], trashFile0);
            // Restart the DN with a new layout version to trigger layout upgrade.
            TestDataNodeRollingUpgrade.LOG.info("Shutting down the Datanode");
            MiniDFSCluster.DataNodeProperties dnprop = cluster.stopDataNode(0);
            DFSTestUtil.addDataNodeLayoutVersion(((CURRENT_LAYOUT_VERSION) - 1), "Test Layout for TestDataNodeRollingUpgrade");
            TestDataNodeRollingUpgrade.LOG.info("Restarting the DataNode");
            cluster.restartDataNode(dnprop, true);
            cluster.waitActive();
            dn0 = cluster.getDataNodes().get(0);
            TestDataNodeRollingUpgrade.LOG.info("The DN has been restarted");
            Assert.assertFalse(trashFile0.exists());
            Assert.assertFalse(dn0.getStorage().getBPStorage(blockPoolId).isTrashAllowed(blockFiles[0]));
            // Ensure that the block file for the first file was moved from 'trash' to 'previous'.
            Assert.assertTrue(isBlockFileInPrevious(blockFiles[0]));
            Assert.assertFalse(isTrashRootPresent());
            // Delete the second file. Ensure that its block file is in previous.
            blockFiles[1] = getBlockForFile(paths[1], true);
            fs.delete(paths[1], false);
            Assert.assertTrue(isBlockFileInPrevious(blockFiles[1]));
            Assert.assertFalse(isTrashRootPresent());
            // Create and delete a third file. Its block file should not be
            // in either trash or previous after deletion.
            paths[2] = new Path((("/" + (GenericTestUtils.getMethodName())) + ".2.dat"));
            DFSTestUtil.createFile(fs, paths[2], TestDataNodeRollingUpgrade.BLOCK_SIZE, ((short) (1)), seed);
            blockFiles[2] = getBlockForFile(paths[2], true);
            fs.delete(paths[2], false);
            Assert.assertFalse(isBlockFileInPrevious(blockFiles[2]));
            Assert.assertFalse(isTrashRootPresent());
            // Rollback and ensure that the first two file contents were restored.
            rollbackRollingUpgrade();
            for (int i = 0; i < 2; ++i) {
                byte[] actual = DFSTestUtil.readFileBuffer(fs, paths[i]);
                byte[] calculated = DFSTestUtil.calculateFileContentsFromSeed(seed, TestDataNodeRollingUpgrade.BLOCK_SIZE);
                Assert.assertArrayEquals(actual, calculated);
            }
            // And none of the block files must be in previous or trash.
            Assert.assertFalse(isTrashRootPresent());
            for (int i = 0; i < 3; ++i) {
                Assert.assertFalse(isBlockFileInPrevious(blockFiles[i]));
            }
        } finally {
            shutdownCluster();
        }
    }
}

