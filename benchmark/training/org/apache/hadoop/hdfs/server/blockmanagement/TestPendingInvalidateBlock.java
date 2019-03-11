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
package org.apache.hadoop.hdfs.server.blockmanagement;


import BlockManager.LOG;
import ClientProtocol.GET_STATS_PENDING_DELETION_BLOCKS_IDX;
import ClientProtocol.STATS_ARRAY_LENGTH;
import java.lang.reflect.Method;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.Whitebox;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test if we can correctly delay the deletion of blocks.
 */
public class TestPendingInvalidateBlock {
    {
        GenericTestUtils.setLogLevel(LOG, Level.DEBUG);
    }

    private static final int BLOCKSIZE = 1024;

    private static final short REPLICATION = 2;

    private Configuration conf;

    private MiniDFSCluster cluster;

    private DistributedFileSystem dfs;

    @Test
    public void testPendingDeletion() throws Exception {
        final Path foo = new Path("/foo");
        DFSTestUtil.createFile(dfs, foo, TestPendingInvalidateBlock.BLOCKSIZE, TestPendingInvalidateBlock.REPLICATION, 0);
        DFSTestUtil.waitForReplication(dfs, foo, TestPendingInvalidateBlock.REPLICATION, 10000);
        // restart NN
        cluster.restartNameNode(true);
        InvalidateBlocks invalidateBlocks = ((InvalidateBlocks) (Whitebox.getInternalState(cluster.getNamesystem().getBlockManager(), "invalidateBlocks")));
        InvalidateBlocks mockIb = Mockito.spy(invalidateBlocks);
        // Return invalidation delay to delay the block's deletion
        Mockito.doReturn(1L).when(mockIb).getInvalidationDelay();
        Whitebox.setInternalState(cluster.getNamesystem().getBlockManager(), "invalidateBlocks", mockIb);
        dfs.delete(foo, true);
        waitForNumPendingDeletionBlocks(TestPendingInvalidateBlock.REPLICATION);
        Assert.assertEquals(0, cluster.getNamesystem().getBlocksTotal());
        Assert.assertEquals(TestPendingInvalidateBlock.REPLICATION, cluster.getNamesystem().getPendingDeletionBlocks());
        Assert.assertEquals(TestPendingInvalidateBlock.REPLICATION, dfs.getPendingDeletionBlocksCount());
        Mockito.doReturn(0L).when(mockIb).getInvalidationDelay();
        waitForNumPendingDeletionBlocks(0);
        Assert.assertEquals(0, cluster.getNamesystem().getBlocksTotal());
        Assert.assertEquals(0, cluster.getNamesystem().getPendingDeletionBlocks());
        Assert.assertEquals(0, dfs.getPendingDeletionBlocksCount());
        long nnStarted = cluster.getNamesystem().getNNStartedTimeInMillis();
        long blockDeletionStartTime = cluster.getNamesystem().getBlockDeletionStartTime();
        Assert.assertTrue(String.format("Expect blockDeletionStartTime = %d > nnStarted = %d.", blockDeletionStartTime, nnStarted), (blockDeletionStartTime > nnStarted));
        // test client protocol compatibility
        Method method = DFSClient.class.getDeclaredMethod("getStateByIndex", int.class);
        method.setAccessible(true);
        // get number of pending deletion blocks by its index
        long validState = ((Long) (method.invoke(dfs.getClient(), GET_STATS_PENDING_DELETION_BLOCKS_IDX)));
        // get an out of index value
        long invalidState = ((Long) (method.invoke(dfs.getClient(), STATS_ARRAY_LENGTH)));
        Assert.assertEquals(0, validState);
        Assert.assertEquals((-1), invalidState);
    }

    /**
     * Test whether we can delay the deletion of unknown blocks in DataNode's
     * first several block reports.
     */
    @Test
    public void testPendingDeleteUnknownBlocks() throws Exception {
        final int fileNum = 5;// 5 files

        final Path[] files = new Path[fileNum];
        final MiniDFSCluster.DataNodeProperties[] dnprops = new MiniDFSCluster.DataNodeProperties[TestPendingInvalidateBlock.REPLICATION];
        // create a group of files, each file contains 1 block
        for (int i = 0; i < fileNum; i++) {
            files[i] = new Path(("/file" + i));
            DFSTestUtil.createFile(dfs, files[i], TestPendingInvalidateBlock.BLOCKSIZE, TestPendingInvalidateBlock.REPLICATION, i);
        }
        // wait until all DataNodes have replicas
        waitForReplication();
        for (int i = (TestPendingInvalidateBlock.REPLICATION) - 1; i >= 0; i--) {
            dnprops[i] = cluster.stopDataNode(i);
        }
        Thread.sleep(2000);
        // delete 2 files, we still have 3 files remaining so that we can cover
        // every DN storage
        for (int i = 0; i < 2; i++) {
            dfs.delete(files[i], true);
        }
        // restart NameNode
        cluster.restartNameNode(false);
        InvalidateBlocks invalidateBlocks = ((InvalidateBlocks) (Whitebox.getInternalState(cluster.getNamesystem().getBlockManager(), "invalidateBlocks")));
        InvalidateBlocks mockIb = Mockito.spy(invalidateBlocks);
        Mockito.doReturn(1L).when(mockIb).getInvalidationDelay();
        Whitebox.setInternalState(cluster.getNamesystem().getBlockManager(), "invalidateBlocks", mockIb);
        Assert.assertEquals(0L, cluster.getNamesystem().getPendingDeletionBlocks());
        // restart DataNodes
        for (int i = 0; i < (TestPendingInvalidateBlock.REPLICATION); i++) {
            cluster.restartDataNode(dnprops[i]);
        }
        cluster.waitActive();
        for (int i = 0; i < (TestPendingInvalidateBlock.REPLICATION); i++) {
            DataNodeTestUtils.triggerBlockReport(cluster.getDataNodes().get(i));
        }
        Thread.sleep(2000);
        // make sure we have received block reports by checking the total block #
        Assert.assertEquals(3, cluster.getNamesystem().getBlocksTotal());
        Assert.assertEquals(4, cluster.getNamesystem().getPendingDeletionBlocks());
        cluster.restartNameNode(true);
        waitForNumPendingDeletionBlocks(0);
        Assert.assertEquals(3, cluster.getNamesystem().getBlocksTotal());
        Assert.assertEquals(0, cluster.getNamesystem().getPendingDeletionBlocks());
    }
}

