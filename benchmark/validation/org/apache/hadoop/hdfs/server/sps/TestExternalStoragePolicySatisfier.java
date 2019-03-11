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
package org.apache.hadoop.hdfs.server.sps;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_DATANODE_BLOCK_PINNING_ENABLED;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY;
import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY;
import DFSConfigKeys.DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY;
import DFSConfigKeys.DFS_STORAGE_POLICY_ENABLED_KEY;
import DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_RECHECK_TIMEOUT_MILLIS_KEY;
import DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_SELF_RETRY_TIMEOUT_MILLIS_KEY;
import HdfsConstants.ONESSD_STORAGE_POLICY_NAME;
import HdfsServerConstants.MOVER_ID_PATH;
import StorageType.ARCHIVE;
import StorageType.DISK;
import StorageType.SSD;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.NameNodeProxies;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.balancer.NameNodeConnector;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.namenode.FSEditLog;
import org.apache.hadoop.hdfs.server.namenode.INode;
import org.apache.hadoop.hdfs.server.namenode.sps.BlockMovementListener;
import org.apache.hadoop.hdfs.server.namenode.sps.BlockStorageMovementAttemptedItems;
import org.apache.hadoop.hdfs.server.namenode.sps.StoragePolicySatisfier;
import org.apache.hadoop.minikdc.MiniKdc;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.GenericTestUtils.LogCapturer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests the external sps service plugins.
 */
public class TestExternalStoragePolicySatisfier {
    private static final String ONE_SSD = "ONE_SSD";

    private static final String COLD = "COLD";

