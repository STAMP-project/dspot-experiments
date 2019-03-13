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
package org.apache.activemq.openwire;


import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transport.tcp.TcpTransportServer;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test that connection attempts that don't send the WireFormatInfo performative
 * get cleaned up by the inactivity monitor.
 */
@RunWith(Parameterized.class)
public class OpenWireConnectionTimeoutTest {
    private static final Logger LOG = LoggerFactory.getLogger(OpenWireConnectionTimeoutTest.class);

    @Rule
    public TestName name = new TestName();

    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String SERVER_KEYSTORE = "src/test/resources/server.keystore";

    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";

    private Socket connection;

    protected String connectorScheme;

    protected int port;

    protected BrokerService brokerService;

    protected Vector<Throwable> exceptions = new Vector<Throwable>();

    static {
        System.setProperty("javax.net.ssl.trustStore", OpenWireConnectionTimeoutTest.TRUST_KEYSTORE);
        System.setProperty("javax.net.ssl.trustStorePassword", OpenWireConnectionTimeoutTest.PASSWORD);
        System.setProperty("javax.net.ssl.trustStoreType", OpenWireConnectionTimeoutTest.KEYSTORE_TYPE);
        System.setProperty("javax.net.ssl.keyStore", OpenWireConnectionTimeoutTest.SERVER_KEYSTORE);
        System.setProperty("javax.net.ssl.keyStoreType", OpenWireConnectionTimeoutTest.KEYSTORE_TYPE);
        System.setProperty("javax.net.ssl.keyStorePassword", OpenWireConnectionTimeoutTest.PASSWORD);
    }

    public OpenWireConnectionTimeoutTest(String connectorScheme) {
        this.connectorScheme = connectorScheme;
    }

    @Test(timeout = 90000)
    public void testInactivityMonitor() throws Exception {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    connection = createConnection();
                    connection.getOutputStream().write('A');
                    connection.getOutputStream().flush();
                } catch (Exception ex) {
                    OpenWireConnectionTimeoutTest.LOG.error("unexpected exception on connect/disconnect", ex);
                    exceptions.add(ex);
                }
            }
        };
        t1.start();
        Assert.assertTrue("one connection", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                TcpTransportServer server = ((TcpTransportServer) (brokerService.getTransportConnectorByScheme(getConnectorScheme()).getServer()));
                return 1 == (server.getCurrentTransportCount().get());
            }
        }, TimeUnit.SECONDS.toMillis(15), TimeUnit.MILLISECONDS.toMillis(250)));
        // and it should be closed due to inactivity
        Assert.assertTrue("no dangling connections", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                TcpTransportServer server = ((TcpTransportServer) (brokerService.getTransportConnectorByScheme(getConnectorScheme()).getServer()));
                return 0 == (server.getCurrentTransportCount().get());
            }
        }, TimeUnit.SECONDS.toMillis(15), TimeUnit.MILLISECONDS.toMillis(500)));
        Assert.assertTrue("no exceptions", exceptions.isEmpty());
    }
}

