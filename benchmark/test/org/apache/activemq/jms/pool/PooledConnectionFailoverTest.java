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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Assert;
import org.junit.Test;


public class PooledConnectionFailoverTest extends JmsPoolTestSupport {
    protected ActiveMQConnectionFactory directConnFact;

    protected PooledConnectionFactory pooledConnFact;

    @Test
    public void testConnectionFailures() throws Exception {
        final CountDownLatch failed = new CountDownLatch(1);
        Connection connection = pooledConnFact.createConnection();
        JmsPoolTestSupport.LOG.info("Fetched new connection from the pool: {}", connection);
        connection.setExceptionListener(new ExceptionListener() {
            @Override
            public void onException(JMSException exception) {
                JmsPoolTestSupport.LOG.info("Pooled Connection failed");
                failed.countDown();
            }
        });
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(getTestName());
        MessageProducer producer = session.createProducer(queue);
        brokerService.stop();
        Assert.assertTrue(failed.await(15, TimeUnit.SECONDS));
        createBroker();
        try {
            producer.send(session.createMessage());
            Assert.fail("Should be disconnected");
        } catch (JMSException ex) {
            JmsPoolTestSupport.LOG.info("Producer failed as expected: {}", ex.getMessage());
        }
        Connection connection2 = pooledConnFact.createConnection();
        Assert.assertNotSame(connection, connection2);
        JmsPoolTestSupport.LOG.info("Fetched new connection from the pool: {}", connection2);
        session = connection2.createSession(false, AUTO_ACKNOWLEDGE);
        connection2.close();
        pooledConnFact.stop();
    }
}

