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


import Session.SESSION_TRANSACTED;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// https://issues.apache.org/jira/browse/AMQ-4262
public class TransactedStoreUsageSuspendResumeTest {
    private static final Logger LOG = LoggerFactory.getLogger(TransactedStoreUsageSuspendResumeTest.class);

    private static final int MAX_MESSAGES = 10000;

    private static final String QUEUE_NAME = "test.queue";

    private BrokerService broker;

    private final CountDownLatch messagesReceivedCountDown = new CountDownLatch(TransactedStoreUsageSuspendResumeTest.MAX_MESSAGES);

    private final CountDownLatch messagesSentCountDown = new CountDownLatch(TransactedStoreUsageSuspendResumeTest.MAX_MESSAGES);

    private final CountDownLatch consumerStartLatch = new CountDownLatch(1);

    private class ConsumerThread extends Thread {
        @Override
        public void run() {
            try {
                consumerStartLatch.await(30, TimeUnit.SECONDS);
                ConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
                Connection connection = factory.createConnection();
                connection.start();
                Session session = connection.createSession(true, SESSION_TRANSACTED);
                // wait for producer to stop
                long currentSendCount;
                do {
                    currentSendCount = messagesSentCountDown.getCount();
                    TimeUnit.SECONDS.sleep(5);
                } while (currentSendCount != (messagesSentCountDown.getCount()) );
                TransactedStoreUsageSuspendResumeTest.LOG.info(("Starting consumer at: " + currentSendCount));
                MessageConsumer consumer = session.createConsumer(session.createQueue(TransactedStoreUsageSuspendResumeTest.QUEUE_NAME));
                do {
                    Message message = consumer.receive(5000);
                    if (message != null) {
                        session.commit();
                        messagesReceivedCountDown.countDown();
                    }
                    if (((messagesReceivedCountDown.getCount()) % 500) == 0) {
                        TransactedStoreUsageSuspendResumeTest.LOG.info(("remaining to receive: " + (messagesReceivedCountDown.getCount())));
                    }
                } while ((messagesReceivedCountDown.getCount()) != 0 );
                session.commit();
                consumer.close();
                session.close();
                connection.close();
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    @Test
    public void testTransactedStoreUsageSuspendResume() throws Exception {
        TransactedStoreUsageSuspendResumeTest.ConsumerThread thread = new TransactedStoreUsageSuspendResumeTest.ConsumerThread();
        thread.start();
        ExecutorService sendExecutor = Executors.newSingleThreadExecutor();
        sendExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    sendMessages();
                } catch (Exception ignored) {
                }
            }
        });
        sendExecutor.shutdown();
        sendExecutor.awaitTermination(10, TimeUnit.MINUTES);
        boolean allMessagesReceived = messagesReceivedCountDown.await(10, TimeUnit.MINUTES);
        if (!allMessagesReceived) {
            TransactedStoreUsageSuspendResumeTest.LOG.info("Giving up - not all received on time...");
            TransactedStoreUsageSuspendResumeTest.LOG.info(("System Mem Usage: " + (broker.getSystemUsage().getMemoryUsage())));
            TransactedStoreUsageSuspendResumeTest.LOG.info(("System Store Usage: " + (broker.getSystemUsage().getStoreUsage())));
            TransactedStoreUsageSuspendResumeTest.LOG.info(("Producer sent: " + (messagesSentCountDown.getCount())));
            TransactedStoreUsageSuspendResumeTest.LOG.info(("Consumer remaining to receive: " + (messagesReceivedCountDown.getCount())));
            dumpAllThreads("StuckConsumer!");
        }
        Assert.assertTrue(("Got all messages: " + (messagesReceivedCountDown)), allMessagesReceived);
        // give consumers a chance to exit gracefully
        TimeUnit.SECONDS.sleep(5);
    }
}

