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
package org.apache.activemq.transport.mqtt;


import RetainedMessageSubscriptionRecoveryPolicy.RETAINED_PROPERTY;
import RetainedMessageSubscriptionRecoveryPolicy.RETAIN_PROPERTY;
import Session.AUTO_ACKNOWLEDGE;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.util.ByteSequence;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class MQTTCompositeQueueRetainedTest extends MQTTTestSupport {
    // configure composite topic
    private static final String COMPOSITE_TOPIC = "Composite.TopicA";

    private static final String FORWARD_QUEUE = "Composite.Queue.A";

    private static final String FORWARD_TOPIC = "Composite.Topic.A";

    private static final int NUM_MESSAGES = 25;

    @Test(timeout = 60 * 1000)
    public void testSendMQTTReceiveJMSCompositeDestinations() throws Exception {
        final MQTTClientProvider provider = getMQTTClientProvider();
        initializeConnection(provider);
        // send retained message
        final String MQTT_TOPIC = "Composite/TopicA";
        final String RETAINED = "RETAINED";
        provider.publish(MQTT_TOPIC, RETAINED.getBytes(), MQTTTestSupport.AT_LEAST_ONCE, true);
        ActiveMQConnection activeMQConnection = ((ActiveMQConnection) (new ActiveMQConnectionFactory(jmsUri).createConnection()));
        // MUST set to true to receive retained messages
        activeMQConnection.setUseRetroactiveConsumer(true);
        activeMQConnection.setClientID("jms-client");
        activeMQConnection.start();
        Session s = activeMQConnection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue jmsQueue = s.createQueue(MQTTCompositeQueueRetainedTest.FORWARD_QUEUE);
        Topic jmsTopic = s.createTopic(MQTTCompositeQueueRetainedTest.FORWARD_TOPIC);
        MessageConsumer queueConsumer = s.createConsumer(jmsQueue);
        MessageConsumer topicConsumer = s.createDurableSubscriber(jmsTopic, "jms-subscription");
        // check whether we received retained message twice on mapped Queue, once marked as RETAINED
        ActiveMQMessage message;
        ByteSequence bs;
        for (int i = 0; i < 2; i++) {
            message = ((ActiveMQMessage) (queueConsumer.receive(5000)));
            Assert.assertNotNull(("Should get retained message from " + (MQTTCompositeQueueRetainedTest.FORWARD_QUEUE)), message);
            bs = message.getContent();
            Assert.assertEquals(RETAINED, new String(bs.data, bs.offset, bs.length));
            Assert.assertTrue(((message.getBooleanProperty(RETAIN_PROPERTY)) != (message.getBooleanProperty(RETAINED_PROPERTY))));
        }
        // check whether we received retained message on mapped Topic
        message = ((ActiveMQMessage) (topicConsumer.receive(5000)));
        Assert.assertNotNull(("Should get retained message from " + (MQTTCompositeQueueRetainedTest.FORWARD_TOPIC)), message);
        bs = message.getContent();
        Assert.assertEquals(RETAINED, new String(bs.data, bs.offset, bs.length));
        Assert.assertFalse(message.getBooleanProperty(RETAIN_PROPERTY));
        Assert.assertTrue(message.getBooleanProperty(RETAINED_PROPERTY));
        for (int i = 0; i < (MQTTCompositeQueueRetainedTest.NUM_MESSAGES); i++) {
            String payload = "Test Message: " + i;
            provider.publish(MQTT_TOPIC, payload.getBytes(), MQTTTestSupport.AT_LEAST_ONCE);
            message = ((ActiveMQMessage) (queueConsumer.receive(5000)));
            Assert.assertNotNull(("Should get a message from " + (MQTTCompositeQueueRetainedTest.FORWARD_QUEUE)), message);
            bs = message.getContent();
            Assert.assertEquals(payload, new String(bs.data, bs.offset, bs.length));
            message = ((ActiveMQMessage) (topicConsumer.receive(5000)));
            Assert.assertNotNull(("Should get a message from " + (MQTTCompositeQueueRetainedTest.FORWARD_TOPIC)), message);
            bs = message.getContent();
            Assert.assertEquals(payload, new String(bs.data, bs.offset, bs.length));
        }
        // close consumer and look for retained messages again
        queueConsumer.close();
        topicConsumer.close();
        queueConsumer = s.createConsumer(jmsQueue);
        topicConsumer = s.createDurableSubscriber(jmsTopic, "jms-subscription");
        // check whether we received retained message on mapped Queue, again
        message = ((ActiveMQMessage) (queueConsumer.receive(5000)));
        Assert.assertNotNull(("Should get recovered retained message from " + (MQTTCompositeQueueRetainedTest.FORWARD_QUEUE)), message);
        bs = message.getContent();
        Assert.assertEquals(RETAINED, new String(bs.data, bs.offset, bs.length));
        Assert.assertTrue(message.getBooleanProperty(RETAINED_PROPERTY));
        Assert.assertNull(("Should not get second retained message from " + (MQTTCompositeQueueRetainedTest.FORWARD_QUEUE)), queueConsumer.receive(2000));
        // check whether we received retained message on mapped Topic, again
        message = ((ActiveMQMessage) (topicConsumer.receive(5000)));
        Assert.assertNotNull(("Should get recovered retained message from " + (MQTTCompositeQueueRetainedTest.FORWARD_TOPIC)), message);
        bs = message.getContent();
        Assert.assertEquals(RETAINED, new String(bs.data, bs.offset, bs.length));
        Assert.assertTrue(message.getBooleanProperty(RETAINED_PROPERTY));
        Assert.assertNull(("Should not get second retained message from " + (MQTTCompositeQueueRetainedTest.FORWARD_TOPIC)), topicConsumer.receive(2000));
        // create second queue consumer and verify that it doesn't trigger message recovery
        final MessageConsumer queueConsumer2 = s.createConsumer(jmsQueue);
        Assert.assertNull(("Second consumer MUST not receive retained message from " + (MQTTCompositeQueueRetainedTest.FORWARD_QUEUE)), queueConsumer2.receive(2000));
        activeMQConnection.close();
        provider.disconnect();
    }
}

