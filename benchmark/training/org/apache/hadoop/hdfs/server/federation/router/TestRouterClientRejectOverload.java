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
package org.apache.hadoop.hdfs.server.federation.router;


import java.net.URI;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.federation.FederationTestUtils;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.apache.hadoop.hdfs.server.federation.StateStoreDFSCluster;
import org.apache.hadoop.hdfs.server.federation.metrics.FederationRPCMetrics;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the Router overload control which rejects requests when the RPC client
 * is overloaded. This feature is managed by
 * {@link RBFConfigKeys#DFS_ROUTER_CLIENT_REJECT_OVERLOAD}.
 */
public class TestRouterClientRejectOverload {
    private static final Logger LOG = LoggerFactory.getLogger(TestRouterClientRejectOverload.class);

    private StateStoreDFSCluster cluster;

    @Test
    public void testWithoutOverloadControl() throws Exception {
        setupCluster(false);
        // Nobody should get overloaded
        testOverloaded(0);
        // Set subcluster 0 as slow
        MiniDFSCluster dfsCluster = cluster.getCluster();
        NameNode nn0 = dfsCluster.getNameNode(0);
        FederationTestUtils.simulateSlowNamenode(nn0, 1);
        // Nobody should get overloaded, but it will be really slow
        testOverloaded(0);
        // No rejected requests expected
        for (MiniRouterDFSCluster.RouterContext router : cluster.getRouters()) {
            FederationRPCMetrics rpcMetrics = router.getRouter().getRpcServer().getRPCMetrics();
            Assert.assertEquals(0, rpcMetrics.getProxyOpFailureClientOverloaded());
        }
    }

    @Test
    public void testOverloadControl() throws Exception {
        setupCluster(true);
        List<MiniRouterDFSCluster.RouterContext> routers = cluster.getRouters();
        FederationRPCMetrics rpcMetrics0 = routers.get(0).getRouter().getRpcServer().getRPCMetrics();
        FederationRPCMetrics rpcMetrics1 = routers.get(1).getRouter().getRpcServer().getRPCMetrics();
        // Nobody should get overloaded
        testOverloaded(0);
        Assert.assertEquals(0, rpcMetrics0.getProxyOpFailureClientOverloaded());
        Assert.assertEquals(0, rpcMetrics1.getProxyOpFailureClientOverloaded());
        // Set subcluster 0 as slow
        MiniDFSCluster dfsCluster = cluster.getCluster();
        NameNode nn0 = dfsCluster.getNameNode(0);
        FederationTestUtils.simulateSlowNamenode(nn0, 1);
        // The subcluster should be overloaded now and reject 4-5 requests
        testOverloaded(4, 6);
        Assert.assertTrue((((rpcMetrics0.getProxyOpFailureClientOverloaded()) + (rpcMetrics1.getProxyOpFailureClientOverloaded())) >= 4));
        // Client using HA with 2 Routers
        // A single Router gets overloaded, but 2 will handle it
        Configuration clientConf = cluster.getRouterClientConf();
        // Each Router should get a similar number of ops (>=8) out of 2*10
        long iniProxyOps0 = rpcMetrics0.getProxyOps();
        long iniProxyOps1 = rpcMetrics1.getProxyOps();
        testOverloaded(0, 0, new URI("hdfs://fed/"), clientConf, 10);
        long proxyOps0 = (rpcMetrics0.getProxyOps()) - iniProxyOps0;
        long proxyOps1 = (rpcMetrics1.getProxyOps()) - iniProxyOps1;
        Assert.assertEquals((2 * 10), (proxyOps0 + proxyOps1));
        Assert.assertTrue((proxyOps0 + " operations: not distributed"), (proxyOps0 >= 8));
        Assert.assertTrue((proxyOps1 + " operations: not distributed"), (proxyOps1 >= 8));
    }
}

