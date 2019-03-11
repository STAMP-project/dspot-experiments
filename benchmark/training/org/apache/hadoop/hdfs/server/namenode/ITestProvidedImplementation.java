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


import DFSConfigKeys.DFS_NAMENODE_PROVIDED_ENABLED;
import DFSConfigKeys.DFS_PROVIDED_ALIASMAP_LOAD_RETRIES;
import DFSConfigKeys.DFS_REPLICATION_KEY;
import FixedBlockMultiReplicaResolver.REPLICATION;
import FixedBlockResolver.BLOCKSIZE;
import HdfsServerConstants.StartupOption.FORMAT;
import ImageWriter.Options.UGI_CLASS;
import MiniDFSCluster.Builder;
import MiniDFSCluster.DataNodeProperties;
import ProvidedStorageMap.ProvidedDescriptor;
import StorageType.PROVIDED;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfo;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeDescriptor;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeManager;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeStatistics;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeStorageInfo;
import org.apache.hadoop.hdfs.server.common.blockaliasmap.impl.TextFileRegionAliasMap;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.hdfs.server.namenode.ha.BootstrapStandby;
import org.apache.hadoop.hdfs.server.protocol.StorageReport;
import org.apache.hadoop.net.NodeBase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration tests for the Provided implementation.
 */
public class ITestProvidedImplementation {
    @Rule
    public TestName name = new TestName();

    public static final Logger LOG = LoggerFactory.getLogger(ITestProvidedImplementation.class);

    private final Random r = new Random();

    private final File fBASE = new File(MiniDFSCluster.getBaseDirectory());

    private final Path pBASE = new Path(fBASE.toURI().toString());

    private final Path providedPath = new Path(pBASE, "providedDir");

    private final Path nnDirPath = new Path(pBASE, "nnDir");

    private final String singleUser = "usr1";

    private final String singleGroup = "grp1";

    private final int numFiles = 10;

    private final String filePrefix = "file";

    private final String fileSuffix = ".dat";

    private final int baseFileLen = 1024;

    private long providedDataSize = 0;

    private final String bpid = "BP-1234-10.1.1.1-1224";

    private static final String clusterID = "CID-PROVIDED";

    private Configuration conf;

    private MiniDFSCluster cluster;

    @Test(timeout = 20000)
    public void testLoadImage() throws Exception {
        final long seed = r.nextLong();
        ITestProvidedImplementation.LOG.info(("providedPath: " + (providedPath)));
        createImage(new RandomTreeWalk(seed), nnDirPath, FixedBlockResolver.class);
        startCluster(nnDirPath, 0, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, null, false);
        FileSystem fs = cluster.getFileSystem();
        for (TreePath e : new RandomTreeWalk(seed)) {
            FileStatus rs = e.getFileStatus();
            Path hp = new Path(rs.getPath().toUri().getPath());
            Assert.assertTrue(fs.exists(hp));
            FileStatus hs = fs.getFileStatus(hp);
            Assert.assertEquals(rs.getPath().toUri().getPath(), hs.getPath().toUri().getPath());
            Assert.assertEquals(rs.getPermission(), hs.getPermission());
            Assert.assertEquals(rs.getLen(), hs.getLen());
            Assert.assertEquals(singleUser, hs.getOwner());
            Assert.assertEquals(singleGroup, hs.getGroup());
            Assert.assertEquals(rs.getAccessTime(), hs.getAccessTime());
            Assert.assertEquals(rs.getModificationTime(), hs.getModificationTime());
        }
    }

