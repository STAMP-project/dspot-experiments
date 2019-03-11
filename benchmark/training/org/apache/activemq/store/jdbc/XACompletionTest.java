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
package org.apache.activemq.store.jdbc;


import Session.CLIENT_ACKNOWLEDGE;
import java.util.Arrays;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.XASession;
import javax.management.ObjectName;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnection;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.TestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.XATransactionId;
import org.apache.activemq.util.TestUtils;
import org.apache.activemq.util.Wait;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class XACompletionTest extends TestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(XACompletionTest.class);

    protected ActiveMQXAConnectionFactory factory;

    protected static final int messagesExpected = 1;

    protected BrokerService broker;

    protected String connectionUri;

    @Parameterized.Parameter
    public TestSupport.PersistenceAdapterChoice persistenceAdapterChoice;

    @Test
    public void testStatsAndRedispatchAfterAckPreparedClosed() throws Exception {
        factory = new ActiveMQXAConnectionFactory((((connectionUri) + "?jms.prefetchPolicy.all=0&jms.redeliveryPolicy.maximumRedeliveries=") + 0));
        factory.setWatchTopicAdvisories(false);
        sendMessages(1);
        ActiveMQXAConnection activeMQXAConnection = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnection.start();
        XASession xaSession = activeMQXAConnection.createXASession();
        Destination destination = xaSession.createQueue("TEST");
        MessageConsumer consumer = xaSession.createConsumer(destination);
        XAResource resource = xaSession.getXAResource();
        resource.recover(XAResource.TMSTARTRSCAN);
        resource.recover(XAResource.TMNOFLAGS);
        Xid tid = TestUtils.createXid();
        resource.start(tid, XAResource.TMNOFLAGS);
        Message message = consumer.receive(2000);
        XACompletionTest.LOG.info(("Received : " + message));
        resource.end(tid, XAResource.TMSUCCESS);
        activeMQXAConnection.close();
        dumpMessages();
        dumpMessages();
        XACompletionTest.LOG.info("Try jmx browse... after commit");
        ObjectName queueViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST");
        QueueViewMBean proxy = ((QueueViewMBean) (broker.getManagementContext().newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true)));
        assertEquals("size", 1, proxy.getQueueSize());
        XACompletionTest.LOG.info("Try receive... after rollback");
        message = regularReceive("TEST");
        assertNotNull("message gone", message);
    }

    @Test
    public void testStatsAndBrowseAfterAckPreparedCommitted() throws Exception {
        factory = new ActiveMQXAConnectionFactory((((connectionUri) + "?jms.prefetchPolicy.all=0&jms.redeliveryPolicy.maximumRedeliveries=") + (XACompletionTest.messagesExpected)));
        factory.setWatchTopicAdvisories(false);
        sendMessages(XACompletionTest.messagesExpected);
        ActiveMQXAConnection activeMQXAConnection = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnection.start();
        XASession xaSession = activeMQXAConnection.createXASession();
        Destination destination = xaSession.createQueue("TEST");
        MessageConsumer consumer = xaSession.createConsumer(destination);
        XAResource resource = xaSession.getXAResource();
        resource.recover(XAResource.TMSTARTRSCAN);
        resource.recover(XAResource.TMNOFLAGS);
        Xid tid = TestUtils.createXid();
        resource.start(tid, XAResource.TMNOFLAGS);
        int messagesReceived = 0;
        for (int i = 0; i < (XACompletionTest.messagesExpected); i++) {
            Message message = null;
            try {
                XACompletionTest.LOG.debug(((("Receiving message " + (messagesReceived + 1)) + " of ") + (XACompletionTest.messagesExpected)));
                message = consumer.receive(2000);
                XACompletionTest.LOG.info(("Received : " + message));
                messagesReceived++;
            } catch (Exception e) {
                XACompletionTest.LOG.debug("Caught exception:", e);
            }
        }
        resource.end(tid, XAResource.TMSUCCESS);
        resource.prepare(tid);
        consumer.close();
        dumpMessages();
        resource.commit(tid, false);
        dumpMessages();
        XACompletionTest.LOG.info("Try jmx browse... after commit");
        ObjectName queueViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST");
        QueueViewMBean proxy = ((QueueViewMBean) (broker.getManagementContext().newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true)));
        assertTrue(proxy.browseMessages().isEmpty());
        assertEquals("prefetch 0", 0, proxy.getInFlightCount());
        assertEquals("size 0", 0, proxy.getQueueSize());
        XACompletionTest.LOG.info("Try browse... after commit");
        Message browsed = regularBrowseFirst();
        assertNull("message gone", browsed);
        XACompletionTest.LOG.info("Try receive... after commit");
        Message message = regularReceive("TEST");
        assertNull("message gone", message);
    }

    @Test
    public void testStatsAndBrowseAfterAckPreparedRolledback() throws Exception {
        factory = new ActiveMQXAConnectionFactory(((connectionUri) + "?jms.prefetchPolicy.all=0"));
        factory.setWatchTopicAdvisories(false);
        sendMessages(10);
        ObjectName queueViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST");
        QueueViewMBean proxy = ((QueueViewMBean) (broker.getManagementContext().newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true)));
        ActiveMQXAConnection activeMQXAConnection = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnection.start();
        XASession xaSession = activeMQXAConnection.createXASession();
        Destination destination = xaSession.createQueue("TEST");
        MessageConsumer consumer = xaSession.createConsumer(destination);
        XAResource resource = xaSession.getXAResource();
        resource.recover(XAResource.TMSTARTRSCAN);
        resource.recover(XAResource.TMNOFLAGS);
        assertEquals("prefetch 0", 0, proxy.getInFlightCount());
        assertEquals("size 0", 10, proxy.getQueueSize());
        assertEquals("size 0", 0, proxy.cursorSize());
        Xid tid = TestUtils.createXid();
        resource.start(tid, XAResource.TMNOFLAGS);
        for (int i = 0; i < 5; i++) {
            Message message = null;
            try {
                message = consumer.receive(2000);
                XACompletionTest.LOG.info(("Received : " + message));
            } catch (Exception e) {
                XACompletionTest.LOG.debug("Caught exception:", e);
            }
        }
        resource.end(tid, XAResource.TMSUCCESS);
        resource.prepare(tid);
        consumer.close();
        dumpMessages();
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (proxy.getInFlightCount()) == 0L;
            }
        });
        assertEquals("prefetch", 0, proxy.getInFlightCount());
        assertEquals("size", 10, proxy.getQueueSize());
        assertEquals("cursor size", 0, proxy.cursorSize());
        resource.rollback(tid);
        dumpMessages();
        XACompletionTest.LOG.info("Try jmx browse... after rollback");
        assertEquals(10, proxy.browseMessages().size());
        assertEquals("prefetch", 0, proxy.getInFlightCount());
        assertEquals("size", 10, proxy.getQueueSize());
        assertEquals("cursor size", 0, proxy.cursorSize());
        XACompletionTest.LOG.info("Try browse... after");
        Message browsed = regularBrowseFirst();
        assertNotNull("message gone", browsed);
        XACompletionTest.LOG.info("Try receive... after");
        for (int i = 0; i < 10; i++) {
            Message message = regularReceive("TEST");
            assertNotNull("message gone", message);
        }
    }

    @Test
    public void testStatsAndConsumeAfterAckPreparedRolledback() throws Exception {
        factory = new ActiveMQXAConnectionFactory(((connectionUri) + "?jms.prefetchPolicy.all=0"));
        factory.setWatchTopicAdvisories(false);
        sendMessages(10);
        ActiveMQXAConnection activeMQXAConnection = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnection.start();
        XASession xaSession = activeMQXAConnection.createXASession();
        Destination destination = xaSession.createQueue("TEST");
        MessageConsumer consumer = xaSession.createConsumer(destination);
        XAResource resource = xaSession.getXAResource();
        resource.recover(XAResource.TMSTARTRSCAN);
        resource.recover(XAResource.TMNOFLAGS);
        dumpMessages();
        Xid tid = TestUtils.createXid();
        resource.start(tid, XAResource.TMNOFLAGS);
        int messagesReceived = 0;
        for (int i = 0; i < 5; i++) {
            Message message = null;
            try {
                XACompletionTest.LOG.debug(((("Receiving message " + (messagesReceived + 1)) + " of ") + (XACompletionTest.messagesExpected)));
                message = consumer.receive(2000);
                XACompletionTest.LOG.info(("Received : " + message));
                messagesReceived++;
            } catch (Exception e) {
                XACompletionTest.LOG.debug("Caught exception:", e);
            }
        }
        resource.end(tid, XAResource.TMSUCCESS);
        resource.prepare(tid);
        consumer.close();
        XACompletionTest.LOG.info("after close");
        dumpMessages();
        assertEquals("drain", 5, drainUnack(5, "TEST"));
        dumpMessages();
        broker = restartBroker();
        assertEquals("redrain", 5, drainUnack(5, "TEST"));
        XACompletionTest.LOG.info("Try consume... after restart");
        dumpMessages();
        ObjectName queueViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST");
        QueueViewMBean proxy = ((QueueViewMBean) (broker.getManagementContext().newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true)));
        assertEquals("prefetch", 0, proxy.getInFlightCount());
        assertEquals("size", 5, proxy.getQueueSize());
        assertEquals("cursor size 0", 0, proxy.cursorSize());
        factory = new ActiveMQXAConnectionFactory(((connectionUri) + "?jms.prefetchPolicy.all=0"));
        factory.setWatchTopicAdvisories(false);
        activeMQXAConnection = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnection.start();
        xaSession = activeMQXAConnection.createXASession();
        XAResource xaResource = xaSession.getXAResource();
        Xid[] xids = xaResource.recover(XAResource.TMSTARTRSCAN);
        xaResource.recover(XAResource.TMNOFLAGS);
        XACompletionTest.LOG.info("Rollback outcome for ack");
        xaResource.rollback(xids[0]);
        XACompletionTest.LOG.info("Try receive... after rollback");
        for (int i = 0; i < 10; i++) {
            Message message = regularReceive("TEST");
            assertNotNull(("message gone: " + i), message);
        }
        dumpMessages();
        assertNull("none left", regularReceive("TEST"));
        assertEquals("prefetch", 0, proxy.getInFlightCount());
        assertEquals("size", 0, proxy.getQueueSize());
        assertEquals("cursor size", 0, proxy.cursorSize());
        assertEquals("dq", 10, proxy.getDequeueCount());
    }

    @Test
    public void testStatsAndConsumeAfterAckPreparedRolledbackOutOfOrderRecovery() throws Exception {
        factory = new ActiveMQXAConnectionFactory(((connectionUri) + "?jms.prefetchPolicy.all=0"));
        factory.setWatchTopicAdvisories(false);
        sendMessages(20);
        for (int i = 0; i < 10; i++) {
            ActiveMQXAConnection activeMQXAConnection = ((ActiveMQXAConnection) (factory.createXAConnection()));
            activeMQXAConnection.start();
            XASession xaSession = activeMQXAConnection.createXASession();
            Destination destination = xaSession.createQueue("TEST");
            MessageConsumer consumer = xaSession.createConsumer(destination);
            XAResource resource = xaSession.getXAResource();
            Xid tid = TestUtils.createXid();
            resource.start(tid, XAResource.TMNOFLAGS);
            Message message = null;
            try {
                message = consumer.receive(2000);
                XACompletionTest.LOG.info(((("Received (" + i) + ") : ,") + message));
            } catch (Exception e) {
                XACompletionTest.LOG.debug("Caught exception:", e);
            }
            resource.end(tid, XAResource.TMSUCCESS);
            resource.prepare(tid);
            // no close - b/c messages end up in pagedInPendingDispatch!
            // activeMQXAConnection.close();
        }
        ActiveMQXAConnection activeMQXAConnection = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnection.start();
        XASession xaSession = activeMQXAConnection.createXASession();
        XAResource xaResource = xaSession.getXAResource();
        Xid[] xids = xaResource.recover(XAResource.TMSTARTRSCAN);
        xaResource.recover(XAResource.TMNOFLAGS);
        xaResource.rollback(xids[0]);
        xaResource.rollback(xids[1]);
        activeMQXAConnection.close();
        XACompletionTest.LOG.info("RESTART");
        broker = restartBroker();
        dumpMessages();
        ObjectName queueViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST");
        QueueViewMBean proxy = ((QueueViewMBean) (broker.getManagementContext().newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true)));
        // set maxBatchSize=1
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory((((connectionUri) + "?jms.prefetchPolicy.all=") + 1));
        factory.setWatchTopicAdvisories(false);
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue("TEST");
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.close();
        ActiveMQConnectionFactory receiveFactory = new ActiveMQConnectionFactory(((connectionUri) + "?jms.prefetchPolicy.all=0"));
        receiveFactory.setWatchTopicAdvisories(false);
        // recover/rollback the second tx
        ActiveMQXAConnectionFactory activeMQXAConnectionFactory = new ActiveMQXAConnectionFactory(((connectionUri) + "?jms.prefetchPolicy.all=0"));
        activeMQXAConnectionFactory.setWatchTopicAdvisories(false);
        activeMQXAConnection = ((ActiveMQXAConnection) (activeMQXAConnectionFactory.createXAConnection()));
        activeMQXAConnection.start();
        xaSession = activeMQXAConnection.createXASession();
        xaResource = xaSession.getXAResource();
        xids = xaResource.recover(XAResource.TMSTARTRSCAN);
        xaResource.recover(XAResource.TMNOFLAGS);
        for (int i = 0; i < (xids.length); i++) {
            xaResource.rollback(xids[i]);
        }
        // another prefetch demand of 1
        MessageConsumer consumer2 = session.createConsumer(new ActiveMQQueue("TEST?consumer.prefetchSize=2"));
        XACompletionTest.LOG.info("Try receive... after rollback");
        Message message = regularReceiveWith(receiveFactory, "TEST");
        assertNotNull("message 1: ", message);
        XACompletionTest.LOG.info(("Received : " + message));
        dumpMessages();
        message = regularReceiveWith(receiveFactory, "TEST");
        assertNotNull("last message", message);
        XACompletionTest.LOG.info(("Received : " + message));
    }

    @Test
    public void testMoveInTwoBranches() throws Exception {
        factory = new ActiveMQXAConnectionFactory((((connectionUri) + "?jms.prefetchPolicy.all=0&jms.redeliveryPolicy.maximumRedeliveries=") + (XACompletionTest.messagesExpected)));
        factory.setWatchTopicAdvisories(false);
        sendMessages(XACompletionTest.messagesExpected);
        ActiveMQXAConnection activeMQXAConnection = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnection.start();
        XASession xaSession = activeMQXAConnection.createXASession();
        Destination destination = xaSession.createQueue("TEST");
        MessageConsumer consumer = xaSession.createConsumer(destination);
        XAResource resource = xaSession.getXAResource();
        final Xid tid = TestUtils.createXid();
        byte[] branch = tid.getBranchQualifier();
        final byte[] branch2 = Arrays.copyOf(branch, branch.length);
        branch2[0] = '!';
        Xid branchTid = new Xid() {
            @Override
            public int getFormatId() {
                return tid.getFormatId();
            }

            @Override
            public byte[] getGlobalTransactionId() {
                return tid.getGlobalTransactionId();
            }

            @Override
            public byte[] getBranchQualifier() {
                return branch2;
            }
        };
        resource.start(tid, XAResource.TMNOFLAGS);
        int messagesReceived = 0;
        Message message = null;
        for (int i = 0; i < (XACompletionTest.messagesExpected); i++) {
            try {
                XACompletionTest.LOG.debug(((("Receiving message " + (messagesReceived + 1)) + " of ") + (XACompletionTest.messagesExpected)));
                message = consumer.receive(2000);
                XACompletionTest.LOG.info(("Received : " + message));
                messagesReceived++;
            } catch (Exception e) {
                XACompletionTest.LOG.debug("Caught exception:", e);
            }
        }
        resource.end(tid, XAResource.TMSUCCESS);
        ActiveMQXAConnection activeMQXAConnectionSend = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnectionSend.start();
        XASession xaSessionSend = activeMQXAConnection.createXASession();
        Destination destinationSend = xaSessionSend.createQueue("TEST_MOVE");
        MessageProducer producer = xaSessionSend.createProducer(destinationSend);
        XAResource resourceSend = xaSessionSend.getXAResource();
        resourceSend.start(branchTid, XAResource.TMNOFLAGS);
        ActiveMQMessage toSend = ((ActiveMQMessage) (xaSessionSend.createTextMessage()));
        toSend.setTransactionId(new XATransactionId(branchTid));
        producer.send(toSend);
        resourceSend.end(branchTid, XAResource.TMSUCCESS);
        resourceSend.prepare(branchTid);
        resource.prepare(tid);
        consumer.close();
        XACompletionTest.LOG.info("Prepared");
        dumpMessages();
        XACompletionTest.LOG.info("Commit Ack");
        resource.commit(tid, false);
        dumpMessages();
        XACompletionTest.LOG.info("Commit Send");
        resourceSend.commit(branchTid, false);
        dumpMessages();
        XACompletionTest.LOG.info("Try jmx browse... after commit");
        ObjectName queueViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST");
        QueueViewMBean proxy = ((QueueViewMBean) (broker.getManagementContext().newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true)));
        assertTrue(proxy.browseMessages().isEmpty());
        assertEquals("dq ", 1, proxy.getDequeueCount());
        assertEquals("size 0", 0, proxy.getQueueSize());
        ObjectName queueMoveViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST_MOVE");
        QueueViewMBean moveProxy = ((QueueViewMBean) (broker.getManagementContext().newProxyInstance(queueMoveViewMBeanName, QueueViewMBean.class, true)));
        assertEquals("enq", 1, moveProxy.getEnqueueCount());
        assertEquals("size 1", 1, moveProxy.getQueueSize());
        assertNotNull(regularReceive("TEST_MOVE"));
        assertEquals("size 0", 0, moveProxy.getQueueSize());
    }

    @Test
    public void testMoveInTwoBranchesPreparedAckRecoveryRestartRollback() throws Exception {
        factory = new ActiveMQXAConnectionFactory((((connectionUri) + "?jms.prefetchPolicy.all=0&jms.redeliveryPolicy.maximumRedeliveries=") + (XACompletionTest.messagesExpected)));
        factory.setWatchTopicAdvisories(false);
        sendMessages(XACompletionTest.messagesExpected);
        ActiveMQXAConnection activeMQXAConnection = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnection.start();
        XASession xaSession = activeMQXAConnection.createXASession();
        Destination destination = xaSession.createQueue("TEST");
        MessageConsumer consumer = xaSession.createConsumer(destination);
        XAResource resource = xaSession.getXAResource();
        final Xid tid = TestUtils.createXid();
        byte[] branch = tid.getBranchQualifier();
        final byte[] branch2 = Arrays.copyOf(branch, branch.length);
        branch2[0] = '!';
        Xid branchTid = new Xid() {
            @Override
            public int getFormatId() {
                return tid.getFormatId();
            }

            @Override
            public byte[] getGlobalTransactionId() {
                return tid.getGlobalTransactionId();
            }

            @Override
            public byte[] getBranchQualifier() {
                return branch2;
            }
        };
        resource.start(tid, XAResource.TMNOFLAGS);
        int messagesReceived = 0;
        Message message = null;
        for (int i = 0; i < (XACompletionTest.messagesExpected); i++) {
            try {
                XACompletionTest.LOG.debug(((("Receiving message " + (messagesReceived + 1)) + " of ") + (XACompletionTest.messagesExpected)));
                message = consumer.receive(2000);
                XACompletionTest.LOG.info(("Received : " + message));
                messagesReceived++;
            } catch (Exception e) {
                XACompletionTest.LOG.debug("Caught exception:", e);
            }
        }
        resource.end(tid, XAResource.TMSUCCESS);
        ActiveMQXAConnection activeMQXAConnectionSend = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnectionSend.start();
        XASession xaSessionSend = activeMQXAConnection.createXASession();
        Destination destinationSend = xaSessionSend.createQueue("TEST_MOVE");
        MessageProducer producer = xaSessionSend.createProducer(destinationSend);
        XAResource resourceSend = xaSessionSend.getXAResource();
        resourceSend.start(branchTid, XAResource.TMNOFLAGS);
        ActiveMQMessage toSend = ((ActiveMQMessage) (xaSessionSend.createTextMessage()));
        toSend.setTransactionId(new XATransactionId(branchTid));
        producer.send(toSend);
        resourceSend.end(branchTid, XAResource.TMSUCCESS);
        resourceSend.prepare(branchTid);
        // ack on TEST is prepared
        resource.prepare(tid);
        // send to TEST_MOVE is rolledback
        resourceSend.rollback(branchTid);
        consumer.close();
        XACompletionTest.LOG.info("Prepared");
        dumpMessages();
        broker = restartBroker();
        XACompletionTest.LOG.info("New broker");
        dumpMessages();
        ObjectName queueViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST");
        QueueViewMBean proxy = ((QueueViewMBean) (broker.getManagementContext().newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true)));
        assertEquals("size", 0, proxy.getQueueSize());
        assertNull(regularReceive("TEST_MOVE"));
        ObjectName queueMoveViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST_MOVE");
        QueueViewMBean moveProxy = ((QueueViewMBean) (broker.getManagementContext().newProxyInstance(queueMoveViewMBeanName, QueueViewMBean.class, true)));
        assertEquals("enq", 0, moveProxy.getDequeueCount());
        assertEquals("size", 0, moveProxy.getQueueSize());
        assertEquals("size 0", 0, moveProxy.getQueueSize());
        factory = new ActiveMQXAConnectionFactory((((connectionUri) + "?jms.prefetchPolicy.all=0&jms.redeliveryPolicy.maximumRedeliveries=") + (XACompletionTest.messagesExpected)));
        factory.setWatchTopicAdvisories(false);
        activeMQXAConnection = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnection.start();
        xaSession = activeMQXAConnection.createXASession();
        resource = xaSession.getXAResource();
        resource.rollback(tid);
        assertEquals("size", 1, proxy.getQueueSize());
        assertEquals("c size", 1, proxy.cursorSize());
        assertNotNull(regularReceive("TEST"));
        assertEquals("size", 0, proxy.getQueueSize());
        assertEquals("c size", 0, proxy.cursorSize());
        assertEquals("dq", 1, proxy.getDequeueCount());
    }

    @Test
    public void testMoveInTwoBranchesTwoBrokers() throws Exception {
        factory = new ActiveMQXAConnectionFactory((((connectionUri) + "?jms.prefetchPolicy.all=0&jms.redeliveryPolicy.maximumRedeliveries=") + (XACompletionTest.messagesExpected)));
        factory.setWatchTopicAdvisories(false);
        sendMessages(XACompletionTest.messagesExpected);
        ActiveMQXAConnection activeMQXAConnection = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnection.start();
        XASession xaSession = activeMQXAConnection.createXASession();
        Destination destination = xaSession.createQueue("TEST");
        MessageConsumer consumer = xaSession.createConsumer(destination);
        XAResource resource = xaSession.getXAResource();
        final Xid tid = TestUtils.createXid();
        byte[] branch = tid.getBranchQualifier();
        final byte[] branch2 = Arrays.copyOf(branch, branch.length);
        branch2[0] = '!';
        Xid branchTid = new Xid() {
            @Override
            public int getFormatId() {
                return tid.getFormatId();
            }

            @Override
            public byte[] getGlobalTransactionId() {
                return tid.getGlobalTransactionId();
            }

            @Override
            public byte[] getBranchQualifier() {
                return branch2;
            }
        };
        resource.start(tid, XAResource.TMNOFLAGS);
        int messagesReceived = 0;
        Message message = null;
        for (int i = 0; i < (XACompletionTest.messagesExpected); i++) {
            try {
                XACompletionTest.LOG.debug(((("Receiving message " + (messagesReceived + 1)) + " of ") + (XACompletionTest.messagesExpected)));
                message = consumer.receive(2000);
                XACompletionTest.LOG.info(("Received : " + message));
                messagesReceived++;
            } catch (Exception e) {
                XACompletionTest.LOG.debug("Caught exception:", e);
            }
        }
        resource.end(tid, XAResource.TMSUCCESS);
        ActiveMQXAConnection activeMQXAConnectionSend = ((ActiveMQXAConnection) (factory.createXAConnection()));
        activeMQXAConnectionSend.start();
        XASession xaSessionSend = activeMQXAConnection.createXASession();
        Destination destinationSend = xaSessionSend.createQueue("TEST_MOVE");
        MessageProducer producer = xaSessionSend.createProducer(destinationSend);
        XAResource resourceSend = xaSessionSend.getXAResource();
        resourceSend.start(branchTid, XAResource.TMNOFLAGS);
        ActiveMQMessage toSend = ((ActiveMQMessage) (xaSessionSend.createTextMessage()));
        toSend.setTransactionId(new XATransactionId(branchTid));
        producer.send(toSend);
        resourceSend.end(branchTid, XAResource.TMSUCCESS);
        resourceSend.prepare(branchTid);
        resource.prepare(tid);
        consumer.close();
        XACompletionTest.LOG.info("Prepared");
        dumpMessages();
        XACompletionTest.LOG.info("Commit Ack");
        resource.commit(tid, false);
        dumpMessages();
        XACompletionTest.LOG.info("Commit Send");
        resourceSend.commit(branchTid, false);
        dumpMessages();
        XACompletionTest.LOG.info("Try jmx browse... after commit");
        ObjectName queueViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST");
        QueueViewMBean proxy = ((QueueViewMBean) (broker.getManagementContext().newProxyInstance(queueViewMBeanName, QueueViewMBean.class, true)));
        assertTrue(proxy.browseMessages().isEmpty());
        assertEquals("dq ", 1, proxy.getDequeueCount());
        assertEquals("size 0", 0, proxy.getQueueSize());
        ObjectName queueMoveViewMBeanName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName=TEST_MOVE");
        QueueViewMBean moveProxy = ((QueueViewMBean) (broker.getManagementContext().newProxyInstance(queueMoveViewMBeanName, QueueViewMBean.class, true)));
        assertEquals("enq", 1, moveProxy.getEnqueueCount());
        assertEquals("size 1", 1, moveProxy.getQueueSize());
        assertNotNull(regularReceive("TEST_MOVE"));
        assertEquals("size 0", 0, moveProxy.getQueueSize());
    }
}

