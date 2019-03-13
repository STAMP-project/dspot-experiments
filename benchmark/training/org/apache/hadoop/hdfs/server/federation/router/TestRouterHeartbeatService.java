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


import java.io.IOException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.apache.hadoop.hdfs.server.federation.store.RouterStore;
import org.apache.hadoop.hdfs.server.federation.store.StateStoreService;
import org.apache.hadoop.hdfs.server.federation.store.protocol.GetRouterRegistrationRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.GetRouterRegistrationResponse;
import org.apache.hadoop.hdfs.server.federation.store.records.RouterState;
import org.apache.hadoop.hdfs.server.federation.store.records.StateStoreVersion;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for router heartbeat service.
 */
public class TestRouterHeartbeatService {
    private Router router;

    private final String routerId = "router1";

    private TestingServer testingServer;

    private CuratorFramework curatorFramework;

    @Test
    public void testStateStoreUnavailable() throws IOException {
        curatorFramework.close();
        testingServer.stop();
        router.getStateStore().stop();
        // The driver is not ready
        Assert.assertFalse(router.getStateStore().isDriverReady());
        // Do a heartbeat, and no exception thrown out
        RouterHeartbeatService heartbeatService = new RouterHeartbeatService(router);
        heartbeatService.updateStateStore();
    }

    @Test
    public void testStateStoreAvailable() throws Exception {
        // The driver is ready
        StateStoreService stateStore = router.getStateStore();
        Assert.assertTrue(router.getStateStore().isDriverReady());
        RouterStore routerStore = router.getRouterStateManager();
        // No record about this router
        stateStore.refreshCaches(true);
        GetRouterRegistrationRequest request = GetRouterRegistrationRequest.newInstance(routerId);
        GetRouterRegistrationResponse response = router.getRouterStateManager().getRouterRegistration(request);
        RouterState routerState = response.getRouter();
        String id = routerState.getRouterId();
        StateStoreVersion version = routerState.getStateStoreVersion();
        Assert.assertNull(id);
        Assert.assertNull(version);
        // Do a heartbeat
        RouterHeartbeatService heartbeatService = new RouterHeartbeatService(router);
        heartbeatService.updateStateStore();
        // We should have a record
        stateStore.refreshCaches(true);
        request = GetRouterRegistrationRequest.newInstance(routerId);
        response = routerStore.getRouterRegistration(request);
        routerState = response.getRouter();
        id = routerState.getRouterId();
        version = routerState.getStateStoreVersion();
        Assert.assertNotNull(id);
        Assert.assertNotNull(version);
    }
}

