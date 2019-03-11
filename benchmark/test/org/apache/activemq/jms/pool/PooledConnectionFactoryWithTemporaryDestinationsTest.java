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
import javax.jms.Connection;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PooledConnectionFactoryWithTemporaryDestinationsTest extends JmsPoolTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(PooledConnectionFactoryWithTemporaryDestinationsTest.class);

    private ActiveMQConnectionFactory factory;

    private PooledConnectionFactory pooledFactory;

    @Test(timeout = 60000)
    public void testTemporaryQueueWithMultipleConnectionUsers() throws Exception {
        Connection pooledConnection = null;
        Connection pooledConnection2 = null;
        Session session = null;
        Session session2 = null;
        Queue tempQueue = null;
        Queue normalQueue = null;
        pooledConnection = pooledFactory.createConnection();
        session = pooledConnection.createSession(false, AUTO_ACKNOWLEDGE);
        tempQueue = session.createTemporaryQueue();
        PooledConnectionFactoryWithTemporaryDestinationsTest.LOG.info(("Created queue named: " + (tempQueue.getQueueName())));
        Assert.assertEquals(1, countBrokerTemporaryQueues());
        pooledConnection2 = pooledFactory.createConnection();
        session2 = pooledConnection2.createSession(false, AUTO_ACKNOWLEDGE);
        normalQueue = session2.createQueue("queue:FOO.TEST");
        PooledConnectionFactoryWithTemporaryDestinationsTest.LOG.info(("Created queue named: " + (normalQueue.getQueueName())));
        // didn't create a temp queue on pooledConnection2 so we should still have a temp queue
        pooledConnection2.close();
        Assert.assertEquals(1, countBrokerTemporaryQueues());
        // after closing pooledConnection, where we created the temp queue, there should
        // be no temp queues left
        pooledConnection.close();
        Assert.assertEquals(0, countBrokerTemporaryQueues());
    }

    @Test(timeout = 60000)
    public void testTemporaryQueueLeakAfterConnectionClose() throws Exception {
        Connection pooledConnection = null;
        Session session = null;
        Queue tempQueue = null;
        for (int i = 0; i < 2; i++) {
            pooledConnection = pooledFactory.createConnection();
            session = pooledConnection.createSession(false, AUTO_ACKNOWLEDGE);
            tempQueue = session.createTemporaryQueue();
            PooledConnectionFactoryWithTemporaryDestinationsTest.LOG.info(("Created queue named: " + (tempQueue.getQueueName())));
            pooledConnection.close();
        }
        Assert.assertEquals(0, countBrokerTemporaryQueues());
    }

    @Test(timeout = 60000)
    public void testTemporaryTopicLeakAfterConnectionClose() throws Exception {
        Connection pooledConnection = null;
        Session session = null;
        Topic tempTopic = null;
        for (int i = 0; i < 2; i++) {
            pooledConnection = pooledFactory.createConnection();
            session = pooledConnection.createSession(false, AUTO_ACKNOWLEDGE);
            tempTopic = session.createTemporaryTopic();
            PooledConnectionFactoryWithTemporaryDestinationsTest.LOG.info(("Created topic named: " + (tempTopic.getTopicName())));
            pooledConnection.close();
        }
        Assert.assertEquals(0, countBrokerTemporaryTopics());
    }
}

