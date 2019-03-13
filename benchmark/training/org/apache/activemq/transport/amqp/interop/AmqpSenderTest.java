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
package org.apache.activemq.transport.amqp.interop;


import ReceiverSettleMode.FIRST;
import ReceiverSettleMode.SECOND;
import SenderSettleMode.MIXED;
import SenderSettleMode.SETTLED;
import SenderSettleMode.UNSETTLED;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.apache.activemq.transport.amqp.AmqpSupport;
import org.apache.activemq.transport.amqp.AmqpTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpClient;
import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpConnection;
import org.apache.activemq.transport.amqp.client.AmqpMessage;
import org.apache.activemq.transport.amqp.client.AmqpSender;
import org.apache.activemq.transport.amqp.client.AmqpSession;
import org.apache.activemq.transport.amqp.client.AmqpValidator;
import org.apache.activemq.util.Wait;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.engine.Delivery;
import org.apache.qpid.proton.engine.Sender;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.activemq.transport.amqp.client.AmqpSupport.DELAYED_DELIVERY;


/**
 * Test broker behavior when creating AMQP senders
 */
public class AmqpSenderTest extends AmqpClientTestSupport {
    @Test(timeout = 60000)
    public void testCreateQueueSender() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getQueues().length);
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        Assert.assertEquals(1, brokerService.getAdminView().getQueues().length);
        Assert.assertNotNull(getProxyToQueue(getTestName()));
        Assert.assertEquals(1, brokerService.getAdminView().getQueueProducers().length);
        sender.close();
        Assert.assertEquals(0, brokerService.getAdminView().getQueueProducers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testCreateTopicSender() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getTopics().length);
        AmqpSender sender = session.createSender(("topic://" + (getTestName())));
        Assert.assertEquals(1, brokerService.getAdminView().getTopics().length);
        Assert.assertNotNull(getProxyToTopic(getTestName()));
        Assert.assertEquals(1, brokerService.getAdminView().getTopicProducers().length);
        sender.close();
        Assert.assertEquals(0, brokerService.getAdminView().getTopicProducers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testSenderSettlementModeSettledIsHonored() throws Exception {
        doTestSenderSettlementModeIsHonored(SETTLED);
    }

    @Test(timeout = 60000)
    public void testSenderSettlementModeUnsettledIsHonored() throws Exception {
        doTestSenderSettlementModeIsHonored(UNSETTLED);
    }

    @Test(timeout = 60000)
    public void testSenderSettlementModeMixedIsHonored() throws Exception {
        doTestSenderSettlementModeIsHonored(MIXED);
    }

    @Test(timeout = 60000)
    public void testReceiverSettlementModeSetToFirst() throws Exception {
        doTestReceiverSettlementModeForcedToFirst(FIRST);
    }

    @Test(timeout = 60000)
    public void testReceiverSettlementModeSetToSecond() throws Exception {
        doTestReceiverSettlementModeForcedToFirst(SECOND);
    }

    @Test(timeout = 60000)
    public void testSendMessageToQueue() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        AmqpMessage message = new AmqpMessage();
        message.setText("Test-Message");
        sender.send(message);
        QueueViewMBean queue = getProxyToQueue(getTestName());
        Assert.assertEquals(1, queue.getQueueSize());
        sender.close();
        connection.close();
    }

    @Test(timeout = 60000)
    public void testSendMultipleMessagesToQueue() throws Exception {
        final int MSG_COUNT = 100;
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        for (int i = 0; i < MSG_COUNT; ++i) {
            AmqpMessage message = new AmqpMessage();
            message.setText(("Test-Message: " + i));
            sender.send(message);
        }
        QueueViewMBean queue = getProxyToQueue(getTestName());
        Assert.assertEquals(MSG_COUNT, queue.getQueueSize());
        sender.close();
        connection.close();
    }

    @Test(timeout = 60000)
    public void testUnsettledSender() throws Exception {
        final int MSG_COUNT = 1000;
        final CountDownLatch settled = new CountDownLatch(MSG_COUNT);
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        connection.setStateInspector(new AmqpValidator() {
            @Override
            public void inspectDeliveryUpdate(Sender sender, Delivery delivery) {
                if (delivery.remotelySettled()) {
                    AmqpTestSupport.LOG.trace("Remote settled message for sender: {}", sender.getName());
                    settled.countDown();
                }
            }
        });
        AmqpSession session = connection.createSession();
        AmqpSender sender = session.createSender(("topic://" + (getTestName())), false);
        for (int i = 1; i <= MSG_COUNT; ++i) {
            AmqpMessage message = new AmqpMessage();
            message.setText(("Test-Message: " + i));
            sender.send(message);
            if ((i % 1000) == 0) {
                AmqpTestSupport.LOG.info("Sent message: {}", i);
            }
        }
        final TopicViewMBean topic = getProxyToTopic(getTestName());
        Assert.assertTrue("All messages should arrive", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (topic.getEnqueueCount()) == MSG_COUNT;
            }
        }));
        sender.close();
        Assert.assertTrue("Remote should have settled all deliveries", settled.await(5, TimeUnit.MINUTES));
        connection.close();
    }

    @Test(timeout = 60000)
    public void testPresettledSender() throws Exception {
        final int MSG_COUNT = 1000;
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpSender sender = session.createSender(("topic://" + (getTestName())), true);
        for (int i = 1; i <= MSG_COUNT; ++i) {
            AmqpMessage message = new AmqpMessage();
            message.setText(("Test-Message: " + i));
            sender.send(message);
            if ((i % 1000) == 0) {
                AmqpTestSupport.LOG.info("Sent message: {}", i);
            }
        }
        final TopicViewMBean topic = getProxyToTopic(getTestName());
        Assert.assertTrue("All messages should arrive", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (topic.getEnqueueCount()) == MSG_COUNT;
            }
        }));
        sender.close();
        connection.close();
    }

    @Test
    public void testDeliveryDelayOfferedWhenRequested() throws Exception {
        final BrokerViewMBean brokerView = getProxyToBroker();
        AmqpClient client = createAmqpClient();
        client.setValidator(new AmqpValidator() {
            @Override
            public void inspectOpenedResource(Sender sender) {
                Symbol[] offered = sender.getRemoteOfferedCapabilities();
                if (!(AmqpSupport.contains(offered, DELAYED_DELIVERY))) {
                    markAsInvalid("Broker did not indicate it support delayed message delivery");
                }
            }
        });
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerView.getQueues().length);
        AmqpSender sender = session.createSender(("queue://" + (getTestName())), new Symbol[]{ DELAYED_DELIVERY });
        Assert.assertNotNull(sender);
        Assert.assertEquals(1, brokerView.getQueues().length);
        connection.getStateInspector().assertValid();
        sender.close();
        connection.close();
    }
}

