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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.TestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerView;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.util.Wait;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class AMQ2584Test extends TestSupport {
    static final Logger LOG = LoggerFactory.getLogger(AMQ2584Test.class);

    BrokerService broker = null;

    ActiveMQTopic topic;

    ActiveMQConnection consumerConnection = null;

    ActiveMQConnection producerConnection = null;

    Session producerSession;

    MessageProducer producer;

    final int minPercentUsageForStore = 3;

    String data;

    private final TestSupport.PersistenceAdapterChoice persistenceAdapterChoice;

    public AMQ2584Test(TestSupport.PersistenceAdapterChoice choice) {
        this.persistenceAdapterChoice = choice;
    }

    @Test(timeout = 120000)
    public void testSize() throws Exception {
        int messages = 1000;
        CountDownLatch redeliveryConsumerLatch = new CountDownLatch((messages * 3));
        openConsumer(redeliveryConsumerLatch);
        assertEquals(0, broker.getAdminView().getStorePercentUsage());
        for (int i = 0; i < messages; i++) {
            sendMessage(false);
        }
        final BrokerView brokerView = broker.getAdminView();
        broker.getSystemUsage().getStoreUsage().isFull();
        AMQ2584Test.LOG.info(("store percent usage: " + (brokerView.getStorePercentUsage())));
        int storePercentUsage = broker.getAdminView().getStorePercentUsage();
        assertTrue("some store in use", (storePercentUsage > (minPercentUsageForStore)));
        assertTrue("redelivery consumer got all it needs", redeliveryConsumerLatch.await(60, TimeUnit.SECONDS));
        closeConsumer();
        // consume from DLQ
        final CountDownLatch received = new CountDownLatch(messages);
        consumerConnection = ((ActiveMQConnection) (createConnection()));
        Session dlqSession = consumerConnection.createSession(false, AUTO_ACKNOWLEDGE);
        MessageConsumer dlqConsumer = dlqSession.createConsumer(new ActiveMQQueue("ActiveMQ.DLQ"));
        dlqConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (((received.getCount()) % 500) == 0) {
                    AMQ2584Test.LOG.info(("remaining on DLQ: " + (received.getCount())));
                }
                received.countDown();
            }
        });
        consumerConnection.start();
        assertTrue("Not all messages reached the DLQ", received.await(60, TimeUnit.SECONDS));
        assertTrue("Store usage exceeds expected usage", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                broker.getSystemUsage().getStoreUsage().isFull();
                AMQ2584Test.LOG.info(("store precent usage: " + (brokerView.getStorePercentUsage())));
                return (broker.getAdminView().getStorePercentUsage()) < (minPercentUsageForStore);
            }
        }));
        closeConsumer();
    }
}

