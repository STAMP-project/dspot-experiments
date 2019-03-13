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
import DFSConfigKeys.DFS_DATANODE_SCAN_PERIOD_HOURS_KEY;
import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import StartupOption.ROLLBACK;
import StartupOption.UPGRADE;
import com.google.common.base.Charsets;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.hdfs.server.common.StorageInfo;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.hdfs.server.common.HdfsServerConstants.NodeType.DATA_NODE;
import static org.apache.hadoop.hdfs.server.common.HdfsServerConstants.NodeType.NAME_NODE;


/**
 * This test ensures the appropriate response (successful or failure) from
 * the system when the system is rolled back under various storage state and
 * version conditions.
 */
public class TestDFSRollback {
    private static final Logger LOG = LoggerFactory.getLogger("org.apache.hadoop.hdfs.TestDFSRollback");

    private Configuration conf;

    private int testCounter = 0;

    private MiniDFSCluster cluster = null;

    /**
     * This test attempts to rollback the NameNode and DataNode under
     * a number of valid and invalid conditions.
     */
    @Test
    public void testRollback() throws Exception {
        File[] baseDirs;
        UpgradeUtilities.initialize();
        StorageInfo storageInfo = null;
        for (int numDirs = 1; numDirs <= 2; numDirs++) {
            conf = new HdfsConfiguration();
            conf.setInt(DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, (-1));
            conf = UpgradeUtilities.initializeStorageStateConf(numDirs, conf);
            String[] nameNodeDirs = conf.getStrings(DFS_NAMENODE_NAME_DIR_KEY);
            String[] dataNodeDirs = conf.getStrings(DFS_DATANODE_DATA_DIR_KEY);
            log("Normal NameNode rollback", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            NameNode.doRollback(conf, false);
            checkResult(NodeType.NAME_NODE, nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            log("Normal DataNode rollback", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            NameNode.doRollback(conf, false);
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).dnStartupOption(ROLLBACK).build();
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "current");
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "previous");
            cluster.startDataNodes(conf, 1, false, ROLLBACK, null);
            checkResult(NodeType.DATA_NODE, dataNodeDirs);
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(dataNodeDirs);
            log("Normal BlockPool rollback", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            NameNode.doRollback(conf, false);
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).dnStartupOption(ROLLBACK).build();
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "current");
            UpgradeUtilities.createBlockPoolStorageDirs(dataNodeDirs, "current", UpgradeUtilities.getCurrentBlockPoolID(cluster));
            // Create a previous snapshot for the blockpool
            UpgradeUtilities.createBlockPoolStorageDirs(dataNodeDirs, "previous", UpgradeUtilities.getCurrentBlockPoolID(cluster));
            // Put newer layout version in current.
            storageInfo = new StorageInfo(((HdfsServerConstants.DATANODE_LAYOUT_VERSION) - 1), UpgradeUtilities.getCurrentNamespaceID(cluster), UpgradeUtilities.getCurrentClusterID(cluster), UpgradeUtilities.getCurrentFsscTime(cluster), DATA_NODE);
            // Overwrite VERSION file in the current directory of
            // volume directories and block pool slice directories
            // with a layout version from future.
            File[] dataCurrentDirs = new File[dataNodeDirs.length];
            for (int i = 0; i < (dataNodeDirs.length); i++) {
                dataCurrentDirs[i] = new File(new Path(((dataNodeDirs[i]) + "/current")).toString());
            }
            UpgradeUtilities.createDataNodeVersionFile(dataCurrentDirs, storageInfo, UpgradeUtilities.getCurrentBlockPoolID(cluster), conf);
            cluster.startDataNodes(conf, 1, false, ROLLBACK, null);
            Assert.assertTrue(cluster.isDataNodeUp());
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(dataNodeDirs);
            log("NameNode rollback without existing previous dir", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            startNameNodeShouldFail("None of the storage directories contain previous fs state");
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            log("DataNode rollback without existing previous dir", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).startupOption(UPGRADE).build();
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "current");
            cluster.startDataNodes(conf, 1, false, ROLLBACK, null);
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(dataNodeDirs);
            log("DataNode rollback with future stored layout version in previous", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            NameNode.doRollback(conf, false);
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).dnStartupOption(ROLLBACK).build();
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "current");
            baseDirs = UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "previous");
            storageInfo = new StorageInfo(Integer.MIN_VALUE, UpgradeUtilities.getCurrentNamespaceID(cluster), UpgradeUtilities.getCurrentClusterID(cluster), UpgradeUtilities.getCurrentFsscTime(cluster), DATA_NODE);
            UpgradeUtilities.createDataNodeVersionFile(baseDirs, storageInfo, UpgradeUtilities.getCurrentBlockPoolID(cluster), conf);
            startBlockPoolShouldFail(ROLLBACK, cluster.getNamesystem().getBlockPoolId());
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(dataNodeDirs);
            log("DataNode rollback with newer fsscTime in previous", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            NameNode.doRollback(conf, false);
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).dnStartupOption(ROLLBACK).build();
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "current");
            baseDirs = UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "previous");
            storageInfo = new StorageInfo(HdfsServerConstants.DATANODE_LAYOUT_VERSION, UpgradeUtilities.getCurrentNamespaceID(cluster), UpgradeUtilities.getCurrentClusterID(cluster), Long.MAX_VALUE, DATA_NODE);
            UpgradeUtilities.createDataNodeVersionFile(baseDirs, storageInfo, UpgradeUtilities.getCurrentBlockPoolID(cluster), conf);
            startBlockPoolShouldFail(ROLLBACK, cluster.getNamesystem().getBlockPoolId());
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(dataNodeDirs);
            log("NameNode rollback with no edits file", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            baseDirs = UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            deleteMatchingFiles(baseDirs, "edits.*");
            startNameNodeShouldFail("Gap in transactions");
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            log("NameNode rollback with no image file", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            baseDirs = UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            deleteMatchingFiles(baseDirs, "fsimage_.*");
            startNameNodeShouldFail("No valid image files found");
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            log("NameNode rollback with corrupt version file", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            baseDirs = UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            for (File f : baseDirs) {
                UpgradeUtilities.corruptFile(new File(f, "VERSION"), "layoutVersion".getBytes(Charsets.UTF_8), "xxxxxxxxxxxxx".getBytes(Charsets.UTF_8));
            }
            startNameNodeShouldFail("file VERSION has layoutVersion missing");
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            log("NameNode rollback with old layout version in previous", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            baseDirs = UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            storageInfo = new StorageInfo(1, UpgradeUtilities.getCurrentNamespaceID(null), UpgradeUtilities.getCurrentClusterID(null), UpgradeUtilities.getCurrentFsscTime(null), NAME_NODE);
            UpgradeUtilities.createNameNodeVersionFile(conf, baseDirs, storageInfo, UpgradeUtilities.getCurrentBlockPoolID(cluster));
            startNameNodeShouldFail("Cannot rollback to storage version 1 using this version");
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
        }// end numDir loop

    }
}

