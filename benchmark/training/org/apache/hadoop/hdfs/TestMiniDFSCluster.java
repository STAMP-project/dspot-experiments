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


import DFSConfigKeys.DFS_DATANODE_HOST_NAME_KEY;
import HdfsConstants.SafeModeAction.SAFEMODE_ENTER;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Tests MiniDFS cluster setup/teardown and isolation.
 * Every instance is brought up with a new data dir, to ensure that
 * shutdown work in background threads don't interfere with bringing up
 * the new cluster.
 */
public class TestMiniDFSCluster {
    private static final String CLUSTER_1 = "cluster1";

    private static final String CLUSTER_2 = "cluster2";

    private static final String CLUSTER_3 = "cluster3";

    private static final String CLUSTER_4 = "cluster4";

    private static final String CLUSTER_5 = "cluster5";

    protected File testDataPath;

    /**
     * Verify that without system properties the cluster still comes up, provided
     * the configuration is set
     *
     * @throws Throwable
     * 		on a failure
     */
    @Test(timeout = 100000)
    public void testClusterWithoutSystemProperties() throws Throwable {
        String oldPrp = System.getProperty(MiniDFSCluster.PROP_TEST_BUILD_DATA);
        System.clearProperty(MiniDFSCluster.PROP_TEST_BUILD_DATA);
        Configuration conf = new HdfsConfiguration();
        File testDataCluster1 = new File(testDataPath, TestMiniDFSCluster.CLUSTER_1);
        String c1Path = testDataCluster1.getAbsolutePath();
        conf.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, c1Path);
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build()) {
            Assert.assertEquals(new File((c1Path + "/data")), new File(cluster.getDataDirectory()));
        } finally {
            if (oldPrp != null) {
                System.setProperty(MiniDFSCluster.PROP_TEST_BUILD_DATA, oldPrp);
            }
        }
    }

    /**
     * Tests storage capacity setting still effective after cluster restart.
     */
    @Test(timeout = 100000)
    public void testClusterSetStorageCapacity() throws Throwable {
        final Configuration conf = new HdfsConfiguration();
        final int numDatanodes = 1;
        final int defaultBlockSize = 1024;
        final int blocks = 100;
        final int blocksSize = 1024;
        final int fileLen = blocks * blocksSize;
        final long capcacity = (defaultBlockSize * 2) * fileLen;
        final long[] capacities = new long[]{ capcacity, 2 * capcacity };
        final MiniDFSCluster cluster = newCluster(conf, numDatanodes, capacities, defaultBlockSize, fileLen);
        try {
            verifyStorageCapacity(cluster, capacities);
            /* restart all data nodes */
            cluster.restartDataNodes();
            cluster.waitActive();
            verifyStorageCapacity(cluster, capacities);
            /* restart all name nodes */
            cluster.restartNameNodes();
            cluster.waitActive();
            verifyStorageCapacity(cluster, capacities);
            /* restart all name nodes firstly and data nodes then */
            cluster.restartNameNodes();
            cluster.restartDataNodes();
            cluster.waitActive();
            verifyStorageCapacity(cluster, capacities);
            /* restart all data nodes firstly and name nodes then */
            cluster.restartDataNodes();
            cluster.restartNameNodes();
            cluster.waitActive();
            verifyStorageCapacity(cluster, capacities);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test(timeout = 100000)
    public void testIsClusterUpAfterShutdown() throws Throwable {
        Configuration conf = new HdfsConfiguration();
        File testDataCluster4 = new File(testDataPath, TestMiniDFSCluster.CLUSTER_4);
        MiniDFSCluster cluster4 = new MiniDFSCluster.Builder(conf, testDataCluster4).build();
        try {
            DistributedFileSystem dfs = cluster4.getFileSystem();
            dfs.setSafeMode(SAFEMODE_ENTER);
            cluster4.shutdown();
        } finally {
            while (cluster4.isClusterUp()) {
                Thread.sleep(1000);
            } 
        }
    }

    /**
     * MiniDFSCluster should not clobber dfs.datanode.hostname if requested
     */
    @Test(timeout = 100000)
    public void testClusterSetDatanodeHostname() throws Throwable {
        Assume.assumeTrue(System.getProperty("os.name").startsWith("Linux"));
        Configuration conf = new HdfsConfiguration();
        conf.set(DFS_DATANODE_HOST_NAME_KEY, "MYHOST");
        File testDataCluster5 = new File(testDataPath, TestMiniDFSCluster.CLUSTER_5);
        try (MiniDFSCluster cluster5 = new MiniDFSCluster.Builder(conf, testDataCluster5).numDataNodes(1).checkDataNodeHostConfig(true).build()) {
            Assert.assertEquals("DataNode hostname config not respected", "MYHOST", cluster5.getDataNodes().get(0).getDatanodeId().getHostName());
        }
    }

    @Test
    public void testClusterSetDatanodeDifferentStorageType() throws IOException {
        final Configuration conf = new HdfsConfiguration();
        StorageType[][] storageType = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE }, new StorageType[]{ StorageType.DISK }, new StorageType[]{ StorageType.ARCHIVE } };
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).storageTypes(storageType).build()) {
            cluster.waitActive();
            ArrayList<DataNode> dataNodes = cluster.getDataNodes();
            // Check the number of directory in DN's
            for (int i = 0; i < (storageType.length); i++) {
                Assert.assertEquals(DataNode.getStorageLocations(dataNodes.get(i).getConf()).size(), storageType[i].length);
            }
        }
    }

    @Test
    public void testClusterNoStorageTypeSetForDatanodes() throws IOException {
        final Configuration conf = new HdfsConfiguration();
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build()) {
            cluster.waitActive();
            ArrayList<DataNode> dataNodes = cluster.getDataNodes();
            // Check the number of directory in DN's
            for (DataNode datanode : dataNodes) {
                Assert.assertEquals(DataNode.getStorageLocations(datanode.getConf()).size(), 2);
            }
        }
    }

    @Test
    public void testSetUpFederatedCluster() throws Exception {
        Configuration conf = new Configuration();
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).nnTopology(MiniDFSNNTopology.simpleHAFederatedTopology(2)).numDataNodes(2).build()) {
            cluster.waitActive();
            cluster.transitionToActive(1);
            cluster.transitionToActive(3);
            Assert.assertEquals("standby", cluster.getNamesystem(0).getHAState());
            Assert.assertEquals("active", cluster.getNamesystem(1).getHAState());
            Assert.assertEquals("standby", cluster.getNamesystem(2).getHAState());
            Assert.assertEquals("active", cluster.getNamesystem(3).getHAState());
            String ns0nn0 = conf.get(DFSUtil.addKeySuffixes(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY, "ns0", "nn0"));
            String ns0nn1 = conf.get(DFSUtil.addKeySuffixes(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY, "ns0", "nn1"));
            String ns1nn0 = conf.get(DFSUtil.addKeySuffixes(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY, "ns1", "nn0"));
            String ns1nn1 = conf.get(DFSUtil.addKeySuffixes(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY, "ns1", "nn1"));
            for (MiniDFSCluster.NameNodeInfo nnInfo : cluster.getNameNodeInfos()) {
                Assert.assertEquals(ns0nn0, nnInfo.conf.get(DFSUtil.addKeySuffixes(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY, "ns0", "nn0")));
                Assert.assertEquals(ns0nn1, nnInfo.conf.get(DFSUtil.addKeySuffixes(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY, "ns0", "nn1")));
                Assert.assertEquals(ns1nn0, nnInfo.conf.get(DFSUtil.addKeySuffixes(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY, "ns1", "nn0")));
                Assert.assertEquals(ns1nn1, nnInfo.conf.get(DFSUtil.addKeySuffixes(DFSConfigKeys.DFS_NAMENODE_HTTP_ADDRESS_KEY, "ns1", "nn1")));
            }
        }
    }
}

