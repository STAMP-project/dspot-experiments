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


import RBFConfigKeys.DFS_ROUTER_ADMIN_ADDRESS_KEY;
import RouterServiceState.RUNNING;
import RouterServiceState.SAFEMODE;
import STATE.INITED;
import STATE.STARTED;
import STATE.STOPPED;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.tools.federation.RouterAdmin;
import org.apache.hadoop.ipc.StandbyException;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the safe mode for the {@link Router} controlled by
 * {@link RouterSafemodeService}.
 */
public class TestRouterSafemode {
    private Router router;

    private static Configuration conf;

    @Test
    public void testSafemodeService() throws IOException {
        RouterSafemodeService server = new RouterSafemodeService(router);
        server.init(TestRouterSafemode.conf);
        Assert.assertEquals(INITED, server.getServiceState());
        server.start();
        Assert.assertEquals(STARTED, server.getServiceState());
        server.stop();
        Assert.assertEquals(STOPPED, server.getServiceState());
        server.close();
    }

    @Test
    public void testRouterExitSafemode() throws IOException, IllegalStateException, InterruptedException {
        Assert.assertTrue(router.getSafemodeService().isInSafeMode());
        verifyRouter(SAFEMODE);
        // Wait for initial time in milliseconds
        long interval = (TestRouterSafemode.conf.getTimeDuration(RBFConfigKeys.DFS_ROUTER_SAFEMODE_EXTENSION, TimeUnit.SECONDS.toMillis(2), TimeUnit.MILLISECONDS)) + (TestRouterSafemode.conf.getTimeDuration(RBFConfigKeys.DFS_ROUTER_CACHE_TIME_TO_LIVE_MS, TimeUnit.SECONDS.toMillis(1), TimeUnit.MILLISECONDS));
        Thread.sleep(interval);
        Assert.assertFalse(router.getSafemodeService().isInSafeMode());
        verifyRouter(RUNNING);
    }

    @Test
    public void testRouterEnterSafemode() throws IOException, IllegalStateException, InterruptedException {
        // Verify starting state
        Assert.assertTrue(router.getSafemodeService().isInSafeMode());
        verifyRouter(SAFEMODE);
        // We should be in safe mode for DFS_ROUTER_SAFEMODE_EXTENSION time
        long interval0 = (TestRouterSafemode.conf.getTimeDuration(RBFConfigKeys.DFS_ROUTER_SAFEMODE_EXTENSION, TimeUnit.SECONDS.toMillis(2), TimeUnit.MILLISECONDS)) - 1000;
        long t0 = Time.now();
        while (((Time.now()) - t0) < interval0) {
            verifyRouter(SAFEMODE);
            Thread.sleep(100);
        } 
        // We wait some time for the state to propagate
        long interval1 = 1000 + (2 * (TestRouterSafemode.conf.getTimeDuration(RBFConfigKeys.DFS_ROUTER_CACHE_TIME_TO_LIVE_MS, TimeUnit.SECONDS.toMillis(1), TimeUnit.MILLISECONDS)));
        Thread.sleep(interval1);
        // Running
        Assert.assertFalse(router.getSafemodeService().isInSafeMode());
        verifyRouter(RUNNING);
        // Disable cache
        router.getStateStore().stopCacheUpdateService();
        // Wait until the State Store cache is stale in milliseconds
        long interval2 = (TestRouterSafemode.conf.getTimeDuration(RBFConfigKeys.DFS_ROUTER_SAFEMODE_EXPIRATION, TimeUnit.SECONDS.toMillis(2), TimeUnit.MILLISECONDS)) + (2 * (TestRouterSafemode.conf.getTimeDuration(RBFConfigKeys.DFS_ROUTER_CACHE_TIME_TO_LIVE_MS, TimeUnit.SECONDS.toMillis(1), TimeUnit.MILLISECONDS)));
        Thread.sleep(interval2);
        // Safemode
        Assert.assertTrue(router.getSafemodeService().isInSafeMode());
        verifyRouter(SAFEMODE);
    }

    @Test
    public void testRouterRpcSafeMode() throws IOException, IllegalStateException {
        Assert.assertTrue(router.getSafemodeService().isInSafeMode());
        verifyRouter(SAFEMODE);
        // If the Router is in Safe Mode, we should get a SafeModeException
        boolean exception = false;
        try {
            router.getRpcServer().delete("/testfile.txt", true);
            Assert.fail("We should have thrown a safe mode exception");
        } catch (StandbyException sme) {
            exception = true;
        }
        Assert.assertTrue("We should have thrown a safe mode exception", exception);
    }

    @Test
    public void testRouterManualSafeMode() throws Exception {
        InetSocketAddress adminAddr = router.getAdminServerAddress();
        TestRouterSafemode.conf.setSocketAddr(DFS_ROUTER_ADMIN_ADDRESS_KEY, adminAddr);
        RouterAdmin admin = new RouterAdmin(TestRouterSafemode.conf);
        Assert.assertTrue(router.getSafemodeService().isInSafeMode());
        verifyRouter(SAFEMODE);
        // Wait until the Router exit start up safe mode
        long interval = (TestRouterSafemode.conf.getTimeDuration(RBFConfigKeys.DFS_ROUTER_SAFEMODE_EXTENSION, TimeUnit.SECONDS.toMillis(2), TimeUnit.MILLISECONDS)) + 300;
        Thread.sleep(interval);
        verifyRouter(RUNNING);
        // Now enter safe mode via Router admin command - it should work
        Assert.assertEquals(0, ToolRunner.run(admin, new String[]{ "-safemode", "enter" }));
        verifyRouter(SAFEMODE);
        // Wait for update interval of the safe mode service, it should still in
        // safe mode.
        interval = 2 * (TestRouterSafemode.conf.getTimeDuration(RBFConfigKeys.DFS_ROUTER_CACHE_TIME_TO_LIVE_MS, TimeUnit.SECONDS.toMillis(1), TimeUnit.MILLISECONDS));
        Thread.sleep(interval);
        verifyRouter(SAFEMODE);
        // Exit safe mode via admin command
        Assert.assertEquals(0, ToolRunner.run(admin, new String[]{ "-safemode", "leave" }));
        verifyRouter(RUNNING);
    }
}

