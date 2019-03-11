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
package org.apache.activemq.network.jms;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


/**
 * These test cases are used to verify that queue outbound bridge connections get
 * re-established in all broker restart scenarios. This is possible when the
 * outbound bridge is configured using the failover URI with a timeout.
 */
public class TopicOutboundBridgeReconnectTest {
    private BrokerService producerBroker;

    private BrokerService consumerBroker;

    private ActiveMQConnectionFactory producerConnectionFactory;

    private ActiveMQConnectionFactory consumerConnectionFactory;

    private Destination destination;

    private final ArrayList<Connection> connections = new ArrayList<Connection>();

    @Test
    public void testMultipleProducerBrokerRestarts() throws Exception {
        for (int i = 0; i < 10; i++) {
            testWithProducerBrokerRestart();
            disposeConsumerConnections();
        }
    }

    @Test
    public void testWithoutRestartsConsumerFirst() throws Exception {
        startConsumerBroker();
        startProducerBroker();
        MessageConsumer consumer = createConsumer();
        sendMessage("test123");
        sendMessage("test456");
        Message message = consumer.receive(2000);
        Assert.assertNotNull(message);
        Assert.assertEquals("test123", getText());
        message = consumer.receive(5000);
        Assert.assertNotNull(message);
        Assert.assertEquals("test456", getText());
        Assert.assertNull(consumer.receiveNoWait());
    }

    @Test
    public void testWithoutRestartsProducerFirst() throws Exception {
        startProducerBroker();
        sendMessage("test123");
        startConsumerBroker();
        // unless using a failover URI, the first attempt of this send will likely fail, so increase the timeout below
        // to give the bridge time to recover
        sendMessage("test456");
        MessageConsumer consumer = createConsumer();
        Message message = consumer.receive(5000);
        Assert.assertNotNull(message);
        Assert.assertEquals("test123", getText());
        message = consumer.receive(5000);
        Assert.assertNotNull(message);
        Assert.assertEquals("test456", getText());
        Assert.assertNull(consumer.receiveNoWait());
    }

    @Test
    public void testWithProducerBrokerRestart() throws Exception {
        startProducerBroker();
        startConsumerBroker();
        MessageConsumer consumer = createConsumer();
        Assert.assertTrue("Should have a bridge to the topic", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToProducerBroker().getTopicSubscribers().length) > 0;
            }
        }));
        sendMessage("test123");
        Message message = consumer.receive(5000);
        Assert.assertNotNull(message);
        Assert.assertEquals("test123", getText());
        Assert.assertNull(consumer.receiveNoWait());
        // Restart the first broker...
        stopProducerBroker();
        startProducerBroker();
        Assert.assertTrue("Should have a bridge to the topic", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getProxyToProducerBroker().getTopicSubscribers().length) > 0;
            }
        }));
        sendMessage("test123");
        message = consumer.receive(5000);
        Assert.assertNotNull(message);
        Assert.assertEquals("test123", getText());
        Assert.assertNull(consumer.receiveNoWait());
    }

    @Test
    public void testWithConsumerBrokerRestart() throws Exception {
        startProducerBroker();
        startConsumerBroker();
        final MessageConsumer consumer1 = createConsumer();
        sendMessage("test123");
        Message message = consumer1.receive(5000);
        Assert.assertNotNull(message);
        Assert.assertEquals("test123", getText());
        Assert.assertNull(consumer1.receiveNoWait());
        consumer1.close();
        // Restart the first broker...
        stopConsumerBroker();
        startConsumerBroker();
        // unless using a failover URI, the first attempt of this send will likely fail, so increase the timeout below
        // to give the bridge time to recover
        sendMessage("test123");
        final MessageConsumer consumer2 = createConsumer();
        Assert.assertTrue("Expected recover and delivery failed", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Message message = consumer2.receiveNoWait();
                if ((message == null) || (!(getText().equals("test123")))) {
                    return false;
                }
                return true;
            }
        }));
        Assert.assertNull(consumer2.receiveNoWait());
    }

    @Test
    public void testWithConsumerBrokerStartDelay() throws Exception {
        startConsumerBroker();
        final MessageConsumer consumer = createConsumer();
        TimeUnit.SECONDS.sleep(5);
        startProducerBroker();
        sendMessage("test123");
        Assert.assertTrue("Expected recover and delivery failed", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                Message message = consumer.receiveNoWait();
                if ((message == null) || (!(getText().equals("test123")))) {
                    return false;
                }
                return true;
            }
        }));
        Assert.assertNull(consumer.receiveNoWait());
    }

    @Test
    public void testWithProducerBrokerStartDelay() throws Exception {
        startProducerBroker();
        TimeUnit.SECONDS.sleep(5);
        startConsumerBroker();
        MessageConsumer consumer = createConsumer();
        final BrokerViewMBean producerBrokerView = getProxyToProducerBroker();
        Assert.assertTrue("Should have a bridge to the topic", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (producerBrokerView.getTopicSubscribers().length) > 0;
            }
        }));
        sendMessage("test123");
        Message message = consumer.receive(2000);
        Assert.assertNotNull(message);
        Assert.assertEquals("test123", getText());
        Assert.assertNull(consumer.receiveNoWait());
    }
}

