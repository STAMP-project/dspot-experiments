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


import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.server.balancer.TestBalancer;
import org.apache.log4j.Level;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test fsck with multiple NameNodes
 */
public class TestFsckWithMultipleNameNodes {
    static final Logger LOG = LoggerFactory.getLogger(TestFsckWithMultipleNameNodes.class);

    {
        DFSTestUtil.setNameNodeLogLevel(Level.ALL);
    }

    private static final String FILE_NAME = "/tmp.txt";

    private static final Path FILE_PATH = new Path(TestFsckWithMultipleNameNodes.FILE_NAME);

    private static final Random RANDOM = new Random();

    static {
        TestBalancer.initTestSetup();
    }

    /**
     * Common objects used in various methods.
     */
    private static class Suite {
        final MiniDFSCluster cluster;

        final ClientProtocol[] clients;

        final short replication;

        Suite(MiniDFSCluster cluster, final int nNameNodes, final int nDataNodes) throws IOException {
            this.cluster = cluster;
            clients = new ClientProtocol[nNameNodes];
            for (int i = 0; i < nNameNodes; i++) {
                clients[i] = cluster.getNameNode(i).getRpcServer();
            }
            replication = ((short) (Math.max(1, (nDataNodes - 1))));
        }

        /**
         * create a file with a length of <code>fileLen</code>
         */
        private void createFile(int index, long len) throws IOException, InterruptedException, TimeoutException {
            final FileSystem fs = cluster.getFileSystem(index);
            DFSTestUtil.createFile(fs, TestFsckWithMultipleNameNodes.FILE_PATH, len, replication, TestFsckWithMultipleNameNodes.RANDOM.nextLong());
            DFSTestUtil.waitReplication(fs, TestFsckWithMultipleNameNodes.FILE_PATH, replication);
        }
    }

    /**
     * Test a cluster with even distribution,
     * then a new empty node is added to the cluster
     */
    @Test
    public void testFsck() throws Exception {
        final Configuration conf = TestFsckWithMultipleNameNodes.createConf();
        runTest(3, 1, conf);
    }
}

