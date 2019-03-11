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


import TerminusDurability.UNSETTLED_STATE;
import TerminusExpiryPolicy.NEVER;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.transport.amqp.AmqpSupport;
import org.apache.activemq.transport.amqp.AmqpTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpClient;
import org.apache.activemq.transport.amqp.client.AmqpClientTestSupport;
import org.apache.activemq.transport.amqp.client.AmqpConnection;
import org.apache.activemq.transport.amqp.client.AmqpFrameValidator;
import org.apache.activemq.transport.amqp.client.AmqpReceiver;
import org.apache.activemq.transport.amqp.client.AmqpSession;
import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.Source;
import org.apache.qpid.proton.amqp.transport.Detach;
import org.apache.qpid.proton.engine.Receiver;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for broker side support of the Durable Subscription mapping for JMS.
 */
public class AmqpDurableReceiverTest extends AmqpClientTestSupport {
    private final String SELECTOR_STRING = "color = red";

    @Test(timeout = 60000)
    public void testCreateDurableReceiver() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setContainerId(getTestName());
        connection.connect();
        AmqpSession session = connection.createSession();
        session.createDurableReceiver(("topic://" + (getTestName())), getTestName());
        final BrokerViewMBean brokerView = getProxyToBroker();
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testDetachedDurableReceiverRemainsActive() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setContainerId(getTestName());
        connection.connect();
        connection.setReceivedFrameInspector(new AmqpFrameValidator() {
            @Override
            public void inspectDetach(Detach detach, Binary encoded) {
                if (detach.getClosed()) {
                    markAsInvalid("Remote should have detached but closed instead.");
                }
            }
        });
        connection.setSentFrameInspector(new AmqpFrameValidator() {
            @Override
            public void inspectDetach(Detach detach, Binary encoded) {
                if (detach.getClosed()) {
                    markAsInvalid("Client should have detached but closed instead.");
                }
            }
        });
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createDurableReceiver(("topic://" + (getTestName())), getTestName());
        final BrokerViewMBean brokerView = getProxyToBroker();
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.detach();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerView.getInactiveDurableTopicSubscribers().length);
        connection.getSentFrameInspector().assertValid();
        connection.getReceivedFrameInspector().assertValid();
        connection.close();
    }

    @Test(timeout = 60000)
    public void testCloseDurableReceiverRemovesSubscription() throws Exception {
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setContainerId(getTestName());
        connection.connect();
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createDurableReceiver(("topic://" + (getTestName())), getTestName());
        final BrokerViewMBean brokerView = getProxyToBroker();
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        receiver.close();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testReattachToDurableNode() throws Exception {
        final BrokerViewMBean brokerView = getProxyToBroker();
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setContainerId(getTestName());
        connection.connect();
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createDurableReceiver(("topic://" + (getTestName())), getTestName());
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.detach();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver = session.createDurableReceiver(("topic://" + (getTestName())), getTestName());
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.close();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testReattachToDurableNodeAfterRestart() throws Exception {
        final BrokerViewMBean brokerView = getProxyToBroker();
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setContainerId(getTestName());
        connection.connect();
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createDurableReceiver(("topic://" + (getTestName())), getTestName());
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.detach();
        connection.close();
        restartBroker();
        connection = client.createConnection();
        connection.setContainerId(getTestName());
        connection.connect();
        session = connection.createSession();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver = session.createDurableReceiver(("topic://" + (getTestName())), getTestName());
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.close();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testLookupExistingSubscription() throws Exception {
        final BrokerViewMBean brokerView = getProxyToBroker();
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setContainerId(getTestName());
        connection.connect();
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createDurableReceiver(("topic://" + (getTestName())), getTestName());
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.detach();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver = session.lookupSubscription(getTestName());
        Assert.assertNotNull(receiver);
        Receiver protonReceiver = receiver.getReceiver();
        Assert.assertNotNull(protonReceiver.getRemoteSource());
        Source remoteSource = ((Source) (protonReceiver.getRemoteSource()));
        if ((remoteSource.getFilter()) != null) {
            Assert.assertFalse(remoteSource.getFilter().containsKey(AmqpSupport.NO_LOCAL_NAME));
            Assert.assertFalse(remoteSource.getFilter().containsKey(AmqpSupport.JMS_SELECTOR_NAME));
        }
        Assert.assertEquals(NEVER, remoteSource.getExpiryPolicy());
        Assert.assertEquals(UNSETTLED_STATE, remoteSource.getDurable());
        Assert.assertEquals(AmqpSupport.COPY, remoteSource.getDistributionMode());
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.close();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testLookupExistingSubscriptionWithSelector() throws Exception {
        final BrokerViewMBean brokerView = getProxyToBroker();
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setContainerId(getTestName());
        connection.connect();
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createDurableReceiver(("topic://" + (getTestName())), getTestName(), SELECTOR_STRING);
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.detach();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver = session.lookupSubscription(getTestName());
        Assert.assertNotNull(receiver);
        Receiver protonReceiver = receiver.getReceiver();
        Assert.assertNotNull(protonReceiver.getRemoteSource());
        Source remoteSource = ((Source) (protonReceiver.getRemoteSource()));
        Assert.assertNotNull(remoteSource.getFilter());
        Assert.assertFalse(remoteSource.getFilter().containsKey(AmqpSupport.NO_LOCAL_NAME));
        Assert.assertTrue(remoteSource.getFilter().containsKey(AmqpSupport.JMS_SELECTOR_NAME));
        Assert.assertEquals(SELECTOR_STRING, getDescribed());
        Assert.assertEquals(NEVER, remoteSource.getExpiryPolicy());
        Assert.assertEquals(UNSETTLED_STATE, remoteSource.getDurable());
        Assert.assertEquals(AmqpSupport.COPY, remoteSource.getDistributionMode());
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.close();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testLookupExistingSubscriptionWithNoLocal() throws Exception {
        final BrokerViewMBean brokerView = getProxyToBroker();
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setContainerId(getTestName());
        connection.connect();
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createDurableReceiver(("topic://" + (getTestName())), getTestName(), null, true);
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.detach();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver = session.lookupSubscription(getTestName());
        Assert.assertNotNull(receiver);
        Receiver protonReceiver = receiver.getReceiver();
        Assert.assertNotNull(protonReceiver.getRemoteSource());
        Source remoteSource = ((Source) (protonReceiver.getRemoteSource()));
        Assert.assertNotNull(remoteSource.getFilter());
        Assert.assertTrue(remoteSource.getFilter().containsKey(AmqpSupport.NO_LOCAL_NAME));
        Assert.assertFalse(remoteSource.getFilter().containsKey(AmqpSupport.JMS_SELECTOR_NAME));
        Assert.assertEquals(NEVER, remoteSource.getExpiryPolicy());
        Assert.assertEquals(UNSETTLED_STATE, remoteSource.getDurable());
        Assert.assertEquals(AmqpSupport.COPY, remoteSource.getDistributionMode());
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.close();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testLookupExistingSubscriptionWithSelectorAndNoLocal() throws Exception {
        final BrokerViewMBean brokerView = getProxyToBroker();
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setContainerId(getTestName());
        connection.connect();
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createDurableReceiver(("topic://" + (getTestName())), getTestName(), SELECTOR_STRING, true);
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.detach();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver = session.lookupSubscription(getTestName());
        Assert.assertNotNull(receiver);
        Receiver protonReceiver = receiver.getReceiver();
        Assert.assertNotNull(protonReceiver.getRemoteSource());
        Source remoteSource = ((Source) (protonReceiver.getRemoteSource()));
        Assert.assertNotNull(remoteSource.getFilter());
        Assert.assertTrue(remoteSource.getFilter().containsKey(AmqpSupport.NO_LOCAL_NAME));
        Assert.assertTrue(remoteSource.getFilter().containsKey(AmqpSupport.JMS_SELECTOR_NAME));
        Assert.assertEquals(SELECTOR_STRING, getDescribed());
        Assert.assertEquals(NEVER, remoteSource.getExpiryPolicy());
        Assert.assertEquals(UNSETTLED_STATE, remoteSource.getDurable());
        Assert.assertEquals(AmqpSupport.COPY, remoteSource.getDistributionMode());
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.close();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testLookupExistingSubscriptionAfterRestartWithSelectorAndNoLocal() throws Exception {
        final BrokerViewMBean brokerView = getProxyToBroker();
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setContainerId(getTestName());
        connection.connect();
        AmqpSession session = connection.createSession();
        AmqpReceiver receiver = session.createDurableReceiver(("topic://" + (getTestName())), getTestName(), SELECTOR_STRING, true);
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.detach();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(1, brokerView.getInactiveDurableTopicSubscribers().length);
        restartBroker();
        connection = client.createConnection();
        connection.setContainerId(getTestName());
        connection.connect();
        session = connection.createSession();
        receiver = session.lookupSubscription(getTestName());
        Assert.assertNotNull(receiver);
        Receiver protonReceiver = receiver.getReceiver();
        Assert.assertNotNull(protonReceiver.getRemoteSource());
        Source remoteSource = ((Source) (protonReceiver.getRemoteSource()));
        Assert.assertNotNull(remoteSource.getFilter());
        Assert.assertTrue(remoteSource.getFilter().containsKey(AmqpSupport.NO_LOCAL_NAME));
        Assert.assertTrue(remoteSource.getFilter().containsKey(AmqpSupport.JMS_SELECTOR_NAME));
        Assert.assertEquals(SELECTOR_STRING, getDescribed());
        Assert.assertEquals(NEVER, remoteSource.getExpiryPolicy());
        Assert.assertEquals(UNSETTLED_STATE, remoteSource.getDurable());
        Assert.assertEquals(AmqpSupport.COPY, remoteSource.getDistributionMode());
        Assert.assertEquals(1, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        receiver.close();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testLookupNonExistingSubscription() throws Exception {
        final BrokerViewMBean brokerView = getProxyToBroker();
        AmqpClient client = createAmqpClient();
        AmqpConnection connection = trackConnection(client.createConnection());
        connection.setContainerId(getTestName());
        connection.connect();
        AmqpSession session = connection.createSession();
        Assert.assertEquals(0, brokerView.getDurableTopicSubscribers().length);
        Assert.assertEquals(0, brokerView.getInactiveDurableTopicSubscribers().length);
        try {
            session.lookupSubscription(getTestName());
            Assert.fail("Should throw an exception since there is not subscription");
        } catch (Exception e) {
            AmqpTestSupport.LOG.info("Error on lookup: {}", e.getMessage());
        }
        connection.close();
    }
}

