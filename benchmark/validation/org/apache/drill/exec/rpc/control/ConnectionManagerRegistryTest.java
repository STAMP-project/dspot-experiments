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
package org.apache.drill.exec.rpc.control;


import DrillbitEndpoint.State.ONLINE;
import DrillbitEndpoint.State.STARTUP;
import org.apache.drill.exec.proto.CoordinationProtos.DrillbitEndpoint;
import org.junit.Assert;
import org.junit.Test;


public class ConnectionManagerRegistryTest {
    private static final DrillbitEndpoint localEndpoint = DrillbitEndpoint.newBuilder().setAddress("10.0.0.1").setControlPort(31012).setDataPort(31011).setUserPort(31010).setState(STARTUP).build();

    private static final DrillbitEndpoint foremanEndpoint = DrillbitEndpoint.newBuilder().setAddress("10.0.0.2").setControlPort(31012).setDataPort(31011).setUserPort(31010).setState(STARTUP).build();

    private static ControlConnectionConfig config;

    @Test
    public void testLocalConnectionManager() {
        final ConnectionManagerRegistry registry = new ConnectionManagerRegistry(ConnectionManagerRegistryTest.config);
        registry.setLocalEndpoint(ConnectionManagerRegistryTest.localEndpoint);
        final ControlConnectionManager manager = registry.getConnectionManager(ConnectionManagerRegistryTest.localEndpoint);
        Assert.assertTrue(registry.iterator().hasNext());
        Assert.assertEquals(manager, registry.iterator().next());
        Assert.assertTrue((manager instanceof LocalControlConnectionManager));
    }

    @Test
    public void testLocalConnectionManager_differentState() {
        final ConnectionManagerRegistry registry = new ConnectionManagerRegistry(ConnectionManagerRegistryTest.config);
        registry.setLocalEndpoint(ConnectionManagerRegistryTest.localEndpoint);
        final DrillbitEndpoint foremanEndpoint2 = ConnectionManagerRegistryTest.localEndpoint.toBuilder().setState(ONLINE).build();
        final ControlConnectionManager manager = registry.getConnectionManager(foremanEndpoint2);
        Assert.assertTrue(registry.iterator().hasNext());
        Assert.assertEquals(manager, registry.iterator().next());
        Assert.assertTrue((manager instanceof LocalControlConnectionManager));
    }

    @Test
    public void testLocalConnectionManager_differentUserDataPort() {
        final ConnectionManagerRegistry registry = new ConnectionManagerRegistry(ConnectionManagerRegistryTest.config);
        registry.setLocalEndpoint(ConnectionManagerRegistryTest.localEndpoint);
        final DrillbitEndpoint foremanEndpoint2 = ConnectionManagerRegistryTest.localEndpoint.toBuilder().setState(ONLINE).setUserPort(10000).setDataPort(11000).build();
        final ControlConnectionManager manager = registry.getConnectionManager(foremanEndpoint2);
        Assert.assertTrue(registry.iterator().hasNext());
        Assert.assertEquals(manager, registry.iterator().next());
        Assert.assertTrue((manager instanceof LocalControlConnectionManager));
    }

    @Test
    public void testRemoteConnectionManager() {
        final ConnectionManagerRegistry registry = new ConnectionManagerRegistry(ConnectionManagerRegistryTest.config);
        registry.setLocalEndpoint(ConnectionManagerRegistryTest.localEndpoint);
        final ControlConnectionManager manager = registry.getConnectionManager(ConnectionManagerRegistryTest.foremanEndpoint);
        Assert.assertTrue(registry.iterator().hasNext());
        Assert.assertTrue((manager instanceof RemoteControlConnectionManager));
    }

    @Test
    public void testRemoteConnectionManager_differentControlPort() {
        final ConnectionManagerRegistry registry = new ConnectionManagerRegistry(ConnectionManagerRegistryTest.config);
        registry.setLocalEndpoint(ConnectionManagerRegistryTest.localEndpoint);
        final DrillbitEndpoint foremanEndpoint2 = ConnectionManagerRegistryTest.localEndpoint.toBuilder().setControlPort(10000).build();
        final ControlConnectionManager manager = registry.getConnectionManager(foremanEndpoint2);
        Assert.assertTrue(registry.iterator().hasNext());
        Assert.assertEquals(manager, registry.iterator().next());
        Assert.assertTrue((manager instanceof RemoteControlConnectionManager));
    }

    @Test
    public void testRemoteConnectionManager_differentAddress() {
        final ConnectionManagerRegistry registry = new ConnectionManagerRegistry(ConnectionManagerRegistryTest.config);
        registry.setLocalEndpoint(ConnectionManagerRegistryTest.localEndpoint);
        final DrillbitEndpoint foremanEndpoint2 = ConnectionManagerRegistryTest.localEndpoint.toBuilder().setAddress("10.0.0.0").build();
        final ControlConnectionManager manager = registry.getConnectionManager(foremanEndpoint2);
        Assert.assertTrue(registry.iterator().hasNext());
        Assert.assertEquals(manager, registry.iterator().next());
        Assert.assertTrue((manager instanceof RemoteControlConnectionManager));
    }

    @Test
    public void testRemoteAndLocalConnectionManager() {
        final ConnectionManagerRegistry registry = new ConnectionManagerRegistry(ConnectionManagerRegistryTest.config);
        registry.setLocalEndpoint(ConnectionManagerRegistryTest.localEndpoint);
        final DrillbitEndpoint foremanEndpoint2 = ConnectionManagerRegistryTest.localEndpoint.toBuilder().setAddress("10.0.0.0").build();
        final ControlConnectionManager remoteManager = registry.getConnectionManager(foremanEndpoint2);
        final ControlConnectionManager localManager = registry.getConnectionManager(ConnectionManagerRegistryTest.localEndpoint);
        final ControlConnectionManager remoteManager_2 = registry.getConnectionManager(foremanEndpoint2);
        final ControlConnectionManager localManager_2 = registry.getConnectionManager(ConnectionManagerRegistryTest.localEndpoint);
        Assert.assertTrue((remoteManager instanceof RemoteControlConnectionManager));
        Assert.assertEquals(remoteManager, remoteManager_2);
        Assert.assertTrue((localManager instanceof LocalControlConnectionManager));
        Assert.assertEquals(localManager, localManager_2);
    }
}

