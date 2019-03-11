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


import Session.AUTO_ACKNOWLEDGE;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.Topic;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMessage;
import org.junit.Assert;
import org.junit.Test;


public class AdvisoryTempDestinationTests {
    protected static final int MESSAGE_COUNT = 2000;

    protected static final int EXPIRE_MESSAGE_PERIOD = 10000;

    protected BrokerService broker;

    protected Connection connection;

    protected String connectionURI;

    protected int topicCount;

    @Test(timeout = 60000)
    public void testNoSlowConsumerAdvisory() throws Exception {
        Session s = connection.createSession(false, AUTO_ACKNOWLEDGE);
        TemporaryQueue queue = s.createTemporaryQueue();
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
        for (int i = 0; i < (AdvisoryTempDestinationTests.MESSAGE_COUNT); i++) {
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
        TemporaryQueue queue = s.createTemporaryQueue();
        MessageConsumer consumer = s.createConsumer(queue);
        Assert.assertNotNull(consumer);
        Topic advisoryTopic = AdvisorySupport.getSlowConsumerAdvisoryTopic(((ActiveMQDestination) (queue)));
        s = connection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer advisoryConsumer = s.createConsumer(advisoryTopic);
        // start throwing messages at the consumer
        MessageProducer producer = s.createProducer(queue);
        for (int i = 0; i < (AdvisoryTempDestinationTests.MESSAGE_COUNT); i++) {
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
        TemporaryQueue queue = s.createTemporaryQueue();
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
    }

    @Test(timeout = 60000)
    public void testTempMessageConsumedAdvisory() throws Exception {
        Session s = connection.createSession(false, AUTO_ACKNOWLEDGE);
        TemporaryQueue queue = s.createTemporaryQueue();
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
        for (int i = 0; i < (AdvisoryTempDestinationTests.MESSAGE_COUNT); i++) {
            BytesMessage m = s.createBytesMessage();
            m.writeBytes(new byte[1024]);
            producer.send(m);
        }
        Message msg = advisoryConsumer.receive(AdvisoryTempDestinationTests.EXPIRE_MESSAGE_PERIOD);
        Assert.assertNotNull(msg);
    }
}

