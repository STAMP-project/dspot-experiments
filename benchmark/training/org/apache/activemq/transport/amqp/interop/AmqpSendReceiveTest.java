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


import java.util.concurrent.TimeUnit;
import javax.jms.Queue;
import javax.jms.Topic;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.junit.ActiveMQTestRunner;
import org.apache.activemq.junit.Repeat;
import org.apache.activemq.transport.amqp.client.AmqpClient;
import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpConnection;
import org.apache.activemq.transport.amqp.client.AmqpMessage;
import org.apache.activemq.transport.amqp.client.AmqpReceiver;
import org.apache.activemq.transport.amqp.client.AmqpSender;
import org.apache.activemq.transport.amqp.client.AmqpSession;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test basic send and receive scenarios using only AMQP sender and receiver
 * links.
 */
@RunWith(ActiveMQTestRunner.class)
public class AmqpSendReceiveTest extends AmqpClientTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(AmqpSendReceiveTest.class);

    @Test(timeout = 60000)
    public void testSimpleSendOneReceiveOneToQueue() throws Exception {
        doTestSimpleSendOneReceiveOne(Queue.class);
    }

    @Test(timeout = 60000)
    public void testSimpleSendOneReceiveOneToTopic() throws Exception {
        doTestSimpleSendOneReceiveOne(Topic.class);
    }

    @Test(timeout = 60000)
    public void testCloseBusyReceiver() throws Exception {
        final int MSG_COUNT = 20;
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        for (int i = 0; i < MSG_COUNT; i++) {
            AmqpMessage message = new AmqpMessage();
            message.setMessageId(("msg" + i));
            message.setMessageAnnotation("serialNo", i);
            message.setText("Test-Message");
            sender.send(message);
        }
        sender.close();
        QueueViewMBean queue = getProxyToQueue(getTestName());
        Assert.assertEquals(20, queue.getQueueSize());
        AmqpReceiver receiver1 = session.createReceiver(("queue://" + (getTestName())));
        receiver1.flow(MSG_COUNT);
        AmqpMessage received = receiver1.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull("Should have got a message", received);
        Assert.assertEquals("msg0", received.getMessageId());
        receiver1.close();
        AmqpReceiver receiver2 = session.createReceiver(("queue://" + (getTestName())));
        receiver2.flow(200);
        for (int i = 0; i < MSG_COUNT; ++i) {
            received = receiver2.receive(5, TimeUnit.SECONDS);
            Assert.assertNotNull("Should have got a message", received);
            Assert.assertEquals(("msg" + i), received.getMessageId());
        }
        receiver2.close();
        connection.close();
    }

    @Test(timeout = 60000)
    public void testReceiveWithJMSSelectorFilter() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpMessage message1 = new AmqpMessage();
        message1.setGroupId("abcdefg");
        message1.setApplicationProperty("sn", 100);
        AmqpMessage message2 = new AmqpMessage();
        message2.setGroupId("hijklm");
        message2.setApplicationProperty("sn", 200);
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        sender.send(message1);
        sender.send(message2);
        sender.close();
        AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())), "sn = 100");
        receiver.flow(2);
        AmqpMessage received = receiver.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull("Should have read a message", received);
        Assert.assertEquals(100, received.getApplicationProperty("sn"));
        Assert.assertEquals("abcdefg", received.getGroupId());
        received.accept();
        Assert.assertNull(receiver.receive(1, TimeUnit.SECONDS));
        receiver.close();
        connection.close();
    }

    @Test(timeout = 60000)
    @Repeat(repetitions = 1)
    public void testAdvancedLinkFlowControl() throws Exception {
        final int MSG_COUNT = 20;
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        for (int i = 0; i < MSG_COUNT; i++) {
            AmqpMessage message = new AmqpMessage();
            message.setMessageId(("msg" + i));
            message.setMessageAnnotation("serialNo", i);
            message.setText("Test-Message");
            sender.send(message);
        }
        sender.close();
        AmqpSendReceiveTest.LOG.info("Attempting to read first two messages with receiver #1");
        AmqpReceiver receiver1 = session.createReceiver(("queue://" + (getTestName())));
        receiver1.flow(2);
        AmqpMessage message1 = receiver1.receive(10, TimeUnit.SECONDS);
        AmqpMessage message2 = receiver1.receive(10, TimeUnit.SECONDS);
        Assert.assertNotNull("Should have read message 1", message1);
        Assert.assertNotNull("Should have read message 2", message2);
        Assert.assertEquals("msg0", message1.getMessageId());
        Assert.assertEquals("msg1", message2.getMessageId());
        message1.accept();
        message2.accept();
        AmqpSendReceiveTest.LOG.info("Attempting to read next two messages with receiver #2");
        AmqpReceiver receiver2 = session.createReceiver(("queue://" + (getTestName())));
        receiver2.flow(2);
        AmqpMessage message3 = receiver2.receive(10, TimeUnit.SECONDS);
        AmqpMessage message4 = receiver2.receive(10, TimeUnit.SECONDS);
        Assert.assertNotNull("Should have read message 3", message3);
        Assert.assertNotNull("Should have read message 4", message4);
        Assert.assertEquals("msg2", message3.getMessageId());
        Assert.assertEquals("msg3", message4.getMessageId());
        message3.accept();
        message4.accept();
        AmqpSendReceiveTest.LOG.info("Attempting to read remaining messages with receiver #1");
        receiver1.flow((MSG_COUNT - 4));
        for (int i = 4; i < MSG_COUNT; i++) {
            AmqpMessage message = receiver1.receive(10, TimeUnit.SECONDS);
            Assert.assertNotNull("Should have read a message", message);
            Assert.assertEquals(("msg" + i), message.getMessageId());
            message.accept();
        }
        receiver1.close();
        receiver2.close();
        connection.close();
    }

    @Test(timeout = 60000)
    @Repeat(repetitions = 1)
    public void testDispatchOrderWithPrefetchOfOne() throws Exception {
        final int MSG_COUNT = 20;
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        for (int i = 0; i < MSG_COUNT; i++) {
            AmqpMessage message = new AmqpMessage();
            message.setMessageId(("msg" + i));
            message.setMessageAnnotation("serialNo", i);
            message.setText("Test-Message");
            sender.send(message);
        }
        sender.close();
        AmqpReceiver receiver1 = session.createReceiver(("queue://" + (getTestName())));
        receiver1.flow(1);
        AmqpReceiver receiver2 = session.createReceiver(("queue://" + (getTestName())));
        receiver2.flow(1);
        AmqpMessage message1 = receiver1.receive(10, TimeUnit.SECONDS);
        AmqpMessage message2 = receiver2.receive(10, TimeUnit.SECONDS);
        Assert.assertNotNull("Should have read message 1", message1);
        Assert.assertNotNull("Should have read message 2", message2);
        Assert.assertEquals("msg0", message1.getMessageId());
        Assert.assertEquals("msg1", message2.getMessageId());
        message1.accept();
        message2.accept();
        receiver1.flow(1);
        AmqpMessage message3 = receiver1.receive(10, TimeUnit.SECONDS);
        receiver2.flow(1);
        AmqpMessage message4 = receiver2.receive(10, TimeUnit.SECONDS);
        Assert.assertNotNull("Should have read message 3", message3);
        Assert.assertNotNull("Should have read message 4", message4);
        Assert.assertEquals("msg2", message3.getMessageId());
        Assert.assertEquals("msg3", message4.getMessageId());
        message3.accept();
        message4.accept();
        AmqpSendReceiveTest.LOG.info("*** Attempting to read remaining messages with both receivers");
        int splitCredit = (MSG_COUNT - 4) / 2;
        AmqpSendReceiveTest.LOG.info("**** Receiver #1 granting creadit[{}] for its block of messages", splitCredit);
        receiver1.flow(splitCredit);
        for (int i = 0; i < splitCredit; i++) {
            AmqpMessage message = receiver1.receive(10, TimeUnit.SECONDS);
            Assert.assertNotNull("Receiver #1 should have read a message", message);
            AmqpSendReceiveTest.LOG.info("Receiver #1 read message: {}", message.getMessageId());
            message.accept();
        }
        AmqpSendReceiveTest.LOG.info("**** Receiver #2 granting creadit[{}] for its block of messages", splitCredit);
        receiver2.flow(splitCredit);
        for (int i = 0; i < splitCredit; i++) {
            AmqpMessage message = receiver2.receive(10, TimeUnit.SECONDS);
            Assert.assertNotNull((("Receiver #2 should have read message[" + i) + "]"), message);
            AmqpSendReceiveTest.LOG.info("Receiver #2 read message: {}", message.getMessageId());
            message.accept();
        }
        receiver1.close();
        receiver2.close();
        connection.close();
    }

    @Test(timeout = 60000)
    public void testReceiveMessageAndRefillCreditBeforeAcceptOnQueue() throws Exception {
        doTestReceiveMessageAndRefillCreditBeforeAccept(Queue.class);
    }

    @Test(timeout = 60000)
    public void testReceiveMessageAndRefillCreditBeforeAcceptOnTopic() throws Exception {
        doTestReceiveMessageAndRefillCreditBeforeAccept(Topic.class);
    }

    @Test(timeout = 60000)
    public void testReceiveMessageAndRefillCreditBeforeAcceptOnQueueAsync() throws Exception {
        doTestReceiveMessageAndRefillCreditBeforeAcceptOnTopicAsync(Queue.class);
    }

    @Test(timeout = 60000)
    public void testReceiveMessageAndRefillCreditBeforeAcceptOnTopicAsync() throws Exception {
        doTestReceiveMessageAndRefillCreditBeforeAcceptOnTopicAsync(Topic.class);
    }

    @Test(timeout = 60000)
    public void testMessageDurabliltyFollowsSpec() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        AmqpReceiver receiver1 = session.createReceiver(("queue://" + (getTestName())));
        QueueViewMBean queue = getProxyToQueue(getTestName());
        // Create default message that should be sent as non-durable
        AmqpMessage message1 = new AmqpMessage();
        message1.setText("Test-Message -> non-durable");
        message1.setDurable(false);
        message1.setMessageId("ID:Message:1");
        sender.send(message1);
        Assert.assertEquals(1, queue.getQueueSize());
        receiver1.flow(1);
        message1 = receiver1.receive(50, TimeUnit.SECONDS);
        Assert.assertNotNull("Should have read a message", message1);
        Assert.assertFalse("First message sent should not be durable", message1.isDurable());
        message1.accept();
        // Create default message that should be sent as non-durable
        AmqpMessage message2 = new AmqpMessage();
        message2.setText("Test-Message -> durable");
        message2.setDurable(true);
        message2.setMessageId("ID:Message:2");
        sender.send(message2);
        Assert.assertEquals(1, queue.getQueueSize());
        receiver1.flow(1);
        message2 = receiver1.receive(50, TimeUnit.SECONDS);
        Assert.assertNotNull("Should have read a message", message2);
        Assert.assertTrue("Second message sent should be durable", message2.isDurable());
        message2.accept();
        sender.close();
        connection.close();
    }

    @Test(timeout = 60000)
    public void testMessageWithNoHeaderNotMarkedDurable() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        AmqpReceiver receiver1 = session.createReceiver(("queue://" + (getTestName())));
        // Create default message that should be sent as non-durable
        AmqpMessage message1 = new AmqpMessage();
        message1.setText("Test-Message -> non-durable");
        message1.setMessageId("ID:Message:1");
        sender.send(message1);
        receiver1.flow(1);
        AmqpMessage message2 = receiver1.receive(50, TimeUnit.SECONDS);
        Assert.assertNotNull("Should have read a message", message2);
        Assert.assertFalse("Second message sent should not be durable", message2.isDurable());
        message2.accept();
        sender.close();
        connection.close();
    }

    @Test(timeout = 60000)
    public void testSendMessageToQueueNoPrefixReceiveWithPrefix() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpSender sender = session.createSender(getTestName());
        AmqpMessage message = new AmqpMessage();
        message.setText("Test-Message");
        sender.send(message);
        QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertEquals(1, queueView.getQueueSize());
        sender.close();
        AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())));
        Assert.assertEquals(1, queueView.getQueueSize());
        Assert.assertEquals(0, queueView.getDispatchCount());
        receiver.flow(1);
        AmqpMessage received = receiver.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(received);
        received.accept();
        receiver.close();
        Assert.assertEquals(0, queueView.getQueueSize());
        connection.close();
    }

    @Test(timeout = 60000)
    public void testSendMessageToQueueWithPrefixReceiveWithNoPrefix() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        AmqpMessage message = new AmqpMessage();
        message.setText("Test-Message");
        sender.send(message);
        QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertEquals(1, queueView.getQueueSize());
        sender.close();
        AmqpReceiver receiver = session.createReceiver(getTestName());
        Assert.assertEquals(1, queueView.getQueueSize());
        Assert.assertEquals(0, queueView.getDispatchCount());
        receiver.flow(1);
        AmqpMessage received = receiver.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(received);
        received.accept();
        receiver.close();
        Assert.assertEquals(0, queueView.getQueueSize());
        connection.close();
    }

    @Test(timeout = 60000)
    public void testReceiveMessageBeyondAckedAmountQueue() throws Exception {
        doTestReceiveMessageBeyondAckedAmount(Queue.class);
    }

    @Test(timeout = 60000)
    public void testReceiveMessageBeyondAckedAmountTopic() throws Exception {
        doTestReceiveMessageBeyondAckedAmount(Topic.class);
    }

    @Test(timeout = 60000)
    public void testTwoPresettledReceiversReceiveAllMessages() throws Exception {
        final int MSG_COUNT = 100;
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        final String address = "queue://" + (getTestName());
        AmqpSender sender = session.createSender(address);
        AmqpReceiver receiver1 = session.createReceiver(address, null, false, true);
        AmqpReceiver receiver2 = session.createReceiver(address, null, false, true);
        for (int i = 0; i < MSG_COUNT; i++) {
            AmqpMessage message = new AmqpMessage();
            message.setMessageId(("msg" + i));
            sender.send(message);
        }
        final DestinationViewMBean destinationView = getProxyToQueue(getTestName());
        AmqpSendReceiveTest.LOG.info("Attempting to read first two messages with receiver #1");
        receiver1.flow(2);
        AmqpMessage message1 = receiver1.receive(10, TimeUnit.SECONDS);
        AmqpMessage message2 = receiver1.receive(10, TimeUnit.SECONDS);
        Assert.assertNotNull("Should have read message 1", message1);
        Assert.assertNotNull("Should have read message 2", message2);
        Assert.assertEquals("msg0", message1.getMessageId());
        Assert.assertEquals("msg1", message2.getMessageId());
        message1.accept();
        message2.accept();
        AmqpSendReceiveTest.LOG.info("Attempting to read next two messages with receiver #2");
        receiver2.flow(2);
        AmqpMessage message3 = receiver2.receive(10, TimeUnit.SECONDS);
        AmqpMessage message4 = receiver2.receive(10, TimeUnit.SECONDS);
        Assert.assertNotNull("Should have read message 3", message3);
        Assert.assertNotNull("Should have read message 4", message4);
        Assert.assertEquals("msg2", message3.getMessageId());
        Assert.assertEquals("msg3", message4.getMessageId());
        message3.accept();
        message4.accept();
        Assert.assertTrue(("Should be no inflight messages: " + (destinationView.getInFlightCount())), Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (destinationView.getInFlightCount()) == 0;
            }
        }));
        AmqpSendReceiveTest.LOG.info("*** Attempting to read remaining messages with both receivers");
        int splitCredit = (MSG_COUNT - 4) / 2;
        AmqpSendReceiveTest.LOG.info("**** Receiver #1 granting credit[{}] for its block of messages", splitCredit);
        receiver1.flow(splitCredit);
        for (int i = 0; i < splitCredit; i++) {
            AmqpMessage message = receiver1.receive(10, TimeUnit.SECONDS);
            Assert.assertNotNull("Receiver #1 should have read a message", message);
            AmqpSendReceiveTest.LOG.info("Receiver #1 read message: {}", message.getMessageId());
            message.accept();
        }
        AmqpSendReceiveTest.LOG.info("**** Receiver #2 granting credit[{}] for its block of messages", splitCredit);
        receiver2.flow(splitCredit);
        for (int i = 0; i < splitCredit; i++) {
            AmqpMessage message = receiver2.receive(10, TimeUnit.SECONDS);
            Assert.assertNotNull((("Receiver #2 should have read a message[" + i) + "]"), message);
            AmqpSendReceiveTest.LOG.info("Receiver #2 read message: {}", message.getMessageId());
            message.accept();
        }
        receiver1.close();
        receiver2.close();
        connection.close();
    }

    @Test(timeout = 60000)
    public void testSendReceiveLotsOfDurableMessagesOnQueue() throws Exception {
        doTestSendReceiveLotsOfDurableMessages(Queue.class);
    }

    @Test(timeout = 60000)
    public void testSendReceiveLotsOfDurableMessagesOnTopic() throws Exception {
        doTestSendReceiveLotsOfDurableMessages(Topic.class);
    }
}

