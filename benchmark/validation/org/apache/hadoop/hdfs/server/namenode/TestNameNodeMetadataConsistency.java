/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.hdfs.server.namenode;


import java.io.OutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfo;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManagerTestUtil;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestNameNodeMetadataConsistency {
    private static final Path filePath1 = new Path("/testdata1.txt");

    private static final Path filePath2 = new Path("/testdata2.txt");

    private static final String TEST_DATA_IN_FUTURE = "This is test data";

    private static final int SCAN_INTERVAL = 1;

    private static final int SCAN_WAIT = 3;

    MiniDFSCluster cluster;

    HdfsConfiguration conf;

    /**
     * This test creates a file and modifies the block generation stamp to number
     * that name node has not seen yet. It then asserts that name node moves into
     * safe mode while it is in startup mode.
     */
    @Test
    public void testGenerationStampInFuture() throws Exception {
        cluster.waitActive();
        FileSystem fs = cluster.getFileSystem();
        OutputStream ostream = fs.create(TestNameNodeMetadataConsistency.filePath1);
        ostream.write(TestNameNodeMetadataConsistency.TEST_DATA_IN_FUTURE.getBytes());
        ostream.close();
        // Re-write the Generation Stamp to a Generation Stamp in future.
        ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, TestNameNodeMetadataConsistency.filePath1);
        final long genStamp = block.getGenerationStamp();
        final int datanodeIndex = 0;
        cluster.changeGenStampOfBlock(datanodeIndex, block, (genStamp + 1));
        DataNodeTestUtils.runDirectoryScanner(cluster.getDataNodes().get(0));
        // stop the data node so that it won't remove block
        final MiniDFSCluster.DataNodeProperties dnProps = cluster.stopDataNode(datanodeIndex);
        // Simulate Namenode forgetting a Block
        cluster.restartNameNode(true);
        cluster.getNameNode().getNamesystem().writeLock();
        BlockInfo bInfo = cluster.getNameNode().getNamesystem().getBlockManager().getStoredBlock(block.getLocalBlock());
        bInfo.delete();
        cluster.getNameNode().getNamesystem().getBlockManager().removeBlock(bInfo);
        cluster.getNameNode().getNamesystem().writeUnlock();
        // we also need to tell block manager that we are in the startup path
        BlockManagerTestUtil.setStartupSafeModeForTest(cluster.getNameNode().getNamesystem().getBlockManager());
        cluster.restartDataNode(dnProps);
        waitForNumBytes(TestNameNodeMetadataConsistency.TEST_DATA_IN_FUTURE.length());
        // Make sure that we find all written bytes in future block
        Assert.assertEquals(TestNameNodeMetadataConsistency.TEST_DATA_IN_FUTURE.length(), cluster.getNameNode().getBytesWithFutureGenerationStamps());
        // Assert safemode reason
        Assert.assertTrue(cluster.getNameNode().getNamesystem().getSafeModeTip().contains("Name node detected blocks with generation stamps in future"));
    }

    /**
     * Pretty much the same tests as above but does not setup safeMode == true,
     * hence we should not have positive count of Blocks in future.
     */
    @Test
    public void testEnsureGenStampsIsStartupOnly() throws Exception {
        String testData = " This is test data";
        cluster.restartDataNodes();
        cluster.restartNameNodes();
        cluster.waitActive();
        FileSystem fs = cluster.getFileSystem();
        OutputStream ostream = fs.create(TestNameNodeMetadataConsistency.filePath2);
        ostream.write(testData.getBytes());
        ostream.close();
        ExtendedBlock block = DFSTestUtil.getFirstBlock(fs, TestNameNodeMetadataConsistency.filePath2);
        long genStamp = block.getGenerationStamp();
        // Re-write the Generation Stamp to a Generation Stamp in future.
        cluster.changeGenStampOfBlock(0, block, (genStamp + 1));
        MiniDFSCluster.DataNodeProperties dnProps = cluster.stopDataNode(0);
        // Simulate  Namenode forgetting a Block
        cluster.restartNameNode(true);
        BlockInfo bInfo = cluster.getNameNode().getNamesystem().getBlockManager().getStoredBlock(block.getLocalBlock());
        cluster.getNameNode().getNamesystem().writeLock();
        bInfo.delete();
        cluster.getNameNode().getNamesystem().getBlockManager().removeBlock(bInfo);
        cluster.getNameNode().getNamesystem().writeUnlock();
        cluster.restartDataNode(dnProps);
        waitForNumBytes(0);
        // Make sure that there are no bytes in future since isInStartupSafe
        // mode is not true.
        Assert.assertEquals(0, cluster.getNameNode().getBytesWithFutureGenerationStamps());
    }
}

