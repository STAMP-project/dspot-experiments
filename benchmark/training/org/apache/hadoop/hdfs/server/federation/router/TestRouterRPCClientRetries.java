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


import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.server.federation.FederationTestUtils;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.apache.hadoop.hdfs.server.federation.StateStoreDFSCluster;
import org.apache.hadoop.hdfs.server.federation.metrics.FederationRPCMetrics;
import org.apache.hadoop.hdfs.server.federation.metrics.NamenodeBeanMetrics;
import org.apache.hadoop.hdfs.server.federation.resolver.MembershipNamenodeResolver;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.ipc.RemoteException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Test retry behavior of the Router RPC Client.
 */
public class TestRouterRPCClientRetries {
    private static StateStoreDFSCluster cluster;

    private static MiniRouterDFSCluster.NamenodeContext nnContext1;

    private static MiniRouterDFSCluster.RouterContext routerContext;

    private static MembershipNamenodeResolver resolver;

    private static ClientProtocol routerProtocol;

    @Rule
    public final Timeout testTimeout = new Timeout(100000);

    @Test
    public void testRetryWhenAllNameServiceDown() throws Exception {
        // shutdown the dfs cluster
        MiniDFSCluster dfsCluster = TestRouterRPCClientRetries.cluster.getCluster();
        dfsCluster.shutdown();
        // register an invalid namenode report
        registerInvalidNameReport();
        // Create a directory via the router
        String dirPath = "/testRetryWhenClusterisDown";
        FsPermission permission = new FsPermission("705");
        try {
            TestRouterRPCClientRetries.routerProtocol.mkdirs(dirPath, permission, false);
            Assert.fail("Should have thrown RemoteException error.");
        } catch (RemoteException e) {
            String ns0 = TestRouterRPCClientRetries.cluster.getNameservices().get(0);
            assertExceptionContains(("No namenode available under nameservice " + ns0), e);
        }
        // Verify the retry times, it should only retry one time.
        FederationRPCMetrics rpcMetrics = TestRouterRPCClientRetries.routerContext.getRouter().getRpcServer().getRPCMetrics();
        Assert.assertEquals(1, rpcMetrics.getProxyOpRetries());
    }

    @Test
    public void testRetryWhenOneNameServiceDown() throws Exception {
        // shutdown the dfs cluster
        MiniDFSCluster dfsCluster = TestRouterRPCClientRetries.cluster.getCluster();
        dfsCluster.shutdownNameNode(0);
        // register an invalid namenode report
        registerInvalidNameReport();
        DFSClient client = TestRouterRPCClientRetries.nnContext1.getClient();
        // Renew lease for the DFS client, it will succeed.
        TestRouterRPCClientRetries.routerProtocol.renewLease(client.getClientName());
        // Verify the retry times, it will retry one time for ns0.
        FederationRPCMetrics rpcMetrics = TestRouterRPCClientRetries.routerContext.getRouter().getRpcServer().getRPCMetrics();
        Assert.assertEquals(1, rpcMetrics.getProxyOpRetries());
    }

    @Test
    public void testNamenodeMetricsSlow() throws Exception {
        final Router router = TestRouterRPCClientRetries.routerContext.getRouter();
        final NamenodeBeanMetrics metrics = router.getNamenodeMetrics();
        // Initially, there are 4 DNs in total
        final String jsonString0 = metrics.getLiveNodes();
        Assert.assertEquals(4, TestRouterRPCClientRetries.getNumDatanodes(jsonString0));
        // The response should be cached
        Assert.assertEquals(jsonString0, metrics.getLiveNodes());
        // Check that the cached value gets updated eventually
        TestRouterRPCClientRetries.waitUpdateLiveNodes(jsonString0, metrics);
        final String jsonString2 = metrics.getLiveNodes();
        Assert.assertNotEquals(jsonString0, jsonString2);
        Assert.assertEquals(4, TestRouterRPCClientRetries.getNumDatanodes(jsonString2));
        // Making subcluster0 slow to reply, should only get DNs from nn1
        MiniDFSCluster dfsCluster = TestRouterRPCClientRetries.cluster.getCluster();
        NameNode nn0 = dfsCluster.getNameNode(0);
        FederationTestUtils.simulateSlowNamenode(nn0, 3);
        TestRouterRPCClientRetries.waitUpdateLiveNodes(jsonString2, metrics);
        final String jsonString3 = metrics.getLiveNodes();
        Assert.assertEquals(2, TestRouterRPCClientRetries.getNumDatanodes(jsonString3));
        // Making subcluster1 slow to reply, shouldn't get any DNs
        NameNode nn1 = dfsCluster.getNameNode(1);
        FederationTestUtils.simulateSlowNamenode(nn1, 3);
        TestRouterRPCClientRetries.waitUpdateLiveNodes(jsonString3, metrics);
        final String jsonString4 = metrics.getLiveNodes();
        Assert.assertEquals(0, TestRouterRPCClientRetries.getNumDatanodes(jsonString4));
    }
}

