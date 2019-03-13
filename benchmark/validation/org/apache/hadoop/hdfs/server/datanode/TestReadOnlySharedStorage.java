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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfo;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeManager;
import org.apache.hadoop.hdfs.server.blockmanagement.NumberReplicas;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test proper {@link BlockManager} replication counting for {@link DatanodeStorage}s
 * with {@link DatanodeStorage.State#READ_ONLY_SHARED READ_ONLY} state.
 *
 * Uses {@link SimulatedFSDataset} to inject read-only replicas into a DataNode.
 */
public class TestReadOnlySharedStorage {
    public static final Logger LOG = LoggerFactory.getLogger(TestReadOnlySharedStorage.class);

    private static final short NUM_DATANODES = 3;

    private static final int RO_NODE_INDEX = 0;

    private static final int BLOCK_SIZE = 1024;

    private static final long seed = 464384013L;

    private static final Path PATH = new Path((("/" + (TestReadOnlySharedStorage.class.getName())) + ".dat"));

    private static final int RETRIES = 10;

    private Configuration conf;

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private DFSClient client;

    private BlockManager blockManager;

    private DatanodeManager datanodeManager;

    private DatanodeInfo normalDataNode;

    private DatanodeInfo readOnlyDataNode;

    private Block block;

    private BlockInfo storedBlock;

    private ExtendedBlock extendedBlock;

    /**
     * Verify that <tt>READ_ONLY_SHARED</tt> replicas are <i>not</i> counted towards the overall
     * replication count, but <i>are</i> included as replica locations returned to clients for reads.
     */
    @Test
    public void testReplicaCounting() throws Exception {
        // There should only be 1 *replica* (the READ_ONLY_SHARED doesn't count)
        validateNumberReplicas(1);
        fs.setReplication(TestReadOnlySharedStorage.PATH, ((short) (2)));
        // There should now be 3 *locations* for the block, and 2 *replicas*
        waitForLocations(3);
        validateNumberReplicas(2);
    }

    /**
     * Verify that the NameNode is able to still use <tt>READ_ONLY_SHARED</tt> replicas even
     * when the single NORMAL replica is offline (and the effective replication count is 0).
     */
    @Test
    public void testNormalReplicaOffline() throws Exception {
        // Stop the datanode hosting the NORMAL replica
        cluster.stopDataNode(normalDataNode.getXferAddr());
        // Force NameNode to detect that the datanode is down
        BlockManagerTestUtil.noticeDeadDatanode(cluster.getNameNode(), normalDataNode.getXferAddr());
        // The live replica count should now be zero (since the NORMAL replica is offline)
        NumberReplicas numberReplicas = blockManager.countNodes(storedBlock);
        Assert.assertThat(numberReplicas.liveReplicas(), CoreMatchers.is(0));
        // The block should be reported as under-replicated
        BlockManagerTestUtil.updateState(blockManager);
        Assert.assertThat(blockManager.getLowRedundancyBlocksCount(), CoreMatchers.is(1L));
        // The BlockManager should be able to heal the replication count back to 1
        // by triggering an inter-datanode replication from one of the READ_ONLY_SHARED replicas
        BlockManagerTestUtil.computeAllPendingWork(blockManager);
        DFSTestUtil.waitForReplication(cluster, extendedBlock, 1, 1, 0);
        // There should now be 2 *locations* for the block, and 1 *replica*
        Assert.assertThat(getLocatedBlock().getLocations().length, CoreMatchers.is(2));
        validateNumberReplicas(1);
    }

    /**
     * Verify that corrupt <tt>READ_ONLY_SHARED</tt> replicas aren't counted
     * towards the corrupt replicas total.
     */
    @Test
    public void testReadOnlyReplicaCorrupt() throws Exception {
        // "Corrupt" a READ_ONLY_SHARED replica by reporting it as a bad replica
        client.reportBadBlocks(new LocatedBlock[]{ new LocatedBlock(extendedBlock, new DatanodeInfo[]{ readOnlyDataNode }) });
        // There should now be only 1 *location* for the block as the READ_ONLY_SHARED is corrupt
        waitForLocations(1);
        // However, the corrupt READ_ONLY_SHARED replica should *not* affect the overall corrupt replicas count
        NumberReplicas numberReplicas = blockManager.countNodes(storedBlock);
        Assert.assertThat(numberReplicas.corruptReplicas(), CoreMatchers.is(0));
    }
}

