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


import DeliveryMode.NON_PERSISTENT;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.jms.TopicRequestor;
import javax.jms.TopicSession;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.util.Wait;
import org.apache.activemq.util.Wait.Condition;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;


public class SimpleNetworkTest extends BaseNetworkTest {
    protected static final int MESSAGE_COUNT = 10;

    protected AbstractApplicationContext context;

    protected ActiveMQTopic included;

    protected ActiveMQTopic excluded;

    protected String consumerName = "durableSubs";

    // works b/c of non marshaling vm transport, the connection
    // ref from the client is used during the forward
    @Test(timeout = 60 * 1000)
    public void testMessageCompression() throws Exception {
        ActiveMQConnection localAmqConnection = ((ActiveMQConnection) (localConnection));
        localAmqConnection.setUseCompression(true);
        MessageConsumer consumer1 = remoteSession.createConsumer(included);
        MessageProducer producer = localSession.createProducer(included);
        producer.setDeliveryMode(NON_PERSISTENT);
        waitForConsumerRegistration(localBroker, 1, included);
        for (int i = 0; i < (SimpleNetworkTest.MESSAGE_COUNT); i++) {
            Message test = localSession.createTextMessage(("test-" + i));
            producer.send(test);
            Message msg = consumer1.receive(3000);
            Assert.assertNotNull(("not null? message: " + i), msg);
            ActiveMQMessage amqMessage = ((ActiveMQMessage) (msg));
            Assert.assertTrue(amqMessage.isCompressed());
        }
        // ensure no more messages received
        Assert.assertNull(consumer1.receive(1000));
        assertNetworkBridgeStatistics(SimpleNetworkTest.MESSAGE_COUNT, 0);
    }

