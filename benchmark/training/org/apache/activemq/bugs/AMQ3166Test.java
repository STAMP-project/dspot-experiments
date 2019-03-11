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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TransactionRolledBackException;
import javax.transaction.xa.XAException;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.AsyncCallback;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.transaction.Synchronization;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3166Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ3166Test.class);

    private BrokerService brokerService;

    private AtomicInteger sendAttempts = new AtomicInteger(0);

    @Test
    public void testCommitThroughAsyncErrorNoForceRollback() throws Exception {
        startBroker(false);
        Connection connection = createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        MessageProducer producer = session.createProducer(session.createQueue("QAT"));
        for (int i = 0; i < 10; i++) {
            producer.send(session.createTextMessage("Hello A"));
        }
        session.commit();
        Assert.assertTrue("only one message made it through", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getTotalEnqueueCount()) == 1;
            }
        }));
        connection.close();
    }

    @Test
    public void testCommitThroughAsyncErrorForceRollback() throws Exception {
        startBroker(true);
        Connection connection = createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        MessageProducer producer = session.createProducer(session.createQueue("QAT"));
        try {
            for (int i = 0; i < 10; i++) {
                producer.send(session.createTextMessage("Hello A"));
            }
            session.commit();
            Assert.fail("Expect TransactionRolledBackException");
        } catch (JMSException expected) {
            Assert.assertTrue(((expected.getCause()) instanceof XAException));
        }
        Assert.assertTrue("only one message made it through", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getTotalEnqueueCount()) == 0;
            }
        }));
        connection.close();
    }

    @Test
    public void testAckCommitThroughAsyncErrorForceRollback() throws Exception {
        startBroker(true);
        Connection connection = createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        Queue destination = session.createQueue("QAT");
        MessageProducer producer = session.createProducer(destination);
        producer.send(session.createTextMessage("Hello A"));
        producer.close();
        session.commit();
        MessageConsumer messageConsumer = session.createConsumer(destination);
        Assert.assertNotNull("got message", messageConsumer.receive(4000));
        try {
            session.commit();
            Assert.fail("Expect TransactionRolledBackException");
        } catch (JMSException expected) {
            Assert.assertTrue(((expected.getCause()) instanceof XAException));
            Assert.assertTrue(((expected.getCause().getCause()) instanceof TransactionRolledBackException));
            Assert.assertTrue(((expected.getCause().getCause().getCause()) instanceof RuntimeException));
        }
        Assert.assertTrue("one message still there!", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getTotalMessageCount()) == 1;
            }
        }));
        connection.close();
    }

    @Test
    public void testErrorOnSyncSend() throws Exception {
        startBroker(false);
        ActiveMQConnection connection = ((ActiveMQConnection) (createConnection()));
        connection.setAlwaysSyncSend(true);
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        MessageProducer producer = session.createProducer(session.createQueue("QAT"));
        try {
            for (int i = 0; i < 10; i++) {
                producer.send(session.createTextMessage("Hello A"));
            }
            session.commit();
        } catch (JMSException expectedSendFail) {
            AMQ3166Test.LOG.info(("Got expected: " + expectedSendFail));
            session.rollback();
        }
        Assert.assertTrue("only one message made it through", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getTotalEnqueueCount()) == 0;
            }
        }));
        connection.close();
    }

    @Test
    public void testRollbackOnAsyncErrorAmqApi() throws Exception {
        startBroker(false);
        ActiveMQConnection connection = ((ActiveMQConnection) (createConnection()));
        connection.start();
        final ActiveMQSession session = ((ActiveMQSession) (connection.createSession(true, SESSION_TRANSACTED)));
        int batchSize = 10;
        final CountDownLatch batchSent = new CountDownLatch(batchSize);
        ActiveMQMessageProducer producer = ((ActiveMQMessageProducer) (session.createProducer(session.createQueue("QAT"))));
        for (int i = 0; i < batchSize; i++) {
            producer.send(session.createTextMessage("Hello A"), new AsyncCallback() {
                @Override
                public void onSuccess() {
                    batchSent.countDown();
                }

                @Override
                public void onException(JMSException e) {
                    session.getTransactionContext().setRollbackOnly(true);
                    batchSent.countDown();
                }
            });
            if (i == 0) {
                // transaction context begun on first send
                session.getTransactionContext().addSynchronization(new Synchronization() {
                    @Override
                    public void beforeEnd() throws Exception {
                        // await response to all sends in the batch
                        if (!(batchSent.await(10, TimeUnit.SECONDS))) {
                            AMQ3166Test.LOG.error("TimedOut waiting for aync send requests!");
                            session.getTransactionContext().setRollbackOnly(true);
                        }
                        super.beforeEnd();
                    }
                });
            }
        }
        try {
            session.commit();
            Assert.fail("expect rollback on async error");
        } catch (TransactionRolledBackException expected) {
        }
        Assert.assertTrue("only one message made it through", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getTotalEnqueueCount()) == 0;
            }
        }));
        connection.close();
    }
}

