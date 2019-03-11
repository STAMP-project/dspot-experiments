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
import TerminusDurability.NONE;
import TerminusExpiryPolicy.LINK_DETACH;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.junit.ActiveMQTestRunner;
import org.apache.activemq.junit.Repeat;
import org.apache.activemq.transport.amqp.AmqpSupport;
import org.apache.activemq.transport.amqp.AmqpTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpClient;
import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpConnection;
import org.apache.activemq.transport.amqp.client.AmqpMessage;
import org.apache.activemq.transport.amqp.client.AmqpReceiver;
import org.apache.activemq.transport.amqp.client.AmqpSession;
import org.apache.activemq.transport.amqp.client.AmqpUnknownFilterType;
import org.apache.activemq.transport.amqp.client.AmqpValidator;
import org.apache.activemq.util.Wait;
import org.apache.qpid.proton.amqp.DescribedType;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.amqp.messaging.Source;
import org.apache.qpid.proton.engine.Receiver;
import org.apache.qpid.proton.engine.Session;
import org.apache.qpid.proton.message.Message;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test various behaviors of AMQP receivers with the broker.
 */
@RunWith(ActiveMQTestRunner.class)
public class AmqpReceiverTest extends AmqpClientTestSupport {
    @Test(timeout = 60000)
    public void testReceiverCloseSendsRemoteClose() throws Exception {
        AmqpClient client = createAmqpClient();
        Assert.assertNotNull(client);
        final AtomicBoolean closed = new AtomicBoolean();
        client.setValidator(new AmqpValidator() {
            @Override
            public void inspectClosedResource(Session session) {
                AmqpTestSupport.LOG.info("Session closed: {}", session.getContext());
            }

            @Override
            public void inspectDetachedResource(Receiver receiver) {
                markAsInvalid("Broker should not detach receiver linked to closed session.");
            }

            @Override
            public void inspectClosedResource(Receiver receiver) {
                AmqpTestSupport.LOG.info("Receiver closed: {}", receiver.getContext());
                closed.set(true);
            }
        });
        AmqpConnection connection = trackConnection(client.connect());
        Assert.assertNotNull(connection);
        AmqpSession session = connection.createSession();
        Assert.assertNotNull(session);
        AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())));
        Assert.assertNotNull(receiver);
        receiver.close();
        Assert.assertTrue("Did not process remote close as expected", closed.get());
        connection.getStateInspector().assertValid();
        connection.close();
    }

    @Test(timeout = 60000)
    public void testCreateQueueReceiver() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getQueues().length);
        AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())));
        Assert.assertEquals(1, brokerService.getAdminView().getQueues().length);
        Assert.assertNotNull(getProxyToQueue(getTestName()));
        Assert.assertEquals(1, brokerService.getAdminView().getQueueSubscribers().length);
        receiver.close();
        Assert.assertEquals(0, brokerService.getAdminView().getQueueSubscribers().length);
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
    public void testCreateQueueReceiverWithJMSSelector() throws Exception {
        AmqpClient client = createAmqpClient();
        client.setValidator(new AmqpValidator() {
            @SuppressWarnings("unchecked")
            @Override
            public void inspectOpenedResource(Receiver receiver) {
                AmqpTestSupport.LOG.info("Receiver opened: {}", receiver);
                if ((receiver.getRemoteSource()) == null) {
                    markAsInvalid("Link opened with null source.");
                }
                Source source = ((Source) (receiver.getRemoteSource()));
                Map<Symbol, Object> filters = source.getFilter();
                if ((AmqpSupport.findFilter(filters, AmqpSupport.JMS_SELECTOR_FILTER_IDS)) == null) {
                    markAsInvalid("Broker did not return the JMS Filter on Attach");
                }
            }
        });
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getQueues().length);
        session.createReceiver(("queue://" + (getTestName())), "JMSPriority > 8");
        Assert.assertEquals(1, brokerService.getAdminView().getQueueSubscribers().length);
        connection.getStateInspector().assertValid();
        connection.close();
    }

    @Test(timeout = 60000)
    public void testCreateQueueReceiverWithNoLocalSet() throws Exception {
        AmqpClient client = createAmqpClient();
        client.setValidator(new AmqpValidator() {
            @SuppressWarnings("unchecked")
            @Override
            public void inspectOpenedResource(Receiver receiver) {
                AmqpTestSupport.LOG.info("Receiver opened: {}", receiver);
                if ((receiver.getRemoteSource()) == null) {
                    markAsInvalid("Link opened with null source.");
                }
                Source source = ((Source) (receiver.getRemoteSource()));
                Map<Symbol, Object> filters = source.getFilter();
                if ((AmqpSupport.findFilter(filters, AmqpSupport.NO_LOCAL_FILTER_IDS)) == null) {
                    markAsInvalid("Broker did not return the NoLocal Filter on Attach");
                }
            }
        });
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getQueues().length);
        session.createReceiver(("queue://" + (getTestName())), null, true);
        Assert.assertEquals(1, brokerService.getAdminView().getQueueSubscribers().length);
        connection.getStateInspector().assertValid();
        connection.close();
    }

    @Test(timeout = 60000)
    public void testCreateTopicReceiver() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getTopics().length);
        AmqpReceiver receiver = session.createReceiver(("topic://" + (getTestName())));
        Assert.assertEquals(1, brokerService.getAdminView().getTopics().length);
        Assert.assertNotNull(getProxyToTopic(getTestName()));
        Assert.assertEquals(1, brokerService.getAdminView().getTopicSubscribers().length);
        receiver.close();
        Assert.assertEquals(0, brokerService.getAdminView().getTopicSubscribers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testQueueReceiverReadMessage() throws Exception {
        sendMessages(getTestName(), 1, false);
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())));
        QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertEquals(1, queueView.getQueueSize());
        Assert.assertEquals(0, queueView.getDispatchCount());
        receiver.flow(1);
        Assert.assertNotNull(receiver.receive(5, TimeUnit.SECONDS));
        receiver.close();
        Assert.assertEquals(1, queueView.getQueueSize());
        connection.close();
    }

    @Test(timeout = 60000)
    @Repeat(repetitions = 1)
    public void testPresettledReceiverReadsAllMessages() throws Exception {
        final int MSG_COUNT = 100;
        sendMessages(getTestName(), MSG_COUNT, false);
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createReceiver(("queue://" + (getTestName())), null, false, true);
        QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertEquals(MSG_COUNT, queueView.getQueueSize());
        Assert.assertEquals(0, queueView.getDispatchCount());
        receiver.flow(MSG_COUNT);
        for (int i = 0; i < MSG_COUNT; ++i) {
            Assert.assertNotNull(receiver.receive(5, TimeUnit.SECONDS));
        }
        receiver.close();
        Assert.assertEquals(0, queueView.getQueueSize());
        connection.close();
    }

    @Test(timeout = 60000)
    @Repeat(repetitions = 1)
    public void testPresettledReceiverReadsAllMessagesInNonFlowBatchQueue() throws Exception {
        doTestPresettledReceiverReadsAllMessagesInNonFlowBatch(false);
    }

    @Test(timeout = 60000)
    @Repeat(repetitions = 1)
    public void testPresettledReceiverReadsAllMessagesInNonFlowBatchTopic() throws Exception {
        doTestPresettledReceiverReadsAllMessagesInNonFlowBatch(true);
    }

    @Test(timeout = 60000)
    @Repeat(repetitions = 1)
    public void testTwoQueueReceiversOnSameConnectionReadMessagesNoDispositions() throws Exception {
        int MSG_COUNT = 4;
        sendMessages(getTestName(), MSG_COUNT, false);
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver1 = session.createReceiver(("queue://" + (getTestName())));
        QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertEquals(MSG_COUNT, queueView.getQueueSize());
        receiver1.flow(2);
        Assert.assertNotNull(receiver1.receive(5, TimeUnit.SECONDS));
        Assert.assertNotNull(receiver1.receive(5, TimeUnit.SECONDS));
        AmqpReceiver receiver2 = session.createReceiver(("queue://" + (getTestName())));
        Assert.assertEquals(2, brokerService.getAdminView().getQueueSubscribers().length);
        receiver2.flow(2);
        Assert.assertNotNull(receiver2.receive(5, TimeUnit.SECONDS));
        Assert.assertNotNull(receiver2.receive(5, TimeUnit.SECONDS));
        Assert.assertEquals(MSG_COUNT, queueView.getDispatchCount());
        Assert.assertEquals(0, queueView.getDequeueCount());
        receiver1.close();
        receiver2.close();
        Assert.assertEquals(MSG_COUNT, queueView.getQueueSize());
        connection.close();
    }

    @Test(timeout = 60000)
    public void testTwoQueueReceiversOnSameConnectionReadMessagesAcceptOnEach() throws Exception {
        int MSG_COUNT = 4;
        sendMessages(getTestName(), MSG_COUNT, false);
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver1 = session.createReceiver(("queue://" + (getTestName())));
        final QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertEquals(MSG_COUNT, queueView.getQueueSize());
        receiver1.flow(2);
        AmqpMessage message = receiver1.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(message);
        message.accept();
        message = receiver1.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(message);
        message.accept();
        Assert.assertTrue("Should have ack'd two", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (queueView.getDequeueCount()) == 2;
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS.toMillis(50)));
        AmqpReceiver receiver2 = session.createReceiver(("queue://" + (getTestName())));
        Assert.assertEquals(2, brokerService.getAdminView().getQueueSubscribers().length);
        receiver2.flow(2);
        message = receiver2.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(message);
        message.accept();
        message = receiver2.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(message);
        message.accept();
        Assert.assertEquals(MSG_COUNT, queueView.getDispatchCount());
        Assert.assertTrue("Queue should be empty now", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (queueView.getDequeueCount()) == 4;
            }
        }, TimeUnit.SECONDS.toMillis(15), TimeUnit.MILLISECONDS.toMillis(10)));
        receiver1.close();
        receiver2.close();
        Assert.assertEquals(0, queueView.getQueueSize());
        connection.close();
    }

    @Test(timeout = 60000)
    public void testSecondReceiverOnQueueGetsAllUnconsumedMessages() throws Exception {
        int MSG_COUNT = 20;
        sendMessages(getTestName(), MSG_COUNT, false);
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver1 = session.createReceiver(("queue://" + (getTestName())));
        final QueueViewMBean queueView = getProxyToQueue(getTestName());
        Assert.assertEquals(MSG_COUNT, queueView.getQueueSize());
        receiver1.flow(20);
        Assert.assertTrue("Should have dispatch to prefetch", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (queueView.getInFlightCount()) >= 2;
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS.toMillis(50)));
        receiver1.close();
        AmqpReceiver receiver2 = session.createReceiver(("queue://" + (getTestName())));
        Assert.assertEquals(1, brokerService.getAdminView().getQueueSubscribers().length);
        receiver2.flow((MSG_COUNT * 2));
        AmqpMessage message = receiver2.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(message);
        message.accept();
        message = receiver2.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull(message);
        message.accept();
        Assert.assertTrue("Should have ack'd two", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (queueView.getDequeueCount()) == 2;
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS.toMillis(50)));
        receiver2.close();
        Assert.assertEquals((MSG_COUNT - 2), queueView.getQueueSize());
        connection.close();
    }

    @Test(timeout = 60000)
    public void testUnsupportedFiltersAreNotListedAsSupported() throws Exception {
        AmqpClient client = createAmqpClient();
        client.setValidator(new AmqpValidator() {
            @SuppressWarnings("unchecked")
            @Override
            public void inspectOpenedResource(Receiver receiver) {
                AmqpTestSupport.LOG.info("Receiver opened: {}", receiver);
                if ((receiver.getRemoteSource()) == null) {
                    markAsInvalid("Link opened with null source.");
                }
                Source source = ((Source) (receiver.getRemoteSource()));
                Map<Symbol, Object> filters = source.getFilter();
                if ((AmqpSupport.findFilter(filters, AmqpUnknownFilterType.UNKNOWN_FILTER_IDS)) != null) {
                    markAsInvalid("Broker should not return unsupported filter on attach.");
                }
            }
        });
        Map<Symbol, DescribedType> filters = new HashMap<>();
        filters.put(AmqpUnknownFilterType.UNKNOWN_FILTER_NAME, AmqpUnknownFilterType.UNKOWN_FILTER);
        Source source = new Source();
        source.setAddress(("queue://" + (getTestName())));
        source.setFilter(filters);
        source.setDurable(NONE);
        source.setExpiryPolicy(LINK_DETACH);
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerService.getAdminView().getQueues().length);
        session.createReceiver(source);
        Assert.assertEquals(1, brokerService.getAdminView().getQueueSubscribers().length);
        connection.getStateInspector().assertValid();
        connection.close();
    }

    @Test(timeout = 30000)
    public void testReleasedDisposition() throws Exception {
        sendMessages(getTestName(), 1, false);
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createReceiver(getTestName());
        receiver.flow(2);
        AmqpMessage message = receiver.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull("did not receive message first time", message);
        Message protonMessage = message.getWrappedMessage();
        Assert.assertNotNull(protonMessage);
        Assert.assertEquals("Unexpected initial value for AMQP delivery-count", 0, protonMessage.getDeliveryCount());
        message.release();
        // Read the message again and validate its state
        message = receiver.receive(10, TimeUnit.SECONDS);
        Assert.assertNotNull("did not receive message again", message);
        message.accept();
        protonMessage = message.getWrappedMessage();
        Assert.assertNotNull(protonMessage);
        Assert.assertEquals("Unexpected updated value for AMQP delivery-count", 0, protonMessage.getDeliveryCount());
        connection.close();
    }

    @Test(timeout = 30000)
    public void testRejectedDisposition() throws Exception {
        sendMessages(getTestName(), 1, false);
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.connect());
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createReceiver(getTestName());
        receiver.flow(2);
        AmqpMessage message = receiver.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull("did not receive message first time", message);
        Message protonMessage = message.getWrappedMessage();
        Assert.assertNotNull(protonMessage);
        Assert.assertEquals("Unexpected initial value for AMQP delivery-count", 0, protonMessage.getDeliveryCount());
        message.reject();
        // Attempt to read the message again but should not get it.
        message = receiver.receive(2, TimeUnit.SECONDS);
        Assert.assertNull("shoudl not receive message again", message);
        connection.close();
    }

    @Test(timeout = 30000)
    public void testModifiedDispositionWithDeliveryFailedWithoutUndeliverableHereFieldsSet() throws Exception {
        doModifiedDispositionTestImpl(Boolean.TRUE, null);
    }

    @Test(timeout = 30000)
    public void testModifiedDispositionWithoutDeliveryFailedWithoutUndeliverableHereFieldsSet() throws Exception {
        doModifiedDispositionTestImpl(null, null);
    }

    @Test(timeout = 30000)
    public void testModifiedDispositionWithoutDeliveryFailedWithUndeliverableHereFieldsSet() throws Exception {
        doModifiedDispositionTestImpl(null, Boolean.TRUE);
    }

    @Test(timeout = 30000)
    public void testModifiedDispositionWithDeliveryFailedWithUndeliverableHereFieldsSet() throws Exception {
        doModifiedDispositionTestImpl(Boolean.TRUE, Boolean.TRUE);
    }
}

