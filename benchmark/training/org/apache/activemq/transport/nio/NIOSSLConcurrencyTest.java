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
package org.apache.activemq.transport.nio;


import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings({ "javadoc" })
public class NIOSSLConcurrencyTest extends TestCase {
    BrokerService broker;

    Connection connection;

    public static final String KEYSTORE_TYPE = "jks";

    public static final String PASSWORD = "password";

    public static final String SERVER_KEYSTORE = "src/test/resources/server.keystore";

    public static final String TRUST_KEYSTORE = "src/test/resources/client.keystore";

    public static final int PRODUCER_COUNT = 10;

    public static final int CONSUMER_COUNT = 10;

    public static final int MESSAGE_COUNT = 10000;

    public static final int MESSAGE_SIZE = 4096;

    final NIOSSLConcurrencyTest.ConsumerThread[] consumers = new NIOSSLConcurrencyTest.ConsumerThread[NIOSSLConcurrencyTest.CONSUMER_COUNT];

    final Session[] producerSessions = new Session[NIOSSLConcurrencyTest.PRODUCER_COUNT];

    final Session[] consumerSessions = new Session[NIOSSLConcurrencyTest.CONSUMER_COUNT];

    byte[] messageData;

    volatile boolean failed;

    public void testLoad() throws Exception {
        for (int i = 0; i < (NIOSSLConcurrencyTest.PRODUCER_COUNT); i++) {
            Queue dest = producerSessions[i].createQueue(("TEST" + i));
            NIOSSLConcurrencyTest.ProducerThread producer = new NIOSSLConcurrencyTest.ProducerThread(producerSessions[i], dest);
            producer.setMessageCount(NIOSSLConcurrencyTest.MESSAGE_COUNT);
            producer.start();
        }
        for (int i = 0; i < (NIOSSLConcurrencyTest.CONSUMER_COUNT); i++) {
            Queue dest = consumerSessions[i].createQueue(("TEST" + i));
            NIOSSLConcurrencyTest.ConsumerThread consumer = new NIOSSLConcurrencyTest.ConsumerThread(consumerSessions[i], dest);
            consumer.setMessageCount(NIOSSLConcurrencyTest.MESSAGE_COUNT);
            consumer.start();
            consumers[i] = consumer;
        }
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (failed) || ((getReceived()) == ((NIOSSLConcurrencyTest.PRODUCER_COUNT) * (NIOSSLConcurrencyTest.MESSAGE_COUNT)));
            }
        }, 120000);
        TestCase.assertEquals(((NIOSSLConcurrencyTest.PRODUCER_COUNT) * (NIOSSLConcurrencyTest.MESSAGE_COUNT)), getReceived());
    }

    private class ConsumerThread extends Thread {
        private final Logger LOG = LoggerFactory.getLogger(NIOSSLConcurrencyTest.ConsumerThread.class);

        int messageCount = 1000;

        int received = 0;

        Destination dest;

        Session sess;

        boolean breakOnNull = true;

        public ConsumerThread(Session sess, Destination dest) {
            this.dest = dest;
            this.sess = sess;
        }

        @Override
        public void run() {
            MessageConsumer consumer = null;
            try {
                consumer = sess.createConsumer(dest);
                while ((received) < (messageCount)) {
                    Message msg = consumer.receive(3000);
                    if (msg != null) {
                        LOG.info(("Received test message: " + ((received)++)));
                    } else {
                        if (breakOnNull) {
                            break;
                        }
                    }
                } 
            } catch (JMSException e) {
                e.printStackTrace();
                failed = true;
            } finally {
                if (consumer != null) {
                    try {
                        consumer.close();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public int getReceived() {
            return received;
        }

        public void setMessageCount(int messageCount) {
            this.messageCount = messageCount;
        }
    }

    private class ProducerThread extends Thread {
        private final Logger LOG = LoggerFactory.getLogger(NIOSSLConcurrencyTest.ProducerThread.class);

        int messageCount = 1000;

        Destination dest;

        protected Session sess;

        int sleep = 0;

        int sentCount = 0;

        public ProducerThread(Session sess, Destination dest) {
            this.dest = dest;
            this.sess = sess;
        }

        @Override
        public void run() {
            MessageProducer producer = null;
            try {
                producer = sess.createProducer(dest);
                for (sentCount = 0; (sentCount) < (messageCount); (sentCount)++) {
                    producer.send(createMessage(sentCount));
                    LOG.info((("Sent 'test message: " + (sentCount)) + "'"));
                    if ((sleep) > 0) {
                        Thread.sleep(sleep);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                failed = true;
            } finally {
                if (producer != null) {
                    try {
                        producer.close();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        protected Message createMessage(int i) throws Exception {
            BytesMessage b = sess.createBytesMessage();
            b.writeBytes(messageData);
            return b;
        }

        public void setMessageCount(int messageCount) {
            this.messageCount = messageCount;
        }
    }
}