    @Test(timeout = 60 * 1000)
    public void testRequestReply() throws Exception {
        final MessageProducer remoteProducer = remoteSession.createProducer(null);
        MessageConsumer remoteConsumer = remoteSession.createConsumer(included);
        remoteConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                try {
                    TextMessage textMsg = ((TextMessage) (msg));
                    String payload = "REPLY: " + (textMsg.getText());
                    Destination replyTo;
                    replyTo = msg.getJMSReplyTo();
                    textMsg.clearBody();
                    textMsg.setText(payload);
                    remoteProducer.send(replyTo, textMsg);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        TopicRequestor requestor = new TopicRequestor(((TopicSession) (localSession)), included);
        // allow for consumer infos to perculate arround
        Thread.sleep(5000);
        for (int i = 0; i < (SimpleNetworkTest.MESSAGE_COUNT); i++) {
            TextMessage msg = localSession.createTextMessage(("test msg: " + i));
            TextMessage result = ((TextMessage) (requestor.request(msg)));
            Assert.assertNotNull(result);
            LOG.info(result.getText());
        }
        assertNetworkBridgeStatistics(SimpleNetworkTest.MESSAGE_COUNT, SimpleNetworkTest.MESSAGE_COUNT);
    }

    @Test(timeout = 60 * 1000)
    public void testFiltering() throws Exception {
        MessageConsumer includedConsumer = remoteSession.createConsumer(included);
        MessageConsumer excludedConsumer = remoteSession.createConsumer(excluded);
        MessageProducer includedProducer = localSession.createProducer(included);
        MessageProducer excludedProducer = localSession.createProducer(excluded);
        // allow for consumer infos to perculate around
        Thread.sleep(2000);
        Message test = localSession.createTextMessage("test");
        includedProducer.send(test);
        excludedProducer.send(test);
        Assert.assertNull(excludedConsumer.receive(1000));
        Assert.assertNotNull(includedConsumer.receive(1000));
        assertNetworkBridgeStatistics(1, 0);
    }

    @Test(timeout = 60 * 1000)
    public void testConduitBridge() throws Exception {
        MessageConsumer consumer1 = remoteSession.createConsumer(included);
        MessageConsumer consumer2 = remoteSession.createConsumer(included);
        MessageProducer producer = localSession.createProducer(included);
        producer.setDeliveryMode(NON_PERSISTENT);
        waitForConsumerRegistration(localBroker, 2, included);
        for (int i = 0; i < (SimpleNetworkTest.MESSAGE_COUNT); i++) {
            Message test = localSession.createTextMessage(("test-" + i));
            producer.send(test);
            Assert.assertNotNull(consumer1.receive(1000));
            Assert.assertNotNull(consumer2.receive(1000));
        }
        // ensure no more messages received
        Assert.assertNull(consumer1.receive(1000));
        Assert.assertNull(consumer2.receive(1000));
        assertNetworkBridgeStatistics(SimpleNetworkTest.MESSAGE_COUNT, 0);
        Assert.assertNotNull(localBroker.getManagementContext().getObjectInstance(localBroker.createNetworkConnectorObjectName(localBroker.getNetworkConnectors().get(0))));
    }

    // Added for AMQ-6465 to make sure memory usage decreased back to 0 after messages are forwarded
    // to the other broker
    @Test(timeout = 60 * 1000)
    public void testDurableTopicSubForwardMemoryUsage() throws Exception {
        // create a remote durable consumer to create demand
        MessageConsumer remoteConsumer = remoteSession.createDurableSubscriber(included, consumerName);
        Thread.sleep(1000);
        MessageProducer producer = localSession.createProducer(included);
        for (int i = 0; i < (SimpleNetworkTest.MESSAGE_COUNT); i++) {
            Message test = localSession.createTextMessage(("test-" + i));
            producer.send(test);
        }
        Thread.sleep(1000);
        // Make sure stats are set
        Assert.assertEquals(SimpleNetworkTest.MESSAGE_COUNT, localBroker.getDestination(included).getDestinationStatistics().getForwards().getCount());
        Assert.assertTrue(Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (localBroker.getSystemUsage().getMemoryUsage().getUsage()) == 0;
            }
        }, 10000, 500));
        remoteConsumer.close();
    }

    // Added for AMQ-6465 to make sure memory usage decreased back to 0 after messages are forwarded
    // to the other broker
    @Test(timeout = 60 * 1000)
    public void testTopicSubForwardMemoryUsage() throws Exception {
        // create a remote durable consumer to create demand
        MessageConsumer remoteConsumer = remoteSession.createConsumer(included);
        Thread.sleep(1000);
        MessageProducer producer = localSession.createProducer(included);
        for (int i = 0; i < (SimpleNetworkTest.MESSAGE_COUNT); i++) {
            Message test = localSession.createTextMessage(("test-" + i));
            producer.send(test);
        }
        Thread.sleep(1000);
        // Make sure stats are set
        Assert.assertEquals(SimpleNetworkTest.MESSAGE_COUNT, localBroker.getDestination(included).getDestinationStatistics().getForwards().getCount());
        Assert.assertTrue(Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (localBroker.getSystemUsage().getMemoryUsage().getUsage()) == 0;
            }
        }, 10000, 500));
        for (int i = 0; i < (SimpleNetworkTest.MESSAGE_COUNT); i++) {
            Assert.assertNotNull(("message count: " + i), remoteConsumer.receive(2500));
        }
        remoteConsumer.close();
    }

    // Added for AMQ-6465 to make sure memory usage decreased back to 0 after messages are forwarded
    // to the other broker
    @Test(timeout = 60 * 1000)
    public void testQueueSubForwardMemoryUsage() throws Exception {
        ActiveMQQueue queue = new ActiveMQQueue("include.test.foo");
        MessageConsumer remoteConsumer = remoteSession.createConsumer(queue);
        Thread.sleep(1000);
        MessageProducer producer = localSession.createProducer(queue);
        for (int i = 0; i < (SimpleNetworkTest.MESSAGE_COUNT); i++) {
            Message test = localSession.createTextMessage(("test-" + i));
            producer.send(test);
        }
        Thread.sleep(1000);
        // Make sure stats are set
        Assert.assertEquals(SimpleNetworkTest.MESSAGE_COUNT, localBroker.getDestination(queue).getDestinationStatistics().getForwards().getCount());
        Assert.assertTrue(Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (localBroker.getSystemUsage().getMemoryUsage().getUsage()) == 0;
            }
        }, 10000, 500));
        for (int i = 0; i < (SimpleNetworkTest.MESSAGE_COUNT); i++) {
            Assert.assertNotNull(("message count: " + i), remoteConsumer.receive(2500));
        }
        remoteConsumer.close();
    }

    @Test(timeout = 60 * 1000)
    public void testDurableStoreAndForward() throws Exception {
        // create a remote durable consumer
        MessageConsumer remoteConsumer = remoteSession.createDurableSubscriber(included, consumerName);
        Thread.sleep(1000);
        // now close everything down and restart
        doTearDown();
        doSetUp(false);
        MessageProducer producer = localSession.createProducer(included);
        for (int i = 0; i < (SimpleNetworkTest.MESSAGE_COUNT); i++) {
            Message test = localSession.createTextMessage(("test-" + i));
            producer.send(test);
        }
        Thread.sleep(1000);
        // Make sure stats are set
        Assert.assertEquals(SimpleNetworkTest.MESSAGE_COUNT, localBroker.getDestination(included).getDestinationStatistics().getForwards().getCount());
        // close everything down and restart
        doTearDown();
        doSetUp(false);
        remoteConsumer = remoteSession.createDurableSubscriber(included, consumerName);
        for (int i = 0; i < (SimpleNetworkTest.MESSAGE_COUNT); i++) {
            Assert.assertNotNull(("message count: " + i), remoteConsumer.receive(2500));
        }
    }
}

