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
package org.apache.activemq.store;


import Session.SESSION_TRANSACTED;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// https://issues.apache.org/activemq/browse/AMQ-2594
public abstract class StoreOrderTest {
    private static final Logger LOG = LoggerFactory.getLogger(StoreOrderTest.class);

    protected BrokerService broker;

    private ActiveMQConnection connection;

    public Destination destination = new ActiveMQQueue("StoreOrderTest?consumer.prefetchSize=0");

    public class TransactedSend implements Runnable {
        private CountDownLatch readyForCommit;

        private CountDownLatch firstDone;

        private boolean first;

        private Session session;

        private MessageProducer producer;

        public TransactedSend(CountDownLatch readyForCommit, CountDownLatch firstDone, boolean b) throws Exception {
            this.readyForCommit = readyForCommit;
            this.firstDone = firstDone;
            this.first = b;
            session = connection.createSession(true, SESSION_TRANSACTED);
            producer = session.createProducer(destination);
        }

        public void run() {
            try {
                if (!(first)) {
                    firstDone.await(30, TimeUnit.SECONDS);
                }
                producer.send(session.createTextMessage((first ? "first" : "second")));
                if (first) {
                    firstDone.countDown();
                }
                readyForCommit.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(("unexpected ex on run " + e));
            }
        }

        public void commit() throws Exception {
            session.commit();
            session.close();
        }
    }

    @Test
    public void testCompositeSendReceiveAfterRestart() throws Exception {
        destination = new ActiveMQQueue("StoreOrderTest,SecondStoreOrderTest");
        enqueueOneMessage();
        StoreOrderTest.LOG.info("restart broker");
        stopBroker();
        broker = createRestartedBroker();
        dumpMessages();
        initConnection();
        destination = new ActiveMQQueue("StoreOrderTest");
        Assert.assertNotNull("got one message from first dest", receiveOne());
        dumpMessages();
        destination = new ActiveMQQueue("SecondStoreOrderTest");
        Assert.assertNotNull("got one message from second dest", receiveOne());
    }

    @Test
    public void validateUnorderedTxCommit() throws Exception {
        Executor executor = Executors.newCachedThreadPool();
        CountDownLatch readyForCommit = new CountDownLatch(2);
        CountDownLatch firstDone = new CountDownLatch(1);
        StoreOrderTest.TransactedSend first = new StoreOrderTest.TransactedSend(readyForCommit, firstDone, true);
        StoreOrderTest.TransactedSend second = new StoreOrderTest.TransactedSend(readyForCommit, firstDone, false);
        executor.execute(first);
        executor.execute(second);
        Assert.assertTrue("both started", readyForCommit.await(20, TimeUnit.SECONDS));
        StoreOrderTest.LOG.info("commit out of order");
        // send interleaved so sequence id at time of commit could be reversed
        second.commit();
        // force usage over the limit before second commit to flush cache
        enqueueOneMessage();
        // can get lost in the cursor as it is behind the last sequenceId that was cached
        first.commit();
        StoreOrderTest.LOG.info("send/commit done..");
        dumpMessages();
        String received1;
        String received2;
        String received3 = null;
        if (true) {
            StoreOrderTest.LOG.info("receive and rollback...");
            Session session = connection.createSession(true, SESSION_TRANSACTED);
            received1 = receive(session);
            received2 = receive(session);
            received3 = receive(session);
            Assert.assertEquals("second", received1);
            Assert.assertEquals("middle", received2);
            Assert.assertEquals("first", received3);
            session.rollback();
            session.close();
        }
        StoreOrderTest.LOG.info("restart broker");
        stopBroker();
        broker = createRestartedBroker();
        initConnection();
        if (true) {
            StoreOrderTest.LOG.info("receive and rollback after restart...");
            Session session = connection.createSession(true, SESSION_TRANSACTED);
            received1 = receive(session);
            received2 = receive(session);
            received3 = receive(session);
            Assert.assertEquals("second", received1);
            Assert.assertEquals("middle", received2);
            Assert.assertEquals("first", received3);
            session.rollback();
            session.close();
        }
        StoreOrderTest.LOG.info("receive and ack each message");
        received1 = receiveOne();
        received2 = receiveOne();
        received3 = receiveOne();
        Assert.assertEquals("second", received1);
        Assert.assertEquals("middle", received2);
        Assert.assertEquals("first", received3);
    }
}

