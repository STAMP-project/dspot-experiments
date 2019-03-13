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


import DFSConfigKeys.DFS_DATANODE_DATA_DIR_KEY;
import DFSConfigKeys.DFS_DATANODE_DUPLICATE_REPLICA_DELETION;
import DFSConfigKeys.DFS_DATANODE_SCAN_PERIOD_HOURS_KEY;
import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import RollingUpgradeAction.PREPARE;
import SafeModeAction.SAFEMODE_ENTER;
import StartupOption.REGULAR;
import StartupOption.UPGRADE;
import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.NodeType;
import org.apache.hadoop.hdfs.server.common.InconsistentFSStateException;
import org.apache.hadoop.hdfs.server.common.Storage;
import org.apache.hadoop.hdfs.server.common.StorageInfo;
import org.apache.hadoop.hdfs.server.namenode.TestParallelImageWrite;
import org.apache.hadoop.ipc.RemoteException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test ensures the appropriate response (successful or failure) from
 * the system when the system is upgraded under various storage state and
 * version conditions.
 */
public class TestDFSUpgrade {
    // TODO: Avoid hard-coding expected_txid. The test should be more robust.
    private static final int EXPECTED_TXID = 61;

    private static final Logger LOG = LoggerFactory.getLogger(TestDFSUpgrade.class.getName());

    private Configuration conf;

    private int testCounter = 0;

    private MiniDFSCluster cluster = null;

