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
package org.apache.activemq.statistics;


import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * This test shows Inflight Message sizes are correct for various acknowledgement modes.
 */
public abstract class AbstractInflightMessageSizeTest {
    protected BrokerService brokerService;

    protected Connection connection;

    protected String brokerUrlString;

    protected Session session;

    protected Destination dest;

    protected Destination amqDestination;

    protected MessageConsumer consumer;

    protected int prefetch = 100;

    protected boolean useTopicSubscriptionInflightStats;

    protected final int ackType;

    protected final boolean optimizeAcknowledge;

    protected final String destName = "testDest";

    public AbstractInflightMessageSizeTest(int ackType, boolean optimizeAcknowledge, boolean useTopicSubscriptionInflightStats) {
        this.ackType = ackType;
        this.optimizeAcknowledge = optimizeAcknowledge;
        this.useTopicSubscriptionInflightStats = useTopicSubscriptionInflightStats;
    }

    /**
     * Tests that inflight message size goes up and comes back down to 0 after
     * messages are consumed
     *
     * @throws javax.jms.JMSException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 31000)
    public void testInflightMessageSize() throws Exception {
        Assume.assumeTrue(useTopicSubscriptionInflightStats);
        final long size = sendMessages(10);
        Assert.assertTrue("Inflight message size should be greater than the content length sent", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getSubscription().getInFlightMessageSize()) > size;
            }
        }));
        receiveMessages(10);
        Assert.assertTrue("Inflight message size should be 0", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getSubscription().getInFlightMessageSize()) == 0;
            }
        }));
    }

    /**
     * Test that the in flight message size won't rise after prefetch is filled
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 31000)
    public void testInflightMessageSizePrefetchFilled() throws Exception {
        Assume.assumeTrue(useTopicSubscriptionInflightStats);
        final long size = sendMessages(prefetch);
        Assert.assertTrue("Inflight message size should be greater than content length", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getSubscription().getInFlightMessageSize()) > size;
            }
        }));
        final long inFlightSize = getSubscription().getInFlightMessageSize();
        sendMessages(10);
        // Prefetch has been filled, so the size should not change with 10 more messages
        Assert.assertEquals("Inflight message size should not change", inFlightSize, getSubscription().getInFlightMessageSize());
        receiveMessages(((prefetch) + 10));
        Assert.assertTrue("Inflight message size should be 0", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getSubscription().getInFlightMessageSize()) == 0;
            }
        }));
    }

    /**
     * Test that the in flight message size will still rise if prefetch is not filled
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 31000)
    public void testInflightMessageSizePrefetchNotFilled() throws Exception {
        Assume.assumeTrue(useTopicSubscriptionInflightStats);
        final long size = sendMessages(((prefetch) - 10));
        Assert.assertTrue("Inflight message size should be greater than content length", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getSubscription().getInFlightMessageSize()) > size;
            }
        }));
        // capture the inflight size and send 10 more messages
        final long inFlightSize = getSubscription().getInFlightMessageSize();
        sendMessages(10);
        // Prefetch has NOT been filled, so the size should rise with 10 more messages
        Assert.assertTrue("Inflight message size should be greater than previous inlight size", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getSubscription().getInFlightMessageSize()) > inFlightSize;
            }
        }));
        receiveMessages(prefetch);
        Assert.assertTrue("Inflight message size should be 0", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getSubscription().getInFlightMessageSize()) == 0;
            }
        }));
    }

    /**
     * Tests that inflight message size goes up and doesn't go down if receive is rolledback
     *
     * @throws javax.jms.JMSException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 31000)
    public void testInflightMessageSizeRollback() throws Exception {
        Assume.assumeTrue(useTopicSubscriptionInflightStats);
        Assume.assumeTrue(((ackType) == (ActiveMQSession.SESSION_TRANSACTED)));
        final long size = sendMessages(10);
        Assert.assertTrue("Inflight message size should be greater than the content length sent", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (getSubscription().getInFlightMessageSize()) > size;
            }
        }));
        long inFlightSize = getSubscription().getInFlightMessageSize();
        for (int i = 0; i < 10; i++) {
            consumer.receive();
        }
        session.rollback();
        Assert.assertEquals("Inflight message size should not change on rollback", inFlightSize, getSubscription().getInFlightMessageSize());
    }
}

