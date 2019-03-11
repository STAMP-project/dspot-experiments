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
import java.util.concurrent.CountDownLatch;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.kahadb.KahaDBStore;
import org.junit.Assert;
import org.junit.Test;


public class AMQ2982Test {
    private static final int MAX_MESSAGES = 500;

    private static final String QUEUE_NAME = "test.queue";

    private BrokerService broker;

    private final CountDownLatch messageCountDown = new CountDownLatch(AMQ2982Test.MAX_MESSAGES);

    private AMQ2982Test.CleanableKahaDBStore kahaDB;

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

    class ConsumerThread extends Thread {
        @Override
        public void run() {
            try {
                ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
                RedeliveryPolicy policy = new RedeliveryPolicy();
                policy.setMaximumRedeliveries(0);
                policy.setInitialRedeliveryDelay(100);
                policy.setUseExponentialBackOff(false);
                factory.setRedeliveryPolicy(policy);
                Connection connection = factory.createConnection();
                connection.start();
                Session session = connection.createSession(true, SESSION_TRANSACTED);
                MessageConsumer consumer = session.createConsumer(session.createQueue(AMQ2982Test.QUEUE_NAME));
                do {
                    Message message = consumer.receive(300);
                    if (message != null) {
                        session.rollback();
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
    public void testNoStickyKahaDbLogFilesOnLocalTransactionRollback() throws Exception {
        Connection dlqConnection = registerDLQMessageListener();
        AMQ2982Test.ConsumerThread thread = new AMQ2982Test.ConsumerThread();
        thread.start();
        sendMessages();
        thread.join((60 * 1000));
        Assert.assertFalse(thread.isAlive());
        dlqConnection.close();
        kahaDB.forceCleanup();
        Assert.assertEquals("only one active KahaDB log file after cleanup is expected", 1, kahaDB.getFileMapSize());
    }
}