    private StorageType[][] allDiskTypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK } };

    private File keytabFile;

    private String principal;

    private MiniKdc kdc;

    private File baseDir;

    private NameNodeConnector nnc;

    private StoragePolicySatisfier externalSps;

    private ExternalSPSContext externalCtxt;

    private DistributedFileSystem dfs = null;

    private MiniDFSCluster hdfsCluster = null;

    private Configuration config = null;

    private static final int NUM_OF_DATANODES = 3;

    private static final int STORAGES_PER_DATANODE = 2;

    private static final long CAPACITY = ((2 * 256) * 1024) * 1024;

    private static final String FILE = "/testMoveToSatisfyStoragePolicy";

    private static final int DEFAULT_BLOCK_SIZE = 1024;

    private static final Logger LOG = LoggerFactory.getLogger(TestExternalStoragePolicySatisfier.class);

    /**
     * Test SPS runs fine when logging in with a keytab in kerberized env. Reusing
     * testWhenStoragePolicySetToALLSSD here for basic functionality testing.
     */
    @Test(timeout = 300000)
    public void testWithKeytabs() throws Exception {
        try {
            initSecureConf(getConf());
            final UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytabFile.getAbsolutePath());
            ugi.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    // verify that sps runs Ok.
                    testWhenStoragePolicySetToALLSSD();
                    // verify that UGI was logged in using keytab.
                    Assert.assertTrue(UserGroupInformation.isLoginKeytabBased());
                    return null;
                }
            });
        } finally {
            // Reset UGI so that other tests are not affected.
            UserGroupInformation.reset();
            UserGroupInformation.setConfiguration(new Configuration());
        }
    }

    /**
     * Test verifies that SPS call will throw exception if the call Q exceeds
     * OutstandingQueueLimit value.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testOutstandingQueueLimitExceeds() throws Exception {
        try {
            getConf().setInt(DFSConfigKeys.DFS_SPS_MAX_OUTSTANDING_PATHS_KEY, 3);
            createCluster();
            List<String> files = new ArrayList<>();
            files.add(TestExternalStoragePolicySatisfier.FILE);
            DistributedFileSystem fs = getFS();
            // stops sps to make the SPS Q with many outstanding requests.
            externalSps.stopGracefully();
            // Creates 4 more files. Send all of them for satisfying the storage
            // policy together.
            for (int i = 0; i < 3; i++) {
                String file1 = "/testOutstandingQueueLimitExceeds_" + i;
                files.add(file1);
                writeContent(file1);
                fs.satisfyStoragePolicy(new Path(file1));
            }
            String fileExceeds = "/testOutstandingQueueLimitExceeds_" + 4;
            files.add(fileExceeds);
            writeContent(fileExceeds);
            try {
                fs.satisfyStoragePolicy(new Path(fileExceeds));
                Assert.fail(("Should throw exception as it exceeds " + "outstanding SPS call Q limit"));
            } catch (IOException ioe) {
                GenericTestUtils.assertExceptionContains("Outstanding satisfier queue limit: 3 exceeded, try later!", ioe);
            }
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests to verify that SPS should be able to start when the Mover ID file
     * is not being hold by a Mover. This can be the case when Mover exits
     * ungracefully without deleting the ID file from HDFS.
     */
    @Test(timeout = 300000)
    public void testWhenMoverExitsWithoutDeleteMoverIDFile() throws IOException {
        try {
            createCluster();
            // Simulate the case by creating MOVER_ID file
            DFSTestUtil.createFile(getCluster().getFileSystem(), MOVER_ID_PATH, 0, ((short) (1)), 0);
            restartNamenode();
            boolean running = externalCtxt.isRunning();
            Assert.assertTrue(("SPS should be running as " + "no Mover really running"), running);
        } finally {
            shutdownCluster();
        }
    }

    @Test(timeout = 300000)
    public void testWhenStoragePolicySetToCOLD() throws Exception {
        try {
            createCluster();
            doTestWhenStoragePolicySetToCOLD();
        } finally {
            shutdownCluster();
        }
    }

    @Test(timeout = 300000)
    public void testWhenStoragePolicySetToALLSSD() throws Exception {
        try {
            createCluster();
            // Change policy to ALL_SSD
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), "ALL_SSD");
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK } };
            // Making sure SDD based nodes added to cluster. Adding SSD based
            // datanodes.
            startAdditionalDNs(config, 3, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, newtypes, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY, hdfsCluster);
            dfs.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            hdfsCluster.triggerHeartbeats();
            // Wait till StorgePolicySatisfier Identified that block to move to SSD
            // areas
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, SSD, 3, 30000, dfs);
        } finally {
            shutdownCluster();
        }
    }

    @Test(timeout = 300000)
    public void testWhenStoragePolicySetToONESSD() throws Exception {
        try {
            createCluster();
            // Change policy to ONE_SSD
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), TestExternalStoragePolicySatisfier.ONE_SSD);
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK } };
            // Making sure SDD based nodes added to cluster. Adding SSD based
            // datanodes.
            startAdditionalDNs(config, 1, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, newtypes, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY, hdfsCluster);
            dfs.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            hdfsCluster.triggerHeartbeats();
            // Wait till StorgePolicySatisfier Identified that block to move to SSD
            // areas
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, SSD, 1, 30000, dfs);
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, DISK, 2, 30000, dfs);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests to verify that the block storage movement report will be propagated
     * to Namenode via datanode heartbeat.
     */
    @Test(timeout = 300000)
    public void testBlksStorageMovementAttemptFinishedReport() throws Exception {
        try {
            createCluster();
            // Change policy to ONE_SSD
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), TestExternalStoragePolicySatisfier.ONE_SSD);
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK } };
            // Making sure SDD based nodes added to cluster. Adding SSD based
            // datanodes.
            startAdditionalDNs(config, 1, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, newtypes, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY, hdfsCluster);
            dfs.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            hdfsCluster.triggerHeartbeats();
            // Wait till the block is moved to SSD areas
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, SSD, 1, 30000, dfs);
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, DISK, 2, 30000, dfs);
            waitForBlocksMovementAttemptReport(1, 30000);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests to verify that multiple files are giving to satisfy storage policy
     * and should work well altogether.
     */
    @Test(timeout = 300000)
    public void testMultipleFilesForSatisfyStoragePolicy() throws Exception {
        try {
            createCluster();
            List<String> files = new ArrayList<>();
            files.add(TestExternalStoragePolicySatisfier.FILE);
            // Creates 4 more files. Send all of them for satisfying the storage
            // policy together.
            for (int i = 0; i < 4; i++) {
                String file1 = "/testMoveWhenStoragePolicyNotSatisfying_" + i;
                files.add(file1);
                writeContent(file1);
            }
            // Change policy to ONE_SSD
            for (String fileName : files) {
                dfs.setStoragePolicy(new Path(fileName), TestExternalStoragePolicySatisfier.ONE_SSD);
                dfs.satisfyStoragePolicy(new Path(fileName));
            }
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK } };
            // Making sure SDD based nodes added to cluster. Adding SSD based
            // datanodes.
            startAdditionalDNs(config, 1, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, newtypes, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY, hdfsCluster);
            hdfsCluster.triggerHeartbeats();
            for (String fileName : files) {
                // Wait till the block is moved to SSD areas
                DFSTestUtil.waitExpectedStorageType(fileName, SSD, 1, 30000, dfs);
                DFSTestUtil.waitExpectedStorageType(fileName, DISK, 2, 30000, dfs);
            }
            waitForBlocksMovementAttemptReport(files.size(), 30000);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests to verify hdfsAdmin.satisfyStoragePolicy works well for file.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testSatisfyFileWithHdfsAdmin() throws Exception {
        try {
            createCluster();
            HdfsAdmin hdfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(config), config);
            // Change policy to COLD
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), TestExternalStoragePolicySatisfier.COLD);
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE } };
            startAdditionalDNs(config, 3, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, newtypes, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY, hdfsCluster);
            hdfsAdmin.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            hdfsCluster.triggerHeartbeats();
            // Wait till namenode notified about the block location details
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, ARCHIVE, 3, 30000, dfs);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests to verify hdfsAdmin.satisfyStoragePolicy works well for dir.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testSatisfyDirWithHdfsAdmin() throws Exception {
        try {
            createCluster();
            HdfsAdmin hdfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(config), config);
            final String subDir = "/subDir";
            final String subFile1 = subDir + "/subFile1";
            final String subDir2 = subDir + "/subDir2";
            final String subFile2 = subDir2 + "/subFile2";
            dfs.mkdirs(new Path(subDir));
            writeContent(subFile1);
            dfs.mkdirs(new Path(subDir2));
            writeContent(subFile2);
            // Change policy to COLD
            dfs.setStoragePolicy(new Path(subDir), TestExternalStoragePolicySatisfier.ONE_SSD);
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK } };
            startAdditionalDNs(config, 1, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, newtypes, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY, hdfsCluster);
            hdfsAdmin.satisfyStoragePolicy(new Path(subDir));
            hdfsCluster.triggerHeartbeats();
            // take effect for the file in the directory.
            DFSTestUtil.waitExpectedStorageType(subFile1, SSD, 1, 30000, dfs);
            DFSTestUtil.waitExpectedStorageType(subFile1, DISK, 2, 30000, dfs);
            // take no effect for the sub-dir's file in the directory.
            DFSTestUtil.waitExpectedStorageType(subFile2, SSD, 1, 30000, dfs);
            DFSTestUtil.waitExpectedStorageType(subFile2, DISK, 2, 30000, dfs);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests to verify hdfsAdmin.satisfyStoragePolicy exceptions.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testSatisfyWithExceptions() throws Exception {
        try {
            createCluster();
            final String nonExistingFile = "/noneExistingFile";
            hdfsCluster.getConfiguration(0).setBoolean(DFS_STORAGE_POLICY_ENABLED_KEY, false);
            restartNamenode();
            HdfsAdmin hdfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(config), config);
            try {
                hdfsAdmin.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
                Assert.fail(String.format(("Should failed to satisfy storage policy " + "for %s since %s is set to false."), TestExternalStoragePolicySatisfier.FILE, DFSConfigKeys.DFS_STORAGE_POLICY_ENABLED_KEY));
            } catch (IOException e) {
                GenericTestUtils.assertExceptionContains(String.format("Failed to satisfy storage policy since %s is set to false.", DFSConfigKeys.DFS_STORAGE_POLICY_ENABLED_KEY), e);
            }
            hdfsCluster.getConfiguration(0).setBoolean(DFS_STORAGE_POLICY_ENABLED_KEY, true);
            restartNamenode();
            hdfsAdmin = new HdfsAdmin(FileSystem.getDefaultUri(config), config);
            try {
                hdfsAdmin.satisfyStoragePolicy(new Path(nonExistingFile));
                Assert.fail(("Should throw FileNotFoundException for " + nonExistingFile));
            } catch (FileNotFoundException e) {
            }
            try {
                hdfsAdmin.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
                hdfsAdmin.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            } catch (Exception e) {
                Assert.fail(String.format(("Allow to invoke mutlipe times " + ("#satisfyStoragePolicy() api for a path %s , internally just " + "skipping addtion to satisfy movement queue.")), TestExternalStoragePolicySatisfier.FILE));
            }
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests to verify that for the given path, some of the blocks or block src
     * locations(src nodes) under the given path will be scheduled for block
     * movement.
     *
     * For example, there are two block for a file:
     *
     * File1 => blk_1[locations=A(DISK),B(DISK),C(DISK)],
     * blk_2[locations=A(DISK),B(DISK),C(DISK)]. Now, set storage policy to COLD.
     * Only one datanode is available with storage type ARCHIVE, say D.
     *
     * SPS will schedule block movement to the coordinator node with the details,
     * blk_1[move A(DISK) -> D(ARCHIVE)], blk_2[move A(DISK) -> D(ARCHIVE)].
     */
    @Test(timeout = 300000)
    public void testWhenOnlyFewTargetDatanodeAreAvailableToSatisfyStoragePolicy() throws Exception {
        try {
            createCluster();
            // Change policy to COLD
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), TestExternalStoragePolicySatisfier.COLD);
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE } };
            // Adding ARCHIVE based datanodes.
            startAdditionalDNs(config, 1, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, newtypes, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY, hdfsCluster);
            dfs.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            hdfsCluster.triggerHeartbeats();
            // Wait till StorgePolicySatisfier identified that block to move to
            // ARCHIVE area.
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, ARCHIVE, 1, 30000, dfs);
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, DISK, 2, 30000, dfs);
            waitForBlocksMovementAttemptReport(1, 30000);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests to verify that for the given path, no blocks or block src
     * locations(src nodes) under the given path will be scheduled for block
     * movement as there are no available datanode with required storage type.
     *
     * For example, there are two block for a file:
     *
     * File1 => blk_1[locations=A(DISK),B(DISK),C(DISK)],
     * blk_2[locations=A(DISK),B(DISK),C(DISK)]. Now, set storage policy to COLD.
     * No datanode is available with storage type ARCHIVE.
     *
     * SPS won't schedule any block movement for this path.
     */
    @Test(timeout = 300000)
    public void testWhenNoTargetDatanodeToSatisfyStoragePolicy() throws Exception {
        try {
            createCluster();
            // Change policy to COLD
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), TestExternalStoragePolicySatisfier.COLD);
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.DISK } };
            // Adding DISK based datanodes
            startAdditionalDNs(config, 1, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, newtypes, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY, hdfsCluster);
            dfs.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            hdfsCluster.triggerHeartbeats();
            // No block movement will be scheduled as there is no target node
            // available with the required storage type.
            waitForAttemptedItems(1, 30000);
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, DISK, 3, 30000, dfs);
            // Since there is no target node the item will get timed out and then
            // re-attempted.
            waitForAttemptedItems(1, 30000);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Test to verify that satisfy worker can't move blocks. If the given block is
     * pinned it shouldn't be considered for retries.
     */
    @Test(timeout = 120000)
    public void testMoveWithBlockPinning() throws Exception {
        try {
            config.setBoolean(DFS_DATANODE_BLOCK_PINNING_ENABLED, true);
            hdfsCluster = startCluster(config, allDiskTypes, 3, 2, TestExternalStoragePolicySatisfier.CAPACITY);
            hdfsCluster.waitActive();
            dfs = hdfsCluster.getFileSystem();
            // create a file with replication factor 3 and mark 2 pinned block
            // locations.
            final String file1 = createFileAndSimulateFavoredNodes(2);
            // Change policy to COLD
            dfs.setStoragePolicy(new Path(file1), TestExternalStoragePolicySatisfier.COLD);
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE }, new StorageType[]{ StorageType.ARCHIVE, StorageType.ARCHIVE } };
            // Adding DISK based datanodes
            startAdditionalDNs(config, 3, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, newtypes, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY, hdfsCluster);
            dfs.satisfyStoragePolicy(new Path(file1));
            hdfsCluster.triggerHeartbeats();
            // No block movement will be scheduled as there is no target node
            // available with the required storage type.
            waitForAttemptedItems(1, 30000);
            waitForBlocksMovementAttemptReport(1, 30000);
            DFSTestUtil.waitExpectedStorageType(file1, ARCHIVE, 1, 30000, dfs);
            DFSTestUtil.waitExpectedStorageType(file1, DISK, 2, 30000, dfs);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests to verify that for the given path, only few of the blocks or block
     * src locations(src nodes) under the given path will be scheduled for block
     * movement.
     *
     * For example, there are two block for a file:
     *
     * File1 => two blocks and default storage policy(HOT).
     * blk_1[locations=A(DISK),B(DISK),C(DISK),D(DISK),E(DISK)],
     * blk_2[locations=A(DISK),B(DISK),C(DISK),D(DISK),E(DISK)].
     *
     * Now, set storage policy to COLD.
     * Only two Dns are available with expected storage type ARCHIVE, say A, E.
     *
     * SPS will schedule block movement to the coordinator node with the details,
     * blk_1[move A(DISK) -> A(ARCHIVE), move E(DISK) -> E(ARCHIVE)],
     * blk_2[move A(DISK) -> A(ARCHIVE), move E(DISK) -> E(ARCHIVE)].
     */
    @Test(timeout = 300000)
    public void testWhenOnlyFewSourceNodesHaveMatchingTargetNodes() throws Exception {
        try {
            int numOfDns = 5;
            config.setLong("dfs.block.size", 1024);
            allDiskTypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE } };
            hdfsCluster = startCluster(config, allDiskTypes, numOfDns, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY);
            dfs = hdfsCluster.getFileSystem();
            writeContent(TestExternalStoragePolicySatisfier.FILE, ((short) (5)));
            // Change policy to COLD
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), TestExternalStoragePolicySatisfier.COLD);
            dfs.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            hdfsCluster.triggerHeartbeats();
            // Wait till StorgePolicySatisfier identified that block to move to
            // ARCHIVE area.
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, ARCHIVE, 2, 30000, dfs);
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, DISK, 3, 30000, dfs);
            waitForBlocksMovementAttemptReport(1, 30000);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests that moving block storage with in the same datanode. Let's say we
     * have DN1[DISK,ARCHIVE], DN2[DISK, SSD], DN3[DISK,RAM_DISK] when
     * storagepolicy set to ONE_SSD and request satisfyStoragePolicy, then block
     * should move to DN2[SSD] successfully.
     */
    @Test(timeout = 300000)
    public void testBlockMoveInSameDatanodeWithONESSD() throws Exception {
        StorageType[][] diskTypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.RAM_DISK } };
        config.setLong("dfs.block.size", TestExternalStoragePolicySatisfier.DEFAULT_BLOCK_SIZE);
        try {
            hdfsCluster = startCluster(config, diskTypes, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY);
            dfs = hdfsCluster.getFileSystem();
            writeContent(TestExternalStoragePolicySatisfier.FILE);
            // Change policy to ONE_SSD
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), TestExternalStoragePolicySatisfier.ONE_SSD);
            dfs.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            hdfsCluster.triggerHeartbeats();
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, SSD, 1, 30000, dfs);
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, DISK, 2, 30000, dfs);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests that moving block storage with in the same datanode and remote node.
     * Let's say we have DN1[DISK,ARCHIVE], DN2[ARCHIVE, SSD], DN3[DISK,DISK],
     * DN4[DISK,DISK] when storagepolicy set to WARM and request
     * satisfyStoragePolicy, then block should move to DN1[ARCHIVE] and
     * DN2[ARCHIVE] successfully.
     */
    @Test(timeout = 300000)
    public void testBlockMoveInSameAndRemoteDatanodesWithWARM() throws Exception {
        StorageType[][] diskTypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.ARCHIVE, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.DISK } };
        config.setLong("dfs.block.size", TestExternalStoragePolicySatisfier.DEFAULT_BLOCK_SIZE);
        try {
            hdfsCluster = startCluster(config, diskTypes, diskTypes.length, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY);
            dfs = hdfsCluster.getFileSystem();
            writeContent(TestExternalStoragePolicySatisfier.FILE);
            // Change policy to WARM
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), "WARM");
            dfs.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            hdfsCluster.triggerHeartbeats();
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, DISK, 1, 30000, dfs);
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, ARCHIVE, 2, 30000, dfs);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * If replica with expected storage type already exist in source DN then that
     * DN should be skipped.
     */
    @Test(timeout = 300000)
    public void testSPSWhenReplicaWithExpectedStorageAlreadyAvailableInSource() throws Exception {
        StorageType[][] diskTypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE } };
        try {
            hdfsCluster = startCluster(config, diskTypes, diskTypes.length, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY);
            dfs = hdfsCluster.getFileSystem();
            // 1. Write two replica on disk
            DFSTestUtil.createFile(dfs, new Path(TestExternalStoragePolicySatisfier.FILE), TestExternalStoragePolicySatisfier.DEFAULT_BLOCK_SIZE, ((short) (2)), 0);
            // 2. Change policy to COLD, so third replica will be written to ARCHIVE.
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), "COLD");
            // 3.Change replication factor to 3.
            dfs.setReplication(new Path(TestExternalStoragePolicySatisfier.FILE), ((short) (3)));
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, DISK, 2, 30000, dfs);
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, ARCHIVE, 1, 30000, dfs);
            // 4. Change policy to HOT, so we can move the all block to DISK.
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), "HOT");
            // 4. Satisfy the policy.
            dfs.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            // 5. Block should move successfully .
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, DISK, 3, 30000, dfs);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests that movements should not be assigned when there is no space in
     * target DN.
     */
    @Test(timeout = 300000)
    public void testChooseInSameDatanodeWithONESSDShouldNotChooseIfNoSpace() throws Exception {
        StorageType[][] diskTypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.DISK } };
        config.setLong("dfs.block.size", (2 * (TestExternalStoragePolicySatisfier.DEFAULT_BLOCK_SIZE)));
        long dnCapacity = (1024 * (TestExternalStoragePolicySatisfier.DEFAULT_BLOCK_SIZE)) + ((2 * (TestExternalStoragePolicySatisfier.DEFAULT_BLOCK_SIZE)) - 1);
        try {
            hdfsCluster = startCluster(config, diskTypes, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, dnCapacity);
            dfs = hdfsCluster.getFileSystem();
            writeContent(TestExternalStoragePolicySatisfier.FILE);
            // Change policy to ONE_SSD
            dfs.setStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE), TestExternalStoragePolicySatisfier.ONE_SSD);
            Path filePath = new Path("/testChooseInSameDatanode");
            final FSDataOutputStream out = dfs.create(filePath, false, 100, ((short) (1)), (2 * (TestExternalStoragePolicySatisfier.DEFAULT_BLOCK_SIZE)));
            try {
                dfs.setStoragePolicy(filePath, TestExternalStoragePolicySatisfier.ONE_SSD);
                // Try to fill up SSD part by writing content
                long remaining = (dfs.getStatus().getRemaining()) / (3 * 2);
                for (int i = 0; i < remaining; i++) {
                    out.write(i);
                }
            } finally {
                out.close();
            }
            hdfsCluster.triggerHeartbeats();
            ArrayList<DataNode> dataNodes = hdfsCluster.getDataNodes();
            // Temporarily disable heart beats, so that we can assert whether any
            // items schedules for DNs even though DN's does not have space to write.
            // Disabling heart beats can keep scheduled items on DatanodeDescriptor
            // itself.
            for (DataNode dataNode : dataNodes) {
                DataNodeTestUtils.setHeartbeatsDisabledForTests(dataNode, true);
            }
            dfs.satisfyStoragePolicy(new Path(TestExternalStoragePolicySatisfier.FILE));
            // Wait for items to be processed
            waitForAttemptedItems(1, 30000);
            // Enable heart beats now
            for (DataNode dataNode : dataNodes) {
                DataNodeTestUtils.setHeartbeatsDisabledForTests(dataNode, false);
            }
            hdfsCluster.triggerHeartbeats();
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, DISK, 3, 30000, dfs);
            DFSTestUtil.waitExpectedStorageType(TestExternalStoragePolicySatisfier.FILE, SSD, 0, 30000, dfs);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Tests that Xattrs should be cleaned if satisfy storage policy called on EC
     * file with unsuitable storage policy set.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testSPSShouldNotLeakXattrIfSatisfyStoragePolicyCallOnECFiles() throws Exception {
        StorageType[][] diskTypes = new StorageType[][]{ new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.SSD, StorageType.DISK }, new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.SSD } };
        int defaultStripedBlockSize = (StripedFileTestUtil.getDefaultECPolicy().getCellSize()) * 4;
        config.setLong(DFS_BLOCK_SIZE_KEY, defaultStripedBlockSize);
        config.setLong(DFS_HEARTBEAT_INTERVAL_KEY, 1L);
        config.setLong(DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY, 1L);
        config.setBoolean(DFS_NAMENODE_REDUNDANCY_CONSIDERLOAD_KEY, false);
        try {
            hdfsCluster = startCluster(config, diskTypes, diskTypes.length, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY);
            dfs = hdfsCluster.getFileSystem();
            dfs.enableErasureCodingPolicy(StripedFileTestUtil.getDefaultECPolicy().getName());
            // set "/foo" directory with ONE_SSD storage policy.
            ClientProtocol client = NameNodeProxies.createProxy(config, hdfsCluster.getFileSystem(0).getUri(), ClientProtocol.class).getProxy();
            String fooDir = "/foo";
            client.mkdirs(fooDir, new FsPermission(((short) (777))), true);
            // set an EC policy on "/foo" directory
            client.setErasureCodingPolicy(fooDir, StripedFileTestUtil.getDefaultECPolicy().getName());
            // write file to fooDir
            final String testFile = "/foo/bar";
            long fileLen = 20 * defaultStripedBlockSize;
            DFSTestUtil.createFile(dfs, new Path(testFile), fileLen, ((short) (3)), 0);
            // ONESSD is unsuitable storage policy on EC files
            client.setStoragePolicy(fooDir, ONESSD_STORAGE_POLICY_NAME);
            dfs.satisfyStoragePolicy(new Path(testFile));
            // Thread.sleep(9000); // To make sure SPS triggered
            // verify storage types and locations
            LocatedBlocks locatedBlocks = client.getBlockLocations(testFile, 0, fileLen);
            for (LocatedBlock lb : locatedBlocks.getLocatedBlocks()) {
                for (StorageType type : lb.getStorageTypes()) {
                    Assert.assertEquals(DISK, type);
                }
            }
            // Make sure satisfy xattr has been removed.
            DFSTestUtil.waitForXattrRemoved(testFile, HdfsServerConstants.XATTR_SATISFY_STORAGE_POLICY, hdfsCluster.getNamesystem(), 30000);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Test SPS with empty file.
     * 1. Create one empty file.
     * 2. Call satisfyStoragePolicy for empty file.
     * 3. SPS should skip this file and xattr should not be added for empty file.
     */
    @Test(timeout = 300000)
    public void testSPSWhenFileLengthIsZero() throws Exception {
        try {
            hdfsCluster = startCluster(config, allDiskTypes, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY);
            hdfsCluster.waitActive();
            DistributedFileSystem fs = hdfsCluster.getFileSystem();
            Path filePath = new Path("/zeroSizeFile");
            DFSTestUtil.createFile(fs, filePath, 0, ((short) (1)), 0);
            FSEditLog editlog = hdfsCluster.getNameNode().getNamesystem().getEditLog();
            long lastWrittenTxId = editlog.getLastWrittenTxId();
            fs.satisfyStoragePolicy(filePath);
            Assert.assertEquals("Xattr should not be added for the file", lastWrittenTxId, editlog.getLastWrittenTxId());
            INode inode = hdfsCluster.getNameNode().getNamesystem().getFSDirectory().getINode(filePath.toString());
            Assert.assertTrue("XAttrFeature should be null for file", ((inode.getXAttrFeature()) == null));
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Test SPS for low redundant file blocks.
     * 1. Create cluster with 3 datanode.
     * 1. Create one file with 3 replica.
     * 2. Set policy and call satisfyStoragePolicy for file.
     * 3. Stop NameNode and Datanodes.
     * 4. Start NameNode with 2 datanode and wait for block movement.
     * 5. Start third datanode.
     * 6. Third Datanode replica also should be moved in proper
     * sorage based on policy.
     */
    @Test(timeout = 300000)
    public void testSPSWhenFileHasLowRedundancyBlocks() throws Exception {
        try {
            config.set(DFS_STORAGE_POLICY_SATISFIER_RECHECK_TIMEOUT_MILLIS_KEY, "3000");
            config.set(DFS_STORAGE_POLICY_SATISFIER_SELF_RETRY_TIMEOUT_MILLIS_KEY, "5000");
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.ARCHIVE, StorageType.DISK }, new StorageType[]{ StorageType.ARCHIVE, StorageType.DISK }, new StorageType[]{ StorageType.ARCHIVE, StorageType.DISK } };
            hdfsCluster = startCluster(config, newtypes, 3, 2, TestExternalStoragePolicySatisfier.CAPACITY);
            hdfsCluster.waitActive();
            DistributedFileSystem fs = hdfsCluster.getFileSystem();
            Path filePath = new Path("/zeroSizeFile");
            DFSTestUtil.createFile(fs, filePath, 1024, ((short) (3)), 0);
            fs.setStoragePolicy(filePath, "COLD");
            List<MiniDFSCluster.DataNodeProperties> list = new ArrayList<>();
            list.add(hdfsCluster.stopDataNode(0));
            list.add(hdfsCluster.stopDataNode(0));
            list.add(hdfsCluster.stopDataNode(0));
            restartNamenode();
            hdfsCluster.restartDataNode(list.get(0), false);
            hdfsCluster.restartDataNode(list.get(1), false);
            hdfsCluster.waitActive();
            fs.satisfyStoragePolicy(filePath);
            DFSTestUtil.waitExpectedStorageType(filePath.toString(), ARCHIVE, 2, 30000, hdfsCluster.getFileSystem());
            hdfsCluster.restartDataNode(list.get(2), false);
            DFSTestUtil.waitExpectedStorageType(filePath.toString(), ARCHIVE, 3, 30000, hdfsCluster.getFileSystem());
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Test SPS for extra redundant file blocks.
     * 1. Create cluster with 5 datanode.
     * 2. Create one file with 5 replica.
     * 3. Set file replication to 3.
     * 4. Set policy and call satisfyStoragePolicy for file.
     * 5. Block should be moved successfully.
     */
    @Test(timeout = 300000)
    public void testSPSWhenFileHasExcessRedundancyBlocks() throws Exception {
        try {
            config.set(DFS_STORAGE_POLICY_SATISFIER_RECHECK_TIMEOUT_MILLIS_KEY, "3000");
            config.set(DFS_STORAGE_POLICY_SATISFIER_SELF_RETRY_TIMEOUT_MILLIS_KEY, "5000");
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.ARCHIVE, StorageType.DISK }, new StorageType[]{ StorageType.ARCHIVE, StorageType.DISK }, new StorageType[]{ StorageType.ARCHIVE, StorageType.DISK }, new StorageType[]{ StorageType.ARCHIVE, StorageType.DISK }, new StorageType[]{ StorageType.ARCHIVE, StorageType.DISK } };
            hdfsCluster = startCluster(config, newtypes, 5, 2, TestExternalStoragePolicySatisfier.CAPACITY);
            hdfsCluster.waitActive();
            DistributedFileSystem fs = hdfsCluster.getFileSystem();
            Path filePath = new Path("/zeroSizeFile");
            DFSTestUtil.createFile(fs, filePath, 1024, ((short) (5)), 0);
            fs.setReplication(filePath, ((short) (3)));
            LogCapturer logs = GenericTestUtils.LogCapturer.captureLogs(LoggerFactory.getLogger(BlockStorageMovementAttemptedItems.class));
            fs.setStoragePolicy(filePath, "COLD");
            fs.satisfyStoragePolicy(filePath);
            DFSTestUtil.waitExpectedStorageType(filePath.toString(), ARCHIVE, 3, 60000, hdfsCluster.getFileSystem());
            Assert.assertFalse("Log output does not contain expected log message: ", logs.getOutput().contains("some of the blocks are low redundant"));
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Test SPS for empty directory, xAttr should be removed.
     */
    @Test(timeout = 300000)
    public void testSPSForEmptyDirectory() throws IOException, InterruptedException, TimeoutException {
        try {
            hdfsCluster = startCluster(config, allDiskTypes, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY);
            hdfsCluster.waitActive();
            DistributedFileSystem fs = hdfsCluster.getFileSystem();
            Path emptyDir = new Path("/emptyDir");
            fs.mkdirs(emptyDir);
            fs.satisfyStoragePolicy(emptyDir);
            // Make sure satisfy xattr has been removed.
            DFSTestUtil.waitForXattrRemoved("/emptyDir", HdfsServerConstants.XATTR_SATISFY_STORAGE_POLICY, hdfsCluster.getNamesystem(), 30000);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Test SPS for not exist directory.
     */
    @Test(timeout = 300000)
    public void testSPSForNonExistDirectory() throws Exception {
        try {
            hdfsCluster = startCluster(config, allDiskTypes, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY);
            hdfsCluster.waitActive();
            DistributedFileSystem fs = hdfsCluster.getFileSystem();
            Path emptyDir = new Path("/emptyDir");
            try {
                fs.satisfyStoragePolicy(emptyDir);
                Assert.fail("FileNotFoundException should throw");
            } catch (FileNotFoundException e) {
                // nothing to do
            }
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Test SPS for directory tree which doesn't have files.
     */
    @Test(timeout = 300000)
    public void testSPSWithDirectoryTreeWithoutFile() throws Exception {
        try {
            hdfsCluster = startCluster(config, allDiskTypes, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY);
            hdfsCluster.waitActive();
            // Create directories
            /* root
                               |
                      A--------C--------D
                               |
                          G----H----I
                               |
                               O
             */
            DistributedFileSystem fs = hdfsCluster.getFileSystem();
            fs.mkdirs(new Path("/root/C/H/O"));
            fs.mkdirs(new Path("/root/A"));
            fs.mkdirs(new Path("/root/D"));
            fs.mkdirs(new Path("/root/C/G"));
            fs.mkdirs(new Path("/root/C/I"));
            fs.satisfyStoragePolicy(new Path("/root"));
            // Make sure satisfy xattr has been removed.
            DFSTestUtil.waitForXattrRemoved("/root", HdfsServerConstants.XATTR_SATISFY_STORAGE_POLICY, hdfsCluster.getNamesystem(), 30000);
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Test SPS for directory which has multilevel directories.
     */
    @Test(timeout = 300000)
    public void testMultipleLevelDirectoryForSatisfyStoragePolicy() throws Exception {
        try {
            StorageType[][] diskTypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.ARCHIVE, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.DISK } };
            config.setLong("dfs.block.size", TestExternalStoragePolicySatisfier.DEFAULT_BLOCK_SIZE);
            hdfsCluster = startCluster(config, diskTypes, diskTypes.length, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY);
            dfs = hdfsCluster.getFileSystem();
            TestExternalStoragePolicySatisfier.createDirectoryTree(dfs);
            List<String> files = getDFSListOfTree();
            dfs.setStoragePolicy(new Path("/root"), TestExternalStoragePolicySatisfier.COLD);
            dfs.satisfyStoragePolicy(new Path("/root"));
            for (String fileName : files) {
                // Wait till the block is moved to ARCHIVE
                DFSTestUtil.waitExpectedStorageType(fileName, ARCHIVE, 2, 30000, dfs);
            }
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Test storage move blocks while under replication block tasks exists in the
     * system. So, both will share the max transfer streams.
     *
     * 1. Create cluster with 3 datanode.
     * 2. Create 20 files with 2 replica.
     * 3. Start 2 more DNs with DISK & SSD types
     * 4. SetReplication factor for the 1st 10 files to 4 to trigger replica task
     * 5. Set policy to SSD to the 2nd set of files from 11-20
     * 6. Call SPS for 11-20 files to trigger move block tasks to new DNs
     * 7. Wait for the under replica and SPS tasks completion
     */
    @Test(timeout = 300000)
    public void testMoveBlocksWithUnderReplicatedBlocks() throws Exception {
        try {
            config.setInt(DFS_NAMENODE_REPLICATION_MAX_STREAMS_KEY, 3);
            config.setLong("dfs.block.size", TestExternalStoragePolicySatisfier.DEFAULT_BLOCK_SIZE);
            config.set(DFS_STORAGE_POLICY_SATISFIER_RECHECK_TIMEOUT_MILLIS_KEY, "3000");
            config.set(DFS_STORAGE_POLICY_SATISFIER_SELF_RETRY_TIMEOUT_MILLIS_KEY, "5000");
            StorageType[][] storagetypes = new StorageType[][]{ new StorageType[]{ StorageType.ARCHIVE, StorageType.DISK }, new StorageType[]{ StorageType.ARCHIVE, StorageType.DISK } };
            hdfsCluster = startCluster(config, storagetypes, 2, 2, TestExternalStoragePolicySatisfier.CAPACITY);
            hdfsCluster.waitActive();
            dfs = hdfsCluster.getFileSystem();
            // Below files will be used for pending replication block tasks.
            for (int i = 1; i <= 20; i++) {
                Path filePath = new Path(("/file" + i));
                DFSTestUtil.createFile(dfs, filePath, ((TestExternalStoragePolicySatisfier.DEFAULT_BLOCK_SIZE) * 5), ((short) (2)), 0);
            }
            StorageType[][] newtypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.SSD } };
            startAdditionalDNs(config, 2, TestExternalStoragePolicySatisfier.NUM_OF_DATANODES, newtypes, TestExternalStoragePolicySatisfier.STORAGES_PER_DATANODE, TestExternalStoragePolicySatisfier.CAPACITY, hdfsCluster);
            // increase replication factor to 4 for the first 10 files and thus
            // initiate replica tasks
            for (int i = 1; i <= 10; i++) {
                Path filePath = new Path(("/file" + i));
                dfs.setReplication(filePath, ((short) (4)));
            }
            // invoke SPS for 11-20 files
            for (int i = 11; i <= 20; i++) {
                Path filePath = new Path(("/file" + i));
                dfs.setStoragePolicy(filePath, "ALL_SSD");
                dfs.satisfyStoragePolicy(filePath);
            }
            for (int i = 1; i <= 10; i++) {
                Path filePath = new Path(("/file" + i));
                DFSTestUtil.waitExpectedStorageType(filePath.toString(), DISK, 4, 60000, hdfsCluster.getFileSystem());
            }
            for (int i = 11; i <= 20; i++) {
                Path filePath = new Path(("/file" + i));
                DFSTestUtil.waitExpectedStorageType(filePath.toString(), SSD, 2, 30000, hdfsCluster.getFileSystem());
            }
        } finally {
            shutdownCluster();
        }
    }

    /**
     * Implementation of listener callback, where it collects all the sps move
     * attempted blocks for assertion.
     */
    public static final class ExternalBlockMovementListener implements BlockMovementListener {
        private List<Block> actualBlockMovements = new ArrayList<>();

        @Override
        public void notifyMovementTriedBlocks(Block[] moveAttemptFinishedBlks) {
            for (Block block : moveAttemptFinishedBlks) {
                actualBlockMovements.add(block);
            }
            TestExternalStoragePolicySatisfier.LOG.info("Movement attempted blocks:{}", actualBlockMovements);
        }

        public List<Block> getActualBlockMovements() {
            return actualBlockMovements;
        }

        public void clear() {
            actualBlockMovements.clear();
        }
    }
}

