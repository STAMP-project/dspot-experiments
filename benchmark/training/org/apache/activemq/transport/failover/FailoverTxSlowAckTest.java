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


import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.org.apache.activemq.command.Message;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.BrokerPluginSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.transaction.Synchronization;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FailoverTxSlowAckTest {
    private static final Logger LOG = LoggerFactory.getLogger(FailoverTxSlowAckTest.class);

    private static final String QUEUE_IN = "IN";

    private static final String QUEUE_OUT = "OUT";

    private static final String MESSAGE_TEXT = "Test message ";

    private static final String TRANSPORT_URI = "tcp://localhost:0";

    private String url;

    final int prefetch = 1;

    BrokerService broker;

    @Test
    public void testFailoverDuringAckRollsback() throws Exception {
        broker = createBroker(true);
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        broker.setPlugins(new BrokerPlugin[]{ new BrokerPluginSupport() {
            int sendCount = 0;

            @Override
            public void send(ProducerBrokerExchange producerExchange, org.apache.activemq.command.Message messageSend) throws Exception {
                super.send(producerExchange, messageSend);
                (sendCount)++;
                if ((sendCount) > 1) {
                    // need new thread b/c we have the service write lock
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            FailoverTxSlowAckTest.LOG.info("Stopping broker before commit...");
                            try {
                                broker.stop();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        } });
        broker.start();
        url = broker.getTransportConnectors().get(0).getConnectUri().toString();
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory((("failover:(" + (url)) + ")"));
        cf.setWatchTopicAdvisories(false);
        cf.setDispatchAsync(false);
        final ActiveMQConnection connection = ((ActiveMQConnection) (cf.createConnection()));
        connection.start();
        final Session producerSession = connection.createSession(false, AUTO_ACKNOWLEDGE);
        final Queue in = producerSession.createQueue((((FailoverTxSlowAckTest.QUEUE_IN) + "?consumer.prefetchSize=") + (prefetch)));
        final Session consumerSession = connection.createSession(true, AUTO_ACKNOWLEDGE);
        final Queue out = consumerSession.createQueue(FailoverTxSlowAckTest.QUEUE_OUT);
        final MessageProducer consumerProducer = consumerSession.createProducer(out);
        final CountDownLatch commitDoneLatch = new CountDownLatch(1);
        final CountDownLatch messagesReceived = new CountDownLatch(1);
        final CountDownLatch brokerDisconnectedLatch = new CountDownLatch(1);
        final AtomicInteger receivedCount = new AtomicInteger();
        final AtomicBoolean gotDisconnect = new AtomicBoolean();
        final AtomicBoolean gotReconnected = new AtomicBoolean();
        final MessageConsumer testConsumer = consumerSession.createConsumer(in);
        testConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                FailoverTxSlowAckTest.LOG.info("consume one and commit");
                Assert.assertNotNull("got message", message);
                receivedCount.incrementAndGet();
                messagesReceived.countDown();
                try {
                    // ensure message expires broker side so it won't get redelivered
                    TimeUnit.SECONDS.sleep(1);
                    consumerProducer.send(message);
                    // hack to block the transaction completion
                    // ensure session does not get to send commit message before failover reconnect
                    // if the commit message is in progress during failover we get rollback via the state
                    // tracker
                    getTransactionContext().addSynchronization(new Synchronization() {
                        @Override
                        public void beforeEnd() throws Exception {
                            FailoverTxSlowAckTest.LOG.info("waiting for failover reconnect");
                            gotDisconnect.set(Wait.waitFor(new Wait.Condition() {
                                @Override
                                public boolean isSatisified() throws Exception {
                                    return !(getConnection().getTransport().isConnected());
                                }
                            }));
                            // connect down to trigger reconnect
                            brokerDisconnectedLatch.countDown();
                            FailoverTxSlowAckTest.LOG.info("got disconnect");
                            gotReconnected.set(Wait.waitFor(new Wait.Condition() {
                                @Override
                                public boolean isSatisified() throws Exception {
                                    return getConnection().getTransport().isConnected();
                                }
                            }));
                            FailoverTxSlowAckTest.LOG.info("got failover reconnect");
                        }
                    });
                    consumerSession.commit();
                    FailoverTxSlowAckTest.LOG.info("done commit");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    commitDoneLatch.countDown();
                }
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                FailoverTxSlowAckTest.LOG.info("producer started");
                try {
                    produceMessage(producerSession, in, 1);
                } catch (javax.jms SessionClosedExpectedOnShutdown) {
                } catch (JMSException e) {
                    e.printStackTrace();
                    Assert.fail(("unexpceted ex on producer: " + e));
                }
                FailoverTxSlowAckTest.LOG.info("producer done");
            }
        });
        // will be stopped by the plugin on TX ack
        broker.waitUntilStopped();
        // await for listener to detect disconnect
        brokerDisconnectedLatch.await();
        broker = createBroker(false, url);
        broker.start();
        Assert.assertTrue("message was recieved ", messagesReceived.await(20, TimeUnit.SECONDS));
        Assert.assertTrue("tx complete through failover", commitDoneLatch.await(40, TimeUnit.SECONDS));
        Assert.assertEquals("one delivery", 1, receivedCount.get());
        Assert.assertTrue("got disconnect/reconnect", gotDisconnect.get());
        Assert.assertTrue("got reconnect", gotReconnected.get());
        Assert.assertNull("No message produced", receiveMessage(cf, out));
    }
}

