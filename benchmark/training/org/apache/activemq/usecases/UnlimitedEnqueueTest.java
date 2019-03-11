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
package org.apache.activemq.usecases;


import DeliveryMode.PERSISTENT;
import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ResourceAllocationException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.Wait;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UnlimitedEnqueueTest {
    private static final Logger LOG = LoggerFactory.getLogger(UnlimitedEnqueueTest.class);

    BrokerService brokerService = null;

    final long numMessages = 5000;

    final long numThreads = 10;

    final int payLoadSize = 100 * 1024;

    @Test
    public void testEnqueueIsOnlyLimitedByDisk() throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < (numThreads); i++) {
            executor.execute(new UnlimitedEnqueueTest.Producer(((numMessages) / (numThreads))));
        }
        Assert.assertTrue("Temp Store is filling ", Wait.waitFor(new Wait.Condition() {
            @Override
            public boolean isSatisified() throws Exception {
                UnlimitedEnqueueTest.LOG.info(((((("Temp Usage,  " + (brokerService.getSystemUsage().getTempUsage())) + ", full=") + (brokerService.getSystemUsage().getTempUsage().isFull())) + ", % usage: ") + (brokerService.getSystemUsage().getTempUsage().getPercentUsage())));
                return (brokerService.getSystemUsage().getTempUsage().getPercentUsage()) > 1;
            }
        }, TimeUnit.MINUTES.toMillis(4)));
        executor.shutdownNow();
    }

    public class Producer implements Runnable {
        private final long numberOfMessages;

        public Producer(final long n) {
            this.numberOfMessages = n;
        }

        public void run() {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerService.getVmConnectorURI());
            try {
                Connection conn = factory.createConnection();
                conn.start();
                byte[] bytes = new byte[payLoadSize];
                for (int i = 0; i < (numberOfMessages); i++) {
                    Session session = conn.createSession(false, AUTO_ACKNOWLEDGE);
                    Destination destination = session.createQueue("test-queue");
                    MessageProducer producer = session.createProducer(destination);
                    producer.setDeliveryMode(PERSISTENT);
                    BytesMessage message = session.createBytesMessage();
                    message.writeBytes(bytes);
                    try {
                        producer.send(message);
                    } catch (ResourceAllocationException e) {
                        e.printStackTrace();
                    }
                    session.close();
                }
            } catch (JMSException e) {
                // expect interrupted exception on shutdownNow
            }
        }
    }
}

