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
package org.apache.hadoop.hdfs.server.namenode.metrics;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import DFSConfigKeys.DFS_HEARTBEAT_INTERVAL_KEY;
import DFSConfigKeys.DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY;
import HdfsFileStatus.EMPTY_NAME;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.test.MetricsAsserts;
import org.junit.Test;


/**
 * Test case for FilesInGetListingOps metric in Namenode
 */
public class TestNNMetricFilesInGetListingOps {
    private static final Configuration CONF = new HdfsConfiguration();

    private static final String NN_METRICS = "NameNodeActivity";

    static {
        TestNNMetricFilesInGetListingOps.CONF.setLong(DFS_BLOCK_SIZE_KEY, 100);
        TestNNMetricFilesInGetListingOps.CONF.setInt(DFS_BYTES_PER_CHECKSUM_KEY, 1);
        TestNNMetricFilesInGetListingOps.CONF.setLong(DFS_HEARTBEAT_INTERVAL_KEY, 1L);
        TestNNMetricFilesInGetListingOps.CONF.setInt(DFS_NAMENODE_REDUNDANCY_INTERVAL_SECONDS_KEY, 1);
    }

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private final Random rand = new Random();

    @Test
    public void testFilesInGetListingOps() throws Exception {
        createFile("/tmp1/t1", 3200, ((short) (3)));
        createFile("/tmp1/t2", 3200, ((short) (3)));
        createFile("/tmp2/t1", 3200, ((short) (3)));
        createFile("/tmp2/t2", 3200, ((short) (3)));
        cluster.getNameNodeRpc().getListing("/tmp1", EMPTY_NAME, false);
        MetricsAsserts.assertCounter("FilesInGetListingOps", 2L, MetricsAsserts.getMetrics(TestNNMetricFilesInGetListingOps.NN_METRICS));
        cluster.getNameNodeRpc().getListing("/tmp2", EMPTY_NAME, false);
        MetricsAsserts.assertCounter("FilesInGetListingOps", 4L, MetricsAsserts.getMetrics(TestNNMetricFilesInGetListingOps.NN_METRICS));
    }
}

