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
package org.apache.activemq.pool;


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PooledConsumerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PooledConsumerTest.class);

    public static final String USERNAME = "test";

    public static final String PASSWORD = "test";

    private static final ActiveMQQueue QUEUE = new ActiveMQQueue("TEST");

    BrokerService brokerService;

    class PooledConsumer implements MessageListener {
        private ConnectionFactory factory;

        private Connection connection;

        public boolean done = false;

        public PooledConsumer(String url) throws JMSException {
            org.apache.activemq.pool.PooledConnectionFactory factory = new org.apache.activemq.pool.PooledConnectionFactory(url);
            factory.setMaxConnections(5);
            factory.setIdleTimeout(0);
            this.factory = factory;
            init();
        }

        private void init() throws JMSException {
            if ((connection) != null) {
                close();
            }
            connection = factory.createConnection(PooledConsumerTest.USERNAME, PooledConsumerTest.PASSWORD);
            connection.start();
        }

        public void listen() {
            Session session = null;
            MessageConsumer consumer = null;
            boolean success = true;
            while (!(done)) {
                try {
                    session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                    consumer = session.createConsumer(PooledConsumerTest.QUEUE);
                    onMessage(consumer.receive());
                    success = true;
                } catch (JMSException e) {
                    PooledConsumerTest.LOGGER.info(e.getMessage());
                    success = false;
                } finally {
                    try {
                        if (consumer != null)
                            consumer.close();

                        if (session != null)
                            session.close();

                        if (!success)
                            init();

                    } catch (JMSException ignore) {
                        ignore.printStackTrace();
                    }
                }
                sleep(50);
            } 
        }

        private void sleep(long milliseconds) {
            try {
                TimeUnit.MILLISECONDS.sleep(milliseconds);
            } catch (InterruptedException e) {
            }
        }

        @Override
        public void onMessage(Message message) {
            if (message != null) {
                TextMessage textMessage = ((TextMessage) (message));
                try {
                    String response = textMessage.getText();
                    PooledConsumerTest.LOGGER.info("Received: '{}'", response);
                } catch (Exception e) {
                    PooledConsumerTest.LOGGER.error(e.getMessage(), e);
                }
            }
        }

        public void close() {
            try {
                if ((connection) != null) {
                    connection.close();
                }
            } catch (JMSException e) {
            }
        }

        public void done() {
            done = true;
            close();
        }
    }

    @Test
    public void testFailedConsumerNotRetainedByFailover() throws Exception {
        startBroker("test", "tcp://0.0.0.0:0");
        String url = brokerService.getTransportConnectorByScheme("tcp").getPublishableConnectString();
        final PooledConsumerTest.PooledConsumer consumer = new PooledConsumerTest.PooledConsumer((("failover:(" + (brokerService.getTransportConnectorByScheme("tcp").getPublishableConnectString())) + ")?jms.watchTopicAdvisories=false"));
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                consumer.listen();
            }
        });
        Assert.assertTrue("5 connectons - pool fils up", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 5 == (brokerService.getTransportConnectorByScheme("tcp").getConnections().size());
            }
        }));
        stopBroker();
        // with perms
        startBroker("users", url);
        Assert.assertTrue("5 reconnections from the pool", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return 5 == (brokerService.getTransportConnectorByScheme("tcp").getConnections().size());
            }
        }));
        Assert.assertEquals("one consumer", 1, brokerService.getRegionBroker().getDestinationMap().get(PooledConsumerTest.QUEUE).getConsumers().size());
        consumer.done();
        executorService.shutdownNow();
    }
}

