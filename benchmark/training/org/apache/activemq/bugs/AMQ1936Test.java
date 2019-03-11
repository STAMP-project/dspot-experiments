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


import QueueSession.AUTO_ACKNOWLEDGE;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.naming.NamingException;
import junit.framework.TestCase;
import org.apache.activemq.AutoFailTestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.apache.log4j.Logger;


/**
 * A AMQ1936Test
 */
public class AMQ1936Test extends TestCase {
    private static final Logger logger = Logger.getLogger(AMQ1936Test.class);

    private static final String TEST_QUEUE_NAME = "dynamicQueues/duplicate.message.test.queue";

    // //--
    // 
    private static final long TEST_MESSAGE_COUNT = 6000;// The number of test messages to use


    // 
    // //--
    private static final int CONSUMER_COUNT = 2;// The number of message receiver instances


    private static final boolean TRANSACTED_RECEIVE = true;// Flag used by receiver which indicates messages should be


    // processed within a JMS transaction
    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(AMQ1936Test.CONSUMER_COUNT, AMQ1936Test.CONSUMER_COUNT, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private final AMQ1936Test.ThreadedMessageReceiver[] receivers = new AMQ1936Test.ThreadedMessageReceiver[AMQ1936Test.CONSUMER_COUNT];

    private BrokerService broker = null;

    static QueueConnectionFactory connectionFactory = null;

    public void testForDuplicateMessages() throws Exception {
        final ConcurrentMap<String, String> messages = new ConcurrentHashMap<String, String>();
        final Object lock = new Object();
        final CountDownLatch duplicateSignal = new CountDownLatch(1);
        final AtomicInteger messageCount = new AtomicInteger(0);
        // add 1/2 the number of our total messages
        for (int i = 0; i < ((AMQ1936Test.TEST_MESSAGE_COUNT) / 2); i++) {
            if ((duplicateSignal.getCount()) == 0) {
                TestCase.fail("Duplicate message id detected");
            }
            sendTextMessage(AMQ1936Test.TEST_QUEUE_NAME, i);
        }
        // create a number of consumers to read of the messages and start them with a handler which simply stores the
        // message ids
        // in a Map and checks for a duplicate
        for (int i = 0; i < (AMQ1936Test.CONSUMER_COUNT); i++) {
            receivers[i] = new AMQ1936Test.ThreadedMessageReceiver(AMQ1936Test.TEST_QUEUE_NAME, new AMQ1936Test.IMessageHandler() {
                @Override
                public void onMessage(Message message) throws Exception {
                    synchronized(lock) {
                        int current = messageCount.incrementAndGet();
                        if ((current % 1000) == 0) {
                            AMQ1936Test.logger.info(((("Received message:" + (message.getJMSMessageID())) + " with content: ") + (getText())));
                        }
                        if (messages.containsKey(message.getJMSMessageID())) {
                            duplicateSignal.countDown();
                            AMQ1936Test.logger.fatal(("duplicate message id detected:" + (message.getJMSMessageID())));
                            TestCase.fail(("Duplicate message id detected:" + (message.getJMSMessageID())));
                        } else {
                            messages.put(message.getJMSMessageID(), message.getJMSMessageID());
                        }
                    }
                }
            });
            threadPool.submit(receivers[i]);
        }
        // starting adding the remaining messages
        for (int i = 0; i < ((AMQ1936Test.TEST_MESSAGE_COUNT) / 2); i++) {
            if ((duplicateSignal.getCount()) == 0) {
                TestCase.fail("Duplicate message id detected");
            }
            sendTextMessage(AMQ1936Test.TEST_QUEUE_NAME, i);
        }
        AMQ1936Test.logger.info((("sent all " + (AMQ1936Test.TEST_MESSAGE_COUNT)) + " messages"));
        // allow some time for messages to be delivered to receivers.
        boolean ok = Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (AMQ1936Test.TEST_MESSAGE_COUNT) == (messages.size());
            }
        }, TimeUnit.MINUTES.toMillis(7));
        if (!ok) {
            AutoFailTestSupport.dumpAllThreads("--STUCK?--");
        }
        TestCase.assertEquals("Number of messages received does not match the number sent", AMQ1936Test.TEST_MESSAGE_COUNT, messages.size());
        TestCase.assertEquals(AMQ1936Test.TEST_MESSAGE_COUNT, messageCount.get());
    }

    private static final class ThreadedMessageReceiver implements Runnable {
        private AMQ1936Test.IMessageHandler handler = null;

        private final AtomicBoolean shouldStop = new AtomicBoolean(false);

        public ThreadedMessageReceiver(String queueName, AMQ1936Test.IMessageHandler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            QueueConnection queueConnection = null;
            QueueSession session = null;
            QueueReceiver receiver = null;
            Queue queue = null;
            Message message = null;
            try {
                try {
                    queueConnection = AMQ1936Test.connectionFactory.createQueueConnection();
                    // create a transacted session
                    session = queueConnection.createQueueSession(AMQ1936Test.TRANSACTED_RECEIVE, AUTO_ACKNOWLEDGE);
                    queue = session.createQueue(AMQ1936Test.TEST_QUEUE_NAME);
                    receiver = session.createReceiver(queue);
                    // start the connection
                    queueConnection.start();
                    AMQ1936Test.logger.info((("Receiver " + (Thread.currentThread().getName())) + " connected."));
                    // start receive loop
                    while (!((shouldStop.get()) || (Thread.currentThread().isInterrupted()))) {
                        try {
                            message = receiver.receive(200);
                        } catch (Exception e) {
                            // 
                            // ignore interrupted exceptions
                            // 
                            if ((e instanceof InterruptedException) || ((e.getCause()) instanceof InterruptedException)) {
                                /* ignore */
                            } else {
                                throw e;
                            }
                        }
                        if ((message != null) && ((this.handler) != null)) {
                            this.handler.onMessage(message);
                        }
                        // commit session on successful handling of message
                        if (session.getTransacted()) {
                            session.commit();
                        }
                    } 
                    AMQ1936Test.logger.info((("Receiver " + (Thread.currentThread().getName())) + " shutting down."));
                } finally {
                    if (receiver != null) {
                        try {
                            receiver.close();
                        } catch (JMSException e) {
                            AMQ1936Test.logger.warn(e);
                        }
                    }
                    if (session != null) {
                        try {
                            session.close();
                        } catch (JMSException e) {
                            AMQ1936Test.logger.warn(e);
                        }
                    }
                    if (queueConnection != null) {
                        queueConnection.close();
                    }
                }
            } catch (JMSException e) {
                AMQ1936Test.logger.error(e);
                e.printStackTrace();
            } catch (NamingException e) {
                AMQ1936Test.logger.error(e);
            } catch (Exception e) {
                AMQ1936Test.logger.error(e);
                e.printStackTrace();
            }
        }

        public void setShouldStop(Boolean shouldStop) {
            this.shouldStop.set(shouldStop);
        }
    }

    public interface IMessageHandler {
        void onMessage(Message message) throws Exception;
    }
}

