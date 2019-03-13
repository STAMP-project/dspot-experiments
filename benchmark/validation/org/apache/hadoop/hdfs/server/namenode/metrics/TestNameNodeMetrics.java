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
package org.apache.hadoop.hdfs.server.namenode.metrics;


import CommonConfigurationKeysPublic.HADOOP_SECURITY_KEY_PROVIDER_PATH;
import CreateEncryptionZoneFlag.NO_TRASH;
import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import DFSConfigKeys.DFS_DATANODE_DISK_CHECK_MIN_GAP_KEY;
import DFSConfigKeys.DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY;
import DFSConfigKeys.DFS_HA_TAILEDITS_PERIOD_KEY;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_DEFAULT;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import DFSConfigKeys.DFS_METRICS_PERCENTILES_INTERVALS_KEY;
import DFSConfigKeys.DFS_NAMENODE_AVOID_STALE_DATANODE_FOR_READ_KEY;
import DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_CHECK_PERIOD_KEY;
import DFSConfigKeys.DFS_NAMENODE_CHECKPOINT_TXNS_KEY;
import DFSConfigKeys.DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY;
import DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_DEFAULT;
import DFSConfigKeys.DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_LIST_ENCRYPTION_ZONES_NUM_RESPONSES;
import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY;
import DFSConfigKeys.DFS_NAMENODE_STALE_DATANODE_INTERVAL_DEFAULT;
import DFSConfigKeys.DFS_NAMENODE_STALE_DATANODE_INTERVAL_KEY;
import FsDatasetSpi.FsVolumeReferences;
import Rename.OVERWRITE;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import SystemErasureCodingPolicies.XOR_2_1_POLICY_ID;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.key.JavaKeyStoreProvider;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.FileSystemTestWrapper;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.protocol.LocatedStripedBlock;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeDescriptor;
import org.apache.hadoop.hdfs.server.common.Storage;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsVolumeImpl;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.MockNameNodeResourceChecker;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.apache.hadoop.hdfs.server.namenode.ha.HATestUtil;
import org.apache.hadoop.hdfs.tools.NNHAServiceTarget;
import org.apache.hadoop.hdfs.util.HostsFileWriter;
import org.apache.hadoop.metrics2.MetricsRecordBuilder;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.MetricsAsserts;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * Test for metrics published by the Namenode
 */
public class TestNameNodeMetrics {
    private static final Configuration CONF = new HdfsConfiguration();

    private static final int DFS_REDUNDANCY_INTERVAL = 1;

    private static final Path TEST_ROOT_DIR_PATH = new Path("/testNameNodeMetrics");

    private static final String NN_METRICS = "NameNodeActivity";

    private static final String NS_METRICS = "FSNamesystem";

    private static final int BLOCK_SIZE = 1024 * 1024;

    private static final ErasureCodingPolicy EC_POLICY = SystemErasureCodingPolicies.getByID(XOR_2_1_POLICY_ID);

    public static final Logger LOG = LoggerFactory.getLogger(TestNameNodeMetrics.class);

    // Number of datanodes in the cluster
    private static final int DATANODE_COUNT = ((TestNameNodeMetrics.EC_POLICY.getNumDataUnits()) + (TestNameNodeMetrics.EC_POLICY.getNumParityUnits())) + 1;

    private static final int WAIT_GAUGE_VALUE_RETRIES = 20;

    // Rollover interval of percentile metrics (in seconds)
    private static final int PERCENTILES_INTERVAL = 1;

