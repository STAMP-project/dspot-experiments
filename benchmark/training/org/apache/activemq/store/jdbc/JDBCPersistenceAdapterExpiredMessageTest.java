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


import Session.AUTO_ACKNOWLEDGE;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TopicSubscriber;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.util.Wait;
import org.apache.activemq.util.Wait.Condition;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JDBCPersistenceAdapterExpiredMessageTest {
    private static final Logger LOG = LoggerFactory.getLogger(JDBCPersistenceAdapterExpiredMessageTest.class);

    @Rule
    public TemporaryFolder dataFileDir = new TemporaryFolder(new File("target"));

    protected BrokerService brokerService;

    private AtomicBoolean hasSpaceCalled = new AtomicBoolean();

    private int expireSize = 5;

    @Test
    public void testMaxExpirePageSize() throws Exception {
        final ActiveMQTopic topic = new ActiveMQTopic("test.topic");
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        factory.setClientID("clientId");
        Connection conn = factory.createConnection();
        conn.start();
        Session sess = conn.createSession(false, AUTO_ACKNOWLEDGE);
        TopicSubscriber sub = sess.createDurableSubscriber(topic, "sub1");
        sub.close();
        MessageProducer producer = sess.createProducer(topic);
        producer.setTimeToLive(1000);
        for (int i = 0; i < 50; i++) {
            producer.send(sess.createTextMessage(("test message: " + i)));
        }
        // There should be exactly 5 messages expired because the limit was hit and it stopped
        // The expire messages period is 5 seconds which should give enough time for this assertion
        // to pass before expiring more messages
        Assert.assertTrue(Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                long expired = brokerService.getDestination(topic).getDestinationStatistics().getExpired().getCount();
                return (expired == (expireSize)) && (hasSpaceCalled.get());
            }
        }, 15000, 1000));
    }

    @Test
    public void testExpiredAfterCacheExhausted() throws Exception {
        final ActiveMQQueue queue = new ActiveMQQueue("test.q");
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost");
        factory.setWatchTopicAdvisories(false);
        Connection conn = factory.createConnection();
        conn.start();
        Session sess = conn.createSession(false, AUTO_ACKNOWLEDGE);
        MessageProducer producer = sess.createProducer(queue);
        producer.setTimeToLive(1000);
        String payLoad = new String(new byte[16 * 1024]);
        final int numMessages = 500;
        for (int i = 0; i < numMessages; i++) {
            producer.send(sess.createTextMessage(("test message: " + payLoad)));
        }
        Assert.assertTrue(Wait.waitFor(new Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                long expired = brokerService.getDestination(queue).getDestinationStatistics().getExpired().getCount();
                JDBCPersistenceAdapterExpiredMessageTest.LOG.info(("Expired: " + expired));
                return expired == numMessages;
            }
        }, 15000, 1000));
    }
}

