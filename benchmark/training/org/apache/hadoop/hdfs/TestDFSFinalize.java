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
import StartupOption.REGULAR;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test ensures the appropriate response from the system when
 * the system is finalized.
 */
public class TestDFSFinalize {
    private static final Logger LOG = LoggerFactory.getLogger("org.apache.hadoop.hdfs.TestDFSFinalize");

    private Configuration conf;

    private int testCounter = 0;

    private MiniDFSCluster cluster = null;

    /**
     * This test attempts to finalize the NameNode and DataNode.
     */
    @Test
    public void testFinalize() throws Exception {
        UpgradeUtilities.initialize();
        for (int numDirs = 1; numDirs <= 2; numDirs++) {
            /* This test requires that "current" directory not change after
            the upgrade. Actually it is ok for those contents to change.
            For now disabling block verification so that the contents are 
            not changed.
            Disable duplicate replica deletion as the test intentionally
            mirrors the contents of storage directories.
             */
            conf = new HdfsConfiguration();
            conf.setInt(DFS_DATANODE_SCAN_PERIOD_HOURS_KEY, (-1));
            conf.setBoolean(DFS_DATANODE_DUPLICATE_REPLICA_DELETION, false);
            conf = UpgradeUtilities.initializeStorageStateConf(numDirs, conf);
            String[] nameNodeDirs = conf.getStrings(DFS_NAMENODE_NAME_DIR_KEY);
            String[] dataNodeDirs = conf.getStrings(DFS_DATANODE_DATA_DIR_KEY);
            log("Finalize NN & DN with existing previous dir", numDirs);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "current");
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "previous");
            cluster = new MiniDFSCluster.Builder(conf).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).startupOption(REGULAR).build();
            cluster.finalizeCluster(conf);
            cluster.triggerBlockReports();
            // 1 second should be enough for asynchronous DN finalize
            Thread.sleep(1000);
            TestDFSFinalize.checkResult(nameNodeDirs, dataNodeDirs, null);
            log("Finalize NN & DN without existing previous dir", numDirs);
            cluster.finalizeCluster(conf);
            cluster.triggerBlockReports();
            // 1 second should be enough for asynchronous DN finalize
            Thread.sleep(1000);
            TestDFSFinalize.checkResult(nameNodeDirs, dataNodeDirs, null);
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(dataNodeDirs);
            log("Finalize NN & BP with existing previous dir", numDirs);
            String bpid = UpgradeUtilities.getCurrentBlockPoolID(cluster);
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "current");
            UpgradeUtilities.createNameNodeStorageDirs(nameNodeDirs, "previous");
            UpgradeUtilities.createDataNodeStorageDirs(dataNodeDirs, "current");
            UpgradeUtilities.createBlockPoolStorageDirs(dataNodeDirs, "current", bpid);
            UpgradeUtilities.createBlockPoolStorageDirs(dataNodeDirs, "previous", bpid);
            cluster = new MiniDFSCluster.Builder(conf).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).startupOption(REGULAR).build();
            cluster.finalizeCluster(conf);
            cluster.triggerBlockReports();
            // 1 second should be enough for asynchronous BP finalize
            Thread.sleep(1000);
            TestDFSFinalize.checkResult(nameNodeDirs, dataNodeDirs, bpid);
            log("Finalize NN & BP without existing previous dir", numDirs);
            cluster.finalizeCluster(conf);
            cluster.triggerBlockReports();
            // 1 second should be enough for asynchronous BP finalize
            Thread.sleep(1000);
            TestDFSFinalize.checkResult(nameNodeDirs, dataNodeDirs, bpid);
            cluster.shutdown();
            UpgradeUtilities.createEmptyDirs(nameNodeDirs);
            UpgradeUtilities.createEmptyDirs(dataNodeDirs);
        }// end numDir loop

    }
}

