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


import DFSConfigKeys.DFS_BLOCKREPORT_INTERVAL_MSEC_KEY;
import DFSConfigKeys.DFS_NAMENODE_RECONSTRUCTION_PENDING_TIMEOUT_SEC_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.junit.Assert;
import org.junit.Test;


public class TestProcessCorruptBlocks {
    /**
     * The corrupt block has to be removed when the number of valid replicas
     * matches replication factor for the file. In this the above condition is
     * tested by reducing the replication factor
     * The test strategy :
     *   Bring up Cluster with 3 DataNodes
     *   Create a file of replication factor 3
     *   Corrupt one replica of a block of the file
     *   Verify that there are still 2 good replicas and 1 corrupt replica
     *    (corrupt replica should not be removed since number of good
     *     replicas (2) is less than replication factor (3))
     *   Set the replication factor to 2
     *   Verify that the corrupt replica is removed.
     *     (corrupt replica  should not be removed since number of good
     *      replicas (2) is equal to replication factor (2))
     */
    @Test
    public void testWhenDecreasingReplication() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000L);
        conf.set(DFS_NAMENODE_RECONSTRUCTION_PENDING_TIMEOUT_SEC_KEY, Integer.toString(2));
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
        FileSystem fs = cluster.getFileSystem();
        final FSNamesystem namesystem = cluster.getNamesystem();
        try {
            final Path fileName = new Path("/foo1");
            DFSTestUtil.createFile(fs, fileName, 2, ((short) (3)), 0L);
            DFSTestUtil.waitReplication(fs, fileName, ((short) (3)));
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, fileName);
            corruptBlock(cluster, fs, fileName, 0, block);
            DFSTestUtil.waitReplication(fs, fileName, ((short) (2)));
            Assert.assertEquals(2, TestProcessCorruptBlocks.countReplicas(namesystem, block).liveReplicas());
            Assert.assertEquals(1, TestProcessCorruptBlocks.countReplicas(namesystem, block).corruptReplicas());
            namesystem.setReplication(fileName.toString(), ((short) (2)));
            // wait for 3 seconds so that all block reports are processed.
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            Assert.assertEquals(2, TestProcessCorruptBlocks.countReplicas(namesystem, block).liveReplicas());
            Assert.assertEquals(0, TestProcessCorruptBlocks.countReplicas(namesystem, block).corruptReplicas());
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * The corrupt block has to be removed when the number of valid replicas
     * matches replication factor for the file. In this test, the above
     * condition is achieved by increasing the number of good replicas by
     * replicating on a new Datanode.
     * The test strategy :
     *   Bring up Cluster with 3 DataNodes
     *   Create a file  of replication factor 3
     *   Corrupt one replica of a block of the file
     *   Verify that there are still 2 good replicas and 1 corrupt replica
     *     (corrupt replica should not be removed since number of good replicas
     *      (2) is less  than replication factor (3))
     *   Start a new data node
     *   Verify that the a new replica is created and corrupt replica is
     *   removed.
     */
    @Test
    public void testByAddingAnExtraDataNode() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000L);
        conf.set(DFS_NAMENODE_RECONSTRUCTION_PENDING_TIMEOUT_SEC_KEY, Integer.toString(2));
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(4).build();
        FileSystem fs = cluster.getFileSystem();
        final FSNamesystem namesystem = cluster.getNamesystem();
        MiniDFSCluster.DataNodeProperties dnPropsFourth = cluster.stopDataNode(3);
        try {
            final Path fileName = new Path("/foo1");
            DFSTestUtil.createFile(fs, fileName, 2, ((short) (3)), 0L);
            DFSTestUtil.waitReplication(fs, fileName, ((short) (3)));
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, fileName);
            corruptBlock(cluster, fs, fileName, 0, block);
            DFSTestUtil.waitReplication(fs, fileName, ((short) (2)));
            Assert.assertEquals(2, TestProcessCorruptBlocks.countReplicas(namesystem, block).liveReplicas());
            Assert.assertEquals(1, TestProcessCorruptBlocks.countReplicas(namesystem, block).corruptReplicas());
            cluster.restartDataNode(dnPropsFourth);
            DFSTestUtil.waitReplication(fs, fileName, ((short) (3)));
            Assert.assertEquals(3, TestProcessCorruptBlocks.countReplicas(namesystem, block).liveReplicas());
            Assert.assertEquals(0, TestProcessCorruptBlocks.countReplicas(namesystem, block).corruptReplicas());
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * The corrupt block has to be removed when the number of valid replicas
     * matches replication factor for the file. The above condition should hold
     * true as long as there is one good replica. This test verifies that.
     *
     * The test strategy :
     *   Bring up Cluster with 2 DataNodes
     *   Create a file of replication factor 2
     *   Corrupt one replica of a block of the file
     *   Verify that there is  one good replicas and 1 corrupt replica
     *     (corrupt replica should not be removed since number of good
     *     replicas (1) is less than replication factor (2)).
     *   Set the replication factor to 1
     *   Verify that the corrupt replica is removed.
     *     (corrupt replica should  be removed since number of good
     *      replicas (1) is equal to replication factor (1))
     */
    @Test(timeout = 20000)
    public void testWithReplicationFactorAsOne() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000L);
        conf.set(DFS_NAMENODE_RECONSTRUCTION_PENDING_TIMEOUT_SEC_KEY, Integer.toString(2));
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(2).build();
        FileSystem fs = cluster.getFileSystem();
        final FSNamesystem namesystem = cluster.getNamesystem();
        try {
            final Path fileName = new Path("/foo1");
            DFSTestUtil.createFile(fs, fileName, 2, ((short) (2)), 0L);
            DFSTestUtil.waitReplication(fs, fileName, ((short) (2)));
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, fileName);
            corruptBlock(cluster, fs, fileName, 0, block);
            DFSTestUtil.waitReplication(fs, fileName, ((short) (1)));
            Assert.assertEquals(1, TestProcessCorruptBlocks.countReplicas(namesystem, block).liveReplicas());
            Assert.assertEquals(1, TestProcessCorruptBlocks.countReplicas(namesystem, block).corruptReplicas());
            namesystem.setReplication(fileName.toString(), ((short) (1)));
            // wait for 3 seconds so that all block reports are processed.
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                if ((TestProcessCorruptBlocks.countReplicas(namesystem, block).corruptReplicas()) == 0) {
                    break;
                }
            }
            Assert.assertEquals(1, TestProcessCorruptBlocks.countReplicas(namesystem, block).liveReplicas());
            Assert.assertEquals(0, TestProcessCorruptBlocks.countReplicas(namesystem, block).corruptReplicas());
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * None of the blocks can be removed if all blocks are corrupt.
     *
     * The test strategy :
     *    Bring up Cluster with 3 DataNodes
     *    Create a file of replication factor 3
     *    Corrupt all three replicas
     *    Verify that all replicas are corrupt and 3 replicas are present.
     *    Set the replication factor to 1
     *    Verify that all replicas are corrupt and 3 replicas are present.
     */
    @Test
    public void testWithAllCorruptReplicas() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setLong(DFS_BLOCKREPORT_INTERVAL_MSEC_KEY, 1000L);
        conf.set(DFS_NAMENODE_RECONSTRUCTION_PENDING_TIMEOUT_SEC_KEY, Integer.toString(2));
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
        FileSystem fs = cluster.getFileSystem();
        final FSNamesystem namesystem = cluster.getNamesystem();
        try {
            final Path fileName = new Path("/foo1");
            DFSTestUtil.createFile(fs, fileName, 2, ((short) (3)), 0L);
            DFSTestUtil.waitReplication(fs, fileName, ((short) (3)));
            ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, fileName);
            corruptBlock(cluster, fs, fileName, 0, block);
            corruptBlock(cluster, fs, fileName, 1, block);
            corruptBlock(cluster, fs, fileName, 2, block);
            // wait for 3 seconds so that all block reports are processed.
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            Assert.assertEquals(0, TestProcessCorruptBlocks.countReplicas(namesystem, block).liveReplicas());
            Assert.assertEquals(3, TestProcessCorruptBlocks.countReplicas(namesystem, block).corruptReplicas());
            namesystem.setReplication(fileName.toString(), ((short) (1)));
            // wait for 3 seconds so that all block reports are processed.
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            Assert.assertEquals(0, TestProcessCorruptBlocks.countReplicas(namesystem, block).liveReplicas());
            Assert.assertEquals(3, TestProcessCorruptBlocks.countReplicas(namesystem, block).corruptReplicas());
        } finally {
            cluster.shutdown();
        }
    }
}

