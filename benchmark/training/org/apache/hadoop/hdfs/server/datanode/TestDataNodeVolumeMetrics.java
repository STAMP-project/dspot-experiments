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


import java.io.File;
import java.util.ArrayList;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test class for DataNodeVolumeMetrics.
 */
public class TestDataNodeVolumeMetrics {
    private static final Logger LOG = LoggerFactory.getLogger(TestDataNodeVolumeMetrics.class);

    private static final int BLOCK_SIZE = 1024;

    private static final short REPL = 1;

    private static final int NUM_DATANODES = 1;

    @Rule
    public Timeout timeout = new Timeout(300000);

    @Test
    public void testVolumeMetrics() throws Exception {
        MiniDFSCluster cluster = setupClusterForVolumeMetrics();
        try {
            FileSystem fs = cluster.getFileSystem();
            final Path fileName = new Path("/test.dat");
            final long fileLen = (Integer.MAX_VALUE) + 1L;
            DFSTestUtil.createFile(fs, fileName, false, TestDataNodeVolumeMetrics.BLOCK_SIZE, fileLen, fs.getDefaultBlockSize(fileName), TestDataNodeVolumeMetrics.REPL, 1L, true);
            try (FSDataOutputStream out = fs.append(fileName)) {
                out.writeBytes("hello world");
                hsync();
            }
            verifyDataNodeVolumeMetrics(fs, cluster, fileName);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }

    @Test
    public void testVolumeMetricsWithVolumeDepartureArrival() throws Exception {
        MiniDFSCluster cluster = setupClusterForVolumeMetrics();
        try {
            FileSystem fs = cluster.getFileSystem();
            final Path fileName = new Path("/test.dat");
            final long fileLen = (Integer.MAX_VALUE) + 1L;
            DFSTestUtil.createFile(fs, fileName, false, TestDataNodeVolumeMetrics.BLOCK_SIZE, fileLen, fs.getDefaultBlockSize(fileName), TestDataNodeVolumeMetrics.REPL, 1L, true);
            try (FSDataOutputStream out = fs.append(fileName)) {
                out.writeBytes("hello world");
                hsync();
            }
            ArrayList<DataNode> dns = cluster.getDataNodes();
            Assert.assertTrue("DN1 should be up", dns.get(0).isDatanodeUp());
            final File dn1Vol2 = cluster.getInstanceStorageDir(0, 1);
            DataNodeTestUtils.injectDataDirFailure(dn1Vol2);
            verifyDataNodeVolumeMetrics(fs, cluster, fileName);
            DataNodeTestUtils.restoreDataDirFromFailure(dn1Vol2);
            DataNodeTestUtils.reconfigureDataNode(dns.get(0), dn1Vol2);
            verifyDataNodeVolumeMetrics(fs, cluster, fileName);
        } finally {
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

