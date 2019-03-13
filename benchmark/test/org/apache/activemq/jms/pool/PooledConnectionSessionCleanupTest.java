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
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;


public class PooledConnectionSessionCleanupTest extends JmsPoolTestSupport {
    protected ActiveMQConnectionFactory directConnFact;

    protected Connection directConn1;

    protected Connection directConn2;

    protected PooledConnectionFactory pooledConnFact;

    protected Connection pooledConn1;

    protected Connection pooledConn2;

    private final ActiveMQQueue queue = new ActiveMQQueue("ContendedQueue");

    private final int MESSAGE_COUNT = 50;

    @Test(timeout = 60000)
    public void testLingeringPooledSessionsHoldingPrefetchedMessages() throws Exception {
        produceMessages();
        Session pooledSession1 = pooledConn1.createSession(false, AUTO_ACKNOWLEDGE);
        pooledSession1.createConsumer(queue);
        final QueueViewMBean view = getProxyToQueue(queue.getPhysicalName());
        Assert.assertTrue("Should have all sent messages in flight:", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (view.getInFlightCount()) == (MESSAGE_COUNT);
            }
        }, TimeUnit.SECONDS.toMillis(20), TimeUnit.MILLISECONDS.toMillis(25)));
        // While all the message are in flight we should get anything on this consumer.
        Session session = directConn1.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(queue);
        Assert.assertNull(consumer.receive(1000));
        pooledConn1.close();
        Assert.assertTrue("Should have only one consumer now:", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (view.getSubscriptions().length) == 1;
            }
        }, TimeUnit.SECONDS.toMillis(20), TimeUnit.MILLISECONDS.toMillis(25)));
        // Now we'd expect that the message stuck in the prefetch of the pooled session's
        // consumer would be rerouted to the non-pooled session's consumer.
        Assert.assertNotNull(consumer.receive(10000));
    }

    @Test(timeout = 60000)
    public void testNonPooledConnectionCloseNotHoldingPrefetchedMessages() throws Exception {
        produceMessages();
        Session directSession = directConn2.createSession(false, AUTO_ACKNOWLEDGE);
        directSession.createConsumer(queue);
        final QueueViewMBean view = getProxyToQueue(queue.getPhysicalName());
        Assert.assertTrue("Should have all sent messages in flight:", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (view.getInFlightCount()) == (MESSAGE_COUNT);
            }
        }, TimeUnit.SECONDS.toMillis(20), TimeUnit.MILLISECONDS.toMillis(25)));
        // While all the message are in flight we should get anything on this consumer.
        Session session = directConn1.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(queue);
        Assert.assertNull(consumer.receive(1000));
        directConn2.close();
        Assert.assertTrue("Should have only one consumer now:", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (view.getSubscriptions().length) == 1;
            }
        }, TimeUnit.SECONDS.toMillis(20), TimeUnit.MILLISECONDS.toMillis(25)));
        // Now we'd expect that the message stuck in the prefetch of the first session's
        // consumer would be rerouted to the alternate session's consumer.
        Assert.assertNotNull(consumer.receive(10000));
    }
}

