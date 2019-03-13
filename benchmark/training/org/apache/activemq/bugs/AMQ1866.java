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
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is a test case for the issue reported at:
 * https://issues.apache.org/activemq/browse/AMQ-1866
 *
 * If you have a JMS producer sending messages to multiple fast consumers and
 * one slow consumer, eventually all consumers will run as slow as
 * the slowest consumer.
 */
public class AMQ1866 extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(AMQ1866.ConsumerThread.class);

    private BrokerService brokerService;

    private ArrayList<Thread> threads = new ArrayList<Thread>();

    private final String ACTIVEMQ_BROKER_BIND = "tcp://localhost:0";

    private String ACTIVEMQ_BROKER_URI;

    AtomicBoolean shutdown = new AtomicBoolean();

    private ActiveMQQueue destination;

    public void testConsumerSlowDownPrefetch0() throws Exception {
        ACTIVEMQ_BROKER_URI = (ACTIVEMQ_BROKER_URI) + "?jms.prefetchPolicy.queuePrefetch=0";
        doTestConsumerSlowDown();
    }

    public void testConsumerSlowDownPrefetch10() throws Exception {
        ACTIVEMQ_BROKER_URI = (ACTIVEMQ_BROKER_URI) + "?jms.prefetchPolicy.queuePrefetch=10";
        doTestConsumerSlowDown();
    }

    public void testConsumerSlowDownDefaultPrefetch() throws Exception {
        doTestConsumerSlowDown();
    }

    public class ConsumerThread extends Thread {
        final AtomicLong counter = new AtomicLong();

        public ConsumerThread(String threadId) {
            super(threadId);
        }

        public void run() {
            Connection connection = null;
            try {
                AMQ1866.log.debug(((getName()) + ": is running"));
                ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVEMQ_BROKER_URI);
                factory.setDispatchAsync(true);
                connection = factory.createConnection();
                Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
                MessageConsumer consumer = session.createConsumer(destination);
                connection.start();
                while (!(shutdown.get())) {
                    TextMessage msg = ((TextMessage) (consumer.receive(1000)));
                    if (msg != null) {
                        int sleepingTime;
                        if (getName().equals("Consumer-1")) {
                            sleepingTime = 1000 * 1000;
                        } else {
                            sleepingTime = 1;
                        }
                        counter.incrementAndGet();
                        Thread.sleep(sleepingTime);
                    }
                } 
            } catch (Exception e) {
            } finally {
                AMQ1866.log.debug(((getName()) + ": is stopping"));
                try {
                    connection.close();
                } catch (Throwable e) {
                }
            }
        }
    }
}