    /**
     * This test attempts to upgrade the NameNode and DataNode under
     * a number of valid and invalid conditions.
     */
    @Test(timeout = 60000)
    public void testUpgrade() throws Exception {
        File[] baseDirs;
        StorageInfo storageInfo = null;
        for (int numDirs = 1; numDirs <= 2; numDirs++) {
            conf = new HdfsConfiguration();
            conf = UpgradeUtilities.initializeStorageStateConf(numDirs, conf);
            String[] nameNodeDirs = conf.getStrings(DFS_NAMENODE_NAME_DIR_KEY);
            String[] dataNodeDirs = conf.getStrings(DFS_DATANODE_DATA_DIR_KEY);
            conf.setBoolean(DFS_DATANODE_DUPLICATE_REPLICA_DELETION, false);
            log("Normal NameNode upgrade", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            cluster = createCluster();
            // make sure that rolling upgrade cannot be started
            try {
                final DistributedFileSystem dfs = cluster.getFileSystem();
                dfs.setSafeMode(SAFEMODE_ENTER);
                dfs.rollingUpgrade(PREPARE);
                Assert.fail();
            } catch (RemoteException re) {
                Assert.assertEquals(InconsistentFSStateException.class.getName(), re.getClassName());
                TestDFSUpgrade.LOG.info("The exception is expected.", re);
            }
            checkNameNode(nameNodeDirs, TestDFSUpgrade.EXPECTED_TXID);
            if (numDirs > 1)
                TestParallelImageWrite.checkImages(cluster.getNamesystem(), numDirs);

            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            log("Normal DataNode upgrade", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            cluster = createCluster();
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "current");
            cluster.startDataNodes(conf, 1, false, REGULAR, null);
            checkDataNode(dataNodeDirs, UpgradeUtilities.getCurrentBlockPoolID(null));
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(dataNodeDirs);
            log("NameNode upgrade with existing previous dir", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            startNameNodeShouldFail(UPGRADE);
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            log("DataNode upgrade with existing previous dir", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            cluster = createCluster();
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "current");
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "previous");
            cluster.startDataNodes(conf, 1, false, REGULAR, null);
            checkDataNode(dataNodeDirs, UpgradeUtilities.getCurrentBlockPoolID(null));
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(dataNodeDirs);
            log("DataNode upgrade with future stored layout version in current", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            cluster = createCluster();
            baseDirs = UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "current");
            storageInfo = new StorageInfo(Integer.MIN_VALUE, UpgradeUtilities.getCurrentNamespaceID(cluster), UpgradeUtilities.getCurrentClusterID(cluster), UpgradeUtilities.getCurrentFsscTime(cluster), NodeType.DATA_NODE);
            UpgradeUtilities.createDataNodeVersionFile(baseDirs, storageInfo, UpgradeUtilities.getCurrentBlockPoolID(cluster), conf);
            startBlockPoolShouldFail(REGULAR, UpgradeUtilities.getCurrentBlockPoolID(null));
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(dataNodeDirs);
            log("DataNode upgrade with newer fsscTime in current", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            cluster = createCluster();
            baseDirs = UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "current");
            storageInfo = new StorageInfo(HdfsServerConstants.DATANODE_LAYOUT_VERSION, UpgradeUtilities.getCurrentNamespaceID(cluster), UpgradeUtilities.getCurrentClusterID(cluster), Long.MAX_VALUE, NodeType.DATA_NODE);
            UpgradeUtilities.createDataNodeVersionFile(baseDirs, storageInfo, UpgradeUtilities.getCurrentBlockPoolID(cluster), conf);
            // Ensure corresponding block pool failed to initialized
            startBlockPoolShouldFail(REGULAR, UpgradeUtilities.getCurrentBlockPoolID(null));
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(dataNodeDirs);
            log("NameNode upgrade with no edits file", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            deleteStorageFilesWithPrefix(nameNodeDirs, "edits_");
            startNameNodeShouldFail(UPGRADE);
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            log("NameNode upgrade with no image file", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            deleteStorageFilesWithPrefix(nameNodeDirs, "fsimage_");
            startNameNodeShouldFail(UPGRADE);
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            log("NameNode upgrade with corrupt version file", numDirs);
            baseDirs = UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            for (File f : baseDirs) {
                UpgradeUtilities.corruptFile(new File(f, "VERSION"), "layoutVersion".getBytes(Charsets.UTF_8), "xxxxxxxxxxxxx".getBytes(Charsets.UTF_8));
            }
            startNameNodeShouldFail(UPGRADE);
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            log("NameNode upgrade with old layout version in current", numDirs);
            baseDirs = UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            storageInfo = new StorageInfo(((Storage.LAST_UPGRADABLE_LAYOUT_VERSION) + 1), UpgradeUtilities.getCurrentNamespaceID(null), UpgradeUtilities.getCurrentClusterID(null), UpgradeUtilities.getCurrentFsscTime(null), NodeType.NAME_NODE);
            UpgradeUtilities.createNameNodeVersionFile(conf, baseDirs, storageInfo, UpgradeUtilities.getCurrentBlockPoolID(cluster));
            startNameNodeShouldFail(UPGRADE);
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            log("NameNode upgrade with future layout version in current", numDirs);
            baseDirs = UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            storageInfo = new StorageInfo(Integer.MIN_VALUE, UpgradeUtilities.getCurrentNamespaceID(null), UpgradeUtilities.getCurrentClusterID(null), UpgradeUtilities.getCurrentFsscTime(null), NodeType.NAME_NODE);
            UpgradeUtilities.createNameNodeVersionFile(conf, baseDirs, storageInfo, UpgradeUtilities.getCurrentBlockPoolID(cluster));
            startNameNodeShouldFail(UPGRADE);
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
        }// end numDir loop

        // One more check: normal NN upgrade with 4 directories, concurrent write
        int numDirs = 4;
        {
            conf = new HdfsConfiguration();
            conf.setInt(DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, (-1));
            conf.setBoolean(DFS_DATANODE_DUPLICATE_REPLICA_DELETION, false);
            conf = UpgradeUtilities.initializeStorageStateConf(numDirs, conf);
            String[] nameNodeDirs = conf.getStrings(DFS_NAMENODE_NAME_DIR_KEY);
            log("Normal NameNode upgrade", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            cluster = createCluster();
            // make sure that rolling upgrade cannot be started
            try {
                final DistributedFileSystem dfs = cluster.getFileSystem();
                dfs.setSafeMode(SAFEMODE_ENTER);
                dfs.rollingUpgrade(PREPARE);
                Assert.fail();
            } catch (RemoteException re) {
                Assert.assertEquals(InconsistentFSStateException.class.getName(), re.getClassName());
                TestDFSUpgrade.LOG.info("The exception is expected.", re);
            }
            checkNameNode(nameNodeDirs, TestDFSUpgrade.EXPECTED_TXID);
            TestParallelImageWrite.checkImages(cluster.getNamesystem(), numDirs);
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
        }
    }

    @Test(expected = IOException.class)
    public void testUpgradeFromPreUpgradeLVFails() throws IOException {
        // Upgrade from versions prior to Storage#LAST_UPGRADABLE_LAYOUT_VERSION
        // is not allowed
        Storage.checkVersionUpgradable(((Storage.LAST_PRE_UPGRADE_LAYOUT_VERSION) + 1));
        Assert.fail("Expected IOException is not thrown");
    }
}

