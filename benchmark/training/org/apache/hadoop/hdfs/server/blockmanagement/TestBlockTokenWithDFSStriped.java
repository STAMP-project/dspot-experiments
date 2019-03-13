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


import DFSConfigKeys.DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY;
import DFSConfigKeys.DFS_DATANODE_ADDRESS_KEY;
import DFSConfigKeys.DFS_DATANODE_IPC_ADDRESS_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.StripedFileTestUtil;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.server.balancer.TestBalancer;
import org.apache.hadoop.net.ServerSocketUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class TestBlockTokenWithDFSStriped extends TestBlockTokenWithDFS {
    private final ErasureCodingPolicy ecPolicy = StripedFileTestUtil.getDefaultECPolicy();

    private final int dataBlocks = ecPolicy.getNumDataUnits();

    private final int parityBlocks = ecPolicy.getNumParityUnits();

    private final int cellSize = ecPolicy.getCellSize();

    private final int stripesPerBlock = 4;

    private final int numDNs = ((dataBlocks) + (parityBlocks)) + 2;

    private MiniDFSCluster cluster;

    private Configuration conf;

    {
        TestBlockTokenWithDFS.BLOCK_SIZE = (cellSize) * (stripesPerBlock);
        TestBlockTokenWithDFS.FILE_SIZE = ((TestBlockTokenWithDFS.BLOCK_SIZE) * (dataBlocks)) * 3;
    }

    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    @Test
    @Override
    public void testRead() throws Exception {
        conf = getConf();
        /* prefer non-ephemeral port to avoid conflict with tests using
        ephemeral ports on MiniDFSCluster#restartDataNode(true).
         */
        Configuration[] overlays = new Configuration[numDNs];
        for (int i = 0; i < (overlays.length); i++) {
            int offset = i * 10;
            Configuration c = new Configuration();
            c.set(DFS_DATANODE_ADDRESS_KEY, ("127.0.0.1:" + (ServerSocketUtil.getPort((19866 + offset), 100))));
            c.set(DFS_DATANODE_IPC_ADDRESS_KEY, ("127.0.0.1:" + (ServerSocketUtil.getPort((19867 + offset), 100))));
            overlays[i] = c;
        }
        cluster = new MiniDFSCluster.Builder(conf).nameNodePort(ServerSocketUtil.getPort(18020, 100)).nameNodeHttpPort(ServerSocketUtil.getPort(19870, 100)).numDataNodes(numDNs).build();
        cluster.getFileSystem().enableErasureCodingPolicy(StripedFileTestUtil.getDefaultECPolicy().getName());
        cluster.getFileSystem().getClient().setErasureCodingPolicy("/", StripedFileTestUtil.getDefaultECPolicy().getName());
        try {
            cluster.waitActive();
            doTestRead(conf, cluster, true);
        } finally {
            if ((cluster) != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    @Override
    public void testAppend() throws Exception {
        // TODO: support Append for striped file
    }

    @Test
    @Override
    public void testEnd2End() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY, true);
        new TestBalancer().integrationTestWithStripedFile(conf);
    }
}

