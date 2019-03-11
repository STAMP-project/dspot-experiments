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


import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Connection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.region.Destination;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test checks that KahaDB properly sets the new storeMessageSize statistic.
 *
 * AMQ-5748
 */
public abstract class AbstractMessageStoreSizeStatTest extends AbstractStoreStatTestSupport {
    protected static final Logger LOG = LoggerFactory.getLogger(AbstractMessageStoreSizeStatTest.class);

    protected BrokerService broker;

    protected URI brokerConnectURI;

    protected String defaultQueueName = "test.queue";

    protected String defaultTopicName = "test.topic";

    @Test(timeout = 60000)
    public void testMessageSize() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Destination dest = publishTestQueueMessages(200, publishedMessageSize);
        verifyStats(dest, 200, publishedMessageSize.get());
    }

    @Test(timeout = 60000)
    public void testMessageSizeAfterConsumption() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Destination dest = publishTestQueueMessages(200, publishedMessageSize);
        verifyStats(dest, 200, publishedMessageSize.get());
        consumeTestQueueMessages();
        verifyStats(dest, 0, 0);
    }

    @Test(timeout = 60000)
    public void testMessageSizeOneDurable() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        Destination dest = publishTestMessagesDurable(connection, new String[]{ "sub1" }, 200, 200, publishedMessageSize);
        // verify the count and size
        verifyStats(dest, 200, publishedMessageSize.get());
        // consume all messages
        consumeDurableTestMessages(connection, "sub1", 200, publishedMessageSize);
        // All messages should now be gone
        verifyStats(dest, 0, 0);
        connection.close();
    }

    @Test(timeout = 60000)
    public void testMessageSizeTwoDurables() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Connection connection = new ActiveMQConnectionFactory(brokerConnectURI).createConnection();
        connection.setClientID("clientId");
        connection.start();
        Destination dest = publishTestMessagesDurable(connection, new String[]{ "sub1", "sub2" }, 200, 200, publishedMessageSize);
        // verify the count and size
        verifyStats(dest, 200, publishedMessageSize.get());
        // consume messages just for sub1
        consumeDurableTestMessages(connection, "sub1", 200, publishedMessageSize);
        // There is still a durable that hasn't consumed so the messages should exist
        verifyStats(dest, 200, publishedMessageSize.get());
        connection.stop();
    }

    @Test
    public void testMessageSizeAfterDestinationDeletion() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Destination dest = publishTestQueueMessages(200, publishedMessageSize);
        verifyStats(dest, 200, publishedMessageSize.get());
        // check that the size is 0 after deletion
        broker.removeDestination(dest.getActiveMQDestination());
        verifyStats(dest, 0, 0);
    }

    @Test
    public void testQueueBrowserMessageSize() throws Exception {
        AtomicLong publishedMessageSize = new AtomicLong();
        Destination dest = publishTestQueueMessages(200, publishedMessageSize);
        browseTestQueueMessages(dest.getName());
        verifyStats(dest, 200, publishedMessageSize.get());
    }
}

