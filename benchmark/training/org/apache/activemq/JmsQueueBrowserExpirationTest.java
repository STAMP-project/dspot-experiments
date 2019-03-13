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
package org.apache.activemq;


import Session.AUTO_ACKNOWLEDGE;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test shows that when all messages are expired the QueueBrowser will
 * still finish properly and not hang indefinitely.  If a queue browser subscription
 * detects an expired message, it will tell the broker to expire the message but still
 * dispatch the message to the client as we want to get a snapshot in time.  This prevents
 * the problem of the browser enumeration returning true for hasMoreElements and then
 * hanging forever because all messages expired on dispatch.
 *
 * See: https://issues.apache.org/jira/browse/AMQ-5340
 *
 * <p>
 * This test is based on a test case submitted by Henno Vermeulen for AMQ-5340
 */
public class JmsQueueBrowserExpirationTest {
    private static final int MESSAGES_TO_SEND = 50;

    // Message expires after 1 second
    private static final long TTL = 1000;

    private static final Logger LOG = LoggerFactory.getLogger(JmsQueueBrowserExpirationTest.class);

    private BrokerService broker;

    private URI connectUri;

    private ActiveMQConnectionFactory factory;

    private final ActiveMQQueue queue = new ActiveMQQueue("TEST");

    @Test(timeout = 10000)
    public void testBrowsingExpiration() throws InterruptedException, JMSException {
        sendTestMessages();
        // Browse the queue.
        Connection browserConnection = factory.createConnection();
        browserConnection.start();
        int browsed = browse(queue, browserConnection);
        // The number of messages browsed should be equal to the number of
        // messages sent.
        Assert.assertEquals(JmsQueueBrowserExpirationTest.MESSAGES_TO_SEND, browsed);
        long begin = System.nanoTime();
        while (browsed != 0) {
            // Give JMS threads more opportunity to do their work.
            Thread.sleep(100);
            browsed = browse(queue, browserConnection);
            JmsQueueBrowserExpirationTest.LOG.info("[{}ms] found {}", TimeUnit.NANOSECONDS.toMillis(((System.nanoTime()) - begin)), browsed);
        } 
        JmsQueueBrowserExpirationTest.LOG.info("Finished");
        browserConnection.close();
    }

    @Test(timeout = 10000)
    public void testDoNotReceiveExpiredMessage() throws Exception {
        int WAIT_TIME = 1000;
        Connection connection = factory.createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue producerQueue = session.createQueue("MyTestQueue");
        MessageProducer producer = session.createProducer(producerQueue);
        producer.setTimeToLive(WAIT_TIME);
        TextMessage message = session.createTextMessage("Test message");
        producer.send(producerQueue, message);
        int count = getMessageCount(producerQueue, session);
        Assert.assertEquals(1, count);
        Thread.sleep((WAIT_TIME + 1000));
        count = getMessageCount(producerQueue, session);
        Assert.assertEquals(0, count);
        producer.close();
        session.close();
        connection.close();
    }
}

