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


import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import DFSConfigKeys.DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY;
import DFSConfigKeys.DFS_REPLICATION_KEY;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.BlockListAsLongs;
import org.apache.hadoop.hdfs.server.datanode.SimulatedFSDataset;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests the replication and injection of blocks of a DFS file for simulated storage.
 */
public class TestInjectionForSimulatedStorage {
    private final int checksumSize = 16;

    private final int blockSize = (checksumSize) * 2;

    private final int numBlocks = 4;

    private final int filesize = (blockSize) * (numBlocks);

    private final int numDataNodes = 4;

    private static final Logger LOG = LoggerFactory.getLogger("org.apache.hadoop.hdfs.TestInjectionForSimulatedStorage");

    /* This test makes sure that NameNode retries all the available blocks 
    for under replicated blocks. This test uses simulated storage and one
    of its features to inject blocks,

    It creates a file with several blocks and replication of 4. 
    The cluster is then shut down - NN retains its state but the DNs are 
    all simulated and hence loose their blocks. 
    The blocks are then injected in one of the DNs. The  expected behaviour is
    that the NN will arrange for themissing replica will be copied from a valid source.
     */
    @Test
    public void testInjection() throws IOException {
        MiniDFSCluster cluster = null;
        String testFile = "/replication-test-file";
        Path testPath = new Path(testFile);
        byte[] buffer = new byte[1024];
        for (int i = 0; i < (buffer.length); i++) {
            buffer[i] = '1';
        }
        try {
            Configuration conf = new HdfsConfiguration();
            conf.set(DFS_REPLICATION_KEY, Integer.toString(numDataNodes));
            conf.setInt(DFS_BYTES_PER_CHECKSUM_KEY, checksumSize);
            SimulatedFSDataset.setFactory(conf);
            // first time format
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDataNodes).build();
            cluster.waitActive();
            String bpid = cluster.getNamesystem().getBlockPoolId();
            DFSClient dfsClient = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), conf);
            DFSTestUtil.createFile(cluster.getFileSystem(), testPath, filesize, filesize, blockSize, ((short) (numDataNodes)), 0L);
            waitForBlockReplication(testFile, dfsClient.getNamenode(), numDataNodes, 20);
            List<Map<DatanodeStorage, BlockListAsLongs>> blocksList = cluster.getAllBlockReports(bpid);
            cluster.shutdown();
            cluster = null;
            /* Start the MiniDFSCluster with more datanodes since once a writeBlock
            to a datanode node fails, same block can not be written to it
            immediately. In our case some replication attempts will fail.
             */
            TestInjectionForSimulatedStorage.LOG.info("Restarting minicluster");
            conf = new HdfsConfiguration();
            SimulatedFSDataset.setFactory(conf);
            conf.set(DFS_NAMENODE_SAFEMODE_THRESHOLD_PCT_KEY, "0.0f");
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(((numDataNodes) * 2)).format(false).build();
            cluster.waitActive();
            Set<Block> uniqueBlocks = new HashSet<Block>();
            for (Map<DatanodeStorage, BlockListAsLongs> map : blocksList) {
                for (BlockListAsLongs blockList : map.values()) {
                    for (Block b : blockList) {
                        uniqueBlocks.add(new Block(b));
                    }
                }
            }
            // Insert all the blocks in the first data node
            TestInjectionForSimulatedStorage.LOG.info((("Inserting " + (uniqueBlocks.size())) + " blocks"));
            cluster.injectBlocks(0, uniqueBlocks, null);
            dfsClient = new DFSClient(new InetSocketAddress("localhost", cluster.getNameNodePort()), conf);
            waitForBlockReplication(testFile, dfsClient.getNamenode(), numDataNodes, (-1));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

