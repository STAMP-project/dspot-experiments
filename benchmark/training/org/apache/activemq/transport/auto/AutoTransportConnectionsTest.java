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
package org.apache.activemq.transport.auto;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.transport.tcp.TcpTransportServer;
import org.apache.activemq.util.Wait;
import org.apache.activemq.util.Wait.Condition;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AutoTransportConnectionsTest {
    @Rule
    public Timeout globalTimeout = new Timeout(60, TimeUnit.SECONDS);

    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String SERVER_KEYSTORE = "src/test/resources/server.keystore";

    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";

    private static final int maxConnections = 20;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private String connectionUri;

    private BrokerService service;

    private TransportConnector connector;

    private final String transportType;

    public AutoTransportConnectionsTest(String transportType) {
        super();
        this.transportType = transportType;
    }

    @Test
    public void testMaxConnectionControl() throws Exception {
        configureConnectorAndStart((((transportType) + "://0.0.0.0:0?maxConnectionThreadPoolSize=10&maximumConnections=") + (AutoTransportConnectionsTest.maxConnections)));
        final ConnectionFactory cf = createConnectionFactory();
        final CountDownLatch startupLatch = new CountDownLatch(1);
        // create an extra 10 connections above max
        for (int i = 0; i < ((AutoTransportConnectionsTest.maxConnections) + 10); i++) {
            final int count = i;
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    Connection conn = null;
                    try {
                        startupLatch.await();
                        // sleep for a short period of time
                        Thread.sleep((count * 3));
                        conn = cf.createConnection();
                        conn.start();
                    } catch (Exception e) {
                    }
                }
            });
        }
        TcpTransportServer transportServer = ((TcpTransportServer) (connector.getServer()));
        // ensure the max connections is in effect
        Assert.assertEquals(AutoTransportConnectionsTest.maxConnections, transportServer.getMaximumConnections());
        // No connections at first
        Assert.assertEquals(0, connector.getConnections().size());
        // Release the latch to set up connections in parallel
        startupLatch.countDown();
        final TransportConnector connector = this.connector;
        // Expect the max connections is created
        Assert.assertTrue(((("Expected: " + (AutoTransportConnectionsTest.maxConnections)) + " found: ") + (connector.getConnections().size())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (connector.getConnections().size()) == (AutoTransportConnectionsTest.maxConnections);
            }
        }));
    }

    @Test
    public void testConcurrentConnections() throws Exception {
        configureConnectorAndStart(((transportType) + "://0.0.0.0:0"));
        int connectionAttempts = 50;
        ConnectionFactory factory = createConnectionFactory();
        final AtomicInteger connectedCount = new AtomicInteger(0);
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            for (int i = 0; i < connectionAttempts; i++) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            latch.await();
                            Connection con = factory.createConnection();
                            con.start();
                            connectedCount.incrementAndGet();
                        } catch (Exception e) {
                            // print for debugging but don't fail it might just be the transport stopping
                            e.printStackTrace();
                        }
                    }
                });
            }
            latch.countDown();
            // Make sure all attempts connected without error
            Assert.assertTrue(Wait.waitFor(new Condition() {
                @Override
                public boolean isSatisified() throws Exception {
                    return (connectedCount.get()) == connectionAttempts;
                }
            }));
        } catch (Exception e) {
            // print for debugging but don't fail it might just be the transport stopping
            e.printStackTrace();
        }
    }
}

