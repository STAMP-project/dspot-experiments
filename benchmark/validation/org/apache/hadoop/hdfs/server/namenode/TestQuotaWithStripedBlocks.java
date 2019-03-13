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


import StorageType.DISK;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Make sure we correctly update the quota usage with the striped blocks.
 */
public class TestQuotaWithStripedBlocks {
    private int blockSize;

    private ErasureCodingPolicy ecPolicy;

    private int dataBlocks;

    private int parityBlocsk;

    private int groupSize;

    private int cellSize;

    private Path ecDir;

    private long diskQuota;

    private MiniDFSCluster cluster;

    private FSDirectory dir;

    private DistributedFileSystem dfs;

    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    @Test
    public void testUpdatingQuotaCount() throws Exception {
        final Path file = new Path(ecDir, "file");
        FSDataOutputStream out = null;
        try {
            out = dfs.create(file, ((short) (1)));
            INodeFile fileNode = dir.getINode4Write(file.toString()).asFile();
            ExtendedBlock previous = null;
            // Create striped blocks which have a cell in each block.
            Block newBlock = DFSTestUtil.addBlockToFile(true, cluster.getDataNodes(), dfs, cluster.getNamesystem(), file.toString(), fileNode, dfs.getClient().getClientName(), previous, 1, 0);
            previous = new ExtendedBlock(cluster.getNamesystem().getBlockPoolId(), newBlock);
            final INodeDirectory dirNode = dir.getINode4Write(ecDir.toString()).asDirectory();
            final long spaceUsed = dirNode.getDirectoryWithQuotaFeature().getSpaceConsumed().getStorageSpace();
            final long diskUsed = dirNode.getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(DISK);
            // When we add a new block we update the quota using the full block size.
            Assert.assertEquals(((blockSize) * (groupSize)), spaceUsed);
            Assert.assertEquals(((blockSize) * (groupSize)), diskUsed);
            dfs.getClient().getNamenode().complete(file.toString(), dfs.getClient().getClientName(), previous, fileNode.getId());
            final long actualSpaceUsed = dirNode.getDirectoryWithQuotaFeature().getSpaceConsumed().getStorageSpace();
            final long actualDiskUsed = dirNode.getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(DISK);
            // In this case the file's real size is cell size * block group size.
            Assert.assertEquals(((cellSize) * (groupSize)), actualSpaceUsed);
            Assert.assertEquals(((cellSize) * (groupSize)), actualDiskUsed);
        } finally {
            IOUtils.cleanup(null, out);
        }
    }
}

