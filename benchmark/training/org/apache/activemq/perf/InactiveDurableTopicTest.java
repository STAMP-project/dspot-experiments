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
package org.apache.activemq.perf;


import Message.DEFAULT_TIME_TO_LIVE;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Session.AUTO_ACKNOWLEDGE;
import javax.jms.Session.CLIENT_ACKNOWLEDGE;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.javax.jms.Message;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class InactiveDurableTopicTest extends TestCase {
    private static final transient Logger LOG = LoggerFactory.getLogger(InactiveDurableTopicTest.class);

    private static final int MESSAGE_COUNT = 2000;

    private static final String DEFAULT_PASSWORD = "";

    private static final String USERNAME = "testuser";

    private static final String CLIENTID = "mytestclient";

    private static final String TOPIC_NAME = "testevent";

    private static final String SUBID = "subscription1";

    private static final int DELIVERY_MODE = DeliveryMode.PERSISTENT;

    private static final int DELIVERY_PRIORITY = javax.jms.Message;

    private Connection connection;

    private MessageProducer publisher;

    private TopicSubscriber subscriber;

    private Topic topic;

    private Session session;

    private ActiveMQConnectionFactory connectionFactory;

    private BrokerService broker;

    public void test1CreateSubscription() throws Exception {
        try {
            /* Step 1 - Establish a connection with a client id and create a
            durable subscription
             */
            connection = connectionFactory.createConnection(InactiveDurableTopicTest.USERNAME, InactiveDurableTopicTest.DEFAULT_PASSWORD);
            TestCase.assertNotNull(connection);
            connection.setClientID(InactiveDurableTopicTest.CLIENTID);
            connection.start();
            session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
            TestCase.assertNotNull(session);
            topic = session.createTopic(InactiveDurableTopicTest.TOPIC_NAME);
            TestCase.assertNotNull(topic);
            subscriber = session.createDurableSubscriber(topic, InactiveDurableTopicTest.SUBID, "", false);
            TestCase.assertNotNull(subscriber);
            subscriber.close();
            session.close();
            connection.close();
        } catch (JMSException ex) {
            try {
                connection.close();
            } catch (Exception ignore) {
            }
            throw new AssertionFailedError(("Create Subscription caught: " + ex));
        }
    }

    public void test2ProducerTestCase() {
        /* Step 2 - Establish a connection without a client id and create a
        producer and start pumping messages. We will get hung
         */
        try {
            connection = connectionFactory.createConnection(InactiveDurableTopicTest.USERNAME, InactiveDurableTopicTest.DEFAULT_PASSWORD);
            TestCase.assertNotNull(connection);
            session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
            TestCase.assertNotNull(session);
            topic = session.createTopic(InactiveDurableTopicTest.TOPIC_NAME);
            TestCase.assertNotNull(topic);
            publisher = session.createProducer(topic);
            TestCase.assertNotNull(publisher);
            MapMessage msg = session.createMapMessage();
            TestCase.assertNotNull(msg);
            msg.setString("key1", "value1");
            int loop;
            for (loop = 0; loop < (InactiveDurableTopicTest.MESSAGE_COUNT); loop++) {
                msg.setInt("key2", loop);
                publisher.send(msg, InactiveDurableTopicTest.DELIVERY_MODE, InactiveDurableTopicTest.DELIVERY_PRIORITY, DEFAULT_TIME_TO_LIVE);
                if ((loop % 5000) == 0) {
                    InactiveDurableTopicTest.LOG.info((("Sent " + loop) + " messages"));
                }
            }
            TestCase.assertEquals(loop, InactiveDurableTopicTest.MESSAGE_COUNT);
            publisher.close();
            session.close();
            connection.stop();
            connection.stop();
        } catch (JMSException ex) {
            try {
                connection.close();
            } catch (Exception ignore) {
            }
            throw new AssertionFailedError(("Create Subscription caught: " + ex));
        }
    }

    public void test3CreateSubscription() throws Exception {
        try {
            /* Step 1 - Establish a connection with a client id and create a
            durable subscription
             */
            connection = connectionFactory.createConnection(InactiveDurableTopicTest.USERNAME, InactiveDurableTopicTest.DEFAULT_PASSWORD);
            TestCase.assertNotNull(connection);
            connection.setClientID(InactiveDurableTopicTest.CLIENTID);
            connection.start();
            session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            TestCase.assertNotNull(session);
            topic = session.createTopic(InactiveDurableTopicTest.TOPIC_NAME);
            TestCase.assertNotNull(topic);
            subscriber = session.createDurableSubscriber(topic, InactiveDurableTopicTest.SUBID, "", false);
            TestCase.assertNotNull(subscriber);
            int loop;
            for (loop = 0; loop < (InactiveDurableTopicTest.MESSAGE_COUNT); loop++) {
                subscriber.receive();
                if ((loop % 500) == 0) {
                    InactiveDurableTopicTest.LOG.debug((("Received " + loop) + " messages"));
                }
            }
            TestCase.assertEquals(loop, InactiveDurableTopicTest.MESSAGE_COUNT);
            subscriber.close();
            session.close();
            connection.close();
        } catch (JMSException ex) {
            try {
                connection.close();
            } catch (Exception ignore) {
            }
            throw new AssertionFailedError(("Create Subscription caught: " + ex));
        }
    }
}

