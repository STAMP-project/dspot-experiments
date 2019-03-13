/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.tier.sockets;


import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


/**
 * This is a functional-test for <code>ClientHealthMonitor</code>.
 */
@Category({ ClientServerTest.class })
public class ClientHealthMonitorJUnitTest {
    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    private int pingIntervalMillis = 200;

    private int monitorIntervalMillis = 20;

    private ClientHealthMonitor clientHealthMonitor;

    @Test
    public void idleServerConnectionTerminatedByHealthMonitor() throws Exception {
        ClientProxyMembershipID mockId = Mockito.mock(ClientProxyMembershipID.class);
        ServerConnection mockConnection = Mockito.mock(ServerConnection.class);
        clientHealthMonitor.addConnection(mockId, mockConnection);
        clientHealthMonitor.receivedPing(mockId);
        clientHealthMonitor.testUseCustomHeartbeatCheck(( a, b, c) -> true);// Fail all heartbeats

        await().untilAsserted(() -> verify(mockConnection).handleTermination(true));
    }

    class HeartbeatOverride implements ClientHealthMonitor.HeartbeatTimeoutCheck {
        public int numHeartbeats = 0;

        @Override
        public boolean timedOut(long current, long lastHeartbeat, long interval) {
            ++(numHeartbeats);
            return false;
        }
    }

    @Test
    public void activeServerConnectionNotTerminatedByHealthMonitor() throws Exception {
        ClientProxyMembershipID mockId = Mockito.mock(ClientProxyMembershipID.class);
        ServerConnection mockConnection = Mockito.mock(ServerConnection.class);
        clientHealthMonitor.addConnection(mockId, mockConnection);
        clientHealthMonitor.receivedPing(mockId);
        ClientHealthMonitorJUnitTest.HeartbeatOverride heartbeater = new ClientHealthMonitorJUnitTest.HeartbeatOverride();
        clientHealthMonitor.testUseCustomHeartbeatCheck(heartbeater);
        await().until(() -> heartbeater.numHeartbeats >= 5);
        // Check that we never tried to terminate the connection
        Mockito.verify(mockConnection, Mockito.times(0)).handleTermination(true);
    }
}

