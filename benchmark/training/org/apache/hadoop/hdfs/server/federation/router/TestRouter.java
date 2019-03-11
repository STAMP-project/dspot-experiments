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


import DatanodeReportType.LIVE;
import STATE.STARTED;
import STATE.STOPPED;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.server.federation.RouterConfigBuilder;
import org.apache.hadoop.ipc.RemoteException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * The the safe mode for the {@link Router} controlled by
 * {@link SafeModeTimer}.
 */
public class TestRouter {
    private static Configuration conf;

    @Test
    public void testRouterService() throws IOException, InterruptedException {
        // Admin only
        TestRouter.testRouterStartup(new RouterConfigBuilder(TestRouter.conf).admin().build());
        // Http only
        TestRouter.testRouterStartup(new RouterConfigBuilder(TestRouter.conf).http().build());
        // Rpc only
        TestRouter.testRouterStartup(new RouterConfigBuilder(TestRouter.conf).rpc().build());
        // Safemode only
        TestRouter.testRouterStartup(new RouterConfigBuilder(TestRouter.conf).rpc().safemode().build());
        // Metrics only
        TestRouter.testRouterStartup(new RouterConfigBuilder(TestRouter.conf).metrics().build());
        // Statestore only
        TestRouter.testRouterStartup(new RouterConfigBuilder(TestRouter.conf).stateStore().build());
        // Heartbeat only
        TestRouter.testRouterStartup(new RouterConfigBuilder(TestRouter.conf).heartbeat().build());
        // Run with all services
        TestRouter.testRouterStartup(new RouterConfigBuilder(TestRouter.conf).all().build());
    }

    @Test
    public void testRouterRestartRpcService() throws IOException {
        // Start
        Router router = new Router();
        router.init(new RouterConfigBuilder(TestRouter.conf).rpc().build());
        router.start();
        // Verify RPC server is running
        Assert.assertNotNull(router.getRpcServerAddress());
        RouterRpcServer rpcServer = router.getRpcServer();
        Assert.assertNotNull(rpcServer);
        Assert.assertEquals(STARTED, rpcServer.getServiceState());
        // Stop router and RPC server
        router.stop();
        Assert.assertEquals(STOPPED, rpcServer.getServiceState());
        router.close();
    }

    @Test
    public void testRouterRpcWithNoSubclusters() throws IOException {
        Router router = new Router();
        router.init(new RouterConfigBuilder(TestRouter.conf).rpc().build());
        router.start();
        InetSocketAddress serverAddress = router.getRpcServerAddress();
        DFSClient dfsClient = new DFSClient(serverAddress, TestRouter.conf);
        try {
            dfsClient.create("/test.txt", false);
            Assert.fail("Create with no subclusters should fail");
        } catch (RemoteException e) {
            assertExceptionContains("Cannot find locations for /test.txt", e);
        }
        try {
            dfsClient.datanodeReport(LIVE);
            Assert.fail("Get datanode reports with no subclusters should fail");
        } catch (IOException e) {
            assertExceptionContains("No remote locations available", e);
        }
        dfsClient.close();
        router.stop();
        router.close();
    }

    @Test
    public void testRouterIDInRouterRpcClient() throws Exception {
        Router router = new Router();
        router.init(new RouterConfigBuilder(TestRouter.conf).rpc().build());
        router.setRouterId("Router-0");
        RemoteMethod remoteMethod = Mockito.mock(RemoteMethod.class);
        intercept(IOException.class, "Router-0", () -> router.getRpcServer().getRPCClient().invokeSingle("ns0", remoteMethod));
        router.stop();
        router.close();
    }
}

