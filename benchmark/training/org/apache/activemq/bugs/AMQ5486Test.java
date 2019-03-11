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


import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jms.support.JmsUtils;


public class AMQ5486Test {
    private static final int maxConnections = 100;

    private static final int maxPoolSize = 10;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private String connectionUri;

    private BrokerService service;

    private TransportConnector connector;

    final ConcurrentLinkedQueue<Connection> connections = new ConcurrentLinkedQueue<Connection>();

    @Test
    public void testFailureOnSelectorThreadPoolExhaustion() throws Exception {
        final ConnectionFactory cf = createConnectionFactory();
        final CountDownLatch startupLatch = new CountDownLatch(1);
        final LinkedList<Exception> exceptions = new LinkedList<Exception>();
        for (int i = 0; i < (AMQ5486Test.maxConnections); i++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    ActiveMQConnection conn = null;
                    try {
                        startupLatch.await();
                        conn = ((ActiveMQConnection) (cf.createConnection()));
                        conn.start();
                        // conn.syncSendPacket(new TransactionInfo(conn.getConnectionInfo().getConnectionId(), null, TransactionInfo.END));
                        connections.add(conn);
                    } catch (Exception e) {
                        exceptions.add(e);
                        JmsUtils.closeConnection(conn);
                    }
                }
            });
        }
        // No connections at first
        Assert.assertEquals(0, connector.getConnections().size());
        // Release the latch to set up connections in parallel
        startupLatch.countDown();
        final TransportConnector connector = this.connector;
        // Expect the max connections is created
        Assert.assertTrue("Expected some exceptions", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return !(exceptions.isEmpty());
            }
        }));
        Assert.assertTrue(((("Expected: more than " + ((AMQ5486Test.maxPoolSize) - 1)) + " connections, found: ") + (connector.getConnections().size())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                // selector thread will take one thread from the pool
                return (connector.getConnections().size()) >= ((AMQ5486Test.maxPoolSize) - 1);
            }
        }));
    }
}