    @Test(timeout = 30000)
    public void testProvidedReporting() throws Exception {
        conf.setClass(UGI_CLASS, SingleUGIResolver.class, UGIResolver.class);
        createImage(new FSTreeWalk(providedPath, conf), nnDirPath, FixedBlockResolver.class);
        int numDatanodes = 10;
        startCluster(nnDirPath, numDatanodes, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, null, false);
        long diskCapacity = 1000;
        // set the DISK capacity for testing
        for (DataNode dn : cluster.getDataNodes()) {
            for (FsVolumeSpi ref : dn.getFSDataset().getFsVolumeReferences()) {
                if ((ref.getStorageType()) == (StorageType.DISK)) {
                    setCapacityForTesting(diskCapacity);
                }
            }
        }
        // trigger heartbeats to update the capacities
        cluster.triggerHeartbeats();
        Thread.sleep(10000);
        // verify namenode stats
        FSNamesystem namesystem = cluster.getNameNode().getNamesystem();
        DatanodeStatistics dnStats = namesystem.getBlockManager().getDatanodeManager().getDatanodeStatistics();
        // total capacity reported includes only the local volumes and
        // not the provided capacity
        Assert.assertEquals((diskCapacity * numDatanodes), namesystem.getTotal());
        // total storage used should be equal to the totalProvidedStorage
        // no capacity should be remaining!
        Assert.assertEquals(providedDataSize, dnStats.getProvidedCapacity());
        Assert.assertEquals(providedDataSize, namesystem.getProvidedCapacityTotal());
        Assert.assertEquals(providedDataSize, dnStats.getStorageTypeStats().get(PROVIDED).getCapacityTotal());
        Assert.assertEquals(providedDataSize, dnStats.getStorageTypeStats().get(PROVIDED).getCapacityUsed());
        // verify datanode stats
        for (DataNode dn : cluster.getDataNodes()) {
            for (StorageReport report : dn.getFSDataset().getStorageReports(namesystem.getBlockPoolId())) {
                if ((report.getStorage().getStorageType()) == (StorageType.PROVIDED)) {
                    Assert.assertEquals(providedDataSize, report.getCapacity());
                    Assert.assertEquals(providedDataSize, report.getDfsUsed());
                    Assert.assertEquals(providedDataSize, report.getBlockPoolUsed());
                    Assert.assertEquals(0, report.getNonDfsUsed());
                    Assert.assertEquals(0, report.getRemaining());
                }
            }
        }
        DFSClient client = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), cluster.getConfiguration(0));
        BlockManager bm = namesystem.getBlockManager();
        for (int fileId = 0; fileId < (numFiles); fileId++) {
            String filename = (("/" + (filePrefix)) + fileId) + (fileSuffix);
            LocatedBlocks locatedBlocks = client.getLocatedBlocks(filename, 0, baseFileLen);
            for (LocatedBlock locatedBlock : locatedBlocks.getLocatedBlocks()) {
                BlockInfo blockInfo = bm.getStoredBlock(locatedBlock.getBlock().getLocalBlock());
                Iterator<DatanodeStorageInfo> storagesItr = blockInfo.getStorageInfos();
                DatanodeStorageInfo info = storagesItr.next();
                Assert.assertEquals(PROVIDED, info.getStorageType());
                DatanodeDescriptor dnDesc = info.getDatanodeDescriptor();
                // check the locations that are returned by FSCK have the right name
                Assert.assertEquals((((ProvidedDescriptor.NETWORK_LOCATION) + (PATH_SEPARATOR_STR)) + (ProvidedDescriptor.NAME)), NodeBase.getPath(dnDesc));
                // no DatanodeStorageInfos should remain
                Assert.assertFalse(storagesItr.hasNext());
            }
        }
    }

    @Test(timeout = 500000)
    public void testDefaultReplication() throws Exception {
        int targetReplication = 2;
        conf.setInt(REPLICATION, targetReplication);
        createImage(new FSTreeWalk(providedPath, conf), nnDirPath, FixedBlockMultiReplicaResolver.class);
        // make the last Datanode with only DISK
        startCluster(nnDirPath, 3, null, new StorageType[][]{ new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, new StorageType[]{ StorageType.DISK } }, false);
        // wait for the replication to finish
        Thread.sleep(50000);
        FileSystem fs = cluster.getFileSystem();
        int count = 0;
        for (TreePath e : new FSTreeWalk(providedPath, conf)) {
            FileStatus rs = e.getFileStatus();
            Path hp = ITestProvidedImplementation.removePrefix(providedPath, rs.getPath());
            ITestProvidedImplementation.LOG.info(("path: " + (hp.toUri().getPath())));
            e.accept((count++));
            Assert.assertTrue(fs.exists(hp));
            FileStatus hs = fs.getFileStatus(hp);
            if (rs.isFile()) {
                BlockLocation[] bl = fs.getFileBlockLocations(hs.getPath(), 0, hs.getLen());
                int i = 0;
                for (; i < (bl.length); i++) {
                    int currentRep = bl[i].getHosts().length;
                    Assert.assertEquals(targetReplication, currentRep);
                }
            }
        }
    }

    @Test(timeout = 30000)
    public void testClusterWithEmptyImage() throws IOException {
        // start a cluster with 2 datanodes without any provided storage
        startCluster(nnDirPath, 2, null, new StorageType[][]{ new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.DISK } }, true);
        Assert.assertTrue(cluster.isClusterUp());
        Assert.assertTrue(cluster.isDataNodeUp());
        BlockLocation[] locations = createFile(new Path("/testFile1.dat"), ((short) (2)), (1024 * 1024), (1024 * 1024));
        Assert.assertEquals(1, locations.length);
        Assert.assertEquals(2, locations[0].getHosts().length);
    }

    /**
     * Tests setting replication of provided files.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 50000)
    public void testSetReplicationForProvidedFiles() throws Exception {
        createImage(new FSTreeWalk(providedPath, conf), nnDirPath, FixedBlockResolver.class);
        // 10 Datanodes with both DISK and PROVIDED storage
        startCluster(nnDirPath, 10, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, null, false);
        setAndUnsetReplication(((("/" + (filePrefix)) + ((numFiles) - 1)) + (fileSuffix)));
    }

    @Test(timeout = 30000)
    public void testProvidedDatanodeFailures() throws Exception {
        createImage(new FSTreeWalk(providedPath, conf), nnDirPath, FixedBlockResolver.class);
        startCluster(nnDirPath, 3, null, new StorageType[][]{ new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, new StorageType[]{ StorageType.DISK } }, false);
        DataNode providedDatanode1 = cluster.getDataNodes().get(0);
        DataNode providedDatanode2 = cluster.getDataNodes().get(1);
        DFSClient client = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), cluster.getConfiguration(0));
        DatanodeStorageInfo providedDNInfo = getProvidedDatanodeStorageInfo();
        if ((numFiles) >= 1) {
            String filename = (("/" + (filePrefix)) + ((numFiles) - 1)) + (fileSuffix);
            // 2 locations returned as there are 2 PROVIDED datanodes
            DatanodeInfo[] dnInfos = getAndCheckBlockLocations(client, filename, baseFileLen, 1, 2);
            // the location should be one of the provided DNs available
            Assert.assertTrue(((dnInfos[0].getDatanodeUuid().equals(providedDatanode1.getDatanodeUuid())) || (dnInfos[0].getDatanodeUuid().equals(providedDatanode2.getDatanodeUuid()))));
            // stop the 1st provided datanode
            MiniDFSCluster.DataNodeProperties providedDNProperties1 = cluster.stopDataNode(0);
            // make NameNode detect that datanode is down
            BlockManagerTestUtil.noticeDeadDatanode(cluster.getNameNode(), providedDatanode1.getDatanodeId().getXferAddr());
            // should find the block on the 2nd provided datanode
            dnInfos = getAndCheckBlockLocations(client, filename, baseFileLen, 1, 1);
            Assert.assertEquals(providedDatanode2.getDatanodeUuid(), dnInfos[0].getDatanodeUuid());
            // stop the 2nd provided datanode
            MiniDFSCluster.DataNodeProperties providedDNProperties2 = cluster.stopDataNode(0);
            // make NameNode detect that datanode is down
            BlockManagerTestUtil.noticeDeadDatanode(cluster.getNameNode(), providedDatanode2.getDatanodeId().getXferAddr());
            getAndCheckBlockLocations(client, filename, baseFileLen, 1, 0);
            // BR count for the provided ProvidedDatanodeStorageInfo should reset to
            // 0, when all DNs with PROVIDED storage fail.
            Assert.assertEquals(0, providedDNInfo.getBlockReportCount());
            // restart the provided datanode
            cluster.restartDataNode(providedDNProperties1, true);
            cluster.waitActive();
            Assert.assertEquals(1, providedDNInfo.getBlockReportCount());
            // should find the block on the 1st provided datanode now
            dnInfos = getAndCheckBlockLocations(client, filename, baseFileLen, 1, 1);
            // not comparing UUIDs as the datanode can now have a different one.
            Assert.assertEquals(providedDatanode1.getDatanodeId().getXferAddr(), dnInfos[0].getXferAddr());
        }
    }

    @Test(timeout = 300000)
    public void testTransientDeadDatanodes() throws Exception {
        createImage(new FSTreeWalk(providedPath, conf), nnDirPath, FixedBlockResolver.class);
        // 3 Datanodes, 2 PROVIDED and other DISK
        startCluster(nnDirPath, 3, null, new StorageType[][]{ new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, new StorageType[]{ StorageType.DISK } }, false);
        DataNode providedDatanode = cluster.getDataNodes().get(0);
        DatanodeStorageInfo providedDNInfo = getProvidedDatanodeStorageInfo();
        int initialBRCount = providedDNInfo.getBlockReportCount();
        for (int i = 0; i < (numFiles); i++) {
            // expect to have 2 locations as we have 2 provided Datanodes.
            verifyFileLocation(i, 2);
            // NameNode thinks the datanode is down
            BlockManagerTestUtil.noticeDeadDatanode(cluster.getNameNode(), providedDatanode.getDatanodeId().getXferAddr());
            cluster.waitActive();
            cluster.triggerHeartbeats();
            Thread.sleep(1000);
            // the report count should just continue to increase.
            Assert.assertEquals(((initialBRCount + i) + 1), providedDNInfo.getBlockReportCount());
            verifyFileLocation(i, 2);
        }
    }

    @Test(timeout = 30000)
    public void testNamenodeRestart() throws Exception {
        createImage(new FSTreeWalk(providedPath, conf), nnDirPath, FixedBlockResolver.class);
        // 3 Datanodes, 2 PROVIDED and other DISK
        startCluster(nnDirPath, 3, null, new StorageType[][]{ new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, new StorageType[]{ StorageType.DISK } }, false);
        verifyFileLocation(((numFiles) - 1), 2);
        cluster.restartNameNodes();
        cluster.waitActive();
        verifyFileLocation(((numFiles) - 1), 2);
    }

    @Test(timeout = 30000)
    public void testSetClusterID() throws Exception {
        String clusterID = "PROVIDED-CLUSTER";
        createImage(new FSTreeWalk(providedPath, conf), nnDirPath, FixedBlockResolver.class, clusterID, TextFileRegionAliasMap.class);
        // 2 Datanodes, 1 PROVIDED and other DISK
        startCluster(nnDirPath, 2, null, new StorageType[][]{ new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, new StorageType[]{ StorageType.DISK } }, false);
        NameNode nn = cluster.getNameNode();
        Assert.assertEquals(clusterID, nn.getNamesystem().getClusterId());
    }

    @Test(timeout = 30000)
    public void testNumberOfProvidedLocations() throws Exception {
        // set default replication to 4
        conf.setInt(DFS_REPLICATION_KEY, 4);
        createImage(new FSTreeWalk(providedPath, conf), nnDirPath, FixedBlockResolver.class);
        // start with 4 PROVIDED location
        startCluster(nnDirPath, 4, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, null, false);
        int expectedLocations = 4;
        for (int i = 0; i < (numFiles); i++) {
            verifyFileLocation(i, expectedLocations);
        }
        // stop 2 datanodes, one after the other and verify number of locations.
        for (int i = 1; i <= 2; i++) {
            DataNode dn = cluster.getDataNodes().get(0);
            cluster.stopDataNode(0);
            // make NameNode detect that datanode is down
            BlockManagerTestUtil.noticeDeadDatanode(cluster.getNameNode(), dn.getDatanodeId().getXferAddr());
            expectedLocations = 4 - i;
            for (int j = 0; j < (numFiles); j++) {
                verifyFileLocation(j, expectedLocations);
            }
        }
    }

    @Test(timeout = 30000)
    public void testNumberOfProvidedLocationsManyBlocks() throws Exception {
        // increase number of blocks per file to at least 10 blocks per file
        conf.setLong(BLOCKSIZE, ((baseFileLen) / 10));
        // set default replication to 4
        conf.setInt(DFS_REPLICATION_KEY, 4);
        createImage(new FSTreeWalk(providedPath, conf), nnDirPath, FixedBlockResolver.class);
        // start with 4 PROVIDED location
        startCluster(nnDirPath, 4, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, null, false);
        int expectedLocations = 4;
        for (int i = 0; i < (numFiles); i++) {
            verifyFileLocation(i, expectedLocations);
        }
    }

    @Test
    public void testInMemoryAliasMap() throws Exception {
        File aliasMapImage = createInMemoryAliasMapImage();
        // start cluster with two datanodes,
        // each with 1 PROVIDED volume and other DISK volume
        conf.setBoolean(DFSConfigKeys.DFS_PROVIDED_ALIASMAP_INMEMORY_ENABLED, true);
        conf.setInt(DFS_PROVIDED_ALIASMAP_LOAD_RETRIES, 10);
        startCluster(nnDirPath, 2, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, null, false);
        verifyFileSystemContents(0);
        FileUtils.deleteDirectory(aliasMapImage);
    }

    private static String providedNameservice;

    /**
     * Extends the {@link MiniDFSCluster.Builder} to create instances of
     * {@link MiniDFSClusterBuilderAliasMap}.
     */
    private static class MiniDFSClusterBuilderAliasMap extends MiniDFSCluster.Builder {
        MiniDFSClusterBuilderAliasMap(Configuration conf) {
            super(conf);
        }

        @Override
        public MiniDFSCluster build() throws IOException {
            return new ITestProvidedImplementation.MiniDFSClusterAliasMap(this);
        }
    }

    /**
     * Extends {@link MiniDFSCluster} to correctly configure the InMemoryAliasMap.
     */
    private static class MiniDFSClusterAliasMap extends MiniDFSCluster {
        private Map<String, Collection<URI>> formattedDirsByNamespaceId;

        private Set<Integer> completedNNs;

        MiniDFSClusterAliasMap(MiniDFSCluster.Builder builder) throws IOException {
            super(builder);
        }

        @Override
        protected void initNameNodeConf(Configuration conf, String nameserviceId, int nsIndex, String nnId, boolean manageNameDfsDirs, boolean enableManagedDfsDirsRedundancy, int nnIndex) throws IOException {
            if ((formattedDirsByNamespaceId) == null) {
                formattedDirsByNamespaceId = new HashMap<>();
                completedNNs = new HashSet<>();
            }
            super.initNameNodeConf(conf, nameserviceId, nsIndex, nnId, manageNameDfsDirs, enableManagedDfsDirsRedundancy, nnIndex);
            if (ITestProvidedImplementation.providedNameservice.equals(nameserviceId)) {
                // configure the InMemoryAliasMp.
                conf.setBoolean(DFSConfigKeys.DFS_PROVIDED_ALIASMAP_INMEMORY_ENABLED, true);
                String directory = conf.get(DFSConfigKeys.DFS_PROVIDED_ALIASMAP_INMEMORY_LEVELDB_DIR);
                if ((directory == null) || (!(new File(directory).exists()))) {
                    throw new IllegalArgumentException((("In-memory alias map configured" + "with the proper location; Set ") + (DFSConfigKeys.DFS_PROVIDED_ALIASMAP_INMEMORY_LEVELDB_DIR)));
                }
                // get the name of the directory (final component in path) used for map.
                // Assume that the aliasmap configured with the same final component
                // name in all Namenodes but is located in the path specified by
                // DFS_NAMENODE_NAME_DIR_KEY
                String dirName = new Path(directory).getName();
                String nnDir = conf.getTrimmedStringCollection(DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY).iterator().next();
                conf.set(DFSConfigKeys.DFS_PROVIDED_ALIASMAP_INMEMORY_LEVELDB_DIR, new File(new Path(nnDir, dirName).toUri()).getAbsolutePath());
                conf.setBoolean(DFS_NAMENODE_PROVIDED_ENABLED, true);
                // format the shared edits dir with the proper VERSION file.
                NameNode.initializeSharedEdits(conf);
            } else {
                if (!(completedNNs.contains(nnIndex))) {
                    // format the NN directories for non-provided namespaces
                    // if the directory for a namespace has been formatted, copy it over.
                    Collection<URI> namespaceDirs = FSNamesystem.getNamespaceDirs(conf);
                    if (formattedDirsByNamespaceId.containsKey(nameserviceId)) {
                        copyNameDirs(formattedDirsByNamespaceId.get(nameserviceId), namespaceDirs, conf);
                    } else {
                        for (URI nameDirUri : namespaceDirs) {
                            File nameDir = new File(nameDirUri);
                            if ((nameDir.exists()) && (!(FileUtil.fullyDelete(nameDir)))) {
                                throw new IOException(("Could not fully delete " + nameDir));
                            }
                        }
                        FORMAT.setClusterId(ITestProvidedImplementation.clusterID);
                        DFSTestUtil.formatNameNode(conf);
                        formattedDirsByNamespaceId.put(nameserviceId, namespaceDirs);
                    }
                    conf.setBoolean(DFS_NAMENODE_PROVIDED_ENABLED, false);
                    completedNNs.add(nnIndex);
                }
            }
        }
    }

    @Test
    public void testInMemoryAliasMapMultiTopologies() throws Exception {
        MiniDFSNNTopology[] topologies = new MiniDFSNNTopology[]{ MiniDFSNNTopology.simpleHATopology(), MiniDFSNNTopology.simpleFederatedTopology(3), MiniDFSNNTopology.simpleHAFederatedTopology(3) };
        for (MiniDFSNNTopology topology : topologies) {
            ITestProvidedImplementation.LOG.info("Starting test with topology with HA = {}, federation = {}", topology.isHA(), topology.isFederated());
            setSeed();
            createInMemoryAliasMapImage();
            conf.setBoolean(DFSConfigKeys.DFS_PROVIDED_ALIASMAP_INMEMORY_ENABLED, true);
            conf.setInt(DFS_PROVIDED_ALIASMAP_LOAD_RETRIES, 10);
            ITestProvidedImplementation.providedNameservice = topology.getNameservices().get(0).getId();
            // configure the AliasMap addresses
            configureAliasMapAddresses(topology, ITestProvidedImplementation.providedNameservice);
            startCluster(nnDirPath, 2, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, null, false, null, topology, new ITestProvidedImplementation.MiniDFSClusterBuilderAliasMap(conf));
            verifyPathsWithHAFailoverIfNecessary(topology, ITestProvidedImplementation.providedNameservice);
            shutdown();
        }
    }

    @Test
    public void testDatanodeLifeCycle() throws Exception {
        createImage(new FSTreeWalk(providedPath, conf), nnDirPath, FixedBlockResolver.class);
        startCluster(nnDirPath, 3, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, null, false);
        int fileIndex = (numFiles) - 1;
        final BlockManager blockManager = cluster.getNamesystem().getBlockManager();
        final DatanodeManager dnm = blockManager.getDatanodeManager();
        // to start, all 3 DNs are live in ProvidedDatanodeDescriptor.
        verifyFileLocation(fileIndex, 3);
        // de-commision first DN; still get 3 replicas.
        startDecommission(cluster.getNamesystem(), dnm, 0);
        verifyFileLocation(fileIndex, 3);
        // remains the same even after heartbeats.
        cluster.triggerHeartbeats();
        verifyFileLocation(fileIndex, 3);
        // start maintenance for 2nd DN; still get 3 replicas.
        startMaintenance(cluster.getNamesystem(), dnm, 1);
        verifyFileLocation(fileIndex, 3);
        DataNode dn1 = cluster.getDataNodes().get(0);
        DataNode dn2 = cluster.getDataNodes().get(1);
        // stop the 1st DN while being decommissioned.
        MiniDFSCluster.DataNodeProperties dn1Properties = cluster.stopDataNode(0);
        BlockManagerTestUtil.noticeDeadDatanode(cluster.getNameNode(), dn1.getDatanodeId().getXferAddr());
        // get 2 locations
        verifyFileLocation(fileIndex, 2);
        // stop dn2 while in maintenance.
        MiniDFSCluster.DataNodeProperties dn2Properties = cluster.stopDataNode(1);
        BlockManagerTestUtil.noticeDeadDatanode(cluster.getNameNode(), dn2.getDatanodeId().getXferAddr());
        // 2 valid locations will be found as blocks on nodes that die during
        // maintenance are not marked for removal.
        verifyFileLocation(fileIndex, 2);
        // stop the maintenance; get only 1 replicas
        stopMaintenance(cluster.getNamesystem(), dnm, 0);
        verifyFileLocation(fileIndex, 1);
        // restart the stopped DN.
        cluster.restartDataNode(dn1Properties, true);
        cluster.waitActive();
        // reports all 3 replicas
        verifyFileLocation(fileIndex, 2);
        cluster.restartDataNode(dn2Properties, true);
        cluster.waitActive();
        // reports all 3 replicas
        verifyFileLocation(fileIndex, 3);
    }

    @Test
    public void testProvidedWithHierarchicalTopology() throws Exception {
        conf.setClass(UGI_CLASS, FsUGIResolver.class, UGIResolver.class);
        String packageName = "org.apache.hadoop.hdfs.server.blockmanagement";
        String[] policies = new String[]{ "BlockPlacementPolicyDefault", "BlockPlacementPolicyRackFaultTolerant", "BlockPlacementPolicyWithNodeGroup", "BlockPlacementPolicyWithUpgradeDomain" };
        createImage(new FSTreeWalk(providedPath, conf), nnDirPath, FixedBlockResolver.class);
        String[] racks = new String[]{ "/pod0/rack0", "/pod0/rack0", "/pod0/rack1", "/pod0/rack1", "/pod1/rack0", "/pod1/rack0", "/pod1/rack1", "/pod1/rack1" };
        for (String policy : policies) {
            ITestProvidedImplementation.LOG.info(((("Using policy: " + packageName) + ".") + policy));
            conf.set(DFSConfigKeys.DFS_BLOCK_REPLICATOR_CLASSNAME_KEY, ((packageName + ".") + policy));
            startCluster(nnDirPath, racks.length, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, null, false, racks);
            verifyFileSystemContents(0);
            setAndUnsetReplication(((("/" + (filePrefix)) + ((numFiles) - 1)) + (fileSuffix)));
            cluster.shutdown();
        }
    }

    @Test
    public void testBootstrapAliasMap() throws Exception {
        int numNamenodes = 3;
        MiniDFSNNTopology topology = MiniDFSNNTopology.simpleHATopology(numNamenodes);
        createInMemoryAliasMapImage();
        conf.setBoolean(DFSConfigKeys.DFS_PROVIDED_ALIASMAP_INMEMORY_ENABLED, true);
        conf.setInt(DFS_PROVIDED_ALIASMAP_LOAD_RETRIES, 10);
        ITestProvidedImplementation.providedNameservice = topology.getNameservices().get(0).getId();
        // configure the AliasMap addresses
        configureAliasMapAddresses(topology, ITestProvidedImplementation.providedNameservice);
        startCluster(nnDirPath, 2, new StorageType[]{ StorageType.PROVIDED, StorageType.DISK }, null, false, null, topology, new ITestProvidedImplementation.MiniDFSClusterBuilderAliasMap(conf));
        // make NN with index 0 the active, shutdown and delete the directories
        // of others. This will delete the aliasmap on these namenodes as well.
        cluster.transitionToActive(0);
        verifyFileSystemContents(0);
        for (int nnIndex = 1; nnIndex < numNamenodes; nnIndex++) {
            cluster.shutdownNameNode(nnIndex);
            // delete the namenode directories including alias map.
            for (URI u : cluster.getNameDirs(nnIndex)) {
                File dir = new File(u.getPath());
                Assert.assertTrue(FileUtil.fullyDelete(dir));
            }
        }
        // start the other namenodes and bootstrap them
        for (int index = 1; index < numNamenodes; index++) {
            // add some content to aliasmap dir
            File aliasMapDir = new File(fBASE, ("aliasmap-" + index));
            // create a directory inside aliasMapDir
            if (!(new File(aliasMapDir, "tempDir").mkdirs())) {
                throw new IOException(("Unable to create directory " + aliasMapDir));
            }
            Configuration currNNConf = cluster.getConfiguration(index);
            currNNConf.set(DFSConfigKeys.DFS_PROVIDED_ALIASMAP_INMEMORY_LEVELDB_DIR, aliasMapDir.getAbsolutePath());
            // without force this should fail as aliasmap is not empty.
            int rc = BootstrapStandby.run(new String[]{ "-nonInteractive" }, currNNConf);
            Assert.assertNotEquals(0, rc);
            // force deletes the contents of the aliasmap.
            rc = BootstrapStandby.run(new String[]{ "-nonInteractive", "-force" }, currNNConf);
            Assert.assertEquals(0, rc);
        }
        // check if aliasmap files are the same on all NNs
        checkInMemoryAliasMapContents(0, numNamenodes);
        // restart the killed namenodes.
        for (int i = 1; i < numNamenodes; i++) {
            cluster.restartNameNode(i, false);
        }
        cluster.waitClusterUp();
        cluster.waitActive();
        // transition to namenode 1 as the active
        int nextNN = 1;
        cluster.shutdownNameNode(0);
        cluster.transitionToActive(nextNN);
        // all files must be accessible from nextNN.
        verifyFileSystemContents(nextNN);
    }
}

