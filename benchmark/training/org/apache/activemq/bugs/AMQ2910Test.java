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
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.JmsMultipleClientsTestSupport;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;


@RunWith(BlockJUnit4ClassRunner.class)
public class AMQ2910Test extends JmsMultipleClientsTestSupport {
    final int maxConcurrency = 60;

    final int msgCount = 200;

    final Vector<Throwable> exceptions = new Vector<Throwable>();

    @Test(timeout = 120 * 1000)
    public void testConcurrentSendToPendingCursor() throws Exception {
        final ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(broker.getTransportConnectors().get(0).getConnectUri());
        factory.setCloseTimeout(30000);
        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 0; i < (maxConcurrency); i++) {
            final ActiveMQQueue dest = new ActiveMQQueue(("Queue-" + i));
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendMessages(factory.createConnection(), dest, msgCount);
                    } catch (Throwable t) {
                        exceptions.add(t);
                    }
                }
            });
        }
        executor.shutdown();
        Assert.assertTrue("send completed", executor.awaitTermination(60, TimeUnit.SECONDS));
        assertNoExceptions();
        executor = Executors.newCachedThreadPool();
        for (int i = 0; i < (maxConcurrency); i++) {
            final ActiveMQQueue dest = new ActiveMQQueue(("Queue-" + i));
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        startConsumers(factory, dest);
                    } catch (Throwable t) {
                        exceptions.add(t);
                    }
                }
            });
        }
        executor.shutdown();
        Assert.assertTrue("consumers completed", executor.awaitTermination(30, TimeUnit.SECONDS));
        allMessagesList.setMaximumDuration((90 * 1000));
        final int numExpected = (maxConcurrency) * (msgCount);
        allMessagesList.waitForMessagesToArrive(numExpected);
        if ((allMessagesList.getMessageCount()) != numExpected) {
            JmsMultipleClientsTestSupport.dumpAllThreads(getName());
        }
        allMessagesList.assertMessagesReceivedNoWait(numExpected);
        Assert.assertTrue(("no exceptions: " + (exceptions)), exceptions.isEmpty());
    }
}

