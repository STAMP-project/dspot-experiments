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
package org.apache.hadoop.hdfs.server.balancer;


import BalancingPolicy.Pool.INSTANCE;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import static Balancer.LOG;
import static org.apache.log4j.Level.TRACE;


/**
 * Test balancer with multiple NameNodes
 */
public class TestBalancerWithMultipleNameNodes {
    static final Logger LOG = LOG;

    {
        GenericTestUtils.setLogLevel(TestBalancerWithMultipleNameNodes.LOG, Level.TRACE);
        DFSTestUtil.setNameNodeLogLevel(TRACE);
    }

    private static final long CAPACITY = 500L;

    private static final String RACK0 = "/rack0";

    private static final String RACK1 = "/rack1";

    private static final String RACK2 = "/rack2";

    private static final String FILE_NAME = "/tmp.txt";

    private static final Path FILE_PATH = new Path(TestBalancerWithMultipleNameNodes.FILE_NAME);

    private static final Random RANDOM = new Random();

    static {
        TestBalancer.initTestSetup();
    }

    /**
     * Common objects used in various methods.
     */
    private static class Suite {
        final Configuration conf;

        final MiniDFSCluster cluster;

        final ClientProtocol[] clients;

        final short replication;

        final BalancerParameters parameters;

        Suite(MiniDFSCluster cluster, final int nNameNodes, final int nDataNodes, BalancerParameters parameters, Configuration conf) throws IOException {
            this.conf = conf;
            this.cluster = cluster;
            clients = new ClientProtocol[nNameNodes];
            for (int i = 0; i < nNameNodes; i++) {
                clients[i] = cluster.getNameNode(i).getRpcServer();
            }
            // hard coding replication factor to 1 so logical and raw HDFS size are
            // equal
            replication = 1;
            this.parameters = parameters;
        }

        Suite(MiniDFSCluster cluster, final int nNameNodes, final int nDataNodes, BalancerParameters parameters, Configuration conf, short replicationFactor) throws IOException {
            this.conf = conf;
            this.cluster = cluster;
            clients = new ClientProtocol[nNameNodes];
            for (int i = 0; i < nNameNodes; i++) {
                clients[i] = cluster.getNameNode(i).getRpcServer();
            }
            replication = replicationFactor;
            this.parameters = parameters;
        }
    }

    /**
     * Test a cluster with even distribution,
     * then a new empty node is added to the cluster.
     */
    @Test
    public void testTwoOneOne() throws Exception {
        final Configuration conf = TestBalancerWithMultipleNameNodes.createConf();
        runTest(2, new String[]{ TestBalancerWithMultipleNameNodes.RACK0 }, new String[]{ TestBalancerWithMultipleNameNodes.RACK0 }, conf, 2, null);
    }

    /**
     * Test unevenly distributed cluster
     */
    @Test
    public void testUnevenDistribution() throws Exception {
        final Configuration conf = TestBalancerWithMultipleNameNodes.createConf();
        unevenDistribution(2, 2, new long[]{ (30 * (TestBalancerWithMultipleNameNodes.CAPACITY)) / 100, (5 * (TestBalancerWithMultipleNameNodes.CAPACITY)) / 100 }, new long[]{ TestBalancerWithMultipleNameNodes.CAPACITY, TestBalancerWithMultipleNameNodes.CAPACITY }, new String[]{ TestBalancerWithMultipleNameNodes.RACK0, TestBalancerWithMultipleNameNodes.RACK1 }, conf);
    }

    @Test
    public void testBalancing1OutOf2Blockpools() throws Exception {
        final Configuration conf = TestBalancerWithMultipleNameNodes.createConf();
        unevenDistribution(2, 1, new long[]{ (30 * (TestBalancerWithMultipleNameNodes.CAPACITY)) / 100, (5 * (TestBalancerWithMultipleNameNodes.CAPACITY)) / 100 }, new long[]{ TestBalancerWithMultipleNameNodes.CAPACITY, TestBalancerWithMultipleNameNodes.CAPACITY }, new String[]{ TestBalancerWithMultipleNameNodes.RACK0, TestBalancerWithMultipleNameNodes.RACK1 }, conf);
    }

    @Test
    public void testBalancing2OutOf3Blockpools() throws Exception {
        final Configuration conf = TestBalancerWithMultipleNameNodes.createConf();
        unevenDistribution(3, 2, new long[]{ (30 * (TestBalancerWithMultipleNameNodes.CAPACITY)) / 100, (5 * (TestBalancerWithMultipleNameNodes.CAPACITY)) / 100, (10 * (TestBalancerWithMultipleNameNodes.CAPACITY)) / 100 }, new long[]{ TestBalancerWithMultipleNameNodes.CAPACITY, TestBalancerWithMultipleNameNodes.CAPACITY, TestBalancerWithMultipleNameNodes.CAPACITY }, new String[]{ TestBalancerWithMultipleNameNodes.RACK0, TestBalancerWithMultipleNameNodes.RACK1, TestBalancerWithMultipleNameNodes.RACK2 }, conf);
    }

    /**
     * Even distribution with 2 Namenodes, 4 Datanodes and 2 new Datanodes.
     */
    @Test(timeout = 600000)
    public void testTwoFourTwo() throws Exception {
        final Configuration conf = TestBalancerWithMultipleNameNodes.createConf();
        runTest(2, new String[]{ TestBalancerWithMultipleNameNodes.RACK0, TestBalancerWithMultipleNameNodes.RACK0, TestBalancerWithMultipleNameNodes.RACK1, TestBalancerWithMultipleNameNodes.RACK1 }, new String[]{ TestBalancerWithMultipleNameNodes.RACK2, TestBalancerWithMultipleNameNodes.RACK2 }, conf, 2, null);
    }

    @Test(timeout = 600000)
    public void testBalancingBlockpoolsWithBlockPoolPolicy() throws Exception {
        final Configuration conf = TestBalancerWithMultipleNameNodes.createConf();
        BalancerParameters balancerParameters = new BalancerParameters.Builder().setBalancingPolicy(INSTANCE).build();
        runTest(2, new String[]{ TestBalancerWithMultipleNameNodes.RACK0, TestBalancerWithMultipleNameNodes.RACK0, TestBalancerWithMultipleNameNodes.RACK1, TestBalancerWithMultipleNameNodes.RACK1 }, new String[]{ TestBalancerWithMultipleNameNodes.RACK2, TestBalancerWithMultipleNameNodes.RACK2 }, conf, 2, balancerParameters);
    }

    @Test(timeout = 600000)
    public void test1OutOf2BlockpoolsWithBlockPoolPolicy() throws Exception {
        final Configuration conf = TestBalancerWithMultipleNameNodes.createConf();
        BalancerParameters balancerParameters = new BalancerParameters.Builder().setBalancingPolicy(INSTANCE).build();
        runTest(2, new String[]{ TestBalancerWithMultipleNameNodes.RACK0, TestBalancerWithMultipleNameNodes.RACK0, TestBalancerWithMultipleNameNodes.RACK1, TestBalancerWithMultipleNameNodes.RACK1 }, new String[]{ TestBalancerWithMultipleNameNodes.RACK2, TestBalancerWithMultipleNameNodes.RACK2 }, conf, 1, balancerParameters);
    }
}

