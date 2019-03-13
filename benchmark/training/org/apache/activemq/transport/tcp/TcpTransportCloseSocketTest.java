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
package org.apache.activemq.transport.tcp;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.apache.activemq.util.Wait.Condition;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test for https://issues.apache.org/jira/browse/AMQ-6561 to make sure sockets
 * are closed on all connection attempt errors
 */
@RunWith(Parameterized.class)
public class TcpTransportCloseSocketTest {
    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String SERVER_KEYSTORE = "src/test/resources/server.keystore";

    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";

    private String uri;

    private final String protocol;

    private BrokerService brokerService;

    static {
        System.setProperty("javax.net.ssl.trustStore", TcpTransportCloseSocketTest.TRUST_KEYSTORE);
        System.setProperty("javax.net.ssl.trustStorePassword", TcpTransportCloseSocketTest.PASSWORD);
        System.setProperty("javax.net.ssl.trustStoreType", TcpTransportCloseSocketTest.KEYSTORE_TYPE);
        System.setProperty("javax.net.ssl.keyStore", TcpTransportCloseSocketTest.SERVER_KEYSTORE);
        System.setProperty("javax.net.ssl.keyStorePassword", TcpTransportCloseSocketTest.PASSWORD);
        System.setProperty("javax.net.ssl.keyStoreType", TcpTransportCloseSocketTest.KEYSTORE_TYPE);
    }

    /**
     *
     *
     * @param isNio
     * 		
     */
    public TcpTransportCloseSocketTest(String protocol) {
        this.protocol = protocol;
    }

    // We want to make sure that the socket will be closed if there as an error on broker.addConnection
    // even if the client doesn't close the connection to prevent dangling open sockets
    @Test(timeout = 60000)
    public void testDuplicateClientIdCloseConnection() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(uri);
        factory.setClientID("id");
        final TcpTransportServer server = ((TcpTransportServer) (brokerService.getTransportConnectorByName("tcp").getServer()));
        // Try and create 2 connections, the second should fail because of a duplicate clientId
        int failed = 0;
        for (int i = 0; i < 2; i++) {
            try {
                factory.createConnection().start();
            } catch (Exception e) {
                e.printStackTrace();
                failed++;
            }
        }
        Assert.assertEquals(1, failed);
        // after 2 seconds the connection should be terminated by the broker because of the exception
        // on broker.addConnection
        Assert.assertTrue(Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (server.getCurrentTransportCount().get()) == 1;
            }
        }, 10000, 500));
    }
}

