/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.nio.tcp;


import ConnectionType.MEMBER;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.nio.Connection;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastTestSupport;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Test;


/**
 * A test that verifies if two members can connect to each other.
 */
public abstract class TcpIpEndpointManager_AbstractConnectMemberTest extends TcpIpConnection_AbstractTest {
    @Test
    public void testConnectionCount() {
        networkingServiceA.start();
        networkingServiceB.start();
        connect(networkingServiceA, addressB);
        Assert.assertEquals(1, networkingServiceA.getEndpointManager(EndpointQualifier.MEMBER).getConnections().size());
        Assert.assertEquals(1, networkingServiceB.getEndpointManager(EndpointQualifier.MEMBER).getConnections().size());
    }

    // ================== getOrConnect ======================================================
    @Test
    public void getOrConnect_whenNotConnected_thenEventuallyConnectionAvailable() throws UnknownHostException {
        startAllNetworkingServices();
        Connection c = networkingServiceA.getEndpointManager(EndpointQualifier.MEMBER).getOrConnect(addressB);
        Assert.assertNull(c);
        connect(networkingServiceA, addressB);
        Assert.assertEquals(1, networkingServiceA.getEndpointManager(EndpointQualifier.MEMBER).getActiveConnections().size());
        Assert.assertEquals(1, networkingServiceB.getEndpointManager(EndpointQualifier.MEMBER).getActiveConnections().size());
    }

    @Test
    public void getOrConnect_whenAlreadyConnectedSameConnectionReturned() throws UnknownHostException {
        startAllNetworkingServices();
        Connection c1 = connect(networkingServiceA, addressB);
        Connection c2 = networkingServiceA.getEndpointManager(EndpointQualifier.MEMBER).getOrConnect(addressB);
        Assert.assertSame(c1, c2);
    }

    // ================== destroy ======================================================
    @Test
    public void destroyConnection_whenActive() throws Exception {
        startAllNetworkingServices();
        final TcpIpConnection connAB = connect(networkingServiceA, addressB);
        final TcpIpConnection connBA = connect(networkingServiceB, addressA);
        connAB.close(null, null);
        assertIsDestroyed(connAB);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                assertIsDestroyed(connBA);
            }
        });
    }

    @Test
    public void destroyConnection_whenAlreadyDestroyed_thenCallIgnored() throws Exception {
        startAllNetworkingServices();
        networkingServiceA.getEndpointManager(EndpointQualifier.MEMBER).getOrConnect(addressB);
        TcpIpConnection c = connect(networkingServiceA, addressB);
        // first destroy
        c.close(null, null);
        // second destroy
        c.close(null, null);
        assertIsDestroyed(c);
    }

    // ================== connection ======================================================
    @Test
    public void connect() throws UnknownHostException {
        startAllNetworkingServices();
        TcpIpConnection connAB = connect(networkingServiceA, addressB);
        Assert.assertTrue(connAB.isAlive());
        Assert.assertEquals(MEMBER, connAB.getType());
        Assert.assertEquals(1, networkingServiceA.getEndpointManager(EndpointQualifier.MEMBER).getActiveConnections().size());
        TcpIpConnection connBA = ((TcpIpConnection) (networkingServiceB.getEndpointManager(EndpointQualifier.MEMBER).getConnection(addressA)));
        Assert.assertTrue(connBA.isAlive());
        Assert.assertEquals(MEMBER, connBA.getType());
        Assert.assertEquals(1, networkingServiceB.getEndpointManager(EndpointQualifier.MEMBER).getActiveConnections().size());
        Assert.assertEquals(networkingServiceA.getIoService().getThisAddress(), connBA.getEndPoint());
        Assert.assertEquals(networkingServiceB.getIoService().getThisAddress(), connAB.getEndPoint());
    }
}

