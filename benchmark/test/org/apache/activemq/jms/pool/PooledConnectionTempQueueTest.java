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
import java.util.concurrent.Executors;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PooledConnectionTempQueueTest extends JmsPoolTestSupport {
    private final Logger LOG = LoggerFactory.getLogger(PooledConnectionTempQueueTest.class);

    protected static final String SERVICE_QUEUE = "queue1";

    @Test(timeout = 60000)
    public void testTempQueueIssue() throws InterruptedException, JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false&broker.useJmx=false");
        final PooledConnectionFactory cf = new PooledConnectionFactory();
        cf.setConnectionFactory(factory);
        Connection connection = cf.createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        LOG.info("First connection was {}", connection);
        // This order seems to matter to reproduce the issue
        connection.close();
        session.close();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    receiveAndRespondWithMessageIdAsCorrelationId(cf, PooledConnectionTempQueueTest.SERVICE_QUEUE);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        sendWithReplyToTemp(cf, PooledConnectionTempQueueTest.SERVICE_QUEUE);
        cf.stop();
    }
}

