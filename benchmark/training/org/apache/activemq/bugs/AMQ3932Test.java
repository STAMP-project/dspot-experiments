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
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3932Test {
    static final Logger LOG = LoggerFactory.getLogger(AMQ3932Test.class);

    private Connection connection;

    private BrokerService broker;

    @Test
    public void testPlainReceiveBlocks() throws Exception {
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final MessageConsumer consumer = session.createConsumer(session.createQueue(getClass().getName()));
        broker.stop();
        broker.waitUntilStopped();
        broker = null;
        final CountDownLatch done = new CountDownLatch(1);
        final CountDownLatch started = new CountDownLatch(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            public void run() {
                try {
                    started.countDown();
                    AMQ3932Test.LOG.info("Entering into a Sync receive call");
                    consumer.receive();
                } catch (JMSException e) {
                }
                done.countDown();
            }
        });
        Assert.assertTrue(started.await(10, TimeUnit.SECONDS));
        Assert.assertFalse(done.await(20, TimeUnit.SECONDS));
    }

    @Test
    public void testHungReceiveNoWait() throws Exception {
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final MessageConsumer consumer = session.createConsumer(session.createQueue(getClass().getName()));
        broker.stop();
        broker.waitUntilStopped();
        broker = null;
        final CountDownLatch done = new CountDownLatch(1);
        final CountDownLatch started = new CountDownLatch(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            public void run() {
                try {
                    started.countDown();
                    AMQ3932Test.LOG.info("Entering into a Sync receiveNoWait call");
                    consumer.receiveNoWait();
                } catch (JMSException e) {
                }
                done.countDown();
            }
        });
        Assert.assertTrue(started.await(10, TimeUnit.SECONDS));
        Assert.assertTrue(done.await(20, TimeUnit.SECONDS));
    }

    @Test
    public void testHungReceiveTimed() throws Exception {
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final MessageConsumer consumer = session.createConsumer(session.createQueue(getClass().getName()));
        broker.stop();
        broker.waitUntilStopped();
        broker = null;
        final CountDownLatch done = new CountDownLatch(1);
        final CountDownLatch started = new CountDownLatch(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            public void run() {
                try {
                    started.countDown();
                    AMQ3932Test.LOG.info("Entering into a timed Sync receive call");
                    consumer.receive(10);
                } catch (JMSException e) {
                }
                done.countDown();
            }
        });
        Assert.assertTrue(started.await(10, TimeUnit.SECONDS));
        Assert.assertTrue(done.await(20, TimeUnit.SECONDS));
    }
}

