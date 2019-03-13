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
package org.apache.hadoop.hdfs.server.federation.store;


import RouterServiceState.EXPIRED;
import RouterServiceState.RUNNING;
import RouterServiceState.UNINITIALIZED;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.hdfs.server.federation.FederationTestUtils;
import org.apache.hadoop.hdfs.server.federation.router.FederationUtil;
import org.apache.hadoop.hdfs.server.federation.store.protocol.GetRouterRegistrationRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.GetRouterRegistrationsRequest;
import org.apache.hadoop.hdfs.server.federation.store.protocol.RouterHeartbeatRequest;
import org.apache.hadoop.hdfs.server.federation.store.records.RouterState;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the basic {@link StateStoreService} {@link RouterStore} functionality.
 */
public class TestStateStoreRouterState extends TestStateStoreBase {
    private static RouterStore routerStore;

    @Test
    public void testStateStoreDisconnected() throws Exception {
        // Close the data store driver
        TestStateStoreBase.getStateStore().closeDriver();
        Assert.assertEquals(false, TestStateStoreBase.getStateStore().isDriverReady());
        // Test all APIs that access the data store to ensure they throw the correct
        // exception.
        GetRouterRegistrationRequest getSingleRequest = GetRouterRegistrationRequest.newInstance();
        FederationTestUtils.verifyException(TestStateStoreRouterState.routerStore, "getRouterRegistration", StateStoreUnavailableException.class, new Class[]{ GetRouterRegistrationRequest.class }, new Object[]{ getSingleRequest });
        GetRouterRegistrationsRequest getRequest = GetRouterRegistrationsRequest.newInstance();
        TestStateStoreRouterState.routerStore.loadCache(true);
        FederationTestUtils.verifyException(TestStateStoreRouterState.routerStore, "getRouterRegistrations", StateStoreUnavailableException.class, new Class[]{ GetRouterRegistrationsRequest.class }, new Object[]{ getRequest });
        RouterHeartbeatRequest hbRequest = RouterHeartbeatRequest.newInstance(RouterState.newInstance("test", 0, UNINITIALIZED));
        FederationTestUtils.verifyException(TestStateStoreRouterState.routerStore, "routerHeartbeat", StateStoreUnavailableException.class, new Class[]{ RouterHeartbeatRequest.class }, new Object[]{ hbRequest });
    }

    // 
    // Router
    // 
    @Test
    public void testUpdateRouterStatus() throws IOException, IllegalStateException {
        long dateStarted = Time.now();
        String address = "testaddress";
        // Set
        RouterHeartbeatRequest request = RouterHeartbeatRequest.newInstance(RouterState.newInstance(address, dateStarted, RUNNING));
        Assert.assertTrue(TestStateStoreRouterState.routerStore.routerHeartbeat(request).getStatus());
        // Verify
        GetRouterRegistrationRequest getRequest = GetRouterRegistrationRequest.newInstance(address);
        RouterState record = TestStateStoreRouterState.routerStore.getRouterRegistration(getRequest).getRouter();
        Assert.assertNotNull(record);
        Assert.assertEquals(RUNNING, record.getStatus());
        Assert.assertEquals(address, record.getAddress());
        Assert.assertEquals(FederationUtil.getCompileInfo(), record.getCompileInfo());
        // Build version may vary a bit
        Assert.assertFalse(record.getVersion().isEmpty());
    }

    @Test
    public void testRouterStateExpired() throws IOException, InterruptedException {
        long dateStarted = Time.now();
        String address = "testaddress";
        RouterHeartbeatRequest request = RouterHeartbeatRequest.newInstance(RouterState.newInstance(address, dateStarted, RUNNING));
        // Set
        Assert.assertTrue(TestStateStoreRouterState.routerStore.routerHeartbeat(request).getStatus());
        // Verify
        GetRouterRegistrationRequest getRequest = GetRouterRegistrationRequest.newInstance(address);
        RouterState record = TestStateStoreRouterState.routerStore.getRouterRegistration(getRequest).getRouter();
        Assert.assertNotNull(record);
        // Wait past expiration (set to 5 sec in config)
        Thread.sleep(6000);
        // Verify expired
        RouterState r = TestStateStoreRouterState.routerStore.getRouterRegistration(getRequest).getRouter();
        Assert.assertEquals(EXPIRED, r.getStatus());
        // Heartbeat again and this shouldn't be EXPIRED anymore
        Assert.assertTrue(TestStateStoreRouterState.routerStore.routerHeartbeat(request).getStatus());
        r = TestStateStoreRouterState.routerStore.getRouterRegistration(getRequest).getRouter();
        Assert.assertEquals(RUNNING, r.getStatus());
    }

    @Test
    public void testGetAllRouterStates() throws IOException, StateStoreUnavailableException {
        // Set 2 entries
        RouterHeartbeatRequest heartbeatRequest1 = RouterHeartbeatRequest.newInstance(RouterState.newInstance("testaddress1", Time.now(), RUNNING));
        Assert.assertTrue(TestStateStoreRouterState.routerStore.routerHeartbeat(heartbeatRequest1).getStatus());
        RouterHeartbeatRequest heartbeatRequest2 = RouterHeartbeatRequest.newInstance(RouterState.newInstance("testaddress2", Time.now(), RUNNING));
        Assert.assertTrue(TestStateStoreRouterState.routerStore.routerHeartbeat(heartbeatRequest2).getStatus());
        // Verify
        TestStateStoreRouterState.routerStore.loadCache(true);
        GetRouterRegistrationsRequest request = GetRouterRegistrationsRequest.newInstance();
        List<RouterState> entries = TestStateStoreRouterState.routerStore.getRouterRegistrations(request).getRouters();
        Assert.assertEquals(2, entries.size());
        Collections.sort(entries);
        Assert.assertEquals("testaddress1", entries.get(0).getAddress());
        Assert.assertEquals("testaddress2", entries.get(1).getAddress());
        Assert.assertEquals(RUNNING, entries.get(0).getStatus());
        Assert.assertEquals(RUNNING, entries.get(1).getStatus());
    }
}

