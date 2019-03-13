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
package org.apache.activemq;


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.TimeUnit;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


public class RemoveDestinationTest {
    private static final String VM_BROKER_URL = "vm://localhost?create=false";

    BrokerService broker;

    @Test(timeout = 60000)
    public void testRemoveQueue() throws Exception {
        ActiveMQConnection amqConnection = ((ActiveMQConnection) (createConnection(true)));
        final DestinationSource destinationSource = amqConnection.getDestinationSource();
        Session session = amqConnection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("TEST.FOO");
        MessageProducer producer = session.createProducer(queue);
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage msg = session.createTextMessage("Hellow World");
        producer.send(msg);
        Assert.assertNotNull(consumer.receive(5000));
        final ActiveMQQueue amqQueue = ((ActiveMQQueue) (queue));
        consumer.close();
        producer.close();
        session.close();
        Assert.assertTrue("Destination discovered", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return destinationSource.getQueues().contains(amqQueue);
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        amqConnection.destroyDestination(((ActiveMQDestination) (queue)));
        Assert.assertTrue("Destination is removed", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return !(destinationSource.getQueues().contains(amqQueue));
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
    }

    @Test(timeout = 60000)
    public void testRemoveDestinationWithoutSubscriber() throws Exception {
        ActiveMQConnection amqConnection = ((ActiveMQConnection) (createConnection(true)));
        final DestinationSource destinationSource = amqConnection.getDestinationSource();
        Session session = amqConnection.createSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("TEST.FOO");
        MessageProducer producer = session.createProducer(topic);
        final int consumerCount = broker.getAdminView().getTopicSubscribers().length;
        MessageConsumer consumer = session.createConsumer(topic);
        TextMessage msg = session.createTextMessage("Hellow World");
        producer.send(msg);
        Assert.assertNotNull(consumer.receive(5000));
        final ActiveMQTopic amqTopic = ((ActiveMQTopic) (topic));
        Assert.assertTrue("Destination never discovered", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return destinationSource.getTopics().contains(amqTopic);
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        consumer.close();
        producer.close();
        session.close();
        Assert.assertTrue("Subscriber still active", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (broker.getAdminView().getTopicSubscribers().length) == consumerCount;
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        amqConnection.destroyDestination(((ActiveMQDestination) (topic)));
        Assert.assertTrue("Destination still active", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return !(destinationSource.getTopics().contains(amqTopic));
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        Assert.assertTrue("Destination never unregistered", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return !(destinationPresentInAdminView(broker, amqTopic));
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
    }

    @Test(timeout = 60000)
    public void testRemoveDestinationWithSubscriber() throws Exception {
        ActiveMQConnection amqConnection = ((ActiveMQConnection) (createConnection(true)));
        final DestinationSource destinationSource = amqConnection.getDestinationSource();
        Session session = amqConnection.createSession(false, AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("TEST.FOO");
        MessageProducer producer = session.createProducer(topic);
        final int consumerCount = broker.getAdminView().getTopicSubscribers().length;
        MessageConsumer consumer = session.createConsumer(topic);
        TextMessage msg = session.createTextMessage("Hellow World");
        producer.send(msg);
        Assert.assertNotNull(consumer.receive(5000));
        final ActiveMQTopic amqTopic = ((ActiveMQTopic) (topic));
        Assert.assertTrue("Destination never registered", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return destinationPresentInAdminView(broker, amqTopic);
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        Assert.assertTrue("Destination never discovered", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return destinationSource.getTopics().contains(amqTopic);
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        // This line generates a broker error since the consumer is still active.
        try {
            amqConnection.destroyDestination(((ActiveMQDestination) (topic)));
            Assert.fail("expect exception on destroy if comsumer present");
        } catch (JMSException expected) {
            Assert.assertTrue(((expected.getMessage().indexOf(amqTopic.getTopicName())) != (-1)));
        }
        Assert.assertTrue("Destination never registered", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return destinationPresentInAdminView(broker, amqTopic);
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        Assert.assertTrue("Destination never discovered", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return destinationSource.getTopics().contains(amqTopic);
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        consumer.close();
        producer.close();
        session.close();
        Assert.assertTrue("Subscriber still active", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (broker.getAdminView().getTopicSubscribers().length) == consumerCount;
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        // The destination will not be removed with this call, but if you remove
        // the call above that generates the error it will.
        amqConnection.destroyDestination(amqTopic);
        Assert.assertTrue("Destination still active", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return !(destinationSource.getTopics().contains(amqTopic));
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
        Assert.assertTrue("Destination never unregistered", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return !(destinationPresentInAdminView(broker, amqTopic));
            }
        }, TimeUnit.SECONDS.toMillis(30), TimeUnit.MILLISECONDS.toMillis(100)));
    }
}

