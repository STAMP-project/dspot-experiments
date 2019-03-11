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


import ActiveMQDestination.QUEUE_TYPE;
import ActiveMQMessage.BROKER_PATH_PROPERTY;
import DeliveryMode.NON_PERSISTENT;
import MessageAck.INDIVIDUAL_ACK_TYPE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.management.openmbean.CompositeData;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.StubConnection;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ConnectionInfo;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ProducerInfo;
import org.apache.activemq.command.SessionInfo;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class duplicates most of the functionality in {@link NetworkTestSupport}
 * and {@link BrokerTestSupport} because more control was needed over how brokers
 * and connectors are created. Also, this test asserts message counts via JMX on
 * each broker.
 */
public class BrokerNetworkWithStuckMessagesTest {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerNetworkWithStuckMessagesTest.class);

    private BrokerService localBroker;

    private BrokerService remoteBroker;

    private BrokerService secondRemoteBroker;

    private DemandForwardingBridge bridge;

    protected Map<String, BrokerService> brokers = new HashMap<String, BrokerService>();

    protected ArrayList<StubConnection> connections = new ArrayList<StubConnection>();

    protected TransportConnector connector;

    protected TransportConnector remoteConnector;

    protected TransportConnector secondRemoteConnector;

    protected long idGenerator;

    protected int msgIdGenerator;

    protected int tempDestGenerator;

    protected int maxWait = 4000;

    protected String queueName = "TEST";

    protected String amqDomain = "org.apache.activemq";

    @Test(timeout = 120000)
    public void testBrokerNetworkWithStuckMessages() throws Exception {
        int sendNumMessages = 10;
        int receiveNumMessages = 5;
        // Create a producer
        StubConnection connection1 = createConnection();
        ConnectionInfo connectionInfo1 = createConnectionInfo();
        SessionInfo sessionInfo1 = createSessionInfo(connectionInfo1);
        ProducerInfo producerInfo = createProducerInfo(sessionInfo1);
        connection1.send(connectionInfo1);
        connection1.send(sessionInfo1);
        connection1.send(producerInfo);
        // Create a destination on the local broker
        ActiveMQDestination destinationInfo1 = null;
        // Send a 10 messages to the local broker
        for (int i = 0; i < sendNumMessages; ++i) {
            destinationInfo1 = createDestinationInfo(connection1, connectionInfo1, QUEUE_TYPE);
            connection1.request(createMessage(producerInfo, destinationInfo1, NON_PERSISTENT));
        }
        // Ensure that there are 10 messages on the local broker
        Object[] messages = browseQueueWithJmx(localBroker);
        Assert.assertEquals(sendNumMessages, messages.length);
        // Create a synchronous consumer on the remote broker
        StubConnection connection2 = createRemoteConnection();
        ConnectionInfo connectionInfo2 = createConnectionInfo();
        SessionInfo sessionInfo2 = createSessionInfo(connectionInfo2);
        connection2.send(connectionInfo2);
        connection2.send(sessionInfo2);
        ActiveMQDestination destinationInfo2 = createDestinationInfo(connection2, connectionInfo2, QUEUE_TYPE);
        final ConsumerInfo consumerInfo2 = createConsumerInfo(sessionInfo2, destinationInfo2);
        connection2.send(consumerInfo2);
        // Consume 5 of the messages from the remote broker and ack them.
        for (int i = 0; i < receiveNumMessages; ++i) {
            Message message1 = receiveMessage(connection2, 20000);
            Assert.assertNotNull(message1);
            BrokerNetworkWithStuckMessagesTest.LOG.info(("on remote, got: " + (message1.getMessageId())));
            connection2.send(createAck(consumerInfo2, message1, 1, INDIVIDUAL_ACK_TYPE));
            Assert.assertTrue("JMSActiveMQBrokerPath property present and correct", ((ActiveMQMessage) (message1)).getStringProperty(BROKER_PATH_PROPERTY).contains(localBroker.getBroker().getBrokerId().toString()));
        }
        // Ensure that there are zero messages on the local broker. This tells
        // us that those messages have been prefetched to the remote broker
        // where the demand exists.
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Object[] result = browseQueueWithJmx(localBroker);
                return 0 == (result.length);
            }
        });
        messages = browseQueueWithJmx(localBroker);
        Assert.assertEquals(0, messages.length);
        // try and pull the messages from remote, should be denied b/c on networkTtl
        BrokerNetworkWithStuckMessagesTest.LOG.info("creating demand on second remote...");
        StubConnection connection3 = createSecondRemoteConnection();
        ConnectionInfo connectionInfo3 = createConnectionInfo();
        SessionInfo sessionInfo3 = createSessionInfo(connectionInfo3);
        connection3.send(connectionInfo3);
        connection3.send(sessionInfo3);
        ActiveMQDestination destinationInfo3 = createDestinationInfo(connection3, connectionInfo3, QUEUE_TYPE);
        final ConsumerInfo consumerInfoS3 = createConsumerInfo(sessionInfo3, destinationInfo3);
        connection3.send(consumerInfoS3);
        Message messageExceedingTtl = receiveMessage(connection3, 5000);
        if (messageExceedingTtl != null) {
            BrokerNetworkWithStuckMessagesTest.LOG.error(("got message on Second remote: " + messageExceedingTtl));
            connection3.send(createAck(consumerInfoS3, messageExceedingTtl, 1, INDIVIDUAL_ACK_TYPE));
        }
        BrokerNetworkWithStuckMessagesTest.LOG.info("Closing consumer on remote");
        // Close the consumer on the remote broker
        connection2.send(consumerInfo2.createRemoveCommand());
        // also close connection etc.. so messages get dropped from the local consumer  q
        connection2.send(connectionInfo2.createRemoveCommand());
        // There should now be 5 messages stuck on the remote broker
        Assert.assertTrue("correct stuck message count", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Object[] result = browseQueueWithJmx(remoteBroker);
                return 5 == (result.length);
            }
        }));
        messages = browseQueueWithJmx(remoteBroker);
        Assert.assertEquals(5, messages.length);
        Assert.assertTrue("can see broker path property", ((String) (((CompositeData) (messages[1])).get("BrokerPath"))).contains(localBroker.getBroker().getBrokerId().toString()));
        BrokerNetworkWithStuckMessagesTest.LOG.info("Messages now stuck on remote");
        // receive again on the origin broker
        ConsumerInfo consumerInfo1 = createConsumerInfo(sessionInfo1, destinationInfo1);
        connection1.send(consumerInfo1);
        BrokerNetworkWithStuckMessagesTest.LOG.info(("create local consumer: " + consumerInfo1));
        Message message1 = receiveMessage(connection1, 20000);
        Assert.assertNotNull("Expect to get a replay as remote consumer is gone", message1);
        connection1.send(createAck(consumerInfo1, message1, 1, INDIVIDUAL_ACK_TYPE));
        BrokerNetworkWithStuckMessagesTest.LOG.info("acked one message on origin, waiting for all messages to percolate back");
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Object[] result = browseQueueWithJmx(localBroker);
                return 4 == (result.length);
            }
        });
        messages = browseQueueWithJmx(localBroker);
        Assert.assertEquals(4, messages.length);
        BrokerNetworkWithStuckMessagesTest.LOG.info("checking for messages on remote again");
        // messages won't migrate back again till consumer closes
        connection2 = createRemoteConnection();
        connectionInfo2 = createConnectionInfo();
        sessionInfo2 = createSessionInfo(connectionInfo2);
        connection2.send(connectionInfo2);
        connection2.send(sessionInfo2);
        ConsumerInfo consumerInfo3 = createConsumerInfo(sessionInfo2, destinationInfo2);
        connection2.send(consumerInfo3);
        message1 = receiveMessage(connection2, 20000);
        Assert.assertNull(("Messages have migrated back: " + message1), message1);
        // Consume the last 4 messages from the local broker and ack them just
        // to clean up the queue.
        int counter = 1;
        for (; counter < receiveNumMessages; counter++) {
            message1 = receiveMessage(connection1);
            BrokerNetworkWithStuckMessagesTest.LOG.info(("local consume of: " + (message1 != null ? message1.getMessageId() : " null")));
            connection1.send(createAck(consumerInfo1, message1, 1, INDIVIDUAL_ACK_TYPE));
        }
        // Ensure that 5 messages were received
        Assert.assertEquals(receiveNumMessages, counter);
        // verify all messages consumed
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Object[] result = browseQueueWithJmx(remoteBroker);
                return 0 == (result.length);
            }
        });
        messages = browseQueueWithJmx(remoteBroker);
        Assert.assertEquals(0, messages.length);
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Object[] result = browseQueueWithJmx(localBroker);
                return 0 == (result.length);
            }
        });
        messages = browseQueueWithJmx(localBroker);
        Assert.assertEquals(0, messages.length);
        // Close the consumer on the remote broker
        connection2.send(consumerInfo3.createRemoveCommand());
        connection1.stop();
        connection2.stop();
        connection3.stop();
    }
}

