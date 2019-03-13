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
import Session.CLIENT_ACKNOWLEDGE;
import java.io.ByteArrayOutputStream;
import java.net.Socket;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SlowConsumerTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(SlowConsumerTest.class);

    private static final int MESSAGES_COUNT = 10000;

    private final int messageLogFrequency = 2500;

    private final long messageReceiveTimeout = 10000L;

    private Socket stompSocket;

    private ByteArrayOutputStream inputBuffer;

    private int messagesCount;

    /**
     *
     *
     * @param args
     * 		
     * @throws Exception
     * 		
     */
    public void testRemoveSubscriber() throws Exception {
        final BrokerService broker = new BrokerService();
        broker.setPersistent(true);
        broker.setUseJmx(true);
        broker.setDeleteAllMessagesOnStartup(true);
        broker.addConnector("tcp://localhost:0").setName("Default");
        broker.start();
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(broker.getTransportConnectors().get(0).getPublishableConnectString());
        final Connection connection = factory.createConnection();
        connection.start();
        Thread producingThread = new Thread("Producing thread") {
            public void run() {
                try {
                    Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                    MessageProducer producer = session.createProducer(new ActiveMQQueue(getDestinationName()));
                    for (int idx = 0; idx < (SlowConsumerTest.MESSAGES_COUNT); ++idx) {
                        Message message = session.createTextMessage(("" + idx));
                        producer.send(message);
                        SlowConsumerTest.LOG.debug(("Sending: " + idx));
                    }
                    producer.close();
                    session.close();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        };
        producingThread.setPriority(Thread.MAX_PRIORITY);
        producingThread.start();
        Thread.sleep(1000);
        Thread consumingThread = new Thread("Consuming thread") {
            public void run() {
                try {
                    Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
                    MessageConsumer consumer = session.createConsumer(new ActiveMQQueue(getDestinationName()));
                    int diff = 0;
                    while ((messagesCount) != (SlowConsumerTest.MESSAGES_COUNT)) {
                        Message msg = consumer.receive(messageReceiveTimeout);
                        if (msg == null) {
                            SlowConsumerTest.LOG.warn((("Got null message at count: " + (messagesCount)) + ". Continuing..."));
                            break;
                        }
                        String text = getText();
                        int currentMsgIdx = Integer.parseInt(text);
                        SlowConsumerTest.LOG.debug(((("Received: " + text) + " messageCount: ") + (messagesCount)));
                        msg.acknowledge();
                        if (((messagesCount) + diff) != currentMsgIdx) {
                            SlowConsumerTest.LOG.debug(((("Message(s) skipped!! Should be message no.: " + (messagesCount)) + " but got: ") + currentMsgIdx));
                            diff = currentMsgIdx - (messagesCount);
                        }
                        ++(messagesCount);
                        if (((messagesCount) % (messageLogFrequency)) == 0) {
                            SlowConsumerTest.LOG.info((("Received: " + (messagesCount)) + " messages so far"));
                        }
                        // Thread.sleep(70);
                    } 
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        };
        consumingThread.start();
        consumingThread.join();
        TestCase.assertEquals(SlowConsumerTest.MESSAGES_COUNT, messagesCount);
    }
}

