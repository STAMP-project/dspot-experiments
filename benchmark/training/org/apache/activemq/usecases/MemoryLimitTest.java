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
package org.apache.activemq.usecases;


import Session.CLIENT_ACKNOWLEDGE;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.TestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.ProducerThread;
import org.apache.activemq.util.Wait;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class MemoryLimitTest extends TestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(MemoryLimitTest.class);

    final byte[] payload = new byte[10 * 1024];// 10KB


    protected BrokerService broker;

    @Parameterized.Parameter
    public TestSupport.PersistenceAdapterChoice persistenceAdapterChoice;

    @Test(timeout = 640000)
    public void testCursorBatch() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?jms.prefetchPolicy.all=10");
        factory.setOptimizeAcknowledge(true);
        Connection conn = factory.createConnection();
        conn.start();
        Session sess = conn.createSession(false, CLIENT_ACKNOWLEDGE);
        Queue queue = sess.createQueue("STORE");
        final ProducerThread producer = new ProducerThread(sess, queue) {
            @Override
            protected Message createMessage(int i) throws Exception {
                BytesMessage bytesMessage = session.createBytesMessage();
                bytesMessage.writeBytes(payload);
                return bytesMessage;
            }
        };
        producer.setMessageCount(2000);
        producer.start();
        producer.join();
        Thread.sleep(1000);
        // assert we didn't break high watermark (70%) usage
        final Destination dest = broker.getDestination(((ActiveMQQueue) (queue)));
        MemoryLimitTest.LOG.info(("Destination usage: " + (dest.getMemoryUsage())));
        int percentUsage = dest.getMemoryUsage().getPercentUsage();
        assertTrue(("Should be less than 70% of limit but was: " + percentUsage), (percentUsage <= 71));
        MemoryLimitTest.LOG.info(("Broker usage: " + (broker.getSystemUsage().getMemoryUsage())));
        assertTrue(((broker.getSystemUsage().getMemoryUsage().getPercentUsage()) <= 71));
        // consume one message
        MessageConsumer consumer = sess.createConsumer(queue);
        Message msg = consumer.receive(5000);
        msg.acknowledge();
        assertTrue(("Should be less than 70% of limit but was: " + percentUsage), (percentUsage <= 71));
        MemoryLimitTest.LOG.info(("Broker usage: " + (broker.getSystemUsage().getMemoryUsage())));
        assertTrue(((broker.getSystemUsage().getMemoryUsage().getPercentUsage()) <= 91));
        // let's make sure we can consume all messages
        for (int i = 1; i < 2000; i++) {
            msg = consumer.receive(5000);
            if (msg == null) {
                dumpAllThreads("NoMessage");
            }
            assertNotNull(("Didn't receive message " + i), msg);
            msg.acknowledge();
        }
    }

    @Test(timeout = 120000)
    public void testMoveMessages() throws Exception {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?jms.prefetchPolicy.all=10");
        factory.setOptimizeAcknowledge(true);
        Connection conn = factory.createConnection();
        conn.start();
        Session sess = conn.createSession(false, CLIENT_ACKNOWLEDGE);
        Queue queue = sess.createQueue("IN");
        final byte[] payload = new byte[200 * 1024];// 200KB

        final int count = 4;
        final ProducerThread producer = new ProducerThread(sess, queue) {
            @Override
            protected Message createMessage(int i) throws Exception {
                BytesMessage bytesMessage = session.createBytesMessage();
                bytesMessage.writeBytes(payload);
                return bytesMessage;
            }
        };
        producer.setMessageCount(count);
        producer.start();
        producer.join();
        Thread.sleep(1000);
        final QueueViewMBean in = getProxyToQueue("IN");
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (in.getQueueSize()) == count;
            }
        });
        assertEquals("Messages not sent", count, in.getQueueSize());
        int moved = in.moveMatchingMessagesTo("", "OUT");
        assertEquals("Didn't move all messages", count, moved);
        final QueueViewMBean out = getProxyToQueue("OUT");
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (out.getQueueSize()) == count;
            }
        });
        assertEquals("Messages not moved", count, out.getQueueSize());
    }
}

