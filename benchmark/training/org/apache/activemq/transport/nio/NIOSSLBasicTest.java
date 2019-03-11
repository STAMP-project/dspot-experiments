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
package org.apache.activemq.transport.nio;


import org.apache.activemq.broker.BrokerService;
import org.junit.Test;


public class NIOSSLBasicTest {
    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String SERVER_KEYSTORE = "src/test/resources/org/apache/activemq/security/broker1.ks";

    public static final String TRUST_KEYSTORE = "src/test/resources/org/apache/activemq/security/broker1.ks";

    public static final int MESSAGE_COUNT = 1000;

    @Test
    public void basicConnector() throws Exception {
        BrokerService broker = createBroker("nio+ssl", ((getTransportType()) + "://localhost:0?transport.needClientAuth=true"));
        basicSendReceive((("ssl://localhost:" + (broker.getConnectorByName("nio+ssl").getConnectUri().getPort())) + "?socket.verifyHostName=false"));
        stopBroker(broker);
    }

    @Test
    public void enabledCipherSuites() throws Exception {
        BrokerService broker = createBroker("nio+ssl", ((getTransportType()) + "://localhost:0?transport.needClientAuth=true&transport.verifyHostName=false&transport.enabledCipherSuites=TLS_RSA_WITH_AES_256_CBC_SHA256"));
        basicSendReceive((("ssl://localhost:" + (broker.getConnectorByName("nio+ssl").getConnectUri().getPort())) + "?socket.verifyHostName=false"));
        stopBroker(broker);
    }

    @Test
    public void enabledProtocols() throws Exception {
        BrokerService broker = createBroker("nio+ssl", ((getTransportType()) + "://localhost:61616?transport.needClientAuth=true&transport.enabledProtocols=TLSv1,TLSv1.1,TLSv1.2"));
        basicSendReceive((("ssl://localhost:" + (broker.getConnectorByName("nio+ssl").getConnectUri().getPort())) + "?socket.verifyHostName=false"));
        stopBroker(broker);
    }

    // Client is missing verifyHostName=false so it should fail as cert doesn't have right host name
    @Test(expected = Exception.class)
    public void verifyHostNameErrorClient() throws Exception {
        BrokerService broker = null;
        try {
            broker = createBroker("nio+ssl", ((getTransportType()) + "://localhost:61616?transport.needClientAuth=true"));
            basicSendReceive(("ssl://localhost:" + (broker.getConnectorByName("nio+ssl").getConnectUri().getPort())));
        } finally {
            if (broker != null) {
                stopBroker(broker);
            }
        }
    }
}

