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


import DFSConfigKeys.DFS_DATANODE_BALANCE_BANDWIDTHPERSEC_KEY;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test ensures that the balancer bandwidth is dynamically adjusted
 * correctly.
 */
public class TestBalancerBandwidth {
    private static final Configuration conf = new Configuration();

    private static final int NUM_OF_DATANODES = 2;

    private static final int DEFAULT_BANDWIDTH = 1024 * 1024;

    public static final Logger LOG = LoggerFactory.getLogger(TestBalancerBandwidth.class);

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final PrintStream outStream = new PrintStream(outContent);

    @Test
    public void testBalancerBandwidth() throws Exception {
        /* Set bandwidthPerSec to a low value of 1M bps. */
        TestBalancerBandwidth.conf.setLong(DFS_DATANODE_BALANCE_BANDWIDTHPERSEC_KEY, TestBalancerBandwidth.DEFAULT_BANDWIDTH);
        /* Create and start cluster */
        try (MiniDFSCluster cluster = new MiniDFSCluster.Builder(TestBalancerBandwidth.conf).numDataNodes(TestBalancerBandwidth.NUM_OF_DATANODES).build()) {
            cluster.waitActive();
            DistributedFileSystem fs = cluster.getFileSystem();
            ArrayList<DataNode> datanodes = cluster.getDataNodes();
            // Ensure value from the configuration is reflected in the datanodes.
            Assert.assertEquals(TestBalancerBandwidth.DEFAULT_BANDWIDTH, ((long) (datanodes.get(0).getBalancerBandwidth())));
            Assert.assertEquals(TestBalancerBandwidth.DEFAULT_BANDWIDTH, ((long) (datanodes.get(1).getBalancerBandwidth())));
            DFSAdmin admin = new DFSAdmin(TestBalancerBandwidth.conf);
            String dn1Address = ((datanodes.get(0).ipcServer.getListenerAddress().getHostName()) + ":") + (datanodes.get(0).getIpcPort());
            String dn2Address = ((datanodes.get(1).ipcServer.getListenerAddress().getHostName()) + ":") + (datanodes.get(1).getIpcPort());
            // verifies the dfsadmin command execution
            String[] args = new String[]{ "-getBalancerBandwidth", dn1Address };
            runGetBalancerBandwidthCmd(admin, args, TestBalancerBandwidth.DEFAULT_BANDWIDTH);
            args = new String[]{ "-getBalancerBandwidth", dn2Address };
            runGetBalancerBandwidthCmd(admin, args, TestBalancerBandwidth.DEFAULT_BANDWIDTH);
            // Dynamically change balancer bandwidth and ensure the updated value
            // is reflected on the datanodes.
            long newBandwidth = 12 * (TestBalancerBandwidth.DEFAULT_BANDWIDTH);// 12M bps

            fs.setBalancerBandwidth(newBandwidth);
            verifyBalancerBandwidth(datanodes, newBandwidth);
            // verifies the dfsadmin command execution
            args = new String[]{ "-getBalancerBandwidth", dn1Address };
            runGetBalancerBandwidthCmd(admin, args, newBandwidth);
            args = new String[]{ "-getBalancerBandwidth", dn2Address };
            runGetBalancerBandwidthCmd(admin, args, newBandwidth);
            // Dynamically change balancer bandwidth to 0. Balancer bandwidth on the
            // datanodes should remain as it was.
            fs.setBalancerBandwidth(0);
            verifyBalancerBandwidth(datanodes, newBandwidth);
            // verifies the dfsadmin command execution
            args = new String[]{ "-getBalancerBandwidth", dn1Address };
            runGetBalancerBandwidthCmd(admin, args, newBandwidth);
            args = new String[]{ "-getBalancerBandwidth", dn2Address };
            runGetBalancerBandwidthCmd(admin, args, newBandwidth);
        }
    }
}

