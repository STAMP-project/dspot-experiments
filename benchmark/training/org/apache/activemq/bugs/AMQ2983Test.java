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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.KahaDBStore;
import org.junit.Assert;
import org.junit.Test;


public class AMQ2983Test {
    private static final int MAX_CONSUMER = 10;

    private static final int MAX_MESSAGES = 2000;

    private static final String QUEUE_NAME = "test.queue";

    private BrokerService broker;

    private final CountDownLatch messageCountDown = new CountDownLatch(AMQ2983Test.MAX_MESSAGES);

    private AMQ2983Test.CleanableKahaDBStore kahaDB;

    private static class CleanableKahaDBStore extends KahaDBStore {
        // make checkpoint cleanup accessible
        public void forceCleanup() throws IOException {
            checkpointCleanup(true);
        }

        public int getFileMapSize() throws IOException {
            // ensure save memory publishing, use the right lock
            indexLock.readLock().lock();
            try {
                return getJournal().getFileMap().size();
            } finally {
                indexLock.readLock().unlock();
            }
        }
    }

    private class ConsumerThread extends Thread {
        @Override
        public void run() {
            try {
                ConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
                Connection connection = factory.createConnection();
                connection.start();
                Session session = connection.createSession(true, SESSION_TRANSACTED);
                MessageConsumer consumer = session.createConsumer(session.createQueue(AMQ2983Test.QUEUE_NAME));
                do {
                    Message message = consumer.receive(200);
                    if (message != null) {
                        session.commit();
                        messageCountDown.countDown();
                    }
                } while ((messageCountDown.getCount()) != 0 );
                consumer.close();
                session.close();
                connection.close();
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    @Test
    public void testNoStickyKahaDbLogFilesOnConcurrentTransactionalConsumer() throws Exception {
        List<Thread> consumerThreads = new ArrayList<Thread>();
        for (int i = 0; i < (AMQ2983Test.MAX_CONSUMER); i++) {
            AMQ2983Test.ConsumerThread thread = new AMQ2983Test.ConsumerThread();
            thread.start();
            consumerThreads.add(thread);
        }
        sendMessages();
        boolean allMessagesReceived = messageCountDown.await(60, TimeUnit.SECONDS);
        Assert.assertTrue(allMessagesReceived);
        for (Thread thread : consumerThreads) {
            thread.join(TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS));
            Assert.assertFalse(thread.isAlive());
        }
        kahaDB.forceCleanup();
        Assert.assertEquals("Expect only one active KahaDB log file after cleanup", 1, kahaDB.getFileMapSize());
    }
}

