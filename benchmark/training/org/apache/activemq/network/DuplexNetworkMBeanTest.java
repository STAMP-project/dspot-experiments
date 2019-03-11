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
package org.apache.activemq.network;


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.management.MBeanServer;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.ManagementContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DuplexNetworkMBeanTest {
    protected static final Logger LOG = LoggerFactory.getLogger(DuplexNetworkMBeanTest.class);

    protected final int numRestarts = 3;

    private int primaryBrokerPort;

    private int secondaryBrokerPort;

    private MBeanServer mBeanServer = new ManagementContext().getMBeanServer();

    @Test
    public void testMbeanPresenceOnNetworkBrokerRestart() throws Exception {
        BrokerService broker = createBroker();
        try {
            broker.start();
            Assert.assertEquals(1, countMbeans(broker, "connector", 30000));
            Assert.assertEquals(0, countMbeans(broker, "connectionName"));
            BrokerService networkedBroker = null;
            for (int i = 0; i < (numRestarts); i++) {
                networkedBroker = createNetworkedBroker();
                try {
                    networkedBroker.start();
                    Assert.assertEquals(1, countMbeans(networkedBroker, "networkBridge", 2000));
                    Assert.assertEquals(1, countMbeans(broker, "networkBridge", 2000));
                    Assert.assertEquals(2, countMbeans(broker, "connectionName"));
                } finally {
                    networkedBroker.stop();
                    networkedBroker.waitUntilStopped();
                }
                Assert.assertEquals(0, countMbeans(networkedBroker, "stopped"));
                Assert.assertEquals(0, countMbeans(broker, "networkBridge"));
            }
            Assert.assertEquals(0, countMbeans(networkedBroker, "networkBridge"));
            Assert.assertEquals(0, countMbeans(networkedBroker, "connector"));
            Assert.assertEquals(0, countMbeans(networkedBroker, "connectionName"));
            Assert.assertEquals(1, countMbeans(broker, "connector"));
        } finally {
            broker.stop();
            broker.waitUntilStopped();
        }
    }

    @Test
    public void testMbeanPresenceOnBrokerRestart() throws Exception {
        BrokerService networkedBroker = createNetworkedBroker();
        try {
            networkedBroker.start();
            Assert.assertEquals(1, countMbeans(networkedBroker, "connector=networkConnectors", 30000));
            Assert.assertEquals(0, countMbeans(networkedBroker, "connectionName"));
            BrokerService broker = null;
            for (int i = 0; i < (numRestarts); i++) {
                broker = createBroker();
                try {
                    broker.start();
                    Assert.assertEquals(1, countMbeans(networkedBroker, "networkBridge", 5000));
                    Assert.assertEquals(("restart number: " + i), 2, countMbeans(broker, "connectionName", 10000));
                } finally {
                    broker.stop();
                    broker.waitUntilStopped();
                }
                Assert.assertEquals(0, countMbeans(broker, "stopped"));
            }
            Assert.assertEquals(1, countMbeans(networkedBroker, "connector=networkConnectors"));
            Assert.assertEquals(0, countMbeans(networkedBroker, "connectionName"));
            Assert.assertEquals(0, countMbeans(broker, "connectionName"));
        } finally {
            networkedBroker.stop();
            networkedBroker.waitUntilStopped();
        }
    }

    @Test
    public void testMBeansNotOverwrittenOnCleanup() throws Exception {
        BrokerService broker = createBroker();
        BrokerService networkedBroker = createNetworkedBroker();
        MessageProducer producerBroker = null;
        MessageConsumer consumerBroker = null;
        Session sessionNetworkBroker = null;
        Session sessionBroker = null;
        MessageProducer producerNetworkBroker = null;
        MessageConsumer consumerNetworkBroker = null;
        try {
            broker.start();
            broker.waitUntilStarted();
            networkedBroker.start();
            try {
                Assert.assertEquals(2, countMbeans(networkedBroker, "connector=networkConnectors", 10000));
                Assert.assertEquals(1, countMbeans(broker, "connector=duplexNetworkConnectors", 10000));
                Connection brokerConnection = createConnection();
                brokerConnection.start();
                sessionBroker = brokerConnection.createSession(false, AUTO_ACKNOWLEDGE);
                producerBroker = sessionBroker.createProducer(sessionBroker.createTopic("testTopic"));
                consumerBroker = sessionBroker.createConsumer(sessionBroker.createTopic("testTopic"));
                Connection netWorkBrokerConnection = createConnection();
                netWorkBrokerConnection.start();
                sessionNetworkBroker = netWorkBrokerConnection.createSession(false, AUTO_ACKNOWLEDGE);
                producerNetworkBroker = sessionNetworkBroker.createProducer(sessionBroker.createTopic("testTopic"));
                consumerNetworkBroker = sessionNetworkBroker.createConsumer(sessionBroker.createTopic("testTopic"));
                Assert.assertEquals(4, countMbeans(broker, "destinationType=Topic,destinationName=testTopic", 15000));
                Assert.assertEquals(4, countMbeans(networkedBroker, "destinationType=Topic,destinationName=testTopic", 15000));
                producerBroker.send(sessionBroker.createTextMessage("test1"));
                producerNetworkBroker.send(sessionNetworkBroker.createTextMessage("test2"));
                Assert.assertEquals(2, countMbeans(networkedBroker, "destinationName=testTopic,direction=*", 10000));
                Assert.assertEquals(2, countMbeans(broker, "destinationName=testTopic,direction=*", 10000));
            } finally {
                if (producerBroker != null) {
                    producerBroker.close();
                }
                if (consumerBroker != null) {
                    consumerBroker.close();
                }
                if (sessionBroker != null) {
                    sessionBroker.close();
                }
                if (sessionNetworkBroker != null) {
                    sessionNetworkBroker.close();
                }
                if (producerNetworkBroker != null) {
                    producerNetworkBroker.close();
                }
                if (consumerNetworkBroker != null) {
                    consumerNetworkBroker.close();
                }
                networkedBroker.stop();
                networkedBroker.waitUntilStopped();
            }
            Assert.assertEquals(0, countMbeans(broker, "destinationName=testTopic,direction=*", 1500));
        } finally {
            broker.stop();
            broker.waitUntilStopped();
        }
    }
}

