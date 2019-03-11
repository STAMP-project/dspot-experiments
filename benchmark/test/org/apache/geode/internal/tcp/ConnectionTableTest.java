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
package org.apache.geode.internal.tcp;


import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.test.junit.categories.MembershipTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ MembershipTest.class })
public class ConnectionTableTest {
    private ConnectionTable connectionTable;

    private Socket socket;

    private PeerConnectionFactory factory;

    private Connection connection;

    @Test
    public void testConnectionsClosedDuringCreateAreNotAddedAsReceivers() throws Exception {
        Mockito.when(connection.isReceiverStopped()).thenReturn(false);
        Mockito.when(connection.isSocketClosed()).thenReturn(true);// Pretend this closed as soon at it was

        // created
        connectionTable.acceptConnection(socket, factory);
        Assert.assertEquals(0, connectionTable.getNumberOfReceivers());
    }

    @Test
    public void testThreadStoppedNotAddedAsReceivers() throws Exception {
        Mockito.when(connection.isSocketClosed()).thenReturn(false);// connection is not closed

        Mockito.when(connection.isReceiverStopped()).thenReturn(true);// but receiver is stopped

        connectionTable.acceptConnection(socket, factory);
        Assert.assertEquals(0, connectionTable.getNumberOfReceivers());
    }

    @Test
    public void testSocketNotClosedAddedAsReceivers() throws Exception {
        Mockito.when(connection.isSocketClosed()).thenReturn(false);// connection is not closed

        connectionTable.acceptConnection(socket, factory);
        Assert.assertEquals(1, connectionTable.getNumberOfReceivers());
    }

    @Test
    public void testThreadOwnedSocketsAreRemoved() throws Exception {
        Boolean wantsResources = ConnectionTable.getThreadOwnsResourcesRegistration();
        ConnectionTable.threadWantsOwnResources();
        try {
            Map<DistributedMember, Connection> threadConnectionMap = new HashMap<>();
            connectionTable.threadOrderedConnMap.set(threadConnectionMap);
            ConnectionTable.releaseThreadsSockets();
            Assert.assertEquals(0, threadConnectionMap.size());
        } finally {
            if (wantsResources != (Boolean.FALSE)) {
                ConnectionTable.threadWantsSharedResources();
            }
        }
    }
}

