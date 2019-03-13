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


import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.SslContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FailoverStaticNetworkTest {
    protected static final Logger LOG = LoggerFactory.getLogger(FailoverStaticNetworkTest.class);

    private static final String DESTINATION_NAME = "testQ";

    protected BrokerService brokerA;

    protected BrokerService brokerA1;

    protected BrokerService brokerB;

    protected BrokerService brokerC;

    private SslContext sslContext;

    @Test
    public void testSendReceiveAfterReconnect() throws Exception {
        brokerA = createBroker("tcp", "61617", null);
        brokerA.start();
        brokerB = createBroker("tcp", "62617", new String[]{ "61617" });
        brokerB.start();
        doTestNetworkSendReceive();
        FailoverStaticNetworkTest.LOG.info("stopping brokerA");
        brokerA.stop();
        brokerA.waitUntilStopped();
        FailoverStaticNetworkTest.LOG.info("restarting brokerA");
        brokerA = createBroker("tcp", "61617", null);
        brokerA.start();
        doTestNetworkSendReceive();
    }

    @Test
    public void testSendReceiveFailover() throws Exception {
        brokerA = createBroker("tcp", "61617", null);
        brokerA.start();
        brokerB = createBroker("tcp", "62617", new String[]{ "61617", "63617" });
        brokerB.start();
        doTestNetworkSendReceive();
        // check mbean
        Set<String> bridgeNames = getNetworkBridgeMBeanName(brokerB);
        Assert.assertEquals(("only one bridgeName: " + bridgeNames), 1, bridgeNames.size());
        FailoverStaticNetworkTest.LOG.info("stopping brokerA");
        brokerA.stop();
        brokerA.waitUntilStopped();
        FailoverStaticNetworkTest.LOG.info("restarting brokerA");
        brokerA = createBroker("tcp", "63617", null);
        brokerA.start();
        doTestNetworkSendReceive();
        Set<String> otherBridgeNames = getNetworkBridgeMBeanName(brokerB);
        Assert.assertEquals(("only one bridgeName: " + otherBridgeNames), 1, otherBridgeNames.size());
        Assert.assertTrue("there was an addition", bridgeNames.addAll(otherBridgeNames));
    }

    @Test
    public void testSendReceiveFailoverDuplex() throws Exception {
        final Vector<Throwable> errors = new Vector<Throwable>();
        final String dataDir = "target/data/shared";
        brokerA = createBroker("61617", dataDir);
        brokerA.start();
        final BrokerService slave = createBroker("63617", dataDir);
        brokerA1 = slave;
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    slave.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    errors.add(e);
                }
            }
        });
        executor.shutdown();
        HashMap<String, String> networkConnectorProps = new HashMap<String, String>();
        networkConnectorProps.put("duplex", "true");
        brokerB = createBroker("tcp", "62617", new String[]{ "61617", "63617" }, networkConnectorProps);
        brokerB.start();
        doTestNetworkSendReceive(brokerA, brokerB);
        doTestNetworkSendReceive(brokerB, brokerA);
        FailoverStaticNetworkTest.LOG.info("stopping brokerA (master shared_broker)");
        brokerA.stop();
        brokerA.waitUntilStopped();
        // wait for slave to start
        brokerA1.waitUntilStarted();
        doTestNetworkSendReceive(brokerA1, brokerB);
        doTestNetworkSendReceive(brokerB, brokerA1);
        Assert.assertTrue(("No unexpected exceptions " + errors), errors.isEmpty());
    }

    // master slave piggy in the middle setup
    @Test
    public void testSendReceiveFailoverDuplexWithPIM() throws Exception {
        final String dataDir = "target/data/shared/pim";
        brokerA = createBroker("61617", dataDir);
        brokerA.start();
        final BrokerService slave = createBroker("63617", dataDir);
        brokerA1 = slave;
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    slave.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        executor.shutdown();
        HashMap<String, String> networkConnectorProps = new HashMap<String, String>();
        networkConnectorProps.put("duplex", "true");
        networkConnectorProps.put("networkTTL", "2");
        brokerB = createBroker("tcp", "62617", new String[]{ "61617", "63617" }, networkConnectorProps);
        brokerB.start();
        Assert.assertTrue("all props applied", networkConnectorProps.isEmpty());
        networkConnectorProps.put("duplex", "true");
        networkConnectorProps.put("networkTTL", "2");
        brokerC = createBroker("tcp", "64617", new String[]{ "61617", "63617" }, networkConnectorProps);
        brokerC.start();
        Assert.assertTrue("all props applied a second time", networkConnectorProps.isEmpty());
        doTestNetworkSendReceive(brokerC, brokerB);
        doTestNetworkSendReceive(brokerB, brokerC);
        FailoverStaticNetworkTest.LOG.info("stopping brokerA (master shared_broker)");
        brokerA.stop();
        brokerA.waitUntilStopped();
        doTestNetworkSendReceive(brokerC, brokerB);
        doTestNetworkSendReceive(brokerB, brokerC);
        brokerC.stop();
        brokerC.waitUntilStopped();
    }

    /**
     * networked broker started after target so first connect attempt succeeds
     * start order is important
     */
    @Test
    public void testSendReceive() throws Exception {
        brokerA = createBroker("tcp", "61617", null);
        brokerA.start();
        brokerB = createBroker("tcp", "62617", new String[]{ "61617", "1111" });
        brokerB.start();
        doTestNetworkSendReceive();
    }

    @Test
    public void testSendReceiveSsl() throws Exception {
        brokerA = createBroker("ssl", "61617", null);
        brokerA.start();
        brokerB = createBroker("ssl", "62617", new String[]{ "61617", "1111" });
        brokerB.start();
        doTestNetworkSendReceive();
    }

    @Test
    public void testRepeatedSendReceiveWithMasterSlaveAlternate() throws Exception {
        doTestRepeatedSendReceiveWithMasterSlaveAlternate(null);
    }

    @Test
    public void testRepeatedSendReceiveWithMasterSlaveAlternateDuplex() throws Exception {
        HashMap<String, String> networkConnectorProps = new HashMap<String, String>();
        networkConnectorProps.put("duplex", "true");
        doTestRepeatedSendReceiveWithMasterSlaveAlternate(networkConnectorProps);
    }
}

