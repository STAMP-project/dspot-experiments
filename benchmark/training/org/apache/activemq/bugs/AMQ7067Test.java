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
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.management.InstanceNotFoundException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnection;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.store.kahadb.MessageDatabase;
import org.apache.activemq.util.Wait;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


public class AMQ7067Test {
    protected static Random r = new Random();

    static final String WIRE_LEVEL_ENDPOINT = "tcp://localhost:61616";

    protected BrokerService broker;

    protected ActiveMQXAConnection connection;

    protected XASession xaSession;

    protected XAResource xaRes;

    private final String xbean = "xbean:";

    private final String confBase = "src/test/resources/org/apache/activemq/bugs/amq7067";

    private static final ActiveMQXAConnectionFactory ACTIVE_MQ_CONNECTION_FACTORY;

    private static final ActiveMQConnectionFactory ACTIVE_MQ_NON_XA_CONNECTION_FACTORY;

    static {
        ACTIVE_MQ_CONNECTION_FACTORY = new ActiveMQXAConnectionFactory(AMQ7067Test.WIRE_LEVEL_ENDPOINT);
        ACTIVE_MQ_NON_XA_CONNECTION_FACTORY = new ActiveMQConnectionFactory(AMQ7067Test.WIRE_LEVEL_ENDPOINT);
    }

