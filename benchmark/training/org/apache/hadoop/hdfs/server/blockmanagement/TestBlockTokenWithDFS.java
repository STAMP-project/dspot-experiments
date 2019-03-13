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


import DFSClient.LOG;
import DFSConfigKeys.DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.security.token.block.BlockTokenIdentifier;
import org.apache.hadoop.hdfs.security.token.block.BlockTokenSecretManager;
import org.apache.hadoop.hdfs.security.token.block.SecurityTestUtil;
import org.apache.hadoop.hdfs.server.balancer.TestBalancer;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.net.ServerSocketUtil;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;


public class TestBlockTokenWithDFS {
    protected static int BLOCK_SIZE = 1024;

    protected static int FILE_SIZE = 2 * (TestBlockTokenWithDFS.BLOCK_SIZE);

    private static final String FILE_TO_READ = "/fileToRead.dat";

    private static final String FILE_TO_WRITE = "/fileToWrite.dat";

    private static final String FILE_TO_APPEND = "/fileToAppend.dat";

    {
        GenericTestUtils.setLogLevel(LOG, Level.ALL);
    }

    /**
     * testing that APPEND operation can handle token expiration when
     * re-establishing pipeline is needed
     */
    @Test
    public void testAppend() throws Exception {
        MiniDFSCluster cluster = null;
        int numDataNodes = 2;
        Configuration conf = getConf(numDataNodes);
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDataNodes).build();
            cluster.waitActive();
            Assert.assertEquals(numDataNodes, cluster.getDataNodes().size());
            final NameNode nn = cluster.getNameNode();
            final BlockManager bm = nn.getNamesystem().getBlockManager();
            final BlockTokenSecretManager sm = bm.getBlockTokenSecretManager();
            // set a short token lifetime (1 second)
            SecurityTestUtil.setBlockTokenLifetime(sm, 1000L);
            Path fileToAppend = new Path(TestBlockTokenWithDFS.FILE_TO_APPEND);
            FileSystem fs = cluster.getFileSystem();
            byte[] expected = TestBlockTokenWithDFS.generateBytes(TestBlockTokenWithDFS.FILE_SIZE);
            // write a one-byte file
            FSDataOutputStream stm = TestBlockTokenWithDFS.writeFile(fs, fileToAppend, ((short) (numDataNodes)), TestBlockTokenWithDFS.BLOCK_SIZE);
            stm.write(expected, 0, 1);
            stm.close();
            // open the file again for append
            stm = fs.append(fileToAppend);
            int mid = (expected.length) - 1;
            stm.write(expected, 1, (mid - 1));
            stm.hflush();
            /* wait till token used in stm expires */
            Token<BlockTokenIdentifier> token = DFSTestUtil.getBlockToken(stm);
            while (!(SecurityTestUtil.isBlockTokenExpired(token))) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            } 
            // remove a datanode to force re-establishing pipeline
            cluster.stopDataNode(0);
            // append the rest of the file
            stm.write(expected, mid, ((expected.length) - mid));
            stm.close();
            // check if append is successful
            FSDataInputStream in5 = fs.open(fileToAppend);
            Assert.assertTrue(checkFile1(in5, expected));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * testing that WRITE operation can handle token expiration when
     * re-establishing pipeline is needed
     */
    @Test
    public void testWrite() throws Exception {
        MiniDFSCluster cluster = null;
        int numDataNodes = 2;
        Configuration conf = getConf(numDataNodes);
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDataNodes).build();
            cluster.waitActive();
            Assert.assertEquals(numDataNodes, cluster.getDataNodes().size());
            final NameNode nn = cluster.getNameNode();
            final BlockManager bm = nn.getNamesystem().getBlockManager();
            final BlockTokenSecretManager sm = bm.getBlockTokenSecretManager();
            // set a short token lifetime (1 second)
            SecurityTestUtil.setBlockTokenLifetime(sm, 1000L);
            Path fileToWrite = new Path(TestBlockTokenWithDFS.FILE_TO_WRITE);
            FileSystem fs = cluster.getFileSystem();
            byte[] expected = TestBlockTokenWithDFS.generateBytes(TestBlockTokenWithDFS.FILE_SIZE);
            FSDataOutputStream stm = TestBlockTokenWithDFS.writeFile(fs, fileToWrite, ((short) (numDataNodes)), TestBlockTokenWithDFS.BLOCK_SIZE);
            // write a partial block
            int mid = (expected.length) - 1;
            stm.write(expected, 0, mid);
            stm.hflush();
            /* wait till token used in stm expires */
            Token<BlockTokenIdentifier> token = DFSTestUtil.getBlockToken(stm);
            while (!(SecurityTestUtil.isBlockTokenExpired(token))) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {
                }
            } 
            // remove a datanode to force re-establishing pipeline
            cluster.stopDataNode(0);
            // write the rest of the file
            stm.write(expected, mid, ((expected.length) - mid));
            stm.close();
            // check if write is successful
            FSDataInputStream in4 = fs.open(fileToWrite);
            Assert.assertTrue(checkFile1(in4, expected));
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testRead() throws Exception {
        MiniDFSCluster cluster = null;
        int numDataNodes = 2;
        Configuration conf = getConf(numDataNodes);
        try {
            // prefer non-ephemeral port to avoid port collision on restartNameNode
            cluster = new MiniDFSCluster.Builder(conf).nameNodePort(ServerSocketUtil.getPort(18020, 100)).nameNodeHttpPort(ServerSocketUtil.getPort(19870, 100)).numDataNodes(numDataNodes).build();
            cluster.waitActive();
            Assert.assertEquals(numDataNodes, cluster.getDataNodes().size());
            doTestRead(conf, cluster, false);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    /**
     * Integration testing of access token, involving NN, DN, and Balancer
     */
    @Test
    public void testEnd2End() throws Exception {
        Configuration conf = new Configuration();
        conf.setBoolean(DFS_BLOCK_ACCESS_TOKEN_ENABLE_KEY, true);
        new TestBalancer().integrationTest(conf);
    }
}

