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
/**
 * A StoreBasedCursorTest
 */
package org.apache.activemq.broker.region.cursors;


import DeliveryMode.NON_PERSISTENT;
import DeliveryMode.PERSISTENT;
import javax.jms.Connection;
import javax.jms.Queue;
import javax.jms.Session;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;


public class StoreBasedCursorTest extends TestCase {
    protected String bindAddress = "tcp://localhost:60706";

    BrokerService broker;

    ActiveMQConnectionFactory factory;

    Connection connection;

    Session session;

    Queue queue;

    int messageSize = 1024;

    // actual message is messageSize*2, and 4*MessageSize would allow 2 messages be delivered, but the flush of the cache is async so the flush
    // triggered on 2nd message maxing out the usage may not be in effect for the 3rd message to succeed. Making the memory usage more lenient
    // gives the usageChange listener in the cursor an opportunity to kick in.
    int memoryLimit = 12 * (messageSize);

    // use QueueStorePrefetch
    public void testTwoUsageEqualPersistent() throws Exception {
        configureBroker(memoryLimit, memoryLimit);
        sendMessages(PERSISTENT);
    }

    public void testUseCachePersistent() throws Exception {
        int limit = (memoryLimit) / 2;
        configureBroker(limit, memoryLimit);
        sendMessages(PERSISTENT);
    }

    public void testMemoryUsageLowPersistent() throws Exception {
        configureBroker(memoryLimit, (10 * (memoryLimit)));
        sendMessages(PERSISTENT);
    }

    // use FilePendingMessageCursor
    public void testTwoUsageEqualNonPersistent() throws Exception {
        configureBroker(memoryLimit, memoryLimit);
        sendMessages(NON_PERSISTENT);
    }

    public void testMemoryUsageLowNonPersistent() throws Exception {
        configureBroker(memoryLimit, (10 * (memoryLimit)));
        sendMessages(NON_PERSISTENT);
    }
}

