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
package org.apache.activemq.broker.virtual;


import DestinationInfo.REMOVE_OPERATION_TYPE;
import Session.AUTO_ACKNOWLEDGE;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.DestinationInfo;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ7088Test {
    private static final Logger LOG = LoggerFactory.getLogger(AMQ7088Test.class);

    BrokerService brokerService;

    ConnectionFactory connectionFactory;

    @Test
    public void testDeadlockOnAddRemoveDest() throws Exception {
        final int numConnections = 100;
        final AtomicInteger numConsumers = new AtomicInteger(numConnections);
        ExecutorService executorService = Executors.newFixedThreadPool(numConnections);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    do {
                        int i = numConsumers.decrementAndGet();
                        if (i >= 0) {
                            Connection connection1 = connectionFactory.createConnection();
                            connection1.start();
                            Session session = connection1.createSession(false, AUTO_ACKNOWLEDGE);
                            ActiveMQQueue queue = new ActiveMQQueue((("Consumer." + i) + ".VirtualTopic.TEST.*"));
                            MessageConsumer messageConsumer = session.createConsumer(queue);
                            messageConsumer.close();
                            ActiveMQConnection activeMQConnection = ((ActiveMQConnection) (connection1));
                            DestinationInfo remove = new DestinationInfo();
                            remove.setConnectionId(activeMQConnection.getConnectionInfo().getConnectionId());
                            remove.setDestination(queue);
                            remove.setOperationType(REMOVE_OPERATION_TYPE);
                            activeMQConnection.getTransport().request(remove);
                            connection1.close();
                        }
                    } while ((numConsumers.get()) > 0 );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < numConnections; i++) {
            executorService.execute(runnable);
        }
        AMQ7088Test.LOG.info("Letting it run to completion...");
        executorService.shutdown();
        Assert.assertTrue("all done", executorService.awaitTermination(5, TimeUnit.MINUTES));
    }
}

