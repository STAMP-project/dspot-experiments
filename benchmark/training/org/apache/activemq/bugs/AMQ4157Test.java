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


import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.jms.Destination;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AMQ4157Test {
    static final Logger LOG = LoggerFactory.getLogger(AMQ4157Test.class);

    private BrokerService broker;

    private ActiveMQConnectionFactory connectionFactory;

    private final Destination destination = new ActiveMQQueue("Test");

    private final String payloadString = new String(new byte[8 * 1024]);

    private final boolean useBytesMessage = true;

    private final int parallelProducer = 20;

    private final int parallelConsumer = 100;

    private final Vector<Exception> exceptions = new Vector<Exception>();

    long toSend = 1000;

    @Test
    public void testPublishCountsWithRollbackConsumer() throws Exception {
        startBroker(true);
        final AtomicLong sharedCount = new AtomicLong(toSend);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < (parallelConsumer); i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        consumeOneAndRollback();
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
            });
        }
        for (int i = 0; i < (parallelProducer); i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        publishMessages(sharedCount, 0);
                    } catch (Exception e) {
                        exceptions.add(e);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(30, TimeUnit.MINUTES);
        Assert.assertTrue("Producers done in time", executorService.isTerminated());
        Assert.assertTrue(("No exceptions: " + (exceptions)), exceptions.isEmpty());
        restartBroker(500);
        AMQ4157Test.LOG.info("Attempting consume of {} messages", toSend);
        consumeMessages(toSend);
    }
}

