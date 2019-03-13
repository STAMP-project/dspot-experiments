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
package org.apache.activemq.partition;


import Session.AUTO_ACKNOWLEDGE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.partition.dto.Partitioning;
import org.apache.activemq.partition.dto.Target;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the PartitionBroker plugin.
 */
public class PartitionBrokerTest {
    protected HashMap<String, BrokerService> brokers = new HashMap<String, BrokerService>();

    protected ArrayList<Connection> connections = new ArrayList<Connection>();

    Partitioning partitioning;

    /**
     * Partitioning can only re-direct failover clients since those
     * can re-connect and re-establish their state with another broker.
     */
    @Test(timeout = (1000 * 60) * 60)
    public void testNonFailoverClientHasNoPartitionEffect() throws Exception {
        partitioning.byClientId = new HashMap<String, Target>();
        partitioning.byClientId.put("client1", new Target("broker1"));
        createBrokerCluster(2);
        Connection connection = createConnectionToUrl(getConnectURL("broker2"));
        within(5, TimeUnit.SECONDS, new PartitionBrokerTest.Task() {
            public void run() throws Exception {
                Assert.assertEquals(0, getTransportConnector("broker1").getConnections().size());
                Assert.assertEquals(1, getTransportConnector("broker2").getConnections().size());
            }
        });
        connection.setClientID("client1");
        connection.start();
        Thread.sleep(1000);
        Assert.assertEquals(0, getTransportConnector("broker1").getConnections().size());
        Assert.assertEquals(1, getTransportConnector("broker2").getConnections().size());
    }

    @Test(timeout = (1000 * 60) * 60)
    public void testPartitionByClientId() throws Exception {
        partitioning.byClientId = new HashMap<String, Target>();
        partitioning.byClientId.put("client1", new Target("broker1"));
        partitioning.byClientId.put("client2", new Target("broker2"));
        createBrokerCluster(2);
        Connection connection = createConnectionTo("broker2");
        within(5, TimeUnit.SECONDS, new PartitionBrokerTest.Task() {
            public void run() throws Exception {
                Assert.assertEquals(0, getTransportConnector("broker1").getConnections().size());
                Assert.assertEquals(1, getTransportConnector("broker2").getConnections().size());
            }
        });
        connection.setClientID("client1");
        connection.start();
        within(5, TimeUnit.SECONDS, new PartitionBrokerTest.Task() {
            public void run() throws Exception {
                Assert.assertEquals(1, getTransportConnector("broker1").getConnections().size());
                Assert.assertEquals(0, getTransportConnector("broker2").getConnections().size());
            }
        });
    }

    @Test(timeout = (1000 * 60) * 60)
    public void testPartitionByQueue() throws Exception {
        partitioning.byQueue = new HashMap<String, Target>();
        partitioning.byQueue.put("foo", new Target("broker1"));
        createBrokerCluster(2);
        Connection connection2 = createConnectionTo("broker2");
        within(5, TimeUnit.SECONDS, new PartitionBrokerTest.Task() {
            public void run() throws Exception {
                Assert.assertEquals(0, getTransportConnector("broker1").getConnections().size());
                Assert.assertEquals(1, getTransportConnector("broker2").getConnections().size());
            }
        });
        Session session2 = connection2.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session2.createConsumer(session2.createQueue("foo"));
        within(5, TimeUnit.SECONDS, new PartitionBrokerTest.Task() {
            public void run() throws Exception {
                Assert.assertEquals(1, getTransportConnector("broker1").getConnections().size());
                Assert.assertEquals(0, getTransportConnector("broker2").getConnections().size());
            }
        });
        Connection connection1 = createConnectionTo("broker2");
        Session session1 = connection1.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = session1.createProducer(session1.createQueue("foo"));
        within(5, TimeUnit.SECONDS, new PartitionBrokerTest.Task() {
            public void run() throws Exception {
                Assert.assertEquals(1, getTransportConnector("broker1").getConnections().size());
                Assert.assertEquals(1, getTransportConnector("broker2").getConnections().size());
            }
        });
        for (int i = 0; i < 100; i++) {
            producer.send(session1.createTextMessage(("#" + i)));
        }
        within(5, TimeUnit.SECONDS, new PartitionBrokerTest.Task() {
            public void run() throws Exception {
                Assert.assertEquals(2, getTransportConnector("broker1").getConnections().size());
                Assert.assertEquals(0, getTransportConnector("broker2").getConnections().size());
            }
        });
    }

    static interface Task {
        public void run() throws Exception;
    }
}

