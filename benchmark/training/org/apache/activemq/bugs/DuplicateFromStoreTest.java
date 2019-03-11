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


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DuplicateFromStoreTest {
    static Logger LOG = LoggerFactory.getLogger(DuplicateFromStoreTest.class);

    String activemqURL;

    BrokerService broker;

    protected static final String DESTNAME = "TEST";

    protected static final int NUM_PRODUCERS = 100;

    protected static final int NUM_CONSUMERS = 20;

    protected static final int NUM_MSGS = 20000;

    protected static final int CONSUMER_SLEEP = 0;

    protected static final int PRODUCER_SLEEP = 10;

    public static CountDownLatch producersFinished = new CountDownLatch(DuplicateFromStoreTest.NUM_PRODUCERS);

    public static CountDownLatch consumersFinished = new CountDownLatch(DuplicateFromStoreTest.NUM_CONSUMERS);

    public AtomicInteger totalMessagesToSend = new AtomicInteger(DuplicateFromStoreTest.NUM_MSGS);

    public AtomicInteger totalMessagesSent = new AtomicInteger(DuplicateFromStoreTest.NUM_MSGS);

    public AtomicInteger totalReceived = new AtomicInteger(0);

    public int messageSize = 16 * 1000;

    @Test
    public void testDuplicateMessage() throws Exception {
        DuplicateFromStoreTest.LOG.info("Testing for duplicate messages.");
        // create producer and consumer threads
        ExecutorService producers = Executors.newFixedThreadPool(DuplicateFromStoreTest.NUM_PRODUCERS);
        ExecutorService consumers = Executors.newFixedThreadPool(DuplicateFromStoreTest.NUM_CONSUMERS);
        createOpenwireClients(producers, consumers);
        DuplicateFromStoreTest.LOG.info("All producers and consumers got started. Awaiting their termination");
        DuplicateFromStoreTest.producersFinished.await(100, TimeUnit.MINUTES);
        DuplicateFromStoreTest.LOG.info(((("All producers have terminated. remaining to send: " + (totalMessagesToSend.get())) + ", sent:") + (totalMessagesSent.get())));
        DuplicateFromStoreTest.consumersFinished.await(100, TimeUnit.MINUTES);
        DuplicateFromStoreTest.LOG.info("All consumers have terminated.");
        producers.shutdownNow();
        consumers.shutdownNow();
        Assert.assertEquals("no messages pending, i.e. dlq empty", 0L, getDestinationStatistics().getMessages().getCount());
        // validate cache can be enabled if disabled
    }

    class Producer implements Runnable {
        Logger log = DuplicateFromStoreTest.LOG;

        protected String destName = "TEST";

        protected boolean isTopicDest = false;

        public Producer(String dest, boolean isTopic, int ttl) {
            this.destName = dest;
            this.isTopicDest = isTopic;
        }

        /**
         * Connect to broker and constantly send messages
         */
        public void run() {
            Connection connection = null;
            Session session = null;
            MessageProducer producer = null;
            try {
                ActiveMQConnectionFactory amq = new ActiveMQConnectionFactory(activemqURL);
                connection = amq.createConnection();
                connection.setExceptionListener(new ExceptionListener() {
                    public void onException(JMSException e) {
                        e.printStackTrace();
                    }
                });
                connection.start();
                // Create a Session
                session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                Destination destination;
                if (isTopicDest) {
                    // Create the destination (Topic or Queue)
                    destination = session.createTopic(destName);
                } else {
                    destination = session.createQueue(destName);
                }
                // Create a MessageProducer from the Session to the Topic or Queue
                producer = session.createProducer(destination);
                // Create message
                long counter = 0;
                // enlarge msg to 16 kb
                int msgSize = 16 * 1024;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.setLength((msgSize + 15));
                stringBuilder.append("Message: ");
                stringBuilder.append(counter);
                for (int j = 0; j < (msgSize / 10); j++) {
                    stringBuilder.append("XXXXXXXXXX");
                }
                String text = stringBuilder.toString();
                TextMessage message = session.createTextMessage(text);
                // send message
                while ((totalMessagesToSend.decrementAndGet()) >= 0) {
                    producer.send(message);
                    totalMessagesSent.incrementAndGet();
                    log.debug(("Sent message: " + counter));
                    counter++;
                    if ((counter % 10000) == 0)
                        log.info((("sent " + counter) + " messages"));

                    Thread.sleep(DuplicateFromStoreTest.PRODUCER_SLEEP);
                } 
            } catch (Exception ex) {
                log.error(ex.toString());
                return;
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception ignored) {
                } finally {
                    DuplicateFromStoreTest.producersFinished.countDown();
                }
            }
            log.debug(("Closing producer for " + (destName)));
        }
    }

    class Consumer implements Runnable {
        public Object init = new Object();

        protected String queueName = "TEST";

        boolean isTopic = false;

        Logger log = DuplicateFromStoreTest.LOG;

        public Consumer(String destName, boolean topic) {
            this.isTopic = topic;
            this.queueName = destName;
        }

        /**
         * connect to broker and receive messages
         */
        public void run() {
            Connection connection = null;
            Session session = null;
            MessageConsumer consumer = null;
            try {
                ActiveMQConnectionFactory amq = new ActiveMQConnectionFactory(activemqURL);
                connection = amq.createConnection();
                connection.setExceptionListener(new ExceptionListener() {
                    public void onException(JMSException e) {
                        e.printStackTrace();
                    }
                });
                connection.start();
                // Create a Session
                session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                // Create the destination (Topic or Queue)
                Destination destination = null;
                if (isTopic)
                    destination = session.createTopic(queueName);
                else
                    destination = session.createQueue(queueName);

                // Create a MessageConsumer from the Session to the Topic or Queue
                consumer = session.createConsumer(destination);
                synchronized(init) {
                    init.notifyAll();
                }
                // Wait for a message
                long counter = 0;
                while ((totalReceived.get()) < (DuplicateFromStoreTest.NUM_MSGS)) {
                    Message message2 = consumer.receive(5000);
                    if (message2 instanceof TextMessage) {
                        TextMessage textMessage = ((TextMessage) (message2));
                        String text = textMessage.getText();
                        log.debug(("Received: " + (text.substring(0, 50))));
                    } else
                        if ((totalReceived.get()) < (DuplicateFromStoreTest.NUM_MSGS)) {
                            log.error(("Received message of unsupported type. Expecting TextMessage. count: " + (totalReceived.get())));
                        } else {
                            // all done
                            break;
                        }

                    if (message2 != null) {
                        counter++;
                        totalReceived.incrementAndGet();
                        if ((counter % 10000) == 0)
                            log.info((("received " + counter) + " messages"));

                        Thread.sleep(DuplicateFromStoreTest.CONSUMER_SLEEP);
                    }
                } 
            } catch (Exception e) {
                log.error(("Error in Consumer: " + (e.getMessage())));
                return;
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (Exception ignored) {
                } finally {
                    DuplicateFromStoreTest.consumersFinished.countDown();
                }
            }
        }
    }
}

