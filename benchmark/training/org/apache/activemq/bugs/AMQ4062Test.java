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
package org.apache.activemq.bugs;


import DeliveryMode.PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import Session.CLIENT_ACKNOWLEDGE;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.DurableTopicSubscription;
import org.apache.activemq.broker.region.policy.PolicyEntry;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.util.SubscriptionKey;
import org.junit.Assert;
import org.junit.Test;


public class AMQ4062Test {
    private BrokerService service;

    private PolicyEntry policy;

    private ConcurrentMap<SubscriptionKey, DurableTopicSubscription> durableSubscriptions;

    private static final int PREFETCH_SIZE_5 = 5;

    private String connectionUri;

    @Test
    public void testDirableSubPrefetchRecovered() throws Exception {
        AMQ4062Test.PrefetchConsumer consumer = new AMQ4062Test.PrefetchConsumer(true, connectionUri);
        consumer.recieve();
        durableSubscriptions = getDurableSubscriptions();
        ConsumerInfo info = getConsumerInfo(durableSubscriptions);
        // check if the prefetchSize equals to the size we set in the PolicyEntry
        Assert.assertEquals(AMQ4062Test.PREFETCH_SIZE_5, info.getPrefetchSize());
        consumer.a.countDown();
        AMQ4062Test.Producer p = new AMQ4062Test.Producer(connectionUri);
        p.send();
        p = null;
        service.stop();
        service.waitUntilStopped();
        durableSubscriptions = null;
        consumer = null;
        stopBroker();
        restartBroker();
        getDurableSubscriptions();
        info = null;
        info = getConsumerInfo(durableSubscriptions);
        // check if the prefetchSize equals to 0 after persistent storage recovered
        // assertEquals(0, info.getPrefetchSize());
        consumer = new AMQ4062Test.PrefetchConsumer(false, connectionUri);
        consumer.recieve();
        consumer.a.countDown();
        info = null;
        info = getConsumerInfo(durableSubscriptions);
        // check if the prefetchSize is the default size for durable consumer and the PolicyEntry
        // we set earlier take no effect
        // assertEquals(100, info.getPrefetchSize());
        // info.getPrefetchSize() is 100,it should be 5,because I set the PolicyEntry as follows,
        // policy.setDurableTopicPrefetch(PREFETCH_SIZE_5);
        Assert.assertEquals(5, info.getPrefetchSize());
    }

    public class PrefetchConsumer implements MessageListener {
        public static final String SUBSCRIPTION_NAME = "A_NAME_ABC_DEF";

        private final String user = ActiveMQConnection.DEFAULT_USER;

        private final String password = ActiveMQConnection.DEFAULT_PASSWORD;

        private final String uri;

        private boolean transacted;

        ActiveMQConnection connection;

        Session session;

        MessageConsumer consumer;

        private boolean needAck = false;

        CountDownLatch a = new CountDownLatch(1);

        public PrefetchConsumer(boolean needAck, String uri) {
            this.needAck = needAck;
            this.uri = uri;
        }

        public void recieve() throws Exception {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, uri);
            connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
            connection.setClientID("3");
            connection.start();
            session = connection.createSession(transacted, CLIENT_ACKNOWLEDGE);
            Destination destination = session.createTopic("topic2");
            consumer = session.createDurableSubscriber(((Topic) (destination)), AMQ4062Test.PrefetchConsumer.SUBSCRIPTION_NAME);
            consumer.setMessageListener(this);
        }

        @Override
        public void onMessage(Message message) {
            try {
                a.await();
            } catch (InterruptedException e1) {
            }
            if (needAck) {
                try {
                    message.acknowledge();
                    consumer.close();
                    session.close();
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    }

    public class Producer {
        protected final String user = ActiveMQConnection.DEFAULT_USER;

        private final String password = ActiveMQConnection.DEFAULT_PASSWORD;

        private final String uri;

        private boolean transacted;

        public Producer(String uri) {
            this.uri = uri;
        }

        public void send() throws Exception {
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, uri);
            ActiveMQConnection connection = ((ActiveMQConnection) (connectionFactory.createConnection()));
            connection.start();
            ActiveMQSession session = ((ActiveMQSession) (connection.createSession(transacted, AUTO_ACKNOWLEDGE)));
            Destination destination = session.createTopic("topic2");
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(PERSISTENT);
            for (int i = 0; i < 100; i++) {
                TextMessage om = session.createTextMessage("hello from producer");
                producer.send(om);
            }
            producer.close();
            session.close();
            connection.close();
        }
    }
}

