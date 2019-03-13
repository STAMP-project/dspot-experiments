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


import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.jmx.JobSchedulerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.transport.amqp.AmqpTestSupport;
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


/**
 * Test for scheduled message support using AMQP message annotations.
 */
public class AmqpScheduledMessageTest extends AmqpClientTestSupport {
    @Test(timeout = 60000)
    public void testSendWithDeliveryTimeIsScheduled() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getQueues().length);
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        // Get the Queue View early to avoid racing the delivery.
        Assert.assertEquals(1, brokerService.getAdminView().getQueues().length);
        final QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertNotNull(queueView);
        AmqpMessage message = new AmqpMessage();
        long deliveryTime = (System.currentTimeMillis()) + (TimeUnit.MINUTES.toMillis(2));
        message.setMessageAnnotation("x-opt-delivery-time", deliveryTime);
        message.setText("Test-Message");
        sender.send(message);
        JobSchedulerViewMBean view = getJobSchedulerMBean();
        Assert.assertNotNull(view);
        Assert.assertEquals(1, view.getAllJobs().size());
        connection.close();
    }

    @Test(timeout = 60000)
    public void testSendRecvWithDeliveryTime() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getQueues().length);
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())));
        // Get the Queue View early to avoid racing the delivery.
        Assert.assertEquals(1, brokerService.getAdminView().getQueues().length);
        final QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertNotNull(queueView);
        AmqpMessage message = new AmqpMessage();
        long deliveryTime = (System.currentTimeMillis()) + 2000;
        message.setMessageAnnotation("x-opt-delivery-time", deliveryTime);
        message.setText("Test-Message");
        sender.send(message);
        Assert.assertTrue("Delayed message should be delivered", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (queueView.getQueueSize()) == 1;
            }
        }));
        // Now try and get the message
        receiver.flow(1);
        AmqpMessage received = receiver.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(received);
        received.accept();
        Long msgDeliveryTime = ((Long) (received.getMessageAnnotation("x-opt-delivery-time")));
        Assert.assertNotNull(msgDeliveryTime);
        Assert.assertEquals(deliveryTime, msgDeliveryTime.longValue());
        connection.close();
    }

    @Test(timeout = 60000)
    public void testSendScheduledReceiveOverOpenWire() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getQueues().length);
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        // Get the Queue View early to avoid racing the delivery.
        Assert.assertEquals(1, brokerService.getAdminView().getQueues().length);
        final QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertNotNull(queueView);
        AmqpMessage message = new AmqpMessage();
        long deliveryTime = (System.currentTimeMillis()) + 2000;
        message.setMessageAnnotation("x-opt-delivery-time", deliveryTime);
        message.setText("Test-Message");
        sender.send(message);
        sender.close();
        // Read the message with short timeout, shouldn't get it.
        try {
            readMessages(getTestName(), 1, false, 500);
            Assert.fail("Should not read the message");
        } catch (Throwable ex) {
        }
        // Read the message
        readMessages(getTestName(), 1, false);
        connection.close();
    }

    @Test
    public void testScheduleWithDelay() throws Exception {
        final long DELAY = 5000;
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getQueues().length);
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())));
        // Get the Queue View early to avoid racing the delivery.
        Assert.assertEquals(1, brokerService.getAdminView().getQueues().length);
        final QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertNotNull(queueView);
        long sendTime = System.currentTimeMillis();
        AmqpMessage message = new AmqpMessage();
        message.setMessageAnnotation("x-opt-delivery-delay", DELAY);
        message.setText("Test-Message");
        sender.send(message);
        sender.close();
        receiver.flow(1);
        // Read the message with short timeout, shouldn't get it.
        try {
            Assert.assertNull(receiver.receive(1, TimeUnit.SECONDS));
            Assert.fail("Should not read the message");
        } catch (Throwable ex) {
        }
        // Read the message with long timeout, should get it.
        AmqpMessage delivered = null;
        try {
            delivered = receiver.receive(10, TimeUnit.SECONDS);
        } catch (Throwable ex) {
            Assert.fail("Should read the message");
        }
        long receivedTime = System.currentTimeMillis();
        Assert.assertNotNull(delivered);
        Long msgDeliveryTime = ((Long) (delivered.getMessageAnnotation("x-opt-delivery-delay")));
        Assert.assertNotNull(msgDeliveryTime);
        Assert.assertEquals(DELAY, msgDeliveryTime.longValue());
        long totalDelay = receivedTime - sendTime;
        AmqpTestSupport.LOG.debug("Sent at: {}, received at: {} ", new Date(sendTime), new Date(receivedTime), totalDelay);
        Assert.assertTrue(("Delay not as expected: " + totalDelay), ((receivedTime - sendTime) >= DELAY));
        connection.close();
    }

    @Test
    public void testScheduleRepeated() throws Exception {
        final int NUMBER = 10;
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getQueues().length);
        AmqpSender sender = session.createSender(("queue://" + (getTestName())));
        // Get the Queue View early to avoid racing the delivery.
        Assert.assertEquals(1, brokerService.getAdminView().getQueues().length);
        final QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertNotNull(queueView);
        AmqpMessage message = new AmqpMessage();
        long delay = 1000;
        message.setMessageAnnotation("x-opt-delivery-delay", delay);
        message.setMessageAnnotation("x-opt-delivery-period", 500);
        message.setMessageAnnotation("x-opt-delivery-repeat", (NUMBER - 1));
        message.setText("Test-Message");
        sender.send(message);
        sender.close();
        readMessages(getTestName(), NUMBER, false);
        // Read the message with short timeout, shouldn't get it.
        try {
            readMessages(getTestName(), 1, false, 600);
            Assert.fail("Should not read more messages");
        } catch (Throwable ex) {
        }
        connection.close();
    }
}

