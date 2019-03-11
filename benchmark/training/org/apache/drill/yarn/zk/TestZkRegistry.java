/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.yarn.zk;


import DrillbitTracker.State.DEREGISTERED;
import DrillbitTracker.State.NEW;
import DrillbitTracker.State.REGISTERED;
import DrillbitTracker.State.UNMANAGED;
import Event.ALLOCATED;
import Event.CREATED;
import Event.ENDED;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.test.TestingServer;
import org.apache.drill.exec.proto.CoordinationProtos.DrillbitEndpoint;
import org.apache.drill.exec.work.foreman.DrillbitStatusListener;
import org.apache.drill.yarn.appMaster.EventContext;
import org.apache.drill.yarn.appMaster.RegistryHandler;
import org.apache.drill.yarn.appMaster.Task;
import org.apache.drill.yarn.zk.ZKRegistry.DrillbitTracker;
import org.junit.Assert;
import org.junit.Test;

import static TrackingState.NEW;


/**
 * Tests for the AM version of the cluster coordinator. The AM version has no
 * dependencies on the DoY config system or other systems, making it easy to
 * test in isolation using the Curator-provided test server.
 */
public class TestZkRegistry {
    private static final String BARNEY_HOST = "barney";

    private static final String WILMA_HOST = "wilma";

    private static final String TEST_HOST = "host";

    private static final String FRED_HOST = "fred";

    public static final int TEST_USER_PORT = 123;

    public static final int TEST_CONTROL_PORT = 456;

    public static final int TEST_DATA_PORT = 789;

    public static final String ZK_ROOT = "test-root";

    public static final String CLUSTER_ID = "test-cluster";

    /**
     * Validate that the key format used for endpoint is the same as that
     * generated for hosts coming from YARN.
     */
    @Test
    public void testFormat() {
        DrillbitEndpoint dbe = makeEndpoint(TestZkRegistry.TEST_HOST);
        Assert.assertEquals(TestZkRegistry.makeKey(TestZkRegistry.TEST_HOST), ZKClusterCoordinatorDriver.asString(dbe));
        ZKClusterCoordinatorDriver driver = new ZKClusterCoordinatorDriver().setPorts(123, 456, 789);
        Assert.assertEquals(TestZkRegistry.makeKey(TestZkRegistry.TEST_HOST), driver.toKey(TestZkRegistry.TEST_HOST));
        // Internal default ports (used mostly for testing.)
        driver = new ZKClusterCoordinatorDriver();
        Assert.assertEquals("fred:31010:31011:31012", driver.toKey(TestZkRegistry.FRED_HOST));
    }

