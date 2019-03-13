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
package org.apache.activemq.jms.pool;


import Session.AUTO_ACKNOWLEDGE;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class PooledSessionExhaustionTest extends JmsPoolTestSupport {
    private static final String QUEUE = "FOO";

    private static final int NUM_MESSAGES = 500;

    private static final Logger LOG = Logger.getLogger(PooledSessionExhaustionTest.class);

    private ActiveMQConnectionFactory factory;

    private PooledConnectionFactory pooledFactory;

    private String connectionUri;

    private int numReceived = 0;

    private final List<Exception> exceptionList = new ArrayList<Exception>();

    class TestRunner implements Runnable {
        CyclicBarrier barrier;

        TestRunner(CyclicBarrier barrier) {
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                barrier.await();
                sendMessages(pooledFactory);
            } catch (Exception e) {
                exceptionList.add(e);
                throw new RuntimeException(e);
            }
        }
    }

    @Test(timeout = 60000)
    public void testCanExhaustSessions() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectionUri);
                    Connection connection = connectionFactory.createConnection();
                    connection.start();
                    Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                    Destination destination = session.createQueue(PooledSessionExhaustionTest.QUEUE);
                    MessageConsumer consumer = session.createConsumer(destination);
                    for (int i = 0; i < (PooledSessionExhaustionTest.NUM_MESSAGES); ++i) {
                        Message msg = consumer.receive(5000);
                        if (msg == null) {
                            return;
                        }
                        (numReceived)++;
                        if (((numReceived) % 20) == 0) {
                            PooledSessionExhaustionTest.LOG.debug((("received " + (numReceived)) + " messages "));
                            System.runFinalization();
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
        ExecutorService threads = Executors.newFixedThreadPool(2);
        final CyclicBarrier barrier = new CyclicBarrier(2, new Runnable() {
            @Override
            public void run() {
                PooledSessionExhaustionTest.LOG.trace("Starting threads to send messages!");
            }
        });
        threads.execute(new PooledSessionExhaustionTest.TestRunner(barrier));
        threads.execute(new PooledSessionExhaustionTest.TestRunner(barrier));
        thread.join();
        // we should expect that one of the threads will die because it cannot acquire a session,
        // will throw an exception
        Assert.assertEquals(PooledSessionExhaustionTest.NUM_MESSAGES, numReceived);
        Assert.assertEquals(exceptionList.size(), 1);
    }
}

