/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.usecases;


import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Connection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerPluginSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.network.DemandForwardingBridge;
import org.apache.activemq.network.NetworkBridge;
import org.apache.activemq.network.NetworkConnector;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// https://issues.apache.org/jira/browse/AMQ-6640
public class DuplexAdvisoryRaceTest {
    private static final Logger LOG = LoggerFactory.getLogger(DuplexAdvisoryRaceTest.class);

    private static String hostName;

    final AtomicLong responseReceived = new AtomicLong(0);

    BrokerService brokerA;

    BrokerService brokerB;

    String networkConnectorUrlString;

    @Test
    public void testHang() throws Exception {
        brokerA.setPlugins(new BrokerPlugin[]{ new BrokerPluginSupport() {
            @Override
            public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
                Subscription subscription = super.addConsumer(context, info);
                // delay return to allow dispatch to interleave
                if (context.isNetworkConnection()) {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
                return subscription;
            }
        } });
        // bridge
        NetworkConnector networkConnector = bridgeBrokers(brokerA, brokerB);
        brokerA.start();
        brokerB.start();
        ActiveMQConnectionFactory brokerAFactory = new ActiveMQConnectionFactory(((brokerA.getTransportConnectorByScheme("tcp").getPublishableConnectString()) + "?jms.watchTopicAdvisories=false"));
        ActiveMQConnectionFactory brokerBFactory = new ActiveMQConnectionFactory(((brokerB.getTransportConnectorByScheme("tcp").getPublishableConnectString()) + "?jms.watchTopicAdvisories=false"));
        // populate dests
        final int numDests = 400;
        final int numMessagesPerDest = 50;
        final int numConsumersPerDest = 5;
        populate(brokerAFactory, 0, (numDests / 2), numMessagesPerDest);
        populate(brokerBFactory, (numDests / 2), numDests, numMessagesPerDest);
        // demand
        List<Connection> connections = new LinkedList<>();
        connections.add(demand(brokerBFactory, 0, (numDests / 2), numConsumersPerDest));
        connections.add(demand(brokerAFactory, (numDests / 2), numDests, numConsumersPerDest));
        DuplexAdvisoryRaceTest.LOG.info("Allow duplex bridge to connect....");
        // allow bridge to start
        brokerB.startTransportConnector(brokerB.addConnector(((networkConnectorUrlString) + "?transport.socketBufferSize=1024")));
        if (!(Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                DuplexAdvisoryRaceTest.LOG.info(("received: " + (responseReceived.get())));
                return (responseReceived.get()) >= (numMessagesPerDest * numDests);
            }
        }, ((10 * 60) * 1000)))) {
            dumpAllThreads("DD");
            // when hung close will also hang!
            for (NetworkBridge networkBridge : networkConnector.activeBridges()) {
                if (networkBridge instanceof DemandForwardingBridge) {
                    DemandForwardingBridge demandForwardingBridge = ((DemandForwardingBridge) (networkBridge));
                    Socket socket = demandForwardingBridge.getRemoteBroker().narrow(Socket.class);
                    socket.close();
                }
            }
        }
        networkConnector.stop();
        for (Connection connection : connections) {
            try {
                connection.close();
            } catch (Exception ignored) {
            }
        }
        Assert.assertTrue(("received all sent: " + (responseReceived.get())), ((responseReceived.get()) >= (numMessagesPerDest * numDests)));
    }
}

