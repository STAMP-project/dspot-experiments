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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ1893Test extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(AMQ1893Test.class);

    static final String QUEUE_NAME = "TEST";

    static final int MESSAGE_COUNT_OF_ONE_GROUP = 10000;

    static final int[] PRIORITIES = new int[]{ 0, 5, 10 };

    static final boolean debug = false;

    private BrokerService brokerService;

    private ActiveMQQueue destination;

    public void testProduceConsumeWithSelector() throws Exception {
        new AMQ1893Test.TestProducer().produceMessages();
        new AMQ1893Test.TestConsumer().consume();
    }

    class TestProducer {
        public void produceMessages() throws Exception {
            ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(brokerService.getTransportConnectors().get(0).getConnectUri().toString());
            Connection connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(AMQ1893Test.QUEUE_NAME);
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(PERSISTENT);
            long start = System.currentTimeMillis();
            for (int priority : AMQ1893Test.PRIORITIES) {
                String name = null;
                if (priority == 10) {
                    name = "high";
                } else
                    if (priority == 5) {
                        name = "mid";
                    } else {
                        name = "low";
                    }

                for (int i = 1; i <= (AMQ1893Test.MESSAGE_COUNT_OF_ONE_GROUP); i++) {
                    TextMessage message = session.createTextMessage(((name + "_") + i));
                    message.setIntProperty("priority", priority);
                    producer.send(message);
                }
            }
            long end = System.currentTimeMillis();
            AMQ1893Test.log.info((((("sent " + ((AMQ1893Test.MESSAGE_COUNT_OF_ONE_GROUP) * 3)) + " messages in ") + (end - start)) + " ms"));
            producer.close();
            session.close();
            connection.close();
        }
    }

    class TestConsumer {
        private CountDownLatch finishLatch = new CountDownLatch(1);

        public void consume() throws Exception {
            ConnectionFactory connectionFactory = new org.apache.activemq.ActiveMQConnectionFactory(brokerService.getTransportConnectors().get(0).getConnectUri().toString());
            final int totalMessageCount = (AMQ1893Test.MESSAGE_COUNT_OF_ONE_GROUP) * (AMQ1893Test.PRIORITIES.length);
            final AtomicInteger counter = new AtomicInteger();
            final MessageListener listener = new MessageListener() {
                public void onMessage(Message message) {
                    if (AMQ1893Test.debug) {
                        try {
                            AMQ1893Test.log.info(getText());
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                    if ((counter.incrementAndGet()) == totalMessageCount) {
                        finishLatch.countDown();
                    }
                }
            };
            int consumerCount = AMQ1893Test.PRIORITIES.length;
            Connection[] connections = new Connection[consumerCount];
            Session[] sessions = new Session[consumerCount];
            MessageConsumer[] consumers = new MessageConsumer[consumerCount];
            for (int i = 0; i < consumerCount; i++) {
                String selector = "priority = " + (AMQ1893Test.PRIORITIES[i]);
                connections[i] = connectionFactory.createConnection();
                sessions[i] = connections[i].createSession(false, AUTO_ACKNOWLEDGE);
                consumers[i] = sessions[i].createConsumer(destination, selector);
                consumers[i].setMessageListener(listener);
            }
            for (Connection connection : connections) {
                connection.start();
            }
            AMQ1893Test.log.info((("received " + (counter.get())) + " messages"));
            TestCase.assertTrue("got all messages in time", finishLatch.await(60, TimeUnit.SECONDS));
            AMQ1893Test.log.info((("received " + (counter.get())) + " messages"));
            for (MessageConsumer consumer : consumers) {
                consumer.close();
            }
            for (Session session : sessions) {
                session.close();
            }
            for (Connection connection : connections) {
                connection.close();
            }
        }
    }
}

