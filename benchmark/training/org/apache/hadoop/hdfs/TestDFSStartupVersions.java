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
import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import StartupOption.REGULAR;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants.NodeType;
import org.apache.hadoop.hdfs.server.common.StorageInfo;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test ensures the appropriate response (successful or failure) from
 * a Datanode when the system is started with differing version combinations.
 */
public class TestDFSStartupVersions {
    private static final Logger LOG = LoggerFactory.getLogger("org.apache.hadoop.hdfs.TestDFSStartupVersions");

    private MiniDFSCluster cluster = null;

    /**
     * Class used for initializing version information for tests
     */
    private static class StorageData {
        private final StorageInfo storageInfo;

        private final String blockPoolId;

        StorageData(int layoutVersion, int namespaceId, String clusterId, long cTime, String bpid) {
            storageInfo = new StorageInfo(layoutVersion, namespaceId, clusterId, cTime, NodeType.DATA_NODE);
            blockPoolId = bpid;
        }
    }

    /**
     * This test ensures the appropriate response (successful or failure) from
     * a Datanode when the system is started with differing version combinations.
     * <pre>
     * For each 3-tuple in the cross product
     *   ({oldLayoutVersion,currentLayoutVersion,futureLayoutVersion},
     *    {currentNamespaceId,incorrectNamespaceId},
     *    {pastFsscTime,currentFsscTime,futureFsscTime})
     *      1. Startup Namenode with version file containing
     *         (currentLayoutVersion,currentNamespaceId,currentFsscTime)
     *      2. Attempt to startup Datanode with version file containing
     *         this iterations version 3-tuple
     * </pre>
     */
    @Test(timeout = 300000)
    public void testVersions() throws Exception {
        UpgradeUtilities.initialize();
        Configuration conf = UpgradeUtilities.initializeStorageStateConf(1, new HdfsConfiguration());
        TestDFSStartupVersions.StorageData[] versions = initializeVersions();
        UpgradeUtilities.createNameNodeStorageDirs(conf.getStrings(DFS_NAMENODE_NAME_DIR_KEY), "current");
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).format(false).manageDataDfsDirs(false).manageNameDfsDirs(false).startupOption(REGULAR).build();
        TestDFSStartupVersions.StorageData nameNodeVersion = new TestDFSStartupVersions.StorageData(HdfsServerConstants.NAMENODE_LAYOUT_VERSION, UpgradeUtilities.getCurrentNamespaceID(cluster), UpgradeUtilities.getCurrentClusterID(cluster), UpgradeUtilities.getCurrentFsscTime(cluster), UpgradeUtilities.getCurrentBlockPoolID(cluster));
        log("NameNode version info", NodeType.NAME_NODE, null, nameNodeVersion);
        String bpid = UpgradeUtilities.getCurrentBlockPoolID(cluster);
        for (int i = 0; i < (versions.length); i++) {
            File[] storage = UpgradeUtilities.createDataNodeStorageDirs(conf.getStrings(DFS_DATANODE_DATA_DIR_KEY), "current");
            log("DataNode version info", NodeType.DATA_NODE, i, versions[i]);
            UpgradeUtilities.createDataNodeVersionFile(storage, versions[i].storageInfo, bpid, versions[i].blockPoolId, conf);
            try {
                cluster.startDataNodes(conf, 1, false, REGULAR, null);
            } catch (Exception ignore) {
                // Ignore.  The asserts below will check for problems.
                // ignore.printStackTrace();
            }
            Assert.assertTrue(((cluster.getNameNode()) != null));
            Assert.assertEquals(isVersionCompatible(nameNodeVersion, versions[i]), cluster.isDataNodeUp());
            cluster.shutdownDataNodes();
        }
    }
}

