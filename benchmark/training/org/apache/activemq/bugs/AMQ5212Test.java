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
package org.apache.activemq.bugs;


import Session.AUTO_ACKNOWLEDGE;
import Session.CLIENT_ACKNOWLEDGE;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.org.apache.activemq.command.Message;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageProducer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.MutableBrokerFilter;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AMQ5212Test {
    BrokerService brokerService;

    @Parameterized.Parameter(0)
    public boolean concurrentStoreAndDispatchQ = true;

    @Test
    public void verifyDuplicateSuppressionWithConsumer() throws Exception {
        doVerifyDuplicateSuppression(100, 100, true);
    }

    @Test
    public void verifyDuplicateSuppression() throws Exception {
        doVerifyDuplicateSuppression(100, 100, false);
    }

    @Test
    public void verifyConsumptionOnDuplicate() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerService.getTransportConnectors().get(0).getPublishableConnectString());
        connectionFactory.setCopyMessageOnSend(false);
        connectionFactory.setWatchTopicAdvisories(false);
        ActiveMQConnection activeMQConnection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        activeMQConnection.start();
        ActiveMQSession activeMQSession = ((ActiveMQSession) (activeMQConnection.createSession(false, AUTO_ACKNOWLEDGE)));
        ActiveMQQueue dest = new ActiveMQQueue("Q");
        ActiveMQMessageProducer activeMQMessageProducer = ((ActiveMQMessageProducer) (activeMQSession.createProducer(dest)));
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setDestination(dest);
        activeMQMessageProducer.send(message, null);
        // send a duplicate
        activeMQConnection.syncSendPacket(message);
        activeMQConnection.close();
        // verify original can be consumed after restart
        brokerService.stop();
        brokerService.start(false);
        connectionFactory = new ActiveMQConnectionFactory(brokerService.getTransportConnectors().get(0).getPublishableConnectString());
        connectionFactory.setCopyMessageOnSend(false);
        connectionFactory.setWatchTopicAdvisories(false);
        activeMQConnection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        activeMQConnection.start();
        activeMQSession = ((ActiveMQSession) (activeMQConnection.createSession(false, AUTO_ACKNOWLEDGE)));
        MessageConsumer messageConsumer = activeMQSession.createConsumer(dest);
        Message received = messageConsumer.receive(4000);
        Assert.assertNotNull("Got message", received);
        Assert.assertEquals("match", message.getJMSMessageID(), received.getJMSMessageID());
        activeMQConnection.close();
    }

    @Test
    public void verifyClientAckConsumptionOnDuplicate() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerService.getTransportConnectors().get(0).getPublishableConnectString());
        connectionFactory.setCopyMessageOnSend(false);
        connectionFactory.setWatchTopicAdvisories(false);
        ActiveMQConnection activeMQConnection = ((ActiveMQConnection) (connectionFactory.createConnection()));
        activeMQConnection.start();
        ActiveMQSession activeMQSession = ((ActiveMQSession) (activeMQConnection.createSession(false, CLIENT_ACKNOWLEDGE)));
        ActiveMQQueue dest = new ActiveMQQueue("Q");
        MessageConsumer messageConsumer = activeMQSession.createConsumer(dest);
        ActiveMQMessageProducer activeMQMessageProducer = ((ActiveMQMessageProducer) (activeMQSession.createProducer(dest)));
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setDestination(dest);
        activeMQMessageProducer.send(message, null);
        // send a duplicate
        activeMQConnection.syncSendPacket(message);
        Message received = messageConsumer.receive(4000);
        Assert.assertNotNull("Got message", received);
        Assert.assertEquals("match", message.getJMSMessageID(), received.getJMSMessageID());
        messageConsumer.close();
        messageConsumer = activeMQSession.createConsumer(dest);
        received = messageConsumer.receive(4000);
        Assert.assertNotNull("Got message", received);
        Assert.assertEquals("match", message.getJMSMessageID(), received.getJMSMessageID());
        received.acknowledge();
        activeMQConnection.close();
    }

    @Test
    public void verifyProducerAudit() throws Exception {
        MutableBrokerFilter filter = ((MutableBrokerFilter) (brokerService.getBroker().getAdaptor(MutableBrokerFilter.class)));
        filter.setNext(new MutableBrokerFilter(filter.getNext()) {
            @Override
            public void send(ProducerBrokerExchange producerExchange, org.apache.activemq.command.Message messageSend) throws Exception {
                super.send(producerExchange, messageSend);
                Object seq = messageSend.getProperty("seq");
                if (seq instanceof Integer) {
                    if ((((((Integer) (seq)).intValue()) % 200) == 0) && ((producerExchange.getConnectionContext().getConnection()) != null)) {
                        producerExchange.getConnectionContext().setDontSendReponse(true);
                        producerExchange.getConnectionContext().getConnection().serviceException(new IOException("force reconnect"));
                    }
                }
            }
        });
        final AtomicInteger received = new AtomicInteger(0);
        final ActiveMQQueue dest = new ActiveMQQueue("Q");
        final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(("failover://" + (brokerService.getTransportConnectors().get(0).getPublishableConnectString())));
        connectionFactory.setCopyMessageOnSend(false);
        connectionFactory.setWatchTopicAdvisories(false);
        final int numConsumers = 40;
        ExecutorService executorService = Executors.newCachedThreadPool();
        final CountDownLatch consumerStarted = new CountDownLatch(numConsumers);
        final ConcurrentLinkedQueue<ActiveMQConnection> connectionList = new ConcurrentLinkedQueue<ActiveMQConnection>();
        for (int i = 0; i < numConsumers; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ActiveMQConnection activeMQConnection = ((ActiveMQConnection) (connectionFactory.createConnection()));
                        activeMQConnection.getPrefetchPolicy().setAll(0);
                        activeMQConnection.start();
                        connectionList.add(activeMQConnection);
                        ActiveMQSession activeMQSession = ((ActiveMQSession) (activeMQConnection.createSession(false, AUTO_ACKNOWLEDGE)));
                        MessageConsumer messageConsumer = activeMQSession.createConsumer(dest);
                        consumerStarted.countDown();
                        while (true) {
                            if ((messageConsumer.receive(500)) != null) {
                                received.incrementAndGet();
                            }
                        } 
                    } catch (javax.jms expected) {
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            });
        }
        final String payload = new String(new byte[8 * 1024]);
        final int totalToProduce = 5000;
        final AtomicInteger toSend = new AtomicInteger(totalToProduce);
        final int numProducers = 10;
        final CountDownLatch producerDone = new CountDownLatch(numProducers);
        for (int i = 0; i < numProducers; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ActiveMQConnection activeMQConnectionP = ((ActiveMQConnection) (connectionFactory.createConnection()));
                        activeMQConnectionP.start();
                        ActiveMQSession activeMQSessionP = ((ActiveMQSession) (activeMQConnectionP.createSession(false, AUTO_ACKNOWLEDGE)));
                        ActiveMQMessageProducer activeMQMessageProducer = ((ActiveMQMessageProducer) (activeMQSessionP.createProducer(dest)));
                        int seq = 0;
                        while ((seq = toSend.decrementAndGet()) >= 0) {
                            ActiveMQTextMessage message = new ActiveMQTextMessage();
                            message.setText(payload);
                            message.setIntProperty("seq", seq);
                            activeMQMessageProducer.send(message);
                        } 
                        activeMQConnectionP.close();
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    } finally {
                        producerDone.countDown();
                    }
                }
            });
        }
        consumerStarted.await(10, TimeUnit.MINUTES);
        producerDone.await(10, TimeUnit.MINUTES);
        for (ActiveMQConnection c : connectionList) {
            c.close();
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
        Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                return (brokerService.getAdminView().getTotalEnqueueCount()) >= totalToProduce;
            }
        });
        Assert.assertEquals("total enqueue as expected, nothing added to dlq", totalToProduce, brokerService.getAdminView().getTotalEnqueueCount());
    }
}

