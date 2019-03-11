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
package org.apache.activemq.transport.failover;


import Session.CLIENT_ACKNOWLEDGE;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQMessageTransformation;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerPluginSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ConsumerId;
import org.apache.activemq.command.ConsumerInfo;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// see https://issues.apache.org/activemq/browse/AMQ-2573
public class FailoverConsumerUnconsumedTest {
    private static final Logger LOG = LoggerFactory.getLogger(FailoverConsumerUnconsumedTest.class);

    private static final String QUEUE_NAME = "FailoverWithUnconsumed";

    private static final String TRANSPORT_URI = "tcp://localhost:0";

    private String url;

    final int prefetch = 10;

    BrokerService broker;

    @Test
    public void testFailoverConsumerDups() throws Exception {
        doTestFailoverConsumerDups(true);
    }

    @Test
    public void testFailoverConsumerDupsNoAdvisoryWatch() throws Exception {
        doTestFailoverConsumerDups(false);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFailoverClientAckMissingRedelivery() throws Exception {
        final int maxConsumers = 2;
        broker = createBroker(true);
        broker.setPlugins(new BrokerPlugin[]{ new BrokerPluginSupport() {
            int consumerCount;

            // broker is killed on x create consumer
            @Override
            public Subscription addConsumer(ConnectionContext context, final ConsumerInfo info) throws Exception {
                if ((++(consumerCount)) == maxConsumers) {
                    context.setDontSendReponse(true);
                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        public void run() {
                            FailoverConsumerUnconsumedTest.LOG.info(("Stopping broker on consumer: " + (info.getConsumerId())));
                            try {
                                broker.stop();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                return super.addConsumer(context, info);
            }
        } });
        broker.start();
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory((("failover:(" + (url)) + ")"));
        cf.setWatchTopicAdvisories(false);
        final ActiveMQConnection connection = ((ActiveMQConnection) (cf.createConnection()));
        connection.start();
        final Session consumerSession = connection.createSession(false, CLIENT_ACKNOWLEDGE);
        final Queue destination = consumerSession.createQueue((((FailoverConsumerUnconsumedTest.QUEUE_NAME) + "?jms.consumer.prefetch=") + (prefetch)));
        final Vector<FailoverConsumerUnconsumedTest.TestConsumer> testConsumers = new Vector<FailoverConsumerUnconsumedTest.TestConsumer>();
        FailoverConsumerUnconsumedTest.TestConsumer testConsumer = new FailoverConsumerUnconsumedTest.TestConsumer(consumerSession, destination, connection);
        testConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    FailoverConsumerUnconsumedTest.LOG.info(("onMessage:" + (message.getJMSMessageID())));
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        testConsumers.add(testConsumer);
        produceMessage(consumerSession, destination, (maxConsumers * (prefetch)));
        Assert.assertTrue("add messages are delivered", Wait.waitFor(new Wait.Condition() {
            public boolean isSatisified() throws Exception {
                int totalDelivered = 0;
                for (FailoverConsumerUnconsumedTest.TestConsumer testConsumer : testConsumers) {
                    long delivered = testConsumer.deliveredSize();
                    FailoverConsumerUnconsumedTest.LOG.info((((getConsumerId()) + " delivered: ") + delivered));
                    totalDelivered += delivered;
                }
                return totalDelivered == (maxConsumers * (prefetch));
            }
        }));
        final CountDownLatch shutdownConsumerAdded = new CountDownLatch(1);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            public void run() {
                try {
                    FailoverConsumerUnconsumedTest.LOG.info("add last consumer...");
                    FailoverConsumerUnconsumedTest.TestConsumer testConsumer = new FailoverConsumerUnconsumedTest.TestConsumer(consumerSession, destination, connection);
                    setMessageListener(new MessageListener() {
                        @Override
                        public void onMessage(Message message) {
                            try {
                                FailoverConsumerUnconsumedTest.LOG.info(("onMessage:" + (message.getJMSMessageID())));
                            } catch (JMSException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    testConsumers.add(testConsumer);
                    shutdownConsumerAdded.countDown();
                    FailoverConsumerUnconsumedTest.LOG.info("done add last consumer");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        // will be stopped by the plugin
        broker.waitUntilStopped();
        broker = createBroker(false, this.url);
        broker.start();
        Assert.assertTrue("consumer added through failover", shutdownConsumerAdded.await(30, TimeUnit.SECONDS));
        // each should again get prefetch messages - all unacked deliveries should be rolledback
        Assert.assertTrue("after restart all messages are re dispatched", Wait.waitFor(new Wait.Condition() {
            public boolean isSatisified() throws Exception {
                int totalDelivered = 0;
                for (FailoverConsumerUnconsumedTest.TestConsumer testConsumer : testConsumers) {
                    long delivered = testConsumer.deliveredSize();
                    FailoverConsumerUnconsumedTest.LOG.info((((getConsumerId()) + " delivered: ") + delivered));
                    totalDelivered += delivered;
                }
                return totalDelivered == (maxConsumers * (prefetch));
            }
        }));
        Assert.assertTrue("after restart each got prefetch amount", Wait.waitFor(new Wait.Condition() {
            public boolean isSatisified() throws Exception {
                for (FailoverConsumerUnconsumedTest.TestConsumer testConsumer : testConsumers) {
                    long delivered = testConsumer.deliveredSize();
                    FailoverConsumerUnconsumedTest.LOG.info((((getConsumerId()) + " delivered: ") + delivered));
                    if (delivered != (prefetch)) {
                        return false;
                    }
                }
                return true;
            }
        }));
        connection.close();
    }

    // allow access to unconsumedMessages
    class TestConsumer extends ActiveMQMessageConsumer {
        TestConsumer(Session consumerSession, Destination destination, ActiveMQConnection connection) throws Exception {
            super(((ActiveMQSession) (consumerSession)), new ConsumerId(new org.apache.activemq.command.SessionId(connection.getConnectionInfo().getConnectionId(), 1), FailoverConsumerUnconsumedTest.nextGen()), ActiveMQMessageTransformation.transformDestination(destination), null, "", prefetch, (-1), false, false, true, null);
        }

        public int unconsumedSize() {
            return unconsumedMessages.size();
        }

        public int deliveredSize() {
            return deliveredMessages.size();
        }
    }

    static long idGen = 100;
}

