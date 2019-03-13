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
package org.apache.activemq.transport.failover;


import Session.AUTO_ACKNOWLEDGE;
import Session.SESSION_TRANSACTED;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FailoverConsumerOutstandingCommitTest {
    private static final Logger LOG = LoggerFactory.getLogger(FailoverConsumerOutstandingCommitTest.class);

    private static final String QUEUE_NAME = "FailoverWithOutstandingCommit";

    private static final String MESSAGE_TEXT = "Test message ";

    private static final String TRANSPORT_URI = "tcp://localhost:0";

    private String url;

    final int prefetch = 10;

    BrokerService broker;

    @Test
    public void testFailoverConsumerDups() throws Exception {
        doTestFailoverConsumerDups(true);
    }

    @Test
    public void TestFailoverConsumerOutstandingSendTxIncomplete() throws Exception {
        doTestFailoverConsumerOutstandingSendTx(false);
    }

    @Test
    public void TestFailoverConsumerOutstandingSendTxComplete() throws Exception {
        doTestFailoverConsumerOutstandingSendTx(true);
    }

    @Test
    public void testRollbackFailoverConsumerTx() throws Exception {
        broker = createBroker(true);
        broker.start();
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory((("failover:(" + (url)) + ")"));
        cf.setConsumerFailoverRedeliveryWaitPeriod(10000);
        final ActiveMQConnection connection = ((ActiveMQConnection) (cf.createConnection()));
        connection.start();
        final Session producerSession = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final Queue destination = producerSession.createQueue(FailoverConsumerOutstandingCommitTest.QUEUE_NAME);
        final Session consumerSession = connection.createSession(true, SESSION_TRANSACTED);
        final MessageConsumer testConsumer = consumerSession.createConsumer(destination);
        Assert.assertNull("no message yet", testConsumer.receiveNoWait());
        produceMessage(producerSession, destination, 1);
        producerSession.close();
        // consume then rollback after restart
        Message msg = testConsumer.receive(5000);
        Assert.assertNotNull(msg);
        // restart with outstanding delivered message
        broker.stop();
        broker.waitUntilStopped();
        broker = createBroker(false, url);
        broker.start();
        consumerSession.rollback();
        // receive again
        msg = testConsumer.receive(10000);
        Assert.assertNotNull("got message again after rollback", msg);
        consumerSession.commit();
        // close before sweep
        consumerSession.close();
        msg = receiveMessage(cf, destination);
        Assert.assertNull("should be nothing left after commit", msg);
        connection.close();
    }
}

