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
package org.apache.activemq.network;


import DeliveryMode.NON_PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.jms.Connection;
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
import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test case is used to load test store and forwarding between brokers.  It sets up
 * n brokers to which have a chain of queues which this test consumes and produces to.
 *
 * If the network bridges gets stuck at any point subsequent queues will not get messages.  This test
 * samples the production and consumption stats every second and if the flow of messages
 * get stuck then this tast fails.  The test monitors the flow of messages for 1 min.
 *
 * @author chirino
 */
public class NetworkLoadTest extends TestCase {
    private static final transient Logger LOG = LoggerFactory.getLogger(NetworkLoadTest.class);

    // How many times do we sample?
    private static final long SAMPLES = Integer.parseInt(System.getProperty("SAMPLES", ("" + ((60 * 1) / 5))));

    // Slower machines might need to make this bigger.
    private static final long SAMPLE_DURATION = Integer.parseInt(System.getProperty("SAMPLES_DURATION", ("" + (1000 * 5))));

    protected static final int BROKER_COUNT = 4;

    protected static final int MESSAGE_SIZE = 2000;

    String groupId;

    class ForwardingClient {
        private final AtomicLong forwardCounter = new AtomicLong();

        private final Connection toConnection;

        private final Connection fromConnection;

        public ForwardingClient(int from, int to) throws JMSException {
            toConnection = createConnection(from);
            Session toSession = toConnection.createSession(false, AUTO_ACKNOWLEDGE);
            final MessageProducer producer = toSession.createProducer(new ActiveMQQueue(("Q" + to)));
            producer.setDeliveryMode(NON_PERSISTENT);
            producer.setDisableMessageID(true);
            fromConnection = createConnection(from);
            Session fromSession = fromConnection.createSession(false, AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = fromSession.createConsumer(new ActiveMQQueue(("Q" + from)));
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message msg) {
                    try {
                        producer.send(msg);
                        forwardCounter.incrementAndGet();
                    } catch (JMSException e) {
                        // this is caused by the connection getting closed.
                    }
                }
            });
        }

        public void start() throws JMSException {
            toConnection.start();
            fromConnection.start();
        }

        public void stop() throws JMSException {
            toConnection.stop();
            fromConnection.stop();
        }

        public void close() throws JMSException {
            toConnection.close();
            fromConnection.close();
        }
    }

    private BrokerService[] brokers;

    private NetworkLoadTest.ForwardingClient[] forwardingClients;

    public void testRequestReply() throws Exception {
        final int to = 0;// Send to the first broker

        int from = (brokers.length) - 1;// consume from the last broker..

        NetworkLoadTest.LOG.info("Staring Final Consumer");
        Connection fromConnection = createConnection(from);
        fromConnection.start();
        Session fromSession = fromConnection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = fromSession.createConsumer(new ActiveMQQueue(("Q" + from)));
        final AtomicReference<ActiveMQTextMessage> lastMessageReceived = new AtomicReference<ActiveMQTextMessage>();
        final AtomicLong producedMessages = new AtomicLong();
        final AtomicLong receivedMessages = new AtomicLong();
        final AtomicBoolean done = new AtomicBoolean();
        // Setup the consumer..
        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message msg) {
                ActiveMQTextMessage m = ((ActiveMQTextMessage) (msg));
                ActiveMQTextMessage last = lastMessageReceived.get();
                if (last != null) {
                    // Some order checking...
                    if ((last.getMessageId().getProducerSequenceId()) > (m.getMessageId().getProducerSequenceId())) {
                        System.out.println(((("Received an out of order message. Got " + (m.getMessageId())) + ", expected something after ") + (last.getMessageId())));
                    }
                }
                lastMessageReceived.set(m);
                receivedMessages.incrementAndGet();
            }
        });
        NetworkLoadTest.LOG.info("Staring Initial Producer");
        final Connection toConnection = createConnection(to);
        Thread producer = new Thread("Producer") {
            @Override
            public void run() {
                try {
                    toConnection.start();
                    Session toSession = toConnection.createSession(false, AUTO_ACKNOWLEDGE);
                    final MessageProducer producer = toSession.createProducer(new ActiveMQQueue(("Q" + to)));
                    producer.setDeliveryMode(NON_PERSISTENT);
                    producer.setDisableMessageID(true);
                    for (int i = 0; !(done.get()); i++) {
                        TextMessage msg = toSession.createTextMessage(createMessageText(i));
                        producer.send(msg);
                        producedMessages.incrementAndGet();
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }

            private String createMessageText(int index) {
                StringBuffer buffer = new StringBuffer(NetworkLoadTest.MESSAGE_SIZE);
                buffer.append((((index + " on ") + (new Date())) + " ..."));
                if ((buffer.length()) > (NetworkLoadTest.MESSAGE_SIZE)) {
                    return buffer.substring(0, NetworkLoadTest.MESSAGE_SIZE);
                }
                for (int i = buffer.length(); i < (NetworkLoadTest.MESSAGE_SIZE); i++) {
                    buffer.append(' ');
                }
                return buffer.toString();
            }
        };
        producer.start();
        // Give the forwarding clients a chance to get going and fill the down
        // stream broker queues..
        Thread.sleep(((NetworkLoadTest.BROKER_COUNT) * 200));
        for (int i = 0; i < (NetworkLoadTest.SAMPLES); i++) {
            long start = System.currentTimeMillis();
            producedMessages.set(0);
            receivedMessages.set(0);
            for (int j = 0; j < (forwardingClients.length); j++) {
                forwardingClients[j].forwardCounter.set(0);
            }
            Thread.sleep(NetworkLoadTest.SAMPLE_DURATION);
            long end = System.currentTimeMillis();
            long r = receivedMessages.get();
            long p = producedMessages.get();
            NetworkLoadTest.LOG.info(((((((((("published: " + p) + " msgs at ") + ((p * 1000.0F) / (end - start))) + " msgs/sec, ") + "consumed: ") + r) + " msgs at ") + ((r * 1000.0F) / (end - start))) + " msgs/sec"));
            StringBuffer fwdingmsg = new StringBuffer(500);
            fwdingmsg.append("  forwarding counters: ");
            for (int j = 0; j < (forwardingClients.length); j++) {
                if (j != 0) {
                    fwdingmsg.append(", ");
                }
                fwdingmsg.append(forwardingClients[j].forwardCounter.get());
            }
            NetworkLoadTest.LOG.info(fwdingmsg.toString());
            // The test is just checking to make sure thaat the producer and consumer does not hang
            // due to the network hops take to route the message form the producer to the consumer.
            TestCase.assertTrue("Recieved some messages since last sample", (r > 0));
            TestCase.assertTrue("Produced some messages since last sample", (p > 0));
        }
        NetworkLoadTest.LOG.info("Sample done.");
        done.set(true);
        // Wait for the producer to finish.
        producer.join((1000 * 5));
        toConnection.close();
        fromConnection.close();
    }
}