    @Test
    public void testXAPrepare() throws Exception {
        setupXAConnection();
        Queue holdKahaDb = xaSession.createQueue("holdKahaDb");
        MessageProducer holdKahaDbProducer = xaSession.createProducer(holdKahaDb);
        XATransactionId txid = AMQ7067Test.createXATransaction();
        System.out.println(("****** create new txid = " + txid));
        xaRes.start(txid, XAResource.TMNOFLAGS);
        TextMessage helloMessage = xaSession.createTextMessage(StringUtils.repeat("a", 10));
        holdKahaDbProducer.send(helloMessage);
        xaRes.end(txid, XAResource.TMSUCCESS);
        Queue queue = xaSession.createQueue("test");
        AMQ7067Test.produce(xaRes, xaSession, queue, 100, (512 * 1024));
        xaRes.prepare(txid);
        AMQ7067Test.produce(xaRes, xaSession, queue, 100, (512 * 1024));
        purge();
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 0 == (getQueueSize(queue.getQueueName()));
            }
        });
        // force gc
        broker.getPersistenceAdapter().checkpoint(true);
        Xid[] xids = xaRes.recover(XAResource.TMSTARTRSCAN);
        // Should be 1 since we have only 1 prepared
        Assert.assertEquals(1, xids.length);
        connection.close();
        broker.stop();
        broker.waitUntilStopped();
        createBroker();
        setupXAConnection();
        xids = xaRes.recover(XAResource.TMSTARTRSCAN);
        System.out.println(("****** recovered = " + xids));
        // THIS SHOULD NOT FAIL AS THERE SHOULD DBE ONLY 1 TRANSACTION!
        Assert.assertEquals(1, xids.length);
    }

    @Test
    public void testXAPrepareWithAckCompactionDoesNotLooseInflight() throws Exception {
        // investigate liner gc issue - store usage not getting released
        Logger.getLogger(MessageDatabase.class).setLevel(Level.TRACE);
        setupXAConnection();
        Queue holdKahaDb = xaSession.createQueue("holdKahaDb");
        MessageProducer holdKahaDbProducer = xaSession.createProducer(holdKahaDb);
        XATransactionId txid = AMQ7067Test.createXATransaction();
        System.out.println(("****** create new txid = " + txid));
        xaRes.start(txid, XAResource.TMNOFLAGS);
        TextMessage helloMessage = xaSession.createTextMessage(StringUtils.repeat("a", 10));
        holdKahaDbProducer.send(helloMessage);
        xaRes.end(txid, XAResource.TMSUCCESS);
        Queue queue = xaSession.createQueue("test");
        AMQ7067Test.produce(xaRes, xaSession, queue, 100, (512 * 1024));
        xaRes.prepare(txid);
        AMQ7067Test.produce(xaRes, xaSession, queue, 100, (512 * 1024));
        purge();
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 0 == (getQueueSize(queue.getQueueName()));
            }
        });
        // force gc, two data files requires two cycles
        int limit = (getCompactAcksAfterNoGC()) + 1;
        for (int i = 0; i < (limit * 2); i++) {
            broker.getPersistenceAdapter().checkpoint(true);
        }
        // ack compaction task operates in the background
        TimeUnit.SECONDS.sleep(5);
        Xid[] xids = xaRes.recover(XAResource.TMSTARTRSCAN);
        // Should be 1 since we have only 1 prepared
        Assert.assertEquals(1, xids.length);
        connection.close();
        broker.stop();
        broker.waitUntilStopped();
        createBroker();
        setupXAConnection();
        xids = xaRes.recover(XAResource.TMSTARTRSCAN);
        System.out.println(("****** recovered = " + xids));
        // THIS SHOULD NOT FAIL AS THERE SHOULD DBE ONLY 1 TRANSACTION!
        Assert.assertEquals(1, xids.length);
    }

    @Test
    public void testXACommitWithAckCompactionDoesNotLooseOutcomeOnFullRecovery() throws Exception {
        doTestXACompletionWithAckCompactionDoesNotLooseOutcomeOnFullRecovery(true);
    }

    @Test
    public void testXARollbackWithAckCompactionDoesNotLooseOutcomeOnFullRecovery() throws Exception {
        doTestXACompletionWithAckCompactionDoesNotLooseOutcomeOnFullRecovery(false);
    }

    @Test
    public void testXAcommit() throws Exception {
        setupXAConnection();
        Queue holdKahaDb = xaSession.createQueue("holdKahaDb");
        AMQ7067Test.createDanglingTransaction(xaRes, xaSession, holdKahaDb);
        MessageProducer holdKahaDbProducer = xaSession.createProducer(holdKahaDb);
        XATransactionId txid = AMQ7067Test.createXATransaction();
        System.out.println(("****** create new txid = " + txid));
        xaRes.start(txid, XAResource.TMNOFLAGS);
        TextMessage helloMessage = xaSession.createTextMessage(StringUtils.repeat("a", 10));
        holdKahaDbProducer.send(helloMessage);
        xaRes.end(txid, XAResource.TMSUCCESS);
        xaRes.prepare(txid);
        Queue queue = xaSession.createQueue("test");
        AMQ7067Test.produce(xaRes, xaSession, queue, 100, (512 * 1024));
        xaRes.commit(txid, false);
        AMQ7067Test.produce(xaRes, xaSession, queue, 100, (512 * 1024));
        purge();
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 0 == (getQueueSize(queue.getQueueName()));
            }
        });
        // force gc
        broker.getPersistenceAdapter().checkpoint(true);
        Xid[] xids = xaRes.recover(XAResource.TMSTARTRSCAN);
        // Should be 1 since we have only 1 prepared
        Assert.assertEquals(1, xids.length);
        connection.close();
        broker.stop();
        broker.waitUntilStopped();
        createBroker();
        setupXAConnection();
        xids = xaRes.recover(XAResource.TMSTARTRSCAN);
        // THIS SHOULD NOT FAIL AS THERE SHOULD DBE ONLY 1 TRANSACTION!
        Assert.assertEquals(1, xids.length);
    }

    @Test
    public void testXArollback() throws Exception {
        setupXAConnection();
        Queue holdKahaDb = xaSession.createQueue("holdKahaDb");
        AMQ7067Test.createDanglingTransaction(xaRes, xaSession, holdKahaDb);
        MessageProducer holdKahaDbProducer = xaSession.createProducer(holdKahaDb);
        XATransactionId txid = AMQ7067Test.createXATransaction();
        System.out.println(("****** create new txid = " + txid));
        xaRes.start(txid, XAResource.TMNOFLAGS);
        TextMessage helloMessage = xaSession.createTextMessage(StringUtils.repeat("a", 10));
        holdKahaDbProducer.send(helloMessage);
        xaRes.end(txid, XAResource.TMSUCCESS);
        xaRes.prepare(txid);
        Queue queue = xaSession.createQueue("test");
        AMQ7067Test.produce(xaRes, xaSession, queue, 100, (512 * 1024));
        xaRes.rollback(txid);
        AMQ7067Test.produce(xaRes, xaSession, queue, 100, (512 * 1024));
        purge();
        Xid[] xids = xaRes.recover(XAResource.TMSTARTRSCAN);
        // Should be 1 since we have only 1 prepared
        Assert.assertEquals(1, xids.length);
        connection.close();
        broker.stop();
        broker.waitUntilStopped();
        createBroker();
        setupXAConnection();
        xids = xaRes.recover(XAResource.TMSTARTRSCAN);
        // THIS SHOULD NOT FAIL AS THERE SHOULD BE ONLY 1 TRANSACTION!
        Assert.assertEquals(1, xids.length);
    }

    @Test
    public void testCommit() throws Exception {
        final Connection connection = AMQ7067Test.ACTIVE_MQ_NON_XA_CONNECTION_FACTORY.createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        Queue holdKahaDb = session.createQueue("holdKahaDb");
        MessageProducer holdKahaDbProducer = session.createProducer(holdKahaDb);
        TextMessage helloMessage = session.createTextMessage(StringUtils.repeat("a", 10));
        holdKahaDbProducer.send(helloMessage);
        Queue queue = session.createQueue("test");
        AMQ7067Test.produce(connection, queue, 100, (512 * 1024));
        session.commit();
        AMQ7067Test.produce(connection, queue, 100, (512 * 1024));
        System.out.println(String.format("QueueSize %s: %d", holdKahaDb.getQueueName(), getQueueSize(holdKahaDb.getQueueName())));
        purgeQueue(queue.getQueueName());
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 0 == (getQueueSize(queue.getQueueName()));
            }
        });
        // force gc
        broker.getPersistenceAdapter().checkpoint(true);
        connection.close();
        AMQ7067Test.curruptIndexFile(getDataDirectory());
        broker.stop();
        broker.waitUntilStopped();
        createBroker();
        broker.waitUntilStarted();
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(String.format("QueueSize %s: %d", holdKahaDb.getQueueName(), getQueueSize(holdKahaDb.getQueueName())));
                break;
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                break;
            }
        } 
        // THIS SHOULD NOT FAIL AS THERE SHOULD BE ONLY 1 TRANSACTION!
        Assert.assertEquals(1, getQueueSize(holdKahaDb.getQueueName()));
    }

    @Test
    public void testRollback() throws Exception {
        final Connection connection = AMQ7067Test.ACTIVE_MQ_NON_XA_CONNECTION_FACTORY.createConnection();
        connection.start();
        Session session = connection.createSession(true, SESSION_TRANSACTED);
        Queue holdKahaDb = session.createQueue("holdKahaDb");
        MessageProducer holdKahaDbProducer = session.createProducer(holdKahaDb);
        TextMessage helloMessage = session.createTextMessage(StringUtils.repeat("a", 10));
        holdKahaDbProducer.send(helloMessage);
        Queue queue = session.createQueue("test");
        AMQ7067Test.produce(connection, queue, 100, (512 * 1024));
        session.rollback();
        AMQ7067Test.produce(connection, queue, 100, (512 * 1024));
        System.out.println(String.format("QueueSize %s: %d", holdKahaDb.getQueueName(), getQueueSize(holdKahaDb.getQueueName())));
        purgeQueue(queue.getQueueName());
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 0 == (getQueueSize(queue.getQueueName()));
            }
        });
        // force gc
        broker.getPersistenceAdapter().checkpoint(true);
        connection.close();
        AMQ7067Test.curruptIndexFile(getDataDirectory());
        broker.stop();
        broker.waitUntilStopped();
        createBroker();
        broker.waitUntilStarted();
        // no sign of the test queue on recovery, rollback is the default for any inflight
        // this test serves as a sanity check on existing behaviour
        try {
            getQueueSize(holdKahaDb.getQueueName());
            Assert.fail("expect InstanceNotFoundException");
        } catch (UndeclaredThrowableException expected) {
            Assert.assertTrue(((expected.getCause()) instanceof InstanceNotFoundException));
        }
    }
}

