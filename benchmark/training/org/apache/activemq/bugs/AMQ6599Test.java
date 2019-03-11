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
package org.apache.activemq.bugs;


import java.lang.reflect.Field;
import java.net.Socket;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnection;
import org.apache.activemq.transport.tcp.TcpTransport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AMQ6599Test {
    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String SERVER_KEYSTORE = "src/test/resources/server.keystore";

    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";

    private String uri;

    private final String protocol;

    private BrokerService brokerService;

    static {
        System.setProperty("javax.net.ssl.trustStore", AMQ6599Test.TRUST_KEYSTORE);
        System.setProperty("javax.net.ssl.trustStorePassword", AMQ6599Test.PASSWORD);
        System.setProperty("javax.net.ssl.trustStoreType", AMQ6599Test.KEYSTORE_TYPE);
        System.setProperty("javax.net.ssl.keyStore", AMQ6599Test.SERVER_KEYSTORE);
        System.setProperty("javax.net.ssl.keyStorePassword", AMQ6599Test.PASSWORD);
        System.setProperty("javax.net.ssl.keyStoreType", AMQ6599Test.KEYSTORE_TYPE);
    }

    public AMQ6599Test(String protocol) {
        this.protocol = protocol;
    }

    @Test(timeout = 30000)
    public void testSoTimeout() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(uri);
        factory.createConnection().start();
        // Validate soTimeout value was set on the TcpTransport and the socket
        // Before this patch the TcpTransport value did not have the option set which caused NIO not to work right
        for (TransportConnection connection : brokerService.getTransportConnectorByName("connector").getConnections()) {
            TcpTransport tcpTransport = connection.getTransport().narrow(TcpTransport.class);
            Field socketField = TcpTransport.class.getDeclaredField("socket");
            socketField.setAccessible(true);
            Socket socket = ((Socket) (socketField.get(tcpTransport)));
            Assert.assertEquals(3500, tcpTransport.getSoTimeout());
            Assert.assertEquals(3500, socket.getSoTimeout());
        }
    }
}

