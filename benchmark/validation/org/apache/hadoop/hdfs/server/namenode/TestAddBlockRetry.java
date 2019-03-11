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


import CreateFlag.CREATE;
import FSDirWriteFileOp.ValidateAddBlockResult;
import HdfsConstants.GRANDFATHER_INODE_ID;
import java.util.EnumSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeStorageInfo;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Race between two threads simultaneously calling
 * FSNamesystem.getAdditionalBlock().
 */
public class TestAddBlockRetry {
    public static final Logger LOG = LoggerFactory.getLogger(TestAddBlockRetry.class);

    private static final short REPLICATION = 3;

    private Configuration conf;

    private MiniDFSCluster cluster;

    /**
     * Retry addBlock() while another thread is in chooseTarget().
     * See HDFS-4452.
     */
    @Test
    public void testRetryAddBlockWhileInChooseTarget() throws Exception {
        final String src = "/testRetryAddBlockWhileInChooseTarget";
        final FSNamesystem ns = cluster.getNamesystem();
        final NamenodeProtocols nn = cluster.getNameNodeRpc();
        // create file
        nn.create(src, FsPermission.getFileDefault(), "clientName", new org.apache.hadoop.io.EnumSetWritable<org.apache.hadoop.fs.CreateFlag>(EnumSet.of(CREATE)), true, ((short) (3)), 1024, null, null, null);
        // start first addBlock()
        TestAddBlockRetry.LOG.info(("Starting first addBlock for " + src));
        LocatedBlock[] onRetryBlock = new LocatedBlock[1];
        ns.readLock();
        FSDirWriteFileOp.ValidateAddBlockResult r;
        FSPermissionChecker pc = Mockito.mock(FSPermissionChecker.class);
        try {
            r = FSDirWriteFileOp.validateAddBlock(ns, pc, src, GRANDFATHER_INODE_ID, "clientName", null, onRetryBlock);
        } finally {
            ns.readUnlock();
        }
        DatanodeStorageInfo[] targets = FSDirWriteFileOp.chooseTargetForNewBlock(ns.getBlockManager(), src, null, null, null, r);
        Assert.assertNotNull("Targets must be generated", targets);
        // run second addBlock()
        TestAddBlockRetry.LOG.info(("Starting second addBlock for " + src));
        nn.addBlock(src, "clientName", null, null, GRANDFATHER_INODE_ID, null, null);
        Assert.assertTrue("Penultimate block must be complete", checkFileProgress(src, false));
        LocatedBlocks lbs = nn.getBlockLocations(src, 0, Long.MAX_VALUE);
        Assert.assertEquals("Must be one block", 1, lbs.getLocatedBlocks().size());
        LocatedBlock lb2 = lbs.get(0);
        Assert.assertEquals("Wrong replication", TestAddBlockRetry.REPLICATION, lb2.getLocations().length);
        // continue first addBlock()
        ns.writeLock();
        LocatedBlock newBlock;
        try {
            newBlock = FSDirWriteFileOp.storeAllocatedBlock(ns, src, GRANDFATHER_INODE_ID, "clientName", null, targets);
        } finally {
            ns.writeUnlock();
        }
        Assert.assertEquals("Blocks are not equal", lb2.getBlock(), newBlock.getBlock());
        // check locations
        lbs = nn.getBlockLocations(src, 0, Long.MAX_VALUE);
        Assert.assertEquals("Must be one block", 1, lbs.getLocatedBlocks().size());
        LocatedBlock lb1 = lbs.get(0);
        Assert.assertEquals("Wrong replication", TestAddBlockRetry.REPLICATION, lb1.getLocations().length);
        Assert.assertEquals("Blocks are not equal", lb1.getBlock(), lb2.getBlock());
    }

    /* Since NameNode will not persist any locations of the block, addBlock()
    retry call after restart NN should re-select the locations and return to
    client. refer HDFS-5257
     */
    @Test
    public void testAddBlockRetryShouldReturnBlockWithLocations() throws Exception {
        final String src = "/testAddBlockRetryShouldReturnBlockWithLocations";
        NamenodeProtocols nameNodeRpc = cluster.getNameNodeRpc();
        // create file
        nameNodeRpc.create(src, FsPermission.getFileDefault(), "clientName", new org.apache.hadoop.io.EnumSetWritable<org.apache.hadoop.fs.CreateFlag>(EnumSet.of(CREATE)), true, ((short) (3)), 1024, null, null, null);
        // start first addBlock()
        TestAddBlockRetry.LOG.info(("Starting first addBlock for " + src));
        LocatedBlock lb1 = nameNodeRpc.addBlock(src, "clientName", null, null, GRANDFATHER_INODE_ID, null, null);
        Assert.assertTrue("Block locations should be present", ((lb1.getLocations().length) > 0));
        cluster.restartNameNode();
        nameNodeRpc = cluster.getNameNodeRpc();
        LocatedBlock lb2 = nameNodeRpc.addBlock(src, "clientName", null, null, GRANDFATHER_INODE_ID, null, null);
        Assert.assertEquals("Blocks are not equal", lb1.getBlock(), lb2.getBlock());
        Assert.assertTrue("Wrong locations with retry", ((lb2.getLocations().length) > 0));
    }
}

