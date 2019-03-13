/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.broker;


import java.net.URI;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.jms.Connection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class OpenwireConnectionTest {
    BrokerService broker;

    URI brokerConnectURI;

    @Test
    public void testAMQ5050DefaultHost() throws Exception {
        // Let verify a host header is added to the connection.
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.start();
        CopyOnWriteArrayList<TransportConnection> connections = broker.getConnectorByName("tcp").getConnections();
        Assert.assertEquals(1, connections.size());
        Assert.assertNotNull(connections.get(0).getRemoteWireFormatInfo().getHost());
        connection.stop();
    }

    @Test
    public void testAMQ5050WithManualSpecifiedHost() throws Exception {
        // Let verify a host header is added to the connection.
        Connection connection = new ActiveMQConnectionFactory(((brokerConnectURI) + "?wireFormat.host=foo")).createConnection();
        connection.start();
        CopyOnWriteArrayList<TransportConnection> connections = broker.getConnectorByName("tcp").getConnections();
        Assert.assertEquals(1, connections.size());
        Assert.assertEquals("foo", connections.get(0).getRemoteWireFormatInfo().getHost());
        connection.stop();
    }
}

