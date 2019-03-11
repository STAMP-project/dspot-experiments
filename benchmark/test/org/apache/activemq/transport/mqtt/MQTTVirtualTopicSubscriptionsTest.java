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


import DeliveryMode.PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.TimeUnit;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.Wait;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Run the basic tests with the NIO Transport.
 */
public class MQTTVirtualTopicSubscriptionsTest extends MQTTTest {
    private static final Logger LOG = LoggerFactory.getLogger(MQTTVirtualTopicSubscriptionsTest.class);

    @Override
    @Test(timeout = 60 * 1000)
    public void testSendMQTTReceiveJMS() throws Exception {
        doTestSendMQTTReceiveJMS("VirtualTopic.foo.*");
    }

    @Override
    @Test(timeout = (2 * 60) * 1000)
    public void testSendJMSReceiveMQTT() throws Exception {
        doTestSendJMSReceiveMQTT("VirtualTopic.foo.far");
    }

    @Override
    @Test(timeout = 30 * 10000)
    public void testJmsMapping() throws Exception {
        doTestJmsMapping("VirtualTopic.test.foo");
    }

    @Test(timeout = 60 * 1000)
    public void testSubscribeOnVirtualTopicAsDurable() throws Exception {
        MQTT mqtt = createMQTTConnection();
        mqtt.setClientId("VirtualTopicSubscriber");
        mqtt.setKeepAlive(((short) (2)));
        mqtt.setCleanSession(false);
        final BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        final String topicName = "VirtualTopic/foo/bah";
        connection.subscribe(new Topic[]{ new Topic(topicName, QoS.EXACTLY_ONCE) });
        Assert.assertTrue("Should create a durable subscription", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getDurableTopicSubscribers().length) == 1;
            }
        }));
        connection.unsubscribe(new String[]{ topicName });
        Assert.assertTrue("Should remove a durable subscription", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getDurableTopicSubscribers().length) == 0;
            }
        }));
        connection.disconnect();
    }

    @Test(timeout = 60 * 1000)
    public void testDurableVirtaulTopicSubIsRecovered() throws Exception {
        MQTT mqtt = createMQTTConnection();
        mqtt.setClientId("VirtualTopicSubscriber");
        mqtt.setKeepAlive(((short) (2)));
        mqtt.setCleanSession(false);
        final String topicName = "VirtualTopic/foo/bah";
        {
            final BlockingConnection connection = mqtt.blockingConnection();
            connection.connect();
            connection.subscribe(new Topic[]{ new Topic(topicName, QoS.EXACTLY_ONCE) });
            Assert.assertTrue("Should create a durable subscription", Wait.waitFor(new Wait.Condition() {
                @Override
                public boolean isSatisified() throws Exception {
                    return (brokerService.getAdminView().getDurableTopicSubscribers().length) == 1;
                }
            }));
            connection.disconnect();
        }
        Assert.assertTrue("Should be one inactive subscription", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getInactiveDurableTopicSubscribers().length) == 1;
            }
        }));
        {
            final BlockingConnection connection = mqtt.blockingConnection();
            connection.connect();
            Assert.assertTrue("Should recover a durable subscription", Wait.waitFor(new Wait.Condition() {
                @Override
                public boolean isSatisified() throws Exception {
                    return (brokerService.getAdminView().getDurableTopicSubscribers().length) == 1;
                }
            }));
            connection.subscribe(new Topic[]{ new Topic(topicName, QoS.EXACTLY_ONCE) });
            Assert.assertTrue("Should still be just one durable subscription", Wait.waitFor(new Wait.Condition() {
                @Override
                public boolean isSatisified() throws Exception {
                    return (brokerService.getAdminView().getDurableTopicSubscribers().length) == 1;
                }
            }));
            connection.disconnect();
        }
    }

    @Test(timeout = 60 * 1000)
    public void testRetainMessageDurability() throws Exception {
        MQTT mqtt = createMQTTConnection();
        mqtt.setClientId("sub");
        final BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        final String topicName = "foo/bah";
        connection.subscribe(new Topic[]{ new Topic(topicName, QoS.EXACTLY_ONCE) });
        // jms client
        ActiveMQConnection activeMQConnection = ((ActiveMQConnection) (cf.createConnection()));
        // MUST set to true to receive retained messages
        activeMQConnection.setUseRetroactiveConsumer(true);
        activeMQConnection.start();
        Session s = activeMQConnection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue consumerQ = s.createQueue("Consumer.RegularSub.VirtualTopic.foo.bah");
        MessageConsumer consumer = s.createConsumer(consumerQ);
        // publisher
        final MQTTClientProvider provider = getMQTTClientProvider();
        initializeConnection(provider);
        // send retained message
        final String RETAINED = "RETAINED_MESSAGE_TEXT";
        provider.publish(topicName, RETAINED.getBytes(), MQTTTestSupport.EXACTLY_ONCE, true);
        Message message = connection.receive(5, TimeUnit.SECONDS);
        Assert.assertNotNull("got message", message);
        String response = new String(message.getPayload());
        MQTTVirtualTopicSubscriptionsTest.LOG.info(("Got message:" + response));
        // jms - verify retained message is persistent
        ActiveMQMessage activeMQMessage = ((ActiveMQMessage) (consumer.receive(5000)));
        Assert.assertNotNull("Should get retained message", activeMQMessage);
        ByteSequence bs = activeMQMessage.getContent();
        Assert.assertEquals(RETAINED, new String(bs.data, bs.offset, bs.length));
        MQTTVirtualTopicSubscriptionsTest.LOG.info(("Got message with deliverMode:" + (activeMQMessage.getJMSDeliveryMode())));
        Assert.assertEquals(PERSISTENT, activeMQMessage.getJMSDeliveryMode());
        activeMQConnection.close();
        connection.unsubscribe(new String[]{ topicName });
        connection.disconnect();
    }
}

