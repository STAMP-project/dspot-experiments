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
package org.apache.geode.cache.client.internal;


import java.io.IOException;
import org.apache.geode.CancelCriterion;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.distributed.internal.ServerLocation;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.apache.geode.internal.net.SocketCreator;
import org.apache.geode.security.GemFireSecurityException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ConnectionConnectorTest {
    private CancelCriterion cancelCriterion;

    private EndpointManager endpointManager;

    private InternalDistributedSystem ds;

    private ClientProxyMembershipID proxyId;

    private ClientSideHandshakeImpl handshake;

    private SocketCreator socketCreator;

    private ConnectionImpl connection;

    @Test(expected = GemFireSecurityException.class)
    public void failedConnectionIsDestroyed() throws IOException {
        ConnectionConnector spyConnector = Mockito.spy(new ConnectionConnector(endpointManager, ds, 0, 0, 0, cancelCriterion, false, null, socketCreator, handshake));
        Mockito.doReturn(connection).when(spyConnector).getConnection(ds, cancelCriterion);
        Mockito.doReturn(handshake).when(spyConnector).getClientSideHandshake(handshake);
        Mockito.when(connection.connect(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new GemFireSecurityException("Expected exception"));
        try {
            spyConnector.connectClientToServer(Mockito.mock(ServerLocation.class), false);
        } finally {
            Mockito.verify(spyConnector).destroyConnection(ArgumentMatchers.any());
        }
    }

    @Test
    public void successfulConnectionIsNotDestroyed() throws IOException {
        ConnectionConnector spyConnector = Mockito.spy(new ConnectionConnector(endpointManager, ds, 0, 0, 0, cancelCriterion, false, null, socketCreator, handshake));
        Mockito.doReturn(connection).when(spyConnector).getConnection(ds, cancelCriterion);
        Mockito.doReturn(handshake).when(spyConnector).getClientSideHandshake(handshake);
        try {
            spyConnector.connectClientToServer(Mockito.mock(ServerLocation.class), false);
        } finally {
            Mockito.verify(spyConnector, Mockito.times(0)).destroyConnection(ArgumentMatchers.any());
        }
    }
}