    static {
        TestNameNodeMetrics.CONF.setLong(DFS_BLOCK_SIZE_KEY, TestNameNodeMetrics.BLOCK_SIZE);
        TestNameNodeMetrics.CONF.setInt(DFS_BYTES_PER_CHECKSUM_KEY, 1);
        TestNameNodeMetrics.CONF.setLong(DFS_HEARTBEAT_INTERVAL_KEY, TestNameNodeMetrics.DFS_REDUNDANCY_INTERVAL);
        // Set it long enough to essentially disable unless we manually call it
        // Used for decommissioning DataNode metrics
        TestNameNodeMetrics.CONF.setTimeDuration(MiniDFSCluster.DFS_NAMENODE_DECOMMISSION_INTERVAL_TESTING_KEY, 999, TimeUnit.DAYS);
        // Next two configs used for checking failed volume metrics
        TestNameNodeMetrics.CONF.setTimeDuration(DFS_DATANODE_DISK_CHECK_MIN_GAP_KEY, 10, TimeUnit.MILLISECONDS);
        TestNameNodeMetrics.CONF.setInt(DFS_DATANODE_FAILED_VOLUMES_TOLERATED_KEY, 1);
        TestNameNodeMetrics.CONF.setInt(DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY, TestNameNodeMetrics.DFS_REDUNDANCY_INTERVAL);
        TestNameNodeMetrics.CONF.set(DFS_METRICS_PERCENTILES_INTERVALS_KEY, ("" + (TestNameNodeMetrics.PERCENTILES_INTERVAL)));
        // Enable stale DataNodes checking
        TestNameNodeMetrics.CONF.setBoolean(DFS_NAMENODE_AVOID_STALE_DATANODE_FOR_READ_KEY, true);
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(MetricsAsserts.class), Level.DEBUG);
    }

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private final Random rand = new Random();

    private FSNamesystem namesystem;

    private HostsFileWriter hostsFileWriter;

    private BlockManager bm;

    private Path ecDir;

    /**
     * Test that capacity metrics are exported and pass
     * basic sanity tests.
     */
    @Test(timeout = 10000)
    public void testCapacityMetrics() throws Exception {
        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS);
        long capacityTotal = MetricsAsserts.getLongGauge("CapacityTotal", rb);
        assert capacityTotal != 0;
        long capacityUsed = MetricsAsserts.getLongGauge("CapacityUsed", rb);
        long capacityRemaining = MetricsAsserts.getLongGauge("CapacityRemaining", rb);
        long capacityUsedNonDFS = MetricsAsserts.getLongGauge("CapacityUsedNonDFS", rb);
        // There will be 5% space reserved in ext filesystem which is not
        // considered.
        assert ((capacityUsed + capacityRemaining) + capacityUsedNonDFS) <= capacityTotal;
    }

    /**
     * Test metrics indicating the number of stale DataNodes
     */
    @Test
    public void testStaleNodes() throws Exception {
        // Set two datanodes as stale
        for (int i = 0; i < 2; i++) {
            DataNode dn = cluster.getDataNodes().get(i);
            DataNodeTestUtils.setHeartbeatsDisabledForTests(dn, true);
            long staleInterval = TestNameNodeMetrics.CONF.getLong(DFS_NAMENODE_STALE_DATANODE_INTERVAL_KEY, DFS_NAMENODE_STALE_DATANODE_INTERVAL_DEFAULT);
            DatanodeDescriptor dnDes = cluster.getNameNode().getNamesystem().getBlockManager().getDatanodeManager().getDatanode(dn.getDatanodeId());
            DFSTestUtil.resetLastUpdatesWithOffset(dnDes, (-(staleInterval + 1)));
        }
        // Let HeartbeatManager to check heartbeat
        BlockManagerTestUtil.checkHeartbeat(cluster.getNameNode().getNamesystem().getBlockManager());
        MetricsAsserts.assertGauge("StaleDataNodes", 2, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        // Reset stale datanodes
        for (int i = 0; i < 2; i++) {
            DataNode dn = cluster.getDataNodes().get(i);
            DataNodeTestUtils.setHeartbeatsDisabledForTests(dn, false);
            DatanodeDescriptor dnDes = cluster.getNameNode().getNamesystem().getBlockManager().getDatanodeManager().getDatanode(dn.getDatanodeId());
            DFSTestUtil.resetLastUpdatesWithOffset(dnDes, 0);
        }
        // Let HeartbeatManager to refresh
        BlockManagerTestUtil.checkHeartbeat(cluster.getNameNode().getNamesystem().getBlockManager());
        MetricsAsserts.assertGauge("StaleDataNodes", 0, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
    }

    /**
     * Test metrics associated with volume failures.
     */
    @Test
    public void testVolumeFailures() throws Exception {
        MetricsAsserts.assertGauge("VolumeFailuresTotal", 0, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("EstimatedCapacityLostTotal", 0L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        DataNode dn = cluster.getDataNodes().get(0);
        FsDatasetSpi.FsVolumeReferences volumeReferences = DataNodeTestUtils.getFSDataset(dn).getFsVolumeReferences();
        FsVolumeImpl fsVolume = ((FsVolumeImpl) (volumeReferences.get(0)));
        File dataDir = new File(fsVolume.getBaseURI());
        long capacity = fsVolume.getCapacity();
        volumeReferences.close();
        File storageDir = new File(dataDir, Storage.STORAGE_DIR_CURRENT);
        DataNodeTestUtils.injectDataDirFailure(storageDir);
        DataNodeTestUtils.waitForDiskError(dn, fsVolume);
        DataNodeTestUtils.triggerHeartbeat(dn);
        BlockManagerTestUtil.checkHeartbeat(bm);
        MetricsAsserts.assertGauge("VolumeFailuresTotal", 1, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("EstimatedCapacityLostTotal", capacity, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
    }

    /**
     * Test metrics associated with liveness and decommission status of DataNodes.
     */
    @Test
    public void testDataNodeLivenessAndDecom() throws Exception {
        List<DataNode> dataNodes = cluster.getDataNodes();
        DatanodeDescriptor[] dnDescriptors = new DatanodeDescriptor[TestNameNodeMetrics.DATANODE_COUNT];
        String[] dnAddresses = new String[TestNameNodeMetrics.DATANODE_COUNT];
        for (int i = 0; i < (TestNameNodeMetrics.DATANODE_COUNT); i++) {
            dnDescriptors[i] = bm.getDatanodeManager().getDatanode(dataNodes.get(i).getDatanodeId());
            dnAddresses[i] = dnDescriptors[i].getXferAddr();
        }
        // First put all DNs into include
        hostsFileWriter.initIncludeHosts(dnAddresses);
        bm.getDatanodeManager().refreshNodes(TestNameNodeMetrics.CONF);
        MetricsAsserts.assertGauge("NumDecomLiveDataNodes", 0, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumLiveDataNodes", TestNameNodeMetrics.DATANODE_COUNT, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        // Now decommission one DN
        hostsFileWriter.initExcludeHost(dnAddresses[0]);
        bm.getDatanodeManager().refreshNodes(TestNameNodeMetrics.CONF);
        MetricsAsserts.assertGauge("NumDecommissioningDataNodes", 1, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        BlockManagerTestUtil.recheckDecommissionState(bm.getDatanodeManager());
        MetricsAsserts.assertGauge("NumDecommissioningDataNodes", 0, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumDecomLiveDataNodes", 1, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumLiveDataNodes", TestNameNodeMetrics.DATANODE_COUNT, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        // Now kill all DNs by expiring their heartbeats
        for (int i = 0; i < (TestNameNodeMetrics.DATANODE_COUNT); i++) {
            DataNodeTestUtils.setHeartbeatsDisabledForTests(dataNodes.get(i), true);
            long expireInterval = ((TestNameNodeMetrics.CONF.getLong(DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_KEY, DFS_NAMENODE_HEARTBEAT_RECHECK_INTERVAL_DEFAULT)) * 2L) + (((TestNameNodeMetrics.CONF.getLong(DFS_HEARTBEAT_INTERVAL_KEY, DFS_HEARTBEAT_INTERVAL_DEFAULT)) * 10) * 1000L);
            DFSTestUtil.resetLastUpdatesWithOffset(dnDescriptors[i], (-(expireInterval + 1)));
        }
        BlockManagerTestUtil.checkHeartbeat(bm);
        MetricsAsserts.assertGauge("NumDecomLiveDataNodes", 0, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumDecomDeadDataNodes", 1, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumLiveDataNodes", 0, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumDeadDataNodes", TestNameNodeMetrics.DATANODE_COUNT, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        // Now remove the decommissioned DN altogether
        String[] includeHosts = new String[(dnAddresses.length) - 1];
        for (int i = 0; i < (includeHosts.length); i++) {
            includeHosts[i] = dnAddresses[(i + 1)];
        }
        hostsFileWriter.initIncludeHosts(includeHosts);
        hostsFileWriter.initExcludeHosts(new ArrayList<>());
        bm.getDatanodeManager().refreshNodes(TestNameNodeMetrics.CONF);
        MetricsAsserts.assertGauge("NumDecomLiveDataNodes", 0, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumDecomDeadDataNodes", 0, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumLiveDataNodes", 0, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumDeadDataNodes", ((TestNameNodeMetrics.DATANODE_COUNT) - 1), MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        // Finally mark the remaining DNs as live again
        for (int i = 1; i < (dataNodes.size()); i++) {
            DataNodeTestUtils.setHeartbeatsDisabledForTests(dataNodes.get(i), false);
            DFSTestUtil.resetLastUpdatesWithOffset(dnDescriptors[i], 0);
        }
        BlockManagerTestUtil.checkHeartbeat(bm);
        MetricsAsserts.assertGauge("NumLiveDataNodes", ((TestNameNodeMetrics.DATANODE_COUNT) - 1), MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumDeadDataNodes", 0, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
    }

    /**
     * Test metrics associated with addition of a file
     */
    @Test
    public void testFileAdd() throws Exception {
        // File creations
        final long blockCount = 32;
        final Path normalFile = TestNameNodeMetrics.getTestPath("testFileAdd");
        createFile(normalFile, (blockCount * (TestNameNodeMetrics.BLOCK_SIZE)), ((short) (3)));
        final Path ecFile = new Path(ecDir, "ecFile.log");
        DFSTestUtil.createStripedFile(cluster, ecFile, null, ((int) (blockCount)), 1, false, TestNameNodeMetrics.EC_POLICY);
        int blockCapacity = namesystem.getBlockCapacity();
        MetricsAsserts.assertGauge("BlockCapacity", blockCapacity, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS);
        // File create operations are 2
        MetricsAsserts.assertCounter("CreateFileOps", 2L, rb);
        // Number of files created is depth of normalFile and ecFile, after
        // removing the duplicate accounting for root test dir.
        MetricsAsserts.assertCounter("FilesCreated", ((long) ((normalFile.depth()) + (ecFile.depth()))), rb);
        /* ecDir */
        long filesTotal = ((normalFile.depth()) + (ecFile.depth())) + 1;
        rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS);
        MetricsAsserts.assertGauge("FilesTotal", filesTotal, rb);
        MetricsAsserts.assertGauge("BlocksTotal", (blockCount * 2), rb);
        fs.delete(normalFile, true);
        filesTotal--;// reduce the filecount for deleted file

        rb = waitForDnMetricValue(TestNameNodeMetrics.NS_METRICS, "FilesTotal", filesTotal);
        MetricsAsserts.assertGauge("BlocksTotal", blockCount, rb);
        MetricsAsserts.assertGauge("PendingDeletionBlocks", 0L, rb);
        fs.delete(ecFile, true);
        filesTotal--;
        rb = waitForDnMetricValue(TestNameNodeMetrics.NS_METRICS, "FilesTotal", filesTotal);
        MetricsAsserts.assertGauge("BlocksTotal", 0L, rb);
        MetricsAsserts.assertGauge("PendingDeletionBlocks", 0L, rb);
        rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS);
        // Delete file operations and number of files deleted must be 1
        MetricsAsserts.assertCounter("DeleteFileOps", 2L, rb);
        MetricsAsserts.assertCounter("FilesDeleted", 2L, rb);
    }

    /**
     * Corrupt a block and ensure metrics reflects it
     */
    @Test
    public void testCorruptBlock() throws Exception {
        // Create a file with single block with two replicas
        final Path file = TestNameNodeMetrics.getTestPath("testCorruptBlock");
        final short replicaCount = 2;
        createFile(file, 100, replicaCount);
        DFSTestUtil.waitForReplication(fs, file, replicaCount, 15000);
        // Disable the heartbeats, so that no corrupted replica
        // can be fixed
        for (DataNode dn : cluster.getDataNodes()) {
            DataNodeTestUtils.setHeartbeatsDisabledForTests(dn, true);
        }
        verifyZeroMetrics();
        verifyAggregatedMetricsTally();
        BlockManagerTestUtil.stopRedundancyThread(bm);
        // Corrupt first replica of the block
        LocatedBlock block = NameNodeAdapter.getBlockLocations(cluster.getNameNode(), file.toString(), 0, 1).get(0);
        cluster.getNamesystem().writeLock();
        try {
            bm.findAndMarkBlockAsCorrupt(block.getBlock(), block.getLocations()[0], "STORAGE_ID", "TEST");
        } finally {
            cluster.getNamesystem().writeUnlock();
        }
        BlockManagerTestUtil.updateState(bm);
        MetricsRecordBuilder rb = waitForDnMetricValue(TestNameNodeMetrics.NS_METRICS, "CorruptBlocks", 1L, 500);
        // Verify aggregated blocks metrics
        MetricsAsserts.assertGauge("LowRedundancyBlocks", 1L, rb);
        MetricsAsserts.assertGauge("PendingReplicationBlocks", 0L, rb);
        MetricsAsserts.assertGauge("PendingReconstructionBlocks", 0L, rb);
        // Verify replicated blocks metrics
        MetricsAsserts.assertGauge("LowRedundancyReplicatedBlocks", 1L, rb);
        MetricsAsserts.assertGauge("CorruptReplicatedBlocks", 1L, rb);
        MetricsAsserts.assertGauge("HighestPriorityLowRedundancyReplicatedBlocks", 1L, rb);
        // Verify striped blocks metrics
        MetricsAsserts.assertGauge("LowRedundancyECBlockGroups", 0L, rb);
        MetricsAsserts.assertGauge("CorruptECBlockGroups", 0L, rb);
        MetricsAsserts.assertGauge("HighestPriorityLowRedundancyECBlocks", 0L, rb);
        verifyAggregatedMetricsTally();
        for (DataNode dn : cluster.getDataNodes()) {
            DataNodeTestUtils.setHeartbeatsDisabledForTests(dn, false);
        }
        // Start block reconstruction work
        BlockManagerTestUtil.getComputedDatanodeWork(bm);
        BlockManagerTestUtil.updateState(bm);
        DFSTestUtil.waitForReplication(fs, file, replicaCount, 30000);
        rb = waitForDnMetricValue(TestNameNodeMetrics.NS_METRICS, "CorruptBlocks", 0L, 500);
        // Verify aggregated blocks metrics
        MetricsAsserts.assertGauge("LowRedundancyBlocks", 0L, rb);
        MetricsAsserts.assertGauge("CorruptBlocks", 0L, rb);
        MetricsAsserts.assertGauge("PendingReplicationBlocks", 0L, rb);
        MetricsAsserts.assertGauge("PendingReconstructionBlocks", 0L, rb);
        // Verify replicated blocks metrics
        MetricsAsserts.assertGauge("LowRedundancyReplicatedBlocks", 0L, rb);
        MetricsAsserts.assertGauge("CorruptReplicatedBlocks", 0L, rb);
        MetricsAsserts.assertGauge("HighestPriorityLowRedundancyReplicatedBlocks", 0L, rb);
        // Verify striped blocks metrics
        MetricsAsserts.assertGauge("LowRedundancyECBlockGroups", 0L, rb);
        MetricsAsserts.assertGauge("CorruptECBlockGroups", 0L, rb);
        MetricsAsserts.assertGauge("HighestPriorityLowRedundancyECBlocks", 0L, rb);
        verifyAggregatedMetricsTally();
        fs.delete(file, true);
        BlockManagerTestUtil.getComputedDatanodeWork(bm);
        // During the file deletion, both BlockManager#corruptReplicas and
        // BlockManager#pendingReplications will be updated, i.e., the records
        // for the blocks of the deleted file will be removed from both
        // corruptReplicas and pendingReplications. The corresponding
        // metrics (CorruptBlocks and PendingReplicationBlocks) will only be updated
        // when BlockManager#computeDatanodeWork is run where the
        // BlockManager#updateState is called. And in
        // BlockManager#computeDatanodeWork the metric ScheduledReplicationBlocks
        // will also be updated.
        BlockManagerTestUtil.updateState(bm);
        waitForDnMetricValue(TestNameNodeMetrics.NS_METRICS, "CorruptBlocks", 0L, 500);
        verifyZeroMetrics();
        verifyAggregatedMetricsTally();
    }

    @Test(timeout = 90000L)
    public void testStripedFileCorruptBlocks() throws Exception {
        final long fileLen = (TestNameNodeMetrics.BLOCK_SIZE) * 4;
        final Path ecFile = new Path(ecDir, "ecFile.log");
        DFSTestUtil.createFile(fs, ecFile, fileLen, ((short) (1)), 0L);
        StripedFileTestUtil.waitBlockGroupsReported(fs, ecFile.toString());
        // Disable the heartbeats, so that no corrupted replica
        // can be fixed
        for (DataNode dn : cluster.getDataNodes()) {
            DataNodeTestUtils.setHeartbeatsDisabledForTests(dn, true);
        }
        verifyZeroMetrics();
        verifyAggregatedMetricsTally();
        BlockManagerTestUtil.stopRedundancyThread(bm);
        // Corrupt first replica of the block
        LocatedBlocks lbs = fs.getClient().getNamenode().getBlockLocations(ecFile.toString(), 0, fileLen);
        assert (lbs.get(0)) instanceof LocatedStripedBlock;
        LocatedStripedBlock bg = ((LocatedStripedBlock) (lbs.get(0)));
        cluster.getNamesystem().writeLock();
        try {
            bm.findAndMarkBlockAsCorrupt(bg.getBlock(), bg.getLocations()[0], "STORAGE_ID", "TEST");
        } finally {
            cluster.getNamesystem().writeUnlock();
        }
        BlockManagerTestUtil.updateState(bm);
        MetricsRecordBuilder rb = waitForDnMetricValue(TestNameNodeMetrics.NS_METRICS, "CorruptBlocks", 1L, 500);
        // Verify aggregated blocks metrics
        MetricsAsserts.assertGauge("LowRedundancyBlocks", 1L, rb);
        MetricsAsserts.assertGauge("PendingReplicationBlocks", 0L, rb);
        MetricsAsserts.assertGauge("PendingReconstructionBlocks", 0L, rb);
        // Verify replica metrics
        MetricsAsserts.assertGauge("LowRedundancyReplicatedBlocks", 0L, rb);
        MetricsAsserts.assertGauge("CorruptReplicatedBlocks", 0L, rb);
        MetricsAsserts.assertGauge("HighestPriorityLowRedundancyReplicatedBlocks", 0L, rb);
        // Verify striped block groups metrics
        MetricsAsserts.assertGauge("LowRedundancyECBlockGroups", 1L, rb);
        MetricsAsserts.assertGauge("CorruptECBlockGroups", 1L, rb);
        MetricsAsserts.assertGauge("HighestPriorityLowRedundancyECBlocks", 1L, rb);
        verifyAggregatedMetricsTally();
        for (DataNode dn : cluster.getDataNodes()) {
            DataNodeTestUtils.setHeartbeatsDisabledForTests(dn, false);
        }
        // Start block reconstruction work
        BlockManagerTestUtil.getComputedDatanodeWork(bm);
        BlockManagerTestUtil.updateState(bm);
        StripedFileTestUtil.waitForReconstructionFinished(ecFile, fs, 3);
        rb = waitForDnMetricValue(TestNameNodeMetrics.NS_METRICS, "CorruptBlocks", 0L, 500);
        MetricsAsserts.assertGauge("CorruptBlocks", 0L, rb);
        MetricsAsserts.assertGauge("PendingReplicationBlocks", 0L, rb);
        MetricsAsserts.assertGauge("PendingReconstructionBlocks", 0L, rb);
        // Verify replicated blocks metrics
        MetricsAsserts.assertGauge("LowRedundancyReplicatedBlocks", 0L, rb);
        MetricsAsserts.assertGauge("CorruptReplicatedBlocks", 0L, rb);
        MetricsAsserts.assertGauge("HighestPriorityLowRedundancyReplicatedBlocks", 0L, rb);
        // Verify striped blocks metrics
        MetricsAsserts.assertGauge("LowRedundancyECBlockGroups", 0L, rb);
        MetricsAsserts.assertGauge("CorruptECBlockGroups", 0L, rb);
        MetricsAsserts.assertGauge("HighestPriorityLowRedundancyECBlocks", 0L, rb);
        verifyAggregatedMetricsTally();
        fs.delete(ecFile, true);
        BlockManagerTestUtil.getComputedDatanodeWork(bm);
        // During the file deletion, both BlockManager#corruptReplicas and
        // BlockManager#pendingReplications will be updated, i.e., the records
        // for the blocks of the deleted file will be removed from both
        // corruptReplicas and pendingReplications. The corresponding
        // metrics (CorruptBlocks and PendingReplicationBlocks) will only be updated
        // when BlockManager#computeDatanodeWork is run where the
        // BlockManager#updateState is called. And in
        // BlockManager#computeDatanodeWork the metric ScheduledReplicationBlocks
        // will also be updated.
        BlockManagerTestUtil.updateState(bm);
        waitForDnMetricValue(TestNameNodeMetrics.NS_METRICS, "CorruptBlocks", 0L, 500);
        verifyZeroMetrics();
        verifyAggregatedMetricsTally();
    }

    /**
     * Create excess blocks by reducing the replication factor for
     * for a file and ensure metrics reflects it
     */
    @Test
    public void testExcessBlocks() throws Exception {
        Path file = TestNameNodeMetrics.getTestPath("testExcessBlocks");
        createFile(file, 100, ((short) (2)));
        NameNodeAdapter.setReplication(namesystem, file.toString(), ((short) (1)));
        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS);
        MetricsAsserts.assertGauge("ExcessBlocks", 1L, rb);
        // verify ExcessBlocks metric is decremented and
        // excessReplicateMap is cleared after deleting a file
        fs.delete(file, true);
        rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS);
        MetricsAsserts.assertGauge("ExcessBlocks", 0L, rb);
        Assert.assertEquals(0L, bm.getExcessBlocksCount());
    }

    /**
     * Test to ensure metrics reflects missing blocks
     */
    @Test
    public void testMissingBlock() throws Exception {
        // Create a file with single block with two replicas
        Path file = TestNameNodeMetrics.getTestPath("testMissingBlocks");
        createFile(file, 100, ((short) (1)));
        // Corrupt the only replica of the block to result in a missing block
        LocatedBlock block = NameNodeAdapter.getBlockLocations(cluster.getNameNode(), file.toString(), 0, 1).get(0);
        cluster.getNamesystem().writeLock();
        try {
            bm.findAndMarkBlockAsCorrupt(block.getBlock(), block.getLocations()[0], "STORAGE_ID", "TEST");
        } finally {
            cluster.getNamesystem().writeUnlock();
        }
        Thread.sleep(1000);// Wait for block to be marked corrupt

        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS);
        MetricsAsserts.assertGauge("UnderReplicatedBlocks", 1L, rb);
        MetricsAsserts.assertGauge("MissingBlocks", 1L, rb);
        MetricsAsserts.assertGauge("MissingReplOneBlocks", 1L, rb);
        MetricsAsserts.assertGauge("HighestPriorityLowRedundancyReplicatedBlocks", 0L, rb);
        MetricsAsserts.assertGauge("HighestPriorityLowRedundancyECBlocks", 0L, rb);
        fs.delete(file, true);
        waitForDnMetricValue(TestNameNodeMetrics.NS_METRICS, "UnderReplicatedBlocks", 0L);
    }

    @Test
    public void testRenameMetrics() throws Exception {
        Path src = TestNameNodeMetrics.getTestPath("src");
        createFile(src, 100, ((short) (1)));
        Path target = TestNameNodeMetrics.getTestPath("target");
        createFile(target, 100, ((short) (1)));
        fs.rename(src, target, OVERWRITE);
        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS);
        MetricsAsserts.assertCounter("FilesRenamed", 1L, rb);
        MetricsAsserts.assertCounter("FilesDeleted", 1L, rb);
    }

    /**
     * Test numGetBlockLocations metric
     *
     * Test initiates and performs file operations (create,read,close,open file )
     * which results in metrics changes. These metrics changes are updated and
     * tested for correctness.
     *
     *  create file operation does not increment numGetBlockLocation
     *  one read file operation increments numGetBlockLocation by 1
     *
     * @throws IOException
     * 		in case of an error
     */
    @Test
    public void testGetBlockLocationMetric() throws Exception {
        Path file1_Path = new Path(TestNameNodeMetrics.TEST_ROOT_DIR_PATH, "file1.dat");
        // When cluster starts first time there are no file  (read,create,open)
        // operations so metric GetBlockLocations should be 0.
        MetricsAsserts.assertCounter("GetBlockLocations", 0L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS));
        // Perform create file operation
        createFile(file1_Path, 100, ((short) (2)));
        // Create file does not change numGetBlockLocations metric
        // expect numGetBlockLocations = 0 for previous and current interval
        MetricsAsserts.assertCounter("GetBlockLocations", 0L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS));
        // Open and read file operation increments GetBlockLocations
        // Perform read file operation on earlier created file
        readFile(fs, file1_Path);
        // Verify read file operation has incremented numGetBlockLocations by 1
        MetricsAsserts.assertCounter("GetBlockLocations", 1L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS));
        // opening and reading file  twice will increment numGetBlockLocations by 2
        readFile(fs, file1_Path);
        readFile(fs, file1_Path);
        MetricsAsserts.assertCounter("GetBlockLocations", 3L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS));
    }

    /**
     * Testing TransactionsSinceLastCheckpoint. Need a new cluster as
     * the other tests in here don't use HA. See HDFS-7501.
     */
    @Test(timeout = 300000)
    public void testTransactionSinceLastCheckpointMetrics() throws Exception {
        Random random = new Random();
        int retryCount = 0;
        while (retryCount < 5) {
            try {
                int basePort = 10060 + ((random.nextInt(100)) * 2);
                MiniDFSNNTopology topology = new MiniDFSNNTopology().addNameservice(new MiniDFSNNTopology.NSConf("ns1").addNN(new MiniDFSNNTopology.NNConf("nn1").setHttpPort(basePort)).addNN(new MiniDFSNNTopology.NNConf("nn2").setHttpPort((basePort + 1))));
                HdfsConfiguration conf2 = new HdfsConfiguration();
                // Lower the checkpoint condition for purpose of testing.
                conf2.setInt(DFS_NAMENODE_CHECKPOINT_TXNS_KEY, 100);
                // Check for checkpoint condition very often, for purpose of testing.
                conf2.setInt(DFS_NAMENODE_CHECKPOINT_CHECK_PERIOD_KEY, 1);
                // Poll and follow ANN txns very often, for purpose of testing.
                conf2.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, 1);
                MiniDFSCluster cluster2 = new MiniDFSCluster.Builder(conf2).nnTopology(topology).numDataNodes(1).build();
                cluster2.waitActive();
                DistributedFileSystem fs2 = cluster2.getFileSystem(0);
                NameNode nn0 = cluster2.getNameNode(0);
                NameNode nn1 = cluster2.getNameNode(1);
                cluster2.transitionToActive(0);
                fs2.mkdirs(new Path("/tmp-t1"));
                fs2.mkdirs(new Path("/tmp-t2"));
                HATestUtil.waitForStandbyToCatchUp(nn0, nn1);
                // Test to ensure tracking works before the first-ever
                // checkpoint.
                // 2 txns added further when catch-up is called.
                Assert.assertEquals("SBN failed to track 2 transactions pre-checkpoint.", 4L, cluster2.getNameNode(1).getNamesystem().getTransactionsSinceLastCheckpoint());
                // Complete up to the boundary required for
                // an auto-checkpoint. Using 94 to expect fsimage
                // rounded at 100, as 4 + 94 + 2 (catch-up call) = 100.
                for (int i = 1; i <= 94; i++) {
                    fs2.mkdirs(new Path(("/tmp-" + i)));
                }
                HATestUtil.waitForStandbyToCatchUp(nn0, nn1);
                // Assert 100 transactions in checkpoint.
                HATestUtil.waitForCheckpoint(cluster2, 1, ImmutableList.of(100));
                // Test to ensure number tracks the right state of
                // uncheckpointed edits, and does not go negative
                // (as fixed in HDFS-7501).
                Assert.assertEquals("Should be zero right after the checkpoint.", 0L, cluster2.getNameNode(1).getNamesystem().getTransactionsSinceLastCheckpoint());
                fs2.mkdirs(new Path("/tmp-t3"));
                fs2.mkdirs(new Path("/tmp-t4"));
                HATestUtil.waitForStandbyToCatchUp(nn0, nn1);
                // Test to ensure we track the right numbers after
                // the checkpoint resets it to zero again.
                Assert.assertEquals("SBN failed to track 2 added txns after the ckpt.", 4L, cluster2.getNameNode(1).getNamesystem().getTransactionsSinceLastCheckpoint());
                cluster2.shutdown();
                break;
            } catch (Exception e) {
                TestNameNodeMetrics.LOG.warn(("Unable to set up HA cluster, exception thrown: " + e));
                retryCount++;
            }
        } 
    }

    /**
     * Test NN checkpoint and transaction-related metrics.
     */
    @Test
    public void testTransactionAndCheckpointMetrics() throws Exception {
        long lastCkptTime = MetricsAsserts.getLongGauge("LastCheckpointTime", MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("LastCheckpointTime", lastCkptTime, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("LastWrittenTransactionId", 4L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("TransactionsSinceLastCheckpoint", 4L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("TransactionsSinceLastLogRoll", 4L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        fs.mkdirs(new Path(TestNameNodeMetrics.TEST_ROOT_DIR_PATH, "/tmp"));
        MetricsAsserts.assertGauge("LastCheckpointTime", lastCkptTime, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("LastWrittenTransactionId", 5L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("TransactionsSinceLastCheckpoint", 5L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("TransactionsSinceLastLogRoll", 5L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        cluster.getNameNodeRpc().rollEditLog();
        MetricsAsserts.assertGauge("LastCheckpointTime", lastCkptTime, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("LastWrittenTransactionId", 7L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("TransactionsSinceLastCheckpoint", 7L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("TransactionsSinceLastLogRoll", 1L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        cluster.getNameNodeRpc().setSafeMode(SAFEMODE_ENTER, false);
        cluster.getNameNodeRpc().saveNamespace(0, 0);
        cluster.getNameNodeRpc().setSafeMode(SAFEMODE_LEAVE, false);
        long newLastCkptTime = MetricsAsserts.getLongGauge("LastCheckpointTime", MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        Assert.assertTrue((lastCkptTime < newLastCkptTime));
        MetricsAsserts.assertGauge("LastWrittenTransactionId", 9L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("TransactionsSinceLastCheckpoint", 1L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("TransactionsSinceLastLogRoll", 1L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
    }

    /**
     * Tests that the sync and block report metrics get updated on cluster
     * startup.
     */
    @Test
    public void testSyncAndBlockReportMetric() throws Exception {
        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS);
        // We have one sync when the cluster starts up, just opening the journal
        MetricsAsserts.assertCounter("SyncsNumOps", 4L, rb);
        // Each datanode reports in when the cluster comes up
        MetricsAsserts.assertCounter("StorageBlockReportNumOps", (((long) (TestNameNodeMetrics.DATANODE_COUNT)) * (cluster.getStoragesPerDatanode())), rb);
        // Sleep for an interval+slop to let the percentiles rollover
        Thread.sleep((((TestNameNodeMetrics.PERCENTILES_INTERVAL) + 1) * 1000));
        // Check that the percentiles were updated
        MetricsAsserts.assertQuantileGauges("Syncs1s", rb);
        MetricsAsserts.assertQuantileGauges("StorageBlockReport1s", rb);
    }

    /**
     * Test NN ReadOps Count and WriteOps Count
     */
    @Test
    public void testReadWriteOps() throws Exception {
        MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS);
        long startWriteCounter = MetricsAsserts.getLongCounter("TransactionsNumOps", rb);
        Path file1_Path = new Path(TestNameNodeMetrics.TEST_ROOT_DIR_PATH, "ReadData.dat");
        // Perform create file operation
        createFile(file1_Path, 1024, ((short) (2)));
        // Perform read file operation on earlier created file
        readFile(fs, file1_Path);
        MetricsRecordBuilder rbNew = MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS);
        Assert.assertTrue(((MetricsAsserts.getLongCounter("TransactionsNumOps", rbNew)) > startWriteCounter));
    }

    /**
     * Test metrics indicating the number of active clients and the files under
     * construction
     */
    @Test(timeout = 60000)
    public void testNumActiveClientsAndFilesUnderConstructionMetrics() throws Exception {
        final Path file1 = TestNameNodeMetrics.getTestPath("testFileAdd1");
        createFile(file1, 100, ((short) (3)));
        MetricsAsserts.assertGauge("NumActiveClients", 0L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumFilesUnderConstruction", 0L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        Path file2 = new Path("/testFileAdd2");
        FSDataOutputStream output2 = fs.create(file2);
        output2.writeBytes("Some test data");
        MetricsAsserts.assertGauge("NumActiveClients", 1L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumFilesUnderConstruction", 1L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        Path file3 = new Path("/testFileAdd3");
        FSDataOutputStream output3 = fs.create(file3);
        output3.writeBytes("Some test data");
        MetricsAsserts.assertGauge("NumActiveClients", 1L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        MetricsAsserts.assertGauge("NumFilesUnderConstruction", 2L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        // create another DistributedFileSystem client
        DistributedFileSystem fs1 = ((DistributedFileSystem) (cluster.getNewFileSystemInstance(0)));
        try {
            Path file4 = new Path("/testFileAdd4");
            FSDataOutputStream output4 = fs1.create(file4);
            output4.writeBytes("Some test data");
            MetricsAsserts.assertGauge("NumActiveClients", 2L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
            MetricsAsserts.assertGauge("NumFilesUnderConstruction", 3L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
            Path file5 = new Path("/testFileAdd35");
            FSDataOutputStream output5 = fs1.create(file5);
            output5.writeBytes("Some test data");
            MetricsAsserts.assertGauge("NumActiveClients", 2L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
            MetricsAsserts.assertGauge("NumFilesUnderConstruction", 4L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
            output2.close();
            output3.close();
            MetricsAsserts.assertGauge("NumActiveClients", 1L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
            MetricsAsserts.assertGauge("NumFilesUnderConstruction", 2L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
            output4.close();
            output5.close();
            MetricsAsserts.assertGauge("NumActiveClients", 0L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
            MetricsAsserts.assertGauge("NumFilesUnderConstruction", 0L, MetricsAsserts.getMetrics(TestNameNodeMetrics.NS_METRICS));
        } finally {
            fs1.close();
        }
    }

    @Test
    public void testGenerateEDEKTime() throws IOException, NoSuchAlgorithmException {
        // Create new MiniDFSCluster with EncryptionZone configurations
        Configuration conf = new HdfsConfiguration();
        FileSystemTestHelper fsHelper = new FileSystemTestHelper();
        // Set up java key store
        String testRoot = fsHelper.getTestRootDir();
        File testRootDir = new File(testRoot).getAbsoluteFile();
        conf.set(HADOOP_SECURITY_KEY_PROVIDER_PATH, (((JavaKeyStoreProvider.SCHEME_NAME) + "://file") + (new Path(testRootDir.toString(), "test.jks").toUri())));
        conf.setBoolean(DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY, true);
        conf.setInt(DFS_NAMENODE_LIST_ENCRYPTION_ZONES_NUM_RESPONSES, 2);
        File basedir = new File(MiniDFSCluster.getBaseDirectory(), GenericTestUtils.getMethodName());
        try (MiniDFSCluster clusterEDEK = new MiniDFSCluster.Builder(conf, basedir).numDataNodes(1).build()) {
            DistributedFileSystem fsEDEK = clusterEDEK.getFileSystem();
            FileSystemTestWrapper fsWrapper = new FileSystemTestWrapper(fsEDEK);
            HdfsAdmin dfsAdmin = new HdfsAdmin(clusterEDEK.getURI(), conf);
            fsEDEK.getClient().setKeyProvider(clusterEDEK.getNameNode().getNamesystem().getProvider());
            String testKey = "test_key";
            DFSTestUtil.createKey(testKey, clusterEDEK, conf);
            final Path zoneParent = new Path("/zones");
            final Path zone1 = new Path(zoneParent, "zone1");
            fsWrapper.mkdir(zone1, FsPermission.getDirDefault(), true);
            dfsAdmin.createEncryptionZone(zone1, "test_key", EnumSet.of(NO_TRASH));
            MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS);
            for (int i = 0; i < 3; i++) {
                Path filePath = new Path(("/zones/zone1/testfile-" + i));
                DFSTestUtil.createFile(fsEDEK, filePath, 1024, ((short) (3)), 1L);
                MetricsAsserts.assertQuantileGauges("GenerateEDEKTime1s", rb);
            }
        }
    }

    @Test
    public void testResourceCheck() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        File basedir = new File(MiniDFSCluster.getBaseDirectory(), GenericTestUtils.getMethodName());
        MiniDFSCluster tmpCluster = new MiniDFSCluster.Builder(conf, basedir).numDataNodes(0).nnTopology(MiniDFSNNTopology.simpleHATopology()).build();
        try {
            MockNameNodeResourceChecker mockResourceChecker = new MockNameNodeResourceChecker(conf);
            tmpCluster.getNameNode(0).getNamesystem().setNNResourceChecker(mockResourceChecker);
            NNHAServiceTarget haTarget = new NNHAServiceTarget(conf, DFSUtil.getNamenodeNameServiceId(new HdfsConfiguration()), "nn1");
            HAServiceProtocol rpc = haTarget.getHealthMonitorProxy(conf, conf.getInt(CommonConfigurationKeys.HA_HM_RPC_TIMEOUT_KEY, CommonConfigurationKeys.HA_HM_RPC_TIMEOUT_DEFAULT));
            MetricsRecordBuilder rb = MetricsAsserts.getMetrics(TestNameNodeMetrics.NN_METRICS);
            for (long i = 0; i < 10; i++) {
                rpc.monitorHealth();
                MetricsAsserts.assertQuantileGauges("ResourceCheckTime1s", rb);
            }
        } finally {
            if (tmpCluster != null) {
                tmpCluster.shutdown();
            }
        }
    }

    @Test
    public void testEditLogTailing() throws Exception {
        HdfsConfiguration conf = new HdfsConfiguration();
        conf.setInt(DFS_HA_TAILEDITS_PERIOD_KEY, 1);
        conf.setInt(DFS_METRICS_PERCENTILES_INTERVALS_KEY, 60);
        MiniDFSCluster dfsCluster = null;
        try {
            dfsCluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).nnTopology(MiniDFSNNTopology.simpleHATopology()).build();
            DistributedFileSystem dfs = dfsCluster.getFileSystem(0);
            dfsCluster.transitionToActive(0);
            dfsCluster.waitActive();
            Path testDir = new Path("/testdir");
            dfs.mkdir(testDir, FsPermission.getDefault());
            dfsCluster.getNameNodeRpc(0).rollEditLog();
            Thread.sleep((2 * 1000));
            // We need to get the metrics for the SBN (excluding the NN from dfs
            // cluster created in setUp() and the ANN).
            MetricsRecordBuilder rb = MetricsAsserts.getMetrics(((TestNameNodeMetrics.NN_METRICS) + "-2"));
            MetricsAsserts.assertQuantileGauges("EditLogTailTime60s", rb);
            MetricsAsserts.assertQuantileGauges("EditLogFetchTime60s", rb);
            MetricsAsserts.assertQuantileGauges("NumEditLogLoaded60s", rb, "Count");
            MetricsAsserts.assertQuantileGauges("EditLogTailInterval60s", rb);
            MetricsAsserts.assertCounterGt("EditLogTailTimeNumOps", 0L, rb);
            MetricsAsserts.assertCounterGt("EditLogFetchTimeNumOps", 0L, rb);
            MetricsAsserts.assertCounterGt("NumEditLogLoadedNumOps", 0L, rb);
            MetricsAsserts.assertCounterGt("EditLogTailIntervalNumOps", 0L, rb);
        } finally {
            if (dfsCluster != null) {
                dfsCluster.shutdown();
            }
        }
    }
}

