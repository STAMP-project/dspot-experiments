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


import Session.CLIENT_ACKNOWLEDGE;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is a test case for the issue reported at: https://issues.apache.org/activemq/browse/AMQ-2021 Bug is modification
 * of inflight message properties so the failure can manifest itself in a bunch or ways, from message receipt with null
 * properties to marshall errors
 */
public class AMQ2021Test implements Thread.UncaughtExceptionHandler , ExceptionListener {
    private static final Logger log = LoggerFactory.getLogger(AMQ2021Test.class);

    BrokerService brokerService;

    ArrayList<Thread> threads = new ArrayList<Thread>();

    Vector<Throwable> exceptions;

    @Rule
    public TestName name = new TestName();

    AMQ2021Test testCase;

    private final String ACTIVEMQ_BROKER_BIND = "tcp://localhost:0";

    private String CONSUMER_BROKER_URL = "?jms.redeliveryPolicy.maximumRedeliveries=1&jms.redeliveryPolicy.initialRedeliveryDelay=0";

    private String PRODUCER_BROKER_URL;

    private final int numMessages = 1000;

    private final int numConsumers = 2;

    private final int dlqMessages = (numMessages) / 2;

    private CountDownLatch receivedLatch;

    private ActiveMQTopic destination;

    private CountDownLatch started;

    @Test(timeout = 240000)
    public void testConcurrentTopicResendToDLQ() throws Exception {
        for (int i = 0; i < (numConsumers); i++) {
            AMQ2021Test.ConsumerThread c1 = new AMQ2021Test.ConsumerThread(("Consumer-" + i));
            threads.add(c1);
            c1.start();
        }
        Assert.assertTrue(started.await(10, TimeUnit.SECONDS));
        Thread producer = new Thread() {
            @Override
            public void run() {
                try {
                    produce(numMessages);
                } catch (Exception e) {
                }
            }
        };
        threads.add(producer);
        producer.start();
        boolean allGood = receivedLatch.await(90, TimeUnit.SECONDS);
        for (Throwable t : exceptions) {
            AMQ2021Test.log.error("failing test with first exception", t);
            Assert.fail(("exception during test : " + t));
        }
        Assert.assertTrue("excepted messages received within time limit", allGood);
        Assert.assertEquals(0, exceptions.size());
        for (int i = 0; i < (numConsumers); i++) {
            // last recovery sends message to deq so is not received again
            Assert.assertEquals(((dlqMessages) * 2), ((AMQ2021Test.ConsumerThread) (threads.get(i))).recoveries);
            Assert.assertEquals(((numMessages) + (dlqMessages)), ((AMQ2021Test.ConsumerThread) (threads.get(i))).counter);
        }
        // half of the messages for each consumer should go to the dlq but duplicates will
        // be suppressed
        consumeFromDLQ(dlqMessages);
    }

    public class ConsumerThread extends Thread implements MessageListener {
        public long counter = 0;

        public long recoveries = 0;

        private Session session;

        public ConsumerThread(String threadId) {
            super(threadId);
        }

        @Override
        public void run() {
            try {
                ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(CONSUMER_BROKER_URL);
                Connection connection = connectionFactory.createConnection();
                connection.setExceptionListener(testCase);
                connection.setClientID(getName());
                session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
                MessageConsumer consumer = session.createDurableSubscriber(destination, getName());
                consumer.setMessageListener(this);
                connection.start();
                started.countDown();
            } catch (JMSException exception) {
                AMQ2021Test.log.error("unexpected ex in consumer run", exception);
                exceptions.add(exception);
            }
        }

        @Override
        public void onMessage(Message message) {
            try {
                (counter)++;
                int messageNumber = message.getIntProperty("MsgNumber");
                if ((messageNumber % 2) == 0) {
                    session.recover();
                    (recoveries)++;
                } else {
                    message.acknowledge();
                }
                if (((counter) % 200) == 0) {
                    AMQ2021Test.log.info(((((("recoveries:" + (recoveries)) + ", Received ") + (counter)) + ", counter'th ") + message));
                }
                receivedLatch.countDown();
            } catch (Exception e) {
                AMQ2021Test.log.error("unexpected ex on onMessage", e);
                exceptions.add(e);
            }
        }
    }
}

