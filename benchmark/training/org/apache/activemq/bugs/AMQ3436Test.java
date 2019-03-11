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


import DeliveryMode.PERSISTENT;
import Session.SESSION_TRANSACTED;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.store.PersistenceAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ3436Test {
    protected static final Logger LOG = LoggerFactory.getLogger(AMQ3436Test.class);

    private BrokerService broker;

    private PersistenceAdapter adapter;

    private boolean useCache = true;

    private boolean prioritizeMessages = true;

    @Test
    public void testPriorityWhenConsumerCreatedBeforeProduction() throws Exception {
        int messageCount = 200;
        URI failoverUri = new URI("vm://priorityTest?jms.prefetchPolicy.all=1");
        ActiveMQQueue dest = new ActiveMQQueue("TEST?consumer.dispatchAsync=false");
        ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(failoverUri);
        cf.setDispatchAsync(false);
        // Create producer
        ActiveMQConnection producerConnection = ((ActiveMQConnection) (cf.createConnection()));
        producerConnection.setMessagePrioritySupported(true);
        producerConnection.start();
        final Session producerSession = producerConnection.createSession(true, SESSION_TRANSACTED);
        MessageProducer producer = producerSession.createProducer(dest);
        ActiveMQMessageConsumer consumer;
        // Create consumer on separate connection
        ActiveMQConnection consumerConnection = ((ActiveMQConnection) (cf.createConnection()));
        consumerConnection.setMessagePrioritySupported(true);
        consumerConnection.start();
        final ActiveMQSession consumerSession = ((ActiveMQSession) (consumerConnection.createSession(true, SESSION_TRANSACTED)));
        consumer = ((ActiveMQMessageConsumer) (consumerSession.createConsumer(dest)));
        // Produce X number of messages with a session commit after each message
        Random random = new Random();
        for (int i = 0; i < messageCount; ++i) {
            Message message = producerSession.createTextMessage(("Test message #" + i));
            producer.send(message, PERSISTENT, random.nextInt(10), (45 * 1000));
            producerSession.commit();
        }
        producer.close();
        // ***************************************************
        // If we create the consumer here instead of above, the
        // the messages will be consumed in priority order
        // ***************************************************
        // consumer = (ActiveMQMessageConsumer) consumerSession.createConsumer(dest);
        // Consume all of the messages we produce using a listener.
        // Don't exit until we get all the messages.
        final CountDownLatch latch = new CountDownLatch(messageCount);
        final StringBuffer failureMessage = new StringBuffer();
        consumer.setMessageListener(new MessageListener() {
            int lowestPrioritySeen = 10;

            boolean firstMessage = true;

            @Override
            public void onMessage(Message msg) {
                try {
                    int currentPriority = msg.getJMSPriority();
                    AMQ3436Test.LOG.debug(((currentPriority + "<=") + (lowestPrioritySeen)));
                    // Ignore the first message priority since it is prefetched
                    // and is out of order by design
                    if ((firstMessage) == true) {
                        firstMessage = false;
                        AMQ3436Test.LOG.debug("Ignoring first message since it was prefetched");
                    } else {
                        // Verify that we never see a priority higher than the
                        // lowest
                        // priority seen
                        if ((lowestPrioritySeen) > currentPriority) {
                            lowestPrioritySeen = currentPriority;
                        }
                        if ((lowestPrioritySeen) < currentPriority) {
                            failureMessage.append(((((("Incorrect priority seen (Lowest Priority = " + (lowestPrioritySeen)) + " Current Priority = ") + currentPriority) + ")") + (System.getProperty("line.separator"))));
                        }
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                    AMQ3436Test.LOG.debug(("Messages remaining = " + (latch.getCount())));
                }
            }
        });
        latch.await();
        consumer.close();
        // Cleanup producer resources
        producerSession.close();
        producerConnection.stop();
        producerConnection.close();
        // Cleanup consumer resources
        consumerSession.close();
        consumerConnection.stop();
        consumerConnection.close();
        // Report the failure if found
        if ((failureMessage.length()) > 0) {
            Assert.fail(failureMessage.toString());
        }
    }
}

