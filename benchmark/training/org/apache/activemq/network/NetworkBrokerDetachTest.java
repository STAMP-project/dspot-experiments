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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NetworkBrokerDetachTest {
    private static final String BROKER_NAME = "broker";

    private static final String REM_BROKER_NAME = "networkedBroker";

    private static final String DESTINATION_NAME = "testQ";

    private static final int NUM_CONSUMERS = 1;

    protected static final Logger LOG = LoggerFactory.getLogger(NetworkBrokerDetachTest.class);

    protected final int numRestarts = 3;

    protected final int networkTTL = 2;

    protected final boolean dynamicOnly = false;

    protected BrokerService broker;

    protected BrokerService networkedBroker;

    @Test
    public void testNetworkedBrokerDetach() throws Exception {
        NetworkBrokerDetachTest.LOG.info("Creating Consumer on the networked broker ...");
        // Create a consumer on the networked broker
        ConnectionFactory consFactory = createConnectionFactory(networkedBroker);
        Connection consConn = consFactory.createConnection();
        Session consSession = consConn.createSession(false, AUTO_ACKNOWLEDGE);
        ActiveMQDestination destination = ((ActiveMQDestination) (consSession.createQueue(NetworkBrokerDetachTest.DESTINATION_NAME)));
        for (int i = 0; i < (NetworkBrokerDetachTest.NUM_CONSUMERS); i++) {
            consSession.createConsumer(destination);
        }
        Assert.assertTrue("got expected consumer count from mbean within time limit", verifyConsumerCount(1, destination, broker));
        NetworkBrokerDetachTest.LOG.info("Stopping Consumer on the networked broker ...");
        // Closing the connection will also close the consumer
        consConn.close();
        // We should have 0 consumer for the queue on the local broker
        Assert.assertTrue("got expected 0 count from mbean within time limit", verifyConsumerCount(0, destination, broker));
    }

    @Test
    public void testNetworkedBrokerDurableSubAfterRestart() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        MessageListener counter = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                count.incrementAndGet();
            }
        };
        NetworkBrokerDetachTest.LOG.info("Creating durable consumer on each broker ...");
        ActiveMQTopic destination = registerDurableConsumer(networkedBroker, counter);
        registerDurableConsumer(broker, counter);
        Assert.assertTrue("got expected consumer count from local broker mbean within time limit", verifyConsumerCount(2, destination, broker));
        Assert.assertTrue("got expected consumer count from network broker mbean within time limit", verifyConsumerCount(2, destination, networkedBroker));
        sendMessageTo(destination, broker);
        Assert.assertTrue("Got one message on each", verifyMessageCount(2, count));
        NetworkBrokerDetachTest.LOG.info("Stopping brokerTwo...");
        networkedBroker.stop();
        networkedBroker.waitUntilStopped();
        NetworkBrokerDetachTest.LOG.info("restarting  broker Two...");
        networkedBroker = createNetworkedBroker();
        networkedBroker.start();
        NetworkBrokerDetachTest.LOG.info("Recreating durable Consumer on the broker after restart...");
        registerDurableConsumer(networkedBroker, counter);
        // give advisories a chance to percolate
        TimeUnit.SECONDS.sleep(5);
        sendMessageTo(destination, broker);
        // expect similar after restart
        Assert.assertTrue("got expected consumer count from local broker mbean within time limit", verifyConsumerCount(2, destination, broker));
        // a durable sub is auto bridged on restart unless dynamicOnly=true
        Assert.assertTrue("got expected consumer count from network broker mbean within time limit", verifyConsumerCount(2, destination, networkedBroker));
        Assert.assertTrue("got no inactive subs on broker", verifyDurableConsumerCount(0, broker));
        Assert.assertTrue("got no inactive subs on other broker", verifyDurableConsumerCount(0, networkedBroker));
        Assert.assertTrue("Got two more messages after restart", verifyMessageCount(4, count));
        TimeUnit.SECONDS.sleep(1);
        Assert.assertTrue("still Got just two more messages", verifyMessageCount(4, count));
    }
}