    /**
     * Basic setup: start a ZK and verify that the initial endpoint list is empty.
     * Also validates the basics of the test setup (mock server, etc.)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBasics() throws Exception {
        try (TestingServer server = new TestingServer()) {
            server.start();
            String connStr = server.getConnectString();
            ZKClusterCoordinatorDriver driver = new ZKClusterCoordinatorDriver().setConnect(connStr, "drill", "drillbits").build();
            Assert.assertTrue(driver.getInitialEndpoints().isEmpty());
            driver.close();
            server.stop();
        }
    }

    private class TestDrillbitStatusListener implements DrillbitStatusListener {
        protected Set<DrillbitEndpoint> added;

        protected Set<DrillbitEndpoint> removed;

        @Override
        public void drillbitUnregistered(Set<DrillbitEndpoint> unregisteredDrillbits) {
            removed = unregisteredDrillbits;
        }

        @Override
        public void drillbitRegistered(Set<DrillbitEndpoint> registeredDrillbits) {
            added = registeredDrillbits;
        }

        public void clear() {
            added = null;
            removed = null;
        }
    }

    /**
     * Test a typical life cycle: existing Drillbit on AM start, add a Drilbit
     * (simulates a drillbit starting), and remove a drillbit (simulates a
     * Drillbit ending).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCycle() throws Exception {
        TestingServer server = new TestingServer();
        server.start();
        String connStr = server.getConnectString();
        CuratorFramework probeZk = TestZkRegistry.connectToZk(connStr);
        addDrillbit(probeZk, TestZkRegistry.FRED_HOST);
        ZKClusterCoordinatorDriver driver = new ZKClusterCoordinatorDriver().setConnect(connStr, TestZkRegistry.ZK_ROOT, TestZkRegistry.CLUSTER_ID).build();
        List<DrillbitEndpoint> bits = driver.getInitialEndpoints();
        Assert.assertEquals(1, bits.size());
        Assert.assertEquals(TestZkRegistry.makeKey(TestZkRegistry.FRED_HOST), ZKClusterCoordinatorDriver.asString(bits.get(0)));
        TestZkRegistry.TestDrillbitStatusListener listener = new TestZkRegistry.TestDrillbitStatusListener();
        driver.addDrillbitListener(listener);
        addDrillbit(probeZk, TestZkRegistry.WILMA_HOST);
        Thread.sleep(50);
        Assert.assertNull(listener.removed);
        Assert.assertNotNull(listener.added);
        Assert.assertEquals(1, listener.added.size());
        for (DrillbitEndpoint dbe : listener.added) {
            Assert.assertEquals(TestZkRegistry.makeKey(TestZkRegistry.WILMA_HOST), ZKClusterCoordinatorDriver.asString(dbe));
        }
        listener.clear();
        removeDrillbit(probeZk, TestZkRegistry.FRED_HOST);
        Thread.sleep(50);
        Assert.assertNull(listener.added);
        Assert.assertNotNull(listener.removed);
        Assert.assertEquals(1, listener.removed.size());
        for (DrillbitEndpoint dbe : listener.removed) {
            Assert.assertEquals(TestZkRegistry.makeKey(TestZkRegistry.FRED_HOST), ZKClusterCoordinatorDriver.asString(dbe));
        }
        probeZk.close();
        driver.close();
        server.stop();
        server.close();
    }

    private static class TestRegistryHandler implements RegistryHandler {
        String reserved;

        String released;

        Task start;

        Task end;

        public void clear() {
            reserved = null;
            released = null;
            start = null;
            end = null;
        }

        @Override
        public void reserveHost(String hostName) {
            Assert.assertNull(reserved);
            reserved = hostName;
        }

        @Override
        public void releaseHost(String hostName) {
            Assert.assertNull(released);
            released = hostName;
        }

        @Override
        public void startAck(Task task, String propertyKey, Object value) {
            start = task;
        }

        @Override
        public void completionAck(Task task, String endpointProperty) {
            end = task;
        }

        @Override
        public void registryDown() {
            // TODO Auto-generated method stub
        }
    }

    public static class TestTask extends Task {
        private String host;

        public TestTask(String host) {
            super(null, null);
            this.host = host;
        }

        @Override
        public String getHostName() {
            return host;
        }

        @Override
        public void resetTrackingState() {
            trackingState = NEW;
        }
    }

    @Test
    public void testZKRegistry() throws Exception {
        TestingServer server = new TestingServer();
        server.start();
        String connStr = server.getConnectString();
        CuratorFramework probeZk = TestZkRegistry.connectToZk(connStr);
        addDrillbit(probeZk, TestZkRegistry.FRED_HOST);
        ZKClusterCoordinatorDriver driver = new ZKClusterCoordinatorDriver().setConnect(connStr, TestZkRegistry.ZK_ROOT, TestZkRegistry.CLUSTER_ID).setPorts(TestZkRegistry.TEST_USER_PORT, TestZkRegistry.TEST_CONTROL_PORT, TestZkRegistry.TEST_DATA_PORT).build();
        ZKRegistry registry = new ZKRegistry(driver);
        TestZkRegistry.TestRegistryHandler handler = new TestZkRegistry.TestRegistryHandler();
        registry.start(handler);
        // We started with one "stray" drillbit that will be reported as unmanaged.
        Assert.assertEquals(TestZkRegistry.FRED_HOST, handler.reserved);
        List<String> unmanaged = registry.listUnmanagedDrillits();
        Assert.assertEquals(1, unmanaged.size());
        String fredsKey = TestZkRegistry.makeKey(TestZkRegistry.FRED_HOST);
        Assert.assertEquals(fredsKey, unmanaged.get(0));
        Map<String, DrillbitTracker> trackers = registry.getRegistryForTesting();
        Assert.assertEquals(1, trackers.size());
        Assert.assertTrue(trackers.containsKey(fredsKey));
        DrillbitTracker fredsTracker = trackers.get(fredsKey);
        Assert.assertEquals(fredsKey, fredsTracker.key);
        Assert.assertEquals(UNMANAGED, fredsTracker.state);
        Assert.assertNull(fredsTracker.task);
        Assert.assertEquals(fredsKey, ZKClusterCoordinatorDriver.asString(fredsTracker.endpoint));
        // The handler should have been told about the initial stray.
        Assert.assertEquals(TestZkRegistry.FRED_HOST, handler.reserved);
        // Pretend to start a new Drillbit.
        Task wilmasTask = new TestZkRegistry.TestTask(TestZkRegistry.WILMA_HOST);
        EventContext context = new EventContext(wilmasTask);
        // Registry ignores the created event.
        registry.stateChange(CREATED, context);
        Assert.assertEquals(1, registry.getRegistryForTesting().size());
        // But, does care about the allocated event.
        registry.stateChange(ALLOCATED, context);
        Assert.assertEquals(2, registry.getRegistryForTesting().size());
        String wilmasKey = TestZkRegistry.makeKey(TestZkRegistry.WILMA_HOST);
        DrillbitTracker wilmasTracker = registry.getRegistryForTesting().get(wilmasKey);
        Assert.assertNotNull(wilmasTracker);
        Assert.assertEquals(wilmasTask, wilmasTracker.task);
        Assert.assertNull(wilmasTracker.endpoint);
        Assert.assertEquals(wilmasKey, wilmasTracker.key);
        Assert.assertEquals(NEW, wilmasTracker.state);
        handler.clear();
        // Time goes on. The Drillbit starts and registers itself.
        addDrillbit(probeZk, TestZkRegistry.WILMA_HOST);
        Thread.sleep(100);
        Assert.assertEquals(wilmasTask, handler.start);
        Assert.assertEquals(REGISTERED, wilmasTracker.state);
        Assert.assertEquals(handler.start, wilmasTask);
        // Create another task: Barney
        Task barneysTask = new TestZkRegistry.TestTask(TestZkRegistry.BARNEY_HOST);
        context = new EventContext(barneysTask);
        registry.stateChange(CREATED, context);
        // Start Barney, but assume a latency in Yarn, but not ZK.
        // We get the ZK registration before the YARN launch confirmation.
        handler.clear();
        addDrillbit(probeZk, TestZkRegistry.BARNEY_HOST);
        Thread.sleep(100);
        Assert.assertEquals(TestZkRegistry.BARNEY_HOST, handler.reserved);
        String barneysKey = TestZkRegistry.makeKey(TestZkRegistry.BARNEY_HOST);
        DrillbitTracker barneysTracker = registry.getRegistryForTesting().get(barneysKey);
        Assert.assertNotNull(barneysTracker);
        Assert.assertEquals(UNMANAGED, barneysTracker.state);
        Assert.assertNull(barneysTracker.task);
        Assert.assertEquals(2, registry.listUnmanagedDrillits().size());
        handler.clear();
        registry.stateChange(ALLOCATED, context);
        Assert.assertEquals(REGISTERED, barneysTracker.state);
        Assert.assertEquals(handler.start, barneysTask);
        Assert.assertEquals(barneysTask, barneysTracker.task);
        Assert.assertEquals(1, registry.listUnmanagedDrillits().size());
        // Barney is having problems, it it drops out of ZK.
        handler.clear();
        removeDrillbit(probeZk, TestZkRegistry.BARNEY_HOST);
        Thread.sleep(100);
        Assert.assertEquals(barneysTask, handler.end);
        Assert.assertEquals(DEREGISTERED, barneysTracker.state);
        // Barney comes alive (presumably before the controller gives up and kills
        // the Drillbit.)
        handler.clear();
        addDrillbit(probeZk, TestZkRegistry.BARNEY_HOST);
        Thread.sleep(100);
        Assert.assertEquals(barneysTask, handler.start);
        Assert.assertEquals(REGISTERED, barneysTracker.state);
        // Barney is killed by the controller.
        // ZK entry drops. Tracker is removed, controller is notified.
        handler.clear();
        removeDrillbit(probeZk, TestZkRegistry.BARNEY_HOST);
        Thread.sleep(100);
        Assert.assertNotNull(registry.getRegistryForTesting().get(barneysKey));
        Assert.assertEquals(barneysTask, handler.end);
        // The controller tells the registry to stop tracking the Drillbit.
        handler.clear();
        registry.stateChange(ENDED, context);
        Assert.assertNull(handler.end);
        Assert.assertNull(registry.getRegistryForTesting().get(barneysKey));
        // The stray drillbit deregisters from ZK. The tracker is removed.
        handler.clear();
        removeDrillbit(probeZk, TestZkRegistry.FRED_HOST);
        Thread.sleep(100);
        Assert.assertNull(registry.getRegistryForTesting().get(fredsKey));
        Assert.assertNull(handler.end);
        Assert.assertEquals(TestZkRegistry.FRED_HOST, handler.released);
        // Wilma is killed by the controller.
        handler.clear();
        removeDrillbit(probeZk, TestZkRegistry.WILMA_HOST);
        Thread.sleep(100);
        Assert.assertEquals(wilmasTask, handler.end);
        Assert.assertNull(handler.released);
        Assert.assertEquals(DEREGISTERED, wilmasTracker.state);
        Assert.assertNotNull(registry.getRegistryForTesting().get(wilmasKey));
        handler.clear();
        context = new EventContext(wilmasTask);
        registry.stateChange(ENDED, context);
        Assert.assertNull(registry.getRegistryForTesting().get(wilmasKey));
        Assert.assertNull(handler.released);
        Assert.assertNull(handler.end);
        // All drillbits should be gone.
        Assert.assertTrue(registry.getRegistryForTesting().isEmpty());
        probeZk.close();
        driver.close();
        server.stop();
        server.close();
    }
}

