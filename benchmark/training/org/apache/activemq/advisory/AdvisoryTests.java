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
package org.apache.activemq.advisory;


import AdvisorySupport.MSG_PROPERTY_DESTINATION;
import AdvisorySupport.MSG_PROPERTY_ORIGIN_BROKER_URL;
import Session.AUTO_ACKNOWLEDGE;
import Session.CLIENT_ACKNOWLEDGE;
import java.util.HashSet;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static AdvisorySupport.MESSAGE_DELIVERED_TOPIC_PREFIX;


/**
 * Test for advisory messages sent under the right circumstances.
 */
@RunWith(Parameterized.class)
public class AdvisoryTests {
    protected static final int MESSAGE_COUNT = 2000;

    protected BrokerService broker;

    protected Connection connection;

    protected String bindAddress = ActiveMQConnectionFactory.DEFAULT_BROKER_BIND_URL;

    protected int topicCount;

    protected final boolean includeBodyForAdvisory;

    protected final int EXPIRE_MESSAGE_PERIOD = 10000;

    public AdvisoryTests(boolean includeBodyForAdvisory) {
        super();
        this.includeBodyForAdvisory = includeBodyForAdvisory;
    }

    @Test(timeout = 60000)
    public void testNoSlowConsumerAdvisory() throws Exception {
        Session s = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = s.createQueue(getClass().getName());
        MessageConsumer consumer = s.createConsumer(queue);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
            }
        });
        Topic advisoryTopic = AdvisorySupport.getSlowConsumerAdvisoryTopic(((ActiveMQDestination) (queue)));
        s = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer advisoryConsumer = s.createConsumer(advisoryTopic);
        // start throwing messages at the consumer
        MessageProducer producer = s.createProducer(queue);
        for (int i = 0; i < (AdvisoryTests.MESSAGE_COUNT); i++) {
            BytesMessage m = s.createBytesMessage();
            m.writeBytes(new byte[1024]);
            producer.send(m);
        }
        Message msg = advisoryConsumer.receive(1000);
        Assert.assertNull(msg);
    }

    @Test(timeout = 60000)
    public void testSlowConsumerAdvisory() throws Exception {
        Session s = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = s.createQueue(getClass().getName());
        MessageConsumer consumer = s.createConsumer(queue);
        Assert.assertNotNull(consumer);
        Topic advisoryTopic = AdvisorySupport.getSlowConsumerAdvisoryTopic(((ActiveMQDestination) (queue)));
        s = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer advisoryConsumer = s.createConsumer(advisoryTopic);
        // start throwing messages at the consumer
        MessageProducer producer = s.createProducer(queue);
        for (int i = 0; i < (AdvisoryTests.MESSAGE_COUNT); i++) {
            BytesMessage m = s.createBytesMessage();
            m.writeBytes(new byte[1024]);
            producer.send(m);
        }
        Message msg = advisoryConsumer.receive(1000);
        Assert.assertNotNull(msg);
    }

    @Test(timeout = 60000)
    public void testMessageDeliveryAdvisory() throws Exception {
        Session s = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = s.createQueue(getClass().getName());
        MessageConsumer consumer = s.createConsumer(queue);
        Assert.assertNotNull(consumer);
        Topic advisoryTopic = AdvisorySupport.getMessageDeliveredAdvisoryTopic(((ActiveMQDestination) (queue)));
        MessageConsumer advisoryConsumer = s.createConsumer(advisoryTopic);
        // start throwing messages at the consumer
        MessageProducer producer = s.createProducer(queue);
        BytesMessage m = s.createBytesMessage();
        m.writeBytes(new byte[1024]);
        producer.send(m);
        Message msg = advisoryConsumer.receive(1000);
        Assert.assertNotNull(msg);
        ActiveMQMessage message = ((ActiveMQMessage) (msg));
        ActiveMQMessage payload = ((ActiveMQMessage) (message.getDataStructure()));
        // This should always be tcp:// because that is the transport that is used to connect even though
        // the nio transport is the first one in the list
        Assert.assertTrue(((String) (message.getProperty(MSG_PROPERTY_ORIGIN_BROKER_URL))).startsWith("tcp://"));
        Assert.assertEquals(message.getProperty(MSG_PROPERTY_DESTINATION), getQualifiedName());
        // Add assertion to make sure body is included for advisory topics
        // when includeBodyForAdvisory is true
        assertIncludeBodyForAdvisory(payload);
    }

    @Test(timeout = 60000)
    public void testMessageConsumedAdvisory() throws Exception {
        Session s = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = s.createQueue(getClass().getName());
        MessageConsumer consumer = s.createConsumer(queue);
        Topic advisoryTopic = AdvisorySupport.getMessageConsumedAdvisoryTopic(((ActiveMQDestination) (queue)));
        MessageConsumer advisoryConsumer = s.createConsumer(advisoryTopic);
        // start throwing messages at the consumer
        MessageProducer producer = s.createProducer(queue);
        BytesMessage m = s.createBytesMessage();
        m.writeBytes(new byte[1024]);
        producer.send(m);
        String id = m.getJMSMessageID();
        Message msg = consumer.receive(1000);
        Assert.assertNotNull(msg);
        msg = advisoryConsumer.receive(1000);
        Assert.assertNotNull(msg);
        ActiveMQMessage message = ((ActiveMQMessage) (msg));
        ActiveMQMessage payload = ((ActiveMQMessage) (message.getDataStructure()));
        String originalId = payload.getJMSMessageID();
        Assert.assertEquals(originalId, id);
        // This should always be tcp:// because that is the transport that is used to connect even though
        // the nio transport is the first one in the list
        Assert.assertTrue(((String) (message.getProperty(MSG_PROPERTY_ORIGIN_BROKER_URL))).startsWith("tcp://"));
        Assert.assertEquals(message.getProperty(MSG_PROPERTY_DESTINATION), getQualifiedName());
        // Add assertion to make sure body is included for advisory topics
        // when includeBodyForAdvisory is true
        assertIncludeBodyForAdvisory(payload);
    }

    @Test(timeout = 60000)
    public void testMessageExpiredAdvisory() throws Exception {
        Session s = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = s.createQueue(getClass().getName());
        MessageConsumer consumer = s.createConsumer(queue);
        Assert.assertNotNull(consumer);
        Topic advisoryTopic = AdvisorySupport.getExpiredMessageTopic(((ActiveMQDestination) (queue)));
        MessageConsumer advisoryConsumer = s.createConsumer(advisoryTopic);
        // start throwing messages at the consumer
        MessageProducer producer = s.createProducer(queue);
        producer.setTimeToLive(1);
        for (int i = 0; i < (AdvisoryTests.MESSAGE_COUNT); i++) {
            BytesMessage m = s.createBytesMessage();
            m.writeBytes(new byte[1024]);
            producer.send(m);
        }
        Message msg = advisoryConsumer.receive(EXPIRE_MESSAGE_PERIOD);
        Assert.assertNotNull(msg);
        ActiveMQMessage message = ((ActiveMQMessage) (msg));
        ActiveMQMessage payload = ((ActiveMQMessage) (message.getDataStructure()));
        // This should be set
        Assert.assertNotNull(message.getProperty(MSG_PROPERTY_ORIGIN_BROKER_URL));
        // Add assertion to make sure body is included for advisory topics
        // when includeBodyForAdvisory is true
        assertIncludeBodyForAdvisory(payload);
    }

    @Test(timeout = 60000)
    public void testMessageDLQd() throws Exception {
        ActiveMQPrefetchPolicy policy = new ActiveMQPrefetchPolicy();
        policy.setTopicPrefetch(2);
        ((ActiveMQConnection) (connection)).setPrefetchPolicy(policy);
        Session s = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        Topic topic = s.createTopic(getClass().getName());
        Topic advisoryTopic = s.createTopic(">");
        for (int i = 0; i < 100; i++) {
            s.createConsumer(advisoryTopic);
        }
        MessageConsumer advisoryConsumer = s.createConsumer(AdvisorySupport.getMessageDLQdAdvisoryTopic(((ActiveMQDestination) (topic))));
        MessageProducer producer = s.createProducer(topic);
        int count = 10;
        for (int i = 0; i < count; i++) {
            BytesMessage m = s.createBytesMessage();
            m.writeBytes(new byte[1024]);
            producer.send(m);
        }
        Message msg = advisoryConsumer.receive(1000);
        Assert.assertNotNull(msg);
        ActiveMQMessage message = ((ActiveMQMessage) (msg));
        ActiveMQMessage payload = ((ActiveMQMessage) (message.getDataStructure()));
        // This should be set
        Assert.assertNotNull(message.getProperty(MSG_PROPERTY_ORIGIN_BROKER_URL));
        // Add assertion to make sure body is included for DLQ advisory topics
        // when includeBodyForAdvisory is true
        assertIncludeBodyForAdvisory(payload);
        // we should get here without StackOverflow
    }

    @Test(timeout = 60000)
    public void testMessageDiscardedAdvisory() throws Exception {
        Session s = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        Topic topic = s.createTopic(getClass().getName());
        MessageConsumer consumer = s.createConsumer(topic);
        Assert.assertNotNull(consumer);
        Topic advisoryTopic = AdvisorySupport.getMessageDiscardedAdvisoryTopic(((ActiveMQDestination) (topic)));
        MessageConsumer advisoryConsumer = s.createConsumer(advisoryTopic);
        // start throwing messages at the consumer
        MessageProducer producer = s.createProducer(topic);
        int count = (new ActiveMQPrefetchPolicy().getTopicPrefetch()) * 2;
        for (int i = 0; i < count; i++) {
            BytesMessage m = s.createBytesMessage();
            m.writeBytes(new byte[1024]);
            producer.send(m);
        }
        Message msg = advisoryConsumer.receive(1000);
        Assert.assertNotNull(msg);
        ActiveMQMessage message = ((ActiveMQMessage) (msg));
        ActiveMQMessage payload = ((ActiveMQMessage) (message.getDataStructure()));
        // This should be set
        Assert.assertNotNull(message.getProperty(MSG_PROPERTY_ORIGIN_BROKER_URL));
        Assert.assertEquals(message.getProperty(MSG_PROPERTY_DESTINATION), getQualifiedName());
        // Add assertion to make sure body is included for advisory topics
        // when includeBodyForAdvisory is true
        assertIncludeBodyForAdvisory(payload);
    }

    @Test(timeout = 60000)
    public void testMessageDeliveryVTAdvisory() throws Exception {
        Session s = connection.createSession(false, AUTO_ACKNOWLEDGE);
        ActiveMQTopic vt = new ActiveMQTopic("VirtualTopic.TEST");
        ActiveMQQueue a = new ActiveMQQueue("Consumer.A.VirtualTopic.TEST");
        MessageConsumer consumer = s.createConsumer(a);
        ActiveMQQueue b = new ActiveMQQueue("Consumer.B.VirtualTopic.TEST");
        MessageConsumer consumerB = s.createConsumer(b);
        Assert.assertNotNull(consumer);
        Assert.assertNotNull(consumerB);
        HashSet<String> dests = new HashSet<String>();
        dests.add(vt.getQualifiedName());
        dests.add(a.getQualifiedName());
        dests.add(b.getQualifiedName());
        Topic advisoryTopic = new ActiveMQTopic(((MESSAGE_DELIVERED_TOPIC_PREFIX) + ">"));
        MessageConsumer advisoryConsumer = s.createConsumer(advisoryTopic);
        // throw messages at the vt
        MessageProducer producer = s.createProducer(vt);
        BytesMessage m = s.createBytesMessage();
        m.writeBytes(new byte[1024]);
        producer.send(m);
        Message msg = null;
        while ((msg = advisoryConsumer.receive(1000)) != null) {
            ActiveMQMessage message = ((ActiveMQMessage) (msg));
            String dest = ((String) (message.getProperty(MSG_PROPERTY_DESTINATION)));
            dests.remove(dest);
            assertIncludeBodyForAdvisory(((ActiveMQMessage) (message.getDataStructure())));
        } 
        Assert.assertTrue(("Got delivered for all: " + dests), dests.isEmpty());
    }
}

